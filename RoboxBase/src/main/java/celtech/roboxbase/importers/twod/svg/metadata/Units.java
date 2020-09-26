package celtech.roboxbase.importers.twod.svg.metadata;

/**
 *
 * @author ianhudson
 */
public enum Units
{
    UNKNOWN(0.0, "?", "???"),
    CM(10.0, "cm", "centimetre"),
    CUSTOM(1.0, "-", "custom"),
    IN(25.4, "in", "inch"),
    FEET(304.8, "ft", "feet"),
    M(1000.0, "m", "metre"),
    MM(1.0, "mm", "millimetre"),
    PT(0.3527777778, "pt", "point"),
    PX(1.0, "px", "pixel"),
    YARD(914.4, "yd", "yard");

    private double conversionFactor;
    private String shortName;
    private String longName;
    
    Units(double conversionFactor, String shortName, String longName)
    {
        this.conversionFactor = conversionFactor;
        this.shortName = shortName;
        this.longName = longName;
    }

    private void setConversionFactor(double conversionFactor)
    {
        this.conversionFactor = conversionFactor;
    }

    public double getConversionFactor()
    {
        return conversionFactor;
    }
    
    public String getShortName()
    {
        return shortName;
    }
    
    public String getLongName()
    {
        return longName;
    }

    double convertFromMM(double value)
    {
        return value / conversionFactor;
    }

    double convertToMM(double value)
    {
        return conversionFactor * value;
    }

    double convert(double value, Units fromUnits, Units toUnits)
    {
        return toUnits.conversionFactor * value / fromUnits.conversionFactor;
    }

    public static Units getUnitType(String sizeString)
    {
        Units unitType = UNKNOWN;
        String sslc = sizeString.toLowerCase();
        // Check for mm first, as it clashes with m.
        if (sslc.endsWith(MM.shortName.toLowerCase()))
        {
            unitType = MM;
        }
        else
        {
            for (Units units : Units.values())
            {
                if (sslc.endsWith(units.shortName.toLowerCase()))
                {
                    unitType = units;
                    break;
                }
            }
        }
        return unitType;
    }
    
    public static void setCustomConversionFactor(double conversionFactor)
    {
        CUSTOM.setConversionFactor(conversionFactor);
    }

    public static void setCustomNames(String shortName, String longName)
    {
        CUSTOM.shortName = shortName;
        CUSTOM.longName = longName;
    }

    public static void setPixelConversionFactor(double conversionFactor)
    {
        PX.setConversionFactor(conversionFactor);
    }
}
