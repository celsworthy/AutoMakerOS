package celtech.roboxbase.comms.tx;

import celtech.roboxbase.comms.remote.FixedDecimalFloatFormat;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author ianhudson
 */
public class WriteHeadEEPROM extends RoboxTxPacket
{

    private String headTypeCode;
    private String headUniqueID;
    private float maximumTemperature;
    private float thermistorBeta;
    private float thermistorTCal;
    private float nozzle1XOffset;
    private float nozzle1YOffset;
    private float nozzle1ZOffset;
    private float nozzle1BOffset;

    private String filament0ID;
    private String filament1ID;

    private float nozzle2XOffset;
    private float nozzle2YOffset;
    private float nozzle2ZOffset;
    private float nozzle2BOffset;
    private float lastFilamentTemperature0;
    private float lastFilamentTemperature1;
    private float hourCounter;

    public WriteHeadEEPROM()
    {
        super(TxPacketTypeEnum.WRITE_HEAD_EEPROM, false, false);
    }

    public void populateEEPROM(String headTypeCode, String headUniqueID, float maximumTemperature,
            float thermistorBeta, float thermistorTCal,
            float nozzle1XOffset, float nozzle1YOffset, float nozzle1ZOffset, float nozzle1BOffset,
            String filament0ID, String filament1ID,
            float nozzle2XOffset, float nozzle2YOffset, float nozzle2ZOffset, float nozzle2BOffset,
            float lastFilamentTemperature0, float lastFilamentTemperature1, float hourCounter)
    {

        this.headTypeCode = headTypeCode;
        this.headUniqueID = headUniqueID;
        this.maximumTemperature = maximumTemperature;
        this.thermistorBeta = thermistorBeta;
        this.thermistorTCal = thermistorTCal;
        this.nozzle1XOffset = nozzle1XOffset;
        this.nozzle1YOffset = nozzle1YOffset;
        this.nozzle1ZOffset = nozzle1ZOffset;
        this.nozzle1BOffset = nozzle1BOffset;

        this.filament0ID = filament0ID;
        this.filament1ID = filament1ID;

        this.nozzle2XOffset = nozzle2XOffset;
        this.nozzle2YOffset = nozzle2YOffset;
        this.nozzle2ZOffset = nozzle2ZOffset;
        this.nozzle2BOffset = nozzle2BOffset;
        this.lastFilamentTemperature0 = lastFilamentTemperature0;
        this.lastFilamentTemperature1 = lastFilamentTemperature1;
        this.hourCounter = hourCounter;

        StringBuilder payload = new StringBuilder();

        FixedDecimalFloatFormat decimalFloatFormatter = new FixedDecimalFloatFormat();

        payload.append(String.format("%1$-16s", headTypeCode));
        payload.append(String.format("%1$-24s", headUniqueID));
        payload.append(decimalFloatFormatter.format(maximumTemperature));
        payload.append(decimalFloatFormatter.format(thermistorBeta));
        payload.append(decimalFloatFormatter.format(thermistorTCal));
        payload.append(decimalFloatFormatter.format(nozzle1XOffset));
        payload.append(decimalFloatFormatter.format(nozzle1YOffset));
        payload.append(decimalFloatFormatter.format(nozzle1ZOffset));
        payload.append(decimalFloatFormatter.format(nozzle1BOffset));
        
        int maxFilamentIDLength = 8;
        filament0ID = (filament0ID.length() > maxFilamentIDLength)?filament0ID.substring(0, maxFilamentIDLength):filament0ID;
        filament0ID = filament0ID.replaceAll("-", "");
        payload.append(String.format("%1$-8s", filament0ID));
        filament1ID = (filament1ID.length() > maxFilamentIDLength)?filament1ID.substring(0, maxFilamentIDLength):filament1ID;
        filament1ID = filament1ID.replaceAll("-", "");
        payload.append(String.format("%1$-8s", filament1ID));
        
        payload.append(decimalFloatFormatter.format(nozzle2XOffset));
        payload.append(decimalFloatFormatter.format(nozzle2YOffset));
        payload.append(decimalFloatFormatter.format(nozzle2ZOffset));
        payload.append(decimalFloatFormatter.format(nozzle2BOffset));
        payload.append(String.format("%1$24s", " "));
        // N.B. This is the only place in code (when writing) where filament temps for 1 and 0 are reversed order
        payload.append(decimalFloatFormatter.format(lastFilamentTemperature1));
        payload.append(decimalFloatFormatter.format(lastFilamentTemperature0));
        payload.append(decimalFloatFormatter.format(hourCounter));
        this.setMessagePayload(payload.toString());
    }

