/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.printerControl.model;

/**
 *
 * @author tony
 */
public interface PrinterChangesListener
{
    public void whenHeadAdded();
    public void whenHeadRemoved(Head head);
    public void whenReelAdded(int reelIndex, Reel reel);
    public void whenReelRemoved(int reelIndex, Reel reel);
    public void whenReelChanged(Reel reel);
    public void whenExtruderAdded(int extruderIndex);
    public void whenExtruderRemoved(int extruderIndex);    
}
