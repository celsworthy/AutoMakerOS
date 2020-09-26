package celtech.roboxbase.comms.rx;

import celtech.roboxbase.comms.rx.RxPacketTypeEnum;
import celtech.roboxbase.comms.rx.RoboxRxPacket;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author ianhudson
 */
public class SendFile extends RoboxRxPacket
{

    private final String charsetToUse = "US-ASCII";
    private String fileID = null;
    private final int fileIDBytes = 16;
    private int expectedSequenceNumber = -1;
    private final int expectedSequenceNumberBytes = 8;

    /**
     *
     * @return
     */
    public String getFileID()
    {
        return fileID;
    }

    /**
     *
     * @return
     */
    public int getExpectedSequenceNumber()
    {
        return expectedSequenceNumber;
    }

    /**
     *
     */
    public SendFile()
    {
        super(RxPacketTypeEnum.SEND_FILE, false, false);
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
            this.fileID = new String(byteData, byteOffset, fileIDBytes, charsetToUse);
            byteOffset += fileIDBytes;
            this.fileID = this.fileID.trim();

            String expectedSequenceNumberString = new String(byteData, byteOffset,
                    expectedSequenceNumberBytes,
                    charsetToUse).trim();
            byteOffset += expectedSequenceNumberBytes;

            if (expectedSequenceNumberString.equals("") == false)
            {
                this.expectedSequenceNumber = Integer.valueOf(expectedSequenceNumberString, 16);
            } else
            {
                this.expectedSequenceNumber = -1;
            }

            success = true;
        } catch (UnsupportedEncodingException ex)
        {
            steno.error("Failed to convert byte array to Send File");
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
        outputString.append("File ID: " + getFileID());
        outputString.append("\n");
        outputString.append("Expected sequence number: " + getExpectedSequenceNumber());
        outputString.append("\n");
        outputString.append(">>>>>>>>>>\n");

        return outputString.toString();
    }

    @Override
    public int packetLength(float requiredFirmwareVersion)
    {
        return 25;
    }
}
