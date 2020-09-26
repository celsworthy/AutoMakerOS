package celtech.roboxremote.rootDataStructures;

import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.PrinterColourMap;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.datafileaccessors.FilamentContainer;
import celtech.roboxbase.postprocessor.PrintJobStatistics;
import celtech.roboxbase.printerControl.PrinterStatus;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.HeaterMode;
import celtech.roboxbase.printerControl.model.NozzleHeater;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.utils.ColourStringConverter;
import celtech.roboxremote.PrinterRegistry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.IOException;
import javafx.scene.paint.Color;

/**
 *
 * @author ianhudson
 */
public class StatusData
{
    private static final double HEATING_THREASHOLD = 5;
    private static final double EJECT_TEMPERATURE = 140.0;

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

    @JsonIgnore
    private String lastPrintJobID = null;

    public StatusData()
    {
        // Jackson deserialization
    }

    public void updateFromPrinterData(String printerID)
    {
        this.printerID = printerID;
        Printer printer = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);
      
        printerName = printer.getPrinterIdentity().printerFriendlyNameProperty().get();
        printerTypeCode = printer.printerConfigurationProperty().get().getTypeCode();
        PrinterColourMap colourMap = PrinterColourMap.getInstance();
        Color displayColour = colourMap.printerToDisplayColour(printer.getPrinterIdentity().printerColourProperty().get());

        printerWebColourString = "#" + ColourStringConverter.colourToString(displayColour);

        boolean statusProcessed = false;

        // Has to be in this order, as the printer status is printing even when aused status is paused.
        //if (!statusProcessed)
        //{
            switch (printer.busyStatusProperty().get())
            {
                case LOADING_FILAMENT_D:
                case LOADING_FILAMENT_E:
                case UNLOADING_FILAMENT_D:
                case UNLOADING_FILAMENT_E:
                    printerStatusString = BaseLookup.i18n(printer.busyStatusProperty().get().getI18nString());
                    printerStatusEnumValue = printer.busyStatusProperty().get().name();
                    statusProcessed = true;
                    break;
                default:
                    break;
            }
        //}

        if (!statusProcessed)
        {
            switch (printer.pauseStatusProperty().get())
            {
                case PAUSED:
				case SELFIE_PAUSE:
                case PAUSE_PENDING:
                case RESUME_PENDING:
                    printerStatusString = BaseLookup.i18n(printer.pauseStatusProperty().get().getI18nString());
                    printerStatusEnumValue = printer.pauseStatusProperty().get().name();
                    statusProcessed = true;
                    break;
                default:
                    break;
            }
        }

        if (!statusProcessed)
        {
            switch (printer.printerStatusProperty().get())
            {
                case CALIBRATING_NOZZLE_ALIGNMENT:
                case CALIBRATING_NOZZLE_HEIGHT:
                case CALIBRATING_NOZZLE_OPENING:
                case OPENING_DOOR:
                case PRINTING_PROJECT:
                case PURGING_HEAD:
                case REMOVING_HEAD:
                    printerStatusString = printer.printerStatusProperty().get().getI18nString();
                    printerStatusEnumValue = printer.printerStatusProperty().get().name();
                    statusProcessed = true;
                    break;
                case RUNNING_MACRO_FILE:
                    printerStatusString = printer.getPrintEngine().macroBeingRun.get().getFriendlyName();
                    printerStatusEnumValue = printer.printerStatusProperty().get().name();
                    statusProcessed = true;
                    break;
            }
        }

        heatingProgress = -1;
        if (!statusProcessed)
        {
            Head head = printer.headProperty().get();
            if (head != null && head.getNozzleHeaters().size() > 0)
            {
                if (head.getNozzleHeaters().size() == 1)
                {
                    if (head.getNozzles().size() == 1)
                        statusProcessed = updateHeaterStatus(head.getNozzleHeaters().get(0), "heating-nozzle");
                    else
                        statusProcessed = updateHeaterStatus(head.getNozzleHeaters().get(0), "heating-nozzles");
                }
                else
                {
                    statusProcessed = updateHeaterStatus(head.getNozzleHeaters().get(0), "heating-left");
                    if (head.getNozzleHeaters().size() > 1)
                    {
                        if (statusProcessed)
                        {
                            // If heating both nozzles, progress is the lesser of the two.
                            int leftHeatingProgress = heatingProgress;
                            if (updateHeaterStatus(head.getNozzleHeaters().get(1), "heating-nozzles"))
                                if (leftHeatingProgress < heatingProgress)
                                    heatingProgress = leftHeatingProgress;
                        }
                        else
                            statusProcessed = updateHeaterStatus(head.getNozzleHeaters().get(1), "heating-right");
                    }
                }
            }
        }

