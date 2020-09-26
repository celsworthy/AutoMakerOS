package celtech.coreUI.controllers.panels;

import celtech.Lookup;
import celtech.appManager.ApplicationMode;
import celtech.appManager.ApplicationStatus;
import celtech.appManager.ModelContainerProject;
import celtech.appManager.Project;
import celtech.appManager.ShapeContainerProject;
import celtech.configuration.ApplicationConfiguration;
import celtech.coreUI.StandardColours;
import celtech.roboxbase.ApplicationFeature;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.datafileaccessors.FilamentContainer;
import celtech.roboxbase.configuration.datafileaccessors.HeadContainer;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.services.gcodegenerator.StylusGCodeGeneratorResult;
import celtech.roboxbase.services.gcodegenerator.GCodeGeneratorResult;
import celtech.roboxbase.services.slicer.PrintQualityEnumeration;
import celtech.services.gcodepreview.GCodePreviewExecutorService;
import celtech.services.gcodepreview.GCodePreviewTask;
import java.io.File;
import java.util.List;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * FXML Controller class
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class PreviewManager
{
    private final Stenographer steno = StenographerFactory.getStenographer(PreviewManager.class.getName());

    public enum PreviewState
    {
        CLOSED,
        LOADING,
        OPEN,
        SLICE_UNAVAILABLE,
        NOT_SUPPORTED
    }
    
    private ObjectProperty<PreviewState> previewState = new SimpleObjectProperty<>(PreviewState.CLOSED);
    private Project currentProject = null;
    private GCodePreviewExecutorService updateExecutor = new GCodePreviewExecutorService();
    private GCodePreviewExecutorService previewExecutor = new GCodePreviewExecutorService();
    private GCodePreviewTask previewTask = null;
    
    private final ChangeListener<Boolean> previewRunningListener =(observable, wasRunning, isRunning) -> {
        if (wasRunning && !isRunning) {
            removePreview();
        }
    };
    
    private final ChangeListener<Boolean> gCodePrepChangeListener = (observable, oldValue, newValue) -> {
        //steno.info("gCodePrepChangeListener");
        autoStartAndUpdatePreview();
    };

    private final ChangeListener<PrintQualityEnumeration> printQualityChangeListener = (observable, oldValue, newValue) -> {
        //steno.info("printQualityChangeListener");
        autoStartAndUpdatePreview();
    };
    
    private final ChangeListener<ApplicationMode> applicationModeChangeListener = (observable, oldValue, newValue) -> {
        //steno.info("printQualityChangeListener");
        if (newValue == ApplicationMode.SETTINGS)
        {
            autoStartAndUpdatePreview();
        }
    };
    
    public PreviewManager()
    {
        if(BaseConfiguration.isWindows32Bit())
        {
            //steno.info("Setting previewState to NOT_SUPPORTED");
            previewState.set(PreviewState.NOT_SUPPORTED);
        }
        try
        {
            ApplicationStatus.getInstance().modeProperty().addListener(applicationModeChangeListener);
        } catch (Exception ex)
        {
            steno.exception("Unexpected error in PreviewManager constructor", ex);
        }
    }

    public ReadOnlyObjectProperty<PreviewState> previewStateProperty()
    {
        return previewState;
    }

    public void previewAction(ActionEvent event)
    {
        //steno.info("previewAction");
        if(BaseConfiguration.isApplicationFeatureEnabled(ApplicationFeature.GCODE_VISUALISATION)) {
            if(previewState.get() != PreviewState.OPEN)
            {  
                updatePreview();
            }
        }
    }

    public void setProjectAndPrinter(Project project, Printer printer)
    {
        if (currentProject != project)
        {
            if (currentProject != null)
            {
                currentProject.getGCodeGenManager().getDataChangedProperty().removeListener(this.gCodePrepChangeListener);
                if (currentProject instanceof ModelContainerProject)
                    currentProject.getGCodeGenManager().getPrintQualityProperty().removeListener(this.printQualityChangeListener);
            }

            currentProject = project;
            if (currentProject != null)
            {
                currentProject.getGCodeGenManager().getDataChangedProperty().addListener(this.gCodePrepChangeListener);
                if (currentProject instanceof ModelContainerProject)
                    currentProject.getGCodeGenManager().getPrintQualityProperty().addListener(this.printQualityChangeListener);
                
                if (previewState.get() == PreviewState.OPEN ||
                    previewState.get() == PreviewState.LOADING ||
                    previewState.get() == PreviewState.SLICE_UNAVAILABLE)
                {
                    updatePreview();
                }
                else if (previewState.get() != PreviewState.NOT_SUPPORTED)
                {
                    //steno.info("Setting previewState to CLOSED");
                    previewState.set(PreviewState.CLOSED);
                }
            }
            else
            {
                clearPreview();
            }
        }
    }

    public void shutdown()
    {
        removePreview();

        if (currentProject != null )
        {
            currentProject.getGCodeGenManager().getDataChangedProperty().removeListener(this.gCodePrepChangeListener);
            currentProject.getGCodeGenManager().getPrintQualityProperty().removeListener(this.printQualityChangeListener);
        }
        currentProject = null;

        ApplicationStatus.getInstance().modeProperty().removeListener(applicationModeChangeListener);
    }
    
    private boolean modelIsSuitable()
    {
        return (currentProject != null &&
                currentProject.getGCodeGenManager().projectIsSuitable());
    }
   
    private void clearPreview()
    {
        //steno.info("clearPreview");
        if (previewTask != null)
        {
            //steno.info("Clearing preview");
            previewTask.clearGCode();
        }
        //steno.info("clearPreview done");
    }

    // Start and remove preview need to be synchronized so that
    // the previewTask is started/stopped and the variable updated
    // as a single transaction.
    private synchronized void removePreview()
    {
        //steno.info("removingPreview");
        if (previewTask != null)
        {
            previewTask.runningProperty().removeListener(previewRunningListener);
            previewTask.terminatePreview();
            previewState.set(PreviewState.CLOSED);
        }
        previewTask = null;
        //steno.info("removePreview done");
    }
    
    // There are some curious issues with starting the preview.
    // Originally the preview tried open in  specific position relative
    // to the AutoMaker window. To do this, it called displayManager.getNormalisedPreviewRectangle(),
    // which queries some JavaFX nodes. The calling thread was not necessarily the main JavaFX thread.
    // Most of the time this worked, but if the tabs and the forward button were clicked multiple times,
    // particularly during startup, it would sometimes corrupt the internal JavaFX data structures,
    // causing JavaFX to throw exceptions and enter an infinite loop, freezing the GUI.
    //
    // Making startPreview into a FutureTask, running it on the JavaFx thread and waiting for the result produced even
    // wierder symptoms. Clicking on the forward and tab buttons during starup would cause startPreview to be
    // called multiple times but not complete. A preview window would appear but not update. On closing the preview,
    // another would immediately appear. It seems these previews started by the calls to startPreview that did not complete.
    // If all these "phantom" previews were closed, so no more appeared, then opening a preview with the preview button worked.
    // The current code below, which creates the previewTask immediately, and places in a fixed position rather than calling
    // the displayManager, seems to work OK.
    private synchronized void startPreview() {
        //steno.info("startPreview");
        if (previewTask == null)
        {
            String printerType = null;
            Printer printer = Lookup.getSelectedPrinterProperty().get();
            if (printer != null)
                printerType = printer.printerConfigurationProperty().get().getTypeCode();
            String projDirectory = ApplicationConfiguration.getProjectDirectory()
                                       + currentProject.getProjectName(); 
            Rectangle2D nRectangle = new Rectangle2D(0.25, 0.25, 0.5, 0.5);
            previewTask = new GCodePreviewTask(projDirectory, printerType, nRectangle);
            previewTask.runningProperty().addListener(previewRunningListener);
            previewExecutor.runTask(previewTask);
        }
        //steno.info("startPreview done");
    }

    private void autoStartAndUpdatePreview()
    {
        //steno.info("autoStartAndUpdatePreview");
        if (previewState.get() == PreviewState.OPEN ||
            previewState.get() == PreviewState.LOADING ||
            previewState.get() == PreviewState.SLICE_UNAVAILABLE ||
            (Lookup.getUserPreferences().isAutoGCodePreview() &&
             BaseConfiguration.isApplicationFeatureEnabled(ApplicationFeature.GCODE_VISUALISATION)))
        {
            //steno.info("autoStartAndUpdatePreview calling updatePreview");
            updatePreview();
        }
    }

    private void updatePreview()
    {
       boolean modelUnsuitable = !modelIsSuitable();
        if (modelUnsuitable)
        {
            //steno.info("Model unsuitable: setting previewState to SLICE_UNAVAILABLE ...");
            previewState.set(PreviewState.SLICE_UNAVAILABLE);
            //steno.info("... Model unsuitable: clearing preview ...");
            clearPreview();
            //steno.info("... Model unsuitable done");
        }
        else
        {
            Runnable doUpdatePreview = () ->
            {
                // Showing preview preview button.
                //steno.info("Setting previewState to LOADING");
                previewState.set(PreviewState.LOADING);

                //steno.info("Preview is null");
                if (previewTask == null)
                    startPreview();
                else
                    clearPreview();
                steno.debug("Waiting for prep result");
                boolean prepSuccessful = false;
                
                if (currentProject instanceof ModelContainerProject)
                {
                    Optional<GCodeGeneratorResult> resultOpt = currentProject.getGCodeGenManager().getModelPrepResult(currentProject.getPrintQuality());
                    steno.debug("Got prep result - ifPresent() = " + Boolean.toString(resultOpt.isPresent()) + "isSuccess() = " + (resultOpt.isPresent() ? Boolean.toString(resultOpt.get().isSuccess()) : "---"));
                    if (resultOpt.isPresent() && resultOpt.get().isSuccess())
                    {
                        steno.debug("GCodePrepResult = " + resultOpt.get().getPostProcOutputFileName());
                    
                        // Get tool colours.
                        Color t0Colour = StandardColours.ROBOX_BLUE;
                        Color t1Colour = StandardColours.HIGHLIGHT_ORANGE;
                        String printerType = null;
                        String headTypeCode = HeadContainer.defaultHeadID;
                        Printer printer = Lookup.getSelectedPrinterProperty().get();
                        if (printer != null)
                        {
                            printerType = printer.printerConfigurationProperty().get().getTypeCode();

                            Head head = printer.headProperty().get();
                            if (head != null)
                            {
                                headTypeCode = head.typeCodeProperty().get();

                                // Assume we have at least one extruder.
                                Filament filamentInUse;
                                filamentInUse = printer.effectiveFilamentsProperty().get(0);
                                if (filamentInUse != null && filamentInUse != FilamentContainer.UNKNOWN_FILAMENT)
                                {
                                    Color colour = filamentInUse.getDisplayColour();
                                    if (colour != null)
                                        t0Colour = colour;
                                }
                                if (head.headTypeProperty().get() == Head.HeadType.DUAL_MATERIAL_HEAD)
                                {
                                    t1Colour = t0Colour;
                                    t0Colour = Color.ORANGE;
                                    filamentInUse = printer.effectiveFilamentsProperty().get(1);
                                    if (filamentInUse != null && filamentInUse != FilamentContainer.UNKNOWN_FILAMENT)
                                    {
                                        Color colour = filamentInUse.getDisplayColour();
                                        if (colour != null)
                                            t0Colour = colour;
                                    }
                                }
                                else
                                    t1Colour = t0Colour;
                            }
                        }

                        if (previewTask == null)
                            startPreview();
                        else
                            previewTask.setPrinterType(printerType);
                        steno.debug("Loading GCode file = " + resultOpt.get().getPostProcOutputFileName());
                        previewTask.setToolColour(0, t0Colour);
                        previewTask.setToolColour(1, t1Colour);
                        previewTask.setStylusMovesVisible(false);
                        previewTask.loadGCodeFile(resultOpt.get().getPostProcOutputFileName());
                        prepSuccessful = true;
                    }
                }
                else if (currentProject instanceof ShapeContainerProject)
                {
                    Optional<StylusGCodeGeneratorResult> prepResult = currentProject.getGCodeGenManager().getStylusGCodeGenResult();
                    if (prepResult.isPresent() &&  prepResult.get().getResultOK())
                    {
                        // Get tool colours.
                        Color t0Colour = StandardColours.ROBOX_BLUE;
                        Color t1Colour = StandardColours.HIGHLIGHT_ORANGE;
                        String printerType = null;
                        String headTypeCode = HeadContainer.defaultHeadID;
                        Printer printer = Lookup.getSelectedPrinterProperty().get();
                        if (printer != null)
                        {
                            printerType = printer.printerConfigurationProperty().get().getTypeCode();

                            Head head = printer.headProperty().get();
                            if (head != null)
                                headTypeCode = head.typeCodeProperty().get();
                        }

                        if (previewTask == null)
                            startPreview();
                        else
                            previewTask.setPrinterType(printerType);

                        previewTask.setToolColour(0, t0Colour);
                        previewTask.setToolColour(1, t1Colour);
                        previewTask.setStylusMovesVisible(true);
                        previewTask.loadGCodeFile( prepResult.get().getCompensatedOutputFileName());

                        prepSuccessful = true;
                    }
                }
                    
                if (prepSuccessful)
                {    
                    if (Lookup.getUserPreferences().isAutoGCodePreview())
                        previewTask.giveFocus();
                    //steno.info("Setting previewState to OPEN ...");
                    previewState.set(PreviewState.OPEN);
                    //steno.info("... OPEN done");
                }
                else
                {
                    // Failed.
                    //steno.info("Failed - Setting previewState to SLICE_UNAVAILABLE ...");
                    previewState.set(PreviewState.SLICE_UNAVAILABLE);
                    //steno.info("... SLICE_UNAVAILABLE done");
                }
            };

            //steno.info("Cancelling update tasks");
            updateExecutor.cancelTask();
            //steno.info("Running update tasks");
            updateExecutor.runTask(doUpdatePreview);
            //steno.info("done updates");
        }
        //steno.info("Updating preview done");
    }
}
