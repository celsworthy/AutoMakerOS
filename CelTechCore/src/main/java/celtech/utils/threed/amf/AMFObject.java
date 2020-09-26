package celtech.utils.threed.amf;

import celtech.modelcontrol.ModelContainer;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 *
 * @author Ian
 */
@JacksonXmlRootElement(localName = "object")
public class AMFObject
{

    @JacksonXmlProperty(isAttribute = true)
    private int id;

    private Mesh mesh;

    public AMFObject(ModelContainer modelContainer, int id)
    {
        this.id = id;
    }

    public Mesh getMesh()
    {
        return mesh;
    }

    public void setMesh(Mesh mesh)
    {
        this.mesh = mesh;
    }

}
