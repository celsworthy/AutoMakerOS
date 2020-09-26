/*
 * Copyright 2014 CEL UK
 */
package celtech.coreUI.visualisation;
         
import celtech.appManager.Project;
import celtech.appManager.Project.ProjectChangesListener;
import celtech.appManager.ProjectManager;
import celtech.appManager.ProjectMode;
import celtech.appManager.TimelapseSettingsData;
import celtech.modelcontrol.ModelContainer;
import celtech.modelcontrol.ModelGroup;
import celtech.modelcontrol.ProjectifiableThing;
import celtech.modelcontrol.RotatableThreeD;
import celtech.modelcontrol.RotatableTwoD;
import celtech.modelcontrol.ScaleableThreeD;
import celtech.modelcontrol.ScaleableTwoD;
import celtech.roboxbase.configuration.fileRepresentation.PrinterSettingsOverrides;
import static celtech.roboxbase.utils.DeDuplicator.suggestNonDuplicateName;
import celtech.modelcontrol.ShapeContainer;
import celtech.modelcontrol.ShapeGroup;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

/**
 * ProjectSelection captures all required state about the currently selected
 * ModelContainers.
 *
 * @author tony
 */
public class ProjectSelection implements ProjectChangesListener
{

    private final ObservableSet<ProjectifiableThing> modelContainers;
    private final PrimarySelectedModelDetails primarySelectedModelDetails;
    private final IntegerProperty numModelsSelected = new SimpleIntegerProperty(0);
    private final IntegerProperty numGroupsSelected = new SimpleIntegerProperty(0);
    private final Set<SelectedModelContainersListener> selectedModelContainersListeners;
    
    private final Project project;
    
    /**
     * If any of the current selection are a child of a group then value is
     * true.
     */
    private final BooleanBinding selectionHasChildOfGroup;

    public ProjectSelection(Project project)
    {
        modelContainers = FXCollections.observableSet();
        primarySelectedModelDetails = new PrimarySelectedModelDetails();
        selectedModelContainersListeners = new HashSet<>();
        project.addProjectChangesListener(this);
        this.project = project;
              
        selectionHasChildOfGroup = new BooleanBinding()
        {
            {
                super.bind(modelContainers);
            }

            @Override
            protected boolean computeValue()
            {
                for (ProjectifiableThing modelContainer : modelContainers)
                {
                    if ((modelContainer instanceof ModelContainer)
                            && ((ModelContainer) modelContainer).getParentModelContainer() != null)
                    {
                        return true;
                    }
                }
                return false;
            }

        };
    }

    public BooleanBinding getSelectionHasChildOfGroup()
    {
        return selectionHasChildOfGroup;
    }

    /**
     * Add the given modelContainer to the set of selected ModelContainers.
     */
    public void addSelectedItem(ProjectifiableThing modelContainer)
    {
        if (!modelContainers.contains(modelContainer))
        {
            modelContainer.setSelected(true);
            modelContainers.add(modelContainer);
            primarySelectedModelDetails.setTo(modelContainer);
            numModelsSelected.set(numModelsSelected.get() + 1);
            if (modelContainer instanceof ModelGroup)
            {
                numGroupsSelected.set(numGroupsSelected.get() + 1);
                ModelGroup modelGroup = (ModelGroup) modelContainer;
                modelGroup.updateOriginalModelBounds();
                modelGroup.notifyScreenExtentsChange();
                modelGroup.notifyShapeChange();
            }
            else if (modelContainer instanceof ShapeGroup)
            {
                numGroupsSelected.set(numGroupsSelected.get() + 1);
                ShapeGroup shapeGroup = (ShapeGroup) modelContainer;
                shapeGroup.updateOriginalModelBounds();
                shapeGroup.notifyScreenExtentsChange();
                shapeGroup.notifyShapeChange();
            }
            for (SelectedModelContainersListener selectedModelContainersListener : selectedModelContainersListeners)
            {
                selectedModelContainersListener.whenAdded(modelContainer);
            }
        }
    }

    /**
     * Remove the given modelContainer from the set of selected ModelContainers.
     *
     * @param projectifiableThing
     */
    public void removeModelContainer(ProjectifiableThing projectifiableThing)
    {
        if (modelContainers.contains(projectifiableThing))
        {
            projectifiableThing.setSelected(false);
            modelContainers.remove(projectifiableThing);
            numModelsSelected.set(numModelsSelected.get() - 1);
            if (projectifiableThing instanceof ModelGroup)
            {
                numGroupsSelected.set(numGroupsSelected.get() - 1);
                ModelGroup modelGroup = (ModelGroup) projectifiableThing;
                modelGroup.updateOriginalModelBounds();
                modelGroup.notifyScreenExtentsChange();
                modelGroup.notifyShapeChange();
            }
            else if (projectifiableThing instanceof ShapeGroup)
            {
                numGroupsSelected.set(numGroupsSelected.get() - 1);
                ShapeGroup shapeGroup = (ShapeGroup) projectifiableThing;
                shapeGroup.updateOriginalModelBounds();
                shapeGroup.notifyScreenExtentsChange();
                shapeGroup.notifyShapeChange();
            }
            for (SelectedModelContainersListener selectedModelContainersListener : selectedModelContainersListeners)
            {
                selectedModelContainersListener.whenRemoved(projectifiableThing);
            }
        }

    }

