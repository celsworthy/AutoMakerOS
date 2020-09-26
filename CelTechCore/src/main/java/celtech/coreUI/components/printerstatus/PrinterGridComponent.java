/*
 * Copyright 2015 CEL UK
 */
package celtech.coreUI.components.printerstatus;

import celtech.Lookup;
import celtech.roboxbase.PrinterColourMap;
import celtech.coreUI.components.PrinterIDDialog;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterConnection;
import celtech.roboxbase.printerControl.model.PrinterException;
import celtech.roboxbase.printerControl.model.PrinterIdentity;
import celtech.roboxbase.printerControl.model.PrinterListChangesListener;
import celtech.roboxbase.printerControl.model.Reel;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * This component houses a square grid of PrinterComponents and is used a
 * printer selector.
 *
 * @author tony
 */
public class PrinterGridComponent extends FlowPane implements PrinterListChangesListener, ComponentIsolationInterface
{
    
    private ObservableList<Printer> connectedPrinters;
    private final Map<Printer, PrinterComponent> printerComponentsByPrinter = new HashMap<>();
    private PrinterIDDialog printerIDDialog = null;
    
    public PrinterGridComponent()
    {
        final int width = 260;
        this.setPrefWidth(width);
        this.setMinWidth(width);
        this.setMaxWidth(width);
        this.setPrefHeight(120);
        this.setMaxHeight(260);
        this.setPrefWrapLength(261);
        
        try
        {
            connectedPrinters = BaseLookup.getConnectedPrinters();
            BaseLookup.getPrinterListChangesNotifier().addListener(this);
        } catch (NoClassDefFoundError error)
        {
            // this should only happen in SceneBuilder
            connectedPrinters = new SimpleListProperty<>();
        }
        printerIDDialog = new PrinterIDDialog();
        clearAndAddAllPrintersToGrid();
    }

    /**
     * Add the given printer component to the given grid coordinates.
     */
    private void addPrinterComponentToGrid(PrinterComponent printerComponent)
    {
        PrinterComponent.Size size;
        if (connectedPrinters.size() > 4)
        {
            size = PrinterComponent.Size.SIZE_SMALL;
        } else if (connectedPrinters.size() > 1)
        {
            size = PrinterComponent.Size.SIZE_MEDIUM;
        } else
        {
            size = PrinterComponent.Size.SIZE_LARGE;
        }
        
        printerComponent.setSize(size);
        this.setHgap(size.getSpacing());
        this.setVgap(size.getSpacing());
        this.getChildren().add(printerComponent);
    }
    
    private void removeAllPrintersFromGrid()
    {
        printerComponentsByPrinter.clear();
        this.getChildren().clear();
    }

    /**
     * Remove the given printer from the display. Update the selected printer to
     * one of the remaining printers.
     */
    public void removePrinter(Printer printer)
    {
        PrinterComponent printerComponent = printerComponentsByPrinter.get(printer);
        this.getChildren().remove(printerComponent);
        printerComponentsByPrinter.remove(printer);
        actOnComponentInterruptible();
    }
    
    public final void clearAndAddAllPrintersToGrid()
    {
        removeAllPrintersFromGrid();
        
        if (connectedPrinters.size() > 0)
        {
            for (Printer printer : connectedPrinters)
            {
                PrinterComponent printerComponent = createPrinterComponentForPrinter(printer);
                addPrinterComponentToGrid(printerComponent);
            }
            
            actOnComponentInterruptible();
        } else
        {
            PrinterComponent printerComponent = createPrinterComponentForPrinter(null);
            addPrinterComponentToGrid(printerComponent);
        }
    }

    /**
     * Create the PrinterComponent for the given printer and set up any
     * listeners on component events.
     */
    private PrinterComponent createPrinterComponentForPrinter(Printer printer)
    {
        PrinterComponent printerComponent = new PrinterComponent(printer, this);
        printerComponent.setOnMouseClicked((MouseEvent event) ->
        {
            handlePrinterClicked(event, printer);
        });
        printerComponentsByPrinter.put(printer, printerComponent);
        return printerComponent;
    }

    /**
     * This is called when the user clicks on the printer component for the
     * given printer, and handles click (select printer) and double-click (go to
     * edit printer details).
     *
     * @param event
     */
    private void handlePrinterClicked(MouseEvent event, Printer printer)
    {
        if (event.getClickCount() == 1)
        {
            selectPrinter(printer);
        }
        if (event.getClickCount() > 1)
        {
            showEditPrinterDetails(printer);
        }
    }
    
