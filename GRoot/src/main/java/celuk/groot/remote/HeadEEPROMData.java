package celuk.groot.remote;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author taldhous
 */
public class HeadEEPROMData
{
    private String printerID;

    private String name;
    private String typeCode;
    private String serialNumber;
    private String week;
    private String year;
    private String PONumber;
    private String checksum;
    private String uniqueID;
    private int heaterCount;
    private int nozzleCount;
    private boolean valveFitted;
    private boolean dualMaterialHead;
    private float maxTemp = -1.0F;
    private float beta = -1.0F;
    private float tCal = -1.0F;
    private float hourCount = -1.0F;
    private float leftNozzleXOffset = -1.0F;
    private float leftNozzleYOffset = -1.0F;
    private float leftNozzleZOverrun = -1.0F;
    private float leftNozzleBOffset = -1.0F;
    private float leftNozzleLastFTemp = -1.0F;
    private float rightNozzleXOffset = -1.0F;
    private float rightNozzleYOffset = -1.0F;
    private float rightNozzleZOverrun = -1.0F;
    private float rightNozzleBOffset = -1.0F;
    private float rightNozzleLastFTemp = -1.0F;

    public HeadEEPROMData()
    {
        // Jackson deserialization
    }
    
    @JsonIgnore
    public HeadEEPROMData duplicate() {
        HeadEEPROMData clone = new HeadEEPROMData();
        clone.printerID = this.printerID;
        clone.name = this.name;
        clone.typeCode = this.typeCode;
        clone.serialNumber = this.serialNumber;
        clone.week = this.week;
        clone.year = this.year;
        clone.PONumber = this.PONumber;
        clone.checksum = this.checksum;
        clone.uniqueID = this.uniqueID;
        clone.heaterCount = this.heaterCount;
        clone.nozzleCount = this.nozzleCount;
        clone.valveFitted = this.valveFitted;
        clone.dualMaterialHead = this.dualMaterialHead;
        clone.maxTemp = this.maxTemp;
        clone.beta = this.beta;
        clone.tCal = this.tCal;
        clone.hourCount = this.hourCount;
        clone.leftNozzleXOffset = this.leftNozzleXOffset;
        clone.leftNozzleYOffset = this.leftNozzleYOffset;
        clone.leftNozzleZOverrun = this.leftNozzleZOverrun;
        clone.leftNozzleBOffset = this.leftNozzleBOffset;
        clone.leftNozzleLastFTemp = this.leftNozzleLastFTemp;
        clone.rightNozzleXOffset = this.rightNozzleXOffset;
        clone.rightNozzleYOffset = this.rightNozzleYOffset;
        clone.rightNozzleZOverrun = this.rightNozzleZOverrun;
        clone.rightNozzleBOffset = this.rightNozzleBOffset;
        clone.rightNozzleLastFTemp = this.rightNozzleLastFTemp;

        return clone;
    }

    @JsonProperty
    public String getPrinterID()
    {
        return printerID;
    }

    @JsonProperty
    public void setPrinterID(String printerID)
    {
        this.printerID = printerID;
    }

    @JsonProperty
    public String getName()
    {
        return name;
    }

    @JsonProperty
    public void setName(String name)
    {
        this.name = name;
    }
    
    @JsonProperty
    public String getTypeCode()
    {
        return typeCode;
    }

    @JsonProperty
    public void setTypeCode(String typeCode)
    {
        this.typeCode = typeCode;
    }
    
    @JsonProperty
    public boolean getValveFitted()
    {
        return valveFitted;
    }

    @JsonProperty
    public void setValveFitted(boolean valveFitted)
    {
        this.valveFitted = valveFitted;
    }

    @JsonProperty
    public String getSerialNumber()
    {
        return serialNumber;
    }

    @JsonProperty
    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    @JsonProperty
    public String getWeek()
    {
        return week;
    }

    @JsonProperty
    public void setWeek(String week)
    {
        this.week = week;
    }

    @JsonProperty
    public String getYear()
    {
        return year;
    }

    @JsonProperty
    public void setYear(String year)
    {
        this.year = year;
    }

    @JsonProperty
    public String getPONumber()
    {
        return PONumber;
    }

    @JsonProperty
    public void setPONumber(String PONumber)
    {
        this.PONumber = PONumber;
    }

    @JsonProperty
    public String getChecksum()
    {
        return checksum;
    }

    @JsonProperty
    public void setChecksum(String checksum)
    {
        this.checksum = checksum;
    }

    @JsonProperty
    public String getUniqueID()
    {
        return uniqueID;
    }

