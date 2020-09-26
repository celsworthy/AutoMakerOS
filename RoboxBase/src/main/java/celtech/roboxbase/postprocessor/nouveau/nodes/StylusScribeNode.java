package celtech.roboxbase.postprocessor.nouveau.nodes;

import celtech.roboxbase.postprocessor.nouveau.nodes.nodeFunctions.DurationCalculationException;
import celtech.roboxbase.postprocessor.nouveau.nodes.nodeFunctions.SupportsPrintTimeCalculation;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Feedrate;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.FeedrateProvider;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Movement;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.MovementProvider;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Renderable;
import java.util.Optional;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 *
 * @author Ian
 */
public class StylusScribeNode extends GCodeEventNode implements MovementProvider, FeedrateProvider, SupportsPrintTimeCalculation, Renderable, MergeableWithToolchange
{
    private Movement movement = new Movement();
    private Feedrate feedrate = new Feedrate();

    private boolean isToolChangeRequired = false;
    private int toolNumber;

    //Travel events should always use G1
    @Override
    public String renderForOutput()
    {
        StringBuilder stringToReturn = new StringBuilder();

        if (isToolChangeRequired)
        {
            stringToReturn.append('T');
            stringToReturn.append(toolNumber);
            stringToReturn.append(' ');
        } else
        {
            stringToReturn.append("G1 ");
        }

        stringToReturn.append(feedrate.renderForOutput());
        stringToReturn.append(' ');
        stringToReturn.append(movement.renderForOutput());
        stringToReturn.append(' ');
        stringToReturn.append(getCommentText());

        return stringToReturn.toString();
    }

    @Override
    public Movement getMovement()
    {
        return movement;
    }
    
    @Override
    public Feedrate getFeedrate()
    {
        return feedrate;
    }

    @Override
    public void changeToolDuringMovement(int toolNumber)
    {
        isToolChangeRequired = true;
        this.toolNumber = toolNumber;
    }

    @Override
    public double timeToReach(MovementProvider destinationNode) throws DurationCalculationException
    {
        Vector2D source = movement.toVector2D();
        Vector2D destination = new Vector2D(destinationNode.getMovement().getX(), destinationNode.getMovement().getY());

        double distance = source.distance(destination);

        double time = distance / feedrate.getFeedRate_mmPerSec();

        if (time < 0)
        {
            throw new DurationCalculationException(this, destinationNode);
        }

        return time;
    }

    @Override
    public StylusScribeNode clone()
    {
        StylusScribeNode returnedNode = new StylusScribeNode();
        returnedNode.movement = movement.clone();
        returnedNode.feedrate = feedrate.clone();
        returnedNode.setCommentText(getCommentText());

        return returnedNode;
    }
}
