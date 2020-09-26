package celtech.roboxbase.configuration.slicer;

/**
 *
 * @author Ian
 */
public class NozzleParameters
{

    private String name;

    private float closedPosition;
    private float openPosition;

    private float diameter;
    private float openOverVolume;
    private float preejectionVolume;
    private float ejectionVolume;
    private float openValueAtMidPoint;
    private float midPointPercent;
    private float wipeVolume;
    private float partialBMinimum;

    private float travelBeforeForcedClose;

    public NozzleParameters()
    {
    }
    
    /**
     * Copy constructor
     * 
     * @param nozzleParameters parameters to copy
     */
    public NozzleParameters(NozzleParameters nozzleParameters)
    {
        this.name = nozzleParameters.getName();
        this.closedPosition = nozzleParameters.getClosedPosition();
        this.openPosition = nozzleParameters.getOpenPosition();
        this.diameter = nozzleParameters.getDiameter();
        this.openOverVolume = nozzleParameters.getOpenOverVolume();
        this.preejectionVolume = nozzleParameters.getPreejectionVolume();
        this.ejectionVolume = nozzleParameters.getEjectionVolume();
        this.openValueAtMidPoint = nozzleParameters.getOpenValueAtMidPoint();
        this.midPointPercent = nozzleParameters.getMidPointPercent();
        this.wipeVolume = nozzleParameters.getWipeVolume();
        this.partialBMinimum = nozzleParameters.getPartialBMinimum();
        this.travelBeforeForcedClose = nozzleParameters.getTravelBeforeForcedClose();
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public float getClosedPosition()
    {
        return closedPosition;
    }

    public void setClosedPosition(float closedPosition)
    {
        this.closedPosition = closedPosition;
    }

    public float getOpenPosition()
    {
        return openPosition;
    }

    public void setOpenPosition(float openPosition)
    {
        this.openPosition = openPosition;
    }

    public float getDiameter()
    {
        return diameter;
    }

    public void setDiameter(float diameter)
    {
        this.diameter = diameter;
    }

    public float getOpenOverVolume()
    {
        return openOverVolume;
    }

    public void setOpenOverVolume(float openOverVolume)
    {
        this.openOverVolume = openOverVolume;
    }

    public float getPreejectionVolume()
    {
        return preejectionVolume;
    }

    public void setPreejectionVolume(float preejectionVolume)
    {
        this.preejectionVolume = preejectionVolume;
    }

    public float getEjectionVolume()
    {
        return ejectionVolume;
    }

    public void setEjectionVolume(float ejectionVolume)
    {
        this.ejectionVolume = ejectionVolume;
    }

    public float getOpenValueAtMidPoint()
    {
        return openValueAtMidPoint;
    }

    public void setOpenValueAtMidPoint(float openValueAtMidPoint)
    {
        this.openValueAtMidPoint = openValueAtMidPoint;
    }

    public float getMidPointPercent()
    {
        return midPointPercent;
    }

    public void setMidPointPercent(float midPointPercent)
    {
        this.midPointPercent = midPointPercent;
    }

    public float getWipeVolume()
    {
        return wipeVolume;
    }

    public void setWipeVolume(float wipeVolume)
    {
        this.wipeVolume = wipeVolume;
    }

    public float getPartialBMinimum()
    {
        return partialBMinimum;
    }

    public void setPartialBMinimum(float partialBMinimum)
    {
        this.partialBMinimum = partialBMinimum;
    }

    public float getTravelBeforeForcedClose()
    {
        return travelBeforeForcedClose;
    }

    public void setTravelBeforeForcedClose(float travelBeforeForcedClose)
    {
        this.travelBeforeForcedClose = travelBeforeForcedClose;
    }
}
