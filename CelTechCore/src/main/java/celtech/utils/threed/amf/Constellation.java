package celtech.utils.threed.amf;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.List;

/**
 *
 * @author Ian
 */
public class Constellation
{

    @JacksonXmlProperty(isAttribute = true)
    private int id;

    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ConstellationObjectInstance> instance;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public List<ConstellationObjectInstance> getInstance()
    {
        return instance;
    }

    public void setInstance(List<ConstellationObjectInstance> instance)
    {
        this.instance = instance;
    }

}
