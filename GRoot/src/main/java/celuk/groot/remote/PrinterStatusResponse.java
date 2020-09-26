package celuk.groot.remote;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PrinterStatusResponse
{
    private String printerID;
    private String printerName;
    private String printerTypeCode;
    private String printerWebColourString;
    private String printerStatusString;
    private String printerStatusEnumValue;

    private boolean canPrint;
    private boolean canPause;
    private boolean canResume;
    private boolean canPurgeHead;
    private boolean canRemoveHead;
    private boolean canOpenDoor;
    private boolean[] canEjectFilament;
    private boolean canCancel;
    private boolean canCalibrateHead;

    //Head
    private String headName;
    private String headTypeCode;
    private boolean valvesFitted;            
    private boolean dualMaterialHead;
    private int[] nozzleTemperature;

    //Bed
    private int bedTemperature;

    // Ambient
    private int ambientTemperature;

    private int heatingProgress;

    //Print info
    private String printJobName;
    private String printJobSettings;
    private String printJobProfile;
    private int totalDurationSeconds;
    private int etcSeconds;
    private int currentLayer;
    private int numberOfLayers;

    // Material
    private FilamentDetails[] attachedFilaments = null;

    public PrinterStatusResponse()
    {
        // Jackson deserialization
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
    public String getPrinterName()
    {
        return printerName;
    }

    @JsonProperty
    public void setPrinterName(String printerName)
    {
        this.printerName = printerName;
    }

    @JsonProperty
    public String getPrinterTypeCode()
    {
        return printerTypeCode;
    }

    @JsonProperty
    public void setPrinterTypeCode(String printerTypeCode)
    {
        this.printerTypeCode = printerTypeCode;
    }

    @JsonProperty
    public String getPrinterWebColourString()
    {
        return printerWebColourString;
    }

    @JsonProperty
    public void setPrinterWebColourString(String printerWebColourString)
    {
        this.printerWebColourString = printerWebColourString;
    }

    @JsonProperty
    public String getPrinterStatusString()
    {
        return printerStatusString;
    }

    @JsonProperty
    public void setPrinterStatusString(String printerStatusString)
    {
        this.printerStatusString = printerStatusString;
    }

    @JsonProperty
    public String getPrinterStatusEnumValue()
    {
        return printerStatusEnumValue;
    }

    @JsonProperty
    public void setPrinterStatusEnumValue(String printerStatusEnumValue)
    {
        this.printerStatusEnumValue = printerStatusEnumValue;
    }

    @JsonProperty
    public boolean isCanPrint()
    {
        return canPrint;
    }

    @JsonProperty
    public void setCanPrint(boolean canPrint)
    {
        this.canPrint = canPrint;
    }

    @JsonProperty
    public boolean isCanCalibrateHead()
    {
        return canCalibrateHead;
    }

    @JsonProperty
    public void setCanCalibrateHead(boolean canCalibrateHead)
    {
        this.canCalibrateHead = canCalibrateHead;
    }

    @JsonProperty
    public boolean isCanCancel()
    {
        return canCancel;
    }

    @JsonProperty
    public void setCanCancel(boolean canCancel)
    {
        this.canCancel = canCancel;
    }

    @JsonProperty
    public boolean isCanOpenDoor()
    {
        return canOpenDoor;
    }

    @JsonProperty
    public void setCanOpenDoor(boolean canOpenDoor)
    {
        this.canOpenDoor = canOpenDoor;
    }

    @JsonProperty
    public void setCanEjectFilament(boolean[] canEjectFilament)
    {
        this.canEjectFilament = canEjectFilament;
    }

    @JsonProperty
    public boolean[] getCanEjectFilament()
    {
        return canEjectFilament;
    }

    @JsonProperty
    public boolean isCanPause()
    {
        return canPause;
    }

    @JsonProperty
    public void setCanPause(boolean canPause)
    {
        this.canPause = canPause;
    }

    @JsonProperty
    public boolean isCanPurgeHead()
    {
        return canPurgeHead;
    }

    @JsonProperty
    public void setCanPurgeHead(boolean canPurgeHead)
    {
        this.canPurgeHead = canPurgeHead;
    }

    @JsonProperty
    public boolean isCanRemoveHead()
    {
        return canRemoveHead;
    }

    @JsonProperty
    public void setCanRemoveHead(boolean canRemoveHead)
    {
        this.canRemoveHead = canRemoveHead;
    }

    @JsonProperty
    public boolean isCanResume()
    {
        return canResume;
    }

    @JsonProperty
    public void setCanResume(boolean canResume)
    {
        this.canResume = canResume;
    }

    @JsonProperty
    public String getHeadName()
    {
        return headName;
    }

    @JsonProperty
    public void setHeadName(String headName)
    {
        this.headName = headName;
    }

    @JsonProperty
    public String getHeadTypeCode()
    {
        return headTypeCode;
    }

    @JsonProperty
    public void setHeadTypeCode(String headTypeCode)
    {
        this.headTypeCode = headTypeCode;
    }

    @JsonProperty("areValvesFitted")
    public boolean areValvesFitted()
    {
        return valvesFitted;
    }

    @JsonProperty("areValvesFitted")
    public void setValvesFitted(boolean valvesFitted)
    {
        this.valvesFitted = valvesFitted;
    }

    @JsonProperty
    public boolean isDualMaterialHead()
    {
        return dualMaterialHead;
    }

    @JsonProperty
    public void setDualMaterialHead(boolean dualMaterialHead)
    {
        this.dualMaterialHead = dualMaterialHead;
    }

    @JsonProperty
    public int getBedTemperature()
    {
        return bedTemperature;
    }

    @JsonProperty
    public void setBedTemperature(int bedTemperature)
    {
        this.bedTemperature = bedTemperature;
    }

    @JsonProperty
    public int getAmbientTemperature()
    {
        return ambientTemperature;
    }

    @JsonProperty
    public void setAmbientTemperature(int ambientTemperature)
    {
        this.ambientTemperature = ambientTemperature;
    }

    @JsonProperty
    public int getHeatingProgress()
    {
        return heatingProgress;
    }

    @JsonProperty
    public void setHeatingProgress(int heatingProgress)
    {
        this.heatingProgress = heatingProgress;
    }

    @JsonProperty
    public int[] getNozzleTemperature()
    {
        return nozzleTemperature;
    }

    @JsonProperty
    public void setNozzleTemperature(int[] nozzleTemperature)
    {
        this.nozzleTemperature = nozzleTemperature;
    }

    @JsonProperty
    public String getPrintJobName()
    {
        return printJobName;
    }

    @JsonProperty
    public void setPrintJobName(String printJobName)
    {
        this.printJobName = printJobName;
    }

    @JsonProperty
    public int getEtcSeconds()
    {
        return etcSeconds;
    }

    @JsonProperty
    public void setEtcSeconds(int etcSeconds)
    {
        this.etcSeconds = etcSeconds;
    }

    @JsonProperty
    public int getTotalDurationSeconds()
    {
        return totalDurationSeconds;
    }

    @JsonProperty
    public void setTotalDurationSeconds(int totalDurationSeconds)
    {
        this.totalDurationSeconds = totalDurationSeconds;
    }

    @JsonProperty
    public int getCurrentLayer()
    {
        return currentLayer;
    }

    @JsonProperty
    public void setCurrentLayer(int currentLayer)
    {
        this.currentLayer = currentLayer;
    }
    
    @JsonProperty
    public int getNumberOfLayers()
    {
        return numberOfLayers;
    }

    @JsonProperty
    public void setNumberOfLayers(int numberOfLayers)
    {
        this.numberOfLayers = numberOfLayers;
    }

    @JsonProperty
    public String getPrintJobSettings()
    {
        return printJobSettings;
    }

    @JsonProperty
    public void setPrintJobSettings(String printJobSettings)
    {
        this.printJobSettings = printJobSettings;
    }

    @JsonProperty
    public String getPrintJobProfile()
    {
        return printJobProfile;
    }

    @JsonProperty
    public void setPrintJobProfile(String printJobProfile)
    {
        this.printJobProfile = printJobProfile;
    }
    
    @JsonProperty
    public FilamentDetails[] getAttachedFilaments()
    {
        return attachedFilaments;
    }

    @JsonProperty
    public void setAttachedFilaments(FilamentDetails[] attachedFilaments)
    {
        this.attachedFilaments = attachedFilaments;
    }
}
