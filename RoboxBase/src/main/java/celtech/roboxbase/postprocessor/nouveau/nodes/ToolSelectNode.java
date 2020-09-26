package celtech.roboxbase.postprocessor.nouveau.nodes;

import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Renderable;
import java.util.Optional;

/**
 *
 * @author Ian
 */
public class ToolSelectNode extends GCodeEventNode implements Renderable
{

    //For DM head
    //Tool 0 is extruder D
    //Tool 1 is extruder E
    private int toolNumber = -1;
    private boolean outputSuppressed = false;
    private double estimatedDuration_ignoresFeedrate = 0;
    private Optional<Double> startTimeFromStartOfPrint_secs = Optional.empty();

    public int getToolNumber()
    {
        return toolNumber;
    }

    public void setToolNumber(int toolNumber)
    {
        this.toolNumber = toolNumber;
    }

    public void suppressNodeOutput(boolean suppress)
    {
        outputSuppressed = suppress;
    }

    public boolean isNodeOutputSuppressed()
    {
        return outputSuppressed;
    }

    public void setEstimatedDuration(double estimatedDuration)
    {
        this.estimatedDuration_ignoresFeedrate = estimatedDuration;
    }

    public double getEstimatedDuration()
    {
        return estimatedDuration_ignoresFeedrate;
    }

    public Optional<Double> getStartTimeFromStartOfPrint_secs()
    {
        return startTimeFromStartOfPrint_secs;
    }

    public void setStartTimeFromStartOfPrint_secs(double value)
    {
        startTimeFromStartOfPrint_secs = Optional.of(value);
    }

    @Override
    public String renderForOutput()
    {
        String stringToReturn = "";

        if (!outputSuppressed)
        {
            stringToReturn += "T" + getToolNumber();
            stringToReturn += getCommentText();
        } else
        {
            stringToReturn += "; Suppressed Tool Node -";
            if (getFinishTimeFromStartOfPrint_secs().isPresent())
            {
                stringToReturn += "T start " + getStartTimeFromStartOfPrint_secs().get();
                stringToReturn += " T finish " + getFinishTimeFromStartOfPrint_secs().get();
            }
        }
        stringToReturn += " ; Tool Node duration: " + getEstimatedDuration();

        return stringToReturn;
    }
}
