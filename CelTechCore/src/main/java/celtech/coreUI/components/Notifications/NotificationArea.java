package celtech.coreUI.components.Notifications;

import celtech.Lookup;
import javafx.scene.layout.VBox;

/**
 *
 * @author Ian
 */
public class NotificationArea extends VBox
{
    public NotificationArea()
    {
        this.getChildren().add(Lookup.getNotificationDisplay());
        this.getChildren().add(Lookup.getProgressDisplay());
        setPickOnBounds(false);
    }
}
