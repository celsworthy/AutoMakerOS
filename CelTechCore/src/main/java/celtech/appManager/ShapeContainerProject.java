package celtech.appManager;

import celtech.configuration.ApplicationConfiguration;
import celtech.configuration.fileRepresentation.ProjectFile;
import celtech.configuration.fileRepresentation.ShapeContainerProjectFile;
import celtech.modelcontrol.ItemState;
import celtech.modelcontrol.ProjectifiableThing;
import celtech.modelcontrol.RotatableTwoD;
import celtech.roboxbase.configuration.fileRepresentation.PrinterSettingsOverrides;
import celtech.modelcontrol.ShapeContainer;
import celtech.modelcontrol.ShapeGroup;
import celtech.roboxbase.configuration.datafileaccessors.StylusSettingsContainer;
import celtech.roboxbase.configuration.fileRepresentation.StylusSettings;
import celtech.roboxbase.utils.RectangularBounds;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author ianhudson
 */
public class ShapeContainerProject extends Project
{

    private static final Stenographer steno = StenographerFactory.getStenographer(ShapeContainerProject.class.getName());
    private int version = -1;

    private final StylusSettings stylusSettings = new StylusSettings();

    public ShapeContainerProject()
    {
        super();
        List<StylusSettings> settingsList = StylusSettingsContainer.getInstance()
                                                                   .getCompleteSettingsList();
        if (!settingsList.isEmpty())
            stylusSettings.setFrom(settingsList.get(0));
        stylusSettings.getDataChanged().addListener(
        (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
        {
            projectModified();
            fireWhenPrinterSettingsChanged(printerSettings);
        });
    }

    @Override
    protected void initialise()
    {
        setMode(ProjectMode.SVG);
    }

    @Override
    protected void save(String basePath)
    {
        if (topLevelThings.size() > 0)
        {
            try
            {
                ProjectFile projectFile = new ShapeContainerProjectFile();
                projectFile.populateFromProject(this);
                File file = new File(basePath + ApplicationConfiguration.projectFileExtension);
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
                mapper.writeValue(file, projectFile);
                saveModels(basePath + ApplicationConfiguration.projectModelsFileExtension);
            } catch (FileNotFoundException ex)
            {
                steno.exception("Failed to save project state", ex);
            } catch (IOException ex)
            {
                steno.exception(
                        "Couldn't write project state to file for project "
                        + projectNameProperty.get(), ex);
            }
        }

    }

    @Override
    public void addModel(ProjectifiableThing projectifiableThing)
    {
        if (projectifiableThing instanceof ShapeContainer)
        {
            ShapeContainer modelContainer = (ShapeContainer) projectifiableThing;
            topLevelThings.add(modelContainer);
            projectModified();
            fireWhenModelAdded(modelContainer);
        }
    }
    
    public static void saveProject(ModelContainerProject project)
    {
        String basePath = ApplicationConfiguration.getProjectDirectory() + File.separator
                + project.getProjectName();
        project.save(basePath);
    }
    
    public String getProjectLocation() {
        return ApplicationConfiguration.getProjectDirectory() 
                + File.separator
                + projectNameProperty.get() 
                + File.separator;
    }

    private void saveModels(String path) throws IOException
    {
        ObjectOutputStream modelsOutput = new ObjectOutputStream(new FileOutputStream(path));

        Set<ShapeContainer> allShapes = getAllModels().stream()
                                                    .filter((pt) -> (pt instanceof ShapeContainer &&
                                                                     !(pt instanceof ShapeGroup)))
                                                    .map(ShapeContainer.class::cast)
                                                    .collect(Collectors.toSet());
        modelsOutput.writeInt(allShapes.size());
        allShapes.stream()
                .forEach(s ->
                {
                    try
                    {
                        modelsOutput.writeObject(s);
                    }
                    catch (IOException ex)
                    {
                        steno.exception("Failed to save shape  " + Integer.toString(s.getModelId()), ex);
                    }
                });
    }

    private void fireWhenModelAdded(ShapeContainer modelContainer)
    {
        for (ProjectChangesListener projectChangesListener : projectChangesListeners)
        {
            projectChangesListener.whenModelAdded(modelContainer);
        }
    }

    @Override
    public void removeModels(Set<ProjectifiableThing> projectifiableThings)
    {
        Set<ShapeContainer> shapeContainers = (Set) projectifiableThings;

        for (ShapeContainer sContainer : shapeContainers)
        {
            assert sContainer != null;
        }

        topLevelThings.removeAll(shapeContainers);

        projectModified();
        fireWhenModelsRemoved(projectifiableThings);
    }
    
    private void fireWhenModelsRemoved(Set<ProjectifiableThing> projectifiableThings)
    {
        for (ProjectChangesListener projectChangesListener : projectChangesListeners)
        {
            projectChangesListener.whenModelsRemoved(projectifiableThings);
        }
    }

    @Override
    public Set<ProjectifiableThing> getAllModels()
    {
        Set<ProjectifiableThing> allShapes = new HashSet<>();
        List<ShapeContainer> shapesToProcess = topLevelThings.stream()
                                                    .filter((pt) -> (pt instanceof ShapeContainer))
                                                    .map(ShapeContainer.class::cast)
                                                    .collect(Collectors.toList());
        int sIndex = 0;
        while (sIndex < shapesToProcess.size())
        {
            ShapeContainer stp = shapesToProcess.get(sIndex);
            ++sIndex;
            allShapes.add(stp);
            if (stp instanceof ShapeGroup)
                shapesToProcess.addAll(((ShapeGroup)stp).getChildShapeContainers());
        }
        
        return allShapes;
    }

    @Override
    protected void fireWhenModelsTransformed(Set<ProjectifiableThing> projectifiableThings)
    {
        for (ProjectChangesListener projectChangesListener : projectChangesListeners)
        {
            projectChangesListener.whenModelsTransformed(projectifiableThings);
        }
    }

    @Override
    protected void fireWhenPrinterSettingsChanged(PrinterSettingsOverrides printerSettings)
    {
        for (ProjectChangesListener projectChangesListener : projectChangesListeners)
        {
            projectChangesListener.whenPrinterSettingsChanged(printerSettings);
        }
    }

        @Override
    protected void fireWhenTimelapseSettingsChanged(TimelapseSettingsData timelapseSettings)
    {
    }

    @Override
    protected void load(ProjectFile projectFile, String basePath) throws ProjectLoadException
    {
        suppressProjectChanged = true;

        if (projectFile instanceof ShapeContainerProjectFile)
        {
            try
            {
                ShapeContainerProjectFile scpf = (ShapeContainerProjectFile) projectFile;
                version = projectFile.getVersion();
                projectNameProperty.set(projectFile.getProjectName());
                lastModifiedDate.set(projectFile.getLastModifiedDate());
                lastPrintJobID = projectFile.getLastPrintJobID();
                projectNameModified = projectFile.isProjectNameModified();
                loadTimelapseSettings(projectFile);
                loadModels(basePath);
                recreateGroups(scpf.getGroupStructure(), scpf.getGroupState());

            } catch (IOException ex)
            {
                steno.exception("Failed to load project " + basePath, ex);
            } catch (ClassNotFoundException ex)
            {
                steno.exception("Failed to load project " + basePath, ex);
            }
        }

        suppressProjectChanged = false;
    }

    private void loadModels(String basePath) throws IOException, ClassNotFoundException
    {
        FileInputStream fileInputStream = new FileInputStream(basePath
                + ApplicationConfiguration.projectModelsFileExtension);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        ObjectInputStream modelsInput = new ObjectInputStream(bufferedInputStream);
        int numModels = modelsInput.readInt();

        for (int i = 0; i < numModels; i++)
        {
            ShapeContainer modelContainer = (ShapeContainer) modelsInput.readObject();
            addModel(modelContainer);
        }
    }

    public void rotateTurnModels(Set<RotatableTwoD> modelContainers, double rotation)
    {
        for (RotatableTwoD model : modelContainers)
        {
            model.setRotationTurn(rotation);
        }
        projectModified();

        fireWhenModelsTransformed((Set) modelContainers);
    }
    
    public StylusSettings getStylusSettings()
    {
        return stylusSettings;
    }
    
    /**
     * Return the set of those ShapeContainers which are in any group.
     */
    private Set<ShapeContainer> getDescendentShapesInAllGroups()
    {
        Set<ShapeContainer> shapesInGroups = new HashSet<>();
        for (ProjectifiableThing thing : topLevelThings)
        {
            if (thing instanceof ShapeGroup)
            {
                shapesInGroups.addAll(getDescendentShapesInGroup((ShapeGroup) thing));
            }
        }
        return shapesInGroups;
    }

    /**
     * Return the set of those ShapeContainers which are in any group descending
     * from the given group.
     */
    private Set<ShapeContainer> getDescendentShapesInGroup(ShapeGroup sGroup)
    {
        Set<ShapeContainer> shapesInGroups = new HashSet<>();
        for (ShapeContainer shape : sGroup.getChildShapeContainers())
        {
            if (shape instanceof ShapeGroup)
            {
                shapesInGroups.addAll(getDescendentShapesInGroup((ShapeGroup) shape));
            } else
            {
                shapesInGroups.add(shape);
            }
        }
        return shapesInGroups;
    }

    protected void checkNotAlreadyInGroup(Set<ProjectifiableThing> shapeContainers)
    {
        Set<ShapeContainer> shapesAlreadyInGroups = getDescendentShapesInAllGroups();
        for (ProjectifiableThing shape : shapeContainers)
        {
            if (shapesAlreadyInGroups.contains((ShapeContainer)shape))
            {
                throw new RuntimeException("Shape " + shape + " is already in a group");
            }
        }
    }

    /**
     * Create a new group from shapes that are not yet in the project, and add
     * model listeners to all descendent children.
     *
     * @param modelContainers
     * @return
     */
    public ShapeGroup createNewGroupAndAddModelListeners(Set<ProjectifiableThing> things)
    {
        checkNotAlreadyInGroup(things);
        Set<ShapeContainer> shapeContainers = things.stream()
                                                    .filter((model) -> (model instanceof ShapeContainer))
                                                    .map(ShapeContainer.class::cast)
                                                    .collect(Collectors.toSet());
        ShapeGroup sGroup = new ShapeGroup(shapeContainers);
        sGroup.checkOffBed();
        return sGroup;
    }

    public ShapeGroup group(Set<ProjectifiableThing> shapeContainers)
    {
        removeModels(shapeContainers);
        ShapeGroup modelGroup = createNewGroup(shapeContainers);
        addModel(modelGroup);
        return modelGroup;
    }

    public ShapeGroup group(Set<ProjectifiableThing> shapeContainers, int groupModelId)
    {
        checkNotAlreadyInGroup(shapeContainers);
        removeModels(shapeContainers);
        ShapeGroup modelGroup = createNewGroup(shapeContainers, groupModelId);
        addModel(modelGroup);
        return modelGroup;
    }

    /**
     * Create a new group from models that are not yet in the project.
     *
     * @param shapeContainers
     * @param groupModelId
     * @return
     */
    public ShapeGroup createNewGroup(Set<ProjectifiableThing> things, int groupModelId)
    {
        checkNotAlreadyInGroup(things);
        Set<ShapeContainer> shapeContainers = things.stream()
                                                    .filter((model) -> (model instanceof ShapeContainer))
                                                    .map(ShapeContainer.class::cast)
                                                    .collect(Collectors.toSet());
        ShapeGroup modelGroup = new ShapeGroup(shapeContainers, groupModelId);
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
    public ShapeGroup createNewGroup(Set<ProjectifiableThing> things)
    {
        checkNotAlreadyInGroup(things);
        Set<ShapeContainer> shapeContainers = things.stream()
                                                    .filter((model) -> (model instanceof ShapeContainer))
                                                    .map(ShapeContainer.class::cast)
                                                    .collect(Collectors.toSet());
        ShapeGroup sGroup = new ShapeGroup(shapeContainers);
        sGroup.checkOffBed();
        sGroup.notifyScreenExtentsChange();
        return sGroup;
    }

    public void ungroup(Set<? extends ShapeContainer> shapeContainers)
    {
        for (ShapeContainer sContainer : shapeContainers)
        {
            if (sContainer instanceof ShapeGroup)
            {
                ShapeGroup sGroup = (ShapeGroup) sContainer;
                for (ShapeContainer childShapeContainer : sGroup.getChildShapeContainers())
                {
                    RectangularBounds b = childShapeContainer.calculateBoundsInBedCoordinateSystem();
                    addModel(childShapeContainer);
                    childShapeContainer.applyGroupTransformToThis(sGroup, b.getCentreX(), b.getCentreY());
                    //childShapeContainer.translateTo(b.getCentreX(), b.getCentreY());
                    //childShapeContainer.updateLastTransformedBoundsInParent();
                }
                Set<ProjectifiableThing> shapeGroups = new HashSet<>();
                shapeGroups.add(sGroup);
                removeModels(shapeGroups);
                Set<ProjectifiableThing> changedModels = new HashSet<>(sGroup.getChildShapeContainers());
                fireWhenModelsTransformed(changedModels);
            }
        }
    }
    
    private Set<ShapeGroup> getAllShapeGroups()
    {
        Set<ShapeGroup> allShapeGroups = new HashSet<>();
        List<ShapeGroup> groupsToProcess = topLevelThings.stream()
                                                    .filter((pt) -> (pt instanceof ShapeGroup))
                                                    .map(ShapeGroup.class::cast)
                                                    .collect(Collectors.toList());
        int dIndex = 0;
        while (dIndex < groupsToProcess.size())
        {
            ShapeGroup dsg = groupsToProcess.get(dIndex);
            ++dIndex;
            allShapeGroups.add(dsg);
            dsg.getChildShapeContainers()
                .stream()
                .filter((pt) -> (pt instanceof ShapeGroup))
                .map((pt) -> (ShapeGroup)pt)
                .forEach(csg -> groupsToProcess.add(csg));
        }
        return allShapeGroups;
    }
    
    /**
     * Return a Map of child_model_id -> parent_model_id for all model:group and
     * group:group.
     *
     * @return p relationships.
     */
    public Map<Integer, Set<Integer>> getGroupStructure()
    {
        Map<Integer, Set<Integer>> groupStructure = new HashMap<>();
        getAllShapeGroups().stream()
                           .forEach((sg) -> sg.addGroupStructure(groupStructure));
        return groupStructure;
    }

    /**
     * Return a Map of model_id -> state for all models holding models (ie
     * groups).
     *
     * @return
     */
    public Map<Integer, ItemState> getGroupState()
    {
        Map<Integer, ItemState> groupState = new HashMap<>();
        getAllShapeGroups().stream()
                           .forEach((sg) -> groupState.put(sg.getModelId(), sg.getState()));
        return groupState;
    }

    /**
     * Using the group function, reapply the groupings as given by the
     * groupStructure. The first groups to be created must be those containing
     * only non-groups, and then each level of the group hierarchy.<p>
     * First create new groups where all children are already instantiated. Then
     * repeat until no new groups are created.
     * </p>
     *
     * @param groupStructure
     * @param groupStates
     * @throws celtech.appManager.ModelContainerProject.ProjectLoadException
     */
    public void recreateGroups(Map<Integer, Set<Integer>> groupStructure,
            Map<Integer, ItemState> groupStates) throws ProjectLoadException
    {
        int numNewGroups = 0;
        do
        {
            numNewGroups = makeNewGroups(groupStructure, groupStates);
        } while (numNewGroups > 0);
    }

    /**
     * Create groups where all the children are already instantiated, based on
     * the structure and state given in the parameters.
     *
     * @return the number of groups created
     */
    private int makeNewGroups(Map<Integer, Set<Integer>> groupStructure,
            Map<Integer, ItemState> groupStates) throws ProjectLoadException
    {
        int numGroups = 0;
        for (Map.Entry<Integer, Set<Integer>> entry : groupStructure.entrySet())
        {
            if (allShapesInstantiated(entry.getValue()))
            {
                Set<ProjectifiableThing> shapeContainers = getShapeContainersForIds(entry.getValue())
                        .stream()
                        .filter((model) -> (model instanceof ProjectifiableThing))
                        .collect(Collectors.toSet());
                int groupModelId = entry.getKey();
                ShapeGroup group = group(shapeContainers, groupModelId);
                recreateGroupState(group, groupStates);
                numGroups++;
            }
        }
        return numGroups;
    }
    
    /**
     * Return true if loadedModels contains models for all the given modelIds,
     * else return false.
     */
    private boolean allShapesInstantiated(Set<Integer> modelIds)
    {
        for (int modelId : modelIds)
        {
            boolean modelFound = false;
            for (ProjectifiableThing pt : topLevelThings)
            {
                if (pt.getModelId() == modelId)
                {
                    modelFound = true;
                    break;
                }
            }
            if (!modelFound)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Return the set of models for the given set of modelIds.
     *
     * @param modelIds
     * @return
     * @throws celtech.appManager.ModelContainerProject.ProjectLoadException
     */
    public Set<ShapeContainer> getShapeContainersForIds(Set<Integer> modelIds) throws ProjectLoadException
    {
        Set<ShapeContainer> shapeContainers = new HashSet<>();
        for (int modelId : modelIds)
        {
           ShapeContainer sContainer = getShapeContainerForModelId(modelId);
            if (sContainer != null)
            {
                shapeContainers.add(sContainer);
            } else
            {
                throw new ProjectLoadException("unexpected model id when recreating groups");
            }
        }
        return shapeContainers;
    }

    private ShapeContainer getShapeContainerForModelId(int modelId)
    {
        for (ProjectifiableThing pt : topLevelThings)
        {
            if (pt.getModelId() == modelId)
            {
                return (ShapeContainer) pt;
            }
        }
        return null;
    }
    
    /**
     * Update the transforms of the given group as indicated by groupState.
     */
    private void recreateGroupState(ShapeGroup group, Map<Integer, ItemState> groupStates) throws ProjectLoadException
    {
        group.setState(groupStates.get(group.getModelId()));
        group.checkOffBed();
    }
}
