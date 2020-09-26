package celtech.coreUI.controllers.panels.userpreferences;

import celtech.roboxbase.ApplicationFeature;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.SlicerType;
import javafx.scene.control.ListCell;

/**
 *
 * @author George Salter
 */
public class SlicerTypeCell extends ListCell<SlicerType> {
    
    @Override
    protected void updateItem(SlicerType slicerType, boolean empty) 
    {
        super.updateItem(slicerType, empty);
        
        if(slicerType != null && slicerType.equals(SlicerType.Cura4)) 
        {
            if(BaseConfiguration.isApplicationFeatureEnabled(ApplicationFeature.LATEST_CURA_VERSION)) 
            {
                setStyle("-fx-text-fill:black");
            } 
            else
            {
                setStyle("-fx-text-fill:grey");
            }
        }
        
        setText(slicerType == null ? "" : slicerType.name());
    }
}
