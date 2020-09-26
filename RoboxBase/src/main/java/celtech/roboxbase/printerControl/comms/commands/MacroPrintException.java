package celtech.roboxbase.printerControl.comms.commands;

/**
 *
 * @author Ian
 */
public class MacroPrintException extends Exception
{

    /**
     * Creates a new instance of <code>MacroPrintException</code> without detail message.
     */
    public MacroPrintException()
    {
    }

    /**
     * Constructs an instance of <code>MacroPrintException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public MacroPrintException(String msg)
    {
        super(msg);
    }
}
