package celtech.utils.settingsgeneration;

import celtech.FXTest;
import celtech.JavaFXConfiguredTest;
import celtech.Lookup;
import celtech.coreUI.components.RestrictedNumberField;
import celtech.roboxbase.configuration.SlicerType;
import celtech.roboxbase.configuration.datafileaccessors.PrintProfileSettingsContainer;
import celtech.roboxbase.configuration.profilesettings.PrintProfileSetting;
import celtech.roboxbase.configuration.profilesettings.PrintProfileSettings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Test class for the {@link ProfileDetailsGenerator}
 * 
 * @author George Salter
 */
@Category(FXTest.class)
public class ProfileDetailsGeneratorTest extends JavaFXConfiguredTest {
    
    private static final String FLOAT_VALUE_TYPE = "float";
    private static final String OPTION_VALUE_TYPE = "option";
    private static final String NOZZLE_VALUE_TYPE = "nozzle";
    private static final String EXTRUSION_VALUE_TYPE = "extrusion";
    
    private static final String SLICER_SETTING_NAME = "Slicer Setting Name";
    private static final String TOOLTIP = "this is a tooltip";
    private static final String DEFAULT_FLOAT_VALUE = "1.234";
    private static final String BOOLEAN_TRUE_VALUE = "true";
    private static final String DUAL_EXTRUDER_VALUE = "0.3:0.6";
    private static final String UNIT = "mm";
    private static final String COLON_STYLE = "colon";
    
    private static final String OPTION_ID_1 = "line";
    private static final String OPTION_ID_2 = "tri";
    private static final String OPTION_VALUE_1 = "Line";
    private static final String OPTION_VALUE_2 = "Triangle";
    
    private static final String NOZZLE_OPTION_1 = "0.3mm";
    private static final String NOZZLE_OPTION_2 = "0.6mm";
    
    PrintProfileSettings printProfileSettings;
    ProfileDetailsGenerator profileDetailsGenerator;
    
    GridPane gridPane;
    
    private void setup() {
        printProfileSettings = PrintProfileSettingsContainer.getInstance().getPrintProfileSettingsForSlicer(SlicerType.Cura);
        profileDetailsGenerator = new ProfileDetailsGenerator(printProfileSettings, new SimpleBooleanProperty(false));
        
        List<String> nozzleList = new ArrayList<>();
        nozzleList.add(NOZZLE_OPTION_1);
        nozzleList.add(NOZZLE_OPTION_2);
        ObservableList nozzleOptions = FXCollections.observableArrayList(nozzleList);
        profileDetailsGenerator.setNozzleOptions(nozzleOptions);
        
        profileDetailsGenerator.setHeadType("RBX01-SM");
        
        gridPane = new GridPane();
        
        RowConstraints row0 = new RowConstraints();
        gridPane.getRowConstraints().add(row0);
        
        ColumnConstraints col0 = new ColumnConstraints();
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        ColumnConstraints col3 = new ColumnConstraints();
        ColumnConstraints col4 = new ColumnConstraints();
        gridPane.getColumnConstraints().addAll(col0, col1, col2, col3, col4);
    }
    
    @Test
    public void testAddSingleFieldRow() {
        setup();
        
        PrintProfileSetting slicerSetting = new PrintProfileSetting();
        slicerSetting.setSettingName(SLICER_SETTING_NAME);
        slicerSetting.setTooltip(TOOLTIP);
        slicerSetting.setUnit(Optional.of(UNIT));
        slicerSetting.setValue(DEFAULT_FLOAT_VALUE);
        slicerSetting.setValueType(FLOAT_VALUE_TYPE);
        gridPane = profileDetailsGenerator.addSingleFieldRow(gridPane, slicerSetting, 0);
        
        Label label = (Label) gridPane.getChildren().get(0);
        HBox hbox = (HBox) gridPane.getChildren().get(1);//package celtech.utils.settingsgeneration;
        
        assertThat(label.getText(), is(equalTo(SLICER_SETTING_NAME)));
        assertTrue(label.getStyleClass().contains(COLON_STYLE));
        
        RestrictedNumberField restrictedNumberField = (RestrictedNumberField) hbox.getChildren().get(0);
        Label unitLabel = (Label) hbox.getChildren().get(1);
        
        assertThat(restrictedNumberField.getText(), is(equalTo(DEFAULT_FLOAT_VALUE)));
        assertThat(restrictedNumberField.getTooltip().getText(), is(equalTo(TOOLTIP)));
        assertThat(unitLabel.getText(), is(equalTo(UNIT)));
    }
    
