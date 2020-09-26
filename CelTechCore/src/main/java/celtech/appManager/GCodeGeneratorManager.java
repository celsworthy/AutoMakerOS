/*
 * Copyright 2015 CEL UK
 */
package celtech.appManager;

import celtech.Lookup;
import celtech.configuration.ApplicationConfiguration;
import celtech.modelcontrol.ModelContainer;
import celtech.modelcontrol.ProjectifiableThing;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.comms.remote.RoboxRemoteCommandInterface;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.RoboxProfile;
import celtech.roboxbase.configuration.SlicerType;
import celtech.roboxbase.configuration.datafileaccessors.CameraProfileContainer;
import celtech.roboxbase.configuration.datafileaccessors.HeadContainer;
import celtech.roboxbase.configuration.datafileaccessors.RoboxProfileSettingsContainer;
import celtech.roboxbase.configuration.fileRepresentation.CameraProfile;
import celtech.roboxbase.configuration.fileRepresentation.PrinterSettingsOverrides;
import celtech.roboxbase.configuration.fileRepresentation.TimelapseSettings;
import celtech.roboxbase.configuration.utils.RoboxProfileUtils;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterListChangesAdapter;
import celtech.roboxbase.printerControl.model.PrinterListChangesListener;
import celtech.roboxbase.services.camera.CameraTriggerData;
import celtech.roboxbase.services.gcodegenerator.GCodeGeneratorResult;
import celtech.roboxbase.services.gcodegenerator.GCodeGeneratorTask;
import celtech.roboxbase.services.slicer.PrintQualityEnumeration;
import celtech.roboxbase.utils.models.MeshForProcessing;
import celtech.roboxbase.utils.models.PrintableMeshes;
import celtech.roboxbase.utils.tasks.Cancellable;
import celtech.roboxbase.utils.tasks.SimpleCancellable;
import celtech.roboxbase.utils.threed.CentreCalculations;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * GCodePreparationManager deals with {@link GCodeGeneratorTask}s.
 * It's job is to restart and cancel the tasks when appropriate and to also keep
 * track of the currently selected task.
 *
 * @author Tony and George
 */
public class GCodeGeneratorManager implements ModelContainerProject.ProjectChangesListener
{
    private static final Stenographer STENO = StenographerFactory.getStenographer(GCodeGeneratorManager.class.getName());
    
    private final ExecutorService slicingExecutorService;
    private final ExecutorService printOrSaveExecutorService;
    private final Project project;
    
    private final BooleanProperty dataChanged = new SimpleBooleanProperty(false);
    private final DoubleProperty selectedTaskProgress = new SimpleDoubleProperty(-1);
    private final BooleanProperty selectedTaskRunning = new SimpleBooleanProperty(false);
    private String selectedTaskMessage = "";
    
    private ChangeListener<Number> taskProgressChangeListener;
    private ChangeListener<Boolean> taskRunningChangeListener;
    private ChangeListener<String> taskMessageChangeListener;
    
    private ListChangeListener<RoboxProfile> roboxProfileChangeListener;
    
    private Future restartTask = null;
    private Map<PrintQualityEnumeration, Future> taskMap = new HashMap<>();
    private ObservableMap<PrintQualityEnumeration, Future> observableTaskMap = FXCollections.observableMap(taskMap);
    
    private Future printOrSaveTask = null;
    private BooleanProperty printOrSaveTaskRunning = new SimpleBooleanProperty(false);
    
    private Cancellable cancellable = null;
    private Printer currentPrinter = null;
    private boolean projectListenerInstalled = false;
    private boolean suppressReaction = false;
    
    private ObjectProperty<PrintQualityEnumeration> currentPrintQuality = new SimpleObjectProperty<>(PrintQualityEnumeration.DRAFT);
    private List<PrintQualityEnumeration> slicingOrder = Arrays.asList(PrintQualityEnumeration.values()); 
    private GCodeGeneratorTask selectedTask;
    
    private ChangeListener applicationModeChangeListener;
    private ChangeListener selectedPrinterReactionChangeListener;
    private PrinterListChangesListener printerListChangesListener;
    private MapChangeListener<Integer, Filament> filamentListener;
    
