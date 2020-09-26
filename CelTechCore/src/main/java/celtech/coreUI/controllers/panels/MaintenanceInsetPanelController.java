package celtech.coreUI.controllers.panels;

import celtech.Lookup;
import celtech.configuration.ApplicationConfiguration;
import celtech.configuration.DirectoryMemoryProperty;
import celtech.coreUI.DisplayManager;
import celtech.roboxbase.printerControl.PrinterStatus;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterException;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * FXML Controller class
 *
 * @author Ian
 */
public class MaintenanceInsetPanelController implements Initializable, MenuInnerPanel
{

    private static final Stenographer steno = StenographerFactory.getStenographer(MaintenanceInsetPanelController.class.getName());
    private Printer connectedPrinter;

    private final FileChooser firmwareFileChooser = new FileChooser();

    private final FileChooser gcodeFileChooser = new FileChooser();

    private final BooleanProperty printingDisabled = new SimpleBooleanProperty(false);
    private final BooleanProperty noHead = new SimpleBooleanProperty(false);
    private final BooleanProperty dualHead = new SimpleBooleanProperty(false);
    private final BooleanProperty singleHead = new SimpleBooleanProperty(false);
    private final BooleanProperty noValveHead = new SimpleBooleanProperty(false);
    private final BooleanProperty noFilamentE = new SimpleBooleanProperty(false);
    private final BooleanProperty noFilamentD = new SimpleBooleanProperty(false);
    private final BooleanProperty noFilamentEOrD = new SimpleBooleanProperty(false);

    @FXML
    private VBox container;

    @FXML
    private Button YTestButton;

    @FXML
    private Button PurgeMaterialButton;

    @FXML
    private Button loadFirmwareButton;

    @FXML
    private Button T1CleanButton;

    @FXML
    private Button EjectStuckMaterialButton1;

    @FXML
    private Button EjectStuckMaterialButton2;

    @FXML
    private Button SpeedTestButton;

    @FXML
    private Button XTestButton;

    @FXML
    private Button T0CleanButton;

    @FXML
    private Label currentFirmwareField;

    @FXML
    private Button LevelGantryButton;

    @FXML
    private Button sendGCodeSDButton;

    @FXML
    private Button ZTestButton;

    @FXML
    void ejectStuckMaterial1(ActionEvent event)
    {
        if (connectedPrinter != null && connectedPrinter.headProperty().get() != null)
        {
            try
            {
                int nozzleNumber = 0;
                if (connectedPrinter.headProperty().get().headTypeProperty().get() == Head.HeadType.DUAL_MATERIAL_HEAD)
                    nozzleNumber = 1;
                
                connectedPrinter.ejectStuckMaterial(nozzleNumber, false, null, Lookup.getUserPreferences().isSafetyFeaturesOn());
            }
            catch (PrinterException ex)
            {
                steno.info("Error attempting to run eject stuck material E");
            }
        }
    }

    @FXML
    void ejectStuckMaterial2(ActionEvent event)
    {
        if (connectedPrinter != null &&
            connectedPrinter.headProperty().get() != null &&
            connectedPrinter.headProperty().get().headTypeProperty().get() == Head.HeadType.DUAL_MATERIAL_HEAD)
        {
            try
            {
                connectedPrinter.ejectStuckMaterial(0, false, null, Lookup.getUserPreferences().isSafetyFeaturesOn());
            } catch (PrinterException ex)
            {
                steno.info("Error attempting to run eject stuck material D");
            }
        }
    }

    @FXML
    void levelGantry(ActionEvent event)
    {
        try
        {
            connectedPrinter.levelGantry(false, null);
        } catch (PrinterException ex)
        {
            steno.error("Couldn't level gantry");
        }
    }

    @FXML
    void testX(ActionEvent event)
    {
        try
        {
            connectedPrinter.testX(false, null);
        } catch (PrinterException ex)
        {
            steno.error("Couldn't test X");
        }
    }

    @FXML
    void testY(ActionEvent event)
    {
        try
        {
            connectedPrinter.testY(false, null);
        } catch (PrinterException ex)
        {
            steno.error("Couldn't test Y");
        }
    }

    @FXML
    void testZ(ActionEvent event)
    {
        try
        {
            connectedPrinter.testZ(false, null);
        } catch (PrinterException ex)
        {
            steno.error("Couldn't test Z");
        }
    }

