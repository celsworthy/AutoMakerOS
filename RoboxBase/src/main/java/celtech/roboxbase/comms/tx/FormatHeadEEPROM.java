/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase.comms.tx;

/**
 *
 * @author ianhudson
 */
public class FormatHeadEEPROM extends RoboxTxPacket
{

    /**
     *
     */
    public FormatHeadEEPROM()
    {
        super(TxPacketTypeEnum.FORMAT_HEAD_EEPROM, false, false);
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
