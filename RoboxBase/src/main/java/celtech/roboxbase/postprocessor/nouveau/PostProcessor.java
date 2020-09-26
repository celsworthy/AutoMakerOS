package celtech.roboxbase.postprocessor.nouveau;

import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.appManager.NotificationType;
import celtech.roboxbase.configuration.RoboxProfile;
import celtech.roboxbase.configuration.SlicerType;
import celtech.roboxbase.configuration.datafileaccessors.PrinterContainer;
import celtech.roboxbase.configuration.fileRepresentation.HeadFile;
import celtech.roboxbase.configuration.fileRepresentation.PrinterDefinitionFile;
import celtech.roboxbase.configuration.fileRepresentation.PrinterSettingsOverrides;
import static celtech.roboxbase.configuration.fileRepresentation.SupportType.*;
import celtech.roboxbase.configuration.hardwarevariants.PrinterType;
import celtech.roboxbase.postprocessor.GCodeOutputWriter;
import celtech.roboxbase.postprocessor.NozzleProxy;
import celtech.roboxbase.postprocessor.PrintJobStatistics;
import celtech.roboxbase.postprocessor.RoboxiserResult;
import celtech.roboxbase.postprocessor.nouveau.filamentSaver.FilamentSaver;
import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.LayerNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.MCodeNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.SectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.ToolSelectNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.FeedrateProvider;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Renderable;
import celtech.roboxbase.postprocessor.nouveau.spiralPrint.CuraSpiralPrintFixer;
import celtech.roboxbase.postprocessor.nouveau.timeCalc.TimeAndVolumeCalc;
import celtech.roboxbase.postprocessor.nouveau.timeCalc.TimeAndVolumeCalcResult;
import celtech.roboxbase.postprocessor.nouveau.verifier.OutputVerifier;
import celtech.roboxbase.postprocessor.nouveau.verifier.VerifierResult;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.Head.HeadType;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.services.camera.CameraTriggerData;
import celtech.roboxbase.utils.SystemUtils;
import celtech.roboxbase.utils.TimeUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Task;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.BasicParseRunner;
import org.parboiled.support.ParsingResult;

/**
 *
 * @author Ian
 */
public class PostProcessor
{

    private final Stenographer steno = StenographerFactory.getStenographer(PostProcessor.class.getName());

    private final String movePerimeterTimerName = "ReorderPerimeter";
    private final String moveSupportTimerName = "ReorderSupport";
    private final String unretractTimerName = "Unretract";
    private final String orphanTimerName = "Orphans";
    private final String nozzleControlTimerName = "NozzleControl";
    private final String perRetractTimerName = "PerRetract";
    private final String closeTimerName = "Close";
    private final String unnecessaryToolchangeTimerName = "UnnecessaryToolchange";
    private final String cameraEventTimerName = "CameraEvent";
    private final String openTimerName = "Open";
    private final String assignExtrusionTimerName = "AssignExtrusion";
    private final String layerResultTimerName = "LayerResult";
    private final String timeAndVolumeCalcTimerName = "TimeAndVolumeCalc";
    private final String heaterSaverTimerName = "HeaterSaver";
    private final String parseLayerTimerName = "ParseLayer";
    private final String writeOutputTimerName = "WriteOutput";
    private final String countLinesTimerName = "CountLines";
    private final String outputVerifierTimerName = "OutputVerifier";

    private final String nameOfPrint;
    private final String printJobUUID;
    private final List<Boolean> usedExtruders;
    private final Printer printer;
    private final String gcodeFileToProcess;
    private final String gcodeOutputFile;
    private final HeadFile headFile;
    private final RoboxProfile settingsProfile;
    private final DoubleProperty taskProgress;
    private final boolean safetyFeaturesRequired;
    private final PrinterSettingsOverrides printerOverrides;
    
    private final SlicerType slicerType;

    private final List<NozzleProxy> nozzleProxies = new ArrayList<>();

    private final PostProcessorFeatureSet featureSet;

    private PostProcessingMode postProcessingMode = PostProcessingMode.TASK_BASED_NOZZLE_SELECTION;

    protected List<Integer> layerNumberToLineNumber;
    protected List<Double> layerNumberToPredictedDuration;
    protected double predictedDuration = 0;

    private final UtilityMethods postProcessorUtilityMethods;
    private final NodeManagementUtilities nodeManagementUtilities;
    private final NozzleAssignmentUtilities nozzleControlUtilities;
    private final CloseLogic closeLogic;
    private final FilamentSaver heaterSaver;
    private final OutputVerifier outputVerifier;

