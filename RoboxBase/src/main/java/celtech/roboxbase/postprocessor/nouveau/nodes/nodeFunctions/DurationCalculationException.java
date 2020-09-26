package celtech.roboxbase.postprocessor.nouveau.nodes.nodeFunctions;

import celtech.roboxbase.postprocessor.nouveau.nodes.providers.MovementProvider;

/**
 *
 * @author Ian
 */
public class DurationCalculationException extends Exception
{

    private final MovementProvider fromNode;
    private final MovementProvider toNode;

    /**
     * Creates a new instance of <code>DurationCalculationException</code>
     * without detail message.
     *
     * @param fromNode
     * @param toNode
     */
    public DurationCalculationException(MovementProvider fromNode, MovementProvider toNode)
    {
        this.fromNode = fromNode;
        this.toNode = toNode;
    }

    /**
     * Constructs an instance of <code>DurationCalculationException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     * @param fromNode
     * @param toNode
     */
    public DurationCalculationException(String msg, MovementProvider fromNode, MovementProvider toNode)
    {
        super(msg);
        this.fromNode = fromNode;
        this.toNode = toNode;

    }

    public MovementProvider getFromNode()
    {
        return fromNode;
    }

    public MovementProvider getToNode()
    {
        return toNode;
    }
}
