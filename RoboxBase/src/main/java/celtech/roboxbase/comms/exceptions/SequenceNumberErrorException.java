/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase.comms.exceptions;

/**
 *
 * @author ianhudson
 */
public class SequenceNumberErrorException extends RoboxCommsException
{

    /**
     * Creates a new instance of
     * <code>BadCommandException</code> without detail message.
     */
    public SequenceNumberErrorException()
    {
    }

    /**
     * Constructs an instance of
     * <code>BadCommandException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public SequenceNumberErrorException(String msg)
    {
        super(msg);
    }
}