    private final TimeUtils timeUtils = new TimeUtils();

    public PostProcessor(String printJobUUID,
            String nameOfPrint,
            List<Boolean> usedExtruders,
            Printer printer,
            String gcodeFileToProcess,
            String gcodeOutputFile,
            HeadFile headFile,
            RoboxProfile settings,
            PrinterSettingsOverrides printerOverrides,
            PostProcessorFeatureSet postProcessorFeatureSet,
            String headType,
            DoubleProperty taskProgress,
            Map<Integer, Integer> objectToNozzleNumberMap,
            CameraTriggerData cameraTriggerData,
            boolean safetyFeaturesRequired,
            SlicerType slicerType)
    {
        this.printJobUUID = printJobUUID;
        this.nameOfPrint = nameOfPrint;
        this.usedExtruders = usedExtruders;
        this.printer = printer;
        this.gcodeFileToProcess = gcodeFileToProcess;
        this.gcodeOutputFile = gcodeOutputFile;
        this.headFile = headFile;
        this.featureSet = postProcessorFeatureSet;
        this.settingsProfile = settings;
        this.taskProgress = taskProgress;
        this.printerOverrides = printerOverrides;
        this.safetyFeaturesRequired = safetyFeaturesRequired;
        this.slicerType = slicerType;

        nozzleProxies.clear();

        for (int nozzleIndex = 0;
                nozzleIndex < settingsProfile.getNozzleParameters()
                        .size(); nozzleIndex++)
        {
            NozzleProxy proxy = new NozzleProxy(settingsProfile.getNozzleParameters().get(nozzleIndex));
            proxy.setNozzleReferenceNumber(nozzleIndex);
            nozzleProxies.add(proxy);
        }

        if (headFile.getType() == HeadType.DUAL_MATERIAL_HEAD)
        {
            // If we have a dual extruder head but a single extruder machine force use of the available extruder
            if (!printer.extrudersProperty().get(0).isFittedProperty().get() && !printer.extrudersProperty().get(1).isFittedProperty().get())
            {
                postProcessingMode = PostProcessingMode.NO_AVAILABLE_EXTRUDERS;
                steno.error("Attempt to postprocess with a DM head and no extruders fitted / available.");
            } else if (!printer.extrudersProperty().get(0).isFittedProperty().get())
            {
                postProcessingMode = PostProcessingMode.FORCED_USE_OF_D_EXTRUDER;
                steno.warning("Attempt to postprocess with a DM head and only the D extruder.");
            } else if (!printer.extrudersProperty().get(1).isFittedProperty().get())
            {
                postProcessingMode = PostProcessingMode.FORCED_USE_OF_E_EXTRUDER;
                steno.warning("Attempt to postprocess with a DM head and only the E extruder.");
            } else if (slicerType != SlicerType.Cura4)
            {
                switch (printerOverrides.getPrintSupportTypeOverride())
                {
                    case MATERIAL_1:
                        postProcessingMode = PostProcessingMode.SUPPORT_IN_FIRST_MATERIAL;
                        break;
                    case MATERIAL_2:
                        postProcessingMode = PostProcessingMode.SUPPORT_IN_SECOND_MATERIAL;
                        break;
                }
            } else
            {
                postProcessingMode = PostProcessingMode.LEAVE_TOOL_CHANGES_ALONE_DUAL;
            }
        } else if (slicerType == SlicerType.Cura4) 
        {
            if (!settingsProfile.getSpecificBooleanSettingWithDefault("infill_before_walls", false))
                featureSet.enableFeature(PostProcessorFeature.MOVE_PERIMETERS_TO_FRONT);
            if (settingsProfile.getSpecificBooleanSettingWithDefault("support_after_model", true))
                featureSet.enableFeature(PostProcessorFeature.MOVE_SUPPORT_AFTER_MODEL);
            
            postProcessingMode = PostProcessingMode.LEAVE_TOOL_CHANGES_ALONE_SINGLE;
        } else
        {
            postProcessingMode = PostProcessingMode.TASK_BASED_NOZZLE_SELECTION;
        }

        if (headFile.getValves() == Head.ValveType.NOT_FITTED)
        {
            featureSet.disableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
            featureSet.disableFeature(PostProcessorFeature.OPEN_AND_CLOSE_NOZZLES);
        }

        nodeManagementUtilities = new NodeManagementUtilities(featureSet, nozzleProxies);
        postProcessorUtilityMethods = new UtilityMethods(featureSet, settingsProfile, headType, nodeManagementUtilities, cameraTriggerData);
        nozzleControlUtilities = new NozzleAssignmentUtilities(nozzleProxies, settingsProfile, headFile, featureSet, postProcessingMode, objectToNozzleNumberMap);
        closeLogic = new CloseLogic(settingsProfile, featureSet, headType, nodeManagementUtilities);
        heaterSaver = new FilamentSaver(100, 120);
        outputVerifier = new OutputVerifier(featureSet);
    }

