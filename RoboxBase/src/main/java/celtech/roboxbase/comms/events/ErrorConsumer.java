package celtech.roboxbase.comms.events;

import celtech.roboxbase.comms.rx.FirmwareError;

/**
 *
 * @author Ian
 */
public interface ErrorConsumer
{
    public void consumeError(FirmwareError error);
}
