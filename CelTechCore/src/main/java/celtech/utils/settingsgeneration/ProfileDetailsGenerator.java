package celtech.utils.settingsgeneration;

import celtech.Lookup;
import celtech.coreUI.components.GraphicTab;
import celtech.coreUI.components.HideableTooltip;
import celtech.coreUI.components.RestrictedNumberField;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.configuration.SlicerType;
import celtech.roboxbase.configuration.datafileaccessors.HeadContainer;
import celtech.roboxbase.configuration.fileRepresentation.HeadFile;
import celtech.roboxbase.configuration.fileRepresentation.NozzleData;
import celtech.roboxbase.configuration.profilesettings.PrintProfileSetting;
import celtech.roboxbase.configuration.profilesettings.PrintProfileSettings;
import celtech.roboxbase.configuration.profilesettings.PrintProfileSettingsTab;
import celtech.roboxbase.printerControl.model.Head;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * Class to provide methods for adding generated settings to the FXML print profile pages.
 * 
 * @author George Salter
 */
public class ProfileDetailsGenerator {
    
    private static final Stenographer STENO = StenographerFactory.getStenographer(ProfileDetailsGenerator.class.getName());
    
    private final BooleanProperty isDirty;
    
    enum Nozzle {
        LEFT,
        RIGHT,
        SINGLE
    }
    
    private static final String HEADER_SETTINGS_SELECTOR = "#headerSettings";
    private static final String TAB_PANE_SELECTOR = "#printProfileTabPane";
    
    private static final String TAB_VBOX_STYLE_CLASS = "printProfileTabVBox";
    private static final String TAB_LABEL_HBOX_STYLE_CLASS = "blackTabUnderline";
    private static final String TAB_TITLE_STYLE_CLASS = "tabTitle";
    private static final String TAB_SETTINGS_GRID_STYLE_CLASS = "printProfileGrid";
    private static final String TAB_SETTINGS_GRID_ID = "tabSettingsGrid";
    
    private static final String FLOAT = "float";
    private static final String INT = "int";
    private static final String BOOLEAN = "boolean";
    private static final String OPTION = "option";
    private static final String NOZZLE = "nozzle";
    private static final String EXTRUSION = "extrusion";
    private static final String NUMBER_LIST = "numbers";
    
    private static final double POINT_8_MIN_WIDTH = 0.5;
    private static final double POINT_8_MAX_WIDTH = 1.2;
    private static final double POINT_6_MIN_WIDTH = 0.4;
    private static final double POINT_6_MAX_WIDTH = 0.8;
    private static final double POINT_4_MIN_WIDTH = 0.2;
    private static final double POINT_4_MAX_WIDTH = 0.6;
    private static final double POINT_3_MIN_WIDTH = 0.2;
    private static final double POINT_3_MAX_WIDTH = 0.6;
    
    private static final Pattern NUMBER_LIST_PATTERN = Pattern.compile("^-?[0-9]*(,-?[0-9]*)*,?");
    
    private HBox headerSettingsBox;
    private TabPane tabPane;
    
    private PrintProfileSettings printProfileSettings;
    
    private String headType;
    
    private ObservableList<String> nozzleOptions;
    
    public ProfileDetailsGenerator(PrintProfileSettings printProfileSettings, BooleanProperty isDirty) {
        this.printProfileSettings = printProfileSettings;
        this.isDirty = isDirty;
    }
    
    public void setPrintProfilesettings(PrintProfileSettings printProfileSettings) {
        this.printProfileSettings = printProfileSettings;
    }
    
    public void setHeadType(String headType) {
        this.headType = headType;
    }
    
    public void setNozzleOptions(ObservableList<String> nozzleOptions) {
        this.nozzleOptions = nozzleOptions;
    }
    
    public void bindTabsToEditableProperty(BooleanProperty isEditable)
    {
        if (!tabPane.getTabs().isEmpty())
        {
            tabPane.getTabs().forEach(tab -> 
            {
                ScrollPane scrollPane = (ScrollPane) tab.getContent().lookup("ScrollPane");
                if (scrollPane != null)
                {
                    Node gridPaneNode = scrollPane.getContent();
                    if(gridPaneNode != null)
                    {
                        gridPaneNode.disableProperty().bind(isEditable.not());
                    }
                }
            });
        }
        
        headerSettingsBox.disableProperty().bind(isEditable.not());
    }
    
