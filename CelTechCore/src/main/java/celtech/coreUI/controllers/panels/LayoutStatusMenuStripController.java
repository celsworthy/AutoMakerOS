package celtech.coreUI.controllers.panels;

import celtech.Lookup;
import celtech.appManager.ApplicationMode;
import celtech.appManager.ApplicationStatus;
import celtech.appManager.ModelContainerProject;
import celtech.appManager.Project;
import celtech.appManager.Project.ProjectChangesListener;
import celtech.appManager.ProjectMode;
import celtech.appManager.TimelapseSettingsData;
import celtech.appManager.undo.CommandStack;
import celtech.appManager.undo.UndoableProject;
import celtech.configuration.ApplicationConfiguration;
import celtech.configuration.DirectoryMemoryProperty;
import celtech.coreUI.AmbientLEDState;
import celtech.coreUI.DisplayManager;
import celtech.coreUI.LayoutSubmode;
import celtech.coreUI.ProjectGUIRules;
import celtech.coreUI.ProjectGUIState;
import celtech.coreUI.components.Notifications.ConditionalNotificationBar;
import celtech.coreUI.components.ReprintPanel;
import celtech.coreUI.components.buttons.GraphicButtonWithLabel;
import celtech.coreUI.components.buttons.GraphicToggleButtonWithLabel;
import celtech.coreUI.visualisation.ModelLoader;
import celtech.coreUI.visualisation.ProjectSelection;
import celtech.modelcontrol.Groupable;
import celtech.modelcontrol.ModelContainer;
import celtech.modelcontrol.ModelGroup;
import celtech.modelcontrol.ProjectifiableThing;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.PrinterColourMap;
import celtech.roboxbase.appManager.NotificationType;
import celtech.roboxbase.appManager.PurgeResponse;
import celtech.roboxbase.camera.CameraInfo;
import celtech.roboxbase.comms.DetectedServer;
import celtech.roboxbase.comms.RemoteDetectedPrinter;
import celtech.roboxbase.comms.RoboxCommsManager;
import celtech.roboxbase.comms.remote.RoboxRemoteCommandInterface;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.RoboxProfile;
import celtech.roboxbase.configuration.SlicerType;
import celtech.roboxbase.configuration.datafileaccessors.FilamentContainer;
import celtech.roboxbase.configuration.fileRepresentation.CameraProfile;
import celtech.roboxbase.configuration.fileRepresentation.CameraSettings;
import celtech.roboxbase.configuration.fileRepresentation.PrinterSettingsOverrides;
import celtech.roboxbase.configuration.utils.RoboxProfileUtils;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterConnection;
import celtech.roboxbase.printerControl.model.PrinterException;
import celtech.roboxbase.printerControl.model.PrinterListChangesListener;
import celtech.roboxbase.printerControl.model.Reel;
import celtech.roboxbase.services.camera.CameraTriggerData;
import celtech.roboxbase.services.gcodegenerator.GCodeGeneratorResult;
import celtech.roboxbase.utils.PrintJobUtils;
import celtech.roboxbase.utils.PrinterUtils;
import celtech.roboxbase.utils.SystemUtils;
import celtech.roboxbase.utils.models.PrintableProject;
import celtech.roboxbase.utils.tasks.TaskResponse;
import static celtech.utils.StringMetrics.getWidthOfString;
import java.io.File;
import java.io.IOException;
import static java.lang.Double.max;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Ian
 */
public class LayoutStatusMenuStripController implements PrinterListChangesListener
{
    private final Stenographer steno = StenographerFactory.getStenographer(
            LayoutStatusMenuStripController.class.getName());
    private PrinterSettingsOverrides printerSettings = null;
    private ApplicationStatus applicationStatus = null;
    private DisplayManager displayManager = null;
    
    private final FileChooser modelFileChooser = new FileChooser();
    private final FileChooser saveGCodeFileChooser = new FileChooser();
    private PrinterUtils printerUtils = null;
    private final PrinterColourMap colourMap = PrinterColourMap.getInstance();

    private final IntegerProperty currentNozzle = new SimpleIntegerProperty(0);

    private final BooleanProperty canPrintProject = new SimpleBooleanProperty(false);

    private ReprintPanel reprintPanel = new ReprintPanel();

    private PreviewManager previewManager = null;

    @FXML
    private GraphicButtonWithLabel undoButton;

    @FXML
    private GraphicButtonWithLabel redoButton;

    @FXML
    private GraphicButtonWithLabel backwardFromSettingsButton;

    @FXML
    private GraphicButtonWithLabel backwardFromLayoutButton;

    @FXML
    private GraphicButtonWithLabel calibrateButton;

    @FXML
    private GraphicButtonWithLabel forwardButtonSettings;

    @FXML
    private GraphicButtonWithLabel forwardButtonLayout;

    @FXML
    private GraphicButtonWithLabel unlockDoorButton;

    @FXML
    private GraphicButtonWithLabel ejectFilamentButton;

    @FXML
    private GraphicButtonWithLabel fineNozzleButton;

    @FXML
    private GraphicButtonWithLabel fillNozzleButton;

    @FXML
    private GraphicButtonWithLabel openNozzleButton;

    @FXML
    private GraphicButtonWithLabel closeNozzleButton;

    @FXML
    private GraphicButtonWithLabel homeButton;

    @FXML
    private GraphicButtonWithLabel removeHeadButton;

    @FXML
    private GraphicToggleButtonWithLabel headFanButton;

    @FXML
    private GraphicButtonWithLabel lightsButton;

    @FXML
    private GraphicButtonWithLabel reprintButton;

    @FXML
    private GraphicButtonWithLabel printButton;
    
    @FXML
    private GraphicButtonWithLabel previewButton;

    @FXML
    private GraphicButtonWithLabel saveButton;

    @FXML
    private FlowPane layoutButtonHBox;

    @FXML
    private FlowPane settingsButtonHBox;

    @FXML
    private FlowPane statusButtonHBox;

    @FXML
    private GraphicButtonWithLabel addModelButton;

    @FXML
    private GraphicButtonWithLabel deleteModelButton;

    @FXML
    private GraphicButtonWithLabel duplicateModelButton;

    @FXML
    private GraphicButtonWithLabel distributeModelsButton;

//    @FXML
//    private GraphicButtonWithLabel addCloudModelButton;

    @FXML
    private GraphicToggleButtonWithLabel snapToGroundButton;

    @FXML
    private GraphicButtonWithLabel groupButton;

    @FXML
    private GraphicButtonWithLabel ungroupButton;

    @FXML
    private GraphicButtonWithLabel purgeButton;

//    @FXML
//    private GraphicButtonWithLabel cutButton;
    private Project selectedProject;
    private UndoableProject undoableSelectedProject;
    private ObjectProperty<LayoutSubmode> layoutSubmode;
    private ProjectSelection projectSelection;
    private final ModelLoader modelLoader = new ModelLoader();

    private ConditionalNotificationBar oneExtruderNoFilamentSelectedNotificationBar;
    private ConditionalNotificationBar oneExtruderNoFilamentNotificationBar;
    private ConditionalNotificationBar twoExtrudersNoFilament0SelectedNotificationBar;
    private ConditionalNotificationBar twoExtrudersNoFilament0NotificationBar;
    private ConditionalNotificationBar twoExtrudersNoFilament1SelectedNotificationBar;
    private ConditionalNotificationBar twoExtrudersNoFilament1NotificationBar;
    private ConditionalNotificationBar doorOpenConditionalNotificationBar;
    private ConditionalNotificationBar invalidMeshInProjectNotificationBar;
    private ConditionalNotificationBar chooseACustomProfileNotificationBar;
    private ConditionalNotificationBar printHeadPowerOffNotificationBar;
    private ConditionalNotificationBar noHeadNotificationBar;
    private ConditionalNotificationBar noModelsNotificationBar;
    private ConditionalNotificationBar dmHeadOnSingleExtruderMachineNotificationBar;
    private ConditionalNotificationBar singleXHeadRequiredForFilledMaterialNotificationBar;

    private final BooleanProperty modelsOffBed = new SimpleBooleanProperty(false);
    private final BooleanProperty modelsOffBedWithHead = new SimpleBooleanProperty(false);
    private final BooleanProperty modelsOffBedWithRaft = new SimpleBooleanProperty(false);
    private final BooleanProperty modelOffBedWithSpiral = new SimpleBooleanProperty(false);
    private final BooleanProperty headIsSingleX = new SimpleBooleanProperty(false);
    private ConditionalNotificationBar modelsOffBedNotificationBar;
    private ConditionalNotificationBar modelsOffBedWithHeadNotificationBar;
    private ConditionalNotificationBar modelsOffBedWithRaftNotificationBar;
    private ConditionalNotificationBar modelOffBedWithSpiralNotificationBar;

    private ConditionalNotificationBar notEnoughFilamentForPrintNotificationBar;
    private final BooleanProperty notEnoughFilamentForPrint = new SimpleBooleanProperty(false);
    private ConditionalNotificationBar notEnoughFilament1ForPrintNotificationBar;
    private final BooleanProperty notEnoughFilament1ForPrint = new SimpleBooleanProperty(false);
    private ConditionalNotificationBar notEnoughFilament2ForPrintNotificationBar;
    private final BooleanProperty notEnoughFilament2ForPrint = new SimpleBooleanProperty(false);

