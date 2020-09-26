package celtech.roboxbase.comms.tx;

/**
 *
 * @author ianhudson
 */
public class PausePrint extends RoboxTxPacket
{

    /**
     *
     */
    public PausePrint()
    {
        super(TxPacketTypeEnum.PAUSE_RESUME_PRINT, false, false);
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

    /**
     *
     */
    public void setPause()
    {
        this.setMessagePayload("1");
    }

    /**
     *
     */
    public void setResume()
    {
        this.setMessagePayload("0");
    }
}
