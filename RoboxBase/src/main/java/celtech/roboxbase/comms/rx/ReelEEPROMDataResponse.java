package celtech.roboxbase.comms.rx;

import static celtech.roboxbase.utils.ColourStringConverter.stringToColor;
import static celtech.roboxbase.comms.rx.RoboxRxPacket.steno;
import static celtech.roboxbase.comms.tx.WriteReel0EEPROM.FRIENDLY_NAME_LENGTH;
import static celtech.roboxbase.comms.tx.WriteReel0EEPROM.MATERIAL_TYPE_LENGTH;
import static celtech.roboxbase.comms.tx.WriteReel0EEPROM.REEL_EEPROM_PADDING_LENGTH;
import celtech.roboxbase.MaterialType;
import celtech.roboxbase.comms.remote.EnumStringConverter;
import celtech.roboxbase.comms.remote.FixedDecimalFloatFormat;
import celtech.roboxbase.comms.remote.StringToBase64Encoder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 *
 * @author ianhudson
 */
public abstract class ReelEEPROMDataResponse extends RoboxRxPacket
{

    @JsonIgnore
    private final String charsetToUse = "US-ASCII";

    @JsonIgnore
    private final int decimalFloatFormatBytes = 8;
    @JsonIgnore
    private final int materialTypeCodeBytes = 16;
    @JsonIgnore
    private final int uniqueIDBytes = 24;
    @JsonIgnore
    private final int colourBytes = 6;

    private String filamentID;
    private int firstLayerNozzleTemperature;
    private int nozzleTemperature;
    private int firstLayerBedTemperature;
    private int bedTemperature;
    private int ambientTemperature;
    private float filamentDiameter;
    private float filamentMultiplier;
    private float feedRateMultiplier;
    private float remainingFilament;
    private MaterialType reelMaterialType;
    private String displayColourString;
    private String friendlyName;

    private int reelNumber = 0;

    public ReelEEPROMDataResponse(RxPacketTypeEnum packetType, boolean includeSequenceNumber, boolean includeCharsOfDataInOutput)
    {
        super(packetType, includeSequenceNumber, includeCharsOfDataInOutput);
    }

    /**
     *
     * @param byteData
     * @return
     */
    
    public boolean populatePacket(byte[] byteData, float requiredFirmwareVersion)
    {
        setMessagePayloadBytes(byteData);

        boolean success = false;

        FixedDecimalFloatFormat decimalFloatFormatter = new FixedDecimalFloatFormat();

        try
        {
            int byteOffset = 1;

            filamentID = (new String(byteData, byteOffset, materialTypeCodeBytes, charsetToUse)).trim();
            byteOffset += materialTypeCodeBytes;

            String displayColourString = new String(byteData, byteOffset, colourBytes, charsetToUse);
            this.displayColourString = stringToColor(displayColourString).toString();

            byteOffset += uniqueIDBytes;

            String firstLayerNozzleTempString = new String(byteData, byteOffset, decimalFloatFormatBytes, charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                firstLayerNozzleTemperature = decimalFloatFormatter.parse(firstLayerNozzleTempString.trim()).intValue();
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse first layer nozzle temperature - " + firstLayerNozzleTempString);
            }

            String printNozzleTempString = new String(byteData, byteOffset, decimalFloatFormatBytes, charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                nozzleTemperature = decimalFloatFormatter.parse(printNozzleTempString.trim()).intValue();
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse nozzle temperature - " + printNozzleTempString);
            }

            String firstLayerBedTempString = new String(byteData, byteOffset, decimalFloatFormatBytes, charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                firstLayerBedTemperature = decimalFloatFormatter.parse(firstLayerBedTempString.trim()).intValue();
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse first layer bed temperature - " + firstLayerBedTempString);
            }

            String bedTempString = new String(byteData, byteOffset, decimalFloatFormatBytes, charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                bedTemperature = decimalFloatFormatter.parse(bedTempString.trim()).intValue();
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse bed temperature - " + bedTempString);
            }

            String ambientTempString = new String(byteData, byteOffset, decimalFloatFormatBytes, charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                ambientTemperature = decimalFloatFormatter.parse(ambientTempString.trim()).intValue();
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse ambient temperature - " + ambientTempString);
            }

            String filamentDiameterString = new String(byteData, byteOffset, decimalFloatFormatBytes, charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                filamentDiameter = decimalFloatFormatter.parse(filamentDiameterString.trim()).floatValue();
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse filament diameter - " + filamentDiameterString);
            }

            String filamentMultiplierString = new String(byteData, byteOffset, decimalFloatFormatBytes, charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                filamentMultiplier = decimalFloatFormatter.parse(filamentMultiplierString.trim()).floatValue();
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse max extrusion rate - " + filamentMultiplierString);
            }

            String feedRateMultiplierString = new String(byteData, byteOffset, decimalFloatFormatBytes, charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                feedRateMultiplier = decimalFloatFormatter.parse(feedRateMultiplierString.trim()).floatValue();
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse extrusion multiplier - " + feedRateMultiplierString);
            }

            String encodedFriendlyName = new String(byteData, byteOffset, FRIENDLY_NAME_LENGTH, charsetToUse);
            try
            {
                friendlyName = StringToBase64Encoder.decode(encodedFriendlyName);
            } catch (IllegalArgumentException ex)
            {
                steno.error("Failed to decode material name.");
                friendlyName = "";
            }
            byteOffset += FRIENDLY_NAME_LENGTH;

            //Handle case where reelFriendlyName has not yet been set on EEPROM
            if (friendlyName.length() == 0)
            {
                friendlyName = filamentID;
            }

            String intMaterialTypeString = new String(byteData, byteOffset, MATERIAL_TYPE_LENGTH, charsetToUse);
            int intMaterialType = EnumStringConverter.stringToInt(intMaterialTypeString);
            try
            {
                reelMaterialType = MaterialType.values()[intMaterialType];
            } catch (ArrayIndexOutOfBoundsException ex)
            {
                steno.error("Couldn't parse material type from reel");
                reelMaterialType = MaterialType.values()[0];
            }
            byteOffset += MATERIAL_TYPE_LENGTH;

            byteOffset += REEL_EEPROM_PADDING_LENGTH;

            String remainingLengthString = new String(byteData, byteOffset, decimalFloatFormatBytes, charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                remainingFilament = decimalFloatFormatter.parse(remainingLengthString.trim()).intValue();
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse remaining length - " + remainingLengthString);
            }

            success = true;
        } catch (UnsupportedEncodingException ex)
        {
            steno.error("Failed to convert byte array to Printer ID Response");
        }

        return success;
    }

