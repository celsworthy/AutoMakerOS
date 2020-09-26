package celtech.configuration.units;

/**
 *
 * @author Ian
 */
public class MMToInchesConverter implements UnitConverter
{

    @Override
    public double convertToImperial(double value)
    {
        return value / 25.4;
    }

    @Override
    public double convertToMetric(double value)
    {
        return value * 25.4;
    }

}
