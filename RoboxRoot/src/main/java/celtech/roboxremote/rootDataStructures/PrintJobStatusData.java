package celtech.roboxremote.rootDataStructures;

import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.postprocessor.PrintJobStatistics;
import celtech.roboxbase.printerControl.PrinterStatus;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.HeaterMode;
import celtech.roboxbase.printerControl.model.NozzleHeater;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxremote.PrinterRegistry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.IOException;

/**
 *
 * @author taldhous
 */
public class PrintJobStatusData
{
    private static final double HEATING_THREASHOLD = 5;
    private static final double EJECT_TEMPERATURE = 140.0;

    private String printerID;
    private String printerStatusString;
    private String printerStatusEnumValue;

    private boolean canPrint;
    private boolean canPause;
    private boolean canResume;
    private boolean canOpenDoor;
    private boolean canCancel;

    //Print job info
    private String printJobName;
    private String printJobSettings;
    private String printJobProfile;
    private int totalDurationSeconds;
    private int etcSeconds;
    private int currentLayer;
    private int numberOfLayers;

    private int heatingProgress;
    
    //Errors
    private String[] activeErrors;

    @JsonIgnore
    private String lastPrintJobID = null;


    public PrintJobStatusData()
    {
        // Jackson deserialization
    }

    @JsonIgnore
    public void updateFromPrinterData(String printerID)
    {
        this.printerID = printerID;
        Printer printer = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);

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
        canCancel = printer.canCancelProperty().get();
        canOpenDoor = printer.canOpenDoorProperty().get();

        canPause = printer.canPauseProperty().get();
        canResume = printer.canResumeProperty().get();

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
        
        if (!printer.getActiveErrors().isEmpty())
        {
            activeErrors = new String[printer.getActiveErrors().size()];
            for (int errorCounter = 0; errorCounter < printer.getActiveErrors().size(); errorCounter++)
            {
                activeErrors[errorCounter] = BaseLookup.i18n(printer.getActiveErrors().get(errorCounter).getErrorTitleKey());
            }
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
        this.totalDurationSeconds = numberOfLayers;
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
    public String[] getActiveErrors()
    {
        return activeErrors;
    }

    @JsonProperty
    public void setActiveErrors(String[] activeErrors)
    {
        this.activeErrors = activeErrors;
    }
}
