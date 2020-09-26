package celtech.roboxbase.comms.rx;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author ianhudson
 */
public class GCodeDataResponse extends RoboxRxPacket
{

    @JsonIgnore
    private final String charsetToUse = "US-ASCII";
    @JsonIgnore
    private final int lengthFieldBytes = 4;

    private String GCodeResponse = "";

    /**
     *
     */
    public GCodeDataResponse()
    {
        super(RxPacketTypeEnum.GCODE_RESPONSE, false, false);
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
            
            int lengthOfData = 0;
            
            String lengthString = new String(byteData, byteOffset, lengthFieldBytes, charsetToUse);
            byteOffset += lengthFieldBytes;

            lengthOfData = Integer.valueOf(lengthString, 16);

            GCodeResponse = new String(byteData, byteOffset, lengthOfData, charsetToUse);
            byteOffset += lengthOfData;

            success = true;
        } catch (UnsupportedEncodingException ex)
        {
            steno.error("Failed to convert byte array to GCode Response");
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
//        outputString.append("ID: " + getPrinterID());
        outputString.append("\n");
        outputString.append(">>>>>>>>>>\n");

        return outputString.toString();
    }

    /**
     *
     * @return
     */
    public String getGCodeResponse()
    {
        return GCodeResponse;
    }

    public void setGCodeResponse(String GCodeResponse)
    {
        this.GCodeResponse = GCodeResponse;
    }

    @Override
    public int packetLength(float requiredFirmwareVersion)
    {
        return 5;
    }
}
