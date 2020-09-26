package celtech.configuration.units;

/**
 *
 * @author Ian
 */
public enum UnitType
{

    NONE(null, null, null),
    DISTANCE("mm", "in", new MMToInchesConverter()),
    TEMPERATURE("C", "F", new CelsiusToFahrenheitConverter());

    private final String metricSymbol;
    private final String imperialSymbol;
    private final UnitConverter unitConverter;

    private UnitType(String metricSymbol,
            String imperialSymbol,
            UnitConverter unitConverter)
    {
        this.metricSymbol = metricSymbol;
        this.imperialSymbol = imperialSymbol;
        this.unitConverter = unitConverter;
    }

    public String getMetricSymbol()
    {
        return metricSymbol;
    }

    public String getImperialSymbol()
    {
        return imperialSymbol;
    }

    public UnitConverter getUnitConverter()
    {
        return unitConverter;
    }
}