    /**
     *
     * @return
     */
    
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
    
    public String getFilamentID()
    {
        return filamentID;
    }

    
    public String getFriendlyName()
    {
        return friendlyName;
    }

    /**
     *
     * @return
     */
    
    public int getFirstLayerNozzleTemperature()
    {
        return firstLayerNozzleTemperature;
    }

    /**
     *
     * @return
     */
    
    public int getNozzleTemperature()
    {
        return nozzleTemperature;
    }

    /**
     *
     * @return
     */
    
    public int getFirstLayerBedTemperature()
    {
        return firstLayerBedTemperature;
    }

    /**
     *
     * @return
     */
    
    public int getBedTemperature()
    {
        return bedTemperature;
    }

    /**
     *
     * @return
     */
    
    public int getAmbientTemperature()
    {
        return ambientTemperature;
    }

    /**
     *
     * @return
     */
    
    public float getFilamentDiameter()
    {
        return filamentDiameter;
    }

    /**
     *
     * @return
     */
    
    public float getFilamentMultiplier()
    {
        return filamentMultiplier;
    }

    /**
     *
     * @return
     */
    
    public float getFeedRateMultiplier()
    {
        return feedRateMultiplier;
    }

    /**
     *
     * @return
     */
    
    public float getRemainingFilament()
    {
        return remainingFilament;
    }

    /**
     * @return the reelMaterialType
     */
    
    public MaterialType getMaterialType()
    {
        return reelMaterialType;
    }

    
    public String getDisplayColourString()
    {
        return displayColourString;
    }

    /**
     *
     * @param reelFilamentIDIn
     * @param reelFirstLayerNozzleTemperatureIn
     * @param reelNozzleTemperatureIn
     * @param reelFirstLayerBedTemperatureIn
     * @param reelBedTemperatureIn
     * @param reelAmbientTemperatureIn
     * @param reelFilamentDiameterIn
     * @param reelFilamentMultiplierIn
     * @param reelFeedRateMultiplierIn
     * @param reelRemainingFilamentIn
     * @param reelMaterialTypeIn
     * @param reelDisplayColourIn
     * @param reelFriendlyNameIn
     */
    
