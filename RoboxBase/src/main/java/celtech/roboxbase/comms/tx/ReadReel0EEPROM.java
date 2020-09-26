package celtech.roboxbase.comms.tx;

/**
 *
 * @author ianhudson
 */
public class ReadReel0EEPROM extends RoboxTxPacket
{

    /**
     *
     */
    public ReadReel0EEPROM()
    {
        super(TxPacketTypeEnum.READ_REEL_0_EEPROM, false, false);
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
