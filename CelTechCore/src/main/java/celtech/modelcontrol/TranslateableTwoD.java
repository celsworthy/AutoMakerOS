package celtech.modelcontrol;

/**
 *
 * @author ianhudson
 */
public interface TranslateableTwoD extends Translateable
{

    public void translateBy(double xMove, double zMove);

    public void translateTo(double xPosition, double zPosition);

    public void translateXTo(double xPosition);

}
