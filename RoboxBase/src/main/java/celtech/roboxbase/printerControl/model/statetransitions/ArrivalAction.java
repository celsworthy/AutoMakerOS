/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.printerControl.model.statetransitions;

import celtech.roboxbase.utils.tasks.TaskExecutor;

/**
 * ArrivalAction represents an action to be called (when a state is arrived at).
 * If the action fails then the governing 
 * {@link StateTransitionManager} should move to the {@link #failedState}.
 * @author tony
 */
public class ArrivalAction<StateType>
{
    /**
     * The callable to call. It should return a boolean, true indicates the action succeeded and
     * false indicates the action was cancelled. To indicate a failure the action must throw an
     * exception.
     */
    final TaskExecutor.NoArgsVoidFunc action;
    /**
     * The state to go to if this actions fails (ie an exception is thrown by the {@link #action}).
     */
    StateType failedState;

    public ArrivalAction(TaskExecutor.NoArgsVoidFunc action, StateType failedState)
    {
        this.action = action;
        this.failedState = failedState;
    }
}
