/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.printerControl.model.statetransitions;

import java.util.Map;
import java.util.Set;

/**
 * The Transitions base class provides access to the 
 * allowed transitions {@link #getTransitions() getTransitions} of the state machine and
 * the actions to be performed when a state is arrived at {@link getArrivals() getArrivals}.
 * Actions which are set as part of a transition will run when the transition is initiated
 * and the toState will not be reached until the action has completed. Actions which are
 * declared as arrival actions will start immediately after the arrival state is reached.
 * @author tony
 * @param <StateType>
 */
public abstract class Transitions<StateType> {

    protected Set<StateTransition<StateType>> transitions;
    protected Map<StateType, ArrivalAction<StateType>> arrivals;

    /**
     * Return the set of {@link StateTransition}s that are valid for this state machine.
     * @return 
     */
    public Set<StateTransition<StateType>> getTransitions() {
        return transitions;
    }
    /**
     * Return a Map of state to {@link ArrivalAction} that should run when the given states
     * are reached.
     * @return 
     */
    public Map<StateType, ArrivalAction<StateType>> getArrivals() {
        return arrivals;
    }

}