    @FXML
    void cleanNozzleT0(ActionEvent event)
    {
        try
        {
            connectedPrinter.cleanNozzle(0, false, null, Lookup.getUserPreferences().isSafetyFeaturesOn());
        } catch (PrinterException ex)
        {
            steno.error("Couldn't clean left nozzle");
        }
    }

    @FXML
    void cleanNozzleT1(ActionEvent event)
    {
        try
        {
            connectedPrinter.cleanNozzle(1, false, null, Lookup.getUserPreferences().isSafetyFeaturesOn());
        } catch (PrinterException ex)
        {
            steno.error("Couldn't clean right nozzle");
        }
    }

    @FXML
    void speedTest(ActionEvent event)
    {
        try
        {
            connectedPrinter.speedTest(false, null);
        } catch (PrinterException ex)
        {
            steno.error("Couldn't run speed test");
        }
    }

    @FXML
    void loadFirmware(ActionEvent event)
    {
        firmwareFileChooser.setInitialFileName("Untitled");

        firmwareFileChooser.setInitialDirectory(ApplicationConfiguration.getLastDirectoryFile(DirectoryMemoryProperty.LAST_FIRMWARE_DIRECTORY));

        final File file = firmwareFileChooser.showOpenDialog(DisplayManager.getMainStage());
        if (file != null)
        {
            ApplicationConfiguration.setLastDirectory(DirectoryMemoryProperty.LAST_FIRMWARE_DIRECTORY, file.
                    getParentFile().getAbsolutePath());
            connectedPrinter.loadFirmware(file.getAbsolutePath());
        }
    }

    @FXML
    void sendGCodeSD(ActionEvent event)
    {
        gcodeFileChooser.setInitialFileName("Untitled");
        gcodeFileChooser.setInitialDirectory(ApplicationConfiguration.getLastDirectoryFile(DirectoryMemoryProperty.LAST_GCODE_DIRECTORY));

        final File file = gcodeFileChooser.showOpenDialog(container.getScene().getWindow());

        if (file != null)
        {
            try
            {
                connectedPrinter.executeGCodeFile(file.getAbsolutePath(), false);
            } catch (PrinterException ex)
            {
                steno.error("Error sending SD job");
            }
            ApplicationConfiguration.setLastDirectory(DirectoryMemoryProperty.LAST_GCODE_DIRECTORY, file.
                    getParentFile().getAbsolutePath());
        }
    }

    @FXML
    void purge(ActionEvent event)
    {
        DisplayManager.getInstance().getPurgeInsetPanelController().purge(connectedPrinter);
    }

