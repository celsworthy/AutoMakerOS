package celtech.roboxbase.postprocessor.nouveau.nodes;

import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Extrusion;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.ExtrusionProvider;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Feedrate;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.FeedrateProvider;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Renderable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ian
 */
public class RetractNode extends GCodeEventNode implements ExtrusionProvider, FeedrateProvider, Renderable
{
    private final Feedrate feedrate = new Feedrate();
    private final Extrusion extrusion = new Extrusion();
    private double extrusionSinceLastRetract = 0;
    private ExtrusionNode priorExtrusion = null;
    private List<SectionNode> sectionsToConsider = new ArrayList<>();

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
    
    public void setExtrusionSinceLastRetract(double value)
    {
        extrusionSinceLastRetract = value;
    }

    public double getExtrusionSinceLastRetract()
    {
        return extrusionSinceLastRetract;
    }
    
    public void setPriorExtrusionNode(ExtrusionNode node)
    {
        priorExtrusion = node;
    }
    
    public ExtrusionNode getPriorExtrusionNode()
    {
        return priorExtrusion;
    }
    
    public void setSectionsToConsider(List<SectionNode> sectionNodes)
    {
        sectionsToConsider = sectionNodes;
    }
    
    public List<SectionNode> getSectionsToConsider()
    {
        return sectionsToConsider;
    }

    @Override
    public String toString()
    {
        return "Retract " + feedrate.renderForOutput() + extrusion.renderForOutput();
    }

    @Override
    public String renderForOutput()
    {
        StringBuilder stringToOutput = new StringBuilder();

        stringToOutput.append("G1 ");
        stringToOutput.append(feedrate.renderForOutput());
        stringToOutput.append(' ');
        stringToOutput.append(extrusion.renderForOutput());
        stringToOutput.append(' ');
        stringToOutput.append(getCommentText());
        return stringToOutput.toString().trim();
    }
}
