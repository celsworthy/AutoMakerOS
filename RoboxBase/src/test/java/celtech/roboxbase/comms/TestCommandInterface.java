package celtech.roboxbase.comms;

import celtech.roboxbase.comms.remote.EEPROMState;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.datafileaccessors.HeadContainer;
import celtech.roboxbase.configuration.fileRepresentation.HeadFile;
import celtech.roboxbase.comms.exceptions.RoboxCommsException;
import celtech.roboxbase.comms.rx.FirmwareResponse;
import celtech.roboxbase.comms.rx.GCodeDataResponse;
import celtech.roboxbase.comms.rx.HeadEEPROMDataResponse;
import celtech.roboxbase.comms.rx.PrinterIDResponse;
import celtech.roboxbase.comms.rx.ReelEEPROMDataResponse;
import celtech.roboxbase.comms.rx.RoboxRxPacket;
import celtech.roboxbase.comms.rx.RoboxRxPacketFactory;
import celtech.roboxbase.comms.rx.RxPacketTypeEnum;
import celtech.roboxbase.comms.rx.StatusResponse;
import celtech.roboxbase.comms.tx.QueryFirmwareVersion;
import celtech.roboxbase.comms.tx.ReadHeadEEPROM;
import celtech.roboxbase.comms.tx.ReadPrinterID;
import celtech.roboxbase.comms.tx.ReadReel0EEPROM;
import celtech.roboxbase.comms.tx.ReportErrors;
import celtech.roboxbase.comms.tx.RoboxTxPacket;
import celtech.roboxbase.comms.tx.SendGCodeRequest;
import celtech.roboxbase.comms.tx.StatusRequest;
import celtech.roboxbase.comms.tx.WriteHeadEEPROM;
import celtech.roboxbase.comms.tx.WritePrinterID;
import celtech.roboxbase.configuration.datafileaccessors.FilamentContainer;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.Reel;
import javafx.scene.paint.Color;

/**
 *
 * @author Ian
 */
public class TestCommandInterface extends CommandInterface
{

    public static final Color defaultPrinterColour = Color.CRIMSON;

    private final String attachHeadCommand = "ATTACH HEAD ";
    private final String detachHeadCommand = "DETACH HEAD";
    private final String attachReelCommand = "ATTACH REEL ";
    private final String detachReelCommand = "DETACH REEL";

    private StatusResponse currentStatus = null;
    private Reel attachedReel = null;
    private PrinterIDResponse printerID = null;
    private HeadEEPROMDataResponse headResponse = (HeadEEPROMDataResponse) RoboxRxPacketFactory.createPacket(
            RxPacketTypeEnum.HEAD_EEPROM_DATA);

    public TestCommandInterface(PrinterStatusConsumer controlInterface,
            DetectedDevice printerHandle,
            boolean suppressPrinterIDChecks, int sleepBetweenStatusChecks)
    {
        super(controlInterface, printerHandle, suppressPrinterIDChecks, sleepBetweenStatusChecks, true);
        this.setName("Dummy Printer");

        preTestInitialisation();
    }

    @Override
    protected void setSleepBetweenStatusChecks(int sleepMillis)
    {
    }

