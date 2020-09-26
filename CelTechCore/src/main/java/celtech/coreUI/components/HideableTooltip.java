package celtech.coreUI.components;

import javafx.scene.control.Tooltip;
import javafx.util.Duration;

/**
 *
 * @author Ian
 */
public class HideableTooltip extends Tooltip
{
    public HideableTooltip()
    {
        this.getStyleClass().add("hideableTooltip");
        this.setWrapText(true);
        this.setMaxWidth(600);
        this.setShowDelay(new Duration(500));
        this.setShowDuration(Duration.INDEFINITE);
    }
}
