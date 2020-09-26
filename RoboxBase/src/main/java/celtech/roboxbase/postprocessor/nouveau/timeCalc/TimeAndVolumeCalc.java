package celtech.roboxbase.postprocessor.nouveau.timeCalc;

import celtech.roboxbase.postprocessor.nouveau.LayerPostProcessResult;
import celtech.roboxbase.postprocessor.nouveau.nodes.ExtrusionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.FillSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeDirectiveNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.InnerPerimeterSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.LayerChangeDirectiveNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.LayerNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.MCodeNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.NozzleValvePositionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.OuterPerimeterSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.RetractNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.SkinSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.SkirtSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.SupportSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.ToolReselectNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.ToolSelectNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.TravelNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.UnrecognisedLineNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.UnretractNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.nodeFunctions.DurationCalculationException;
import celtech.roboxbase.postprocessor.nouveau.nodes.nodeFunctions.SupportsPrintTimeCalculation;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.ExtrusionProvider;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.FeedrateProvider;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Movement;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.MovementProvider;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.NozzlePositionProvider;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Renderable;
import celtech.roboxbase.printerControl.model.Head.HeadType;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 *
 * @author Ian
 */
public class TimeAndVolumeCalc
{

    private final Stenographer steno = StenographerFactory.getStenographer(TimeAndVolumeCalc.class.getName());
    private static final double timeForInitialHoming_s = 20;
    private static final double timeForPurgeAndLevelling_s = 40;
    private static final double timeForNozzleSelect_s = 1;
    private static final double zMoveRate_mms = 4;
    private static final double nozzlePositionChange_s = 0.25;
    // Add a percentage to each movement to factor in acceleration across the whole print
    private static final double movementFudgeFactor = 1.1;

    //Data from firmware model
    private static final double TIMER_FREQ = (96.0e6 / 32.0);
    private static final double SPEED_UNITS = (TIMER_FREQ / 65536.0);    // steps/sec
    private static final double ACCELERATION_UNITS = (TIMER_FREQ * SPEED_UNITS / 4096.0); // steps/sec^2

    private static final double MAX_SPEED = (1200 * SPEED_UNITS);      // steps/sec; limit imposed by firmware performance
    private static final double MIN_SPEED = (2 * SPEED_UNITS);         // steps/sec; limit imposed by firmware
    private static final double ACCELERATION = (12 * ACCELERATION_UNITS); // steps/sec^2

    private static final double FILAMENT_CROSS_SECTIONAL_AREA = (1.75 * 1.75 * Math.PI / 4.0);

    private static final int N_AXES = 6; // order is X,Y,Z,E,D,B

    private static final double MAX_SPEEDS[] =
    {
        200.0, 200.0, 3.5, 35.0, 35.0, 6.0
    };    // mm/sec

    private static final double STEPS_PER_UNIT[] =
    {
        133.7, 133.7, 6400.0, 747.0, 747.0, 2500.0
    }; // steps/mm; NB: doesn't incorporate mm^3 to mm conv for E & D axes

    private final HeadType currentHeadType;

    private enum TimeAllocation
    {

        NOT_ALLOCATED, DEPENDS_ON_E, DEPENDS_ON_D, DEPENDS_ON_SELECTED_TOOL, FEEDRATE_INDEPENDENT
    }

    public TimeAndVolumeCalc(HeadType headType)
    {
        this.currentHeadType = headType;
    }

