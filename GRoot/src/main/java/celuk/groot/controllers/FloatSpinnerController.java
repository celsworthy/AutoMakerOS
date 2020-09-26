package celuk.groot.controllers;

import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class FloatSpinnerController  {
    // This is very similar to IntegerSpinnerController.
    static final UnaryOperator<TextFormatter.Change> NUMERIC_FILTER = (change) -> {
        String newText = change.getControlNewText();
        if (newText.matches("[-+]?[0-9]*\\.?[0-9]*")) { 
            return change;
        } 
        else if ("-".equals(change.getText()) ) {
            if (change.getControlText().startsWith("-")) {
                change.setText("");
                change.setRange(0, 1);
                change.setCaretPosition(change.getCaretPosition()-2);
                change.setAnchor(change.getAnchor()-2);
                return change ;
            } 
            else {
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
    public float value;
    public float minValue;
    public float maxValue;
    public float step;
    public String format;
    BiConsumer<FloatSpinnerController, Float> updater;

    public FloatSpinnerController(String name,
                                  TextField valueField,
                                  Button decButton,
                                  Button incButton,
                                  float step,
                                  String format,
                                  BiConsumer<FloatSpinnerController, Float> updater) {
        this.name = name;
        this.valueField = valueField;
        this.decButton= decButton;
        this.incButton = incButton;
        this.value = 0.0F;
        this.minValue = 0.0F;
        this.maxValue = 0.0F;
        this.step = step;
        this.format = format;
        this.updater = updater;
    }

    public void initialize() {
        // Explicit initialisation to avoid leaking "this"
        // in the constructor, which is apparently bad practise.
        this.valueField.setUserData(this);
        this.decButton.setUserData(this);
        this.incButton.setUserData(this);

        valueField.setTextFormatter(new TextFormatter<>(NUMERIC_FILTER));
        valueField.focusedProperty().addListener((o, ov, nv) -> {
            if (!nv) { // focus lost
                //System.out.println("FloatSpinnerController[" + name + "] focusListener");
                processFieldChange();
            }
        });
    }

    public void decAction(ActionEvent event) {
        //System.out.println("SpinnerData[" + name + "] decAction");
        value -= this.step;
        if (value < minValue)
            value = minValue;
        String v = String.format(format, this.value);
        valueField.setText(v);            
        updater.accept(this, value);
    }

    public void fieldAction(ActionEvent event) {
        //System.out.println("SpinnerData[" + name + "] fieldAction");
        processFieldChange();
    }

    public void processFieldChange() {
        //System.out.println("SpinnerData[" + name + "] processFieldChange");
        try {
            float v = Float.parseFloat(valueField.getText());
            if (v < minValue)
                v = minValue;
            if (v > maxValue)
                v = maxValue;
            if (v != value) {
                value = v;
                String vs = String.format(format, this.value);
                valueField.setText(vs);
                updater.accept(this, value);
            }
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

        String v = String.format(format, this.value);
        valueField.setText(v);
        updater.accept(this, value);
    }

    public void updateSpinnerData(float value, float minValue, float maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = value;
        String v = String.format(format, this.value);
        valueField.setText(v);
    }

    public void setVisible(boolean visible) {
        valueField.setVisible(visible);
        decButton.setVisible(visible);
        incButton.setVisible(visible);
    }
}