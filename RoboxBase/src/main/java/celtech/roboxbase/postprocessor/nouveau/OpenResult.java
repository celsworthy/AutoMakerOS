package celtech.roboxbase.postprocessor.nouveau;

import celtech.roboxbase.postprocessor.nouveau.nodes.ExtrusionNode;


/**
 *
 * @author Ian
 */
public class OpenResult
{

    private double outstandingEReplenish;
    private double outstandingDReplenish;
    private final boolean nozzleOpen;
    private final int lastToolNumber;
    private final int opensInLastTool;
    private final ExtrusionNode lastNozzleClose;

    public OpenResult(double outstandingEReplenish,
            double outstandingDReplenish,
            boolean nozzleOpen,
            int lastToolNumber,
            int opensInLastTool,
            ExtrusionNode lastNozzleClose)
    {
        this.outstandingEReplenish = outstandingEReplenish;
        this.outstandingDReplenish = outstandingDReplenish;
        this.nozzleOpen = nozzleOpen;
        this.lastToolNumber = lastToolNumber;
        this.opensInLastTool = opensInLastTool;
        this.lastNozzleClose = lastNozzleClose;
    }

    public double getOutstandingDReplenish()
    {
        return outstandingDReplenish;
    }

    public void setOutstandingDReplenish(double outstandingDReplenish)
    {
        this.outstandingDReplenish = outstandingDReplenish;
    }

    public double getOutstandingEReplenish()
    {
        return outstandingEReplenish;
    }

    public void setOutstandingEReplenish(double outstandingEReplenish)
    {
        this.outstandingEReplenish = outstandingEReplenish;
    }

    public boolean isNozzleOpen()
    {
        return nozzleOpen;
    }

    public int getLastToolNumber()
    {
        return lastToolNumber;
    }

    public int getOpensInLastTool()
    {
        return opensInLastTool;
    }

    public ExtrusionNode getLastNozzleClose()
    {
        return lastNozzleClose;
    }
}
