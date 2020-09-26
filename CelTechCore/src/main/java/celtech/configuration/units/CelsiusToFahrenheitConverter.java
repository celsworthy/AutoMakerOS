package celtech.configuration.units;

/**
 *
 * @author Ian
 */
public class CelsiusToFahrenheitConverter implements UnitConverter
{

    @Override
    public double convertToImperial(double value)
    {
        return ((value * 9.0) / 5.0) + 32.0;
    }

    @Override
    public double convertToMetric(double value)
    {
        return ((value - 32.0) * 5.0) / 9.0;
    }

}
