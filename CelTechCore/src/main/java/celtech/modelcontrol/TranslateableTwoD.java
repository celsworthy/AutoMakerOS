package celtech.modelcontrol;

/**
 *
 * @author ianhudson
 */
public interface TranslateableTwoD extends ContainerOperation
{

    public void translateBy(double xMove, double zMove);

    public void translateTo(double xPosition, double zPosition);

    public void translateXTo(double xPosition);
    
    public void translateDepthPositionTo(double position);
}
