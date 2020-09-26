package celtech.coreUI.visualisation.svg;

import javafx.scene.shape.SVGPath;

/**
 *
 * @author Ian
 */
public class DraggableCircle extends SVGPath implements PrintableShape
{

    public DraggableCircle(double d)
    {
        setContent("M 0 0 A 10 10 0 1 0 0.00001 0 z");
    }

    @Override
    public void relativeTranslate(double x, double y)
    {
        setTranslateX(getTranslateX() + x);
        setTranslateY(getTranslateY() + y);
    }

    @Override
    public String getSVGPathContent()
    {
        return getContent();
    }
}
