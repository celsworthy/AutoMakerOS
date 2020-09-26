package celtech.coreUI.controllers.panels.userpreferences;

import celtech.configuration.units.CurrencySymbol;
import javafx.scene.control.ListCell;

/**
 *
 * @author Ian
 */
public class CurrencySymbolListCell extends ListCell<CurrencySymbol>
{

    @Override
    protected void updateItem(CurrencySymbol symbol, boolean empty)
    {
        super.updateItem(symbol, empty);
        if (empty)
        {
            clearContent();
        } else
        {
            addContent(symbol);
        }
    }

    private void clearContent()
    {
        setText(null);
        setGraphic(null);
    }

    private void addContent(CurrencySymbol symbol)
    {
        setText(symbol.getDisplayString());
    }
}
