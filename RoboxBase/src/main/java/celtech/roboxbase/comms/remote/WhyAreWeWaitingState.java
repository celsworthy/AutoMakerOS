/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase.comms.remote;

/**
 *
 * @author Ian
 */
public enum WhyAreWeWaitingState
{

    /**
     *
     */
    NOT_WAITING(0),

    /**
     *
     */
    COOLING(1),

    /**
     *
     */
    BED_HEATING(2),

    /**
     *
     */
    NOZZLE_HEATING(3);

    /**
     *
     * @param valueOf
     * @return
     */
    public static WhyAreWeWaitingState modeFromValue(Integer valueOf)
    {
        WhyAreWeWaitingState returnedMode = null;

        for (WhyAreWeWaitingState mode : WhyAreWeWaitingState.values())
        {
            if (mode.getValue() == valueOf)
            {
                returnedMode = mode;
                break;
            }
        }

        return returnedMode;
    }

    private int value;

    private WhyAreWeWaitingState(int value)
    {
        this.value = value;
    }

    /**
     *
     * @return
     */
    public int getValue()
    {
        return value;
    }
}