    private boolean projectNeedsSlicing = true;
    private boolean selectedTaskReBound = false;
    
    private ReentrantLock taskMapLock = new ReentrantLock();
    
    public GCodeGeneratorManager(Project project)
    {
        this.project = project;
        ThreadFactory threadFactory = (Runnable runnable) ->
        {
            Thread thread = Executors.defaultThreadFactory().newThread(runnable);
            thread.setDaemon(true);
            return thread;
        };
        // Could run multiple slicers, but probably safer to run them one at a time.
        //int nThreads = Runtime.getRuntime().availableProcessors() - 1;
        //if (nThreads < 1)
        //    nThreads = 1;
        int nThreads = 1;
        slicingExecutorService = Executors.newFixedThreadPool(nThreads, threadFactory);
        printOrSaveExecutorService = Executors.newSingleThreadExecutor();
        currentPrinter = Lookup.getSelectedPrinterProperty().get();
        
        initialiseListeners();
    }
    
    private void initialiseListeners() 
    {
        filamentListener = (MapChangeListener.Change<? extends Integer, ? extends Filament> change) -> 
        {
            if (isCurrentProjectSelected())
            {
                reactToChange(true);
            }
            else
            {
                projectNeedsSlicing = true;
            }
        };
        
        applicationModeChangeListener = (o, oldValue, newValue) -> 
        {
            if (newValue == ApplicationMode.SETTINGS)
            {
                if (!projectListenerInstalled)
                {
                    project.addProjectChangesListener(this);
                    projectListenerInstalled = true;
                }
                if(isCurrentProjectSelected() && projectNeedsSlicing) 
                {
                    // We lock this so when we try to print the tasks are restarted before we try to use them.
                    taskMapLock.lock();
                    try
                    {
                        restartAllTasks();
                    } finally
                    {
                        taskMapLock.unlock();
                    }
                    projectNeedsSlicing = false;
                }
            }
        };
        
        selectedPrinterReactionChangeListener = (o, oldValue, newValue) -> 
        {
            if (currentPrinter != newValue) 
            {
                if (currentPrinter != null) 
                {
                    currentPrinter.effectiveFilamentsProperty().removeListener(filamentListener);
                }
                purgeAllTasks();
                currentPrinter = (Printer) newValue;
                if (currentPrinter != null) 
                {
                    currentPrinter.effectiveFilamentsProperty().addListener(filamentListener);
                }
                if (isCurrentProjectSelected())
                {
                    reactToChange(true);
                }
                else
                {
                    projectNeedsSlicing = true;
                }
            }
        };
        
        printerListChangesListener = new PrinterListChangesAdapter() 
        {
            @Override
            public void whenHeadAdded(Printer printer)
            {
                if (printer == currentPrinter && isCurrentProjectSelected())
                {
                    reactToChange(true);
                }
                else
                {
                    projectNeedsSlicing = true;
                }
            }

            @Override
            public void whenExtruderAdded(Printer printer, int extruderIndex) 
            {
                if (printer == currentPrinter && isCurrentProjectSelected())
                {
                    reactToChange(true);
                }
                else
                {
                    projectNeedsSlicing = true;
                }
            }
        };
        
        taskProgressChangeListener = (observable, oldValue, newValue) -> 
        {
            selectedTaskProgress.set((double) newValue);
        };
        
        taskRunningChangeListener = (observable, oldValue, newValue) -> 
        {
            selectedTaskRunning.set(newValue);
        };
        
        taskMessageChangeListener = (observable, oldValue, newValue) -> 
        {
            selectedTaskMessage = newValue;
        };
        
        roboxProfileChangeListener = (change) -> {
            while(change.next())
            {
                if(change.wasAdded())
                {
                    RoboxProfile savedProfile = change.getAddedSubList().get(0);
                    String currentCustomSettings = project.getPrinterSettings().duplicate().getSettingsName();
                    if (savedProfile.getName().equals(currentCustomSettings))
                    {
                        purgeAllTasks();
                    }
                }
            }
        };
        
        Lookup.getUserPreferences().getSlicerTypeProperty().addListener((observable, oldValue, newValue) -> {
            reactToChange(true);
        });
        
        ApplicationStatus.getInstance().modeProperty().addListener(applicationModeChangeListener);
        Lookup.getSelectedPrinterProperty().addListener(selectedPrinterReactionChangeListener);
        BaseLookup.getPrinterListChangesNotifier().addListener(printerListChangesListener);
        RoboxProfileSettingsContainer.getInstance().addProfileChangeListener(roboxProfileChangeListener);
    }
    
