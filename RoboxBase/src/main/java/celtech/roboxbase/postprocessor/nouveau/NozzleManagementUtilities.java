package celtech.roboxbase.postprocessor.nouveau;

import celtech.roboxbase.configuration.RoboxProfile;
import celtech.roboxbase.configuration.fileRepresentation.HeadFile;
import celtech.roboxbase.postprocessor.NozzleProxy;
import celtech.roboxbase.postprocessor.nouveau.nodes.FillSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.InnerPerimeterSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.LayerNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.NozzleValvePositionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.OuterPerimeterSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.SectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.SkinSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.SkirtSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.SupportInterfaceSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.SupportSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.ToolSelectNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Renderable;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Ian
 */
public class NozzleManagementUtilities
{

    private final List<NozzleProxy> nozzleProxies;
    private final RoboxProfile settingsProfile;
    private final HeadFile headFile;

    public NozzleManagementUtilities(List<NozzleProxy> nozzleProxies,
            RoboxProfile settingsProfile,
            HeadFile headFile)
    {
        this.nozzleProxies = nozzleProxies;
        this.settingsProfile = settingsProfile;
        this.headFile = headFile;
    }

    protected NozzleProxy chooseNozzleProxyForDifferentialSupportMaterial(final GCodeEventNode node,
            final NozzleProxy supportMaterialNozzle,
            final NozzleProxy nozzleForCurrentObject) throws UnableToFindSectionNodeException
    {
        NozzleProxy nozzleProxy = null;

        //Go up through the parents until we either reach the top or find a section node
        GCodeEventNode foundNode = null;
        GCodeEventNode searchNode = node;

        do
        {
            if (searchNode instanceof SectionNode)
            {
                foundNode = searchNode;
                break;
            } else
            {
                if (searchNode.hasParent())
                {
                    searchNode = searchNode.getParent().get();
                }
            }
        } while (searchNode.hasParent());

        if (foundNode == null)
        {
            String outputMessage;
            if (node instanceof Renderable)
            {
                outputMessage = "Unable to find section parent of " + ((Renderable) node).renderForOutput();
            } else
            {
                outputMessage = "Unable to find section parent of " + node.toString();
            }
            throw new UnableToFindSectionNodeException(outputMessage);
        }

        if (foundNode instanceof SupportSectionNode
                || foundNode instanceof SupportInterfaceSectionNode
                || foundNode instanceof SkirtSectionNode)
        {
            nozzleProxy = supportMaterialNozzle;
        } else
        {
            nozzleProxy = nozzleForCurrentObject;
        }

        return nozzleProxy;
    }

    protected NozzleProxy chooseNozzleProxyByTask(final GCodeEventNode node) throws UnableToFindSectionNodeException
    {
        NozzleProxy nozzleProxy = null;

        //Go up through the parents until we either reach the top or find a section node
        GCodeEventNode foundNode = null;
        GCodeEventNode searchNode = node;

        do
        {
            if (searchNode instanceof SectionNode)
            {
                foundNode = searchNode;
                break;
            } else
            {
                if (searchNode.hasParent())
                {
                    searchNode = searchNode.getParent().get();
                }
            }
        } while (searchNode.hasParent());

        if (foundNode == null)
        {
            String outputMessage;
            if (node instanceof Renderable)
            {
                outputMessage = "Unable to find section parent of " + ((Renderable) node).renderForOutput();
            } else
            {
                outputMessage = "Unable to find section parent of " + node.toString();
            }
            throw new UnableToFindSectionNodeException(outputMessage);
        }

        if (foundNode instanceof FillSectionNode)
        {
            nozzleProxy = nozzleProxies.get(settingsProfile.getSpecificIntSetting("fillNozzle"));
        } else if (foundNode instanceof OuterPerimeterSectionNode)
        {
            nozzleProxy = nozzleProxies.get(settingsProfile.getSpecificIntSetting("perimeterNozzle"));
        } else if (foundNode instanceof InnerPerimeterSectionNode)
        {
            nozzleProxy = nozzleProxies.get(settingsProfile.getSpecificIntSetting("perimeterNozzle"));
        } else if (foundNode instanceof SupportSectionNode)
        {
            nozzleProxy = nozzleProxies.get(settingsProfile.getSpecificIntSetting("supportNozzle"));
        } else if (foundNode instanceof SupportInterfaceSectionNode)
        {
            nozzleProxy = nozzleProxies.get(settingsProfile.getSpecificIntSetting("supportInterfaceNozzle"));
        } else if (foundNode instanceof SkinSectionNode)
        {
            nozzleProxy = nozzleProxies.get(settingsProfile.getSpecificIntSetting("fillNozzle"));
        } else
        {
            nozzleProxy = nozzleProxies.get(settingsProfile.getSpecificIntSetting("fillNozzle"));
        }

        return nozzleProxy;
    }

    protected Optional<NozzleProxy> determineNozzleStateAtEndOfLayer(LayerNode layerNode)
    {
        Optional<NozzleProxy> nozzleInUse = Optional.empty();

        Iterator<GCodeEventNode> layerIterator = layerNode.childBackwardsIterator();

        search:
        while (layerIterator.hasNext())
        {
            GCodeEventNode potentialToolSelectNode = layerIterator.next();

            if (potentialToolSelectNode instanceof ToolSelectNode)
            {
                ToolSelectNode lastToolSelect = (ToolSelectNode) potentialToolSelectNode;

                Iterator<GCodeEventNode> toolSelectChildIterator = lastToolSelect.childrenAndMeBackwardsIterator();
                while (toolSelectChildIterator.hasNext())
                {
                    GCodeEventNode potentialNozzleValvePositionNode = toolSelectChildIterator.next();

                    if (potentialNozzleValvePositionNode instanceof NozzleValvePositionNode)
                    {
                        NozzleValvePositionNode nozzleNode = (NozzleValvePositionNode) potentialNozzleValvePositionNode;
                        NozzleProxy proxy = nozzleProxies.get(lastToolSelect.getToolNumber());
                        proxy.setCurrentPosition(nozzleNode.getNozzlePosition().getB());
                        nozzleInUse = Optional.of(proxy);
                        break search;
                    }
                }
            }
        }

        return nozzleInUse;
    }
}
