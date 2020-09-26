/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase.comms.exceptions;

/**
 *
 * @author ianhudson
 */
public class RoboxCommsException extends Exception
{

    /**
     * Creates a new instance of
     * <code>RoboxCommsException</code> without detail message.
     */
    public RoboxCommsException()
    {
    }

    /**
     * Constructs an instance of
     * <code>RoboxCommsException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public RoboxCommsException(String msg)
    {
        super(msg);
    }
}