    @Override
    public RoboxRxPacket writeToPrinterImpl(RoboxTxPacket messageToWrite, boolean dontPublishResult) throws RoboxCommsException
    {
        RoboxRxPacket response = null;

        steno.debug("Dummy printer received " + messageToWrite.getPacketType().name());

        if (messageToWrite instanceof QueryFirmwareVersion)
        {
            FirmwareResponse firmwareResponse = (FirmwareResponse) RoboxRxPacketFactory.createPacket(
                    RxPacketTypeEnum.FIRMWARE_RESPONSE);
            firmwareResponse.setFirmwareRevision("123");
            response = (RoboxRxPacket) firmwareResponse;
        } else if (messageToWrite instanceof StatusRequest)
        {
            currentStatus.setAmbientTemperature((int) (Math.random() * 100));
            response = (RoboxRxPacket) currentStatus;
        } else if (messageToWrite instanceof ReportErrors)
        {
            response = RoboxRxPacketFactory.createPacket(RxPacketTypeEnum.ACK_WITH_ERRORS);
        } else if (messageToWrite instanceof SendGCodeRequest)
        {
            SendGCodeRequest request = (SendGCodeRequest) messageToWrite;
            GCodeDataResponse gcodeResponse = (GCodeDataResponse) RoboxRxPacketFactory.createPacket(
                    RxPacketTypeEnum.GCODE_RESPONSE);

            if (request.getMessagePayload().startsWith(attachHeadCommand))
            {
                String headName = request.getMessagePayload().replaceAll(attachHeadCommand, "");
                HeadFile headData = HeadContainer.getHeadByID(headName);
                if (headData != null)
                {
                    currentStatus.setHeadEEPROMState(EEPROMState.PROGRAMMED);
                    Head attachedHead = new Head(headData);
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
                    gcodeResponse.setMessagePayload("Adding head " + headName + " to dummy printer");
                } else
                {
                    gcodeResponse.setMessagePayload("Didn't recognise head name - " + headName);
                }
            } else if (request.getMessagePayload().startsWith(detachHeadCommand))
            {
                currentStatus.setHeadEEPROMState(EEPROMState.NOT_PRESENT);
            } else if (request.getMessagePayload().startsWith(attachReelCommand))
            {
                String filamentName = request.getMessagePayload().replaceAll(attachReelCommand, "");
                Filament filament = FilamentContainer.getInstance().getFilamentByID(filamentName);
                if (filament != null)
                {
                    currentStatus.setReel0EEPROMState(EEPROMState.PROGRAMMED);
                    attachedReel = new Reel();
                    attachedReel.updateContents(filament);
                    gcodeResponse.setMessagePayload("Adding reel " + filamentName
                            + " to dummy printer");
                } else
                {
                    gcodeResponse.setMessagePayload("Didn't recognise filament name - "
                            + filamentName);
                }
            } else if (request.getMessagePayload().startsWith(detachReelCommand))
            {
                currentStatus.setReel0EEPROMState(EEPROMState.NOT_PRESENT);
            }

            response = (RoboxRxPacket) gcodeResponse;
        } else if (messageToWrite instanceof ReadHeadEEPROM)
        {
            response = (RoboxRxPacket) headResponse;
        } else if (messageToWrite instanceof ReadReel0EEPROM)
        {
            ReelEEPROMDataResponse reelResponse = (ReelEEPROMDataResponse) RoboxRxPacketFactory.createPacket(
                    RxPacketTypeEnum.REEL_0_EEPROM_DATA);

            reelResponse.updateContents(
                    attachedReel.filamentIDProperty().get(),
                    attachedReel.firstLayerNozzleTemperatureProperty().get(),
                    attachedReel.nozzleTemperatureProperty().get(),
                    attachedReel.firstLayerBedTemperatureProperty().get(),
                    attachedReel.bedTemperatureProperty().get(),
                    attachedReel.ambientTemperatureProperty().get(),
                    attachedReel.diameterProperty().get(),
                    attachedReel.filamentMultiplierProperty().get(),
                    attachedReel.feedRateMultiplierProperty().get(),
                    attachedReel.remainingFilamentProperty().get(),
                    attachedReel.materialProperty().get(),
                    attachedReel.displayColourProperty().get().toString(),
                    attachedReel.friendlyFilamentNameProperty().get());
            response = (RoboxRxPacket) reelResponse;
        } else if (messageToWrite instanceof WriteHeadEEPROM)
        {
            response = RoboxRxPacketFactory.createPacket(
                    messageToWrite.getPacketType().getExpectedResponse());
            copyWriteHeadEEPROMToDataResponse((WriteHeadEEPROM) messageToWrite, headResponse);
        } else if (messageToWrite instanceof WritePrinterID)
        {
            response = RoboxRxPacketFactory.createPacket(
                    messageToWrite.getPacketType().getExpectedResponse());

            WritePrinterID writeID = (WritePrinterID) messageToWrite;
            printerID.setEdition(writeID.getEdition());
            printerID.setModel(writeID.getModel());
            printerID.setPoNumber(writeID.getPoNumber());
            printerID.setPrinterColour(writeID.getColourWebString());
            printerID.setPrinterFriendlyName(writeID.getPrinterFriendlyName());
            printerID.setSerialNumber(writeID.getSerialNumber());
            printerID.setWeekOfManufacture(writeID.getWeekOfManufacture());
            printerID.setYearOfManufacture(writeID.getYearOfManufacture());
        } else if (messageToWrite instanceof ReadPrinterID)
        {
            response = (RoboxRxPacket) printerID;
        } else
        {
            response = RoboxRxPacketFactory.createPacket(
                    messageToWrite.getPacketType().getExpectedResponse());
        }

        if (!dontPublishResult)
        {
            printerToUse.processRoboxResponse(response);
        }
        
        return response;
    }