    public RoboxiserResult processInput(Task postProcessorTask)
    {
        RoboxiserResult result = new RoboxiserResult();
        result.setSuccess(false);

        //Do not pass go - do not collect 200 pounds - we shouldn't be here...
        if (postProcessingMode != PostProcessingMode.NO_AVAILABLE_EXTRUDERS)
        {
            BufferedReader fileReader = null;
            GCodeOutputWriter writer = null;

            layerNumberToLineNumber = new ArrayList<>();

            int layerCounter = -1;

            OutputUtilities outputUtilities = new OutputUtilities();

            timeUtils.timerStart(this, "PostProcessor");
            steno.debug("Beginning post-processing operation");

            //Cura has line delineators like this ';LAYER:1'
            try
            {
                File inputFile = new File(gcodeFileToProcess);
                timeUtils.timerStart(this, countLinesTimerName);
                int linesInGCodeFile = SystemUtils.countLinesInFile(inputFile);
                timeUtils.timerStop(this, countLinesTimerName);

                int linesRead = 0;
                double lastPercentSoFar = 0;

                fileReader = new BufferedReader(new FileReader(inputFile));

                writer = BaseLookup.getPostProcessorOutputWriterFactory().create(gcodeOutputFile);

                boolean nozzle0HeatRequired = false;
                boolean nozzle1HeatRequired = false;

                boolean eRequired = false;
                boolean dRequired = false;

                int defaultObjectNumber = 0;

                StringBuilder layerBuffer = new StringBuilder();

                OpenResult lastOpenResult = null;

                List<LayerPostProcessResult> postProcessResults = new ArrayList<>();
                LayerPostProcessResult lastPostProcessResult = new LayerPostProcessResult(null, defaultObjectNumber, null, null, null, -1, 0);
                
                for (String lineRead = fileReader.readLine(); lineRead != null; lineRead = fileReader.readLine())
                {
                    if(postProcessorTask.isCancelled())
                    {
                        steno.debug("Post Processor cancelled, exciting process");
                        fileReader.close();
                        writer.close();
                        return result;
                    }
                    
                    linesRead++;
                    double percentSoFar = ((double) linesRead / (double) linesInGCodeFile) * 100;
                    if (percentSoFar - lastPercentSoFar >= 1)
                    {
                        if (taskProgress != null)
                        {
                            taskProgress.set(percentSoFar);
                        }
                        lastPercentSoFar = percentSoFar;
                    }
                    
                    lineRead = lineRead.trim();
                    
                    if (lineRead.matches("T[0-1]") && layerCounter < 0)
                    {
                        int initialToolChange = Integer.parseInt(lineRead.substring(1));
                        lastPostProcessResult.setLastObjectNumber(initialToolChange);
                    }    
                    
                    if (lineRead.matches(";LAYER:[-]*[0-9]+"))
                    {
                        if (layerCounter >= 0)
                        {
                            //Parse the layer!
                            LayerPostProcessResult parseResult = parseLayer(layerBuffer, lastPostProcessResult);
                            postProcessResults.add(parseResult);
                            lastPostProcessResult = parseResult;
                        }

                        layerCounter++;
                        layerBuffer = new StringBuilder();
                        // Make sure this layer command is at the start
                        layerBuffer.append(lineRead);
                        layerBuffer.append('\n');
                    } else if (!lineRead.equals(""))
                    {
                        //Ignore blank lines
                        // stash it in the buffer
                        layerBuffer.append(lineRead);
                        layerBuffer.append('\n');
                    }
                }

                //This catches the last layer - if we had no data it won't do anything
                LayerPostProcessResult lastLayerParseResult = parseLayer(layerBuffer, lastPostProcessResult);
                postProcessResults.add(lastLayerParseResult);

                if (printerOverrides.getSpiralPrintOverride())
                {
                    //Run the Cura spiral print deshagger
                    CuraSpiralPrintFixer curaSpiralPrintFixer = new CuraSpiralPrintFixer();
                    curaSpiralPrintFixer.fixSpiralPrint(postProcessResults);
                }

                for (LayerPostProcessResult resultToBeProcessed : postProcessResults)
                {
                    if(postProcessorTask.isCancelled())
                    {
                        steno.debug("Post Processor cancelled, exciting process");
                        fileReader.close();
                        writer.close();
                        return result;
                    }
                    
                    timeUtils.timerStart(this, assignExtrusionTimerName);
                    NozzleAssignmentUtilities.ExtrusionAssignmentResult assignmentResult = nozzleControlUtilities.assignExtrusionToCorrectExtruder(resultToBeProcessed.getLayerData());
                    timeUtils.timerStop(this, assignExtrusionTimerName);

                    //Add the opens first - we leave it until now as the layer we have just processed may have affected the one before
                    //NOTE
                    //Since we're using the open/close state here we need to make sure this is the last open/close thing we do...
                    //NOTE
                    if (featureSet.isEnabled(PostProcessorFeature.OPEN_AND_CLOSE_NOZZLES))
                    {
                        timeUtils.timerStart(this, openTimerName);
                        lastOpenResult = postProcessorUtilityMethods.insertOpens(resultToBeProcessed.getLayerData(), lastOpenResult, nozzleProxies, headFile.getTypeCode());
                        timeUtils.timerStop(this, openTimerName);
                    }
                }

                TimeAndVolumeCalc timeAndVolumeCalc = new TimeAndVolumeCalc(headFile.getType());

                timeUtils.timerStart(this, timeAndVolumeCalcTimerName);
                TimeAndVolumeCalcResult timeAndVolumeCalcResult = timeAndVolumeCalc.calculateVolumeAndTime(postProcessResults);
                timeUtils.timerStop(this, timeAndVolumeCalcTimerName);

                if (headFile.getType() == Head.HeadType.DUAL_MATERIAL_HEAD)
                {
                    eRequired = nozzle1HeatRequired = timeAndVolumeCalcResult.getExtruderEStats().getVolume() > 0;
                    dRequired = nozzle0HeatRequired = timeAndVolumeCalcResult.getExtruderDStats().getVolume() > 0;
                } else
                {
                    nozzle0HeatRequired = false;
                    nozzle1HeatRequired = false;
                    eRequired = true;
                }

                Optional<PrinterType> printerTypeCode;
                if(printer == null) {
                    PrinterDefinitionFile printerDef = PrinterContainer.getPrinterByID(PrinterContainer.defaultPrinterID);
                    printerTypeCode = Optional.of(PrinterType.getPrinterTypeForTypeCode(printerDef.getTypeCode()));
                } else {
                    printerTypeCode = Optional.of(printer.findPrinterType());
                }
                    
                outputUtilities.prependPrePrintHeader(writer,
                        printerTypeCode,
                        headFile.getTypeCode(),
                        settingsProfile,
                        nozzle0HeatRequired,
                        nozzle1HeatRequired,
                        safetyFeaturesRequired);

                timeUtils.timerStart(this, heaterSaverTimerName);
                if (headFile.getType() == HeadType.DUAL_MATERIAL_HEAD
                        && postProcessingMode != PostProcessingMode.FORCED_USE_OF_D_EXTRUDER
                        && postProcessingMode != PostProcessingMode.FORCED_USE_OF_E_EXTRUDER)
                {
                    heaterSaver.saveHeaters(postProcessResults, nozzle0HeatRequired, nozzle1HeatRequired);
                }
                timeUtils.timerStop(this, heaterSaverTimerName);

                for (LayerPostProcessResult resultToBeProcessed : postProcessResults)
                {
                    if(postProcessorTask.isCancelled())
                    {
                        steno.debug("Post Processor cancelled, exciting process");
                        fileReader.close();
                        writer.close();
                        return result;
                    }
                    
                    timeUtils.timerStart(this, writeOutputTimerName);
                    if (resultToBeProcessed.getLayerData().getLayerNumber() == 1)
                    {
                        if (headFile.getType() == HeadType.SINGLE_MATERIAL_HEAD
                                || (headFile.getType() == HeadType.DUAL_MATERIAL_HEAD
                                && (postProcessingMode == PostProcessingMode.FORCED_USE_OF_D_EXTRUDER
                                || postProcessingMode == PostProcessingMode.FORCED_USE_OF_E_EXTRUDER)))
                        {
                            outputUtilities.outputSingleMaterialNozzleTemperatureCommands(writer, nozzle0HeatRequired, nozzle1HeatRequired, eRequired, dRequired);
                        }

                        //Always output the bed temperature command at layer 1
                        MCodeNode bedTemp = new MCodeNode(140);
                        bedTemp.setCommentText("Go to bed temperature from loaded reel - don't wait");
                        writer.writeOutput(bedTemp.renderForOutput());
                        writer.newLine();
                    }
                    outputUtilities.writeLayerToFile(resultToBeProcessed.getLayerData(), writer);
                    timeUtils.timerStop(this, writeOutputTimerName);
                    postProcessorUtilityMethods.updateLayerToLineNumber(resultToBeProcessed, layerNumberToLineNumber, writer);
                }

                timeUtils.timerStart(this, writeOutputTimerName);
                outputUtilities.appendPostPrintFooter(writer,
                        timeAndVolumeCalcResult,
                        printerTypeCode,
                        headFile.getTypeCode(),
                        nozzle0HeatRequired,
                        nozzle1HeatRequired,
                        safetyFeaturesRequired);
                timeUtils.timerStop(this, writeOutputTimerName);

                /**
                 * TODO: layerNumberToLineNumber uses lines numbers from the
                 * GCode file so are a little less than the line numbers for
                 * each layer after roboxisation. As a quick fix for now set the
                 * line number of the last layer to the actual maximum line
                 * number.
                 */
                layerNumberToLineNumber.set(layerNumberToLineNumber.size() - 1,
                        writer.getNumberOfLinesOutput());
                int numLines = writer.getNumberOfLinesOutput();

                String statsProfileName = "";
                float statsLayerHeight = 0;

                if (settingsProfile != null)
                {
                    statsProfileName = settingsProfile.getName();
                    statsLayerHeight = settingsProfile.getSpecificFloatSetting("layerHeight_mm");
                }

                PrintJobStatistics roboxisedStatistics = new PrintJobStatistics(
                        headFile.getTypeCode(),
                        headFile.getType().name(),
                        eRequired,
                        dRequired,
                        printJobUUID,
                        nameOfPrint,
                        statsProfileName,
                        statsLayerHeight,
                        numLines,
                        timeAndVolumeCalcResult.getExtruderEStats().getVolume(),
                        timeAndVolumeCalcResult.getExtruderDStats().getVolume(),
                        0,
                        layerNumberToLineNumber,
                        timeAndVolumeCalcResult.getExtruderEStats().getDuration().getLayerNumberToPredictedDuration(),
                        timeAndVolumeCalcResult.getExtruderDStats().getDuration().getLayerNumberToPredictedDuration(),
                        timeAndVolumeCalcResult.getFeedrateIndependentDuration().getLayerNumberToPredictedDuration(),
                        timeAndVolumeCalcResult.getExtruderEStats().getDuration().getTotal_duration()
                        + timeAndVolumeCalcResult.getExtruderDStats().getDuration().getTotal_duration()
                        + timeAndVolumeCalcResult.getFeedrateIndependentDuration().getTotal_duration()
                );

                result.setRoboxisedStatistics(roboxisedStatistics);

                timeUtils.timerStart(this, outputVerifierTimerName);
                List<VerifierResult> verificationResults = outputVerifier.verifyAllLayers(postProcessResults, headFile.getType());
                timeUtils.timerStop(this, outputVerifierTimerName);

                if (verificationResults.size() > 0)
                {
                    steno.error("Fatal errors found in post-processed file");
                    for (VerifierResult verifierResult : verificationResults)
                    {
                        if (verifierResult.getNodeInError() instanceof Renderable)
                        {
                            steno.error(verifierResult.getResultType().getDescription()
                                    + " at Layer:" + verifierResult.getLayerNumber()
                                    + " Tool:" + verifierResult.getToolnumber()
                                    + " Node:" + ((Renderable) verifierResult.getNodeInError()).renderForOutput());
                        } else
                        {
                            steno.error(verifierResult.getResultType().getDescription()
                                    + " at Layer:" + verifierResult.getLayerNumber()
                                    + " Tool:" + verifierResult.getToolnumber()
                                    + " Node:" + verifierResult.getNodeInError().toString());
                        }
                    }
                    steno.error("======================================");
                }

                outputPostProcessingTimerReport();

                timeUtils.timerStop(this, "PostProcessor");
                steno.debug("Post-processing took " + timeUtils.timeTimeSoFar_ms(this, "PostProcessor") + "ms");

                if (verificationResults.size() > 0)
                {
                    result.setSuccess(false);
                } else
                {
                    result.setSuccess(true);
                }
            } catch (IOException ex)
            {
                steno.error("Error reading post-processor input file: " + gcodeFileToProcess);
            } catch (RuntimeException ex)
            {
                if (ex.getCause() instanceof ParserInputException)
                {
                    steno.error("Fatal postprocessing error on layer - out of bounds - " + layerCounter + " got exception: " + ex.getCause().getMessage());
                    BaseLookup.getSystemNotificationHandler().showDismissableNotification(
                            BaseLookup.i18n("notification.postProcessorFailure.modelOutOfBounds"),
                            BaseLookup.i18n("notification.postProcessorFailure.dismiss"),
                            NotificationType.CAUTION);
                } else if (ex.getCause() != null)
                {
                    steno.error("Fatal postprocessing error on layer " + layerCounter + " got exception: " + ex.getCause().getMessage());
                    BaseLookup.getSystemNotificationHandler().showDismissableNotification(
                            BaseLookup.i18n("notification.postProcessorFailure.unknown"),
                            BaseLookup.i18n("notification.postProcessorFailure.dismiss"),
                            NotificationType.CAUTION);
                } else
                {
                    steno.error("Fatal postprocessing error on layer " + layerCounter);
                    BaseLookup.getSystemNotificationHandler().showDismissableNotification(
                            BaseLookup.i18n("notification.postProcessorFailure.unknown"),
                            BaseLookup.i18n("notification.postProcessorFailure.dismiss"),
                            NotificationType.CAUTION);
                }
                ex.printStackTrace();
            } finally
            {
                if (fileReader != null)
                {
                    try
                    {
                        fileReader.close();
                    } catch (IOException ex)
                    {
                        steno.error("Failed to close post processor input file - " + gcodeFileToProcess);
                    }
                }

                if (writer != null)
                {
                    try
                    {
                        writer.close();
                    } catch (IOException ex)
                    {
                        steno.error("Failed to close post processor output file - " + gcodeOutputFile);
                    }
                }
            }
            steno.debug("About to exit post processor with result " + result.isSuccess());
        }

        return result;
    }