    public void populateEEPROM(
            String headTypeCodeIn,
            String uniqueIdIn,
            int numberOfHeaters,
            float maxTempIn,
            float betaIn,
            float tCalIn,
            float lastFilamentTemp0In,
            String filamentID0In,
            float lastFilamentTemp1In,
            String filamentID1In,
            float hoursUsedIn,
            int numberOfNozzles,
            float nozzle1XOffsetIn,
            float nozzle1YOffsetIn,
            float nozzle1ZOffsetIn,
            float nozzle1BOffsetIn,
            float nozzle2XOffsetIn,
            float nozzle2YOffsetIn,
            float nozzle2ZOffsetIn,
            float nozzle2BOffsetIn)
    {
        float maxTempA = 0;
        float maxTempB = 0;
        float beta = 0;
        float tcal = 0;
        float lastFilamentTemperatureA = 0;
        float lastFilamentTemperatureB = 0;
        String filamentIDA = "";
        String filamentIDB = "";

        if (numberOfHeaters > 0)
        {
            maxTempA = maxTempIn;
            lastFilamentTemperatureA = lastFilamentTemp0In;
            filamentIDA = filamentID0In;
            beta = betaIn;
            tcal = tCalIn;
        }

        if (numberOfHeaters > 1)
        {
            maxTempB = maxTempIn;
            lastFilamentTemperatureB = lastFilamentTemp1In;
            filamentIDB = filamentID1In;
        } else
        {
            maxTempB = maxTempA;
            lastFilamentTemperatureB = lastFilamentTemperatureA;
            filamentIDB = filamentIDA;
        }

        if (numberOfNozzles > 1)
        {
            populateEEPROM(headTypeCodeIn,
                    uniqueIdIn,
                    maxTempA,
                    beta,
                    tcal,
                    nozzle1XOffsetIn,
                    nozzle1YOffsetIn,
                    nozzle1ZOffsetIn,
                    nozzle1BOffsetIn,
                    filamentIDA,
                    filamentIDB,
                    nozzle2XOffsetIn,
                    nozzle2YOffsetIn,
                    nozzle2ZOffsetIn,
                    nozzle2BOffsetIn,
                    lastFilamentTemperatureA,
                    lastFilamentTemperatureB,
                    hoursUsedIn);
        } else if (numberOfNozzles > 0)
        {
            populateEEPROM(headTypeCodeIn,
                    uniqueIdIn,
                    maxTempA,
                    beta,
                    tcal,
                    nozzle1XOffsetIn,
                    nozzle1YOffsetIn,
                    nozzle1ZOffsetIn,
                    nozzle1BOffsetIn,
                    filamentIDA,
                    filamentIDB,
                    0,
                    0,
                    0,
                    0,
                    lastFilamentTemperatureA,
                    0,
                    hoursUsedIn);
        } else
        {
            populateEEPROM(headTypeCodeIn,
                    uniqueIdIn,
                    maxTempA,
                    beta,
                    tcal,
                    0,
                    0,
                    0,
                    0,
                    filamentIDA,
                    null,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    hoursUsedIn);
        }
    }

    @Override
    public byte[] toByteArray()
    {
        byte[] outputArray = null;

        int bufferSize = 1; // 1 for the command

        bufferSize += 4;

        if (messagePayload != null)
        {
            bufferSize += messagePayload.length();
        }

        outputArray = new byte[bufferSize];

        outputArray[0] = TxPacketTypeEnum.WRITE_HEAD_EEPROM.getCommandByte();

        StringBuilder finalPayload = new StringBuilder();

        finalPayload.append("00");

        finalPayload.append(String.format("%02X", 192));

        if (getMessagePayload() != null)
        {
            finalPayload.append(messagePayload);
        }

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

    @Override
    public boolean populatePacket(byte[] byteData)
    {
        setMessagePayloadBytes(byteData);
        return false;
    }

    public String getHeadTypeCode()
    {
        return headTypeCode;
    }

    public String getHeadUniqueID()
    {
        return headUniqueID;
    }

    public float getMaximumTemperature()
    {
        return maximumTemperature;
    }

    public float getThermistorBeta()
    {
        return thermistorBeta;
    }

    public float getThermistorTCal()
    {
        return thermistorTCal;
    }

    public float getNozzle1XOffset()
    {
        return nozzle1XOffset;
    }

    public float getNozzle1YOffset()
    {
        return nozzle1YOffset;
    }

    public float getNozzle1ZOffset()
    {
        return nozzle1ZOffset;
    }

    public float getNozzle1BOffset()
    {
        return nozzle1BOffset;
    }

    public float getNozzle2XOffset()
    {
        return nozzle2XOffset;
    }

    public float getNozzle2YOffset()
    {
        return nozzle2YOffset;
    }

    public float getNozzle2ZOffset()
    {
        return nozzle2ZOffset;
    }

    public float getNozzle2BOffset()
    {
        return nozzle2BOffset;
    }

    public float getLastFilamentTemperature0()
    {
        return lastFilamentTemperature0;
    }

    public float getLastFilamentTemperature1()
    {
        return lastFilamentTemperature1;
    }

    public String getFilament0ID()
    {
        return filament0ID;
    }

    public String getFilament1ID()
    {
        return filament1ID;
    }

    public float getHourCounter()
    {
        return hourCounter;
    }

}
