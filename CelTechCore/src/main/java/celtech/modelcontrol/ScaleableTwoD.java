package celtech.modelcontrol;

/**
 *
 * @author ianhudson
 */
public interface ScaleableTwoD extends ContainerOperation
{

    public double getXScale();

    public void setXScale(double scaleFactor, boolean dropToBed);

    public double getYScale();

    public void setYScale(double scaleFactor, boolean dropToBed);
}
