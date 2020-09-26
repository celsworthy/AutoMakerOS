package celtech.roboxremote.rootDataStructures;

import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.PrinterColourMap;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.utils.ColourStringConverter;
import celtech.roboxremote.PrinterRegistry;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.scene.paint.Color;

/**
 *
 * @author taldhous
 */
public class NameStatusData
{

    private String printerID;
    private String printerName;
    private String printerTypeCode;
    private String printerWebColourString;

    public NameStatusData()
    {
        // Jackson deserialization
    }

    public void updateFromPrinterData(String printerID)
    {
        this.printerID = printerID;
        Printer printer = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);
        printerName = printer.getPrinterIdentity().printerFriendlyNameProperty().get();
        printerTypeCode = printer.printerConfigurationProperty().get().getTypeCode();
        PrinterColourMap colourMap = PrinterColourMap.getInstance();
        Color displayColour = colourMap.printerToDisplayColour(printer.getPrinterIdentity().printerColourProperty().get());

        printerWebColourString = "#" + ColourStringConverter.colourToString(displayColour);
    }

    @JsonProperty
    public String getPrinterID()
    {
        return printerID;
    }

    @JsonProperty
    public void setPrinterID(String printerID)
    {
        this.printerID = printerID;
    }

    @JsonProperty
    public String getPrinterName()
    {
        return printerName;
    }

    @JsonProperty
    public void setPrinterName(String printerName)
    {
        this.printerName = printerName;
    }

    @JsonProperty
    public String getPrinterTypeCode()
    {
        return printerTypeCode;
    }

    @JsonProperty
    public void setPrinterTypeCode(String printerTypeCode)
    {
        this.printerTypeCode = printerTypeCode;
    }

    @JsonProperty
    public String getPrinterWebColourString()
    {
        return printerWebColourString;
    }

    @JsonProperty
    public void setPrinterWebColourString(String printerWebColourString)
    {
        this.printerWebColourString = printerWebColourString;
    }
}
