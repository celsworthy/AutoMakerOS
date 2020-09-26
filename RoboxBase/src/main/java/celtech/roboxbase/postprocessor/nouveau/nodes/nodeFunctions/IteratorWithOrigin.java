package celtech.roboxbase.postprocessor.nouveau.nodes.nodeFunctions;

import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import java.util.Iterator;

/**
 *
 * @author Ian
 */
public abstract class IteratorWithOrigin<T> implements Iterator<T>
{
    /**
     *
     * @param originNode
     */
    public abstract void setOriginNode(GCodeEventNode originNode);

}
