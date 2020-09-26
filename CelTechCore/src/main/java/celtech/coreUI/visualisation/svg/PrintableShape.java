package celtech.coreUI.visualisation.svg;

/**
 *
 * @author Ian
 */
public interface PrintableShape
{

    public void relativeTranslate(double x, double y);
    public String getSVGPathContent();
}