    public void generateSettingsForProfileDetails(VBox root, boolean recreateTabs) throws ProfileDetailsGenerationException
    {   
        Node headerNode = root.lookup(HEADER_SETTINGS_SELECTOR);
        Node tabPaneNode = root.lookup(TAB_PANE_SELECTOR);
        
        if(headerNode instanceof HBox && tabPaneNode instanceof TabPane) 
        {
            headerSettingsBox = (HBox) headerNode;
            tabPane = (TabPane) tabPaneNode;
        } else
        {
            throw new ProfileDetailsGenerationException("The given root Node " 
                    + root.getClass().getName() 
                    + "does not contain the correct nodes to hook onto for profile settings generation.");
        }

        // Generate settings for header
        headerSettingsBox.getChildren().clear();
        generateHeaderSettings(headerSettingsBox, printProfileSettings.getHeaderSettings());
        
        List<PrintProfileSettingsTab> printProfileTabs = printProfileSettings.getTabs();
        
        // Generate new tabs and settings
        if(recreateTabs)
        {
            tabPane.getTabs().clear();
        }
        printProfileTabs.forEach(profileTab -> generateProfileSettingsTab(profileTab));
        
        // Weird bit of code to enable tabs to fit the width of the pane and to change size with the window
        tabPane.tabMinWidthProperty().bind(tabPane.widthProperty().divide(tabPane.getTabs().size()).subtract(20));
    }
    
    private void generateHeaderSettings(HBox headerSettingsBox, List<PrintProfileSetting> headerSettings) 
    {
        headerSettings.forEach(setting -> 
        {
            String valueType = setting.getValueType();
            switch(valueType)
            {
                case FLOAT:
                    headerSettingsBox.getChildren().add(createLabelElement(setting.getSettingName(), true));
                    headerSettingsBox.getChildren().add(createInputFieldWithOptionalUnit(setting, setting.getValue(), Nozzle.SINGLE));
                    break;
                default:
                    STENO.error("Setting value type of " + valueType + " is not yet supported for 'header settings'");
            }
        });
    }
    
    private void generateProfileSettingsTab(PrintProfileSettingsTab printProfileSettingsTab)
    {
        Optional<Tab> currentTab = tabPane.getTabs().stream()
                .filter(tab -> tab.getId().equals(BaseLookup.i18n(printProfileSettingsTab.getTabName())))
                .findFirst();
        
        if (currentTab.isPresent())
        {
            generateProfileSettingsForTab(printProfileSettingsTab, (GraphicTab) currentTab.get());
        } else
        {
            GraphicTab newTab = new GraphicTab(printProfileSettingsTab.getFxmlIconName());
            if (printProfileSettingsTab.getFxmlSelectedIconName().isPresent())
            {
                newTab.setFxmlSelectedIconName(printProfileSettingsTab.getFxmlSelectedIconName().get());
            }
            
            tabPane.getTabs().add(generateProfileSettingsForTab(printProfileSettingsTab, newTab));
        }
    }
    
    private GraphicTab generateProfileSettingsForTab(PrintProfileSettingsTab printProfileSettingsTab, GraphicTab graphicTab)
    {
        String tabName = BaseLookup.i18n(printProfileSettingsTab.getTabName());
        Label tabTitle = new Label(tabName);
        tabTitle.getStyleClass().add(TAB_TITLE_STYLE_CLASS);
        
        HBox tabTitleHBox = new HBox();
        tabTitleHBox.setAlignment(Pos.CENTER);
        tabTitleHBox.getStyleClass().add(TAB_LABEL_HBOX_STYLE_CLASS);
        tabTitleHBox.getChildren().add(tabTitle);
        
        GridPane settingGridPane = new GridPane();
        settingGridPane.setAlignment(Pos.TOP_CENTER);
        settingGridPane.setVgap(8.0);
        settingGridPane.setId(TAB_SETTINGS_GRID_ID);
        settingGridPane.getStyleClass().add(TAB_SETTINGS_GRID_STYLE_CLASS);
        generateSettingsForTabGrid(settingGridPane, printProfileSettingsTab.getSettings());
        
        ScrollPane scrollableSettingsPane = new ScrollPane();
        scrollableSettingsPane.setFitToWidth(true);
        scrollableSettingsPane.setFitToHeight(true);
        scrollableSettingsPane.setContent(settingGridPane);
        scrollableSettingsPane.getStyleClass().add("edge-to-edge");
        createBindingToWindowHeight(scrollableSettingsPane);
        
        VBox tabContent = new VBox();
        tabContent.setAlignment(Pos.TOP_CENTER);
        tabContent.getStyleClass().add(TAB_VBOX_STYLE_CLASS);
        tabContent.getChildren().addAll(tabTitleHBox, scrollableSettingsPane);
        
        
        graphicTab.setId(tabName);
        graphicTab.setContent(tabContent);
        return graphicTab;        
    }
    
