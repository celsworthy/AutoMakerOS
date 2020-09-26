/*
 * Copyright 2015 CEL UK
 */
package celtech.coreUI.visualisation;

import celtech.Lookup;
import celtech.appManager.ModelContainerProject;
import celtech.appManager.Project;
import celtech.appManager.ProjectCallback;
import celtech.appManager.ProjectMode;
import celtech.appManager.ShapeContainerProject;
import celtech.appManager.undo.UndoableProject;
import celtech.coreUI.visualisation.metaparts.ModelLoadResult;
import celtech.coreUI.visualisation.metaparts.ModelLoadResultType;
import celtech.modelcontrol.Groupable;
import celtech.roboxbase.utils.RectangularBounds;
import celtech.modelcontrol.ModelContainer;
import celtech.modelcontrol.ModelGroup;
import celtech.modelcontrol.ProjectifiableThing;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.services.modelLoader.ModelLoadResults;
import celtech.services.modelLoader.ModelLoaderService;
import celtech.utils.threed.MeshUtils;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.shape.TriangleMesh;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * ModelLoader contains methods for loading models from a file.
 *
 * @author tony
 */
public class ModelLoader
{

    private static final Stenographer steno = StenographerFactory.getStenographer(
            ModelLoader.class.getName());
    /*
     * Mesh Model loading
     */
    public static final ModelLoaderService modelLoaderService = new ModelLoaderService();

    private void offerShrinkAndAddToProject(Project project, boolean relayout, ProjectCallback callMeBack,
            boolean dontGroupModelsOverride,
            Printer printer)
    {
        ModelLoadResults loadResults = modelLoaderService.getValue();
        if (loadResults.getResults().isEmpty())
        {
            return;
        }

        if (loadResults.getType() == ModelLoadResultType.Mesh)
        {
            project.setMode(ProjectMode.MESH);
            // validate incoming meshes
            // Associate the loaded meshes with extruders in turn, respecting the original groups / model files
            int numExtruders = 1;

            Printer selectedPrinter = Lookup.getSelectedPrinterProperty().get();
            if (selectedPrinter != null)
            {
                numExtruders = selectedPrinter.extrudersProperty().size();
            }

            int currentExtruder = 0;

            for (ModelLoadResult loadResult : loadResults.getResults())
            {
                Set<ModelContainer> modelContainers = (Set) loadResult.getProjectifiableThings();
                Set<String> invalidModelNames = new HashSet<>();
                for (ModelContainer modelContainer : modelContainers)
                {
                    Optional<MeshUtils.MeshError> error = MeshUtils.validate((TriangleMesh) modelContainer.getMeshView().getMesh());
                    if (error.isPresent())
                    {
                        invalidModelNames.add(modelContainer.getModelName());
                        modelContainer.setIsInvalidMesh(true);
                        steno.debug("Model load - " + error.get().name());
                    }

                    //Assign the models incrementally to the extruders
                    modelContainer.getAssociateWithExtruderNumberProperty().set(currentExtruder);
                }

                if (currentExtruder < numExtruders - 1)
                {
                    currentExtruder++;
                } else
                {
                    currentExtruder = 0;
                }

                if (!invalidModelNames.isEmpty())
                {
                    boolean load
                            = BaseLookup.getSystemNotificationHandler().showModelIsInvalidDialog(invalidModelNames);
                    if (!load)
                    {
                        return;
                    }
                }
            }

            boolean projectIsEmpty = project.getNumberOfProjectifiableElements() == 0;
            Set<ModelContainer> allModelContainers = new HashSet<>();
            boolean shouldCentre = loadResults.isShouldCentre();

            for (ModelLoadResult loadResult : loadResults.getResults())
            {
                if (loadResult != null)
                {
                    Set<ModelContainer> modelContainersToOperateOn = (Set) loadResult.getProjectifiableThings();
                    if (Lookup.getUserPreferences().isLoosePartSplitOnLoad())
                    {
                        allModelContainers.add(makeGroup(modelContainersToOperateOn));
                    } else
                    {
                        allModelContainers.addAll(modelContainersToOperateOn);
                    }
                } else
                {
                    steno.error("Error whilst attempting to load model");
                }
            }
            Set<ProjectifiableThing> allProjectifiableThings = (Set) allModelContainers;

            addToProject(project, allProjectifiableThings, shouldCentre, dontGroupModelsOverride, printer);
            if (relayout && projectIsEmpty && loadResults.getResults().size() > 1)
            {
//            project.autoLayout();
            }
        } else if (loadResults.getType() == ModelLoadResultType.SVG)
        {

            project.setMode(ProjectMode.SVG);
            Set<ProjectifiableThing> allProjectifiableThings = new HashSet<>();
            for (ModelLoadResult result : loadResults.getResults())
            {
                allProjectifiableThings.addAll(result.getProjectifiableThings());
            }

            addToProject(project, allProjectifiableThings, false, dontGroupModelsOverride, printer);

        }

        if (project != null
                && callMeBack != null)
        {
            callMeBack.modelAddedToProject(project);
        }
    }

