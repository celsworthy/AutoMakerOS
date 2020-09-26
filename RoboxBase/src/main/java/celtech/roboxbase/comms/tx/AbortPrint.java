package celtech.roboxbase.comms.tx;

/**
 *
 * @author ianhudson
 */
public class AbortPrint extends RoboxTxPacket
{

    /**
     *
     */
    public AbortPrint()
    {
        super(TxPacketTypeEnum.ABORT_PRINT, false, false);
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
