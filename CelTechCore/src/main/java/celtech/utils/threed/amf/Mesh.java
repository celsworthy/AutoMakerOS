package celtech.utils.threed.amf;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.List;

/**
 *
 * @author Ian
 */
public class Mesh
{
    @JacksonXmlElementWrapper(localName = "vertices")
    @JacksonXmlProperty(localName = "vertex")
    private List<Vertex> vertices;

    public List<Vertex> getVertices()
    {
        return vertices;
    }

    public void setVertices(List<Vertex> vertices)
    {
        this.vertices = vertices;
    }

}
