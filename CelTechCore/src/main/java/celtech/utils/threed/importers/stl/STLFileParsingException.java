/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package celtech.utils.threed.importers.stl;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class STLFileParsingException extends Exception
{

    /**
     * Creates a new instance of
     * <code>STLFileParsingException</code> without detail message.
     */
    public STLFileParsingException()
    {
    }

    /**
     * Constructs an instance of
     * <code>STLFileParsingException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public STLFileParsingException(String msg)
    {
        super(msg);
    }
}