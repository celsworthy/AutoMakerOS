package celtech.roboxbase.printerControl.model;

import celtech.roboxbase.MaterialType;
import celtech.roboxbase.comms.CommandInterface;
import celtech.roboxbase.comms.events.ErrorConsumer;
import celtech.roboxbase.comms.events.RoboxResponseConsumer;
import celtech.roboxbase.comms.exceptions.RoboxCommsException;
import celtech.roboxbase.comms.remote.BusyStatus;
import celtech.roboxbase.comms.remote.EEPROMState;
import celtech.roboxbase.comms.remote.PauseStatus;
import celtech.roboxbase.comms.remote.clear.SuitablePrintJob;
import celtech.roboxbase.comms.rx.*;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.Macro;
import celtech.roboxbase.configuration.fileRepresentation.PrinterDefinitionFile;
import celtech.roboxbase.configuration.fileRepresentation.PrinterEdition;
import celtech.roboxbase.configuration.hardwarevariants.PrinterType;
import celtech.roboxbase.postprocessor.PrintJobStatistics;
import celtech.roboxbase.printerControl.PrinterStatus;
import celtech.roboxbase.printerControl.model.statetransitions.calibration.NozzleHeightStateTransitionManager;
import celtech.roboxbase.printerControl.model.statetransitions.calibration.NozzleOpeningStateTransitionManager;
import celtech.roboxbase.printerControl.model.statetransitions.calibration.SingleNozzleHeightStateTransitionManager;
import celtech.roboxbase.printerControl.model.statetransitions.calibration.XAndYStateTransitionManager;
import celtech.roboxbase.printerControl.model.statetransitions.purge.PurgeStateTransitionManager;
import celtech.roboxbase.services.gcodegenerator.GCodeGeneratorResult;
import celtech.roboxbase.services.gcodegenerator.StylusGCodeGeneratorResult;
import celtech.roboxbase.services.printing.DatafileSendAlreadyInProgress;
import celtech.roboxbase.services.printing.DatafileSendNotInitialised;
import celtech.roboxbase.utils.AxisSpecifier;
import celtech.roboxbase.utils.RectangularBounds;
import celtech.roboxbase.utils.models.PrintableProject;
import celtech.roboxbase.utils.tasks.Cancellable;
import celtech.roboxbase.utils.tasks.TaskResponder;
import java.util.List;
import java.util.Optional;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

/**
 *
 * @author Ian
 */
public interface Printer extends RoboxResponseConsumer
{

    enum NozzleHeaters
    {

        NOZZLE_HEATER_0, NOZZLE_HEATER_1, NOZZLE_HEATER_BOTH;
    }

    public ReadOnlyObjectProperty<PrinterDefinitionFile> printerConfigurationProperty();

    public void setPrinterConfiguration(PrinterDefinitionFile printerConfigurationFile);

    public ReadOnlyObjectProperty<PrinterEdition> printerEditionProperty();

    public void setPrinterEdition(PrinterEdition printerEdition);
    
    public ReadOnlyObjectProperty<PrinterConnection> printerConnectionProperty();

    public void setPrinterConnection(PrinterConnection printerConnection);
    
    //Returns Width, Depth and Height centre point
    public Point3D getPrintVolumeCentre();

    public boolean isBiggerThanPrintVolume(RectangularBounds bounds);

    public ReadOnlyObjectProperty<Head> headProperty();

    /**
     *
     * @param gcodeToSend
     */
    public void addToGCodeTranscript(String gcodeToSend);

    /*
     * Cancel
     */
    public ReadOnlyBooleanProperty canCancelProperty();

    /*
     * Print
     */
    public ReadOnlyBooleanProperty canPrintProperty();
    
    /*
     * Can open or close a nozzle
     */
    public ReadOnlyBooleanProperty canOpenCloseNozzleProperty();

    /**
     * Can perform a nozzle height calibration
     */
    public ReadOnlyBooleanProperty canCalibrateNozzleHeightProperty();

    /**
     * Can perform an XY alignment calibration
     */
    public ReadOnlyBooleanProperty canCalibrateXYAlignmentProperty();

    /**
     * Can perform a nozzle opening calibration
     */
    public ReadOnlyBooleanProperty canCalibrateNozzleOpeningProperty();

    /*
     * Purge
     */
    public ReadOnlyBooleanProperty canPurgeHeadProperty();

