package celtech.modelcontrol;

/**
 *
 * @author ianhudson
 */
public class WrongItemStateType extends Exception
{

    /**
     * Creates a new instance of <code>WrongItemStateType</code> without detail
     * message.
     */
    public WrongItemStateType()
    {
    }

    /**
     * Constructs an instance of <code>WrongItemStateType</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public WrongItemStateType(String msg)
    {
        super(msg);
    }
}
