/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase.comms.tx;


/**
 *
 * @author ianhudson
 */
public class InitiatePrint extends RoboxTxPacket
{

    /**
     *
     */
    public InitiatePrint()
    {
        super(TxPacketTypeEnum.INITIATE_PRINT, false, false);
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
