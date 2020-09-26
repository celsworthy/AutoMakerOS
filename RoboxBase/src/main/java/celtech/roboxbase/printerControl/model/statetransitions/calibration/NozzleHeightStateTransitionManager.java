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
public class NozzleHeightStateTransitionManager extends StateTransitionManager<NozzleHeightCalibrationState>
{

    public NozzleHeightStateTransitionManager(StateTransitionActionsFactory stateTransitionActionsFactory,
        TransitionsFactory transitionsFactory)
    {
        super(stateTransitionActionsFactory, transitionsFactory, NozzleHeightCalibrationState.IDLE,
              NozzleHeightCalibrationState.CANCELLING, NozzleHeightCalibrationState.CANCELLED,
              NozzleHeightCalibrationState.FAILED);
    }

    public ReadOnlyDoubleProperty getZcoProperty()
    {
        return ((CalibrationNozzleHeightActions) actions).getZcoGUITProperty();
    }

}
