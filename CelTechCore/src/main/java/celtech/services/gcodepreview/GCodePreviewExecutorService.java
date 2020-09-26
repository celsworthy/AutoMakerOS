/*
 * Copyright 2015 CEL UK
 */
package celtech.services.gcodepreview;

import celtech.coreUI.controllers.panels.*;
import celtech.roboxbase.utils.tasks.Cancellable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * GCodePreviewPrepManager makes sure that all tasks are properly cancelled.
 *
 * @author tony
 */
public class GCodePreviewExecutorService
{
    private final ExecutorService executorService;
    private Future taskFuture;
    
    public GCodePreviewExecutorService()
    {
        ThreadFactory threadFactory = (Runnable runnable) ->
        {
            Thread thread = Executors.defaultThreadFactory().newThread(runnable);
            thread.setDaemon(true);
            return thread;
        };
        executorService = Executors.newFixedThreadPool(1, threadFactory);
    }
    
    public void cancelTask()
    {
//        executorService.shutdownNow();
        if (taskFuture != null)
            taskFuture.cancel(true);
        taskFuture = null;
    }

    public void runTask(Runnable runnable)
    {
        taskFuture = executorService.submit(runnable);
    }
}
