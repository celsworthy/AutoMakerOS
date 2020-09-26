package celtech.roboxbase.printerControl;

import celtech.roboxbase.BaseLookup;

/**
 *
 * @author ianhudson
 */
public enum PrinterStatus
{

    IDLE("printerStatus.idle"),
    PRINTING_PROJECT("printerStatus.printing"),
    RUNNING_TEST("printerStatus.runningTest"),
    RUNNING_MACRO_FILE("printerStatus.executingMacro"),
    REMOVING_HEAD("printerStatus.removingHead"),
    PURGING_HEAD("printerStatus.purging"),
    OPENING_DOOR("printerStatus.openingDoor"),
    CALIBRATING_NOZZLE_ALIGNMENT("printerStatus.calibratingNozzleAlignment"),
    CALIBRATING_NOZZLE_HEIGHT("printerStatus.calibratingNozzleHeight"),
    CALIBRATING_NOZZLE_OPENING("printerStatus.calibratingNozzleOpening");

    private final String i18nString;

    private PrinterStatus(String i18nString)
    {
        this.i18nString = i18nString;
    }

    /**
     *
     * @return
     */
    public String getI18nString()
    {
        return BaseLookup.i18n(i18nString);
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
