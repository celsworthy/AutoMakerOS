package celtech.roboxbase.postprocessor.nouveau;

import celtech.roboxbase.configuration.RoboxProfile;
import celtech.roboxbase.configuration.slicer.NozzleParameters;
import celtech.roboxbase.postprocessor.CannotCloseFromPerimeterException;
import celtech.roboxbase.postprocessor.NoPerimeterToCloseOverException;
import celtech.roboxbase.postprocessor.NotEnoughAvailableExtrusionException;
import celtech.roboxbase.postprocessor.NozzleProxy;
import celtech.roboxbase.postprocessor.PostProcessingError;
import celtech.roboxbase.postprocessor.nouveau.NodeManagementUtilities.AvailableExtrusion;
import celtech.roboxbase.postprocessor.nouveau.nodes.ExtrusionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.InnerPerimeterSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.LayerNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.NodeProcessingException;
import celtech.roboxbase.postprocessor.nouveau.nodes.NozzleValvePositionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.OuterPerimeterSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.RetractNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.SectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.ToolSelectNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.TravelNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.nodeFunctions.IteratorWithOrigin;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Extrusion;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.ExtrusionProvider;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Movement;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.MovementProvider;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.NozzlePositionProvider;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Renderable;
import celtech.roboxbase.utils.Math.MathUtils;
import celtech.roboxbase.utils.SystemUtils;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
public class CloseLogic
{

    private final Stenographer steno = StenographerFactory.getStenographer(CloseLogic.class.getName());
    private final PostProcessorFeatureSet featureSet;

    private final CloseUtilities closeUtilities;
    private final NodeManagementUtilities nodeManagementUtilities;
    public int layerNumber =-99;

    public CloseLogic(RoboxProfile settings,
            PostProcessorFeatureSet featureSet, String headType,
            NodeManagementUtilities nodeManagementUtilities)
    {
        this.featureSet = featureSet;

        closeUtilities = new CloseUtilities(settings, headType);
        this.nodeManagementUtilities = nodeManagementUtilities;
    }

    protected InScopeEvents extractAvailableMovements(GCodeEventNode startingNode,
            List<SectionNode> sectionsToConsider,
            boolean includeInternalPerimeters,
            boolean includeExternalPerimeters,
            boolean stopAtNozzlePositionEvent,
            boolean stopWhenPerimeterHit)
    {
        List<SectionNode> availableSectionsToCloseOver = new ArrayList<>();

//        List<SectionNode> tempSectionHolder = new ArrayList<>();
//        for (SectionNode section : sectionsToConsider)
//        {
//            if ((!(section instanceof OuterPerimeterSectionNode)
//                    && !(section instanceof InnerPerimeterSectionNode))
//                    || ((section instanceof OuterPerimeterSectionNode) && includeExternalPerimeters)
//                    || ((section instanceof InnerPerimeterSectionNode) && includeInternalPerimeters))
//            {
//                tempSectionHolder.add(section);
//            }
//        }
        availableSectionsToCloseOver = sectionsToConsider;

        int sectionDelta = -1;
        int sectionCounter = -1;
        boolean haveConsumedStartNode = true;

        //Work out which section to start in
        if (startingNode.getParent().isPresent()
                && startingNode.getParent().get() instanceof SectionNode)
        {
            for (int sectionSearch = 0; sectionSearch < availableSectionsToCloseOver.size(); sectionSearch++)
            {
                if (availableSectionsToCloseOver.get(sectionSearch) == startingNode.getParent().get())
                {
                    sectionCounter = sectionSearch;
                    haveConsumedStartNode = false;
                    break;
                }
            }
        }

        if (sectionCounter < 0)
        {
            sectionCounter = availableSectionsToCloseOver.size() - 1;
        }

        List<GCodeEventNode> movementNodes = new ArrayList<>();

        boolean keepLooking = true;
        double availableExtrusion = 0;

        while (sectionCounter >= 0
                && sectionCounter < availableSectionsToCloseOver.size()
                && keepLooking)
        {
            SectionNode sectionNode = availableSectionsToCloseOver.get(sectionCounter);

            if ((sectionNode instanceof InnerPerimeterSectionNode
                    || sectionNode instanceof OuterPerimeterSectionNode) && stopWhenPerimeterHit)
            {
                break;
            }

            sectionCounter += sectionDelta;

            Iterator<GCodeEventNode> sectionIterator = null;
            if (!haveConsumedStartNode)
            {
                sectionIterator = startingNode.meAndSiblingsBackwardsIterator();
                haveConsumedStartNode = true;
            } else
            {
                sectionIterator = sectionNode.childBackwardsIterator();
            }

            while (sectionIterator.hasNext())
            {
                GCodeEventNode node = sectionIterator.next();
                //Changed to allow closes to be carried out on any part of previous section
                if (node instanceof NozzleValvePositionNode && stopAtNozzlePositionEvent)
                {
//                    movementNodes.add(node);
                    keepLooking = false;
                    break;
                } else if (node instanceof NozzlePositionProvider
                        && ((NozzlePositionProvider) node).getNozzlePosition().isBSet()
                        && stopAtNozzlePositionEvent)
                {
                    keepLooking = false;
                    break;
                } else if (node instanceof MovementProvider)
                {
                    if ((!(sectionNode instanceof OuterPerimeterSectionNode)
                            && !(sectionNode instanceof InnerPerimeterSectionNode))
                            || ((sectionNode instanceof OuterPerimeterSectionNode) && includeExternalPerimeters)
                            || ((sectionNode instanceof InnerPerimeterSectionNode) && includeInternalPerimeters))
                    {
                        movementNodes.add(node);
                        if (node instanceof ExtrusionNode)
                        {
                            availableExtrusion += ((ExtrusionNode) node).getExtrusion().getE();
                        }
                    }
                }
            }
        }

        //Now trim off the first travel nodes until we reach an extrusion.
        // This prevents odd inward close decisions if the slicer puts in strange moves after the end of the extrusion
        List<GCodeEventNode> travelNodesToDelete = new ArrayList<>();
        for (GCodeEventNode node : movementNodes)
        {
            if (node instanceof TravelNode)
            {
                travelNodesToDelete.add(node);
            } else
            {
                break;
            }
        }

        movementNodes.removeAll(travelNodesToDelete);

        return new InScopeEvents(movementNodes, availableExtrusion);
    }

