package celtech.coreUI.controllers.panels;

import celtech.Lookup;
import celtech.coreUI.components.RestrictedNumberField;
import celtech.coreUI.components.RestrictedTextField;
import celtech.coreUI.components.material.FilamentMenuButton;
import celtech.coreUI.components.material.FilamentSelectionListener;
import celtech.coreUI.components.material.SpecialItemSelectionListener;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.MaterialType;
import celtech.roboxbase.comms.exceptions.RoboxCommsException;
import celtech.roboxbase.comms.remote.EEPROMState;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.datafileaccessors.FilamentContainer;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterException;
import celtech.roboxbase.printerControl.model.PrinterListChangesAdapter;
import celtech.roboxbase.printerControl.model.PrinterListChangesListener;
import celtech.roboxbase.printerControl.model.Reel;
import static celtech.roboxbase.utils.ColourStringConverter.colourToString;
import static celtech.roboxbase.utils.ColourStringConverter.stringToColor;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

public class FilamentLibraryPanelController implements Initializable, MenuInnerPanel, FilamentSelectionListener, SpecialItemSelectionListener
{

    private final Stenographer steno = StenographerFactory.getStenographer(
            FilamentLibraryPanelController.class.getName());

    private final PseudoClass ERROR = PseudoClass.getPseudoClass("error");

    enum State
    {

        /**
         * Editing a new profile that has not yet been saved.
         */
        NEW,
        /**
         * Editing a custom profile.
         */
        CUSTOM,
        /**
         * Viewing a standard profile.
         */
        ROBOX
    };

    private final ObjectProperty<State> state = new SimpleObjectProperty<>();
    private final BooleanProperty isDirty = new SimpleBooleanProperty(false);

    private final BooleanProperty isEditable = new SimpleBooleanProperty(false);
    private final BooleanProperty canSave = new SimpleBooleanProperty(false);
    private final BooleanProperty canSaveAs = new SimpleBooleanProperty(false);
    private final BooleanProperty canDelete = new SimpleBooleanProperty(false);
    private final BooleanProperty canWriteToReel1 = new SimpleBooleanProperty(false);
    private final BooleanProperty canWriteToReel2 = new SimpleBooleanProperty(false);
    /**
     * Indicates if the contents of all widgets is valid or not.
     */
    private final BooleanProperty isValid = new SimpleBooleanProperty(false);
    private final BooleanProperty isNameValid = new SimpleBooleanProperty(false);
    private final BooleanProperty isNozzleTempValid = new SimpleBooleanProperty(true);

    private String currentFilamentID;
    private final ObjectProperty<Printer> currentPrinter = new SimpleObjectProperty<>();
    private final FilamentContainer filamentContainer = FilamentContainer.getInstance();
    private PrinterListChangesListener listener;

    private Filament currentFilament;
    private Filament currentFilamentAsEdited;

    private StringProperty loadedFilamentID0 = new SimpleStringProperty();
    private StringProperty loadedFilamentID1 = new SimpleStringProperty();

    private boolean suspendDirtyTriggers = false;

    private final String reel1MenuItemTitle = "1:";
    private final String reel2MenuItemTitle = "2:";

    @FXML
    private FilamentMenuButton filamentMenuButton;

    @FXML
    private RestrictedNumberField bedTemperature;

    @FXML
    private RestrictedNumberField firstLayerBedTemperature;

    @FXML
    private RestrictedNumberField nozzleTemperature;

    @FXML
    private RestrictedNumberField ambientTemperature;

    @FXML
    private ColorPicker colour;

    @FXML
    private RestrictedNumberField firstLayerNozzleTemperature;

    @FXML
    private ComboBox<MaterialType> material;

    @FXML
    private RestrictedNumberField filamentDiameter;

    @FXML
    private RestrictedNumberField costGBPPerKG;

    @FXML
    private RestrictedNumberField remainingOnReelM;

    @FXML
    private RestrictedNumberField feedRateMultiplier;

    @FXML
    private RestrictedTextField name;

