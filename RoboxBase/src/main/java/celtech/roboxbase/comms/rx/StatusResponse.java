package celtech.roboxbase.comms.rx;

import celtech.roboxbase.comms.remote.BusyStatus;
import celtech.roboxbase.comms.remote.EEPROMState;
import celtech.roboxbase.comms.remote.FixedDecimalFloatFormat;
import celtech.roboxbase.comms.remote.PauseStatus;
import celtech.roboxbase.comms.remote.WhyAreWeWaitingState;
import celtech.roboxbase.printerControl.model.HeaterMode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 *
 * @author ianhudson
 */
public class StatusResponse extends RoboxRxPacket
{
    /*
     V740 firmware
     status: <0xe1> iiiiiiiiiiiiiiii llllllll p b x y z e d b g h i j a m n k mmmmmmmm nnnnnnnn ccccccccl rrrrrrrr uuuuuuuu dddddddd o pppppppp qqqqqqqq aaaaaaaa r ssssssss tttttttt u c v w x p s xxxxxxxx yyyyyyyy zzzzzzzz bbbbbbbb t eeeeeeee gggggggg hhhhhhhh jjjjjjjj ffffffff kkkkkkkk q
     iiiiiiiiiiiiiiii = id of running job
     llllllll = line # of running job in hex
     p = pause ('0'->normal, '1'->pause pending, '2'->paused, '3'->resume pending)
     b = busy ('0'->not busy, '1'->busy, '2'->loading E filament, '3'->unloading E filament, '4'->loading D filament, '5'->unloading D filament)
     x = X switch state
     y = Y switch state
     z = Z switch state
     e = E switch state
     d = D switch state
     b = nozzle switch state
     g = lid switch state
     h = eject switch state
     i = E index wheel state
     j = D index wheel state
     a = Z top switch state
     m = extruder E ('0'->not present, '1'->present)
     n = extruder D ('0'->not present, '1'->present)
     k = left (0) nozzle heater mode ('0'->off, '1'->normal, '2'->first layer, '3' -> filament eject)
     mmmmmmmm = left (0) nozzle temperature (decimal float format)
     nnnnnnnn = left (0) nozzle target (decimal float format)
     cccccccc = left (0) nozzle first layer target (decimal float format)
     l = right (1) nozzle heater mode ('0'-> off, '1'-<normal, '2'->first layer, '3' -> filament eject)
     rrrrrrrr = right (1) nozzle temperature (decimal float format)
     uuuuuuuu = right (1) nozzle target (decimal float format)
     dddddddd = right (1) nozzle first layer target (decimal float format)
     o = bed heater mode ('0'->off, '1'->normal, '2'->first layer)
     pppppppp = bed temperature (decimal float format)
     qqqqqqqq = bed target (decimal float format)
     aaaaaaaa = bed first layer target (decimal float format)
     r = ambient controller on
     ssssssss = ambient temperature (decimal float format)
     tttttttt = ambient target (decimal float format)
     u = head fan on
     c = why are we waiting ('0'->not waiting, '1'->waiting for bed to cool, '2'->waiting for bed to reach target, '3'->waiting for nozzle to reach target
     v = head EEPROM state ('0'->none, '1'->not valid, '2'->valid)
     w = reel0 EEPROM state ('0'->none, '1'->not valid, '2'->valid)
     x = reel1 EEPROM state ('0'->none, '1'->not valid, '2'->valid)
     p = dual-reel adaptor ('0'->not present, '1'->present)
     s = SD card present
     xxxxxxxx = X position (decimal float format)
     yyyyyyyy = Y position (decimal float format)
     zzzzzzzz = Z position (decimal float format)
     bbbbbbbb = B position (decimal float format)
     t = selected tool (nozzle); '0' or '1'
     eeeeeeee = E filament diameter (decimal float format)
     gggggggg = E filament multiplier (decimal float format)
     hhhhhhhh = D filament diameter (decimal float format)
     jjjjjjjj = D filament multiplier (decimal float format)
     ffffffff = E feed rate multiplier (decimal float format)
     kkkkkkkk = D feed rate multiplier (decimal float format)
     q = head power ('0'->off, '1'->on)
     total length = 221
     */

    private boolean dataIsValid = false;
    /**
     * In v699 t and q are not present
     */
    @JsonIgnore
    private final String charsetToUse = "US-ASCII";
    private String runningPrintJobID = null;
    @JsonIgnore
    private final int runningPrintJobIDBytes = 16;
    private String printJobLineNumberString = null;
    private int printJobLineNumber = 0;
    @JsonIgnore
    private final int printJobLineNumberBytes = 8;
    private boolean xSwitchStatus = false;
    private boolean ySwitchStatus = false;
    private boolean zSwitchStatus = false;
    private PauseStatus pauseStatus = PauseStatus.NOT_PAUSED;
    private BusyStatus busyStatus = BusyStatus.NOT_BUSY;
    private boolean filament1SwitchStatus = false;
    private boolean filament2SwitchStatus = false;
    private boolean nozzleSwitchStatus = false;
    private boolean doorOpen = false;
    private boolean reelButtonPressed = false;
    private boolean EIndexStatus = false;
    private boolean DIndexStatus = false;
    private boolean topZSwitchStatus = false;
    private boolean extruderEPresent = false;
    private boolean extruderDPresent = false;
    private HeaterMode nozzle0HeaterMode = HeaterMode.OFF;
    private String nozzle0HeaterModeString = null;
    private String nozzle0TemperatureString = null;
    private int nozzle0Temperature = 0;
    private String nozzle0TargetTemperatureString = null;
    private int nozzle0TargetTemperature = 0;
    private String nozzle0FirstLayerTargetTemperatureString = null;
    private int nozzle0FirstLayerTargetTemperature = 0;
    private HeaterMode nozzle1HeaterMode = HeaterMode.OFF;
    private String nozzle1HeaterModeString = null;
    private String nozzle1TemperatureString = null;
    private int nozzle1Temperature = 0;
    private String nozzle1TargetTemperatureString = null;
    private int nozzle1TargetTemperature = 0;
    private String nozzle1FirstLayerTargetTemperatureString = null;
    private int nozzle1FirstLayerTargetTemperature = 0;
    private HeaterMode bedHeaterMode = HeaterMode.OFF;
    private String bedHeaterModeString = null;
    private String bedTemperatureString = null;
    private int bedTemperature = 0;
    private String bedTargetTemperatureString = null;
    private int bedTargetTemperature = 0;
    private String bedFirstLayerTargetTemperatureString = null;
    private int bedFirstLayerTargetTemperature = 0;
    private boolean ambientFanOn = false;
    private int ambientTemperature = 0;
    private String ambientTemperatureString = null;
    private String ambientTargetTemperatureString = null;
    private int ambientTargetTemperature = 0;
    private boolean headFanOn = false;
    private EEPROMState headEEPROMState = EEPROMState.NOT_PRESENT;
    private EEPROMState reel0EEPROMState = EEPROMState.NOT_PRESENT;
    private EEPROMState reel1EEPROMState = EEPROMState.NOT_PRESENT;
    private boolean dualReelAdaptorPresent = false;
    private boolean sdCardPresent = false;
    @JsonIgnore
    private final int decimalFloatFormatBytes = 8;
    private float headXPosition = 0;
    private float headYPosition = 0;
    private float headZPosition = 0;
    private float BPosition = 0;
    private int nozzleInUse = 0;
    private float EFilamentDiameter = 0;
    private float EFilamentMultiplier = 0;
    private float DFilamentDiameter = 0;
    private float DFilamentMultiplier = 0;
    private float feedRateEMultiplier = 0;
    private float feedRateDMultiplier = 0;
    private WhyAreWeWaitingState whyAreWeWaitingState = WhyAreWeWaitingState.NOT_WAITING;
    private boolean headPowerOn = false;
    private int hardwareRev = 0;

