package celtech.roboxbase.services.slicer;

/**
 *
 * @author ianhudson
 */
public enum PrintQualityEnumeration
{

    DRAFT("Draft"),

    NORMAL("Normal"),

    FINE("Fine"),

    CUSTOM("Custom");

    private final String friendlyName;

    private PrintQualityEnumeration(String friendlyName)
    {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName()
    {
        return friendlyName;
    }
    
    @Override
    public String toString()
    {
        return friendlyName;
    }
}
