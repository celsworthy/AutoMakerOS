/*
 * Copyright 2015 CEL UK
 */
package celtech.appManager.undo;

import celtech.appManager.Project;
import celtech.appManager.ShapeContainerProject;
import celtech.modelcontrol.ItemState;
import celtech.modelcontrol.ShapeContainer;
import celtech.modelcontrol.ProjectifiableThing;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author tony
 */
public class ShapeGroupCommand extends Command
{

    ShapeContainerProject project;
    Set<ProjectifiableThing> modelContainers;
    private Set<ItemState> states;
    ShapeContainer group;

    public ShapeGroupCommand(ShapeContainerProject project, Set<ProjectifiableThing> modelContainers)
    {
        states = new HashSet<>();
        this.project = project;
        this.modelContainers = modelContainers;
    }

    @Override
    public void do_()
    {
        for (ProjectifiableThing modelContainer : modelContainers)
        {
            states.add(modelContainer.getState());
        }
        doGroup();
    }

    @Override
    public void undo()
    {
        Set<ShapeContainer> modelContainers = new HashSet<>();
        modelContainers.add(group);
        project.ungroup(modelContainers);
        project.setModelStates(states);
        group.updateLastTransformedBoundsInParent();
    }

    @Override
    public void redo()
    {
        doGroup();
    }
    
    private void doGroup() {
        if (modelContainers.size() == 1)
        {
            return;
        }
        group = project.group(modelContainers);
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
