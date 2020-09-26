package celtech.roboxbase.postprocessor.nouveau.nodes;

import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Renderable;

/**
 *
 * @author Ian
 */
public class NodeProcessingException extends Exception
{

    /**
     * Creates a new instance of <code>NodeProcessingException</code> without
     * detail message.
     */
    public NodeProcessingException()
    {
    }

    /**
     * Constructs an instance of <code>NodeProcessingException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NodeProcessingException(String msg, GCodeEventNode node)
    {
        super(msg + " whilst processing node:" + node.toString());
    }

    /**
     * Constructs an instance of <code>NodeProcessingException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NodeProcessingException(String msg, Renderable node)
    {
        super(msg + " whilst processing node:" + node.renderForOutput());
    }
}
