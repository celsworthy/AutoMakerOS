/*
 * Copyright 2014 CEL UK
 */
package celtech.printerControl.model;

import celtech.roboxbase.MaterialType;
import celtech.roboxbase.comms.CommandInterface;
import celtech.roboxbase.comms.events.ErrorConsumer;
import celtech.roboxbase.comms.exceptions.RoboxCommsException;
import celtech.roboxbase.comms.remote.BusyStatus;
import celtech.roboxbase.comms.remote.EEPROMState;
import celtech.roboxbase.comms.remote.PauseStatus;
import celtech.roboxbase.comms.remote.clear.SuitablePrintJob;
import celtech.roboxbase.comms.rx.AckResponse;
import celtech.roboxbase.comms.rx.FirmwareError;
import celtech.roboxbase.comms.rx.FirmwareResponse;
import celtech.roboxbase.comms.rx.HeadEEPROMDataResponse;
import celtech.roboxbase.comms.rx.ListFilesResponse;
import celtech.roboxbase.comms.rx.PrinterIDResponse;
import celtech.roboxbase.comms.rx.ReelEEPROM0DataResponse;
import celtech.roboxbase.comms.rx.ReelEEPROMDataResponse;
import celtech.roboxbase.comms.rx.RoboxRxPacket;
import celtech.roboxbase.comms.rx.SendFile;
import celtech.roboxbase.comms.rx.StatusResponse;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.Macro;
import celtech.roboxbase.configuration.fileRepresentation.HeadFile;
import celtech.roboxbase.configuration.fileRepresentation.PrinterDefinitionFile;
import celtech.roboxbase.configuration.fileRepresentation.PrinterEdition;
import celtech.roboxbase.configuration.hardwarevariants.PrinterType;
import celtech.roboxbase.postprocessor.PrintJobStatistics;
import celtech.roboxbase.printerControl.PrinterStatus;
import celtech.roboxbase.printerControl.model.Extruder;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.PrintEngine;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterAncillarySystems;
import celtech.roboxbase.printerControl.model.PrinterConnection;
import celtech.roboxbase.printerControl.model.PrinterException;
import celtech.roboxbase.printerControl.model.PrinterIdentity;
import celtech.roboxbase.printerControl.model.Reel;
import celtech.roboxbase.printerControl.model.TemperatureAndPWMData;
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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

/**
 * The TestPrinter class has a testable/mutable Head, Reels etc.
 *
 * @author tony
 */
public class TestPrinter implements Printer
{

    private final SimpleObjectProperty<Head> headProperty = new SimpleObjectProperty<>();
    private final ObservableMap<Integer, Reel> reelsProperty = FXCollections.observableHashMap();
    private final ObservableMap<Integer, Filament> effectiveFilaments = FXCollections.observableHashMap();
    private final ObservableList<Extruder> extrudersProperty = FXCollections.observableArrayList();
    protected final ObjectProperty<PrinterStatus> printerStatus = new SimpleObjectProperty(
            PrinterStatus.IDLE);
    protected final ObjectProperty<PauseStatus> pauseStatus = new SimpleObjectProperty<>(
            PauseStatus.NOT_PAUSED);
    protected final ObjectProperty<BusyStatus> busyStatus = new SimpleObjectProperty<>(
            BusyStatus.NOT_BUSY);
    private PrintEngine printEngine;
    protected final IntegerProperty printJobLineNumber = new SimpleIntegerProperty(0);
    protected final StringProperty printJobID = new SimpleStringProperty("");

    private final ObservableList<String> gcodeTranscript = FXCollections.observableArrayList();
    private final PrinterIdentity printerIdentity = new PrinterIdentity();
    private final PrinterAncillarySystems printerAncillarySystems = new PrinterAncillarySystems();
    private final ObjectProperty<PrinterDefinitionFile> printerConfiguration = new SimpleObjectProperty<>(null);