    protected Optional<CloseResult> insertProgressiveNozzleClose(GCodeEventNode startingNode,
            List<SectionNode> sectionsToConsider,
            final NozzleProxy nozzleInUse) throws NodeProcessingException, CannotCloseFromPerimeterException, NoPerimeterToCloseOverException, NotEnoughAvailableExtrusionException, PostProcessingError
    {
        nozzleInUse.setCurrentPosition(1.0);
        Optional<CloseResult> closeResult = Optional.empty();

        if (!startingNode.getParent().isPresent()
                || !(startingNode.getParent().get() instanceof SectionNode))
        {
            throw new NodeProcessingException();
        }

        //The last section is the one we want to close in...
        SectionNode sectionContainingNodeToAppendClosesTo = (SectionNode) startingNode.getParent().get();

        //IF the node containing the close request is in a non-perimeter section THEN
        //  IF the sum of extrusion in the inner and non-perimeter sections up to the next nozzle control point > target THEN
        //      Overwrite close towards the end of the extrusion using inner and non-perimeter
        //  ELSE IF 
        //  END IF
        //ELSE
        //IF the node containing the close request is in an inner perimeter section THEN
        //ELSE (must be a close from an outer perimeter
        //END IF
        //For non-perimeters...
        if (!(sectionContainingNodeToAppendClosesTo instanceof OuterPerimeterSectionNode)
                && !(sectionContainingNodeToAppendClosesTo instanceof InnerPerimeterSectionNode))
        {
            boolean processedOK = false;

            InScopeEvents unprioritisedNoPerimetersForOverwrite = extractAvailableMovements(startingNode, sectionsToConsider, false, false, true, true);

            //Attempt to overwrite close in non-perimeter elements
            if (unprioritisedNoPerimetersForOverwrite.getAvailableExtrusion() >= nozzleInUse.getNozzleParameters().getEjectionVolume())
            {
                processedOK = true;
                closeResult = overwriteClose(unprioritisedNoPerimetersForOverwrite, nozzleInUse, false);
            }

            if (!processedOK)
            {
                InScopeEvents unprioritisedNoOuterPerimeterForCopy = extractAvailableMovements(startingNode, sectionsToConsider, true, false, true, false);
                Optional<SearchSegment> finalSegment = nodeManagementUtilities.findPriorMovementPoints(startingNode);
                Optional<IntersectionResult> result = Optional.empty();

                if (finalSegment.isPresent())
                {
                    result = closeUtilities.findClosestMovementNode(finalSegment.get(), unprioritisedNoOuterPerimeterForCopy.getInScopeEvents(), false);
                }

                if (result.isPresent())
                {
                    try
                    {
                        closeResult = copyClose(unprioritisedNoOuterPerimeterForCopy, startingNode, Optional.of(result.get().getClosestNode()), nozzleInUse, false);
                        processedOK = true;
                    } catch (NotEnoughAvailableExtrusionException ex)
                    {
//                        steno.error("Failed to copy close from retract in non-perimeter - outer excluded");
                    }
                }
            }
//
//            if (!processedOK)
//            {
//                try
//                {
//                    InScopeEvents unprioritisedForCopy = extractAvailableMovements(startingNode, sectionsToConsider, true, true, false, false);
//                    closeResult = copyClose(unprioritisedForCopy, startingNode, Optional.empty(), nozzleInUse, false);
//                    processedOK = true;
//                } catch (NotEnoughAvailableExtrusionException ex1)
//                {
////                    steno.error("Failed to copy close from retract in non-perimeter");
//                }
//            }

            if (!processedOK)
            {
                InScopeEvents unprioritisedAllForOverwrite = extractAvailableMovements(startingNode, sectionsToConsider, true, true, true, false);
                if (unprioritisedAllForOverwrite.getAvailableExtrusion() >= nozzleInUse.getNozzleParameters().getEjectionVolume())
                {
                    processedOK = true;
                    closeResult = overwriteClose(unprioritisedAllForOverwrite, nozzleInUse, false);
                }
            }

            if (!processedOK)
            {
                InScopeEvents unprioritisedAllFromLastClose = extractAvailableMovements(startingNode, sectionsToConsider, true, true, true, false);
                closeResult = partialOpenAndCloseAtEndOfExtrusion(unprioritisedAllFromLastClose, nozzleInUse);
            }
        } else if (sectionContainingNodeToAppendClosesTo instanceof InnerPerimeterSectionNode)
        {
            //Do this if we're closing from an inner perimeter
            try
            {
                InScopeEvents unprioritisedNoOuterPerimeterAllOfInner = extractAvailableMovements(startingNode, sectionsToConsider, true, false, true, false);

                //Attempt to close over the inner only
                if (unprioritisedNoOuterPerimeterAllOfInner.getAvailableExtrusion() >= nozzleInUse.getNozzleParameters().getEjectionVolume())
                {
                    closeResult = overwriteClose(unprioritisedNoOuterPerimeterAllOfInner, nozzleInUse, false);
                } else
                {
                    InScopeEvents unprioritisedAllFromLastClose = extractAvailableMovements(startingNode, sectionsToConsider, true, true, true, false);

                    //Close over the inner + outer if there is enough volume
                    if (unprioritisedAllFromLastClose.getAvailableExtrusion() >= nozzleInUse.getNozzleParameters().getEjectionVolume())
                    {
                        closeResult = overwriteClose(unprioritisedAllFromLastClose, nozzleInUse, false);
                    } else
                    {
                        //Not enough volume so partial open
                        closeResult = partialOpenAndCloseAtEndOfExtrusion(unprioritisedAllFromLastClose, nozzleInUse);
                    }
                }
            } catch (NotEnoughAvailableExtrusionException ex)
            {
//                steno.error("Failed to close from retract in inner perimeter");
            }
        } else
        {
            boolean processedOK = false;

            //Do this if we're closing from an outer perimeter
            InScopeEvents unprioritisedNoOuterPerimeterForCopy = extractAvailableMovements(startingNode, sectionsToConsider, true, false, true, false);

            //There is some inner perimeter and we should have enough to close
            // Look for a valid intersection
            Optional<SearchSegment> finalSegment = nodeManagementUtilities.findPriorMovementPoints(startingNode);
            Optional<IntersectionResult> result = Optional.empty();

            if (finalSegment.isPresent())
            {
                result = closeUtilities.findClosestMovementNode(finalSegment.get(), unprioritisedNoOuterPerimeterForCopy.getInScopeEvents(), true);
            }

            if (result.isPresent())
            {
                try
                {
                    closeResult = copyClose(unprioritisedNoOuterPerimeterForCopy, startingNode, Optional.of(result.get().getClosestNode()), nozzleInUse, false);
                    processedOK = true;
                } catch (NotEnoughAvailableExtrusionException ex)
                {
//                    steno.error("Failed to copy close from retract in outer perimeter - outer excluded");
                }
            }

            if (!processedOK)
            {
                try
                {
                    InScopeEvents unprioritisedForCopy = extractAvailableMovements(startingNode, sectionsToConsider, true, true, true, false);
                    closeResult = copyClose(unprioritisedForCopy, startingNode, Optional.empty(), nozzleInUse, false);
                    processedOK = true;
                } catch (NotEnoughAvailableExtrusionException ex1)
                {
//                    steno.error("Failed to copy close from retract in outer perimeter");
                }
            }

            if (!processedOK)
            {
                InScopeEvents unprioritisedAllForOverwrite = extractAvailableMovements(startingNode, sectionsToConsider, true, true, true, false);
                if (unprioritisedAllForOverwrite.getAvailableExtrusion() >= nozzleInUse.getNozzleParameters().getEjectionVolume())
                {
                    processedOK = true;
                    closeResult = overwriteClose(unprioritisedAllForOverwrite, nozzleInUse, false);
                }
            }

            if (!processedOK)
            {
                // We'll have to partial open
                InScopeEvents unprioritisedAllFromLastClose = extractAvailableMovements(startingNode, sectionsToConsider, true, true, true, false);
                closeResult = partialOpenAndCloseAtEndOfExtrusion(unprioritisedAllFromLastClose, nozzleInUse);
            }
        }

        return closeResult;
    }

