package celtech.roboxbase.printerControl.model;

/**
 *
 * @author Ian
 */
public interface RepairableComponent
{
    public RepairResult bringDataInBounds();
    public void resetToDefaults();
    public void allocateRandomID();
}