        if (!statusProcessed && printer.getPrinterAncillarySystems().bedHeaterModeProperty().get() != HeaterMode.OFF)
        {
            double currentTemperature = printer.getPrinterAncillarySystems().bedTemperatureProperty().get();
            double targetTemperature = 0.0;
            switch (printer.getPrinterAncillarySystems().bedHeaterModeProperty().get())
            {
                case FIRST_LAYER:
                    targetTemperature = printer.getPrinterAncillarySystems().bedFirstLayerTargetTemperatureProperty().get();
                    break;

                case NORMAL:
                default:
                    targetTemperature = printer.getPrinterAncillarySystems().bedTargetTemperatureProperty().get();
                    break;
            }
            if (targetTemperature > 0 && (Math.abs(currentTemperature - targetTemperature) > HEATING_THREASHOLD))
            {
                heatingProgress = (int)(Math.floor(0.5 + 100.0 * currentTemperature / targetTemperature));
                printerStatusString = "heating-bed";
                printerStatusEnumValue = "HEATING";
                statusProcessed = true;
            }
        }
                
        if (!statusProcessed)
        {
            printerStatusString = printer.printerStatusProperty().get().getI18nString();
            printerStatusEnumValue = printer.printerStatusProperty().get().name();
        }

        canPrint = printer.canPrintProperty().get();
        canCalibrateHead = printer.canCalibrateHeadProperty().get();
        canCancel = printer.canCancelProperty().get();
        canOpenDoor = printer.canOpenDoorProperty().get();

        int numberOfExtruders = 1;
        if (printer.extrudersProperty().size() == 2
                && printer.extrudersProperty().get(1) != null
                && printer.extrudersProperty().get(1).isFittedProperty().get())
        {
            numberOfExtruders = 2;
        }

        Head printerHead = printer.headProperty().get();
        
        attachedFilaments = new FilamentDetails[numberOfExtruders];
        for (int extruderNumber = 0; extruderNumber < numberOfExtruders; extruderNumber++)
        {
            String filamentName = "";
            String materialName = "";
            String webColour = "";
            int filamentTemperature = -1;
            float remainingFilament = -1.0F;
            boolean customFlag = false;
            boolean extruderFitter = (printer.extrudersProperty().get(extruderNumber) != null &&
                                      printer.extrudersProperty().get(extruderNumber).isFittedProperty().get());
            boolean canEject = (extruderFitter &&
                                printer.extrudersProperty().get(extruderNumber).canEjectProperty().get());
            boolean materialLoaded = (extruderFitter &&
                                      printer.extrudersProperty().get(extruderNumber).filamentLoadedProperty().get());
            boolean canExtrude = (printerHead == null) || (materialLoaded && extruderNumber < printerHead.getNozzleHeaters().size());
            boolean canRetract = (printerHead == null) || materialLoaded;

            if (printer.effectiveFilamentsProperty().get(extruderNumber) != FilamentContainer.UNKNOWN_FILAMENT)
            {
                Filament filament = printer.effectiveFilamentsProperty().get(extruderNumber);
                filamentName = filament.getFriendlyFilamentName();
                materialName = filament.getMaterial().toString();
                filamentTemperature = filament.getNozzleTemperature();
                remainingFilament = filament.getRemainingFilament();
                webColour = "#" + ColourStringConverter.colourToString(filament.getDisplayColourProperty().get());
                customFlag = filament.isMutable();
                
                if (printer.reelsProperty().containsKey(extruderNumber) && printer.reelsProperty().get(extruderNumber) != null)
                {
                    remainingFilament = printer.reelsProperty().get(extruderNumber).remainingFilamentProperty().get();
                    if (remainingFilament < 0.0F)
                        remainingFilament = 0.0F;
                }
            }

            attachedFilaments[extruderNumber] = new FilamentDetails(filamentName, materialName, webColour,
                                                                    filamentTemperature, 0.001F * remainingFilament, // Remaining filament is in mm but needs to be reported in m.
                                                                    customFlag, materialLoaded, canEject, canExtrude, canRetract);
        }

        canPause = printer.canPauseProperty().get();
        canPurgeHead = false;
        canRemoveHead = printer.canRemoveHeadProperty().get();
        canResume = printer.canResumeProperty().get();

