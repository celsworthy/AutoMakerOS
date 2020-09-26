package celtech.roboxbase.printerControl;

import celtech.roboxbase.printerControl.model.PrinterException;

/**
 *
 * @author Ian
 */
public class PrintJobRejectedException extends PrinterException
{

    public PrintJobRejectedException(String loggingMessage)
    {
        super(loggingMessage);
    }
    
}