    private void generateSettingsForTabGrid(GridPane tabGridPane, List<PrintProfileSetting> settingsToGenerate)
    {
        setupColumnsForGridPane(tabGridPane);
        
        int rowNumber = 0;
        
        for(PrintProfileSetting printProfileSetting : settingsToGenerate) 
        {
            tabGridPane.getRowConstraints().add(new RowConstraints());
            
            // Some changes to nozzle settings if there are no valves on the head
            boolean valvesFitted = HeadContainer.getHeadByID(headType).getValves() == Head.ValveType.FITTED;
            if(printProfileSetting.getId().equals("ejectionVolume")) 
            {
                changeLabelingOfEjectionVolumeBasedOnValves(printProfileSetting, valvesFitted);
            }
            if(printProfileSetting.getId().equals("partialBMinimum") && !valvesFitted) 
            {
                continue;
            }
            
            String valueType = printProfileSetting.getValueType();
            switch(valueType) 
            {
                case FLOAT:
                    if(printProfileSetting.isPerExtruder() && printProfileSetting.getValue().contains(":")) 
                    {
                        addPerExtruderValueRow(tabGridPane, printProfileSetting, rowNumber);
                    } else 
                    {
                        addSingleFieldRow(tabGridPane, printProfileSetting, rowNumber);
                    }
                    rowNumber++;
                    break;
                case INT:
                    if(printProfileSetting.isPerExtruder() && printProfileSetting.getValue().contains(":")) 
                    {
                        addPerExtruderValueRow(tabGridPane, printProfileSetting, rowNumber);
                    } else 
                    {
                        addSingleFieldRow(tabGridPane, printProfileSetting, rowNumber);
                    }
                    rowNumber++;
                    break;
                case BOOLEAN:
                    addCheckBoxRow(tabGridPane, printProfileSetting, rowNumber);
                    rowNumber++;
                    break;
                case OPTION:
                    if(printProfileSetting.getOptions().isPresent()) 
                    {
                        addComboBoxRow(tabGridPane, printProfileSetting, rowNumber);
                        rowNumber++;
                    } else
                    {
                        STENO.warning("Option setting, "+ printProfileSetting.getId() + ", has no options, setting will be ignored");
                    }
                    break;
                case NOZZLE:
                    addSelectionAndValueRow(tabGridPane, printProfileSetting, rowNumber);
                    rowNumber++;
                    break;
                case NUMBER_LIST:
                    addListFieldRow(tabGridPane, printProfileSetting, rowNumber, NUMBER_LIST_PATTERN);
                    rowNumber++;
                    break;
                default:
                    STENO.error("Value type of " + valueType + " not recognised, setting will be ignored");
            }
        }
    }
    
