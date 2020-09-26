package celtech.roboxremote.rootDataStructures;

import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.comms.rx.FirmwareError;
import celtech.roboxbase.printerControl.PrinterStatus;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxremote.PrinterRegistry;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author taldhous
 */
public class ActiveErrorStatusData
{

    private final Stenographer steno = StenographerFactory.getStenographer(ActiveErrorStatusData.class.getName());
    private String printerID;
    //Errors
    private ArrayList<ErrorDetails> currentErrors;

    public ActiveErrorStatusData()
    {
        // Jackson deserialization
    }

    public void updateFromPrinterData(String printerID)
    {
        try
        {
            this.printerID = printerID;
            Printer printer = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);

            // The activeErrors list only contains those uncleared errors for which isRequireUserToClear() returns true.
            // The Root interface needs all the current errors, so the currentError list is used, as it contains
            // all the uncleared errors that have not been suppressed.
            if (printer != null && !printer.getCurrentErrors().isEmpty())
            {
                currentErrors = new ArrayList<>();
                for (int errorCounter = 0; errorCounter < printer.getCurrentErrors().size(); errorCounter++)
                {
                    FirmwareError currentError = printer.getCurrentErrors().get(errorCounter);
                    if (currentError == FirmwareError.NOZZLE_FLUSH_NEEDED &&
                        printer.printerStatusProperty().get() == PrinterStatus.IDLE)
                    {
                        //Suppress NOZZLE_FLUSH if the printer is idle.
                    }
                    else
                    {
                        String errorMessage = BaseLookup.i18n(currentError.getErrorMessageKey());
                        currentErrors.add(new ErrorDetails(currentError.getBytePosition(),
                                                          BaseLookup.i18n(currentError.getErrorTitleKey()),
                                                          errorMessage,
                                                          currentError.isRequireUserToClear(),
                                                          currentError.getOptions()
                                                                      .stream()
                                                                      .mapToInt((o) -> o.getFlag())
                                                                      .reduce(0, (a, b) -> a | b)));
                    }
                }
                if (currentErrors.isEmpty())
                    currentErrors = null;
            }
        }
        catch (Exception ex)
        {
            steno.exception("ActiveErrorStatusData.updateFromPrinterData threw exception", ex);
            currentErrors = null;
        }
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
    public ArrayList<ErrorDetails> getActiveErrors()
    {
        return currentErrors;
    }

    @JsonProperty
    public void setActiveErrors(ArrayList<ErrorDetails> activeErrors)
    {
        this.currentErrors = activeErrors;
    }
}