    public boolean getDataIsValid()
    {
        return dataIsValid;
    }

    public void setDataIsValid(boolean dataIsValid)
    {
        this.dataIsValid = dataIsValid;
    }

    /**
     *
     * @return
     */
    public String getRunningPrintJobID()
    {
        return runningPrintJobID;
    }

    /**
     *
     * @return
     */
    public int getPrintJobLineNumber()
    {
        return printJobLineNumber;
    }

    /**
     *
     * @return
     */
    public boolean isxSwitchStatus()
    {
        return xSwitchStatus;
    }

    /**
     *
     * @return
     */
    public boolean isySwitchStatus()
    {
        return ySwitchStatus;
    }

    /**
     *
     * @return
     */
    public boolean iszSwitchStatus()
    {
        return zSwitchStatus;
    }

    /**
     *
     * @return
     */
    public PauseStatus getPauseStatus()
    {
        return pauseStatus;
    }

    /**
     *
     * @return
     */
    public BusyStatus getBusyStatus()
    {
        return busyStatus;
    }

    /**
     *
     * @return
     */
    public boolean isFilament1SwitchStatus()
    {
        return filament1SwitchStatus;
    }

    /**
     *
     * @return
     */
    public boolean isFilament2SwitchStatus()
    {
        return filament2SwitchStatus;
    }

    /**
     *
     * @return
     */
    public boolean isNozzleSwitchStatus()
    {
        return nozzleSwitchStatus;
    }

    /**
     *
     * @return
     */
    public boolean isDoorOpen()
    {
        return doorOpen;
    }

    /**
     *
     * @return
     */
    public boolean isReelButtonPressed()
    {
        return reelButtonPressed;
    }

    /**
     *
     * @return
     */
    public boolean isEIndexStatus()
    {
        return EIndexStatus;
    }

    /**
     *
     * @return
     */
    public boolean isDIndexStatus()
    {
        return DIndexStatus;
    }

    /**
     *
     * @return
     */
    public boolean isTopZSwitchStatus()
    {
        return topZSwitchStatus;
    }

    /**
     *
     * @return
     */
    public boolean isExtruderEPresent()
    {
        return extruderEPresent;
    }

    /**
     *
     * @return
     */
    public boolean isExtruderDPresent()
    {
        return extruderDPresent;
    }

    /**
     *
     * @return
     */
    public HeaterMode getNozzle0HeaterMode()
    {
        return nozzle0HeaterMode;
    }

    /**
     *
     * @return
     */
    public int getNozzle0Temperature()
    {
        return nozzle0Temperature;
    }

    /**
     *
     * @return
     */
    public int getNozzle0TargetTemperature()
    {
        return nozzle0TargetTemperature;
    }

    /**
     *
     * @return
     */
    public int getNozzle0FirstLayerTargetTemperature()
    {
        return nozzle0FirstLayerTargetTemperature;
    }

    /**
     *
     * @return
     */
    public HeaterMode getNozzle1HeaterMode()
    {
        return nozzle1HeaterMode;
    }

    /**
     *
     * @return
     */
    public int getNozzle1Temperature()
    {
        return nozzle1Temperature;
    }

    /**
     *
     * @return
     */
    public int getNozzle1TargetTemperature()
    {
        return nozzle1TargetTemperature;
    }

    /**
     *
     * @return
     */
    public int getNozzle1FirstLayerTargetTemperature()
    {
        return nozzle1FirstLayerTargetTemperature;
    }

    /**
     *
     * @return
     */
    public HeaterMode getBedHeaterMode()
    {
        return bedHeaterMode;
    }

    /**
     *
     * @return
     */
    public int getBedTemperature()
    {
        return bedTemperature;
    }

    /**
     *
     * @return
     */
    public int getBedTargetTemperature()
    {
        return bedTargetTemperature;
    }

    /**
     *
     * @return
     */
    public int getBedFirstLayerTargetTemperature()
    {
        return bedFirstLayerTargetTemperature;
    }

    /**
     *
     * @return
     */
    public boolean isAmbientFanOn()
    {
        return ambientFanOn;
    }

    /**
     *
     * @return
     */
    public int getAmbientTemperature()
    {
        return ambientTemperature;
    }

    /**
     *
     * @return
     */
    public int getAmbientTargetTemperature()
    {
        return ambientTargetTemperature;
    }

    /**
     *
     * @return
     */
    public boolean isHeadFanOn()
    {
        return headFanOn;
    }

    /**
     *
     * @return
     */
    public EEPROMState getHeadEEPROMState()
    {
        return headEEPROMState;
    }

    public EEPROMState getReel0EEPROMState()
    {
        return reel0EEPROMState;
    }

    public EEPROMState getReel1EEPROMState()
    {
        return reel1EEPROMState;
    }

    public void setReelButtonPressed(boolean reelButtonPressed)
    {
        this.reelButtonPressed = reelButtonPressed;
    }

    public void setDoorOpen(boolean doorOpen)
    {
        this.doorOpen = doorOpen;
    }