    private boolean replaceOpenNozzleWithPartialOpen(
            InScopeEvents inScopeEvents,
            double partialOpenValue,
            double idealPartialOpenValue)
    {
        boolean success = false;
        DecimalFormat df = new DecimalFormat("#.###");

        for (GCodeEventNode eventNode : inScopeEvents.getInScopeEvents())
        {
            if (eventNode instanceof NozzleValvePositionNode)
            {
                NozzleValvePositionNode nozzlePosition = (NozzleValvePositionNode) eventNode;
                nozzlePosition.setCommentText("Partial open B" + df.format(idealPartialOpenValue));
                nozzlePosition.getNozzlePosition().setB(partialOpenValue);
                nozzlePosition.getNozzlePosition().setPartialOpen(true);
                success = true;
                break;
            }
        }

        if (!success)
        {
            //No nozzle position events found
            //Insert a partial open node at the start of the section
            //TODO put replenish in for partial opens

            NozzleValvePositionNode nozzlePosition = new NozzleValvePositionNode();
            nozzlePosition.setCommentText("Partial open B" + df.format(idealPartialOpenValue));
            nozzlePosition.getNozzlePosition().setB(partialOpenValue);
            nozzlePosition.getNozzlePosition().setPartialOpen(true);

            int nodeToInsertBeforeIndex = inScopeEvents.getInScopeEvents().size() - 1;

            for (int eventCounter = inScopeEvents.getInScopeEvents().size() - 1; eventCounter >= 0; eventCounter--)
            {
                if (inScopeEvents.getInScopeEvents().get(eventCounter) instanceof ExtrusionNode)
                {
                    nodeToInsertBeforeIndex = eventCounter;
                    break;
                }
            }

            inScopeEvents.getInScopeEvents().get(nodeToInsertBeforeIndex).addSiblingBefore(nozzlePosition);
        }

        return success;
    }

