package celtech.roboxbase.comms;

import celtech.roboxbase.comms.exceptions.ConnectionLostException;
import celtech.roboxbase.comms.exceptions.InvalidCommandByteException;
import celtech.roboxbase.comms.exceptions.InvalidResponseFromPrinterException;
import celtech.roboxbase.comms.exceptions.PortNotFoundException;
import celtech.roboxbase.comms.exceptions.RoboxCommsException;
import celtech.roboxbase.comms.exceptions.UnableToGenerateRoboxPacketException;
import celtech.roboxbase.comms.exceptions.UnknownPacketTypeException;
import celtech.roboxbase.comms.remote.LowLevelInterfaceException;
import celtech.roboxbase.comms.rx.RoboxRxPacket;
import celtech.roboxbase.comms.rx.RoboxRxPacketFactory;
import celtech.roboxbase.comms.rx.RxPacketTypeEnum;
import celtech.roboxbase.comms.tx.RoboxTxPacket;
import celtech.roboxbase.printerControl.model.Printer;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class HardwareCommandInterface extends CommandInterface
{

    private boolean stillWaitingForStatus = false;
    private final SerialPortManager serialPortManager;

    public HardwareCommandInterface(PrinterStatusConsumer controlInterface,
            DetectedDevice printerHandle,
            boolean suppressPrinterIDChecks, int sleepBetweenStatusChecks)
    {
        super(controlInterface, printerHandle, suppressPrinterIDChecks, sleepBetweenStatusChecks, true);
        this.setName("HCI:" + printerHandle + " " + this.toString());
        serialPortManager = new SerialPortManager(printerHandle.getConnectionHandle());
    }

    @Override
    protected boolean connectToPrinterImpl() throws PortNotFoundException
    {
        return serialPortManager.connect(115200);
    }

    @Override
    protected void disconnectPrinterImpl()
    {
        if (serialPortManager != null
                && serialPortManager.serialPort != null)
        {
            if (serialPortManager.serialPort.isOpen())
            {
                try
                {
                    serialPortManager.disconnect();
                } catch (LowLevelInterfaceException ex)
                {
                    steno.error("Failed to shut down serial port " + ex.getMessage());
                }
            }
        }
    }

    @Override
    public synchronized RoboxRxPacket writeToPrinterImpl(RoboxTxPacket messageToWrite,
            boolean dontPublishResult) throws RoboxCommsException
    {
        RoboxRxPacket receivedPacket = null;

        if (commsState == RoboxCommsState.CONNECTED
                || commsState == RoboxCommsState.CHECKING_FIRMWARE
                || commsState == RoboxCommsState.CHECKING_ID
                || commsState == RoboxCommsState.RESETTING_ID
                || commsState == RoboxCommsState.DETERMINING_PRINTER_STATUS)
        {
            try
            {
                byte[] outputBuffer = messageToWrite.toByteArray();

                serialPortManager.writeAndWaitForData(outputBuffer);

                byte[] respCommand = serialPortManager.readSerialPort(1);
                
                RxPacketTypeEnum packetType = RxPacketTypeEnum.getEnumForCommand(respCommand[0]);
                if (packetType != null)
                {
                    if (packetType != messageToWrite.getPacketType().getExpectedResponse())
                    {
                        throw new InvalidResponseFromPrinterException(
                                "Expected response of type "
                                + messageToWrite.getPacketType().getExpectedResponse().name()
                                + " and got "
                                + packetType);
                    }
                    //steno.trace("Got a response packet back of type: " + packetType.toString());
                    RoboxRxPacket rxPacketTemplate = RoboxRxPacketFactory.createPacket(packetType);
                    int packetLength = rxPacketTemplate.packetLength(firmwareVersionInUse);

                    byte[] inputBuffer = null;
                    if (packetType.containsLengthField())
                    {
                        byte[] lengthData = serialPortManager.readSerialPort(packetType.
                                getLengthFieldSize());

                        int payloadSize = Integer.valueOf(new String(lengthData), 16);
                        if (packetType == RxPacketTypeEnum.LIST_FILES_RESPONSE)
                        {
                            payloadSize = payloadSize * 16;
                        }

                        packetLength = 1 + packetType.getLengthFieldSize() + payloadSize;
                        inputBuffer = new byte[packetLength];
                        for (int i = 0; i < packetType.getLengthFieldSize(); i++)
                        {
                            inputBuffer[1 + i] = lengthData[i];
                        }

                        byte[] payloadData = serialPortManager.readSerialPort(payloadSize);
                        for (int i = 0; i < payloadSize; i++)
                        {
                            inputBuffer[1 + packetType.getLengthFieldSize() + i] = payloadData[i];
                        }
                    } else
                    {
                        inputBuffer = new byte[packetLength];
                        int bytesToRead = packetLength - 1;
                        byte[] payloadData = serialPortManager.readSerialPort(bytesToRead);
                        for (int i = 0; i < bytesToRead; i++)
                        {
                            inputBuffer[1 + i] = payloadData[i];
                        }
                    }
                    // Clear any remaining bytes the input
                    // There shouldn't be anything here but just in case...
                    byte[] storage = serialPortManager.readAllDataOnBuffer();
                    if (storage != null && storage.length > 0)
                    {
                        steno.debug("Cleared " + Integer.toString(storage.length) + " extra bytes from input buffer (expected " + Integer.toString(packetLength) + " but received " + Integer.toString(storage.length + packetLength) + ".");
                    }

                    inputBuffer[0] = respCommand[0];

                    try
                    {
                        receivedPacket = RoboxRxPacketFactory.createPacket(inputBuffer, firmwareVersionInUse);
                        steno.trace("Got packet of type " + receivedPacket.getPacketType().name());
    
                        if (!dontPublishResult)
                        {
                            printerToUse.processRoboxResponse(receivedPacket);
                        }
                    } catch (InvalidCommandByteException ex)
                    {
                         steno.error("Command byte of " + String.format("0x%02X", inputBuffer[0])
                                + " is invalid.");
                    } catch (UnknownPacketTypeException ex)
                    {
                        steno.error("Packet type unknown for command byte "
                                + String.format("0x%02X", inputBuffer[0]) + " is invalid.");
                    } catch (UnableToGenerateRoboxPacketException ex)
                    {
                        steno.error("A packet that appeared to be of type " + packetType.name()
                                + " could not be unpacked.");
                    }
                } else
                {
                    // Attempt to drain the crud from the input
                    // There shouldn't be anything here but just in case...
                    byte[] storage = serialPortManager.readAllDataOnBuffer();
    
                    try
                    {
                        String received = new String(storage);

                        steno.warning("Invalid packet received from firmware: " + received);
                    } catch (Exception e)
                    {
                        steno.warning(
                                "Invalid packet received from firmware - couldn't print contents");
                    }

//                    InvalidResponseFromPrinterException exception = new InvalidResponseFromPrinterException("Invalid response - got: " + received);
//                    throw exception;
                }
            } catch (LowLevelInterfaceException ex)
            {
                actionOnCommsFailure();
            }
        } else
        {
            throw new RoboxCommsException("Invalid state for writing data");
        }
//        steno.debug("Command Interface send - completed " + messageToWrite.getPacketType());
        return receivedPacket;
    }

    private void actionOnCommsFailure() throws ConnectionLostException
    {
        //If we get an exception then abort and treat
        steno.debug("Error during write to printer");
        shutdown();
        throw new ConnectionLostException();
    }

    void setPrinterToUse(Printer newPrinter)
    {
        this.printerToUse = newPrinter;
    }

    /**
     *
     * @param sleepMillis
     */
    @Override
    public void setSleepBetweenStatusChecks(int sleepMillis)
    {
        sleepBetweenStatusChecks = sleepMillis;
    }
}
