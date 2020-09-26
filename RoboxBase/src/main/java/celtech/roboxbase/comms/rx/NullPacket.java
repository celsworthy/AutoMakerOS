package celtech.roboxbase.comms.rx;

/**
 *
 * @author ianhudson
 */
public class NullPacket extends RoboxRxPacket
{

    public NullPacket()
    {
        super(RxPacketTypeEnum.NULL_PACKET, false, false);
    }

    /**
     *
     * @param byteData
     * @return
     */
    @Override
    public boolean populatePacket(byte[] byteData, float requiredFirmwareVersion)
    {
        return true;
    }

    @Override
    public int packetLength(float requiredFirmwareVersion)
    {
        return 0;
    }
}
