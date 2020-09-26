package celuk.groot.controllers;

import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class IntegerSpinnerController {
    // This is very similar to FloatSpinnerController.
    static final UnaryOperator<TextFormatter.Change> NUMERIC_FILTER = (change) -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?([1-9][0-9]*)?")) { 
                return change;
            } else if ("-".equals(change.getText()) ) {
                if (change.getControlText().startsWith("-")) {
                    change.setText("");
                    change.setRange(0, 1);
                    change.setCaretPosition(change.getCaretPosition()-2);
                    change.setAnchor(change.getAnchor()-2);
                    return change ;
                } else {
                    change.setRange(0, 0);
                    return change ;
                }
            }
            return null;
        };

    public String name;
    public TextField valueField;
    public Button decButton;
    public Button incButton;
    public int value;
    public int minValue;
    public int maxValue;
    public int step;
    public BiConsumer<IntegerSpinnerController, Integer> updater;
    
    public IntegerSpinnerController(String name,
                       TextField valueField,
                       Button decButton,
                       Button incButton,
                       int step,
                       BiConsumer<IntegerSpinnerController, Integer> updater) {
        this.name = name;
        this.valueField = valueField;
        this.decButton= decButton;
        this.incButton = incButton;
        this.value = 0;
        this.minValue = 0;
        this.maxValue = 0;
        this.step = step;
        this.updater = updater;
        
        decButton.setOnAction(this::decAction);
        incButton.setOnAction(this::incAction);
        valueField.setOnAction(this::fieldAction);
        valueField.setTextFormatter(new TextFormatter <> (NUMERIC_FILTER));
        valueField.focusedProperty().addListener((o, ov, nv) -> {
            if (!nv) { // focus lost
                //System.out.println("SpinnerData[" + name + "] focusListener");
                processFieldChange();
            }
        });
    }

    public void decAction(ActionEvent event) {
        //System.out.println("SpinnerData[" + name + "] decAction");
        value -= this.step;
        if (value < minValue)
            value = minValue;
        updater.accept(this, value);
    }

    public void fieldAction(ActionEvent event) {
        //System.out.println("SpinnerData[" + name + "] fieldAction");
        processFieldChange();
    }

    public void processFieldChange() {
        //System.out.println("SpinnerData[" + name + "] processFieldChange");
        try {
            int v = Integer.parseInt( valueField.getText());
            if (v < minValue)
                v = minValue;
            if (v > maxValue)
                v = maxValue;
            value = v;
            updater.accept(this, value);
        }
        catch (NumberFormatException ex)
        {
        }
    }

    public void incAction(ActionEvent event) {
        //System.out.println("SpinnerData[" + name + "] incAction");
        this.value += this.step;
        if (value > maxValue)
            value = maxValue;

        updater.accept(this, value);
    }

    public void setValue(int value) {
        this.value = value;
        valueField.setText(Integer.toString(this.value));
    }

    public void updateSpinnerData(int value, int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = value;
        if (!valueField.isFocused())
            valueField.setText(Integer.toString(this.value));
    }
}