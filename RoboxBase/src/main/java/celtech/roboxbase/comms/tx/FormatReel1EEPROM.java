package celtech.roboxbase.comms.tx;

/**
 *
 * @author ianhudson
 */
public class FormatReel1EEPROM extends RoboxTxPacket
{

    /**
     *
     */
    public FormatReel1EEPROM()
    {
        super(TxPacketTypeEnum.FORMAT_REEL_1_EEPROM, false, false);
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
