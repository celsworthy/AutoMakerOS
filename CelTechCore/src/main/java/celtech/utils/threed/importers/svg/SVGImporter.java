package celtech.utils.threed.importers.svg;

import celtech.modelcontrol.ShapeContainer;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.parser.AWTTransformProducer;
import org.apache.batik.parser.PathParser;
import org.apache.batik.parser.PointsParser;
import org.apache.batik.parser.TransformListParser;
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
    private final SVGConverterConfiguration converterConfiguration;
    private double viewBoxOriginX = 0.0;
    private double viewBoxOriginY = 0.0;
    private double viewBoxWidth = 0.0;
    private double viewBoxHeight = 0.0;
    private double documentWidth = 0.0;
    private double documentHeight = 0.0;
    private File modelFile = null;
    private SVGOMDocument svgDocument = null;

    public SVGImporter() {
        converterConfiguration = SVGConverterConfiguration.getInstance(); 
    }
    
    public ModelLoadResult loadFile(ModelLoaderTask parentTask, File modelFile,
            DoubleProperty percentProgressProperty)
    {
        this.parentTask = parentTask;
        this.percentProgressProperty = percentProgressProperty;
        this.modelFile = modelFile;
        
        List<Shape> shapes = new ArrayList<>();

        try
        {
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
            String modelURL = modelFile.toURI().toString();
            svgDocument = (SVGOMDocument)f.createDocument(modelURL);

// This used to build a graphics tree for the SVG, which had the side effect
// of populating the document with SVG contexts, so that things like lengths
// can be parsed. Unfortunately, it works with AWT, converting paths
// to AWT Shapes. This makes it less useful than one might expect. So we work
// with the XML document.
//            UserAgent userAgent = new UserAgentAdapter();
//            DocumentLoader loader = new DocumentLoader(userAgent);
//            BridgeContext bridgeContext = new BridgeContext(userAgent, loader);
//            bridgeContext.setDynamicState(BridgeContext.DYNAMIC);
//
//            // Enable CSS- and SVG-specific enhancements.
//            GVTBuilder gvtBuilder = new GVTBuilder();
//            try
//            {
//                gvtBuilder.build(bridgeContext, doc);
//            } catch (RuntimeException e)
//            {
//                steno.exception("Exception building svg tree", e);
//            }

            getDocumentDimensions(svgDocument);
            NodeList paths = svgDocument.getElementsByTagName("path");
            NodeList rects = svgDocument.getElementsByTagName("rect");
            NodeList polygons = svgDocument.getElementsByTagName("polygon");

            buildPathShapes(paths, shapes);
            buildRectangleShapes(rects, shapes);
            buildPolygonShapes(polygons, shapes);
        } catch (IOException ex)
        {
            steno.exception("Failed to process SVG file " + modelFile.getAbsolutePath(), ex);
        }

        ShapeContainer renderableSVG = new ShapeContainer(modelFile.getName(), shapes);
        renderableSVG.setViewBoxTransform(viewBoxOriginX, viewBoxOriginY,
                                          viewBoxWidth, viewBoxHeight,
                                          documentWidth, documentHeight);

//        renderableSVG.setMetaparts(metaparts);
        Set<ProjectifiableThing> renderableSVGs = new HashSet<>();
        renderableSVGs.add(renderableSVG);

        ModelLoadResult result = new ModelLoadResult(
                ModelLoadResultType.SVG,
                modelFile.getAbsolutePath(),
                modelFile.getName(),
                renderableSVGs);
        
        this.parentTask = null;
        this.percentProgressProperty = null;
        this.modelFile = null;
        this.svgDocument = null;
        this.documentWidth = 0.0f;
        this.documentHeight = 0.0f;
        this.viewBoxWidth = 0.0f;
        this.viewBoxHeight = 0.0f;
        this.viewBoxOriginX = 0.0f;
        this.viewBoxOriginY = 0.0f;
        
        return result;
    }

    private void getDocumentDimensions(SVGOMDocument doc)
    {
        try
        {
            NodeList svgData = doc.getElementsByTagName("svg");
            Node svgRoot = svgData.item(0);
            NamedNodeMap svgAttributes = svgRoot.getAttributes();

            Node viewBoxNode = svgAttributes.getNamedItem("viewBox");

            if (viewBoxNode != null &&
                viewBoxNode.getNodeValue() != null)
            {
                String viewBoxString = viewBoxNode.getNodeValue();
                String[] viewBoxParts = viewBoxString.split(" ");
                if (viewBoxParts.length == 4)
                {
                    // For the moment, ignore viewbox offset.
                    viewBoxOriginX = Double.valueOf(viewBoxParts[0]);
                    viewBoxOriginY = Double.valueOf(viewBoxParts[1]);
                    viewBoxWidth = Double.valueOf(viewBoxParts[2]);
                    viewBoxHeight = Double.valueOf(viewBoxParts[3]);
                } else
                {
                    steno.warning("Got viewBox directive but had wrong number of parts");
                }
            }

            Node widthNode = svgAttributes.getNamedItem("width");
            if (widthNode != null &&
                widthNode.getNodeValue() != null)
            {
                // Get the document dimensions in mm.
                // Not sure what to do if units are percentages!
                String widthString = widthNode.getNodeValue();
                Units widthUnits = Units.getUnitType(widthString);
                documentWidth = Double.valueOf(widthString.replaceAll("[a-zA-Z]+", "")) * widthUnits.getConversionFactor(); 
            }
            
            Node heightNode = svgAttributes.getNamedItem("height");
            if (heightNode != null &&
                heightNode.getNodeValue() != null)
            {
                // Get the document dimensions in mm.
                // Not sure what to do if units are percentages!
                String heightString = heightNode.getNodeValue();
                Units heightUnits = Units.getUnitType(heightString);
                documentHeight = Double.valueOf(heightString.replaceAll("[a-zA-Z]+", "")) * heightUnits.getConversionFactor();
            }
            
            if (documentWidth <= 0.0 && viewBoxWidth > 0.0)
                documentWidth = viewBoxWidth;
            else if (viewBoxWidth <= 0.0 && documentWidth > 0.0)
                viewBoxWidth = documentWidth;
            if (documentHeight <= 0.0 && viewBoxHeight > 0.0)
                documentHeight = viewBoxHeight;
            else if (viewBoxHeight <= 0.0 && documentHeight > 0.0)
                viewBoxHeight = documentHeight;
        }
        catch (Exception ex)
        {
            steno.exception("Failed to process svg attributes in SVG file " + modelFile.getAbsolutePath(), ex);
        }
    }
    
    private void buildPathShapes(NodeList paths, List<Shape> shapes)
    {
//        PathParserThing parserThing = new PathParserThing(metaparts);
//            
//        PathParser pathParser = new PathParser();
//        PathParser.setPathHandler(parserThing);
        for (int pathIndex = 0; pathIndex < paths.getLength(); pathIndex++)
        {
            Node pathNode = paths.item(pathIndex);
            NamedNodeMap nodeMap = pathNode.getAttributes();
            Node dNode = nodeMap.getNamedItem("d");
            if (dNode != null)
            {
//                System.out.println(dNode.getNodeValue());
                SVGPath displayablePath = new SVGPath();
                displayablePath.setContent(dNode.getNodeValue());
                getAllTransforms(pathNode, displayablePath.getTransforms());

                shapes.add(displayablePath);
            }
        }

    }
    
    private void buildRectangleShapes(NodeList rects, List<Shape> shapes)
    {
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
            float yValue = ((float)viewBoxHeight - Float.valueOf(yNode.getNodeValue())) * converterConfiguration.getyPointCoefficient();
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
            getAllTransforms(rectNode, displayablePath.getTransforms());

            shapes.add(displayablePath);
        }
    }
    
    private void buildPolygonShapes(NodeList polygons, List<Shape> shapes)
    {
//        PointsParser pp = new PointsParser();
//        PointParserThing pointParserThing = new PointParserThing(metaparts);
//        pp.setPointsHandler(pointParserThing);
        for (int polygonIndex = 0; polygonIndex < polygons.getLength(); polygonIndex++)
        {
            Node polygonNode = polygons.item(polygonIndex);
            NamedNodeMap nodeMap = polygonNode.getAttributes();

            Node points = nodeMap.getNamedItem("points");

//            pp.parse(points.getNodeValue());
            Polygon displayablePoly = new Polygon();
            String[] pointPairs = points.getNodeValue().split(" ");
            for (String pointPair : pointPairs)
            {
                String[] pointPairSplit = pointPair.split(",");
                Double x = Double.valueOf(pointPairSplit[0]);
                Double y = Double.valueOf(pointPairSplit[1]);
                displayablePoly.getPoints().addAll(x * converterConfiguration.getxPointCoefficient(),
                                                   y * converterConfiguration.getyPointCoefficient());
            }

            getAllTransforms(polygonNode, displayablePoly.getTransforms());

            shapes.add(displayablePoly);
//            renderableSVG.getChildren().add(displayablePoly);
        }
    }
    
    private void getAllTransforms(Node gNode, List<Transform> transforms)
    {
        Node n = gNode;
        while (n != null)
        {
            NamedNodeMap nodeMap = n.getAttributes();
            if (nodeMap != null)
            {
                Node tNode = nodeMap.getNamedItem("transform");
                if (tNode != null)
                {
                    try
                    {
                        Affine t = JFXTransformProducer.createAffineTransform(tNode.getNodeValue());
                        transforms.add(t);
                    }
                    catch (ParseException ex)
                    {
                        steno.exception("Failed to parse transform attribute", ex);
                    }
                }
            }
            n = n.getParentNode();
        }
        Collections.reverse(transforms);
    }

}
