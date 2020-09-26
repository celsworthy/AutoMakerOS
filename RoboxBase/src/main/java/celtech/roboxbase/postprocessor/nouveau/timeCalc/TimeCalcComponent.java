package celtech.roboxbase.postprocessor.nouveau.timeCalc;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Ian
 */
public class TimeCalcComponent
{

    private double total_duration;
    private final Map<Integer, Double> layerNumberToPredictedDuration = new HashMap<>();

    public TimeCalcComponent()
    {
    }

    public double getTotal_duration()
    {
        return total_duration;
    }

    public Map<Integer, Double> getLayerNumberToPredictedDuration()
    {
        return layerNumberToPredictedDuration;
    }

    public void incrementDuration(int layerNumber, double duration)
    {
        total_duration += duration;
        if (layerNumberToPredictedDuration.containsKey(layerNumber))
        {
            layerNumberToPredictedDuration.replace(layerNumber, layerNumberToPredictedDuration.get(layerNumber) + duration);
        } else
        {
            layerNumberToPredictedDuration.put(layerNumber, duration);
        }
    }
}
