/*
 * Copyright 2014 CEL UK
 */
package celtech.coreUI.components.Notifications;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;

/**
 *
 * @author ian
 */
public class GenericProgressBar extends AppearingProgressBar implements Initializable
{
    
    private final ChangeListener<Boolean> displayBarChangeListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
    {
        displayBar(newValue);
    };
    
    private void displayBar(boolean displayBar)
    {
        if (displayBar)
        {
            startSlidingInToView();
        } else
        {
            startSlidingOutOfView();
        }
    }
    
    public GenericProgressBar(String title, ReadOnlyBooleanProperty displayProgressBar, ReadOnlyDoubleProperty progressProperty)
    {
        super();
        
        displayBar(displayProgressBar.get());
        displayProgressBar.addListener(displayBarChangeListener);
        
        progressRequired(true);
        targetLegendRequired(false);
        targetValueRequired(false);
        currentValueRequired(false);
        layerDataRequired(false);
        
        largeProgressDescription.setText(title);
        progressBar.progressProperty().bind(progressProperty);
    }
}
