package celtech.roboxbase.printerControl.comms.commands;

/**
 *
 * @author ianhudson
 */
public class GCodeConstants
{

    /**
     *
     */
    public static final String extruderRelativeMoveMode = "M83";

    /**
     *
     */
    public static final String carriageRelativeMoveMode = "G91";

    /**
     *
     */
    public static final String carriageAbsoluteMoveMode = "G90";

    /**
     *
     */
    public static final String setAmbientTemperature = "M170 S";

    /**
     *
     */
    public static final String setFirstLayerBedTemperatureTarget = "M139 S";

    /**
     *
     */
    public static final String setBedTemperatureTarget = "M140 S";

    /**
     *
     */
    public static final String goToTargetFirstLayerBedTemperature = "M139";
    public static final String goToTargetFirstLayerBedTemperatureE = "M139 E";
    public static final String goToTargetFirstLayerBedTemperatureD = "M139 D";

    /**
     *
     */
    public static final String goToTargetBedTemperature = "M140";

    /**
     *
     */
    public static final String waitForBedTemperature = "M190";

    /**
     *
     */
    public static final String switchBedHeaterOff = "M140 S0";

    /**
     *
     */
    public static final String setFirstLayerNozzleHeaterTemperatureTarget0 = "M103 S";
     public static final String setFirstLayerNozzleHeaterTemperatureTarget1 = "M103 T";

    /**
     *
     */
    public static final String setNozzleHeaterTemperatureTarget0 = "M104 S";
    public static final String setNozzleHeaterTemperatureTarget1 = "M104 T";

    /**
     *
     */
    public static final String goToTargetFirstLayerNozzleHeaterTemperature0 = "M103 S";
    public static final String goToTargetFirstLayerNozzleHeaterTemperature1 = "M103 T";

    /**
     *
     */
    public static final String goToTargetNozzleHeaterTemperature0 = "M104 S";
    public static final String goToTargetNozzleHeaterTemperature1 = "M104 T";

    /**
     *
     */
    public static final String waitForNozzleHeaterTemperature0 = "M109 S";
    public static final String waitForNozzleHeaterTemperature1 = "M109 T";

    /**
     *
     */
    public static final String switchNozzleHeaterOff0 = "M104 S0";
    public static final String switchNozzleHeaterOff1 = "M104 T0";

    /**
     *
     */
    public static final String homeXAxis = "G28 X";

    /**
     *
     */
    public static final String homeYAxis = "G28 Y";

    /**
     *
     */
    public static final String homeZAxis = "G28 Z";

    /**
     *
     */
    public static final String probeZAxis = "G28 Z?";

    /**
     *
     */
    public static final String queryZHomeDelta = "M113";

    /**
     *
     */
    public static final String switchOnHeadLEDs = "M129";

    /**
     *
     */
    public static final String switchOffHeadLEDs = "M128";

    /**
     *
     */
    public static final String homeNozzle = "G28 B";

    /**
     *
     */
    public static final String closeNozzle = "G0 B0";

    /**
     *
     */
    public static final String openNozzle = "G0 B1";
    
    /**
     *
     */
    public static final String openNozzleExtra = "G0 B2";

    /**
     *
     */
    public static final String extrude = "G1 E ";

    /**
     *
     */
    public static final String selectNozzle = "T";

    /**
     *
     */
    public static final String disableStepperMotorsUntilNextMove = "M84";

    /**
     *
     */
    public static final String ejectFilament = "M121";

    /**
     *
     */
    public static final String setHeadFanSpeed = "M106 S";

    /**
     *
     */
    public static final String switchOnHeadFan = "M106";

    /**
     *
     */
    public static final String switchOffHeadFan = "M107";

    /**
     *
     */
    public static final String goToOpenDoorPosition = "G37";
    
    /**
     * Don't wait for safe temp before opening door.
     */
    public static final String goToOpenDoorPositionDontWait = "G37 S";
}
