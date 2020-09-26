/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.printerControl.model.statetransitions;

import celtech.roboxbase.printerControl.model.statetransitions.ArrivalAction;
import celtech.roboxbase.printerControl.model.statetransitions.StateTransition;
import celtech.roboxbase.printerControl.model.statetransitions.StateTransitionActions;
import celtech.roboxbase.printerControl.model.statetransitions.Transitions;
import celtech.roboxbase.printerControl.model.statetransitions.StateTransitionManager;
import celtech.roboxbase.printerControl.model.statetransitions.StateTransitionManager.GUIName;
import celtech.roboxbase.utils.BaseEnvironmentConfiguredTest;
import celtech.roboxbase.utils.tasks.Cancellable;
import celtech.roboxbase.utils.tasks.TaskExecutor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.beans.value.ObservableValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author tony
 */
public class StateTransitionManagerTest extends BaseEnvironmentConfiguredTest
{

    enum TestState
    {

        IDLE, PRINT_CIRCLE, GET_Y_OFFSET, DONE, FAILED, CANCELLING, CANCELLED;
    }

    TestStateTransitionManager manager;

    @Before
    public void setUp()
    {
        super.setUp();
        
       StateTransitionManager.StateTransitionActionsFactory actionsFactory = (Cancellable userCancellable,
            Cancellable errorCancellable)
            -> new TestActions(userCancellable, errorCancellable);

        StateTransitionManager.TransitionsFactory transitionsFactory = (StateTransitionActions actions)
            -> new TestTransitions((TestActions) actions);          
        
        manager = new TestStateTransitionManager(actionsFactory, transitionsFactory);
        manager.start();

    }

    @Test
    public void testSetAndGetTransitions()
    {
        Set<StateTransition> allowedTransitions = manager.getTransitions();
        assertEquals(1, allowedTransitions.size());
        assertEquals(GUIName.NEXT, allowedTransitions.iterator().next().guiName);
    }

    @Test
    public void testFollowTransitionFromIdleByNext()
    {
        manager.followTransition(GUIName.NEXT);
        assertEquals(TestState.PRINT_CIRCLE, manager.stateGUITProperty().get());
        assertEquals(10, manager.getX());
    }

    @Test
    public void testFollow2TransitionsFromIdleToGetYOffset()
    {
        manager.followTransition(GUIName.NEXT);
        manager.followTransition(GUIName.NEXT);
        assertEquals(TestState.GET_Y_OFFSET, manager.stateGUITProperty().get());
        assertEquals(11, manager.getX());
    }

    @Test
    public void testFollow2TransitionsFromIdleToPrintCircleToFailed()
    {
        manager.followTransition(GUIName.NEXT);
        manager.followTransition(GUIName.CANCEL);
        assertEquals(TestState.FAILED, manager.stateGUITProperty().get());
        assertEquals(10, manager.getX());
        assertTrue(manager.isCancelled());
    }

    @Test
    public void testFailedTransitionsEndsInFailedState()
    {
        manager.followTransition(GUIName.NEXT);
        manager.followTransition(GUIName.COMPLETE);
        assertEquals(TestState.FAILED, manager.stateGUITProperty().get());
        assertEquals(22, manager.getX());
    }

    @Test
    public void testStateListenerCorrectlyUpdated()
    {
        final List<TestState> states = new ArrayList<>();

        manager.stateGUITProperty().addListener(
            (ObservableValue observable, Object oldValue, Object newValue) ->
            {
                states.add((TestState) newValue);
            });

        manager.followTransition(GUIName.NEXT);
        manager.followTransition(GUIName.NEXT);
        assertEquals(2, states.size());
        assertEquals(TestState.PRINT_CIRCLE, states.get(0));
        assertEquals(TestState.GET_Y_OFFSET, states.get(1));
    }

    @Test
    public void testArrivalActionPerformed()
    {
        manager.followTransition(GUIName.NEXT);
        manager.followTransition(GUIName.NEXT);
        manager.followTransition(GUIName.NEXT);
        assertEquals(43, manager.getX());
    }

    static class TestActions extends StateTransitionActions
    {

        int x = 0;
        boolean cancelled = false;

        private TestActions(Cancellable userCancellable, Cancellable errorCancellable)
        {
            super(userCancellable, errorCancellable);
        }
        
        @Override
        public void initialise()
        {
            int x = 0;
        }        

        private void doAction1()
        {
            x += 10;
        }

        private void doAction1ButFails()
        {
            x += 12;
            throw new RuntimeException();
        }

        private void doDoneAction()
        {
            x = 43;
        }

        private void doAction2()
        {
            x += 1;
        }

        private void cancel()
        {
            cancelled = true;
        }

        @Override
        public void whenUserCancelDetected()
        {
        }

        @Override
        public void whenErrorDetected()
        {
        }

        @Override
        public void resetAfterCancelOrError()
        {
        }
    }

    public class TestTransitions extends Transitions<TestState>
    {

        public TestTransitions(TestActions actions)
        {
            transitions = new HashSet<>();
            transitions.add(new StateTransition(TestState.IDLE,
                                                StateTransitionManager.GUIName.NEXT,
                                                TestState.PRINT_CIRCLE,
                                                () ->
                                                {
                                                    actions.doAction1();
                                                },
                                                TestState.FAILED));

            transitions.add(new StateTransition(TestState.PRINT_CIRCLE,
                                                StateTransitionManager.GUIName.NEXT,
                                                TestState.GET_Y_OFFSET,
                                                () ->
                                                {
                                                    actions.doAction2();
                                                },
                                                TestState.FAILED));

            transitions.add(new StateTransition(TestState.GET_Y_OFFSET,
                                                StateTransitionManager.GUIName.NEXT,
                                                TestState.DONE,
                                                TestState.FAILED));

            transitions.add(new StateTransition(TestState.PRINT_CIRCLE,
                                                StateTransitionManager.GUIName.COMPLETE,
                                                TestState.GET_Y_OFFSET,
                                                () ->
                                                {
                                                    actions.doAction1ButFails();
                                                },
                                                TestState.FAILED));

            transitions.add(new StateTransition(TestState.PRINT_CIRCLE,
                                                StateTransitionManager.GUIName.CANCEL,
                                                TestState.FAILED,
                                                () ->
                                                {
                                                    actions.cancel();
                                                },
                                                TestState.FAILED));

            arrivals = new HashMap<>();

            arrivals.put(TestState.DONE, new ArrivalAction<>((TaskExecutor.NoArgsVoidFunc) () ->
                     {
                         actions.doDoneAction();
            }, TestState.FAILED));

        }
    }

    public class TestStateTransitionManager extends StateTransitionManager
    {

        public TestStateTransitionManager(StateTransitionActionsFactory stateTransitionActionsFactory,
        TransitionsFactory transitionsFactory)
        {
            super(stateTransitionActionsFactory, transitionsFactory, TestState.IDLE, 
                                   TestState.CANCELLING, TestState.CANCELLED, TestState.FAILED);
        }

        public int getX()
        {
            return ((TestActions) actions).x;
        }

        public boolean isCancelled()
        {
            return ((TestActions) actions).cancelled;
        }

    }
}
