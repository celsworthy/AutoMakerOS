package celtech.coreUI.components.tips;

import celtech.coreUI.components.Orientation;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;

/**
 *
 * @author Ian
 */
public abstract class TaggablePane extends Pane
{

    private final ObjectProperty<Orientation> tagOrientation = new SimpleObjectProperty<>(Orientation.NORTH);
    private ArrowTag tipArrow = null;

    // Each taggable class should work out it's own interpretation of the position using the hints provided
    public abstract Point2D getTagPosition();

    public void setTagOrientation(Orientation orientation)
    {
        tagOrientation.set(orientation);
    }

    public Orientation getTagOrientation()
    {
        return tagOrientation.get();
    }

    public ReadOnlyObjectProperty<Orientation> tagOrientationProperty()
    {
        return tagOrientation;
    }
    
    
    public void installTag()
    {
        if (tipArrow == null)
        {
            tipArrow = new ArrowTag();
            tipArrow.initialise(this);
        }
    }

    public void installTag(String i18nTitle)
    {
        if (tipArrow == null)
        {
            tipArrow = new ArrowTag();
            tipArrow.initialise(this, i18nTitle);
        }
    }

    public void uninstallTag()
    {
        if (tipArrow != null)
        {
            tipArrow.destroy();
            tipArrow = null;
        }
    }

    public ArrowTag getTag()
    {
        return tipArrow;
    }
}
