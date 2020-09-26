package celtech.roboxbase.comms.rx;

/**
 *
 * @author ianhudson
 */
public enum RxPacketTypeEnum
{
    NULL_PACKET((byte)0, false, 0),
    /**
     *
     */
    STATUS_RESPONSE((byte) 0xE1, false, 0),
    /**
     *
     */
    FIRMWARE_RESPONSE((byte) 0xE4, false, 0),
    /**
     *
     */
    ACK_WITH_ERRORS((byte) 0xE3, false, 0),
    /**
     *
     */
    PRINTER_ID_RESPONSE((byte) 0xE5, false, 0),
    /**
     *
     */
    REEL_0_EEPROM_DATA((byte) 0xE6, false, 0),
    /**
     *
     */
    REEL_1_EEPROM_DATA((byte) 0xE8, false, 0),
    /**
     *
     */
    HEAD_EEPROM_DATA((byte) 0xE2, false, 0),
    /**
     *
     */
    GCODE_RESPONSE((byte) 0xE7, true, 4),
    /**
     *
     */
    LIST_FILES_RESPONSE((byte) 0xE0, true, 2),
    /**
     *
     */
    SEND_FILE((byte) 0xE9, false, 0),
    /**
     *
     */
    HOURS_COUNTER((byte) 0xEA, false, 0),
    /**
     *
     */
    DEBUG_DATA((byte) 0xEF, false, 0),
    PRINTER_NOT_FOUND((byte) 0, false, 0);

    private final byte commandByte;
    private final boolean containsLengthField;
    private final int lengthFieldSize;

    private RxPacketTypeEnum(byte commandByte, boolean containsLengthField, int lengthFieldSize)
    {
        this.commandByte = commandByte;
        this.containsLengthField = containsLengthField;
        this.lengthFieldSize = lengthFieldSize;
    }

    /**
     *
     * @return
     */
    public byte getCommandByte()
    {
        return commandByte;
    }

    /**
     *
     * @return
     */
    public boolean containsLengthField()
    {
        return containsLengthField;
    }

    /**
     *
     * @return
     */
    public int getLengthFieldSize()
    {
        return lengthFieldSize;
    }

    /**
     *
     * @param commandByte
     * @return
     */
    public static RxPacketTypeEnum getEnumForCommand(byte commandByte)
    {
        RxPacketTypeEnum returnVal = null;

        for (RxPacketTypeEnum packetType : RxPacketTypeEnum.values())
        {
            if (packetType.getCommandByte() == commandByte)
            {
                returnVal = packetType;
                break;
            }
        }

        return returnVal;
    }
}
