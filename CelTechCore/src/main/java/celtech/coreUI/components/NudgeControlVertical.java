package celtech.coreUI.components;

import celtech.coreUI.components.RestrictedNumberField;
import java.io.IOException;
import java.text.ParseException;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 *
 * @author Ian
 */
public class NudgeControlVertical extends VBox
{

    private final DoubleProperty deltaValue = new SimpleDoubleProperty(0);
    private final DoubleProperty maxValue = new SimpleDoubleProperty(0);
    private final DoubleProperty minValue = new SimpleDoubleProperty(0);

    @FXML
    private Button upButton;

    @FXML
    private RestrictedNumberField numberDisplay;

    @FXML
    private Button downButton;

    @FXML
    private void upPressed(ActionEvent event)
    {
            double limitedValue = getLimitedValue(numberDisplay.getAsDouble() + deltaValue.get());
        numberDisplay.setValue(limitedValue);
    }

    @FXML
    private void downPressed(ActionEvent event)
    {
            double limitedValue = getLimitedValue(numberDisplay.getAsDouble() - deltaValue.get());
        numberDisplay.setValue(limitedValue);
    }

    private double getLimitedValue(double newValue)
    {
        double limitedValue = newValue;

        if (limitedValue > maxValue.get())
        {
            limitedValue = maxValue.get();
        } else if (limitedValue < minValue.get())
        {
            limitedValue = minValue.get();
        }

        return limitedValue;
    }

    public NudgeControlVertical()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/celtech/resources/fxml/nudgeControlVertical.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        fxmlLoader.setClassLoader(this.getClass().getClassLoader());

        try
        {
            fxmlLoader.load();
        } catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }

        numberDisplay.setValue(0);
    }

    public void setDecimalPlaces(int numberOfDecimalPlaces)
    {
        numberDisplay.setAllowedDecimalPlaces(numberOfDecimalPlaces);
    }

    public int getDecimalPlaces()
    {
        return numberDisplay.getAllowedDecimalPlaces();
    }

    public IntegerProperty getDecimalPlacesProperty()
    {
        return numberDisplay.allowedDecimalPlacesProperty();
    }

    public double getValue()
    {
        double value = 0;

            numberDisplay.getAsDouble();

        return value;
    }

    public void setValue(double value)
    {
        numberDisplay.setValue(value);
    }

    public double getDeltaValue()
    {
        return deltaValue.get();
    }

    public void setDeltaValue(double value)
    {
        deltaValue.set(value);
    }

    public DoubleProperty getDeltaValueProperty()
    {
        return deltaValue;
    }

    public double getMaxValue()
    {
        return maxValue.get();
    }

    public void setMaxValue(double value)
    {
        maxValue.set(value);
    }

    public DoubleProperty getMaxValueProperty()
    {
        return maxValue;
    }

    public DoubleProperty getMinValueProperty()
    {
        return minValue;
    }

    public double getMinValue()
    {
        return minValue.get();
    }

    public void setMinValue(double value)
    {
        minValue.set(value);
    }
}
