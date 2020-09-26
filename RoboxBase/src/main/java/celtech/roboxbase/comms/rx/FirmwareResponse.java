package celtech.roboxbase.comms.rx;

import celtech.roboxbase.comms.rx.RxPacketTypeEnum;
import celtech.roboxbase.comms.rx.RoboxRxPacket;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author ianhudson
 */
public class FirmwareResponse extends RoboxRxPacket
{

    private final String charsetToUse = "US-ASCII";
    private String firmwareRevision = null;
    private final int firmwareRevisionBytes = 8;
    private float firmwareRevisionFloat = 0;
    private String firmwareRevisionString = "";

    /**
     *
     * @return
     */
    public String getFirmwareRevision()
    {
        return firmwareRevision;
    }

    /**
     *
     * @return
     */
    public float getFirmwareRevisionFloat()
    {
        return firmwareRevisionFloat;
    }

    public String getFirmwareRevisionString()
    {
        return firmwareRevisionString;
    }

    /**
     *
     * @param firmwareRevision
     */
    public void setFirmwareRevision(String firmwareRevision)
    {
        this.firmwareRevision = firmwareRevision;
        this.firmwareRevisionFloat = Float.valueOf(firmwareRevision.trim().substring(1));
    }

    /**
     *
     */
    public FirmwareResponse()
    {
        super(RxPacketTypeEnum.FIRMWARE_RESPONSE, false, false);
    }

    /**
     *
     * @param byteData
     * @return
     */
    @Override
    public boolean populatePacket(byte[] byteData, float requiredFirmwareVersion)
    {
        setMessagePayloadBytes(byteData);

        boolean success = false;

        try
        {
            int byteOffset = 1;
            this.firmwareRevision = new String(byteData, byteOffset, firmwareRevisionBytes, charsetToUse);
            byteOffset += firmwareRevisionBytes;

            this.firmwareRevisionString = firmwareRevision.trim();
            try
            {
                this.firmwareRevisionFloat = Float.valueOf(firmwareRevision.trim().substring(1));
            } catch (NumberFormatException ex)
            {
                steno.warning("Couldn't calculate firmware version number from response");
            }

            success = true;
        } catch (UnsupportedEncodingException ex)
        {
            steno.error("Failed to convert byte array to Status Response");
        }

        return success;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder outputString = new StringBuilder();

        outputString.append(">>>>>>>>>>\n");
        outputString.append("Packet type:");
        outputString.append(getPacketType().name());
        outputString.append("\n");
        outputString.append("Firmware: " + getFirmwareRevision());
        outputString.append("\n");
        outputString.append(">>>>>>>>>>\n");

        return outputString.toString();
    }

    @Override
    public int packetLength(float requiredFirmwareVersion)
    {
        return 9;
    }
}
