package celtech.roboxbase.configuration.datafileaccessors;

import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.PrinterFileFilter;
import celtech.roboxbase.configuration.fileRepresentation.PrinterDefinitionFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author ianhudson
 */
public class PrinterContainer
{

    private static final Stenographer steno = StenographerFactory.getStenographer(PrinterContainer.class.getName());
    private static PrinterContainer instance = null;
    private static ObservableList<PrinterDefinitionFile> completePrinterList;
    private static ObservableMap<String, PrinterDefinitionFile> completePrinterMap;
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final String defaultPrinterID = "RBX01";

    private PrinterContainer()
    {
        completePrinterList = FXCollections.observableArrayList();
        completePrinterMap = FXCollections.observableHashMap();
        File printerDirHandle = new File(BaseConfiguration.getApplicationPrinterDirectory());
        File[] printerFiles = printerDirHandle.listFiles(new PrinterFileFilter());
        if (printerFiles == null)
        {
            steno.error("Error loading printer list from \"" + printerDirHandle.getAbsolutePath() + "\"");
        }
        else
        {
            ArrayList<PrinterDefinitionFile> printers = ingestPrinters(printerFiles);
            completePrinterList.addAll(printers);
        }
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    private ArrayList<PrinterDefinitionFile> ingestPrinters(File[] printerFilesToIngest)
    {
        ArrayList<PrinterDefinitionFile> printerList = new ArrayList<>();

        for (File printerFile : printerFilesToIngest)
        {
            try
            {
                PrinterDefinitionFile printerData = mapper.readValue(printerFile, PrinterDefinitionFile.class);

                printerList.add(printerData);
                completePrinterMap.put(printerData.getTypeCode(), printerData);

            } catch (IOException ex)
            {
                steno.error("Error loading printer " + printerFile.getAbsolutePath());
            }
        }

        return printerList;
    }

    public static PrinterContainer getInstance()
    {
        if (instance == null)
        {
            instance = new PrinterContainer();
        }

        return instance;
    }

    public static PrinterDefinitionFile getPrinterByID(String printerID)
    {
        if (instance == null)
        {
            PrinterContainer.getInstance();
        }

        PrinterDefinitionFile returnedPrinter = completePrinterMap.get(printerID);
        return returnedPrinter;
    }

    public static ObservableList<PrinterDefinitionFile> getCompletePrinterList()
    {
        if (instance == null)
        {
            instance = new PrinterContainer();
        }

        return completePrinterList;
    }
}
