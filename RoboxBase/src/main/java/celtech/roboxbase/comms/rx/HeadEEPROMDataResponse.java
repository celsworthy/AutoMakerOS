package celtech.roboxbase.comms.rx;

import celtech.roboxbase.comms.remote.FixedDecimalFloatFormat;
import celtech.roboxbase.comms.tx.WriteHeadEEPROM;
import celtech.roboxbase.printerControl.model.HeadEEPROMData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 *
 * @author ianhudson
 */
public class HeadEEPROMDataResponse extends RoboxRxPacket
{
    //V736 firmware
    //0x00: head type code (16)
    //0x10: serial number (24)
    //0x28: max temp Celsius (8)
    //0x30: thermistor beta (8)
    //0x38: thermistor tcal (8)
    //0x40: (left) nozzle 0 X offset (8)
    //0x48: (left) nozzle 0 Y offset (8)
    //0x50: (left) nozzle 0 Z offset (8)
    //0x58: (left) nozzle 0 B offset (8)
    //0x60: Filament 0 ID (8) e.g. PLARD057,  SPCMF001
    //0x68: Filament 1 ID (8)
    //0x70: (right) nozzle 1 X offset (8)
    //0x78: (right) nozzle 1 Y offset (8)
    //0x80: (right) nozzle 1 Z offset (8)    
    //0x88: (right) nozzle 1 B offset (8)
    //0x90: spare (24)
    //0xa8: melting temperature of material in nozzle heater 1 (8)
    //0xb0: melting temperature of material in nozzle heater 0 (8)
    //0xb8: hour counter (8)

    @JsonIgnore
    private final String charsetToUse = "US-ASCII";

    @JsonIgnore
    private final int decimalFloatFormatBytes = 8;
    @JsonIgnore
    private final int headTypeCodeBytes = 16;
    @JsonIgnore
    private final int uniqueIDBytes = 24;
    
    private HeadEEPROMData headEEPROMData = new HeadEEPROMData();

    public HeadEEPROMDataResponse()
    {
        super(RxPacketTypeEnum.HEAD_EEPROM_DATA, false, false);
    }

    public HeadEEPROMData getHeadEEPROMData()
    {
        return headEEPROMData;
    }

    public void setHeadEEPROMData(HeadEEPROMData headEEPROMData)
    {
        this.headEEPROMData = headEEPROMData;
    }

