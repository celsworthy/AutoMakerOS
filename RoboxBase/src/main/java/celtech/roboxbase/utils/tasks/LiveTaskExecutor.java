package celtech.roboxbase.utils.tasks;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.concurrent.Task;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class LiveTaskExecutor implements TaskExecutor
{

    private final Stenographer steno = StenographerFactory.getStenographer(
        LiveTaskExecutor.class.getName());

    @Override
    public void runTaskAsDaemon(Task task)
    {
        runOnGUIThread(new Runnable()
        {

            @Override
            public void run()
            {
                Thread th = new Thread(task);
                th.setDaemon(true);
                th.start();
            }
        });
    }

    @Override
    public void runOnGUIThread(Runnable runnable)
    {
        // Running immediately if this is the FX thread
        // prevents things that wait for a FX thread task to complete from
        // locking up, if the task is called from the FX thread. 
        if (Platform.isFxApplicationThread())
        {
            runnable.run();
        } else
        {
            Platform.runLater(runnable);
        }
    }

    @Override
    public void runOnBackgroundThread(Runnable runnable)
    {
        Thread th = new Thread(runnable);
        th.setDaemon(true);
        th.start();
    }
    
    @Override
    public void runDelayedOnBackgroundThread(Runnable runnable, long delay)
    {
        new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    runnable.run();
                                }
                             },
                             delay);
    }

    @Override
    public void respondOnGUIThread(TaskResponder responder, boolean success, String message)
    {
        respondOnGUIThread(responder, success, message, null);
    }

    @Override
    public void respondOnGUIThread(TaskResponder responder, boolean success, String message,
        Object returnedObject)
    {
        if (responder != null)
        {
            TaskResponse taskResponse = new TaskResponse(message);
            taskResponse.setSucceeded(success);

            if (returnedObject != null)
            {
                taskResponse.setReturnedObject(returnedObject);
            }

            Platform.runLater(() ->
            {
                responder.taskEnded(taskResponse);
            });
        }
    }

    @Override
    public void respondOnCurrentThread(TaskResponder responder, boolean success, String message)
    {
        if (responder != null)
        {
            TaskResponse taskResponse = new TaskResponse(message);
            taskResponse.setSucceeded(success);

            responder.taskEnded(taskResponse);
        }
    }

    @Override
    public void runAsTask(NoArgsVoidFunc action, NoArgsVoidFunc successHandler,
        NoArgsVoidFunc failureHandler, String taskName)
    {
        Runnable runTask = () ->
        {
            try
            {
                action.run();
                successHandler.run();

            } catch (Exception ex)
            {
                steno.exception("Failure running task: ", ex);
                try
                {
                    if (failureHandler != null)
                    {
                        failureHandler.run();
                    } else
                    {
                        steno.warning("No failure handler for this case");
                    }
                } catch (Exception ex1)
                {
                    steno.error("Error running failure handler!: " + ex);
                }
            }
        };
        Thread taskThread = new Thread(runTask);
        // Setting to Daemon is not strictly necessary if the cancelling logic
        // is implemented correctly, but just in case.
        taskThread.setDaemon(true);
        taskThread.setName(taskName);
        taskThread.start();
    }
}
