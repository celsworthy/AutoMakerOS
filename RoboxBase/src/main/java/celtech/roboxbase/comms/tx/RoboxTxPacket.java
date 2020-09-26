package celtech.roboxbase.comms.tx;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.UnsupportedEncodingException;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 *
 * @author ianhudson
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
//@JsonSubTypes({
//    @Type(value = AbortPrint.class, name = "AbortPrint"),
//    @Type(value = FormatHeadEEPROM.class, name = "FormatHeadEEPROM"),
//    @Type(value = FormatReel0EEPROM.class, name = "FormatReel0EEPROM"),
//    @Type(value = FormatReel1EEPROM.class, name = "FormatReel1EEPROM"),
//    @Type(value = InitiatePrint.class, name = "InitiatePrint"),
//    @Type(value = ListFiles.class, name = "ListFiles"),
//    @Type(value = PausePrint.class, name = "PausePrint"),
//    @Type(value = QueryFirmwareVersion.class, name = "QueryFirmwareVersion")
//    @Type(value = ReadDebugData.class, name = "ReadDebugData"),
//    @Type(value = PausePrint.class, name = "PausePrint"),
//    @Type(value = PausePrint.class, name = "PausePrint")
//    @Type(value = PausePrint.class, name = "PausePrint")
//    @Type(value = PausePrint.class, name = "PausePrint")
//    @Type(value = PausePrint.class, name = "PausePrint")
//    @Type(value = PausePrint.class, name = "PausePrint")
//    @Type(value = PausePrint.class, name = "PausePrint")
//})

public abstract class RoboxTxPacket
{

    private TxPacketTypeEnum packetType = null;

    /**
     *
     */
    protected String messagePayload = null;
    private Integer sequenceNumber = -1;
    private Boolean includeSequenceNumber = false;
    private Boolean includeCharsOfDataInOutput = false;

    private static final int sequenceNumberLength = 8;
    private static final int charsOfDataLength = 4;

    /**
     *
     */
    protected static Stenographer steno = StenographerFactory.getStenographer(RoboxTxPacket.class.getName());

    public RoboxTxPacket()
    {
    }

    /**
     *
     * @param packetType
     * @param includeSequenceNumber
     * @param includeCharsOfDataInOutput
     */
    public RoboxTxPacket(TxPacketTypeEnum packetType, boolean includeSequenceNumber, boolean includeCharsOfDataInOutput)
    {
        this.packetType = packetType;
        this.includeSequenceNumber = includeSequenceNumber;
        this.includeCharsOfDataInOutput = includeCharsOfDataInOutput;
    }

    /**
     *
     * @return
     */
    public TxPacketTypeEnum getPacketType()
    {
        return packetType;
    }

    public void setPacketType(TxPacketTypeEnum packetType)
    {
        this.packetType = packetType;
    }

    /**
     *
     * @return
     */
    public String getMessagePayload()
    {
        return messagePayload;
    }

    /**
     *
     * @param messagePayload
     */
    public void setMessagePayload(String messagePayload)
    {
        this.messagePayload = messagePayload;
    }

    /**
     *
     * @param messagePayload
     */
    public void setMessagePayloadBytes(byte[] messagePayload)
    {
        this.messagePayload = new String(messagePayload, 1, messagePayload.length - 1);
    }

    public Integer getSequenceNumber()
    {
        return sequenceNumber;
    }

    /**
     *
     * @param sequenceNumber
     */
    public void setSequenceNumber(int sequenceNumber)
    {
        this.sequenceNumber = sequenceNumber;
    }

    public Boolean getIncludeSequenceNumber()
    {
        return includeSequenceNumber;
    }

    public void setIncludeSequenceNumber(boolean includeSequenceNumber)
    {
        this.includeSequenceNumber = includeSequenceNumber;
    }

    public Boolean getIncludeCharsOfDataInOutput()
    {
        return includeCharsOfDataInOutput;
    }

    public void setIncludeCharsOfDataInOutput(boolean includeCharsOfDataInOutput)
    {
        this.includeCharsOfDataInOutput = includeCharsOfDataInOutput;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder output = new StringBuilder();

        output.append("\n>>>---<<<\n");
        output.append("Command code: " + String.format("0x%02X", packetType.getCommandByte()));
        output.append("\n");
        output.append("Message:");
        output.append(messagePayload);
        output.append("\n");
        output.append(">>>---<<<\n");

        return output.toString();
    }

    /**
     *
     * @return
     */
    public byte[] toByteArray()
    {
        byte[] outputArray = null;

        int bufferSize = 1; // 1 for the command

        if (includeSequenceNumber)
        {
            bufferSize += sequenceNumberLength;
        }

        if (includeCharsOfDataInOutput && messagePayload != null)
        {
            bufferSize += charsOfDataLength;
        }

        if (messagePayload != null)
        {
            bufferSize += messagePayload.length();
        }

        outputArray = new byte[bufferSize];

        outputArray[0] = packetType.getCommandByte();

        String finalPayload = constructPayloadString();

        if (bufferSize > 1)
        {
            try
            {
                byte[] payloadBytes = finalPayload.toString().getBytes("US-ASCII");
                //TODO - replace this with a ByteBuffer or equivalent
                for (int i = 1; i <= payloadBytes.length; i++)
                {
                    outputArray[i] = payloadBytes[i - 1];
                }
            } catch (UnsupportedEncodingException ex)
            {
                steno.error("Couldn't encode message for output");
            }
        }

        return outputArray;
    }

    public String constructPayloadString()
    {
        StringBuilder finalPayload = new StringBuilder();
        if (includeSequenceNumber)
        {
            finalPayload.append(String.format("%08X", sequenceNumber));
        }
        if (includeCharsOfDataInOutput && messagePayload != null)
        {
            finalPayload.append(String.format("%04X", messagePayload.length()));
        }
        if (messagePayload != null)
        {
            finalPayload.append(messagePayload);
        }
        return finalPayload.toString();
    }

    /**
     *
     * @param byteData
     * @return
     */
    public abstract boolean populatePacket(byte[] byteData);

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 31).
                append(packetType).
                append(messagePayload).
                append(sequenceNumber).
                append(includeSequenceNumber).
                append(includeCharsOfDataInOutput).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof RoboxTxPacket))
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }

        RoboxTxPacket rhs = (RoboxTxPacket) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                append(packetType, rhs.packetType).
                append(messagePayload, rhs.messagePayload).
                append(sequenceNumber, rhs.sequenceNumber).
                append(includeSequenceNumber, rhs.includeSequenceNumber).
                append(includeCharsOfDataInOutput, rhs.includeCharsOfDataInOutput).
                isEquals();
    }
}
