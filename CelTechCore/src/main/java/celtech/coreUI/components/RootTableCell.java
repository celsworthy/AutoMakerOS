package celtech.coreUI.components;

import celtech.configuration.ApplicationConfiguration;
import celtech.roboxbase.comms.DetectedServer;
import celtech.roboxbase.comms.DetectedServer.ServerStatus;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Ian
 */
public class RootTableCell extends TableCell<DetectedServer, ServerStatus>
{

    private ImageView imageContainer;
    private Image connectedImage;
    private Image disconnectedImage;

    public RootTableCell()
    {
        imageContainer = new ImageView();
        connectedImage = new Image(ApplicationConfiguration.imageResourcePath + "plug_connected.png");
        disconnectedImage = new Image(ApplicationConfiguration.imageResourcePath + "plug_disconnected.png");
    }

    @Override
    protected void updateItem(ServerStatus item, boolean empty)
    {
        super.updateItem(item, empty);
        if (item != null && !empty)
        {
            setGraphic(imageContainer);
            if (item == ServerStatus.CONNECTED || item == ServerStatus.UPGRADING)
            {
                imageContainer.setImage(connectedImage);
            }
            else
            {
                imageContainer.setImage(disconnectedImage);
            }
        } else
        {
            setGraphic(null);
        }
    }
}