    @FXML
    private TextField filamentID;

    @FXML
    private RestrictedNumberField filamentMultiplier;

    @FXML
    private TextArea helpText;

    @FXML
    private GridPane filamentsGridPane;

    private final String REMAINING_ON_REEL_UNCHANGED = "-";

    private final ListChangeListener<EEPROMState> reelEEPROMChangeListener = (ListChangeListener.Change<? extends EEPROMState> change) ->
    {
        updateWriteToReelBindings();
    };

    private void updatePrinter(Printer lastPrinter, Printer newPrinter)
    {
        if (lastPrinter != null)
        {
            lastPrinter.getReelEEPROMStateProperty().removeListener(reelEEPROMChangeListener);
        }

        if (newPrinter != null)
        {
            newPrinter.getReelEEPROMStateProperty().addListener(reelEEPROMChangeListener);
        }

        if (newPrinter != null)
        {
            updateWriteToReelBindings();
            showReelsAtTopOfCombo();
        } else
        {
            clearWidgets();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        currentFilament = filamentMenuButton.initialiseButton(this, this, false);

        updateSaveBindings();

        canSaveAs.bind(state.isNotEqualTo(State.NEW).and(Lookup.getUserPreferences().advancedModeProperty()));

        canDelete.bind(state.isNotEqualTo(State.ROBOX).and(Lookup.getUserPreferences().advancedModeProperty()));

        isEditable.bind(state.isNotEqualTo(State.ROBOX).and(Lookup.getUserPreferences().advancedModeProperty()));

        isValid.bind(isNameValid.and(isNozzleTempValid));

        currentPrinter.bind(Lookup.getSelectedPrinterProperty());
        updatePrinter(null, currentPrinter.get());

        currentPrinter.addListener(
                (ObservableValue<? extends Printer> observable, Printer oldValue, Printer newValue) ->
        {
            updatePrinter(oldValue, newValue);
        });

        for (MaterialType materialType : MaterialType.values())
        {
            material.getItems().add(materialType);
        }

        setupWidgetEditableBindings();

        setupWidgetChangeListeners();

        setupPrinterChangesListener();

        FXMLUtilities.addColonsToLabels(filamentsGridPane);

        updateWidgets(currentFilament);
    }

    private void setupPrinterChangesListener()
    {
        listener = new PrinterListChangesAdapter()
        {

            @Override
            public void whenReelAdded(Printer printer, int reelIndex)
            {
                if (printer == currentPrinter.get())
                {
                    showReelsAtTopOfCombo();
                    updateWriteToReelBindings();
                }
            }

            @Override
            public void whenReelRemoved(Printer printer, Reel reel, int reelIndex)
            {
                if (printer == currentPrinter.get())
                {
                    showReelsAtTopOfCombo();
                    updateWriteToReelBindings();
                }
            }

            @Override
            public void whenReelChanged(Printer printer, Reel reel)
            {
                if (printer == currentPrinter.get())
                {
                    showReelsAtTopOfCombo();
                    updateWriteToReelBindings();
                }
            }

        };
        BaseLookup.getPrinterListChangesNotifier().addListener(listener);
    }

    private void updateSaveBindings()
    {
        canSave.setValue(currentFilament != null
                && !currentFilament.equals(currentFilamentAsEdited)
                && isValid.get()
                && isDirty.get()
                && (state.isEqualTo(State.NEW).get() || state.isEqualTo(State.CUSTOM).get()));
    }

    /**
     * This should be called whenever any field is edited or the reel is
     * changed.
     */
    private void updateWriteToReelBindings()
    {
        updatedLoadedFilamentIDs();

        canWriteToReel1.set(false);
        canWriteToReel2.set(false);

        if (currentPrinter.get() != null)
        {
            boolean filament0OfDifferentID = false;
            boolean filament1OfDifferentID = false;

            if (loadedFilamentID0.get() != null)
            {
                filament0OfDifferentID = !loadedFilamentID0.get().equals(currentFilamentID);
            }

            if (loadedFilamentID1.get() != null)
            {
                filament1OfDifferentID = !loadedFilamentID1.get().equals(currentFilamentID);
            }

            if ((currentPrinter.get().reelsProperty().containsKey(0)
                    && (Lookup.getUserPreferences().isAdvancedMode() || state.get() == State.ROBOX)
                    && (filament0OfDifferentID || !currentFilament.equals(currentFilamentAsEdited)
                    || !remainingOnReelM.getText().equals(REMAINING_ON_REEL_UNCHANGED)))
                    || currentPrinter.get().getReelEEPROMStateProperty().get(0) == EEPROMState.NOT_PROGRAMMED)
            {
                canWriteToReel1.set(true);
            }
            if ((currentPrinter.get().reelsProperty().containsKey(1)
                    && (Lookup.getUserPreferences().isAdvancedMode() || state.get() == State.ROBOX)
                    && (filament1OfDifferentID || !currentFilament.equals(currentFilamentAsEdited)
                    || !remainingOnReelM.getText().equals(REMAINING_ON_REEL_UNCHANGED)))
                    || currentPrinter.get().getReelEEPROMStateProperty().get(1) == EEPROMState.NOT_PROGRAMMED)
            {
                canWriteToReel2.set(true);
            }
        }
    }

    private void updatedLoadedFilamentIDs()
    {
        if (currentPrinter.get() == null)
        {
            loadedFilamentID0.set(null);
            loadedFilamentID1.set(null);
        } else
        {
            if (currentPrinter.get().reelsProperty().containsKey(0))
            {
                loadedFilamentID0.set(
                        currentPrinter.get().reelsProperty().get(0).filamentIDProperty().get());
            } else
            {
                loadedFilamentID0.set(null);
            }
            if (currentPrinter.get().reelsProperty().containsKey(1))
            {
                loadedFilamentID1.set(
                        currentPrinter.get().reelsProperty().get(1).filamentIDProperty().get());
            } else
            {
                loadedFilamentID1.set(null);
            }
        }
    }

    private void clearWidgets()
    {
        name.setText("");
        filamentID.setText("");
//        material.getSelectionModel().select(filament.getMaterial());
        filamentDiameter.setValue(0f);
        filamentMultiplier.setValue(0f);

        feedRateMultiplier.setValue(0f);
        ambientTemperature.setValue(0);
        firstLayerBedTemperature.setValue(0);
        bedTemperature.setValue(0);
        firstLayerNozzleTemperature.setValue(0);
        nozzleTemperature.setValue(0);
        costGBPPerKG.setValue(0);
        remainingOnReelM.setValue(0);
//        colour.setValue(filament.getDisplayColour());
        isDirty.set(false);
    }

    private void setupWidgetChangeListeners()
    {

        name.textProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) ->
        {
            if (!validateMaterialName(newValue))
            {
                isNameValid.set(false);
                name.pseudoClassStateChanged(ERROR, true);
            } else
            {
                isNameValid.set(true);
                name.pseudoClassStateChanged(ERROR, false);
            }
        });