    private final BooleanProperty canRemoveHead = new SimpleBooleanProperty(false);
    private final BooleanProperty canPurgeHead = new SimpleBooleanProperty(false);
    private final BooleanProperty mustPurgeHead = new SimpleBooleanProperty(false);
    private final BooleanProperty canInitiateNewState = new SimpleBooleanProperty(false);
    private final BooleanProperty canPrint = new SimpleBooleanProperty(false);
    private final BooleanProperty canOpenCloseNozzle = new SimpleBooleanProperty(false);
    private final BooleanProperty canPause = new SimpleBooleanProperty(false);
    private final BooleanProperty canResume = new SimpleBooleanProperty(false);
    private final BooleanProperty canRunMacro = new SimpleBooleanProperty(false);
    private final BooleanProperty canCancel = new SimpleBooleanProperty(false);
    private final BooleanProperty canOpenDoor = new SimpleBooleanProperty(false);
    private final BooleanProperty canCalibrateHead = new SimpleBooleanProperty(false);
    private final BooleanProperty canCalibrateNozzleHeight = new SimpleBooleanProperty(false);
    private final BooleanProperty canCalibrateXYAlignment = new SimpleBooleanProperty(false);
    private final BooleanProperty canCalibrateNozzleOpening = new SimpleBooleanProperty(false);

    public TestPrinter()
    {
        this(1);
    }

    public TestPrinter(int numExtruders)
    {
        Extruder extruder0 = new Extruder("E");
        Extruder extruder1 = new Extruder("D");

        if (numExtruders > 0)
        {
            extruder0.isFittedProperty().set(true);
        }

        if (numExtruders > 1)
        {
            extruder1.isFittedProperty().set(true);
        }
        extrudersProperty.add(extruder0);
        extrudersProperty.add(extruder1);

        printEngine = new PrintEngine(this);
    }

    public void addHead()
    {
        HeadFile headFile = new HeadFile();
        headFile.setTypeCode("RBX01-SM");
        Head head = new TestHead(headFile);
        headProperty.setValue(head);
    }

    public void addHeadForHeadFile(HeadFile headFile)
    {
        Head head = new TestHead(headFile);
        headProperty.setValue(head);
    }

    public TestHead getHead()
    {
        return (TestHead) headProperty().get();
    }

    public void removeHead()
    {
        headProperty.setValue(null);
    }

    public void addReel(int i)
    {
        Reel reel = new Reel();
        reelsProperty.put(i, reel);
    }

    public void removeReel(int i)
    {
        reelsProperty.remove(i);
    }

    public void changeReel(int i)
    {
        ReelEEPROMDataResponse eepromData = new ReelEEPROM0DataResponse();
        eepromData.setFilamentID("ABC");
        eepromData.setAmbientTemperature(100);
        eepromData.setBedTemperature(120);
        eepromData.setDisplayColourString(Color.DARKCYAN.toString());
        eepromData.setFeedRateMultiplier(2);
        eepromData.setFilamentDiameter(3);
        eepromData.setFilamentMultiplier(2);
        eepromData.setFirstLayerBedTemperature(110);
        eepromData.setFirstLayerNozzleTemperature(180);
        eepromData.setFriendlyName("F1");
        eepromData.setMaterialType(MaterialType.N66);
        eepromData.setNozzleTemperature(205);
        eepromData.setRemainingFilament(85);
        reelsProperty().get(i).updateFromEEPROMData(eepromData);
    }

    public void loadFilament(int extruderNumber)
    {
        extrudersProperty().get(extruderNumber).filamentLoadedProperty().set(true);
    }

    @Override
    public ObservableList<Extruder> extrudersProperty()
    {
        return extrudersProperty;
    }

    @Override
    public ObservableMap<Integer, Reel> reelsProperty()
    {
        return reelsProperty;
    }

    @Override
    public void goToXYZPosition(double xPosition, double yPosition, double zPosition)
    {
    }

    @Override
    public void transmitWriteReelEEPROM(int reelNumber, String filamentID,
            float reelFirstLayerNozzleTemperature, float reelNozzleTemperature,
            float reelFirstLayerBedTemperature,
            float reelBedTemperature, float reelAmbientTemperature, float reelFilamentDiameter,
            float reelFilamentMultiplier, float reelFeedRateMultiplier, float reelRemainingFilament,
            String friendlyName,
            MaterialType materialType, Color displayColour) throws RoboxCommsException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void transmitSetTemperatures(double nozzle0FirstLayerTarget, double nozzle0Target,
            double nozzle1FirstLayerTarget, double nozzle1Target, double bedFirstLayerTarget,
            double bedTarget,
            double ambientTarget) throws RoboxCommsException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AckResponse formatReelEEPROM(int reelNumber) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addToGCodeTranscript(String gcodeToSend)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReadOnlyBooleanProperty canCancelProperty()
    {
        return canCancel;
    }

