/*
 * Copyright 2015 CEL UK
 */
package celtech.appManager.undo;

import celtech.Lookup;
import celtech.appManager.ModelContainerProject;
import celtech.modelcontrol.Groupable;
import celtech.modelcontrol.ItemState;
import celtech.modelcontrol.ModelContainer;
import celtech.modelcontrol.ModelGroup;
import celtech.modelcontrol.ProjectifiableThing;
import celtech.roboxbase.BaseLookup;
import celtech.utils.threed.MeshCutter2;
import celtech.utils.threed.MeshDebug;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author tony
 */
public class CutCommand extends Command
{

    private final Stenographer steno = StenographerFactory.getStenographer(
            CutCommand.class.getName());

    /**
     * Cuts for groups have to be treated differently to cuts of single models
     * due to the requirement that Undo must reconstitute any previous hierarchy
     * of grouping that existed before the cut.
     */
    class GroupingOperation
    {

        ModelGroup originalGroup;
        Set<Groupable> modelsForTopGroup;
        Set<Groupable> modelsForBottomGroup;
        Set<ModelContainer> modelsToRemoveFromProject;
        Set<ModelContainer> modelsToAddToProject;
        ModelGroup topGroup;
        ModelGroup bottomGroup;

        public GroupingOperation(ModelGroup originalGroup, Set<Groupable> modelsForTopGroup,
                Set<Groupable> modelsForBottomGroup, Set<ModelContainer> modelsToRemoveFromProject,
                Set<ModelContainer> modelsToAddToProject)
        {
            this.originalGroup = originalGroup;
            this.modelsForTopGroup = modelsForTopGroup;
            this.modelsForBottomGroup = modelsForBottomGroup;
            this.modelsToRemoveFromProject = modelsToRemoveFromProject;
            this.modelsToAddToProject = modelsToAddToProject;
        }

        void undo()
        {

            for (ModelContainer modelContainer : modelsToRemoveFromProject)
            {
                project.addModel(modelContainer);
            }

            Set<ModelGroup> groups = new HashSet<>();

            if (topGroup != null)
            {
                groups.add(topGroup);
            }
            if (bottomGroup != null)
            {
                groups.add(bottomGroup);
            }
            project.ungroup(groups);

            Set<ProjectifiableThing> projectifiableThings = (Set) modelsToAddToProject;
            project.removeModels(projectifiableThings);
        }

        void redo()
        {

            ungroupAllDescendentModelGroups(originalGroup);

            Set<ProjectifiableThing> projectifiableThings = (Set) modelsToRemoveFromProject;
            project.removeModels(projectifiableThings);
            for (ModelContainer modelContainer : modelsToAddToProject)
            {
                project.addModel(modelContainer);
            }

            if (!modelsForTopGroup.isEmpty())
            {
                topGroup = project.group(modelsForTopGroup);
                topGroup.setState(originalGroup.getState());
                topGroup.moveToCentre();
                topGroup.dropToBed();
                topGroup.translateBy(-10, -10);
            } else
            {
                topGroup = null;
            }

            if (!modelsForBottomGroup.isEmpty())
            {
                bottomGroup = project.group(modelsForBottomGroup);
                bottomGroup.setState(originalGroup.getState());
                bottomGroup.moveToCentre();
                bottomGroup.dropToBed();
                bottomGroup.translateBy(10, 10);
            } else
            {
                bottomGroup = null;
            }
        }

    }

    private Set<GroupingOperation> groupingOperations;

    final ModelContainerProject project;
    final float cutHeightValue;
    final Set<ModelContainer> modelContainers;

    /**
     * The newly created cut parts that need to be added to the project.
     */
    Set<ModelContainer> createdModelContainers;
    /**
     * The models that were cut into two and should be removed.
     */
    Set<ModelContainer> modelsToRemoveFromProject;

    boolean cutWorked = false;

    public CutCommand(ModelContainerProject project, Set<ModelContainer> modelContainers, float cutHeightValue)
    {
        this.project = project;
        this.cutHeightValue = cutHeightValue;
        this.modelContainers = modelContainers;
    }

    @Override
    public void do_()
    {
        redo();
    }

