package celtech.roboxbase.comms.remote.types;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 *
 * @author ianhudson
 */
public class SerializableColour
{

    private String webColour;

    public SerializableColour()
    {
    }

    public String getWebColour()
    {
        return webColour;
    }

    public void setWebColour(String webColour)
    {
        this.webColour = webColour;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(23, 31).
                append(webColour)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof SerializableColour))
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }

        SerializableColour rhs = (SerializableColour) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                append(webColour, rhs.webColour)
                .isEquals();
    }
}
