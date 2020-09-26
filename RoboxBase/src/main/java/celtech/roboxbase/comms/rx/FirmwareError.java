package celtech.roboxbase.comms.rx;

import celtech.roboxbase.SystemErrorHandlerOptions;
import static celtech.roboxbase.SystemErrorHandlerOptions.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Ian
 */
public enum FirmwareError
{
    /*
     SD_CARD 0
     CHUNK_SEQUENCE 1
     FILE_TOO_LARGE 2
     GCODE_LINE_TOO_LONG 3
     ERROR_USB_RX 4
     ERROR_USB_TX 5
     ERROR_BAD_COMMAND 6
     ERROR_HEAD_EEPROM 7
     ERROR_BAD_FIRMWARE_FILE 8
     ERROR_FLASH_CHECKSUM 9
     ERROR_GCODE_BUFFER_OVERRUN 10
     ERROR_FILE_READ_CLOBBERED 11
     ERROR_MAX_GANTRY_ADJUSTMENT 12
     ERROR_REEL0_EEPROM 13
     ERROR_E_FILAMENT_SLIP 14
     ERROR_D_FILAMENT_SLIP 15
     ERROR_NOZZLE_FLUSH_NEEDED 16
     ERROR_Z_TOP_SWITCH 17
     ERROR_B_STUCK 18
     ERROR_REEL1_EEPROM 19
     ERROR_HEAD_POWER_EEPROM 20
     ERROR_HEAD_POWER_OVERTEMP 21
     ERROR_BED_THERMISTOR 22
     ERROR_NOZZLE0_THERMISTOR 23
     ERROR_NOZZLE1_THERMISTOR 24
     ERROR_B_POSITION_LOST 25
     ERROR_E_LOAD_SLIP 26
     ERROR_D_LOAD_SLIP 27
     ERROR_E_UNLOAD_SLIP 28
     ERROR_D_UNLOAD_SLIP 29
     ERROR_POWEROFF_WHILST_HOT 30
     ERROR_E_NO_FILAMENT 31
     ERROR_D_NO_FILAMENT 32
     ERROR_B_POSITION_WARNING 33
     */

