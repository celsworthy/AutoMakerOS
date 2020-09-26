package celtech.roboxbase.comms.tx;

import celtech.roboxbase.MaterialType;
import celtech.roboxbase.comms.remote.EnumStringConverter;
import celtech.roboxbase.comms.remote.FixedDecimalFloatFormat;
import celtech.roboxbase.comms.remote.StringToBase64Encoder;
import java.io.UnsupportedEncodingException;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author ianhudson
 */
public class WriteReel0EEPROM extends RoboxTxPacket
{

    private Stenographer steno = StenographerFactory.getStenographer(WriteReel0EEPROM.class.getName());

    public static final int FRIENDLY_NAME_LENGTH = 40;
    public static final int MATERIAL_TYPE_LENGTH = 1;
    public static final int DISPLAY_COLOUR_LENGTH = 6;
    public static final int REEL_EEPROM_PADDING_LENGTH
            = 80 - FRIENDLY_NAME_LENGTH - MATERIAL_TYPE_LENGTH;

    public WriteReel0EEPROM()
    {
        super(TxPacketTypeEnum.WRITE_REEL_0_EEPROM, false, false);
    }

    private String formatString(String rawString, int length)
    {
        String formatString = "%-" + length + "s";
        return String.format(formatString, rawString);
    }

    public String filamentID;
    public float reelFirstLayerNozzleTemperature;
    public float reelNozzleTemperature;
    public float reelFirstLayerBedTemperature;
    public float reelBedTemperature;
    public float reelAmbientTemperature;
    public float reelFilamentDiameter;
    public float reelFilamentMultiplier;
    public float reelFeedRateMultiplier;
    public float reelRemainingFilament;
    public String friendlyName;
    public MaterialType materialType;
    public String displayColourString;

    public void populateEEPROM(String filamentID, float reelFirstLayerNozzleTemperature,
            float reelNozzleTemperature,
            float reelFirstLayerBedTemperature, float reelBedTemperature, float reelAmbientTemperature,
            float reelFilamentDiameter,
            float reelFilamentMultiplier, float reelFeedRateMultiplier, float reelRemainingFilament,
            String friendlyName, MaterialType materialType, String displayColourString)
    {
        StringBuilder payload = new StringBuilder();

        FixedDecimalFloatFormat decimalFloatFormatter = new FixedDecimalFloatFormat();

        this.filamentID = filamentID;
        this.reelFirstLayerNozzleTemperature = reelFirstLayerNozzleTemperature;
        this.reelNozzleTemperature = reelNozzleTemperature;
        this.reelFirstLayerBedTemperature = reelFirstLayerBedTemperature;
        this.reelBedTemperature = reelBedTemperature;
        this.reelAmbientTemperature = reelAmbientTemperature;
        this.reelFilamentDiameter = reelFilamentDiameter;
        this.reelFilamentMultiplier = reelFilamentMultiplier;
        this.reelFeedRateMultiplier = reelFeedRateMultiplier;
        this.reelRemainingFilament = reelRemainingFilament;
        this.friendlyName = friendlyName;
        this.materialType = materialType;
        this.displayColourString = displayColourString;

        payload.append(formatString(filamentID, 16));
        payload.append(formatString(displayColourString, DISPLAY_COLOUR_LENGTH));
        payload.append(formatString(" ", 24 - DISPLAY_COLOUR_LENGTH));
        payload.append(decimalFloatFormatter.format(reelFirstLayerNozzleTemperature));
        payload.append(decimalFloatFormatter.format(reelNozzleTemperature));
        payload.append(decimalFloatFormatter.format(reelFirstLayerBedTemperature));
        payload.append(decimalFloatFormatter.format(reelBedTemperature));
        payload.append(decimalFloatFormatter.format(reelAmbientTemperature));
        payload.append(decimalFloatFormatter.format(reelFilamentDiameter));
        payload.append(decimalFloatFormatter.format(reelFilamentMultiplier));
        payload.append(decimalFloatFormatter.format(reelFeedRateMultiplier));

        String friendlyNameEncoded;
        try
        {
            friendlyNameEncoded = StringToBase64Encoder.encode(friendlyName, FRIENDLY_NAME_LENGTH);
        } catch (UnsupportedEncodingException ex)
        {
            steno.error("Unable to encode string: " + friendlyName);
            friendlyNameEncoded = "";
        }
        payload.append(formatString(friendlyNameEncoded, FRIENDLY_NAME_LENGTH));
        String materialTypeInt = EnumStringConverter.intToString(0);
        if (materialType != null)
        {
            materialTypeInt = EnumStringConverter.intToString(materialType.ordinal());
        }
        payload.append(materialTypeInt);

        String paddedBlanks = formatString(" ", REEL_EEPROM_PADDING_LENGTH);
        payload.append(paddedBlanks);
        String remainingFilamentValue = decimalFloatFormatter.format(reelRemainingFilament);
        if (remainingFilamentValue.length() > 8)
        {
            String oldValue = remainingFilamentValue;
            remainingFilamentValue = remainingFilamentValue.substring(0, 8);
            steno.warning("Truncated remaining filament value from " + oldValue + " to "
                    + remainingFilamentValue);
        }
        payload.append(remainingFilamentValue);

        this.setMessagePayload(payload.toString());
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

        outputArray[0] = TxPacketTypeEnum.WRITE_REEL_0_EEPROM.getCommandByte();

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

    public String getFilamentID()
    {
        return filamentID;
    }

    public float getReelFirstLayerNozzleTemperature()
    {
        return reelFirstLayerNozzleTemperature;
    }

    public float getReelNozzleTemperature()
    {
        return reelNozzleTemperature;
    }

    public float getReelFirstLayerBedTemperature()
    {
        return reelFirstLayerBedTemperature;
    }

    public float getReelBedTemperature()
    {
        return reelBedTemperature;
    }

    public float getReelAmbientTemperature()
    {
        return reelAmbientTemperature;
    }

    public float getReelFilamentDiameter()
    {
        return reelFilamentDiameter;
    }

    public float getReelFilamentMultiplier()
    {
        return reelFilamentMultiplier;
    }

    public float getReelFeedRateMultiplier()
    {
        return reelFeedRateMultiplier;
    }

    public float getReelRemainingFilament()
    {
        return reelRemainingFilament;
    }

    public String getFriendlyName()
    {
        return friendlyName;
    }

    public MaterialType getMaterialType()
    {
        return materialType;
    }

    public String getDisplayColourString()
    {
        return displayColourString;
    }

    @Override
    public String toString()
    {
        StringBuilder outputString = new StringBuilder();
        
        outputString.append("Reel 0 write\n");
        outputString.append("Friendly Name: ");
        outputString.append(getFriendlyName());
        outputString.append('\n');
        outputString.append("Filament ID: ");
        outputString.append(getFilamentID());
        outputString.append('\n');
        outputString.append("Material Type: ");
        outputString.append(getMaterialType());
        outputString.append('\n');
        outputString.append("Ambient Temperature: ");
        outputString.append(getReelAmbientTemperature());
        outputString.append('\n');
        outputString.append("Bed Temperature: ");
        outputString.append(getReelBedTemperature());
        outputString.append('\n');
        outputString.append("Feed rate multiplier: ");
        outputString.append(getReelFeedRateMultiplier());
        outputString.append('\n');
        outputString.append("Filament diameter: ");
        outputString.append(getReelFilamentDiameter());
        outputString.append('\n');
        outputString.append("Remaining length: ");
        outputString.append(getReelRemainingFilament());
        outputString.append('\n');
        return outputString.toString();
    }
}
