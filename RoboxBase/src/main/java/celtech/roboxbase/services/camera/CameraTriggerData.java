package celtech.roboxbase.services.camera;

/**
 *
 * @author ianhudson
 */
public class CameraTriggerData
{
    private final boolean turnOffHeadLights;
    private final boolean turnOffAmbient;
    private final boolean moveBeforeCapture;
    private final int xMoveBeforeCapture;
    private final int yMoveBeforeCapture;

    public CameraTriggerData(boolean turnOffHeadLights,
            boolean turnOffAmbient,
            boolean moveBeforeCapture,
            int xMoveBeforeCapture,
            int yMoveBeforeCapture)
    {
        this.turnOffHeadLights = turnOffHeadLights;
        this.turnOffAmbient = turnOffAmbient;
        this.moveBeforeCapture = moveBeforeCapture;
        this.xMoveBeforeCapture = xMoveBeforeCapture;
        this.yMoveBeforeCapture = yMoveBeforeCapture;
    }

    public boolean isTurnOffHeadLights()
    {
        return turnOffHeadLights;
    }
    
    public boolean isTurnOffAmbient()
    {
        return turnOffAmbient;
    }

    public boolean isMoveBeforeCapture()
    {
        return moveBeforeCapture;
    }
    
    public int getxMoveBeforeCapture()
    {
        return xMoveBeforeCapture;
    }

    public int getyMoveBeforeCapture()
    {
        return yMoveBeforeCapture;
    }
}
