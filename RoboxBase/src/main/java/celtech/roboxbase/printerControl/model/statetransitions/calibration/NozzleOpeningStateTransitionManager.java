/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.printerControl.model.statetransitions.calibration;

import celtech.roboxbase.printerControl.model.statetransitions.StateTransitionManager;
import celtech.roboxbase.printerControl.model.statetransitions.StateTransitionManager.StateTransitionActionsFactory;
import javafx.beans.property.ReadOnlyFloatProperty;

/**
 *
 * @author tony
 */
public class NozzleOpeningStateTransitionManager extends StateTransitionManager<NozzleOpeningCalibrationState>
{

    public NozzleOpeningStateTransitionManager(
        StateTransitionActionsFactory stateTransitionActionsFactory,
        TransitionsFactory transitionsFactory)
    {
        super(stateTransitionActionsFactory, transitionsFactory, NozzleOpeningCalibrationState.IDLE,
              NozzleOpeningCalibrationState.CANCELLING, NozzleOpeningCalibrationState.CANCELLED,
              NozzleOpeningCalibrationState.FAILED);
    }

    public ReadOnlyFloatProperty getBPositionProperty()
    {
        return ((CalibrationNozzleOpeningActions) actions).getBPositionGUITProperty();
    }

}
