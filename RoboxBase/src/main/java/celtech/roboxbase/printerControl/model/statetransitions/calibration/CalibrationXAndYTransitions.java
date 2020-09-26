/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.printerControl.model.statetransitions.calibration;

import celtech.roboxbase.printerControl.model.statetransitions.ArrivalAction;
import celtech.roboxbase.printerControl.model.statetransitions.StateTransition;
import celtech.roboxbase.printerControl.model.statetransitions.StateTransitionManager;
import celtech.roboxbase.printerControl.model.statetransitions.Transitions;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author tony
 */
public class CalibrationXAndYTransitions extends Transitions<CalibrationXAndYState>
{

    public CalibrationXAndYTransitions(CalibrationXAndYActions actions)
    {
        
        arrivals = new HashMap<>();

        arrivals.put(CalibrationXAndYState.FINISHED,
                     new ArrivalAction<>(() ->
                         {
                             actions.doFinishedAction();
                     },
                                         CalibrationXAndYState.FAILED));

        arrivals.put(CalibrationXAndYState.FAILED,
                     new ArrivalAction<>(() ->
                         {
                             actions.doFailedAction();
                     },
                                         CalibrationXAndYState.DONE));
        
        transitions = new HashSet<>();

        // IDLE
        transitions.add(new StateTransition(CalibrationXAndYState.IDLE,
                                            StateTransitionManager.GUIName.START,
                                            CalibrationXAndYState.PRINT_PATTERN,
                                            () ->
                                            {
                                                actions.doSaveHead();
                                            }));

        transitions.add(new StateTransition(CalibrationXAndYState.IDLE,
                                            StateTransitionManager.GUIName.BACK,
                                            CalibrationXAndYState.DONE));

        // PRINT PATTERN
        transitions.add(new StateTransition(CalibrationXAndYState.PRINT_PATTERN,
                                            StateTransitionManager.GUIName.AUTO,
                                            CalibrationXAndYState.GET_Y_OFFSET,
                                            () ->
                                            {
                                                actions.doPrintPattern();
                                            }));

        // GET Y OFFSET
        transitions.add(new StateTransition(CalibrationXAndYState.GET_Y_OFFSET,
                                            StateTransitionManager.GUIName.NEXT,
                                            CalibrationXAndYState.FINISHED));

//        // PRINT CIRCLE
//        transitions.add(new StateTransition(CalibrationXAndYState.PRINT_CIRCLE,
//                                            StateTransitionManager.GUIName.AUTO,
//                                            CalibrationXAndYState.PRINT_CIRCLE_CHECK,
//                                            () ->
//                                            {
//                                                actions.doSaveSettingsAndPrintCircle();
//                                            },
//                                            CalibrationXAndYState.FAILED));
//
//        // PRINT CIRCLE CHECK
//        transitions.add(new StateTransition(CalibrationXAndYState.PRINT_CIRCLE_CHECK,
//                                            StateTransitionManager.GUIName.NEXT,
//                                            CalibrationXAndYState.FINISHED,
//                                            CalibrationXAndYState.FAILED));
//
//        transitions.add(new StateTransition(CalibrationXAndYState.PRINT_CIRCLE_CHECK,
//                                            StateTransitionManager.GUIName.RETRY,
//                                            CalibrationXAndYState.PRINT_PATTERN,
//                                            CalibrationXAndYState.FAILED));
        // FINISHED
        transitions.add(new StateTransition(CalibrationXAndYState.FINISHED,
                                            StateTransitionManager.GUIName.BACK,
                                            CalibrationXAndYState.DONE));

        // FAILED
        transitions.add(new StateTransition(CalibrationXAndYState.FAILED,
                                            StateTransitionManager.GUIName.BACK,
                                            CalibrationXAndYState.DONE,
                                            CalibrationXAndYState.DONE));

        transitions.add(new StateTransition(CalibrationXAndYState.FAILED,
                                            StateTransitionManager.GUIName.RETRY,
                                            CalibrationXAndYState.IDLE,
                                            CalibrationXAndYState.DONE));

    }
}