        name.textProperty().addListener(dirtyStringListener);
        colour.valueProperty().addListener(
                (ObservableValue<? extends Color> observable, Color oldValue, Color newValue) ->
        {
            if (!suspendDirtyTriggers)
            {
                isDirty.set(true);
                currentFilamentAsEdited = currentFilament.clone();
                updateFilamentFromWidgets(currentFilamentAsEdited);
                updateWriteToReelBindings();
                updateSaveBindings();
            }
        });
        material.valueProperty().addListener(dirtyMaterialTypeListener);
        filamentDiameter.valueChangedProperty().addListener(dirtyBooleanListener);
        filamentMultiplier.valueChangedProperty().addListener(dirtyBooleanListener);
        feedRateMultiplier.valueChangedProperty().addListener(dirtyBooleanListener);
        firstLayerBedTemperature.valueChangedProperty().addListener(dirtyBooleanListener);
        bedTemperature.valueChangedProperty().addListener(dirtyBooleanListener);
        firstLayerNozzleTemperature.valueChangedProperty().addListener(dirtyBooleanListener);
        nozzleTemperature.valueChangedProperty().addListener(dirtyBooleanListener);
        ambientTemperature.valueChangedProperty().addListener(dirtyBooleanListener);
        costGBPPerKG.valueChangedProperty().addListener(dirtyBooleanListener);
        remainingOnReelM.valueChangedProperty().addListener(dirtyBooleanListener);
    }

    private void setupWidgetEditableBindings()
    {
        filamentID.setDisable(true);
        bedTemperature.disableProperty().bind(isEditable.not());
        firstLayerNozzleTemperature.disableProperty().bind(isEditable.not());
        colour.disableProperty().bind(isEditable.not());
        material.disableProperty().bind(isEditable.not());
        filamentDiameter.disableProperty().bind(isEditable.not());
        filamentMultiplier.disableProperty().bind(isEditable.not());
        feedRateMultiplier.disableProperty().bind(isEditable.not());
        firstLayerBedTemperature.disableProperty().bind(isEditable.not());
        name.disableProperty().bind(isEditable.not());
        nozzleTemperature.disableProperty().bind(isEditable.not());
        ambientTemperature.disableProperty().bind(isEditable.not());
        costGBPPerKG.disableProperty().bind(isEditable.not());
        remainingOnReelM.disableProperty().bind(isEditable.not());
    }

    private final ChangeListener<String> dirtyStringListener
            = (ObservableValue<? extends String> ov, String t, String t1) ->
    {
        if (!suspendDirtyTriggers)
        {
            isDirty.set(true);
            currentFilamentAsEdited = currentFilament.clone();
            updateFilamentFromWidgets(currentFilamentAsEdited);
            updateWriteToReelBindings();
            updateSaveBindings();
        }
    };

    private final ChangeListener<Boolean> dirtyBooleanListener
            = (ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) ->
    {
        if (!suspendDirtyTriggers)
        {
            isDirty.set(true);
            currentFilamentAsEdited = currentFilament.clone();
            updateFilamentFromWidgets(currentFilamentAsEdited);
            updateWriteToReelBindings();
            updateSaveBindings();
        }
    };

    private final ChangeListener<MaterialType> dirtyMaterialTypeListener
            = (ObservableValue<? extends MaterialType> ov, MaterialType t, MaterialType t1) ->
    {
        if (!suspendDirtyTriggers)
        {
            isDirty.set(true);
            currentFilamentAsEdited = currentFilament.clone();
            updateFilamentFromWidgets(currentFilamentAsEdited);
            updateIDBasedOnMaterial(currentFilamentAsEdited);
            updateWriteToReelBindings();
            updateSaveBindings();
        }
    };

    private void selectFilament(Filament filament)
    {
        if (filament != null)
        {
            currentFilamentID = filament.getFilamentID();
            currentFilament = filament;
            if (currentFilamentID.startsWith("U"))
            {
                state.set(State.CUSTOM);
            } else
            {
                state.set(State.ROBOX);
            }

            updateWriteToReelBindings();
            updateSaveBindings();
            updateWidgets(filament);
        }
    }

    public void updateWidgets(Filament filament)
    {
        suspendDirtyTriggers = true;
        name.setText(filament.getFriendlyFilamentName());
        filamentID.setText(filament.getFilamentID());
        material.getSelectionModel().select(filament.getMaterial());
        filamentDiameter.setValue(filament.getDiameter());
        filamentMultiplier.setValue(filament.getFilamentMultiplier());
        feedRateMultiplier.setValue(filament.getFeedRateMultiplier());
        ambientTemperature.setValue(filament.getAmbientTemperature());
        firstLayerBedTemperature.setValue(filament.getFirstLayerBedTemperature());
        bedTemperature.setValue(filament.getBedTemperature());
        firstLayerNozzleTemperature.setValue(filament.getFirstLayerNozzleTemperature());
        nozzleTemperature.setValue(filament.getNozzleTemperature());
        colour.setValue(filament.getDisplayColour());
        costGBPPerKG.setValue(filament.getCostGBPPerKG());
        remainingOnReelM.textProperty().set(REMAINING_ON_REEL_UNCHANGED);
        isDirty.set(false);
        suspendDirtyTriggers = false;
        currentFilamentAsEdited = currentFilament.clone();
    }

    /**
     * Update the given from the contents of the widgets.
     */
    public void updateFilamentFromWidgets(Filament filament)
    {
        filament.setFilamentID(filamentID.getText());
        filament.setFriendlyFilamentName(name.getText());
        filament.setMaterial(material.getSelectionModel().getSelectedItem());
        filament.setFilamentDiameter(filamentDiameter.getAsFloat());
        filament.setFilamentMultiplier(filamentMultiplier.getAsFloat());
        filament.setFeedRateMultiplier(feedRateMultiplier.getAsFloat());
        filament.setAmbientTemperature(ambientTemperature.getAsInt());
        filament.setFirstLayerBedTemperature(firstLayerBedTemperature.getAsInt());
        filament.setBedTemperature(bedTemperature.getAsInt());
        filament.setFirstLayerNozzleTemperature(firstLayerNozzleTemperature.getAsInt());
        filament.setNozzleTemperature(nozzleTemperature.getAsInt());
        Color webDomainColor = stringToColor(colourToString(colour.getValue()));
        filament.setDisplayColour(webDomainColor);
        filament.setCostGBPPerKG(costGBPPerKG.getAsFloat());
    }
    
    /**
     * Make a check to see if the selected material is different from the current
     * on file. If it is we need a new id for the new file. We only need 
     * to generate a new id if it hasn't already changed.
     * 
     * @param currentFilamentAsEdited the current state of the filament in the editor
     */
    protected void updateIDBasedOnMaterial(Filament currentFilamentAsEdited)
    {
        if (material.getSelectionModel().getSelectedItem() != currentFilament.getMaterial()) 
        {
            if(filamentID.getText().equals(currentFilamentID)) 
            {
                currentFilamentAsEdited.setFilamentID(Filament.generateUserFilamentID());
            } else 
            {
                currentFilamentAsEdited.setFilamentID(filamentID.getText());
            }
        } else
        {
            currentFilamentAsEdited.setFilamentID(currentFilamentID);
        }
        
        filamentID.setText(currentFilamentAsEdited.getFilamentID());
    }

    private boolean validateMaterialName(String name)
    {

        boolean valid = true;

        if (name.equals(""))
        {
            valid = false;
        } else if (currentFilamentID == null || currentFilamentID.startsWith("U"))
        {
            ObservableList<Filament> existingMaterialList = filamentContainer.getCompleteFilamentList();
            for (Filament existingMaterial : existingMaterialList)
            {
                if ((!existingMaterial.getFilamentID().equals(currentFilamentID))
                        && existingMaterial.getFriendlyFilamentName().equals(name))
                {
                    valid = false;
                    break;
                }
            }
        }
        return valid;
    }

    private float getRemainingFilament(int reelIndex)
    {
        return currentPrinter.get().reelsProperty().get(reelIndex).remainingFilamentProperty().get();
    }

    void whenSavePressed()
    {
        assert (state.get() != State.ROBOX);
        if (!currentFilament.getFilamentID().equals(filamentID.getText())) {
            // We do this in case the id has changed, in which case we are
            // saving a new filament.
            currentFilament = currentFilament.clone();
        }
        updateFilamentFromWidgets(currentFilament);
        filamentContainer.saveFilament(currentFilament);
        Filament filamentToSelect = currentFilament;
        showReelsAtTopOfCombo();
        selectFilament(filamentToSelect);
        filamentMenuButton.displayFilamentOnButton(filamentToSelect);
    }

    void whenNewPressed()
    {
        state.set(State.NEW);
        clearWidgets();
        currentFilamentID = null;
        currentFilament = currentFilament.clone();
        currentFilament.setFilamentID(Filament.generateUserFilamentID());
        filamentID.setText(currentFilament.getFilamentID());
    }

    void whenSaveAsPressed()
    {
        state.set(State.NEW);
        currentFilamentID = null;
        currentFilament = currentFilament.clone();
        currentFilament.setBrand(FilamentContainer.CUSTOM_BRAND);
        currentFilament.setCategory(FilamentContainer.CUSTOM_CATEGORY);
        currentFilament.setFilamentID(Filament.generateUserFilamentID());
        updateWidgets(currentFilament);
        name.requestFocus();
        name.selectAll();
        // visually marks name as needing to be changed
        name.pseudoClassStateChanged(ERROR, true);
        updateSaveBindings();
    }

    void whenDeletePressed()
    {
        if (state.get() != State.NEW)
        {
            filamentContainer.deleteFilament(currentFilament);
        }
        clearWidgets();
        selectFilament(filamentMenuButton.displayFirstFilament());
    }
    
    void whenWriteToReelPressed(int reelIndex)
    {
        try
        {
            String remainingOnReelText = remainingOnReelM.getText();
            if (isEditable.get() && isDirty.get())
            {
                whenSavePressed();
            }

            Filament filament = filamentMenuButton.getCurrentlyDisplayedFilament();

            if (currentPrinter.get().getReelEEPROMStateProperty().get(reelIndex) == EEPROMState.NOT_PROGRAMMED)
            {
                currentPrinter.get().formatReelEEPROM(reelIndex);
            } else
            {
                float remainingFilament = getRemainingFilament(reelIndex);
                if (state.get() == State.CUSTOM && !remainingOnReelText.equals(REMAINING_ON_REEL_UNCHANGED))
                {
                    remainingFilament = remainingOnReelM.getAsFloat() * 1000f;
                }

                filament.setRemainingFilament(remainingFilament);
            }

            currentPrinter.get().transmitWriteReelEEPROM(reelIndex, filament);
        } catch (RoboxCommsException | PrinterException ex)
        {
            steno.error("Unable to write to Reel " + reelIndex + " " + ex);
        }
    }

    void whenWriteToReel1Pressed()
    {
        whenWriteToReelPressed(0);
    }

    void whenWriteToReel2Pressed()
    {
        whenWriteToReelPressed(1);
    }

    public ReadOnlyBooleanProperty getCanSave()
    {
        return canSave;
    }

    ReadOnlyBooleanProperty getCanDelete()
    {
        return canDelete;
    }

    @Override
    public String getMenuTitle()
    {
        return "extrasMenu.filament";
    }

    @Override
    public List<MenuInnerPanel.OperationButton> getOperationButtons()
    {
        List<MenuInnerPanel.OperationButton> operationButtons = new ArrayList<>();

        MenuInnerPanel.OperationButton saveButton = new MenuInnerPanel.OperationButton()
        {
            @Override
            public String getTextId()
            {
                return "genericFirstLetterCapitalised.Save";
            }

            @Override
            public String getFXMLName()
            {
                return "saveButton";
            }

            @Override
            public String getTooltipTextId()
            {
                return "genericFirstLetterCapitalised.Save";
            }

            @Override
            public void whenClicked()
            {
                whenSavePressed();
            }

            @Override
            public BooleanProperty whenEnabled()
            {
                return canSave;
            }

        };
        operationButtons.add(saveButton);
        MenuInnerPanel.OperationButton saveAsButton = new MenuInnerPanel.OperationButton()
        {
            @Override
            public String getTextId()
            {
                return "genericFirstLetterCapitalised.SaveAs";
            }

            @Override
            public String getFXMLName()
            {
                return "saveAsButton";
            }

            @Override
            public String getTooltipTextId()
            {
                return "genericFirstLetterCapitalised.SaveAs";
            }

            @Override
            public void whenClicked()
            {
                whenSaveAsPressed();
            }

            @Override
            public BooleanProperty whenEnabled()
            {
                return canSaveAs;
            }

        };
        operationButtons.add(saveAsButton);
        MenuInnerPanel.OperationButton deleteButton = new MenuInnerPanel.OperationButton()
        {
            @Override
            public String getTextId()
            {
                return "genericFirstLetterCapitalised.Delete";
            }

            @Override
            public String getFXMLName()
            {
                return "deleteButton";
            }

            @Override
            public String getTooltipTextId()
            {
                return "genericFirstLetterCapitalised.Delete";
            }

            @Override
            public void whenClicked()
            {
                whenDeletePressed();
            }

            @Override
            public BooleanProperty whenEnabled()
            {
                return canDelete;
            }

        };
        operationButtons.add(deleteButton);
        MenuInnerPanel.OperationButton writeToReel1Button = new MenuInnerPanel.OperationButton()
        {
            @Override
            public String getTextId()
            {
                return "filamentLibrary.writeToReel1";
            }

            @Override
            public String getFXMLName()
            {
                return "writeToReel1Button";
            }

            @Override
            public String getTooltipTextId()
            {
                return "filamentLibrary.writeToReel1";
            }

            @Override
            public void whenClicked()
            {
                whenWriteToReel1Pressed();
            }

            @Override
            public BooleanProperty whenEnabled()
            {
                return canWriteToReel1;
            }

        };
        operationButtons.add(writeToReel1Button);
        MenuInnerPanel.OperationButton writeToReel2Button = new MenuInnerPanel.OperationButton()
        {
            @Override
            public String getTextId()
            {
                return "filamentLibrary.writeToReel2";
            }

            @Override
            public String getFXMLName()
            {
                return "writeToReel2Button";
            }

            @Override
            public String getTooltipTextId()
            {
                return "filamentLibrary.writeToReel2";
            }

            @Override
            public void whenClicked()
            {
                whenWriteToReel2Pressed();
            }

            @Override
            public BooleanProperty whenEnabled()
            {
                return canWriteToReel2;
            }

        };
        operationButtons.add(writeToReel2Button);

        return operationButtons;
    }

    private void showReelsAtTopOfCombo()
    {
        filamentMenuButton.deleteSpecialMenuItem(reel1MenuItemTitle);
        filamentMenuButton.deleteSpecialMenuItem(reel2MenuItemTitle);

        if (currentPrinter != null
                && currentPrinter.get() != null)
        {
            if (currentPrinter.get().reelsProperty().containsKey(1))
            {
                String filamentId1 = currentPrinter.get().reelsProperty().get(1).filamentIDProperty().get();
                Filament filament1 = filamentContainer.getFilamentByID(filamentId1);
                filamentMenuButton.addSpecialMenuItem(reel2MenuItemTitle, filament1);
            }
            if (currentPrinter.get().reelsProperty().containsKey(0))
            {
                String filamentId0 = currentPrinter.get().reelsProperty().get(0).filamentIDProperty().get();
                Filament filament0 = filamentContainer.getFilamentByID(filamentId0);
                filamentMenuButton.addSpecialMenuItem(reel1MenuItemTitle, filament0);
            }

            selectFilament(filamentMenuButton.displayFirstFilament());
        }
    }

    @Override
    public void filamentSelected(Filament filament)
    {
        selectFilament(filament);
    }

    @Override
    public void specialItemSelected(String title)
    {
        if (title.equals(reel1MenuItemTitle))
        {
            String filamentId = currentPrinter.get().reelsProperty().get(0).filamentIDProperty().get();
            Filament filament = filamentContainer.getFilamentByID(filamentId);
            selectFilament(filament);
        } else if (title.equals(reel2MenuItemTitle))
        {
            String filamentId = currentPrinter.get().reelsProperty().get(1).filamentIDProperty().get();
            Filament filament = filamentContainer.getFilamentByID(filamentId);
            selectFilament(filament);
        }
    }
    
    @Override
    public void panelSelected() {}
}
