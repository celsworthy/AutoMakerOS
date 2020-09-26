package celtech.roboxbase.comms.rx;

import celtech.roboxbase.comms.exceptions.InvalidCommandByteException;
import celtech.roboxbase.comms.exceptions.UnableToGenerateRoboxPacketException;
import celtech.roboxbase.comms.exceptions.UnknownPacketTypeException;

/**
 *
 * @author ianhudson
 */
public class RoboxRxPacketFactory
{

    /**
     *
     */
    public final static byte commandByteMask = (byte) 0x80;
    public final static float USE_LATEST_FIRMWARE_VERSION = -1;

    private RoboxRxPacketFactory()
    {
    }
    
    public static RoboxRxPacket createNullPacket()
    {
        return new NullPacket();
    }

    /**
     *
     * @param packetType
     * @return
     */
    public static RoboxRxPacket createPacket(RxPacketTypeEnum packetType)
    {
        RoboxRxPacket returnVal = null;

        if (packetType != null)
        {
            switch (packetType)
            {
                case STATUS_RESPONSE:
                    returnVal = new StatusResponse();
                    break;
                case FIRMWARE_RESPONSE:
                    returnVal = new FirmwareResponse();
                    break;
                case ACK_WITH_ERRORS:
                    returnVal = new AckResponse();
                    break;
                case PRINTER_ID_RESPONSE:
                    returnVal = new PrinterIDResponse();
                    break;
                case REEL_0_EEPROM_DATA:
                    returnVal = new ReelEEPROM0DataResponse();
                    break;
                case REEL_1_EEPROM_DATA:
                    returnVal = new ReelEEPROM1DataResponse();
                    break;
                case HEAD_EEPROM_DATA:
                    returnVal = new HeadEEPROMDataResponse();
                    break;
                case GCODE_RESPONSE:
                    returnVal = new GCodeDataResponse();
                    break;
                case LIST_FILES_RESPONSE:
                    returnVal = new ListFilesResponseImpl();
                    break;
                case HOURS_COUNTER:
                    returnVal = new HoursCounterResponse();
                    break;
                case DEBUG_DATA:
                    returnVal = new DebugDataResponse();
                    break;
                case SEND_FILE:
                    returnVal = new SendFile();
                    break;
                case PRINTER_NOT_FOUND:
                    returnVal = new PrinterNotFound();
                    break;
                default:
                    break;
            }
        }

        return returnVal;
    }

    /**
     *
     * @param inputBytes
     * @return
     * @throws InvalidCommandByteException
     * @throws UnableToGenerateRoboxPacketException
     * @throws UnknownPacketTypeException
     */
    public static RoboxRxPacket createPacket(byte[] inputBytes) throws InvalidCommandByteException, UnableToGenerateRoboxPacketException, UnknownPacketTypeException
    {
        return createPacket(inputBytes, USE_LATEST_FIRMWARE_VERSION);
    }
    
    /**
     *
     * @param inputBytes
     * @return
     * @throws InvalidCommandByteException
     * @throws UnableToGenerateRoboxPacketException
     * @throws UnknownPacketTypeException
     */
    public static RoboxRxPacket createPacket(byte[] inputBytes, float requiredFirmwareVersion) throws InvalidCommandByteException, UnableToGenerateRoboxPacketException, UnknownPacketTypeException
    {
        RoboxRxPacket returnVal = null;

        if ((inputBytes[0] & commandByteMask) != commandByteMask)
        {
            throw new InvalidCommandByteException();
        }

        RxPacketTypeEnum packetType = RxPacketTypeEnum.getEnumForCommand(inputBytes[0]);

        if (packetType != null)
        {
            switch (packetType)
            {
                case STATUS_RESPONSE:
                    StatusResponse statusResponse = new StatusResponse();
                    statusResponse.populatePacket(inputBytes, requiredFirmwareVersion);
                    returnVal = statusResponse;
                    break;
                case ACK_WITH_ERRORS:
                    AckResponse ackResponse = new AckResponse();
                    ackResponse.populatePacket(inputBytes, requiredFirmwareVersion);
                    returnVal = ackResponse;
                    break;
                case FIRMWARE_RESPONSE:
                    FirmwareResponse fwResponse = new FirmwareResponse();
                    fwResponse.populatePacket(inputBytes, requiredFirmwareVersion);
                    returnVal = fwResponse;
                    break;
                case PRINTER_ID_RESPONSE:
                    PrinterIDResponse printerIDResponse = new PrinterIDResponse();
                    printerIDResponse.populatePacket(inputBytes, requiredFirmwareVersion);
                    returnVal = printerIDResponse;
                    break;
                case REEL_0_EEPROM_DATA:
                    ReelEEPROM0DataResponse reel0EepromDataResponse = new ReelEEPROM0DataResponse();
                    reel0EepromDataResponse.populatePacket(inputBytes, requiredFirmwareVersion);
                    reel0EepromDataResponse.setReelNumber(0);
                    returnVal = reel0EepromDataResponse;
                    break;
                case REEL_1_EEPROM_DATA:
                    ReelEEPROM1DataResponse reel1EepromDataResponse = new ReelEEPROM1DataResponse();
                    reel1EepromDataResponse.populatePacket(inputBytes, requiredFirmwareVersion);
                    reel1EepromDataResponse.setReelNumber(1);
                    returnVal = reel1EepromDataResponse;
                    break;
                case HEAD_EEPROM_DATA:
                    HeadEEPROMDataResponse headEepromDataResponse = new HeadEEPROMDataResponse();
                    headEepromDataResponse.populatePacket(inputBytes, requiredFirmwareVersion);
                    returnVal = headEepromDataResponse;
                    break;
                case GCODE_RESPONSE:
                    GCodeDataResponse gcodeDataResponse = new GCodeDataResponse();
                    gcodeDataResponse.populatePacket(inputBytes, requiredFirmwareVersion);
                    returnVal = gcodeDataResponse;
                    break;
                case LIST_FILES_RESPONSE:
                    ListFilesResponse listFilesResponse = new ListFilesResponseImpl();
                    listFilesResponse.populatePacket(inputBytes, requiredFirmwareVersion);
                    returnVal = listFilesResponse;
                    break;
                case HOURS_COUNTER:
                    HoursCounterResponse hoursResponse = new HoursCounterResponse();
                    hoursResponse.populatePacket(inputBytes, requiredFirmwareVersion);
                    returnVal = hoursResponse;
                    break;
                case DEBUG_DATA:
                    DebugDataResponse debugResponse = new DebugDataResponse();
                    debugResponse.populatePacket(inputBytes, requiredFirmwareVersion);
                    returnVal = debugResponse;
                    break;
                case SEND_FILE:
                    SendFile sendFileResponse = new SendFile();
                    sendFileResponse.populatePacket(inputBytes, requiredFirmwareVersion);
                    returnVal = sendFileResponse;
                    break;
                default:
                    throw new UnknownPacketTypeException();
            }

            if (returnVal == null)
            {
                throw new UnableToGenerateRoboxPacketException();
            }
        }

        return returnVal;
    }
}
