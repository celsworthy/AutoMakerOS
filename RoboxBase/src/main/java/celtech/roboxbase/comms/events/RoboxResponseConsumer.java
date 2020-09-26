package celtech.roboxbase.comms.events;

import celtech.roboxbase.comms.rx.RoboxRxPacket;

/**
 *
 * @author Ian
 */
public interface RoboxResponseConsumer
{
    public void processRoboxResponse(RoboxRxPacket rxPacket);
}
