package celtech.coreUI.components.Notifications;

import celtech.Lookup;
import celtech.appManager.Project;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.Printer;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class ProgressDisplay extends VBox
{
    private static final Stenographer STENO = StenographerFactory.getStenographer(ProgressDisplay.class.getName());

    private Printer printerInUse = null;
    private PrintStatusBar stateDisplayBar = new PrintStatusBar();
    private BedHeaterStatusBar bedTemperatureDisplayBar = new BedHeaterStatusBar();
    private MaterialHeatingStatusBar material1TemperatureDisplayBar;
    private MaterialHeatingStatusBar material2TemperatureDisplayBar;
    private PrintPreparationStatusBar printPreparationDisplayBar = new PrintPreparationStatusBar();
    private final List<Node> printerRelatedNodes = new ArrayList<>();

    private final ChangeListener<Boolean> headDataChangedListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
    {
        if (printerInUse != null
                && printerInUse.headProperty().get() != null)
        {
            createMaterialHeatBars(printerInUse.headProperty().get());
        }
    };

    private final ChangeListener<Head> headListener = (ObservableValue<? extends Head> ov, Head oldHead, Head newHead) ->
    {
        if (oldHead != null)
        {
            oldHead.dataChangedProperty().removeListener(headDataChangedListener);
        }

        if (newHead != null)
        {
            newHead.dataChangedProperty().removeListener(headDataChangedListener);
            newHead.dataChangedProperty().addListener(headDataChangedListener);
        }

        createMaterialHeatBars(newHead);
    };

    public ProgressDisplay()
    {
        setFillWidth(true);
        setPickOnBounds(false);

        Lookup.getSelectedPrinterProperty().addListener((observable, oldSelection, newSelection) -> {
            unbindFromPrinter();
            if (newSelection != null) {
                bindToPrinter(newSelection);
            }
        });
        
        Lookup.getSelectedProjectProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                bindToProject(newValue);
            }
        });
        
        addPrinterElementToDisplay(bedTemperatureDisplayBar, printPreparationDisplayBar, stateDisplayBar);
    }

    private void bindToPrinter(Printer printer)
    {
        if (this.printerInUse != null)
        {
            unbindFromPrinter();
        }
        this.printerInUse = printer;
        stateDisplayBar.bindToPrinter(printer);
        printPreparationDisplayBar.bindToPrinter(printer);
        bedTemperatureDisplayBar.bindToPrinterSystems(printer.getPrinterAncillarySystems());

        printer.headProperty().addListener(headListener);
        if (printer.headProperty().get() != null)
        {
            printerInUse.headProperty().get().dataChangedProperty().addListener(headDataChangedListener);
            createMaterialHeatBars(printer.headProperty().get());
        }
    }
    
    private void bindToProject(Project project) {
        printPreparationDisplayBar.unbindFromProject();
        printPreparationDisplayBar.bindToProject(project);
    }

    private void destroyMaterialHeatBars()
    {
        if (material1TemperatureDisplayBar != null)
        {
            material1TemperatureDisplayBar.unbindAll();
            removePrinterElementFromDisplay(material1TemperatureDisplayBar);
            material1TemperatureDisplayBar = null;
        }

        if (material2TemperatureDisplayBar != null)
        {
            material2TemperatureDisplayBar.unbindAll();
            removePrinterElementFromDisplay(material2TemperatureDisplayBar);
            material2TemperatureDisplayBar = null;
        }
    }

    private void createMaterialHeatBars(Head head)
    {
        destroyMaterialHeatBars();
        if (head != null
                && head.getNozzleHeaters().size() > 0)
        {
            int materialNumber = 1;
            if (head.headTypeProperty().get() == Head.HeadType.DUAL_MATERIAL_HEAD)
            {
                materialNumber = 2;
            }

            material1TemperatureDisplayBar = new MaterialHeatingStatusBar(head.getNozzleHeaters().get(0), materialNumber, head.getNozzleHeaters().size() == 1);
            addPrinterElementToStartOfDisplay(material1TemperatureDisplayBar);
        }

        if (head != null
                && head.getNozzleHeaters().size() == 2)
        {
            //Must be DM - material 1
            material2TemperatureDisplayBar = new MaterialHeatingStatusBar(head.getNozzleHeaters().get(1), 1, false);
            addPrinterElementToStartOfDisplay(material2TemperatureDisplayBar);
        }
    }

    private void unbindFromPrinter()
    {
        if (printerInUse != null)
        {
            if (printerInUse.headProperty().get() != null)
            {
                printerInUse.headProperty().get().dataChangedProperty().removeListener(headDataChangedListener);
                printerInUse.headProperty().removeListener(headListener);
            }

            destroyMaterialHeatBars();
            stateDisplayBar.unbindAll();
            printPreparationDisplayBar.unbindFromPrinter();
            bedTemperatureDisplayBar.unbindAll();
        }
        printerInUse = null;
    }

    public GenericProgressBar addGenericProgressBarToDisplay(String title, ReadOnlyBooleanProperty displayProgressBar, ReadOnlyDoubleProperty progressProperty)
    {
        GenericProgressBar progressBar = new GenericProgressBar(title, displayProgressBar, progressProperty);
        getChildren().add(progressBar);
        return progressBar;
    }

    public void removeGenericProgressBarFromDisplay(GenericProgressBar progressBar)
    {
        if (progressBar != null)
        {
            progressBar.destroyBar();
            getChildren().remove(progressBar);
        }
    }

    private void addPrinterElementToStartOfDisplay(Node node)
    {
        printerRelatedNodes.add(node);
        getChildren().add(node);
    }

    private void addPrinterElementToDisplay(Node... nodes)
    {
        for (Node node : nodes)
        {
            if(!printerRelatedNodes.contains(node)) 
            {
                printerRelatedNodes.add(node);
                getChildren().add(node);
            }
            else
            {
                STENO.trace("Printer element " + node.toString() + " has already been added to display.");
            }
        }
    }

    private void removePrinterElementFromDisplay(Node... nodes)
    {
        for (Node node : nodes)
        {
            printerRelatedNodes.remove(node);
            getChildren().remove(node);
        }
    }

    private void removeAllPrinterElementsFromDisplay()
    {
        getChildren().removeAll(printerRelatedNodes);
        printerRelatedNodes.clear();
    }
}