    public Optional<GCodeGeneratorResult> getPrepResult(PrintQualityEnumeration quality)
    {
        Future<GCodeGeneratorResult> resultFuture = null;
        
        // This is effectively locked if we are in the middle of restarting the tasks.
        // It makes sure the map has had a chance to be refilled by the newely restarted tasks.
        taskMapLock.lock();
        try
        {
            resultFuture = taskMap.get(quality);
        } finally
        {
            taskMapLock.unlock();
        }
        
        if (resultFuture != null)
        {
            try 
            {
                GCodeGeneratorResult result = resultFuture.get();
                return Optional.ofNullable(result);
            }
            catch (InterruptedException ex)
            {
                STENO.debug("Thread interrupted, usually the case when the user has selected different profile settings");
            }
            catch (CancellationException ex) 
            {
                STENO.debug("Thread cancelled, usually the case when the user has started a print then cancelled during slicing");
            }
            catch (ExecutionException ex)
            {
                STENO.exception("Unexpected error when fetching GCodeGeneratorResult", ex);
            }
        }
        return Optional.empty();
    }
    
    private void toggleDataChanged()
    {
        dataChanged.set(dataChanged.not().get());
    }

    public void setSuppressReaction(boolean flag)
    {
        suppressReaction = flag;
    }

    
    
    public void purgeAllTasks()
    {
        if (cancellable != null)
        {
            cancellable.cancelled().set(true);
        }

        if (restartTask != null)
        {
            restartTask.cancel(true);
            restartTask = null;
        }

        cancelPrintOrSaveTask();

        observableTaskMap.forEach((q, t) -> t.cancel(true));
        observableTaskMap.clear();

        projectNeedsSlicing = true;
    }