        //Head
        if (printerHead != null)
        {
            headName = printerHead.nameProperty().get();
            headTypeCode = printerHead.typeCodeProperty().get().trim();

            dualMaterialHead = printerHead.headTypeProperty().get() == Head.HeadType.DUAL_MATERIAL_HEAD;
            valvesFitted = printerHead.valveTypeProperty().get() == Head.ValveType.FITTED;

            if (dualMaterialHead)
            {
                canPurgeHead = printer.reelsProperty().containsKey(0) && printer.reelsProperty().containsKey(1) && printer.canPurgeHeadProperty().get();
            } else
            {
                canPurgeHead = printer.reelsProperty().containsKey(0) && printer.canPurgeHeadProperty().get();
            }

            nozzleTemperature = new int[printerHead.getNozzleHeaters().size()];
            for (int heaterNumber = 0; heaterNumber < printerHead.getNozzleHeaters().size(); heaterNumber++)
            {
                nozzleTemperature[heaterNumber] = printerHead.getNozzleHeaters().get(heaterNumber).nozzleTemperatureProperty().get();
            }
        }
        else
        {
            headName = "";
            headTypeCode = "";
            dualMaterialHead = false;
            valvesFitted = false;
            canPurgeHead = false;
            nozzleTemperature = null;
        }

        bedTemperature = printer.getPrinterAncillarySystems().bedTemperatureProperty().get();

        ambientTemperature = printer.getPrinterAncillarySystems().ambientTemperatureProperty().get();

        //Print info
        totalDurationSeconds = printer.getPrintEngine().totalDurationSecondsProperty().get();

        if (printer.printerStatusProperty().get() == PrinterStatus.PRINTING_PROJECT)
        {
            if (lastPrintJobID == null
                    || lastPrintJobID.equals(printer.getPrintEngine().printJobProperty().get()))
            {
                lastPrintJobID = printer.getPrintEngine().printJobProperty().get().getJobUUID();
                try
                {
                    PrintJobStatistics printJobStatistics = printer.getPrintEngine().printJobProperty().get().getStatistics();
                    printJobName = printJobStatistics.getProjectName();
                    printJobSettings = printJobStatistics.getProfileName();
                    if (printJobSettings.equalsIgnoreCase(BaseConfiguration.draftSettingsProfileName))
                        printJobProfile = "DRAFT";
                    else if (printJobSettings.equalsIgnoreCase(BaseConfiguration.normalSettingsProfileName))
                        printJobProfile = "NORMAL";
                    else if (printJobSettings.equalsIgnoreCase(BaseConfiguration.fineSettingsProfileName))
                        printJobProfile = "FINE";
                    else
                        printJobProfile = "CUSTOM";
                } catch (IOException ex)
                {
                }
            }

            etcSeconds = printer.getPrintEngine().progressETCProperty().get();
            currentLayer = printer.getPrintEngine().progressCurrentLayerProperty().get();
            numberOfLayers = printer.getPrintEngine().progressNumLayersProperty().get();
        } else
        {
            lastPrintJobID = null;
        }
    }
    
    @JsonIgnore
    private boolean updateHeaterStatus(NozzleHeater heater, String statusString)
    {
        if (heater.heaterModeProperty().get() != HeaterMode.OFF)
        {
            double currentTemperature = heater.nozzleTemperatureProperty().get();
            double targetTemperature = 0.0;
            switch (heater.heaterModeProperty().get())
            {
                case FIRST_LAYER:
                    targetTemperature = heater.nozzleFirstLayerTargetTemperatureProperty().get();
                    break;
                    
                case FILAMENT_EJECT:
                    targetTemperature = EJECT_TEMPERATURE;
                    break;

                case NORMAL:
                default:
                    targetTemperature = heater.nozzleTargetTemperatureProperty().get();
                    break;
            }
            
            if (targetTemperature > 0 && (Math.abs(currentTemperature - targetTemperature) > HEATING_THREASHOLD))
            {
                heatingProgress = (int)(Math.floor(0.5 + 100.0 * currentTemperature / targetTemperature));

                printerStatusString = statusString;
                printerStatusEnumValue = "HEATING";
                return true;
            }
        }

        return false;
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

    @JsonIgnore
    public void setNozzleTemperature(int nozzleIndex, int newNozzleTemperature)
    {
        if (nozzleIndex >= 0
                && nozzleIndex < nozzleTemperature.length)
        {
            nozzleTemperature[nozzleIndex] = newNozzleTemperature;
        }
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
