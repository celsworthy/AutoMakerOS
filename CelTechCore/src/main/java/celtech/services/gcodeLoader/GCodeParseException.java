/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.services.gcodeLoader;

/**
 *
 * @author ianhudson
 */
public class GCodeParseException extends Exception
{

    /**
     * Creates a new instance of
     * <code>GCodeParseException</code> without detail message.
     */
    public GCodeParseException()
    {
    }

    /**
     * Constructs an instance of
     * <code>GCodeParseException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public GCodeParseException(String msg)
    {
        super(msg);
    }
}
