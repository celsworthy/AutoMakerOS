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
public class CalibrationNozzleHeightTransitions extends Transitions<NozzleHeightCalibrationState>
{

    public CalibrationNozzleHeightTransitions(CalibrationNozzleHeightActions actions)
    {
        
        arrivals = new HashMap<>();

        arrivals.put(NozzleHeightCalibrationState.FINISHED,
                     new ArrivalAction<>(() ->
                         {
                             actions.doFinishedAction();
                     },
                                         NozzleHeightCalibrationState.FAILED));

        arrivals.put(NozzleHeightCalibrationState.FAILED,
                     new ArrivalAction<>(() ->
                         {
                             actions.doFailedAction();
                     },
                                         NozzleHeightCalibrationState.DONE));

        transitions = new HashSet<>();

        // IDLE
        transitions.add(new StateTransition(NozzleHeightCalibrationState.IDLE,
                                            StateTransitionManager.GUIName.START,
                                            NozzleHeightCalibrationState.INITIALISING));

        transitions.add(new StateTransition(NozzleHeightCalibrationState.IDLE,
                                            StateTransitionManager.GUIName.BACK,
                                            NozzleHeightCalibrationState.DONE));

        // INITIALISING
        transitions.add(new StateTransition(NozzleHeightCalibrationState.INITIALISING,
                                            StateTransitionManager.GUIName.NEXT,
                                            NozzleHeightCalibrationState.HEATING));

        // HEATING
        transitions.add(new StateTransition(NozzleHeightCalibrationState.HEATING,
                                            StateTransitionManager.GUIName.AUTO,
                                            NozzleHeightCalibrationState.HEAD_CLEAN_CHECK,
                                            () ->
                                            {
                                                actions.doInitialiseAndHeatNozzleAction();
                                            }));

        // HEAD_CLEAN_CHECK
        transitions.add(new StateTransition(NozzleHeightCalibrationState.HEAD_CLEAN_CHECK,
                                            StateTransitionManager.GUIName.NEXT,
                                            NozzleHeightCalibrationState.MEASURE_Z_DIFFERENCE));

        // MEASURE_Z_DIFFERENCE
        transitions.add(new StateTransition(NozzleHeightCalibrationState.MEASURE_Z_DIFFERENCE,
                                            StateTransitionManager.GUIName.AUTO,
                                            NozzleHeightCalibrationState.INSERT_PAPER,
                                            () ->
                                            {
                                                actions.doMeasureZDifferenceAction();
                                            }));

        // INSERT_PAPER 
        transitions.add(new StateTransition(NozzleHeightCalibrationState.INSERT_PAPER,
                                            StateTransitionManager.GUIName.NEXT,
                                            NozzleHeightCalibrationState.PROBING,
                                            () ->
                                            {
                                                actions.doHomeZAction();
                                            }));

        // PROBING
        transitions.add(new StateTransition(NozzleHeightCalibrationState.PROBING,
                                            StateTransitionManager.GUIName.NEXT,
                                            NozzleHeightCalibrationState.BRING_BED_FORWARD,
                                            () ->
                                            {
                                                actions.doLiftHeadAction();
                                            }));

        transitions.add(new StateTransition(NozzleHeightCalibrationState.PROBING,
                                            StateTransitionManager.GUIName.UP,
                                            NozzleHeightCalibrationState.INCREMENT_Z,
                                            () ->
                                            {
                                                actions.doIncrementZAction();
                                            }));

        transitions.add(new StateTransition(NozzleHeightCalibrationState.INCREMENT_Z,
                                            StateTransitionManager.GUIName.AUTO,
                                            NozzleHeightCalibrationState.PROBING,
                                            NozzleHeightCalibrationState.FAILED));

        transitions.add(new StateTransition(NozzleHeightCalibrationState.PROBING,
                                            StateTransitionManager.GUIName.DOWN,
                                            NozzleHeightCalibrationState.DECREMENT_Z,
                                            () ->
                                            {
                                                actions.doDecrementZAction();
                                            }));

        transitions.add(new StateTransition(NozzleHeightCalibrationState.DECREMENT_Z,
                                            StateTransitionManager.GUIName.AUTO,
                                            NozzleHeightCalibrationState.PROBING));

        // BRING_BED_FORWARD
        transitions.add(new StateTransition(NozzleHeightCalibrationState.BRING_BED_FORWARD,
                                            StateTransitionManager.GUIName.AUTO,
                                            NozzleHeightCalibrationState.REPLACE_PEI_BED,
                                            () ->
                                            {
                                                actions.doBringBedToFrontAndRaiseHead();
                                            }));

        // REPLACE_PEI_BED
        transitions.add(new StateTransition(NozzleHeightCalibrationState.REPLACE_PEI_BED,
                                            StateTransitionManager.GUIName.NEXT,
                                            NozzleHeightCalibrationState.FINISHED));

        // FINISHED
        transitions.add(new StateTransition(NozzleHeightCalibrationState.FINISHED,
                                            StateTransitionManager.GUIName.BACK,
                                            NozzleHeightCalibrationState.DONE));

        // FAILED
        transitions.add(new StateTransition(NozzleHeightCalibrationState.FAILED,
                                            StateTransitionManager.GUIName.BACK,
                                            NozzleHeightCalibrationState.DONE,
                                            NozzleHeightCalibrationState.DONE));

        transitions.add(new StateTransition(NozzleHeightCalibrationState.FAILED,
                                            StateTransitionManager.GUIName.RETRY,
                                            NozzleHeightCalibrationState.IDLE,
                                            NozzleHeightCalibrationState.DONE));

    }
}
