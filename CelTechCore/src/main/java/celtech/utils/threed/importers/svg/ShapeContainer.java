package celtech.utils.threed.importers.svg;

import celtech.coreUI.visualisation.Edge;
import celtech.coreUI.visualisation.ScreenExtents;
import celtech.modelcontrol.ItemState;
import celtech.modelcontrol.ProjectifiableThing;
import celtech.modelcontrol.ResizeableTwoD;
import celtech.modelcontrol.ScaleableTwoD;
import celtech.modelcontrol.TranslateableTwoD;
import celtech.modelcontrol.TwoDItemState;
import celtech.roboxbase.utils.RectangularBounds;
import celtech.roboxbase.utils.twod.ShapeToWorldTransformer;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author ianhudson
 */
public class ShapeContainer extends ProjectifiableThing implements Serializable,
        ScaleableTwoD,
        TranslateableTwoD,
        ResizeableTwoD,
        ShapeToWorldTransformer
{

    private final Stenographer steno = StenographerFactory.getStenographer(ShapeContainer.class.getName());
    private static final long serialVersionUID = 1L;

    private List<Shape> shapes = new ArrayList<>();

    public ShapeContainer()
    {
        super();
        initialise();
    }

    public ShapeContainer(File modelFile)
    {
        super(modelFile);
        initialise();
        initialiseTransforms();
    }

    public ShapeContainer(String name, List<Shape> shapes)
    {
        super();
        setModelName(name);

        if (shapes.size() > 1)
        {
            Group shapeGroup = new Group();
            shapeGroup.getChildren().addAll(shapes);
            this.getChildren().add(shapeGroup);
        } else
        {
            this.getChildren().add(shapes.get(0));
        }
        this.shapes.addAll(shapes);
        initialise();
        initialiseTransforms();
    }

    public ShapeContainer(String name, Shape shape)
    {
        super();
        setModelName(name);

        this.getChildren().add(shape);
        this.shapes.add(shape);

        initialise();
        initialiseTransforms();
    }

    private void initialise()
    {
        preferredXScale = new SimpleDoubleProperty(1);
        preferredYScale = new SimpleDoubleProperty(1);
        preferredRotationTurn = new SimpleDoubleProperty(0);
        rotationTransforms = new ArrayList<>();
    }

    @Override
    public ItemState getState()
    {
        return new TwoDItemState(modelId,
                transformMoveToPreferred.getX(),
                transformMoveToPreferred.getY(),
                preferredXScale.get(),
                preferredYScale.get(),
                preferredRotationTurn.get());
    }

    @Override
    public void setState(ItemState state)
    {
        if (state instanceof TwoDItemState)
        {
            TwoDItemState convertedState = (TwoDItemState) state;
            transformMoveToPreferred.setX(convertedState.x);
            transformMoveToPreferred.setZ(convertedState.y);

            preferredXScale.set(convertedState.preferredXScale);
            transformScalePreferred.setX(convertedState.preferredXScale);
            preferredYScale.set(convertedState.preferredYScale);
            transformScalePreferred.setY(convertedState.preferredYScale);

            preferredRotationTurn.set(convertedState.preferredRotationTurn);

            lastTransformedBoundsInParent = calculateBoundsInParentCoordinateSystem();
            notifyScreenExtentsChange();
            notifyShapeChange();
        }
    }

    @Override
    public ProjectifiableThing makeCopy()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearElements()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void translateBy(double xMove, double yMove)
    {
        transformMoveToPreferred.setX(transformMoveToPreferred.getX() + xMove);
        transformMoveToPreferred.setY(transformMoveToPreferred.getY() + yMove);

        updateLastTransformedBoundsInParentForTranslateByX(xMove);
        updateLastTransformedBoundsInParentForTranslateByY(yMove);

        checkOffBed();
    }

    @Override
    public void translateTo(double xPosition, double yPosition)
    {
        translateXTo(xPosition);
        translateDepthPositionTo(yPosition);
    }

    @Override
    public void translateXTo(double xPosition)
    {
        RectangularBounds bounds = lastTransformedBoundsInParent;

        double newMaxX = xPosition + bounds.getWidth() / 2;
        double newMinX = xPosition - bounds.getWidth() / 2;

        double finalXPosition = xPosition;

        if (newMinX < 0)
        {
            finalXPosition += -newMinX;
        } else if (newMaxX > printVolumeWidth)
        {
            finalXPosition -= (newMaxX - printVolumeWidth);
        }

        double currentXPosition = lastTransformedBoundsInParent.getCentreX();
        double requiredTranslation = finalXPosition - currentXPosition;
        transformMoveToPreferred.setX(transformMoveToPreferred.getX() + requiredTranslation);

        updateLastTransformedBoundsInParentForTranslateByX(requiredTranslation);
        checkOffBed();
        notifyShapeChange();
        notifyScreenExtentsChange();
    }

    @Override
    public void translateDepthPositionTo(double yPosition)
    {
        RectangularBounds bounds = lastTransformedBoundsInParent;

        double newMaxY = yPosition + bounds.getDepth() / 2;
        double newMinY = yPosition - bounds.getDepth() / 2;

        double finalYPosition = yPosition;

        if (newMinY < 0)
        {
            finalYPosition += -newMinY;
        } else if (newMaxY > printVolumeDepth)
        {
            finalYPosition -= (newMaxY - printVolumeDepth);
        }

        double currentYPosition = lastTransformedBoundsInParent.getCentreY();
        double requiredTranslation = finalYPosition - currentYPosition;
        transformMoveToPreferred.setY(transformMoveToPreferred.getY() + requiredTranslation);

        updateLastTransformedBoundsInParentForTranslateByY(requiredTranslation);
        checkOffBed();
        notifyShapeChange();
        notifyScreenExtentsChange();
    }

    @Override
    public void resizeHeight(double height)
    {
        Bounds bounds = getBoundsInLocal();

        double currentHeight = bounds.getHeight();

        double newScale = height / currentHeight;
        transformScalePreferred.setY(newScale);

        notifyShapeChange();
        notifyScreenExtentsChange();
    }

    @Override
    public void resizeWidth(double width)
    {
        Bounds bounds = getBoundsInLocal();

        double originalWidth = bounds.getWidth();

        double newScale = width / originalWidth;
        transformScalePreferred.setX(newScale);

        notifyShapeChange();
        notifyScreenExtentsChange();
    }

    @Override
    public void selectedAction()
    {
        if (isSelected())
        {
            //Set outline
            setStyle("-fx-border-color: blue; -fx-border-size: 1px;");
        } else
        {
            //Set outline
            setStyle("-fx-border-color: black;");
        }
    }

    @Override
    protected boolean recalculateScreenExtents()
    {
        boolean extentsChanged = false;

        Bounds localBounds = getBoundsInLocal();

        double minX = localBounds.getMinX();
        double maxX = localBounds.getMaxX();
        double minY = localBounds.getMinY();
        double maxY = localBounds.getMaxY();

        Point2D leftBottom = localToScreen(minX, maxY);
        Point2D rightBottom = localToScreen(maxX, maxY);
        Point2D leftTop = localToScreen(minX, minY);
        Point2D rightTop = localToScreen(maxX, minY);

        ScreenExtents lastExtents = extents;
        if (extents == null && leftBottom != null)
        {
            extents = new ScreenExtents();
        }

        if (extents != null && leftBottom != null)
        {
            extents.heightEdges.clear();
            extents.heightEdges.add(0, new Edge(leftBottom, leftTop));
            extents.heightEdges.add(1, new Edge(rightBottom, rightTop));

            extents.widthEdges.clear();
            extents.widthEdges.add(0, new Edge(leftBottom, rightBottom));
            extents.widthEdges.add(1, new Edge(leftTop, rightTop));

            extents.recalculateMaxMin();
        }

        if (extents != null
                && !extents.equals(lastExtents))
        {
            extentsChanged = true;
        }

        return extentsChanged;
    }

    @Override
    public double getTransformedHeight()
    {
        return getBoundsInParent().getHeight();
    }

    @Override
    public double getTransformedWidth()
    {
        return lastTransformedBoundsInParent.getWidth();
    }

    @Override
    public double getScaledWidth()
    {
        return getTransformedWidth();
    }

    @Override
    public double getScaledHeight()
    {
        return getTransformedHeight();
    }

    @Override
    public double getCentreX()
    {
        return lastTransformedBoundsInParent.getMinX() + lastTransformedBoundsInParent.getWidth() / 2.0;
    }

    @Override
    public double getCentreY()
    {
        return lastTransformedBoundsInParent.getMinY() + lastTransformedBoundsInParent.getHeight() / 2.0;
    }

    @Override
    protected void printVolumeBoundsUpdated()
    {
    }

    @Override
    public void checkOffBed()
    {
    }

    @Override
    public Point2D transformShapeToRealWorldCoordinates(float vertexX, float vertexY)
    {
        return bed.sceneToLocal(localToScene(vertexX, vertexY));
    }

    public List<Shape> getShapes()
    {
        return shapes;
    }

    @Override
    public void shrinkToFitBed()
    {
        BoundingBox printableBoundingBox = (BoundingBox) getBoundsInLocal();

        double scaling = 1.0;

        double relativeXSize = printableBoundingBox.getWidth() / printVolumeWidth;
        double relativeYSize = printableBoundingBox.getHeight() / printVolumeDepth;
        steno.info("Relative sizes of model: X " + relativeXSize + " Y " + relativeYSize);

        if (relativeXSize > relativeYSize)
        {
            if (relativeXSize > 1)
            {
                scaling = 1 / relativeXSize;
            }
        } else if (relativeYSize > relativeXSize)
        {
            if (relativeYSize > 1)
            {
                scaling = 1 / relativeYSize;
            }

        }

        if (scaling != 1.0f)
        {
            transformScalePreferred.setX(scaling);
            transformScalePreferred.setY(scaling);
        }

        lastTransformedBoundsInParent = calculateBoundsInParentCoordinateSystem();
    }

    @Override
    public void setBedCentreOffsetTransform()
    {
        BoundingBox printableBoundingBox = (BoundingBox) getBoundsInLocal();

        bedCentreOffsetX = -printableBoundingBox.getMinX();
        bedCentreOffsetY = -printableBoundingBox.getMinY();
        transformBedCentre.setX(bedCentreOffsetX);
        transformBedCentre.setY(bedCentreOffsetY);
        updateLastTransformedBoundsInParentForTranslateByX(bedCentreOffsetX);
        updateLastTransformedBoundsInParentForTranslateByY(bedCentreOffsetY);
    }

    protected final void initialiseTransforms()
    {
        transformScalePreferred = new Scale(1, 1, 1);
        transformMoveToPreferred = new Translate(0, 0, 0);
        transformBedCentre = new Translate(0, 0, 0);

        transformRotateTurnPreferred = new Rotate(0, 0, 0, 0, Z_AXIS);
        rotationTransforms.add(transformRotateTurnPreferred);

        setBedCentreOffsetTransform();

        /**
         * Rotations (which are all around the centre of the model) must be
         * applied before any translations.
         */
        getTransforms().addAll(transformMoveToPreferred,
                transformBedCentre,
                transformRotateTurnPreferred,
                transformScalePreferred
        );

        updateOriginalModelBounds();

        lastTransformedBoundsInParent = calculateBoundsInParentCoordinateSystem();

        notifyShapeChange();
        notifyScreenExtentsChange();
    }

    @Override
    protected RectangularBounds calculateBoundsInLocal()
    {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;

        for (Shape shape : shapes)
        {
            Bounds localBounds = shape.getBoundsInLocal();
            minX = Math.min(localBounds.getMinX(), minX);
            minY = Math.min(localBounds.getMinY(), minY);
            minZ = Math.min(localBounds.getMinZ(), minZ);

            maxX = Math.max(localBounds.getMaxX(), maxX);
            maxY = Math.max(localBounds.getMaxY(), maxY);
            maxZ = Math.max(localBounds.getMaxZ(), maxZ);
        }

        double newwidth = maxX - minX;
        double newdepth = maxZ - minZ;
        double newheight = maxY - minY;

        double newcentreX = minX + (newwidth / 2);
        double newcentreY = minY + (newheight / 2);
        double newcentreZ = minZ + (newdepth / 2);

        return new RectangularBounds(minX, maxX, minY, maxY, minZ, maxZ, newwidth,
                newheight, newdepth, newcentreX, newcentreY,
                newcentreZ);
    }

    @Override
    public RectangularBounds calculateBoundsInParentCoordinateSystem()
    {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        if (bed != null)
        {
            for (Shape shape : shapes)
            {
                Bounds boundsInLocal = shape.getBoundsInLocal();
                Bounds worldBounds = bed.sceneToLocal(localToScene(boundsInLocal));

                steno.info("Started with local bounds: " + boundsInLocal);
                steno.info("Finished with bed bounds: " + worldBounds);

                minX = Math.min(worldBounds.getMinX(), minX);
                minY = Math.min(worldBounds.getMinY(), minY);

                maxX = Math.max(worldBounds.getMaxX(), maxX);
                maxY = Math.max(worldBounds.getMaxY(), maxY);
            }
        }

        double newwidth = maxX - minX;
        double newheight = maxY - minY;

        double newcentreX = minX + (newwidth / 2);
        double newcentreY = minY + (newheight / 2);

        return new RectangularBounds(minX, maxX, minY, maxY, 0, 0, newwidth,
                newheight, 0, newcentreX, newcentreY,
                0);
    }

    @Override
    public RectangularBounds calculateBoundsInBedCoordinateSystem()
    {
        return calculateBoundsInParentCoordinateSystem();
    }

    @Override
    protected void updateScaleTransform(boolean dropToBed)
    {
        checkOffBed();
        notifyShapeChange();
        notifyScreenExtentsChange();
    }

    private void updateLastTransformedBoundsInParentForTranslateByX(double deltaCentreX)
    {
        if (lastTransformedBoundsInParent != null)
        {
            lastTransformedBoundsInParent.translateX(deltaCentreX);
        }
        notifyShapeChange();
        notifyScreenExtentsChange();
    }

    private void updateLastTransformedBoundsInParentForTranslateByY(double deltaCentreY)
    {
        if (lastTransformedBoundsInParent != null)
        {
            lastTransformedBoundsInParent.translateY(deltaCentreY);
        }
        notifyShapeChange();
        notifyScreenExtentsChange();
    }

    @Override
    public void moveToCentre()
    {
        translateTo(bedCentreOffsetX, bedCentreOffsetY);

        lastTransformedBoundsInParent = calculateBoundsInParentCoordinateSystem();
    }
}
