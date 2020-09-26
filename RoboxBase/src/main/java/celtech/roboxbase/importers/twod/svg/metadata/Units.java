package celtech.roboxbase.importers.twod.svg.metadata;

/**
 *
 * @author ianhudson
 */
public enum Units
{

    UNKNOWN,
    MM,
    IN,
    PX;

    public static Units getUnitType(String sizeString)
    {
        Units unitType = UNKNOWN;
        for (Units units : Units.values())
        {
            if (sizeString.toLowerCase().endsWith(units.name().toLowerCase()))
            {
                unitType = units;
            }
        }
        return unitType;
    }
}
