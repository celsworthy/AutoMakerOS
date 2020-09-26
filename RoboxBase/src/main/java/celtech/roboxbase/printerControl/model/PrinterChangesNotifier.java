/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.printerControl.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;

/**
 * PrinterChangesNotifier listens to a list of printers and notifies registered listeners about the
 * following events:
 * <p>
 * - Head added to printer<p>
 * - Head removed from printer<p>
 * - Reel added to printer (with reel index) <p>
 * - Extruder added (with extruder index)
 * <p>
 * - Reel removed from printer (with reel) <p>
 * - Extruder removed (with extruder index)
 * <p>
 * - Reel changed<p>
 * - Printer Identity changed<p>
 * To be done:
 * <p>
 * - Filament detected on extruder (with extruder index)
 * <p>
 * - Filament removed from extruder (with extruder index)
 * <p>
 *
 * @author tony
 */
public class PrinterChangesNotifier
{

    List<PrinterChangesListener> listeners = new ArrayList<>();
    private final Map<Reel, ReelChangesListener> reelListeners = new HashMap<>();
    private final Map<Reel, ReelChangesNotifier> reelNotifiers = new HashMap<>();

    private ChangeListener<Boolean> extruder0Listener;
    private ChangeListener<Boolean> extruder1Listener;

    public PrinterChangesNotifier(Printer printer)
    {
        printer.headProperty().addListener(
            (ObservableValue<? extends Head> observable, Head oldValue, Head newValue) ->
            {
                if (newValue != null)
                {
                    for (PrinterChangesListener printerChangesListener : listeners)
                    {
                        printerChangesListener.whenHeadAdded();
                    }
                } else
                {
                    for (PrinterChangesListener printerChangesListener : listeners)
                    {
                        printerChangesListener.whenHeadRemoved(oldValue);
                    }
                }
            });

        printer.reelsProperty().addListener(new MapChangeListener<Integer, Reel>()
        {
            @Override
            public void onChanged(
                MapChangeListener.Change<? extends Integer, ? extends Reel> change)
            {
                if (change.wasAdded())
                {
                    for (PrinterChangesListener listener : listeners)
                    {
                        listener.whenReelAdded(change.getKey(), change.getValueAdded());
                        setupReelChangesNotifier(change.getValueAdded());
                    }
                } else
                {
                    for (PrinterChangesListener listener : listeners)
                    {
                        listener.whenReelRemoved(change.getKey(), change.getValueRemoved());
                        setupReelChangesNotifier(change.getValueRemoved());
                    }
                }
            }
        });

        Extruder extruder0 = printer.extrudersProperty().get(0);
        if (extruder0 != null)
        {
            extruder0Listener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
            {
                for (PrinterChangesListener listener : listeners)
                {
                    if (printer.extrudersProperty().get(0).isFittedProperty().get())
                    {
                        listener.whenExtruderAdded(0);
                    } else {
                        listener.whenExtruderRemoved(0);
                    }
                }
            };
            extruder0.isFittedProperty().addListener(extruder0Listener);
        }
        
        Extruder extruder1 = printer.extrudersProperty().get(1);
        if (extruder1 != null)
        {
            extruder1Listener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
            {
                for (PrinterChangesListener listener : listeners)
                {
                    if (printer.extrudersProperty().get(1).isFittedProperty().get())
                    {
                        listener.whenExtruderAdded(1);
                    } else {
                        listener.whenExtruderRemoved(1);
                    }
                }
            };
            extruder1.isFittedProperty().addListener(extruder1Listener);
        }        
    }

    public void addListener(PrinterChangesListener listener)
    {
        this.listeners.add(listener);
    }

    void removeListener(PrinterChangesListener listener)
    {
        this.listeners.remove(listener);
    }

    private void setupReelChangesNotifier(Reel reel)
    {
        ReelChangesNotifier reelChangesNotifier = new ReelChangesNotifier(reel);
        ReelChangesListener reelChangesListener = new ReelChangesListener()
        {
            @Override
            public void whenReelChanged()
            {
                fireWhenReelChanged(reel);
            }

        };
        reelListeners.put(reel, reelChangesListener);
        reelNotifiers.put(reel, reelChangesNotifier);
        reelChangesNotifier.addListener(reelChangesListener);
    }

    private void removeReelChangesNotifier(Reel reel)
    {
        reelNotifiers.get(reel).removeListener(reelListeners.get(reel));
    }

    private void fireWhenReelChanged(Reel reel)
    {
        listeners.stream().
            forEach((listener) ->
                {
                    listener.whenReelChanged(reel);
            });
    }
}
