package celtech.modelcontrol;

/**
 *
 * @author ianhudson
 */
public interface ScaleableThreeD extends ScaleableTwoD
{
    public double getZScale();

    public void setZScale(double scaleFactor, boolean dropToBed);
}
