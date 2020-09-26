package celtech.roboxbase.comms.async;

import celtech.roboxbase.comms.CommandInterface;
import celtech.roboxbase.comms.exceptions.ConnectionLostException;
import celtech.roboxbase.comms.exceptions.RoboxCommsException;
import celtech.roboxbase.comms.rx.RoboxRxPacket;
import celtech.roboxbase.comms.rx.RoboxRxPacketFactory;
import celtech.roboxbase.comms.rx.RxPacketTypeEnum;
import celtech.roboxbase.comms.tx.TxPacketTypeEnum;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class AsyncWriteThread extends Thread
{
    // The timeout here has to be at least greater than the sum of the connect and read time out
    // values in detected server, as it can wait at least as long as that. 
    private final int NUMBER_OF_SIMULTANEOUS_COMMANDS = 50;

    // The thread can retry sending a command if it fails due to a timeout. Currently the maxCommandRetryCount
    // is one, which disables the mechanism.
    private final int maxCommandRetryCount = 1;
    // The poll timeout must be longer than the total timeout of the detected server. Otherwise
    // this thread can timeout before the server to which it is connected times out.
    // It is 12 seconds because the remote server can pause for several seconds, for reasons unknown.
    private final int pollTimeout = 30000;
    private final Stenographer steno = StenographerFactory.getStenographer(AsyncWriteThread.class.getName());
    private final BlockingQueue<CommandHolder> inboundQueue = new ArrayBlockingQueue<>(NUMBER_OF_SIMULTANEOUS_COMMANDS);
    private final List<BlockingQueue<RoboxRxPacket>> outboundQueues;
    
    private final CommandInterface commandInterface;
    private boolean keepRunning = true;
    private boolean[] queueInUse = new boolean[NUMBER_OF_SIMULTANEOUS_COMMANDS];
    
    private static CommandHolder poisonedPill = new CommandHolder(-1, null);

    public AsyncWriteThread(CommandInterface commandInterface, String ciReference)
    {
        this.commandInterface = commandInterface;
        this.setDaemon(true);
        this.setName("AsyncCommandProcessor|" + ciReference);
        this.setPriority(Thread.MAX_PRIORITY);
        
        outboundQueues = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_SIMULTANEOUS_COMMANDS; i++)
        {
            outboundQueues.add(new ArrayBlockingQueue<>(1));
            queueInUse[i] = false;
        }
    }

    private int addCommandToQueue(CommandPacket command) throws RoboxCommsException
    {
        int queueNumber = -1;

        // Look for an empty outbound queue
        for (int queueIndex = 0; queueIndex < outboundQueues.size(); queueIndex++)
        {
            if (!queueInUse[queueIndex])
            {
                outboundQueues.get(queueIndex).clear(); // Clear out any junk in the queue.
                CommandHolder commandHolder = new CommandHolder(queueIndex, command);
                inboundQueue.add(commandHolder);
                queueNumber = queueIndex;
                queueInUse[queueIndex] = true;
                break;
            }
        }

        if (queueNumber < 0)
        {
            steno.info("Message queue full; can not add command:" + command.getCommand().getPacketType());
            throw new RoboxCommsException("Message queue full");
        }

        return queueNumber;
    }

    public synchronized RoboxRxPacket sendCommand(CommandPacket command) throws RoboxCommsException
    {
        RoboxRxPacket response = null;

        //steno.info("**** Sending command:" + command.getCommand().getPacketType());
        //if (command.getCommand().getPacketType() == TxPacketTypeEnum.DATA_FILE_CHUNK)
        //    steno.info("        sequence number = " + command.getCommand().getSequenceNumber());
        for (int retryCount = 0; response == null && retryCount < maxCommandRetryCount; ++retryCount)
        {
            //if (retryCount == 0)
            //    steno.info("@@@@ Adding command " + command.getCommand().getPacketType() + " to queue");
            //else
            //    steno.info("@@@@ requeuing (" + retryCount + ") command " + command.getCommand().getPacketType());
            int queueNumber = addCommandToQueue(command);

            try
            {
                //steno.info("        Awaiting response on queue " + queueNumber);
                // If the async command processor writes to
                // the queue after the listener has timed out, it used to cause the queue to
                // be permanantly lost, because it contained an entry. Now it clears the queue.
                // However, there is still a risk that if a timed-out queue is used, it could
                // get the response intended for the previous queue. This is quite a tricky problem.
                long t1 = System.currentTimeMillis();
                
                // The timeout here has to be at least greater than the sum of the connect and read time out
                // values and 
                response = outboundQueues.get(queueNumber).poll(pollTimeout, TimeUnit.MILLISECONDS);
                long t2 = System.currentTimeMillis();
                if (response == null)
                {
                //    steno.info("    No response on queue " + queueNumber);
                }
                else
                {
                //    steno.info("    Received response on queue " + queueNumber);
                //    steno.info("        response:" + response.getPacketType());
                    if (response.getPacketType() == RxPacketTypeEnum.NULL_PACKET)
                        response = null;
                }
                long dt = t2 -t1;
                if (dt > 500)
                {
                    steno.debug("Long wait (" + Long.toString(dt) + ") for response to command " + command.getCommand().getPacketType());
                    if (command.getCommand().getPacketType() == TxPacketTypeEnum.DATA_FILE_CHUNK)
                            steno.debug("    sequence number = " + command.getCommand().getSequenceNumber());
                    if (retryCount > 0 )
                        steno.debug("    retryCount = " + retryCount);
          
                }
                //steno.info("    Time taken = " + Long.toString(t2 - t1));
            }
            catch (InterruptedException ex)
            {
                steno.debug("**** Throwing RoboxCommsException('Interrupted waiting for response') on queue " + queueNumber);
                throw new RoboxCommsException("Interrupted waiting for response");
            }
            finally {
                queueInUse[queueNumber] = false;
            }
        }
        if (response == null
                || response.getPacketType() == RxPacketTypeEnum.NULL_PACKET)
        {
            steno.debug("**** Throwing RoboxCommsException('No response to message from command " + command + "')");
            throw new RoboxCommsException("No response to message from command " + command);
        }
        //steno.info("**** Returning response " + response.getPacketType() + " for command " + command);
        return response;
    }

    @Override
    public void run()
    {
        while (keepRunning)
        {
            boolean createNullPacket = true;
            CommandHolder commandHolder = null;
            try
            {
                //steno.info("++++ Taking a command");
                commandHolder = inboundQueue.take();
                if (commandHolder != poisonedPill)
                {
                    //steno.info("++++ Processing command for queue " + commandHolder.getQueueIndex() + " : " + commandHolder.getCommandPacket().getCommand().getPacketType());
                    RoboxRxPacket response = processCommand(commandHolder.getCommandPacket());
                    //steno.info("++++ Got response for queue " + commandHolder.getQueueIndex());
                    
                    if (response != null)
                    {
                        createNullPacket = false;
                        //steno.info("++++ sending response to queue " + commandHolder.getQueueIndex());
                        if (outboundQueues.get(commandHolder.getQueueIndex()).offer(response))
                        {
                            //steno.info("++++ sent response to queue " + commandHolder.getQueueIndex());
                        }
                        else
                        {
                            // Queue is full. Nothing is waiting for the response to this queue, so empty the queue.
                            BlockingQueue<RoboxRxPacket> q = outboundQueues.get(commandHolder.getQueueIndex());
                            steno.warning("++++ Unable to send response to queue " + commandHolder.getQueueIndex());
                            //steno.warning("++++ Queue already contains " + Integer.toString(q.size()) + "responses");
                            //if (q.size() > 0)
                            //{
                                //RoboxRxPacket[] r = q.toArray(new RoboxRxPacket[0]);
                               // for (int rIndex = 0; rIndex < r.length; rIndex++)
                                //{
                                //    steno.warning("++++    Response " + Integer.toString(rIndex) + " = " + r[rIndex].getPacketType());
                                //}
                            //}
                            q.clear();
                        }
                    }
                } else
                {
                    //steno.info("++++ Got poisoned pill");
                    //Just drop out - we got the poisoned pill
                    createNullPacket = false;
                }
            } catch (ConnectionLostException ex)
            {
                // This is ok - the printer has probably been unplugged
                steno.info("Connection lost - " + getName());
            } catch (RoboxCommsException | InterruptedException ex)
            {
                steno.exception("Unexpected error during write", ex);
            } finally
            {
                if (createNullPacket)
                {
                    //steno.info("++++ sending null response to queue " + commandHolder.getQueueIndex());
                    if (outboundQueues.get(commandHolder.getQueueIndex()).offer(RoboxRxPacketFactory.createNullPacket()))
                    {
                        //steno.info("++++ sent null response to queue " + commandHolder.getQueueIndex());
                    }
                    else
                    {
                        // Nothing is waiting for the response to this queue.
                        BlockingQueue<RoboxRxPacket> q = outboundQueues.get(commandHolder.getQueueIndex());
                        //steno.warning("++++ Unable to send null response to queue " + commandHolder.getQueueIndex());
                        //steno.warning("++++ Queue already contains " + Integer.toString(q.size()) + "responses");
                        //if (q.size() > 0)
                        //{
                        //    RoboxRxPacket[] r = q.toArray(new RoboxRxPacket[0]);
                        //    for (int rIndex = 0; rIndex < r.length; rIndex++)
                        //    {
                        //        steno.warning("++++    Response " + Integer.toString(rIndex) + " = " + r[rIndex].getPacketType());
                        //    }
                        //}
                        q.clear();
                    }
                }
            }
        }
    }

    private RoboxRxPacket processCommand(CommandPacket command) throws RoboxCommsException
    {
        RoboxRxPacket response = commandInterface.writeToPrinterImpl(command.getCommand(), command.getDontPublish());
        return response;
    }

    public void shutdown()
    {
        keepRunning = false;
        inboundQueue.add(poisonedPill);
    }
}
