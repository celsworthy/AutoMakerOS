package celtech.roboxbase.utils.exporters;

import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.utils.models.MeshForProcessing;
import celtech.roboxbase.utils.threed.CentreCalculations;
import celtech.roboxbase.utils.threed.MeshToWorldTransformer;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.collections.ObservableFloatArray;
import javafx.geometry.Point3D;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.ObservableFaceArray;
import javafx.scene.shape.TriangleMesh;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author ianhudson
 */
public class AMFOutputConverter implements MeshFileOutputConverter
{

    private Stenographer steno = StenographerFactory.getStenographer(AMFOutputConverter.class.
            getName());

    void outputProject(List<MeshForProcessing> meshesForProcessing, XMLStreamWriter streamWriter) throws XMLStreamException
    {
        Map<MeshView, Integer> vertexOffsetForModels = new HashMap<>();

        streamWriter.writeStartDocument();
        streamWriter.writeStartElement("amf");
        streamWriter.writeAttribute("unit", "millimeter");
        streamWriter.writeAttribute("version", "1.1");
        streamWriter.writeStartElement("object");
        streamWriter.writeAttribute("id", Integer.toString(0));
        streamWriter.writeStartElement("mesh");
        int vertexOffset = 0;
        streamWriter.writeStartElement("vertices");

        for (MeshForProcessing meshForProcessing : meshesForProcessing)
        {
            vertexOffsetForModels.put(meshForProcessing.getMeshView(), vertexOffset);
            int numVerticesWritten = outputVertices(meshForProcessing.getMeshToWorldTransformer(), meshForProcessing.getMeshView(), streamWriter);
            vertexOffset += numVerticesWritten;
        }

        streamWriter.writeEndElement();
        for (MeshForProcessing meshForProcessing : meshesForProcessing)
        {
            outputVolume(meshForProcessing.getMeshView(), vertexOffsetForModels.get(meshForProcessing.getMeshView()), streamWriter);
        }
        streamWriter.writeEndElement();
        streamWriter.writeEndElement();
        outputMaterials(streamWriter);
        streamWriter.writeEndElement();
        streamWriter.writeEndDocument();
        streamWriter.flush();
        streamWriter.close();
    }

    void outputMaterials(XMLStreamWriter streamWriter) throws XMLStreamException
    {
        outputMaterial(2, 0.1f, 0.1f, 0.1f, streamWriter);
        outputMaterial(3, 0f, 0.9f, 0.9f, Optional.of(0.5f), streamWriter);
    }

    void outputMaterial(int materialId, float r, float g, float b,
            XMLStreamWriter streamWriter) throws XMLStreamException
    {
        outputMaterial(2, 0.1f, 0.1f, 0.1f, Optional.empty(), streamWriter);
    }

    void outputMaterial(int materialId, float r, float g, float b,
            Optional<Float> a,
            XMLStreamWriter streamWriter) throws XMLStreamException
    {
        streamWriter.writeStartElement("material");
        streamWriter.writeAttribute("id", Integer.toString(materialId));
        streamWriter.writeStartElement("color");
        streamWriter.writeStartElement("r");
        streamWriter.writeCharacters(Float.toString(r));
        streamWriter.writeEndElement();
        streamWriter.writeStartElement("g");
        streamWriter.writeCharacters(Float.toString(g));
        streamWriter.writeEndElement();
        streamWriter.writeStartElement("b");
        streamWriter.writeCharacters(Float.toString(b));
        streamWriter.writeEndElement();
        if (a.isPresent())
        {
            streamWriter.writeStartElement("a");
            streamWriter.writeCharacters(Float.toString(a.get()));
            streamWriter.writeEndElement();
        }
        streamWriter.writeEndElement();
        streamWriter.writeEndElement();
    }

    void outputVolume(MeshView meshView, int vertexOffset, XMLStreamWriter streamWriter) throws XMLStreamException
    {
        streamWriter.writeStartElement("volume");
        streamWriter.writeAttribute("materialid", "2");

        TriangleMesh triMesh = (TriangleMesh) meshView.getMesh();

        ObservableFaceArray faces = triMesh.getFaces();
        for (int i = 0; i < faces.size(); i += 6)
        {
            outputFace(faces, i, vertexOffset, streamWriter);
        }

        streamWriter.writeEndElement();
    }

    /**
     * Write out the vertices and return the number of vertices written.
     */
    int outputVertices(MeshToWorldTransformer meshToWorldTransformer, MeshView meshView, XMLStreamWriter streamWriter) throws XMLStreamException
    {
        int numVertices = 0;

        TriangleMesh triMesh = (TriangleMesh) meshView.getMesh();

        ObservableFloatArray points = triMesh.getPoints();
        for (int i = 0; i < points.size(); i += 3)
        {
            outputVertex(meshToWorldTransformer, meshView, points, i, streamWriter);
            numVertices++;
        }

        return numVertices;
    }

    private void outputFace(ObservableFaceArray faces, int offset, int vertexOffset,
            XMLStreamWriter streamWriter) throws XMLStreamException
    {
        streamWriter.writeStartElement("triangle");
        streamWriter.writeStartElement("v1");
        streamWriter.writeCharacters(Integer.toString(faces.get(offset) + vertexOffset));
        streamWriter.writeEndElement();
        streamWriter.writeStartElement("v2");
        streamWriter.writeCharacters(Integer.toString(faces.get(offset + 2) + vertexOffset));
        streamWriter.writeEndElement();
        streamWriter.writeStartElement("v3");
        streamWriter.writeCharacters(Integer.toString(faces.get(offset + 4) + vertexOffset));
        streamWriter.writeEndElement();
        streamWriter.writeEndElement();
    }