    public void setNozzle1FirstLayerTargetTemperature(int nozzle1FirstLayerTargetTemperature)
    {
        this.nozzle1FirstLayerTargetTemperature = nozzle1FirstLayerTargetTemperature;
    }

    public void setNozzleInUse(int nozzleInUse)
    {
        this.nozzleInUse = nozzleInUse;
    }
    
    /**
     *
     * @return
     */
    public EEPROMState getReelEEPROMState(int reelNumber)
    {
        EEPROMState returnValue = EEPROMState.NOT_PRESENT;

        switch (reelNumber)
        {
            case 0:
                returnValue = reel0EEPROMState;
                break;
            case 1:
                returnValue = reel1EEPROMState;
                break;
        }
        return returnValue;
    }

    /**
     *
     * @return
     */
    public boolean isDualReelAdaptorPresent()
    {
        return dualReelAdaptorPresent;
    }

    /**
     *
     * @return
     */
    public boolean issdCardPresent()
    {
        return sdCardPresent;
    }

    /**
     *
     * @return
     */
    public float getHeadXPosition()
    {
        return headXPosition;
    }

    /**
     *
     * @return
     */
    public float getHeadYPosition()
    {
        return headYPosition;
    }

    /**
     *
     * @return
     */
    public float getHeadZPosition()
    {
        return headZPosition;
    }

    /**
     *
     * @return
     */
    public float getBPosition()
    {
        return BPosition;
    }

    /**
     *
     * @return
     */
    public int getNozzleInUse()
    {
        return nozzleInUse;
    }

    /**
     *
     * @return
     */
    public float getEFilamentDiameter()
    {
        return EFilamentDiameter;
    }

    /**
     *
     * @return
     */
    public float getEFilamentMultiplier()
    {
        return EFilamentMultiplier;
    }

    /**
     *
     * @return
     */
    public float getDFilamentDiameter()
    {
        return DFilamentDiameter;
    }

    /**
     *
     * @return
     */
    public float getDFilamentMultiplier()
    {
        return DFilamentMultiplier;
    }

    /**
     *
     * @return
     */
    public float getFeedRateEMultiplier()
    {
        return feedRateEMultiplier;
    }

    /**
     *
     * @return
     */
    public float getFeedRateDMultiplier()
    {
        return feedRateDMultiplier;
    }

    /**
     *
     * @return
     */
    public WhyAreWeWaitingState getWhyAreWeWaitingState()
    {
        return whyAreWeWaitingState;
    }

    /**
     *
     * @return
     */
    public boolean isHeadPowerOn()
    {
        return headPowerOn;
    }

    public void setHeadPowerOn(boolean value)
    {
        headPowerOn = value;
    }
    
    public int getHardwareRev()
    {
        return hardwareRev;
    }
    
    public void setHardwareRev(int hardwareRev)
    {
        this.hardwareRev = hardwareRev;
    }

    public void setRunningPrintJobID(String runningPrintJobID)
    {
        this.runningPrintJobID = runningPrintJobID;
    }

    public void setPrintJobLineNumberString(String printJobLineNumberString)
    {
        this.printJobLineNumberString = printJobLineNumberString;
    }

    public void setPrintJobLineNumber(int printJobLineNumber)
    {
        this.printJobLineNumber = printJobLineNumber;
    }

    public void setxSwitchStatus(boolean xSwitchStatus)
    {
        this.xSwitchStatus = xSwitchStatus;
    }

    public void setySwitchStatus(boolean ySwitchStatus)
    {
        this.ySwitchStatus = ySwitchStatus;
    }

    public void setzSwitchStatus(boolean zSwitchStatus)
    {
        this.zSwitchStatus = zSwitchStatus;
    }

    public void setPauseStatus(PauseStatus pauseStatus)
    {
        this.pauseStatus = pauseStatus;
    }

    public void setBusyStatus(BusyStatus busyStatus)
    {
        this.busyStatus = busyStatus;
    }

    public void setFilament1SwitchStatus(boolean filament1SwitchStatus)
    {
        this.filament1SwitchStatus = filament1SwitchStatus;
    }

    public void setFilament2SwitchStatus(boolean filament2SwitchStatus)
    {
        this.filament2SwitchStatus = filament2SwitchStatus;
    }

    public void setNozzleSwitchStatus(boolean nozzleSwitchStatus)
    {
        this.nozzleSwitchStatus = nozzleSwitchStatus;
    }

    public void setEIndexStatus(boolean EIndexStatus)
    {
        this.EIndexStatus = EIndexStatus;
    }

    public void setDIndexStatus(boolean DIndexStatus)
    {
        this.DIndexStatus = DIndexStatus;
    }

    public void setTopZSwitchStatus(boolean topZSwitchStatus)
    {
        this.topZSwitchStatus = topZSwitchStatus;
    }

    public void setExtruderEPresent(boolean value)
    {
        this.extruderEPresent = value;
    }

    public void setExtruderDPresent(boolean value)
    {
        this.extruderDPresent = value;
    }

    public void setNozzle0HeaterMode(HeaterMode nozzleHeaterMode)
    {
        this.nozzle0HeaterMode = nozzleHeaterMode;
    }

    public void setNozzle1HeaterMode(HeaterMode nozzleHeaterMode)
    {
        this.nozzle1HeaterMode = nozzleHeaterMode;
    }

    public void setNozzle0HeaterModeString(String nozzleHeaterModeString)
    {
        this.nozzle0HeaterModeString = nozzleHeaterModeString;
    }

    public void setNozzle0TemperatureString(String nozzleTemperatureString)
    {
        this.nozzle0TemperatureString = nozzleTemperatureString;
    }

    public void setNozzle0Temperature(int nozzleTemperature)
    {
        this.nozzle0Temperature = nozzleTemperature;
    }

    public void setNozzle1Temperature(int nozzleTemperature)
    {
        this.nozzle1Temperature = nozzleTemperature;
    }

    public void setNozzle0TargetTemperatureString(String nozzleTargetTemperatureString)
    {
        this.nozzle0TargetTemperatureString = nozzleTargetTemperatureString;
    }

    public void setNozzle0TargetTemperature(int nozzleTargetTemperature)
    {
        this.nozzle0TargetTemperature = nozzleTargetTemperature;
    }

    public void setNozzle1TargetTemperature(int nozzleTargetTemperature)
    {
        this.nozzle1TargetTemperature = nozzleTargetTemperature;
    }

    public void setNozzle0FirstLayerTargetTemperatureString(
            String nozzleFirstLayerTargetTemperatureString)
    {
        this.nozzle0FirstLayerTargetTemperatureString = nozzleFirstLayerTargetTemperatureString;
    }

