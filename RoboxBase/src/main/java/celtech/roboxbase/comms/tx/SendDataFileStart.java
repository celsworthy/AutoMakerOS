package celtech.roboxbase.comms.tx;

/**
 *
 * @author ianhudson
 */
public class SendDataFileStart extends RoboxTxPacket
{

    /**
     *
     */
    public SendDataFileStart()
    {
        super(TxPacketTypeEnum.START_OF_DATA_FILE, false, false);
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