    /**
     * This method gets the {@link ScrollPane} to behave in a sensible-ish manner
     * It's height is bound to a multiple of the height of the window which means it will resize with the window
     * and a scrollbar will appear once the content exceeds it's length.
     * During initialisation the Scene is null so it is necessary to bind the height later.
     * 
     * @param scrollPane 
     */
    private void createBindingToWindowHeight(ScrollPane scrollPane)
    {
        if (tabPane.getScene() != null)
        {
            scrollPane.prefHeightProperty().bind(tabPane.getScene().getWindow().heightProperty().multiply(0.6).add(-50));
        } else
        {
            tabPane.sceneProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) 
                {
                    scrollPane.prefHeightProperty().bind(tabPane.getScene().getWindow().heightProperty().multiply(0.6).add(-50));
                }
            });
        }
    }
    
    /**
     * Clear the {@link GridPane} for regeneration.
     * 
     * @param gridPane 
     */
    private void clearGrid(GridPane gridPane) {
        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
    }
    
    /**
     * Sets up the {@link ColumnConstraints} for the provided {@link GridPane}
     * This set up is for a clean and consistent setting generation.
     * 
     * @param gridPane the pane to set the columns up for 
     */
    private void setupColumnsForGridPane(GridPane gridPane) {
        ColumnConstraints label0Col = new ColumnConstraints();
        ColumnConstraints label1Col = new ColumnConstraints();
        ColumnConstraints field0Col = new ColumnConstraints();
        ColumnConstraints label2Col = new ColumnConstraints();
        ColumnConstraints field1Col = new ColumnConstraints();      
        
        label0Col.setPercentWidth(20);
        label1Col.setPercentWidth(15);
        field0Col.setPercentWidth(20);
        label2Col.setPercentWidth(15);
        field1Col.setPercentWidth(20);
        
        label0Col.setHalignment(HPos.LEFT);
        label1Col.setHalignment(HPos.RIGHT);
        label2Col.setHalignment(HPos.RIGHT);
        
        gridPane.getColumnConstraints().addAll(label0Col, label1Col, field0Col, label2Col, field1Col);
    }
    
    /**
     * Add a single field setting to the given {@link GridPane}
     * It consists of a {@link Label} and a {@link RestrictedNumberField}.
     * 
     * @param gridPane the pane to add to
     * @param printProfileSetting the setting parameters
     * @param row the row number of the grid to add to
     * @return the GridPane
     */
    protected GridPane addSingleFieldRow(GridPane gridPane, PrintProfileSetting printProfileSetting, int row) {
        gridPane.add(createLabelElement(printProfileSetting.getSettingName(), true), 0, row);
        gridPane.add(createInputFieldWithOptionalUnit(printProfileSetting, printProfileSetting.getValue(), Nozzle.SINGLE), 2, row);
        return gridPane;
    }
    
    /**
     * Add a combobox field setting to the given {@link GridPane}
     * It consists of a {@link Label} and a {@link ComboBox}.
     * 
     * @param gridPane the pane to add to
     * @param printProfileSetting the setting parameters
     * @param row the row number of the grid to add to
     * @return the GridPane
     */
    protected GridPane addComboBoxRow(GridPane gridPane, PrintProfileSetting printProfileSetting, int row) {
        gridPane.add(createLabelElement(printProfileSetting.getSettingName(), true), 0, row);
        gridPane.add(createComboBox(printProfileSetting), 2, row);
        return gridPane;
    }
    
    /**
     * Add a nozzle selection box and field setting to the given {@link GridPane}
     * It consists of a {@link Label} a {@link ComboBox} for selection of a nozzle
     * and a {@link RestrictedNumberField} for the value.
     * 
     * @param gridPane the pane to add to
     * @param printProfileSetting the setting parameters
     * @param row the row number of the grid to add to
     * @return the GridPane
     */
    protected GridPane addSelectionAndValueRow(GridPane gridPane, PrintProfileSetting printProfileSetting, int row) 
    {
        gridPane.add(createLabelElement(printProfileSetting.getSettingName(), true), 0, row);
        gridPane.add(createLabelElement("extrusion.nozzle", true), 1, row);
        ComboBox comboBox = createComboBox(printProfileSetting);
        gridPane.add(comboBox, 2, row);
        if(printProfileSetting.getChildren().isPresent()) 
        {
            PrintProfileSetting childSetting = printProfileSetting.getChildren().get().get(0);
            gridPane.add(createLabelElement(childSetting.getSettingName(), true), 3, row);
            if (childSetting.getValueType().equals(EXTRUSION))
            {
                gridPane.add(createInputFieldForNozzleSelection(childSetting, childSetting.getValue(), comboBox), 4, row);
            } else
            {
                gridPane.add(createInputFieldWithOptionalUnit(childSetting, childSetting.getValue(), Nozzle.SINGLE), 4, row);
            }
        }
        return gridPane;
    }
    
    /**
     * Add a double field setting to the given {@link GridPane} for settings with
     * a value per nozzle.
     * It consists of a {@link Label} and two {@link RestrictedNumberField}
     * 
     * @param gridPane the pane to add to
     * @param printProfileSetting the setting parameters
     * @param row the row number of the grid to add to
     * @return the GridPane
     */
    protected GridPane addPerExtruderValueRow(GridPane gridPane, PrintProfileSetting printProfileSetting, int row) {
        String[] values = printProfileSetting.getValue().split(":");
        gridPane.add(createLabelElement(printProfileSetting.getSettingName(), true), 0, row);
        gridPane.add(createLabelElement("Left Nozzle", true), 1, row);
        gridPane.add(createInputFieldWithOptionalUnit(printProfileSetting, values[0], Nozzle.LEFT), 2, row);
        gridPane.add(createLabelElement("Right Nozzle", true), 3, row);
        gridPane.add(createInputFieldWithOptionalUnit(printProfileSetting, values[1], Nozzle.RIGHT), 4, row);
        return gridPane;
    }
    
    /**
     * Add a check box row to the given {@link GridPane}.
     * 
     * @param gridPane the pane to add to
     * @param printProfileSetting the setting parameters
     * @param row the row number of the grid to add to
     * @return the GridPane
     */
    protected GridPane addCheckBoxRow(GridPane gridPane, PrintProfileSetting printProfileSetting, int row) {
        gridPane.add(createLabelElement(printProfileSetting.getSettingName(), true), 0, row);
        gridPane.add(createCheckBoxElement(printProfileSetting), 2, row);
        return gridPane;
    }
    
    protected GridPane addListFieldRow(GridPane gridPane, PrintProfileSetting printProfileSetting, int row, Pattern numberListPattern) {
        gridPane.add(createLabelElement(printProfileSetting.getSettingName(), true), 0, row);
        gridPane.add(createRestrictedNumberFieldWithPattern(printProfileSetting, numberListPattern), 2, row);
        return gridPane;
    }
    
    /**
     * Create a fxml {@link Label}.
     * 
     * @param labelText text to display as label
     * @param addColon if we want a colon added at the end
     * @return the Label
     */
    private Label createLabelElement(String labelText, boolean addColon) {
        String translation = Lookup.i18n(labelText);
        Label label = new Label(translation);
        if(addColon) {
            label.getStyleClass().add("colon");
        }
        label.setPadding(new Insets(0, 4, 0, 0));
        return label;
    }
    
    /**
     * Create a {@link HideableTooltip} element.
     * 
     * @param text the text to display in the tooltip
     * @return the Tooltip
     */
    private HideableTooltip createTooltipElement(String text) {
        HideableTooltip tooltip = new HideableTooltip();
        tooltip.setText(Lookup.i18n(text));
        return tooltip;
    }
    
    /**
     * Create a {@link CheckBox} element.
     * 
     * @param selected is the check box selected
     * @return the CheckBox
     */
    private CheckBox createCheckBoxElement(PrintProfileSetting printProfileSetting) {
        HideableTooltip hideableTooltip = createTooltipElement(printProfileSetting.getTooltip());
        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(Boolean.valueOf(printProfileSetting.getValue()));
        checkBox.setTooltip(hideableTooltip);
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            printProfileSetting.setValue(String.valueOf(newValue));
            isDirty.set(true);
        });
        return checkBox;
    }
   
    /**
     * Create a {@link RestrictedNumberField}.
     * 
     * @param printProfileSetting the setting parameters
     * @return the RestrictedNumberField
     */
    private RestrictedNumberField createRestrictedNumberField(PrintProfileSetting printProfileSetting, String value, Nozzle nozzle) {
        HideableTooltip hideableTooltip = createTooltipElement(printProfileSetting.getTooltip());
        RestrictedNumberField restrictedNumberField = new RestrictedNumberField();
        restrictedNumberField.setTooltip(hideableTooltip);
        restrictedNumberField.setText(value);
        restrictedNumberField.setPrefWidth(60);
        if(printProfileSetting.getValueType().equals(FLOAT) || 
                printProfileSetting.getValueType().equals(EXTRUSION)) {
            restrictedNumberField.setAllowedDecimalPlaces(2);
        }
        restrictedNumberField.setMaxLength(6);
        
        if(printProfileSetting.getMinimumValue().isPresent()) {
            restrictedNumberField.setMinValue(Double.valueOf(printProfileSetting.getMinimumValue().get()));
        }
        if(printProfileSetting.getMaximumValue().isPresent()) {
            restrictedNumberField.setMaxValue(Double.valueOf(printProfileSetting.getMaximumValue().get()));
        }
        
        restrictedNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            String originalValues = printProfileSetting.getValue();
            switch(nozzle) {
            case SINGLE:
                printProfileSetting.setValue(newValue);
                break;
            case LEFT:
                String updatedLeftValue = newValue + ":" + originalValues.split(":")[1];
                printProfileSetting.setValue(updatedLeftValue);
                break;
            case RIGHT:
                String updatedRightValue = originalValues.split(":")[0] + ":" + newValue;
                printProfileSetting.setValue(updatedRightValue);
                break;
            }
            
            isDirty.set(true);
        });
        return restrictedNumberField;
    }
    
    /**
     * Create a {@link ComboBox} element.
     * 
     * @param printProfileSetting the setting parameters
     * @return the ComboBox
     */
    private ComboBox createComboBox(PrintProfileSetting printProfileSetting) {
        ComboBox comboBox = new ComboBox();
        if(printProfileSetting.getOptions().isPresent()) {
            comboBox = setupStandardComboBox(printProfileSetting, comboBox);
        } else if(printProfileSetting.getValueType().equals(NOZZLE)) {
            comboBox = setupComboBoxForNozzleSelection(printProfileSetting, comboBox);
        }
        
        comboBox.setPrefWidth(150);
        comboBox.setTooltip(createTooltipElement(printProfileSetting.getTooltip()));
        comboBox.getStyleClass().add("cmbCleanCombo");
        
        return comboBox;
    }
    
    private ComboBox setupStandardComboBox(PrintProfileSetting printProfileSetting, ComboBox comboBox) {
        Map<String, String> optionMap = printProfileSetting.getOptions().get();
            
        ObservableList<Option> options = optionMap.entrySet().stream()
            .map(entry -> new Option(entry.getKey(), entry.getValue()))
            .collect(Collectors.toCollection(FXCollections::observableArrayList));

        comboBox.setItems(options);

        Optional<Option> value = options.stream()
                .filter(option -> option.getOptionId().equals(printProfileSetting.getValue()))
                .findFirst();

        if(value.isPresent()) {
            comboBox.setValue(value.get());
        }
        
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Option option = (Option) newValue;
            printProfileSetting.setValue(option.getOptionId());
            isDirty.set(true);
        });
        
        return comboBox;
    }
    
    private ComboBox setupComboBoxForNozzleSelection(PrintProfileSetting printProfileSetting, ComboBox comboBox)
    {
        List<String> nozzles = new ArrayList<>();
        nozzles.addAll(nozzleOptions);
        
        HeadFile currentHead = HeadContainer.getHeadByID(headType);
        if (currentHead.getNozzleHeaters().size() == 2 && 
                Lookup.getUserPreferences().getSlicerType() == SlicerType.Cura4) 
        {
            nozzles.set(0, nozzleOptions.get(0) + " (Material 2)");
            nozzles.set(1, nozzleOptions.get(1) + " (Material 1)");
            
            if(printProfileSetting.getNonOverrideAllowed().isPresent() &&
                printProfileSetting.getNonOverrideAllowed().get()) {
                nozzles.add("Model Material");
            }
        } else if (currentHead.getNozzleHeaters().size() == 2 || currentHead.getNozzles().size() == 1) 
        {
            comboBox.setDisable(true);
        }
        
        if (Lookup.getUserPreferences().getSlicerType() == SlicerType.Cura4
                && printProfileSetting.getId().equals("firstLayerNozzle"))
        {
            comboBox.setDisable(true);
        }
        
        comboBox.setItems(FXCollections.observableList(nozzles));
        int selectionIndex = Integer.parseInt(printProfileSetting.getValue());
        comboBox.getSelectionModel().select(selectionIndex); 

        comboBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            printProfileSetting.setValue(String.valueOf(newValue));
            isDirty.set(true);
        });
                    
        return comboBox;
    }
    
    /**
     * Create a {@link HBox} that contains a {@link RestrictedNumberField} with an
     * Optional {@link Label} for the unit.
     * 
     * @param printProfileSetting the setting parameters
     * @return a HBox
     */
    private HBox createInputFieldWithOptionalUnit(PrintProfileSetting printProfileSetting, String value, Nozzle nozzle) {
        RestrictedNumberField field = createRestrictedNumberField(printProfileSetting, value, nozzle);
        HBox hbox = new HBox(field);
        if(printProfileSetting.getUnit().isPresent()) {
            Label unitLabel = createLabelElement(printProfileSetting.getUnit().get(), false);
            hbox.getChildren().add(unitLabel);
        }
        hbox.setSpacing(4);
        return hbox;
    }
    
    private HBox createRestrictedNumberFieldWithPattern(PrintProfileSetting printProfileSetting, Pattern restrictionPattern) {
        RestrictedNumberField field = createRestrictedNumberField(printProfileSetting, printProfileSetting.getValue(), Nozzle.SINGLE);
        field.setRestrictionPattern(restrictionPattern);
        field.setPrefWidth(150);
        HBox hbox = new HBox(field);
        return hbox;
    }
    
    /**
     * Create a {@link HBox} that contains a {@link RestrictedNumberField} with an
     * Optional {@link Label} for the unit. Also pass in the {@link ComboBox} that links
     * to the field.
     * 
     * @param printProfileSetting the setting parameters
     * @return a HBox
     */
    private HBox createInputFieldForNozzleSelection(PrintProfileSetting printProfileSetting, String value, ComboBox nozzleSelection) {
        RestrictedNumberField field = createRestrictedNumberField(printProfileSetting, value, Nozzle.SINGLE);
       
        setExtrusionWidthLimits(nozzleSelection.getSelectionModel().getSelectedIndex(), field);
        nozzleSelection.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            setExtrusionWidthLimits(newValue, field);
        });
        
        HBox hbox = new HBox(field);
        if(printProfileSetting.getUnit().isPresent()) {
            Label unitLabel = createLabelElement(printProfileSetting.getUnit().get(), false);
            hbox.getChildren().add(unitLabel);
        }
        hbox.setSpacing(4);
        return hbox;
    }
    
    private void setExtrusionWidthLimits(Number newValue, RestrictedNumberField extrusionSetting) {
        int index = newValue.intValue();
        if(index < 0 || index > 1) {
            index = 0;
        }
        String widthOption = nozzleOptions.get(index);
        Optional<NozzleData> optionalNozzleData = HeadContainer.getHeadByID(headType).getNozzles()
                .stream()
                .filter(nozzle -> nozzle.getMinExtrusionWidth() > 0.0)
                .filter(nozzle -> (Float.toString(nozzle.getDiameter()) + " mm").equals(widthOption))
                .findFirst();
        
        if (optionalNozzleData.isPresent()) {
            NozzleData nozzleData = optionalNozzleData.get();
            extrusionSetting.setMinValue(nozzleData.getMinExtrusionWidth());
            extrusionSetting.setMaxValue(nozzleData.getMaxExtrusionWidth());
        // For some reason these don't actually exist in the head file so we always do this...
        } else {
            switch (widthOption) {
                case "0.3mm":
                    extrusionSetting.setMinValue(POINT_3_MIN_WIDTH);
                    extrusionSetting.setMaxValue(POINT_3_MAX_WIDTH);
                    break;
                case "0.4mm":
                    extrusionSetting.setMinValue(POINT_4_MIN_WIDTH);
                    extrusionSetting.setMaxValue(POINT_4_MAX_WIDTH);
                    break;
                case "0.6mm":
                    extrusionSetting.setMinValue(POINT_6_MIN_WIDTH);
                    extrusionSetting.setMaxValue(POINT_6_MAX_WIDTH);
                    break;
                case "0.8mm":
                    extrusionSetting.setMinValue(POINT_8_MIN_WIDTH);
                    extrusionSetting.setMaxValue(POINT_8_MAX_WIDTH);
                    break;
                default:
                    break;
            }
        }
    }
    
    /**
     * A very specific method. We need to change the label and tooltip text if
     * there are no valves, i.e. it's actually a retraction amount.
     * 
     * @param ejectionVolume
     */
    private void changeLabelingOfEjectionVolumeBasedOnValves(PrintProfileSetting ejectionVolume, boolean valvesFitted) {
        if(valvesFitted) {
            ejectionVolume.setSettingName("nozzle.ejectionVolume");
            ejectionVolume.setTooltip("profileLibraryHelp.nozzleEjectionVolume");
        } else {
            ejectionVolume.setSettingName("nozzle.retractionVolume");
            ejectionVolume.setTooltip("profileLibraryHelp.nozzleRetractionVolume");
        }
    }
    
    public class ProfileDetailsGenerationException extends Exception
    {
        public ProfileDetailsGenerationException(String message) 
        {
            super(message);
        }
    }
}
