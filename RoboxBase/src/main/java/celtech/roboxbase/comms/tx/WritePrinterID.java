package celtech.roboxbase.comms.tx;

import celtech.roboxbase.comms.remote.PrinterIDDataStructure;
import celtech.roboxbase.comms.remote.StringToBase64Encoder;
import celtech.roboxbase.printerControl.model.PrinterIdentity;
import celtech.roboxbase.utils.ColourStringConverter;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author ianhudson
 */
public class WritePrinterID extends RoboxTxPacket
{

    public static final int BYTES_FOR_NAME = PrinterIDDataStructure.printerFriendlyNameBytes;
    public static final int BYTES_FOR_FIRST_PAD = PrinterIDDataStructure.firstPadBytes;
    public static final int BYTES_FOR_SECOND_PAD = PrinterIDDataStructure.secondPadBytes;

    private final char[] firstPad = new char[BYTES_FOR_FIRST_PAD];
    private final char[] secondPad = new char[BYTES_FOR_SECOND_PAD];

    /**
     *
     */
    public WritePrinterID()
    {
        super(TxPacketTypeEnum.WRITE_PRINTER_ID, false, false);
    }

    /**
     *
     * @param byteData
     * @return
     */
    @Override
    public boolean populatePacket(byte[] byteData)
    {
        setMessagePayloadBytes(byteData);
        return false;
    }

    private String model;
    private String edition;
    private String weekOfManufacture;
    private String yearOfManufacture;
    private String poNumber;
    private String serialNumber;
    private String checkByte;
    private String electronicsVersion;
    private String printerFriendlyName;
    private String colourWebString = "";

    /**
     *
     * @param model
     * @param edition
     * @param weekOfManufacture
     * @param yearOfManufacture
     * @param poNumber
     * @param serialNumber
     * @param checkByte
     * @param electronicsVersion
     * @param printerFriendlyName
     * @param printerColourWebString
     */
    public void setIDAndColour(String model, String edition,
            String weekOfManufacture, String yearOfManufacture, String poNumber,
            String serialNumber, String checkByte, String electronicsVersion, String printerFriendlyName,
            String printerColourWebString)
    {
        this.model = model;
        this.edition = edition;
        this.weekOfManufacture = weekOfManufacture;
        this.yearOfManufacture = yearOfManufacture;
        this.poNumber = poNumber;
        this.serialNumber = serialNumber;
        this.checkByte = checkByte;
        this.electronicsVersion = electronicsVersion;
        this.printerFriendlyName = printerFriendlyName;
        this.colourWebString = printerColourWebString;

        try
        {
            printerFriendlyName = StringToBase64Encoder.encode(printerFriendlyName,
                    BYTES_FOR_NAME);
        } catch (UnsupportedEncodingException ex)
        {
            steno.error("Couldn't encode printer name: " + printerFriendlyName);
            printerFriendlyName = "";
        }

        //The ID is in the first 200 characters
        //The colour is stored in 6 bytes at the end - eg FF FF FF
        StringBuffer payload = new StringBuffer();

        payload.append(String.format("%1$-5s", model));
        payload.append(String.format("%1$-2s", edition));
        payload.append(String.format("%1$-2s", weekOfManufacture));
        payload.append(String.format("%1$-2s", yearOfManufacture));
        payload.append(String.format("%1$-7s", poNumber));
        payload.append(String.format("%1$-4s", serialNumber));
        payload.append(String.format("%1$1s", checkByte));
        payload.append(String.format("%1$1s", electronicsVersion));
        payload.append(firstPad);
        payload.append(String.format("%1$" + BYTES_FOR_NAME + "s",
                printerFriendlyName));
        payload.append(secondPad);

        payload.append(printerColourWebString);

        steno.debug("Outputting string of length " + payload.length());
        this.setMessagePayload(payload.toString());
    }

    public void populatePacket(
            String printerUniqueIDIn,
            String printermodelIn,
            String printereditionIn,
            String printerweekOfManufactureIn,
            String printeryearOfManufactureIn,
            String printerpoNumberIn,
            String printerserialNumberIn,
            String printercheckByteIn,
            String printerElectronicsVersionIn,
            String printerFriendlyNameIn,
            String printerColourWebString,
            String firmwareVersionIn)
    {
        setIDAndColour(printermodelIn,
                printereditionIn,
                printerweekOfManufactureIn,
                printeryearOfManufactureIn,
                printerpoNumberIn,
                printerserialNumberIn,
                printercheckByteIn,
                printerElectronicsVersionIn,
                printerFriendlyNameIn,
                printerColourWebString);
    }
    
    public void populatePacket(
            PrinterIdentity identity)
    {
        setIDAndColour(identity.printermodelProperty().get(),
                identity.printereditionProperty().get(),
                identity.printerweekOfManufactureProperty().get(),
                identity.printeryearOfManufactureProperty().get(),
                identity.printerpoNumberProperty().get(),
                identity.printerserialNumberProperty().get(),
                identity.printercheckByteProperty().get(),
                identity.printerelectronicsVersionProperty().get(),
                identity.printerFriendlyNameProperty().get(),
                ColourStringConverter.colourToString(identity.printerColourProperty().get()));
    }

    public String getModel()
    {
        return model;
    }

    public String getEdition()
    {
        return edition;
    }

    public String getWeekOfManufacture()
    {
        return weekOfManufacture;
    }

    public String getYearOfManufacture()
    {
        return yearOfManufacture;
    }

    public String getPoNumber()
    {
        return poNumber;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public String getCheckByte()
    {
        return checkByte;
    }

    public String getElectronicsVersion()
    {
        return electronicsVersion;
    }

    public String getPrinterFriendlyName()
    {
        return printerFriendlyName;
    }

    public String getColourWebString()
    {
        return colourWebString;
    }

    public void setCheckByte(String checkByte)
    {
        this.checkByte = checkByte;
    }

    public void setElectronicsVersion(String electronicsVersion)
    {
        this.electronicsVersion = electronicsVersion;
    }

    public void setEdition(String edition)
    {
        this.edition = edition;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public void setPoNumber(String poNumber)
    {
        this.poNumber = poNumber;
    }

    public void setPrinterColourWebString(String printerColourWebString)
    {
        this.colourWebString = printerColourWebString;
    }

    public void setPrinterFriendlyName(String printerFriendlyName)
    {
        this.printerFriendlyName = printerFriendlyName;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setWeekOfManufacture(String weekOfManufacture)
    {
        this.weekOfManufacture = weekOfManufacture;
    }

    public void setYearOfManufacture(String yearOfManufacture)
    {
        this.yearOfManufacture = yearOfManufacture;
    }
}
