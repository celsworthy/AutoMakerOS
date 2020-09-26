package celtech.roboxbase.services.slicer;

import celtech.roboxbase.utils.models.PrintableMeshes;
import celtech.roboxbase.printerControl.model.Printer;

/**
 *
 * @author ianhudson
 */
public class SliceResult
{
    private String printJobUUID = null;
    private final PrintableMeshes printableMeshes;
    private Printer printerToUse = null;
    private boolean success = false;

    public SliceResult(String printJobUUID,
            PrintableMeshes printableMeshes,
            Printer printerToUse,
            boolean success)
    {
        this.printJobUUID = printJobUUID;
        this.printableMeshes = printableMeshes;
        this.printerToUse = printerToUse;
        this.success = success;
    }

    public String getPrintJobUUID()
    {
        return printJobUUID;
    }

    public PrintableMeshes getPrintableMeshes()
    {
        return printableMeshes;
    }

    public Printer getPrinterToUse()
    {
        return printerToUse;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }
}
