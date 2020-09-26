package celtech.roboxbase.printerControl.model;

import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.camera.CameraInfo;
import celtech.roboxbase.comms.exceptions.RoboxCommsException;
import celtech.roboxbase.comms.remote.RoboxRemoteCommandInterface;
import celtech.roboxbase.comms.rx.SendFile;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.Macro;
import celtech.roboxbase.configuration.fileRepresentation.CameraSettings;
import celtech.roboxbase.configuration.hardwarevariants.PrinterType;
import celtech.roboxbase.postprocessor.PrintJobStatistics;
import celtech.roboxbase.printerControl.PrintJob;
import celtech.roboxbase.printerControl.PrintQueueStatus;
import celtech.roboxbase.printerControl.PrinterStatus;
import celtech.roboxbase.printerControl.comms.commands.GCodeMacros;
import celtech.roboxbase.printerControl.comms.commands.MacroLoadException;
import celtech.roboxbase.printerControl.comms.commands.MacroPrintException;
import celtech.roboxbase.services.camera.CameraTriggerManager;
import celtech.roboxbase.services.ControllableService;
import celtech.roboxbase.services.gcodegenerator.GCodeGeneratorResult;
import celtech.roboxbase.services.printing.GCodePrintResult;
import celtech.roboxbase.services.printing.TransferGCodeToPrinterService;
import celtech.roboxbase.utils.PrintJobUtils;
import celtech.roboxbase.utils.SystemUtils;
import celtech.roboxbase.utils.models.PrintableProject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author ianhudson
 */
public class PrintEngine implements ControllableService
{

    private final Stenographer steno = StenographerFactory.getStenographer(
            PrintEngine.class.getName());

    private Printer associatedPrinter = null;
//    public final AbstractSlicerService slicerService = new SlicerService();
//    public final PostProcessorService postProcessorService = new PostProcessorService();
    public final TransferGCodeToPrinterService transferGCodeToPrinterService = new TransferGCodeToPrinterService();
    private final IntegerProperty linesInPrintingFile = new SimpleIntegerProperty(0);

    /**
     * Indicates if ETC data is available for the current print
     */
    private final BooleanProperty etcAvailable = new SimpleBooleanProperty(false);

    private EventHandler<WorkerStateEvent> scheduledGCodePostProcessEventHandler = null;

    private EventHandler<WorkerStateEvent> scheduledPrintEventHandler = null;
    private EventHandler<WorkerStateEvent> cancelPrintEventHandler = null;
    private EventHandler<WorkerStateEvent> failedPrintEventHandler = null;
    private EventHandler<WorkerStateEvent> succeededPrintEventHandler = null;

    private final StringProperty printProgressTitle = new SimpleStringProperty();
    private final StringProperty printProgressMessage = new SimpleStringProperty();
    private final BooleanProperty dialogRequired = new SimpleBooleanProperty(
            false);
    private final DoubleProperty primaryProgressPercent = new SimpleDoubleProperty(
            0);
    private final DoubleProperty secondaryProgressPercent = new SimpleDoubleProperty(
            0);
    private final ObjectProperty<Date> printJobStartTime = new SimpleObjectProperty<>();
    public final ObjectProperty<Macro> macroBeingRun = new SimpleObjectProperty<>();

    private ObjectProperty<PrintQueueStatus> printQueueStatus = new SimpleObjectProperty<>(PrintQueueStatus.IDLE);
    private ObjectProperty<PrintJob> printJob = new SimpleObjectProperty<>(null);

    /*
     * 
     */
    private ChangeListener<Number> printLineNumberListener = null;
    private ChangeListener<String> printJobIDListener = null;

    private boolean consideringPrintRequest = false;
    ETCCalculator etcCalculator;

    /**
     * progressETC holds the number of seconds predicted for the ETC of the
     * print
     */
    private final IntegerProperty progressETC = new SimpleIntegerProperty();
    private final IntegerProperty totalDurationSeconds = new SimpleIntegerProperty(0);
    /**
     * The current layer being processed
     */
    private final IntegerProperty progressCurrentLayer = new SimpleIntegerProperty();
    /**
     * The total number of layers in the model being printed
     */
    private final IntegerProperty progressNumLayers = new SimpleIntegerProperty();

    private boolean canDisconnectDuringPrint = true;

    private CameraTriggerManager cameraTriggerManager;
    private boolean cameraIsEnabled = false;

