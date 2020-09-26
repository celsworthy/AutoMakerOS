package celtech.utils;

import celtech.roboxbase.utils.PercentProgressReceiver;
import javafx.concurrent.Task;

/**
 *
 * @author ianhudson
 */
public abstract class TaskWithProgessCallback<V> extends Task<V> implements PercentProgressReceiver
{
}
