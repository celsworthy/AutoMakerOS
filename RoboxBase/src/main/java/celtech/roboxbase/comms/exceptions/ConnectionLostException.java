package celtech.roboxbase.comms.exceptions;

/**
 *
 * @author ianhudson
 */
public class ConnectionLostException extends RoboxCommsException
{

    /**
     * Creates a new instance of
     * <code>BadCommandException</code> without detail message.
     */
    public ConnectionLostException()
    {
    }

    /**
     * Constructs an instance of
     * <code>BadCommandException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ConnectionLostException(String msg)
    {
        super(msg);
    }
}
