package celtech.roboxbase.postprocessor.nouveau;

import celtech.roboxbase.configuration.RoboxProfile;
import celtech.roboxbase.configuration.datafileaccessors.HeadContainer;
import celtech.roboxbase.postprocessor.CannotCloseFromPerimeterException;
import celtech.roboxbase.postprocessor.GCodeOutputWriter;
import celtech.roboxbase.postprocessor.NoPerimeterToCloseOverException;
import celtech.roboxbase.postprocessor.NotEnoughAvailableExtrusionException;
import celtech.roboxbase.postprocessor.NozzleProxy;
import celtech.roboxbase.postprocessor.PostProcessingError;
import celtech.roboxbase.postprocessor.nouveau.nodes.ExtrusionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.LayerChangeDirectiveNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.LayerNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.MCodeNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.MergeableWithToolchange;
import celtech.roboxbase.postprocessor.nouveau.nodes.NodeProcessingException;
import celtech.roboxbase.postprocessor.nouveau.nodes.NozzleValvePositionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.SectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.ToolReselectNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.ToolSelectNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.nodeFunctions.IteratorWithStartPoint;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Movement;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.NozzlePositionProvider;
import celtech.roboxbase.services.camera.CameraTriggerData;
import celtech.roboxbase.services.camera.CameraTriggerManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class UtilityMethods
{

    private final Stenographer steno = StenographerFactory.getStenographer(UtilityMethods.class.getName());
    private final PostProcessorFeatureSet ppFeatureSet;
    private final NodeManagementUtilities nodeManagementUtilities;
    private final CloseLogic closeLogic;
    private final RoboxProfile settings;
    private final CameraTriggerManager cameraTriggerManager;
    private final CameraTriggerData cameraTriggerData;

    public UtilityMethods(final PostProcessorFeatureSet ppFeatureSet,
            RoboxProfile settings,
            String headType,
            NodeManagementUtilities nodeManagementUtilities,
            CameraTriggerData cameraTriggerData)
    {
        this.ppFeatureSet = ppFeatureSet;
        this.settings = settings;
        this.nodeManagementUtilities = nodeManagementUtilities;
        this.closeLogic = new CloseLogic(settings, ppFeatureSet, headType, nodeManagementUtilities);
        this.cameraTriggerManager = new CameraTriggerManager(null);
        this.cameraTriggerData = cameraTriggerData;
        cameraTriggerManager.setTriggerData(cameraTriggerData);
    }

    protected void insertCameraTriggersAndCloses(LayerNode layerNode,
            LayerPostProcessResult lastLayerPostProcessResult,
            List<NozzleProxy> nozzleProxies)
    {
        if (ppFeatureSet.isEnabled(PostProcessorFeature.INSERT_CAMERA_CONTROL_POINTS))
        {
            IteratorWithStartPoint<GCodeEventNode> layerForwards = layerNode.treeSpanningIterator(null);
            while (layerForwards.hasNext())
            {
                GCodeEventNode layerForwardsEvent = layerForwards.next();

                if (layerForwardsEvent instanceof LayerChangeDirectiveNode)
                {
                    cameraTriggerManager.appendLayerEndTriggerCode((LayerChangeDirectiveNode) layerForwardsEvent);
                    break;
                }
            }

            Iterator<GCodeEventNode> layerBackwards = layerNode.childBackwardsIterator();

            if (ppFeatureSet.isEnabled(PostProcessorFeature.OPEN_AND_CLOSE_NOZZLES))
            {
                while (layerBackwards.hasNext())
                {
                    GCodeEventNode layerChild = layerBackwards.next();
                    if (layerChild instanceof ToolSelectNode)
                    {
                        closeAtEndOfToolSelectIfNecessary((ToolSelectNode) layerChild, nozzleProxies);
                        break;
                    }
                }
            }
        }
    }

    protected void suppressUnnecessaryToolChangesAndInsertToolchangeCloses(LayerNode layerNode,
            LayerPostProcessResult lastLayerPostProcessResult,
            List<NozzleProxy> nozzleProxies)
    {
        ToolSelectNode lastToolSelectNode = null;

        if (lastLayerPostProcessResult.getLastToolSelectInForce() != null)
        {
            lastToolSelectNode = lastLayerPostProcessResult.getLastToolSelectInForce();
        }

        // We know that tool selects come directly under a layer node...        
        Iterator<GCodeEventNode> layerIterator = layerNode.childIterator();

        List<ToolSelectNode> toolSelectNodes = new ArrayList<>();

        while (layerIterator.hasNext())
        {
            GCodeEventNode potentialToolSelectNode = layerIterator.next();

            if (potentialToolSelectNode instanceof ToolSelectNode)
            {
                toolSelectNodes.add((ToolSelectNode) potentialToolSelectNode);
            }
        }

        for (ToolSelectNode toolSelectNode : toolSelectNodes)
        {
            if (lastToolSelectNode == null)
            {
                //Our first ever tool select node...
            } else if (lastToolSelectNode.getToolNumber() == toolSelectNode.getToolNumber())
            {
                toolSelectNode.suppressNodeOutput(true);
            } else
            {
                if (ppFeatureSet.isEnabled(PostProcessorFeature.OPEN_AND_CLOSE_NOZZLES))
                {
                    closeAtEndOfToolSelectIfNecessary(lastToolSelectNode, nozzleProxies);
                }

                //Now look to see if we can consolidate the tool change with a travel
                if (lastToolSelectNode.getChildren().size() > 0)
                {
                    if (lastToolSelectNode.getChildren().get(lastToolSelectNode.getChildren().size() - 1) instanceof MergeableWithToolchange)
                    {
                        ((MergeableWithToolchange) lastToolSelectNode.getChildren().get(lastToolSelectNode.getChildren().size() - 1)).changeToolDuringMovement(toolSelectNode.getToolNumber());
                        toolSelectNode.suppressNodeOutput(true);
                    }
                }
            }

            lastToolSelectNode = toolSelectNode;
        }
    }

    protected void closeAtEndOfToolSelectIfNecessary(ToolSelectNode toolSelectNode, List<NozzleProxy> nozzleProxies)
    {
        // The tool has changed
        // Close the nozzle if it isn't already...
        //Insert a close at the end if there isn't already a close following the last extrusion
        Iterator<GCodeEventNode> nodeIterator = toolSelectNode.childBackwardsIterator();
        boolean keepLooking = true;
        boolean needToClose = false;
        GCodeEventNode eventToCloseFrom = null;

        List<SectionNode> sectionsToConsiderForClose = new ArrayList<>();

        //If we see a nozzle event BEFORE an extrusion then the nozzle has already been closed
        //If we see an extrusion BEFORE a nozzle event then we must close
        //Keep looking until we find a nozzle event, so that 
        while (nodeIterator.hasNext()
                && keepLooking)
        {
            GCodeEventNode node = nodeIterator.next();

            if (node instanceof SectionNode)
            {
                Iterator<GCodeEventNode> sectionIterator = node.childBackwardsIterator();
                while (sectionIterator.hasNext()
                        && keepLooking)
                {
                    GCodeEventNode sectionChild = sectionIterator.next();
                    if (sectionChild instanceof NozzlePositionProvider
                            && ((NozzlePositionProvider) sectionChild).getNozzlePosition().isBSet())
                    {
                        keepLooking = false;
                    } else if (sectionChild instanceof ExtrusionNode)
                    {
                        if (!sectionsToConsiderForClose.contains((SectionNode) node))
                        {
                            sectionsToConsiderForClose.add(0, (SectionNode) node);
                        }
                        if (eventToCloseFrom == null)
                        {
                            eventToCloseFrom = sectionChild;
                            needToClose = true;
                        }
                    }
                }
            } else
            {
                if (node instanceof NozzlePositionProvider
                        && ((NozzlePositionProvider) node).getNozzlePosition().isBSet())
                {
                    keepLooking = false;
                } else if (node instanceof ExtrusionNode)
                {
                    if (eventToCloseFrom == null)
                    {
                        eventToCloseFrom = node;
                        needToClose = true;
                    }
                }
            }
        }

        if (needToClose)
        {
            try
            {
                Optional<CloseResult> closeResult = closeLogic.insertProgressiveNozzleClose(eventToCloseFrom, sectionsToConsiderForClose, nozzleProxies.get(toolSelectNode.getToolNumber()));
                if (!closeResult.isPresent())
                {
                    steno.warning("Close failed - unable to record replenish");
                }
            } catch (NodeProcessingException | CannotCloseFromPerimeterException | NoPerimeterToCloseOverException | NotEnoughAvailableExtrusionException | PostProcessingError ex)
            {
                throw new RuntimeException("Error locating available extrusion during tool select normalisation", ex);
            }
        }
    }

    protected OpenResult insertOpens(LayerNode layerNode,
            OpenResult lastOpenResult,
            List<NozzleProxy> nozzleProxies,
            String headTypeCode)
    {
        Iterator<GCodeEventNode> layerIterator = layerNode.treeSpanningIterator(null);
        Movement lastMovement = null;
        boolean nozzleOpen = false;
        double lastNozzleValue = 0;
        int lastToolNumber = -1;
        double replenishExtrusionE = 0;
        double replenishExtrusionD = 0;
        int opensInThisTool = 0;

        ExtrusionNode lastNozzleClose = null;
        final float outOfUseNozzleRelief = 5;

        Map<ExtrusionNode, NozzleValvePositionNode> nozzleOpensToAdd = new HashMap<>();
        Map<ExtrusionNode, Integer> toolReselectsToAdd = new HashMap<>();

        if (lastOpenResult != null)
        {
            nozzleOpen = lastOpenResult.isNozzleOpen();
            replenishExtrusionE = lastOpenResult.getOutstandingEReplenish();
            replenishExtrusionD = lastOpenResult.getOutstandingDReplenish();
            lastToolNumber = lastOpenResult.getLastToolNumber();
            opensInThisTool = lastOpenResult.getOpensInLastTool();
            lastNozzleClose = lastOpenResult.getLastNozzleClose();
        }

        while (layerIterator.hasNext())
        {
            GCodeEventNode layerEvent = layerIterator.next();

            if (layerEvent instanceof ToolSelectNode)
            {
                if (lastToolNumber != ((ToolSelectNode) layerEvent).getToolNumber())
                {
                    if (lastNozzleClose != null)
                    {
                        if (ppFeatureSet.isEnabled(PostProcessorFeature.RETRACT_AT_TOOLCHANGE))
                        {
                            lastNozzleClose.appendCommentText("Adding retract at tool change");
                            switch (HeadContainer.getHeadByID(headTypeCode).getNozzles().get(lastToolNumber).getAssociatedExtruder())
                            {
                                case "E":
                                    lastNozzleClose.getExtrusion().setE(-outOfUseNozzleRelief);
                                    break;
                                case "D":
                                    lastNozzleClose.getExtrusion().setD(-outOfUseNozzleRelief);
                                    break;
                            }
                        }

                        lastNozzleClose = null;
                    }

                    lastToolNumber = ((ToolSelectNode) layerEvent).getToolNumber();
                    opensInThisTool = 0;
                }
            } else if (layerEvent instanceof NozzlePositionProvider
                    && ((NozzlePositionProvider) layerEvent).getNozzlePosition().isPartialOpen())
            {
                nozzleOpen = true;
                lastNozzleValue = ((NozzlePositionProvider) layerEvent).getNozzlePosition().getB();

                //As a special case for partials, insert the elided extrusion here
                double replenishEToUse = 0;
                double replenishDToUse = 0;

                switch (HeadContainer.getHeadByID(headTypeCode).getNozzles().get(lastToolNumber).getAssociatedExtruder())
                {
                    case "E":
                        replenishEToUse = replenishExtrusionE;
                        replenishExtrusionE = 0;
                        replenishDToUse = 0;
                        break;
                    case "D":
                        replenishDToUse = replenishExtrusionD;
                        replenishExtrusionD = 0;
                        replenishEToUse = 0;
                        break;
                }

                if (replenishDToUse == 0 && replenishEToUse == 0)
                {
                    String outputString = "No replenish on open in layer " + layerNode.getLayerNumber() + " before partial open " + ((NozzleValvePositionNode) layerEvent).renderForOutput();
                    if (layerEvent.getGCodeLineNumber().isPresent())
                    {
                        outputString += " on line " + layerEvent.getGCodeLineNumber().get();
                    }
                    steno.warning(outputString);
                }

                ((NozzleValvePositionNode) layerEvent).setReplenishExtrusionE(replenishEToUse);
                ((NozzleValvePositionNode) layerEvent).setReplenishExtrusionD(replenishDToUse);
            } else if (layerEvent instanceof NozzlePositionProvider
                    && (((NozzlePositionProvider) layerEvent).getNozzlePosition().isBSet()
                    && ((NozzlePositionProvider) layerEvent).getNozzlePosition().getB() == 1.0))
            {
                lastNozzleClose = null;

                nozzleOpen = true;
                lastNozzleValue = ((NozzlePositionProvider) layerEvent).getNozzlePosition().getB();
                switch (HeadContainer.getHeadByID(headTypeCode).getNozzles().get(lastToolNumber).getAssociatedExtruder())
                {
                    case "E":
                        replenishExtrusionE = 0;
                        break;
                    case "D":
                        replenishExtrusionD = 0;
                        break;
                }

                if (layerEvent instanceof ExtrusionNode)
                {
                    if (lastMovement == null)
                    {
                        lastMovement = ((ExtrusionNode) layerEvent).getMovement();
                    }
                }
            } else if (layerEvent instanceof NozzlePositionProvider
                    && ((NozzlePositionProvider) layerEvent).getNozzlePosition().isBSet()
                    && ((NozzlePositionProvider) layerEvent).getNozzlePosition().getB() < 1.0)
            {
                if (layerEvent instanceof ExtrusionNode)
                {
                    lastNozzleClose = (ExtrusionNode) layerEvent;
                }

                nozzleOpen = false;
                lastNozzleValue = ((NozzlePositionProvider) layerEvent).getNozzlePosition().getB();
                if (layerEvent instanceof ExtrusionNode)
                {
                    switch (HeadContainer.getHeadByID(headTypeCode).getNozzles().get(lastToolNumber).getAssociatedExtruder())
                    {
                        case "E":
                            replenishExtrusionE = ((ExtrusionNode) layerEvent).getElidedExtrusion();
                            break;
                        case "D":
                            replenishExtrusionD = ((ExtrusionNode) layerEvent).getElidedExtrusion();
                            break;
                    }

                    if (lastMovement == null)
                    {
                        lastMovement = ((ExtrusionNode) layerEvent).getMovement();
                    }
                }
            } else if (layerEvent instanceof ExtrusionNode
                    && !nozzleOpen)
            {
                if (lastNozzleValue > 0)
                {
                    String outputString = "Nozzle was not closed properly on layer " + layerNode.getLayerNumber() + " before extrusion " + ((ExtrusionNode) layerEvent).renderForOutput();
                    if (layerNode.getGCodeLineNumber().isPresent())
                    {
                        outputString += " on line " + layerNode.getGCodeLineNumber().get();
                    }
                    steno.warning(outputString);
                }
                NozzleValvePositionNode newNozzleValvePositionNode = new NozzleValvePositionNode();
                if (ppFeatureSet.isEnabled(PostProcessorFeature.RETRACT_AT_TOOLCHANGE)
                        && opensInThisTool == 0)
                {
                    newNozzleValvePositionNode.appendCommentText("Extra replenish - first use after toolchange");
                }
                newNozzleValvePositionNode.getNozzlePosition().setB(1);

                double replenishEToUse = 0;
                double replenishDToUse = 0;

                switch (HeadContainer.getHeadByID(headTypeCode).getNozzles().get(lastToolNumber).getAssociatedExtruder())
                {
                    case "E":
                        if (ppFeatureSet.isEnabled(PostProcessorFeature.RETRACT_AT_TOOLCHANGE)
                                && opensInThisTool == 0)
                        {
                            replenishExtrusionE += outOfUseNozzleRelief;
                        }
                        replenishEToUse = replenishExtrusionE;
                        replenishExtrusionE = 0;
                        replenishDToUse = 0;
                        break;
                    case "D":
                        if (ppFeatureSet.isEnabled(PostProcessorFeature.RETRACT_AT_TOOLCHANGE)
                                && opensInThisTool == 0)
                        {
                            replenishExtrusionD += outOfUseNozzleRelief;
                        }
                        replenishDToUse = replenishExtrusionD;
                        replenishExtrusionD = 0;
                        replenishEToUse = 0;
                        break;
                }

                if (replenishDToUse == 0 && replenishEToUse == 0)
                {
                    String outputString = "No replenish on open in layer " + layerNode.getLayerNumber() + " before extrusion " + ((ExtrusionNode) layerEvent).renderForOutput();
                    if (layerEvent.getGCodeLineNumber().isPresent())
                    {
                        outputString += " on line " + layerEvent.getGCodeLineNumber().get();
                    }
                    steno.warning(outputString);
                }

                newNozzleValvePositionNode.setReplenishExtrusionE(replenishEToUse);
                newNozzleValvePositionNode.setReplenishExtrusionD(replenishDToUse);
                nozzleOpensToAdd.put((ExtrusionNode) layerEvent, newNozzleValvePositionNode);
                nozzleOpen = true;

                opensInThisTool++;
                if (opensInThisTool > settings.getSpecificIntSetting("maxClosesBeforeNozzleReselect"))
                {
                    toolReselectsToAdd.put((ExtrusionNode) layerEvent, lastToolNumber);
                    opensInThisTool = 0;
                }

                if (lastMovement == null)
                {
                    lastMovement = ((ExtrusionNode) layerEvent).getMovement();
                }
            }
//            else if (layerEvent instanceof TravelNode)
//            {
//                if (lastMovement == null)
//                {
//                    lastMovement = ((TravelNode) layerEvent).getMovement();
//                } else
//                {
//                    Movement thisMovement = ((TravelNode) layerEvent).getMovement();
//                    Vector2D thisPoint = thisMovement.toVector2D();
//                    Vector2D lastPoint = lastMovement.toVector2D();
//
//                    if (lastPoint.distance(thisPoint) > 5 && !nozzleOpen)
//                    {
//                        steno.warning("Travel without close on layer " + layerNode.getLayerNumber() + " at " + ((TravelNode) layerEvent).renderForOutput());
//                    }
//                }
//            }
        }

        nozzleOpensToAdd.entrySet().stream().forEach((entryToUpdate) ->
        {
            if (toolReselectsToAdd.containsKey(entryToUpdate.getKey()))
            {
                int toolToReselect = toolReselectsToAdd.get(entryToUpdate.getKey());
                ToolReselectNode reselect = new ToolReselectNode();
                reselect.setToolNumber(toolToReselect);
                reselect.appendCommentText("Reselect nozzle");
                entryToUpdate.getKey().addSiblingBefore(reselect);
            }

            //Add an M109 to make sure temperature is maintained
            MCodeNode tempSustain = new MCodeNode(109);
            entryToUpdate.getKey().addSiblingBefore(tempSustain);
            entryToUpdate.getKey().addSiblingBefore(entryToUpdate.getValue());
        });

        return new OpenResult(replenishExtrusionE, replenishExtrusionD, nozzleOpen, lastToolNumber, opensInThisTool, lastNozzleClose);
    }

    protected void updateLayerToLineNumber(LayerPostProcessResult lastLayerParseResult,
            List<Integer> layerNumberToLineNumber,
            GCodeOutputWriter writer)
    {
        if (lastLayerParseResult.getLayerData()
                != null)
        {
            int layerNumber = lastLayerParseResult.getLayerData().getLayerNumber();
            if (layerNumber >= 0)
            {
                int nLines = writer.getNumberOfLinesOutput();
                for (int i = layerNumberToLineNumber.size(); i < layerNumber; ++i)
                {
                    steno.warning("Adding missing layer number " + i + " to layer to line number map");
                    layerNumberToLineNumber.add(nLines);
                }
                layerNumberToLineNumber.add(layerNumber, nLines);
            }
        }
    }

    public void recalculatePerSectionExtrusion(LayerNode layerNode)
    {
        Iterator<GCodeEventNode> childrenOfTheLayer = layerNode.childIterator();
        while (childrenOfTheLayer.hasNext())
        {
            GCodeEventNode potentialSectionNode = childrenOfTheLayer.next();

            if (potentialSectionNode instanceof SectionNode)
            {
                ((SectionNode) potentialSectionNode).recalculateExtrusion();
            }
        }
    }
}
