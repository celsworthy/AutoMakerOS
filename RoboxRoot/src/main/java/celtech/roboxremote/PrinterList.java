package celtech.roboxremote;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ianhudson
 */
public class PrinterList
{

    private final List<String> printerNames = new ArrayList<>();

    public PrinterList()
    {
        printerNames.add("Test Printer");
    }

    public List<String> getPrinterNames()
    {
        return printerNames;
    }
}
