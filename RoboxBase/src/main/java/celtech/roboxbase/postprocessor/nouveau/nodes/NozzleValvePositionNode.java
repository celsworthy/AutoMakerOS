package celtech.roboxbase.postprocessor.nouveau.nodes;

import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Renderable;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.NozzlePosition;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.NozzlePositionProvider;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Ian
 */
public class NozzleValvePositionNode extends GCodeEventNode implements NozzlePositionProvider, Renderable
{

    private boolean fastAsPossible = false;
    private final NozzlePosition nozzlePosition = new NozzlePosition();
    private double replenishExtrusionE = 0;
    private double replenishExtrusionD = 0;

    public void setMoveAsFastAsPossible(boolean fastAsPossible)
    {
        this.fastAsPossible = fastAsPossible;
    }

    public void setReplenishExtrusionE(double replenishExtrusionE)
    {
        this.replenishExtrusionE = replenishExtrusionE;
    }

    public void setReplenishExtrusionD(double replenishExtrusionD)
    {
        this.replenishExtrusionD = replenishExtrusionD;
    }

    @Override
    public String renderForOutput()
    {
        NumberFormat fiveDPformatter = DecimalFormat.getNumberInstance(Locale.UK);
        fiveDPformatter.setMaximumFractionDigits(5);
        fiveDPformatter.setMinimumFractionDigits(5);
        fiveDPformatter.setGroupingUsed(false);

        StringBuilder stringToOutput = new StringBuilder();
        stringToOutput.append('G');
        if (fastAsPossible)
        {
            stringToOutput.append('0');
        } else
        {
            stringToOutput.append('1');
        }
        stringToOutput.append(' ');
        stringToOutput.append(nozzlePosition.renderForOutput());

        if (replenishExtrusionE > 0)
        {
            stringToOutput.append(" F150 E");
            stringToOutput.append(fiveDPformatter.format(replenishExtrusionE));
        }

        if (replenishExtrusionD > 0)
        {
            stringToOutput.append(" F150 D");
            stringToOutput.append(fiveDPformatter.format(replenishExtrusionD));
        }

        stringToOutput.append(getCommentText());

        return stringToOutput.toString().trim();
    }

    @Override
    public NozzlePosition getNozzlePosition()
    {
        return nozzlePosition;
    }
}
