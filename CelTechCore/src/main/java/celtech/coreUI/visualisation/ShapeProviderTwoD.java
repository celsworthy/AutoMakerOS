/*
 * Copyright 2014 CEL UK
 */

package celtech.coreUI.visualisation;

/**
 *
 * @author tony
 */
public interface ShapeProviderTwoD extends ShapeProvider
{

    public double getScaledWidth();

    public double getScaledHeight();
    
    public double getCentreX();
    
    public double getCentreY();    
}