    private BooleanProperty highIntensityCommsInProgress = new SimpleBooleanProperty(false);

    private boolean iAmTakingItThroughTheBackDoor = false;
    
    private boolean safetyFeaturesRequiredForCurrentJob = true;

    public PrintEngine(Printer associatedPrinter)
    {
        this.associatedPrinter = associatedPrinter;
        cameraTriggerManager = new CameraTriggerManager(associatedPrinter);

        cancelPrintEventHandler = (WorkerStateEvent t) ->
        {
            steno.info(t.getSource().getTitle() + " has been cancelled");
            Optional<Macro> macroRunning = Macro.getMacroForPrintJobID(((TransferGCodeToPrinterService) t.getSource()).getCurrentPrintJobID());

            if (!macroRunning.isPresent())
            {
                BaseLookup.getSystemNotificationHandler().showPrintJobCancelledNotification();
            }
        };

        failedPrintEventHandler = (WorkerStateEvent t) ->
        {
            steno.error(t.getSource().getTitle() + " has failed");
            Optional<Macro> macroRunning = Macro.getMacroForPrintJobID(((TransferGCodeToPrinterService) t.getSource()).getCurrentPrintJobID());
            if (!macroRunning.isPresent())
            {
                BaseLookup.getSystemNotificationHandler().showPrintJobFailedNotification();
            }
            try
            {
                associatedPrinter.cancel(null, safetyFeaturesRequiredForCurrentJob);
            } catch (PrinterException ex)
            {
                steno.error("Couldn't abort on print job fail");
            }
        };

        succeededPrintEventHandler = (WorkerStateEvent t) ->
        {
            GCodePrintResult result = (GCodePrintResult) (t.getSource().getValue());
            if (result.isSuccess())
            {
                steno.debug("Transfer of file to printer complete for job: " + result.getPrintJobID());
                {
                    Optional<Macro> macroRunning = Macro.getMacroForPrintJobID(result.getPrintJobID());

                    if (!macroRunning.isPresent() && canDisconnectDuringPrint)
                    {
                        BaseLookup.getSystemNotificationHandler().
                                showPrintTransferSuccessfulNotification(
                                        associatedPrinter.getPrinterIdentity().printerFriendlyNameProperty().
                                                get());
                    }
                }
            } else
            {
                Optional<Macro> macroRunning = Macro.getMacroForPrintJobID(result.getPrintJobID());

                if (!macroRunning.isPresent())
                {
                    BaseLookup.getSystemNotificationHandler().showPrintTransferFailedNotification(
                            associatedPrinter.getPrinterIdentity().printerFriendlyNameProperty().get());
                }
                steno.error("Submission of job to printer failed");
            }
        };

        printJobIDListener = (ObservableValue<? extends String> ov, String oldValue, String newValue) ->
        {
            detectAlreadyPrinting();
        };

        scheduledPrintEventHandler = (WorkerStateEvent t) ->
        {
            takingItThroughTheBackDoor(false);

            Optional<Macro> macroRunning = Macro.getMacroForPrintJobID(((TransferGCodeToPrinterService) t.getSource()).getCurrentPrintJobID());

            if (!macroRunning.isPresent())
            {
                BaseLookup.getSystemNotificationHandler().showPrintTransferInitiatedNotification();
            }
        };

        printLineNumberListener = new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> ov,
                    Number oldValue,
                    Number newValue)
            {
                if (etcAvailable.get())
                {
                    updateETCUsingETCCalculator(newValue);
                } else
                {
                    updateETCUsingLineNumber(newValue);
                }
            }
        };
        
        transferGCodeToPrinterService.setOnScheduled(scheduledPrintEventHandler);

        transferGCodeToPrinterService.setOnCancelled(cancelPrintEventHandler);

        transferGCodeToPrinterService.setOnFailed(failedPrintEventHandler);

        transferGCodeToPrinterService.setOnSucceeded(succeededPrintEventHandler);

        associatedPrinter.printJobLineNumberProperty().addListener(printLineNumberListener);
        associatedPrinter.printJobIDProperty().addListener(printJobIDListener);

        printQueueStatus.addListener(new ChangeListener<PrintQueueStatus>()
        {
            @Override
            public void changed(ObservableValue<? extends PrintQueueStatus> ov, PrintQueueStatus t, PrintQueueStatus t1)
            {
                if (t1 == PrintQueueStatus.PRINTING)
                {
                    printJob.set(new PrintJob(associatedPrinter.printJobIDProperty().get()));
                } else
                {
                    printJob.set(null);
                }
            }
        });

        highIntensityCommsInProgress.bind(transferGCodeToPrinterService.runningProperty());

        detectAlreadyPrinting();
    }

    /**
     * Create the ETCCalculator based on the given PrintJobStatistics.
     */
    private void makeETCCalculator(PrintJobStatistics printJobStatistics,
            Printer associatedPrinter)
    {
        int numberOfLines = printJobStatistics.getNumberOfLines();
        linesInPrintingFile.set(numberOfLines);
        Map<Integer, Double> layerNumberToPredictedDuration_E = printJobStatistics
                .getLayerNumberToPredictedDuration_E_FeedrateDependent();
        Map<Integer, Double> layerNumberToPredictedDuration_D = printJobStatistics
                .getLayerNumberToPredictedDuration_D_FeedrateDependent();
        Map<Integer, Double> layerNumberToPredictedDuration_feedrateIndependent = printJobStatistics
                .getLayerNumberToPredictedDuration_FeedrateIndependent();
        List<Integer> layerNumberToLineNumber = printJobStatistics.getLayerNumberToLineNumber();
        etcCalculator = new ETCCalculator(associatedPrinter,
                layerNumberToPredictedDuration_E,
                layerNumberToPredictedDuration_D,
                layerNumberToPredictedDuration_feedrateIndependent,
                layerNumberToLineNumber);
        if (layerNumberToLineNumber != null)
        {
            progressNumLayers.set(layerNumberToLineNumber.size());
        }
        primaryProgressPercent.unbind();
        primaryProgressPercent.set(0);
        totalDurationSeconds.set((int) etcCalculator.totalPredictedDurationAllLayers);
        progressETC.set(etcCalculator.getETCPredicted(0));
        etcAvailable.set(true);
    }

    private void updateETCUsingETCCalculator(Number newValue)
    {
        int lineNumber = newValue.intValue();
        primaryProgressPercent.set(etcCalculator.getPercentCompleteAtLine(lineNumber));
        progressETC.set(etcCalculator.getETCPredicted(lineNumber));
        progressCurrentLayer.set(etcCalculator.getCurrentLayerNumberForLineNumber(lineNumber));
    }

    private void updateETCUsingLineNumber(Number newValue)
    {
        if (linesInPrintingFile.get() > 0)
        {
            double percentDone = newValue.doubleValue()
                    / linesInPrintingFile.doubleValue();
            primaryProgressPercent.set(percentDone);
        }
    }

    public void makeETCCalculatorForJobOfUUID(String printJobID)
    {
        PrintJob localPrintJob = new PrintJob(printJobID);
        PrintJobStatistics statistics = null;
        try
        {
            statistics = localPrintJob.getStatistics();
            makeETCCalculator(statistics, associatedPrinter);
        } catch (IOException ex)
        {
            if (associatedPrinter.getCommandInterface() instanceof RoboxRemoteCommandInterface)
            {
                //OK - ask for the stats from the remote end
                try
                {
                    statistics = ((RoboxRemoteCommandInterface) associatedPrinter.getCommandInterface()).retrieveStatistics();
                    if (statistics != null)
                    {
                        makeETCCalculator(statistics, associatedPrinter);
                        statistics.writeStatisticsToFile(localPrintJob.getStatisticsFileLocation());
                    }
                } catch (RoboxCommsException | IOException rex)
                {
                    steno.debug("Failed to retrieve statistics from remote server");
                }
            }
        }

        if (statistics == null)
        {
            etcAvailable.set(false);
        }
    }

    /**
     *
     */
    public void shutdown()
    {
        stopAllServices();
    }

    public synchronized boolean printProject(PrintableProject printableProject, Optional<GCodeGeneratorResult> potentialGCodeGenResult, boolean safetyFeaturesRequired)
    {
        canDisconnectDuringPrint = true;
        etcAvailable.set(false);

        cameraIsEnabled = printableProject.isCameraEnabled();

        if (cameraIsEnabled)
        {
            cameraTriggerManager.setTriggerData(printableProject.getCameraTriggerData());
        }
        
        if (associatedPrinter.printerStatusProperty().get() == PrinterStatus.IDLE 
                && potentialGCodeGenResult.isPresent()
                && potentialGCodeGenResult.get().isSuccess())
        {
            printFromProject(printableProject);
            return true;
        }
        
        return false;
    }

    protected void printFromProject(PrintableProject printableProject) {
        String slicedFilesLocation = printableProject.getProjectLocation()
                + File.separator
                + printableProject.getPrintQuality();
           
        String jobUUID = SystemUtils.generate16DigitID();
        String printJobDirectoryName = BaseConfiguration.getPrintSpoolDirectory() + jobUUID;
        printableProject.setJobUUID(jobUUID);
        
        try {
            FileUtils.copyDirectory(new File(slicedFilesLocation), new File(printJobDirectoryName));
            PrintJobUtils.assignPrintJobIdToProject(jobUUID, printJobDirectoryName, printableProject.getPrintQuality().toString(), printableProject.getCameraData());
        } catch (IOException ex) {
            steno.exception("Error when copying sliced project into print job directory", ex);
        }
        
        deleteOldPrintJobDirectories();
        
        PrintJob newPrintJob = new PrintJob(jobUUID);
        printFileFromDisk(newPrintJob);
    }
    
    private void deleteOldPrintJobDirectories() {
        File printSpoolDirectory = new File(BaseConfiguration.getPrintSpoolDirectory());
        File[] filesOnDisk = printSpoolDirectory.listFiles();
        
        if (filesOnDisk.length > BaseConfiguration.maxPrintSpoolFiles) {
            int filesToDelete = filesOnDisk.length - BaseConfiguration.maxPrintSpoolFiles;
            
            Arrays.sort(filesOnDisk, (File f1, File f2) -> Long.valueOf(
                    f1.lastModified()).compareTo(f2.lastModified()));
            
            for (int i = 0; i < filesToDelete; i++) {
                FileUtils.deleteQuietly(filesOnDisk[i]);
            }
        }
    }

    private boolean printFileFromDisk(PrintJob printJob, int startFromLineNumber, boolean canBeReprinted)
    {
        String gCodeFileName = printJob.getRoboxisedFileLocation();
        String jobUUID = printJob.getJobUUID();
        boolean acceptedPrintRequest = false;
        canDisconnectDuringPrint = true;

        try
        {
            PrintJobStatistics printJobStatistics = printJob.getStatistics();
            CameraSettings cameraData = printJob.getCameraData();

            linesInPrintingFile.set(printJobStatistics.getNumberOfLines());

            BaseLookup.getTaskExecutor().runOnGUIThread(() ->
            {
                steno.info("Respooling job " + jobUUID + " to printer from line " + startFromLineNumber);
                transferGCodeToPrinterService.reset();
                transferGCodeToPrinterService.setCurrentPrintJobID(jobUUID);
                transferGCodeToPrinterService.setStartFromSequenceNumber(startFromLineNumber);
                transferGCodeToPrinterService.setModelFileToPrint(gCodeFileName);
                transferGCodeToPrinterService.setPrinterToUse(associatedPrinter);
                transferGCodeToPrinterService.setPrintJobStatistics(printJobStatistics);
                transferGCodeToPrinterService.setCameraData(cameraData);
                transferGCodeToPrinterService.setThisCanBeReprinted(canBeReprinted);
                transferGCodeToPrinterService.start();
            });
            acceptedPrintRequest = true;
        } catch (IOException ex)
        {
            steno.error("Couldn't get job statistics for job " + jobUUID);
        }
        return acceptedPrintRequest;
    }
    
    protected boolean printFileFromDisk(PrintJob printJob)
    {
        return printFileFromDisk(printJob, 0, true);
    }

    protected boolean spoolAndPrintFileFromDisk(PrintJob printJob)
    {
        PrintJob spoolJob = new PrintJob(printJob.getJobUUID());
        File spoolJobDirectory = new File(spoolJob.getJobDirectory());
        if (spoolJobDirectory.exists())
        {
            try 
            {
                // Delete the contents of the job directory.
                Files.walk(Paths.get(spoolJob.getJobDirectory()))
                     .filter(Files::isRegularFile)
                     .map(Path::toFile)
                     .forEach(File::delete);
            } 
            catch (IOException ex) 
            {
                steno.error("Couldn't empty job directory \"" + spoolJob.getJobDirectory() + "\"");
            }
        }
        spoolJobDirectory.mkdirs();
        
        try 
        {
            // Copy the GCode file.
            Path spoolGCodePath = Paths.get(spoolJob.getRoboxisedFileLocation());
            Path originalGCodePath = Paths.get(printJob.getRoboxisedFileLocation());
            Files.copy(originalGCodePath, spoolGCodePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Copy the statistics file.
            Path spoolStatisticsPath = Paths.get(spoolJob.getStatisticsFileLocation());
            Path originalStatisticsPath = Paths.get(printJob.getStatisticsFileLocation());
            Files.copy(originalStatisticsPath, spoolStatisticsPath, StandardCopyOption.REPLACE_EXISTING);
        
            // Copy the camera data file, if it exists.
            File originalCameraDataFile = new File(printJob.getCameraDataFileLocation());
            if (originalCameraDataFile.canRead()) {
                Path spoolCameraDataPath = Paths.get(spoolJob.getCameraDataFileLocation());
                Path originalCameraDataPath = Paths.get(printJob.getCameraDataFileLocation());
                Files.copy(originalStatisticsPath, spoolStatisticsPath, StandardCopyOption.REPLACE_EXISTING);
            }

            return printFileFromDisk(spoolJob, 0, true);
        } 
        catch (IOException ex) 
        {
            steno.error("Couldn't copy from \"" + spoolJob.getJobDirectory() + "\"");
        }
        return false;
    }

    private boolean reprintDirectFromPrinter(PrintJob printJob) throws RoboxCommsException
    {
        boolean acceptedPrintRequest;
        //Reprint directly from printer
        steno.info("Printing job " + printJob.getJobUUID() + " from printer store");
        if (macroBeingRun.get() == null)
        {
            BaseLookup.getSystemNotificationHandler().showReprintStartedNotification();
        }

        if (printJob.roboxisedFileExists())
        {
            try
            {
                linesInPrintingFile.set(printJob.getStatistics().getNumberOfLines());
            } catch (IOException ex)
            {
                steno.error("Couldn't get job statistics for job " + printJob.getJobUUID());
            }
        }
        associatedPrinter.initiatePrint(printJob.getJobUUID());
        acceptedPrintRequest = true;
        return acceptedPrintRequest;
    }

    /**
     *
     * @return
     */
    public ReadOnlyDoubleProperty secondaryProgressProperty()
    {
        return secondaryProgressPercent;
    }

    @Override
    public ReadOnlyBooleanProperty runningProperty()
    {
        return dialogRequired;
    }

    @Override
    public ReadOnlyStringProperty messageProperty()
    {
        return printProgressMessage;
    }

    @Override
    public ReadOnlyDoubleProperty progressProperty()
    {
        return primaryProgressPercent;
    }

    @Override
    public ReadOnlyStringProperty titleProperty()
    {
        return printProgressTitle;
    }

    @Override
    public boolean cancelRun()
    {
        return false;
    }

    public ReadOnlyIntegerProperty linesInPrintingFileProperty()
    {
        return linesInPrintingFile;
    }

    protected boolean printGCodeFile(final String printJobName, final String filename, final boolean useSDCard, final boolean canDisconnectDuringPrint) throws MacroPrintException
    {
        return printGCodeFile(printJobName, filename, useSDCard, false, canDisconnectDuringPrint);
    }

    protected boolean printGCodeFile(final String printJobName, final String filename, final boolean useSDCard,
            final boolean dontInitiatePrint, final boolean canDisconnectDuringPrint) throws MacroPrintException
    {
        boolean acceptedPrintRequest = false;
        consideringPrintRequest = true;
        this.canDisconnectDuringPrint = canDisconnectDuringPrint;

        //Create the print job directory
        String printUUID = createPrintJobDirectory();

        tidyPrintSpoolDirectory();

        String printjobFilename = BaseConfiguration.getPrintSpoolDirectory()
                + printUUID + File.separator + printUUID
                + BaseConfiguration.gcodeTempFileExtension;

        PrintJobStatistics printJobStatistics = null;
        if (printJobName != null)
        {
            PrintJob printJob = new PrintJob(printUUID);
            printJobStatistics = new PrintJobStatistics();
            printJobStatistics.setProjectName(printJobName);
            try
            {
                printJobStatistics.writeStatisticsToFile(printJob.getStatisticsFileLocation());
            } catch (IOException ex)
            {
                steno.exception("Failed to write statistics to file: " + printJob.getStatisticsFileLocation(), ex);
            }
        }

        File src = new File(filename);
        File dest = new File(printjobFilename);
        final PrintJobStatistics pjs = printJobStatistics;
        Optional<PrinterType> printerType = Optional.of(associatedPrinter.findPrinterType());
        try
        {
            FileUtils.copyFile(src, dest);
            BaseLookup.getTaskExecutor().runOnGUIThread(() ->
            {
                int numberOfLines = GCodeMacros.countLinesInMacroFile(dest, ";", printerType);
                linesInPrintingFile.set(numberOfLines);
                transferGCodeToPrinterService.reset();
                transferGCodeToPrinterService.setPrintUsingSDCard(useSDCard);
                transferGCodeToPrinterService.setCurrentPrintJobID(printUUID);
                transferGCodeToPrinterService.setModelFileToPrint(printjobFilename);
                transferGCodeToPrinterService.setPrinterToUse(associatedPrinter);
                transferGCodeToPrinterService.setPrintJobStatistics(pjs);
                transferGCodeToPrinterService.setThisCanBeReprinted(true);
                transferGCodeToPrinterService.dontInitiatePrint(dontInitiatePrint);
                transferGCodeToPrinterService.start();
                consideringPrintRequest = false;
            });

            acceptedPrintRequest = true;
        } catch (IOException ex)
        {
            steno.error("Error copying file");
        }

        return acceptedPrintRequest;
    }

    private void tidyPrintSpoolDirectory()
    {
        //Erase old print job directories
        File printSpoolDirectory = new File(
                BaseConfiguration.getPrintSpoolDirectory());
        File[] filesOnDisk = printSpoolDirectory.listFiles();
        if (filesOnDisk.length > BaseConfiguration.maxPrintSpoolFiles)
        {
            int filesToDelete = filesOnDisk.length
                    - BaseConfiguration.maxPrintSpoolFiles;
            Arrays.sort(filesOnDisk,
                    (File f1, File f2) -> Long.valueOf(f1.lastModified()).compareTo(
                            f2.lastModified()));
            for (int i = 0; i < filesToDelete; i++)
            {
                FileUtils.deleteQuietly(filesOnDisk[i]);
            }
        }
    }

    private void tidyMacroSpoolDirectory()
    {
        //Erase old print job directories
        File printSpoolDirectory = new File(
                BaseConfiguration.getApplicationStorageDirectory()
                + BaseConfiguration.macroFileSubpath);
        File[] filesOnDisk = printSpoolDirectory.listFiles();

        if (filesOnDisk.length > BaseConfiguration.maxPrintSpoolFiles)
        {
            int filesToDelete = filesOnDisk.length
                    - BaseConfiguration.maxPrintSpoolFiles;
            Arrays.sort(filesOnDisk,
                    (File f1, File f2) -> Long.valueOf(f1.lastModified()).compareTo(
                            f2.lastModified()));
            for (int i = 0; i < filesToDelete; i++)
            {
                FileUtils.deleteQuietly(filesOnDisk[i]);
            }
        }
    }

    protected boolean runMacroPrintJob(Macro macro,
            boolean requireNozzle0,
            boolean requireNozzle1,
            boolean requireSafetyFeatures) throws MacroPrintException
    {
        return runMacroPrintJob(macro, true, requireNozzle0, requireNozzle1, requireSafetyFeatures);
    }

    protected boolean runMacroPrintJob(Macro macro, boolean useSDCard,
            boolean requireNozzle0,
            boolean requireNozzle1,
            boolean requireSafetyFeatures) throws MacroPrintException
    {
        safetyFeaturesRequiredForCurrentJob = requireSafetyFeatures;
        macroBeingRun.set(macro);

        boolean acceptedPrintRequest = false;
        consideringPrintRequest = true;
        canDisconnectDuringPrint = false;

        //Create the print job directory
        String printUUID = macro.getMacroJobNumber();
        String printJobDirectoryName = BaseConfiguration.getApplicationStorageDirectory()
                + BaseConfiguration.macroFileSubpath;
        File printJobDirectory = new File(printJobDirectoryName);
        printJobDirectory.mkdirs();

        tidyMacroSpoolDirectory();

        String printjobFilename = printJobDirectoryName + printUUID
                + BaseConfiguration.gcodeTempFileExtension;

        File printjobFile = new File(printjobFilename);
        Optional<PrinterType> printerType = Optional.of(associatedPrinter.findPrinterType());
        try
        {
            String s = macro.getMacroFileName();
            String headTypeCode = null;
            Head head = associatedPrinter.headProperty().get();
            if (head != null)
                headTypeCode = head.typeCodeProperty().get();
            ArrayList<String> macroContents = GCodeMacros.getMacroContents(macro.getMacroFileName(),
                    printerType,
                    headTypeCode,
                    requireNozzle0,
                    requireNozzle1,
                    requireSafetyFeatures);

            // Write the contents of the macro file to the print area
            FileUtils.writeLines(printjobFile, macroContents, false);
        } catch (IOException ex)
        {
            throw new MacroPrintException("Error writing macro print job file: "
                    + printjobFilename + " : "
                    + ex.getMessage());
        } catch (MacroLoadException ex)
        {
            throw new MacroPrintException("Error whilst generating macro - " + ex.getMessage());
        }

        BaseLookup.getTaskExecutor().runOnGUIThread(() ->
        {
            int numberOfLines = GCodeMacros.countLinesInMacroFile(printjobFile, ";", printerType);
            linesInPrintingFile.set(numberOfLines);
            steno.info("Print service is in state:" + transferGCodeToPrinterService.stateProperty().get().name());
            if (transferGCodeToPrinterService.isRunning())
            {
                transferGCodeToPrinterService.cancel();
            }
            transferGCodeToPrinterService.reset();
            transferGCodeToPrinterService.setPrintUsingSDCard(useSDCard);
            transferGCodeToPrinterService.setStartFromSequenceNumber(0);
            transferGCodeToPrinterService.setCurrentPrintJobID(printUUID);
            transferGCodeToPrinterService.setModelFileToPrint(printjobFilename);
            transferGCodeToPrinterService.setPrinterToUse(associatedPrinter);
            transferGCodeToPrinterService.setThisCanBeReprinted(false);
            transferGCodeToPrinterService.start();
            consideringPrintRequest = false;
        });

        acceptedPrintRequest = true;

        return acceptedPrintRequest;
    }

    private String createPrintJobDirectory()
    {
        //Create the print job directory
        String printUUID = SystemUtils.generate16DigitID();
        String printJobDirectoryName = BaseConfiguration.getPrintSpoolDirectory()
                + printUUID;
        File printJobDirectory = new File(printJobDirectoryName);
        printJobDirectory.mkdirs();
        return printUUID;
    }

    public boolean isConsideringPrintRequest()
    {
        return consideringPrintRequest;
    }

    public IntegerProperty progressETCProperty()
    {
        return progressETC;
    }

    public IntegerProperty totalDurationSecondsProperty()
    {
        return totalDurationSeconds;
    }

    public ReadOnlyBooleanProperty etcAvailableProperty()
    {
        return etcAvailable;
    }

    public ReadOnlyIntegerProperty progressCurrentLayerProperty()
    {
        return progressCurrentLayer;
    }

    public ReadOnlyIntegerProperty progressNumLayersProperty()
    {
        return progressNumLayers;
    }

    /**
     * Stop all services, in the GUI thread. Block current thread until the
     * routine has completed.
     */
    protected void stopAllServices()
    {

        Callable<Boolean> stopServices = new Callable()
        {
            @Override
            public Boolean call() throws Exception
            {
                steno.debug("Shutdown print services...");
                if (transferGCodeToPrinterService.isRunning())
                {
                    steno.debug("Shutdown print service...");
                    transferGCodeToPrinterService.cancelRun();
                }
                steno.debug("Shutdown print services complete");
                return true;
            }
        };
        FutureTask<Boolean> stopServicesTask = new FutureTask<>(stopServices);
        BaseLookup.getTaskExecutor().runOnGUIThread(stopServicesTask);
        try
        {
            stopServicesTask.get();
        } catch (InterruptedException | ExecutionException ex)
        {
            steno.error("Error while stopping services: " + ex);
        }
    }

    public boolean reEstablishTransfer(String printJobID, int expectedSequenceNumber)
    {
        PrintJob printJob = new PrintJob(printJobID);
        boolean acceptedPrintRequest = false;

        if (printJob.roboxisedFileExists())
        {
            acceptedPrintRequest = printFileFromDisk(printJob, expectedSequenceNumber, false);
            if (macroBeingRun.get() == null)
            {
                BaseLookup.getSystemNotificationHandler().removePrintTransferFailedNotification();
            }
        }

        return acceptedPrintRequest;
    }

    public boolean isRoboxPrinting()
    {
        boolean roboxIsPrinting = false;

        String printJobID = associatedPrinter.printJobIDProperty().get();
        if (printJobID != null)
        {
            if (!printJobID.trim().equals("")
                    && printJobID.codePointAt(0) != 0)
            {
                roboxIsPrinting = true;
            }
        }

        return roboxIsPrinting;
    }

    private void detectAlreadyPrinting()
    {
        if (associatedPrinter
                != null)
        {
            if (isRoboxPrinting())
            {
                String printJobID = associatedPrinter.printJobIDProperty().get();

                if (!iAmTakingItThroughTheBackDoor
                        && !transferGCodeToPrinterService.isRunning())
                {
                    try
                    {
                        SendFile sendFileData = (SendFile) associatedPrinter.requestSendFileReport();

                        if (sendFileData != null
                                && sendFileData.getFileID() != null && !sendFileData.getFileID().equals(""))
                        {
                            if (reEstablishTransfer(sendFileData.getFileID(),
                                    sendFileData.
                                            getExpectedSequenceNumber()))
                            {
                                steno.info("The printer is printing an incomplete job: File ID: "
                                        + sendFileData.getFileID()
                                        + " Expected sequence number: " + sendFileData.getExpectedSequenceNumber());
                            }
                        }
                    } catch (RoboxCommsException ex)
                    {
                        steno.error(
                                "Error determining whether the printer has a partially transferred job in progress");
                    }
                }

                Optional<Macro> macroRunning = Macro.getMacroForPrintJobID(printJobID);

                if (macroRunning.isPresent())
                {
                    steno.debug("Printer "
                            + associatedPrinter.getPrinterIdentity().printerFriendlyName.get()
                            + " is running macro " + macroRunning.get().name());

                    macroBeingRun.set(macroRunning.get());
                    printQueueStatus.set(PrintQueueStatus.RUNNING_MACRO);
                    setParentPrintStatusIfIdle(PrinterStatus.RUNNING_MACRO_FILE);
                } else
                {
                    makeETCCalculatorForJobOfUUID(printJobID);

                    if (etcAvailable.get())
                    {
                        updateETCUsingETCCalculator(associatedPrinter.printJobLineNumberProperty().get());
                    } else
                    {
                        updateETCUsingLineNumber(associatedPrinter.printJobLineNumberProperty().get());
                    }

                    steno.debug("Printer "
                            + associatedPrinter.getPrinterIdentity().printerFriendlyName.get()
                            + " is printing");

                    printQueueStatus.set(PrintQueueStatus.PRINTING);
                    setParentPrintStatusIfIdle(PrinterStatus.PRINTING_PROJECT);
                }
            } else
            {
                printQueueStatus.set(PrintQueueStatus.IDLE);
                switch (associatedPrinter.printerStatusProperty().get())
                {
                    case PRINTING_PROJECT:
                    case RUNNING_MACRO_FILE:
                        associatedPrinter.setPrinterStatus(PrinterStatus.IDLE);
                        steno.info("Print Job complete - " + associatedPrinter.getPrinterIdentity().printerFriendlyName.get() + "---------------------------------------<");
                        break;
                }
                macroBeingRun.set(null);
            }
        }
    }

    private void setParentPrintStatusIfIdle(PrinterStatus desiredStatus)
    {
        switch (associatedPrinter.printerStatusProperty().get())
        {
            case IDLE:
                associatedPrinter.setPrinterStatus(desiredStatus);
                break;
        }
    }

    public ReadOnlyObjectProperty<PrintQueueStatus> printQueueStatusProperty()
    {
        return printQueueStatus;
    }

    public ReadOnlyObjectProperty<PrintJob> printJobProperty()
    {
        return printJob;
    }

    public ReadOnlyBooleanProperty highIntensityCommsInProgressProperty()
    {
        return highIntensityCommsInProgress;
    }

    public void takingItThroughTheBackDoor(boolean ohYesIAm)
    {
        iAmTakingItThroughTheBackDoor = ohYesIAm;
    }

    public boolean isBusy()
    {
        return macroBeingRun.get() != null;
    }
}
