package celuk.groot.remote;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author taldhous
 */
public class MaterialStatusData
{
    private String printerID;

    //Material
     private FilamentDetails[] attachedFilaments = null;

    public MaterialStatusData()
    {
        // Jackson deserialization
    }

    @JsonProperty
    public String getPrinterID()
    {
        return printerID;
    }

    @JsonProperty
    public void setPrinterID(String printerID)
    {
        this.printerID = printerID;
    }

    @JsonProperty
    public void setAttachedFilaments(FilamentDetails[] attachedFilaments)
    {
        this.attachedFilaments = attachedFilaments;
    }

    @JsonProperty
    public FilamentDetails[] getAttachedFilaments()
    {
        return attachedFilaments;
    }
}
