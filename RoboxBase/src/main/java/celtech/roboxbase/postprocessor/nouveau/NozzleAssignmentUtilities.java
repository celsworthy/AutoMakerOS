package celtech.roboxbase.postprocessor.nouveau;

import celtech.roboxbase.configuration.RoboxProfile;
import celtech.roboxbase.configuration.fileRepresentation.HeadFile;
import celtech.roboxbase.postprocessor.NozzleProxy;
import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.LayerNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.ObjectDelineationNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.OrphanSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.RetractNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.SectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.SkirtSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.SupportInterfaceSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.SupportSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.ToolSelectNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.ExtrusionProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class NozzleAssignmentUtilities
{

    private final Stenographer steno = StenographerFactory.getStenographer(NozzleAssignmentUtilities.class.getName());
    private final RoboxProfile settingsProfile;
    private final HeadFile headFile;
    private final PostProcessorFeatureSet featureSet;
    private final PostProcessingMode postProcessingMode;

    private final NozzleManagementUtilities nozzleControlUtilities;
    private final Map<Integer, Integer> objectToNozzleNumberMap;

    public NozzleAssignmentUtilities(List<NozzleProxy> nozzleProxies,
            RoboxProfile settingsProfile,
            HeadFile headFile,
            PostProcessorFeatureSet featureSet,
            PostProcessingMode postProcessingMode,
            Map<Integer, Integer> objectToNozzleNumberMap)
    {
        this.settingsProfile = settingsProfile;
        this.headFile = headFile;
        this.featureSet = featureSet;
        this.postProcessingMode = postProcessingMode;
        this.objectToNozzleNumberMap = objectToNozzleNumberMap;

        nozzleControlUtilities = new NozzleManagementUtilities(nozzleProxies,
                settingsProfile,
                headFile);
    }

    public class ExtrusionAssignmentResult
    {

        private final double eVolume;
        private final double dVolume;

        public ExtrusionAssignmentResult(double eVolume, double dVolume)
        {
            this.eVolume = eVolume;
            this.dVolume = dVolume;
        }

        public double getEVolume()
        {
            return eVolume;
        }

        public double getDVolume()
        {
            return dVolume;
        }
    }

    protected ExtrusionAssignmentResult assignExtrusionToCorrectExtruder(LayerNode layerNode)
    {
        ExtrusionAssignmentResult returnValue = null;
        double eUsed = 0;
        double dUsed = 0;

        //Tool select nodes live directly under layers
        Iterator<GCodeEventNode> layerIterator = layerNode.childIterator();

        while (layerIterator.hasNext())
        {
            GCodeEventNode potentialToolSelectNode = layerIterator.next();

            if (potentialToolSelectNode instanceof ToolSelectNode)
            {
                ToolSelectNode toolSelectNode = (ToolSelectNode) potentialToolSelectNode;

                Iterator<GCodeEventNode> toolSelectNodeIterator = toolSelectNode.treeSpanningIterator(
                        null);

                while (toolSelectNodeIterator.hasNext())
                {
                    GCodeEventNode potentialExtrusionProvider = toolSelectNodeIterator.next();

                    if (potentialExtrusionProvider instanceof ExtrusionProvider)
                    {
                        ExtrusionProvider extrusionNode = (ExtrusionProvider) potentialExtrusionProvider;

                        // Don't change anything if we're in task-based selection as this always uses extruder E
                        switch (postProcessingMode)
                        {
                            case LEAVE_TOOL_CHANGES_ALONE_DUAL:
                            case SUPPORT_IN_FIRST_MATERIAL:
                            case SUPPORT_IN_SECOND_MATERIAL:
                                switch (toolSelectNode.getToolNumber())
                                {
                                    case 1:
                                        extrusionNode.getExtrusion().extrudeUsingEOnly();
                                        break;
                                    case 0:
                                        extrusionNode.getExtrusion().extrudeUsingDOnly();
                                        break;
                                }
                                break;
                            case FORCED_USE_OF_E_EXTRUDER:
                                extrusionNode.getExtrusion().extrudeUsingEOnly();
                                break;
                            case FORCED_USE_OF_D_EXTRUDER:
                                extrusionNode.getExtrusion().extrudeUsingDOnly();
                                break;
                            default:
                                break;
                        }

                        eUsed += (extrusionNode.getExtrusion().isEInUse()) ? extrusionNode.getExtrusion().getE() : 0;
                        dUsed += (extrusionNode.getExtrusion().isDInUse()) ? extrusionNode.getExtrusion().getD() : 0;
                    }
                }
            }
        }

        return new ExtrusionAssignmentResult(eUsed, dUsed);
    }

    protected int insertNozzleControlSectionsByObject(LayerNode layerNode,
            LayerPostProcessResult lastLayerResult)
    {
        List<GCodeEventNode> nodesToRemove = new ArrayList<>();

        int lastObjectReferenceNumber = -1;

        if (lastLayerResult != null)
        {
            Optional<Integer> potentialLastObjNum = lastLayerResult.getLastObjectNumber();
            if (potentialLastObjNum.isPresent())
            {
                lastObjectReferenceNumber = potentialLastObjNum.get();
            }
        }

        Iterator<GCodeEventNode> layerChildIterator = layerNode.childIterator();
        List<ObjectDelineationNode> objectNodes = new ArrayList<>();

        while (layerChildIterator.hasNext())
        {
            GCodeEventNode nodeBeingExamined = layerChildIterator.next();

            if (nodeBeingExamined instanceof ObjectDelineationNode)
            {
                objectNodes.add((ObjectDelineationNode) nodeBeingExamined);
            }
        }

        int objectReferenceNumber = -1;
        //We'll need at least one of these per layer
        ToolSelectNode toolSelectNode = null;

        SectionNode lastSectionNode = null;

        for (ObjectDelineationNode objectNode : objectNodes)
        {
            objectReferenceNumber = objectNode.getObjectNumber();
            // Add retract node to end of previous object section (before tool change)
            if (lastObjectReferenceNumber != -1
                    && lastObjectReferenceNumber != objectReferenceNumber
                    && lastSectionNode != null
                    && (lastSectionNode.getChildren().isEmpty()
                    || !(lastSectionNode.getChildren().get(lastSectionNode.getChildren().size() - 1) instanceof RetractNode)))
            {
                lastSectionNode.addChildAtEnd(new RetractNode());
            }

            lastObjectReferenceNumber = objectReferenceNumber;

            //Object numbers correspond to extruder numbers
            ObjectDelineationNode objectNodeBeingExamined = (ObjectDelineationNode) objectNode;

            objectNodeBeingExamined.removeFromParent();

            List<GCodeEventNode> objectChildren = objectNodeBeingExamined.getChildren().stream().collect(
                    Collectors.toList());

            for (GCodeEventNode childNode : objectChildren)
            {
                if (childNode instanceof SectionNode)
                {
                    SectionNode sectionUnderConsideration = (SectionNode) childNode;
                    int requiredToolNumber = -1;

                    if (sectionUnderConsideration instanceof OrphanSectionNode)
                    {
                        if (lastSectionNode == null)
                        {
                            //Try to get the section from the last layer
                            if (lastLayerResult != null && lastLayerResult.getLayerData() != null)
                            {
                                GCodeEventNode lastEventOnLastLayer = lastLayerResult.getLayerData().getAbsolutelyTheLastEvent();
                                if (lastEventOnLastLayer != null)
                                {
                                    Optional<GCodeEventNode> potentialLastSection = lastEventOnLastLayer.getParent();
                                    if (potentialLastSection.isPresent()
                                            && potentialLastSection.get() instanceof SectionNode)
                                    {
                                        lastSectionNode = (SectionNode) potentialLastSection.get();
                                    }
                                }
                            }

                            if (lastSectionNode == null)
                            {
                                if (postProcessingMode == PostProcessingMode.LEAVE_TOOL_CHANGES_ALONE_DUAL)
                                {
                                    requiredToolNumber = objectReferenceNumber;
                                } else
                                {
                                    requiredToolNumber = 0;
                                    steno.warning("The tool number could not be determined on layer " + layerNode.getLayerNumber());
                                }
                            } else
                            {
                                if (lastSectionNode.getParent() != null
                                        && (lastSectionNode.getParent().get() instanceof ObjectDelineationNode))
                                {
                                    objectReferenceNumber = ((ObjectDelineationNode) lastSectionNode.getParent().get()).getObjectNumber();
                                } else if (lastSectionNode.getParent() != null
                                        && (lastSectionNode.getParent().get() instanceof ToolSelectNode))
                                {
                                    requiredToolNumber = ((ToolSelectNode) lastSectionNode.getParent().get()).getToolNumber();
                                } else
                                {
                                    throw new RuntimeException(
                                            "Couldn't determine prior object for orphan section on layer "
                                            + layerNode.getLayerNumber() + " as last section didn't exist");
                                }
                            }
                        }

                        try
                        {
                            SectionNode replacementSection = null;
                            if (lastSectionNode == null)
                            {
                                replacementSection = new SkirtSectionNode();
                            } else
                            {
                                replacementSection = lastSectionNode.getClass().newInstance();
                            }
                            List<GCodeEventNode> sectionChildren = new ArrayList<>();
                            Iterator<GCodeEventNode> sectionChildrenIterator = sectionUnderConsideration.childIterator();

                            while (sectionChildrenIterator.hasNext())
                            {
                                GCodeEventNode sectionChildNode = sectionChildrenIterator.next();
                                sectionChildren.add(sectionChildNode);
                            }

                            for (GCodeEventNode sectionChildNode : sectionChildren)
                            {
                                sectionChildNode.removeFromParent();
                                replacementSection.addChildAtEnd(sectionChildNode);
                            }

                            nodesToRemove.add(sectionUnderConsideration);
                            sectionUnderConsideration = replacementSection;
                        } catch (InstantiationException | IllegalAccessException ex)
                        {
                            throw new RuntimeException("Failed to process orphan section on layer "
                                    + layerNode.getLayerNumber(), ex);
                        }
                    }

                    if (requiredToolNumber < 0)
                    {
                        //Tool number corresponds to nozzle number
                        if (postProcessingMode == PostProcessingMode.TASK_BASED_NOZZLE_SELECTION)
                        {
                            //Assuming that we'll only be here with a single material nozzle
                            //In this case nozzle 0 corresponds to tool 0
                            if (layerNode.getLayerNumber() == 0)
                            {
                                int notionalNozzleNumber = settingsProfile.getSpecificIntSetting("firstLayerNozzle");
                                if (notionalNozzleNumber >= 0
                                        && notionalNozzleNumber <= 1)
                                {
                                    requiredToolNumber = notionalNozzleNumber;
                                } else
                                {
                                    requiredToolNumber = 0;
                                }
                            } else
                            {
                                try
                                {
                                    NozzleProxy requiredNozzle = nozzleControlUtilities.chooseNozzleProxyByTask(sectionUnderConsideration);
                                    requiredToolNumber = requiredNozzle.getNozzleReferenceNumber();
                                } catch (UnableToFindSectionNodeException ex)
                                {
                                    throw new RuntimeException("Failed to determine correct nozzle - single material mode");
                                }
                            }
                        } else if ((postProcessingMode == PostProcessingMode.SUPPORT_IN_FIRST_MATERIAL
                                || postProcessingMode == PostProcessingMode.SUPPORT_IN_SECOND_MATERIAL)
                                && ((sectionUnderConsideration instanceof SupportSectionNode)
                                || (sectionUnderConsideration instanceof SupportInterfaceSectionNode)
                                || (sectionUnderConsideration instanceof SkirtSectionNode)))
                        {
                            requiredToolNumber = (postProcessingMode == PostProcessingMode.SUPPORT_IN_FIRST_MATERIAL) ? 1 : 0;
                        } else if (postProcessingMode == PostProcessingMode.LEAVE_TOOL_CHANGES_ALONE_DUAL
                                || postProcessingMode == PostProcessingMode.LEAVE_TOOL_CHANGES_ALONE_SINGLE) 
                        {
                            requiredToolNumber = objectReferenceNumber;
                        } else if (postProcessingMode == PostProcessingMode.FORCED_USE_OF_D_EXTRUDER)
                        {
                            requiredToolNumber = 0;
                        } else if (postProcessingMode == PostProcessingMode.FORCED_USE_OF_E_EXTRUDER)
                        {
                            requiredToolNumber = 1;
                        } else
                        {
                            requiredToolNumber = objectToNozzleNumberMap.get(objectReferenceNumber);
                        }
                    }

                    if (toolSelectNode == null
                            || toolSelectNode.getToolNumber() != requiredToolNumber)
                    {
                        //Need to create a new Tool Select Node
                        toolSelectNode = new ToolSelectNode();
                        toolSelectNode.setToolNumber(requiredToolNumber);
                        layerNode.addChildAtEnd(toolSelectNode);
                    }

                    sectionUnderConsideration.removeFromParent();
                    toolSelectNode.addChildAtEnd(sectionUnderConsideration);
                    lastSectionNode = sectionUnderConsideration;
                } else
                {
                    //Probably a travel node - move it over without changing it
                    childNode.removeFromParent();

                    if (toolSelectNode == null)
                    {
                        //Need to create a new Tool Select Node
                        //At this stage all we can do is look at the object number
                        toolSelectNode = new ToolSelectNode();
                        toolSelectNode.setToolNumber(objectToNozzleNumberMap.get(objectReferenceNumber));
                        layerNode.addChildAtEnd(toolSelectNode);
                    }
                    toolSelectNode.addChildAtEnd(childNode);
                }
            }

            for (GCodeEventNode node : nodesToRemove)
            {
                node.removeFromParent();
            }
        }
        return objectReferenceNumber;
    }
}
