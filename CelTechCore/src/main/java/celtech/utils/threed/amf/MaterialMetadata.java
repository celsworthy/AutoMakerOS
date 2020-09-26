package celtech.utils.threed.amf;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

/**
 *
 * @author Ian
 */
public class MaterialMetadata
{
    @JacksonXmlProperty(isAttribute = true)
    private String type = "Name";
    
    @JacksonXmlText()
    private String value;

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
    
    
}
