package celtech.roboxbase.utils;

import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.appManager.PurgeResponse;
import celtech.roboxbase.comms.exceptions.RoboxCommsException;
import celtech.roboxbase.comms.remote.BusyStatus;
import celtech.roboxbase.comms.rx.StatusResponse;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.datafileaccessors.FilamentContainer;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.utils.tasks.Cancellable;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class PrinterUtils
{

    private static final Stenographer steno = StenographerFactory.getStenographer(
            PrinterUtils.class.getName());
    private static PrinterUtils instance = null;
    private boolean purgeDialogVisible = false;

    private PrinterUtils()
    {
    }

    /**
     *
     * @return
     */
    public static PrinterUtils getInstance()
    {
        if (instance == null)
        {
            instance = new PrinterUtils();
        }

        return instance;
    }

    /**
     *
     * @param printerToCheck
     * @param task
     * @return interrupted
     */
    public static boolean waitOnMacroFinished(Printer printerToCheck, Task task)
    {
        boolean interrupted = false;

        if (Platform.isFxApplicationThread())
        {
            throw new RuntimeException("Cannot call this function from the GUI thread");
        }

        if (task != null && !interrupted)
        {
            while (printerToCheck.getPrintEngine().macroBeingRun.get() != null
                    && task.isCancelled() == false && !BaseLookup.isShuttingDown())
            {
                try
                {
                    Thread.sleep(100);
                } catch (InterruptedException ex)
                {
                    interrupted = true;
                    steno.error("Interrupted whilst waiting on Macro");
                }
            }
        } else
        {
            while (printerToCheck.getPrintEngine().isBusy()
                    && !BaseLookup.isShuttingDown())
            {
                try
                {
                    Thread.sleep(100);
                } catch (InterruptedException ex)
                {
                    interrupted = true;
                    steno.error("Interrupted whilst waiting on Macro");
                }
            }
        }
        return interrupted;
    }

    /**
     *
     * @param printerToCheck
     * @param cancellable
     * @return failed
     */
    public static boolean waitOnMacroFinished(Printer printerToCheck, Cancellable cancellable)
    {
        boolean failed = false;

        if (Platform.isFxApplicationThread())
        {
            throw new RuntimeException("Cannot call this function from the GUI thread");
        }

        //Slug the status check if we're working remotely
        try
        {
            Thread.sleep(1000);
        } catch (InterruptedException ex)
        {

        }

        while (printerToCheck.getPrintEngine().macroBeingRun.get() != null
                && !BaseLookup.isShuttingDown())
        {
            try
            {
                Thread.sleep(100);

                if (cancellable != null && cancellable.cancelled().get())
                {
                    failed = true;
                    break;
                }
            } catch (InterruptedException ex)
            {
                failed = true;
                steno.error("Interrupted whilst waiting on Macro");
            }
        }

        return failed;
    }

    /**
     *
     * @param printerToCheck
     * @param task
     * @return failed
     */
    public static boolean waitOnBusy(Printer printerToCheck, Task task)
    {
        boolean failed = false;

        if (task != null)
        {
            try
            {
                //Slug the status check if we're working remotely
                Thread.sleep(1000);

                StatusResponse response = printerToCheck.transmitStatusRequest();

                while (response.getBusyStatus() != BusyStatus.NOT_BUSY && !BaseLookup.isShuttingDown())
                {
                    Thread.sleep(100);
                    response = printerToCheck.transmitStatusRequest();

                    if (task.isCancelled())
                    {
                        failed = true;
                        break;
                    }
                }
            } catch (RoboxCommsException ex)
            {
                steno.error("Error requesting status");
                failed = true;
            } catch (InterruptedException ex)
            {
                steno.error("Interrupted during busy check");
                failed = true;
            }
        } else
        {
            try
            {
                StatusResponse response = printerToCheck.transmitStatusRequest();

                while (response != null && response.getBusyStatus() != BusyStatus.NOT_BUSY && !BaseLookup.isShuttingDown())
                {
                    Thread.sleep(100);
                    response = printerToCheck.transmitStatusRequest();
                }
                if (response == null)
                    failed = true;
            } catch (RoboxCommsException ex)
            {
                steno.error("Error requesting status");
                failed = true;
            } catch (InterruptedException ex)
            {
                steno.error("Interrupted during busy check");
                failed = true;
            }
        }

        return failed;
    }

    /**
     *
     * @param printerToCheck
     * @param cancellable
     * @return failed
     */
    public static boolean waitOnBusy(Printer printerToCheck, Cancellable cancellable)
    {
        boolean failed = false;

        try
        {
            Thread.sleep(1000);

            StatusResponse response = printerToCheck.transmitStatusRequest();
            while (response != null && response.getBusyStatus() != BusyStatus.NOT_BUSY && !BaseLookup.isShuttingDown())
            {
                Thread.sleep(100);
                response = printerToCheck.transmitStatusRequest();

                if (cancellable != null && cancellable.cancelled().get())
                {
                    failed = true;
                    break;
                }
            }
            if (response == null)
                failed = true;
            
        } catch (RoboxCommsException ex)
        {
            steno.error("Error requesting status");
            failed = true;
        } catch (InterruptedException ex)
        {
            steno.error("Interrupted during busy check");
            failed = true;
        }

        return failed;
    }

    public static boolean waitOnPrintFinished(Printer printerToCheck, Cancellable cancellable)
    {
        boolean failed = false;

        if (Platform.isFxApplicationThread())
        {
            throw new RuntimeException("Cannot call this function from the GUI thread");
        }

        //Slug the status check to make sure we see the status change
        try
        {
            Thread.sleep(1000);
        } catch (InterruptedException ex)
        {

        }

        while (printerToCheck.getPrintEngine().isRoboxPrinting()
                || printerToCheck.getPrintEngine().highIntensityCommsInProgressProperty().get()
                && !BaseLookup.isShuttingDown())
        {
            try
            {
                Thread.sleep(100);

                if (cancellable != null && cancellable.cancelled().get())
                {
                    failed = true;
                    break;
                }
            } catch (InterruptedException ex)
            {
                failed = true;
                steno.error("Interrupted whilst waiting on print job to complete");
            }
        }

        return failed;
    }

    /**
     * For each head chamber/heater check if a purge is necessary. Return true
     * if one or more nozzle heaters require a purge.
     *
     * @param printer
     * @param usedExtruders
     * @return
     */
    public static boolean isPurgeNecessary(Printer printer, List<Boolean> usedExtruders)
    {
        boolean purgeIsNecessary = false;

        for (int extruderNumber = 0; extruderNumber < usedExtruders.size(); extruderNumber++)
        {
            if (usedExtruders.get(extruderNumber))
            {
                purgeIsNecessary |= isPurgeNecessaryForExtruder(printer, extruderNumber);
            }
        };

        return purgeIsNecessary;
    }

    /**
     * Return true if the given nozzle heater requires a purge.
     *
     * @param printer
     * @param extruderNumber
     * @return
     */
    public static boolean isPurgeNecessaryForExtruder(Printer printer,
            int extruderNumber)
    {
        float targetNozzleTemperature = 0;
        Filament settingsFilament = null;
        if (extruderNumber == 0)
        {
            settingsFilament = printer.effectiveFilamentsProperty().get(0);
        } else if (extruderNumber == 1)
        {
            settingsFilament = printer.effectiveFilamentsProperty().get(1);
        } else
        {
            throw new RuntimeException("Don't know which filament to use for nozzle heater "
                    + extruderNumber);
        }
        if (settingsFilament != null)
        {
            targetNozzleTemperature = settingsFilament.getNozzleTemperature();
        } else
        {
            throw new RuntimeException("No filament set in printer settings");
        }
        // A reel is attached - check to see if the temperature is different from that stored on the head

        int nozzleNumber = -1;

        if (printer.headProperty().get().headTypeProperty().get() == Head.HeadType.DUAL_MATERIAL_HEAD)
        {
            if (extruderNumber == 0)
            {
                nozzleNumber = 1;
            } else
            {
                nozzleNumber = 0;
            }
        } else
        {
            nozzleNumber = 0;
        }

        if (settingsFilament == FilamentContainer.UNKNOWN_FILAMENT)
        {
            return false;
        } else if (Math.abs(targetNozzleTemperature
                - printer.headProperty().get().getNozzleHeaters().get(nozzleNumber).
                        lastFilamentTemperatureProperty().get())
                > BaseConfiguration.maxPermittedTempDifferenceForPurge)
        {
            return true;
        }

        return false;
    }

    /**
     *
     * @param printer
     * @return
     */
    public PurgeResponse offerPurgeIfNecessary(Printer printer, ObservableList<Boolean> usedExtruders)
    {
        PurgeResponse purgeConsent = PurgeResponse.NOT_NECESSARY;
        if (isPurgeNecessary(printer, usedExtruders) && purgeDialogVisible == false)
        {
            purgeDialogVisible = true;

            purgeConsent = BaseLookup.getSystemNotificationHandler().showPurgeDialog();

            purgeDialogVisible = false;
        }

        return purgeConsent;
    }

    public static boolean waitUntilTemperatureIsReached(ReadOnlyIntegerProperty temperatureProperty,
            Task task, int temperature, int tolerance, int timeoutSec) throws InterruptedException
    {
        return waitUntilTemperatureIsReached(temperatureProperty,
                task, temperature, tolerance, timeoutSec,
                (Cancellable) null);
    }

    public static boolean waitUntilTemperatureIsReached(ReadOnlyIntegerProperty temperatureProperty,
            Task task, int temperature, int tolerance, int timeoutSec, Cancellable cancellable) throws InterruptedException
    {
        int minTemp = temperature - tolerance;
        int maxTemp = temperature + tolerance;
        long timestampAtStart = System.currentTimeMillis();
        long timeoutMillis = timeoutSec * 1000;
        BooleanProperty failed = new SimpleBooleanProperty(false);

        ChangeListener<Boolean> cancelChangeListener = new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                failed.set(true);
            }
        };

        WeakChangeListener weakCancelChangeListener = new WeakChangeListener<>(cancelChangeListener);

        if (cancellable != null)
        {
            cancellable.cancelled().addListener(weakCancelChangeListener);
        }

        if (task != null || cancellable != null)
        {
            try
            {
                while ((temperatureProperty.get() < minTemp
                        || temperatureProperty.get() > maxTemp)
                        && !failed.get())
                {
                    if (task != null && task.isCancelled() || (cancellable != null
                            && cancellable.cancelled().get()))
                    {
                        break;
                    }
                    Thread.sleep(100);

                    long currentTimeMillis = System.currentTimeMillis();
                    if ((currentTimeMillis - timestampAtStart) >= timeoutMillis)
                    {
                        failed.set(true);
                        break;
                    }
                }
            } catch (InterruptedException ex)
            {
                steno.error("Interrupted during busy check");
                failed.set(true);
            }
        } else
        {
            try
            {
                while (temperatureProperty.get() < minTemp
                        || temperatureProperty.get() > maxTemp)
                {
                    Thread.sleep(100);

                    long currentTimeMillis = System.currentTimeMillis();
                    if ((currentTimeMillis - timestampAtStart) >= timeoutMillis)
                    {
                        failed.set(true);
                        break;
                    }
                }
            } catch (InterruptedException ex)
            {
                steno.error("Interrupted during busy check");
                failed.set(true);
            }
        }

        if (cancellable != null)
        {
            cancellable.cancelled().removeListener(weakCancelChangeListener);
        }

        return failed.get();
    }

    public static float deriveNozzle1OverrunFromOffsets(float nozzle1Offset, float nozzle2Offset)
    {
        float delta = nozzle2Offset - nozzle1Offset;
        float halfdelta = delta / 2;

        float nozzle1Overrun = -(nozzle1Offset + halfdelta);
        float nozzle2Overrun = nozzle1Overrun + delta;

        return nozzle1Overrun;
    }

    public static float deriveNozzle2OverrunFromOffsets(float nozzle1Offset, float nozzle2Offset)
    {
        float delta = nozzle2Offset - nozzle1Offset;
        float halfdelta = delta / 2;

        float nozzle1Overrun = -(nozzle1Offset + halfdelta);
        float nozzle2Overrun = nozzle1Overrun + delta;

        return nozzle2Overrun;
    }

    public static float deriveNozzle1ZOffsetsFromOverrun(float nozzle1OverrunValue,
            float nozzle2OverrunValue)
    {
        float offsetAverage = -nozzle1OverrunValue;
        float delta = (nozzle2OverrunValue - nozzle1OverrunValue) / 2;
        float nozzle1Offset = offsetAverage - delta;

        return nozzle1Offset;
    }

    public static float deriveNozzle2ZOffsetsFromOverrun(float nozzle1OverrunValue,
            float nozzle2OverrunValue)
    {
        float offsetAverage = -nozzle1OverrunValue;
        float delta = (nozzle2OverrunValue - nozzle1OverrunValue) / 2;
        float nozzle2Offset = offsetAverage + delta;

        return nozzle2Offset;
    }

    public static boolean printJobIDIndicatesPrinting(String printJobID)
    {
        boolean printing = true;
        if (printJobID == null
                || (printJobID.length() > 0
                && printJobID.charAt(0) == '\0')
                || printJobID.equals(""))
        {
            printing = false;
        }
        return printing;
    }
}
