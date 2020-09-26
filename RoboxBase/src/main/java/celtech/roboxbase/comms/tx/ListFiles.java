/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase.comms.tx;

/**
 *
 * @author ianhudson
 */
public class ListFiles extends RoboxTxPacket
{

    /**
     *
     */
    public ListFiles()
    {
        super(TxPacketTypeEnum.LIST_FILES, false, false);
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