    private Optional<CloseResult> partialOpenAndCloseAtEndOfExtrusion(
            InScopeEvents inScopeEvents,
            NozzleProxy nozzleInUse)
    {
        Optional<CloseResult> closeResult = Optional.empty();
        double nozzleStartPosition = 0;
        double nozzleCloseOverVolume = 0;

        // We shouldn't have been asked to partial open - there is more than the ejection volume of material available
        if (inScopeEvents.getAvailableExtrusion() >= nozzleInUse.getNozzleParameters().getEjectionVolume())
        {
            return closeResult;
        }
        
        double idealBValue = Math.min(1, inScopeEvents.getAvailableExtrusion()
                / nozzleInUse.getNozzleParameters().
                getEjectionVolume());

        double bValue = Math.max(idealBValue, nozzleInUse.getNozzleParameters().getPartialBMinimum());

        nozzleStartPosition = bValue;
        nozzleCloseOverVolume = inScopeEvents.getAvailableExtrusion();

        try
        {
            nozzleInUse.setCurrentPosition(nozzleStartPosition);
            closeResult = overwriteClose(inScopeEvents, nozzleInUse, true);
            replaceOpenNozzleWithPartialOpen(inScopeEvents, bValue, idealBValue);
        } catch (NotEnoughAvailableExtrusionException ex)
        {
            steno.error("Not Got Enough Available Extrusion - shouldn't see this");
        }

        closeResult = Optional.of(new CloseResult(nozzleStartPosition, nozzleCloseOverVolume, closeResult.get().getNodeContainingFinalClose()));

        return closeResult;
    }

//    private Optional<CloseResult> reverseCloseFromEndOfExtrusion(List<SectionNode> sectionsToConsider,
//            ExtrusionNode nodeToAppendClosesTo,
//            NozzleProxy nozzleInUse) throws PostProcessingError, NotEnoughAvailableExtrusionException
//    {
//        Optional<CloseResult> closeResult = Optional.empty();
//        double nozzleStartPosition = 0;
//        double nozzleCloseOverVolume = 0;
//
//        double availableExtrusion = 0;
//
//        for (SectionNode sectionNode : sectionsToConsider)
//        {
//            availableExtrusion += sectionNode.getTotalExtrusion();
//        }
//
//        if (availableExtrusion >= nozzleInUse.getNozzleParameters().getEjectionVolume())
//        {
//            nozzleStartPosition = 1.0;
//            nozzleCloseOverVolume = nozzleInUse.getNozzleParameters().getEjectionVolume();
//
//            copyExtrusionEvents(nozzleCloseOverVolume,
//                    sectionsToConsider,
//                    nodeToAppendClosesTo,
//                    false);
//
//            closeResult = Optional.of(new CloseResult(nozzleStartPosition, nozzleCloseOverVolume));
//        } else
//        {
//            throw new NotEnoughAvailableExtrusionException("Not enough extrusion when attempting to reverse close");
//        }
//
//        return closeResult;
//    }
//    protected Optional<CloseResult> closeInwardsOntoPerimeter(
//            final InScopeEvents inScopeEvents,
//            final NozzleProxy nozzleInUse
//    ) throws CannotCloseFromPerimeterException, NotEnoughAvailableExtrusionException, NoPerimeterToCloseOverException, DidntFindEventException, NodeProcessingException
//    {
//        Optional<CloseResult> closeResult = Optional.empty();
//
//        if (inScopeEvents.getAvailableExtrusion() >= nozzleInUse.getNozzleParameters().getEjectionVolume())
//        {
//            // Look for a valid intersection
//            Optional<IntersectionResult> result = closeUtilities.findClosestMovementNode(inScopeEvents.getInScopeEvents(), true);
//
//            if (result.isPresent())
//            {
//                closeResult = copyClose(inScopeEvents, inScopeEvents.getInScopeEvents().get(0), Optional.of(result.get().getClosestNode()), nozzleInUse);
//            } else
//            {
//                throw new NoPerimeterToCloseOverException("No valid perimeter");
//            }
//        } else
//        {
//            throw new NotEnoughAvailableExtrusionException("Not enough available extrusion to close");
//        }
//
//        return closeResult;
//    }
    /**
     * Close along the existing extrusion Don't add any nodes except where this
     * is necessary to insert breaks for close boundaries
     *
     * @param inScopeEvents
     * @param nozzleInUse
     * @param useAvailableExtrusion
     * @return CloseResult
     * @throws celtech.roboxbase.postprocessor.NotEnoughAvailableExtrusionException
     */
    protected Optional<CloseResult> overwriteClose(
            final InScopeEvents inScopeEvents,
            final NozzleProxy nozzleInUse,
            final boolean useAvailableExtrusion) throws NotEnoughAvailableExtrusionException
    {
        Optional<CloseResult> closeResult = Optional.empty();

        NozzleParameters nozzleParams = nozzleInUse.getNozzleParameters();

        double volumeToCloseOver = (useAvailableExtrusion) ? inScopeEvents.getAvailableExtrusion() : nozzleParams.getEjectionVolume();
        double nozzleStartingPosition = nozzleInUse.getCurrentPosition();

        ExtrusionNode finalCloseNode = null;
        boolean closeStarted = false;

        if (useAvailableExtrusion
                || inScopeEvents.getAvailableExtrusion() >= nozzleInUse.getNozzleParameters().getEjectionVolume())
        {
            double runningTotalOfExtrusion = 0;
            double bValue = -1;

            for (int movementNodeCounter = 0;
                    movementNodeCounter < inScopeEvents.getInScopeEvents().size();
                    movementNodeCounter++)
            {
                if (inScopeEvents.getInScopeEvents().get(movementNodeCounter) instanceof ExtrusionNode)
                {
                    ExtrusionNode extrusionNodeBeingExamined = (ExtrusionNode) inScopeEvents.getInScopeEvents().get(movementNodeCounter);

                    int comparisonResult = MathUtils.compareDouble(runningTotalOfExtrusion + extrusionNodeBeingExamined.getExtrusion().getE(), volumeToCloseOver, 0.00001);
                    if (extrusionNodeBeingExamined.getCommentText().contains("Elided"))
                    {
                        steno.info("Arrgghh....");
                    }

                    if (comparisonResult == MathUtils.LESS_THAN)
                    {
                        //One step along the way
                        bValue = (runningTotalOfExtrusion / volumeToCloseOver) * nozzleStartingPosition;
                        extrusionNodeBeingExamined.getNozzlePosition().setB(bValue);
                        runningTotalOfExtrusion += extrusionNodeBeingExamined.getExtrusion().getE();
                        //No extrusion during a close
                        extrusionNodeBeingExamined.getExtrusion().eNotInUse();
                        extrusionNodeBeingExamined.getExtrusion().dNotInUse();
                        if (extrusionNodeBeingExamined.getCommentText().contains("PROC"))
                        {
                            steno.info("Helpp!!");
                        }
                        extrusionNodeBeingExamined.appendCommentText(" PROC LESS");
                        if (finalCloseNode == null)
                        {
                            finalCloseNode = extrusionNodeBeingExamined;
                        }
                        closeStarted = true;
                    } else if (comparisonResult == MathUtils.EQUAL)
                    {
                        //All done
                        bValue = (runningTotalOfExtrusion / volumeToCloseOver) * nozzleStartingPosition;
                        extrusionNodeBeingExamined.getNozzlePosition().setB(bValue);
                        runningTotalOfExtrusion += extrusionNodeBeingExamined.getExtrusion().getE();
                        //No extrusion during a close
                        extrusionNodeBeingExamined.getExtrusion().eNotInUse();
                        extrusionNodeBeingExamined.getExtrusion().dNotInUse();
                        if (extrusionNodeBeingExamined.getCommentText().contains("PROC"))
                        {
                            steno.info("Helpp!!");
                        }
                        extrusionNodeBeingExamined.appendCommentText(" PROC EQUAL");
                        if (finalCloseNode == null)
                        {
                            finalCloseNode = extrusionNodeBeingExamined;
                        }
                        break;
                    } else
                    {
                        //If we got here then we need to split this extrusion
                        //We're splitting the last part of the line (we're just looking at it in reverse order as we consider the line from the end)
                        MovementProvider priorMovement = null;

                        if ((movementNodeCounter == inScopeEvents.getInScopeEvents().size() - 1
                                || !(inScopeEvents.getInScopeEvents().get(movementNodeCounter + 1) instanceof MovementProvider)))
                        {
                            //We dont have anywhere to go!
                            try
                            {
                                Optional<MovementProvider> priorSectionMovement = nodeManagementUtilities.findPriorMovement(extrusionNodeBeingExamined);
                                priorMovement = priorSectionMovement.get();
                            } catch (NodeProcessingException ex)
                            {
                                throw new RuntimeException("Unable to find prior node when splitting extrusion at node " + extrusionNodeBeingExamined.renderForOutput());
                            }
                        } else
                        {
                            priorMovement = (MovementProvider) inScopeEvents.getInScopeEvents().get(movementNodeCounter + 1);
                        }

                        Vector2D firstPoint = new Vector2D(priorMovement.getMovement().getX(), priorMovement.getMovement().getY());
                        Vector2D secondPoint = new Vector2D(extrusionNodeBeingExamined.getMovement().getX(), extrusionNodeBeingExamined.getMovement().getY());

                        // We can work out how to split this extrusion
                        double extrusionInFirstSection = runningTotalOfExtrusion + extrusionNodeBeingExamined.getExtrusion().getE() - volumeToCloseOver;
                        double extrusionInSecondSection = extrusionNodeBeingExamined.getExtrusion().getE() - extrusionInFirstSection;

                        double proportionOfDistanceInSecondSection = extrusionInFirstSection / extrusionNodeBeingExamined.getExtrusion().getE();

                        Vector2D actualVector = secondPoint.subtract(firstPoint);
                        Vector2D firstSegment = firstPoint.add(proportionOfDistanceInSecondSection,
                                actualVector);

                        ExtrusionNode newExtrusionNode = new ExtrusionNode();
                        newExtrusionNode.setCommentText("Remainder pre close towards end");
                        newExtrusionNode.getExtrusion().setE((float) extrusionInFirstSection);
                        newExtrusionNode.getMovement().setX(firstSegment.getX());
                        newExtrusionNode.getMovement().setY(firstSegment.getY());
                        newExtrusionNode.getFeedrate().setFeedRate_mmPerMin(extrusionNodeBeingExamined.getFeedrate().getFeedRate_mmPerMin());
                        newExtrusionNode.appendCommentText(" MORE REM");

                        extrusionNodeBeingExamined.addSiblingBefore(newExtrusionNode);

                        extrusionNodeBeingExamined.getExtrusion().setE((float) extrusionInSecondSection);
                        extrusionNodeBeingExamined.appendCommentText("Start of overwrite close towards end");
                        bValue = (runningTotalOfExtrusion / volumeToCloseOver) * nozzleStartingPosition;
                        if (closeStarted)
                        {
                            extrusionNodeBeingExamined.getNozzlePosition().setB(bValue);
                        } else
                        {
                            bValue = 0;
                            extrusionNodeBeingExamined.getNozzlePosition().setB(0);
                        }
                        if (extrusionNodeBeingExamined.getCommentText().contains("PROC"))
                        {
                            steno.info("Helpp!!");
                        }

                        extrusionNodeBeingExamined.appendCommentText(" PROC MORE START");

                        runningTotalOfExtrusion += extrusionNodeBeingExamined.getExtrusion().getE();
                        //No extrusion during a close
                        extrusionNodeBeingExamined.getExtrusion().eNotInUse();
                        extrusionNodeBeingExamined.getExtrusion().dNotInUse();
                        if (finalCloseNode == null)
                        {
                            finalCloseNode = extrusionNodeBeingExamined;
                        }
                        break;
                    }
                }
            }

            if (finalCloseNode != null)
            {
                finalCloseNode.setElidedExtrusion(volumeToCloseOver);
            }
            closeResult = Optional.of(new CloseResult(1.0, nozzleInUse.getNozzleParameters().getEjectionVolume(), finalCloseNode));
        } else
        {
            throw new NotEnoughAvailableExtrusionException("When closing towards end of extrusion");
        }

        return closeResult;
    }

