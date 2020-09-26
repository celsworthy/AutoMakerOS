package celtech.roboxbase.postprocessor.nouveau.timeCalc;

/**
 *
 * @author Ian
 */
public class ExtruderTimeAndVolumeCalcComponent
{

    private double volume;
    private final TimeCalcComponent duration = new TimeCalcComponent();

    public ExtruderTimeAndVolumeCalcComponent()
    {
    }

    public void incrementVolume(double increment)
    {
        volume += increment;
    }
    
    public double getVolume()
    {
        return volume;
    }

    public TimeCalcComponent getDuration()
    {
        return duration;
    }
}