    public void resetPurgeTemperatureForNozzleHeater(Head headToWrite, int nozzleHeaterNumber);

    public PurgeStateTransitionManager startPurge(boolean requireSafetyFeatures) throws PrinterException;

    /*
     * Calibrate head
     */
    public ReadOnlyBooleanProperty canCalibrateHeadProperty();

    public XAndYStateTransitionManager startCalibrateXAndY(boolean safetyFeaturesRequired) throws PrinterException;

    public NozzleHeightStateTransitionManager startCalibrateNozzleHeight(boolean safetyFeaturesRequired) throws PrinterException;

    public SingleNozzleHeightStateTransitionManager startCalibrateSingleNozzleHeight(boolean safetyFeaturesRequired) throws PrinterException;

    public NozzleOpeningStateTransitionManager startCalibrateNozzleOpening(boolean safetyFeaturesRequired) throws PrinterException;

    public NozzleHeightStateTransitionManager getNozzleHeightCalibrationStateManager();

    public NozzleOpeningStateTransitionManager getNozzleOpeningCalibrationStateManager();

    public XAndYStateTransitionManager getNozzleAlignmentCalibrationStateManager();

    /*
     * Remove head
     */
    public ReadOnlyBooleanProperty canRemoveHeadProperty();

    public void forcedCancel(TaskResponder responder) throws PrinterException;

    public void cancel(TaskResponder responder, boolean safetyFeaturesRequired) throws PrinterException;

    public void gotoNozzlePosition(float position);

    public void closeNozzleFully() throws PrinterException;

    public void ejectFilament(int extruderNumber, TaskResponder responder) throws PrinterException;

    public ObservableList<Extruder> extrudersProperty();

    public AckResponse formatHeadEEPROM() throws PrinterException;

    public AckResponse formatHeadEEPROM(boolean dontPublishResult) throws PrinterException;

    public AckResponse formatReelEEPROM(int reelNumber) throws PrinterException;

    public ObservableList<String> gcodeTranscriptProperty();

    public ReadOnlyBooleanProperty canPauseProperty();

    public ReadOnlyBooleanProperty canResumeProperty();

    public int getDataFileSequenceNumber();

    public void resetDataFileSequenceNumber();

    public void setDataFileSequenceNumberStartPoint(int startingSequenceNumber);

    public PrintEngine getPrintEngine();

    public PrinterAncillarySystems getPrinterAncillarySystems();

    public PrinterIdentity getPrinterIdentity();
    
    /**
     * Find the {@link PrinterType} from the {@link PrinterDefinitionFile}
     * 
     * @return the PrinterType of the printer
     */
    public PrinterType findPrinterType();

    /*
     * Door open
     */
    public ReadOnlyBooleanProperty canOpenDoorProperty();

    public void goToOpenDoorPosition(TaskResponder responder, boolean safetyFeaturesRequired) throws PrinterException;

    public void goToOpenDoorPositionDontWait(TaskResponder responder) throws PrinterException;

    public void goToTargetBedTemperature();

    public void goToTargetNozzleHeaterTemperature(int nozzleHeaterNumber);

    public void goToZPosition(double position);

    public void goToZPosition(double position, int feedrate_mmPerMin);

    public void goToXYPosition(double xPosition, double yPosition);

    public void goToXYZPosition(double xPosition, double yPosition, double zPosition);

    public void executeMacroWithoutPurgeCheck(Macro macro) throws PrinterException;

    public void executeMacroWithoutPurgeCheck(Macro macro,
            boolean requireNozzle0, boolean requireNozzle1,
            boolean requireSafetyFeatures) throws PrinterException;

    public void homeX();

    public void homeY();

    public void homeZ();

    public void probeX();

    public float getXDelta() throws PrinterException;

    public void probeY();

    public float getYDelta() throws PrinterException;

    public void probeZ();

    public float getZDelta() throws PrinterException;

    public TemperatureAndPWMData getTemperatureAndPWMData() throws PrinterException;

    public void levelGantryRaw();

    public boolean initialiseDataFileSend(String fileID, boolean jobCanBeReprinted) throws DatafileSendAlreadyInProgress, RoboxCommsException;

    public SendFile requestSendFileReport() throws RoboxCommsException;

    /**
     *
     * @param jobUUID
     * @throws RoboxCommsException
     */
    public void initiatePrint(String jobUUID) throws RoboxCommsException;

    public boolean isPrintInitiated();

