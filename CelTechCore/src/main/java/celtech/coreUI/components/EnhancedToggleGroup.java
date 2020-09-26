/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.coreUI.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class EnhancedToggleGroup extends ToggleGroup
{

    private ObjectProperty<Toggle> selectedToggle = new SimpleObjectProperty<Toggle>();

    /**
     *
     * @return
     */
    public ObjectProperty<Toggle> writableSelectedToggleProperty()
    {
        return selectedToggle;
    }

    /**
     *
     */
    public EnhancedToggleGroup()
    {
        selectedToggleProperty().addListener(new ChangeListener<Toggle>()
        {
            @Override
            public void changed(ObservableValue<? extends Toggle> paramObservableValue, Toggle old, Toggle current)
            {
                writableSelectedToggleProperty().set(current);
            }
        });

        writableSelectedToggleProperty().addListener(new ChangeListener<Toggle>()
        {
            @Override
            public void changed(ObservableValue<? extends Toggle> paramObservableValue, Toggle old, Toggle current)
            {
                if (getSelectedToggle() != current)
                {
                    selectToggle(current);
                }
            }
        });
    }
}
