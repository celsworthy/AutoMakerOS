package celtech.roboxbase.comms.rx;

import celtech.roboxbase.comms.remote.PrinterIDDataStructure;
import celtech.roboxbase.comms.remote.StringToBase64Encoder;
import static celtech.roboxbase.utils.ColourStringConverter.stringToColor;
import celtech.roboxbase.comms.tx.WritePrinterID;
import celtech.roboxbase.utils.InvalidChecksumException;
import celtech.roboxbase.utils.SystemUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.UnsupportedEncodingException;
import javafx.scene.paint.Color;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 *
 * @author ianhudson
 */
public class PrinterIDResponse extends RoboxRxPacket
{

    private final String charsetToUse = "US-ASCII";

    private String model;
    private String edition;
    private String weekOfManufacture;
    private String yearOfManufacture;
    private String poNumber;
    private String serialNumber;
    private String checkByte;
    private String electronicsVersion;
    private String printerFriendlyName;
    // This is the web string version of the printer display colour
    private String printerColour = "";

    /**
     *
     */
    public PrinterIDResponse()
    {
        super(RxPacketTypeEnum.PRINTER_ID_RESPONSE, false, false);
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

        try
        {
            int byteOffset = 1;

            this.model = new String(byteData, byteOffset, PrinterIDDataStructure.modelBytes, charsetToUse);
            this.model = this.model.trim();
            byteOffset += PrinterIDDataStructure.modelBytes;

            this.edition = new String(byteData, byteOffset, PrinterIDDataStructure.editionBytes, charsetToUse);
            this.edition = this.edition.trim();
            byteOffset += PrinterIDDataStructure.editionBytes;

            this.weekOfManufacture = new String(byteData, byteOffset, PrinterIDDataStructure.weekOfManufactureBytes, charsetToUse);
            this.weekOfManufacture = this.weekOfManufacture.trim();
            byteOffset += PrinterIDDataStructure.weekOfManufactureBytes;

            this.yearOfManufacture = new String(byteData, byteOffset, PrinterIDDataStructure.yearOfManufactureBytes, charsetToUse);
            this.yearOfManufacture = this.yearOfManufacture.trim();
            byteOffset += PrinterIDDataStructure.yearOfManufactureBytes;

            this.poNumber = new String(byteData, byteOffset, PrinterIDDataStructure.poNumberBytes, charsetToUse);
            this.poNumber = this.poNumber.trim();
            byteOffset += PrinterIDDataStructure.poNumberBytes;

            this.serialNumber = new String(byteData, byteOffset, PrinterIDDataStructure.serialNumberBytes, charsetToUse);
            this.serialNumber = this.serialNumber.trim();
            byteOffset += PrinterIDDataStructure.serialNumberBytes;

            this.checkByte = new String(byteData, byteOffset, PrinterIDDataStructure.checkByteBytes, charsetToUse);
            this.checkByte = this.checkByte.trim();
            byteOffset += PrinterIDDataStructure.checkByteBytes;

            this.electronicsVersion = new String(byteData, byteOffset, PrinterIDDataStructure.electronicsVersionBytes, charsetToUse);
            this.electronicsVersion = this.electronicsVersion.trim();
            byteOffset += PrinterIDDataStructure.electronicsVersionBytes;

            byteOffset += WritePrinterID.BYTES_FOR_FIRST_PAD;

            this.printerFriendlyName = new String(byteData, byteOffset, PrinterIDDataStructure.printerFriendlyNameBytes, charsetToUse);
            this.printerFriendlyName = this.printerFriendlyName.trim();
            // beta testers will have unencoded printer names.
            // TODO: remove this test after 1.000.08 has been out for a while
            if (StringToBase64Encoder.isEncodedData(this.printerFriendlyName))
            {
                this.printerFriendlyName = StringToBase64Encoder.decode(this.printerFriendlyName);
            }
            byteOffset += PrinterIDDataStructure.printerFriendlyNameBytes;

            byteOffset += WritePrinterID.BYTES_FOR_SECOND_PAD;

            String colourDigits = new String(byteData, byteOffset,
                    PrinterIDDataStructure.colourBytes, charsetToUse);
            byteOffset += PrinterIDDataStructure.colourBytes;

            printerColour = stringToColor(colourDigits).toString();

            success = true;

        } catch (UnsupportedEncodingException ex)
        {
            steno.error("Failed to convert byte array to Printer ID Response");
        }

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
        outputString.append("ID: " + getModel());
        outputString.append("\n");
        outputString.append(">>>>>>>>>>\n");

        return outputString.toString();
    }

    /**
     *
     * @return
     */
    public String getPrinterColour()
    {
        return printerColour;
    }