    private void outputVertex(MeshToWorldTransformer meshToWorldTransformer, MeshView meshView, ObservableFloatArray points, int offset,
            XMLStreamWriter streamWriter) throws XMLStreamException
    {
        Point3D transformedVertex = meshToWorldTransformer.
                transformMeshToRealWorldCoordinates(
                        points.get(offset),
                        points.get(offset + 1),
                        points.get(offset + 2));
        streamWriter.writeStartElement("vertex");
        streamWriter.writeStartElement("coordinates");
        streamWriter.writeStartElement("x");
        streamWriter.writeCharacters(Float.toString((float) transformedVertex.getX()));
        streamWriter.writeEndElement();
        streamWriter.writeStartElement("y");
        streamWriter.writeCharacters(Float.toString((float) transformedVertex.getZ()));
        streamWriter.writeEndElement();
        streamWriter.writeStartElement("z");
        streamWriter.writeCharacters(Float.toString((float) -transformedVertex.getY()));
        streamWriter.writeEndElement();
        streamWriter.writeEndElement();
        streamWriter.writeEndElement();
    }

    @Override
    public MeshExportResult outputFile(List<MeshForProcessing> meshesForProcessing, String printJobUUID, boolean outputAsSingleFile)
    {
        return outputFile(meshesForProcessing, printJobUUID, BaseConfiguration.getPrintSpoolDirectory()
                + printJobUUID + File.separator, outputAsSingleFile);
    }

    @Override
    public MeshExportResult outputFile(List<MeshForProcessing> meshesForProcessing, String printJobUUID, String printJobDirectory,
            boolean outputAsSingleFile)
    {
        List<String> createdFiles = new ArrayList<>();
        List<Vector3D> centroids = new ArrayList<>();

        String tempModelFilenameWithPath = printJobDirectory + printJobUUID
                + BaseConfiguration.amfTempFileExtension;

        createdFiles.add(tempModelFilenameWithPath);

        try
        {
            FileWriter fileWriter = new FileWriter(tempModelFilenameWithPath);
            XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter xmlStreamWriter
                    = xmlOutputFactory.createXMLStreamWriter(fileWriter);

            // Ugly code off internet follows==
            PrettyPrintHandler handler = new PrettyPrintHandler(xmlStreamWriter);
            XMLStreamWriter prettyPrintWriter = (XMLStreamWriter) Proxy.newProxyInstance(
                    XMLStreamWriter.class.getClassLoader(),
                    new Class[]
                    {
                        XMLStreamWriter.class
                    },
                    handler);
            //=================================

            outputProject(meshesForProcessing, prettyPrintWriter);
            xmlStreamWriter.flush();
            xmlStreamWriter.close();
        } catch (IOException ex)
        {
            steno.error("Unable to write AMF file to given path: " + ex);
        } catch (XMLStreamException ex)
        {
            steno.error("Unable to write AMF file: " + ex);
        }

        CentreCalculations centreCalc = new CentreCalculations();
        centroids.forEach(centroid ->
        {
            centreCalc.processPoint(centroid);
        });

        return new MeshExportResult(createdFiles, centreCalc.getResult());
    }

    /**
     * This could be much simpler if it just wrapped the interface, but it was
     * free off the internet so I can't argue... It pretty-prints the XML.
     */
    class PrettyPrintHandler implements InvocationHandler
    {

        private final XMLStreamWriter target;
        private int depth = 0;
        private final Map<Integer, Boolean> hasChildElement = new HashMap<Integer, Boolean>();
        private static final String INDENT_CHAR = " ";
        private static final String LINEFEED_CHAR = "\n";

        public PrettyPrintHandler(XMLStreamWriter target)
        {
            this.target = target;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            String m = method.getName();

            // Needs to be BEFORE the actual event, so that for instance the
            // sequence writeStartElem, writeAttr, writeStartElem, writeEndElem, writeEndElem
            // is correctly handled
            if ("writeStartElement".equals(m))
            {
                // update state of parent node
                if (depth > 0)
                {
                    hasChildElement.put(depth - 1, true);
                }
                // reset state of current node
                hasChildElement.put(depth, false);
                // indent for current depth
                target.writeCharacters(LINEFEED_CHAR);
                target.writeCharacters(repeat(depth, INDENT_CHAR));
                depth++;
            } else if ("writeEndElement".equals(m))
            {
                depth--;
                if (hasChildElement.get(depth) == true)
                {
                    target.writeCharacters(LINEFEED_CHAR);
                    target.writeCharacters(repeat(depth, INDENT_CHAR));
                }
            } else if ("writeEmptyElement".equals(m))
            {
                // update state of parent node
                if (depth > 0)
                {
                    hasChildElement.put(depth - 1, true);
                }
                // indent for current depth
                target.writeCharacters(LINEFEED_CHAR);
                target.writeCharacters(repeat(depth, INDENT_CHAR));
            }
            method.invoke(target, args);
            return null;
        }

        private String repeat(int d, String s)
        {
            String _s = "";
            while (d-- > 0)
            {
                _s += s;
            }
            return _s;
        }
    }

}
