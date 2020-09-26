package celtech.roboxbase.comms.remote;

import celtech.roboxbase.comms.exceptions.RoboxCommsException;
import celtech.roboxbase.comms.rx.RoboxRxPacket;
import celtech.roboxbase.comms.tx.RoboxTxPacket;

/**
 *
 * @author ianhudson
 */
public interface LowLevelInterface
{
    public boolean connect(String printerID) throws RoboxCommsException;
    public void disconnect(String printerID) throws RoboxCommsException;
    public RoboxRxPacket writeToPrinter(String printerID, RoboxTxPacket messageToWrite) throws RoboxCommsException;
}
