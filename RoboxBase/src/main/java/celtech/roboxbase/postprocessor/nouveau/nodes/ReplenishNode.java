package celtech.roboxbase.postprocessor.nouveau.nodes;

import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Renderable;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Extrusion;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.ExtrusionProvider;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Feedrate;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.FeedrateProvider;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Ian
 */
public class ReplenishNode extends GCodeEventNode implements ExtrusionProvider, FeedrateProvider, Renderable
{
    private final Extrusion extrusion = new Extrusion();
    private final Feedrate feedrate = new Feedrate();

    public ReplenishNode()
    {
        feedrate.setFeedRate_mmPerMin(150);
    }
    
    //Replenish should always use G1
    @Override
    public String renderForOutput()
    {
        NumberFormat fiveDPformatter = DecimalFormat.getNumberInstance(Locale.UK);
        fiveDPformatter.setMaximumFractionDigits(5);
        fiveDPformatter.setGroupingUsed(false);

        StringBuilder stringToReturn = new StringBuilder();

        stringToReturn.append("G1 ");

        stringToReturn.append(feedrate.renderForOutput());
        stringToReturn.append(' ');
        stringToReturn.append(extrusion.renderForOutput());
        stringToReturn.append(' ');
        stringToReturn.append(getCommentText());

        return stringToReturn.toString().trim();
    }

    @Override
    public Extrusion getExtrusion()
    {
        return extrusion;
    }

    @Override
    public Feedrate getFeedrate()
    {
        return feedrate;
    }
}