    /**
     * Initialises the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        try
        {
            YTestButton.disableProperty().bind(printingDisabled);
            PurgeMaterialButton.disableProperty().bind(
                    noFilamentEOrD.or(noHead).or(printingDisabled));

            T0CleanButton.disableProperty().bind(
                    noHead
                    .or(printingDisabled)
                    .or(dualHead.and(noFilamentD))
                    .or(singleHead.and(noFilamentEOrD))
                    .or(noValveHead));
            T1CleanButton.disableProperty().bind(
                    noHead
                    .or(printingDisabled)
                    .or(dualHead.and(noFilamentE))
                    .or(singleHead.and(noFilamentEOrD))
                    .or(noValveHead));

            EjectStuckMaterialButton1.disableProperty().bind(printingDisabled.or(noFilamentE));
            EjectStuckMaterialButton2.disableProperty().bind(printingDisabled.or(noFilamentD).or(singleHead));

            SpeedTestButton.disableProperty().bind(printingDisabled);
            XTestButton.disableProperty().bind(printingDisabled);

            LevelGantryButton.disableProperty().bind(printingDisabled);
            ZTestButton.disableProperty().bind(printingDisabled);
            sendGCodeSDButton.disableProperty().bind(printingDisabled.or(Lookup.
                    getUserPreferences().advancedModeProperty().not()));

            currentFirmwareField.setStyle("-fx-font-weight: bold;");

            gcodeFileChooser.setTitle(Lookup.i18n("maintenancePanel.gcodeFileChooserTitle"));
            gcodeFileChooser.getExtensionFilters()
                    .addAll(
                            new FileChooser.ExtensionFilter(Lookup.i18n(
                                            "maintenancePanel.gcodeFileDescription"),
                                    "*.gcode"));

            firmwareFileChooser.setTitle(Lookup.i18n("maintenancePanel.firmwareFileChooserTitle"));
            firmwareFileChooser.getExtensionFilters()
                    .addAll(
                            new FileChooser.ExtensionFilter(Lookup.i18n(
                                            "maintenancePanel.firmwareFileDescription"), "*.bin"));

            Lookup.getSelectedPrinterProperty().addListener(
                    (ObservableValue<? extends Printer> observable, Printer oldValue, Printer newValue) ->
                    {
                        if (connectedPrinter != null)
                        {
                            currentFirmwareField.textProperty().unbind();
                            sendGCodeSDButton.disableProperty().unbind();
                            loadFirmwareButton.disableProperty().unbind();

                            printingDisabled.unbind();
                            printingDisabled.set(true);
                            noHead.unbind();
                            noHead.set(true);
                            noFilamentE.unbind();
                            noFilamentE.set(true);
                            noFilamentD.unbind();
                            noFilamentD.set(true);
                            noFilamentEOrD.unbind();
                            noFilamentEOrD.set(true);

                            dualHead.unbind();
                            dualHead.set(false);
                            singleHead.unbind();
                            singleHead.set(false);
                            noValveHead.unbind();
                            noValveHead.set(false);
                        }

                        connectedPrinter = newValue;

                        if (connectedPrinter != null)
                        {
                            currentFirmwareField.textProperty().bind(connectedPrinter.getPrinterIdentity().firmwareVersionProperty());
                            loadFirmwareButton.disableProperty()
                                              .bind(printingDisabled.or(Lookup.getUserPreferences()
                                                                              .advancedModeProperty()
                                                                              .not()
                                                                              .or(connectedPrinter.getPrinterIdentity()
                                                                                                  .validIDProperty()
                                                                                                  .not())));

                            printingDisabled.bind(connectedPrinter.printerStatusProperty().isNotEqualTo(
                                            PrinterStatus.IDLE));

                            noHead.bind(connectedPrinter.headProperty().isNull());

                            //if (noHead.not().get())
                            //{
                            //    dualHead.bind(Bindings.size(
                            //                    connectedPrinter.headProperty().get().getNozzleHeaters()).isEqualTo(
                            //                    2));
                            //    singleHead.bind(Bindings.size(
                            //                    connectedPrinter.headProperty().get().getNozzleHeaters()).isEqualTo(
                            //                    1));
                            //    noValveHead.bind(connectedPrinter.headProperty().get().valveTypeProperty().isEqualTo(Head.ValveType.NOT_FITTED));
                            //}
                            dualHead.bind(Bindings.createBooleanBinding(() -> connectedPrinter.headProperty().get() != null &&
                                                                                (connectedPrinter.headProperty().get().getNozzleHeaters().size() == 2),
                                                                          connectedPrinter.headProperty()));

                            singleHead.bind(Bindings.createBooleanBinding(() -> connectedPrinter.headProperty().get() != null &&
                                                                                (connectedPrinter.headProperty().get().getNozzleHeaters().size() == 1),
                                                                          connectedPrinter.headProperty()));

                            noValveHead.bind(Bindings.createBooleanBinding(() -> connectedPrinter.headProperty().get() != null &&
                                                                                 (connectedPrinter.headProperty().get().valveTypeProperty().get() == Head.ValveType.NOT_FITTED),
                                                                           connectedPrinter.headProperty()));

                            noFilamentE.bind(
                                    connectedPrinter.extrudersProperty().get(0).filamentLoadedProperty().not());
                            noFilamentD.bind(
                                    connectedPrinter.extrudersProperty().get(1).filamentLoadedProperty().not());

                            noFilamentEOrD.bind(
                                    connectedPrinter.extrudersProperty().get(0).filamentLoadedProperty().not()
                                    .and(
                                            connectedPrinter.extrudersProperty().get(1).filamentLoadedProperty().not()));
                        }
                        else
                        {
                            loadFirmwareButton.disableProperty().unbind();
                            loadFirmwareButton.disableProperty().set(true);
                            currentFirmwareField.setText("-");
                        }
                    });
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public String getMenuTitle()
    {
        return "maintenancePanel.title";
    }

    @Override
    public List<OperationButton> getOperationButtons()
    {
        return null;
    }
    
    @Override
    public void panelSelected() {}
}