    @Override
    public void undo()
    {

        if (cutWorked)
        {
            Set<ProjectifiableThing> projectifiableThings = (Set) createdModelContainers;
            project.removeModels(projectifiableThings);

            for (ModelContainer modelContainer : modelsToRemoveFromProject)
            {
                project.addModel(modelContainer);
            }

            for (GroupingOperation groupingOperation : groupingOperations)
            {
                groupingOperation.undo();
            }

            try
            {
                project.recreateGroups(groupStructure, groupState);
            } catch (ModelContainerProject.ProjectLoadException ex)
            {
                steno.exception("Error undoing group cut", ex);
            }
        }
    }

    @Override
    public void redo()
    {

        groupingOperations = new HashSet<>();
        createdModelContainers = new HashSet<>();
        modelsToRemoveFromProject = new HashSet<>();
        groupState = project.getGroupState();
        groupStructure = project.getGroupStructure();

        multiCut(modelContainers, cutHeightValue);

        if (cutWorked)
        {
            for (ModelContainer modelContainer : createdModelContainers)
            {
                project.addModel(modelContainer);
            }

            Set<ProjectifiableThing> projectifiableThings = (Set) modelsToRemoveFromProject;
            project.removeModels(projectifiableThings);

            for (GroupingOperation groupingOperation : groupingOperations)
            {
                groupingOperation.redo();
            }
        }
    }

    private void multiCut(Set<ModelContainer> modelContainers, float cutHeightValue)
    {
        try
        {
            for (ModelContainer modelContainer : modelContainers)
            {
                cut(modelContainer, cutHeightValue);
            }

        } catch (Exception ex)
        {
            cutWorked = false;
            steno.exception("an error occurred during cutting ", ex);
            BaseLookup.getSystemNotificationHandler().showErrorNotification(
                    Lookup.i18n("cutOperation.title"), Lookup.i18n("cutOperation.message"));
            return;
        }
        cutWorked = true;
    }

    private void cut(ModelContainer modelContainer, float cutHeightValue)
    {

        if (modelContainer instanceof ModelGroup)
        {
            cutGroup((ModelGroup) modelContainer, cutHeightValue);
        } else
        {
            cutSingleModel(modelContainer, cutHeightValue);
        }
        return;
    }

    private void cutSingleModel(ModelContainer modelContainer, float cutHeightValue)
    {
        List<ModelContainer> childModelContainers = new ArrayList<>();

        List<Optional<ModelContainer>> modelContainerPair = cutModelContainerAtHeight(modelContainer,
                cutHeightValue);
        Optional<ModelContainer> topModelContainer = modelContainerPair.get(0);
        Optional<ModelContainer> bottomModelContainer = modelContainerPair.get(1);

        boolean cutTookPlace = false;
        if (bottomModelContainer.isPresent() && topModelContainer.isPresent())
        {
            cutTookPlace = true;
        }

        if (bottomModelContainer.isPresent())
        {
            if (cutTookPlace)
            {
                bottomModelContainer.get().moveToCentre();
                bottomModelContainer.get().dropToBed();
                bottomModelContainer.get().translateBy(-10, -10);
            }
            childModelContainers.add(bottomModelContainer.get());
        }
        if (topModelContainer.isPresent())
        {
            if (cutTookPlace)
            {
                topModelContainer.get().moveToCentre();
                topModelContainer.get().dropToBed();
                topModelContainer.get().translateBy(10, 10);
            }
            childModelContainers.add(topModelContainer.get());
        }
        /**
         * The cut can just be the original model, we don't add to
         * childModelContainers in that case.
         */
        if (!childModelContainers.contains(modelContainer))
        {
            createdModelContainers.addAll(childModelContainers);
            modelsToRemoveFromProject.add(modelContainer);
        }
    }

    Map<Integer, ItemState> groupState;
    Map<Integer, Set<Integer>> groupStructure;

