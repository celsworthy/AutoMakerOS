/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package libertysystems.configuration;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class ConfigItemIsAnArray extends Exception
{

    /**
     * Creates a new instance of
     * <code>ConfigItemIsAnArray</code> without detail message.
     */
    public ConfigItemIsAnArray()
    {
    }

    /**
     * Constructs an instance of
     * <code>ConfigItemIsAnArray</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ConfigItemIsAnArray(String msg)
    {
        super(msg);
    }
}
