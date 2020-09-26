package celtech.modelcontrol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author ianhudson
 */
public class TwoDItemState extends ItemState
{

    /**
     * State captures the state of all the transforms being applied to this
     * Container. It is used as an efficient way of applying Undo and Redo
     * to changes to a Set of Containers.
     */

    public TwoDItemState()
    {
    }

    @JsonCreator
    public TwoDItemState(
            @JsonProperty("modelId") int modelId,
            @JsonProperty("x") double x,
            @JsonProperty("y") double y,
            @JsonProperty("preferredXScale") double preferredXScale,
            @JsonProperty("preferredYScale") double preferredYScale,
            @JsonProperty("preferredRotationTurn") double preferredRotationTurn)
    {
        super(modelId);
        this.x = x;
        this.y = y;
        this.preferredXScale = preferredXScale;
        this.preferredYScale = preferredYScale;
        this.preferredRotationTurn = preferredRotationTurn;
    }

    /**
     * The assignment operator.
     *
     * @param fromState
     */
    @Override
    public void assignFrom(ItemState fromState)
    {
        if (fromState instanceof TwoDItemState)
        {
            TwoDItemState convertedState = (TwoDItemState) fromState;
            this.x = convertedState.x;
            this.y = convertedState.y;
            this.preferredXScale = convertedState.preferredXScale;
            this.preferredYScale = convertedState.preferredYScale;
            this.preferredRotationTurn = convertedState.preferredRotationTurn;
        }
    }
}
