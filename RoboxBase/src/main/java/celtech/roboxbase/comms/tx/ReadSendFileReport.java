package celtech.roboxbase.comms.tx;

import celtech.roboxbase.comms.tx.TxPacketTypeEnum;
import celtech.roboxbase.comms.tx.RoboxTxPacket;

/**
 *
 * @author ianhudson
 */
public class ReadSendFileReport extends RoboxTxPacket
{

    /**
     *
     */
    public ReadSendFileReport()
    {
        super(TxPacketTypeEnum.READ_SEND_FILE_REPORT, false, false);
    }

    /**
     *
     * @param byteData
     * @return
     */
    @Override
    public boolean populatePacket(byte[] byteData)
    {
        return false;
    }    
}
