package celtech.roboxbase.comms.exceptions;

/**
 *
 * @author Ian
 */
public class PortNotFoundException extends Exception
{

    /**
     * Creates a new instance of <code>PortNotFoundException</code> without
     * detail message.
     */
    public PortNotFoundException()
    {
    }

    /**
     * Constructs an instance of <code>PortNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public PortNotFoundException(String msg)
    {
        super(msg);
    }
}
