/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.printerControl.model.statetransitions.calibration;

import celtech.roboxbase.printerControl.model.statetransitions.StateTransitionManager;
import celtech.roboxbase.printerControl.model.statetransitions.StateTransitionManager.StateTransitionActionsFactory;
import celtech.roboxbase.printerControl.model.statetransitions.StateTransitionManager.TransitionsFactory;

/**
 *
 * @author tony
 */
public class XAndYStateTransitionManager extends StateTransitionManager<CalibrationXAndYState>
{

    public XAndYStateTransitionManager(StateTransitionActionsFactory stateTransitionActionsFactory,
        TransitionsFactory transitionsFactory)
    {
        super(stateTransitionActionsFactory, transitionsFactory, CalibrationXAndYState.IDLE, 
                      CalibrationXAndYState.CANCELLING, CalibrationXAndYState.CANCELLED,
                      CalibrationXAndYState.FAILED);
    }

    public void setXOffset(String xOffset)
    {
        ((CalibrationXAndYActions) actions).setXOffset(xOffset);
    }

    public void setYOffset(int yOffset)
    {
        ((CalibrationXAndYActions) actions).setYOffset(yOffset);
    }
}
