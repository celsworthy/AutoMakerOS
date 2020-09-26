package celtech.coreUI.visualisation;

/**
 *
 * @author Ian
 */
public interface ScreenExtentsProvider
{

    public ScreenExtents getScreenExtents();

    public void addScreenExtentsChangeListener(ScreenExtentsListener listener);

    public void removeScreenExtentsChangeListener(ScreenExtentsListener listener);

    public interface ScreenExtentsListener
    {
        public void screenExtentsChanged(ScreenExtentsProvider screenExtentsProvider);
    }
}
