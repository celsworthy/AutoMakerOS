/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.printerControl.model.statetransitions.calibration;

import celtech.roboxbase.printerControl.model.statetransitions.StateTransitionManager;
import javafx.beans.property.ReadOnlyDoubleProperty;

/**
 *
 * @author tony
 */
public class SingleNozzleHeightStateTransitionManager extends StateTransitionManager<SingleNozzleHeightCalibrationState>
{

    public SingleNozzleHeightStateTransitionManager(StateTransitionActionsFactory stateTransitionActionsFactory,
        TransitionsFactory transitionsFactory)
    {
        super(stateTransitionActionsFactory, transitionsFactory, SingleNozzleHeightCalibrationState.IDLE,
              SingleNozzleHeightCalibrationState.CANCELLING, SingleNozzleHeightCalibrationState.CANCELLED,
              SingleNozzleHeightCalibrationState.FAILED);
    }

    public ReadOnlyDoubleProperty getZcoProperty()
    {
        return ((CalibrationSingleNozzleHeightActions) actions).getZcoGUITProperty();
    }

}
