package celtech.roboxbase.comms.interapp;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 *
 * @author ianhudson
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class InterAppResponse
{
    private InterAppResponseStatus responseStatus;

    public InterAppResponse()
    {
    }

    public InterAppResponseStatus getResponseStatus()
    {
        return responseStatus;
    }

    public void setResponseStatus(InterAppResponseStatus responseStatus)
    {
        this.responseStatus = responseStatus;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(27, 37).
                append(responseStatus).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof InterAppResponse))
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }

        InterAppResponse rhs = (InterAppResponse) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                append(responseStatus, rhs.responseStatus).
                isEquals();
    }
}
