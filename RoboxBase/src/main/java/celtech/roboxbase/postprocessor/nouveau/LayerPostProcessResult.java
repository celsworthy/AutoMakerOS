package celtech.roboxbase.postprocessor.nouveau;

import celtech.roboxbase.postprocessor.nouveau.nodes.LayerNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.SectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.ToolSelectNode;
import java.util.Optional;

/**
 *
 * @author Ian
 */
public class LayerPostProcessResult
{
    private final LayerNode layerData;
    private String lastSection = null;
    private Optional<Integer> lastObjectNumber = Optional.empty();
    private double lastFeedrateInForce = -1;
    private final ToolSelectNode lastToolSelectInForce;
    private final ToolSelectNode lastToolSelectOfSameNumber;
    private SectionNode lastSectionNodeInForce = null;
    private int lastLineNumber = 0;

    public LayerPostProcessResult(
            LayerNode layerData,
            int lastObjectNumber,
            SectionNode sectionNode,
            ToolSelectNode toolSelectNode,
            ToolSelectNode lastToolSelectOfSameNumber,
            double lastFeedrateInForce,
            int lastLineNumber)
    {
        this.layerData = layerData;
        this.lastObjectNumber = Optional.of(lastObjectNumber);
        this.lastSectionNodeInForce = sectionNode;
        this.lastToolSelectInForce = toolSelectNode;
        this.lastToolSelectOfSameNumber = lastToolSelectOfSameNumber;
        this.lastFeedrateInForce = lastFeedrateInForce;
        this.lastLineNumber = lastLineNumber;
    }

    public LayerNode getLayerData()
    {
        return layerData;
    }

    /**
     * This is the last object (aka tool) that the parser saw.
     * It may be different to the last object in the layer node,
     * as the post processing may reorder the  nodes.
     *
     * @param feedrate
     */
    public Optional<Integer> getLastObjectNumber()
    {
        return lastObjectNumber;
    }

    /**
     * This is the last object (aka tool) that the parser saw.
     * It may be different to the last object in the layer node,
     * as the post processing may reorder the  nodes.
     *
     * @param feedrate
     */
    public void setLastObjectNumber(int lastObjectNumber)
    {
        this.lastObjectNumber = Optional.of(lastObjectNumber);
    }

    /**
     * This is the last feedrate that the parser saw
     *
     * @param feedrate
     */
    public void setLastFeedrateInForce(double feedrate)
    {
        this.lastFeedrateInForce = feedrate;
    }

    /**
     * This is the last feedrate that the parser saw
     *
     * @return
     */
    public double getLastFeedrateInForce()
    {
        return lastFeedrateInForce;
    }

    public ToolSelectNode getLastToolSelectInForce()
    {
        return lastToolSelectInForce;
    }

    public ToolSelectNode getLastToolSelectOfSameNumber()
    {
        return lastToolSelectOfSameNumber;
    }

    public SectionNode getLastSectionNodeInForce()
    {
        return lastSectionNodeInForce;
    }

    /**
     * This is the last section that the parser saw.
     * It may be different to the last section in the layer node,
     * as the post processing may reorder the  nodes.
     *
     */
    public String getLastSection()
    {
        return lastSection;
    }

    /**
     * This is the last section that the parser saw.
     * It may be different to the last object in the layer node,
     * as the post processing may reorder the  nodes.
     *
     * @param lastSection
     */
    public void setLastSection(String lastSection)
    {
        this.lastSection = lastSection;
    }

    public int getLastLineNumber()
    {
        return lastLineNumber;
    }

    public void setLastLineNumber(int lastLineNumber)
    {
        this.lastLineNumber = lastLineNumber;
    }
}