    @Override
    public ReadOnlyBooleanProperty canPrintProperty()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReadOnlyBooleanProperty canPurgeHeadProperty()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReadOnlyBooleanProperty canRemoveHeadProperty()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cancel(TaskResponder responder, boolean safetyFeaturesRequired) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void gotoNozzlePosition(float position)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void closeNozzleFully() throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void ejectFilament(int extruderNumber, TaskResponder responder) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AckResponse formatHeadEEPROM() throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ObservableList<String> gcodeTranscriptProperty()
    {
        return gcodeTranscript;
    }

    @Override
    public ReadOnlyBooleanProperty canPauseProperty()
    {
        return canPause;
    }

    @Override
    public ReadOnlyBooleanProperty canResumeProperty()
    {
        return canResume;
    }

    @Override
    public int getDataFileSequenceNumber()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PrintEngine getPrintEngine()
    {
        return printEngine;
    }

    @Override
    public PrinterAncillarySystems getPrinterAncillarySystems()
    {
        return printerAncillarySystems;
    }

    @Override
    public PrinterIdentity getPrinterIdentity()
    {
        return printerIdentity;
    }

    @Override
    public void goToTargetBedTemperature()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void goToTargetNozzleHeaterTemperature(int nozzleHeaterNumber)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void goToZPosition(double position)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReadOnlyObjectProperty<Head> headProperty()
    {
        return headProperty;
    }

    @Override
    public void homeZ()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean initialiseDataFileSend(String fileID, boolean jobCanBeReprinted) throws DatafileSendAlreadyInProgress, RoboxCommsException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void initiatePrint(String jobUUID) throws RoboxCommsException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isPrintInitiated()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void jogAxis(AxisSpecifier axis, float distance, float feedrate, boolean use_G1) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void openNozzleFully() throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void pause() throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReadOnlyObjectProperty<PrinterStatus> printerStatusProperty()
    {
        return printerStatus;
    }

    @Override
    public void processRoboxResponse(RoboxRxPacket rxPacket)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FirmwareResponse readFirmwareVersion() throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HeadEEPROMDataResponse readHeadEEPROM(boolean dontPublishResponseEvent) throws RoboxCommsException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PrinterIDResponse readPrinterID() throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeHead(TaskResponder responder, boolean safetyFeaturesRequired) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void resume() throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void selectNozzle(int nozzleNumber) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendDataFileChunk(String hexDigits, boolean lastPacket, boolean appendCRLF) throws DatafileSendNotInitialised, RoboxCommsException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String sendRawGCode(String gCode, boolean addToTranscript)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAmbientLEDColour(Color colour) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAmbientTemperature(int targetTemperature)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setBedFirstLayerTargetTemperature(int targetTemperature)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setBedTargetTemperature(int targetTemperature)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setNozzleHeaterTargetTemperature(int nozzleHeaterNumber, int targetTemperature)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setReelLEDColour(Color colour) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void shutdown()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void switchAllNozzleHeatersOff()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void switchBedHeaterOff()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void switchNozzleHeaterOff(int heaterNumber)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void switchOffHeadFan() throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void switchOffHeadLEDs() throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void switchOnHeadFan() throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void switchOnHeadLEDs() throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void switchToAbsoluteMoveMode()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void switchToRelativeMoveMode()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ListFilesResponse transmitListFiles() throws RoboxCommsException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AckResponse transmitReportErrors() throws RoboxCommsException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void transmitResetErrors() throws RoboxCommsException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public StatusResponse transmitStatusRequest() throws RoboxCommsException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void transmitUpdateFirmware(String firmwareID) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AckResponse transmitWriteHeadEEPROM(String headTypeCode, String headUniqueID,
            float maximumTemperature, float thermistorBeta, float thermistorTCal, float nozzle1XOffset,
            float nozzle1YOffset, float nozzle1ZOffset, float nozzle1BOffset,
            String filamentID0, String filamentID1, float nozzle2XOffset,
            float nozzle2YOffset, float nozzle2ZOffset, float nozzle2BOffset,
            float lastFilamentTemperature0, float lastFilamentTemperature1, float hourCounter) throws RoboxCommsException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updatePrinterDisplayColour(Color displayColour) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updatePrinterName(String chosenPrinterName) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getZDelta() throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void writeHeadEEPROM(Head headToWrite) throws RoboxCommsException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPrinterStatus(PrinterStatus printerStatus)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReadOnlyIntegerProperty printJobLineNumberProperty()
    {
        return printJobLineNumber;
    }