    @JsonIgnore
    public Color getPrinterColourData()
    {
        return Color.web(printerColour);
    }

    /**
     *
     * @return
     */
    public String getModel()
    {
        return model;
    }

    /**
     *
     * @return
     */
    public String getEdition()
    {
        return edition;
    }

    /**
     *
     * @return
     */
    public String getWeekOfManufacture()
    {
        return weekOfManufacture;
    }

    /**
     *
     * @return
     */
    public String getYearOfManufacture()
    {
        return yearOfManufacture;
    }

    /**
     *
     * @return
     */
    public String getPoNumber()
    {
        return poNumber;
    }

    /**
     *
     * @return
     */
    public String getSerialNumber()
    {
        return serialNumber;
    }

    /**
     *
     * @return
     */
    public String getCheckByte()
    {
        return checkByte;
    }

    /**
     *
     * @return
     */
    public String getElectronicsVersion()
    {
        return electronicsVersion;
    }

    /**
     *
     * @return
     */
    public String getPrinterFriendlyName()
    {
        return printerFriendlyName;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public void setEdition(String edition)
    {
        this.edition = edition;
    }

    public void setWeekOfManufacture(String weekOfManufacture)
    {
        this.weekOfManufacture = weekOfManufacture;
    }

    public void setYearOfManufacture(String yearOfManufacture)
    {
        this.yearOfManufacture = yearOfManufacture;
    }

    public void setPoNumber(String poNumber)
    {
        this.poNumber = poNumber;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setPrinterColour(String printerColourString)
    {
        printerColour = printerColourString;
    }

    @JsonIgnore
    public void setPrinterColourFromColor(Color printerColour)
    {
        this.printerColour = printerColour.toString();
    }

    public void setPrinterFriendlyName(String printerFriendlyName)
    {
        this.printerFriendlyName = printerFriendlyName;
    }

    public void setCheckByte(String checkByte) 
    {
        this.checkByte = checkByte;
    }
    
    public void setElectronicsVersion(String electronicsVersion)
    {
        this.electronicsVersion = electronicsVersion;
    }

    @Override
    public int packetLength(float requiredFirmwareVersion)
    {
        return 257;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(15, 37)
                .append(model)
                .append(edition)
                .append(weekOfManufacture)
                .append(yearOfManufacture)
                .append(poNumber)
                .append(serialNumber)
                .append(checkByte)
                .append(electronicsVersion)
                .append(printerFriendlyName)
                .append(printerColour)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof PrinterIDResponse))
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }

        PrinterIDResponse rhs = (PrinterIDResponse) obj;
        return new EqualsBuilder()
                // if deriving: .appendSuper(super.equals(obj))
                .append(model, rhs.model)
                .append(edition, rhs.edition)
                .append(weekOfManufacture, rhs.weekOfManufacture)
                .append(yearOfManufacture, rhs.yearOfManufacture)
                .append(poNumber, rhs.poNumber)
                .append(serialNumber, rhs.serialNumber)
                .append(checkByte, rhs.checkByte)
                .append(electronicsVersion, rhs.electronicsVersion)
                .append(printerFriendlyName, rhs.printerFriendlyName)
                .append(printerColour, rhs.printerColour)
                .isEquals();
    }

    @JsonIgnore
    public String getAsFormattedString()
    {
        // To maintain compatibility with older printers,
        // the electronics version property is not included
        // in the formatted string if it is missing, or equal to "1".
        StringBuilder idString = new StringBuilder();
        idString.append(model);
        idString.append("-");
        idString.append(edition);
        idString.append("-");
        idString.append(weekOfManufacture);
        idString.append(yearOfManufacture);
        idString.append("-");
        idString.append(poNumber);
        idString.append("-");
        idString.append(serialNumber);
        idString.append("-");
        idString.append(checkByte);
        
        return idString.toString();
    }
    
    @JsonIgnore
    public boolean isValid()
    {
        boolean valid = false;
        if (model.startsWith("RBX") && checkByte.length() == 1)
        {
            String stringToChecksum = model
                        + edition
                        + weekOfManufacture
                        + yearOfManufacture
                        + poNumber
                        + serialNumber;

            try
            {
                char checkDigit = SystemUtils.generateUPSModulo10Checksum(stringToChecksum.replaceAll("-", ""));
                valid = (checkDigit == checkByte.charAt(0));
            } catch (InvalidChecksumException ex)
            {
                steno.error("Error whilst testing validity of printer identity \"" + stringToChecksum + "\"");
            }
        } else if (yearOfManufacture.equals("1901DUMMY$") && checkByte.length() == 2) {
            return true;
        }
        return valid;
    }
}
