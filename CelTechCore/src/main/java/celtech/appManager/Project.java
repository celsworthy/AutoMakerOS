package celtech.appManager;

import celtech.Lookup;
import celtech.configuration.ApplicationConfiguration;
import celtech.configuration.fileRepresentation.ModelContainerProjectFile;
import celtech.configuration.fileRepresentation.ProjectFile;
import celtech.configuration.fileRepresentation.ProjectFileDeserialiser;
import celtech.configuration.fileRepresentation.ShapeContainerProjectFile;
import celtech.modelcontrol.Groupable;
import celtech.modelcontrol.ItemState;
import celtech.modelcontrol.ModelContainer;
import celtech.modelcontrol.ModelGroup;
import celtech.modelcontrol.ProjectifiableThing;
import celtech.modelcontrol.ResizeableThreeD;
import celtech.modelcontrol.ResizeableTwoD;
import celtech.modelcontrol.ScaleableThreeD;
import celtech.modelcontrol.ScaleableTwoD;
import celtech.modelcontrol.Translateable;
import celtech.modelcontrol.TranslateableTwoD;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.camera.CameraInfo;
import celtech.roboxbase.configuration.SlicerType;
import celtech.roboxbase.configuration.datafileaccessors.CameraProfileContainer;
import celtech.roboxbase.configuration.fileRepresentation.CameraProfile;
import celtech.roboxbase.configuration.fileRepresentation.PrinterSettingsOverrides;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.services.slicer.PrintQualityEnumeration;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public abstract class Project
{

    public static class ProjectLoadException extends Exception
    {

        public ProjectLoadException(String message)
        {
            super(message);
        }
    }

    private int version = -1;

    private static final Stenographer steno = StenographerFactory.getStenographer(Project.class.getName());

    protected Set<ProjectChangesListener> projectChangesListeners;

    protected BooleanProperty canPrint;
    protected BooleanProperty customSettingsNotChosen;

    protected final PrinterSettingsOverrides printerSettings;
    
    protected final TimelapseSettingsData timelapseSettings;

    protected final StringProperty projectNameProperty;
    protected ObjectProperty<Date> lastModifiedDate;

    protected boolean suppressProjectChanged = false;
    protected boolean projectSaved = true;

    protected ObjectProperty<ProjectMode> mode = new SimpleObjectProperty<>(ProjectMode.NONE);

    protected ObservableList<ProjectifiableThing> topLevelThings;

    protected String lastPrintJobID = "";
    
    protected boolean projectNameModified = false;
    
    private GCodeGeneratorManager gCodeGenManager;

    public Project()
    {
        topLevelThings = FXCollections.observableArrayList();

        initialise();

        canPrint = new SimpleBooleanProperty(true);
        customSettingsNotChosen = new SimpleBooleanProperty(true);
        lastModifiedDate = new SimpleObjectProperty<>();
        projectChangesListeners = new HashSet<>();

        printerSettings = new PrinterSettingsOverrides();
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("-hhmmss-ddMMYY");
        projectNameProperty = new SimpleStringProperty(Lookup.i18n("projectLoader.untitled")
                + formatter.format(now));
        lastModifiedDate.set(now);

        gCodeGenManager = new GCodeGeneratorManager(this);
        
        customSettingsNotChosen.bind(
                printerSettings.printQualityProperty().isEqualTo(PrintQualityEnumeration.CUSTOM)
                .and(printerSettings.getSettingsNameProperty().isEmpty()));
        // Cannot print if quality is CUSTOM and no custom settings have been chosen
        canPrint.bind(customSettingsNotChosen.not().and(gCodeGenManager.printOrSaveTaskRunningProperty().not()));

        printerSettings.getDataChanged().addListener(
                (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
                {
                    projectModified();
                    fireWhenPrinterSettingsChanged(printerSettings);
                });

        timelapseSettings = new TimelapseSettingsData();
        timelapseSettings.getDataChanged().addListener(
                (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
                {
                    projectModified();
                    fireWhenTimelapseSettingsChanged(timelapseSettings);
                });

        Lookup.getUserPreferences().getSlicerTypeProperty().addListener(
                (ObservableValue<? extends SlicerType> observable, SlicerType oldValue, SlicerType newValue) ->
                {
                    projectModified();
                });
    }

    protected abstract void initialise();

    public final void setProjectName(String value)
    {
        projectNameProperty.set(value);
    }

    public final String getProjectName()
    {
        return projectNameProperty.get();
    }

    public final StringProperty projectNameProperty()
    {
        return projectNameProperty;
    }

    public final String getAbsolutePath()
    {
        return ApplicationConfiguration.getProjectDirectory() 
                + File.separator
                + getProjectName()
                + File.separator
                + projectNameProperty.get()
                + ApplicationConfiguration.projectFileExtension;
    }

    protected abstract void load(ProjectFile projectFile, String basePath) throws ProjectLoadException;

    public static final Project loadProject(String basePath)
    {
        Project project = null;
        File file = new File(basePath + ApplicationConfiguration.projectFileExtension);

        try
        {
            ProjectFileDeserialiser deserializer
                    = new ProjectFileDeserialiser();
            SimpleModule module
                    = new SimpleModule("LegacyProjectFileDeserialiserModule",
                            new Version(1, 0, 0, null));
            module.addDeserializer(ProjectFile.class, deserializer);

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(module);
            ProjectFile projectFile = mapper.readValue(file, ProjectFile.class);

            if (projectFile instanceof ModelContainerProjectFile)
            {
                project = new ModelContainerProject();
                project.load(projectFile, basePath);
            } else if (projectFile instanceof ShapeContainerProjectFile)
            {
                project = new ShapeContainerProject();
                project.load(projectFile, basePath);
            }
        } catch (Exception ex)
        {
            steno.exception("Unable to load project file at " + basePath, ex);
        }
        return project;
    }

    protected abstract void save(String basePath);

    public static final void saveProject(Project project)
    {
        if (project != null)
        {
            String basePath = ApplicationConfiguration.getProjectDirectory() 
                    + File.separator
                    + project.getProjectName()
                    + File.separator;
            File dirHandle = new File(basePath);
            if (!dirHandle.exists()) {
                dirHandle.mkdirs();
            }
            basePath = basePath + project.getProjectName();
            project.save(basePath);
            project.setProjectSaved(true);
        }
    }

    @Override
    public String toString()
    {
        return projectNameProperty.get();
    }

    public final PrintQualityEnumeration getPrintQuality()
    {
        return printerSettings.getPrintQuality();
    }

    public final void setPrintQuality(PrintQualityEnumeration printQuality)
    {
        if (printerSettings.getPrintQuality() != printQuality)
        {
            projectModified();
            printerSettings.setPrintQuality(printQuality);
        }
    }

    public final PrinterSettingsOverrides getPrinterSettings()
    {
        return printerSettings;
    }

    public final TimelapseSettingsData getTimelapseSettings()
    {
        return timelapseSettings;
    }

    public abstract void addModel(ProjectifiableThing projectifiableThing);

    public abstract void removeModels(Set<ProjectifiableThing> projectifiableThings);

    public final void addProjectChangesListener(ProjectChangesListener projectChangesListener)
    {
        projectChangesListeners.add(projectChangesListener);
    }

    public final void removeProjectChangesListener(ProjectChangesListener projectChangesListener)
    {
        projectChangesListeners.remove(projectChangesListener);
    }

    public final ObjectProperty<Date> getLastModifiedDate()
    {
        return lastModifiedDate;
    }

    public final BooleanProperty canPrintProperty()
    {
        return canPrint;
    }

    public final BooleanProperty customSettingsNotChosenProperty()
    {
        return customSettingsNotChosen;
    }


    public ObservableList<Boolean> getUsedExtruders(Printer printer)
    {
        List<Boolean> localUsedExtruders = new ArrayList<>();
        localUsedExtruders.add(false);
        localUsedExtruders.add(false);
        
        return FXCollections.observableArrayList(localUsedExtruders);
    }
    
    protected void loadTimelapseSettings(ProjectFile pFile) 
    {
        timelapseSettings.setTimelapseTriggerEnabled(pFile.isTimelapseTriggerEnabled());
        String profileName = pFile.getTimelapseProfileName();
        if (profileName.isBlank())
            timelapseSettings.setTimelapseProfile(Optional.empty());
        else {
            timelapseSettings.setTimelapseProfile(Optional.ofNullable(CameraProfileContainer.getInstance().getProfileByName(profileName)));
        }
        String cameraID = pFile.getTimelapseCameraID();
        Optional<CameraInfo> camera = Optional.empty();
        if (!cameraID.isBlank()) {
            String[] fields = cameraID.split(":");
            if (fields.length == 2) {
                String cameraName = fields[0];
                try {
                    int cameraNumber = Integer.parseInt(fields[1]);
                    camera = BaseLookup.getConnectedCameras()
                                       .stream()
                                       .filter(c -> c.getCameraName().equals(cameraName) &&
                                                    c.getCameraNumber() == cameraNumber)
                                       .findFirst();
                }
                catch (NumberFormatException ex) {
                }
            }
        }
        timelapseSettings.setTimelapseCamera(camera);
    }

    /**
     * ProjectChangesListener allows other objects to observe when models are
     * added or removed etc to the project.
     */
    public interface ProjectChangesListener
    {

        /**
         * This should be fired when a model is added to the project.
         *
         * @param projectifiableThing
         */
        void whenModelAdded(ProjectifiableThing projectifiableThing);

        /**
         * This should be fired when a model is removed from the project.
         *
         * @param projectifiableThing
         */
        void whenModelsRemoved(Set<ProjectifiableThing> projectifiableThing);

        /**
         * This should be fired when the project is auto laid out.
         */
        void whenAutoLaidOut();

        /**
         * This should be fired when one or more models have been moved, rotated
         * or scaled etc. If possible try to fire just once for any given group
         * change.
         *
         * @param projectifiableThing
         */
        void whenModelsTransformed(Set<ProjectifiableThing> projectifiableThing);

        /**
         * This should be fired when certain details of the model change.
         * Currently this is only: - associatedExtruder
         *
         * @param modelContainer
         * @param propertyName
         */
        void whenModelChanged(ProjectifiableThing modelContainer, String propertyName);

        /**
         * This should be fired whenever the PrinterSettings of the project
         * changes.
         *
         * @param printerSettings
         */
        void whenPrinterSettingsChanged(PrinterSettingsOverrides printerSettings);

        /**
         * This should be fired whenever the TimelapseSettings of the project
         * changes.
         *
         * @param timelapseSettings
         */
        void whenTimelapseSettingsChanged(TimelapseSettingsData timelapseSettings);
    }

    public abstract void autoLayout();

    //This carries out the same function but leaves the existing things in place
    public abstract void autoLayout(List<ProjectifiableThing> thingsToLayout);

    /**
     * Scale X, Y and Z by the given factor, apply the given ratio to the given
     * scale. I.e. the ratio is not an absolute figure to be applied to the
     * models but a ratio to be applied to the current scale.
     *
     * @param projectifiableThings
     * @param ratio
     */
    public final void scaleXYZRatioSelection(Set<ScaleableThreeD> projectifiableThings, double ratio)
    {
        for (ScaleableThreeD projectifiableThing : projectifiableThings)
        {
            projectifiableThing.setXScale(projectifiableThing.getXScale() * ratio, true);
            projectifiableThing.setYScale(projectifiableThing.getYScale() * ratio, true);
            projectifiableThing.setZScale(projectifiableThing.getZScale() * ratio, true);
        }
        projectModified();
        fireWhenModelsTransformed((Set) projectifiableThings);
    }

    /**
     * Scale X, Y and Z by the given factor, apply the given ratio to the given
     * scale. I.e. the ratio is not an absolute figure to be applied to the
     * models but a ratio to be applied to the current scale.
     *
     * @param projectifiableThings
     * @param ratio
     */
    public final void scaleXYRatioSelection(Set<ScaleableTwoD> projectifiableThings, double ratio)
    {
        for (ScaleableTwoD projectifiableThing : projectifiableThings)
        {
            projectifiableThing.setXScale(projectifiableThing.getXScale() * ratio, true);
            projectifiableThing.setYScale(projectifiableThing.getYScale() * ratio, true);
        }
        projectModified();
        fireWhenModelsTransformed((Set) projectifiableThings);
    }

    public final void scaleXModels(Set<ScaleableTwoD> projectifiableThings, double newScale,
            boolean preserveAspectRatio)
    {
        if (preserveAspectRatio)
        {
            // this only happens for non-multiselect
            assert (projectifiableThings.size() == 1);
            ScaleableTwoD projectifiableThing = projectifiableThings.iterator().next();
            double ratio = newScale / projectifiableThing.getXScale();
            if (projectifiableThing instanceof ScaleableThreeD)
            {
                scaleXYZRatioSelection((Set) projectifiableThings, ratio);
            } else
            {
                scaleXYRatioSelection(projectifiableThings, ratio);
            }
        } else
        {
            for (ScaleableTwoD projectifiableThing : projectifiableThings)
            {
                {
                    projectifiableThing.setXScale(newScale, true);
                }
            }
        }
        projectModified();
        fireWhenModelsTransformed((Set) projectifiableThings);
    }

    public final void scaleYModels(Set<ScaleableTwoD> projectifiableThings, double newScale,
            boolean preserveAspectRatio)
    {
        if (preserveAspectRatio)
        {
            // this only happens for non-multiselect
            assert (projectifiableThings.size() == 1);
            ScaleableTwoD projectifiableThing = projectifiableThings.iterator().next();
            double ratio = newScale / projectifiableThing.getYScale();

            if (projectifiableThing instanceof ScaleableThreeD)
            {
                scaleXYZRatioSelection((Set) projectifiableThings, ratio);
            } else
            {
                scaleXYRatioSelection(projectifiableThings, ratio);
            }
        } else
        {
            for (ScaleableTwoD projectifiableThing : projectifiableThings)
            {
                {
                    projectifiableThing.setYScale(newScale, true);
                }
            }
        }
        projectModified();
        fireWhenModelsTransformed((Set) projectifiableThings);
    }

    public final void scaleZModels(Set<ScaleableThreeD> projectifiableThings, double newScale,
            boolean preserveAspectRatio)
    {
        if (preserveAspectRatio)
        {
            // this only happens for non-multiselect
            assert (projectifiableThings.size() == 1);
            ScaleableThreeD projectifiableThing = projectifiableThings.iterator().next();
            double ratio = newScale / projectifiableThing.getZScale();
            scaleXYZRatioSelection(projectifiableThings, ratio);
        } else
        {
            for (ScaleableThreeD projectifiableThing : projectifiableThings)
            {
                {
                    projectifiableThing.setZScale(newScale, true);
                }
            }
        }
        projectModified();
        fireWhenModelsTransformed((Set) projectifiableThings);
    }

    public void translateModelsBy(Set<TranslateableTwoD> modelContainers, double x, double y)
    {
        for (TranslateableTwoD model : modelContainers)
        {
            model.translateBy(x, y);
        }
        projectModified();

        fireWhenModelsTransformed((Set) modelContainers);
    }

    public void translateModelsTo(Set<TranslateableTwoD> modelContainers, double x, double y)
    {
        for (TranslateableTwoD model : modelContainers)
        {
            model.translateTo(x, y);
        }
        projectModified();
        fireWhenModelsTransformed((Set) modelContainers);
    }

    public void translateModelsXTo(Set<TranslateableTwoD> modelContainers, double x)
    {
        for (TranslateableTwoD model : modelContainers)
        {
            model.translateXTo(x);
        }
        projectModified();

        fireWhenModelsTransformed((Set) modelContainers);
    }

    public void translateModelsDepthPositionTo(Set<Translateable> modelContainers, double z)
    {
        for (Translateable model : modelContainers)
        {
            model.translateDepthPositionTo(z);
        }
        projectModified();

        fireWhenModelsTransformed((Set) modelContainers);
    }

    public void resizeModelsDepth(Set<ResizeableThreeD> modelContainers, double depth)
    {
        for (ResizeableThreeD model : modelContainers)
        {
            model.resizeDepth(depth);
        }
        projectModified();

        fireWhenModelsTransformed((Set) modelContainers);
    }

    public void resizeModelsHeight(Set<ResizeableTwoD> modelContainers, double height)
    {
        for (ResizeableTwoD model : modelContainers)
        {
            model.resizeHeight(height);
        }
        projectModified();

        fireWhenModelsTransformed((Set) modelContainers);
    }

    public void resizeModelsWidth(Set<ResizeableTwoD> modelContainers, double width)
    {
        for (ResizeableTwoD model : modelContainers)
        {
            model.resizeWidth(width);
        }
        projectModified();

        fireWhenModelsTransformed((Set) modelContainers);
    }

    public abstract Set<ProjectifiableThing> getAllModels();

    public final Set<ItemState> getModelStates()
    {
        Set<ItemState> states = new HashSet<>();
        for (ProjectifiableThing model : getAllModels())
        {
            states.add(model.getState());
        }
        return states;
    }

    public final void setModelStates(Set<ItemState> modelStates)
    {
        Set<ProjectifiableThing> modelContainers = new HashSet<>();
        for (ItemState modelState : modelStates)
        {
            for (ProjectifiableThing model : getAllModels())
            {
                if (model.getModelId() == modelState.modelId)
                {
                    model.setState(modelState);
                    model.updateOriginalModelBounds();
                    modelContainers.add(model);
                }
            }
        }
        projectModified();
        fireWhenModelsTransformed(modelContainers);
    }

    public final ReadOnlyObjectProperty<ProjectMode> getModeProperty()
    {
        return mode;
    }

    public ProjectMode getMode()
    {
        return mode.get();
    }

    public final void setMode(ProjectMode mode)
    {
        this.mode.set(mode);
    }

    protected final void projectModified()
    {
        if (!suppressProjectChanged)
        {
            projectSaved = false;
            lastPrintJobID = "";
            lastModifiedDate.set(new Date());
        }
    }

    abstract protected void fireWhenModelsTransformed(Set<ProjectifiableThing> projectifiableThings);

    abstract protected void fireWhenPrinterSettingsChanged(PrinterSettingsOverrides printerSettings);

    abstract protected void fireWhenTimelapseSettingsChanged(TimelapseSettingsData timelapseSettings);

    public int getNumberOfProjectifiableElements()
    {
        return getAllModels().size();
    }

    public ObservableList<ProjectifiableThing> getTopLevelThings()
    {
        return topLevelThings;
    }

    public void setLastPrintJobID(String lastPrintJobID)
    {
        this.lastPrintJobID = lastPrintJobID;
    }

    public String getLastPrintJobID()
    {
        return lastPrintJobID;
    }

    public boolean isProjectNameModified()
    {
        return projectNameModified;
    }

    public void setProjectNameModified(boolean projectNameModified)
    {
        this.projectNameModified = projectNameModified;
    }

    public ModelGroup group(Set<Groupable> modelContainers)
    {
        Set<ProjectifiableThing> projectifiableThings = (Set) modelContainers;

        removeModels(projectifiableThings);
        ModelGroup modelGroup = createNewGroup(modelContainers);
        addModel(modelGroup);
        return modelGroup;
    }

    public ModelGroup group(Set<Groupable> modelContainers, int groupModelId)
    {
        Set<ProjectifiableThing> projectifiableThings = (Set) modelContainers;

        removeModels(projectifiableThings);
        ModelGroup modelGroup = createNewGroup(modelContainers, groupModelId);
        addModel(modelGroup);
        return modelGroup;
    }

    /**
     * Create a new group from models that are not yet in the project.
     *
     * @param modelContainers
     * @param groupModelId
     * @return
     */
    public ModelGroup createNewGroup(Set<Groupable> modelContainers, int groupModelId)
    {
        checkNotAlreadyInGroup(modelContainers);
        ModelGroup modelGroup = new ModelGroup((Set) modelContainers, groupModelId);
        modelGroup.checkOffBed();
        modelGroup.notifyScreenExtentsChange();
        return modelGroup;
    }

    /**
     * Create a new group from models that are not yet in the project.
     *
     * @param modelContainers
     * @return
     */
    public ModelGroup createNewGroup(Set<Groupable> modelContainers)
    {
        checkNotAlreadyInGroup(modelContainers);

        ModelGroup modelGroup = new ModelGroup((Set) modelContainers);
        modelGroup.checkOffBed();
        modelGroup.notifyScreenExtentsChange();
        return modelGroup;
    }

    public void ungroup(Set<? extends ModelContainer> modelContainers)
    {
        List<ProjectifiableThing> ungroupedModels = new ArrayList<>();

        for (ModelContainer modelContainer : modelContainers)
        {
            if (modelContainer instanceof ModelGroup)
            {
                ModelGroup modelGroup = (ModelGroup) modelContainer;
                Set<ProjectifiableThing> modelGroups = new HashSet<>();
                modelGroups.add(modelGroup);
                removeModels(modelGroups);
                for (ModelContainer childModelContainer : modelGroup.getChildModelContainers())
                {
                    addModel(childModelContainer);
                    childModelContainer.setBedCentreOffsetTransform();
                    childModelContainer.applyGroupTransformToThis(modelGroup);
                    childModelContainer.updateLastTransformedBoundsInParent();
                    ungroupedModels.add(childModelContainer);
                }
                Set<ProjectifiableThing> changedModels = new HashSet<>(modelGroup.getChildModelContainers());
                fireWhenModelsTransformed(changedModels);
            }
        }
    }

    protected abstract void checkNotAlreadyInGroup(Set<Groupable> modelContainers);

    /**
     * Create a new group from models that are not yet in the project, and add
     * model listeners to all descendent children.
     *
     * @param modelContainers
     * @return
     */
    public abstract ModelGroup createNewGroupAndAddModelListeners(Set<Groupable> modelContainers);

    @JsonIgnore
    public void invalidate()
    {
        projectModified();
    }
    
    public GCodeGeneratorManager getGCodeGenManager()
    {
        return gCodeGenManager;
    }
    
    public void close()
    {
        gCodeGenManager.shutdown();
    }
    
    public boolean isProjectSaved()
    {
        return projectSaved;
    }
    
    public void setProjectSaved(boolean projectSaved)
    {
        this.projectSaved = projectSaved;
    }
}
