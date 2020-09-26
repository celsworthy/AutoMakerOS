package celtech.coreUI.visualisation.modelDisplay;

import celtech.coreUI.visualisation.ApplicationMaterials;
import celtech.coreUI.visualisation.ShapeProvider;
import celtech.coreUI.visualisation.ShapeProviderThreeD;
import celtech.coreUI.visualisation.Xform;
import celtech.modelcontrol.ModelContainer;
import celtech.modelcontrol.ModelGroup;
import celtech.roboxbase.utils.Math.MathUtils;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class SelectionHighlighter extends Group implements ShapeProvider.ShapeChangeListener
{

    private final Stenographer steno = StenographerFactory.getStenographer(
            SelectionHighlighter.class.getName());
    public static final String idString = "selectionHighlighter";

    private boolean selectionIsGroup = false;

    private Xform selectionBoxBackLeftTop = null;
    private Xform selectionBoxBackRightTop = null;
    private Xform selectionBoxFrontLeftTop = null;
    private Xform selectionBoxFrontRightTop = null;
    private Xform selectionBoxBackLeftBottom = null;
    private Xform selectionBoxBackRightBottom = null;
    private Xform selectionBoxFrontLeftBottom = null;
    private Xform selectionBoxFrontRightBottom = null;

    private final double cornerBracketLength = 3;

    private DoubleProperty boxScaleProperty = new SimpleDoubleProperty(1.0);

//    private final ScaleControls scaleControls;

    /**
     *
     * @param modelContainer
     */
    public SelectionHighlighter(final ModelContainer modelContainer, double cameraDistance)
    {
        cameraDistanceChange(cameraDistance);

        this.setId(idString);
        if (modelContainer instanceof ModelGroup)
        {
            selectionIsGroup = true;
        }
        buildSelectionBox();

//        scaleControls = new ScaleControls(this);
        modelContainer.addShapeChangeListener(this);
    }

    private void buildSelectionBox()
    {
        selectionBoxBackLeftBottom = generateSelectionCornerGroup(0, 90, 0);

        selectionBoxBackRightBottom = generateSelectionCornerGroup(0, -180, 0);

        selectionBoxBackLeftTop = generateSelectionCornerGroup(180, 0, 0);

        selectionBoxBackRightTop = generateSelectionCornerGroup(180, 90, 0);

        selectionBoxFrontLeftBottom = generateSelectionCornerGroup(0, 0, 0);

        selectionBoxFrontRightBottom = generateSelectionCornerGroup(0, -90, 0);

        selectionBoxFrontLeftTop = generateSelectionCornerGroup(180, -90, 0);

        selectionBoxFrontRightTop = generateSelectionCornerGroup(0, 0, 180);

        getChildren().addAll(selectionBoxBackLeftBottom, selectionBoxBackRightBottom,
                selectionBoxBackLeftTop, selectionBoxBackRightTop,
                selectionBoxFrontLeftBottom, selectionBoxFrontRightBottom,
                selectionBoxFrontLeftTop, selectionBoxFrontRightTop);

//        selectionBoxFrontRightTop.getChildren().add(ambientLight);
    }

    @Override
    public void shapeChanged(ShapeProvider shapeProviderRaw)
    {
        if (shapeProviderRaw instanceof ShapeProviderThreeD)
        {
            ShapeProviderThreeD shapeProvider = (ShapeProviderThreeD) shapeProviderRaw;

            double halfWidth = shapeProvider.getScaledWidth() / 2;
            double halfDepth = shapeProvider.getScaledDepth() / 2;
            double halfHeight = shapeProvider.getScaledHeight() / 2;
            double minX = shapeProvider.getCentreX() - halfWidth;
            double maxX = shapeProvider.getCentreX() + halfWidth;
            double minZ = shapeProvider.getCentreZ() - halfDepth;
            double maxZ = shapeProvider.getCentreZ() + halfDepth;
            double minY = shapeProvider.getCentreY() - halfHeight;
            double maxY = shapeProvider.getCentreY() + halfHeight;

            selectionBoxBackLeftBottom.setTz(maxZ);
            selectionBoxBackLeftBottom.setTx(minX);
            selectionBoxBackLeftBottom.setTy(maxY);

            selectionBoxBackRightBottom.setTz(maxZ);
            selectionBoxBackRightBottom.setTx(maxX);
            selectionBoxBackRightBottom.setTy(maxY);

            selectionBoxFrontLeftBottom.setTz(minZ);
            selectionBoxFrontLeftBottom.setTx(minX);
            selectionBoxFrontLeftBottom.setTy(maxY);

            selectionBoxFrontRightBottom.setTz(minZ);
            selectionBoxFrontRightBottom.setTx(maxX);
            selectionBoxFrontRightBottom.setTy(maxY);

            selectionBoxBackLeftTop.setTz(maxZ);
            selectionBoxBackLeftTop.setTx(minX);
            selectionBoxBackLeftTop.setTy(minY);

            selectionBoxBackRightTop.setTz(maxZ);
            selectionBoxBackRightTop.setTx(maxX);
            selectionBoxBackRightTop.setTy(minY);

            selectionBoxFrontLeftTop.setTz(minZ);
            selectionBoxFrontLeftTop.setTx(minX);
            selectionBoxFrontLeftTop.setTy(minY);

            selectionBoxFrontRightTop.setTz(minZ);
            selectionBoxFrontRightTop.setTx(maxX);
            selectionBoxFrontRightTop.setTy(minY);

            //Place the scale boxes
//            scaleControls.place(minX, maxX, minY, maxY, minZ, maxZ);
        }
    }

    private Xform generateSelectionCornerGroup(double xRotate, double yRotate, double zRotate)
    {

        final double cylRadius = 0.75;

        PhongMaterial material = ApplicationMaterials.getSelectionBoxMaterial();

        Xform selectionCornerTransform = new Xform();
        Group selectionCorner = new Group();
        selectionCornerTransform.getChildren().add(selectionCorner);

        Box part1 = new Box(cylRadius, cornerBracketLength, cylRadius);
        part1.setMaterial(material);
        part1.setTranslateY(boxScaleProperty.get() * (-cornerBracketLength / 2));
        part1.translateYProperty().bind(boxScaleProperty.multiply(-cornerBracketLength / 2));

        Box part2 = new Box(cylRadius, cornerBracketLength, cylRadius);
        part2.setMaterial(material);
        part2.setRotationAxis(MathUtils.zAxis);
        part2.setRotate(-90);
        part2.setTranslateX(boxScaleProperty.get() * (cornerBracketLength / 2));
        part2.translateXProperty().bind(boxScaleProperty.multiply(cornerBracketLength / 2));

        Box part3 = new Box(cylRadius, cornerBracketLength, cylRadius);
        part3.setMaterial(material);
        part3.setRotationAxis(MathUtils.xAxis);
        part3.setRotate(-90);
        part3.setTranslateZ(boxScaleProperty.get() * (cornerBracketLength / 2));
        part3.translateZProperty().bind(boxScaleProperty.multiply(cornerBracketLength / 2));
        selectionCorner.getChildren().addAll(part1, part2, part3);

        selectionCornerTransform.setRotateX(xRotate);
        selectionCornerTransform.setRotateY(yRotate);
        selectionCornerTransform.setRotateZ(zRotate);

        part1.scaleXProperty().bind(boxScaleProperty);
        part1.scaleYProperty().bind(boxScaleProperty);
        part1.scaleZProperty().bind(boxScaleProperty);

        part2.scaleXProperty().bind(boxScaleProperty);
        part2.scaleYProperty().bind(boxScaleProperty);
        part2.scaleZProperty().bind(boxScaleProperty);

        part3.scaleXProperty().bind(boxScaleProperty);
        part3.scaleYProperty().bind(boxScaleProperty);
        part3.scaleZProperty().bind(boxScaleProperty);

        return selectionCornerTransform;
    }

    public final void cameraDistanceChange(double cameraDistance)
    {
        double newScale = cameraDistance / 350;
        if (newScale < 0.3)
        {
            newScale = 0.3;
        } else if (newScale > 1.5)
        {
            newScale = 1.5;
        }
        boxScaleProperty.set(newScale);
    }
}
