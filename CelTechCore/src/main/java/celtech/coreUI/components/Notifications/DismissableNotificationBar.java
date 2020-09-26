package celtech.coreUI.components.Notifications;

import celtech.Lookup;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 *
 * @author Ian
 */
public class DismissableNotificationBar extends AppearingNotificationBar
{

    private EventHandler<ActionEvent> dismissAction = new EventHandler<ActionEvent>()
    {
        @Override
        public void handle(ActionEvent t)
        {
            startSlidingOutOfView();
        }
    };

    public DismissableNotificationBar()
    {
        super();
        actionButton.setVisible(true);
        actionButton.setOnAction(dismissAction);
    }

    public DismissableNotificationBar(String buttonText)
    {
        super();
        actionButton.setVisible(true);
        actionButton.setOnAction(dismissAction);
        actionButton.setText(buttonText);
    }

    @Override
    public void show()
    {
        Lookup.getNotificationDisplay().addNotificationBar(this);
        startSlidingInToView();
    }

    @Override
    public void finishedSlidingIntoView()
    {
    }

    @Override
    public void finishedSlidingOutOfView()
    {
        Lookup.getNotificationDisplay().removeNotificationBar(this);
    }

    @Override
    public boolean isSameAs(AppearingNotificationBar bar)
    {
        boolean theSame = false;
        if (this.getType() == bar.getType()
                && this.notificationDescription.getText().equals(bar.notificationDescription.getText())
                && this.notificationType == bar.notificationType
                && actionButton.getText().equals(bar.actionButton.getText()))
        {
            theSame = true;
        }

        return theSame;
    }

    @Override
    public void destroyBar()
    {
        finishedSlidingOutOfView();
    }
}
