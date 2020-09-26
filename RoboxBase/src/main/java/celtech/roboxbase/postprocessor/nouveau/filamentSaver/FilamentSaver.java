package celtech.roboxbase.postprocessor.nouveau.filamentSaver;

import celtech.roboxbase.postprocessor.nouveau.LayerPostProcessResult;
import celtech.roboxbase.postprocessor.nouveau.nodes.ExtrusionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.LayerNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.MCodeNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.ToolSelectNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.TravelNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.nodeFunctions.IteratorWithOrigin;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class FilamentSaver
{

    private final Stenographer steno = StenographerFactory.getStenographer(FilamentSaver.class.getName());
    private final int layer0MValue = 103;
    private final int otherLayerMValue = 104;
    private final double heatUpTime_secs;
    private final double switchOffTime_secs;

    public FilamentSaver(double heatUpTime_secs, double switchOffTime_secs)
    {
        this.heatUpTime_secs = heatUpTime_secs;
        this.switchOffTime_secs = switchOffTime_secs;
    }

    private enum HeaterState
    {

        OFF, ON_FIRST_LAYER, ON
    }

    public void saveHeaters(List<LayerPostProcessResult> allLayerPostProcessResults,
             boolean nozzleHeaterOnAtStart_0,
             boolean nozzleHeaterOnAtStart_1)
    {

        ToolSelectNode[] lastToolSelects =
        {
            null, null
        };

        // Pick up the initial nozzle state
        HeaterState[] nozzleHeaterState =
        {
            (nozzleHeaterOnAtStart_0)?HeaterState.ON_FIRST_LAYER:HeaterState.OFF,
            (nozzleHeaterOnAtStart_1)?HeaterState.ON_FIRST_LAYER:HeaterState.OFF
        };

        for (int layerCounter = 0; layerCounter < allLayerPostProcessResults.size(); layerCounter++)
        {
            LayerPostProcessResult layerPostProcessResult = allLayerPostProcessResults.get(layerCounter);

            List<NodeAddStore> nodesToAdd = new ArrayList<>();

            boolean needToSwitchToSubsequentLayerTemps[] =
            {
                false, false
            };

            if (layerCounter == 1)
            {
                for (int i = 0; i < needToSwitchToSubsequentLayerTemps.length; i++)
                {
                    if (nozzleHeaterState[i] == HeaterState.ON_FIRST_LAYER)
                    {
                        needToSwitchToSubsequentLayerTemps[i] = true;
                        nozzleHeaterState[i] = HeaterState.ON;
                    }
                }
            }

            //Make sure that each heater is switched off if not required for specified time
            //Use tool selects to determine which is required...
            // We know that tool selects come directly under a layer node...        
            Iterator<GCodeEventNode> layerIterator = layerPostProcessResult.getLayerData().childIterator();

            while (layerIterator.hasNext())
            {
                GCodeEventNode node = layerIterator.next();

                if (node instanceof ToolSelectNode)
                {
                    ToolSelectNode toolSelect = (ToolSelectNode) node;

                    int thisToolNumber = toolSelect.getToolNumber();
                    int otherToolNumber = (thisToolNumber == 0) ? 1 : 0;

                    //Do we need to switch the heater on for this tool?
                    if (nozzleHeaterState[thisToolNumber] == HeaterState.OFF)
                    {
                        double targetTimeAfterStart = Math.max(0, toolSelect.getStartTimeFromStartOfPrint_secs().get() - heatUpTime_secs);

                        Optional<FoundHeatUpNode> optionalFoundNodeToHeatAfter = findNodeToHeatAfter(allLayerPostProcessResults, layerCounter, toolSelect, targetTimeAfterStart);

                        if (layerCounter == 1 && optionalFoundNodeToHeatAfter.isPresent()
                                && optionalFoundNodeToHeatAfter.get().foundInLayer == 0)
                        {
                            needToSwitchToSubsequentLayerTemps[thisToolNumber] = true;
                        }

                        if (optionalFoundNodeToHeatAfter.isPresent())
                        {
                            FoundHeatUpNode foundNodeToHeatAfter = optionalFoundNodeToHeatAfter.get();
                            int mValue = (foundNodeToHeatAfter.getFoundInLayer() == 0) ? layer0MValue : otherLayerMValue;
                            MCodeNode heaterOnNode = generateHeaterOnNode(thisToolNumber, mValue);
                            heaterOnNode.appendCommentText("Switch on in " + heatUpTime_secs + " seconds");
                            nodesToAdd.add(new NodeAddStore(foundNodeToHeatAfter.getFoundNode(), heaterOnNode, true));
                            nozzleHeaterState[thisToolNumber] = (foundNodeToHeatAfter.getFoundInLayer() == 0) ? HeaterState.ON_FIRST_LAYER : HeaterState.ON;
                        } else
                        {
                            steno.error("Failed to find place to heat nozzle");
                        }
                    }

                    //Do we need to switch the other tool heater off?
                    double finishTimeForOtherTool = (lastToolSelects[otherToolNumber] != null) ? lastToolSelects[otherToolNumber].getFinishTimeFromStartOfPrint_secs().get() : 0;
                    if ((toolSelect.getFinishTimeFromStartOfPrint_secs().get() - finishTimeForOtherTool > switchOffTime_secs)
                            && nozzleHeaterState[otherToolNumber] != HeaterState.OFF)
                    {
                        if (lastToolSelects[otherToolNumber] == null)
                        {
                            //We need to switch off the other heater at the start of the print
                            MCodeNode heaterOffNode = generateHeaterOffNode(0, otherToolNumber);
                            nodesToAdd.add(new NodeAddStore(allLayerPostProcessResults.get(0).getLayerData().getChildren().get(0), heaterOffNode, false));
                            nozzleHeaterState[otherToolNumber] = HeaterState.OFF;
                            needToSwitchToSubsequentLayerTemps[otherToolNumber] = false;
                        } else
                        {
                            //We need to switch off the other heater after the last tool select for the other tool...
                            MCodeNode heaterOffNode = generateHeaterOffNode(((LayerNode) lastToolSelects[otherToolNumber].getParent().get()).getLayerNumber(), otherToolNumber);
                            nodesToAdd.add(new NodeAddStore(lastToolSelects[otherToolNumber], heaterOffNode, true));
                        }
                        nozzleHeaterState[otherToolNumber] = HeaterState.OFF;
                    }

                    //Remember things for next iteration...
                    lastToolSelects[thisToolNumber] = toolSelect;
                }
            }

            if (needToSwitchToSubsequentLayerTemps[0] || needToSwitchToSubsequentLayerTemps[1])
            {
                MCodeNode heaterOnNode = new MCodeNode();
                heaterOnNode.setMNumber(104);
                heaterOnNode.appendCommentText("Switch heater(s) to subsequent layer temperature");

                if (needToSwitchToSubsequentLayerTemps[0])
                {
                    heaterOnNode.setSOnly(true);
                }

                if (needToSwitchToSubsequentLayerTemps[1])
                {
                    heaterOnNode.setTOnly(true);
                }
                nodesToAdd.add(new NodeAddStore(allLayerPostProcessResults.get(layerCounter).getLayerData().getChildren().getFirst(), heaterOnNode, false));
            }

            for (NodeAddStore addStore : nodesToAdd)
            {
                if (addStore.isAddAfter())
                {
                    addStore.getSiblingForAddedNode().addSiblingAfter(addStore.getNodeToAdd());
                } else
                {
                    addStore.getSiblingForAddedNode().addSiblingBefore(addStore.getNodeToAdd());
                }
            }
        }
    }

    private MCodeNode generateHeaterOffNode(int layerNumber, int heaterNumber)
    {
        MCodeNode heaterOffNode = new MCodeNode();
        heaterOffNode.setMNumber((layerNumber == 0) ? layer0MValue : otherLayerMValue);
        switch (heaterNumber)
        {
            case 0:
                //Extruder D
                heaterOffNode.setSNumber(0);
                heaterOffNode.appendCommentText("Switch heater 0 off");
                break;
            case 1:
                //Extruder E
                heaterOffNode.setTNumber(0);
                heaterOffNode.appendCommentText("Switch heater 1 off");
                break;
        }

        return heaterOffNode;
    }

    private MCodeNode generateHeaterOnNode(int heaterNumber, int mValue)
    {
        MCodeNode heaterOnNode = new MCodeNode();
        heaterOnNode.setMNumber(mValue);
        switch (heaterNumber)
        {
            case 0:
                //Extruder D
                heaterOnNode.setSOnly(true);
                heaterOnNode.appendCommentText("Switch heater 0 on");
                break;
            case 1:
                //Extruder E
                heaterOnNode.setTOnly(true);
                heaterOnNode.appendCommentText("Switch heater 1 on");
                break;
        }

        return heaterOnNode;
    }

    private Optional<FoundHeatUpNode> findNodeToHeatAfter(
            List<LayerPostProcessResult> allLayerPostProcessResults,
            final int startingLayer,
            ToolSelectNode toolSelect,
            double targetTimeAfterStart)
    {
        Optional<FoundHeatUpNode> foundNode = Optional.empty();

        int layerCounter = startingLayer;

        while (!foundNode.isPresent()
                && layerCounter >= 0)
        {
            GCodeEventNode startingNode = null;

            if (layerCounter == startingLayer)
            {
                startingNode = toolSelect;
            } else
            {
                startingNode = allLayerPostProcessResults.get(layerCounter).getLayerData().getAbsolutelyTheLastEvent();
            }

            IteratorWithOrigin<GCodeEventNode> openFinder = startingNode.treeSpanningBackwardsIterator();

            while (openFinder.hasNext())
            {
                GCodeEventNode nodeUnderConsideration = openFinder.next();

                if (nodeUnderConsideration instanceof MCodeNode
                        && ((MCodeNode) nodeUnderConsideration).getMNumber() == 104)
                {
                    steno.info("Warning - came across MCodeNode");
                }

                if ((nodeUnderConsideration instanceof ExtrusionNode || nodeUnderConsideration instanceof TravelNode)
                        && nodeUnderConsideration.getFinishTimeFromStartOfPrint_secs().isPresent()
                        && nodeUnderConsideration.getFinishTimeFromStartOfPrint_secs().get() < targetTimeAfterStart)
                {
                    foundNode = Optional.of(new FoundHeatUpNode(layerCounter, nodeUnderConsideration));
                    break;
                }
            }

            layerCounter--;
        }

        return foundNode;
    }

    private class NodeAddStore
    {

        private final GCodeEventNode siblingForAddedNode;
        private final GCodeEventNode nodeToAdd;
        private final boolean addAfter;

        public NodeAddStore(GCodeEventNode siblingForAddedNode,
                GCodeEventNode nodeToAdd,
                boolean addAfter)
        {
            this.siblingForAddedNode = siblingForAddedNode;
            this.nodeToAdd = nodeToAdd;
            this.addAfter = addAfter;
        }

        public GCodeEventNode getSiblingForAddedNode()
        {
            return siblingForAddedNode;
        }

        public GCodeEventNode getNodeToAdd()
        {
            return nodeToAdd;
        }

        public boolean isAddAfter()
        {
            return addAfter;
        }
    }

    private class FoundHeatUpNode
    {

        private final int foundInLayer;
        private final GCodeEventNode foundNode;

        public FoundHeatUpNode(int foundInLayer,
                GCodeEventNode foundNode)
        {
            this.foundInLayer = foundInLayer;
            this.foundNode = foundNode;
        }

        public int getFoundInLayer()
        {
            return foundInLayer;
        }

        public GCodeEventNode getFoundNode()
        {
            return foundNode;
        }
    }
}
