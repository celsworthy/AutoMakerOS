package celtech.coreUI.controllers.panels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Tony Aldhous
 */
public class CameraProfilesControlSettingsManager
{
    private class FieldHandler
    {
        // Uses controlSettings and populateGrid from enclosing class.
        private TextField field;
        private String control;
        
        FieldHandler() {
        }
        
        void setField(TextField field) {
            this.field = field;
            field.focusedProperty().addListener(this::onFocusChange);
            field.textProperty().addListener(this::onTextChange);
            field.setOnAction(this::onAction);
        }

        void removeField() {
            if (field != null) {
                field.focusedProperty().removeListener(this::onFocusChange);
                field.textProperty().removeListener(this::onTextChange);
                field.setOnAction(null);
                field = null;
            }
        }

        void setControl(String control) {
            this.control = control;
        }

        public void onFocusChange(ObservableValue<? extends Boolean> observable, Boolean oldHasFocus, Boolean newHasFocus)
        {
            //System.out.println("OnFocusChange(" + control + ", " + oldHasFocus + ", " + newHasFocus + ")");
            if (oldHasFocus) {
                update();
            }
        }
    
        public void onTextChange(ObservableValue<? extends String> observable, String oldText, String newText)
        {
            //System.out.println("onTextChange(" + control + ", \"" + oldText + "\", \"" + newText + "\")");
            // Do nothing for the moment.
        }
    
        public void onAction(ActionEvent event) {
            //System.out.println("onAction(" + control + ")");
            update();
        }

        public void update() {
            if (field != null) {
                String text = field.getText().strip();
                if (GridPane.getColumnIndex(field) == 0) {
                    // This is a control name field.
                    if (!text.equals(control)) {
                        String value = controlSettings.getOrDefault(control, "");
                        if (!control.isBlank()) {
                            controlSettings.remove(control);
                        }
                        control = text;
                        if (!control.isBlank()) {
                            controlSettings.put(control, value);
                        }
                        isDirty.set(true);
                        populateGrid();
                    }
                }
                else {
                    // This is a value field.
                    if (!control.isBlank()) {
                        controlSettings.put(control, text);
                        isDirty.set(true);
                    }
                }
            }
        }

    }
    
    private static final Stenographer STENO = StenographerFactory.getStenographer(CameraProfilesControlSettingsManager.class.getName());
    
    private GridPane controlGrid = null;
    private BooleanProperty isDirty = null;
    private boolean isSystemProfile = false;
    private Map<String, String> controlSettings;
    private Map<TextField, FieldHandler> fieldMap = new HashMap<>();
    
    public CameraProfilesControlSettingsManager() {
    }
    
    public void setControlSettings(Map<String, String> controlSettings, boolean isSystemProfile) {
        this.isSystemProfile = isSystemProfile;
        this.controlSettings = controlSettings;
        populateGrid();
    }
    
    public void initialise(GridPane controlGrid, BooleanProperty isDirty) {
        this.controlGrid = controlGrid;
        this.isDirty = isDirty;
        controlGrid.getChildren()
                   .stream()
                   .filter((c)-> c instanceof TextField)
                   .map((c) -> (TextField)c)
                   .forEach((field) -> {
                        FieldHandler handler = new FieldHandler();
                        handler.setField(field);
                        handler.setControl("");
                        fieldMap.put(field, handler);
                   });
    }

    private TextField setupField(List<TextField> fields, int fieldIndex, int column, int row, String fieldControl, String fieldValue) {
        TextField field;
        FieldHandler handler;
        if (fieldIndex < fields.size()) {
            field = (TextField)fields.get(fieldIndex);
            handler = fieldMap.get(field);
            GridPane.setConstraints(field, column, row);
        }
        else {
            field = new TextField();
            controlGrid.add(field, column, row);
            handler = new FieldHandler();
            handler.setField(field);
            fieldMap.put(field, handler);
        }
        handler.setControl(fieldControl);

        if (fieldControl.isBlank() && column > 0) {
            field.setText("");
            field.setDisable(true);
        }
        else {
            field.setText(fieldValue);
            field.setDisable(isSystemProfile);
        }
        
        
        return field;
    }
    
    public void populateGrid() {
        List<Node> children = controlGrid.getChildren();
        List<TextField> fields = children.stream()
                                         .filter((c)-> c instanceof TextField)
                                         .map((c) -> (TextField)c)
                                         .collect(Collectors.toList());
        List<String> controlSet = new ArrayList<>(controlSettings.keySet());
        Collections.sort(controlSet);
        int fieldIndex = 0;
        int lastRow = controlSet.size() + 1;
        // Row 0 contains two labels.
        // The last row contains two empty fields, so new controls can be added.
        for (int row = 1; row <= lastRow; ++row) {
            String control;
            String value;
            if (row == lastRow) {
                control = "";
                value = "";
            }
            else {
                control = controlSet.get(row-1);
                value = controlSettings.get(control);
            }
            setupField(fields, fieldIndex, 0, row, control, control);
            fieldIndex++;

            setupField(fields, fieldIndex, 1, row, control, value);
            fieldIndex++;
        }
        
        // Remove unused fields
        children = controlGrid.getChildren();
        for (int fIndex = fields.size() - 1; fIndex >= fieldIndex; fIndex--) {
            TextField field = fields.get(fIndex);
            FieldHandler handler = fieldMap.get(field);
            handler.removeField();
            fieldMap.remove(field);
            children.remove(fields.get(fIndex));
        }
    }
}
