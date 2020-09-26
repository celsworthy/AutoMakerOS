package celtech.roboxbase.comms.async;

import celtech.roboxbase.comms.tx.RoboxTxPacket;

/**
 *
 * @author Ian
 */
public class CommandPacket
{

    private final RoboxTxPacket command;
    private final boolean dontPublish;

    public CommandPacket(RoboxTxPacket command, boolean dontPublish)
    {
        this.command = command;
        this.dontPublish = dontPublish;
    }

    public RoboxTxPacket getCommand()
    {
        return command;
    }

    public boolean getDontPublish()
    {
        return dontPublish;
    }

    @Override
    public String toString()
    {
        return command.getPacketType().name();
    }

}
