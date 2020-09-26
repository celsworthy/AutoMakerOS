package celtech.automaker;

import celtech.roboxbase.comms.interapp.InterAppRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 *
 * @author ianhudson
 */
public class AutoMakerInterAppRequest extends InterAppRequest
{

    @JsonIgnore
    private final Stenographer steno = StenographerFactory.getStenographer(AutoMakerInterAppRequest.class.getName());

    private AutoMakerInterAppRequestCommands command;
    private List<InterAppParameter> urlEncodedParameters = new ArrayList<>();

    public AutoMakerInterAppRequest()
    {
    }

    public AutoMakerInterAppRequestCommands getCommand()
    {
        return command;
    }

    public void setCommand(AutoMakerInterAppRequestCommands command)
    {
        this.command = command;
    }

    public List<InterAppParameter> getUrlEncodedParameters()
    {
        return urlEncodedParameters;
    }

    public void setUrlEncodedParameters(List<InterAppParameter> urlEncodedParameters)
    {
        this.urlEncodedParameters = urlEncodedParameters;
    }

    @JsonIgnore
    public List<InterAppParameter> getUnencodedParameters()
    {
        List<InterAppParameter> paramsOut = new ArrayList<>();

        for (InterAppParameter paramEntry : urlEncodedParameters)
        {
            paramsOut.add(new InterAppParameter(paramEntry.getType(), paramEntry.getUnencodedParameter()));
        }

        return paramsOut;
    }

    public void addURLEncodedParameter(InterAppParameterType parameterType, String urlEncodedParameter)
    {
        urlEncodedParameters.add(new InterAppParameter(parameterType, urlEncodedParameter));
    }

    public void addSeparatedURLEncodedParameter(InterAppParameterType parameterType, String urlEncodedParametersWithSeparator)
    {
        String[] params = urlEncodedParametersWithSeparator.split("&");
        for (String param : params)
        {
            urlEncodedParameters.add(new InterAppParameter(parameterType, param));
        }
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(13, 37).
                append(command).
                append(urlEncodedParameters).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof AutoMakerInterAppRequest))
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }

        AutoMakerInterAppRequest rhs = (AutoMakerInterAppRequest) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                append(command, rhs.command).
                append(urlEncodedParameters, rhs.urlEncodedParameters).
                isEquals();
    }
}
