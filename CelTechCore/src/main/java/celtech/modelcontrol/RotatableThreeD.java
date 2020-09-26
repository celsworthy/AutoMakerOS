package celtech.modelcontrol;

/**
 *
 * @author ianhudson
 */
public interface RotatableThreeD extends RotatableTwoD
{

    public void setRotationTwist(double value);

    public double getRotationTwist();

    public void setRotationLean(double value);

    public double getRotationLean();

}
