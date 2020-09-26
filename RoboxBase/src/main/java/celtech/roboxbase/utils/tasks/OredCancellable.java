/*
 * Copyright 2015 CEL UK
 */
package celtech.roboxbase.utils.tasks;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;

/**
 * OredCancellable takes two input cancellables and ORs them to look like one cancellable. It
 * gives cancelled of true if any cancellable.cancelled is true. Input cancellables of null
 * are ignored but at least one of the cancellables must be non-null.
 * //TODO should also implement SimpleCancellable.or() so that chaining is possible
 * @author tony
 */
public  class OredCancellable implements Cancellable
    {

        Cancellable cancellable1;
        Cancellable cancellable2;
        Cancellable cancellable = new SimpleCancellable();

        public OredCancellable(Cancellable cancellable1, Cancellable cancellable2)
        {
            this.cancellable1 = cancellable1;
            this.cancellable2 = cancellable2;

            if (cancellable1 != null)
            {
                cancellable1.cancelled().addListener(
                    (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
                    {
                        updateCancellable();
                    });
            }

            if (cancellable2 != null)
            {
                cancellable2.cancelled().addListener(
                    (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
                    {
                        updateCancellable();
                    });
            }
        }

        private void updateCancellable()
        {
            if (cancellable1 == null && cancellable2.cancelled().get())
            {
                cancellable.cancelled().set(true);
            } else if (cancellable2 == null && cancellable1.cancelled().get())
            {
                cancellable.cancelled().set(true);
            } else if ((cancellable1 != null && cancellable1.cancelled().get()) || 
                (cancellable2 != null && cancellable2.cancelled().get()))
            {
                cancellable.cancelled().set(true);
            } else
            {
                cancellable.cancelled().set(false);
            }
        }

        @Override
        public BooleanProperty cancelled()
        {
            return cancellable.cancelled();
        }

    }

