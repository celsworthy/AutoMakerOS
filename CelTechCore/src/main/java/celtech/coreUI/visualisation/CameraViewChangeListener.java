package celtech.coreUI.visualisation;

import javafx.scene.Camera;

/**
 *
 * @author Ian
 */
public interface CameraViewChangeListener
{
    public void heresYourCamera(Camera camera);
    public void cameraViewOfYouHasChanged(double cameraDistance);
}
