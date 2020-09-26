package celtech.coreUI.visualisation;

/**
 *
 * @author Ian
 */
public interface ScreenExtentsProviderThreeD extends ScreenExtentsProvider
{

    public double getTransformedHeight();

    public double getTransformedWidth();

    public double getTransformedDepth();
}
