/*
 * Copyright 2015 CEL UK
 */
package celtech.appManager.undo;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * The CommandStack is a stack of Commands that has an index into the current
 * stack position. When an undo is applied the Command at the current index is
 * undone, and the index is reduced by 1.
 *
 * @author tony
 */
public class CommandStack
{

    private static final Stenographer steno = StenographerFactory.getStenographer(
            CommandStack.class.getName());

    private final BooleanProperty canUndo = new SimpleBooleanProperty();
    private final BooleanProperty canRedo = new SimpleBooleanProperty();
    private final ObservableList<Command> commands;
    /**
     * The position of the last command to be performed.
     */
    private final IntegerProperty index = new SimpleIntegerProperty(-1);

    public class UndoException extends Exception
    {

        public UndoException(String message)
        {
            super(message);
        }
    }

    public CommandStack()
    {
        commands = FXCollections.observableArrayList();
        canUndo.bind(index.greaterThan(-1));
        canRedo.bind(Bindings.size(commands).greaterThan(index.add(1)));
    }

    public void do_(Command command)
    {
        clearEndOfList();
        commands.add(command);
        command.do_();
//        steno.info("Asked to do- " + command.toString());
//        if (index.get() < commands.size() - 1)
//        {
        index.set(index.get() + 1);
//        }
        tryMerge();
    }

    /**
     * Clear all list entries that are after the current index.
     */
    private void clearEndOfList()
    {
        commands.subList(index.get() + 1, commands.size()).clear();
//        List<Command> commandsToClear = commands.subList(index.get() + 1, commands.size());
//        if (!commandsToClear.isEmpty())
//        {
//            commandsToClear.clear();
//        }
    }

    public void undo() throws UndoException
    {
        if (canUndo.not().get())
        {
            throw new UndoException("Cannot undo - nothing to undo");
        }
        Command currentCommand = commands.get(index.get());
        currentCommand.undo();
//        steno.info("Asked to undo - " + currentCommand.toString());
        index.set(index.get() - 1);
    }

    public void redo() throws UndoException
    {
        if (canRedo.not().get())
        {
            throw new UndoException("Cannot redo - nothing to redo");
        }
        Command followingCommand = commands.get(index.get() + 1);
        followingCommand.redo();
//        steno.info("Asked to redo- " + followingCommand.toString());
        index.set(index.get() + 1);
    }

    public ReadOnlyBooleanProperty getCanRedo()
    {
        return canRedo;
    }

    public ReadOnlyBooleanProperty getCanUndo()
    {
        return canUndo;
    }

    /**
     * Try to merge the previous command with the last run command.
     */
    private void tryMerge()
    {
        if (index.get() < 1)
        {
            return;
        }
        Command lastCommand = commands.get(index.get());
        Command previousCommand = commands.get(index.get() - 1);
        if (previousCommand.canMergeWith(lastCommand))
        {
            previousCommand.merge(lastCommand);
            // remove last command from list
            commands.remove(commands.size() - 1);
            index.set(index.get() - 1);
        }
    }

}
