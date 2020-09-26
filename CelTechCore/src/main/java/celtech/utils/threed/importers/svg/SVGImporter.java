package celtech.utils.threed.importers.svg;

import celtech.roboxbase.importers.twod.svg.SVGConverterConfiguration;
import celtech.roboxbase.importers.twod.svg.PointParserThing;
import celtech.roboxbase.importers.twod.svg.PathParserThing;
import celtech.coreUI.visualisation.metaparts.ModelLoadResult;
import celtech.coreUI.visualisation.metaparts.ModelLoadResultType;
import celtech.modelcontrol.ProjectifiableThing;

import celtech.services.modelLoader.ModelLoaderTask;
import celtech.roboxbase.importers.twod.svg.metadata.SVGMetaPart;
import celtech.roboxbase.importers.twod.svg.metadata.Units;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javafx.beans.property.DoubleProperty;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.parser.PathParser;
import org.apache.batik.parser.PointsParser;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class SVGImporter
{

    private final Stenographer steno = StenographerFactory.getStenographer(SVGImporter.class.getName());
    private ModelLoaderTask parentTask = null;
    private DoubleProperty percentProgressProperty = null;

    public ModelLoadResult loadFile(ModelLoaderTask parentTask, File modelFile,
            DoubleProperty percentProgressProperty)
    {
        this.parentTask = parentTask;
        this.percentProgressProperty = percentProgressProperty;

        List<Shape> shapes = new ArrayList<>();

        try
        {
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
            String modelURL = modelFile.toURI().toString();
            Document doc = f.createDocument(modelURL);

            UserAgent userAgent = new UserAgentAdapter();
            DocumentLoader loader = new DocumentLoader(userAgent);
            BridgeContext bridgeContext = new BridgeContext(userAgent, loader);
            bridgeContext.setDynamicState(BridgeContext.DYNAMIC);

            // Enable CSS- and SVG-specific enhancements.
            GVTBuilder gvtBuilder = new GVTBuilder();
//            try
//            {
//                gvtBuilder.build(bridgeContext, doc);
//            } catch (RuntimeException e)
//            {
//                steno.exception("Exception building svg tree", e);
//            }

            SVGConverterConfiguration converterConfiguration = SVGConverterConfiguration.getInstance();

            NodeList svgData = doc.getElementsByTagName("svg");
            NamedNodeMap svgAttributes = svgData.item(0).getAttributes();
            String widthString = svgAttributes.getNamedItem("width").getNodeValue();
            Units fileUnits = Units.getUnitType(widthString);
            float documentWidth = Float.valueOf(widthString.replaceAll("[a-zA-Z]+", ""));
            String heightString = svgAttributes.getNamedItem("height").getNodeValue();
            float documentHeight = Float.valueOf(heightString.replaceAll("[a-zA-Z]+", ""));

            float viewBoxOriginX = 0;
            float viewBoxOriginY = 0;
            float viewBoxWidth = 0;
            float viewBoxHeight = 0;

            Node viewBoxNode = svgAttributes.getNamedItem("viewBox");
            if (viewBoxNode != null
                    && viewBoxNode.getNodeValue() != null)
            {
                String viewBoxString = viewBoxNode.getNodeValue();
                String[] viewBoxParts = viewBoxString.split(" ");
                if (viewBoxParts.length == 4)
                {
                    viewBoxOriginX = Float.valueOf(viewBoxParts[0]);
                    viewBoxOriginY = Float.valueOf(viewBoxParts[1]);
                    viewBoxWidth = Float.valueOf(viewBoxParts[2]);
                    viewBoxHeight = Float.valueOf(viewBoxParts[3]);

                    converterConfiguration.setxPointCoefficient(documentWidth / viewBoxWidth);
                    converterConfiguration.setyPointCoefficient(documentHeight / viewBoxHeight);
                } else
                {
                    steno.warning("Got viewBox directive but had wrong number of parts");
                }
            }

            NodeList paths = doc.getElementsByTagName("path");
            NodeList rects = doc.getElementsByTagName("rect");
            NodeList polygons = doc.getElementsByTagName("polygon");

//            PathParserThing parserThing = new PathParserThing(metaparts);
//            
//            PathParser pathParser = new PathParser();
//            pathParser.setPathHandler(parserThing);
            for (int pathIndex = 0; pathIndex < paths.getLength(); pathIndex++)
            {
                Node pathNode = paths.item(pathIndex);
                NamedNodeMap nodeMap = pathNode.getAttributes();
                Node dNode = nodeMap.getNamedItem("d");
                System.out.println(dNode.getNodeValue());
//                pathParser.parse(dNode.getNodeValue());
                SVGPath displayablePath = new SVGPath();
//                displayablePath.scaleXProperty().set(converterConfiguration.getxPointCoefficient());
//                displayablePath.scaleYProperty().set(converterConfiguration.getyPointCoefficient());
//                displayablePath.
                displayablePath.setContent(dNode.getNodeValue());
                shapes.add(displayablePath);
            }

            NumberFormat threeDPformatter = DecimalFormat.getNumberInstance(Locale.UK);
            threeDPformatter.setMaximumFractionDigits(3);
            threeDPformatter.setGroupingUsed(false);

            for (int rectIndex = 0; rectIndex < rects.getLength(); rectIndex++)
            {
                Node rectNode = rects.item(rectIndex);
                NamedNodeMap nodeMap = rectNode.getAttributes();
                Node xNode = nodeMap.getNamedItem("x");
                Node yNode = nodeMap.getNamedItem("y");
                Node wNode = nodeMap.getNamedItem("width");
                Node hNode = nodeMap.getNamedItem("height");

                float xValue = Float.valueOf(xNode.getNodeValue()) * converterConfiguration.getxPointCoefficient();
                float yValue = (viewBoxHeight - Float.valueOf(yNode.getNodeValue())) * converterConfiguration.getyPointCoefficient();
                float wValue = Float.valueOf(wNode.getNodeValue()) * converterConfiguration.getxPointCoefficient();
                float hValue = Float.valueOf(hNode.getNodeValue()) * converterConfiguration.getyPointCoefficient();

                String synthPath = 'm'
                        + threeDPformatter.format(xValue) + ','
                        + threeDPformatter.format(yValue) + ' '
                        + threeDPformatter.format(wValue) + ','
                        + 0 + ' '
                        + 0 + ','
                        + threeDPformatter.format(hValue) + ' '
                        + (threeDPformatter.format(-wValue)) + ','
                        + 0 + ' '
                        + 0 + ','
                        + (threeDPformatter.format(-hValue)) + ' ';

//                pathParser.parse(synthPath);
                SVGPath displayablePath = new SVGPath();
                displayablePath.setContent(synthPath);
                shapes.add(displayablePath);
            }

//            PointsParser pp = new PointsParser();
//            PointParserThing pointParserThing = new PointParserThing(metaparts);
//            pp.setPointsHandler(pointParserThing);
            for (int polygonIndex = 0; polygonIndex < polygons.getLength(); polygonIndex++)
            {
                Node polygonNode = polygons.item(polygonIndex);
                NamedNodeMap nodeMap = polygonNode.getAttributes();

                Node points = nodeMap.getNamedItem("points");

//                pp.parse(points.getNodeValue());
                Polygon displayablePoly = new Polygon();
                String[] pointPairs = points.getNodeValue().split(" ");
                for (String pointPair : pointPairs)
                {
                    String[] pointPairSplit = pointPair.split(",");
                    Double x = Double.valueOf(pointPairSplit[0]);
                    Double y = Double.valueOf(pointPairSplit[1]);
                    displayablePoly.getPoints().addAll(x * converterConfiguration.getxPointCoefficient(), y * converterConfiguration.getyPointCoefficient());
                }

                shapes.add(displayablePoly);
//                renderableSVG.getChildren().add(displayablePoly);
            }
        } catch (IOException ex)
        {
            steno.exception("Failed to process SVG file " + modelFile.getAbsolutePath(), ex);
        }

        ShapeContainer renderableSVG = new ShapeContainer(modelFile.getName(), shapes);

//        renderableSVG.setMetaparts(metaparts);
        Set<ProjectifiableThing> renderableSVGs = new HashSet<>();
        renderableSVGs.add(renderableSVG);

        return new ModelLoadResult(
                ModelLoadResultType.SVG,
                modelFile.getAbsolutePath(),
                modelFile.getName(),
                renderableSVGs);
    }
}
