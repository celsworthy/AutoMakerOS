package celtech.roboxbase.printerControl.comms.commands;

/**
 *
 * @author Ian
 */
public class MacroLoadException extends Exception
{

    /**
     * Creates a new instance of <code>CircularMacroReferenceException</code> without detail
     * message.
     */
    public MacroLoadException()
    {
    }

    /**
     * Constructs an instance of <code>CircularMacroReferenceException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public MacroLoadException(String msg)
    {
        super(msg);
    }
}
