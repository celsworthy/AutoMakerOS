/*
 * Copyright 2015 CEL UK
 */
package celtech.appManager.undo;

import celtech.appManager.Project;
import celtech.appManager.ShapeContainerProject;
import celtech.modelcontrol.ItemState;
import celtech.modelcontrol.ProjectifiableThing;
import celtech.modelcontrol.ShapeGroup;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author tony
 */
public class ShapeUngroupCommand extends Command
{

    private final Stenographer steno = StenographerFactory.getStenographer(ShapeUngroupCommand.class.getName());

    ShapeContainerProject project;
    Map<Integer, Set<ProjectifiableThing>> groupIds;
    private Set<ItemState> originalStates;

    public ShapeUngroupCommand(ShapeContainerProject project, Set<ProjectifiableThing> modelContainers)
    {
        this.project = project;
        groupIds = new HashMap<>();
        for (ProjectifiableThing modelContainer : modelContainers)
        {
            if (modelContainer instanceof ShapeGroup)
            {
                groupIds.put(modelContainer.getModelId(), (Set) ((ShapeGroup) modelContainer).getChildShapeContainers());
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
                project.ungroup(project.getShapeContainersForIds(groupIds.keySet()));
            } catch (Project.ProjectLoadException ex)
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