    @Override
    public ReadOnlyStringProperty printJobIDProperty()
    {
        return printJobID;
    }

    @Override
    public ReadOnlyObjectProperty pauseStatusProperty()
    {
        return pauseStatus;
    }

    @Override
    public XAndYStateTransitionManager startCalibrateXAndY(boolean safetyFeaturesRequired)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NozzleHeightStateTransitionManager startCalibrateNozzleHeight(boolean safetyFeaturesRequired)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public SingleNozzleHeightStateTransitionManager startCalibrateSingleNozzleHeight(boolean safetyFeaturesRequired)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReadOnlyBooleanProperty canCalibrateHeadProperty()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NozzleOpeningStateTransitionManager startCalibrateNozzleOpening(boolean safetyFeaturesRequired) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void executeGCodeFile(String fileName, boolean monitorForErrors) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void resetHeadToDefaults() throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void inhibitHeadIntegrityChecks(boolean inhibit)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReelEEPROMDataResponse readReelEEPROM(int reelNumber, boolean dontPublishResponseEvent) throws RoboxCommsException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AckResponse transmitWriteReelEEPROM(int reelNumber, Filament filament) throws RoboxCommsException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void registerErrorConsumer(ErrorConsumer errorConsumer,
            List<FirmwareError> errorsOfInterest)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deregisterErrorConsumer(ErrorConsumer errorConsumer)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void goToXYPosition(double xPosition, double yPosition)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void levelGantryRaw()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void goToOpenDoorPositionDontWait(TaskResponder responder) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReadOnlyBooleanProperty canOpenDoorProperty()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void registerErrorConsumerAllErrors(ErrorConsumer errorConsumer)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReadOnlyBooleanProperty canOpenCloseNozzleProperty()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReadOnlyBooleanProperty canCalibrateNozzleHeightProperty()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReadOnlyBooleanProperty canCalibrateXYAlignmentProperty()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReadOnlyBooleanProperty canCalibrateNozzleOpeningProperty()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updatePrinterWeek(String weekIdentifier) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updatePrinterYear(String yearIdentifier) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updatePrinterPONumber(String poIdentifier) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updatePrinterSerialNumber(String serialIdentifier) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updatePrinterIDChecksum(String checksum) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void connectionEstablished()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Integer> requestDebugData(boolean addToGCodeTranscript)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void callbackWhenNotBusy(TaskResponder responder)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReadOnlyObjectProperty busyStatusProperty()
    {
        return busyStatus;
    }

    @Override
    public void resetDataFileSequenceNumber()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDataFileSequenceNumberStartPoint(int startingSequenceNumber)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void changeFilamentInfo(String extruderLetter, double filamentDiameter,
            double extrusionMultiplier) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void homeX()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void homeY()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void probeX()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getXDelta() throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void probeY()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getYDelta() throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void probeZ()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TemperatureAndPWMData getTemperatureAndPWMData() throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void transferGCodeFileToPrinterAndCallbackWhenDone(String string, TaskResponder taskResponder)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void homeAllAxes(boolean blockUntilFinished, Cancellable cancellable) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void levelGantry(boolean blockUntilFinished, Cancellable cancellable) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void levelY(boolean blockUntilFinished, Cancellable cancellable) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void levelGantryTwoPoints(boolean blockUntilFinished, Cancellable cancellable) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void testX(boolean blockUntilFinished, Cancellable cancellable) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void testY(boolean blockUntilFinished, Cancellable cancellable) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void testZ(boolean blockUntilFinished, Cancellable cancellable) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void speedTest(boolean blockUntilFinished, Cancellable cancellable) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void suppressFirmwareErrors(FirmwareError... firmwareErrors)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cancelFirmwareErrorSuppression()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void suppressEEPROMErrorCorrection(boolean suppress)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SendFile requestSendFileReport() throws RoboxCommsException
    {
        return null;
    }

    @Override
    public void runCommissioningTest(Macro macro, Cancellable cancellable) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NozzleHeightStateTransitionManager getNozzleHeightCalibrationStateManager()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NozzleOpeningStateTransitionManager getNozzleOpeningCalibrationStateManager()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public XAndYStateTransitionManager getNozzleAlignmentCalibrationStateManager()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReadOnlyBooleanProperty headPowerOnFlagProperty()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void loadFirmware(String firmwareFilePath)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void startComms()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stopComms()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ObservableList<EEPROMState> getReelEEPROMStateProperty()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void overrideFilament(int reelNumber, Filament filament)
    {
        effectiveFilaments.put(reelNumber, filament);
    }

