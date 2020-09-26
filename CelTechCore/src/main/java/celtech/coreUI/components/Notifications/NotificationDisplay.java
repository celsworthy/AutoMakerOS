package celtech.coreUI.components.Notifications;

import celtech.roboxbase.appManager.NotificationType;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

/**
 *
 * @author Ian
 */
public class NotificationDisplay extends VBox
{

    private final VBox stepCountedNotifications = new VBox();
    private final VBox noteNotifications = new VBox();
    private final VBox cautionNotifications = new VBox();
    private final VBox warningNotifications = new VBox();

    public NotificationDisplay()
    {
        setFillWidth(true);
        stepCountedNotifications.setFillWidth(true);

        stepCountedNotifications.getChildren().add(warningNotifications);
        stepCountedNotifications.getChildren().add(cautionNotifications);
        stepCountedNotifications.getChildren().add(noteNotifications);

        getChildren().add(stepCountedNotifications);
    }

    public void displayTimedNotification(String title, String message, NotificationType notificationType)
    {
        //Check to see if any other identical notifications are on display
        TimedNotificationBar notificationBar = new TimedNotificationBar();
        if (title != null
                && !title.equals(""))
        {
            notificationBar.setMessage(title + " - " + message);
        } else
        {
            notificationBar.setMessage(message);
        }
        notificationBar.setType(notificationType);
        if (!isThisADuplicateBar(notificationBar))
        {
            notificationBar.show();
        }
    }

    private boolean isThisADuplicateBar(AppearingNotificationBar notificationBar)
    {
        boolean isADuplicate = false;

        for (Node child : getChildren())
        {
            if (child instanceof AppearingNotificationBar)
            {
                AppearingNotificationBar bar = (AppearingNotificationBar) child;
                if (bar.isSameAs(notificationBar))
                {
                    isADuplicate = true;
                    break;
                }
            }
        }

        return isADuplicate;
    }

    public void displayDismissableNotification(String message, String buttonText, NotificationType notificationType)
    {
        DismissableNotificationBar notificationBar = new DismissableNotificationBar(buttonText);
        notificationBar.setMessage(message);
        notificationBar.setType(notificationType);
        if (!isThisADuplicateBar(notificationBar))
        {
            notificationBar.show();
        }
    }

    void addNotificationBar(AppearingNotificationBar notificationBar)
    {
        if (!getChildren().contains(notificationBar))
        {
            getChildren().add(notificationBar);
        }
    }

    public void removeNotificationBar(AppearingNotificationBar notificationBar)
    {
        getChildren().remove(notificationBar);
    }

    public void addStepCountedNotificationBar(AppearingNotificationBar notificationBar)
    {
        switch (notificationBar.getType())
        {
            case NOTE:
                if (!noteNotifications.getChildren().contains(notificationBar))
                {
                    noteNotifications.getChildren().add(notificationBar);
                }
                break;
            case CAUTION:
                if (!cautionNotifications.getChildren().contains(notificationBar))
                {
                    cautionNotifications.getChildren().add(notificationBar);
                }
                break;
            case WARNING:
                if (!warningNotifications.getChildren().contains(notificationBar))
                {
                    warningNotifications.getChildren().add(notificationBar);
                }
                break;
            default:
                break;
        }

        updateNotificationNumbers();
    }

    public void removeStepCountedNotificationBar(AppearingNotificationBar notificationBar)
    {
        switch (notificationBar.getType())
        {
            case NOTE:
                noteNotifications.getChildren().remove(notificationBar);
                break;
            case CAUTION:
                cautionNotifications.getChildren().remove(notificationBar);
                break;
            case WARNING:
                warningNotifications.getChildren().remove(notificationBar);
                break;
            default:
                break;
        }

        updateNotificationNumbers();
    }

    private void updateNotificationNumbers()
    {
        int currentStep = 1;
        int totalNotifications = noteNotifications.getChildren().size()
                + cautionNotifications.getChildren().size()
                + warningNotifications.getChildren().size();

        for (Node bar : warningNotifications.getChildren())
        {
            AppearingNotificationBar notificationBar = (AppearingNotificationBar) bar;
            notificationBar.setXOfY(currentStep++, totalNotifications);
        }

        for (Node bar : cautionNotifications.getChildren())
        {
            AppearingNotificationBar notificationBar = (AppearingNotificationBar) bar;
            notificationBar.setXOfY(currentStep++, totalNotifications);
        }

        for (Node bar : noteNotifications.getChildren())
        {
            AppearingNotificationBar notificationBar = (AppearingNotificationBar) bar;
            notificationBar.setXOfY(currentStep++, totalNotifications);
        }
    }
}
