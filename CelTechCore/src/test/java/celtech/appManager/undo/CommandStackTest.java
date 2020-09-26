/*
 * Copyright 2015 CEL UK
 */
package celtech.appManager.undo;

import celtech.ConfiguredTest;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tony
 */
public class CommandStackTest extends ConfiguredTest
{

    public class TestModel
    {
        public int i = 0;
    }

    public class IncrementCommand extends Command
    {

        private TestModel testModel;
        private int increment;
        private int oldI;

        public IncrementCommand(TestModel testModel, int increment)
        {
            this.testModel = testModel;
            this.increment = increment;
        }

        @Override
        public void do_()
        {
            oldI = testModel.i;
            redo();
        }

        @Override
        public void undo()
        {
            testModel.i = oldI;
        }

        @Override
        public void redo()
        {
            testModel.i += increment;
        }

        @Override
        public boolean canMergeWith(Command command)
        {
            return (command instanceof IncrementCommand);
        }

        @Override
        public void merge(Command command)
        {
            IncrementCommand incrementCommand = (IncrementCommand) command;
            increment += incrementCommand.getIncrement();
        }
        
        public int getIncrement() {
            return increment;
        }

    }

    @Test
    public void testDo_()
    {
        TestModel testModel = new TestModel();
        testModel.i = 5;

        CommandStack commandStack = new CommandStack();
        IncrementCommand incrementCommand = new IncrementCommand(testModel, 6);
        commandStack.do_(incrementCommand);

        assertEquals(5 + 6, testModel.i);

    }

    @Test
    public void testUndo() throws CommandStack.UndoException
    {
        TestModel testModel = new TestModel();
        testModel.i = 5;

        CommandStack commandStack = new CommandStack();
        IncrementCommand incrementCommand = new IncrementCommand(testModel, 6);
        commandStack.do_(incrementCommand);
        assertEquals(5 + 6, testModel.i);

        commandStack.undo();
        assertEquals(5, testModel.i);

    }

    @Test
    public void testRedo() throws CommandStack.UndoException
    {
        TestModel testModel = new TestModel();
        testModel.i = 5;

        CommandStack commandStack = new CommandStack();
        IncrementCommand incrementCommand = new IncrementCommand(testModel, 6);
        commandStack.do_(incrementCommand);
        assertEquals(5 + 6, testModel.i);

        commandStack.undo();
        assertEquals(5, testModel.i);

        commandStack.redo();
        assertEquals(5 + 6, testModel.i);

    }

    @Test
    public void testCanRedo() throws CommandStack.UndoException
    {
        TestModel testModel = new TestModel();
        testModel.i = 5;

        CommandStack commandStack = new CommandStack();
        IncrementCommand incrementCommand = new IncrementCommand(testModel, 6);
        commandStack.do_(incrementCommand);
        assertEquals(5 + 6, testModel.i);

        commandStack.undo();
        assertEquals(5, testModel.i);
        assertTrue(commandStack.getCanRedo().get());

        commandStack.redo();
        assertEquals(5 + 6, testModel.i);
        assertFalse(commandStack.getCanRedo().get());
    }

    @Test
    public void testCanUndo() throws CommandStack.UndoException
    {
        TestModel testModel = new TestModel();
        testModel.i = 5;

        CommandStack commandStack = new CommandStack();
        IncrementCommand incrementCommand = new IncrementCommand(testModel, 6);
        commandStack.do_(incrementCommand);
        assertEquals(5 + 6, testModel.i);
        assertTrue(commandStack.getCanUndo().get());

        commandStack.undo();
        assertEquals(5, testModel.i);
        assertFalse(commandStack.getCanUndo().get());

        commandStack.redo();
        assertEquals(5 + 6, testModel.i);
        assertTrue(commandStack.getCanUndo().get());
    }

    @Test
    public void testMergeCommands() throws CommandStack.UndoException
    {
        TestModel testModel = new TestModel();
        testModel.i = 5;

        CommandStack commandStack = new CommandStack();
        IncrementCommand incrementCommand = new IncrementCommand(testModel, 6);
        commandStack.do_(incrementCommand);
        
        IncrementCommand incrementCommand2 = new IncrementCommand(testModel, 8);
        commandStack.do_(incrementCommand2);
        assertEquals(5 + 6 + 8, testModel.i);

        // single undo should undo two previous commands which have been merged
        commandStack.undo();
        assertEquals(5, testModel.i);
        
        // should be no commands left in stack
        assertFalse(commandStack.getCanUndo().get());
        commandStack.redo();
        // should be no more commands in stack
        assertFalse(commandStack.getCanRedo().get());


    }
}
