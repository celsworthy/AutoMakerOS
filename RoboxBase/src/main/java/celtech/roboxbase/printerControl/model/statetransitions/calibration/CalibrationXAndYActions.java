/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.printerControl.model.statetransitions.calibration;

import celtech.roboxbase.comms.exceptions.RoboxCommsException;
import celtech.roboxbase.comms.rx.HeadEEPROMDataResponse;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.printerControl.PrinterStatus;
import celtech.roboxbase.printerControl.comms.commands.GCodeMacros;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterException;
import celtech.roboxbase.printerControl.model.statetransitions.StateTransitionActions;
import celtech.roboxbase.utils.PrinterUtils;
import celtech.roboxbase.utils.tasks.Cancellable;
import java.io.FileNotFoundException;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author tony
 */
public class CalibrationXAndYActions extends StateTransitionActions
{
    private final Stenographer steno = StenographerFactory.getStenographer(
            CalibrationXAndYActions.class.getName());

    private final Printer printer;
    private HeadEEPROMDataResponse savedHeadData;
    private int xOffset = 0;
    private int yOffset = 0;
    private final CalibrationPrinterErrorHandler printerErrorHandler;

    private boolean failedActionPerformed = false;
    
    private final boolean safetyFeaturesRequired;

    public CalibrationXAndYActions(Printer printer, Cancellable userCancellable,
            Cancellable errorCancellable, boolean safetyFeaturesRequired)
    {
        super(userCancellable, errorCancellable);
        this.safetyFeaturesRequired = safetyFeaturesRequired;
        this.printer = printer;

        printerErrorHandler = new CalibrationPrinterErrorHandler(printer, errorCancellable);
        printerErrorHandler.registerForPrinterErrors();
        CalibrationUtils.setCancelledIfPrinterDisconnected(printer, errorCancellable);
    }

    @Override
    public void initialise()
    {
        savedHeadData = null;
        xOffset = 0;
        yOffset = 0;
    }

    public void doSaveHead() throws PrinterException, RoboxCommsException, InterruptedException, CalibrationException
    {
        printerErrorHandler.registerForPrinterErrors();

        printer.setPrinterStatus(PrinterStatus.CALIBRATING_NOZZLE_ALIGNMENT);
        savedHeadData = printer.readHeadEEPROM(true);
    }

    public void doPrintPattern() throws PrinterException, RoboxCommsException, InterruptedException, CalibrationException
    {
        if (printer.headProperty().get().headTypeProperty().get() == Head.HeadType.SINGLE_MATERIAL_HEAD)
        {
            printer.executeGCodeFile(BaseConfiguration.getApplicationModelDirectory().concat(
                    "rbx_test_xy-offset-1_roboxised.gcode"), false);
        } else
        {
            printer.executeGCodeFile(BaseConfiguration.getApplicationModelDirectory().concat(
                    "rbx_test_xy-offset-1_dm_roboxised.gcode"), false);
        }
        PrinterUtils.waitOnPrintFinished(printer, userOrErrorCancellable);
    }

    public void doSaveSettingsAndPrintCircle() throws PrinterException, InterruptedException, CalibrationException
    {
        saveSettings();
//        Thread.sleep(3000);
        //TODO needs to be changed for DM head
        try
        {
            printer.executeGCodeFile(GCodeMacros.getFilename("rbx_test_xy-offset-2_roboxised",
                    null, null, GCodeMacros.NozzleUseIndicator.DONT_CARE,
                    GCodeMacros.SafetyIndicator.DONT_CARE), false);
            PrinterUtils.waitOnMacroFinished(printer, userOrErrorCancellable);
        } catch (FileNotFoundException ex)
        {
            throw new PrinterException("Failed to access calibration macro");
        }
    }

    private void switchHeatersOffAndRaiseHead() throws PrinterException
    {
        printer.switchBedHeaterOff();
        printer.switchAllNozzleHeatersOff();
        printer.switchOffHeadLEDs();
        printer.switchToAbsoluteMoveMode();
        printer.goToZPosition(25);
    }

    private void restoreHeadData()
    {
        if (savedHeadData != null)
        {
            try
            {
                steno.debug("Restore head data");
                printer.transmitWriteHeadEEPROM(savedHeadData.getHeadTypeCode(),
                        savedHeadData.getUniqueID(),
                        savedHeadData.getMaximumTemperature(),
                        savedHeadData.getThermistorBeta(),
                        savedHeadData.getThermistorTCal(),
                        savedHeadData.getNozzle1XOffset(),
                        savedHeadData.getNozzle1YOffset(),
                        savedHeadData.getNozzle1ZOffset(),
                        savedHeadData.getNozzle1BOffset(),
                        savedHeadData.getFilamentID(0),
                        savedHeadData.getFilamentID(1),
                        savedHeadData.getNozzle2XOffset(),
                        savedHeadData.getNozzle2YOffset(),
                        savedHeadData.getNozzle2ZOffset(),
                        savedHeadData.getNozzle2BOffset(),
                        savedHeadData.getLastFilamentTemperature(0),
                        savedHeadData.getLastFilamentTemperature(1),
                        savedHeadData.getHeadHours());
                printer.readHeadEEPROM(false);
            } catch (RoboxCommsException ex)
            {
                steno.error("Unable to restore head! " + ex);
            }
        }
    }

