package celtech.roboxbase.comms;

import celtech.roboxbase.comms.exceptions.RoboxCommsException;
import celtech.roboxbase.comms.remote.EEPROMState;
import celtech.roboxbase.comms.remote.PauseStatus;
import celtech.roboxbase.comms.rx.AckResponse;
import celtech.roboxbase.comms.rx.FirmwareError;
import celtech.roboxbase.comms.rx.FirmwareResponse;
import celtech.roboxbase.comms.rx.GCodeDataResponse;
import celtech.roboxbase.comms.rx.HeadEEPROMDataResponse;
import celtech.roboxbase.comms.rx.PrinterIDResponse;
import celtech.roboxbase.comms.rx.ReelEEPROMDataResponse;
import celtech.roboxbase.comms.rx.RoboxRxPacket;
import celtech.roboxbase.comms.rx.RoboxRxPacketFactory;
import celtech.roboxbase.comms.rx.RxPacketTypeEnum;
import celtech.roboxbase.comms.rx.StatusResponse;
import celtech.roboxbase.comms.tx.AbortPrint;
import celtech.roboxbase.comms.tx.FormatHeadEEPROM;
import celtech.roboxbase.comms.tx.InitiatePrint;
import celtech.roboxbase.comms.tx.PausePrint;
import celtech.roboxbase.comms.tx.QueryFirmwareVersion;
import celtech.roboxbase.comms.tx.ReadHeadEEPROM;
import celtech.roboxbase.comms.tx.ReadPrinterID;
import celtech.roboxbase.comms.tx.ReadReel0EEPROM;
import celtech.roboxbase.comms.tx.ReadReel1EEPROM;
import celtech.roboxbase.comms.tx.ReportErrors;
import celtech.roboxbase.comms.tx.RoboxTxPacket;
import celtech.roboxbase.comms.tx.SendDataFileChunk;
import celtech.roboxbase.comms.tx.SendDataFileEnd;
import celtech.roboxbase.comms.tx.SendDataFileStart;
import celtech.roboxbase.comms.tx.SendGCodeRequest;
import celtech.roboxbase.comms.tx.SendPrintFileStart;
import celtech.roboxbase.comms.tx.SendResetErrors;
import celtech.roboxbase.comms.tx.StatusRequest;
import celtech.roboxbase.comms.tx.WriteHeadEEPROM;
import celtech.roboxbase.comms.tx.WriteReel0EEPROM;
import celtech.roboxbase.comms.tx.WriteReel1EEPROM;
import celtech.roboxbase.configuration.datafileaccessors.FilamentContainer;
import celtech.roboxbase.configuration.datafileaccessors.HeadContainer;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.fileRepresentation.HeadFile;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.HeaterMode;
import celtech.roboxbase.printerControl.model.Reel;
import javafx.scene.paint.Color;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class DummyPrinterCommandInterface extends CommandInterface
{

    private static final Stenographer STENO = StenographerFactory.getStenographer(
            DummyPrinterCommandInterface.class.getName());

    private static final String SM_HEAD = "RBX01-SM";
    private static final String S2_HEAD = "RBX01-S2";
    private static final String DM_HEAD = "RBX01-DM";
    private static final String SX_HEAD = "RBXDV-S1";
    
    private static final String FILAMENT_BLACK = "RBX-ABS-BK091";
    
    public static final String dummyYear = "1901DUMMY$";

    public static final String defaultRoboxAttachCommand = "DEFAULT";
    public static final String defaultRoboxAttachCommand2 = "DEFAULS";
    public static final String defaultRoboxAttachCommand3 = "DEFAULT3";
    
    private final String printerModel;
    
    private final String attachHeadCommand = "ATTACH HEAD ";
    private final String detachHeadCommand = "DETACH HEAD";
    private final String attachReelCommand = "ATTACH REEL ";
    private final String detachReelCommand = "DETACH REEL ";
    private final String detachPrinterCommand = "DETACH PRINTER";
    private final String goToPrintLineCommand = "GOTO LINE ";
    private final String finishPrintCommand = "FINISH PRINT";
    private final String attachExtruderCommand = "ATTACH EXTRUDER ";
    private final String detachExtruderCommand = "DETACH EXTRUDER ";
    private final String loadFilamentCommand = "LOAD ";
    private final String unloadFilamentCommand = "UNLOAD ";
    private final String insertSDCardCommand = "INSERT SD";
    private final String removeSDCardCommand = "REMOVE SD";
    private final String errorCommand = "ERROR ";

    private final StatusResponse currentStatus = (StatusResponse) RoboxRxPacketFactory.createPacket(
            RxPacketTypeEnum.STATUS_RESPONSE);
    private final AckResponse errorStatus = (AckResponse) RoboxRxPacketFactory.createPacket(
            RxPacketTypeEnum.ACK_WITH_ERRORS);
    private Head attachedHead = null;
    private final Reel[] attachedReels = new Reel[2];
    private String printerName;

    private static String NOTHING_PRINTING_JOB_ID = "\0000";
    private String printJobID = NOTHING_PRINTING_JOB_ID;
    protected int printJobLineNo = 0;
    private int linesInCurrentPrintJob = 0;

    private static int ROOM_TEMPERATURE = 20;
    HeaterMode nozzleHeaterModeS = HeaterMode.OFF;
    HeaterMode nozzleHeaterModeT = HeaterMode.OFF;
    protected int currentNozzleTemperatureS = ROOM_TEMPERATURE;
    protected int currentNozzleTemperatureT = ROOM_TEMPERATURE;
    protected int nozzleTargetTemperatureS = 210;
    protected int nozzleTargetTemperatureT = 210;
    HeaterMode bedHeaterMode = HeaterMode.OFF;
    protected int currentBedTemperature = ROOM_TEMPERATURE;
    protected int bedTargetTemperature = 30;
    
    private FilamentContainer filamentContainer;

    public DummyPrinterCommandInterface(PrinterStatusConsumer controlInterface,
            DetectedDevice printerHandle,
            boolean suppressPrinterIDChecks, 
            int sleepBetweenStatusChecks, 
            String printerName,
            String printerModel)
    {
        super(controlInterface, printerHandle, suppressPrinterIDChecks, sleepBetweenStatusChecks, true);
        this.setName(printerName);
        this.printerName = printerName;
        this.printerModel = printerModel;
        filamentContainer = FilamentContainer.getInstance();

        currentStatus.setsdCardPresent(true);
    }

    public DummyPrinterCommandInterface(PrinterStatusConsumer controlInterface,
            DetectedDevice printerHandle, boolean suppressPrinterIDChecks, 
            int sleepBetweenStatusChecks)
    {
        this(controlInterface, printerHandle, suppressPrinterIDChecks, sleepBetweenStatusChecks,
                "Dummy Printer", "RBX01");
        filamentContainer = FilamentContainer.getInstance();
    }

    @Override
    protected void setSleepBetweenStatusChecks(int sleepMillis)
    {
    }

    private void handleNozzleTempChange()
    {
        if (nozzleHeaterModeS != HeaterMode.OFF && currentNozzleTemperatureS
                < nozzleTargetTemperatureS)
        {
            currentNozzleTemperatureS += 10;
            if (currentNozzleTemperatureS > nozzleTargetTemperatureS)
            {
                currentNozzleTemperatureS = nozzleTargetTemperatureS;
            }
        } else if (nozzleHeaterModeS == HeaterMode.OFF && currentNozzleTemperatureS
                > ROOM_TEMPERATURE)
        {
            currentNozzleTemperatureS -= 10;
            if (currentNozzleTemperatureS < ROOM_TEMPERATURE)
            {
                currentNozzleTemperatureS = ROOM_TEMPERATURE;
            }
        }
        if (nozzleHeaterModeT != HeaterMode.OFF && currentNozzleTemperatureT
                < nozzleTargetTemperatureT)
        {
            currentNozzleTemperatureT += 7;
            if (currentNozzleTemperatureT > nozzleTargetTemperatureT)
            {
                currentNozzleTemperatureT = nozzleTargetTemperatureT;
            }
        } else if (nozzleHeaterModeT == HeaterMode.OFF && currentNozzleTemperatureT
                > ROOM_TEMPERATURE)
        {
            currentNozzleTemperatureT -= 7;
            if (currentNozzleTemperatureT < ROOM_TEMPERATURE)
            {
                currentNozzleTemperatureT = ROOM_TEMPERATURE;
            }
        }
        currentStatus.setNozzle0HeaterMode(nozzleHeaterModeS);
        currentStatus.setNozzle0Temperature(currentNozzleTemperatureS);
        currentStatus.setNozzle0TargetTemperature(nozzleTargetTemperatureS);
        currentStatus.setNozzle1HeaterMode(nozzleHeaterModeT);
        currentStatus.setNozzle1Temperature(currentNozzleTemperatureT);
        currentStatus.setNozzle1TargetTemperature(nozzleTargetTemperatureT);
    }

    private void handleBedTempChange()
    {
        if (bedHeaterMode != HeaterMode.OFF && currentBedTemperature < bedTargetTemperature)
        {
            currentBedTemperature += 5;
            if (currentBedTemperature > bedTargetTemperature)
            {
                currentBedTemperature = bedTargetTemperature;
            }
        } else if (bedHeaterMode == HeaterMode.OFF && currentBedTemperature > ROOM_TEMPERATURE)
        {
            currentBedTemperature -= 5;
            if (currentBedTemperature < ROOM_TEMPERATURE)
            {
                currentBedTemperature = ROOM_TEMPERATURE;
            }
        }
        currentStatus.setBedHeaterMode(bedHeaterMode);
        currentStatus.setBedTemperature(currentBedTemperature);
        currentStatus.setBedTargetTemperature(bedTargetTemperature);
    }

    private void detachReel(int reelNumber)
    {
        switch (reelNumber)
        {
            case 0:
                currentStatus.setReel0EEPROMState(EEPROMState.NOT_PRESENT);
                break;
            case 1:
                currentStatus.setReel1EEPROMState(EEPROMState.NOT_PRESENT);
                break;
        }
        attachedReels[reelNumber] = null;
    }

    private boolean attachReel(Filament filament, int reelNumber) throws NumberFormatException
    {
        boolean success = false;

        if (filament != null && reelNumber >= 0 && reelNumber <= 2)
        {
            switch (reelNumber)
            {
                case 0:
                    currentStatus.setReel0EEPROMState(EEPROMState.PROGRAMMED);
                    currentStatus.setFilament1SwitchStatus(true);
                    break;
                case 1:
                    currentStatus.setReel1EEPROMState(EEPROMState.PROGRAMMED);
                    currentStatus.setFilament2SwitchStatus(true);
                    break;
            }
            attachedReels[reelNumber] = new Reel();
            attachedReels[reelNumber].updateContents(filament);
            success = true;
        }

        return success;
    }

    @Override
    protected boolean connectToPrinterImpl()
    {
        STENO.info("Dummy printer connected");
        return true;
    }

    @Override
    protected void disconnectPrinterImpl()
    {
        STENO.info("Dummy printer disconnected");
    }
    
    public void setupHead(String headName) {
        detachExtruder(0);
        detachExtruder(1);
        detachReel(0);
        detachReel(1);
        
        switch(headName) {
            case SM_HEAD:
            case S2_HEAD:
            case SX_HEAD:
                attachExtruder(0);
                break;
            case DM_HEAD:
                attachExtruder(0);
                attachExtruder(1);
                break;
            default:
                if (headName.startsWith("RX") && headName.length() == 8) {
                    // Version 2 head code.
                    int feedCount = ((headName.charAt(7) - 48) & 3);
                    if (feedCount > 0)
                        attachExtruder(0);
                    if (feedCount > 1)
                        attachExtruder(1);
                }
                else
                    STENO.warning("Attempted to attach head of: " + headName + " onto dummy printer");
        }
        
        attachHead(headName);
    }

    private boolean attachHead(String headName)
    {
        boolean success = false;

        HeadFile headData = HeadContainer.getHeadByID(headName);

        if (headData != null)
        {
            currentStatus.setHeadEEPROMState(EEPROMState.PROGRAMMED);
            attachedHead = new Head(headData);
            currentStatus.setHeadPowerOn(true);
            success = true;
        } else if (headName.equalsIgnoreCase("BLANK"))
        {
            currentStatus.setHeadEEPROMState(EEPROMState.PROGRAMMED);
            attachedHead = new Head();
            success = true;
        } else if (headName.equalsIgnoreCase("UNFORMATTED"))
        {
            currentStatus.setHeadEEPROMState(EEPROMState.NOT_PROGRAMMED);
            attachedHead = new Head();
            success = true;
        } else if (headName.equalsIgnoreCase("BADTYPE"))
        {
            currentStatus.setHeadEEPROMState(EEPROMState.PROGRAMMED);
            attachedHead = new Head();
            attachedHead.typeCodeProperty().set("WRONG");
            success = true;
        } else if (headName.equalsIgnoreCase("UNREAL"))
        {
            currentStatus.setHeadEEPROMState(EEPROMState.PROGRAMMED);
            attachedHead = new Head();
            attachedHead.typeCodeProperty().set("RBX01-??");
            success = true;
        }

        return success;
    }

    private boolean attachExtruder(int extruderNumber)
    {
        boolean success = false;

        switch (extruderNumber)
        {
            case 0:
                currentStatus.setExtruderEPresent(true);
                success = true;
                break;
            case 1:
                currentStatus.setExtruderDPresent(true);
                success = true;
                break;
            default:
        }

        return success;
    }

    private boolean detachExtruder(int extruderNumber)
    {
        boolean success = false;

        switch (extruderNumber)
        {
            case 0:
                currentStatus.setExtruderEPresent(false);
                success = true;
                break;
            case 1:
                currentStatus.setExtruderDPresent(false);
                success = true;
                break;
            default:
        }

        return success;
    }

    private void setPrintLine(int printLineNumber)
    {
        currentStatus.setPrintJobLineNumber(printLineNumber);
    }

    protected void finishPrintJob()
    {
        currentStatus.setPrintJobLineNumberString("");
        currentStatus.setRunningPrintJobID("");
    }

    protected void raiseError(FirmwareError error)
    {
        errorStatus.getFirmwareErrors().add(error);
    }

    public void clearError(FirmwareError error)
    {
        errorStatus.getFirmwareErrors().remove(error);
    }

    public void clearAllErrors()
    {
        errorStatus.getFirmwareErrors().clear();
    }

    public void doStatusRequest()
    {
        RoboxRxPacket response = null;

        currentStatus.setAmbientTemperature((int) (Math.random() * 100));
        handleNozzleTempChange();
        handleBedTempChange();

        if (!printJobID.equals(NOTHING_PRINTING_JOB_ID))
        {
            printJobLineNo += 100;

            if (printJobLineNo >= linesInCurrentPrintJob)
            {
                printJobLineNo = 0;
                printJobID = NOTHING_PRINTING_JOB_ID;
            }
        }
        currentStatus.setPrintJobLineNumber(printJobLineNo);
        currentStatus.setRunningPrintJobID(printJobID);
        currentStatus.setHeadPowerOn(true);

        response = (RoboxRxPacket) currentStatus;
        printerToUse.processRoboxResponse(response);
    }

    @Override
    public RoboxRxPacket writeToPrinterImpl(RoboxTxPacket messageToWrite, boolean dontPublishResult) throws RoboxCommsException
    {
        RoboxRxPacket response = null;

//        steno.debug("Dummy printer received " + messageToWrite.getPacketType().name());
        if (messageToWrite instanceof QueryFirmwareVersion)
        {
            FirmwareResponse firmwareResponse = (FirmwareResponse) RoboxRxPacketFactory.
                    createPacket(RxPacketTypeEnum.FIRMWARE_RESPONSE);
            firmwareResponse.setFirmwareRevision("r" + requiredFirmwareVersionString);
            response = (RoboxRxPacket) firmwareResponse;
        } else if (messageToWrite instanceof ReadPrinterID)
        {
            PrinterIDResponse idResponse = (PrinterIDResponse) RoboxRxPacketFactory.createPacket(
                    RxPacketTypeEnum.PRINTER_ID_RESPONSE);
            idResponse.setEdition("KS");
            // Every other dummy printer is a RoboxPro
            char lastDigitOfName = printerName.charAt(printerName.length() - 1);
            idResponse.setModel(printerModel);
            // this dummy year marks this printer as a dummy printer
            idResponse.setWeekOfManufacture("00");
            idResponse.setYearOfManufacture(dummyYear);
            idResponse.setPoNumber("0000000");
            idResponse.setSerialNumber("0000");
            idResponse.setPrinterFriendlyName(printerName);
            idResponse.setCheckByte("00");
            idResponse.setElectronicsVersion("");
            idResponse.setPrinterColour(Color.web("#000000").toString());
            response = (RoboxRxPacket) idResponse;
        } else if (messageToWrite instanceof StatusRequest)
        {
            doStatusRequest();
            response = currentStatus;
        } else if (messageToWrite instanceof AbortPrint)
        {
            STENO.debug("ABORT print");
            printJobLineNo = 0;
            printJobID = NOTHING_PRINTING_JOB_ID;
            currentStatus.setPrintJobLineNumber(printJobLineNo);
            currentStatus.setRunningPrintJobID(printJobID);
            currentStatus.setNozzle0HeaterMode(HeaterMode.OFF);
            currentStatus.setNozzle0Temperature(0);
            currentStatus.setNozzle0TargetTemperature(0);
            currentStatus.setAmbientTargetTemperature(0);
            currentStatus.setAmbientTemperature(0);
            currentStatus.setBedHeaterMode(HeaterMode.OFF);
            currentStatus.setBedTargetTemperature(0);
            currentStatus.setBedTemperature(0);
            response = (AckResponse) RoboxRxPacketFactory.createPacket(
                    RxPacketTypeEnum.ACK_WITH_ERRORS);
        } else if (messageToWrite instanceof ReportErrors)
        {
            response = errorStatus;
        } else if (messageToWrite instanceof SendResetErrors)
        {
            errorStatus.getFirmwareErrors().clear();
            response = errorStatus;
        } else if (messageToWrite instanceof SendGCodeRequest)
        {
            SendGCodeRequest request = (SendGCodeRequest) messageToWrite;
            GCodeDataResponse gcodeResponse = (GCodeDataResponse) RoboxRxPacketFactory.createPacket(
                    RxPacketTypeEnum.GCODE_RESPONSE);

            String messageData = request.getMessagePayload().trim();

            if (messageData.equalsIgnoreCase(defaultRoboxAttachCommand))
            {
                gcodeResponse.setMessagePayload(
                        "Adding single material head, 1 extruder loaded with orange PLA to dummy printer");
                attachExtruder(0);
                attachHead("RBX01-SM");
                currentStatus.setFilament1SwitchStatus(true);
            } else if (messageData.equalsIgnoreCase(defaultRoboxAttachCommand2))
            {
                gcodeResponse.setMessagePayload(
                        "Adding dual material head, 1 extruder loaded with red ABS to dummy printer");
                attachExtruder(0);
                attachHead("RBX01-DM");
//                attachReel("RBX-ABS-RD537", 0);

            } else if (messageData.equalsIgnoreCase(defaultRoboxAttachCommand3))
            {
                gcodeResponse.setMessagePayload(
                        "Adding dual material head, 2 extruders loaded with red and black ABS to dummy printer");
                attachExtruder(0);
                attachExtruder(1);
                attachHead("RBX01-DM");
//                attachReel("RBX-ABS-RD537", 0);
//                attachReel("RBX-ABS-BK091", 1);

            } else if (messageData.startsWith(attachHeadCommand))
            {
                String headName = messageData.replaceAll(attachHeadCommand, "");
                boolean headAttached = attachHead(headName);
                if (headAttached)
                {
                    gcodeResponse.setMessagePayload("Adding head " + headName + " to dummy printer");
                } else
                {
                    gcodeResponse.setMessagePayload("Couldn't add head " + headName
                            + " to dummy printer");
                }
            } else if (messageData.startsWith(detachHeadCommand))
            {
                currentStatus.setHeadEEPROMState(EEPROMState.NOT_PRESENT);
                currentStatus.setHeadPowerOn(false);
            } else if (messageData.equalsIgnoreCase(detachPrinterCommand))
            {
                RoboxCommsManager.getInstance().removeDummyPrinter(printerHandle);
            } else if (messageData.startsWith(attachReelCommand))
            {
                boolean attachSuccess = false;
                String filamentName = "";

                String[] attachReelElements = messageData.replaceAll(attachReelCommand, "").trim().
                        split(" ");
                if (attachReelElements.length == 2)
                {
                    filamentName = attachReelElements[0];
                    int reelNumber = Integer.valueOf(attachReelElements[1]);
                    attachSuccess = attachReel(filamentContainer.getFilamentByID(filamentName), reelNumber);
                }

                if (attachSuccess)
                {
                    gcodeResponse.setMessagePayload("Adding reel " + filamentName
                            + " to dummy printer");
                } else
                {
                    gcodeResponse.setMessagePayload("Couldn't attach reel - " + filamentName);
                }
            } else if (messageData.startsWith(detachReelCommand))
            {
                int reelNumber = Integer.valueOf(messageData.replaceAll(detachReelCommand, "").
                        trim());
                detachReel(reelNumber);
            } else if (messageData.startsWith(goToPrintLineCommand))
            {
                String printJobLineNumberString = messageData.replaceAll(goToPrintLineCommand, "");
                setPrintLine(Integer.valueOf(printJobLineNumberString));
            } else if (messageData.equalsIgnoreCase(finishPrintCommand))
            {
                finishPrintJob();
            } else if (messageData.startsWith(attachExtruderCommand))
            {
                String extruderNumberString = messageData.replaceAll(attachExtruderCommand, "");
                switch (extruderNumberString)
                {
                    case "0":
                        attachExtruder(0);
                        break;
                    case "1":
                        attachExtruder(1);
                        break;
                }
            } else if (messageData.startsWith(detachExtruderCommand))
            {
                String extruderNumberString = messageData.replaceAll(unloadFilamentCommand, "");
                switch (extruderNumberString)
                {
                    case "0":
                        detachExtruder(0);
                        break;
                    case "1":
                        detachExtruder(1);
                        break;
                }
            } else if (messageData.startsWith(loadFilamentCommand))
            {
                String extruderNumberString = messageData.replaceAll(loadFilamentCommand, "");
                switch (extruderNumberString)
                {
                    case "0":
                        currentStatus.setFilament1SwitchStatus(true);
                        break;
                    case "1":
                        currentStatus.setFilament2SwitchStatus(true);
                        break;
                }
            } else if (messageData.startsWith(unloadFilamentCommand))
            {
                String extruderNumberString = messageData.replaceAll(unloadFilamentCommand, "");
                switch (extruderNumberString)
                {
                    case "0":
                        currentStatus.setFilament1SwitchStatus(false);
                        break;
                    case "1":
                        currentStatus.setFilament2SwitchStatus(false);
                        break;
                }
            } else if (messageData.equalsIgnoreCase(insertSDCardCommand))
            {
                currentStatus.setsdCardPresent(true);
            } else if (messageData.equalsIgnoreCase(removeSDCardCommand))
            {
                currentStatus.setsdCardPresent(false);
            } else if (messageData.startsWith("M104 S"))
            {
                if (messageData.substring(6).length() > 0)
                {
                    nozzleTargetTemperatureS = Integer.parseInt(messageData.substring(6));
                    STENO.debug("set S temp to " + nozzleTargetTemperatureS);

                    if (nozzleTargetTemperatureS == 0)
                    {
                        nozzleHeaterModeS = HeaterMode.OFF;
                        STENO.debug("set heater mode off for S");
                    }
                } else
                {
                    nozzleHeaterModeS = HeaterMode.NORMAL;
                    STENO.debug("set heater mode S to normal");
                }
            } else if (messageData.startsWith("M104 T"))
            {
                if (messageData.substring(6).length() > 0)
                {
                    nozzleTargetTemperatureT = Integer.parseInt(messageData.substring(6));
                    STENO.debug("set T temp to " + nozzleTargetTemperatureT);

                    if (nozzleTargetTemperatureT == 0)
                    {
                        nozzleHeaterModeT = HeaterMode.OFF;
                        STENO.debug("set heater mode off for T");
                    }
                } else
                {
                    nozzleHeaterModeT = HeaterMode.NORMAL;
                    STENO.debug("set heater mode T to normal");
                }
            } else if (messageData.startsWith("M104"))
            {
                nozzleHeaterModeS = HeaterMode.NORMAL;
                STENO.debug("set heater mode S to normal");
            } else if (messageData.startsWith("M140 S") || messageData.startsWith("M139 S"))
            {
                bedTargetTemperature = Integer.parseInt(messageData.substring(6));
                STENO.debug("set bed target temp to " + bedTargetTemperature);
                if (bedTargetTemperature == 0)
                {
                    bedHeaterMode = HeaterMode.OFF;
                    STENO.debug("set bed heater mode off");
                }
            } else if (messageData.startsWith("M140") || messageData.startsWith("M139"))
            {
                bedHeaterMode = HeaterMode.NORMAL;
                STENO.debug("set bed heater mode normal");
            } else if (messageData.startsWith("M113"))
            {
                // ZDelta
                gcodeResponse.populatePacket("0000eZdelta:0.01\nok".getBytes(), RoboxRxPacketFactory.USE_LATEST_FIRMWARE_VERSION);
            } else if (messageData.startsWith(errorCommand))
            {
                String errorString = messageData.replaceAll(errorCommand, "");
                try
                {
                    FirmwareError fwError = FirmwareError.valueOf(errorString);
                    if (fwError != null)
                    {
                        errorStatus.getFirmwareErrors().add(fwError);
                    }
                } catch (IllegalArgumentException ex)
                {
                    STENO.info("Dummy printer didn't understand error " + errorString);
                }
            } else if (messageData.startsWith("M121"))
            {
                String extruderAxis = messageData.replaceAll("M121", "").trim();
                switch (extruderAxis)
                {
                    case "E":
                        currentStatus.setFilament1SwitchStatus(false);
                        break;
                    case "D":
                        currentStatus.setFilament2SwitchStatus(false);
                        break;
                }
            }

            response = (RoboxRxPacket) gcodeResponse;
        } else if (messageToWrite instanceof FormatHeadEEPROM)
        {
            currentStatus.setHeadEEPROMState(EEPROMState.PROGRAMMED);
            attachedHead = new Head();
            response = RoboxRxPacketFactory.createPacket(messageToWrite.getPacketType().
                    getExpectedResponse());
        } else if (messageToWrite instanceof ReadHeadEEPROM)
        {
            HeadEEPROMDataResponse headResponse = (HeadEEPROMDataResponse) RoboxRxPacketFactory.
                    createPacket(RxPacketTypeEnum.HEAD_EEPROM_DATA);

            String filamentID1 = "";
            float lastFilamentTemp1 = 0;

            if (attachedHead.getNozzleHeaters().size() > 1)
            {
                filamentID1 = attachedHead.getNozzleHeaters().get(1).filamentIDProperty().get();
                lastFilamentTemp1 = attachedHead.getNozzleHeaters().get(1).lastFilamentTemperatureProperty().get();
            }

            float nozzle1XOffset = 0;
            float nozzle1YOffset = 0;
            float nozzle1ZOffset = 0;
            float nozzle1BOffset = 0;

            if (attachedHead.getNozzles().size() > 1)
            {
                nozzle1XOffset = attachedHead.getNozzles().get(1).xOffsetProperty().get();
                nozzle1YOffset = attachedHead.getNozzles().get(1).yOffsetProperty().get();
                nozzle1ZOffset = attachedHead.getNozzles().get(1).zOffsetProperty().get();
                nozzle1BOffset = attachedHead.getNozzles().get(1).bOffsetProperty().get();
            }

            headResponse.updateContents(attachedHead.typeCodeProperty().get(),
                    attachedHead.uniqueIDProperty().get(),
                    attachedHead.getNozzleHeaters().size(),
                    attachedHead.getNozzleHeaters().get(0).maximumTemperatureProperty().get(),
                    attachedHead.getNozzleHeaters().get(0).betaProperty().get(),
                    attachedHead.getNozzleHeaters().get(0).tCalProperty().get(),
                    attachedHead.getNozzleHeaters().get(0).lastFilamentTemperatureProperty().get(),
                    attachedHead.getNozzleHeaters().get(0).filamentIDProperty().get(),
                    lastFilamentTemp1,
                    filamentID1,
                    attachedHead.headHoursProperty().get(),
                    attachedHead.getNozzles().size(),
                    attachedHead.getNozzles().get(0).xOffsetProperty().get(),
                    attachedHead.getNozzles().get(0).yOffsetProperty().get(),
                    attachedHead.getNozzles().get(0).zOffsetProperty().get(),
                    attachedHead.getNozzles().get(0).bOffsetProperty().get(),
                    nozzle1XOffset,
                    nozzle1YOffset,
                    nozzle1ZOffset,
                    nozzle1BOffset);
            response = (RoboxRxPacket) headResponse;
        } else if (messageToWrite instanceof WriteHeadEEPROM)
        {
            WriteHeadEEPROM headWriteCommand = (WriteHeadEEPROM) messageToWrite;

            HeadEEPROMDataResponse headResponse = (HeadEEPROMDataResponse) RoboxRxPacketFactory.
                    createPacket(RxPacketTypeEnum.HEAD_EEPROM_DATA);

            headResponse.updateFromWrite(headWriteCommand);
            attachedHead.updateFromEEPROMData(headResponse);

            response = RoboxRxPacketFactory.createPacket(messageToWrite.getPacketType().
                    getExpectedResponse());
        } else if (messageToWrite instanceof ReadReel0EEPROM)
        {
            ReelEEPROMDataResponse reelResponse = (ReelEEPROMDataResponse) RoboxRxPacketFactory.
                    createPacket(RxPacketTypeEnum.REEL_0_EEPROM_DATA);

            reelResponse.updateContents(
                    attachedReels[0].filamentIDProperty().get(),
                    attachedReels[0].firstLayerNozzleTemperatureProperty().get(),
                    attachedReels[0].nozzleTemperatureProperty().get(),
                    attachedReels[0].firstLayerBedTemperatureProperty().get(),
                    attachedReels[0].bedTemperatureProperty().get(),
                    attachedReels[0].ambientTemperatureProperty().get(),
                    attachedReels[0].diameterProperty().get(),
                    attachedReels[0].filamentMultiplierProperty().get(),
                    attachedReels[0].feedRateMultiplierProperty().get(),
                    attachedReels[0].remainingFilamentProperty().get(),
                    attachedReels[0].materialProperty().get(),
                    attachedReels[0].displayColourProperty().get().toString(),
                    attachedReels[0].friendlyFilamentNameProperty().get());
            response = (RoboxRxPacket) reelResponse;
        } else if (messageToWrite instanceof ReadReel1EEPROM)
        {
            ReelEEPROMDataResponse reelResponse = (ReelEEPROMDataResponse) RoboxRxPacketFactory.
                    createPacket(RxPacketTypeEnum.REEL_1_EEPROM_DATA);

            reelResponse.updateContents(
                    attachedReels[1].filamentIDProperty().get(),
                    attachedReels[1].firstLayerNozzleTemperatureProperty().get(),
                    attachedReels[1].nozzleTemperatureProperty().get(),
                    attachedReels[1].firstLayerBedTemperatureProperty().get(),
                    attachedReels[1].bedTemperatureProperty().get(),
                    attachedReels[1].ambientTemperatureProperty().get(),
                    attachedReels[1].diameterProperty().get(),
                    attachedReels[1].filamentMultiplierProperty().get(),
                    attachedReels[1].feedRateMultiplierProperty().get(),
                    attachedReels[1].remainingFilamentProperty().get(),
                    attachedReels[1].materialProperty().get(),
                    attachedReels[1].displayColourProperty().get().toString(),
                    attachedReels[1].friendlyFilamentNameProperty().get());
            reelResponse.setReelNumber(1);
            response = (RoboxRxPacket) reelResponse;
        } else if (messageToWrite instanceof WriteReel0EEPROM)
        {
            WriteReel0EEPROM reelWriteMessage = (WriteReel0EEPROM) messageToWrite;

            Filament f = new Filament(reelWriteMessage.getFriendlyName(),
                    reelWriteMessage.getMaterialType(),
                    reelWriteMessage.getFilamentID(),
                    "", "",
                    reelWriteMessage.getReelFilamentDiameter(),
                    reelWriteMessage.getReelFilamentMultiplier(),
                    reelWriteMessage.getReelFeedRateMultiplier(),
                    (int) reelWriteMessage.getReelAmbientTemperature(),
                    (int) reelWriteMessage.getReelFirstLayerBedTemperature(),
                    (int) reelWriteMessage.getReelBedTemperature(),
                    (int) reelWriteMessage.getReelFirstLayerNozzleTemperature(),
                    (int) reelWriteMessage.getReelNozzleTemperature(),
                    Color.web(reelWriteMessage.getDisplayColourString()),
                    1.0f,
                    (int) reelWriteMessage.getReelRemainingFilament(),
                    false,
                    true);
            attachedReels[0].updateContents(f);
            STENO.debug(reelWriteMessage.toString());
            response = RoboxRxPacketFactory.createPacket(messageToWrite.getPacketType().
                    getExpectedResponse());
        } else if (messageToWrite instanceof WriteReel1EEPROM)
        {
            WriteReel1EEPROM reelWriteMessage = (WriteReel1EEPROM) messageToWrite;
            Filament f = new Filament(reelWriteMessage.getFriendlyName(),
                    reelWriteMessage.getMaterialType(),
                    reelWriteMessage.getFilamentID(),
                    "", "",
                    reelWriteMessage.getReelFilamentDiameter(),
                    reelWriteMessage.getReelFilamentMultiplier(),
                    reelWriteMessage.getReelFeedRateMultiplier(),
                    (int) reelWriteMessage.getReelAmbientTemperature(),
                    (int) reelWriteMessage.getReelFirstLayerBedTemperature(),
                    (int) reelWriteMessage.getReelBedTemperature(),
                    (int) reelWriteMessage.getReelFirstLayerNozzleTemperature(),
                    (int) reelWriteMessage.getReelNozzleTemperature(),
                    Color.web(reelWriteMessage.getDisplayColourString()),
                    1.0f,
                    (int) reelWriteMessage.getReelRemainingFilament(),
                    false,
                    true);
            attachedReels[1].updateContents(f);
            STENO.debug(reelWriteMessage.toString());
            response = RoboxRxPacketFactory.createPacket(messageToWrite.getPacketType().
                    getExpectedResponse());
        } else if (messageToWrite instanceof PausePrint)
        {
            switch (messageToWrite.getMessagePayload())
            {
                case "0":
                    currentStatus.setPauseStatus(PauseStatus.NOT_PAUSED);
                    break;
                case "1":
                    currentStatus.setPauseStatus(PauseStatus.PAUSED);
                    break;
            }
            response = RoboxRxPacketFactory.createPacket(messageToWrite.getPacketType().
                    getExpectedResponse());
        } else if (messageToWrite instanceof SendPrintFileStart)
        {
            linesInCurrentPrintJob = 0;
            printJobID = messageToWrite.getMessagePayload();
            response = RoboxRxPacketFactory.createPacket(messageToWrite.getPacketType().
                    getExpectedResponse());
            STENO.debug("Got start of print file - job " + printJobID);
        } else if (messageToWrite instanceof SendDataFileStart)
        {
            linesInCurrentPrintJob = 0;
            printJobID = messageToWrite.getMessagePayload();
            response = RoboxRxPacketFactory.createPacket(messageToWrite.getPacketType().
                    getExpectedResponse());
            STENO.debug("Got start of data file - job " + printJobID);
        } else if (messageToWrite instanceof SendDataFileChunk)
        {
            String payload = messageToWrite.getMessagePayload();
            String onlyCommas = payload.replaceAll("[^\r]+", "");
            linesInCurrentPrintJob += onlyCommas.length();
            response = RoboxRxPacketFactory.createPacket(messageToWrite.getPacketType().
                    getExpectedResponse());
            STENO.debug("Got data file chunk");
        } else if (messageToWrite instanceof SendDataFileEnd)
        {
            String payload = messageToWrite.getMessagePayload();
            String onlyCommas = payload.replaceAll("[^\r]+", "");
            linesInCurrentPrintJob += onlyCommas.length();
            response = RoboxRxPacketFactory.createPacket(messageToWrite.getPacketType().
                    getExpectedResponse());
            STENO.debug("Got end of data file");
        } else if (messageToWrite instanceof InitiatePrint)
        {
            printJobID = messageToWrite.getMessagePayload();
            STENO.debug("Dummy printer asked to initiate print " + printJobID);
            currentStatus.setRunningPrintJobID(printJobID);
            response = RoboxRxPacketFactory.createPacket(messageToWrite.getPacketType().
                    getExpectedResponse());
            STENO.debug("Asked to initiate print " + printJobID);
        } else if (messageToWrite instanceof AbortPrint)
        {
            currentStatus.setRunningPrintJobID(null);
            response = RoboxRxPacketFactory.createPacket(messageToWrite.getPacketType().
                    getExpectedResponse());
        } else
        {
            response = RoboxRxPacketFactory.createPacket(messageToWrite.getPacketType().
                    getExpectedResponse());
        }

        if (!dontPublishResult)
        {
            printerToUse.processRoboxResponse(response);
        }

        return response;
    }
}
