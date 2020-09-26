package celtech.roboxbase.services.firmware;

import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.services.ControllableService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 *
 * @author ianhudson
 */
public class FirmwareLoadService extends Service<FirmwareLoadResult> implements ControllableService
{
    private final StringProperty firmwareFileToLoad = new SimpleStringProperty();
    private Printer printerToUse = null;

    /**
     *
     * @param value
     */
    public final void setFirmwareFileToLoad(String value)
    {
        firmwareFileToLoad.set(value);
    }

    /**
     *
     * @return
     */
    public final String getFirmwareFileToLoad()
    {
        return firmwareFileToLoad.get();
    }

    /**
     *
     * @return
     */
    public final StringProperty firmwareFileToLoadProperty()
    {
        return firmwareFileToLoad;
    }

    @Override
    protected Task<FirmwareLoadResult> createTask()
    {
        return new FirmwareLoadTask(getFirmwareFileToLoad(), printerToUse);
    }

    /**
     *
     * @return
     */
    @Override
    public boolean cancelRun()
    {
        return cancel();
    }

    /**
     *
     * @param printerToUse
     */
    public void setPrinterToUse(Printer printerToUse)
    {
        this.printerToUse = printerToUse;
    }
}
