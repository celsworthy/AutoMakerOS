/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package libertysystems.stenographer;

/**
 *
 * @author ian_2
 * Added this comment to test update
 */
public class InvalidLogLevelException extends Exception
{

    /**
     * Creates a new instance of <code>InvalidLogLevelException</code> without detail message.
     */
    public InvalidLogLevelException()
    {
    }

    /**
     * Constructs an instance of <code>InvalidLogLevelException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public InvalidLogLevelException(String msg)
    {
        super(msg);
    }
}
