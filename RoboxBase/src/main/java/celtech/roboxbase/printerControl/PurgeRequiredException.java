package celtech.roboxbase.printerControl;

import celtech.roboxbase.printerControl.model.PrinterException;

/**
 *
 * @author Ian
 */
public class PurgeRequiredException extends PrinterException
{
    public PurgeRequiredException(String loggingMessage)
    {
        super(loggingMessage);
    }
}
