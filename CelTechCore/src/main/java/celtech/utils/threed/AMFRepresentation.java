package celtech.utils.threed;

import celtech.utils.threed.amf.AMFObject;
import celtech.utils.threed.amf.Constellation;
import celtech.utils.threed.amf.Material;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ian
 */
@JacksonXmlRootElement(localName = "amf")
public class AMFRepresentation
{

    @JacksonXmlProperty(isAttribute = true)
    private String unit = "millimeter";

    @JacksonXmlProperty(isAttribute = true)
    private String version = "1.1";

    @JacksonXmlElementWrapper(useWrapping = false)
    private AMFObject object;

    private Constellation constellation;

    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Material> material = new ArrayList<>();

    public String getUnit()
    {
        return unit;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public AMFObject getObject()
    {
        return object;
    }

    public void setObject(AMFObject object)
    {
        this.object = object;
    }

    public Constellation getConstellation()
    {
        return constellation;
    }

    public void setConstellation(Constellation constellation)
    {
        this.constellation = constellation;
    }

    public List<Material> getMaterial()
    {
        return material;
    }

    public void setMaterial(List<Material> material)
    {
        this.material = material;
    }

}
