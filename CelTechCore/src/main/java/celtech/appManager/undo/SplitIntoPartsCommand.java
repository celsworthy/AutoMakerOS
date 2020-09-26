/*
 * Copyright 2015 CEL UK
 */
package celtech.appManager.undo;

import celtech.appManager.ModelContainerProject;
import celtech.modelcontrol.ItemState;
import celtech.modelcontrol.ModelContainer;
import celtech.modelcontrol.ProjectifiableThing;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author tony
 */
public class SplitIntoPartsCommand extends Command
{

    ModelContainerProject project;
    Set<ModelContainer> modelContainers;
    Set<ModelContainer> newModelContainers;
    Map<ModelContainer, ItemState> states;

    public SplitIntoPartsCommand(ModelContainerProject project, Set<ModelContainer> modelContainers)
    {
        this.project = project;
        this.modelContainers = modelContainers;
    }

    @Override
    public void do_()
    {
        states = new HashMap<>();
        for (ModelContainer modelContainer : modelContainers)
        {
            states.put(modelContainer, modelContainer.getState());
        }
//        newModelContainers = project.splitIntoParts(modelContainers);
    }

    @Override
    public void undo()
    {
        if (newModelContainers.size() == 1)
        {
            return;
        }
        Set<ProjectifiableThing> projectifiableThings = (Set) newModelContainers;
        project.removeModels(projectifiableThings);
        for (ModelContainer modelContainer : modelContainers)
        {
            project.addModel(modelContainer);
        }
    }

    @Override
    public void redo()
    {
        if (newModelContainers.size() == 1)
        {
            return;
        }
        Set<ProjectifiableThing> projectifiableThings = (Set) modelContainers;
        project.removeModels(projectifiableThings);
        for (ModelContainer modelContainer : newModelContainers)
        {
            project.addModel(modelContainer);
            try
            {
                modelContainer.setState(states.get(modelContainer));
            } catch (Exception ex)
            {
                //TODO fix storing state!!
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
