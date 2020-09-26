package celtech.roboxbase.printerControl;

import celtech.roboxbase.printerControl.model.PrinterException;

/**
 *
 * @author Ian
 */
public class PrintActionUnavailableException extends PrinterException
{
    public PrintActionUnavailableException(String loggingMessage)
    {
        super(loggingMessage);
    }
}
