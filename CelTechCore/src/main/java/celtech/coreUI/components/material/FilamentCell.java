/*
 * Copyright 2015 CEL UK
 */
package celtech.coreUI.components.material;

import celtech.Lookup;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.datafileaccessors.FilamentContainer;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author tony
 */
public class FilamentCell extends ListCell<Filament>
{

    private static int SWATCH_SQUARE_SIZE = 16;

    HBox cellContainer;
    Rectangle rectangle = new Rectangle();
    Label label;

    public FilamentCell()
    {
        cellContainer = new HBox();
        cellContainer.setAlignment(Pos.CENTER_LEFT);
        rectangle = new Rectangle(SWATCH_SQUARE_SIZE, SWATCH_SQUARE_SIZE);
        label = new Label();
        label.setId("materialComponentComboLabel");
        label.getStyleClass().add("filamentSwatchPadding");
        cellContainer.getChildren().addAll(rectangle, label);
    }

    @Override
    protected void updateItem(Filament item, boolean empty)
    {
        super.updateItem(item, empty);
        if (item != null && !empty
                && item != FilamentContainer.UNKNOWN_FILAMENT)
        {
            Filament filament = (Filament) item;
            setGraphic(cellContainer);
            rectangle.setVisible(true);
            rectangle.setFill(filament.getDisplayColour());

            label.setText(filament.getLongFriendlyName());
        } else if (item == FilamentContainer.UNKNOWN_FILAMENT)
        {
            Filament filament = (Filament) item;
            setGraphic(cellContainer);
            rectangle.setVisible(false);

            label.setText(filament.getLongFriendlyName());
        } else
        {
            setGraphic(null);
            label.setText(Lookup.i18n("materialComponent.unknown"));
        }
    }
}
