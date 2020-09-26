/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package libertysystems.configuration;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class ConfigNotLoadedException extends Exception
{

    /**
     * Creates a new instance of
     * <code>ConfigNotLoadedException</code> without detail message.
     */
    public ConfigNotLoadedException()
    {
    }

    /**
     * Constructs an instance of
     * <code>ConfigNotLoadedException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ConfigNotLoadedException(String msg)
    {
        super(msg);
    }
}
