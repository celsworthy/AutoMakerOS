/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.utils.tasks;

/**
 *
 * @author tony
 * @param <T>
 */
public interface TaskResponder<T>
{
    public void taskEnded(TaskResponse<T> taskResponse);
}
