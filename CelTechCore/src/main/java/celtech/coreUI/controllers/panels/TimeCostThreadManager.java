/*
 * Copyright 2015 CEL UK
 */
package celtech.coreUI.controllers.panels;

import celtech.roboxbase.utils.tasks.Cancellable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * TimeCostThreadManager makes sure that all tasks are properly cancelled.
 *
 * @author tony
 */
public class TimeCostThreadManager
{

    private final ExecutorService executorService;
    private Future timeCostFuture;
    private Cancellable cancellable;
    private static TimeCostThreadManager instance;
    
    private TimeCostThreadManager()
    {
        ThreadFactory threadFactory = (Runnable runnable) ->
        {
            Thread thread = Executors.defaultThreadFactory().newThread(runnable);
            thread.setDaemon(true);
            return thread;
        };
        executorService = Executors.newFixedThreadPool(1, threadFactory);
    }
    
    public static TimeCostThreadManager getInstance()
    {
        if (instance == null)
        {
            instance = new TimeCostThreadManager();
        }
        
        return instance;
    }

    public void cancelRunningTimeCostTasks()
    {
//        executorService.shutdownNow();
        if (cancellable != null)
        {
            cancellable.cancelled().set(true);
            timeCostFuture.cancel(true);
            cancellable = null;
        }
    }

    public void cancelRunningTimeCostTasksAndRun(Runnable runnable, Cancellable cancellable)
    {
        cancelRunningTimeCostTasks();
        this.cancellable = cancellable;
        timeCostFuture = executorService.submit(() ->
        {
            try {
            runnable.run();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

}