    public void updateContents(
            String reelFilamentIDIn,
            int reelFirstLayerNozzleTemperatureIn,
            int reelNozzleTemperatureIn,
            int reelFirstLayerBedTemperatureIn,
            int reelBedTemperatureIn,
            int reelAmbientTemperatureIn,
            float reelFilamentDiameterIn,
            float reelFilamentMultiplierIn,
            float reelFeedRateMultiplierIn,
            float reelRemainingFilamentIn,
            MaterialType reelMaterialTypeIn,
            String reelDisplayColourIn,
            String reelFriendlyNameIn)
    {
        filamentID = reelFilamentIDIn;
        firstLayerNozzleTemperature = reelFirstLayerNozzleTemperatureIn;
        nozzleTemperature = reelNozzleTemperatureIn;
        firstLayerBedTemperature = reelFirstLayerBedTemperatureIn;
        bedTemperature = reelBedTemperatureIn;
        ambientTemperature = reelAmbientTemperatureIn;
        filamentDiameter = reelFilamentDiameterIn;
        filamentMultiplier = reelFilamentMultiplierIn;
        feedRateMultiplier = reelFeedRateMultiplierIn;
        remainingFilament = reelRemainingFilamentIn;
        reelMaterialType = reelMaterialTypeIn;
        displayColourString = reelDisplayColourIn;
        friendlyName = reelFriendlyNameIn;
    }

    
    public void setFilamentID(String reelFilamentID)
    {
        this.filamentID = reelFilamentID;
    }

    
    public void setFirstLayerNozzleTemperature(int reelFirstLayerNozzleTemperature)
    {
        this.firstLayerNozzleTemperature = reelFirstLayerNozzleTemperature;
    }

    
    public void setNozzleTemperature(int reelNozzleTemperature)
    {
        this.nozzleTemperature = reelNozzleTemperature;
    }

    
    public void setFirstLayerBedTemperature(int reelFirstLayerBedTemperature)
    {
        this.firstLayerBedTemperature = reelFirstLayerBedTemperature;
    }

    
    public void setBedTemperature(int reelBedTemperature)
    {
        this.bedTemperature = reelBedTemperature;
    }

    
    public void setAmbientTemperature(int reelAmbientTemperature)
    {
        this.ambientTemperature = reelAmbientTemperature;
    }

    
    public void setFilamentDiameter(float reelFilamentDiameter)
    {
        this.filamentDiameter = reelFilamentDiameter;
    }

    
    public void setFilamentMultiplier(float reelFilamentMultiplier)
    {
        this.filamentMultiplier = reelFilamentMultiplier;
    }

    
    public void setFeedRateMultiplier(float reelFeedRateMultiplier)
    {
        this.feedRateMultiplier = reelFeedRateMultiplier;
    }

    
    public void setRemainingFilament(float reelRemainingFilament)
    {
        this.remainingFilament = reelRemainingFilament;
    }

    
    public void setMaterialType(MaterialType reelMaterialType)
    {
        this.reelMaterialType = reelMaterialType;
    }

    public void setDisplayColourString(String reelDisplayColour)
    {
        this.displayColourString = reelDisplayColour;
    }

    
    public void setFriendlyName(String reelFriendlyName)
    {
        this.friendlyName = reelFriendlyName;
    }

    
    public void setReelNumber(int reelNumber)
    {
        this.reelNumber = reelNumber;
    }

    
    public int getReelNumber()
    {
        return reelNumber;
    }

    
    public int packetLength(float requiredFirmwareVersion)
    {
        return 193;
    }

    
    public int hashCode()
    {
        return new HashCodeBuilder(19, 31).
                append(filamentID).
                append(firstLayerNozzleTemperature).
                append(nozzleTemperature).
                append(firstLayerBedTemperature).
                append(bedTemperature).
                append(ambientTemperature).
                append(filamentDiameter).
                append(filamentMultiplier).
                append(feedRateMultiplier).
                append(remainingFilament).
                append(reelMaterialType).
                append(displayColourString).
                append(friendlyName).
                append(reelNumber).
                toHashCode();
    }

    
    public boolean equals(Object obj)
    {
        if (!(obj instanceof ReelEEPROMDataResponse))
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }

        ReelEEPROMDataResponse rhs = (ReelEEPROMDataResponse) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                append(filamentID, rhs.filamentID).
                append(firstLayerNozzleTemperature, rhs.firstLayerNozzleTemperature).
                append(nozzleTemperature, rhs.nozzleTemperature).
                append(firstLayerBedTemperature, rhs.firstLayerBedTemperature).
                append(bedTemperature, rhs.bedTemperature).
                append(ambientTemperature, rhs.ambientTemperature).
                append(filamentDiameter, rhs.filamentDiameter).
                append(filamentMultiplier, rhs.filamentMultiplier).
                append(feedRateMultiplier, rhs.feedRateMultiplier).
                append(remainingFilament, rhs.remainingFilament).
                append(reelMaterialType, rhs.reelMaterialType).
                append(displayColourString, rhs.displayColourString).
                append(friendlyName, rhs.friendlyName).
                append(reelNumber, rhs.reelNumber).
                isEquals();
    }

}
