package celuk.groot.remote;

import com.fasterxml.jackson.annotation.JsonProperty;
/**
 *
 * @author tonya
 */
public class PurgeData {
    private String printerID;
    private MaterialStatusData materialStatus;
    private HeadEEPROMData headData;

    public PurgeData()
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
    public void setMaterialStatus(MaterialStatusData materialStatus)
    {
        this.materialStatus = materialStatus;
    }

    @JsonProperty
    public MaterialStatusData getMaterialStatus()
    {
        return materialStatus;
    }

    @JsonProperty
    public void setHeadData(HeadEEPROMData headData)
    {
        this.headData = headData;
    }

    @JsonProperty
    public HeadEEPROMData getHeadData()
    {
        return headData;
    }
}