    //This method must:
    // Update data used for filament saver calculations:
    //              Calculate and set the finish time from start for each node
    //              Calculate the estimated duration for each tool select node
    // 
    // Stash information used for time and cost and ETC displays
    //              Total E volume used
    //              Total D volume used
    //              Per-layer and per tool feedrate independent duration 
    //              Per-layer and per tool feedrate dependent duration
    //
    public TimeAndVolumeCalcResult calculateVolumeAndTimeOLD(List<LayerPostProcessResult> allLayerPostProcessResults)
    {
        ExtruderTimeAndVolumeCalcComponent extruderEStats = new ExtruderTimeAndVolumeCalcComponent();
        ExtruderTimeAndVolumeCalcComponent extruderDStats = new ExtruderTimeAndVolumeCalcComponent();
        TimeCalcComponent feedrateIndependentDuration = new TimeCalcComponent();

        SupportsPrintTimeCalculation lastNodeSupportingPrintTimeCalcs = null;
        LayerNode lastLayerNode = null;
        double timeFromStart = 0;
        double timeInThisTool = 0;

        ToolSelectNode lastToolSelectNode = null;
        double lastFeedrateInForce = 0;

        for (int layerCounter = 0;
                layerCounter < allLayerPostProcessResults.size();
                layerCounter++)
        {

            //Make sure we at least have a zero entry for each layer
            extruderEStats.getDuration().incrementDuration(layerCounter, 0);
            extruderDStats.getDuration().incrementDuration(layerCounter, 0);
            feedrateIndependentDuration.incrementDuration(layerCounter, 0);

            if (layerCounter == 0)
            {
                //Insert some data for the pre-print preamble
                feedrateIndependentDuration.incrementDuration(0, timeForInitialHoming_s + timeForPurgeAndLevelling_s);
            }

            LayerPostProcessResult layerPostProcessResult = allLayerPostProcessResults.get(layerCounter);

            LayerNode layerNode = layerPostProcessResult.getLayerData();
            Iterator<GCodeEventNode> layerIterator = layerNode.treeSpanningIterator(null);

            while (layerIterator.hasNext())
            {
                GCodeEventNode node = layerIterator.next();
                TimeAllocation chosenAllocation = TimeAllocation.NOT_ALLOCATED;

                //Total up the extruded volume
                if (node instanceof ExtrusionProvider)
                {
                    ExtrusionProvider extrusionProvider = (ExtrusionProvider) node;
                    extruderEStats.incrementVolume(extrusionProvider.getExtrusion().getE());
                    extruderDStats.incrementVolume(extrusionProvider.getExtrusion().getD());
                }

                //If the tool is selected (or reselected) stash the current elapsed time in tool
                if (node instanceof ToolSelectNode)
                {
                    if (lastToolSelectNode != null)
                    {
                        lastToolSelectNode.setEstimatedDuration(timeInThisTool);
                        lastToolSelectNode.setFinishTimeFromStartOfPrint_secs(timeFromStart);
                    }

                    lastToolSelectNode = (ToolSelectNode) node;
                    timeInThisTool = 0;
                }

                double eventDuration = -1;

                if (node instanceof SupportsPrintTimeCalculation)
                {
                    SupportsPrintTimeCalculation timeCalculationNode = (SupportsPrintTimeCalculation) node;

                    if (lastNodeSupportingPrintTimeCalcs != null)
                    {
                        try
                        {
                            eventDuration = lastNodeSupportingPrintTimeCalcs.timeToReach((MovementProvider) node) * movementFudgeFactor;

                            if (node instanceof NozzlePositionProvider)
                            {
                                chosenAllocation = TimeAllocation.DEPENDS_ON_SELECTED_TOOL;
                            } else if (node instanceof ExtrusionProvider)
                            {
                                if (((ExtrusionProvider) node).getExtrusion().isEInUse())
                                {
                                    chosenAllocation = TimeAllocation.DEPENDS_ON_E;
                                } else if (((ExtrusionProvider) node).getExtrusion().isDInUse())
                                {
                                    chosenAllocation = TimeAllocation.DEPENDS_ON_D;
                                }
                            } else
                            {
                                chosenAllocation = TimeAllocation.FEEDRATE_INDEPENDENT;
                            }
                        } catch (DurationCalculationException ex)
                        {
                            if (ex.getFromNode() instanceof Renderable
                                    && ex.getToNode() instanceof Renderable)
                            {
                                steno.error("Unable to calculate duration correctly for nodes source:"
                                        + ((Renderable) ex.getFromNode()).renderForOutput()
                                        + " destination:"
                                        + ((Renderable) ex.getToNode()).renderForOutput());
                            } else
                            {
                                steno.error("Unable to calculate duration correctly for nodes source:"
                                        + ex.getFromNode().getMovement().renderForOutput()
                                        + " destination:"
                                        + ex.getToNode().getMovement().renderForOutput());
                            }

                            throw new RuntimeException("Unable to calculate duration correctly on layer "
                                    + layerNode.getLayerNumber(), ex);
                        }
                    }
                    lastNodeSupportingPrintTimeCalcs = timeCalculationNode;

                    if (((FeedrateProvider) lastNodeSupportingPrintTimeCalcs).getFeedrate().getFeedRate_mmPerMin() < 0)
                    {
                        if (lastFeedrateInForce > 0)
                        {
                            ((FeedrateProvider) lastNodeSupportingPrintTimeCalcs).getFeedrate().setFeedRate_mmPerMin(lastFeedrateInForce);
                        } else
                        {
                            steno.warning("Couldn't set feedrate during time calculation");
                        }
                    }
                    lastFeedrateInForce = ((FeedrateProvider) lastNodeSupportingPrintTimeCalcs).getFeedrate().getFeedRate_mmPerMin();
                } else if (node instanceof ToolSelectNode)
                {
                    eventDuration = timeForNozzleSelect_s;
                    chosenAllocation = TimeAllocation.FEEDRATE_INDEPENDENT;
                } else if (node instanceof LayerChangeDirectiveNode)
                {
                    LayerChangeDirectiveNode lNode = (LayerChangeDirectiveNode) node;
                    double heightChange = lNode.getMovement().getZ() - layerNode.getLayerHeight_mm();
                    if (heightChange > 0)
                    {
                        eventDuration = heightChange / zMoveRate_mms;
                        chosenAllocation = TimeAllocation.FEEDRATE_INDEPENDENT;
                    }
                } else if (node instanceof NozzleValvePositionNode)
                {
                    eventDuration = nozzlePositionChange_s;
                    chosenAllocation = TimeAllocation.DEPENDS_ON_SELECTED_TOOL;
                } else if (node instanceof GCodeDirectiveNode
                        && ((GCodeDirectiveNode) node).getGValue() == 4)
                {
                    GCodeDirectiveNode directive = (GCodeDirectiveNode) node;
                    if (directive.getGValue() == 4)
                    {
                        //Found a dwell
                        Optional<Integer> sValue = directive.getSValue();
                        if (sValue.isPresent())
                        {
                            //Seconds
                            eventDuration = sValue.get();
                            chosenAllocation = TimeAllocation.FEEDRATE_INDEPENDENT;
                        }
                        Optional<Integer> pValue = directive.getPValue();
                        if (pValue.isPresent())
                        {
                            //Microseconds
                            eventDuration = pValue.get() / 1000.0;
                            chosenAllocation = TimeAllocation.FEEDRATE_INDEPENDENT;
                        }
                    }
                } else if (!(node instanceof FillSectionNode)
                        && !(node instanceof InnerPerimeterSectionNode)
                        && !(node instanceof SkinSectionNode)
                        && !(node instanceof OuterPerimeterSectionNode)
                        && !(node instanceof SkirtSectionNode)
                        && !(node instanceof MCodeNode))
                {
                    steno.trace("Not possible to calculate time for: " + node.getClass().getName() + " : " + node.toString());
                }

                //Store the per-layer duration data
                if (eventDuration > 0)
                {
                    switch (chosenAllocation)
                    {
                        case DEPENDS_ON_E:
                            extruderEStats.getDuration().incrementDuration(layerCounter, eventDuration);
                            break;
                        case DEPENDS_ON_D:
                            extruderDStats.getDuration().incrementDuration(layerCounter, eventDuration);
                            break;
                        case DEPENDS_ON_SELECTED_TOOL:
                            int currentToolInUse = (lastToolSelectNode != null) ? lastToolSelectNode.getToolNumber() : 0;
                            switch (currentToolInUse)
                            {
                                case 0:
                                    if (currentHeadType == HeadType.DUAL_MATERIAL_HEAD)
                                    {
                                        extruderDStats.getDuration().incrementDuration(layerCounter, eventDuration);
                                    } else
                                    {
                                        extruderEStats.getDuration().incrementDuration(layerCounter, eventDuration);
                                    }
                                    break;
                                case 1:
                                    extruderEStats.getDuration().incrementDuration(layerCounter, eventDuration);
                                    break;
                            }
                            break;
                        case FEEDRATE_INDEPENDENT:
                            feedrateIndependentDuration.incrementDuration(layerCounter, eventDuration);
                            break;
                        default:
                            steno.warning("Event duration was not allocated");
                            break;
                    }

                    //Store the finish time for this node
                    timeFromStart += eventDuration;
                    timeInThisTool += eventDuration;
                }

                if (timeFromStart > 0)
                {
                    node.setFinishTimeFromStartOfPrint_secs(timeFromStart);
                }
            }

            if (lastLayerNode != null)
            {
                lastLayerNode.setFinishTimeFromStartOfPrint_secs(timeFromStart);
            }
            lastLayerNode = layerNode;
        }

        return new TimeAndVolumeCalcResult(extruderEStats, extruderDStats, feedrateIndependentDuration);
    }

