/*
 * Copyright 2015 CEL UK
 */
package celtech.roboxbase.services.slicer;

/**
 *
 * @author tony
 */
public interface ProgressReceiver
{

    void progressUpdateFromSlicer(String message, float workDone);
}