    /**
     * Return if the given ModelContainer is selected or not.
     *
     * @param modelContainer
     * @return
     */
    public boolean isSelected(ModelContainer modelContainer)
    {
        return modelContainers.contains(modelContainer);
    }

    /**
     * Deselect all ModelContainers in the set of ModelContainers.
     */
    public void deselectAllModels()
    {
        Set<ProjectifiableThing> allSelectedModelContainers = new HashSet<>(modelContainers);
        for (ProjectifiableThing modelContainer : allSelectedModelContainers)
        {
            removeModelContainer(modelContainer);
        }
    }

    /**
     * Return a copy of the set of selected models.
     *
     * @return
     */
    public Set<ProjectifiableThing> getSelectedModelsSnapshot()
    {
        return new HashSet<>(modelContainers);
    }

    /**
     * Return a copy of the set of selected models.
     *
     * @param desiredClass
     * @return
     */
    public <T> Set<T> getSelectedModelsSnapshot(Class desiredClass)
    {
        Set<T> returnedModels = new HashSet<>();

        for (ProjectifiableThing container : modelContainers)
        {
            if (desiredClass.isInstance(container))
            {
                returnedModels.add((T) container);
            }
        }

        return returnedModels;
    }

    /**
     * Return the number of selected ModelContainers as an observable number.
     *
     * @return
     */
    public ReadOnlyIntegerProperty getNumModelsSelectedProperty()
    {
        return numModelsSelected;
    }

    /**
     * Return the number of selected ModelGroups as an observable number.
     *
     * @return
     */
    public ReadOnlyIntegerProperty getNumGroupsSelectedProperty()
    {
        return numGroupsSelected;
    }

    /**
     * Return the details of the primary selected ModelContainer.
     *
     * @return
     */
    public PrimarySelectedModelDetails getPrimarySelectedModelDetails()
    {
        return primarySelectedModelDetails;
    }

    /**
     * Call this method when the transformed geometry of the selected model have
     * changed.
     */
    public void updateSelectedValues()
    {
        primarySelectedModelDetails.updateSelectedProperties();
    }

    @Override
    public void whenModelAdded(ProjectifiableThing projectifiableThing)
    {
        if(!project.isProjectNameModified()) 
        {
            String projectName = "Project";
                
            if (project.getMode() == ProjectMode.MESH)
            {
                ModelContainer modelContainer = (ModelContainer) projectifiableThing;
                Optional<ModelContainer> childModel = modelContainer.getChildModelContainers().stream().findFirst();
                if(childModel.isPresent()) {
                    // Regex looks for split on any period follwed by any number of non-periods and the end of input
                    projectName = childModel.get().getModelName().split("\\.(?=[^\\.]+$)")[0];
                } else {
                    // Regex looks for split on any period follwed by any number of non-periods and the end of input
                    projectName = modelContainer.getModelName().split("\\.(?=[^\\.]+$)")[0];
                }

            }
            else if (project.getMode() == ProjectMode.SVG)
            {
                ShapeContainer shapeContainer = (ShapeContainer) projectifiableThing;
                projectName = shapeContainer.getModelName().split("\\.(?=[^\\.]+$)")[0];
            }

            Set<String> currentProjectNames = ProjectManager.getInstance().getOpenAndAvailableProjectNames();
            projectName = suggestNonDuplicateName(projectName, currentProjectNames);
            project.setProjectName(projectName);
            project.setProjectNameModified(true);
        }
    }

    @Override
    public void whenModelsRemoved(Set<ProjectifiableThing> projectifiableThings)
    {
        List<ProjectifiableThing> thingsToRemove = new ArrayList<>();
        thingsToRemove.addAll(modelContainers);
        for (ProjectifiableThing modelContainer : thingsToRemove)
        {
            removeModelContainer(modelContainer);
        }
    }

    @Override
    public void whenAutoLaidOut()
    {
        updateSelectedValues();
    }

    @Override
    public void whenModelsTransformed(Set<ProjectifiableThing> projectifiableThings)
    {
        updateSelectedValues();
    }

    @Override
    public void whenModelChanged(ProjectifiableThing modelContainer, String propertyName)
    {
    }

    @Override
    public void whenPrinterSettingsChanged(PrinterSettingsOverrides printerSettings)
    {
    }

    @Override
    public void whenTimelapseSettingsChanged(TimelapseSettingsData timelapseSettings)
    {
    }

    /**
     * Add a listener that will be notified whenever a ModelContainer is
     * selected or deselected.
     *
     * @param selectedModelContainersListener
     */
    public void addListener(SelectedModelContainersListener selectedModelContainersListener)
    {
        selectedModelContainersListeners.add(selectedModelContainersListener);
    }

