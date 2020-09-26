/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.utils.tasks;

import javafx.beans.property.BooleanProperty;

/**
 *
 * @author tony
 */
public interface Cancellable
{
    public BooleanProperty cancelled();
}
