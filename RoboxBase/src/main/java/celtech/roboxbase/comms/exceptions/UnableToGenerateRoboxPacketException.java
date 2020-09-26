/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase.comms.exceptions;

/**
 *
 * @author ianhudson
 */
public class UnableToGenerateRoboxPacketException extends Exception
{

    /**
     * Creates a new instance of
     * <code>UnableToGenerateRoboxPacketException</code> without detail message.
     */
    public UnableToGenerateRoboxPacketException()
    {
    }

    /**
     * Constructs an instance of
     * <code>UnableToGenerateRoboxPacketException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public UnableToGenerateRoboxPacketException(String msg)
    {
        super(msg);
    }
}