    /**
     * Remove a listener that will be notified whenever a ModelContainer is
     * selected or deselected.
     *
     * @param selectedModelContainersListener
     */
    public void removeListener(SelectedModelContainersListener selectedModelContainersListener)
    {
        selectedModelContainersListeners.remove(selectedModelContainersListener);
    }

    public interface SelectedModelContainersListener
    {

        /**
         * Called when a ModelContainer is selected.
         */
        public void whenAdded(ProjectifiableThing projectifiableThing);

        /**
         * Called when a ModelContainer is removed.
         */
        public void whenRemoved(ProjectifiableThing projectifiableThing);
    }

    /**
     * PrimarySelectedModelDetails contains the details pertaining to the
     * primary selected ModelContainer.
     */
    public class PrimarySelectedModelDetails
    {

        ProjectifiableThing boundModelContainer;

        // initing values to -1 forces a change update when value first set to 0 (e.g. rotY)
        private final DoubleProperty width = new SimpleDoubleProperty(-1);
        private final DoubleProperty centreX = new SimpleDoubleProperty(-1);
        private final DoubleProperty centreDepth = new SimpleDoubleProperty(-1);
        private final DoubleProperty height = new SimpleDoubleProperty(-1);
        private final DoubleProperty depth = new SimpleDoubleProperty(-1);
        private final DoubleProperty scaleX = new SimpleDoubleProperty(-1);
        private final DoubleProperty scaleY = new SimpleDoubleProperty(-1);
        private final DoubleProperty scaleZ = new SimpleDoubleProperty(-1);
        private final DoubleProperty rotationLean = new SimpleDoubleProperty(-1);
        private final DoubleProperty rotationTwist = new SimpleDoubleProperty(-1);
        private final DoubleProperty rotationTurn = new SimpleDoubleProperty(-1);

        private PrimarySelectedModelDetails()
        {
        }

        public DoubleProperty getWidth()
        {
            return width;
        }

        public void setTo(ProjectifiableThing modelContainer)
        {
            boundModelContainer = modelContainer;
            updateSelectedProperties();
        }

        private void updateSelectedProperties()
        {
            if (boundModelContainer != null)
            {
                if (boundModelContainer instanceof ShapeProviderThreeD)
                {
                    width.set(((ShapeProviderTwoD) boundModelContainer).getScaledWidth());
                    height.set(((ShapeProviderTwoD) boundModelContainer).getScaledHeight());
                    depth.set(((ShapeProviderThreeD) boundModelContainer).getScaledDepth());
                    centreX.set(boundModelContainer.getTransformedCentreX());
                    centreDepth.set(boundModelContainer.getTransformedCentreDepth());
                } else if (boundModelContainer instanceof ShapeProviderTwoD)
                {
                    width.set(((ShapeProviderTwoD) boundModelContainer).getScaledWidth());
                    height.set(((ShapeProviderTwoD) boundModelContainer).getScaledHeight());
                    centreX.set(boundModelContainer.getTransformedCentreX());
                    centreDepth.set(boundModelContainer.getTransformedCentreDepth());
                }

                if (boundModelContainer instanceof ScaleableThreeD)
                {
                    scaleX.set(((ScaleableTwoD) boundModelContainer).getXScale());
                    scaleY.set(((ScaleableTwoD) boundModelContainer).getYScale());
                    scaleZ.set(((ScaleableThreeD) boundModelContainer).getZScale());
                } else if (boundModelContainer instanceof ScaleableTwoD)
                {
                    scaleX.set(((ScaleableTwoD) boundModelContainer).getXScale());
                    scaleY.set(((ScaleableTwoD) boundModelContainer).getYScale());
                }

                if (boundModelContainer instanceof RotatableThreeD)
                {
                    rotationLean.set(((RotatableThreeD) boundModelContainer).getRotationLean());
                    rotationTwist.set(((RotatableThreeD) boundModelContainer).getRotationTwist());
                    rotationTurn.set(((RotatableTwoD) boundModelContainer).getRotationTurn());
                } else if (boundModelContainer instanceof RotatableTwoD)
                {
                    rotationTurn.set(((RotatableTwoD) boundModelContainer).getRotationTurn());
                }
            }
        }

        public DoubleProperty getCentreDepth()
        {
            return centreDepth;
        }

        public DoubleProperty getCentreX()
        {
            return centreX;
        }

        public DoubleProperty getHeight()
        {
            return height;
        }

        public DoubleProperty getDepth()
        {
            return depth;
        }

        public DoubleProperty getScaleX()
        {
            return scaleX;
        }

        public DoubleProperty getScaleY()
        {
            return scaleY;
        }

        public DoubleProperty getScaleZ()
        {
            return scaleZ;
        }

        public DoubleProperty getRotationLean()
        {
            return rotationLean;
        }

        public DoubleProperty getRotationTwist()
        {
            return rotationTwist;
        }

        public DoubleProperty getRotationTurn()
        {
            return rotationTurn;
        }
    }

}
