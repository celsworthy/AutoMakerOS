package celtech.roboxbase.comms.exceptions;

/**
 *
 * @author ianhudson
 */
public class CommsSuppressedException extends RoboxCommsException
{

    /**
     * Creates a new instance of
     * <code>BadCommandException</code> without detail message.
     */
    public CommsSuppressedException()
    {
    }

    /**
     * Constructs an instance of
     * <code>BadCommandException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public CommsSuppressedException(String msg)
    {
        super(msg);
    }
}
