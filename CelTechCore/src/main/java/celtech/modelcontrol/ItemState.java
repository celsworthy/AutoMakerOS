package celtech.modelcontrol;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author ianhudson
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class ItemState
{

    public int modelId;
    public double x;
    public double y;
    public double preferredXScale;
    public double preferredYScale;
    public double preferredRotationTurn;
    
    public abstract void assignFrom(ItemState fromState);

    public ItemState()
    {
    }
    
    public ItemState(int modelId)
    {
        this.modelId = modelId;
    }
}
