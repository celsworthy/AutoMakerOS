package celtech.roboxbase.importers.twod.svg;

import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class ShortestArc
{

    private final Stenographer steno = StenographerFactory.getStenographer(ShortestArc.class.getName());

    private double stepValue = Math.PI / 18;
    private double angularDifference = 0;
    private double targetAngle = 0;
    private double currentAngle = 0;

    public ShortestArc(double startingCurrentAngle, double startingTargetAngle)
    {
        double differenceBetweenVectors = startingTargetAngle - startingCurrentAngle;

        if (differenceBetweenVectors > Math.PI)
        {
            targetAngle = startingTargetAngle - (Math.PI * 2.0);
            currentAngle = startingCurrentAngle;
        } else if (differenceBetweenVectors < -Math.PI)
        {
            targetAngle = startingTargetAngle + (Math.PI * 2.0);
            currentAngle = startingCurrentAngle;
        }
        else
        {
            currentAngle = startingCurrentAngle;
            targetAngle = startingTargetAngle;
        }

        angularDifference = targetAngle - currentAngle;

        if (angularDifference < 0)
        {
            stepValue = -stepValue;
            //steno.info("Direction = backwards");
        } else
        {
            //steno.info("Direction = forwards");
        }
    }

    public double getAngularDifference()
    {
        return angularDifference;
    }

    public double getStepValue()
    {
        return stepValue;
    }

    public double getCurrentAngle()
    {
        return currentAngle;
    }

    public double getTargetAngle()
    {
        return targetAngle;
    }
}