    @Override
    protected boolean connectToPrinterImpl()
    {
        steno.info("Dummy printer connected");
        return true;
    }

    @Override
    protected void disconnectPrinterImpl()
    {
        steno.info("Dummy printer disconnected");
    }

    public void noHead()
    {
        currentStatus.setHeadEEPROMState(EEPROMState.NOT_PRESENT);
    }

    public void noReels()
    {
        currentStatus.setReel0EEPROMState(EEPROMState.NOT_PRESENT);
    }

    public void addHead(HeadEEPROMDataResponse headResponse)
    {
        this.headResponse = headResponse;
        currentStatus.setHeadEEPROMState(EEPROMState.PROGRAMMED);
    }

    public void preTestInitialisation()
    {
        currentStatus = (StatusResponse) RoboxRxPacketFactory.createPacket(
                RxPacketTypeEnum.STATUS_RESPONSE);
        noHead();
        noReels();

        printerID = (PrinterIDResponse) RoboxRxPacketFactory.createPacket(
                RxPacketTypeEnum.PRINTER_ID_RESPONSE);
        printerID.setEdition("KS");
        printerID.setPrinterFriendlyName("Dummy");
        printerID.setPrinterColour(defaultPrinterColour.toString());
    }

    private void copyWriteHeadEEPROMToDataResponse(WriteHeadEEPROM message,
            HeadEEPROMDataResponse headResponse)
    {
        headResponse.setHeadTypeCode(message.getHeadTypeCode());
        headResponse.setUniqueID(message.getHeadUniqueID());
        headResponse.setMaximumTemperature(message.getMaximumTemperature());
        headResponse.setThermistorBeta(message.getThermistorBeta());
        headResponse.setThermistorTCal(message.getThermistorTCal());
        headResponse.setNozzle1XOffset(message.getNozzle1XOffset());
        headResponse.setNozzle1YOffset(message.getNozzle1YOffset());
        headResponse.setNozzle1ZOffset(message.getNozzle1ZOffset());
        headResponse.setNozzle1BOffset(message.getNozzle1BOffset());
        headResponse.setFilament0ID(message.getFilament0ID());
        headResponse.setFilament1ID(message.getFilament1ID());
        headResponse.setNozzle2XOffset(message.getNozzle2XOffset());
        headResponse.setNozzle2YOffset(message.getNozzle2YOffset());
        headResponse.setNozzle2ZOffset(message.getNozzle2ZOffset());
        headResponse.setNozzle2BOffset(message.getNozzle2BOffset());
        headResponse.setLastFilamentTemperature0(message.getLastFilamentTemperature0());
        headResponse.setLastFilamentTemperature1(message.getLastFilamentTemperature1());
        headResponse.setHeadHours(message.getHourCounter());

    }
}