    private void restartAllTasks()
    {
        purgeAllTasks();
        Runnable restartTasks = () ->
        {                        
            try
            {
                // We sleep here so that any rapid changes to print settings 
                // aren't setting off the slicer.
                Thread.sleep(500);
            } catch (InterruptedException ex)
            {
                return;
            }
            if (cancellable.cancelled().get())
                return;
            
            selectedTaskReBound = false;
            
            slicingOrder.forEach(printQuality ->
            {
                if (modelIsSuitable(printQuality))
                {
                    String headType = HeadContainer.defaultHeadID;
                    SlicerType slicerType = Lookup.getUserPreferences().getSlicerType();

                    if (currentPrinter != null && currentPrinter.headProperty().get() != null)
                    {
                        headType = currentPrinter.headProperty().get().typeCodeProperty().get();
                    }

                    PrinterSettingsOverrides printerSettingsOverrides = project.getPrinterSettings().duplicate();
                    printerSettingsOverrides.setPrintQuality(printQuality);
                    RoboxProfile profileSettings = printerSettingsOverrides.getSettings(headType, slicerType);
                    if (profileSettings != null)
                    {
                        GCodeGeneratorTask prepTask = new GCodeGeneratorTask();
                        Supplier<PrintableMeshes> meshSupplier = () ->
                        {
                            List<MeshForProcessing> meshesForProcessing = new ArrayList<>();
                            List<Integer> extruderForModel = new ArrayList<>();

                            // Only to be run on a ModelContainerProject
                            if(project instanceof ModelContainerProject)
                            {
                                project.getTopLevelThings().forEach((modelContainer) -> 
                                {
                                    ((ModelContainer)modelContainer).getModelsHoldingMeshViews().forEach((modelContainerWithMesh) ->
                                    {
                                        MeshForProcessing meshForProcessing = new MeshForProcessing(modelContainerWithMesh.getMeshView(), modelContainerWithMesh);
                                        meshesForProcessing.add(meshForProcessing);
                                        extruderForModel.add(modelContainerWithMesh.getAssociateWithExtruderNumberProperty().get());
                                    });
                                });
                            }

                            // We need to tell the slicers where the centre of the printed objects is - otherwise everything is put in the centre of the bed...
                            CentreCalculations centreCalc = new CentreCalculations();

                            project.getTopLevelThings().forEach(model ->
                            {
                                Bounds modelBounds = model.getBoundsInParent();
                                centreCalc.processPoint(modelBounds.getMinX(), modelBounds.getMinY(), modelBounds.getMinZ());
                                centreCalc.processPoint(modelBounds.getMaxX(), modelBounds.getMaxY(), modelBounds.getMaxZ());
                            });

                            Vector3D centreOfPrintedObject = centreCalc.getResult();

                            TimelapseSettingsData timelapseSettings = project.getTimelapseSettings();
                            CameraTriggerData cameraTriggerData = null;
                            
                            if (currentPrinter != null &&
                                currentPrinter.getCommandInterface() instanceof RoboxRemoteCommandInterface &&
                                timelapseSettings.isTimelapseEnabled())
                            {
                                Optional<CameraTriggerData> ctd = timelapseSettings.getTimelapseProfile()
                                    .map((profile) -> { return new CameraTriggerData(profile.isHeadLightOff(),
                                                                                     profile.isAmbientLightOff(),
                                                                                     profile.isMoveBeforeCapture(),
                                                                                     profile.getMoveToX(),
                                                                                     profile.getMoveToY());
                                                      });
                                // Clunky adaption to existing interface -
                                // ideally, the optional would be passed into PrintableMeshes.
                                if (ctd.isPresent())
                                    cameraTriggerData = ctd.get();
                            }

                            return new PrintableMeshes(
                                    meshesForProcessing,
                                    project.getUsedExtruders(currentPrinter),
                                    extruderForModel,
                                    project.getProjectName(),
                                    project.getProjectName(),
                                    profileSettings,
                                    printerSettingsOverrides,
                                    printQuality,
                                    slicerType,
                                    centreOfPrintedObject,
                                    Lookup.getUserPreferences().isSafetyFeaturesOn(),
                                    cameraTriggerData != null,
                                    cameraTriggerData);
                        };

                        if (cancellable.cancelled().get())
                            return;
                        observableTaskMap.put(printQuality, prepTask);
                        tidyProjectDirectory(getGCodeDirectory(printQuality));
                        prepTask.initialise(currentPrinter, meshSupplier, getGCodeDirectory(printQuality));
                        slicingExecutorService.execute(prepTask);
                        if (!selectedTaskReBound)
                        {
                            selectedTaskReBound = true;
                            BaseLookup.getTaskExecutor().runOnGUIThread(() -> {bindToSelectedTask(printQuality);});
                        }
                    }
                    toggleDataChanged();
                }
            });
        };

        cancellable = new SimpleCancellable();
        restartTask = slicingExecutorService.submit(restartTasks);
    }
    
    public String getGCodeDirectory(PrintQualityEnumeration printQuality)
    {
        String directoryName = ApplicationConfiguration.getProjectDirectory()
                + project.getProjectName() 
                + File.separator 
                + printQuality.getFriendlyName()
                + File.separator;
        File dirHandle = new File(directoryName);

        if (!dirHandle.exists())
        {
            dirHandle.mkdirs();
        }
        return directoryName;
    }
    
    private void tidyProjectDirectory(String directoryName)
    {
        File projectDirectory = new File(directoryName);
        File[] filesOnDisk = projectDirectory.listFiles();
        if (filesOnDisk != null)
        {
            for (int i = 0; i < filesOnDisk.length; ++i)
            {
                FileUtils.deleteQuietly(filesOnDisk[i]);
            }
        }
    }
    
