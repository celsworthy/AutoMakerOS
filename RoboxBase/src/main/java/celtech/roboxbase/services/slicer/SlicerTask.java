package celtech.roboxbase.services.slicer;

import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.MachineType;
import celtech.roboxbase.configuration.SlicerType;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.utils.TimeUtils;
import celtech.roboxbase.utils.exporters.AMFOutputConverter;
import celtech.roboxbase.utils.exporters.MeshExportResult;
import celtech.roboxbase.utils.exporters.MeshFileOutputConverter;
import celtech.roboxbase.utils.exporters.STLOutputConverter;
import celtech.roboxbase.utils.models.PrintableMeshes;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import javafx.concurrent.Task;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author ianhudson
 */
public class SlicerTask extends Task<SliceResult> implements ProgressReceiver
{
    private static final Stenographer STENO = StenographerFactory.getStenographer(SlicerTask.class.getName());
    private static final TimeUtils TIME_UTILS = new TimeUtils();
    private static final String SLICER_TIMER_NAME = "Slicer";
    
    private final String printJobUUID;
    private final PrintableMeshes printableMeshes;
    private final String printJobDirectory;
    private final Printer printerToUse;
    private final ProgressReceiver progressReceiver;

    public SlicerTask(String printJobUUID,
            PrintableMeshes printableMeshes,
            String printJobDirectory,
            Printer printerToUse,
            ProgressReceiver progressReceiver)
    {
        this.printJobUUID = printJobUUID;
        this.printableMeshes = printableMeshes;
        this.printJobDirectory = printJobDirectory;
        this.printerToUse = printerToUse;
        this.progressReceiver = progressReceiver;
        updateProgress(0.0, 100.0);
    }

    @Override
    protected SliceResult call() throws Exception
    {
        if (isCancelled())
        {
            STENO.debug("Slice cancelled");
            return null;
        }

        STENO.debug("slice " + printableMeshes.getSettings().getName());
        updateTitle("Slicer");
        updateMessage("Preparing model for conversion");
        updateProgress(0.0, 100.0);

        STENO.debug("Starting slicing");
        String timerUUID = UUID.randomUUID().toString();
        TIME_UTILS.timerStart(timerUUID, SLICER_TIMER_NAME);
        
        SlicerType slicerType = printableMeshes.getDefaultSlicerType();

        MeshFileOutputConverter outputConverter = null;

        if (slicerType == SlicerType.Slic3r)
        {
            outputConverter = new AMFOutputConverter();
        } else
        {
            outputConverter = new STLOutputConverter();
        }

        MeshExportResult meshExportResult = null;

        // Output multiple files if we are using Cura
        if (printerToUse == null
                || printerToUse.headProperty().get() == null
                || printerToUse.headProperty().get().headTypeProperty().get() == Head.HeadType.SINGLE_MATERIAL_HEAD)
        {
            meshExportResult = outputConverter.outputFile(printableMeshes.getMeshesForProcessing(), printJobUUID, printJobDirectory,
                    true);
        } else
        {
            meshExportResult = outputConverter.outputFile(printableMeshes.getMeshesForProcessing(), printJobUUID, printJobDirectory,
                    false);
        }
        
        if (isCancelled())
        {
            STENO.debug("Slice cancelled");
            return null;
        }

        Vector3D centreOfPrintedObject = meshExportResult.getCentre();

        boolean succeeded = sliceFile(printJobUUID, 
                printJobDirectory, 
                slicerType, 
                meshExportResult.getCreatedFiles(), 
                printableMeshes.getExtruderForModel(), 
                centreOfPrintedObject, 
                progressReceiver,
                printableMeshes.getNumberOfNozzles());

        try
        {
            TIME_UTILS.timerStop(timerUUID, SLICER_TIMER_NAME);
            STENO.debug("Slicer Timer Report");
            STENO.debug("============");
            STENO.debug(SLICER_TIMER_NAME + " " + 0.001 * TIME_UTILS.timeTimeSoFar_ms(timerUUID, SLICER_TIMER_NAME) + " seconds");
            STENO.debug("============");
            TIME_UTILS.timerDelete(timerUUID, SLICER_TIMER_NAME);
        }
        catch (TimeUtils.TimerNotFoundException ex)
        {
            // This really should not happen!
            STENO.debug("Slicer Timer Report - timer not found!");
        }

        return new SliceResult(printJobUUID, printableMeshes, printerToUse, succeeded);
    }