    private void selectPrinter(Printer printer)
    {
        if (Lookup.getSelectedPrinterProperty().get() != null)
        {
            PrinterComponent printerComponent = printerComponentsByPrinter.get(Lookup.getSelectedPrinterProperty().get());
            if (printerComponent != null)
            {
                printerComponent.setSelected(false);
            }
        }
        if (printer != null)
        {
            PrinterComponent printerComponent = printerComponentsByPrinter.get(printer);
            printerComponent.setSelected(true);
        }
        Lookup.setSelectedPrinter(printer);
        actOnComponentInterruptible();
    }

    /**
     * Show the printerIDDialog for the given printer.
     */
    private void showEditPrinterDetails(Printer printer)
    {
        Stenographer steno = StenographerFactory.getStenographer(
                PrinterGridComponent.class.getName());
        PrinterColourMap colourMap = PrinterColourMap.getInstance();
        if (printer != null && printer.printerConnectionProperty().isNotEqualTo(PrinterConnection.OFFLINE).get())
        {
            printerIDDialog.setPrinterToUse(printer);
            PrinterIdentity printerIdentity = printer.getPrinterIdentity();
            printerIDDialog.setChosenDisplayColour(colourMap.printerToDisplayColour(
                    printerIdentity.printerColourProperty().get()));
            printerIDDialog.
                    setChosenPrinterName(printerIdentity.printerFriendlyNameProperty().get());
            
            boolean okPressed = printerIDDialog.show();
            
            if (okPressed)
            {
                try
                {
                    PrinterIdentity clonedID = printer.getPrinterIdentity().clone();
                    clonedID.printerFriendlyNameProperty().set(printerIDDialog.getChosenPrinterName());
                    clonedID.printerColourProperty().set(colourMap.displayToPrinterColour(
                            printerIDDialog.getChosenDisplayColour()));
                    printer.updatePrinterIdentity(clonedID);
                } catch (PrinterException ex)
                {
                    steno.error("Error writing printer ID");
                }
            }
        }
    }

    /**
     * Select any one of the active printers. If there are no printers left then
     * select 'null'
     */
    private void selectOnePrinter()
    {
        if (connectedPrinters.size() > 0)
        {
            selectPrinter(connectedPrinters.get(0));
        } else
        {
            selectPrinter(null);
        }
    }
    
    @Override
    public void whenPrinterAdded(Printer printer)
    {
        clearAndAddAllPrintersToGrid();
        selectPrinter(printer);
    }
    
    @Override
    public void whenPrinterRemoved(Printer printer)
    {
        removePrinter(printer);
        clearAndAddAllPrintersToGrid();
        selectOnePrinter();
    }
    
    @Override
    public void whenHeadAdded(Printer printer)
    {
    }
    
    @Override
    public void whenHeadRemoved(Printer printer, Head head)
    {
    }
    
    @Override
    public void whenReelAdded(Printer printer, int reelIndex)
    {
    }
    
    @Override
    public void whenReelRemoved(Printer printer, Reel reel, int reelIndex)
    {
    }
    
    @Override
    public void whenReelChanged(Printer printer, Reel reel)
    {
    }
    
    @Override
    public void whenExtruderAdded(Printer printer, int extruderIndex)
    {
    }
    
    @Override
    public void whenExtruderRemoved(Printer printer, int extruderIndex)
    {
    }
    
    @Override
    public void interruptibilityUpdated(PrinterComponent component)
    {
        actOnComponentInterruptible();
    }
    
    private void actOnComponentInterruptible()
    {
        if (Lookup.getSelectedPrinterProperty().get() != null)
        {
            for (Entry<Printer, PrinterComponent> componentEntry : printerComponentsByPrinter.entrySet())
            {
                if (componentEntry.getKey() == Lookup.getSelectedPrinterProperty().get())
                {
                    componentEntry.getValue().setDisable(false);
                } else
                {
                    PrinterComponent componentToExamine = printerComponentsByPrinter.get(Lookup.getSelectedPrinterProperty().get());
                    if (componentToExamine != null && !componentToExamine.isInterruptible())
                    {
                        componentEntry.getValue().setDisable(true);
                    } else
                    {
                        componentEntry.getValue().setDisable(false);
                    }
                }
            }
        }
    }
}