    private final BooleanProperty printerConnectionOffline = new SimpleBooleanProperty(false);
    
    private TimeCostThreadManager timeCostThreadManager;

    private final MapChangeListener<Integer, Filament> effectiveFilamentListener = (MapChangeListener.Change<? extends Integer, ? extends Filament> change) ->
    {
        whenProjectOrSettingsPrinterChange();
    };

    private ChangeListener<Boolean> outOfBoundsModelListener = new ChangeListener<Boolean>()
    {
        @Override
        public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1)
        {
            timeCostThreadManager.cancelRunningTimeCostTasks();
        }
    };

    @FXML
    void group(ActionEvent event)
    {
        Project currentProject = Lookup.getSelectedProjectProperty().get();

        if (currentProject instanceof ModelContainerProject)
        {
            ModelContainerProject projectToWorkOn = (ModelContainerProject) currentProject;
            Set<ProjectifiableThing> modelGroups = projectToWorkOn.getTopLevelThings().stream().filter(
                    mc -> mc instanceof ModelGroup).collect(Collectors.toSet());
            Set<ProjectifiableThing> modelContainers = Lookup.getProjectGUIState(currentProject).getProjectSelection().getSelectedModelsSnapshot();
            Set<Groupable> thingsToGroup = (Set) modelContainers;
            undoableSelectedProject.group(thingsToGroup);
            Set<ModelContainer> changedModelGroups = currentProject.getAllModels().stream().map(ModelContainer.class::cast).filter(
                    mc -> mc instanceof ModelGroup).collect(Collectors.toSet());
            changedModelGroups.removeAll(modelGroups);

            Lookup.getProjectGUIState(currentProject).getProjectSelection().deselectAllModels();
            if (changedModelGroups.size() == 1)
            {
                changedModelGroups.iterator().next().notifyScreenExtentsChange();
                Lookup.getProjectGUIState(currentProject).getProjectSelection().addSelectedItem(
                        changedModelGroups.iterator().next());
            }
        }
    }

    @FXML
    void ungroup(ActionEvent event)
    {
        Project currentProject = Lookup.getSelectedProjectProperty().get();

        if (currentProject instanceof ModelContainerProject)
        {
            Set<ProjectifiableThing> modelContainers = Lookup.getProjectGUIState(currentProject).getProjectSelection().getSelectedModelsSnapshot();

            Set<ModelContainer> thingsToUngroup = (Set) modelContainers;
            undoableSelectedProject.ungroup(thingsToUngroup);
            Lookup.getProjectGUIState(currentProject).getProjectSelection().deselectAllModels();
        }
    }

    @FXML
    void startCut(ActionEvent event)
    {
        layoutSubmode.set(LayoutSubmode.Z_CUT);
    }

    @FXML
    void forwardPressed(ActionEvent event)
    {
        switch (applicationStatus.getMode())
        {
            case STATUS:
                applicationStatus.setMode(ApplicationMode.LAYOUT);
                break;
            case LAYOUT:
                applicationStatus.setMode(ApplicationMode.SETTINGS);
                break;
            default:
                break;
        }
    }

    @FXML
    void printPressed(ActionEvent event)
    {
        Printer printer = Lookup.getSelectedPrinterProperty().get();
        
        Project currentProject = Lookup.getSelectedProjectProperty().get();

        if (!currentProject.isProjectSaved())
        {
            Project.saveProject(currentProject);
        }
        
        if (currentProject instanceof ModelContainerProject)
        {
            String projectLocation = ApplicationConfiguration.getProjectDirectory()
                    + currentProject.getProjectName();
            PrintableProject printableProject = new PrintableProject(currentProject.getProjectName(), 
                    currentProject.getPrintQuality(), projectLocation);
            
            PurgeResponse purgeConsent = printerUtils.offerPurgeIfNecessary(printer,
                    ((ModelContainerProject) currentProject).getUsedExtruders(printer));

            // Trigger data is used by post processor.
            // Camera data is sent to Root.
            CameraTriggerData cameraTriggerData = null;
            Optional<CameraSettings> cameraData = Optional.empty();
            boolean timelapseEnabled = false;
            TimelapseSettingsData tlsd = currentProject.getTimelapseSettings();
            if (tlsd != null &&
                printer.getCommandInterface() instanceof RoboxRemoteCommandInterface) {
                DetectedServer printerServer = ((RemoteDetectedPrinter)printer.getCommandInterface()
                                                                              .getPrinterHandle())
                                                                              .getServerPrinterIsAttachedTo();
                Optional<CameraInfo> infoOpt = tlsd.getTimelapseCamera();
                Optional<CameraProfile> profileOpt = tlsd.getTimelapseProfile();
                cameraData = infoOpt.flatMap((ci) -> profileOpt.map((cp) -> new CameraSettings(cp, ci)));
                // Get trigger data only if there is a suitable camera on the current server.
                Optional<CameraTriggerData> tdOpt = cameraData.flatMap(cd -> {
                    String cameraName = cd.getCamera().getCameraName();
                    return BaseLookup.getConnectedCameras()
                        .stream()
                        .filter(cc -> cc.getServer() == printerServer &&
                                      cameraName.equalsIgnoreCase(cc.getCameraName()))
                        .findAny()
                        .flatMap(cc -> profileOpt.map(pp -> new CameraTriggerData(pp.isHeadLightOff(),
                                                                                  pp.isAmbientLightOff(),
                                                                                  pp.isMoveBeforeCapture(),
                                                                                  pp.getMoveToX(),
                                                                                  pp.getMoveToY())));
                });

                // Can't set local variables inside lambda expressions.
                if (tdOpt.isPresent()) {
                    cameraTriggerData= tdOpt.get();
                    printerServer.setCameraTag(cameraData.get().getProfile().getProfileName(),
                                               cameraData.get().getCamera().getCameraName());
                    timelapseEnabled = tlsd.getTimelapseTriggerEnabled();
                }
            }
                
            printableProject.setCameraTriggerData(cameraTriggerData);
            printableProject.setCameraEnabled(timelapseEnabled);
            printableProject.setCameraData(cameraData);

            if (purgeConsent == PurgeResponse.PRINT_WITH_PURGE)
            {
                displayManager.getPurgeInsetPanelController().purgeAndPrint(
                        (ModelContainerProject) currentProject, printer);
            } else if (purgeConsent == PurgeResponse.PRINT_WITHOUT_PURGE
                    || purgeConsent == PurgeResponse.NOT_NECESSARY)
            {
                ObservableList<Boolean> usedExtruders = ((ModelContainerProject) currentProject).getUsedExtruders(printer);
                printableProject.setUsedExtruders(usedExtruders);
                for (int extruderNumber = 0; extruderNumber < usedExtruders.size(); extruderNumber++)
                {
                    if (usedExtruders.get(extruderNumber))
                    {
                        if (extruderNumber == 0)
                        {
                            if (currentPrinter.headProperty().get().headTypeProperty().get() == Head.HeadType.DUAL_MATERIAL_HEAD)
                            {
                                currentPrinter.resetPurgeTemperatureForNozzleHeater(currentPrinter.headProperty().get(), 1);
                            } else
                            {
                                currentPrinter.resetPurgeTemperatureForNozzleHeater(currentPrinter.headProperty().get(), 0);
                            }
                        } else
                        {
                            currentPrinter.resetPurgeTemperatureForNozzleHeater(currentPrinter.headProperty().get(), 0);
                        }
                    }
                }
                applicationStatus.setMode(ApplicationMode.STATUS);

                Task<Boolean> fetchGCodeResultAndPrint = new Task<Boolean>() 
                {
                    @Override
                    protected Boolean call() throws Exception 
                    {
                        try 
                        {
                            Optional<GCodeGeneratorResult> potentialGCodeGenResult = ((ModelContainerProject) currentProject)
                                    .getGCodeGenManager().getPrepResult(currentProject.getPrintQuality());
                            if(potentialGCodeGenResult.isPresent())
                            {
                                printer.printProject(printableProject, potentialGCodeGenResult, Lookup.getUserPreferences().isSafetyFeaturesOn());
                            }
                            return true;
                        } catch (PrinterException ex)
                        {
                            steno.error("Error during print project " + ex.getMessage());
                            return false;
                        }
                    }
                };
                // Run the task from GCodeGenManager so it can be managed...
                ((ModelContainerProject) currentProject).getGCodeGenManager().replaceAndExecutePrintOrSaveTask(fetchGCodeResultAndPrint);
            }
        }
    }
    
    @FXML
    void savePressed(ActionEvent event) 
    {
        steno.trace("Save slice to file pressed");
        Project currentProject = Lookup.getSelectedProjectProperty().get();
        
        if (!currentProject.isProjectSaved())
        {
            Project.saveProject(currentProject);
        }
        
        if (currentProject instanceof ModelContainerProject) 
        {
            Task<Boolean> fetchGCodeResultAndSave = new Task<Boolean>() 
            {
                @Override
                protected Boolean call() throws Exception 
                {
                    String projectLocation = ApplicationConfiguration.getProjectDirectory()
                            + currentProject.getProjectName();
                    Optional<GCodeGeneratorResult> potentialGCodeGenResult = ((ModelContainerProject) currentProject)
                            .getGCodeGenManager().getPrepResult(currentProject.getPrintQuality());
                    if(potentialGCodeGenResult.isPresent() 
                            && potentialGCodeGenResult.get().isSuccess())
                    {
                        BaseLookup.getTaskExecutor().runOnGUIThread(() -> {
                            steno.debug("Slicing successful prompting user to save sliced files..."); 
                            String slicedFilesLocation = projectLocation
                                    + File.separator
                                    + currentProject.getPrintQuality();
                            saveGCodeFileChooser.setTitle(Lookup.i18n("dialogs.saveGCodeToFile"));
                            saveGCodeFileChooser.setInitialFileName(currentProject.getProjectName());
                            File dest = saveGCodeFileChooser.showSaveDialog(DisplayManager.getMainStage());

                            if (dest != null)
                            {
                                try 
                                {
                                    FileUtils.copyDirectory(new File(slicedFilesLocation), dest);
                                    steno.debug("Files copied to new location - " + dest.getPath());

                                    // The files must use an appropriate print job id in order for the printer to accept it at.
                                    String jobUUID = SystemUtils.generate16DigitID();
                                    Optional<CameraInfo> infoOpt = currentProject.getTimelapseSettings().getTimelapseCamera();
                                    Optional<CameraProfile> profileOpt = currentProject.getTimelapseSettings().getTimelapseProfile();
                                    Optional<CameraSettings> settings = infoOpt.flatMap((i) -> profileOpt.map((p) -> new CameraSettings(p, i)));
                                    PrintJobUtils.assignPrintJobIdToProject(jobUUID, dest.getPath(), currentProject.getPrintQuality().toString(), settings);
                                }
                                catch (IOException ex)
                                {
                                    steno.exception("Error occured when attempting to save sliced GCode", ex);
                                }
                            }
                        });
                        return true;
                    }
                    return false;
                }
            };
            // Run the task from GCodeGenManager so it can be managed...
            ((ModelContainerProject) currentProject).getGCodeGenManager().replaceAndExecutePrintOrSaveTask(fetchGCodeResultAndSave);
        }
    }
	
    @FXML
    void previewPressed(ActionEvent event)
    {
        if (previewManager != null)
            previewManager.previewAction(event);
    }

    @FXML
    void backwardPressed(ActionEvent event)
    {
        switch (applicationStatus.getMode())
        {
            case LAYOUT:
                applicationStatus.setMode(ApplicationMode.STATUS);
                break;
            case SETTINGS:
                applicationStatus.setMode(ApplicationMode.LAYOUT);
                break;
            default:
                break;
        }
    }

    @FXML
    void calibrate(ActionEvent event)
    {
        ApplicationStatus.getInstance().setMode(ApplicationMode.CALIBRATION_CHOICE);
    }

    @FXML
    void purge(ActionEvent event)
    {
        DisplayManager.getInstance().getPurgeInsetPanelController().purge(currentPrinter);
    }

    @FXML
    void register(ActionEvent event
    )
    {
        ApplicationStatus.getInstance().setMode(ApplicationMode.REGISTRATION);
    }

    @FXML
    void addModelContext(ContextMenuEvent event)
    {
        ContextMenu contextMenu = new ContextMenu();

        String cm1Text = "Blank 2D Project";
        String cm2Text = "Blank 3D Project";

        MenuItem cmItem1 = new MenuItem(cm1Text);
        MenuItem cmItem2 = new MenuItem(cm2Text);

        cmItem1.setOnAction((ActionEvent e) ->
        {
            DisplayManager.getInstance().initialiseBlank2DProject();
        });
        cmItem2.setOnAction((ActionEvent e) ->
        {
            DisplayManager.getInstance().initialiseBlank3DProject();
        });

        contextMenu.getItems().add(cmItem1);
        contextMenu.getItems().add(cmItem2);

        double cm1Width = getWidthOfString(cm1Text, "lightText", 14);
        double cm2Width = getWidthOfString(cm2Text, "lightText", 14);

        contextMenu.show(addModelButton, Side.TOP,
                35 - ((max(cm1Width, cm2Width) + 20) / 2.0), -25);

    }

    @FXML
    void addModel(ActionEvent event
    )
    {
        Platform.runLater(() ->
        {
            List<File> files = selectFiles();

            if (files != null && !files.isEmpty())
            {
                ApplicationConfiguration.setLastDirectory(DirectoryMemoryProperty.LAST_MODEL_DIRECTORY,
                        files.get(0).getParentFile().getAbsolutePath());
                modelLoader.loadExternalModels(Lookup.getSelectedProjectProperty().get(), files, true, DisplayManager.getInstance(), false);
            }
        });
    }

    /**
     * Allow the user to select the files they want to load.
     */
    private List<File> selectFiles()
    {
        ListIterator iterator = modelFileChooser.getExtensionFilters().listIterator();
        while (iterator.hasNext())
        {
            iterator.next();
            iterator.remove();
        }
        String descriptionOfFile = Lookup.i18n("dialogs.meshFileChooserDescription");

        Project currentProject = Lookup.getSelectedProjectProperty().get();
        ProjectMode currentProjectMode = (currentProject == null) ? ProjectMode.NONE : currentProject.getMode();
        modelFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(descriptionOfFile,
                        ApplicationConfiguration.
                                getSupportedFileExtensionWildcards(
                                        currentProjectMode)));
        modelFileChooser.setInitialDirectory(ApplicationConfiguration.getLastDirectoryFile(DirectoryMemoryProperty.LAST_MODEL_DIRECTORY));
        List<File> files;

        files = modelFileChooser.showOpenMultipleDialog(displayManager.getMainStage());

        return files;
    }

    @FXML
    void undo(ActionEvent event)
    {
        CommandStack commandStack = Lookup.getProjectGUIState(selectedProject).getCommandStack();
        if (commandStack.getCanUndo().get())
        {
            try
            {
                commandStack.undo();
            } catch (CommandStack.UndoException ex)
            {
                steno.error("Unable to undo: " + ex);
            }
        }
    }

    @FXML
    void redo(ActionEvent event)
    {
        CommandStack commandStack = Lookup.getProjectGUIState(selectedProject).getCommandStack();
        if (commandStack.getCanRedo().get())
        {
            try
            {
                commandStack.redo();
            } catch (CommandStack.UndoException ex)
            {
                steno.error("Unable to redo: " + ex);
            }
        }
    }

