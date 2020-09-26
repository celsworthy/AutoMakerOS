package celtech.roboxbase.comms.tx;

/**
 *
 * @author ianhudson
 */
public class SendDataFileEnd extends RoboxTxPacket
{

    /**
     *
     */
    public SendDataFileEnd()
    {
        super(TxPacketTypeEnum.END_OF_DATA_FILE, true, true);
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
