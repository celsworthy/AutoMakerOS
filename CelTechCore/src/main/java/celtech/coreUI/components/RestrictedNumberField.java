package celtech.coreUI.components;

import celtech.configuration.units.UnitType;
import celtech.coreUI.DisplayManager;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.utils.Math.MathUtils;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class RestrictedNumberField extends TextField
{

    private final IntegerProperty maxLength = new SimpleIntegerProperty(0);
    private final IntegerProperty allowedDecimalPlaces = new SimpleIntegerProperty(0);
    private final BooleanProperty allowNegative = new SimpleBooleanProperty(false);
    private final BooleanProperty maxValueSet = new SimpleBooleanProperty(false);
    private final DoubleProperty maxValue = new SimpleDoubleProperty(-1);
    private final BooleanProperty minValueSet = new SimpleBooleanProperty(false);
    private final DoubleProperty minValue = new SimpleDoubleProperty(-1);
    private final ObjectProperty<UnitType> units = new SimpleObjectProperty<>(UnitType.NONE);
    private Pattern restrictionPattern = Pattern.compile("[0-9]+");
    private BooleanProperty drivesUndoableOperation = new SimpleBooleanProperty(false);
    private BooleanProperty immediateUpdateMode = new SimpleBooleanProperty(false);

    private boolean lastValueValid = false;
    private double lastValue = 0;
    private double currentValue = 0;

    private final BooleanProperty valueChangedProperty = new SimpleBooleanProperty(false);

    private NumberFormat numberFormatter = null;

    private final String standardAllowedCharacters = "[\u0008\u007f0-9]+";
    private String restriction = "[0-9]+";
    private String decimalSeparator = null;

    public UnitType getUnits()
    {
        return units.get();
    }

    public void setUnits(UnitType units)
    {
        this.units.set(units);
    }

    public ObjectProperty<UnitType> unitsProperty()
    {
        return units;
    }

    public boolean getAllowNegative()
    {
        return allowNegative.get();
    }

    public void setAllowNegative(boolean allowNegative)
    {
        this.allowNegative.set(allowNegative);
    }

    public BooleanProperty allowNegativeProperty()
    {
        return allowNegative;
    }

    public boolean getImmediateUpdateMode()
    {
        return immediateUpdateMode.get();
    }

    public void setImmediateUpdateMode(boolean immediateUpdateMode)
    {
        this.immediateUpdateMode.set(immediateUpdateMode);
    }

    public BooleanProperty immediateUpdateModeProperty()
    {
        return immediateUpdateMode;
    }

    public boolean getMaxValueSet()
    {
        return maxValueSet.get();
    }

    public void setMaxValueSet(boolean maxValueSet)
    {
        this.maxValueSet.set(maxValueSet);
    }

    public BooleanProperty maxValueSetProperty()
    {
        return maxValueSet;
    }

    public boolean getMinValueSet()
    {
        return minValueSet.get();
    }

    public void setMinValueSet(boolean minValueSet)
    {
        this.minValueSet.set(minValueSet);
    }

    public BooleanProperty minValueSetProperty()
    {
        return minValueSet;
    }

    public double getMinValue()
    {
        return minValue.get();
    }

    public void setMinValue(double minValue)
    {
        this.minValue.set(minValue);
        this.minValueSet.set(true);
    }

    public DoubleProperty minValueProperty()
    {
        return minValue;
    }

    public double getMaxValue()
    {
        return maxValue.get();
    }

    public void setMaxValue(double maxValue)
    {
        this.maxValue.set(maxValue);
        this.maxValueSet.set(true);
    }

    public DoubleProperty maxValueProperty()
    {
        return maxValue;
    }

    /**
     *
     * @return
     */
    public int getMaxLength()
    {
        return maxLength.get();
    }

    /**
     * Sets the max length of the text field.
     *
     * @param maxLength The max length.
     */
    public void setMaxLength(int maxLength)
    {
        this.maxLength.set(maxLength);
        configureRestriction();
    }

    /**
     *
     * @return
     */
    public IntegerProperty maxLengthProperty()
    {
        return maxLength;
    }

    /**
     *
     * @return
     */
    public int getAllowedDecimalPlaces()
    {
        return allowedDecimalPlaces.get();
    }

    /**
     * Sets the number of decimal places allowed in this field.
     *
     * @param numberOfDecimalPlaces
     */
    public void setAllowedDecimalPlaces(int numberOfDecimalPlaces)
    {
        this.allowedDecimalPlaces.set(numberOfDecimalPlaces);

        NumberFormat numberFormatter = getNumberFormatter();

        if (numberFormatter != null)
        {
            numberFormatter.setMaximumFractionDigits(allowedDecimalPlaces.get());
            numberFormatter.setMinimumFractionDigits(allowedDecimalPlaces.get());
        }
        configureRestriction();
    }
    
    /**
     * Setter to allow more flexibility for restrictions. Restriction pattern will
     * be overridden by {@link #configureRestriction()} so be careful.
     * @param restrictionPattern 
     */
    public void setRestrictionPattern(Pattern restrictionPattern) {
        this.restrictionPattern = restrictionPattern;
    }

    private void configureRestriction()
    {
        String newRestriction = null;
        if (allowedDecimalPlaces.get() > 0 && maxLength.get() > allowedDecimalPlaces.get())
        {
            newRestriction = "[0-9]{0," + (maxLength.get() - allowedDecimalPlaces.get() - 1) + "}(?:\\" + getDecimalSeparator() + "[0-9]{0," + allowedDecimalPlaces.get() + "})?";
        } else if (allowedDecimalPlaces.get() > 0)
        {
            newRestriction = "[0-9]+(?:\\" + getDecimalSeparator() + "[0-9]{0," + allowedDecimalPlaces.get() + "})?";
        } else
        {
            newRestriction = "[0-9]{0," + maxLength.get() + "}";
        }
        if (allowNegative.get())
        {
            newRestriction = "-?" + newRestriction;
        }
        restrictionPattern = Pattern.compile(newRestriction);
    }

    /**
     *
     * @return
     */
    public IntegerProperty allowedDecimalPlacesProperty()
    {
        return allowedDecimalPlaces;
    }

    public boolean getDrivesUndoableOperation()
    {
        return drivesUndoableOperation.get();
    }

    public void setDrivesUndoableOperation(boolean value)
    {
        this.drivesUndoableOperation.set(value);
    }

    public BooleanProperty drivesUndoableOperationProperty()
    {
        return drivesUndoableOperation;
    }

    /**
     *
     */
    public RestrictedNumberField()
    {
        this.getStyleClass().add(this.getClass().getSimpleName());

        setText("-1");

        addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent event)
            {
                if (event.getCode() == KeyCode.ESCAPE)
                {
                    if (lastValueValid && immediateUpdateMode.get())
                    {
                        setText(getNumberFormatter().format(lastValue));
                        updateNumberValuesFromText();
                    } else
                    {
                    setText(getNumberFormatter().format(currentValue));
                    }
                    event.consume();
                } else if (event.getCode() == KeyCode.ENTER)
                {
                    updateNumberValuesFromText();
                    event.consume();
                } else if (drivesUndoableOperation.get())
                {
                    if ((event.getCode() == KeyCode.Z
                            || event.getCode() == KeyCode.Y)
                            && event.isShortcutDown())
                    {
                        DisplayManager.getInstance().handle(event);
                    }
                }
            }
        });

        focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                if (!newValue && oldValue)
                {
                    updateNumberValuesFromText();
                }
            }
        });
    }

    private void enforceRestriction(String oldText, IndexRange oldSelectionRange)
    {
        Matcher m = restrictionPattern.matcher(this.getText());
        if (!m.matches())
        {
            this.setText(oldText);
            this.selectRange(oldSelectionRange.getStart(), oldSelectionRange.getEnd());
        } else if (immediateUpdateMode.get())
        {
            updateNumberValuesFromText();
        }
    }

    @Override
    public void replaceText(int start, int end, String text)
    {
        String oldText = this.getText();
        IndexRange oldSelectionRange = this.getSelection();

        super.replaceText(start, end, text);

        enforceRestriction(oldText, oldSelectionRange);
    }

    private void updateNumberValuesFromText()
    {
        try
        {
            double candidateValue = getNumberFormatter().parse(this.getText()).doubleValue();
            double boundedCandidateValue = doMinMax(candidateValue);

            if (MathUtils.compareDouble(currentValue, boundedCandidateValue, 0.000001) != MathUtils.EQUAL)
            {
                lastValue = currentValue;
                lastValueValid = true;
                currentValue = boundedCandidateValue;
                
                valueChangedProperty.set(!valueChangedProperty.get());
            }
            if (MathUtils.compareDouble(candidateValue, boundedCandidateValue, 0.000001) != MathUtils.EQUAL)
            {
                //Update the text again to make sure any min/max issues are dealt with
                setText(getNumberFormatter().format(currentValue));
            }
        } catch (ParseException ex)
        {
        }
    }

    @Override
    public void replaceSelection(String text)
    {
        String oldText = this.getText();
        IndexRange oldSelectionRange = this.getSelection();

        super.replaceSelection(text);

        enforceRestriction(oldText, oldSelectionRange);
    }

    @Override
    public void replaceText(IndexRange range, String text)
    {
        String oldText = this.getText();
        IndexRange oldSelectionRange = this.getSelection();

        super.replaceText(range, text);

        enforceRestriction(oldText, oldSelectionRange);
    }

    private NumberFormat getNumberFormatter()
    {
        if (numberFormatter == null)
        {
            Locale usersLocale = null;
            try
            {
                Locale applicationLocale = BaseLookup.getApplicationLocale();
                if (applicationLocale == null)
                {
                    usersLocale = Locale.getDefault();
                } else
                {
                    usersLocale = applicationLocale;
                }
                numberFormatter = NumberFormat.getInstance(usersLocale);
            } catch (NoClassDefFoundError ex)
            {
                //We should only be here if we're being loaded by Scene Builder
                numberFormatter = NumberFormat.getInstance();
            }
            numberFormatter.setMaximumFractionDigits(allowedDecimalPlaces.get());
            numberFormatter.setMinimumFractionDigits(allowedDecimalPlaces.get());
        }

        return numberFormatter;
    }

    private String getDecimalSeparator()
    {
        if (decimalSeparator == null)
        {
            try
            {
                decimalSeparator = Character.toString(new DecimalFormatSymbols(BaseLookup.getApplicationLocale()).getDecimalSeparator());
            } catch (NoClassDefFoundError ex)
            {
                //We should only be here if we're being loaded by Scene Builder
                decimalSeparator = ".";
            }
        }
        return decimalSeparator;
    }

    public ReadOnlyBooleanProperty valueChangedProperty()
    {
        return valueChangedProperty;
    }

    private void setValueFromExternalSource(double candidateValue)
    {
        double boundedCandidateValue = doMinMax(candidateValue);
        if (MathUtils.compareDouble(currentValue, boundedCandidateValue, 0.000001) != MathUtils.EQUAL)
        {
            currentValue = boundedCandidateValue;
            setText(getNumberFormatter().format(currentValue));
            lastValueValid = false;
        }
    }

    public void setValue(double value)
    {
        setValueFromExternalSource(value);
    }

    public void setValue(float value)
    {
        setValueFromExternalSource(value);
    }

    public void setValue(int value)
    {
        setValueFromExternalSource(value);
    }

    private double doMinMax(final double inputValue)
    {
        double outputValue = inputValue;

        if (maxValueSet.get() && outputValue > maxValue.get())
        {
            outputValue = maxValue.get();
        }

        if (minValueSet.get() && outputValue < minValue.get())
        {
            outputValue = minValue.get();
        }

        return outputValue;
    }

    public float getAsFloat()
    {
        return (float) currentValue;
    }

    public double getAsDouble()
    {
        return currentValue;
    }

    public int getAsInt()
    {
        return (int) currentValue;
    }
}
