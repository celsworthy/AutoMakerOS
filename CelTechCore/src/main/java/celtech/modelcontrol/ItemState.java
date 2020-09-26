package celtech.modelcontrol;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author ianhudson
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class ItemState
{

    public int modelId = -1;
    public double x = 0.0;
    public double y = 0.0;
    public double preferredXScale = 1.0;
    public double preferredYScale = 1.0;
    public double preferredRotationTurn = 0.0;
    
    public abstract void assignFrom(ItemState fromState);

    public ItemState()
    {
    }
    
    public ItemState(int modelId)
    {
        this.modelId = modelId;
    }
}
