package celtech.roboxbase.comms.tx;

/**
 *
 * @author ianhudson
 */
//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class QueryFirmwareVersion extends RoboxTxPacket
{

    /**
     *
     */
    public QueryFirmwareVersion()
    {
        super(TxPacketTypeEnum.QUERY_FIRMWARE_VERSION, false, false);
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
