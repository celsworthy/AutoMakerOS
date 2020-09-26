package celtech.roboxbase.configuration.fileRepresentation;

/**
 *
 * @author Ian
 */
public class PrinterEdition
{
    private String typeCode;
    private String friendlyName;

    public String getTypeCode()
    {
        return typeCode;
    }

    public void setTypeCode(String typeCode)
    {
        this.typeCode = typeCode;
    }

    public String getFriendlyName()
    {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName)
    {
        this.friendlyName = friendlyName;
    }
    
    @Override
    public String toString()
    {
        if (friendlyName != null && friendlyName.length() > 0)
            return friendlyName;
        else
            return super.toString();
    } 
}