    //This method must:
    // Update data used for filament saver calculations:
    //              Calculate and set the finish time from start for each node
    //              Calculate the estimated duration for each tool select node
    // 
    // Stash information used for time and cost and ETC displays
    //              Total E volume used
    //              Total D volume used
    //              Per-layer and per tool feedrate independent duration 
    //              Per-layer and per tool feedrate dependent duration
    //
    public TimeAndVolumeCalcResult calculateVolumeAndTime(List<LayerPostProcessResult> allLayerPostProcessResults)
    {
        ExtruderTimeAndVolumeCalcComponent extruderEStats = new ExtruderTimeAndVolumeCalcComponent();
        ExtruderTimeAndVolumeCalcComponent extruderDStats = new ExtruderTimeAndVolumeCalcComponent();
        TimeCalcComponent feedrateIndependentDuration = new TimeCalcComponent();

        GCodeEventNode lastNodeContainingMovement = null;
        LayerNode lastLayerNode = null;
        double timeFromStart = 0;
        double timeInThisTool = 0;

        ToolSelectNode lastToolSelectNode = null;
        //Default the feedrate to 200mm/s
        double feedrateInForce_mm_sec = 200;
        double lastB = 0;
        double lastZ = 0;
        TMove lastTMove = null;

        for (int layerCounter = 0;
                layerCounter < allLayerPostProcessResults.size();
                layerCounter++)
        {

            //Make sure we at least have a zero entry for each layer
            extruderEStats.getDuration().incrementDuration(layerCounter, 0);
            extruderDStats.getDuration().incrementDuration(layerCounter, 0);
            feedrateIndependentDuration.incrementDuration(layerCounter, 0);

            if (layerCounter == 0)
            {
                //Insert some data for the pre-print preamble
                feedrateIndependentDuration.incrementDuration(0, timeForInitialHoming_s + timeForPurgeAndLevelling_s);
}

            LayerPostProcessResult layerPostProcessResult = allLayerPostProcessResults.get(layerCounter);

            LayerNode layerNode = layerPostProcessResult.getLayerData();
            Iterator<GCodeEventNode> layerIterator = layerNode.treeSpanningIterator(null);

            while (layerIterator.hasNext())
            {
                GCodeEventNode node = layerIterator.next();
                TimeAllocation chosenAllocation = TimeAllocation.NOT_ALLOCATED;

                //Total up the extruded volume
                if (node instanceof ExtrusionProvider)
                {
                    ExtrusionProvider extrusionProvider = (ExtrusionProvider) node;
                    extruderEStats.incrementVolume(extrusionProvider.getExtrusion().getE());
                    extruderDStats.incrementVolume(extrusionProvider.getExtrusion().getD());
                }

                //If the tool is selected (or reselected) stash the current elapsed time in tool
                if (node instanceof ToolSelectNode)
                {
                    ((ToolSelectNode)node).setStartTimeFromStartOfPrint_secs(timeFromStart);
                    
                    if (lastToolSelectNode != null)
                    {
                        lastToolSelectNode.setEstimatedDuration(timeInThisTool);
                        lastToolSelectNode.setFinishTimeFromStartOfPrint_secs(timeFromStart);
                    }

                    lastToolSelectNode = (ToolSelectNode) node;
                    timeInThisTool = 0;
                }

                double eventDuration = -1;

                if (node instanceof FeedrateProvider)
                {
                    if (((FeedrateProvider) node).getFeedrate().isFeedrateSet())
                    {
                        feedrateInForce_mm_sec = ((FeedrateProvider) node).getFeedrate().getFeedRate_mmPerSec();
                    }
                }

                if (node instanceof MovementProvider)
                {
                    Movement sourceMovement = null;
                    feedrateInForce_mm_sec = ((FeedrateProvider) node).getFeedrate().getFeedRate_mmPerSec();

                    if (lastNodeContainingMovement != null)
                    {
                        sourceMovement = ((MovementProvider) lastNodeContainingMovement).getMovement();
                    } else
                    {
                        TravelNode tNode = new TravelNode();
                        tNode.getMovement().setX(0);
                        tNode.getMovement().setY(0);
                        tNode.getMovement().setZ(0);
                        sourceMovement = tNode.getMovement();
                    }

//                            eventDuration = lastNodeSupportingPrintTimeCalcs.timeToReach((MovementProvider) node) * movementFudgeFactor;
                    // moves[0] is X relative move, in mm
                    // moves[1] is Y relative move, in mm
                    // moves[2] is Z relative move, in mm
                    // moves[3] is E relative move, in mm^3
                    // moves[4] is D relative move, in mm^3
                    // moves[5] is B relative move
                    double moves[] = new double[6];

                    chosenAllocation = TimeAllocation.FEEDRATE_INDEPENDENT;

                    Movement destinationMovement = ((MovementProvider) node).getMovement();

                    if (destinationMovement.isXSet() && destinationMovement.isYSet())
                    {
                        Vector2D resultantXY = destinationMovement.toVector2D().subtract(sourceMovement.toVector2D());
                        moves[0] = resultantXY.getX();
                        moves[1] = resultantXY.getY();
                    }

                    if (destinationMovement.isZSet())
                    {
                        double relZ;
                        if (sourceMovement.isZSet())
                        {
                            relZ = destinationMovement.getZ() - sourceMovement.getZ();
                        } else
                        {
                            relZ = destinationMovement.getZ() - lastZ;
                        }

                        lastZ = destinationMovement.getZ();
                        moves[2] = relZ;
                    }

                    if (node instanceof ExtrusionNode)
                    {
                        moves[3] = ((ExtrusionNode) node).getExtrusion().getE();
                        moves[4] = ((ExtrusionNode) node).getExtrusion().getD();
                    }

                    if (node instanceof NozzlePositionProvider)
                    {
                        double newVal = ((NozzlePositionProvider) node).getNozzlePosition().getB();
                        double relB = newVal - lastB;
                        lastB = newVal;
                        moves[5] = relB;
                    }

                    TMoveResult result = calculate_run_time(lastTMove, feedrateInForce_mm_sec, moves);
                    lastTMove = result.tMove;
                    eventDuration = result.duration;

                    if (node instanceof NozzlePositionProvider)
                    {
                        chosenAllocation = TimeAllocation.DEPENDS_ON_SELECTED_TOOL;
                    } else if (node instanceof ExtrusionProvider)
                    {
                        if (((ExtrusionProvider) node).getExtrusion().isEInUse())
                        {
                            chosenAllocation = TimeAllocation.DEPENDS_ON_E;
                        } else if (((ExtrusionProvider) node).getExtrusion().isDInUse())
                        {
                            chosenAllocation = TimeAllocation.DEPENDS_ON_D;
                        }
                    } else
                    {
                        chosenAllocation = TimeAllocation.FEEDRATE_INDEPENDENT;
                    }

//                        catch (DurationCalculationException ex)
//                        {
//                            if (ex.getFromNode() instanceof Renderable
//                                    && ex.getToNode() instanceof Renderable)
//                            {
//                                steno.error("Unable to calculate duration correctly for nodes source:"
//                                        + ((Renderable) ex.getFromNode()).renderForOutput()
//                                        + " destination:"
//                                        + ((Renderable) ex.getToNode()).renderForOutput());
//                            } else
//                            {
//                                steno.error("Unable to calculate duration correctly for nodes source:"
//                                        + ex.getFromNode().getMovement().renderForOutput()
//                                        + " destination:"
//                                        + ex.getToNode().getMovement().renderForOutput());
//                            }
//
//                            throw new RuntimeException("Unable to calculate duration correctly on layer "
//                                    + layerNode.getLayerNumber(), ex);
//                        }
                    lastNodeContainingMovement = node;
                } else if (node instanceof ToolSelectNode)
                {
                    eventDuration = timeForNozzleSelect_s;
                    chosenAllocation = TimeAllocation.FEEDRATE_INDEPENDENT;
                } else if (node instanceof LayerChangeDirectiveNode)
                {
                    LayerChangeDirectiveNode lNode = (LayerChangeDirectiveNode) node;

                    if (lastNodeContainingMovement != null)
                    {
                        // moves[0] is X relative move, in mm
                        // moves[1] is Y relative move, in mm
                        // moves[2] is Z relative move, in mm
                        // moves[3] is E relative move, in mm^3
                        // moves[4] is D relative move, in mm^3
                        // moves[5] is B relative move
                        double moves[] = new double[6];

                        chosenAllocation = TimeAllocation.FEEDRATE_INDEPENDENT;

                        Movement sourceMovement = ((MovementProvider) lastNodeContainingMovement).getMovement();
                        Movement destinationMovement = ((MovementProvider) lNode).getMovement();

                        if (destinationMovement.isXSet() && destinationMovement.isYSet())
                        {
                            Vector2D resultantXY = destinationMovement.toVector2D().subtract(sourceMovement.toVector2D());
                            moves[0] = resultantXY.getX();
                            moves[1] = resultantXY.getY();
                        }

                        if (destinationMovement.isZSet())
                        {
                            double relZ;
                            if (sourceMovement.isZSet())
                            {
                                relZ = destinationMovement.getZ() - sourceMovement.getZ();
                            } else
                            {
                                relZ = destinationMovement.getZ() - lastZ;
                            }

                            lastZ = destinationMovement.getZ();
                            moves[2] = relZ;
                        }

                        if (node instanceof NozzlePositionProvider)
                        {
                            double newVal = ((NozzlePositionProvider) node).getNozzlePosition().getB();
                            double relB = newVal - lastB;
                            lastB = newVal;
                            moves[5] = relB;
                        }

                        TMoveResult result = calculate_run_time(lastTMove, feedrateInForce_mm_sec, moves);
                        lastTMove = result.tMove;
                        eventDuration = result.duration;
                    }
                } else if (node instanceof NozzleValvePositionNode)
                {
                    // This isn't a travel or an extrusion so probably B on its own
                    // moves[0] is X relative move, in mm
                    // moves[1] is Y relative move, in mm
                    // moves[2] is Z relative move, in mm
                    // moves[3] is E relative move, in mm^3
                    // moves[4] is D relative move, in mm^3
                    // moves[5] is B relative move
                    double moves[] = new double[6];
                    double newVal = ((NozzlePositionProvider) node).getNozzlePosition().getB();
                    double relB = newVal - lastB;
                    lastB = newVal;
                    moves[5] = relB;

                    TMoveResult result = calculate_run_time(lastTMove, feedrateInForce_mm_sec, moves);
                    lastTMove = result.tMove;
                    eventDuration = result.duration;

                    chosenAllocation = TimeAllocation.DEPENDS_ON_SELECTED_TOOL;
                } else if (node instanceof GCodeDirectiveNode
                        && ((GCodeDirectiveNode) node).getGValue() == 4)
                {
                    GCodeDirectiveNode directive = (GCodeDirectiveNode) node;
                    if (directive.getGValue() == 4)
                    {
                        //Found a dwell
                        Optional<Integer> sValue = directive.getSValue();
                        if (sValue.isPresent())
                        {
                            //Seconds
                            eventDuration = sValue.get();
                            chosenAllocation = TimeAllocation.FEEDRATE_INDEPENDENT;
                        }
                        Optional<Integer> pValue = directive.getPValue();
                        if (pValue.isPresent())
                        {
                            //Microseconds
                            eventDuration = pValue.get() / 1000.0;
                            chosenAllocation = TimeAllocation.FEEDRATE_INDEPENDENT;
                        }
                    }
                } else if (!(node instanceof FillSectionNode)
                        && !(node instanceof InnerPerimeterSectionNode)
                        && !(node instanceof SkinSectionNode)
                        && !(node instanceof OuterPerimeterSectionNode)
                        && !(node instanceof SkirtSectionNode)
                        && !(node instanceof SupportSectionNode)
                        && !(node instanceof UnrecognisedLineNode)
                        && !(node instanceof MCodeNode)
                        && !(node instanceof RetractNode)
                        && !(node instanceof UnretractNode)
                        && !(node instanceof ToolReselectNode))
                {
                    steno.trace("Not possible to calculate time for: " + node.getClass().getName() + " : " + node.toString());
                }

                //Store the per-layer duration data
                if (eventDuration > 0)
                {
                    switch (chosenAllocation)
                    {
                        case DEPENDS_ON_E:
                            extruderEStats.getDuration().incrementDuration(layerCounter, eventDuration);
                            break;
                        case DEPENDS_ON_D:
                            extruderDStats.getDuration().incrementDuration(layerCounter, eventDuration);
                            break;
                        case DEPENDS_ON_SELECTED_TOOL:
                            int currentToolInUse = (lastToolSelectNode != null) ? lastToolSelectNode.getToolNumber() : 0;
                            switch (currentToolInUse)
                            {
                                case 0:
                                    if (currentHeadType == HeadType.DUAL_MATERIAL_HEAD)
                                    {
                                        extruderDStats.getDuration().incrementDuration(layerCounter, eventDuration);
                                    } else
                                    {
                                        extruderEStats.getDuration().incrementDuration(layerCounter, eventDuration);
                                    }
                                    break;
                                case 1:
                                    extruderEStats.getDuration().incrementDuration(layerCounter, eventDuration);
                                    break;
                            }
                            break;
                        case FEEDRATE_INDEPENDENT:
                            feedrateIndependentDuration.incrementDuration(layerCounter, eventDuration);
                            break;
                        default:
                            steno.warning("Event duration was not allocated");
                            break;
                    }

                    //Store the finish time for this node
                    timeFromStart += eventDuration;
                    timeInThisTool += eventDuration;
                }

                if (timeFromStart > 0)
                {
                    node.setFinishTimeFromStartOfPrint_secs(timeFromStart);
                }
            }

            if (lastLayerNode != null)
            {
                lastLayerNode.setFinishTimeFromStartOfPrint_secs(timeFromStart);
            }
            lastLayerNode = layerNode;
        }

        return new TimeAndVolumeCalcResult(extruderEStats, extruderDStats, feedrateIndependentDuration);
    }

