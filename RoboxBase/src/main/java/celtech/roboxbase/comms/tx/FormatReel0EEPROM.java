/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase.comms.tx;

/**
 *
 * @author ianhudson
 */
public class FormatReel0EEPROM extends RoboxTxPacket
{

    /**
     *
     */
    public FormatReel0EEPROM()
    {
        super(TxPacketTypeEnum.FORMAT_REEL_0_EEPROM, false, false);
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
