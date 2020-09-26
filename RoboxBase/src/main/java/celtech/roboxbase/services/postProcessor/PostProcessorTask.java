package celtech.roboxbase.services.postProcessor;

import celtech.roboxbase.configuration.SlicerType;
import celtech.roboxbase.configuration.datafileaccessors.HeadContainer;
import celtech.roboxbase.configuration.fileRepresentation.HeadFile;
import celtech.roboxbase.postprocessor.RoboxiserResult;
import celtech.roboxbase.postprocessor.nouveau.PostProcessor;
import celtech.roboxbase.postprocessor.nouveau.PostProcessorFeature;
import celtech.roboxbase.postprocessor.nouveau.PostProcessorFeatureSet;
import celtech.roboxbase.printerControl.PrintJob;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.utils.models.PrintableMeshes;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Task;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class PostProcessorTask extends Task<GCodePostProcessingResult>
{

    private static final Stenographer STENO = StenographerFactory.getStenographer(
            PostProcessorTask.class.getName());

    private final String printJobUUID;
    private final PrintableMeshes printableMeshes;
    private final String printJobDirectory;
    private final Printer printerToUse;
    private final DoubleProperty taskProgress;
    private final SlicerType slicerType;

    public PostProcessorTask(
            String printJobUUID,
            PrintableMeshes printableMeshes,
            String printJobDirectory,
            Printer printerToUse,
            DoubleProperty taskProgress,
            SlicerType slicerType)
    {
        this.printJobUUID = printJobUUID;
        this.printableMeshes = printableMeshes;
        this.printJobDirectory = printJobDirectory;
        this.printerToUse = printerToUse;
        this.taskProgress = taskProgress;
        this.slicerType = slicerType;
        updateTitle("Post Processor");
        updateProgress(0.0, 100.0);
    }

    @Override
    protected GCodePostProcessingResult call() throws Exception
    {  
        if (isCancelled())
        {
            STENO.debug("Slice cancelled");
            return null;
        }
        
        String headType;
        if (printerToUse != null && printerToUse.headProperty().get() != null)
        {
            headType = printerToUse.headProperty().get().typeCodeProperty().get();
        } else
        {
            headType = HeadContainer.defaultHeadID;
        }

        PrintJob printJob = new PrintJob(printJobUUID, printJobDirectory);
        String gcodeFileToProcess = printJob.getGCodeFileLocation();
        String gcodeOutputFile = printJob.getRoboxisedFileLocation();

        GCodePostProcessingResult postProcessingResult = new GCodePostProcessingResult(printJobUUID, gcodeOutputFile, printerToUse, new RoboxiserResult());

        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();

        HeadFile headFileToUse;
        if (printerToUse == null
                || printerToUse.headProperty().get() == null)
        {
            headFileToUse = HeadContainer.getHeadByID(HeadContainer.defaultHeadID);
            ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
            ppFeatures.enableFeature(PostProcessorFeature.OPEN_AND_CLOSE_NOZZLES);
            ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
            ppFeatures.enableFeature(PostProcessorFeature.REPLENISH_BEFORE_OPEN);
        } else
        {
            headFileToUse = HeadContainer.getHeadByID(printerToUse.headProperty().get().typeCodeProperty().get());
            if (!headFileToUse.getTypeCode().equals("RBX01-SL")
                    && !headFileToUse.getTypeCode().equals("RBX01-DL"))
            {
                ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
                ppFeatures.enableFeature(PostProcessorFeature.OPEN_AND_CLOSE_NOZZLES);
                ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
                ppFeatures.enableFeature(PostProcessorFeature.REPLENISH_BEFORE_OPEN);
            }
        }

        if (printableMeshes.isCameraEnabled())
        {
            ppFeatures.enableFeature(PostProcessorFeature.INSERT_CAMERA_CONTROL_POINTS);
        }

        Map<Integer, Integer> objectToNozzleNumberMap = new HashMap<>();
        int objectIndex = 0;

        headFileToUse.getNozzles().get(0).getAssociatedExtruder();
        for (int extruderForModel : printableMeshes.getExtruderForModel())
        {
            Optional<Integer> nozzleForExtruder = headFileToUse.getNozzleNumberForExtruderNumber(extruderForModel);
            if (nozzleForExtruder.isPresent())
            {
                objectToNozzleNumberMap.put(objectIndex, nozzleForExtruder.get());
            } else
            {
                STENO.warning("Couldn't get extruder number for object " + objectIndex);
            }
            objectIndex++;
        }
        
        if (isCancelled())
        {
            STENO.debug("Slice cancelled");
            return null;
        }
        
        PostProcessor postProcessor = new PostProcessor(
                printJobUUID,
                printableMeshes.getProjectName(),
                printableMeshes.getUsedExtruders(),
                printerToUse,
                gcodeFileToProcess,
                gcodeOutputFile,
                headFileToUse,
                printableMeshes.getSettings(),
                printableMeshes.getPrintOverrides(),
                ppFeatures,
                headType,
                taskProgress,
                objectToNozzleNumberMap,
                printableMeshes.getCameraTriggerData(),
                printableMeshes.isSafetyFeaturesRequired(),
                slicerType);

        RoboxiserResult roboxiserResult = postProcessor.processInput(this);
        if (roboxiserResult.isSuccess())
        {
            roboxiserResult.getPrintJobStatistics().writeStatisticsToFile(printJob.getStatisticsFileLocation());
            postProcessingResult = new GCodePostProcessingResult(printJobUUID, gcodeOutputFile, printerToUse, roboxiserResult);
        }

        return postProcessingResult;
    }
}