//    @FXML
//    void addCloudModel(ActionEvent event)
//    {
//        applicationStatus.modeProperty().set(ApplicationMode.MY_MINI_FACTORY);
//    }

    @FXML
    void deleteModel(ActionEvent event)
    {
        undoableSelectedProject.deleteModels(projectSelection.getSelectedModelsSnapshot());
    }

    @FXML
    void copyModel(ActionEvent event)
    {
        undoableSelectedProject.copyModels(projectSelection.getSelectedModelsSnapshot());
    }

    @FXML
    void autoLayoutModels(ActionEvent event)
    {
        undoableSelectedProject.autoLayout();
    }

    @FXML
    void snapToGround(ActionEvent event)
    {
        layoutSubmode.set(LayoutSubmode.SNAP_TO_GROUND);
    }

    @FXML
    void unlockDoor(ActionEvent event)
    {
        try
        {
            currentPrinter.goToOpenDoorPosition(null, Lookup.getUserPreferences().isSafetyFeaturesOn());
        } catch (PrinterException ex)
        {
            steno.error("Error opening door " + ex.getMessage());
        }
    }

    @FXML
    void ejectFilament(ActionEvent event)
    {

        Printer printer = Lookup.getSelectedPrinterProperty().get();
        if (printer.extrudersProperty().get(0).filamentLoadedProperty().get()
                && printer.extrudersProperty().get(1).filamentLoadedProperty().get())
        {

            ContextMenu contextMenu = new ContextMenu();

            String cm1Text;
            String cm2Text;

            if (printer.reelsProperty().containsKey(0))
            {
                cm1Text = "1: "
                        + printer.reelsProperty().get(0).friendlyFilamentNameProperty().get();
            } else
            {
                cm1Text = "1: " + Lookup.i18n("materialComponent.unknown");
            }

            if (printer.reelsProperty().containsKey(1))
            {
                cm2Text = "2: "
                        + printer.reelsProperty().get(1).friendlyFilamentNameProperty().get();
            } else
            {
                cm2Text = "2: " + Lookup.i18n("materialComponent.unknown");
            }

            MenuItem cmItem1 = new MenuItem(cm1Text);
            MenuItem cmItem2 = new MenuItem(cm2Text);
            MenuItem bothItem = new MenuItem(Lookup.i18n("misc.Both"));
            cmItem1.setOnAction((ActionEvent e) ->
            {
                ejectFilament(0);
            });
            cmItem2.setOnAction((ActionEvent e) ->
            {
                ejectFilament(1);
            });
            bothItem.setOnAction((ActionEvent e) ->
            {
                ejectFilament(1);
                ejectFilament(0);
            });

            contextMenu.getItems().add(cmItem1);
            contextMenu.getItems().add(cmItem2);
            contextMenu.getItems().add(bothItem);

            double cm1Width = getWidthOfString(cm1Text, "lightText", 14);
            double cm2Width = getWidthOfString(cm2Text, "lightText", 14);

            contextMenu.show(ejectFilamentButton, Side.TOP,
                    35 - ((max(cm1Width, cm2Width) + 20) / 2.0), -25);
        } else if (printer.extrudersProperty().get(0).filamentLoadedProperty().get())
        {
            ejectFilament(0);
        } else if (printer.extrudersProperty().get(1).filamentLoadedProperty().get())
        {
            ejectFilament(1);
        }
    }

    private void ejectFilament(int extruder)
    {
        try
        {
            currentPrinter.ejectFilament(extruder, null);
        } catch (PrinterException ex)
        {
            steno.error("Error when sending eject filament - " + ex.getMessage());
        }
    }

    @FXML
    void selectNozzle0(ActionEvent event)
    {
        try
        {
            currentPrinter.selectNozzle(0);
            currentNozzle.set(0);
        } catch (PrinterException ex)
        {
            steno.error("Error when selecting nozzle 0" + ex.getMessage());
        }
    }

    @FXML
    void selectNozzle1(ActionEvent event)
    {
        try
        {
            currentPrinter.selectNozzle(1);
            currentNozzle.set(1);
        } catch (PrinterException ex)
        {
            steno.error("Error when selecting left nozzle" + ex.getMessage());
        }
    }

    @FXML
    void openNozzle(ActionEvent event)
    {
        try
        {
            currentPrinter.openNozzleFully();
        } catch (PrinterException ex)
        {
            steno.error("Error when opening nozzle" + ex.getMessage());
        }
    }

    @FXML
    void closeNozzle(ActionEvent event)
    {
        try
        {
            currentPrinter.closeNozzleFully();
        } catch (PrinterException ex)
        {
            steno.error("Error when closing nozzle" + ex.getMessage());
        }
    }

    @FXML
    void homeAll(ActionEvent event)
    {
        try
        {
            currentPrinter.homeAllAxes(false, null);
        } catch (PrinterException ex)
        {
            steno.error("Couldn't run home macro");
        }
    }

    @FXML
    void toggleLight(ActionEvent event)
    {
        ContextMenu contextMenu = new ContextMenu();

        String cm1Text = BaseLookup.i18n("buttonText.headLights");
        String cm2Text = BaseLookup.i18n("buttonText.ambientLights");

        MenuItem toggleHeadLight = new MenuItem(cm1Text);
        MenuItem toggleAmbientLight = new MenuItem(cm2Text);
        toggleHeadLight.setOnAction((ActionEvent e) ->
        {
            toggleHeadLight();
        });
        toggleAmbientLight.setOnAction((ActionEvent e) ->
        {
            toggleAmbientLight();
        });

        contextMenu.getItems().add(toggleHeadLight);
        contextMenu.getItems().add(toggleAmbientLight);

        double cm1Width = getWidthOfString(cm1Text, "lightText", 14);
        double cm2Width = getWidthOfString(cm2Text, "lightText", 14);

        contextMenu.show(lightsButton, Side.TOP,
                35 - ((max(cm1Width, cm2Width) + 20) / 2.0), -25);
    }

    @FXML
    void toggleHeadFan(ActionEvent event)
    {
        try
        {
            if (currentPrinter.getPrinterAncillarySystems().headFanOnProperty().get())
            {
                currentPrinter.switchOffHeadFan();
            } else
            {
                currentPrinter.switchOnHeadFan();
            }
        } catch (PrinterException ex)
        {
            steno.error("Failed to send head fan command - " + ex.getMessage());
        }
    }

    boolean headLEDOn = false;

    private void toggleHeadLight()
    {
        try
        {
            if (headLEDOn == true)
            {
                currentPrinter.switchOffHeadLEDs();
                headLEDOn = false;
            } else
            {
                currentPrinter.switchOnHeadLEDs();
                headLEDOn = true;
            }
        } catch (PrinterException ex)
        {
            steno.error("Failed to send head LED command - " + ex.getMessage());
        }
    }

    private AmbientLEDState ambientLEDState = AmbientLEDState.COLOUR;

    private void toggleAmbientLight()
    {
        try
        {
            // Off, White, Colour
            ambientLEDState = ambientLEDState.getNextState();

            switch (ambientLEDState)
            {
                case OFF:
                    currentPrinter.setAmbientLEDColour(Color.BLACK);
                    break;
                case WHITE:
                    currentPrinter.setAmbientLEDColour(
                            colourMap.displayToPrinterColour(Color.WHITE));
                    break;
                case COLOUR:
                    currentPrinter.setAmbientLEDColour(
                            currentPrinter.getPrinterIdentity().printerColourProperty().get());
                    break;
            }
        } catch (PrinterException ex)
        {
            steno.error("Failed to send ambient LED command");
        }
    }

    @FXML
    void removeHead(ActionEvent event)
    {
        try
        {
            currentPrinter.removeHead((TaskResponse taskResponse) ->
            {
                removeHeadFinished(taskResponse);
            }, Lookup.getUserPreferences().isSafetyFeaturesOn());
        } catch (PrinterException ex)
        {
            steno.error("PrinterException whilst invoking remove head: " + ex.getMessage());
        }
    }

    private void removeHeadFinished(TaskResponse taskResponse)
    {
        if (taskResponse.succeeded())
        {
            BaseLookup.getSystemNotificationHandler().showInformationNotification(Lookup.i18n(
                    "removeHead.title"), Lookup.i18n("removeHead.finished"));
            steno.debug("Head remove completed");
        } else
        {
            BaseLookup.getSystemNotificationHandler().showWarningNotification(Lookup.i18n(
                    "removeHead.title"), Lookup.i18n("removeHead.failed"));
        }
    }

    @FXML
    void showReprintDialog(ActionEvent event)
    {
        reprintPanel.show(currentPrinter);
    }

    /**
     * The printer selected on the Status screen.
     */
    private Printer currentPrinter = null;
    private final BooleanProperty printerAvailable = new SimpleBooleanProperty(false);

    private final ChangeListener<Boolean> headFanStatusListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
    {
        headFanButton.setSelected(newValue);
    };

    private final ChangeListener<Boolean> tooManyRoboxAttachedListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
    {
        if (newValue)
            BaseLookup.getSystemNotificationHandler()
                      .showWarningNotification(Lookup.i18n("dialogs.toomanyrobox.title"),
                                               Lookup.i18n("dialogs.toomanyrobox.message"));
    };

    private final ChangeListener<PreviewManager.PreviewState> previewStateChangeListener = (observable, oldState, newState) -> {
        BaseLookup.getTaskExecutor().runOnGUIThread(() ->
        {
            updatePreviewButton(newState);
        });
    };

    /*
     * JavaFX initialisation method
     */
    @FXML
    void initialize()
    {
        timeCostThreadManager = TimeCostThreadManager.getInstance();

        modelsOffBed.addListener(outOfBoundsModelListener);
        modelsOffBedWithHead.addListener(outOfBoundsModelListener);
        modelOffBedWithSpiral.addListener(outOfBoundsModelListener);
        modelsOffBedWithRaft.addListener(outOfBoundsModelListener);

        oneExtruderNoFilamentSelectedNotificationBar = new ConditionalNotificationBar("dialogs.cantPrintNoFilamentSelectedMessage", NotificationType.CAUTION);
        oneExtruderNoFilamentNotificationBar = new ConditionalNotificationBar("dialogs.cantPrintNoFilamentMessage", NotificationType.CAUTION);
        twoExtrudersNoFilament0SelectedNotificationBar = new ConditionalNotificationBar("dialogs.cantPrintNoFilamentSelectedMessage0", NotificationType.CAUTION);
        twoExtrudersNoFilament0NotificationBar = new ConditionalNotificationBar("dialogs.cantPrintNoFilamentMessage0", NotificationType.CAUTION);
        twoExtrudersNoFilament1SelectedNotificationBar = new ConditionalNotificationBar("dialogs.cantPrintNoFilamentSelectedMessage1", NotificationType.CAUTION);
        twoExtrudersNoFilament1NotificationBar = new ConditionalNotificationBar("dialogs.cantPrintNoFilamentMessage1", NotificationType.CAUTION);
        doorOpenConditionalNotificationBar = new ConditionalNotificationBar("dialogs.cantPrintDoorIsOpenMessage", NotificationType.CAUTION);
        invalidMeshInProjectNotificationBar = new ConditionalNotificationBar("dialogs.invalidMeshInProjectMessage", NotificationType.NOTE);
        chooseACustomProfileNotificationBar = new ConditionalNotificationBar("dialogs.chooseACustomProfile", NotificationType.CAUTION);
        printHeadPowerOffNotificationBar = new ConditionalNotificationBar("dialogs.printHeadPowerOff", NotificationType.CAUTION);
        noHeadNotificationBar = new ConditionalNotificationBar("dialogs.cantPrintNoHeadMessage", NotificationType.CAUTION);
        noModelsNotificationBar = new ConditionalNotificationBar("dialogs.cantPrintNoModelOnBed", NotificationType.CAUTION);

        dmHeadOnSingleExtruderMachineNotificationBar = new ConditionalNotificationBar("dialogs.dualMaterialHeadOnSingleExtruderMachine", NotificationType.WARNING);
        singleXHeadRequiredForFilledMaterialNotificationBar = new ConditionalNotificationBar("dialogs.singleXHeadRequiredForFilledMaterial", NotificationType.CAUTION);

        modelsOffBedNotificationBar = new ConditionalNotificationBar("dialogs.modelsOffBed", NotificationType.CAUTION);
        modelsOffBedWithHeadNotificationBar = new ConditionalNotificationBar("dialogs.modelsOffBedWithHead", NotificationType.CAUTION);
        modelsOffBedWithRaftNotificationBar = new ConditionalNotificationBar("dialogs.modelsOffBedWithRaft", NotificationType.CAUTION);
        modelOffBedWithSpiralNotificationBar = new ConditionalNotificationBar("dialogs.modelOffBedWithSpiral", NotificationType.CAUTION);

        modelsOffBedNotificationBar.setAppearanceCondition(ApplicationStatus.getInstance().modeProperty().isEqualTo(ApplicationMode.SETTINGS).and(modelsOffBed));
        modelsOffBedWithHeadNotificationBar.setAppearanceCondition(ApplicationStatus.getInstance().modeProperty().isEqualTo(ApplicationMode.SETTINGS).and(modelsOffBedWithHead).and(modelsOffBed.not()));
        modelsOffBedWithRaftNotificationBar.setAppearanceCondition(ApplicationStatus.getInstance().modeProperty().isEqualTo(ApplicationMode.SETTINGS).and(modelsOffBedWithRaft).and(modelsOffBed.not()).and(modelsOffBedWithHead.not()));
        modelOffBedWithSpiralNotificationBar.setAppearanceCondition(ApplicationStatus.getInstance().modeProperty().isEqualTo(ApplicationMode.SETTINGS).and(modelOffBedWithSpiral).and(modelsOffBed.not()).and(modelsOffBedWithHead.not()).and(modelsOffBedWithRaft.not()));

        notEnoughFilamentForPrintNotificationBar = new ConditionalNotificationBar("dialogs.notEnoughFilamentToCompletePrint", NotificationType.CAUTION);
        notEnoughFilamentForPrintNotificationBar.setAppearanceCondition(ApplicationStatus.getInstance().modeProperty().isEqualTo(ApplicationMode.SETTINGS)
                .and(notEnoughFilamentForPrint)
                .and(printerConnectionOffline.not()));
        notEnoughFilament1ForPrintNotificationBar = new ConditionalNotificationBar("dialogs.notEnoughFilament2ToCompletePrint", NotificationType.CAUTION);
        notEnoughFilament1ForPrintNotificationBar.setAppearanceCondition(ApplicationStatus.getInstance().modeProperty().isEqualTo(ApplicationMode.SETTINGS)
                .and(notEnoughFilament1ForPrint)
                .and(printerConnectionOffline.not()));
        notEnoughFilament2ForPrintNotificationBar = new ConditionalNotificationBar("dialogs.notEnoughFilament2ToCompletePrint", NotificationType.CAUTION);
        notEnoughFilament2ForPrintNotificationBar.setAppearanceCondition(ApplicationStatus.getInstance().modeProperty().isEqualTo(ApplicationMode.SETTINGS)
                .and(notEnoughFilament2ForPrint)
                .and(printerConnectionOffline.not()));

        RoboxCommsManager.getInstance().tooManyRoboxAttachedProperty().addListener(tooManyRoboxAttachedListener);

        displayManager = DisplayManager.getInstance();
        applicationStatus = ApplicationStatus.getInstance();
        printerUtils = PrinterUtils.getInstance();

        statusButtonHBox.setVisible(false);

        createStatusPrinterListener();

        printButton.disableProperty().bind(canPrintProject.not());

        setupButtonVisibility();

        Lookup.getSelectedProjectProperty().addListener((ObservableValue<? extends Project> observable, Project oldValue, Project newValue) ->
        {
            whenProjectChanges(newValue);
        });

        Lookup.getSelectedPrinterProperty().addListener(
                (ObservableValue<? extends Printer> observable, Printer oldValue, Printer newValue) ->
        {
            currentPrinter = newValue;
            if (oldValue != null)
            {
                oldValue.effectiveFilamentsProperty().removeListener(effectiveFilamentListener);
            }

            if (newValue != null)
            {
                newValue.effectiveFilamentsProperty().addListener(effectiveFilamentListener);
            }
        });
        
        currentPrinter = Lookup.getSelectedPrinterProperty().get();
        previewManager = new PreviewManager();
        previewManager.setProjectAndPrinter(selectedProject, currentPrinter);
        previewManager.previewStateProperty().addListener(previewStateChangeListener);
        displayManager.setPreviewManager(previewManager);

        BaseLookup.getPrinterListChangesNotifier().addListener(this);
    }
        
    private void updatePreviewButton(PreviewManager.PreviewState newState)
    {
        switch(newState)
        {
            case CLOSED:
            case OPEN:
                previewButton.setFxmlFileName("previewButton");
                previewButton.disableProperty().set(false);
                break;
            case NOT_SUPPORTED:
            case SLICE_UNAVAILABLE:
                previewButton.setFxmlFileName("previewButton");
                previewButton.disableProperty().set(true);
                break;
            case LOADING:
                previewButton.setFxmlFileName("previewLoadingButton");
                previewButton.disableProperty().set(true);
                break;
        }
    }
    
    private void setupButtonVisibility()
    {

        backwardFromLayoutButton.visibleProperty().bind(applicationStatus.modeProperty().isEqualTo(
                ApplicationMode.LAYOUT));
        backwardFromSettingsButton.visibleProperty().bind(applicationStatus.modeProperty().
                isEqualTo(ApplicationMode.SETTINGS));

        updateSaveAndPrintButtonVisibility();
        
        closeNozzleButton.setVisible(false);
        fillNozzleButton.setVisible(false);

        groupButton.setVisible(true);
        ungroupButton.setVisible(false);

        // Prevent the status bar affecting layout when it is invisible
        statusButtonHBox.visibleProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            statusButtonHBox.setManaged(newValue);
        });

        // Prevent the layout bar affecting layout when it is invisible
        layoutButtonHBox.visibleProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            layoutButtonHBox.setManaged(newValue);
        });

        // Prevent the settings bar affecting layout when it is invisible
        settingsButtonHBox.visibleProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            settingsButtonHBox.setManaged(newValue);
        });
        
        statusButtonHBox.visibleProperty().bind(applicationStatus.modeProperty().isEqualTo(
                ApplicationMode.STATUS)
                .and(printerAvailable));
        layoutButtonHBox.visibleProperty().bind(applicationStatus.modeProperty().isEqualTo(
                ApplicationMode.LAYOUT));
        settingsButtonHBox.visibleProperty().bind(applicationStatus.modeProperty().isEqualTo(
                ApplicationMode.SETTINGS));
        modelFileChooser.setTitle(Lookup.i18n("dialogs.modelFileChooser"));
        modelFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(Lookup.i18n(
                        "dialogs.modelFileChooserDescription"),
                        ApplicationConfiguration.
                                getSupportedFileExtensionWildcards(
                                        ProjectMode.NONE)));

        forwardButtonSettings.visibleProperty().bind(applicationStatus.modeProperty().isEqualTo(
                ApplicationMode.LAYOUT));
        forwardButtonLayout.visibleProperty().bind((applicationStatus.modeProperty().isEqualTo(
                ApplicationMode.STATUS)));
    }
    
    private void updateSaveAndPrintButtonVisibility() {
        printButton.visibleProperty().unbind();
        saveButton.visibleProperty().unbind();
        
        printButton.visibleProperty().bind(applicationStatus.modeProperty()
                .isEqualTo(ApplicationMode.SETTINGS)
                .and(printerConnectionOffline.not()));

        saveButton.visibleProperty().bind(applicationStatus.modeProperty()
                .isEqualTo(ApplicationMode.SETTINGS)
                .and(printerConnectionOffline));
    }

    ChangeListener<Printer> printerSettingsListener = (ObservableValue<? extends Printer> observable, Printer oldValue, Printer newValue) ->
    {
        if (newValue != null)
        {
            whenProjectOrSettingsPrinterChange();
        } else
        {
            printButton.disableProperty().unbind();
            printButton.setDisable(true);
        }
    };

    /**
     * Create the bindings for when tied to the SettingsScreen.
     */
    private void createPrinterSettingsListener(PrinterSettingsOverrides printerSettings)
    {
        if (printerSettings != null)
        {
            Lookup.getSelectedPrinterProperty().addListener(printerSettingsListener);
        }
    }

    private void updatePrintButtonConditionalText(Printer printer, Project project)
    {
        if (printer == null || project == null)
        {
            return;
        }

        doorOpenConditionalNotificationBar.setAppearanceCondition(
                printer.getPrinterAncillarySystems().doorOpenProperty()
                        .and(Lookup.getUserPreferences().safetyFeaturesOnProperty())
                        .and(applicationStatus.modeProperty().isEqualTo(ApplicationMode.SETTINGS)));
        chooseACustomProfileNotificationBar.setAppearanceCondition(project.customSettingsNotChosenProperty()
                .and(applicationStatus.modeProperty().isEqualTo(ApplicationMode.SETTINGS)));
        printHeadPowerOffNotificationBar.setAppearanceCondition(printer.headPowerOnFlagProperty().not()
                .and(applicationStatus.modeProperty().isEqualTo(ApplicationMode.SETTINGS))
                .and(printer.headProperty().isNotNull()));
        noHeadNotificationBar.setAppearanceCondition(printer.headProperty().isNull()
                .and(applicationStatus.modeProperty().isEqualTo(ApplicationMode.SETTINGS)));
        if (printer.headProperty().get() != null)
        {
            printer.extrudersProperty().get(0).isFittedProperty().get();
            printer.extrudersProperty().get(1).isFittedProperty().get();
            dmHeadOnSingleExtruderMachineNotificationBar.setAppearanceCondition(printer.headProperty().get().headTypeProperty().isEqualTo(Head.HeadType.DUAL_MATERIAL_HEAD)
                    .and(printer.extrudersProperty().get(0).isFittedProperty().not().or(printer.extrudersProperty().get(1).isFittedProperty().not())));
            headIsSingleX.set(printer.headProperty().get().typeCodeProperty().get().equalsIgnoreCase("RBXDV-S1"));
        }
        else
            headIsSingleX.set(false);
 
        if (project instanceof ModelContainerProject)
        {
            ModelContainerProject mcProject = (ModelContainerProject) project;

            BooleanBinding oneExtruderPrinter = printer.extrudersProperty().get(1).isFittedProperty().not();
            oneExtruderPrinter.get();
            BooleanBinding twoExtruderPrinter = printer.extrudersProperty().get(1).isFittedProperty().not().not();
            twoExtruderPrinter.get();
            BooleanBinding noFilament0Selected = Bindings.valueAt(printer.effectiveFilamentsProperty(), 0).isEqualTo(FilamentContainer.UNKNOWN_FILAMENT);
            noFilament0Selected.get();
            BooleanBinding noFilament1Selected = Bindings.valueAt(printer.effectiveFilamentsProperty(), 1).isEqualTo(FilamentContainer.UNKNOWN_FILAMENT);
            noFilament1Selected.get();

            ObservableList<Boolean> usedExtruders = ((ModelContainerProject) project).getUsedExtruders(printer);

            oneExtruderNoFilamentSelectedNotificationBar.setAppearanceCondition(oneExtruderPrinter.and(Bindings.booleanValueAt(usedExtruders, 0))
                    .and(noFilament0Selected)
                    .and(applicationStatus.modeProperty().isEqualTo(ApplicationMode.SETTINGS))
                    .and(printerConnectionOffline.not()));

            oneExtruderNoFilamentNotificationBar.setAppearanceCondition(oneExtruderPrinter
                    .and(Bindings.booleanValueAt(usedExtruders, 0))
                    .and(printer.extrudersProperty().get(0).filamentLoadedProperty().not())
                    .and(applicationStatus.modeProperty().isEqualTo(ApplicationMode.SETTINGS))
                    .and(printerConnectionOffline.not()));

            twoExtrudersNoFilament0SelectedNotificationBar.setAppearanceCondition(twoExtruderPrinter
                    .and(Bindings.booleanValueAt(usedExtruders, 0))
                    .and(noFilament0Selected)
                    .and(applicationStatus.modeProperty().isEqualTo(ApplicationMode.SETTINGS))
                    .and(printerConnectionOffline.not()));

            twoExtrudersNoFilament0NotificationBar.setAppearanceCondition(twoExtruderPrinter
                    .and(Bindings.booleanValueAt(usedExtruders, 0))
                    .and(printer.extrudersProperty().get(0).filamentLoadedProperty().not())
                    .and(applicationStatus.modeProperty().isEqualTo(ApplicationMode.SETTINGS))
                    .and(printerConnectionOffline.not()));

            twoExtrudersNoFilament1SelectedNotificationBar.setAppearanceCondition(twoExtruderPrinter
                    .and(Bindings.booleanValueAt(usedExtruders, 1))
                    .and(noFilament1Selected)
                    .and(applicationStatus.modeProperty().isEqualTo(ApplicationMode.SETTINGS))
                    .and(printerConnectionOffline.not()));

            twoExtrudersNoFilament1NotificationBar.setAppearanceCondition(twoExtruderPrinter
                    .and(Bindings.booleanValueAt(usedExtruders, 1))
                    .and(printer.extrudersProperty().get(1).filamentLoadedProperty().not())
                    .and(applicationStatus.modeProperty().isEqualTo(ApplicationMode.SETTINGS))
                    .and(printerConnectionOffline.not()));

            invalidMeshInProjectNotificationBar.setAppearanceCondition(mcProject.hasInvalidMeshes()
                    .and(applicationStatus.modeProperty().isEqualTo(ApplicationMode.SETTINGS)));

            noModelsNotificationBar.setAppearanceCondition(Bindings.isEmpty(project.getTopLevelThings())
                    .and(applicationStatus.modeProperty().isEqualTo(ApplicationMode.SETTINGS)));

            BooleanBinding filledMaterialAndNotSingleXHead = headIsSingleX.not().and(
                    Bindings.createBooleanBinding(() -> printer.effectiveFilamentsProperty()
                                                               .entrySet()
                                                               .stream()
                                                               .filter(entry -> usedExtruders.get(entry.getKey()) &&
                                                                                entry.getValue()
                                                                                     .isFilled()
                                                                                
                                                                       )
                                                               .findAny()
                                                               .isPresent(),
                                                  printer.effectiveFilamentsProperty()));
            singleXHeadRequiredForFilledMaterialNotificationBar.setAppearanceCondition(filledMaterialAndNotSingleXHead
                                                                                           .and(applicationStatus.modeProperty().isEqualTo(ApplicationMode.SETTINGS)));
        }
    }

    /**
     * Create the bindings to the Status selected printer.
     */
    private void createStatusPrinterListener()
    {
        currentPrinter = Lookup.getSelectedPrinterProperty().get();

        Lookup.getSelectedPrinterProperty().addListener((ObservableValue<? extends Printer> observable, Printer oldValue, Printer newValue) ->
        {
            if (newValue != null)
            {
                printerAvailable.set(true);

                if (currentPrinter != null)
                {
                    unlockDoorButton.disableProperty().unbind();
                    ejectFilamentButton.disableProperty().unbind();
                    fineNozzleButton.visibleProperty().unbind();
                    fillNozzleButton.visibleProperty().unbind();
                    openNozzleButton.visibleProperty().unbind();
                    closeNozzleButton.visibleProperty().unbind();
                    fineNozzleButton.disableProperty().unbind();
                    fillNozzleButton.disableProperty().unbind();
                    openNozzleButton.disableProperty().unbind();
                    closeNozzleButton.disableProperty().unbind();
                    homeButton.disableProperty().unbind();
                    currentPrinter.getPrinterAncillarySystems().headFanOnProperty().
                            removeListener(headFanStatusListener);
                    lightsButton.disableProperty().unbind();
                    calibrateButton.disableProperty().unbind();
                    removeHeadButton.disableProperty().unbind();
                    purgeButton.disableProperty().unbind();
                    reprintButton.disableProperty().unbind();
                    headFanButton.disableProperty().unbind();

                    clearConditionalNotificationBarConditions();
                }

                unlockDoorButton.disableProperty()
                        .bind(newValue.canOpenDoorProperty().not()
                                .or(printerConnectionOffline));
                ejectFilamentButton.disableProperty()
                        .bind((newValue.extrudersProperty().get(0).canEjectProperty().not()
                                .and(newValue.extrudersProperty().get(1).canEjectProperty().not()))
                                .or(printerConnectionOffline));

                // These buttons should only be available in advanced mode
                fineNozzleButton.disableProperty()
                        .bind(newValue.canOpenCloseNozzleProperty().not()
                                .or(Lookup.getUserPreferences().advancedModeProperty().not())
                                .or(printerConnectionOffline));
                fillNozzleButton.disableProperty()
                        .bind(newValue.canOpenCloseNozzleProperty().not()
                                .or(Lookup.getUserPreferences().advancedModeProperty().not())
                                .or(printerConnectionOffline));
                openNozzleButton.disableProperty()
                        .bind(newValue.canOpenCloseNozzleProperty().not()
                                .or(Lookup.getUserPreferences().advancedModeProperty().not())
                                .or(printerConnectionOffline));
                closeNozzleButton.disableProperty().bind(
                        newValue.canOpenCloseNozzleProperty().not()
                                .or(Lookup.getUserPreferences().advancedModeProperty().not())
                                .or(printerConnectionOffline));
                homeButton.disableProperty()
                        .bind(newValue.canPrintProperty().not()
                                .or(Lookup.getUserPreferences().advancedModeProperty().not())
                                .or(printerConnectionOffline));

                newValue.getPrinterAncillarySystems().headFanOnProperty().addListener(
                        headFanStatusListener);

                lightsButton.disableProperty().bind(printerConnectionOffline);
                headFanButton.disableProperty().bind(printerConnectionOffline);
                calibrateButton.disableProperty()
                        .bind(newValue.canCalibrateHeadProperty().not()
                        .or(printerConnectionOffline));
                removeHeadButton.disableProperty().bind(newValue.canPrintProperty().not()
                        .or(printerConnectionOffline));
                purgeButton.disableProperty()
                        .bind(newValue.canPurgeHeadProperty().not()
                        .or(printerConnectionOffline));
                reprintButton.disableProperty().bind(newValue.canPrintProperty().not()
                        .or(printerConnectionOffline));

                if (newValue.headProperty().get() != null)
                {
                    bindNozzleControls(newValue);
                }

                currentPrinter = newValue;

            } else
            {
                printerAvailable.set(false);
                clearConditionalNotificationBarConditions();
            }
        });
    }

    private void clearConditionalNotificationBarConditions()
    {
        oneExtruderNoFilamentSelectedNotificationBar.clearAppearanceCondition();
        oneExtruderNoFilamentNotificationBar.clearAppearanceCondition();
        twoExtrudersNoFilament0SelectedNotificationBar.clearAppearanceCondition();
        twoExtrudersNoFilament0NotificationBar.clearAppearanceCondition();
        twoExtrudersNoFilament1SelectedNotificationBar.clearAppearanceCondition();
        twoExtrudersNoFilament1NotificationBar.clearAppearanceCondition();
        doorOpenConditionalNotificationBar.clearAppearanceCondition();
        invalidMeshInProjectNotificationBar.clearAppearanceCondition();
        chooseACustomProfileNotificationBar.clearAppearanceCondition();
        printHeadPowerOffNotificationBar.clearAppearanceCondition();
        noHeadNotificationBar.clearAppearanceCondition();
        noModelsNotificationBar.clearAppearanceCondition();
        dmHeadOnSingleExtruderMachineNotificationBar.clearAppearanceCondition();
        singleXHeadRequiredForFilledMaterialNotificationBar.clearAppearanceCondition();
    }

    private final ChangeListener<LayoutSubmode> layoutSubmodeListener = (ObservableValue<? extends LayoutSubmode> observable, LayoutSubmode oldValue, LayoutSubmode newValue) ->
    {
        if (newValue != LayoutSubmode.SNAP_TO_GROUND)
        {
            snapToGroundButton.selectedProperty().set(false);
        }
    };
    
    ProjectChangesListener projectChangesListener = new ProjectChangesListener()
    {

        @Override
        public void whenModelAdded(ProjectifiableThing modelContainer)
        {
            whenProjectOrSettingsPrinterChange();
        }

        @Override
        public void whenModelsRemoved(Set<ProjectifiableThing> modelContainers)
        {
            whenProjectOrSettingsPrinterChange();
        }

        @Override
        public void whenAutoLaidOut()
        {
            dealWithOutOfBoundsModels();
        }

        @Override
        public void whenModelsTransformed(Set<ProjectifiableThing> modelContainers)
        {
            dealWithOutOfBoundsModels();
        }

        @Override
        public void whenModelChanged(ProjectifiableThing modelContainer, String propertyName)
        {
            whenProjectOrSettingsPrinterChange();
        }

        @Override
        public void whenPrinterSettingsChanged(PrinterSettingsOverrides printerSettings)
        {
            whenProjectOrSettingsPrinterChange();
        }

        @Override
        public void whenTimelapseSettingsChanged(TimelapseSettingsData timelapseSettings)
        {
            whenProjectOrSettingsPrinterChange();
        }
    };

    private void unbindProject(Project project)
    {
        Lookup.getSelectedPrinterProperty().removeListener(printerSettingsListener);
        layoutSubmode.removeListener(layoutSubmodeListener);
        project.removeProjectChangesListener(projectChangesListener);
        if (previewManager != null) {
            previewManager.setProjectAndPrinter(null, currentPrinter);
            previewManager.previewStateProperty().removeListener(previewStateChangeListener);
        }
        undoButton.disableProperty().unbind();
        redoButton.disableProperty().unbind();
    }

    private void bindProject(Project project)
    {

        createPrinterSettingsListener(printerSettings);
        if (project != null) {
            bindSelectedModels(project);

            if (currentPrinter != null)
            {
                whenProjectOrSettingsPrinterChange();
            }

            layoutSubmode.addListener(layoutSubmodeListener);
            project.addProjectChangesListener(projectChangesListener);

            undoButton.disableProperty().bind(
                    Lookup.getProjectGUIState(project).getCommandStack().getCanUndo().not());
            redoButton.disableProperty().bind(
                    Lookup.getProjectGUIState(project).getCommandStack().getCanRedo().not());
        }
        if (previewManager != null) {
            previewManager.setProjectAndPrinter(project, currentPrinter);
            previewManager.previewStateProperty().addListener(previewStateChangeListener);
        }
    }

    private void dealWithOutOfBoundsModels()
    {
        boolean aModelIsOffTheBed = false;
        boolean aModelIsOffTheBedWithHead = false;
        boolean aModelIsOffTheBedWithRaft = false;
        boolean aModelIsOffTheBedWithSpiral = false;

        if (selectedProject != null)
        {
            float zReduction = 0.0f;
            double raftOffset = 0.0;
    
            if (currentPrinter != null
                && currentPrinter.headProperty().get() != null)
            {
                RoboxProfile profileSettings = selectedProject.getPrinterSettings()
                        .getSettings(currentPrinter.headProperty().get().typeCodeProperty().get(), getSlicerType());
                if (profileSettings != null)
                    raftOffset = RoboxProfileUtils.calculateRaftOffset(profileSettings, getSlicerType());
           
                // Needed as heads differ in size and will need to adjust print volume for this
                zReduction = currentPrinter.headProperty().get().getZReductionProperty().get();
            }

            //TODO use settings derived offset values
            final double spiralOffset = 0.5 + zReduction;

            for (ProjectifiableThing projectifiableThing : selectedProject.getTopLevelThings())
            {
                if (projectifiableThing instanceof ModelContainer)
                {
                    ModelContainer modelContainer = (ModelContainer) projectifiableThing;

                    if (modelContainer.isOffBedProperty().get())
                    {
                        aModelIsOffTheBed = true;
                    }

                    if (zReduction > 0.0 
                            && modelContainer.isModelTooHighWithOffset(zReduction))
                    {
                        aModelIsOffTheBedWithHead = true;
                    }

                    if (selectedProject.getPrinterSettings().getRaftOverride()
                            && modelContainer.isModelTooHighWithOffset(raftOffset))
                    {
                        aModelIsOffTheBedWithRaft = true;
                    }

                    if (selectedProject.getPrinterSettings().getSpiralPrintOverride()
                            && modelContainer.isModelTooHighWithOffset(spiralOffset))
                    {
                        aModelIsOffTheBedWithSpiral = true;
                    }
                }
            }
        }

        if (aModelIsOffTheBed != modelsOffBed.get())
        {
            modelsOffBed.set(aModelIsOffTheBed);
        }

        if (aModelIsOffTheBedWithHead != modelsOffBedWithHead.get())
        {
            modelsOffBedWithHead.set(aModelIsOffTheBedWithHead);
        }

        if (aModelIsOffTheBedWithRaft != modelsOffBedWithRaft.get())
        {
            modelsOffBedWithRaft.set(aModelIsOffTheBedWithRaft);
        }

        if (aModelIsOffTheBedWithSpiral != modelOffBedWithSpiral.get())
        {
            modelOffBedWithSpiral.set(aModelIsOffTheBedWithSpiral);
        }
    }

    private void checkRemainingFilament()
    {
        boolean thereIsNotEnoughFilament = false;
        boolean thereIsNotEnoughFilament1 = false;
        boolean thereIsNotEnoughFilament2 = false;

        if (currentPrinter != null
                && selectedProject != null)
        {
//            steno.info("Got - " + selectedProject.getPrinterSettings().getSettingsName());
//if (timeCostThreadManager.
        }

        if (thereIsNotEnoughFilament != notEnoughFilamentForPrint.get())
        {
            notEnoughFilamentForPrint.set(thereIsNotEnoughFilament);
        }

        if (thereIsNotEnoughFilament1 != notEnoughFilament1ForPrint.get())
        {
            notEnoughFilament1ForPrint.set(thereIsNotEnoughFilament1);
        }

        if (thereIsNotEnoughFilament2 != notEnoughFilament2ForPrint.get())
        {
            notEnoughFilament2ForPrint.set(thereIsNotEnoughFilament2);
        }
    }

    private void whenProjectOrSettingsPrinterChange()
    {
        try
        {
            if (previewManager != null)
                previewManager.setProjectAndPrinter(selectedProject, currentPrinter);
            updateCanPrintProjectBindings(currentPrinter, selectedProject);
            updateSaveAndPrintButtonVisibility();
            updatePrintButtonConditionalText(currentPrinter, selectedProject);
            dealWithOutOfBoundsModels();
            checkRemainingFilament();
            if(currentPrinter != null) {
                printerConnectionOffline.set(currentPrinter.printerConnectionProperty().get().equals(PrinterConnection.OFFLINE));
            }
        } catch (Exception ex)
        {
            steno.exception("Error updating can print or print button conditionals", ex);
        }
    }

    /**
     * This must be called whenever the project is changed.
     *
     * @param project
     */
    public void whenProjectChanges(Project project)
    {
        if (selectedProject != null)
        {
            unbindProject(selectedProject);
            selectedProject = null;
        }

        if (project != null)
        {
            selectedProject = project;
            undoableSelectedProject = new UndoableProject(project);
            printerSettings = project.getPrinterSettings();
            currentPrinter = Lookup.getSelectedPrinterProperty().get();
            projectSelection = Lookup.getProjectGUIState(project).getProjectSelection();
            layoutSubmode = Lookup.getProjectGUIState(project).getLayoutSubmodeProperty();

            bindProject(project);
        }
    }

    /**
     * This should be called whenever the printer or project changes and updates
     * the bindings for the canPrintProject property.
     */
    private void updateCanPrintProjectBindings(Printer printer, Project project)
    {
        if (project instanceof ModelContainerProject)
        {
            if (printer != null && project != null)
            {
                printButton.disableProperty().unbind();
                ObservableList<Boolean> usedExtruders = ((ModelContainerProject) project).getUsedExtruders(printer);

                if (usedExtruders.get(0) && usedExtruders.get(1))
                {
                    BooleanBinding filament0Selected = Bindings.valueAt(printer.effectiveFilamentsProperty(), 0).isNotEqualTo(FilamentContainer.UNKNOWN_FILAMENT);
                    BooleanBinding filament1Selected = Bindings.valueAt(printer.effectiveFilamentsProperty(), 1).isNotEqualTo(FilamentContainer.UNKNOWN_FILAMENT);

                    canPrintProject.bind(
                            Bindings.isNotEmpty(project.getTopLevelThings())
                                    .and(printer.canPrintProperty())
                                    .and(project.canPrintProperty())
                                    .and(filament0Selected)
                                    .and(filament1Selected)
                                    .and(printer.getPrinterAncillarySystems().doorOpenProperty().not()
                                            .or(Lookup.getUserPreferences().safetyFeaturesOnProperty().not()))
                                    .and(printer.extrudersProperty().get(0).filamentLoadedProperty())
                                    .and(printer.extrudersProperty().get(1).filamentLoadedProperty()
                                            .and(printer.headPowerOnFlagProperty()))
                                    .and(modelsOffBed.not())
                                    .and(modelsOffBedWithHead.not())
                                    .and(modelsOffBedWithRaft.not())
                                    .and(modelOffBedWithSpiral.not())
                                    .and(headIsSingleX
                                             .or(printer.effectiveFilamentsProperty().get(0).getFilledProperty().not()
                                                     .and(printer.effectiveFilamentsProperty().get(1).getFilledProperty().not())))
                                    .and(printerConnectionOffline.not())
                    );
                } else
                {
                    // only one extruder required, which one is it?
                    int extruderNumber = (((ModelContainerProject) project).getUsedExtruders(printer).get(0)) ? 0 : 1;
                    BooleanBinding filamentPresentBinding = Bindings.valueAt(printer.effectiveFilamentsProperty(), extruderNumber).isNotEqualTo(FilamentContainer.UNKNOWN_FILAMENT);

                    canPrintProject.bind(
                            Bindings.isNotEmpty(project.getTopLevelThings())
                                    .and(printer.canPrintProperty())
                                    .and(project.canPrintProperty())
                                    .and(filamentPresentBinding)
                                    .and(printer.getPrinterAncillarySystems().doorOpenProperty().not()
                                            .or(Lookup.getUserPreferences().safetyFeaturesOnProperty().not()))
                                    .and(printer.extrudersProperty().get(extruderNumber).
                                            filamentLoadedProperty()
                                            .and(printer.headPowerOnFlagProperty()))
                                    .and(modelsOffBed.not())
                                    .and(modelsOffBedWithHead.not())
                                    .and(modelsOffBedWithRaft.not())
                                    .and(modelOffBedWithSpiral.not())
                                    .and(headIsSingleX
                                            .or(printer.effectiveFilamentsProperty().get(extruderNumber).getFilledProperty().not()))
                                    .and(printerConnectionOffline.not())
                    );
                }
                printButton.disableProperty().bind(canPrintProject.not());
            }
        }
    }

    /**
     * Binds button disabled properties to the selection container This disables
     * and enables buttons depending on whether a model is selected.
     */
    private void bindSelectedModels(Project project)
    {
        ProjectGUIState projectGUIState = Lookup.getProjectGUIState(project);
        ProjectSelection myProjectSelection = projectGUIState.getProjectSelection();
        ProjectGUIRules projectGUIRules = projectGUIState.getProjectGUIRules();
        ReadOnlyObjectProperty<LayoutSubmode> layoutSubmodeProperty = projectGUIState.
                getLayoutSubmodeProperty();

        addModelButton.disableProperty().unbind();
        deleteModelButton.disableProperty().unbind();
        duplicateModelButton.disableProperty().unbind();
        snapToGroundButton.disableProperty().unbind();
        distributeModelsButton.disableProperty().unbind();
        groupButton.disableProperty().unbind();
        groupButton.visibleProperty().unbind();
        ungroupButton.disableProperty().unbind();
        groupButton.visibleProperty().unbind();
//        cutButton.disableProperty().unbind();

        BooleanBinding notSelectModeOrNoSelectedModels
                = Bindings.notEqual(LayoutSubmode.SELECT, layoutSubmodeProperty).or(
                        Bindings.equal(0, myProjectSelection.getNumModelsSelectedProperty()));
        BooleanBinding notSelectModeOrNoLoadedModels
                = Bindings.notEqual(LayoutSubmode.SELECT, layoutSubmodeProperty).or(
                        Bindings.isEmpty(project.getTopLevelThings()));
        BooleanBinding snapToGround
                = Bindings.equal(LayoutSubmode.SNAP_TO_GROUND, layoutSubmodeProperty);
        BooleanBinding noLoadedModels = Bindings.isEmpty(project.getTopLevelThings());
        deleteModelButton.disableProperty().bind(
                notSelectModeOrNoSelectedModels.or(projectGUIRules.canRemoveOrDuplicateSelection().not()));
        duplicateModelButton.disableProperty().bind(
                notSelectModeOrNoSelectedModels.or(projectGUIRules.canRemoveOrDuplicateSelection().not()));
        distributeModelsButton.setDisable(true);

        addModelButton.disableProperty().bind(
                snapToGround.or(projectGUIRules.canAddModel().not()));
//        addCloudModelButton.disableProperty().bind(snapToGround.or(projectGUIRules.canAddModel().not()));

        distributeModelsButton.disableProperty().bind(
                notSelectModeOrNoLoadedModels.or(projectGUIRules.canAddModel().not()));
        snapToGroundButton.disableProperty().bind(
                noLoadedModels.or(projectGUIRules.canSnapToGroundSelection().not()));

        groupButton.disableProperty().bind(
                noLoadedModels.or(projectGUIRules.canGroupSelection().not()));
        groupButton.visibleProperty().bind(ungroupButton.visibleProperty().not());

        ungroupButton.visibleProperty().bind(
                noLoadedModels.not().and(projectGUIRules.canGroupSelection().not()).and(projectGUIRules.canUngroupSelection()));

//        cutButton.disableProperty().bind(
//                noLoadedModels.or(projectGUIRules.canCutModel().not()));
        ChangeListener<LayoutSubmode> whenSubModeChanges
                = (ObservableValue<? extends LayoutSubmode> ov, LayoutSubmode oldMode, LayoutSubmode newMode) ->
        {
            if (oldMode.equals(LayoutSubmode.SNAP_TO_GROUND) && newMode.equals(
                    LayoutSubmode.SELECT))
            {
                snapToGroundButton.setSelected(false);
            }
        };
        layoutSubmodeProperty.addListener(whenSubModeChanges);

    }

    @Override
    public void whenPrinterAdded(Printer printer)
    {
    }

    @Override
    public void whenPrinterRemoved(Printer printer)
    {
    }

    @Override
    public void whenHeadAdded(Printer printer)
    {
        if (printer == currentPrinter)
        {
            bindNozzleControls(printer);
            whenProjectOrSettingsPrinterChange();
        }
    }

    private void bindNozzleControls(Printer printer)
    {
        openNozzleButton.visibleProperty().bind(
                printer.headProperty().get().bPositionProperty().lessThan(0.5));
        closeNozzleButton.visibleProperty().bind(printer.headProperty().get().
                bPositionProperty().greaterThan(0.5));
        fineNozzleButton.visibleProperty().bind(printer.headProperty().get().
                nozzleInUseProperty().isEqualTo(1));
        fillNozzleButton.visibleProperty().bind(printer.headProperty().get().
                nozzleInUseProperty().isEqualTo(0));
    }

    @Override
    public void whenHeadRemoved(Printer printer, Head head)
    {
        if (printer == currentPrinter)
        {
            openNozzleButton.visibleProperty().unbind();
            openNozzleButton.setVisible(false);
            closeNozzleButton.visibleProperty().unbind();
            closeNozzleButton.setVisible(false);
            fineNozzleButton.visibleProperty().unbind();
            fineNozzleButton.setVisible(false);
            fillNozzleButton.visibleProperty().unbind();
            fillNozzleButton.setVisible(false);
            whenProjectOrSettingsPrinterChange();
        }
    }

    @Override
    public void whenReelAdded(Printer printer, int reelIndex)
    {
        if (printer == currentPrinter)
        {
            whenProjectOrSettingsPrinterChange();
        }
    }

    @Override
    public void whenReelRemoved(Printer printer, Reel reel, int reelIndex)
    {
        if (printer == currentPrinter)
        {
            whenProjectOrSettingsPrinterChange();
        }
    }

    @Override
    public void whenReelChanged(Printer printer, Reel reel)
    {
        if (printer == currentPrinter)
        {
            whenProjectOrSettingsPrinterChange();
        }
    }

    @Override
    public void whenExtruderAdded(Printer printer, int extruderIndex)
    {
        if (printer == currentPrinter)
        {
            whenProjectOrSettingsPrinterChange();
        }
    }

    @Override
    public void whenExtruderRemoved(Printer printer, int extruderIndex)
    {
        if (printer == currentPrinter)
        {
            whenProjectOrSettingsPrinterChange();
        }
    }

    private SlicerType getSlicerType() {
        return Lookup.getUserPreferences().getSlicerType();
    }
}
