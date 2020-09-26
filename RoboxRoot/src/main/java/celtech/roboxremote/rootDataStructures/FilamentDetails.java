package celtech.roboxremote.rootDataStructures;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author taldhous
 */
public class FilamentDetails
{
    private String filamentName = null;
    private String materialName = null;
    private String webColour = null;
    private int filamentTemperature = -1;
    private float remainingFilament = -1.0F;
    private boolean customFlag = false;
    private boolean materialLoaded = false;
    private boolean canEject = false;
    private boolean canExtrude = false;
    private boolean canRetract = false;
    
    public FilamentDetails()
    {
        // Jackson deserialization
    }

    public FilamentDetails(String filamentName, String materialName, String webColour, int filamentTemperature,
                           float remainingFilament, boolean customFlag, boolean materialLoaded, boolean canEject,
                           boolean canExtrude, boolean canRetract)
    {
        this.filamentName = filamentName;
        this.materialName = materialName;
        this.webColour = webColour;
        this.filamentTemperature = filamentTemperature;
        this.remainingFilament = remainingFilament;
        this.customFlag = customFlag;
        this.materialLoaded = materialLoaded;
        this.canEject = canEject;
        this.canExtrude = canExtrude;
        this.canRetract = canRetract;
    }

    @JsonProperty
    public String getFilamentName()
    {
        return filamentName;
    }

    @JsonProperty
    public void setFilamentName(String filamentName)
    {
        this.filamentName = filamentName;
    }

    @JsonProperty
    public String getMaterialName()
    {
        return materialName;
    }

    @JsonProperty
    public void setMaterialName(String materialName)
    {
        this.materialName = materialName;
    }

    @JsonProperty
    public String getWebColour()
    {
        return webColour;
    }

    @JsonProperty
    public void setWebColour(String webColour)
    {
        this.webColour = webColour;
    }
    
    @JsonProperty
    public int getFilamentTemperature()
    {
        return filamentTemperature;
    }

    @JsonProperty
    public void setFilamentTemperature(int filamentTemperature)
    {
        this.filamentTemperature = filamentTemperature;
    }

    @JsonProperty
    public float getRemainingFilament()
    {
        return remainingFilament;
    }

    @JsonProperty
    public void setRemainingFilament(float remainingFilament)
    {
        this.remainingFilament = remainingFilament;
    }

    @JsonProperty
    public boolean getCustomFlag()
    {
        return customFlag;
    }

    @JsonProperty
    public void setCustomFlag(boolean customFlag)
    {
        this.customFlag = customFlag;
    }

    @JsonProperty
    public boolean getMaterialLoaded()
    {
        return materialLoaded;
    }

    @JsonProperty
    public void setMaterialLoaded(boolean materialLoaded)
    {
        this.materialLoaded = materialLoaded;
    }

    @JsonProperty
    public boolean getCanEject()
    {
        return canEject;
    }

    @JsonProperty
    public void setCanEject(boolean canEject)
    {
        this.canEject = canEject;
    }

    @JsonProperty
    public boolean getCanExtrude()
    {
        return canExtrude;
    }

    @JsonProperty
    public void setCanExtrude(boolean canExtrude)
    {
        this.canExtrude = canExtrude;
    }

    @JsonProperty
    public boolean getCanRetract()
    {
        return canRetract;
    }

    @JsonProperty
    public void setCanRetract(boolean canRetract)
    {
        this.canRetract = canRetract;
    }
}
