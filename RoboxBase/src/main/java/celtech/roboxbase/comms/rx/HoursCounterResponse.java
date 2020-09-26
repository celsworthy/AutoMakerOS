package celtech.roboxbase.comms.rx;

import celtech.roboxbase.comms.rx.RxPacketTypeEnum;
import celtech.roboxbase.comms.rx.RoboxRxPacket;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author ianhudson
 */
public class HoursCounterResponse extends RoboxRxPacket
{

    private final String charsetToUse = "US-ASCII";
    private final int bytesUsed = 8;
    private float hoursCounterFloat = 0;

    /**
     *
     * @return
     */
    public float getHoursCounter()
    {
        return hoursCounterFloat;
    }

    /**
     *
     */
    public HoursCounterResponse()
    {
        super(RxPacketTypeEnum.HOURS_COUNTER, false, false);
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
            //Offset past the command
            int byteOffset = 1;

            String hoursCounterString = new String(byteData, byteOffset, bytesUsed, charsetToUse);
            byteOffset += bytesUsed;

            hoursCounterString = hoursCounterString.trim();
            this.hoursCounterFloat = Float.valueOf(hoursCounterString);

            success = true;
        } catch (UnsupportedEncodingException ex)
        {
            steno.error("Failed to convert byte array to Hours Counter Response");
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
        outputString.append("Hours Counter: " + getHoursCounter());
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
