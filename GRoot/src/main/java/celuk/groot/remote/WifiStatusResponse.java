package celuk.groot.remote;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
}