    private LayerPostProcessResult parseLayer(StringBuilder layerBuffer,
            LayerPostProcessResult lastLayerParseResult)
    {
        LayerPostProcessResult parseResultAtEndOfThisLayer = null;

        // Parse the last layer if it exists...
        if (layerBuffer.length() > 0)
        {
            GCodeParser gcodeParser;
            
            if(slicerType == SlicerType.Cura4) {
                gcodeParser = Parboiled.createParser(Cura4GCodeParser.class);
            } else {
                gcodeParser = Parboiled.createParser(CuraGCodeParser.class);
            }

            if (printer == null)
            {
                PrinterDefinitionFile printerDef = PrinterContainer.getPrinterByID(PrinterContainer.defaultPrinterID);
                gcodeParser.setPrintVolumeBounds(
                        printerDef.getPrintVolumeWidth(),
                        printerDef.getPrintVolumeDepth(),
                        printerDef.getPrintVolumeHeight());
            } else
            {
                gcodeParser.setPrintVolumeBounds(printer.printerConfigurationProperty().get().getPrintVolumeWidth(),
                        printer.printerConfigurationProperty().get().getPrintVolumeDepth(),
                        printer.printerConfigurationProperty().get().getPrintVolumeHeight());
            }

            if (lastLayerParseResult != null)
            {
                gcodeParser.setStartingLineNumber(lastLayerParseResult.getLastLineNumber());
                gcodeParser.setFeedrateInForce(lastLayerParseResult.getLastFeedrateInForce());
                gcodeParser.setCurrentObject(lastLayerParseResult.getLastObjectNumber().orElse(-1));
                gcodeParser.setCurrentSection(lastLayerParseResult.getLastSection());
            }

            BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.Layer());

            timeUtils.timerStart(this, parseLayerTimerName);
            ParsingResult result = runner.run(layerBuffer.toString());

            timeUtils.timerStop(this, parseLayerTimerName);

            if (result.hasErrors()
                    || !result.matched)
            {
                throw new RuntimeException("Parsing failure");
            } else
            {
                LayerNode layerNode = gcodeParser.getLayerNode();
                double lastFeedrate = gcodeParser.getFeedrateInForce();
                int lastLineNumber = gcodeParser.getCurrentLineNumber();
                int lastObjectNumber = gcodeParser.getCurrentObject();
                String lastSection = gcodeParser.getCurrentSection();
                parseResultAtEndOfThisLayer = postProcess(layerNode, lastLayerParseResult);
                parseResultAtEndOfThisLayer.setLastFeedrateInForce(lastFeedrate);
                parseResultAtEndOfThisLayer.setLastLineNumber(lastLineNumber);
                parseResultAtEndOfThisLayer.setLastObjectNumber(lastObjectNumber);
                parseResultAtEndOfThisLayer.setLastSection(lastSection);
            }
        } else
        {
            parseResultAtEndOfThisLayer = lastLayerParseResult;
        }

