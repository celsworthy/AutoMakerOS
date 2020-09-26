/*
 * Copyright 2015 CEL UK
 */
package celtech.appManager.undo;

import celtech.appManager.Project;
import celtech.modelcontrol.ItemState;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * The TransformCommand is a Command that allows x,y, scaleX, scaleY, scaleZ,
 * lean, twist and turn changes to be undone.
 *
 * @author tony
 */
class TransformCommand extends Command
{

    private Stenographer steno = StenographerFactory.getStenographer(
        TransformCommand.class.getName());

    private final UndoableProject.NoArgsVoidFunc func;
    private Set<ItemState> originalStates;
    private Set<ItemState> newStates;
    private final boolean canMerge;
    private final Project project;

    public TransformCommand(Project project, UndoableProject.NoArgsVoidFunc func, boolean canMerge)
    {
        this.project = project;
        this.func = func;
        this.canMerge = canMerge;
    }

    @Override
    public void do_()
    {
        originalStates = project.getModelStates();
        try
        {
            func.run();
            newStates = project.getModelStates();
        } catch (Exception ex)
        {
            steno.exception("Failed running command ", ex);
        }
    }

    @Override
    public void undo()
    {
        project.setModelStates(originalStates);
    }

    @Override
    public void redo()
    {
        project.setModelStates(newStates);
    }

    @Override
    public boolean canMergeWith(Command command)
    {
        if (command instanceof TransformCommand)
        {
            TransformCommand transformCommand = (TransformCommand) command;
            return transformCommand.getCanMerge();
        } else {
            return false;
        }
    }

    @Override
    public void merge(Command command)
    {
        if (command instanceof TransformCommand)
        {
            TransformCommand transformCommand = (TransformCommand) command;
            if (transformCommand.getCanMerge())
            {
                mergeStates(newStates, transformCommand.newStates);
            }
        }

    }

    protected boolean getCanMerge()
    {
        return canMerge;
    }

    /**
     * Update states to include the changes in toStates.
     */
    private void mergeStates(Set<ItemState> states, Set<ItemState> toStates)
    {
        Map<Integer, ItemState> toStatesById = makeStatesById(toStates);
        for (ItemState state : states)
        {
            state.assignFrom(toStatesById.get(state.modelId));
        }

    }

    private Map<Integer, ItemState> makeStatesById(Set<ItemState> states)
    {
        Map<Integer, ItemState> statesById = new HashMap<>();
        for (ItemState state : states)
        {
            statesById.put(state.modelId, state);
        }
        return statesById;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().toGenericString());
        sb.append("\n");
        sb.append("Project: ");
        sb.append(project.getProjectName());
        sb.append("\n");
        sb.append("Original States: ");
        if (originalStates != null)
        {
            originalStates.iterator().forEachRemaining(item ->
            {
                sb.append(item.toString());
            });
        } else
        {
            sb.append("None");
}
        sb.append("\n");
        sb.append("New States: ");
        if (newStates != null)
        {
            newStates.iterator().forEachRemaining(item ->
            {
                sb.append(item.toString());
            });
        } else
        {
            sb.append("None");
        }
        sb.append("\n");
        return sb.toString();
    }
}
