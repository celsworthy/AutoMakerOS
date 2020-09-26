/*
 * Copyright 2015 CEL UK
 */
package celtech.roboxbase.printerControl.model.statetransitions.purge;

import celtech.roboxbase.printerControl.model.statetransitions.StateTransitionManager;
import celtech.roboxbase.configuration.Filament;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javax.print.PrintException;

/**
 * The PurgeStateTransitionManager manages state and transitions between different states, and also
 * functions as the data transfer interface between the StateActions instance and the GUI.
 *
 * @author tony
 */
public class PurgeStateTransitionManager extends StateTransitionManager<PurgeState>
{

    public PurgeStateTransitionManager(StateTransitionActionsFactory stateTransitionActionsFactory,
        TransitionsFactory transitionsFactory)
    {
        super(stateTransitionActionsFactory, transitionsFactory, PurgeState.IDLE,
              PurgeState.CANCELLING, PurgeState.CANCELLED, PurgeState.FAILED);
    }

    public void setPurgeTemperature(int nozzleHeaterNumber, int purgeTemperature)
    {
        ((PurgeActions) actions).setPurgeTemperature(nozzleHeaterNumber, purgeTemperature);
    }

    public ReadOnlyIntegerProperty getLastMaterialTemperature(int nozzleHeaterNumber)
    {
        return ((PurgeActions) actions).getLastMaterialTemperatureProperty(nozzleHeaterNumber);
    }

    public ReadOnlyIntegerProperty getCurrentMaterialTemperature(int nozzleHeaterNumber)
    {
        return ((PurgeActions) actions).getCurrentMaterialTemperatureProperty(nozzleHeaterNumber);
    }

    public ReadOnlyIntegerProperty getPurgeTemperature(int nozzleHeaterNumber)
    {
        return ((PurgeActions) actions).getPurgeTemperatureProperty(nozzleHeaterNumber);
    }

    public void setPurgeFilament(int nozzleHeaterNumber, Filament filament) throws PrintException
    {
        ((PurgeActions) actions).setPurgeFilament(nozzleHeaterNumber, filament);
    }

    public void setPurgeNozzleHeater0(boolean selected)
    {
        ((PurgeActions) actions).setPurgeNozzleHeater0(selected);
    }

    public ReadOnlyBooleanProperty getPurgeNozzleHeater0()
    {
        return ((PurgeActions) actions).getPurgeNozzleHeater0();
    }
    
    public ReadOnlyBooleanProperty getPurgeNozzleHeater1()
    {
        return ((PurgeActions) actions).getPurgeNozzleHeater1();
    }    

    public void setPurgeNozzleHeater1(boolean selected)
    {
        ((PurgeActions) actions).setPurgeNozzleHeater1(selected);
    }

}
