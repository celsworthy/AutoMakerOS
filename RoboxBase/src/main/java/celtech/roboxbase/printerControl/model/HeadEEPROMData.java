package celtech.roboxbase.printerControl.model;

import org.apache.commons.lang.builder.EqualsBuilder;

/**
 *
 * @author ianhudson
 */
public class HeadEEPROMData
{
    private String headTypeCode;
    private String uniqueID;
    private String weekNumber = "";
    private String yearNumber = "";
    private String PONumber = "";
    private String serialNumber = "";
    private String checksum = "";
    private float maximumTemperature = 0;
    private float thermistorBeta = 0;
    private float thermistorTCal = 0;

    private float nozzle1XOffset = 0;
    private float nozzle1YOffset = 0;
    private float nozzle1ZOffset = 0;
    private float nozzle1BOffset = 0;

    private String filament0ID = "";
    private String filament1ID = "";

    private float nozzle2XOffset = 0;
    private float nozzle2YOffset = 0;
    private float nozzle2ZOffset = 0;
    private float nozzle2BOffset = 0;

    private float lastFilamentTemperature0 = 0;
    private float lastFilamentTemperature1 = 0;
    private float headHours = 0;

    public String getHeadTypeCode()
    {
        return headTypeCode;
    }

    public void setHeadTypeCode(String headTypeCode)
    {
        this.headTypeCode = headTypeCode;
    }

    public String getUniqueID()
    {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID)
    {
        this.uniqueID = uniqueID;
    }

    public String getWeekNumber()
    {
        return weekNumber;
    }

    public void setWeekNumber(String weekNumber)
    {
        this.weekNumber = weekNumber;
    }

    public String getYearNumber()
    {
        return yearNumber;
    }

    public void setYearNumber(String yearNumber)
    {
        this.yearNumber = yearNumber;
    }

    public String getPONumber()
    {
        return PONumber;
    }