    public void jogAxis(AxisSpecifier axis, float distance, float feedrate, boolean use_G1) throws PrinterException;

    public void openNozzleFully() throws PrinterException;

    public void pause() throws PrinterException;

    public void printProject(PrintableProject printableProject, Optional<GCodeGeneratorResult> potentialGCodeGenResult, boolean safetyFeaturesRequired) throws PrinterException;

    public void printStylusProject(PrintableProject printableProject, Optional<StylusGCodeGeneratorResult> potentialGCodeGenResult, boolean safetyFeaturesRequired) throws PrinterException;
    
    public ReadOnlyObjectProperty<PrinterStatus> printerStatusProperty();

    @Override
    public void processRoboxResponse(RoboxRxPacket rxPacket);

    public FirmwareResponse readFirmwareVersion() throws PrinterException;

    public HeadEEPROMDataResponse readHeadEEPROM(boolean dontPublishResponseEvent) throws RoboxCommsException;

    public PrinterIDResponse readPrinterID() throws PrinterException;

    public ReelEEPROMDataResponse readReelEEPROM(int reelNumber, boolean dontPublishResponseEvent) throws RoboxCommsException;

    public ObservableMap<Integer, Reel> reelsProperty();

    public void removeHead(TaskResponder responder, boolean safetyFeaturesRequired) throws PrinterException;

    public void resume() throws PrinterException;

    /**
     *
     * @param blockUntilFinished
     * @param cancellable
     * @throws PrinterException
     */
    public void homeAllAxes(boolean blockUntilFinished, Cancellable cancellable) throws PrinterException;

    /**
     *
     * @param requireNozzle0
     * @param requireNozzle1
     * @param safetyFeaturesRequired
     * @param blockUntilFinished
     * @param cancellable
     * @throws PrinterException
     */
    public void purgeMaterial(boolean requireNozzle0, boolean requireNozzle1, boolean safetyFeaturesRequired, boolean blockUntilFinished, Cancellable cancellable) throws PrinterException;

    public void miniPurge(boolean blockUntilFinished, Cancellable cancellable, int nozzleNumber, boolean safetyFeaturesRequired) throws PrinterException;

    public void testX(boolean blockUntilFinished, Cancellable cancellable) throws PrinterException;

    public void testY(boolean blockUntilFinished, Cancellable cancellable) throws PrinterException;

    public void testZ(boolean blockUntilFinished, Cancellable cancellable) throws PrinterException;

    public void speedTest(boolean blockUntilFinished, Cancellable cancellable) throws PrinterException;

    /**
     *
     * @param blockUntilFinished
     * @param cancellable
     * @throws PrinterException
     */
    public void levelGantry(boolean blockUntilFinished, Cancellable cancellable) throws PrinterException;

    /**
     *
     * @param blockUntilFinished
     * @param cancellable
     * @throws PrinterException
     */
    public void levelGantryTwoPoints(boolean blockUntilFinished, Cancellable cancellable) throws PrinterException;

    /**
     *
     * @param blockUntilFinished
     * @param cancellable
     * @throws PrinterException
     */
    public void levelY(boolean blockUntilFinished, Cancellable cancellable) throws PrinterException;

    /**
     *
     * @param nozzleNumber
     * @param blockUntilFinished
     * @param cancellable
     * @throws PrinterException
     */
    public void ejectStuckMaterial(int nozzleNumber, boolean blockUntilFinished, Cancellable cancellable, boolean safetyFeaturesRequired) throws PrinterException;

    /**
     *
     * @param nozzleNumber
     * @param blockUntilFinished
     * @param cancellable
     * @throws PrinterException
     */
    public void cleanNozzle(int nozzleNumber, boolean blockUntilFinished, Cancellable cancellable, boolean safetyFeaturesRequired) throws PrinterException;

    /**
     *
     * @param macro
     * @param cancellable
     * @throws PrinterException
     */
    public void runCommissioningTest(Macro macro, Cancellable cancellable) throws PrinterException;

    /**
     *
     * @param macro
     * @param cancellable
     * @param requireNozzle0
     * @param requireNozzle1
     * @throws PrinterException
     */
    public void runCommissioningTest(Macro macro, Cancellable cancellable, boolean requireNozzle0, boolean requireNozzle1) throws PrinterException;