    @Override
    public boolean populatePacket(byte[] byteData, float requiredFirmwareVersion)
    {
        setMessagePayloadBytes(byteData);

        boolean success = false;

        FixedDecimalFloatFormat decimalFloatFormatter = new FixedDecimalFloatFormat();

        try
        {
            int byteOffset = 1;

            headEEPROMData.setHeadTypeCode((new String(byteData, byteOffset, headTypeCodeBytes, charsetToUse)).trim());
            byteOffset += headTypeCodeBytes;

            headEEPROMData.setUniqueID((new String(byteData, byteOffset, uniqueIDBytes, charsetToUse)).trim());
            byteOffset += uniqueIDBytes;

            String maxTempString = new String(byteData, byteOffset, decimalFloatFormatBytes,
                    charsetToUse);
            byteOffset += decimalFloatFormatBytes;
            try
            {
                headEEPROMData.setMaximumTemperature(decimalFloatFormatter.parse(maxTempString.trim()).floatValue());
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse maximum temperature - " + maxTempString);
            }

            String thermistorBetaString = new String(byteData, byteOffset, decimalFloatFormatBytes,
                    charsetToUse);
            byteOffset += decimalFloatFormatBytes;
            try
            {
                headEEPROMData.setThermistorBeta(decimalFloatFormatter.parse(thermistorBetaString.trim()).floatValue());
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse thermistor beta - " + thermistorBetaString);
            }

            String thermistorTCalString = new String(byteData, byteOffset, decimalFloatFormatBytes,
                    charsetToUse);
            byteOffset += decimalFloatFormatBytes;
            try
            {
                headEEPROMData.setThermistorTCal(decimalFloatFormatter.parse(thermistorTCalString.trim()).floatValue());
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse thermistor tcal - " + thermistorTCalString);
            }

            String nozzle1XOffsetString = new String(byteData, byteOffset, decimalFloatFormatBytes,
                    charsetToUse);
            byteOffset += decimalFloatFormatBytes;
            try
            {
                headEEPROMData.setNozzle1XOffset(decimalFloatFormatter.parse(nozzle1XOffsetString.trim()).floatValue());
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse right nozzle X offset - " + nozzle1XOffsetString);
            }

            String nozzle1YOffsetString = new String(byteData, byteOffset, decimalFloatFormatBytes,
                    charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                headEEPROMData.setNozzle1YOffset(decimalFloatFormatter.parse(nozzle1YOffsetString.trim()).floatValue());
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse right nozzle Y offset - " + nozzle1YOffsetString);
            }

            String nozzle1ZOffsetString = new String(byteData, byteOffset, decimalFloatFormatBytes,
                    charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                headEEPROMData.setNozzle1ZOffset(decimalFloatFormatter.parse(nozzle1ZOffsetString.trim()).floatValue());
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse right nozzle Z offset - " + nozzle1ZOffsetString);
            }

            String nozzle1BOffsetString = new String(byteData, byteOffset, decimalFloatFormatBytes,
                    charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                headEEPROMData.setNozzle1BOffset(decimalFloatFormatter.parse(nozzle1BOffsetString.trim()).floatValue());
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse right nozzle B offset - " + nozzle1BOffsetString);
            }

            //Empty section
            byteOffset += 16;

            String nozzle2XOffsetString = new String(byteData, byteOffset, decimalFloatFormatBytes,
                    charsetToUse);
            byteOffset += decimalFloatFormatBytes;
            try
            {
                headEEPROMData.setNozzle2XOffset(decimalFloatFormatter.parse(nozzle2XOffsetString.trim()).floatValue());
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse left nozzle X offset - " + nozzle2XOffsetString);
            }

            String nozzle2YOffsetString = new String(byteData, byteOffset, decimalFloatFormatBytes,
                    charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                headEEPROMData.setNozzle2YOffset(decimalFloatFormatter.parse(nozzle2YOffsetString.trim()).floatValue());
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse left nozzle Y offset - " + nozzle2YOffsetString);
            }

            String nozzle2ZOffsetString = new String(byteData, byteOffset, decimalFloatFormatBytes,
                    charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                headEEPROMData.setNozzle2ZOffset(decimalFloatFormatter.parse(nozzle2ZOffsetString.trim()).floatValue());
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse left nozzle Z offset - " + nozzle2ZOffsetString);
            }

            String nozzle2BOffsetString = new String(byteData, byteOffset, decimalFloatFormatBytes,
                    charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                headEEPROMData.setNozzle2BOffset(decimalFloatFormatter.parse(nozzle2BOffsetString.trim()).floatValue());
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse left nozzle B offset - " + nozzle2BOffsetString);
            }

            //Empty section
            byteOffset += 24;

            String lastFilamentTemperatureString1 = new String(byteData, byteOffset,
                    decimalFloatFormatBytes, charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                headEEPROMData.setLastFilamentTemperature1(decimalFloatFormatter.parse(
                        lastFilamentTemperatureString1.trim()).floatValue());
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse last filament temperature 1 - "
                        + lastFilamentTemperatureString1);
            }

            String lastFilamentTemperatureString0 = new String(byteData, byteOffset,
                    decimalFloatFormatBytes, charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                headEEPROMData.setLastFilamentTemperature0(decimalFloatFormatter.parse(
                        lastFilamentTemperatureString0.trim()).floatValue());
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse last filament temperature 0 - "
                        + lastFilamentTemperatureString0);
            }

            String hoursUsedString = new String(byteData, byteOffset, decimalFloatFormatBytes,
                    charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                headEEPROMData.setHeadHours(decimalFloatFormatter.parse(hoursUsedString.trim()).intValue());
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse hours used - " + hoursUsedString);
            }