    class TMoveResult
    {

        public TMoveResult(TMove tMove, double duration)
        {
            this.tMove = tMove;
            this.duration = duration;
        }

        private TMove tMove;
        private double duration;
    };

    class TMove
    {

        double inc[] = new double[N_AXES];
        double steps;
        double target_speed;
        double end_speed;
    };

    static double calculate_end_speed(TMove thisMove, TMove nextMove)
    {
        double v, end_speed;
        int i;

        // calculate allowable speed entering next block = sqrt((2 * length * acceleration) + (end_speed * end_speed))
        v = nextMove.end_speed * nextMove.end_speed;
        v += 2.0 * nextMove.steps * ACCELERATION;
        end_speed = Math.sqrt(v);

        // calculate allowable speed at junction
        v = 0.0;

        for (i = 0; i < N_AXES; i++)
        {
            v = Math.max(v, Math.abs(thisMove.inc[i] - nextMove.inc[i]
            ));
        } /* endfor */

        if (v > 0.0)
        { // if v is zero, there's no implied speed limit (so we avoid divide by zero)
            end_speed = Math.min(end_speed, Math.sqrt(2.0 * ACCELERATION) / v);
        } /* endif */

        end_speed = Math.min(end_speed, thisMove.target_speed
        );
        end_speed = Math.min(end_speed, nextMove.target_speed);
        end_speed = Math.min(end_speed, MAX_SPEED);
        end_speed = Math.max(end_speed, MIN_SPEED);
        return (end_speed);
    }