    /**
     * Create new nodes whilst closing the nozzle along the specified path
     *
     * @param extractedMovements
     * @param nodeToAddClosesTo
     * @param nodeToStartCopyingFrom
     * @param nozzleInUse
     * @param preferForwards
     * @return
     * @throws celtech.roboxbase.postprocessor.NotEnoughAvailableExtrusionException
     * @throws celtech.roboxbase.postprocessor.nouveau.nodes.NodeProcessingException
     */
    protected Optional<CloseResult> copyClose(
            final InScopeEvents extractedMovements,
            final GCodeEventNode nodeToAddClosesTo,
            final Optional<GCodeEventNode> nodeToStartCopyingFrom,
            final NozzleProxy nozzleInUse,
            final boolean preferForwards) throws NotEnoughAvailableExtrusionException, NodeProcessingException
    {
        String id = SystemUtils.generate16DigitID();
        String additionalComment = "";
        NozzleParameters nozzleParams = nozzleInUse.getNozzleParameters();

        double volumeToCloseOver = nozzleParams.getEjectionVolume();

        double runningTotalOfExtrusion = 0;
        double currentNozzlePosition = nozzleInUse.getCurrentPosition();
        double closePermm3Volume = currentNozzlePosition / volumeToCloseOver;
        double requiredVolumeToCloseOver = currentNozzlePosition / closePermm3Volume;

        int nodeToStartCopyingFromIndex = 0;
        double availableExtrusion = 0;

        Extrusion previousNodeExtrusion = new Extrusion();
        boolean goingBackwards = true;
        
        TravelNode travelToStart = null;

        GCodeEventNode nodeToAddToPlaceholder = nodeToAddClosesTo;

        if (nodeToStartCopyingFrom.isPresent())
        {

            // Find the index of this node
            for (int inScopeEventCounter = 0; inScopeEventCounter < extractedMovements.getInScopeEvents().size(); inScopeEventCounter++)
            {
                if (extractedMovements.getInScopeEvents().get(inScopeEventCounter) == nodeToStartCopyingFrom.get())
                {
                    nodeToStartCopyingFromIndex = inScopeEventCounter;
                    break;
                }
            }

            AvailableExtrusion availableExtrusionBackward = nodeManagementUtilities.findAvailableExtrusion(extractedMovements, nodeToStartCopyingFromIndex, false);
            double availableExtrusionBackwardsToStartOfExtrusion = availableExtrusionBackward.getAvailableExtrusion();

            AvailableExtrusion availableExtrusionForward = nodeManagementUtilities.findAvailableExtrusion(extractedMovements, nodeToStartCopyingFromIndex, true);
            double availableExtrusionForwardsToEndOfExtrusion = availableExtrusionForward.getAvailableExtrusion();

            if (availableExtrusionBackwardsToStartOfExtrusion >= volumeToCloseOver)
            {
                additionalComment = "backwards to start";
                availableExtrusion = availableExtrusionBackwardsToStartOfExtrusion;
            } else if (availableExtrusionForwardsToEndOfExtrusion >= volumeToCloseOver)
            {
                additionalComment = "forwards to end";
                availableExtrusion = availableExtrusionForwardsToEndOfExtrusion;
                goingBackwards = false;
            } else
            {
                throw new NotEnoughAvailableExtrusionException("Not enough extrusion forward or back");
            }
        } else
        {
            //No start node specified
            // The copy point should be as early as possible

            // Find the index of the start node
            GCodeEventNode startToLookBackFrom = nodeToAddClosesTo;
            if (nodeToAddClosesTo instanceof RetractNode)
            {
                startToLookBackFrom = ((RetractNode) nodeToAddClosesTo).getPriorExtrusionNode();
            }

            int indexOfNodeToLookBackFrom = 0;

            for (int inScopeEventCounter = 0; inScopeEventCounter < extractedMovements.getInScopeEvents().size(); inScopeEventCounter++)
            {
                if (extractedMovements.getInScopeEvents().get(inScopeEventCounter) == startToLookBackFrom)
                {
                    indexOfNodeToLookBackFrom = inScopeEventCounter;
                    break;
                }
            }

            AvailableExtrusion availableExtrusionTowardsStart = nodeManagementUtilities.findAvailableExtrusion(extractedMovements, indexOfNodeToLookBackFrom, false);

            if (availableExtrusionTowardsStart.getAvailableExtrusion() >= volumeToCloseOver)
            {
                for (int inScopeEventCounter = indexOfNodeToLookBackFrom; inScopeEventCounter < extractedMovements.getInScopeEvents().size(); inScopeEventCounter++)
                {
                    if (extractedMovements.getInScopeEvents().get(inScopeEventCounter) == availableExtrusionTowardsStart.getLastNodeExamined())
                    {
                        nodeToStartCopyingFromIndex = inScopeEventCounter;
                        break;
                    }
                }
                goingBackwards = false;
                availableExtrusion = availableExtrusionTowardsStart.getAvailableExtrusion();
            } else
            {
                throw new NotEnoughAvailableExtrusionException("Not enough extrusion when considering extrusion from start");
            }
        }
        
        if(!goingBackwards && nodeToStartCopyingFromIndex == extractedMovements.getInScopeEvents().size() - 1)
        {
            // In this case we cannot start from the last in the list (which is the first move in the section)
            // This is because we need to go back to the previous node (which is the next in the list) to find the travel too point for the nozzle close.
            nodeToStartCopyingFromIndex--;
        }

        if (extractedMovements.getInScopeEvents().get(nodeToStartCopyingFromIndex) instanceof MovementProvider)
        {
            if (extractedMovements.getInScopeEvents().get(nodeToStartCopyingFromIndex) instanceof ExtrusionProvider)
            {
                Extrusion extrusionInFirstPart = ((ExtrusionProvider) extractedMovements.getInScopeEvents().get(nodeToStartCopyingFromIndex)).getExtrusion();
                
                if(goingBackwards)
                {
                    // Need to save the extrusion as we are working backwards, effectively reversing the nodes.
                    // This extrusion will be copied to the previous node etc.
                    previousNodeExtrusion = extrusionInFirstPart.clone();
                    extrusionInFirstPart.eNotInUse();
                    extrusionInFirstPart.dNotInUse();
                } else
                {
//                    // In the forwards direction we are in front of this nodes extrusion, therefore it is unavailable to use
//                    availableExtrusion -= extrusionInFirstPart.getE();
//                    availableExtrusion -= extrusionInFirstPart.getD();
                }
            }

            Movement firstMovement;
            
            if(!goingBackwards)
            {
                // Travel to beginning of this extrusion
                firstMovement = ((MovementProvider) extractedMovements.getInScopeEvents().get(nodeToStartCopyingFromIndex + 1)).getMovement();
            } else 
            {
                firstMovement = ((MovementProvider) extractedMovements.getInScopeEvents().get(nodeToStartCopyingFromIndex)).getMovement();
            }

            //Travel to the start of the intersected extrusion
            travelToStart = new TravelNode();
            travelToStart.getMovement().setX(firstMovement.getX());
            travelToStart.getMovement().setY(firstMovement.getY());
            travelToStart.appendCommentText("Travel to start of close");
            travelToStart.getFeedrate().setFeedRate_mmPerMin(12000);

            if (goingBackwards && nodeToStartCopyingFromIndex < extractedMovements.getInScopeEvents().size() - 1)
            {
                nodeToStartCopyingFromIndex++;
            }
//            } else if (!goingBackwards && nodeToStartCopyingFromIndex > 0)
//            {
//                nodeToStartCopyingFromIndex--;
//            }
        }

        ExtrusionNode finalCloseNode = null;

        if (availableExtrusion >= nozzleInUse.getNozzleParameters().getEjectionVolume())
        {
            int directionIterator = goingBackwards ? 1 : -1;
            
            OUTER:
            for (int inScopeEventCounter = nodeToStartCopyingFromIndex; 
                    inScopeEventCounter >= 0 && inScopeEventCounter < extractedMovements.getInScopeEvents().size(); 
                    inScopeEventCounter += directionIterator) 
            {
                ExtrusionNode convertedTravelNode = null;
                
                if (extractedMovements.getInScopeEvents().get(inScopeEventCounter) instanceof TravelNode)
                {
                    if (goingBackwards)
                    {
                        // If we are working backwards and we find a travel, we need to make sure the travel goes to the next node.
                        // The current travel needs to be turned to an extrusion.
                        TravelNode travelNode = ((TravelNode) extractedMovements.getInScopeEvents().get(inScopeEventCounter));
                        convertedTravelNode = new ExtrusionNode();
                        convertedTravelNode.getMovement().setX(travelNode.getMovement().getX());
                        convertedTravelNode.getMovement().setY(travelNode.getMovement().getY());
                        convertedTravelNode.getFeedrate().setFeedRate_mmPerMin(travelNode.getFeedrate().getFeedRate_mmPerMin());
                        convertedTravelNode.getExtrusion().setE(0);
                    } else
                    {
                        TravelNode copy = ((TravelNode) extractedMovements.getInScopeEvents().get(inScopeEventCounter)).clone();
                        nodeToAddToPlaceholder.addSiblingAfter(copy);
                        nodeToAddToPlaceholder = copy;
                    }
                }
                if (extractedMovements.getInScopeEvents().get(inScopeEventCounter) instanceof ExtrusionNode
                        || convertedTravelNode != null) 
                {
                    ExtrusionNode extrusionNodeBeingExamined;
                
                    if (convertedTravelNode != null)
                    {
                        extrusionNodeBeingExamined = convertedTravelNode;
                    } else
                    {
                        extrusionNodeBeingExamined = (ExtrusionNode) extractedMovements.getInScopeEvents().get(inScopeEventCounter);
                    }

                    ExtrusionNode copy = extrusionNodeBeingExamined.clone();
                    nodeToAddToPlaceholder.addSiblingAfter(copy);
                    nodeToAddToPlaceholder = copy;
                    
                    if(goingBackwards)
                    {
                        // Travelling backwards this copy needs the previous (the node after in actual GCode) nodes extrusion.
                        copy.getExtrusion().setE(previousNodeExtrusion.getE());
                        previousNodeExtrusion = extrusionNodeBeingExamined.getExtrusion().clone();
                    }
                    
                    int comparisonResult = MathUtils.compareDouble(runningTotalOfExtrusion + copy.getExtrusion().getE(), requiredVolumeToCloseOver, 0.00001);
                    
                    switch (comparisonResult) 
                    {
                        case MathUtils.LESS_THAN:
                        {
                            //One step along the way
                            runningTotalOfExtrusion += copy.getExtrusion().getE();
                            double bValue = 1 - (runningTotalOfExtrusion / volumeToCloseOver);
                            copy.getNozzlePosition().setB(bValue);
                            //No extrusion during a close
                            copy.getExtrusion().eNotInUse();
                            copy.getExtrusion().dNotInUse();
                            copy.appendCommentText("copied node <" + id);
                            finalCloseNode = copy;
                            //Wipe out extrusion in the area we copied from as well
                            extrusionNodeBeingExamined.getExtrusion().eNotInUse();
                            extrusionNodeBeingExamined.getExtrusion().dNotInUse();
                            extrusionNodeBeingExamined.appendCommentText("Elided-" + id);
                            break;
                        }
                        case MathUtils.EQUAL: 
                        {
                            //All done
                            runningTotalOfExtrusion += copy.getExtrusion().getE();
                            double bValue = 0;
                            copy.getNozzlePosition().setB(bValue);
                            //No extrusion during a close
                            copy.getExtrusion().eNotInUse();
                            copy.getExtrusion().dNotInUse();
                            copy.appendCommentText("copied node =" + id);
                            finalCloseNode = copy;
                            //Wipe out extrusion in the area we copied from as well
                            extrusionNodeBeingExamined.getExtrusion().eNotInUse();
                            extrusionNodeBeingExamined.getExtrusion().dNotInUse();
                            extrusionNodeBeingExamined.appendCommentText("Elided-" + id);
                            break OUTER;
                        }
                        default:
                        {
                            MovementProvider priorMovement = null;
                            if (goingBackwards)
                            {
                                if (inScopeEventCounter == 0)
                                {
                                    Optional<SectionNode> parentSection = nodeManagementUtilities.lookForParentSectionNode(extrusionNodeBeingExamined);
                                    Optional<MovementProvider> movement = Optional.empty();
                                    
                                    if (parentSection.isPresent())
                                    {
                                        movement = nodeManagementUtilities.findNextMovement(parentSection.get().getParent().get(), extrusionNodeBeingExamined);
                                    }
                                    
                                    if (!movement.isPresent())
                                    {
                                        steno.error("Nowhere to go");
                                        throw new RuntimeException("Nowhere nowhere nowhere to go");
                                    }
                                    
                                    priorMovement = movement.get();
                                } else
                                {
                                    priorMovement = (MovementProvider) extractedMovements.getInScopeEvents().get(inScopeEventCounter - 1);
                                }
                            } 
                            else
                            {
                                if (inScopeEventCounter == extractedMovements.getInScopeEvents().size() - 1
                                        || !(extractedMovements.getInScopeEvents().get(inScopeEventCounter + 1) instanceof MovementProvider))
                                {
                                    Optional<MovementProvider> movement = nodeManagementUtilities.findPriorMovement(extractedMovements.getInScopeEvents().get(inScopeEventCounter));
                                    if (!movement.isPresent())
                                    {
                                        steno.error("Nowhere to go");
                                        throw new RuntimeException("Nowhere nowhere nowhere to go");
                                    }
                                    
                                    priorMovement = movement.get();
                                } else
                                {
                                    priorMovement = (MovementProvider) extractedMovements.getInScopeEvents().get(inScopeEventCounter + 1);
                                }
                            }
                            
                            // We can work out how to split this extrusion
                            Vector2D firstPoint = new Vector2D(priorMovement.getMovement().getX(), priorMovement.getMovement().getY());
                            Vector2D secondPoint = new Vector2D(extrusionNodeBeingExamined.getMovement().getX(), extrusionNodeBeingExamined.getMovement().getY());
                            
                            // Backwards direction
                            //                               
                            // ------>-------->--------->---> Extrusions
                            //       |----------------------| Running total
                            //    |-------------------------| Required volume to close
                            //    |--|                        First section
                            // |--|                           Second section
                            //
                            // Forwards direction
                            //
                            // --->-------->--------->------> Extrusions
                            //    |------------------|        Running total
                            // |-------------------------|    Required volume to close
                            //                       |---|    First section
                            //                           |--| Second section
                            
                            double extrusionInFirstSection = requiredVolumeToCloseOver - runningTotalOfExtrusion;
                            double extrusionInSecondSection = copy.getExtrusion().getE() - extrusionInFirstSection;
                            
                            double proportionOfDistanceInFirstSection = extrusionInFirstSection / copy.getExtrusion().getE();
                            
                            Vector2D actualVector = secondPoint.subtract(firstPoint);
                            Vector2D firstSegment = firstPoint.add(proportionOfDistanceInFirstSection,
                                    actualVector);
                            
                            copy.getMovement().setX(firstSegment.getX());
                            copy.getMovement().setY(firstSegment.getY());
                            runningTotalOfExtrusion += copy.getExtrusion().getE();
                            copy.getExtrusion().setE(0);
                            copy.getExtrusion().setD(0);
                            copy.getNozzlePosition().bNotInUse();
                            copy.appendCommentText("End of copy close segment - " + additionalComment + " " + id);
                            copy.getNozzlePosition().setB(0);
                            //No extrusion during a close
                            copy.getExtrusion().eNotInUse();
                            copy.getExtrusion().dNotInUse();
                            finalCloseNode = copy;
                            
                            if(goingBackwards)
                            {
                                ExtrusionNode remainderSection = new ExtrusionNode();
                                remainderSection.getExtrusion().setE(extrusionInSecondSection);
                                remainderSection.getMovement().setX(firstSegment.getX());
                                remainderSection.getMovement().setY(firstSegment.getY());
                                remainderSection.getFeedrate().setFeedRate_mmPerMin(extrusionNodeBeingExamined.getFeedrate().getFeedRate_mmPerMin());
                                extrusionNodeBeingExamined.addSiblingAfter(remainderSection);
                            } else
                            {
                                TravelNode newTravel = new TravelNode();
                                newTravel.getMovement().setX(firstSegment.getX());
                                newTravel.getMovement().setY(firstSegment.getY());
                                newTravel.getFeedrate().setFeedRate_mmPerMin(extrusionNodeBeingExamined.getFeedrate().getFeedRate_mmPerMin());
                                extrusionNodeBeingExamined.addSiblingBefore(newTravel);
                                extrusionNodeBeingExamined.getExtrusion().setE(extrusionInSecondSection);
                            }
                            
                            break OUTER;
                        }
                    }
                }
            }

            if (travelToStart != null)
            {
                nodeToAddClosesTo.addSiblingAfter(travelToStart);
            }
        } else
        {
            throw new NotEnoughAvailableExtrusionException("Not enough extrusion when attempting to copy close");
        }

        if (finalCloseNode != null)
        {
            finalCloseNode.setElidedExtrusion(volumeToCloseOver);
        } else
        {
            steno.error("No final node from copy close");
        }

        return Optional.of(new CloseResult(1.0, volumeToCloseOver, finalCloseNode));
    }

