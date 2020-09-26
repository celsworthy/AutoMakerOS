package celtech.roboxbase.printerControl.model;

/**
 *
 * @author Ian
 */
public enum HeaterMode
{

    /**
     *
     */
    OFF(0),

    /**
     *
     */
    NORMAL(1),

    /**
     *
     */
    FIRST_LAYER(2),

    /**
     *
     */
    FILAMENT_EJECT(3);

    /**
     *
     * @param valueOf
     * @return
     */
    public static HeaterMode modeFromValue(Integer valueOf)
    {
        HeaterMode returnedMode = null;
        
        for (HeaterMode mode : HeaterMode.values())
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

    private HeaterMode(int value)
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
