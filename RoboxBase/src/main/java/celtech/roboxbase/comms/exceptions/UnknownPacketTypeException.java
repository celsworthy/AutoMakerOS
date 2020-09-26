/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase.comms.exceptions;

/**
 *
 * @author ianhudson
 */
public class UnknownPacketTypeException extends Exception
{

    /**
     * Creates a new instance of
     * <code>UnknownPacketTypeException</code> without detail message.
     */
    public UnknownPacketTypeException()
    {
    }

    /**
     * Constructs an instance of
     * <code>UnknownPacketTypeException</code> with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public UnknownPacketTypeException(String msg)
    {
        super(msg);
    }
}
