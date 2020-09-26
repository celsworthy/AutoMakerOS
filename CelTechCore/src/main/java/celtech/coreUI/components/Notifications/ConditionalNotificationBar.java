package celtech.coreUI.components.Notifications;

import celtech.Lookup;
import celtech.roboxbase.appManager.NotificationType;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Ian
 */
public class ConditionalNotificationBar extends AppearingNotificationBar
{

    private ObservableValue<Boolean> appearanceCondition;

    private final ChangeListener<Boolean> conditionChangeListener = new ChangeListener<Boolean>()
    {
        @Override
        public void changed(
                ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
        {
            if (Platform.isFxApplicationThread())
            {
                calculateVisibility();
            } else
            {
                Platform.runLater(() ->
                {
                    calculateVisibility();
                });
            }
        }
    };

    public ConditionalNotificationBar(String message, NotificationType notificationType)
    {
        notificationDescription.replaceText(Lookup.i18n(message));
        setType(notificationType);
    }

    public void clearAppearanceCondition()
    {
        if (appearanceCondition != null)
        {
            appearanceCondition.removeListener(conditionChangeListener);
        }
        appearanceCondition = null;
        startSlidingOutOfView();
    }

    public ObservableValue<Boolean> getAppearanceCondition()
    {
        return appearanceCondition;
    }

    public void setAppearanceCondition(BooleanBinding appearanceCondition)
    {
        if (this.appearanceCondition != null)
        {
            this.appearanceCondition.removeListener(conditionChangeListener);
        }
        this.appearanceCondition = appearanceCondition;
        this.appearanceCondition.addListener(conditionChangeListener);
        calculateVisibility();
    }

    private void calculateVisibility()
    {
        if (appearanceCondition.getValue())
        {
            show();
        } else
        {
            startSlidingOutOfView();
        }
    }

    @Override
    public void show()
    {
        Lookup.getNotificationDisplay().addStepCountedNotificationBar(this);
        startSlidingInToView();
    }

    @Override
    public void finishedSlidingIntoView()
    {
    }

    @Override
    public void finishedSlidingOutOfView()
    {
        Lookup.getNotificationDisplay().removeStepCountedNotificationBar(this);
    }

    @Override
    public boolean isSameAs(AppearingNotificationBar bar)
    {
        boolean theSame = false;
        if (this.getType() == bar.getType()
                && this.notificationDescription.getText().equals(bar.notificationDescription.getText()))
        {
            theSame = true;
        }

        return theSame;
    }

    @Override
    public void destroyBar()
    {
        clearAppearanceCondition();
        finishedSlidingOutOfView();
    }
}