    public void setNozzle0FirstLayerTargetTemperature(int nozzleFirstLayerTargetTemperature)
    {
        this.nozzle0FirstLayerTargetTemperature = nozzleFirstLayerTargetTemperature;
    }

    public void setBedHeaterMode(HeaterMode bedHeaterMode)
    {
        this.bedHeaterMode = bedHeaterMode;
    }

    public void setBedHeaterModeString(String bedHeaterModeString)
    {
        this.bedHeaterModeString = bedHeaterModeString;
    }

    public void setBedTemperatureString(String bedTemperatureString)
    {
        this.bedTemperatureString = bedTemperatureString;
    }

    public void setBedTemperature(int bedTemperature)
    {
        this.bedTemperature = bedTemperature;
    }

    public void setBedTargetTemperatureString(String bedTargetTemperatureString)
    {
        this.bedTargetTemperatureString = bedTargetTemperatureString;
    }

    public void setBedTargetTemperature(int bedTargetTemperature)
    {
        this.bedTargetTemperature = bedTargetTemperature;
    }

    public void setBedFirstLayerTargetTemperatureString(String bedFirstLayerTargetTemperatureString)
    {
        this.bedFirstLayerTargetTemperatureString = bedFirstLayerTargetTemperatureString;
    }

    public void setBedFirstLayerTargetTemperature(int bedFirstLayerTargetTemperature)
    {
        this.bedFirstLayerTargetTemperature = bedFirstLayerTargetTemperature;
    }

    public void setAmbientFanOn(boolean ambientFanOn)
    {
        this.ambientFanOn = ambientFanOn;
    }

    public void setAmbientTemperature(int ambientTemperature)
    {
        this.ambientTemperature = ambientTemperature;
    }

    public void setAmbientTemperatureString(String ambientTemperatureString)
    {
        this.ambientTemperatureString = ambientTemperatureString;
    }

    public void setAmbientTargetTemperatureString(String ambientTargetTemperatureString)
    {
        this.ambientTargetTemperatureString = ambientTargetTemperatureString;
    }

    public void setAmbientTargetTemperature(int ambientTargetTemperature)
    {
        this.ambientTargetTemperature = ambientTargetTemperature;
    }

    public void setHeadFanOn(boolean headFanOn)
    {
        this.headFanOn = headFanOn;
    }

    public void setHeadEEPROMState(EEPROMState headEEPROMState)
    {
        this.headEEPROMState = headEEPROMState;
    }

    public void setReel0EEPROMState(EEPROMState reelEEPROMState)
    {
        this.reel0EEPROMState = reelEEPROMState;
    }

    public void setReel1EEPROMState(EEPROMState reelEEPROMState)
    {
        this.reel1EEPROMState = reelEEPROMState;
    }

    public void setDualReelAdaptorPresent(boolean value)
    {
        this.dualReelAdaptorPresent = value;
    }

    public void setsdCardPresent(boolean sdCardPresent)
    {
        this.sdCardPresent = sdCardPresent;
    }

    public void setHeadXPosition(float headXPosition)
    {
        this.headXPosition = headXPosition;
    }

    public void setHeadYPosition(float headYPosition)
    {
        this.headYPosition = headYPosition;
    }

    public void setHeadZPosition(float headZPosition)
    {
        this.headZPosition = headZPosition;
    }

    public void setBPosition(float BPosition)
    {
        this.BPosition = BPosition;
    }

    public void setEFilamentDiameter(float filamentDiameter)
    {
        this.EFilamentDiameter = filamentDiameter;
    }

    public void setDFilamentDiameter(float filamentDiameter)
    {
        this.DFilamentDiameter = filamentDiameter;
    }

    public void setEFilamentMultiplier(float filamentMultiplier)
    {
        this.EFilamentMultiplier = filamentMultiplier;
    }

    public void setDFilamentMultiplier(float filamentMultiplier)
    {
        this.DFilamentMultiplier = filamentMultiplier;
    }

    public void setFeedRateEMultiplier(float feedRateMultiplier)
    {
        this.feedRateEMultiplier = feedRateMultiplier;
    }

    public void setFeedRateDMultiplier(float feedRateMultiplier)
    {
        this.feedRateDMultiplier = feedRateMultiplier;
    }

    public void setWhyAreWeWaitingState(WhyAreWeWaitingState whyAreWeWaitingState)
    {
        this.whyAreWeWaitingState = whyAreWeWaitingState;
    }

    /*
     * Errors...
     */
    /**
     *
     */
    public StatusResponse()
    {
        super(RxPacketTypeEnum.STATUS_RESPONSE, false, false);
    }

