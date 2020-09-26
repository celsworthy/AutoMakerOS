package celtech.roboxremote.rootDataStructures;

import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxremote.PrinterRegistry;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author ianhudson
 */
public class ControlStatusData
{

    private String printerID;
    private String printerStatusEnumValue;

    private boolean canPrint;
    private boolean canPause;
    private boolean canResume;
    private boolean canPurgeHead;
    private boolean canRemoveHead;
    private boolean canOpenDoor;
    private boolean canCancel;
    private boolean canCalibrateHead;
    private boolean dualMaterialHead;

    public ControlStatusData()
    {
        // Jackson deserialization
    }

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
                    printerStatusEnumValue = printer.printerStatusProperty().get().name();
                    statusProcessed = true;
                    break;
                case RUNNING_MACRO_FILE:
                    printerStatusEnumValue = printer.printerStatusProperty().get().name();
                    statusProcessed = true;
                    break;
            }
        }
        
        if (!statusProcessed)
        {
            printerStatusEnumValue = printer.printerStatusProperty().get().name();
            statusProcessed = true;
        }

        canPrint = printer.canPrintProperty().get();
        canCalibrateHead = printer.canCalibrateHeadProperty().get();
        canCancel = printer.canCancelProperty().get();
        canOpenDoor = printer.canOpenDoorProperty().get();
        canPause = printer.canPauseProperty().get();
        canPurgeHead = false;
        canRemoveHead = printer.canRemoveHeadProperty().get();
        canResume = printer.canResumeProperty().get();

        if (printer.headProperty().get() != null)
        {
            dualMaterialHead = printer.headProperty().get().headTypeProperty().get() == Head.HeadType.DUAL_MATERIAL_HEAD;

            if (dualMaterialHead)
            {
                canPurgeHead = printer.reelsProperty().containsKey(0) && printer.reelsProperty().containsKey(1) && printer.canPurgeHeadProperty().get();
            } else
            {
                canPurgeHead = printer.reelsProperty().containsKey(0) && printer.canPurgeHeadProperty().get();
            }
        }
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

    public String getPrinterStatusEnumValue()
    {
        return printerStatusEnumValue;
    }

    public void setPrinterStatusEnumValue(String printerStatusEnumValue)
    {
        this.printerStatusEnumValue = printerStatusEnumValue;
    }

    public boolean isCanPrint()
    {
        return canPrint;
    }

    public void setCanPrint(boolean canPrint)
    {
        this.canPrint = canPrint;
    }

    public boolean isCanCalibrateHead()
    {
        return canCalibrateHead;
    }

    public void setCanCalibrateHead(boolean canCalibrateHead)
    {
        this.canCalibrateHead = canCalibrateHead;
    }

    public boolean isCanCancel()
    {
        return canCancel;
    }

    public void setCanCancel(boolean canCancel)
    {
        this.canCancel = canCancel;
    }

    public boolean isCanOpenDoor()
    {
        return canOpenDoor;
    }

    public void setCanOpenDoor(boolean canOpenDoor)
    {
        this.canOpenDoor = canOpenDoor;
    }

    public boolean isCanPause()
    {
        return canPause;
    }

    public void setCanPause(boolean canPause)
    {
        this.canPause = canPause;
    }

    public boolean isCanPurgeHead()
    {
        return canPurgeHead;
    }

    public void setCanPurgeHead(boolean canPurgeHead)
    {
        this.canPurgeHead = canPurgeHead;
    }

    public boolean isCanRemoveHead()
    {
        return canRemoveHead;
    }

    public void setCanRemoveHead(boolean canRemoveHead)
    {
        this.canRemoveHead = canRemoveHead;
    }

    public boolean isCanResume()
    {
        return canResume;
    }

    public void setCanResume(boolean canResume)
    {
        this.canResume = canResume;
    }

    public boolean isDualMaterialHead()
    {
        return dualMaterialHead;
    }

    public void setDualMaterialHead(boolean dualMaterialHead)
    {
        this.dualMaterialHead = dualMaterialHead;
    }
}
