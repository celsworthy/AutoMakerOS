package celtech.modelcontrol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author ianhudson
 */
public class ThreeDItemState extends ItemState
{

    /**
     * State captures the state of all the transforms being applied to this
     * Container. It is used as an efficient way of applying Undo and Redo
     * to changes to a Set of ModelContainers.
     */
    public double z = 0.0;
    public double preferredZScale = 1.0;
    public double preferredRotationTwist = 0.0;
    public double preferredRotationLean = 0.0;

    public ThreeDItemState()
    {
    }

    @JsonCreator
    public ThreeDItemState(
            @JsonProperty("modelId") int modelId,
            @JsonProperty("x") double x,
            @JsonProperty("y") double y,
            @JsonProperty("z") double z,
            @JsonProperty("preferredXScale") double preferredXScale,
            @JsonProperty("preferredYScale") double preferredYScale,
            @JsonProperty("preferredZScale") double preferredZScale,
            @JsonProperty("preferredRotationTwist") double preferredRotationTwist,
            @JsonProperty("preferredRotationTurn") double preferredRotationTurn,
            @JsonProperty("preferredRotationLean") double preferredRotationLean)
    {
        super(modelId);
        this.x = x;
        this.y = y;
        this.z = z;
        this.preferredXScale = preferredXScale;
        this.preferredYScale = preferredYScale;
        this.preferredZScale = preferredZScale;
        this.preferredRotationTwist = preferredRotationTwist;
        this.preferredRotationTurn = preferredRotationTurn;
        this.preferredRotationLean = preferredRotationLean;
    }

    /**
     * The assignment operator.
     *
     * @param fromState
     */
    @Override
    public void assignFrom(ItemState fromState)
    {
        if (fromState instanceof ThreeDItemState)
        {
            ThreeDItemState convertedState = (ThreeDItemState) fromState;
            this.x = convertedState.x;
            this.y = convertedState.y;
            this.z = convertedState.z;
            this.preferredXScale = convertedState.preferredXScale;
            this.preferredYScale = convertedState.preferredYScale;
            this.preferredZScale = convertedState.preferredZScale;
            this.preferredRotationTwist = convertedState.preferredRotationTwist;
            this.preferredRotationTurn = convertedState.preferredRotationTurn;
            this.preferredRotationLean = convertedState.preferredRotationLean;
        }
    }
}