    public void setPONumber(String PONumber)
    {
        this.PONumber = PONumber;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public String getChecksum()
    {
        return checksum;
    }

    public void setChecksum(String checksum)
    {
        this.checksum = checksum;
    }

    public float getMaximumTemperature()
    {
        return maximumTemperature;
    }

    public void setMaximumTemperature(float maximumTemperature)
    {
        this.maximumTemperature = maximumTemperature;
    }

    public float getThermistorBeta()
    {
        return thermistorBeta;
    }

    public void setThermistorBeta(float thermistorBeta)
    {
        this.thermistorBeta = thermistorBeta;
    }

    public float getThermistorTCal()
    {
        return thermistorTCal;
    }

    public void setThermistorTCal(float thermistorTCal)
    {
        this.thermistorTCal = thermistorTCal;
    }

    public float getNozzle1XOffset()
    {
        return nozzle1XOffset;
    }

    public void setNozzle1XOffset(float nozzle1XOffset)
    {
        this.nozzle1XOffset = nozzle1XOffset;
    }

    public float getNozzle1YOffset()
    {
        return nozzle1YOffset;
    }

    public void setNozzle1YOffset(float nozzle1YOffset)
    {
        this.nozzle1YOffset = nozzle1YOffset;
    }

    public float getNozzle1ZOffset()
    {
        return nozzle1ZOffset;
    }

    public void setNozzle1ZOffset(float nozzle1ZOffset)
    {
        this.nozzle1ZOffset = nozzle1ZOffset;
    }

    public float getNozzle1BOffset()
    {
        return nozzle1BOffset;
    }

    public void setNozzle1BOffset(float nozzle1BOffset)
    {
        this.nozzle1BOffset = nozzle1BOffset;
    }

    public String getFilament0ID()
    {
        return filament0ID;
    }

    public void setFilament0ID(String filament0ID)
    {
        this.filament0ID = filament0ID;
    }

    public String getFilament1ID()
    {
        return filament1ID;
    }

    public void setFilament1ID(String filament1ID)
    {
        this.filament1ID = filament1ID;
    }

    public String getFilamentID(int nozzleHeaterNumber)
    {
        if (nozzleHeaterNumber == 0)
        {
            return filament0ID;
        } else if (nozzleHeaterNumber == 1)
        {
            return filament1ID;
        } else
        {
            throw new RuntimeException("unrecognised nozzle heater number: " + nozzleHeaterNumber);
        }
    }

    public float getNozzle2XOffset()
    {
        return nozzle2XOffset;
    }

    public void setNozzle2XOffset(float nozzle2XOffset)
    {
        this.nozzle2XOffset = nozzle2XOffset;
    }

    public float getNozzle2YOffset()
    {
        return nozzle2YOffset;
    }

    public void setNozzle2YOffset(float nozzle2YOffset)
    {
        this.nozzle2YOffset = nozzle2YOffset;
    }

    public float getNozzle2ZOffset()
    {
        return nozzle2ZOffset;
    }

    public void setNozzle2ZOffset(float nozzle2ZOffset)
    {
        this.nozzle2ZOffset = nozzle2ZOffset;
    }

    public float getNozzle2BOffset()
    {
        return nozzle2BOffset;
    }

    public void setNozzle2BOffset(float nozzle2BOffset)
    {
        this.nozzle2BOffset = nozzle2BOffset;
    }

    public float getLastFilamentTemperature0()
    {
        return lastFilamentTemperature0;
    }

    public void setLastFilamentTemperature0(float lastFilamentTemperature0)
    {
        this.lastFilamentTemperature0 = lastFilamentTemperature0;
    }

    public float getLastFilamentTemperature1()
    {
        return lastFilamentTemperature1;
    }

    public void setLastFilamentTemperature1(float lastFilamentTemperature1)
    {
        this.lastFilamentTemperature1 = lastFilamentTemperature1;
    }

    public float getLastFilamentTemperature(int nozzleHeaterNumber)
    {
        if (nozzleHeaterNumber == 0)
        {
            return lastFilamentTemperature0;
        } else if (nozzleHeaterNumber == 1)
        {
            return lastFilamentTemperature1;
        } else
        {
            throw new RuntimeException("unrecognised nozzle heater number: " + nozzleHeaterNumber);
        }
    }

    public float getHeadHours()
    {
        return headHours;
    }

    public void setHeadHours(float headHours)
    {
        this.headHours = headHours;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof HeadEEPROMData))
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }

        HeadEEPROMData rhs = (HeadEEPROMData) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                append(headTypeCode, rhs.headTypeCode).
                append(uniqueID, rhs.uniqueID).
                append(weekNumber, rhs.weekNumber).
                append(yearNumber, rhs.yearNumber).
                append(PONumber, rhs.PONumber).
                append(serialNumber, rhs.serialNumber).
                append(checksum, rhs.checksum).
                append(maximumTemperature, rhs.maximumTemperature).
                append(thermistorBeta, rhs.thermistorBeta).
                append(thermistorTCal, rhs.thermistorTCal).
                append(nozzle1XOffset, rhs.nozzle1XOffset).
                append(nozzle1YOffset, rhs.nozzle1YOffset).
                append(nozzle1ZOffset, rhs.nozzle1ZOffset).
                append(nozzle1BOffset, rhs.nozzle1BOffset).
                append(filament0ID, rhs.filament0ID).
                append(filament1ID, rhs.filament1ID).
                append(nozzle2XOffset, rhs.nozzle2XOffset).
                append(nozzle2YOffset, rhs.nozzle2YOffset).
                append(nozzle2ZOffset, rhs.nozzle2ZOffset).
                append(nozzle2BOffset, rhs.nozzle2BOffset).
                append(lastFilamentTemperature0, rhs.lastFilamentTemperature0).
                append(lastFilamentTemperature1, rhs.lastFilamentTemperature1).
                append(headHours, rhs.headHours).
                isEquals();
    }

    @Override
    public String toString()
    {
        StringBuilder outputString = new StringBuilder();
        outputString.append("HeadType: ");
        outputString.append(headTypeCode);
        outputString.append("ID:");
        outputString.append(uniqueID);

        return outputString.toString();
    }

}
