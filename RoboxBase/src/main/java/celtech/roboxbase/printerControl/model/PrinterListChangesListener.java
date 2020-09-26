package celtech.roboxbase.printerControl.model;

/*
 * Copyright 2014 CEL UK
 */


/**
 *
 * @author tony
 */
public interface PrinterListChangesListener
{

    public void whenPrinterAdded(Printer printer);

    public void whenPrinterRemoved(Printer printer);

    public void whenHeadAdded(Printer printer);

    public void whenHeadRemoved(Printer printer, Head head);

    public void whenReelAdded(Printer printer, int reelIndex);

    public void whenReelRemoved(Printer printer, Reel reel, int reelIndex);
    
    public void whenReelChanged(Printer printer, Reel reel);
    
    public void whenExtruderAdded(Printer printer, int extruderIndex);

    public void whenExtruderRemoved(Printer printer, int extruderIndex);    
    
}