    @Test
    public void testAddComboBoxRow() {
        setup();
        
        PrintProfileSetting slicerSetting = new PrintProfileSetting();
        slicerSetting.setSettingName(SLICER_SETTING_NAME);
        slicerSetting.setTooltip(TOOLTIP);
        slicerSetting.setValueType(OPTION_VALUE_TYPE);
        Map<String, String> optionsMap = new HashMap<>();
        optionsMap.put(OPTION_ID_1, OPTION_VALUE_1);
        optionsMap.put(OPTION_ID_2, OPTION_VALUE_2);
        Optional<Map<String, String>> options = Optional.of(optionsMap);
        slicerSetting.setOptions(options);
        slicerSetting.setValue(OPTION_ID_2);
        gridPane = profileDetailsGenerator.addComboBoxRow(gridPane, slicerSetting, 0);
        
        Label label = (Label) gridPane.getChildren().get(0);
        ComboBox combo = (ComboBox) gridPane.getChildren().get(1);
        
        assertThat(label.getText(), is(equalTo(SLICER_SETTING_NAME)));
        assertTrue(label.getStyleClass().contains(COLON_STYLE));
        
        Option value = (Option) combo.getValue();
        
        assertThat(value.getOptionId(), is(equalTo(OPTION_ID_2)));
        assertThat(value.getOptionValue(), is(equalTo(OPTION_VALUE_2)));
        assertThat(combo.getItems().size(), is(equalTo(2)));
        assertThat(combo.getTooltip().getText(), is(equalTo(TOOLTIP)));
        assertTrue(combo.getStyleClass().contains("cmbCleanCombo"));
    }
    
    @Test
    public void testAddSelectionAndValueRow() {
        setup();
        
        PrintProfileSetting extrusionSlicerSetting = new PrintProfileSetting();
        extrusionSlicerSetting.setSettingName(SLICER_SETTING_NAME);
        extrusionSlicerSetting.setTooltip(TOOLTIP);
        extrusionSlicerSetting.setUnit(Optional.empty());
        extrusionSlicerSetting.setValueType(EXTRUSION_VALUE_TYPE);
        extrusionSlicerSetting.setValue(DEFAULT_FLOAT_VALUE);
        
        PrintProfileSetting nozzleSlicerSetting = new PrintProfileSetting();
        nozzleSlicerSetting.setSettingName(SLICER_SETTING_NAME);
        nozzleSlicerSetting.setTooltip(TOOLTIP);
        nozzleSlicerSetting.setValueType(NOZZLE_VALUE_TYPE);
        nozzleSlicerSetting.setValue("0");
        List<PrintProfileSetting> children = new ArrayList<>();
        children.add(extrusionSlicerSetting);
        nozzleSlicerSetting.setChildren(Optional.of(children));
        
        gridPane = profileDetailsGenerator.addSelectionAndValueRow(gridPane, nozzleSlicerSetting, 0);
        
        Label label = (Label) gridPane.getChildren().get(0);
        Label boxLabel = (Label) gridPane.getChildren().get(1);
        ComboBox combo = (ComboBox) gridPane.getChildren().get(2);
        HBox fieldHBox = (HBox) gridPane.getChildren().get(4);
        
        assertThat(label.getText(), is(equalTo(SLICER_SETTING_NAME)));
        assertTrue(label.getStyleClass().contains(COLON_STYLE));
        
        assertThat(boxLabel.getText(), is(equalTo(Lookup.i18n("extrusion.nozzle"))));
        assertThat(combo.getItems().size(), is(equalTo(2)));
        assertThat(combo.getItems().get(0), is(equalTo(NOZZLE_OPTION_1)));
        assertThat(combo.getItems().get(1), is(equalTo(NOZZLE_OPTION_2)));
        assertThat(combo.getTooltip().getText(), is(equalTo(TOOLTIP)));
        assertTrue(combo.getStyleClass().contains("cmbCleanCombo"));
        
        RestrictedNumberField restrictedNumberField = (RestrictedNumberField) fieldHBox.getChildren().get(0);
        // We should not have a unit label
        assertThat(fieldHBox.getChildren().size(), is(equalTo(1)));
        
        assertThat(restrictedNumberField.getTooltip().getText(), is(equalTo(TOOLTIP)));
    }
    
