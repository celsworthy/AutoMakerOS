package celtech.roboxbase.postprocessor.nouveau.verifier;

import celtech.roboxbase.postprocessor.nouveau.LayerPostProcessResult;
import celtech.roboxbase.postprocessor.nouveau.PostProcessorFeature;
import celtech.roboxbase.postprocessor.nouveau.PostProcessorFeatureSet;
import celtech.roboxbase.postprocessor.nouveau.nodes.ExtrusionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.MCodeNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.ToolSelectNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.nodeFunctions.IteratorWithStartPoint;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.NozzlePosition;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.NozzlePositionProvider;
import celtech.roboxbase.postprocessor.nouveau.verifier.VerifierResult.ResultType;
import celtech.roboxbase.printerControl.model.Head.HeadType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Ian
 */
public class OutputVerifier
{

    private final PostProcessorFeatureSet featureSet;

    public OutputVerifier(PostProcessorFeatureSet featureSet)
    {
        this.featureSet = featureSet;
    }

    public List<VerifierResult> verifyAllLayers(final List<LayerPostProcessResult> allLayerPostProcessResults, final HeadType headType)
    {
        List<VerifierResult> output = new ArrayList<>();

        double nozzlePosition = 0;
        //The heaters should be left on after the preamble
        boolean heater0On = true;
        boolean heater1On = true;
        int currentToolNumber = -1;
        Set<ResultType> resultTypes = new HashSet<>();

        for (int layerCounter = 0; layerCounter < allLayerPostProcessResults.size(); layerCounter++)
        {
            LayerPostProcessResult layerPostProcessResult = allLayerPostProcessResults.get(layerCounter);

            IteratorWithStartPoint<GCodeEventNode> layerIterator = layerPostProcessResult.getLayerData().treeSpanningIterator(null);

            while (layerIterator.hasNext())
            {
                GCodeEventNode node = layerIterator.next();
                if (node instanceof ToolSelectNode)
                {
                    currentToolNumber = ((ToolSelectNode) node).getToolNumber();
                    resultTypes.clear();
                } else if (node instanceof NozzlePositionProvider)
                {
                    NozzlePosition nozzlePositionNode = ((NozzlePositionProvider) node).getNozzlePosition();
                    if (nozzlePositionNode.isBSet())
                    {
                        nozzlePosition = nozzlePositionNode.getB();
                    }
                } else if (node instanceof MCodeNode)
                {
                    MCodeNode mcodeNode = (MCodeNode) node;
                    if (mcodeNode.getMNumber() == 104
                            || mcodeNode.getMNumber() == 103)
                    {
                        if (mcodeNode.isSAndNumber() && mcodeNode.getSNumber() == 0)
                        {
                            heater0On = false;
                        } else if (mcodeNode.isTAndNumber() && mcodeNode.getTNumber() == 0)
                        {
                            heater1On = false;
                        } else if (mcodeNode.isSOnly())
                        {
                            heater0On = true;
                        } else if (mcodeNode.isTOnly())
                        {
                            heater1On = true;
                        }

                    }
                }

                if (node instanceof ExtrusionNode)
                {
                    boolean heaterError = false;
                    switch (currentToolNumber)
                    {
                        case 0:
                            if (!heater0On)
                            {
                                heaterError = true;
                            }
                            break;
                        case 1:
                            if (headType == HeadType.DUAL_MATERIAL_HEAD)
                            {
                                if (!heater1On)
                                {
                                    heaterError = true;
                                }
                            } else
                            {
                                if (!heater0On)
                                {
                                    heaterError = true;
                                }
                            }
                            break;
                    }

                    if (heaterError)
                    {
                        if (!resultTypes.contains(ResultType.EXTRUDE_NO_HEAT))
                        {
                            output.add(new VerifierResult(ResultType.EXTRUDE_NO_HEAT, node, layerCounter, currentToolNumber));
                            resultTypes.add(ResultType.EXTRUDE_NO_HEAT);
                        }
                    }

                    if (featureSet.isEnabled(PostProcessorFeature.OPEN_AND_CLOSE_NOZZLES)
                            && nozzlePosition < 1
                            && ((((ExtrusionNode) node).getExtrusion().isDInUse() && ((ExtrusionNode) node).getExtrusion().getD() > 0)
                            || (((ExtrusionNode) node).getExtrusion().isEInUse() && ((ExtrusionNode) node).getExtrusion().getE() > 0))
                            && !resultTypes.contains(ResultType.EXTRUDE_NOT_FULLY_OPEN))
                    {
                        output.add(new VerifierResult(ResultType.EXTRUDE_NOT_FULLY_OPEN, node, layerCounter, currentToolNumber));
                        resultTypes.add(ResultType.EXTRUDE_NOT_FULLY_OPEN);
                    }
                }
            }
        }

        return output;
    }
}
