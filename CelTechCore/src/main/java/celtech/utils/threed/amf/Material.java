package celtech.utils.threed.amf;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 *
 * @author Ian
 */
public class Material
{
    @JacksonXmlProperty(isAttribute = true)
    private int id;
    
    private MaterialMetadata metadata;
    private MaterialColour color;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public MaterialMetadata getMetadata()
    {
        return metadata;
    }

    public void setMetadata(MaterialMetadata metadata)
    {
        this.metadata = metadata;
    }

    public MaterialColour getColor()
    {
        return color;
    }

    public void setColor(MaterialColour color)
    {
        this.color = color;
    }
    
    
}
