/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase.services;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;

/**
 *
 * @author ianhudson
 */
public interface ControllableService
{

    /**
     *
     * @return
     */
    public ReadOnlyStringProperty titleProperty();

    /**
     *
     * @return
     */
    public boolean cancelRun();

    /**
     *
     * @return
     */
    public ReadOnlyBooleanProperty runningProperty();

    /**
     *
     * @return
     */
    public ReadOnlyStringProperty messageProperty();

    /**
     *
     * @return
     */
    public ReadOnlyDoubleProperty progressProperty();
}