    /**
     * This method 'prints' a GCode file. A print job is created and the printer
     * will manage extrusion dynamically. The printer will register as an error
     * handler for the duration of the 'print'.
     *
     * @param canDisconnectDuringPrint
     * @see executeMacro executeMacro - if you wish to run a macro rather than
     * execute a print job
     * @param fileName
     * @throws PrinterException
     */
    public void executeGCodeFile(String fileName, boolean canDisconnectDuringPrint) throws PrinterException;

    public void executeGCodeFile(String printJobName, String fileName, boolean canDisconnectDuringPrint) throws PrinterException;

    public void callbackWhenNotBusy(TaskResponder responder);

    public void selectNozzle(int nozzleNumber) throws PrinterException;

    public void sendDataFileChunk(String hexDigits, boolean lastPacket, boolean appendCRLF) throws DatafileSendNotInitialised, RoboxCommsException;

    public String sendRawGCode(String gCode, boolean addToTranscript);

    public void setAmbientLEDColour(Color colour) throws PrinterException;

    public void setAmbientTemperature(int targetTemperature);

    public void setBedFirstLayerTargetTemperature(int targetTemperature);

    public void setBedTargetTemperature(int targetTemperature);

    public void setNozzleHeaterTargetTemperature(int nozzleHeaterNumber, int targetTemperature);

    public void setReelLEDColour(Color colour) throws PrinterException;

    public void shutdown();

    public void switchAllNozzleHeatersOff();

    public void switchBedHeaterOff();

    public void switchNozzleHeaterOff(int heaterNumber);

    public void switchOffHeadFan() throws PrinterException;

    public void switchOffHeadLEDs() throws PrinterException;

    public void switchOnHeadFan() throws PrinterException;

    public void switchOnHeadLEDs() throws PrinterException;

    public void switchToAbsoluteMoveMode();

    public void switchToRelativeMoveMode();

    public ListFilesResponse transmitListFiles() throws RoboxCommsException;

    public AckResponse transmitReportErrors() throws RoboxCommsException;

    public void transmitResetErrors() throws RoboxCommsException;

    public void clearError(FirmwareError error);

    public void clearAllErrors();

    public ObservableList<FirmwareError> getActiveErrors();

    public ObservableList<FirmwareError> getCurrentErrors();

    /*
     * Higher level controls
     */
    public void transmitSetTemperatures(double nozzle0FirstLayerTarget, double nozzle0Target,
            double nozzle1FirstLayerTarget, double nozzle1Target,
            double bedFirstLayerTarget, double bedTarget, double ambientTarget) throws RoboxCommsException;

    public StatusResponse transmitStatusRequest() throws RoboxCommsException;

    public void transmitUpdateFirmware(final String firmwareID) throws PrinterException;

    public AckResponse transmitWriteHeadEEPROM(String headTypeCode, String headUniqueID,
            float maximumTemperature, float thermistorBeta, float thermistorTCal, float nozzle1XOffset,
            float nozzle1YOffset,
            float nozzle1ZOffset, float nozzle1BOffset,
            String filament0ID, String filament1ID, float nozzle2XOffset, float nozzle2YOffset,
            float nozzle2ZOffset, float nozzle2BOffset, float lastFilamentTemperature0,
            float lastFilamentTemperature1, float hourCounter) throws RoboxCommsException;

    public AckResponse transmitWriteReelEEPROM(int reelNumber, Filament filament) throws RoboxCommsException;

    public void transmitWriteReelEEPROM(int reelNumber, String filamentID,
            float reelFirstLayerNozzleTemperature, float reelNozzleTemperature,
            float reelFirstLayerBedTemperature,
            float reelBedTemperature,
            float reelAmbientTemperature, float reelFilamentDiameter, float reelFilamentMultiplier,
            float reelFeedRateMultiplier, float reelRemainingFilament, String friendlyName,
            MaterialType materialType, Color displayColour) throws RoboxCommsException;

    public void updatePrinterDisplayColour(Color displayColour) throws PrinterException;

    public void updatePrinterName(String chosenPrinterName) throws PrinterException;

    public void updatePrinterModelAndEdition(PrinterDefinitionFile printerDefinition, PrinterEdition printerEdition) throws PrinterException;

    public void updatePrinterWeek(String weekIdentifier) throws PrinterException;

    public void updatePrinterYear(String yearIdentifier) throws PrinterException;

    public void updatePrinterPONumber(String poIdentifier) throws PrinterException;

    public void updatePrinterSerialNumber(String serialIdentifier) throws PrinterException;

    public void updatePrinterIDChecksum(String checksum) throws PrinterException;

