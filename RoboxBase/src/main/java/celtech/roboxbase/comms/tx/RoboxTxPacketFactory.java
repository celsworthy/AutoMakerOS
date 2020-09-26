package celtech.roboxbase.comms.tx;

import celtech.roboxbase.comms.tx.ReadSendFileReport;
import celtech.roboxbase.comms.tx.ReadDebugData;
import celtech.roboxbase.comms.tx.ReadPrinterID;
import celtech.roboxbase.comms.tx.ReadHoursCounter;
import celtech.roboxbase.comms.tx.ReadHeadEEPROM;
import celtech.roboxbase.comms.tx.ReadReel0EEPROM;
import celtech.roboxbase.comms.tx.ReportErrors;
import celtech.roboxbase.comms.tx.ReadReel1EEPROM;
import celtech.roboxbase.comms.tx.QueryFirmwareVersion;
import celtech.roboxbase.comms.tx.PausePrint;
import celtech.roboxbase.comms.tx.FormatReel1EEPROM;
import celtech.roboxbase.comms.tx.FormatReel0EEPROM;
import celtech.roboxbase.comms.tx.FormatHeadEEPROM;
import celtech.roboxbase.comms.tx.InitiatePrint;
import celtech.roboxbase.comms.tx.ListFiles;
import celtech.roboxbase.comms.tx.AbortPrint;
import celtech.roboxbase.comms.tx.TxPacketTypeEnum;
import celtech.roboxbase.comms.tx.RoboxTxPacket;

/**
 *
 * @author ianhudson
 */
public class RoboxTxPacketFactory
{

    /**
     *
     */
    public final static byte commandByteMask = (byte) 0x80;

    private RoboxTxPacketFactory()
    {
    }

    private static RoboxTxPacket instantiatePacket(TxPacketTypeEnum packetType)
    {
        RoboxTxPacket returnVal = null;

        switch (packetType)
        {
            case STATUS_REQUEST:
                returnVal = new StatusRequest();
                break;
            case EXECUTE_GCODE:
                returnVal = new SendGCodeRequest();
                break;
            case SEND_PRINT_FILE_START:
                returnVal = new SendPrintFileStart();
                break;
            case START_OF_DATA_FILE:
                returnVal = new SendDataFileStart();
                break;
            case DATA_FILE_CHUNK:
                returnVal = new SendDataFileChunk();
                break;
            case END_OF_DATA_FILE:
                returnVal = new SendDataFileEnd();
                break;
            case REPORT_ERRORS:
                returnVal = new ReportErrors();
                break;
            case RESET_ERRORS:
                returnVal = new SendResetErrors();
                break;
            case UPDATE_FIRMWARE:
                returnVal = new UpdateFirmware();
                break;
            case INITIATE_PRINT:
                returnVal = new InitiatePrint();
                break;
            case ABORT_PRINT:
                returnVal = new AbortPrint();
                break;
            case PAUSE_RESUME_PRINT:
                returnVal = new PausePrint();
                break;
            case QUERY_FIRMWARE_VERSION:
                returnVal = new QueryFirmwareVersion();
                break;
            case READ_PRINTER_ID:
                returnVal = new ReadPrinterID();
                break;
            case WRITE_PRINTER_ID:
                returnVal = new WritePrinterID();
                break;
            case FORMAT_HEAD_EEPROM:
                returnVal = new FormatHeadEEPROM();
                break;
            case FORMAT_REEL_0_EEPROM:
                returnVal = new FormatReel0EEPROM();
                break;
            case FORMAT_REEL_1_EEPROM:
                returnVal = new FormatReel1EEPROM();
                break;
            case READ_HEAD_EEPROM:
                returnVal = new ReadHeadEEPROM();
                break;
            case READ_REEL_0_EEPROM:
                returnVal = new ReadReel0EEPROM();
                break;
            case READ_REEL_1_EEPROM:
                returnVal = new ReadReel1EEPROM();
                break;
            case WRITE_HEAD_EEPROM:
                returnVal = new WriteHeadEEPROM();
                break;
            case WRITE_REEL_0_EEPROM:
                returnVal = new WriteReel0EEPROM();
                break;
            case WRITE_REEL_1_EEPROM:
                returnVal = new WriteReel1EEPROM();
                break;
            case SET_AMBIENT_LED_COLOUR:
                returnVal = new SetAmbientLEDColour();
                break;
            case SET_REEL_LED_COLOUR:
                returnVal = new SetReelLEDColour();
                break;
            case SET_TEMPERATURES:
                returnVal = new SetTemperatures();
                break;
            case SET_E_FEED_RATE_MULTIPLIER:
                returnVal = new SetEFeedRateMultiplier();
                break;
            case SET_D_FEED_RATE_MULTIPLIER:
                returnVal = new SetDFeedRateMultiplier();
                break;
            case SET_E_FILAMENT_INFO:
                returnVal = new SetEFilamentInfo();
                break;
            case SET_D_FILAMENT_INFO:
                returnVal = new SetDFilamentInfo();
                break;
            case LIST_FILES:
                returnVal = new ListFiles();
                break;
            case READ_HOURS_COUNTER:
                returnVal = new ReadHoursCounter();
                break;
            case READ_DEBUG_DATA:
                returnVal = new ReadDebugData();
                break;
            case READ_SEND_FILE_REPORT:
                returnVal = new ReadSendFileReport();
                break;
            default:
                break;
        }
        return returnVal;
    }

    /**
     *
     * @param packetType
     * @return
     */
    public static RoboxTxPacket createPacket(TxPacketTypeEnum packetType)
    {
        RoboxTxPacket returnVal = null;

        if (packetType != null)
        {
            returnVal = instantiatePacket(packetType);
        }

        return returnVal;
    }

//    /**
//     *
//     * @param remotePacket
//     * @return
//     * @throws InvalidCommandByteException
//     * @throws UnableToGenerateRoboxPacketException
//     * @throws UnknownPacketTypeException
//     */
//    public static RoboxTxPacket createPacket(RoboxTxPacketRemote remotePacket) throws InvalidCommandByteException, UnableToGenerateRoboxPacketException, UnknownPacketTypeException
//    {
//        RoboxTxPacket returnVal = null;
//        
//        byte[] messageData = remotePacket.getRawData();
//
//        if ((inputBytes[0] & commandByteMask) != commandByteMask)
//        {
//            throw new InvalidCommandByteException();
//        }
//
//        TxPacketTypeEnum packetType = TxPacketTypeEnum.getEnumForCommand(inputBytes[0]);
//
//        if (packetType != null)
//        {
//            returnVal = instantiatePacket(packetType);
//
//            if (returnVal != null)
//            {
//                returnVal.populatePacket(inputBytes);
//            }
//        }
//
//        return returnVal;
//    }
}
