package celtech.roboxbase.postprocessor;

/**
 *
 * @author Ian
 */
public class DidntFindEventException extends Exception
{

    /**
     * Creates a new instance of <code>DidntFindEventException</code> without
     * detail message.
     */
    public DidntFindEventException()
    {
    }

    /**
     * Constructs an instance of <code>DidntFindEventException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DidntFindEventException(String msg)
    {
        super(msg);
    }
}
