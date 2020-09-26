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
public class CalibrationSingleNozzleHeightTransitions extends Transitions<SingleNozzleHeightCalibrationState>
{

    public CalibrationSingleNozzleHeightTransitions(CalibrationSingleNozzleHeightActions actions)
    {
        
        arrivals = new HashMap<>();

        arrivals.put(SingleNozzleHeightCalibrationState.FINISHED,
                     new ArrivalAction<>(() ->
                         {
                             actions.doFinishedAction();
                     },
                                         SingleNozzleHeightCalibrationState.FAILED));

        arrivals.put(SingleNozzleHeightCalibrationState.FAILED,
                     new ArrivalAction<>(() ->
                         {
                             actions.doFailedAction();
                     },
                                         SingleNozzleHeightCalibrationState.DONE));

        transitions = new HashSet<>();

        // IDLE
        transitions.add(new StateTransition(SingleNozzleHeightCalibrationState.IDLE,
                                            StateTransitionManager.GUIName.START,
                                            SingleNozzleHeightCalibrationState.INITIALISING));

        transitions.add(new StateTransition(SingleNozzleHeightCalibrationState.IDLE,
                                            StateTransitionManager.GUIName.BACK,
                                            SingleNozzleHeightCalibrationState.DONE));

        // INITIALISING
        transitions.add(new StateTransition(SingleNozzleHeightCalibrationState.INITIALISING,
                                            StateTransitionManager.GUIName.NEXT,
                                            SingleNozzleHeightCalibrationState.HEATING));

        // HEATING
        transitions.add(new StateTransition(SingleNozzleHeightCalibrationState.HEATING,
                                            StateTransitionManager.GUIName.AUTO,
                                            SingleNozzleHeightCalibrationState.HEAD_CLEAN_CHECK,
                                            () ->
                                            {
                                                actions.doInitialiseAndHeatNozzleAction();
                                            }));

        // HEAD_CLEAN_CHECK
        transitions.add(new StateTransition(SingleNozzleHeightCalibrationState.HEAD_CLEAN_CHECK,
                                            StateTransitionManager.GUIName.NEXT,
                                            SingleNozzleHeightCalibrationState.INSERT_PAPER));

        // INSERT_PAPER 
        transitions.add(new StateTransition(SingleNozzleHeightCalibrationState.INSERT_PAPER,
                                            StateTransitionManager.GUIName.NEXT,
                                            SingleNozzleHeightCalibrationState.PROBING,
                                            () ->
                                            {
                                                actions.doHomeZAction();
                                            }));

        // PROBING
        transitions.add(new StateTransition(SingleNozzleHeightCalibrationState.PROBING,
                                            StateTransitionManager.GUIName.NEXT,
                                            SingleNozzleHeightCalibrationState.BRING_BED_FORWARD,
                                            () ->
                                            {
                                                actions.doLiftHeadAction();
                                            }));

        transitions.add(new StateTransition(SingleNozzleHeightCalibrationState.PROBING,
                                            StateTransitionManager.GUIName.UP,
                                            SingleNozzleHeightCalibrationState.INCREMENT_Z,
                                            () ->
                                            {
                                                actions.doIncrementZAction();
                                            }));

        transitions.add(new StateTransition(SingleNozzleHeightCalibrationState.INCREMENT_Z,
                                            StateTransitionManager.GUIName.AUTO,
                                            SingleNozzleHeightCalibrationState.PROBING,
                                            SingleNozzleHeightCalibrationState.FAILED));

        transitions.add(new StateTransition(SingleNozzleHeightCalibrationState.PROBING,
                                            StateTransitionManager.GUIName.DOWN,
                                            SingleNozzleHeightCalibrationState.DECREMENT_Z,
                                            () ->
                                            {
                                                actions.doDecrementZAction();
                                            }));

        transitions.add(new StateTransition(SingleNozzleHeightCalibrationState.DECREMENT_Z,
                                            StateTransitionManager.GUIName.AUTO,
                                            SingleNozzleHeightCalibrationState.PROBING));

        // BRING_BED_FORWARD
        transitions.add(new StateTransition(SingleNozzleHeightCalibrationState.BRING_BED_FORWARD,
                                            StateTransitionManager.GUIName.AUTO,
                                            SingleNozzleHeightCalibrationState.REPLACE_PEI_BED,
                                            () ->
                                            {
                                                actions.doBringBedToFrontAndRaiseHead();
                                            }));

        // REPLACE_PEI_BED
        transitions.add(new StateTransition(SingleNozzleHeightCalibrationState.REPLACE_PEI_BED,
                                            StateTransitionManager.GUIName.NEXT,
                                            SingleNozzleHeightCalibrationState.FINISHED));

        // FINISHED
        transitions.add(new StateTransition(SingleNozzleHeightCalibrationState.FINISHED,
                                            StateTransitionManager.GUIName.BACK,
                                            SingleNozzleHeightCalibrationState.DONE));

        // FAILED
        transitions.add(new StateTransition(SingleNozzleHeightCalibrationState.FAILED,
                                            StateTransitionManager.GUIName.BACK,
                                            SingleNozzleHeightCalibrationState.DONE,
                                            SingleNozzleHeightCalibrationState.DONE));

        transitions.add(new StateTransition(SingleNozzleHeightCalibrationState.FAILED,
                                            StateTransitionManager.GUIName.RETRY,
                                            SingleNozzleHeightCalibrationState.IDLE,
                                            SingleNozzleHeightCalibrationState.DONE));

    }
}
