/*
 * Copyright 2015 CEL UK
 */
package celtech.appManager.undo;

import celtech.Lookup;
import celtech.appManager.ModelContainerProject;
import celtech.appManager.Project;
import celtech.modelcontrol.Groupable;
import celtech.roboxbase.configuration.Filament;
import celtech.modelcontrol.ModelContainer;
import celtech.modelcontrol.ProjectifiableThing;
import celtech.modelcontrol.ResizeableThreeD;
import celtech.modelcontrol.ResizeableTwoD;
import celtech.modelcontrol.RotatableThreeD;
import celtech.modelcontrol.RotatableTwoD;
import celtech.modelcontrol.ScaleableThreeD;
import celtech.modelcontrol.ScaleableTwoD;
import celtech.modelcontrol.Translateable;
import celtech.modelcontrol.TranslateableThreeD;
import celtech.modelcontrol.TranslateableTwoD;
import java.util.Set;
import javafx.scene.shape.MeshView;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * UndoableProject wraps Project and puts each change into a Command that can be
 * undone.
 *
 * @author tony
 */
public class UndoableProject
{

    private final Stenographer steno = StenographerFactory.getStenographer(
            Stenographer.class.getName());

    Project project;
    private final CommandStack commandStack;

    /**
     * A point interface (ie only one method) that takes no arguments and
     * returns void.
     */
    public interface NoArgsVoidFunc
    {

        void run() throws Exception;
    }

    private void doTransformCommand(NoArgsVoidFunc func)
    {
        doTransformCommand(func, false);
    }

    private void doTransformCommand(NoArgsVoidFunc func, boolean canMerge)
    {
        Command command = new TransformCommand(project, func, canMerge);
        commandStack.do_(command);
    }

    public UndoableProject(Project project)
    {
        this.project = project;
        commandStack = Lookup.getProjectGUIState(project).getCommandStack();
    }

    public void translateModelsXTo(Set<TranslateableTwoD> modelContainers, double x)
    {
        doTransformCommand(() ->
        {
            project.translateModelsXTo(modelContainers, x);
        });
    }

    public void translateModelsDepthPositionTo(Set<Translateable> modelContainers, double position)
    {
        doTransformCommand(() ->
        {
            project.translateModelsDepthPositionTo(modelContainers, position);
        });
    }

    public void scaleXModels(Set<ScaleableTwoD> modelContainers, double newScale,
            boolean preserveAspectRatio)
    {
        doTransformCommand(() ->
        {
            project.scaleXModels(modelContainers, newScale, preserveAspectRatio);
        });
    }

    public void scaleYModels(Set<ScaleableTwoD> modelContainers, double newScale,
            boolean preserveAspectRatio)
    {
        doTransformCommand(() ->
        {
            project.scaleYModels(modelContainers, newScale, preserveAspectRatio);
        });
    }

    public void scaleZModels(Set<ScaleableThreeD> modelContainers, double newScale,
            boolean preserveAspectRatio)
    {
        doTransformCommand(() ->
        {
            project.scaleZModels(modelContainers, newScale, preserveAspectRatio);
        });
    }

    public void scaleXYZRatioSelection(Set<ScaleableThreeD> modelContainers, double ratio)
    {
        doTransformCommand(() ->
        {
            project.scaleXYZRatioSelection(modelContainers, ratio);
        });
    }

    public void scaleXYRatioSelection(Set<ScaleableTwoD> modelContainers, double ratio)
    {
        doTransformCommand(() ->
        {
            project.scaleXYRatioSelection(modelContainers, ratio);
        });
    }

    public void resizeModelsDepth(Set<ResizeableThreeD> modelContainers, double depth)
    {
        doTransformCommand(() ->
        {
            project.resizeModelsDepth(modelContainers, depth);
        });
    }

    public void resizeModelsHeight(Set<ResizeableTwoD> modelContainers, double height)
    {
        doTransformCommand(() ->
        {
            project.resizeModelsHeight(modelContainers, height);
        });
    }

    public void resizeModelsWidth(Set<ResizeableTwoD> modelContainers, double width)
    {
        doTransformCommand(() ->
        {
            project.resizeModelsWidth(modelContainers, width);
        });
    }