    /**
     *
     * @param byteData
     * @return
     */
    @Override
    public boolean populatePacket(byte[] byteData, float requiredFirmwareVersion)
    {
        setMessagePayloadBytes(byteData);

        boolean success = false;

        FixedDecimalFloatFormat decimalFloatFormatter = new FixedDecimalFloatFormat();

        try
        {
            int byteOffset = 1;

            this.runningPrintJobID = new String(byteData, byteOffset, runningPrintJobIDBytes,
                    charsetToUse);
            byteOffset += runningPrintJobIDBytes;

            this.printJobLineNumberString = new String(byteData, byteOffset, printJobLineNumberBytes,
                    charsetToUse);
            byteOffset += printJobLineNumberBytes;

            this.printJobLineNumber = Integer.valueOf(printJobLineNumberString, 16);

            String pauseStatusString = new String(byteData, byteOffset, 1, charsetToUse);
            byteOffset += 1;
            this.pauseStatus = PauseStatus.modeFromValue(Integer.valueOf(pauseStatusString, 16));

            String busyStatusString = new String(byteData, byteOffset, 1, charsetToUse);
            byteOffset += 1;
            this.busyStatus = BusyStatus.modeFromValue(Integer.valueOf(busyStatusString, 16));

            this.xSwitchStatus = (byteData[byteOffset] & 1) > 0 ? true : false;
            byteOffset += 1;

            this.ySwitchStatus = (byteData[byteOffset] & 1) > 0 ? true : false;
            byteOffset += 1;

            this.zSwitchStatus = (byteData[byteOffset] & 1) > 0 ? true : false;
            byteOffset += 1;

            this.filament1SwitchStatus = (byteData[byteOffset] & 1) > 0 ? true : false;
            byteOffset += 1;

            this.filament2SwitchStatus = (byteData[byteOffset] & 1) > 0 ? true : false;
            byteOffset += 1;

            this.nozzleSwitchStatus = (byteData[byteOffset] & 1) > 0 ? true : false;
            byteOffset += 1;

            this.doorOpen = (byteData[byteOffset] & 1) > 0 ? true : false;
            byteOffset += 1;

            this.reelButtonPressed = (byteData[byteOffset] & 1) > 0 ? false : true;
            byteOffset += 1;

            this.EIndexStatus = (byteData[byteOffset] & 1) > 0 ? true : false;
            byteOffset += 1;

            this.DIndexStatus = (byteData[byteOffset] & 1) > 0 ? true : false;
            byteOffset += 1;

            this.topZSwitchStatus = (byteData[byteOffset] & 1) > 0 ? true : false;
            byteOffset += 1;

            this.extruderEPresent = (byteData[byteOffset] & 1) > 0 ? true : false;
            byteOffset += 1;

            this.extruderDPresent = (byteData[byteOffset] & 1) > 0 ? true : false;
            byteOffset += 1;

            // Left (0) Nozzle
            this.nozzle0HeaterModeString = new String(byteData, byteOffset, 1, charsetToUse);
            byteOffset += 1;
            this.nozzle0HeaterMode = HeaterMode.modeFromValue(Integer.valueOf(
                    nozzle0HeaterModeString, 16));

            this.nozzle0TemperatureString = new String(byteData, byteOffset, decimalFloatFormatBytes,
                    charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                this.nozzle0Temperature = Math.round(decimalFloatFormatter.parse(nozzle0TemperatureString).
                        floatValue());
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse nozzle temperature - " + nozzle0TemperatureString);
            }

            this.nozzle0TargetTemperatureString = new String(byteData, byteOffset,
                    decimalFloatFormatBytes, charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                this.nozzle0TargetTemperature = Math.round(decimalFloatFormatter.parse(
                        nozzle0TargetTemperatureString).floatValue());
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse nozzle target temperature - "
                        + nozzle0TargetTemperatureString);
            }

            this.nozzle0FirstLayerTargetTemperatureString = new String(byteData, byteOffset,
                    decimalFloatFormatBytes,
                    charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                this.nozzle0FirstLayerTargetTemperature = Math.round(decimalFloatFormatter.parse(
                        nozzle0FirstLayerTargetTemperatureString).floatValue());
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse nozzle first layer target temperature - "
                        + nozzle0FirstLayerTargetTemperatureString);
            }

            // Right (1) Nozzle
            this.nozzle1HeaterModeString = new String(byteData, byteOffset, 1, charsetToUse);
            byteOffset += 1;
            this.nozzle1HeaterMode = HeaterMode.modeFromValue(Integer.valueOf(
                    nozzle1HeaterModeString, 16));

            this.nozzle1TemperatureString = new String(byteData, byteOffset, decimalFloatFormatBytes,
                    charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                this.nozzle1Temperature = Math.round(decimalFloatFormatter.parse(nozzle1TemperatureString).
                        floatValue());
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse nozzle temperature - " + nozzle1TemperatureString);
            }

