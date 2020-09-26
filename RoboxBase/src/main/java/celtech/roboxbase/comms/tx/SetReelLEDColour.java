package celtech.roboxbase.comms.tx;

/**
 *
 * @author ianhudson
 */
public class SetReelLEDColour extends RoboxTxPacket
{

    /**
     *
     */
    public SetReelLEDColour()
    {
        super(TxPacketTypeEnum.SET_REEL_LED_COLOUR, false, false);
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
     * @param colourString
     */
    public void setLEDColour(String colourString)
    {
        StringBuffer payload = new StringBuffer();

        payload.append(colourString);

        this.setMessagePayload(payload.toString());
    }
}
