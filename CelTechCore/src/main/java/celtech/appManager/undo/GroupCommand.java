/*
 * Copyright 2015 CEL UK
 */
package celtech.appManager.undo;

import celtech.appManager.Project;
import celtech.modelcontrol.Groupable;
import celtech.modelcontrol.ItemState;
import celtech.modelcontrol.ModelContainer;
import celtech.modelcontrol.ProjectifiableThing;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author tony
 */
public class GroupCommand extends Command
{

    Project project;
    Set<Groupable> modelContainers;
    private Set<ItemState> states;
    ModelContainer group;

    public GroupCommand(Project project, Set<Groupable> modelContainers)
    {
        states = new HashSet<>();
        this.project = project;
        this.modelContainers = (Set)modelContainers;
    }

    @Override
    public void do_()
    {
        for (Groupable modelContainer : modelContainers)
        {
            states.add(((ProjectifiableThing)modelContainer).getState());
        }
        doGroup();
    }

    @Override
    public void undo()
    {
        Set<ModelContainer> modelContainers = new HashSet<>();
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