    public ReadOnlyBooleanProperty modelLoadingProperty()
    {
        return modelLoaderService.runningProperty();
    }

    /**
     * Load each model in modelsToLoad, do not lay them out on the bed.
     *
     * @param project
     * @param modelsToLoad
     * @param callMeBack
     */
    public void loadExternalModels(Project project, List<File> modelsToLoad, ProjectCallback callMeBack)
    {
        loadExternalModels(project, modelsToLoad, false, callMeBack, false);
    }

    /**
     * Load each model in modelsToLoad and relayout if requested. If there are
     * already models loaded in the project then do not relayout even if
     * relayout=true;
     *
     * @param project
     * @param modelsToLoad
     * @param relayout
     * @param callMeBack
     */
    public void loadExternalModels(Project project, List<File> modelsToLoad, boolean relayout, ProjectCallback callMeBack,
            boolean dontGroupModelsOverride)
    {
        modelLoaderService.reset();
        modelLoaderService.setModelFilesToLoad(modelsToLoad);
        modelLoaderService.setOnSucceeded((WorkerStateEvent t) ->
        {
            Project projectToUse = null;

            if (project == null)
            {
                ModelLoadResults loadResults = modelLoaderService.getValue();
                if (!loadResults.getResults().isEmpty())
                {
                    switch (loadResults.getType())
                    {
                        case Mesh:
                            projectToUse = new ModelContainerProject();
                            break;
                        case SVG:
                            projectToUse = new ShapeContainerProject();
                            break;
                    }
                }
            } else
            {
                projectToUse = project;
            }
            offerShrinkAndAddToProject(projectToUse, relayout, callMeBack, dontGroupModelsOverride, Lookup.getSelectedPrinterProperty().get());
        });
        modelLoaderService.start();
    }

    /**
     * Add the given ModelContainers to the project. Some may be ModelGroups. If
     * there is more than one ModelContainer/Group then put them in one
     * overarching group.
     */
    private void addToProject(Project project, Set<ProjectifiableThing> modelContainers,
            boolean shouldCentre,
            boolean dontGroupModelsOverride,
            Printer printer)
    {
        UndoableProject undoableProject = new UndoableProject(project);

        if (project instanceof ModelContainerProject)
        {
            ModelContainer modelContainer;

            if (modelContainers.size() == 1)
            {
                modelContainer = (ModelContainer) modelContainers.iterator().next();
                addModelSequence(undoableProject, modelContainer, shouldCentre, printer);
            } else if (!dontGroupModelsOverride)
            {
                Set<Groupable> thingsToGroup = (Set) modelContainers;
                modelContainer = ((ModelContainerProject) project).createNewGroupAndAddModelListeners(thingsToGroup);
                addModelSequence(undoableProject, modelContainer, shouldCentre, printer);
            } else
            {
                modelContainers.iterator().forEachRemaining(mc ->
                {
                    addModelSequence(undoableProject, mc, shouldCentre, printer);
                });
            }
        } else
        {
            addModelSequence(undoableProject, modelContainers.iterator().next(), shouldCentre, printer);
        }
    }

    private void addModelSequence(UndoableProject undoableProject,
            ProjectifiableThing projectifiableThing,
            boolean shouldCentre,
            Printer printer)
    {
        shrinkIfRequested(projectifiableThing, printer);
        if (shouldCentre)
        {
            projectifiableThing.moveToCentre();
            if (projectifiableThing instanceof ModelContainer)
            {
                ((ModelContainer)projectifiableThing).dropToBed();
            }
        }
        projectifiableThing.checkOffBed();
        undoableProject.addModel(projectifiableThing);
    }

    private void shrinkIfRequested(ProjectifiableThing projectifiableThing, Printer printer)
    {
        boolean shrinkModel = false;
        RectangularBounds originalBounds = projectifiableThing.getOriginalModelBounds();

        if (printer != null)
        {
            boolean modelIsTooLarge = printer.isBiggerThanPrintVolume(originalBounds);
            if (modelIsTooLarge)
            {
                shrinkModel = BaseLookup.getSystemNotificationHandler().
                        showModelTooBigDialog(projectifiableThing.getModelName());
            }
            if (shrinkModel)
            {
                projectifiableThing.shrinkToFitBed();
            }
        }
    }

    private ModelContainer makeGroup(Set<ModelContainer> modelContainers)
    {
        Set<ModelContainer> splitModelContainers = new HashSet<>();
        for (ModelContainer modelContainer : modelContainers)
        {
            try
            {
                ModelContainer splitContainerOrGroup = modelContainer.splitIntoParts();
                splitModelContainers.add(splitContainerOrGroup);
            } catch (StackOverflowError ex)
            {
                splitModelContainers.add(modelContainer);
            }
        }
        if (splitModelContainers.size() == 1)
        {
            return splitModelContainers.iterator().next();
        } else
        {
            return new ModelGroup(splitModelContainers);
        }
    }

}
