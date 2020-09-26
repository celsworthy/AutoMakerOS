/*
 * Copyright 2015 CEL UK
 */
package celtech.coreUI;

import javafx.scene.layout.Region;

/**
 *
 * @author tony
 */
public interface SpinnerControl
{
        /**
     * Show the spinner, and keep it centred on the given region.
     */
    public void startSpinning(Region centreRegion);

    /**
     * Stop and hide the spinner.
     */
    public void stopSpinning();
    
}
