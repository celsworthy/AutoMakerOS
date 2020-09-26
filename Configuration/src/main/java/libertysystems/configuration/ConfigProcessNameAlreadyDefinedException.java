/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package libertysystems.configuration;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class ConfigProcessNameAlreadyDefinedException extends Exception
{

    /**
     * Creates a new instance of
     * <code>ConfigProcessNameAlreadyDefinedException</code> without detail
     * message.
     */
    public ConfigProcessNameAlreadyDefinedException()
    {
    }

    /**
     * Constructs an instance of
     * <code>ConfigProcessNameAlreadyDefinedException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public ConfigProcessNameAlreadyDefinedException(String msg)
    {
        super(msg);
    }
}
