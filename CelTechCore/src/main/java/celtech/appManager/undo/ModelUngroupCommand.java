/*
 * Copyright 2015 CEL UK
 */
package celtech.appManager.undo;

import celtech.appManager.ModelContainerProject;
import celtech.modelcontrol.ItemState;
import celtech.modelcontrol.ModelGroup;
import celtech.modelcontrol.ProjectifiableThing;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author tony
 */
public class ModelUngroupCommand extends Command
{

    private final Stenographer steno = StenographerFactory.getStenographer(ModelUngroupCommand.class.getName());

    ModelContainerProject project;
    Map<Integer, Set<ProjectifiableThing>> groupIds;
    private Set<ItemState> originalStates;

    public ModelUngroupCommand(ModelContainerProject project, Set<ProjectifiableThing> modelContainers)
    {
        this.project = project;
        groupIds = new HashMap<>();
        for (ProjectifiableThing modelContainer : modelContainers)
        {
            if (modelContainer instanceof ModelGroup)
            {
                ModelGroup mg = (ModelGroup)modelContainer;
                groupIds.put(mg.getModelId(), (Set) (mg.getChildModelContainers()));
            }
        }
    }

    @Override
    public void do_()
    {
        redo();
    }

    @Override
    public void undo()
    {
        for (int groupId : groupIds.keySet())
        {
            project.group(groupIds.get(groupId), groupId);
        }
        project.setModelStates(originalStates);
    }

    @Override
    public void redo()
    {
        originalStates = project.getModelStates();
        try
        {
            try
            {
                project.ungroup(project.getModelContainersOfIds(groupIds.keySet()));
            } catch (ModelContainerProject.ProjectLoadException ex)
            {
                steno.exception("Could not ungroup", ex);
            }
        } catch (Exception ex)
        {
            steno.exception("Failed running command ", ex);
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