    private void cutGroup(ModelGroup modelGroup, float cutHeightValue)
    {

        Set<ModelContainer> modelsToRemoveFromProject = new HashSet<>();
        Set<ModelContainer> modelsToAddToProject = new HashSet<>();

        Set<Groupable> topModelContainers = new HashSet<>();
        Set<Groupable> bottomModelContainers = new HashSet<>();

        Set<ModelContainer> allMeshViews = modelGroup.getModelsHoldingMeshViews();

        for (ModelContainer descendentModelContainer : allMeshViews)
        {
            List<Optional<ModelContainer>> modelContainerPair = cutModelContainerAtHeight(
                    descendentModelContainer,
                    cutHeightValue);
            if (modelContainerPair.get(0).isPresent())
            {
                topModelContainers.add(modelContainerPair.get(0).get());
            }
            if (modelContainerPair.get(1).isPresent())
            {
                bottomModelContainers.add(modelContainerPair.get(1).get());
            }
            if (modelContainerPair.get(0).isPresent() && modelContainerPair.get(1).isPresent())
            {
                // this part was cut
                modelsToRemoveFromProject.add(descendentModelContainer);
                modelsToAddToProject.add(modelContainerPair.get(0).get());
                modelsToAddToProject.add(modelContainerPair.get(1).get());
            }
        }

        GroupingOperation groupingOperation = new GroupingOperation(modelGroup, topModelContainers,
                bottomModelContainers,
                modelsToRemoveFromProject,
                modelsToAddToProject);

        groupingOperations.add(groupingOperation);
    }

    private List<Optional<ModelContainer>> cutModelContainerAtHeight(ModelContainer modelContainer,
            float cutHeight)
    {
        List<Optional<ModelContainer>> modelContainerPair = new ArrayList<>();

        /**
         * First check for the case where the cutting plane is entirely above or
         * below the model.
         */
        List<Float> limits = modelContainer.getMaxAndMinYInBedCoords();
        float maxHeight = limits.get(0);
        float minHeight = limits.get(1);
        if (cutHeight <= minHeight)
        {
            modelContainerPair.add(Optional.empty());
            modelContainerPair.add(Optional.of(modelContainer));

            return modelContainerPair;
        } else if (cutHeight >= maxHeight)
        {
            modelContainerPair.add(Optional.of(modelContainer));
            modelContainerPair.add(Optional.empty());

            return modelContainerPair;
        }

        cutHeight -= modelContainer.getYAdjust();

        //these transforms must be cleared so that bedToLocal conversions work properly in the cutter.
        modelContainer.saveAndClearBedTransform();
        modelContainer.saveAndClearDropToBedYTransform();

        try
        {
            List<TriangleMesh> meshPair = MeshCutter2.cut(
                    (TriangleMesh) modelContainer.getMeshView().getMesh(),
                    cutHeight, modelContainer.getBedToLocalConverter());

            String modelName = modelContainer.getModelName();

            int ix = 1;
            for (TriangleMesh subMesh : meshPair)
            {
                MeshView meshView = new MeshView(subMesh);
                meshView.cullFaceProperty().set(CullFace.NONE);
                ModelContainer newModelContainer = new ModelContainer(
                        modelContainer.getModelFile(), meshView);
                MeshDebug.setDebuggingNode(newModelContainer);
                newModelContainer.setModelName(modelName + " " + ix);
                newModelContainer.setState(modelContainer.getState());
                newModelContainer.getAssociateWithExtruderNumberProperty().set(
                        modelContainer.getAssociateWithExtruderNumberProperty().get());
                modelContainerPair.add(Optional.of(newModelContainer));

//                newModelContainer.getMeshView().setDrawMode(DrawMode.LINE);
                ix++;
            }
        } finally
        {
            modelContainer.restoreBedTransform();
            modelContainer.restoreDropToBedYTransform();
        }

        return modelContainerPair;
    }

    /**
     * Ungroup this model group and any descendent model groups.
     */
    private void ungroupAllDescendentModelGroups(ModelGroup modelGroup)
    {
        Set<ModelContainer> groupChildren = modelGroup.getChildModelContainers();
        Set<ModelContainer> modelGroups = new HashSet<>();
        modelGroups.add(modelGroup);
        project.ungroup(modelGroups);

        for (ModelContainer childModel : groupChildren)
        {
            if (childModel instanceof ModelGroup)
            {
                ungroupAllDescendentModelGroups((ModelGroup) childModel);
            }
        }
    }

    @Override
    public boolean canMergeWith(Command command)
    {
        return false;
    }

    @Override
    public void merge(Command command)
    {
        throw new UnsupportedOperationException("Should never be called");
    }
}