    private void reactToChange(boolean globalChange) 
    {
        if ((!suppressReaction && isCurrentProjectSelected()) || globalChange) 
        {
            if (ApplicationStatus.getInstance().modeProperty().get() == ApplicationMode.SETTINGS) 
            {
                restartAllTasks();
                projectNeedsSlicing = false;
            }
            else 
            {
                purgeAllTasks();
            }
        }
    }
    
    public boolean modelIsSuitable()
    {
        return modelIsSuitable(currentPrintQuality.get());
    }
    
    private boolean modelIsSuitable(PrintQualityEnumeration printQuality)
    {
        if (project != null)
        {
            RoboxProfile slicerParameters = null; 
            if (project.getNumberOfProjectifiableElements() > 0)
            {
                String headType = HeadContainer.defaultHeadID;
                if (currentPrinter != null && currentPrinter.headProperty().get() != null)
                {
                    headType = currentPrinter.headProperty().get().typeCodeProperty().get();
                }
                slicerParameters = project.getPrinterSettings().getSettings(headType, Lookup.getUserPreferences().getSlicerType(), printQuality);
            }
            if (slicerParameters != null)
            {
                // NOTE - this needs to change if raft settings in slicermapping.dat is changed                
                // Needed as heads differ in size and will need to adjust print volume for this
                double zReduction = 0.0;
                if (currentPrinter != null && currentPrinter.headProperty().get() != null)
                {
                    zReduction = currentPrinter.headProperty().get().getZReductionProperty().get();
                }
            
                double raftOffset = RoboxProfileUtils.calculateRaftOffset(slicerParameters, getSlicerType());

                for (ProjectifiableThing projectifiableThing : project.getTopLevelThings())
                {
                    if (projectifiableThing instanceof ModelContainer)
                    {
                        ModelContainer modelContainer = (ModelContainer) projectifiableThing;

                        //TODO use settings derived offset values for spiral
                        if (modelContainer.isOffBedProperty().get()
                                || (project.getPrinterSettings().getRaftOverride()
                                && modelContainer.isModelTooHighWithOffset(raftOffset + zReduction))
                                || (project.getPrinterSettings().getSpiralPrintOverride()
                                && modelContainer.isModelTooHighWithOffset(0.5)))
                        {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isCurrentProjectSelected()
    {
        return Lookup.getSelectedProjectProperty().get() == project;
    }
    
    public void changeSlicingOrder(List<PrintQualityEnumeration> slicingOrder) 
    {
        PrintQualityEnumeration oldPrintQuality = this.slicingOrder.get(0);
        PrintQualityEnumeration selectedQuality = slicingOrder.get(0);
        if(oldPrintQuality != selectedQuality)
        {
            // This is to stop odd behaviour when something triggers the slicing order to change, but the order hasn't actually changed.
            cancelPrintOrSaveTask();
        }
        this.slicingOrder = slicingOrder;
        bindToSelectedTask(selectedQuality);
    }
    
    /**
     * Return the {@link GCodeGeneratorTask} attached to the given {@link PrintQualityEnumeration}
     * 
     * @param printQuality
     * @return An Optional task as the map may not contain anything
     */
    private Optional<GCodeGeneratorTask> getTaskFromTaskMap(PrintQualityEnumeration printQuality) 
    {
        Future gCodeGenTask = taskMap.get(printQuality);
        if(gCodeGenTask != null && gCodeGenTask instanceof GCodeGeneratorTask) 
        {
            return Optional.of((GCodeGeneratorTask) gCodeGenTask);
        }
        
        return Optional.empty();
    }
    
    /**
     * Bind the {@link GCodeGeneratorTask} denoted by the selected print quality.
     * Attaches listeners to the Tasks running and progress properties.
     * 
     * @param selectedQuality 
     */
    private void bindToSelectedTask(PrintQualityEnumeration selectedQuality) 
    {
        unbindCurrentTask();
        Optional<GCodeGeneratorTask> potentialSelectedTask = getTaskFromTaskMap(selectedQuality);
        if(potentialSelectedTask.isPresent())
        {
            selectedTask = potentialSelectedTask.get();
            selectedTask.runningProperty().addListener(taskRunningChangeListener);
            selectedTask.progressProperty().addListener(taskProgressChangeListener);
            selectedTask.messageProperty().addListener(taskMessageChangeListener);
            selectedTaskMessage = selectedTask.messageProperty().get();
        }
    }
    
    /**
     * Unbind the currently bound {@link GCodeGeneratorTask} by removing the listeners attached 
     */
    private void unbindCurrentTask()
    {
        if(selectedTask != null) 
        {
            selectedTask.runningProperty().removeListener(taskRunningChangeListener);
            selectedTask.progressProperty().removeListener(taskProgressChangeListener);
            selectedTask.messageProperty().removeListener(taskMessageChangeListener);
        }
    }
    
    public void replaceAndExecutePrintOrSaveTask(Task printOrSaveTask)
    {
        cancelPrintOrSaveTask();
        printOrSaveTaskRunning.bind(printOrSaveTask.runningProperty());
        this.printOrSaveTask = printOrSaveTask;
        printOrSaveExecutorService.execute(printOrSaveTask);
    }
    
    public BooleanProperty printOrSaveTaskRunningProperty()
    {
        return printOrSaveTaskRunning;
    }
    
    public boolean cancelPrintOrSaveTask()
    {
        printOrSaveTaskRunning.unbind();
        printOrSaveTaskRunning.set(false);
        
        if (printOrSaveTask != null)
        {
            return printOrSaveTask.cancel(true);
        }
        
        return false;
    }
    
    public Project getProject()
    {
        return project;
    }
    
    public ObservableMap<PrintQualityEnumeration, Future> getObservableTaskMap() 
    {
        return observableTaskMap;
    }
    
    public Cancellable getCancellable()
    {
        return cancellable;
    }
    
    /**
     * Listen to this property to be notified when the GCode files are being regenerated.
     * 
     * @return
     */
    public ReadOnlyBooleanProperty getDataChangedProperty()
    {
        return dataChanged;
    }
    
    /**
     * This property mirrors the progress of the selected profile task.
     * 
     * @return 
     */
    public final ReadOnlyDoubleProperty selectedTaskProgressProperty() 
    { 
        return selectedTaskProgress;
    }
    
    /**
     * This property mirrors the running property from the selected profile task.
     * 
     * @return 
     */
    public final ReadOnlyBooleanProperty selectedTaskRunningProperty()
    {
        return selectedTaskRunning;
    }

    public ReadOnlyObjectProperty<PrintQualityEnumeration> getPrintQualityProperty()
    {
        return currentPrintQuality;
    }
    
    public String getSelectedTaskMessage() 
    {
        return selectedTaskMessage;
    }

    @Override
    public void whenModelAdded(ProjectifiableThing projectifiableThing)
    {
        reactToChange(false);
    }

    @Override
    public void whenModelsRemoved(Set<ProjectifiableThing> projectifiableThing) 
    {
        reactToChange(false);
    }

    @Override
    public void whenAutoLaidOut() 
    {
        reactToChange(false);
    }

    @Override
    public void whenModelsTransformed(Set<ProjectifiableThing> projectifiableThing)
    {
        reactToChange(false);
    }

    @Override
    public void whenModelChanged(ProjectifiableThing modelContainer, String propertyName) 
    {
        reactToChange(false);
    }

    @Override
    public void whenPrinterSettingsChanged(PrinterSettingsOverrides printerSettings) 
    {
        reactToChange(false);
        if (printerSettings.getPrintQuality() != currentPrintQuality.get())
        {
            currentPrintQuality.set(printerSettings.getPrintQuality());
        }
    }
    
    @Override
    public void whenTimelapseSettingsChanged(TimelapseSettingsData timelapseSettings) {
        reactToChange(false);
    }

    public void shutdown()
    {
        slicingExecutorService.shutdown();
        printOrSaveExecutorService.shutdown(); 
    }
    
    private SlicerType getSlicerType() 
    {
        return Lookup.getUserPreferences().getSlicerType();
    }
}