    public void rotateLeanModels(Set<RotatableThreeD> modelContainers, double rotation)
    {
        if (project instanceof ModelContainerProject)
        {
            doTransformCommand(() ->
            {
                ((ModelContainerProject) project).rotateLeanModels(modelContainers, rotation);
            });
        }
    }

    public void rotateTwistModels(Set<RotatableThreeD> modelContainers, double rotation)
    {
        if (project instanceof ModelContainerProject)
        {
            doTransformCommand(() ->
            {
                ((ModelContainerProject) project).rotateTwistModels(modelContainers, rotation);
            });
        }
    }

    public void rotateTurnModels(Set<RotatableTwoD> modelContainers, double rotation)
    {
        if (project instanceof ModelContainerProject)
        {
            doTransformCommand(() ->
            {
                ((ModelContainerProject) project).rotateTurnModels(modelContainers, rotation);
            });
        }
    }

    public void translateModelsBy(Set<TranslateableTwoD> modelContainers, double x, double y,
            boolean canMerge)
    {
        doTransformCommand(() ->
        {
            project.translateModelsBy(modelContainers, x, y);
        }, canMerge);
    }

    public void translateModelsTo(Set<TranslateableTwoD> modelContainers, double x, double y,
            boolean canMerge)
    {
        doTransformCommand(() ->
        {
            project.translateModelsTo(modelContainers, x, y);
        }, canMerge);
    }

    public void autoLayout()
    {
        doTransformCommand(() ->
        {
            project.autoLayout();
        });
    }

    public void dropToBed(Set<ModelContainer> modelContainers)
    {
        if (project instanceof ModelContainerProject)
        {
            doTransformCommand(() ->
            {
                ((ModelContainerProject) project).dropToBed(modelContainers);
            });
        }
    }

    public void snapToGround(ModelContainer modelContainer, MeshView meshView, int faceNumber)
    {
        if (project instanceof ModelContainerProject)
        {
            doTransformCommand(() ->
            {
                ((ModelContainerProject) project).snapToGround(modelContainer, meshView, faceNumber);
            });
        }
    }

    public void addModel(ProjectifiableThing modelContainer)
    {
        Command addModelCommand = new AddModelCommand(project, modelContainer);
        commandStack.do_(addModelCommand);
    }

    public void deleteModels(Set<ProjectifiableThing> modelContainers)
    {
        Command deleteModelCommand = new DeleteModelsCommand(project, modelContainers);
        commandStack.do_(deleteModelCommand);
    }

    public void copyModels(Set<ProjectifiableThing> modelContainers)
    {
        Command copyModelsCommand = new CopyModelsCommand(project, modelContainers);
        commandStack.do_(copyModelsCommand);
    }

    public void assignModelToExtruder(ModelContainer modelContainer,
            boolean assignToExtruder0)
    {
        if (project instanceof ModelContainerProject)
        {
            Command setUserExtruder0Command = new AssignModelToExtruderCommand((ModelContainerProject)project,
                    modelContainer,
                    assignToExtruder0);
            commandStack.do_(setUserExtruder0Command);
        }
    }

    public void assignModelsToExtruders(Set<ModelContainer> modelContainersToAssignToExtruder0,
            Set<ModelContainer> modelContainersToAssignToExtruder1)
    {
        Command setUserExtruder0Command = new AssignModelToExtruderCommand((ModelContainerProject)project,
                modelContainersToAssignToExtruder0,
                modelContainersToAssignToExtruder1);
        commandStack.do_(setUserExtruder0Command);
    }

    public void group(Set<Groupable> modelContainers)
    {
        if (project instanceof ModelContainerProject)
        {
            Command groupCommand = new GroupCommand(((ModelContainerProject) project), modelContainers);
            commandStack.do_(groupCommand);
        }
    }

    public void ungroup(Set<ModelContainer> modelContainers)
    {
        if (project instanceof ModelContainerProject)
        {
            Command ungroupCommand = new UngroupCommand(((ModelContainerProject) project), modelContainers);
            commandStack.do_(ungroupCommand);
        }
    }

    public void cut(Set<ModelContainer> modelContainers, float cutHeightValue)
    {
        if (project instanceof ModelContainerProject)
        {
            Command cutCommand = new CutCommand(((ModelContainerProject) project), modelContainers, cutHeightValue);
            commandStack.do_(cutCommand);
        }
    }
}
