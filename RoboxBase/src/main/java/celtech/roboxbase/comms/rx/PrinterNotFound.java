package celtech.roboxbase.comms.rx;

/**
 * This packet is only used when remote comms determines that it has been asked for a printer that doesn't exist
 * @author ianhudson
 */
public class PrinterNotFound extends RoboxRxPacket
{
    /**
     *
     */
    public PrinterNotFound()
    {
        super(RxPacketTypeEnum.SEND_FILE, false, false);
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

        return true;
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
        outputString.append(">>>>>>>>>>\n");

        return outputString.toString();
    }

    @Override
    public int packetLength(float requiredFirmwareVersion)
    {
        return 0;
    }
}