    protected void insertCloseNodes(LayerNode layerNode, LayerPostProcessResult lastLayerParseResult, List<NozzleProxy> nozzleProxies)
    {
        if (featureSet.isEnabled(PostProcessorFeature.OPEN_AND_CLOSE_NOZZLES))
        {
            //Tool select nodes are directly under a layer
            Iterator<GCodeEventNode> layerChildIterator = layerNode.childIterator();

            ToolSelectNode lastToolSelectNode = null;

            if (lastLayerParseResult != null)
            {
                lastToolSelectNode = lastLayerParseResult.getLastToolSelectInForce();
            }

            List<RetractHolder> retractNodes = new ArrayList<>();

            while (layerChildIterator.hasNext())
            {
                GCodeEventNode layerChild = layerChildIterator.next();

                if (layerChild instanceof RetractNode)
                {
                    if (lastToolSelectNode != null)
                    {
                        NozzleProxy nozzleInUse = nozzleProxies.get(lastToolSelectNode.getToolNumber());
                        retractNodes.add(new RetractHolder((RetractNode) layerChild, nozzleInUse));
                    } else
                    {
                        steno.warning("Removed retract on layer " + layerNode.getLayerNumber() + " with no prior tool selected");
                        retractNodes.add(new RetractHolder((RetractNode) layerChild, null));
                    }
                } else if (layerChild instanceof ToolSelectNode)
                {
                    ToolSelectNode toolSelectNode = (ToolSelectNode) layerChild;

                    NozzleProxy nozzleInUse = nozzleProxies.get(toolSelectNode.getToolNumber());

                    Iterator<GCodeEventNode> toolSelectChildren = toolSelectNode.treeSpanningIterator(null);

                    while (toolSelectChildren.hasNext())
                    {
                        GCodeEventNode toolSelectChild = toolSelectChildren.next();

                        if (toolSelectChild instanceof RetractNode)
                        {
                            retractNodes.add(new RetractHolder((RetractNode) toolSelectChild, nozzleInUse));
                        }
                    }
                }
            }

            retractNodes.stream().map((retractHolder) -> {
                if (retractHolder.getNozzle() != null)
                {
                    boolean success = processRetractNode(retractHolder.getNode(), retractHolder.getNozzle(), layerNode, lastLayerParseResult);
                    if (!success)
                    {
                        steno.warning("Close failed - removing retract anyway on layer " + layerNode.getLayerNumber());
                    }
                }
                return retractHolder;
            }).forEachOrdered((retractHolder) -> {
                retractHolder.getNode().removeFromParent();
            });
        }
    }

