/*
 * Copyright 2015 CEL UK
 */
package celtech.appManager.undo;

import celtech.appManager.ModelContainerProject;
import celtech.modelcontrol.ModelContainer;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author tony
 */
public class AssignModelToExtruderCommand extends Command
{

    ModelContainerProject project;
    private final Set<ModelContainer> modelContainersToAssignToExtruder0;
    private final Set<ModelContainer> modelContainersToAssignToExtruder1;

    public AssignModelToExtruderCommand(ModelContainerProject project,
            ModelContainer modelContainerToChangeExtruderAssociation,
            boolean assignToExtruder0)
    {
        this.project = project;
        Set<ModelContainer> tempSet = new HashSet<>();
        tempSet.add(modelContainerToChangeExtruderAssociation);
        if (assignToExtruder0)
        {
            modelContainersToAssignToExtruder0 = tempSet;
            modelContainersToAssignToExtruder1 = null;
        } else
        {
            modelContainersToAssignToExtruder0 = null;
            modelContainersToAssignToExtruder1 = tempSet;
        }
    }

    public AssignModelToExtruderCommand(ModelContainerProject project,
            Set<ModelContainer> modelContainersToAssignToExtruder0,
            Set<ModelContainer> modelContainersToAssignToExtruder1)
    {
        this.project = project;
        this.modelContainersToAssignToExtruder0 = modelContainersToAssignToExtruder0;
        this.modelContainersToAssignToExtruder1 = modelContainersToAssignToExtruder1;
    }

    @Override
    public void do_()
    {
        redo();
    }

    @Override
    public void undo()
    {
        if (modelContainersToAssignToExtruder0 != null
                && modelContainersToAssignToExtruder0.size() > 0)
        {
            project.setAssociatedExtruder(modelContainersToAssignToExtruder0, false);
        }

        if (modelContainersToAssignToExtruder1 != null
                && modelContainersToAssignToExtruder1.size() > 0)
        {
            project.setAssociatedExtruder(modelContainersToAssignToExtruder1, true);
        }
    }

    @Override
    public void redo()
    {
        if (modelContainersToAssignToExtruder0 != null
                && modelContainersToAssignToExtruder0.size() > 0)
        {
            project.setAssociatedExtruder(modelContainersToAssignToExtruder0, true);
        }

        if (modelContainersToAssignToExtruder1 != null
                && modelContainersToAssignToExtruder1.size() > 0)
        {
            project.setAssociatedExtruder(modelContainersToAssignToExtruder1, false);
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
