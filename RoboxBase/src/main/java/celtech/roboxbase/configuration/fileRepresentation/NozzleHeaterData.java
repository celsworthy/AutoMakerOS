package celtech.roboxbase.configuration.fileRepresentation;

/**
 *
 * @author Ian
 */
public class NozzleHeaterData
{

    private int maximum_temperature_C;
    private float beta;
    private float tcal;

    public int getMaximum_temperature_C()
    {
        return maximum_temperature_C;
    }

    public void setMaximum_temperature_C(int maximum_temperature_C)
    {
        this.maximum_temperature_C = maximum_temperature_C;
    }

    public float getBeta()
    {
        return beta;
    }

    public void setBeta(float beta)
    {
        this.beta = beta;
    }

    public float getTcal()
    {
        return tcal;
    }

    public void setTcal(float tcal)
    {
        this.tcal = tcal;
    }
}
