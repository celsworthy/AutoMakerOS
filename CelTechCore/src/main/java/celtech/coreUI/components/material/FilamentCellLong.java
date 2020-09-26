/*
 * Copyright 2015 CEL UK
 */
package celtech.coreUI.components.material;

import celtech.Lookup;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.datafileaccessors.FilamentContainer;

/**
 *
 * @author tony
 */
public class FilamentCellLong extends FilamentCell
{
    public FilamentCellLong()
    {
        super();
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

            if (filament.getMaterial() != null)
            {
                label.setText(filament.getLongFriendlyName() + " "
                        + filament.getMaterial().getFriendlyName());
            } else
            {
                label.setText(filament.getLongFriendlyName());
            }
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
