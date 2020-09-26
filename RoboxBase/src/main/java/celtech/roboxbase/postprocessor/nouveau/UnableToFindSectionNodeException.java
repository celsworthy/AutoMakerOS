package celtech.roboxbase.postprocessor.nouveau;

/**
 *
 * @author Ian
 */
public class UnableToFindSectionNodeException extends Exception
{

    /**
     * Creates a new instance of <code>NotAChildOfASectionException</code>
     * without detail message.
     */
    public UnableToFindSectionNodeException()
    {
    }

    /**
     * Constructs an instance of <code>NotAChildOfASectionException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public UnableToFindSectionNodeException(String msg)
    {
        super(msg);
    }
}
