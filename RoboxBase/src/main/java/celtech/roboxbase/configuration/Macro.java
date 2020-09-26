package celtech.roboxbase.configuration;

import celtech.roboxbase.BaseLookup;
import java.util.Optional;

/**
 *
 * @author Ian
 */
public enum Macro
{
    //NOTE - don't change or reuse any of these macro job numbers - just keep on incrementing

    CANCEL_PRINT("abort_print", "printerStatus.macro.cancelling", "M1"),
    HOME_ALL("Home_all", "printerStatus.macro.homing", "M2"),
    LEVEL_GANTRY("level_gantry", "printerStatus.macro.levellingGantry", "M4"),
    LEVEL_GANTRY_TWO_POINTS("level_gantry", "printerStatus.macro.levellingGantry", "M5"),
    SPEED_TEST("speed_test", "printerStatus.macro.speedTest", "M6"),
    TEST_X("x_test", "printerStatus.macro.testX", "M7"),
    TEST_Y("y_test", "printerStatus.macro.testY", "M8"),
    TEST_Z("z_test", "printerStatus.macro.testZ", "M9"),
    LEVEL_Y("level_Y", "printerStatus.macro.levellingY", "M10"),
    CLEAN_NOZZLE("nozzle_clean", "printerStatus.macro.cleanNozzle", "M11"),
    PURGE_MATERIAL("PurgeMaterial", "printerStatus.purging", "M13"),
    EJECT_STUCK_MATERIAL("eject_stuck_material", "printerStatus.macro.ejectStuckMaterial", "M18"),
    REMOVE_HEAD("Remove_Head", "printerStatus.macro.removeHead", "M20"),
    MINI_PURGE("Short_Purge", "printerStatus.macro.miniPurge", "M21"),
    BEFORE_NOZZLE_CALIBRATION("before_Nozzle_Cal", "printerStatus.macro.beforeNozzleCal", "M22"),
    MOTE_INITIATED_PURGE("Mote_PurgeMaterial", "printerStatus.purging", "M23"),
    // Commissionator macros
    COMMISSIONING_XMOTOR("x_commissioning", "printerStatus.macro.testX", "C1"),
    COMMISSIONING_YMOTOR("y_commissioning", "printerStatus.macro.testY", "C2"),
    COMMISSIONING_ZMOTOR_DIRECTION("commissioning_level_gantry_test", "printerStatus.macro.testZ", "C3"),
    COMMISSIONING_HEAD_FLUSH("commissioning_head_flush", "printerStatus.macro.headFlush", "C4"),
    COMMISSIONING_EJECT_STUCK_MATERIAL("commissioning_eject_stuck_material", "printerStatus.macro.ejectStuckMaterial", "C5"),
    COMMISSIONING_EJECT("commissioning_eject", "printerStatus.macro.ejectingFilament", "C6"),
    COMMISSIONING_ARTICULATE("commissioning_articulate", "printerStatus.macro.articulate", "C7");

    private String macroFileName;
    private String i18nKey;
    private String macroJobNumber;

    private Macro(String macroFileName,
            String i18nKey,
            String macroJobNumber)
    {
        this.macroFileName = macroFileName;
        this.i18nKey = i18nKey;
        this.macroJobNumber = macroJobNumber;
    }

    public String getMacroFileName()
    {
        return macroFileName;
    }

    public String getFriendlyName()
    {
        return BaseLookup.i18n(i18nKey);
    }

    public String getMacroJobNumber()
    {
        String jobNumber = String.format("%1$-16s", macroJobNumber).replace(' ', '-');
        return jobNumber;
    }

    public static Optional<Macro> getMacroForPrintJobID(String printJobID)
    {
        Optional<Macro> foundMacro = Optional.empty();

        for (Macro macro : Macro.values())
        {
            if (macro.getMacroJobNumber().equalsIgnoreCase(printJobID.trim()))
            {
                foundMacro = Optional.of(macro);
                break;
            }
        }

        return foundMacro;
    }
}
