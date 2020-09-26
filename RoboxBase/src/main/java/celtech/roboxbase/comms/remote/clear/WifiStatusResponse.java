package celtech.roboxbase.comms.remote.clear;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 *
 * @author ianhudson
 */
public class WifiStatusResponse
{

    private boolean poweredOn;
    private boolean associated;
    private String ssid;

    public WifiStatusResponse()
    {
        // Jackson deserialization
    }

    public WifiStatusResponse(boolean poweredOn, boolean associated, String ssid)
    {
        this.poweredOn = poweredOn;
        this.associated = associated;
        this.ssid = ssid;
    }

    public boolean isPoweredOn()
    {
        return poweredOn;
    }

    public void setPoweredOn(boolean poweredOn)
    {
        this.poweredOn = poweredOn;
    }

    public boolean isAssociated()
    {
        return associated;
    }

    public void setAssociated(boolean associated)
    {
        this.associated = associated;
    }

    public String getSsid()
    {
        return ssid;
    }

    public void setSsid(String ssid)
    {
        this.ssid = ssid;
    }

    @JsonIgnore
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(21, 31)
                .append(poweredOn)
                .append(associated)
                .append(ssid)
                .toHashCode();
    }

    @JsonIgnore
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof WifiStatusResponse))
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }

        WifiStatusResponse rhs = (WifiStatusResponse) obj;
        return new EqualsBuilder()
                .append(poweredOn, rhs.poweredOn)
                .append(associated, rhs.associated)
                .append(ssid, rhs.ssid)
                .isEquals();
    }
}
