package celuk.groot.remote;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author taldhous
 */
public class PurgeTarget
{
    private int[] targetTemperature = new int[] {-1, -1};
    private int[] lastTemperature = new int[] {-1, -1};
    private int[] newTemperature = new int[] {-1, -1};
    private boolean safetyOn = false;
    
    public PurgeTarget()
    {
        // Jackson deserialization
    }

    @JsonProperty
    public int[] getTargetTemperature()
    {
        return targetTemperature;
    }

   @JsonProperty
    public void setTargetTemperature(int[] targetTemperature)
    {
        this.targetTemperature = targetTemperature;
    }
 
    @JsonProperty
    public int[] getLastTemperature()
    {
        return lastTemperature;
    }

   @JsonProperty
    public void setLastTemperature(int[] lastTemperature)
    {
        this.lastTemperature = lastTemperature;
    }
 
    @JsonProperty
    public int[] getNewTemperature()
    {
        return newTemperature;
    }

   @JsonProperty
    public void setNewTemperature(int[] newTemperature)
    {
        this.newTemperature = newTemperature;
    }
 
    @JsonProperty
    public boolean getSafetyOn()
    {
        return safetyOn;
    }

   @JsonProperty
    public void setSafetyOn(boolean safetyOn)
    {
        this.safetyOn = safetyOn;
    }
}
