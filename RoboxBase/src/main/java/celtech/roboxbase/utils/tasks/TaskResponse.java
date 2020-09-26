/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.utils.tasks;

/**
 *
 * @author tony
 */
public class TaskResponse<T>
{
    private boolean succeeded = false;
    private final String message;
    private T returnedObject;

    public TaskResponse(String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return message;
    }

    /**
     *
     * @return
     */
    public boolean succeeded()
    {
        return succeeded;
    }

    /**
     *
     * @param succeeded
     */
    public void setSucceeded(boolean succeeded)
    {
        this.succeeded = succeeded;
    }

    /**
     *
     * @param returnedObject
     */
    public void setReturnedObject(T returnedObject)
    {
        this.returnedObject = returnedObject;
    }

    /**
     *
     * @return
     */
    public T getReturnedObject()
    {
        return returnedObject;
    }
}
