package celtech.roboxbase.printerControl.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class Extruder
{

    private static final Stenographer steno = StenographerFactory.getStenographer(Extruder.class.getName());
    private final String extruderAxisLetter;
    protected final BooleanProperty filamentLoaded = new SimpleBooleanProperty(false);
    protected final BooleanProperty indexWheelState = new SimpleBooleanProperty(false);
    protected final BooleanProperty canEject = new SimpleBooleanProperty(false);
    protected final BooleanProperty isFitted = new SimpleBooleanProperty(false);
    protected final FloatProperty filamentDiameter = new SimpleFloatProperty(0);
    protected final FloatProperty extrusionMultiplier = new SimpleFloatProperty(0);
    protected final FloatProperty lastFeedrateMultiplierInUse = new SimpleFloatProperty(0);

    public Extruder(String extruderAxisLetter)
    {
        this.extruderAxisLetter = extruderAxisLetter;
    }

    public String getExtruderAxisLetter()
    {
        return extruderAxisLetter;
    }

    public int getExtruderIndex()
    {
        return getExtruderIndexForLetter(extruderAxisLetter);
    }

    public static int getExtruderIndexForLetter(String letter)
    {
        int returnValue = 0;

        if (letter != null)
        {
            switch (letter)
            {
                case "E":
                    returnValue = 0;
                    break;
                case "D":
                    returnValue = 1;
                    break;
                default:
                    steno.error("Couldn't look up extruder index: " + letter);
                    break;
            }
        } else
        {
            steno.error("Asked to look up extruder index when it was not set");
        }

        return returnValue;
    }

    public static String getExtruderLetterForNumber(int number)
    {
        String returnValue = "";

        switch (number)
        {
            case 0:
                returnValue = "E";
                break;
            case 1:
                returnValue = "D";
                break;
            default:
                steno.error("Couldn't look up extruder number: " + number);
                break;
        }

        return returnValue;
    }

    public BooleanProperty filamentLoadedProperty()
    {
        return filamentLoaded;
    }

    public ReadOnlyBooleanProperty indexWheelStateProperty()
    {
        return indexWheelState;
    }

    public ReadOnlyBooleanProperty canEjectProperty()
    {
        return canEject;
    }

    public BooleanProperty isFittedProperty()
    {
        return isFitted;
    }

    public ReadOnlyFloatProperty filamentDiameterProperty()
    {
        return filamentDiameter;
    }

    public ReadOnlyFloatProperty extrusionMultiplierProperty()
    {
        return extrusionMultiplier;
    }

    public ReadOnlyFloatProperty lastFeedrateMultiplierInUseProperty()
    {
        return lastFeedrateMultiplierInUse;
    }
}