    @Test
    public void testAddPerExtruderValueRow() {
        setup();
        
        PrintProfileSetting slicerSetting = new PrintProfileSetting();
        slicerSetting.setSettingName(SLICER_SETTING_NAME);
        slicerSetting.setTooltip(TOOLTIP);
        slicerSetting.setUnit(Optional.of(UNIT));
        slicerSetting.setValueType(FLOAT_VALUE_TYPE);
        slicerSetting.setValue(DUAL_EXTRUDER_VALUE);
        gridPane = profileDetailsGenerator.addPerExtruderValueRow(gridPane, slicerSetting, 0);
        
        Label label = (Label) gridPane.getChildren().get(0);
        Label leftLabel = (Label) gridPane.getChildren().get(1);
        HBox leftHBox = (HBox) gridPane.getChildren().get(2);
        Label rightLabel = (Label) gridPane.getChildren().get(3);
        HBox rightHBox = (HBox) gridPane.getChildren().get(4);
        
        String[] expectedValues = DUAL_EXTRUDER_VALUE.split(":");
        
        assertThat(label.getText(), is(equalTo(SLICER_SETTING_NAME)));
        assertTrue(label.getStyleClass().contains(COLON_STYLE));
        
        assertThat(leftLabel.getText(), is(equalTo("Left Nozzle")));
        assertTrue(leftLabel.getStyleClass().contains(COLON_STYLE));

        RestrictedNumberField leftField = (RestrictedNumberField) leftHBox.getChildren().get(0);
 
        assertThat(leftField.getTooltip().getText(), is(equalTo(TOOLTIP)));
        assertThat(leftField.getText(), is(equalTo(expectedValues[0])));
        
        assertThat(rightLabel.getText(), is(equalTo("Right Nozzle")));
        assertTrue(rightLabel.getStyleClass().contains(COLON_STYLE));
        
        RestrictedNumberField rightField = (RestrictedNumberField) rightHBox.getChildren().get(0);
        Label rightUnitLabel = (Label) rightHBox.getChildren().get(1);
        
        assertThat(rightField.getTooltip().getText(), is(equalTo(TOOLTIP)));
        assertThat(rightField.getText(), is(equalTo(expectedValues[1])));
        
        assertThat(rightUnitLabel.getText(), is(equalTo(UNIT)));
        assertFalse(rightUnitLabel.getStyleClass().contains(COLON_STYLE));
    }
    
    @Test
    public void testAddCheckBoxRow() {
        setup();
        
        PrintProfileSetting slicerSetting = new PrintProfileSetting();
        slicerSetting.setSettingName(SLICER_SETTING_NAME);
        slicerSetting.setTooltip(TOOLTIP);
        slicerSetting.setValue(BOOLEAN_TRUE_VALUE);
        gridPane = profileDetailsGenerator.addCheckBoxRow(gridPane, slicerSetting, 0);
        
        Label label = (Label) gridPane.getChildren().get(0);
        CheckBox checkBox = (CheckBox) gridPane.getChildren().get(1);
        
        assertThat(label.getText(), is(equalTo(SLICER_SETTING_NAME)));
        assertTrue(label.getStyleClass().contains(COLON_STYLE));
        
        assertTrue(checkBox.isSelected());
        assertThat(checkBox.getTooltip().getText(), is(equalTo(TOOLTIP)));
    }
}
