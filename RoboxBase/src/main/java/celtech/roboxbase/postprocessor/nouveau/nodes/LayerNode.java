package celtech.roboxbase.postprocessor.nouveau.nodes;

import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Renderable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Ian
 */
public class LayerNode extends GCodeEventNode implements Renderable
{

    private int layerNumber = -1;
    private int numberOfUnrecognisedElements = 0;
    private double layerHeight_mm = 0;

    public LayerNode()
    {
    }

    public LayerNode(int layerNumber)
    {
        this.layerNumber = layerNumber;
    }

    public void setLayerNumber(int layerNumber)
    {
        this.layerNumber = layerNumber;
    }

    public int getLayerNumber()
    {
        return layerNumber;
    }

    public int getNumberOfUnrecognisedElements()
    {
        return numberOfUnrecognisedElements;
    }

    public void setNumberOfUnrecognisedElements(int numberOfUnrecognisedElements)
    {
        this.numberOfUnrecognisedElements = numberOfUnrecognisedElements;
    }

    public void setLayerHeight_mm(double layerHeight_mm)
    {
        this.layerHeight_mm = layerHeight_mm;
    }

    public double getLayerHeight_mm()
    {
        return layerHeight_mm;
    }

    @Override
    public String renderForOutput()
    {
        NumberFormat threeDPformatter = DecimalFormat.getNumberInstance(Locale.UK);
        threeDPformatter.setMaximumFractionDigits(3);
        threeDPformatter.setMinimumFractionDigits(3);
        threeDPformatter.setGroupingUsed(false);
        
        return ";LAYER:" + layerNumber + " height:" + threeDPformatter.format(layerHeight_mm) + getCommentText();
    }
}
