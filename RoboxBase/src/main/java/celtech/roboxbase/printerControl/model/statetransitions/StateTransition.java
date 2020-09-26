/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.printerControl.model.statetransitions;

import celtech.roboxbase.printerControl.model.statetransitions.StateTransitionManager.GUIName;
import celtech.roboxbase.utils.tasks.TaskExecutor;

/**
 * StateTransition represents a transition from the fromState to the toState. If the action has not been
 * set then the toState is reached directly. If an action was set then it is called and the
 * toState will be reached if the action did not fail (throw an exception). If the action fails (throws
 * an exception) then the transition goes to the transitionFailedState.
 * If the guiName is AUTO then the transition is run automatically whenever the fromState is reached.
 * @author tony
 */
public class StateTransition<T>
{
    /**
     * The state machine must be in the fromState for this transition to be applicable.
     */
    final T fromState;
    /**
     * The state machine will go to the toState when this transition is followed.
     */
    final T toState;
    /**
     * If the transition fails (the action throws an exception) then the state machine will
     * go to the transitionFailedState.
     */
    final T transitionFailedState;
    /**
     * The GUI action associated with this transition.
     */
    final StateTransitionManager.GUIName guiName;
    /**
     * If an action is declared then it takes no arguments and returns void. To indicate
     * a failure an exception should be raised.
     */
    final TaskExecutor.NoArgsVoidFunc action;

    public StateTransition(T fromState, StateTransitionManager.GUIName guiName, 
        T toState, TaskExecutor.NoArgsVoidFunc action, T failedState)
    {
        this.fromState = fromState;
        this.toState = toState;
        this.guiName = guiName;
        this.action = action;
        transitionFailedState = failedState;
    }
    
    public StateTransition(T fromState, StateTransitionManager.GUIName guiName, 
        T toState, T failedState)
    {
        this(fromState, guiName, toState, null, failedState);
    }    
    
    public StateTransition(T fromState, StateTransitionManager.GUIName guiName, 
        T toState, TaskExecutor.NoArgsVoidFunc action)
    {
        this(fromState, guiName, toState, action, null);
    }
    
    public StateTransition(T fromState, StateTransitionManager.GUIName guiName, 
        T toState)
    {
        this(fromState, guiName, toState, null, null);
    }       
    
    public GUIName getGUIName() {
        return guiName;
    }
}
