/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.coreUI.components;

import java.util.regex.Pattern;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author ianhudson
 */
public class BoundTextField extends TextField
{    
    private DoubleProperty propertyToDisplay = null;
    private String gcodePrefix = null;
    private final Pattern wholeNumberPattern = Pattern.compile("\\d*");

    /**
     *
     */
    public BoundTextField()
    {
        this.getStyleClass().add("boundTextField");
    }
    
    /**
     *
     * @param displayValue
     * @param gcodePrefix
     */
    public void bindToDataSource(DoubleProperty displayValue, String gcodePrefix)
    {
        this.propertyToDisplay = displayValue;
        this.gcodePrefix = gcodePrefix;
        bindTextDisplay();
        
        this.textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> ov, String oldValue, String newValue)
            {
                if (!wholeNumberPattern.matcher(newValue).matches())
                {
                    revertValue(oldValue);
                }
            }
        });
        
        this.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue)
            {
                if (newValue == true)
                {
                    unbindTextDisplay();
                } else
                {
                    bindTextDisplay();
                }
            }
        });
        
        this.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent event)
            {
                switch (event.getCode())
                {
                    case ESCAPE:
                        ((Node) event.getSource()).getParent().requestFocus();
                        break;
                    case ENTER:
//                        sendValueToPrinter();
                        ((Node) event.getSource()).getParent().requestFocus();
                        break;
                    default:
                        break;
                }
            }
        });
    }
    
    private void revertValue(String valueToRevertTo)
    {
        this.setText(valueToRevertTo);
    }
    
//    private void sendValueToPrinter()
//    {
//        try
//        {
//            fxToPrinterInterface.sendDirectGCode(gcodePrefix + this.textProperty().getValue());
//        } catch (RoboxCommsException ex)
//        {
//            System.err.println("Error whilst sending bound text field value to printer " + ex);
//        }
//    }
    
    private void bindTextDisplay()
    {
        this.textProperty()
                .bind(propertyToDisplay.asString("%.0f"));
    }
    
    private void unbindTextDisplay()
    {
        this.textProperty().unbind();
    }
}
