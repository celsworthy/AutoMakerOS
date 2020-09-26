package celtech.roboxbase.comms.async;

/**
 *
 * @author Ian
 */
public class CommandHolder
{

    private final int queueIndex;
    private final CommandPacket commandPacket;

    public CommandHolder(int queueIndex, CommandPacket commandPacket)
    {
        this.queueIndex = queueIndex;
        this.commandPacket = commandPacket;
    }

    public CommandPacket getCommandPacket()
    {
        return commandPacket;
    }

    public int getQueueIndex()
    {
        return queueIndex;
    }
}
