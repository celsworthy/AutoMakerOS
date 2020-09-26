package celtech.roboxbase.postprocessor.nouveau.nodes;

import java.util.Iterator;

/**
 *
 * @author Ian
 */
public class SectionNode extends GCodeEventNode
{

    private boolean totalExtrusionValid = false;
    private double totalExtrusion = 0;

    private void setTotalExtrusion(double extrusionInSection)
    {
        totalExtrusion = extrusionInSection;
        totalExtrusionValid = true;
    }

    public void recalculateExtrusion()
    {
        double extrusionInSection = 0;

        Iterator<GCodeEventNode> sectionChildren = this.childIterator();
        while (sectionChildren.hasNext())
        {
            GCodeEventNode eventToExamine = sectionChildren.next();
            if (eventToExamine instanceof ExtrusionNode)
            {
                extrusionInSection += ((ExtrusionNode) eventToExamine).getExtrusion().getE();
            }
        }

        setTotalExtrusion(extrusionInSection);
    }

    public boolean isTotalExtrusionValid()
    {
        return totalExtrusionValid;
    }

    public double getTotalExtrusion()
    {
        if (totalExtrusionValid)
        {
            return totalExtrusion;
        } else
        {
            return 0;
        }
    }
}
