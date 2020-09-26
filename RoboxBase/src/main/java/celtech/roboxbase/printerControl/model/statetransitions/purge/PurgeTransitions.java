/*
 * Copyright 2015 CEL UK
 */
package celtech.roboxbase.printerControl.model.statetransitions.purge;

import celtech.roboxbase.printerControl.model.statetransitions.ArrivalAction;
import celtech.roboxbase.printerControl.model.statetransitions.Transitions;
import celtech.roboxbase.printerControl.model.statetransitions.StateTransitionManager;
import celtech.roboxbase.printerControl.model.statetransitions.StateTransition;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author tony
 */
public class PurgeTransitions extends Transitions<PurgeState>
{
    /**
     * In the constructor we must populate the arrivals and transitions.
     */
    public PurgeTransitions(PurgeActions actions)
    {
        arrivals = new HashMap<>();

        arrivals.put(PurgeState.FINISHED,
                     new ArrivalAction<>(() ->
                         {
                             actions.doFinishedAction();
                     },
                                         PurgeState.FAILED));

        arrivals.put(PurgeState.FAILED,
                     new ArrivalAction<>(() ->
                         {
                             actions.doFailedAction();
                     },
                                         PurgeState.FINISHED));

        transitions = new HashSet<>();

        // IDLE -> INITIALISING
        transitions.add(new StateTransition(PurgeState.IDLE,
                                            StateTransitionManager.GUIName.START,
                                            PurgeState.INITIALISING));

        // INITIALISING -> CONFIRM_TEMPERATURE
        transitions.add(new StateTransition(PurgeState.INITIALISING,
                                            StateTransitionManager.GUIName.AUTO,
                                            PurgeState.CONFIRM_TEMPERATURE,
                                            () ->
                                            {
                                                actions.doInitialiseAction();
                                            }));

        // CONFIRM_TEMPERATURE -> HEATING
        transitions.add(new StateTransition(PurgeState.CONFIRM_TEMPERATURE,
                                            StateTransitionManager.GUIName.NEXT,
                                            PurgeState.HEATING));

        // HEATING -> RUNNING_PURGE
        transitions.add(new StateTransition(PurgeState.HEATING,
                                            StateTransitionManager.GUIName.AUTO,
                                            PurgeState.RUNNING_PURGE,
                                            () ->
                                            {
                                                actions.doHeatingAction();
                                            }));

        // RUNNING_PURGE -> FINISHED
        transitions.add(new StateTransition(PurgeState.RUNNING_PURGE,
                                            StateTransitionManager.GUIName.AUTO,
                                            PurgeState.FINISHED,
                                            () ->
                                            {
                                                actions.doRunPurgeAction();
                                            }));

        // FINISHED (OK) -> DONE
        transitions.add(new StateTransition(PurgeState.FINISHED,
                                            StateTransitionManager.GUIName.COMPLETE,
                                            PurgeState.DONE));

        // FINISHED (RETRY) -> INITIALISING
        transitions.add(new StateTransition(PurgeState.FINISHED,
                                            StateTransitionManager.GUIName.RETRY,
                                            PurgeState.INITIALISING));
        
        // FAILED(OK) -> DONE
        transitions.add(new StateTransition(PurgeState.FAILED,
                                            StateTransitionManager.GUIName.COMPLETE,
                                            PurgeState.DONE,
                                            PurgeState.DONE));        
    }

}
