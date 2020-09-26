/*
 * Copyright 2014 CEL UK
 */

package celtech.printerControl;

import celtech.roboxbase.comms.remote.EEPROMState;
import celtech.roboxbase.comms.rx.StatusResponse;

/**
 *
 * @author tony
 */
public class TestStatusResponse extends StatusResponse
{
    
    public EEPROMState getHeadEEPROMState()
    {
        return EEPROMState.NOT_PROGRAMMED;
    }
    
}