    private boolean processRetractNode(RetractNode retractNode,
            NozzleProxy nozzleInUse,
            LayerNode thisLayer,
            LayerPostProcessResult lastLayerParseResult)
    {
        this.layerNumber = thisLayer.getLayerNumber();
        Optional<CloseResult> closeResult = Optional.empty();

        boolean processedClose = false;

        boolean succeeded = false;

        try
        {
            if (retractNode.getPriorExtrusionNode() != null)
            {
                closeResult = insertProgressiveNozzleClose(retractNode, retractNode.getSectionsToConsider(), nozzleInUse);
                processedClose = true;
            } else
            {
                LayerNode lastLayer = lastLayerParseResult.getLayerData();

                //Look for the last extrusion on the previous layer
                if (lastLayer == null)
                {
                    // There wasn't a last layer - this is a lone retract at the start of the file
                    steno.warning("Discarding retract from layer " + thisLayer.getLayerNumber());
                } else
                {
                    ExtrusionNode extrusionToCloseFrom = null;

                    List<SectionNode> sectionsToConsider = new ArrayList<>();
                    GCodeEventNode lastNodeOnLastLayer = lastLayer.getAbsolutelyTheLastEvent();
                    IteratorWithOrigin<GCodeEventNode> layerBackwardsIterator = lastNodeOnLastLayer.treeSpanningBackwardsAndMeIterator();
                    SectionNode lastSectionNode = null;

                    //Don't need to close if the nozzle is already closed!
//                    if (!(lastNodeOnLastLayer instanceof NozzlePositionProvider
//                            && ((NozzlePositionProvider) lastNodeOnLastLayer).getNozzlePosition().isBSet()))
//                    {
                        boolean foundExtrusionBeforeNozzleClose = false;

//                        if (lastNodeOnLastLayer instanceof ExtrusionNode)
//                        {
//                            foundExtrusionBeforeNozzleClose = true;
//                            extrusionToCloseFrom = (ExtrusionNode) lastNodeOnLastLayer;
//                            Optional<SectionNode> parentSection = nodeManagementUtilities.lookForParentSectionNode(lastNodeOnLastLayer);
//                            if (parentSection.isPresent())
//                            {
//                                if (lastSectionNode != parentSection.get())
//                                {
//                                    sectionsToConsider.add(0, parentSection.get());
//                                    lastSectionNode = parentSection.get();
//                                }
//                            }
//                        }
                        search:
                        while (layerBackwardsIterator.hasNext())
                        {
                            GCodeEventNode node = layerBackwardsIterator.next();
                            if (node instanceof ExtrusionNode)
                            {
                            ExtrusionNode exNode = (ExtrusionNode) node;
                            if (exNode.getNozzlePosition().isBSet())
                            {
                                // Nozzle is closed/closing
                                break;
                            } else
                            {
                                foundExtrusionBeforeNozzleClose = true;
                                if (extrusionToCloseFrom == null)
                                {
                                    extrusionToCloseFrom = exNode;
                                }
                                Optional<SectionNode> parentSection = nodeManagementUtilities.lookForParentSectionNode(node);
                                if (parentSection.isPresent())
                                {
                                    if (lastSectionNode != parentSection.get())
                                    {
                                        sectionsToConsider.add(0, parentSection.get());
                                        lastSectionNode = parentSection.get();
                                    }
                                }
                            }
                        }
                    }

                        if (foundExtrusionBeforeNozzleClose)
                        {
                            processedClose = true;
                            if (sectionsToConsider.size() > 0)
                            {
                                closeResult = insertProgressiveNozzleClose(extrusionToCloseFrom, sectionsToConsider, nozzleInUse);
                            } else
                            {
                                //We only seem to have one extrusion to close over...
                                if (extrusionToCloseFrom != null)
                                {
                                    sectionsToConsider.add((SectionNode) extrusionToCloseFrom.getParent().get());
                                    closeResult = insertProgressiveNozzleClose(extrusionToCloseFrom, sectionsToConsider, nozzleInUse);
                                }
                            }
                        }
//                    }
                    }
                }

            if (processedClose)
            {
                if (closeResult.isPresent())
                {
//                    if (!nextExtrusionNode.isPresent())
//                    {
//                        nextExtrusionNode = nodeManagementUtilities.findNextExtrusion(thisLayer, retractNode);
//                    }
//
//                    if (nextExtrusionNode.isPresent())
//                    {
//                        //Add the elided extrusion to this node - the open routine will find it later
//                        ((ExtrusionNode) nextExtrusionNode.get()).setElidedExtrusion(closeResult.get().getNozzleCloseOverVolume());
//                    }

                    succeeded = true;
                } else
                {
                    throw new NodeProcessingException("Failed to close after retract", (Renderable) retractNode);
                }
            } else
            {
                succeeded = true;
            }

        } catch (NodeProcessingException | CannotCloseFromPerimeterException | NoPerimeterToCloseOverException | NotEnoughAvailableExtrusionException | PostProcessingError ex)
        {
            throw new RuntimeException("Failed to process retract on layer " + thisLayer.getLayerNumber() + " this will affect open and close", ex);
        }

        return succeeded;
    }
}
