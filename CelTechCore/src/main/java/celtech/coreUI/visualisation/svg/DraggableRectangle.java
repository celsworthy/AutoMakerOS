package celtech.coreUI.visualisation.svg;

import javafx.scene.shape.SVGPath;

/**
 *
 * @author Ian
 */
public class DraggableRectangle extends SVGPath implements PrintableShape
{

    public DraggableRectangle(double w, double h)
    {
        setContent("M0 0 H 50 V 50 H 0 L 0 0 z");
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
