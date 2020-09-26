/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase.comms.tx;

import celtech.roboxbase.comms.tx.TxPacketTypeEnum;
import celtech.roboxbase.comms.tx.RoboxTxPacket;

/**
 *
 * @author ianhudson
 */
public class ReportErrors extends RoboxTxPacket
{

    /**
     *
     */
    public ReportErrors()
    {
        super(TxPacketTypeEnum.REPORT_ERRORS, false, false);
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