    SD_CARD("error.ERROR_SD_CARD", 0, true, OK_ABORT),
    CHUNK_SEQUENCE("error.ERROR_CHUNK_SEQUENCE", 1, false, OK_ABORT),
    FILE_TOO_LARGE("error.ERROR_FILE_TOO_LARGE", 2, false, OK_ABORT),
    GCODE_LINE_TOO_LONG("error.ERROR_GCODE_LINE_TOO_LONG", 3, false, OK_ABORT),
    USB_RX("error.ERROR_USB_RX", 4, false, OK),
    USB_TX("error.ERROR_USB_TX", 5, false, OK),
    BAD_COMMAND("error.ERROR_BAD_COMMAND", 6, false, OK_ABORT),
    HEAD_EEPROM("error.ERROR_HEAD_EEPROM", 7, false, OK_ABORT),
    BAD_FIRMWARE_FILE("error.ERROR_BAD_FIRMWARE_FILE", 8, false, OK_ABORT),
    FLASH_CHECKSUM("error.ERROR_FLASH_CHECKSUM", 9, false, OK_ABORT),
    GCODE_BUFFER_OVERRUN("error.ERROR_GCODE_BUFFER_OVERRUN", 10, false, OK_ABORT),
    FILE_READ_CLOBBERED("error.ERROR_FILE_READ_CLOBBERED", 11, false, OK_ABORT),
    MAX_GANTRY_ADJUSTMENT("error.ERROR_MAX_GANTRY_ADJUSTMENT", 12, true, OK_ABORT),
    REEL0_EEPROM("error.ERROR_REEL0_EEPROM", 13, false, CLEAR_CONTINUE, ABORT),
    E_FILAMENT_SLIP("error.ERROR_E_FILAMENT_SLIP", 14, false, OK_CONTINUE, ABORT),
    D_FILAMENT_SLIP("error.ERROR_D_FILAMENT_SLIP", 15, false, OK_CONTINUE, ABORT),
    NOZZLE_FLUSH_NEEDED("error.ERROR_NOZZLE_FLUSH_NEEDED", 16, true, CLEAR_CONTINUE, ABORT),
    Z_TOP_SWITCH("error.ERROR_Z_TOP_SWITCH", 17, true, CLEAR_CONTINUE, ABORT),
    B_STUCK("error.ERROR_B_STUCK", 18, true, OK_ABORT),
    REEL1_EEPROM("error.ERROR_REEL1_EEPROM", 19, false, CLEAR_CONTINUE, ABORT),
    HEAD_POWER_EEPROM("error.ERROR_HEAD_POWER_EEPROM", 20, false, OK),
    HEAD_POWER_OVERTEMP("error.ERROR_HEAD_POWER_OVERTEMP", 21, false, CLEAR_CONTINUE, ABORT),
    BED_THERMISTOR("error.ERROR_BED_THERMISTOR", 22, false, OK_ABORT),
    NOZZLE0_THERMISTOR("error.ERROR_NOZZLE0_THERMISTOR", 23, false, OK_ABORT),
    NOZZLE1_THERMISTOR("error.ERROR_NOZZLE1_THERMISTOR", 24, false, OK_ABORT),
    B_POSITION_LOST("error.ERROR_B_POSITION_LOST", 25, false, OK),
    E_LOAD_ERROR("error.ERROR_LOAD", 26, false, OK),
    D_LOAD_ERROR("error.ERROR_LOAD", 27, false, OK),
    E_UNLOAD_ERROR("error.ERROR_UNLOAD", 28, false, OK),
    D_UNLOAD_ERROR("error.ERROR_UNLOAD", 29, false, OK),
    ERROR_POWEROFF_WHILST_HOT("error.ERROR_POWEROFF_WHILST_HOT", 30, true, OK),
    ERROR_E_NO_FILAMENT("error.ERROR_E_NO_FILAMENT", 31, true, OK),
    ERROR_D_NO_FILAMENT("error.ERROR_D_NO_FILAMENT", 32, true, OK),
    B_POSITION_WARNING("error.ERROR_B_POSITION_WARNING", 33, false, OK),
    ERROR_HEAD_SHORTED("error.ERROR_HEAD_SHORTED", 34, true, OK),
    ERROR_X_DRIVER("error.ERROR_X_DRIVER", 35, true, OK),
    ERROR_Y_DRIVER("error.ERROR_Y_DRIVER", 36, true, OK),
    ERROR_ZA_DRIVER("error.ERROR_ZA_DRIVER", 37, true, OK),
    ERROR_ZB_DRIVER("error.ERROR_ZB_DRIVER", 38, true, OK),
    ERROR_E_DRIVER("error.ERROR_E_DRIVER", 39, true, OK),
    ERROR_D_DRIVER("error.ERROR_D_DRIVER", 40, true, OK),
    ERROR_BED_TEMPERATURE_DROOP("error.ERROR_BED_TEMPERATURE_DROOP", 41, true, OK),
    UNKNOWN("error.ERROR_UNKNOWN", -1, false, OK_ABORT),
    PSEUDO_E_FILAMENT_SLIP_WHILST_PAUSED("error.ERROR_E_FILAMENT_SLIP_WHILST_PAUSED", -100, false, OK),
    PSEUDO_D_FILAMENT_SLIP_WHILST_PAUSED("error.ERROR_D_FILAMENT_SLIP_WHILST_PAUSED", -100, false, OK),
    ALL_ERRORS("", -99, false);

    private final String errorText;
    private final int bytePosition;
    private final boolean requireUserToClear;
    private final Set<SystemErrorHandlerOptions> options;

    private FirmwareError(String errorText,
            int bytePosition,
            boolean requireUserToClear,
            SystemErrorHandlerOptions... options)
    {
        this.errorText = errorText;
        this.bytePosition = bytePosition;
        this.requireUserToClear = requireUserToClear;
        this.options = new HashSet(Arrays.asList(options));
    }

    public String getErrorTitleKey()
    {
        return errorText;
    }

    public String getErrorMessageKey()
    {
        return errorText + ".message";
    }

    public Set<SystemErrorHandlerOptions> getOptions()
    {
        return options;
    }

    public int getBytePosition()
    {
        return bytePosition;
    }

    public static FirmwareError fromBytePosition(int bytePosition)
    {
        FirmwareError errorToReturn = FirmwareError.UNKNOWN;

        for (FirmwareError error : FirmwareError.values())
        {
            if (error.getBytePosition() == bytePosition)
            {
                errorToReturn = error;
                break;
            }
        }

        return errorToReturn;
    }

    public boolean isRequireUserToClear()
    {
        return requireUserToClear;
    }
}
