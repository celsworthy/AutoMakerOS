package celtech.roboxbase.postprocessor.nouveau.timeCalc;

/**
 *
 * @author Ian
 */
public class TimeAndVolumeCalcResult
{

    private final ExtruderTimeAndVolumeCalcComponent extruderEStats;
    private final ExtruderTimeAndVolumeCalcComponent extruderDStats;
    private final TimeCalcComponent feedrateIndependentDuration;

    public TimeAndVolumeCalcResult(ExtruderTimeAndVolumeCalcComponent extruderEStats,
            ExtruderTimeAndVolumeCalcComponent extruderDStats,
            TimeCalcComponent feedrateIndependentDuration)
    {
        this.extruderEStats = extruderEStats;
        this.extruderDStats = extruderDStats;
        this.feedrateIndependentDuration = feedrateIndependentDuration;
    }

    public ExtruderTimeAndVolumeCalcComponent getExtruderEStats()
    {
        return extruderEStats;
    }

    public ExtruderTimeAndVolumeCalcComponent getExtruderDStats()
    {
        return extruderDStats;
    }

    public TimeCalcComponent getFeedrateIndependentDuration()
    {
        return feedrateIndependentDuration;
    }
}
