package celtech.roboxbase.comms.tx;

/**
 *
 * @author ianhudson
 */
public class SendDataFileChunk extends RoboxTxPacket
{

    /**
     *
     */
    public SendDataFileChunk()
    {
        super(TxPacketTypeEnum.DATA_FILE_CHUNK, true, false);
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
