package celtech.roboxbase.appManager;

import celtech.roboxbase.comms.RoboxResetIDResult;
import celtech.roboxbase.comms.rx.FirmwareError;
import celtech.roboxbase.comms.rx.PrinterIDResponse;
import celtech.roboxbase.configuration.fileRepresentation.HeadFile;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.services.firmware.FirmwareLoadResult;
import celtech.roboxbase.services.firmware.FirmwareLoadService;
import celtech.roboxbase.utils.tasks.TaskResponder;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author Ian
 */
public interface SystemNotificationManager
{

    public enum PrinterErrorChoice
    {

        CONTINUE, ABORT, RETRY, OK;
    }

    void showInformationNotification(String title, String message);

    void showWarningNotification(String title, String message);

    void showErrorNotification(String title, String message);

    void showDismissableNotification(String message, String buttonText, NotificationType notificationType);

    /**
     * Returns true for no update and false for update
     *
     * @param printerToUpdate
     * @return True if the user has agreed to update, otherwise false
     */
    boolean askUserToUpdateFirmware(Printer printerToUpdate);
    
    boolean showDowngradeFirmwareDialog(Printer printerToUpdate);

    RoboxResetIDResult askUserToResetPrinterID(Printer printerToUse, PrinterIDResponse printerID);
    
    void processErrorPacketFromPrinter(FirmwareError error, Printer printer);

    void showCalibrationDialogue();

    void showFirmwareUpgradeStatusNotification(FirmwareLoadResult result);

    void showGCodePostProcessSuccessfulNotification();

    void showHeadUpdatedNotification();

    void showPrintJobCancelledNotification();

    void showPrintJobFailedNotification();

    void showPrintTransferInitiatedNotification();

    void showPrintTransferSuccessfulNotification(String printerName);

    void showPrintTransferFailedNotification(String printerName);

    void removePrintTransferFailedNotification();

    void showReprintStartedNotification();

    void showSDCardNotification();

    void showSliceSuccessfulNotification();

    void configureFirmwareProgressDialog(FirmwareLoadService firmwareLoadService);

    public void showNoSDCardDialog();

    void showNoPrinterIDDialog(Printer printer);

    boolean showOpenDoorDialog();

    boolean showModelTooBigDialog(String modelFilename);

    boolean showApplicationUpgradeDialog(String applicationName);
    
    boolean showAreYouSureYouWantToDowngradeDialog();

    public PurgeResponse showPurgeDialog();
    public PurgeResponse showPurgeDialog(boolean allowAutoPrint);

    public boolean showJobsTransferringShutdownDialog();

    public void showProgramInvalidHeadDialog(TaskResponder<HeadFile> taskResponse);
    public void hideProgramInvalidHeadDialog();

    public void showHeadNotRecognisedDialog(String printerName);

    /**
     * Show a dialog to the user asking them to choose between available
     * Continue, Abort or Retry actions when a printer error has occurred.
     */
    public Optional<PrinterErrorChoice> showPrinterErrorDialog(String title, String message,
            boolean showContinueOption,
            boolean showAbortOption, boolean showRetryOption, boolean showOKOption);

    public void showReelNotRecognisedDialog(String printerName);

    public void showReelUpdatedNotification();

    public void askUserToClearBed();

    public boolean confirmAdvancedMode();

    public void showKeepPushingFilamentNotification();

    public void hideKeepPushingFilamentNotification();

    public void showEjectFailedDialog(Printer printer, int nozzleNumber, FirmwareError error);

    public void showFilamentMotionCheckBanner();

    public void hideFilamentMotionCheckBanner();

    public void showFilamentStuckMessage();

    public void showLoadFilamentNowMessage();

    public boolean showModelIsInvalidDialog(Set<String> modelNames);

    public void clearAllDialogsOnDisconnect();
}
