package celtech.roboxbase.postprocessor.nouveau.nodes.nodeFunctions;

import celtech.roboxbase.postprocessor.nouveau.nodes.providers.MovementProvider;

/**
 *
 * @author Ian
 */
public interface SupportsPrintTimeCalculation
{
    public double timeToReach(MovementProvider destinationNode) throws DurationCalculationException;
}