    private double calculate_duration(TMove m)
    {
        double start_speed = 0.0;
        double t, peak_speed, accel_dist, decel_dist, peak_dist;

        peak_speed = Math.max(m.target_speed, Math.max(start_speed, m.end_speed));
        accel_dist = (peak_speed - start_speed) * (peak_speed + start_speed) / (2.0 * ACCELERATION);
        decel_dist = (peak_speed - m.end_speed) * (peak_speed + m.end_speed) / (2.0 * ACCELERATION);
        peak_dist = m.steps - (accel_dist + decel_dist);

        if (peak_dist < 0.0)
        { // if speed never gets to peak
            peak_speed = Math.sqrt((ACCELERATION * m.steps) + (((start_speed * start_speed) + (m.end_speed * m.end_speed)) / 2.0));
            accel_dist = (peak_speed - start_speed) * (peak_speed + start_speed) / (2.0 * ACCELERATION);
            decel_dist = (peak_speed - m.end_speed) * (peak_speed + m.end_speed) / (2.0 * ACCELERATION);
            peak_dist = 0.0;
        } /* endif */

        t = accel_dist / (0.5 * (start_speed + peak_speed));
        t += peak_dist / peak_speed;
        t += decel_dist / (0.5 * (peak_speed + m.end_speed));

        start_speed = m.end_speed;
        return t;
    }

// Adds a move to buffer.  When flush=1, calculates run time of buffer contents then discards buffer.
// feed_rate is in mm/sec.  For rapid moves, just make feed_rate large, eg. 1000.
// moves[0] is X relative move, in mm
// moves[1] is Y relative move, in mm
// moves[2] is Z relative move, in mm
// moves[3] is E relative move, in mm^3
// moves[4] is D relative move, in mm^3
// moves[5] is B relative move
    private TMoveResult calculate_run_time(TMove lastTMove, double feed_rate, double moves[])
    {
        TMove newMove = null;
        double time, min_dur, steps, length, v;
        int i;

        time = 0.0;
//  buffer = realloc((void *)buffer, (buffer_n + 1) * sizeof(t_move));

        // convert E, D moves from mm^3 to mm
        moves[3] /= FILAMENT_CROSS_SECTIONAL_AREA;
        moves[4] /= FILAMENT_CROSS_SECTIONAL_AREA;

        min_dur = steps = 0.0;

        for (i = 0; i < N_AXES; i++)
        {
            min_dur = Math.max(min_dur, Math.abs(moves[i]) / Math.min(feed_rate, MAX_SPEEDS[i]));
            steps = Math.max(steps, Math.abs(moves[i]) * STEPS_PER_UNIT[i]);
        } /* endfor */

        if (steps > 0.0)
        {
            newMove = new TMove();

            for (i = 0; i < N_AXES; i++)
            {
                newMove.inc[i] = moves[i] * STEPS_PER_UNIT[i] / steps;
//      buffer[buffer_n].inc[i] = moves[i] * STEPS_PER_UNIT[i] / steps;
            } /* endfor */

            v = 0.0;

            for (i = 0; i <= 2; i++)
            {
                v += moves[i] * moves[i];
            } /* endfor */

            v = Math.sqrt(v); // 3-dimensional distance of X,Y,Z
            min_dur = Math.max(min_dur, v / feed_rate);
            newMove.target_speed = Math.max(MIN_SPEED, Math.min(MAX_SPEED, steps / min_dur));
            newMove.end_speed = 0.0; // gets increased later
            newMove.steps = steps;
        } /* endif */

        if (newMove != null)
        {
            // do "planning", ie. set end_speed of each move depending on what comes next
            if (lastTMove != null)
            {
                newMove.end_speed = calculate_end_speed(lastTMove, newMove);
            }

            time = calculate_duration(newMove);

            //for (i = 0; i < buffer_n; i ++) {
            //  printf("%d: X:%.3lf Y:%.3lf Z:%.3lf E:%.3lf D:%.3lf B:%.3lf s:%.3lf t:%.3lf e:%.3lf\n", i,
            //         buffer[i].inc[0], buffer[i].inc[1], buffer[i].inc[2], buffer[i].inc[3], buffer[i].inc[4], buffer[i].inc[5],
            //         buffer[i].steps, buffer[i].target_speed, buffer[i].end_speed);
            //} /* endfor */
//    free((void *)buffer);
//    buffer = null;
        } /* endif */

        return new TMoveResult(newMove, time);
    }
}