            this.nozzle1TargetTemperatureString = new String(byteData, byteOffset,
                    decimalFloatFormatBytes, charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                this.nozzle1TargetTemperature = Math.round(decimalFloatFormatter.parse(
                        nozzle1TargetTemperatureString).floatValue());
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse nozzle target temperature - "
                        + nozzle1TargetTemperatureString);
            }

            this.nozzle1FirstLayerTargetTemperatureString = new String(byteData, byteOffset,
                    decimalFloatFormatBytes,
                    charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                this.nozzle1FirstLayerTargetTemperature = Math.round(decimalFloatFormatter.parse(
                        nozzle1FirstLayerTargetTemperatureString).floatValue());
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse nozzle first layer target temperature - "
                        + nozzle1FirstLayerTargetTemperatureString);
            }

            this.bedHeaterModeString = new String(byteData, byteOffset, 1, charsetToUse);
            byteOffset += 1;
            this.bedHeaterMode = HeaterMode.modeFromValue(Integer.valueOf(bedHeaterModeString, 16));

            this.bedTemperatureString = new String(byteData, byteOffset, decimalFloatFormatBytes,
                    charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                this.bedTemperature = Math.round(decimalFloatFormatter.parse(bedTemperatureString).floatValue());
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse bed temperature - " + bedTemperatureString);
            }

            this.bedTargetTemperatureString = new String(byteData, byteOffset,
                    decimalFloatFormatBytes, charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                this.bedTargetTemperature = Math.round(decimalFloatFormatter.parse(bedTargetTemperatureString).
                        floatValue());
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse bed target temperature - " + bedTargetTemperatureString);
            }

            this.bedFirstLayerTargetTemperatureString = new String(byteData, byteOffset,
                    decimalFloatFormatBytes,
                    charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                this.bedFirstLayerTargetTemperature = Math.round(decimalFloatFormatter.parse(
                        bedFirstLayerTargetTemperatureString).floatValue());
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse bed first layer target temperature - "
                        + bedFirstLayerTargetTemperatureString);
            }

            this.ambientFanOn = (byteData[byteOffset] & 1) > 0 ? true : false;
            byteOffset += 1;

            this.ambientTemperatureString = new String(byteData, byteOffset, decimalFloatFormatBytes,
                    charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                this.ambientTemperature = Math.round(decimalFloatFormatter.parse(ambientTemperatureString).
                        floatValue());
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse ambient temperature - " + ambientTemperatureString);
            }

            this.ambientTargetTemperatureString = new String(byteData, byteOffset,
                    decimalFloatFormatBytes, charsetToUse);
            byteOffset += decimalFloatFormatBytes;

            try
            {
                this.ambientTargetTemperature = Math.round(decimalFloatFormatter.parse(
                        ambientTargetTemperatureString).floatValue());
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse ambient target temperature - "
                        + ambientTargetTemperatureString);
            }

            this.headFanOn = (byteData[byteOffset] & 1) > 0 ? true : false;
            byteOffset += 1;

            String whyAreWeWaitingStateString = new String(byteData, byteOffset, 1, charsetToUse);
            byteOffset += 1;
            this.whyAreWeWaitingState = WhyAreWeWaitingState.modeFromValue(Integer.valueOf(
                    whyAreWeWaitingStateString, 16));

            String headEEPROMStateString = new String(byteData, byteOffset, 1, charsetToUse);
            byteOffset += 1;
            this.headEEPROMState = EEPROMState.modeFromValue(Integer.valueOf(headEEPROMStateString,
                    16));

            String reel0EEPROMStateString = new String(byteData, byteOffset, 1, charsetToUse);
            byteOffset += 1;
            this.reel0EEPROMState = EEPROMState.modeFromValue(Integer.
                    valueOf(reel0EEPROMStateString, 16));

            String reel1EEPROMStateString = new String(byteData, byteOffset, 1, charsetToUse);
            byteOffset += 1;
            this.reel1EEPROMState = EEPROMState.modeFromValue(Integer.
                    valueOf(reel1EEPROMStateString, 16));

            this.dualReelAdaptorPresent = (byteData[byteOffset] & 1) > 0 ? true : false;
            byteOffset += 1;

            this.sdCardPresent = (byteData[byteOffset] & 1) > 0 ? true : false;
            byteOffset += 1;

            String headXPositionString = new String(byteData, byteOffset, decimalFloatFormatBytes,
                    charsetToUse);
            byteOffset += decimalFloatFormatBytes;
            try
            {
                this.headXPosition = decimalFloatFormatter.parse(headXPositionString).floatValue();
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse head X position - " + headXPositionString);
            }

            String headYPositionString = new String(byteData, byteOffset, decimalFloatFormatBytes,
                    charsetToUse);
            byteOffset += decimalFloatFormatBytes;
            try
            {
                this.headYPosition = decimalFloatFormatter.parse(headYPositionString).floatValue();
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse head Y position - " + headYPositionString);
            }

            String headZPositionString = new String(byteData, byteOffset, decimalFloatFormatBytes,
                    charsetToUse);
            byteOffset += decimalFloatFormatBytes;
            try
            {
                this.headZPosition = decimalFloatFormatter.parse(headZPositionString).floatValue();
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse head Z position - " + headZPositionString);
            }

            String BPositionString = new String(byteData, byteOffset, decimalFloatFormatBytes,
                    charsetToUse);
            byteOffset += decimalFloatFormatBytes;
            try
            {
                this.BPosition = decimalFloatFormatter.parse(BPositionString).floatValue();
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse B position - " + BPositionString);
            }

            if (requiredFirmwareVersion >= 701)
            {
                String nozzleInUseString = new String(byteData, byteOffset, 1, charsetToUse);
                try
                {
                    this.nozzleInUse = Math.round(decimalFloatFormatter.parse(nozzleInUseString).floatValue());
                } catch (ParseException ex)
                {
                    steno.error("Couldn't parse nozzle in use - " + nozzleInUseString);
                }
                byteOffset += 1;
            }

            // E Filament
            String filamentDiameterString = new String(byteData, byteOffset, decimalFloatFormatBytes,
                    charsetToUse);
            byteOffset += decimalFloatFormatBytes;
            try
            {
                this.EFilamentDiameter = decimalFloatFormatter.parse(filamentDiameterString).
                        floatValue();
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse filament diameter - " + filamentDiameterString);
            }

            String filamentMultiplierString = new String(byteData, byteOffset,
                    decimalFloatFormatBytes, charsetToUse);
            byteOffset += decimalFloatFormatBytes;
            try
            {
                this.EFilamentMultiplier = decimalFloatFormatter.parse(filamentMultiplierString).
                        floatValue();
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse filament multiplier - " + filamentMultiplierString);
            }

            // D Filament
            String DfilamentDiameterString = new String(byteData, byteOffset,
                    decimalFloatFormatBytes, charsetToUse);
            byteOffset += decimalFloatFormatBytes;
            try
            {
                this.DFilamentDiameter = decimalFloatFormatter.parse(DfilamentDiameterString).
                        floatValue();
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse filament diameter - " + DfilamentDiameterString);
            }

            String DfilamentMultiplierString = new String(byteData, byteOffset,
                    decimalFloatFormatBytes, charsetToUse);
            byteOffset += decimalFloatFormatBytes;
            try
            {
                this.DFilamentMultiplier = decimalFloatFormatter.parse(DfilamentMultiplierString).
                        floatValue();
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse filament multiplier - " + DfilamentMultiplierString);
            }

            String feedRateDMultiplierString = new String(byteData, byteOffset,
                    decimalFloatFormatBytes, charsetToUse);
            byteOffset += decimalFloatFormatBytes;
            try
            {
                this.feedRateDMultiplier = decimalFloatFormatter.parse(feedRateDMultiplierString).
                        floatValue();
            } catch (ParseException ex)
            {
                steno.error("Couldn't parse D feed rate multiplier - " + feedRateDMultiplierString);
            }

            if (requiredFirmwareVersion >= 740)
            {
                String feedRateEMultiplierString = new String(byteData, byteOffset,
                        decimalFloatFormatBytes, charsetToUse);
                byteOffset += decimalFloatFormatBytes;
                try
                {
                    this.feedRateEMultiplier = decimalFloatFormatter.parse(feedRateEMultiplierString).
                            floatValue();
                } catch (ParseException ex)
                {
                    steno.error("Couldn't parse E feed rate multiplier - " + feedRateEMultiplierString);
                }
            }

            if (requiredFirmwareVersion >= 724)
            {
                this.headPowerOn = (byteData[byteOffset] & 1) > 0;
                byteOffset += 1;
            }
            
            if (requiredFirmwareVersion >= 768)
            {
                String hardwareRevString = new String(byteData, byteOffset, 1, charsetToUse);
                byteOffset += 1;
                try
                {
                    this.hardwareRev = Math.round(decimalFloatFormatter.parse(hardwareRevString).floatValue());
                } catch (ParseException ex)
                {
                    steno.error("Couldn't parse nozzle in use - " + hardwareRevString);
                }
            }

            success = true;
        } catch (UnsupportedEncodingException ex)
        {
            steno.error("Failed to convert byte array to Status Response");
        }

        this.dataIsValid = success;
        
        return success;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder outputString = new StringBuilder();

        outputString.append(">>>>>>>>>>\n");
        outputString.append("Packet type:");
        outputString.append(getPacketType().name());
        outputString.append("\n");
        outputString.append("Print job ID: " + getRunningPrintJobID());
        outputString.append("\n");
        outputString.append("Print line number: " + getPrintJobLineNumber());
        outputString.append("\n");
        outputString.append("Pause status: " + getPauseStatus().name());
        outputString.append("\n");
        outputString.append("Busy status: " + getBusyStatus().name());
        outputString.append("\n");
        outputString.append("X switch status: " + isxSwitchStatus());
        outputString.append("\n");
        outputString.append("Y switch status: " + isySwitchStatus());
        outputString.append("\n");
        outputString.append("Z switch status: " + iszSwitchStatus());
        outputString.append("\n");
        outputString.append("Filament 1 switch status: " + isFilament1SwitchStatus());
        outputString.append("\n");
        outputString.append("Filament 2 switch status: " + isFilament2SwitchStatus());
        outputString.append("\n");
        outputString.append("Nozzle switch status: " + isNozzleSwitchStatus());
        outputString.append("\n");
        outputString.append("Door open: " + isDoorOpen());
        outputString.append("\n");
        outputString.append("Reel button pressed: " + isReelButtonPressed());
        outputString.append("\n");
        outputString.append("E index status: " + isEIndexStatus());
        outputString.append("\n");
        outputString.append("D index status: " + isDIndexStatus());
        outputString.append("\n");
        outputString.append("Top Z switch status: " + isTopZSwitchStatus());
        outputString.append("\n");
        outputString.append("Extruder E present: " + isExtruderEPresent());
        outputString.append("\n");
        outputString.append("Extruder D present: " + isExtruderDPresent());
        outputString.append("\n");
        outputString.append("Nozzle 0 heater mode: " + getNozzle0HeaterMode());
        outputString.append("\n");
        outputString.append("Nozzle 0 first layer target temperature: "
                + getNozzle0FirstLayerTargetTemperature());
        outputString.append("\n");
        outputString.append("Nozzle 0 target temperature: " + getNozzle0TargetTemperature());
        outputString.append("\n");
        outputString.append("Nozzle 0 temperature: " + getNozzle0Temperature());
        outputString.append("\n");
        outputString.append("Nozzle 1 heater mode: " + getNozzle1HeaterMode());
        outputString.append("\n");
        outputString.append("Nozzle 1 first layer target temperature: "
                + getNozzle1FirstLayerTargetTemperature());
        outputString.append("\n");
        outputString.append("Nozzle 1 target temperature: " + getNozzle1TargetTemperature());
        outputString.append("\n");
        outputString.append("Nozzle 1 temperature: " + getNozzle1Temperature());
        outputString.append("\n");
        outputString.append("Bed heater on: " + getBedHeaterMode());
        outputString.append("\n");
        outputString.append("Bed temperature: " + getBedTemperature());
        outputString.append("\n");
        outputString.append("Bed target temperature: " + getBedTargetTemperature());
        outputString.append("\n");
        outputString.append("Bed first layer target temperature: "
                + getBedFirstLayerTargetTemperature());
        outputString.append("\n");
        outputString.append("Ambient fan on: " + isAmbientFanOn());
        outputString.append("\n");
        outputString.append("Ambient temperature: " + getAmbientTemperature());
        outputString.append("\n");
        outputString.append("Ambient target temperature: " + getAmbientTargetTemperature());
        outputString.append("\n");
        outputString.append("Head fan on: " + isHeadFanOn());
        outputString.append("\n");
        outputString.append("Head EEPROM present: " + getHeadEEPROMState());
        outputString.append("\n");
        outputString.append("Reel 0 EEPROM present: " + getReelEEPROMState(0));
        outputString.append("\n");
        outputString.append("Reel 1 EEPROM present: " + getReelEEPROMState(1));
        outputString.append("\n");
        outputString.append("Dual reel adaptor present: " + isDualReelAdaptorPresent());
        outputString.append("\n");
        outputString.append("SD card present: " + issdCardPresent());
        outputString.append("\n");
        outputString.append("Head X Position: " + getHeadXPosition());
        outputString.append("\n");
        outputString.append("Head Y Position: " + getHeadYPosition());
        outputString.append("\n");
        outputString.append("Head Z Position: " + getHeadZPosition());
        outputString.append("\n");
        outputString.append("Head B Position: " + getBPosition());
        outputString.append("\n");
        outputString.append("Nozzle in use: " + getNozzleInUse());
        outputString.append("\n");
        outputString.append("E Filament diameter: " + EFilamentDiameter);
        outputString.append("\n");
        outputString.append("E Filament multiplier: " + EFilamentMultiplier);
        outputString.append("\n");
        outputString.append("D Filament diameter: " + DFilamentDiameter);
        outputString.append("\n");
        outputString.append("D Filament multiplier: " + DFilamentMultiplier);
        outputString.append("\n");
        outputString.append("E Feed rate multiplier: " + feedRateEMultiplier);
        outputString.append("\n");
        outputString.append("D Feed rate multiplier: " + feedRateDMultiplier);
        outputString.append("\n");
        outputString.append("Head power on: " + headPowerOn);
        outputString.append("\n");
        outputString.append("Hardware revision: " + hardwareRev);
        outputString.append("\n");
        outputString.append(">>>>>>>>>>\n");

        return outputString.toString();
    }

    @Override
    public int packetLength(float requiredFirmwareVersion)
    {
        if (requiredFirmwareVersion >= 768 || requiredFirmwareVersion == RoboxRxPacketFactory.USE_LATEST_FIRMWARE_VERSION)
        {
            return 222;
        }
        if (requiredFirmwareVersion >= 740)
        {
            return 221;
        } else if (requiredFirmwareVersion >= 724)
        {
            return 213;
        } else if (requiredFirmwareVersion >= 701)
        {
            return 212;
        } else
        {
            return 211;
        }
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(13, 33).
                append(runningPrintJobID).
                append(printJobLineNumberString).
                append(printJobLineNumber).
                append(xSwitchStatus).
                append(ySwitchStatus).
                append(zSwitchStatus).
                append(pauseStatus).
                append(busyStatus).
                append(filament1SwitchStatus).
                append(filament2SwitchStatus).
                append(nozzleSwitchStatus).
                append(doorOpen).
                append(reelButtonPressed).
                append(EIndexStatus).
                append(DIndexStatus).
                append(topZSwitchStatus).
                append(extruderEPresent).
                append(extruderDPresent).
                append(nozzle0HeaterMode).
                append(nozzle0HeaterModeString).
                append(nozzle0TemperatureString).
                append(nozzle0Temperature).
                append(nozzle0TargetTemperatureString).
                append(nozzle0TargetTemperature).
                append(nozzle0FirstLayerTargetTemperatureString).
                append(nozzle0FirstLayerTargetTemperature).
                append(nozzle1HeaterMode).
                append(nozzle1HeaterModeString).
                append(nozzle1TemperatureString).
                append(nozzle1Temperature).
                append(nozzle1TargetTemperatureString).
                append(nozzle1TargetTemperature).
                append(nozzle1FirstLayerTargetTemperatureString).
                append(nozzle1FirstLayerTargetTemperature).
                append(bedHeaterMode).
                append(bedHeaterModeString).
                append(bedTemperatureString).
                append(bedTemperature).
                append(bedTargetTemperatureString).
                append(bedTargetTemperature).
                append(bedFirstLayerTargetTemperatureString).
                append(bedFirstLayerTargetTemperature).
                append(ambientFanOn).
                append(ambientTemperature).
                append(ambientTemperatureString).
                append(ambientTargetTemperatureString).
                append(ambientTargetTemperature).
                append(headFanOn).
                append(headEEPROMState).
                append(reel0EEPROMState).
                append(reel1EEPROMState).
                append(dualReelAdaptorPresent).
                append(sdCardPresent).
                append(headXPosition).
                append(headYPosition).
                append(headZPosition).
                append(BPosition).
                append(nozzleInUse).
                append(EFilamentDiameter).
                append(EFilamentMultiplier).
                append(DFilamentDiameter).
                append(DFilamentMultiplier).
                append(feedRateEMultiplier).
                append(feedRateDMultiplier).
                append(whyAreWeWaitingState).
                append(headPowerOn).
                append(hardwareRev).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof StatusResponse))
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }

        StatusResponse rhs = (StatusResponse) obj;
        return new EqualsBuilder().
                append(runningPrintJobID, rhs.runningPrintJobID).
                append(printJobLineNumberString, rhs.printJobLineNumberString).
                append(printJobLineNumber, rhs.printJobLineNumber).
                append(xSwitchStatus, rhs.xSwitchStatus).
                append(ySwitchStatus, rhs.ySwitchStatus).
                append(zSwitchStatus, rhs.zSwitchStatus).
                append(pauseStatus, rhs.pauseStatus).
                append(busyStatus, rhs.busyStatus).
                append(filament1SwitchStatus, rhs.filament1SwitchStatus).
                append(filament2SwitchStatus, rhs.filament2SwitchStatus).
                append(nozzleSwitchStatus, rhs.nozzleSwitchStatus).
                append(doorOpen, rhs.doorOpen).
                append(reelButtonPressed, rhs.reelButtonPressed).
                append(EIndexStatus, rhs.EIndexStatus).
                append(DIndexStatus, rhs.DIndexStatus).
                append(topZSwitchStatus, rhs.topZSwitchStatus).
                append(extruderEPresent, rhs.extruderEPresent).
                append(extruderDPresent, rhs.extruderDPresent).
                append(nozzle0HeaterMode, rhs.nozzle0HeaterMode).
                append(nozzle0HeaterModeString, rhs.nozzle0HeaterModeString).
                append(nozzle0TemperatureString, rhs.nozzle0TemperatureString).
                append(nozzle0Temperature, rhs.nozzle0Temperature).
                append(nozzle0TargetTemperatureString, rhs.nozzle0TargetTemperatureString).
                append(nozzle0TargetTemperature, rhs.nozzle0TargetTemperature).
                append(nozzle0FirstLayerTargetTemperatureString, rhs.nozzle0FirstLayerTargetTemperatureString).
                append(nozzle0FirstLayerTargetTemperature, rhs.nozzle0FirstLayerTargetTemperature).
                append(nozzle1HeaterMode, rhs.nozzle1HeaterMode).
                append(nozzle1HeaterModeString, rhs.nozzle1HeaterModeString).
                append(nozzle1TemperatureString, rhs.nozzle1TemperatureString).
                append(nozzle1Temperature, rhs.nozzle1Temperature).
                append(nozzle1TargetTemperatureString, rhs.nozzle1TargetTemperatureString).
                append(nozzle1TargetTemperature, rhs.nozzle1TargetTemperature).
                append(nozzle1FirstLayerTargetTemperatureString, rhs.nozzle1FirstLayerTargetTemperatureString).
                append(nozzle1FirstLayerTargetTemperature, rhs.nozzle1FirstLayerTargetTemperature).
                append(bedHeaterMode, rhs.bedHeaterMode).
                append(bedHeaterModeString, rhs.bedHeaterModeString).
                append(bedTemperatureString, rhs.bedTemperatureString).
                append(bedTemperature, rhs.bedTemperature).
                append(bedTargetTemperatureString, rhs.bedTargetTemperatureString).
                append(bedTargetTemperature, rhs.bedTargetTemperature).
                append(bedFirstLayerTargetTemperatureString, rhs.bedFirstLayerTargetTemperatureString).
                append(bedFirstLayerTargetTemperature, rhs.bedFirstLayerTargetTemperature).
                append(ambientFanOn, rhs.ambientFanOn).
                append(ambientTemperature, rhs.ambientTemperature).
                append(ambientTemperatureString, rhs.ambientTemperatureString).
                append(ambientTargetTemperatureString, rhs.ambientTargetTemperatureString).
                append(ambientTargetTemperature, rhs.ambientTargetTemperature).
                append(headFanOn, rhs.headFanOn).
                append(headEEPROMState, rhs.headEEPROMState).
                append(reel0EEPROMState, rhs.reel0EEPROMState).
                append(reel1EEPROMState, rhs.reel1EEPROMState).
                append(dualReelAdaptorPresent, rhs.dualReelAdaptorPresent).
                append(sdCardPresent, rhs.sdCardPresent).
                append(headXPosition, rhs.headXPosition).
                append(headYPosition, rhs.headYPosition).
                append(headZPosition, rhs.headZPosition).
                append(BPosition, rhs.BPosition).
                append(nozzleInUse, rhs.nozzleInUse).
                append(EFilamentDiameter, rhs.EFilamentDiameter).
                append(EFilamentMultiplier, rhs.EFilamentMultiplier).
                append(DFilamentDiameter, rhs.DFilamentDiameter).
                append(DFilamentMultiplier, rhs.DFilamentMultiplier).
                append(feedRateEMultiplier, rhs.feedRateEMultiplier).
                append(feedRateDMultiplier, rhs.feedRateDMultiplier).
                append(whyAreWeWaitingState, rhs.whyAreWeWaitingState).
                append(headPowerOn, rhs.headPowerOn).
                append(hardwareRev, rhs.hardwareRev).
                isEquals();
        // if deriving: appendSuper(super.equals(obj)).
    }
}