            if (headEEPROMData.getUniqueID().length() == 24)
            {
                String inTheBeginning = headEEPROMData.getUniqueID();
                headEEPROMData.setWeekNumber(inTheBeginning.substring(8, 10));
                headEEPROMData.setYearNumber(inTheBeginning.substring(10, 12));
                headEEPROMData.setPONumber(inTheBeginning.substring(12, 19));
                headEEPROMData.setSerialNumber(inTheBeginning.substring(19, 23));
                headEEPROMData.setChecksum(inTheBeginning.substring(23, 24));
            }
            success = true;
        } catch (UnsupportedEncodingException ex)
        {
            steno.error("Failed to convert byte array to Head EEPROM Response");
        }

        return success;
    }

    @Override
    public String toString()
    {
        StringBuilder outputString = new StringBuilder();

        outputString.append(">>>>>>>>>>\n");
        outputString.append("Packet type:");
        outputString.append(getPacketType().name());
        outputString.append("\n");
        outputString.append(headEEPROMData.toString());
        outputString.append("\n");
        outputString.append(">>>>>>>>>>\n");

        return outputString.toString();
    }

    @JsonIgnore
    public float getMaximumTemperature()
    {
        return headEEPROMData.getMaximumTemperature();
    }

    @JsonIgnore
    public float getNozzle1XOffset()
    {
        return headEEPROMData.getNozzle1XOffset();
    }

    @JsonIgnore
    public float getNozzle1YOffset()
    {
        return headEEPROMData.getNozzle1YOffset();
    }

    @JsonIgnore
    public float getNozzle1ZOffset()
    {
        return headEEPROMData.getNozzle1ZOffset();
    }

    @JsonIgnore
    public float getNozzle1BOffset()
    {
        return headEEPROMData.getNozzle1BOffset();
    }

    @JsonIgnore
    public float getNozzle2XOffset()
    {
        return headEEPROMData.getNozzle2XOffset();
    }

    @JsonIgnore
    public float getNozzle2YOffset()
    {
        return headEEPROMData.getNozzle2YOffset();
    }

    @JsonIgnore
    public float getNozzle2ZOffset()
    {
        return headEEPROMData.getNozzle2ZOffset();
    }

    @JsonIgnore
    public float getNozzle2BOffset()
    {
        return headEEPROMData.getNozzle2BOffset();
    }

    @JsonIgnore
    public float getHeadHours()
    {
        return headEEPROMData.getHeadHours();
    }

    @JsonIgnore
    public String getHeadTypeCode()
    {
        return headEEPROMData.getHeadTypeCode();
    }

    @JsonIgnore
    public String getUniqueID()
    {
        return headEEPROMData.getUniqueID();
    }

    @JsonIgnore
    public float getThermistorBeta()
    {
        return headEEPROMData.getThermistorBeta();
    }

    @JsonIgnore
    public float getThermistorTCal()
    {
        return headEEPROMData.getThermistorTCal();
    }

    @JsonIgnore
    public float getLastFilamentTemperature(int nozzleHeaterNumber)
    {
        return headEEPROMData.getLastFilamentTemperature(nozzleHeaterNumber);
    }

    @JsonIgnore
    public String getFilament0ID()
    {
        return headEEPROMData.getFilament0ID();
    }

    @JsonIgnore
    public String getFilament1ID()
    {
        return headEEPROMData.getFilament1ID();
    }

    @JsonIgnore
    public String getFilamentID(int nozzleHeaterNumber)
    {
        return headEEPROMData.getFilamentID(nozzleHeaterNumber);
    }

    @JsonIgnore
    public void updateContents(String headTypeCodeIn,
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
        headEEPROMData.setHeadTypeCode(headTypeCodeIn);
        headEEPROMData.setUniqueID(uniqueIdIn);

        if (numberOfHeaters > 0)
        {
            headEEPROMData.setMaximumTemperature(maxTempIn);
            headEEPROMData.setThermistorBeta(betaIn);
            headEEPROMData.setThermistorTCal(tCalIn);
            headEEPROMData.setLastFilamentTemperature0(lastFilamentTemp0In);
            headEEPROMData.setFilament0ID(filamentID0In);

            if (numberOfHeaters > 1)
            {
                headEEPROMData.setLastFilamentTemperature1(lastFilamentTemp1In);
                headEEPROMData.setFilament1ID(filamentID1In);
            }
        }
        headEEPROMData.setHeadHours(hoursUsedIn);

        if (numberOfNozzles > 0)
        {
            headEEPROMData.setNozzle1XOffset(nozzle1XOffsetIn);
            headEEPROMData.setNozzle1YOffset(nozzle1YOffsetIn);
            headEEPROMData.setNozzle1ZOffset(nozzle1ZOffsetIn);
            headEEPROMData.setNozzle1BOffset(nozzle1BOffsetIn);
        }

        if (numberOfNozzles > 1)
        {
            headEEPROMData.setNozzle2XOffset(nozzle2XOffsetIn);
            headEEPROMData.setNozzle2YOffset(nozzle2YOffsetIn);
            headEEPROMData.setNozzle2ZOffset(nozzle2ZOffsetIn);
            headEEPROMData.setNozzle2BOffset(nozzle2BOffsetIn);
        }
    }

    @JsonIgnore
    public void setHeadTypeCode(String headTypeCode)
    {
        headEEPROMData.setHeadTypeCode(headTypeCode);
    }

    @JsonIgnore
    public void setUniqueID(String uniqueID)
    {
        headEEPROMData.setUniqueID(uniqueID);
    }

    @JsonIgnore
    public void setMaximumTemperature(float maximumTemperature)
    {
        headEEPROMData.setMaximumTemperature(maximumTemperature);
    }

    @JsonIgnore
    public void setThermistorBeta(float thermistorBeta)
    {
        headEEPROMData.setThermistorBeta(thermistorBeta);
    }

    @JsonIgnore
    public void setThermistorTCal(float thermistorTCal)
    {
        headEEPROMData.setThermistorTCal(thermistorTCal);
    }

    @JsonIgnore
    public void setNozzle1XOffset(float nozzle1XOffset)
    {
        headEEPROMData.setNozzle1XOffset(nozzle1XOffset);
    }

    @JsonIgnore
    public void setNozzle1YOffset(float nozzle1YOffset)
    {
        headEEPROMData.setNozzle1YOffset(nozzle1YOffset);
    }

    @JsonIgnore
    public void setNozzle1ZOffset(float nozzle1ZOffset)
    {
        headEEPROMData.setNozzle1ZOffset(nozzle1ZOffset);
    }

    @JsonIgnore
    public void setNozzle1BOffset(float nozzle1BOffset)
    {
        headEEPROMData.setNozzle1BOffset(nozzle1BOffset);
    }

    @JsonIgnore
    public void setNozzle2XOffset(float nozzle2XOffset)
    {
        headEEPROMData.setNozzle2XOffset(nozzle2XOffset);
    }

    @JsonIgnore
    public void setNozzle2YOffset(float nozzle2YOffset)
    {
        headEEPROMData.setNozzle2YOffset(nozzle2YOffset);
    }

    @JsonIgnore
    public void setNozzle2ZOffset(float nozzle2ZOffset)
    {
        headEEPROMData.setNozzle2ZOffset(nozzle2ZOffset);
    }

    @JsonIgnore
    public void setNozzle2BOffset(float nozzle2BOffset)
    {
        headEEPROMData.setNozzle2BOffset(nozzle2BOffset);
    }

    @JsonIgnore
    public void setLastFilamentTemperature0(float lastFilamentTemperature)
    {
        headEEPROMData.setLastFilamentTemperature0(lastFilamentTemperature);
    }

    @JsonIgnore
    public void setLastFilamentTemperature1(float lastFilamentTemperature)
    {
        headEEPROMData.setLastFilamentTemperature1(lastFilamentTemperature);
    }

    @JsonIgnore
    public void setFilament0ID(String filamentID)
    {
        headEEPROMData.setFilament0ID(filamentID);
    }

    @JsonIgnore
    public void setFilament1ID(String filamentID)
    {
        headEEPROMData.setFilament1ID(filamentID);
    }

    @JsonIgnore
    public void setHeadHours(float hoursUsed)
    {
        headEEPROMData.setHeadHours(hoursUsed);
    }

    @JsonIgnore
    public String getWeekNumber()
    {
        return headEEPROMData.getWeekNumber();
    }

    @JsonIgnore
    public void setWeekNumber(String weekNumber)
    {
        headEEPROMData.setWeekNumber(weekNumber);
    }

    @JsonIgnore
    public String getYearNumber()
    {
        return headEEPROMData.getYearNumber();
    }

    @JsonIgnore
    public void setYearNumber(String yearNumber)
    {
        headEEPROMData.setYearNumber(yearNumber);
    }

    @JsonIgnore
    public String getPONumber()
    {
        return headEEPROMData.getPONumber();
    }

    @JsonIgnore
    public void setPONumber(String PONumber)
    {
        headEEPROMData.setPONumber(PONumber);
    }

    @JsonIgnore
    public String getSerialNumber()
    {
        return headEEPROMData.getSerialNumber();
    }

    @JsonIgnore
    public void setSerialNumber(String serialNumber)
    {
        headEEPROMData.setSerialNumber(serialNumber);
    }

    @JsonIgnore
    public String getChecksum()
    {
        return headEEPROMData.getChecksum();
    }

    @JsonIgnore
    public void setChecksum(String checksum)
    {
        headEEPROMData.setChecksum(checksum);
    }

    /**
     * This method is used to populate the response data prior to head update It
     * should be used for test purposes ONLY.
     *
     * @param headWriteCommand
     */
    @JsonIgnore
    public void updateFromWrite(WriteHeadEEPROM headWriteCommand)
    {
        //TODO ensure this copes with all data
        headEEPROMData.setHeadTypeCode(headWriteCommand.getHeadTypeCode());
        headEEPROMData.setUniqueID(headWriteCommand.getHeadUniqueID());

        headEEPROMData.setMaximumTemperature(headWriteCommand.getMaximumTemperature());
        headEEPROMData.setThermistorBeta(headWriteCommand.getThermistorBeta());
        headEEPROMData.setThermistorTCal(headWriteCommand.getThermistorTCal());
        headEEPROMData.setLastFilamentTemperature0(headWriteCommand.getLastFilamentTemperature0());
        headEEPROMData.setLastFilamentTemperature1(headWriteCommand.getLastFilamentTemperature1());

        headEEPROMData.setNozzle1XOffset(headWriteCommand.getNozzle1XOffset());
        headEEPROMData.setNozzle1YOffset(headWriteCommand.getNozzle1YOffset());
        headEEPROMData.setNozzle1ZOffset(headWriteCommand.getNozzle1ZOffset());
        headEEPROMData.setNozzle1BOffset(headWriteCommand.getNozzle1BOffset());

        headEEPROMData.setFilament0ID(headWriteCommand.getFilament0ID());
        headEEPROMData.setFilament1ID(headWriteCommand.getFilament1ID());

        headEEPROMData.setNozzle2XOffset(headWriteCommand.getNozzle2XOffset());
        headEEPROMData.setNozzle2YOffset(headWriteCommand.getNozzle2YOffset());
        headEEPROMData.setNozzle2ZOffset(headWriteCommand.getNozzle2ZOffset());
        headEEPROMData.setNozzle2BOffset(headWriteCommand.getNozzle2BOffset());
    }

    @Override
    public int packetLength(float requiredFirmwareVersion)
    {
        return 193;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(15, 39).
                append(headEEPROMData).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof HeadEEPROMDataResponse))
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }

        HeadEEPROMDataResponse rhs = (HeadEEPROMDataResponse) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                append(headEEPROMData, rhs.headEEPROMData).
                isEquals();
    }
}
