package celtech.utils.tasks;

import celtech.roboxbase.utils.tasks.TaskExecutor;
import celtech.roboxbase.utils.tasks.TaskResponder;
import celtech.roboxbase.utils.tasks.TaskResponse;
import javafx.concurrent.Task;

/**
 *
 * @author Ian
 */
public class TestTaskExecutor implements TaskExecutor
{

    @Override
    public void respondOnGUIThread(TaskResponder responder, boolean success, String message)
    {
        TaskResponse taskResponse = new TaskResponse(message);
        taskResponse.setSucceeded(success);

        responder.taskEnded(taskResponse);
    }

    @Override
    public void respondOnGUIThread(TaskResponder responder, boolean success, String message,
        Object returnedObject)
    {
        TaskResponse taskResponse = new TaskResponse(message);
        taskResponse.setSucceeded(success);
        taskResponse.setReturnedObject(returnedObject);

        responder.taskEnded(taskResponse);
    }

    @Override
    public void respondOnCurrentThread(TaskResponder responder, boolean success, String message)
    {
        TaskResponse taskResponse = new TaskResponse(message);
        taskResponse.setSucceeded(success);

        responder.taskEnded(taskResponse);
    }

    @Override
    public void runOnGUIThread(Runnable runnable)
    {
        runnable.run();
    }
    
    @Override
    public void runOnBackgroundThread(Runnable runnable)
    {
        runnable.run();
    }
    
    @Override
    public void runDelayedOnBackgroundThread(Runnable runnable, long delay)
    {
        try {
            Thread.sleep(delay);
        }
        catch (InterruptedException ex)
        {
        }
        runnable.run();
    }

    @Override
    public void runAsTask(NoArgsVoidFunc action, NoArgsVoidFunc successHandler,
        NoArgsVoidFunc failureHandler, String taskName)
    {
        try
        {
            action.run();
            successHandler.run();

        } catch (Exception ex)
        {
            ex.printStackTrace();
            try
            {
                failureHandler.run();
            } catch (Exception ex1)
            {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void runTaskAsDaemon(Task task)
    {
    }
}