    public void updatePrinterIdentity(PrinterIdentity identity) throws PrinterException;

    public void writeHeadEEPROM(Head headToWrite, boolean readback) throws RoboxCommsException;

    /**
     *
     * @param headToWrite
     * @throws RoboxCommsException
     */
    public void writeHeadEEPROM(Head headToWrite) throws RoboxCommsException;

    void setPrinterStatus(PrinterStatus printerStatus);

    public ReadOnlyIntegerProperty printJobLineNumberProperty();

    public ReadOnlyStringProperty printJobIDProperty();

    public ReadOnlyObjectProperty<PauseStatus> pauseStatusProperty();

    public ReadOnlyBooleanProperty headPowerOnFlagProperty();

    public void resetHeadToDefaults() throws PrinterException;

    public void inhibitHeadIntegrityChecks(boolean inhibit);

    public void changeEFeedRateMultiplier(double feedRate) throws PrinterException;

    public void changeDFeedRateMultiplier(double feedRate) throws PrinterException;

    public void changeFilamentInfo(String extruderLetter,
            double filamentDiameter,
            double extrusionMultiplier) throws PrinterException;

    public void registerErrorConsumer(ErrorConsumer errorConsumer,
            List<FirmwareError> errorsOfInterest);

    public void registerErrorConsumerAllErrors(ErrorConsumer errorConsumer);

    public void deregisterErrorConsumer(ErrorConsumer errorConsumer);

    public void connectionEstablished();

    public List<Integer> requestDebugData(boolean addToGCodeTranscript);

    public ReadOnlyObjectProperty<BusyStatus> busyStatusProperty();

    /**
     * As of v741 firmware this is now handled within Robox Causes a reduction
     * in feedrate until the minimum value is reached. Returns false if the
     * limit has not been reached and true if it has (implying further action is
     * needed by the caller)
     *
     * @param error
     * @return
     */
//    public boolean doFilamentSlipActionWhilePrinting(FirmwareError error);
    public void extrudeUntilSlip(int extruderNumber, int extrusionVolume, int feedrate_mm_per_min) throws PrinterException;

    /**
     * This method is intended to be used by commissioning tools and should not
     * be called in normal operation. Causes the specified list of firmware
     * errors to be suppressed. The printer will not take any action if these
     * errors occur, beyond clearing the error flags in the firmware. This
     * method adds to the set of firmware errors that are being suppressed.
     *
     * @param firmwareErrors
     */
    public void suppressFirmwareErrors(FirmwareError... firmwareErrors);

    /**
     * This method is intended to be used by commissioning tools and should not
     * be called in normal operation. Cancel the suppression of firmware error
     * detection. All errors will be handled normally after calling this method.
     */
    public void cancelFirmwareErrorSuppression();

    /**
     * This method is intended to be used by commissioning tools and should not
     * be called in normal operation. Prevents the printer from repairing reel
     * or head eeprom data.
     *
     * @param suppress
     */
    public void suppressEEPROMErrorCorrection(boolean suppress);

    public void transferGCodeFileToPrinterAndCallbackWhenDone(String string, TaskResponder responder);

    public void loadFirmware(String firmwareFilePath);

    public ObservableList<EEPROMState> getReelEEPROMStateProperty();

    public ReadOnlyObjectProperty<EEPROMState> getHeadEEPROMStateProperty();

    public void startComms();

    public void stopComms();

    public void overrideFilament(int reelNumber, Filament filament);

    public ObservableMap<Integer, Filament> effectiveFilamentsProperty();

    public void setCommissioningTestMode(boolean inCommissioningMode);

    /**
     * Special interface to enable remote printer controller to bypass normal
     * printer logic NOT TO BE USED FOR ANY OTHER REASON
     *
     * @return
     */
    public CommandInterface getCommandInterface();

    public List<SuitablePrintJob> listJobsReprintableByMe();

    public List<PrintJobStatistics> listReprintableJobs();
    
    public List<SuitablePrintJob> createSuitablePrintJobsFromStatistics(List<PrintJobStatistics> printJobStats);
    
    public void tidyPrintJobDirectories();

    public boolean printJob(String printJobID);

    public boolean printJobFromDirectory(String printJobName, String directoryPath);

    // Methods provided to allow a simple, more thread-safe way of accessing the printer info
    public AckResponse getLastErrorResponse();

    public StatusResponse getLastStatusResponse();
}
