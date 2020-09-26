package celtech.roboxbase.postprocessor.nouveau.spiralPrint;

import celtech.roboxbase.postprocessor.nouveau.LayerPostProcessResult;
import celtech.roboxbase.postprocessor.nouveau.nodes.ExtrusionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.TravelNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.nodeFunctions.IteratorWithStartPoint;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ian
 */
public class CuraSpiralPrintFixer
{

    public void fixSpiralPrint(List<LayerPostProcessResult> allLayerPostProcessResults)
    {
        boolean beginRemovingTravels = false;
        List<GCodeEventNode> travelNodesToDelete = new ArrayList<>();

        for (int layerCounter = 0; layerCounter < allLayerPostProcessResults.size(); layerCounter++)
        {
            LayerPostProcessResult layerPostProcessResult = allLayerPostProcessResults.get(layerCounter);

            IteratorWithStartPoint<GCodeEventNode> layerIterator = layerPostProcessResult.getLayerData().treeSpanningIterator(null);

            while (layerIterator.hasNext())
            {
                GCodeEventNode node = layerIterator.next();

                if (beginRemovingTravels
                        && node instanceof TravelNode)
                {
                    travelNodesToDelete.add(node);
                }
 
                if (!beginRemovingTravels
                        && node instanceof ExtrusionNode
                        && ((ExtrusionNode) node).getMovement().isZSet())
                {
                    beginRemovingTravels = true;
                }
            }
        }
        
        for (GCodeEventNode nodeToDelete : travelNodesToDelete)
        {
            nodeToDelete.removeFromParent();
        }
    }
}
