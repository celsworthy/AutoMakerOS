package celtech.roboxbase.postprocessor.nouveau;

/**
 *
 * @author Ian
 */
public class ParserInputException extends RuntimeException
{

    /**
     * Creates a new instance of <code>ParserInputException</code> without
     * detail message.
     */
    public ParserInputException()
    {
    }

    /**
     * Constructs an instance of <code>ParserInputException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ParserInputException(String msg)
    {
        super(msg);
    }
}
