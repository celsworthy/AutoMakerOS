package celtech.roboxbase.comms.tx;

import celtech.roboxbase.comms.tx.TxPacketTypeEnum;
import celtech.roboxbase.comms.tx.RoboxTxPacket;

/**
 *
 * @author ianhudson
 */
public class SendPrintFileStart extends RoboxTxPacket
{

    /**
     *
     */
    public SendPrintFileStart()
    {
        super(TxPacketTypeEnum.SEND_PRINT_FILE_START, false, false);
    }

    /**
     *
     * @param byteData
     * @return
     */
    @Override
    public boolean populatePacket(byte[] byteData)
    {
        setMessagePayloadBytes(byteData);
        return false;
    }
}
