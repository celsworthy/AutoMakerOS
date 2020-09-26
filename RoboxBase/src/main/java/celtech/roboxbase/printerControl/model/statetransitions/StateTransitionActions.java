/*
 * Copyright 2015 CEL UK
 */
package celtech.roboxbase.printerControl.model.statetransitions;

import celtech.roboxbase.utils.tasks.Cancellable;
import celtech.roboxbase.utils.tasks.OredCancellable;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author tony
 */
public abstract class StateTransitionActions
{

    public Cancellable userCancellable;
    public Cancellable errorCancellable;
    /**
     * userOrErrorCancellable is an OR of userCancellable and errorCancellable. Actions should
     * listen to this and stop if cancelled goes to true.
     */
    public Cancellable userOrErrorCancellable;

    public StateTransitionActions(Cancellable userCancellable, Cancellable errorCancellable)
    {
        setUserCancellable(userCancellable);
        setErrorCancellable(errorCancellable);
    }

    /**
     * Initialise any variables so as to restart.
     */
    public abstract void initialise();

    public final void setUserCancellable(Cancellable cancellable)
    {
        // userCancellable is set when the user requests a cancel. The state machine also detects
        // this condition and goes to CANCELLED, so we only need to stop
        // any ongoing actions.
        this.userCancellable = cancellable;
        setUserOrErrorCancellable();
        cancellable.cancelled().addListener(
            (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
            {
                // this is run immediately after the cancel. An ongoing transition may continue
                // to run after this has been called. See resetAfterCancelOrError.
                if (newValue)
                {
                    whenUserCancelDetected();
                }
            });
    }

    private void setUserOrErrorCancellable()
    {
        userOrErrorCancellable = new OredCancellable(userCancellable, errorCancellable);
    }

    public final void setErrorCancellable(Cancellable errorCancellable)
    {
        // errorCancellable is set when an out-of-band error occurs such as a printer fault. The
        // state machine will also detect this state and go to FAILED, so we only need to stop
        // any ongoing actions.
        this.errorCancellable = errorCancellable;
        setUserOrErrorCancellable();
        errorCancellable.cancelled().addListener(
            (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
            {
                // this is run immediately after the error. An ongoing transition may continue
                // to run after this has been called. See resetAfterCancelOrError.
                if (newValue)
                {
                    whenErrorDetected();
                }
            });
    }

    /**
     * whenUserCancelDetected is called immediately after StateTransitionManager.cancel is called.
     * This is typically used to abort any ongoing print.
     */
    public abstract void whenUserCancelDetected();

    /**
     * whenErrorDetected is called immediately after errorCancellable.cancelled() is set to true.
     * This is typically used to abort any ongoing print.
     */
    public abstract void whenErrorDetected();

    /**
     * resetAfterCancelOrError is called after a userCancel or errorCancel, but not until any
     * running transition / arrival is stopped. If there is no running transition then it is called
     * immediately after whenUserCancelDetected or whenErrorDetected. It is intended to allow a
     * clear-up after the last transition has been cancelled / aborted and after it (the transition)
     * has fully stopped.
     */
    public abstract void resetAfterCancelOrError();

}
