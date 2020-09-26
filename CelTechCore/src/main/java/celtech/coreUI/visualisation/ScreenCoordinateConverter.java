package celtech.coreUI.visualisation;

import javafx.geometry.Point2D;

/**
 *
 * @author Ian
 */
public interface ScreenCoordinateConverter
{
    public Point2D convertWorldCoordinatesToScreen(double worldX, double worldY, double worldZ);
}
