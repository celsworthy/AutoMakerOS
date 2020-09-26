//TODO remove when chop finished

//package celtech.coreUI.controllers.panels;
//
//import celtech.Lookup;
//import celtech.appManager.Project;
//import celtech.configuration.Filament;
//import celtech.coreUI.components.material.MaterialComponent;
//import celtech.coreUI.components.printerstatus.PrinterGridComponent;
//import celtech.coreUI.controllers.PrinterSettings;
//import celtech.printerControl.model.Extruder;
//import celtech.printerControl.model.Head;
//import celtech.printerControl.model.Printer;
//import celtech.printerControl.model.Reel;
//import celtech.utils.PrinterListChangesListener;
//import java.net.URL;
//import java.util.ResourceBundle;
//import javafx.beans.property.ObjectProperty;
//import javafx.beans.property.SimpleObjectProperty;
//import javafx.beans.value.ChangeListener;
//import javafx.beans.value.ObservableValue;
//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//import javafx.scene.Node;
//import javafx.scene.layout.VBox;
//import libertysystems.stenographer.Stenographer;
//import libertysystems.stenographer.StenographerFactory;
//
///**
// * FXML Controller class
// *
// * @author Ian Hudson @ Liberty Systems Limited
// */
//public class SettingsSidePanelController implements Initializable, SidePanelManager,
//    PrinterListChangesListener
//{
//
//    private final Stenographer steno = StenographerFactory.getStenographer(
//        SettingsSidePanelController.class.getName());
//    private PrinterSettings printerSettings = null;
//
//    @FXML
//    private VBox materialContainer;
//
//    @FXML
//    private PrinterGridComponent printerGrid;
//
//    private Printer previouslySelectedPrinter = null;
//    private final ObjectProperty<Printer> currentPrinter = new SimpleObjectProperty<>();
//    private Project currentProject;
//    /**
//     * filament0 is updated by the MaterialComponent for extruder 0, then changes to filament0 are
//     * reflected in PrinterSettings filament 0.
//     */
//    private ObjectProperty<Filament> filament0 = new SimpleObjectProperty<>(null);
//    private ObjectProperty<Filament> filament1 = new SimpleObjectProperty<>(null);
//
//    /**
//     * Initialises the controller class.
//     *
//     * @param url
//     * @param rb
//     */
//    @Override
//    public void initialize(URL url, ResourceBundle rb)
//    {
//        currentPrinter.bind(printerGrid.getSelectedPrinter());
//        currentPrinter.addListener(
//            (ObservableValue<? extends Printer> observable, Printer oldValue, Printer newValue) ->
//            {
//                if (previouslySelectedPrinter != null)
//                {
//                    unbindPrinter(previouslySelectedPrinter);
//                }
//                previouslySelectedPrinter = currentPrinter.get();
//
//                if (currentPrinter.get() != null)
//                {
//                    bindPrinter(currentPrinter.get());
//                    configureMaterialComponents(currentPrinter.get());
//                }
//            });
//
//        Lookup.getSelectedProjectProperty().addListener(
//            (ObservableValue<? extends Project> observable, Project oldValue, Project newValue) ->
//            {
//                whenProjectChanged(newValue);
//            });
//        Lookup.getPrinterListChangesNotifier().addListener(this);
//
//    }
//
//    private ChangeListener<Filament> filament0Listener;
//    private ChangeListener<Filament> filament1Listener;
//
//    private void removeFilamentListeners()
//    {
//        if (filament0Listener != null)
//        {
//            filament0.removeListener(filament0Listener);
//        }
//        if (filament1Listener != null)
//        {
//            filament1.removeListener(filament1Listener);
//        }
//    }
//
//    private void setupFilamentListeners()
//    {
//
//        filament0Listener = (ObservableValue<? extends Filament> observable, Filament oldValue, Filament newValue) ->
//        {
//            if (printerSettings != null)
//            {
//                printerSettings.setFilament0(newValue);
//            }
//        };
//
//        filament1Listener = (ObservableValue<? extends Filament> observable, Filament oldValue, Filament newValue) ->
//        {
//            if (printerSettings != null)
//            {
//                printerSettings.setFilament1(newValue);
//            }
//        };
//
//        filament0.addListener(filament0Listener);
//        filament1.addListener(filament1Listener);
//
//        printerSettings.setFilament0(filament0.get());
//        printerSettings.setFilament1(filament1.get());
//    }
//
//    private ChangeListener<Filament> materialFilament0Listener = (ObservableValue<? extends Filament> observable, Filament oldValue, Filament newValue) ->
//    {
//        filament0.set(newValue);
//    };
//
//    private ChangeListener<Filament> materialFilament1Listener = (ObservableValue<? extends Filament> observable, Filament oldValue, Filament newValue) ->
//    {
//        filament1.set(newValue);
//    };
//
//    /**
//     * Show the correct number of MaterialComponents according to the number of extruders, and
//     * configure them to the printer and extruder number. Listen to changes on the chosen filament
//     * and update the settingsScreenState accordingly.
//     */
//    private void configureMaterialComponents(Printer printer)
//    {
//        for (Node materialNode : materialContainer.getChildren())
//        {
//            MaterialComponent previousMaterialComponent = (MaterialComponent) materialNode;
//            previousMaterialComponent.getSelectedFilamentProperty().removeListener(
//                materialFilament0Listener);
//            previousMaterialComponent.getSelectedFilamentProperty().removeListener(
//                materialFilament1Listener);
//        }
//
//        materialContainer.getChildren().clear();
//        for (int extruderNumber = 0; extruderNumber < 2; extruderNumber++)
//        {
//            Extruder extruder = printer.extrudersProperty().get(extruderNumber);
//            if (extruder.isFittedProperty().get())
//            {
//                MaterialComponent materialComponent
//                    = new MaterialComponent(MaterialComponent.Mode.SETTINGS, printer, extruderNumber);
//                materialContainer.getChildren().add(materialComponent);
//
//                if (extruderNumber == 0)
//                {
//                    materialComponent.getSelectedFilamentProperty().addListener(
//                        materialFilament0Listener);
//                } else
//                {
//                    materialComponent.getSelectedFilamentProperty().addListener(
//                        materialFilament1Listener);
//                }
//
//                if (materialComponent.getSelectedFilamentProperty().get() != null)
//                {
//                    // the materialComponent has detected a reel and set its filament
//                    // accordingly.
//                    if (extruderNumber == 0)
//                    {
//                        filament0.set(materialComponent.getSelectedFilamentProperty().get());
//                    } else
//                    {
//                        filament1.set(materialComponent.getSelectedFilamentProperty().get());
//                    }
//                } else
//                {
//                    // use printer settings value as the default because there is no reel
//                    // loaded
//                    if (currentProject != null)
//                    {
//                        if (extruderNumber == 0)
//                        {
//                            materialComponent.setSelectedFilamentInComboBox(
//                                currentProject.getPrinterSettings().getFilament0());
//                        } else
//                        {
//                            materialComponent.setSelectedFilamentInComboBox(
//                                currentProject.getPrinterSettings().getFilament1());
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private ChangeListener<Boolean> extruder0Listener;
//    private ChangeListener<Boolean> extruder1Listener;
//
//    private void bindPrinter(Printer printer)
//    {
//        // in case the extruder is added to the model after the printer is detected
//        Extruder extruder0 = printer.extrudersProperty().get(0);
//        if (extruder0 != null)
//        {
//            extruder0Listener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
//            {
//                configureMaterialComponents(printer);
//            };
//            extruder0.isFittedProperty().addListener(extruder0Listener);
//        }
//
//        Extruder extruder1 = printer.extrudersProperty().get(1);
//        if (extruder1 != null)
//        {
//            extruder1Listener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
//            {
//                configureMaterialComponents(printer);
//            };
//            extruder1.isFittedProperty().addListener(extruder1Listener);
//        }
//    }
//
//    private void unbindPrinter(Printer printer)
//    {
//        if (extruder0Listener != null)
//        {
//            Extruder extruder0 = printer.extrudersProperty().get(0);
//            extruder0.isFittedProperty().removeListener(extruder0Listener);
//        }
//        if (extruder1Listener != null)
//        {
//            Extruder extruder1 = printer.extrudersProperty().get(1);
//            extruder1.isFittedProperty().removeListener(extruder1Listener);
//        }
//    }
//
//    @Override
//    public void configure(Initializable slideOutController)
//    {
//    }
//
//    private void whenProjectChanged(Project project)
//    {
//        removeFilamentListeners();
//
//        currentProject = project;
//        printerSettings = project.getPrinterSettings();
//
//        printerSettings.setFilament0(filament0.get());
//        printerSettings.setFilament0(filament1.get());
//
//        setupFilamentListeners();
//    }
//
//    @Override
//    public void whenPrinterAdded(Printer printer)
//    {
//    }
//
//    @Override
//    public void whenPrinterRemoved(Printer printer)
//    {
//    }
//
//    @Override
//    public void whenHeadAdded(Printer printer)
//    {
//    }
//
//    @Override
//    public void whenHeadRemoved(Printer printer, Head head)
//    {
//    }
//
//    @Override
//    public void whenReelAdded(Printer printer, int reelIndex)
//    {
//    }
//
//    @Override
//    public void whenReelRemoved(Printer printer, Reel reel, int reelNumber)
//    {
//    }
//
//    @Override
//    public void whenReelChanged(Printer printer, Reel reel)
//    {
//    }
//
//    @Override
//    public void whenExtruderAdded(Printer printer, int extruderIndex)
//    {
//        if (printer == currentPrinter.get())
//        {
//            configureMaterialComponents(printer);
//        }
//    }
//
//    @Override
//    public void whenExtruderRemoved(Printer printer, int extruderIndex)
//    {
//        if (printer == currentPrinter.get())
//        {
//            configureMaterialComponents(printer);
//        }
//    }
//}
