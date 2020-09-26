/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.printerControl.model.statetransitions.purge;

import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.appManager.SystemNotificationManager.PrinterErrorChoice;
import celtech.roboxbase.comms.events.ErrorConsumer;
import celtech.roboxbase.comms.rx.FirmwareError;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.statetransitions.calibration.CalibrationXAndYActions;
import celtech.roboxbase.utils.tasks.Cancellable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * The PurgePrinterErrorHandler listens for printer errors and if they occur then cause the user
 * to get a Continue/Abort dialog.
 *
 * @author tony
 */
public class PurgePrinterErrorHandler
{

    private final Stenographer steno = StenographerFactory.getStenographer(
        CalibrationXAndYActions.class.getName());

    private final Printer printer;
    private final Cancellable errorCancellable;
    private boolean showingFilamentSlipErrorDialog = false;

    public PurgePrinterErrorHandler(Printer printer, Cancellable errorCancellable)
    {
        this.printer = printer;
        this.errorCancellable = errorCancellable;
    }

    ErrorConsumer errorConsumer = (FirmwareError error) ->
    {
        steno.debug("ERROR consumed in purge");
        notifyUserErrorHasOccurredAndAbortIfNotSlip(error);
    };

    public void registerForPrinterErrors()
    {
        List<FirmwareError> errors = new ArrayList<>();
        errors.add(FirmwareError.ALL_ERRORS);
        printer.registerErrorConsumer(errorConsumer, errors);
    }

    /**
     * Check if a printer error has occurred and if so notify the user via a dialog box (only giving
     * the Abort option). Return a boolean indicating if the process should abort.
     */
    private void notifyUserErrorHasOccurredAndAbortIfNotSlip(FirmwareError error)
    {
        if (!errorCancellable.cancelled().get())
        {
            if (error == FirmwareError.B_POSITION_LOST
                    || error == FirmwareError.B_POSITION_WARNING
                    || error == FirmwareError.ERROR_BED_TEMPERATURE_DROOP)
            {
                // Do nothing for the moment...
                printer.clearError(error);
            } else if (error == FirmwareError.D_FILAMENT_SLIP
                || error == FirmwareError.E_FILAMENT_SLIP)
            {
                if (showingFilamentSlipErrorDialog) {
                    return;
                }
                showingFilamentSlipErrorDialog = true;
                String errorTitle = BaseLookup.i18n("purgeMaterial.filamentSlipTitle");
                String errorMessage = BaseLookup.i18n("purgeMaterial.filamentSlipMessage");
                String extruderName = "1";
                if (error == FirmwareError.D_FILAMENT_SLIP) {
                    extruderName = "2";
                }
                errorTitle = errorTitle.replace("%s", extruderName);
                errorMessage = errorMessage.replace("%s", extruderName);
                Optional<PrinterErrorChoice> response = BaseLookup.getSystemNotificationHandler().
                    showPrinterErrorDialog(
                        errorTitle,
                        errorMessage,
                        true,
                        true,
                        false,
                        false);
                
                showingFilamentSlipErrorDialog = false;

                boolean abort = false;

                if (response.isPresent())
                {
                    switch (response.get())
                    {
                        case ABORT:
                            abort = true;
                            break;
                    }
                } else
                {
                    abort = true;
                }

                if (abort)
                {
                    cancelPurge();
                }
            } else
            {
                // Must be something else
                // if not filament slip or B POSITION then cancel / abort printer activity immediately
                cancelPurge();
                BaseLookup.getSystemNotificationHandler().
                    showPrinterErrorDialog(
                        BaseLookup.i18n(error.getErrorTitleKey()),
                        BaseLookup.i18n("error.purge.cannotContinue"),
                        false,
                        false,
                        false,
                        true);
            }
        }
    }

    private void cancelPurge()
    {
        errorCancellable.cancelled().set(true);
    }

    public void deregisterForPrinterErrors()
    {
        printer.deregisterErrorConsumer(errorConsumer);
    }
}
