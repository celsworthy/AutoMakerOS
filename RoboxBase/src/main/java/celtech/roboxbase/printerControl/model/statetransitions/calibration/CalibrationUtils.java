package celtech.roboxbase.printerControl.model.statetransitions.calibration;

import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterListChangesAdapter;
import celtech.roboxbase.utils.tasks.Cancellable;

/**
 *
 * @author ianhudson
 */
public class CalibrationUtils
{
    public static void setCancelledIfPrinterDisconnected(Printer printerToMonitor,
            Cancellable cancellable)
    {
        BaseLookup.getPrinterListChangesNotifier().addListener(new PrinterListChangesAdapter()
        {
            @Override
            public void whenPrinterRemoved(Printer printer)
            {
                if (printerToMonitor == printer)
                {
                    cancellable.cancelled().set(true);
                }
            }
        });
    }
}
