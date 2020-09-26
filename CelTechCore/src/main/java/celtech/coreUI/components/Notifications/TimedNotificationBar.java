package celtech.coreUI.components.Notifications;

import celtech.Lookup;
import celtech.roboxbase.BaseLookup;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Ian
 */
public class TimedNotificationBar extends AppearingNotificationBar
{

    private final int displayFor_ms = 4000;
    private final int selfDestructIn_ms = 6000;
    private Timer selfDestructTimer = null;

    @Override
    public void show()
    {
        Lookup.getNotificationDisplay().addNotificationBar(this);
        selfDestructTimer = new Timer("TimedNotificationSelfDestruct", true);
        startSlidingInToView();
        selfDestructTimer.schedule(new SelfDestructTask(), selfDestructIn_ms);
    }

    @Override
    public void finishedSlidingIntoView()
    {
        Timer putItAwayTimer = new Timer("TimedNotificationDisposer", true);
        putItAwayTimer.schedule(new SlideAwayTask(), displayFor_ms);
    }

    @Override
    public void finishedSlidingOutOfView()
    {
        Lookup.getNotificationDisplay().removeNotificationBar(this);
        if (selfDestructTimer != null)
        {
            selfDestructTimer.cancel();
            selfDestructTimer = null;
        }
    }

    private class SlideAwayTask extends TimerTask
    {

        @Override
        public void run()
        {
            startSlidingOutOfView();
        }
    }

    private class SelfDestructTask extends TimerTask
    {

        @Override
        public void run()
        {
            BaseLookup.getTaskExecutor().runOnGUIThread(() ->
            {
                finishedSlidingOutOfView();
            });
        }
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
        finishedSlidingOutOfView();
    }

}
