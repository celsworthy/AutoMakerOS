package celtech.coreUI.components.material;

import celtech.Lookup;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.datafileaccessors.FilamentContainer;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Ian
 */
public class SelectedFilamentDisplayNode extends HBox
{

    private static final int SWATCH_SQUARE_SIZE = 16;

    private final Rectangle rectangle;
    private final Label label;
    
    private Filament filamentOnDisplay = null;

    public SelectedFilamentDisplayNode()
    {
        setAlignment(Pos.CENTER_LEFT);
        rectangle = new Rectangle(SWATCH_SQUARE_SIZE, SWATCH_SQUARE_SIZE);
        label = new Label();
        label.setId("materialComponentComboLabel");
        label.getStyleClass().add("filamentSwatchPadding");
        getChildren().addAll(rectangle, label);
    }

    public void updateSelectedFilament(Filament filament)
    {
        if (filament != null
                && filament != FilamentContainer.UNKNOWN_FILAMENT)
        {
            rectangle.setVisible(true);
            rectangle.setFill(filament.getDisplayColour());

            label.setText(filament.getLongFriendlyName());
        } else if (filament == FilamentContainer.UNKNOWN_FILAMENT)
        {
            rectangle.setVisible(false);
            label.setText(filament.getLongFriendlyName());
        } else
        {
            rectangle.setVisible(false);
            label.setText(Lookup.i18n("materialComponent.unknown"));
        }
        
        filamentOnDisplay = filament;
    }
    
    public Filament getSelectedFilament()
    {
        return filamentOnDisplay;
    }
}
