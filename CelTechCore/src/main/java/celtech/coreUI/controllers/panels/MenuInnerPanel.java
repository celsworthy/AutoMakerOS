/*
 * Copyright 2015 CEL UK
 */
package celtech.coreUI.controllers.panels;

import java.util.List;
import javafx.beans.value.ObservableBooleanValue;

/**
 * MenuInnerPanel defines the properties needed to instantiate an inner panel from
 the ExtrasMenuPanel.
 * 
 * @author tony
 */
public interface MenuInnerPanel
{
    /**
     * An OperationButton defines a button which is required to be shown when this
     * panel is displayed, and the callback to call when the button is pressed.
     */
    public interface OperationButton {
        
        /**
         * The i18n id of the text of the button.
         */
        public String getTextId();
        
        /**
         * Return the file name for the fxml that defines the button graphic.
         */
        public String getFXMLName();
        
        /**
         * The i18n id of the tooltip text.
         */
        public String getTooltipTextId();

        /**
         * This will be called when the button has been clicked.
         */
        public void whenClicked();
        
        /**
         * This observable property governs when the button should be enabled.
         */
        public ObservableBooleanValue whenEnabled();
    }
    
    /**
     * Return i18n id of the title to appear in the ExtrasMenu vertical menu.
     */
    public String getMenuTitle();
    
    /**
     * Return the list of OperationButtons that should be offered when this panel is displayed.
     */
    public List<OperationButton> getOperationButtons();
    
    /**
     * Used to notify the panel controlled that it has been selected and shown
     */
    public void panelSelected();
}
