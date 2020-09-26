package celtech.roboxbase.comms.remote;

/**
 *
 * @author Ian
 */
public enum PauseStatus
{

    /**
     *
     */
    NOT_PAUSED(0, null),
    /**
     *
     */
    PAUSE_PENDING(1, "printerStatus.pausing"),
    /**
     *
     */
    PAUSED(2, "printerStatus.paused"),
    /**
     *
     */
    RESUME_PENDING(3, "printerStatus.resuming"),
    /**
     * 
     */
    SELFIE_PAUSE(4, "printerStatus.selfie");
    
    
    /**
     *
     * @param valueOf
     * @return
     */
    public static PauseStatus modeFromValue(Integer valueOf)
    {
        PauseStatus returnedMode = null;

        for (PauseStatus mode : PauseStatus.values())
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
    private final String i18nString;

    private PauseStatus(int value, String i18nString)
    {
        this.value = value;
        this.i18nString = i18nString;
    }

    /**
     *
     * @return
     */
    public int getValue()
    {
        return value;
    }

    /**
     *
     * @return
     */
    public String getI18nString()
    {
        return i18nString;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString()
    {
        return getI18nString();
    }
}