        return parseResultAtEndOfThisLayer;
    }

    private LayerPostProcessResult postProcess(LayerNode layerNode,
            LayerPostProcessResult lastLayerParseResult)
    {
        if(lastLayerParseResult.getLayerData() == null) {
            nodeManagementUtilities.removeFirstUnretractWithNoRetract(layerNode);
        }
        
        timeUtils.timerStart(this, unretractTimerName);
        nodeManagementUtilities.rehabilitateUnretractNodes(layerNode);
        timeUtils.timerStop(this, unretractTimerName);

        timeUtils.timerStart(this, orphanTimerName);
        nodeManagementUtilities.rehomeOrphanObjects(layerNode, lastLayerParseResult);
        //nodeManagementUtilities.tidySections(layerNode, lastLayerParseResult);
        timeUtils.timerStop(this, orphanTimerName);

        if (featureSet.isEnabled(PostProcessorFeature.MOVE_PERIMETERS_TO_FRONT))
        {
            timeUtils.timerStart(this, movePerimeterTimerName);
            nodeManagementUtilities.movePerimeterSections(layerNode, lastLayerParseResult);
            timeUtils.timerStop(this, movePerimeterTimerName);
        }

        if (featureSet.isEnabled(PostProcessorFeature.MOVE_SUPPORT_AFTER_MODEL))
        {
            timeUtils.timerStart(this, moveSupportTimerName);
            nodeManagementUtilities.moveSupportSections(layerNode, lastLayerParseResult);
            timeUtils.timerStop(this, moveSupportTimerName);
        }
        
        int lastObjectNumber = -1;

        timeUtils.timerStart(this, nozzleControlTimerName);
        lastObjectNumber = nozzleControlUtilities.insertNozzleControlSectionsByObject(layerNode, lastLayerParseResult);
        timeUtils.timerStop(this, nozzleControlTimerName);

        nodeManagementUtilities.recalculateSectionExtrusion(layerNode);

        timeUtils.timerStart(this, perRetractTimerName);
        nodeManagementUtilities.calculatePerRetractExtrusionAndNode(layerNode);
        timeUtils.timerStop(this, perRetractTimerName);

        timeUtils.timerStart(this, closeTimerName);
        closeLogic.insertCloseNodes(layerNode, lastLayerParseResult, nozzleProxies);
        timeUtils.timerStop(this, closeTimerName);

        timeUtils.timerStart(this, unnecessaryToolchangeTimerName);
        postProcessorUtilityMethods.suppressUnnecessaryToolChangesAndInsertToolchangeCloses(layerNode, lastLayerParseResult, nozzleProxies);
        timeUtils.timerStop(this, unnecessaryToolchangeTimerName);
        
        if (featureSet.isEnabled(PostProcessorFeature.INSERT_CAMERA_CONTROL_POINTS))
        {
            timeUtils.timerStart(this, cameraEventTimerName);
            postProcessorUtilityMethods.insertCameraTriggersAndCloses(layerNode, lastLayerParseResult, nozzleProxies);
            timeUtils.timerStop(this, cameraEventTimerName);
        }

        timeUtils.timerStart(this, layerResultTimerName);
        LayerPostProcessResult postProcessResult = determineLayerPostProcessResult(layerNode, lastLayerParseResult);
        postProcessResult.setLastObjectNumber(lastObjectNumber);
        timeUtils.timerStop(this, layerResultTimerName);

        return postProcessResult;
    }

    private LayerPostProcessResult determineLayerPostProcessResult(LayerNode layerNode, LayerPostProcessResult lastLayerPostProcessResult)
    {
        Iterator<GCodeEventNode> layerIterator = layerNode.treeSpanningIterator(null);

        double lastFeedrate = -1;

        SectionNode lastSectionNode = null;
        ToolSelectNode lastToolSelectNode = null;
        ToolSelectNode firstToolSelectNodeWithSameNumber = null;

        while (layerIterator.hasNext())
        {
            GCodeEventNode foundNode = layerIterator.next();

            if (foundNode instanceof FeedrateProvider)
            {
                if (((FeedrateProvider) foundNode).getFeedrate().getFeedRate_mmPerMin() < 0)
                {
                    if (lastFeedrate < 0)
                    {
                        ((FeedrateProvider) foundNode).getFeedrate().setFeedRate_mmPerMin(lastLayerPostProcessResult.getLastFeedrateInForce());
                    } else
                    {
                        ((FeedrateProvider) foundNode).getFeedrate().setFeedRate_mmPerMin(lastFeedrate);
                    }
                }
                lastFeedrate = ((FeedrateProvider) foundNode).getFeedrate().getFeedRate_mmPerMin();
            }

            if (foundNode instanceof ToolSelectNode)
            {
                ToolSelectNode newToolSelectNode = (ToolSelectNode) foundNode;

                if (lastToolSelectNode != null)
                {
                    if (newToolSelectNode.getToolNumber() != lastToolSelectNode.getToolNumber())
                    {
                        firstToolSelectNodeWithSameNumber = newToolSelectNode;
                    }
                } else
                {
                    firstToolSelectNodeWithSameNumber = newToolSelectNode;
                }

                lastToolSelectNode = newToolSelectNode;
            } else if (foundNode instanceof SectionNode)
            {
                lastSectionNode = (SectionNode) foundNode;
            }
        }

        if (lastSectionNode == null)
        {
            lastSectionNode = lastLayerPostProcessResult.getLastSectionNodeInForce();
        }

        if (lastToolSelectNode == null)
        {
            lastToolSelectNode = lastLayerPostProcessResult.getLastToolSelectInForce();
        }

        return new LayerPostProcessResult(layerNode, -1,
                lastSectionNode, lastToolSelectNode, firstToolSelectNodeWithSameNumber, lastFeedrate, 0);
    }

    private void outputPostProcessingTimerReport()
    {
        steno.debug("Post Processor Timer Report");
        steno.debug("============");
        if (featureSet.isEnabled(PostProcessorFeature.MOVE_PERIMETERS_TO_FRONT))
            steno.debug(movePerimeterTimerName + " " + timeUtils.timeTimeSoFar_ms(this, movePerimeterTimerName));
        if (featureSet.isEnabled(PostProcessorFeature.MOVE_SUPPORT_AFTER_MODEL))
            steno.debug(moveSupportTimerName + " " + timeUtils.timeTimeSoFar_ms(this, moveSupportTimerName));
        steno.debug(unretractTimerName + " " + timeUtils.timeTimeSoFar_ms(this, unretractTimerName));
        steno.debug(orphanTimerName + " " + timeUtils.timeTimeSoFar_ms(this, orphanTimerName));
        steno.debug(nozzleControlTimerName + " " + timeUtils.timeTimeSoFar_ms(this, nozzleControlTimerName));
        steno.debug(perRetractTimerName + " " + timeUtils.timeTimeSoFar_ms(this, perRetractTimerName));
        steno.debug(unnecessaryToolchangeTimerName + " " + timeUtils.timeTimeSoFar_ms(this, unnecessaryToolchangeTimerName));
        if (featureSet.isEnabled(PostProcessorFeature.INSERT_CAMERA_CONTROL_POINTS))
        {
            steno.debug(cameraEventTimerName + " " + timeUtils.timeTimeSoFar_ms(this, cameraEventTimerName));
        }
        if (featureSet.isEnabled(PostProcessorFeature.OPEN_AND_CLOSE_NOZZLES))
        {
            steno.debug(closeTimerName + " " + timeUtils.timeTimeSoFar_ms(this, closeTimerName));
            steno.debug(openTimerName + " " + timeUtils.timeTimeSoFar_ms(this, openTimerName));
        }
        steno.debug(assignExtrusionTimerName + " " + timeUtils.timeTimeSoFar_ms(this, assignExtrusionTimerName));
        steno.debug(layerResultTimerName + " " + timeUtils.timeTimeSoFar_ms(this, layerResultTimerName));
        steno.debug(parseLayerTimerName + " " + timeUtils.timeTimeSoFar_ms(this, parseLayerTimerName));
        steno.debug(timeAndVolumeCalcTimerName + " " + timeUtils.timeTimeSoFar_ms(this, timeAndVolumeCalcTimerName));
        steno.debug(heaterSaverTimerName + " " + timeUtils.timeTimeSoFar_ms(this, heaterSaverTimerName));
        steno.debug(outputVerifierTimerName + " " + timeUtils.timeTimeSoFar_ms(this, outputVerifierTimerName));
        steno.debug(writeOutputTimerName + " " + timeUtils.timeTimeSoFar_ms(this, writeOutputTimerName));
        steno.debug("============");
    }
}
