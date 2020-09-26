package celtech.roboxbase.utils;

import java.util.HashMap;
import java.util.Map;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class TimeUtils
{

    private static final Stenographer steno = StenographerFactory.getStenographer(TimeUtils.class.
            getName());

    private class TimerEntry
    {

        private boolean timerStarted = false;
        private long lastStartTime = 0;
        private long timeSoFar_ms = 0;

        public void start()
        {
            lastStartTime = System.currentTimeMillis();
            timerStarted = true;
        }

        public void stop()
        {
            if (timerStarted)
            {
                timeSoFar_ms += System.currentTimeMillis() - lastStartTime;
            } else
            {
                steno.warning("Attempted to stop timer that hadn't been started");
            }

            timerStarted = false;
        }

        public void reset()
        {
            timerStarted = false;
            timeSoFar_ms = 0;
        }

        public long getTimeSoFar_ms()
        {
            return timeSoFar_ms;
        }
    }

    private final Map<String, Map<String, TimerEntry>> timersByObjectName = new HashMap<>();

    public static String convertToHoursMinutes(int seconds)
    {
        int minutes = (int) (seconds / 60);
        int hours = minutes / 60;
        minutes = minutes - (60 * hours);
        return String.format("%02d:%02d", hours, minutes);
    }

    public static String convertToHoursMinutesSeconds(final int secondsInput)
    {
        int minutes = (int) (secondsInput / 60);
        int hours = minutes / 60;
        minutes = minutes - (60 * hours);
        int seconds = secondsInput - (minutes * 60) - (hours * 3600);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public void timerStart(Object referenceObject, String timerTitle)
    {
        String objectName = referenceObject.toString();

        if (timersByObjectName.containsKey(objectName))
        {
            Map<String, TimerEntry> timerMap = timersByObjectName.get(objectName);

            TimerEntry timerEntry;

            if (timerMap.containsKey(timerTitle))
            {
                timerEntry = timerMap.get(timerTitle);
            } else
            {
                timerEntry = new TimerEntry();
                timerMap.put(timerTitle, timerEntry);
            }
            timerEntry.start();
        } else
        {
            //New timer
            TimerEntry newTimer = new TimerEntry();
            newTimer.start();
            Map<String, TimerEntry> timerMap = new HashMap<>();
            timerMap.put(timerTitle, newTimer);

            timersByObjectName.put(objectName, timerMap);
        }
    }

    public void timerStop(Object referenceObject, String timerTitle) throws TimerNotFoundException
    {
        boolean timerStopped = false;

        String objectName = referenceObject.toString();
        if (timersByObjectName.containsKey(objectName))
        {
            Map<String, TimerEntry> timerMap = timersByObjectName.get(objectName);

            if (timerMap.containsKey(timerTitle))
            {
                timerMap.get(timerTitle).stop();
                timerStopped = true;
            }
        }

        if (!timerStopped)
        {
            throw new TimerNotFoundException("Attempted to stop a non-existent timer for object:" + objectName
                    + " class:" + referenceObject.getClass().getCanonicalName()
                    + " timerTitle: " + timerTitle);
        }
    }

    public long timeTimeSoFar_ms(Object referenceObject, String timerTitle) throws TimerNotFoundException
    {
        long timeSoFar_ms = 0;
        boolean foundTimer = false;

        String objectName = referenceObject.toString();

        if (timersByObjectName.containsKey(objectName))
        {
            Map<String, TimerEntry> timerMap = timersByObjectName.get(objectName);

            if (timerMap.containsKey(timerTitle))
            {
                timeSoFar_ms = timerMap.get(timerTitle).getTimeSoFar_ms();
                foundTimer = true;
            }
        }
        
        if (!foundTimer)
        {
            throw new TimerNotFoundException("Attempted to get time so far from stop a non-existent timer for object:" + objectName
                    + " class:" + referenceObject.getClass().getCanonicalName()
                    + " timerTitle: " + timerTitle);
        }

        return timeSoFar_ms;
    }

    public void timerDelete(Object referenceObject, String timerTitle)
    {
        String objectName = referenceObject.toString();
        timersByObjectName.remove(objectName);
    }

    public class TimerNotFoundException extends RuntimeException
    {

        public TimerNotFoundException(String message)
        {
            super(message);
        }
    }
}