    @Override
    public ObservableMap<Integer, Filament> effectiveFilamentsProperty()
    {
        return effectiveFilaments;
    }

    @Override
    public void writeHeadEEPROM(Head headToWrite, boolean readback) throws RoboxCommsException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReadOnlyObjectProperty<EEPROMState> getHeadEEPROMStateProperty()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCommissioningTestMode(boolean inCommissioningMode)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void extrudeUntilSlip(int extruderNumber, int extrusionVolume, int feedrate_mm_per_min) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void goToZPosition(double position, int feedrate_mmPerMin)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void runCommissioningTest(Macro macro, Cancellable cancellable, boolean requireNozzle0, boolean requireNozzle1) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AckResponse formatHeadEEPROM(boolean dontPublishResult) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void resetPurgeTemperatureForNozzleHeater(Head headToWrite, int nozzleHeaterNumber)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void changeEFeedRateMultiplier(double feedRate) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void changeDFeedRateMultiplier(double feedRate) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CommandInterface getCommandInterface()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PurgeStateTransitionManager startPurge(boolean requireSafetyFeatures) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void goToOpenDoorPosition(TaskResponder responder, boolean safetyFeaturesRequired) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void printProject(PrintableProject printableProject, Optional<GCodeGeneratorResult> potentialGCodeGenResult, boolean safetyFeaturesRequired) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void purgeMaterial(boolean requireNozzle0, boolean requireNozzle1, boolean safetyFeaturesRequired, boolean blockUntilFinished, Cancellable cancellable) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void miniPurge(boolean blockUntilFinished, Cancellable cancellable, int nozzleNumber, boolean safetyFeaturesRequired) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void ejectStuckMaterial(int nozzleNumber, boolean blockUntilFinished, Cancellable cancellable, boolean safetyFeaturesRequired) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cleanNozzle(int nozzleNumber, boolean blockUntilFinished, Cancellable cancellable, boolean safetyFeaturesRequired) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReadOnlyObjectProperty<PrinterDefinitionFile> printerConfigurationProperty()
    {
        return printerConfiguration;
    }
    
    @Override
    public PrinterType findPrinterType() 
    {
        if(printerConfigurationProperty().get() == null) {
            return null;
        }
        
        return printerConfigurationProperty().get().getPrinterType();
    }

    @Override
    public void setPrinterConfiguration(PrinterDefinitionFile printerConfigurationFile)
    {
        this.printerConfiguration.set(printerConfigurationFile);
    }

    @Override
    public Point3D getPrintVolumeCentre()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isBiggerThanPrintVolume(RectangularBounds bounds)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReadOnlyObjectProperty<PrinterEdition> printerEditionProperty()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPrinterEdition(PrinterEdition printerEdition)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updatePrinterModelAndEdition(PrinterDefinitionFile printerDefinition, PrinterEdition printerEdition) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void forcedCancel(TaskResponder responder) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearError(FirmwareError error)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ObservableList<FirmwareError> getActiveErrors()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearAllErrors()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SuitablePrintJob> listJobsReprintableByMe()
    {
        return createSuitablePrintJobsFromStatistics(listReprintableJobs());
    }

    @Override
    public List<PrintJobStatistics> listReprintableJobs()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SuitablePrintJob> createSuitablePrintJobsFromStatistics(List<PrintJobStatistics> printJobStats)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean printJob(String printJobID)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public boolean printJobFromDirectory(String printJobName, String directoryPath)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AckResponse getLastErrorResponse()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public StatusResponse getLastStatusResponse()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updatePrinterIdentity(PrinterIdentity identity) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void executeGCodeFile(String printJobName, String fileName, boolean canDisconnectDuringPrint) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void executeMacroWithoutPurgeCheck(Macro macro) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void executeMacroWithoutPurgeCheck(Macro macro, boolean requireNozzle0, boolean requireNozzle1, boolean requireSafetyFeatures) throws PrinterException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReadOnlyObjectProperty<PrinterConnection> printerConnectionProperty() 
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPrinterConnection(PrinterConnection printerConnection) 
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void tidyPrintJobDirectories() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ObservableList<FirmwareError> getCurrentErrors() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void printStylusProject(PrintableProject printableProject, Optional<StylusGCodeGeneratorResult> potentialGCodeGenResult, boolean safetyFeaturesRequired) throws PrinterException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
