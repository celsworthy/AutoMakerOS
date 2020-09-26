package celtech.utils.threed.amf;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 *
 * @author Ian
 */
public class ConstellationObjectInstance
{
    @JacksonXmlProperty(isAttribute = true)
    private int objectid;
    private int deltax;
    private int deltay;
    private int deltaz;
    private int rx;
    private int ry;
    private int rz;

    public int getObjectid()
    {
        return objectid;
    }

    public void setObjectid(int objectid)
    {
        this.objectid = objectid;
    }

    public int getDeltax()
    {
        return deltax;
    }

    public void setDeltax(int deltax)
    {
        this.deltax = deltax;
    }

    public int getDeltay()
    {
        return deltay;
    }

    public void setDeltay(int deltay)
    {
        this.deltay = deltay;
    }

    public int getDeltaz()
    {
        return deltaz;
    }

    public void setDeltaz(int deltaz)
    {
        this.deltaz = deltaz;
    }

    public int getRx()
    {
        return rx;
    }

    public void setRx(int rx)
    {
        this.rx = rx;
    }

    public int getRy()
    {
        return ry;
    }

    public void setRy(int ry)
    {
        this.ry = ry;
    }

    public int getRz()
    {
        return rz;
    }

    public void setRz(int rz)
    {
        this.rz = rz;
    }
    
    
}
