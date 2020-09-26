/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.utils;

import celtech.roboxbase.printerControl.model.PrinterChangesNotifier;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.PrinterChangesListener;
import celtech.roboxbase.printerControl.model.Reel;
import celtech.roboxbase.printerControl.model.TestPrinter;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author tony
 */
public class PrinterChangesNotifierTest extends BaseEnvironmentConfiguredTest
{

    @Test
    public void testWhenHeadAdded()
    {
        TestPrinter printer = new TestPrinter();
        PrinterChangesNotifier notifier = new PrinterChangesNotifier(printer);
        TestPrinterChangesListener listener = new TestPrinterChangesListener();
        notifier.addListener(listener);

        printer.addHead();

        assertTrue(listener.headAdded);
    }

    @Test
    public void testWhenHeadRemoved()
    {
        TestPrinter printer = new TestPrinter();
        PrinterChangesNotifier notifier = new PrinterChangesNotifier(printer);
        TestPrinterChangesListener listener = new TestPrinterChangesListener();
        notifier.addListener(listener);

        printer.addHead();
        printer.removeHead();

        assertTrue(listener.headAdded);
    }

    @Test
    public void testWhenReelAdded()
    {
        TestPrinter printer = new TestPrinter();
        PrinterChangesNotifier notifier = new PrinterChangesNotifier(printer);
        TestPrinterChangesListener listener = new TestPrinterChangesListener();
        notifier.addListener(listener);

        printer.addReel(0);

        assertTrue(listener.reel0Added);
    }
    
    @Test
    public void testWhenReelRemoved()
    {
        TestPrinter printer = new TestPrinter();
        PrinterChangesNotifier notifier = new PrinterChangesNotifier(printer);
        TestPrinterChangesListener listener = new TestPrinterChangesListener();
        notifier.addListener(listener);

        printer.addReel(0);
        printer.removeReel(0);

        assertTrue(listener.reel0Removed);
    }

    @Test
    public void testWhenReelChanged()
    {
        TestPrinter printer = new TestPrinter();
        PrinterChangesNotifier notifier = new PrinterChangesNotifier(printer);
        TestPrinterChangesListener listener = new TestPrinterChangesListener();
        notifier.addListener(listener);

        printer.addReel(0);
        printer.changeReel(0);

        assertTrue(listener.reel0Changed);
    }       

    private static class TestPrinterChangesListener implements PrinterChangesListener
    {

        public boolean headAdded = false;
        public boolean headRemoved = false;
        public boolean reel0Added = false;
        public boolean reel0Removed = false;
        public boolean reel0Changed = false;

        @Override
        public void whenHeadAdded()
        {
            headAdded = true;
        }

        @Override
        public void whenHeadRemoved(Head head)
        {
            headRemoved = true;
        }

        @Override
        public void whenReelAdded(int reelIndex, Reel reel)
        {
            reel0Added = true;
        }

        @Override
        public void whenReelRemoved(int reelIndex, Reel reel)
        {
            reel0Removed = true;
        }
        
        @Override
        public void whenReelChanged(Reel reel)
        {
            reel0Changed = true;
        }        

        @Override
        public void whenExtruderAdded(int extruderIndex)
        {
        }

        @Override
        public void whenExtruderRemoved(int extruderIndex)
        {
        }
        
    }

}
