package celtech.coreUI.components;

/**
 *
 * @author Ian
 */
public enum Orientation
{

    NORTH(0),
    NORTH_EAST(45),
    EAST(90),
    SOUTH_EAST(135),
    SOUTH(180),
    SOUTH_WEST(225),
    WEST(270),
    NORTH_WEST(315),
    TWELVE_O_CLOCK(0),
    THREE_O_CLOCK(90),
    SIX_O_CLOCK(180),
    NINE_O_CLOCK(270);

    private final double angle;

    private Orientation(double angle)
    {
        this.angle = angle;
    }

    public double getAngle()
    {
        return angle;
    }
}