    @JsonProperty
    public void setUniqueID(String uniqueID)
    {
        this.uniqueID = uniqueID;
    }
 
    @JsonProperty
    public boolean getDualMaterialHead()
    {
        return dualMaterialHead;
    }

    @JsonProperty
    public void setDualMaterialHead(boolean dualMaterialHead)
    {
        this.dualMaterialHead = dualMaterialHead;
    }

    @JsonProperty
    public int getHeaterCount()
    {
        return heaterCount;
    }

    @JsonProperty
    public void setHeaterCount(int heaterCount)
    {
        this.heaterCount = heaterCount;
    }

    @JsonProperty
    public int getNozzleCount()
    {
        return nozzleCount;
    }

    @JsonProperty
    public void setNozzleCount(int nozzleCount)
    {
        this.nozzleCount = nozzleCount;
    }

    @JsonProperty
    public float getMaxTemp()
    {
        return maxTemp;
    }

    @JsonProperty
    public void setMaxTemp(float maxTemp)
    {
        this.maxTemp = maxTemp;
    }

    @JsonProperty
    public float getBeta()
    {
        return beta;
    }

    @JsonProperty
    public void setBeta(float beta)
    {
        this.beta = beta;
    }

    @JsonProperty
    public float getTCal()
    {
        return tCal;
    }

    @JsonProperty
    public void setTCal(float tCal)
    {
        this.tCal = tCal;
    }

    @JsonProperty
    public float getHourCount()
    {
        return hourCount;
    }

    @JsonProperty
    public void setHourCount(float hourCount)
    {
        this.hourCount = hourCount;
    }

    @JsonProperty
    public float getLeftNozzleXOffset()
    {
        return leftNozzleXOffset;
    }

    @JsonProperty
    public void setLeftNozzleXOffset(float leftNozzleXOffset)
    {
        this.leftNozzleXOffset = leftNozzleXOffset;
    }

    @JsonProperty
    public float getLeftNozzleYOffset()
    {
        return leftNozzleYOffset;
    }

    @JsonProperty
    public void setLeftNozzleYOffset(float leftNozzleYOffset)
    {
        this.leftNozzleYOffset = leftNozzleYOffset;
    }

    @JsonProperty
    public float getLeftNozzleZOverrun()
    {
        return leftNozzleZOverrun;
    }

    @JsonProperty
    public void setLeftNozzleZOverrun(float leftNozzleZOverrun)
    {
        this.leftNozzleZOverrun = leftNozzleZOverrun;
    }
    @JsonProperty
    public float getLeftNozzleBOffset()
    {
        return leftNozzleBOffset;
    }

    @JsonProperty
    public void setLeftNozzleBOffset(float leftNozzleBOffset)
    {
        this.leftNozzleBOffset = leftNozzleBOffset;
    }
    
    @JsonProperty
    public float getLeftNozzleLastFTemp()
    {
        return leftNozzleLastFTemp;
    }

    @JsonProperty
    public void setLeftNozzleLastFTemp(float leftNozzleLastFTemp)
    {
        this.leftNozzleLastFTemp = leftNozzleLastFTemp;
    }

    @JsonProperty
    public float getRightNozzleXOffset()
    {
        return rightNozzleXOffset;
    }

    @JsonProperty
    public void setRightNozzleXOffset(float rightNozzleXOffset)
    {
        this.rightNozzleXOffset = rightNozzleXOffset;
    }

    @JsonProperty
    public float getRightNozzleYOffset()
    {
        return rightNozzleYOffset;
    }

    @JsonProperty
    public void setRightNozzleYOffset(float rightNozzleYOffset)
    {
        this.rightNozzleYOffset = rightNozzleYOffset;
    }
    
    @JsonProperty
    public float getRightNozzleZOverrun()
    {
        return rightNozzleZOverrun;
    }

    @JsonProperty
    public void setRightNozzleZOverrun(float rightNozzleZOverrun)
    {
        this.rightNozzleZOverrun = rightNozzleZOverrun;
    }

    @JsonProperty
    public float getRightNozzleBOffset()
    {
        return rightNozzleBOffset;
    }

    @JsonProperty
    public void setRightNozzleBOffset(float rightNozzleBOffset)
    {
        this.rightNozzleBOffset = rightNozzleBOffset;
    }

    @JsonProperty
    public float getRightNozzleLastFTemp()
    {
        return rightNozzleLastFTemp;
    }

    @JsonProperty
    public void setRightNozzleLastFTempp(float rightNozzleLastFTemp)
    {
        this.rightNozzleLastFTemp = rightNozzleLastFTemp;
    }
}