    private boolean sliceFile(String printJobUUID,
            String printJobDirectory,
            SlicerType slicerType,
            List<String> createdMeshFiles,
            List<Integer> extrudersForMeshes,
            Vector3D centreOfPrintedObject,
            ProgressReceiver progressReceiver,
            int numberOfNozzles)
    {
        // Heads with a single nozzle are anomalous because
        // tool zero uses the "E" extruder, which is usually
        // extruder number 1. So for these kinds of head, the
        // extruder number needs to be reset to 0, hence the
        // need for the numberOfNozzles parameter.
        // This hack is closely related to the hack in
        // CuraDefaultSettingsEditor that also sets the extruder
        // number to zero for single nozzle heads.
 
 
        boolean succeeded = false;

        String tempGcodeFilename = printJobUUID + BaseConfiguration.gcodeTempFileExtension;

        String configFile = printJobUUID + BaseConfiguration.printProfileFileExtension;
        String jsonSettingsFile = "fdmprinter_robox.def.json";
        
        MachineType machineType = BaseConfiguration.getMachineType();
        ArrayList<String> commands = new ArrayList<>();

        String windowsSlicerCommand = "";
        String macSlicerCommand = "";
        String linuxSlicerCommand = "";
        String configLoadCommand = "";
        String configLoadFile = "";
        //The next variable is only required for Cura4
        String actionCommand = "";
        //The next variable is only required for Slic3r
        String printCenterCommand = "";
        String verboseOutputCommand = "";
        String progressOutputCommand = "";
        String modelFileCommand = "";
        String extruderTrainCommand = "";
        String settingCommand = "-s";
        String extruderSettingFormat = "extruder_nr=%d";

        switch (slicerType)
        {
            case Slic3r:
                windowsSlicerCommand = "\"" + BaseConfiguration.
                        getCommonApplicationDirectory() + "Slic3r\\slic3r.exe\"";
                macSlicerCommand = "Slic3r.app/Contents/MacOS/slic3r";
                linuxSlicerCommand = "Slic3r/bin/slic3r";
                configLoadCommand = "--load";
                configLoadFile = configFile;
                printCenterCommand = "--print-center";
                break;
            case Cura:
                windowsSlicerCommand = "\"" + BaseConfiguration.
                        getCommonApplicationDirectory() + "Cura\\CuraEngine.exe\"";
                macSlicerCommand = "Cura/CuraEngine";
                linuxSlicerCommand = "Cura/CuraEngine";
                verboseOutputCommand = "-v";
                configLoadCommand = "-c";
                configLoadFile = configFile;
                progressOutputCommand = "-p";
                break;
            case Cura4:
                windowsSlicerCommand = "\"" + BaseConfiguration.
                        getCommonApplicationDirectory() + "Cura4\\CuraEngine.exe\"";
                macSlicerCommand = "Cura4/CuraEngine";
                linuxSlicerCommand = "Cura4/CuraEngine";
                actionCommand = "slice";
                verboseOutputCommand = "-v";
                configLoadCommand = "-j";
                configLoadFile = jsonSettingsFile;
                progressOutputCommand = "-p";
                modelFileCommand = "-l";
                extruderTrainCommand = "-e";
                break;
        }

        STENO.debug("Selected slicer is " + slicerType + " : " + Thread.currentThread().getName());

        int previousExtruder;
        int extruderNo;
        switch (machineType)
        {
            case WINDOWS_95:
                commands.add("command.com");
                commands.add("/S");
                commands.add("/C");
                String win95PrintCommand = "\"pushd \""
                        + printJobDirectory
                        + "\" && "
                        + windowsSlicerCommand
                        + " ";
                if (!actionCommand.isEmpty())
                    win95PrintCommand += actionCommand + " ";
                win95PrintCommand += verboseOutputCommand
                        + " "
                        + progressOutputCommand
                        + " "
                        + configLoadCommand
                        + " \"" + configLoadFile + "\""
                        + " -o "
                        + "\"" + tempGcodeFilename + "\"";
                for (String fileName : createdMeshFiles)
                {
                    win95PrintCommand += " \"";
                    win95PrintCommand += fileName;
                    win95PrintCommand += "\"";
                }
                win95PrintCommand += " && popd\"";
                commands.add(win95PrintCommand);
                break;
            case WINDOWS:
                commands.add("cmd.exe");
                commands.add("/S");
                commands.add("/C");
                String windowsPrintCommand = "\"pushd \""
                        + printJobDirectory
                        + "\" && "
                        + windowsSlicerCommand
                        + " ";
                if (!actionCommand.isEmpty())
                    windowsPrintCommand += actionCommand + " ";
                windowsPrintCommand += verboseOutputCommand
                        + " "
                        + progressOutputCommand
                        + " "
                        + configLoadCommand
                        + " \"" + configLoadFile + "\"";
                 windowsPrintCommand += " -o "
                        + "\"" + tempGcodeFilename + "\"";

                if (!printCenterCommand.isEmpty())
                {
                    windowsPrintCommand += " " + printCenterCommand;
                    windowsPrintCommand += " "
                            + String.format(Locale.UK, "%.3f", centreOfPrintedObject.getX())
                            + ","
                            + String.format(Locale.UK, "%.3f", centreOfPrintedObject.getZ());
                }

                previousExtruder = -1;
                extruderNo = 0;
                for (int i = 0; i < createdMeshFiles.size(); i++)
                {
                    if (slicerType == SlicerType.Cura4 && previousExtruder != extrudersForMeshes.get(i)) 
                    {
                        if (numberOfNozzles > 1)
                        {
                            // Extruder needs swapping... just because
                            extruderNo = extrudersForMeshes.get(i) > 0 ? 0 : 1;
                        }
                        windowsPrintCommand += " " + extruderTrainCommand + extruderNo;
                    }
                    windowsPrintCommand += " " + modelFileCommand;
                    windowsPrintCommand += " \"";
                    windowsPrintCommand += createdMeshFiles.get(i);
                    windowsPrintCommand += "\"";
                    
                    if (slicerType == SlicerType.Cura4)
                    {
                        windowsPrintCommand += " " + settingCommand;
                        windowsPrintCommand += " " + String.format(extruderSettingFormat, extruderNo);
                    }
                    
                    previousExtruder = extrudersForMeshes.get(i);
                }
                windowsPrintCommand += " && popd\"";
                STENO.debug(windowsPrintCommand);
                commands.add(windowsPrintCommand);
                break;
            case MAC:
                commands.add(BaseConfiguration.getCommonApplicationDirectory()
                        + macSlicerCommand);
                if (!actionCommand.isEmpty())
                    commands.add(actionCommand);
                if (!verboseOutputCommand.isEmpty())
                {
                    commands.add(verboseOutputCommand);
                }
                if (!progressOutputCommand.isEmpty())
                {
                    commands.add(progressOutputCommand);
                }
                commands.add(configLoadCommand);
                commands.add(configLoadFile);
                commands.add("-o");
                commands.add(tempGcodeFilename);
                if (!printCenterCommand.isEmpty())
                {
                    commands.add(printCenterCommand);
                    commands.add(String.format(Locale.UK, "%.3f", centreOfPrintedObject.getX())
                            + ","
                            + String.format(Locale.UK, "%.3f", centreOfPrintedObject.getZ()));
                }

                previousExtruder = -1;
                extruderNo = 0;
                for (int i = 0; i < createdMeshFiles.size(); i++)
                {
                    if (slicerType == SlicerType.Cura4 && previousExtruder != extrudersForMeshes.get(i)) 
                    {
                        if (numberOfNozzles > 1)
                        {
                            // Extruder needs swapping... just because
                            extruderNo = extrudersForMeshes.get(i) > 0 ? 0 : 1;
                        }
                        commands.add(extruderTrainCommand + extruderNo);
                    }
                    if (!modelFileCommand.isEmpty())
                        commands.add(modelFileCommand);
                    commands.add(createdMeshFiles.get(i));
                    
                    if (slicerType == SlicerType.Cura4)
                    {
                        commands.add(settingCommand);
                        commands.add(String.format(extruderSettingFormat, extruderNo));
                    }
                    
                    previousExtruder = extrudersForMeshes.get(i);
                }

                break;
            case LINUX_X86:
            case LINUX_X64:
                commands.add(BaseConfiguration.getCommonApplicationDirectory()
                        + linuxSlicerCommand);
                if (!actionCommand.isEmpty())
                    commands.add(actionCommand);
                if (!verboseOutputCommand.isEmpty())
                {
                    commands.add(verboseOutputCommand);
                }
                if (!progressOutputCommand.isEmpty())
                {
                    commands.add(progressOutputCommand);
                }
                commands.add(configLoadCommand);
                commands.add(configLoadFile);
                commands.add("-o");
                commands.add(tempGcodeFilename);
                if (!printCenterCommand.isEmpty())
                {
                    commands.add(printCenterCommand);
                    commands.add(String.format(Locale.UK, "%.3f", centreOfPrintedObject.getX())
                            + ","
                            + String.format(Locale.UK, "%.3f", centreOfPrintedObject.getZ()));
                }
                previousExtruder = -1;
                extruderNo = 0;
                for (int i = 0; i < createdMeshFiles.size(); i++)
                {
                    if (slicerType == SlicerType.Cura4 && previousExtruder != extrudersForMeshes.get(i))
                    {
                        if (numberOfNozzles > 1)
                        {
                            // Extruder needs swapping... just because
                            extruderNo = extrudersForMeshes.get(i) > 0 ? 0 : 1;
                        }
                        commands.add(extruderTrainCommand + extruderNo);
                    }
                    if (!modelFileCommand.isEmpty())
                        commands.add(modelFileCommand);
                    commands.add(createdMeshFiles.get(i));
                    
                    if (slicerType == SlicerType.Cura4)
                    {
                        commands.add(settingCommand);
                        commands.add(String.format(extruderSettingFormat, extruderNo));
                    }
                    
                    previousExtruder = extrudersForMeshes.get(i);
                }
                break;
            default:
                STENO.error("Couldn't determine how to run slicer");
        }

        if (commands.size() > 0)
        {
            //steno.debug("Slicer command is " + String.join(" ", commands));
            ProcessBuilder slicerProcessBuilder = new ProcessBuilder(commands);
            if (machineType != MachineType.WINDOWS && machineType != MachineType.WINDOWS_95)
            {
                STENO.debug("Set working directory (Non-Windows) to " + printJobDirectory);
                slicerProcessBuilder.directory(new File(printJobDirectory));
            }
            STENO.info("Slicer command is " + String.join(" ", slicerProcessBuilder.command()));

            Process slicerProcess = null;
            
            if (isCancelled())
            {
                STENO.debug("Slice cancelled");
                return false;
            }
            
            try
            {
                slicerProcess = slicerProcessBuilder.start();
                // any error message?
                SlicerOutputGobbler errorGobbler = new SlicerOutputGobbler(progressReceiver, slicerProcess.
                        getErrorStream(), "ERROR",
                        slicerType);

                // any output?
                SlicerOutputGobbler outputGobbler = new SlicerOutputGobbler(progressReceiver, slicerProcess.
                        getInputStream(),
                        "OUTPUT", slicerType);

                // kick them off
                errorGobbler.start();
                outputGobbler.start();

                int exitStatus = slicerProcess.waitFor();
                
                if (isCancelled())
                {
                    STENO.debug("Slice cancelled");
                    return false;
                }
                
                switch (exitStatus)
                {
                    case 0:
                        STENO.debug("Slicer terminated successfully ");
                        succeeded = true;
                        break;
                    default:
                        STENO.error("Failure when invoking slicer with command line: " + String.join(
                                " ", slicerProcessBuilder.command()));
                        STENO.error("Slicer terminated with exit code " + exitStatus);
                        break;
                }
            } catch (IOException ex)
            {
                STENO.error("Exception whilst running slicer: " + ex);
            } catch (InterruptedException ex)
            {
                STENO.warning("Interrupted whilst waiting for slicer to complete");
                if (slicerProcess != null)
                {
                    slicerProcess.destroyForcibly();
                }
            }
        } else
        {
            STENO.error("Couldn't run slicer - no commands for OS ");
        }

        return succeeded;
    }

    @Override
    public void progressUpdateFromSlicer(String message, float workDone)
    {
        updateMessage(message);
        updateProgress(workDone, 100.0);
    }
}
