/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase.comms.exceptions;

/**
 *
 * @author ianhudson
 */
public class BadCommandException extends RoboxCommsException
{

    /**
     * Creates a new instance of
     * <code>BadCommandException</code> without detail message.
     */
    public BadCommandException()
    {
    }

    /**
     * Constructs an instance of
     * <code>BadCommandException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public BadCommandException(String msg)
    {
        super(msg);
    }
}