    private void saveSettings()
    {

        // F and 6 are zero values
        float nozzle1XCorrection = -xOffset * 0.025f;
        float nozzle2XCorrection = xOffset * 0.025f;

        float nozzle1YCorrection = (yOffset - 6) * 0.025f;
        float nozzle2YCorrection = -(yOffset - 6) * 0.025f;

        steno.info(String.format("Saving XY with correction %1.2f %1.2f %1.2f %1.2f ",
                nozzle1XCorrection,
                nozzle2XCorrection, nozzle1YCorrection, nozzle2YCorrection));

        try
        {
            printer.transmitWriteHeadEEPROM(savedHeadData.getHeadTypeCode(),
                    savedHeadData.getUniqueID(),
                    savedHeadData.getMaximumTemperature(),
                    savedHeadData.getThermistorBeta(),
                    savedHeadData.getThermistorTCal(),
                    savedHeadData.getNozzle1XOffset()
                    + nozzle1XCorrection,
                    savedHeadData.getNozzle1YOffset()
                    + nozzle1YCorrection,
                    savedHeadData.getNozzle1ZOffset(),
                    savedHeadData.getNozzle1BOffset(),
                    savedHeadData.getFilamentID(0),
                    savedHeadData.getFilamentID(1),
                    savedHeadData.getNozzle2XOffset()
                    + nozzle2XCorrection,
                    savedHeadData.getNozzle2YOffset()
                    + nozzle2YCorrection,
                    savedHeadData.getNozzle2ZOffset(),
                    savedHeadData.getNozzle2BOffset(),
                    savedHeadData.getLastFilamentTemperature(0),
                    savedHeadData.getLastFilamentTemperature(1),
                    savedHeadData.getHeadHours());

        } catch (RoboxCommsException ex)
        {
            steno.error("Error in needle valve calibration - saving settings");
        }
    }

    public void setXOffset(String xStr)
    {
        switch (xStr)
        {
            case "A":
                xOffset = -5;
                break;
            case "B":
                xOffset = -4;
                break;
            case "C":
                xOffset = -3;
                break;
            case "D":
                xOffset = -2;
                break;
            case "E":
                xOffset = -1;
                break;
            case "F":
                xOffset = 0;
                break;
            case "G":
                xOffset = 1;
                break;
            case "H":
                xOffset = 2;
                break;
            case "I":
                xOffset = 3;
                break;
            case "J":
                xOffset = 4;
                break;
            case "K":
                xOffset = 5;
                break;
        }
    }

    public void setYOffset(int yOffset)
    {
        this.yOffset = yOffset;
    }

    public void doFinishedAction()
    {
        try
        {
            saveSettings();
            switchHeatersOffAndRaiseHead();
            printerErrorHandler.deregisterForPrinterErrors();
        } catch (PrinterException ex)
        {
            steno.error("Error in finished action: " + ex);
        }
        printer.setPrinterStatus(PrinterStatus.IDLE);
    }

    public void doFailedAction()
    {
        // this can be called twice if an error occurs
        if (failedActionPerformed)
        {
            return;
        }

        failedActionPerformed = true;
        try
        {
            restoreHeadData();
            switchHeatersOffAndRaiseHead();
            printerErrorHandler.deregisterForPrinterErrors();
        } catch (PrinterException ex)
        {
            steno.error("Error in finished action: " + ex);
        }
        printer.setPrinterStatus(PrinterStatus.IDLE);
    }

    @Override
    public void whenUserCancelDetected()
    {
        restoreHeadData();
        abortAnyOngoingPrint();

    }

    @Override
    public void whenErrorDetected()
    {
        printerErrorHandler.deregisterForPrinterErrors();
        restoreHeadData();
        abortAnyOngoingPrint();
    }

    @Override
    public void resetAfterCancelOrError()
    {
        try
        {
            doFailedAction();
        } catch (Exception ex)
        {
            ex.printStackTrace();
            steno.error("error resetting printer " + ex);
        }
    }

    private void abortAnyOngoingPrint()
    {
        try
        {
            if (printer.canCancelProperty().get())
            {
                steno.debug("call cancel");
                printer.cancel(null, safetyFeaturesRequired);
            } else
            {
                steno.debug("can't cancel");
            }
        } catch (PrinterException ex)
        {
            steno.error("Failed to abort print - " + ex.getMessage());
        }
    }
}
