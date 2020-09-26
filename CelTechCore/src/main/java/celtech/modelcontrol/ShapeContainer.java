package celtech.modelcontrol;

import celtech.coreUI.StandardColours;
import celtech.coreUI.visualisation.Edge;
import celtech.coreUI.visualisation.ScreenExtents;
import celtech.roboxbase.utils.Math.MathUtils;
import celtech.roboxbase.utils.RectangularBounds;
import celtech.roboxbase.utils.twod.ShapeToWorldTransformer;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
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
        RotatableTwoD,
        ShapeToWorldTransformer
{

    private static final Stenographer steno = StenographerFactory.getStenographer(ShapeContainer.class.getName());
    private static final long serialVersionUID = 1L;
    private static final double NOMINAL_STROKE_WIDTH = 0.3;
    
    protected static int nextModelId = 1;

    protected Group shapeGroup = new Group();
    private List<Shape> shapes = new ArrayList<>();
    private Scale transformMirror = null;
    protected boolean notifyEnabled = true;

    public void debugPrintTransforms(String message)
    {
        System.out.println(message +
                           "TMP: " +
                           transformMoveToPreferred);
    }
    
    public ShapeContainer()
    {
        super();
        this.getChildren().add(shapeGroup);
        initialise();
    }

    public ShapeContainer(String name, int modelId)
    {
        super();
        this.modelId = modelId;
        setModelName(name);
        this.getChildren().add(shapeGroup);
        initialise();
    }
    
    public ShapeContainer(File modelFile)
    {
        super(modelFile);
        this.getChildren().add(shapeGroup);
        initialise();
        initialiseTransforms(true);
   }

    public ShapeContainer(String name, List<Shape> shapes)
    {
        super();
        setModelName(name);

        shapeGroup.getChildren().addAll(shapes);
        this.getChildren().add(shapeGroup);
        this.shapes.addAll(shapes);
        initialise();
        initialiseTransforms(true);
    }
    
    public ShapeContainer(String name, Shape shape)
    {
        super();
        setModelName(name);

        shapeGroup.getChildren().add(shape);
        this.getChildren().add(shapeGroup);
        this.shapes.add(shape);

        initialise();
        initialiseTransforms(true);
    }

    public void setViewBoxTransform(double viewBoxOriginX, double viewBoxOriginY,
                                    double viewBoxWidth, double viewBoxHeight,
                                    double documentWidth, double documentHeight)
    {
        // Apply viewbox -> display scaling and offset.
        double scaleX = documentWidth / viewBoxWidth;
        double scaleY = documentHeight / viewBoxHeight;
        shapeGroup.scaleXProperty().set(scaleX);
        shapeGroup.scaleYProperty().set(scaleY);
        shapeGroup.translateXProperty().set(viewBoxOriginX * scaleX);
        shapeGroup.translateYProperty().set(viewBoxOriginY * scaleY);
        updateOriginalModelBounds();
        lastTransformedBoundsInParent = calculateBoundsInParentCoordinateSystem();
    }

    protected void initialise()
    {
        if (modelId < 0)
        {
            modelId = nextModelId;
            nextModelId += 1;
        }
        else if (nextModelId < modelId)
            nextModelId = modelId + 1;

        preferredXScale = new SimpleDoubleProperty(1.0);
        preferredYScale = new SimpleDoubleProperty(1.0);
        preferredRotationTurn = new SimpleDoubleProperty(0.0);
        rotationTransforms = new ArrayList<>();
        notifyEnabled = true;
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
            transformMoveToPreferred.setY(convertedState.y);

            preferredXScale.set(convertedState.preferredXScale);
            transformScalePreferred.setX(convertedState.preferredXScale);
            preferredYScale.set(convertedState.preferredYScale);
            transformScalePreferred.setY(convertedState.preferredYScale);

            preferredRotationTurn.set(convertedState.preferredRotationTurn);
            updateTransformsFromTurnAngle();

            lastTransformedBoundsInParent = calculateBoundsInParentCoordinateSystem();
            notifyShapeHasChanged();
        }
    }

    @Override
    public ProjectifiableThing makeCopy()
    {
        List<Shape> newShapes = new ArrayList<>();
        for (Shape originalShape : shapes)
        {
            Shape newShape = null;
            if (originalShape instanceof Arc)
            {
                Arc originalArc = (Arc)(originalShape);
                Arc newArc = new Arc();
                newArc.setCenterX(originalArc.getCenterX());
                newArc.setCenterY(originalArc.getCenterY());
                newArc.setLength(originalArc.getLength());
                newArc.setRadiusX(originalArc.getRadiusX());
                newArc.setRadiusY(originalArc.getRadiusY());
                newArc.setStartAngle(originalArc.getStartAngle());
                newArc.setType(originalArc.getType());
                newShape = newArc;
            }
            else if (originalShape instanceof Circle)
            {
                Circle originalCircle = (Circle)(originalShape);
                Circle newCircle = new Circle();
                newCircle.setCenterX(originalCircle.getCenterX());
                newCircle.setCenterY(originalCircle.getCenterY());
                newCircle.setRadius(originalCircle.getRadius());
                newShape = newCircle;
            }
            else if (originalShape instanceof CubicCurve)
            {
                CubicCurve originalCubic = (CubicCurve)(originalShape);
                CubicCurve newCubic = new CubicCurve();
                newCubic.setStartX(originalCubic.getStartX());
                newCubic.setStartY(originalCubic.getStartY());
                newCubic.setControlX1(originalCubic.getControlX1());
                newCubic.setControlY1(originalCubic.getControlY1());
                newCubic.setControlX2(originalCubic.getControlX2());
                newCubic.setControlY2(originalCubic.getControlY2());
                newCubic.setEndX(originalCubic.getEndX());
                newCubic.setEndY(originalCubic.getEndY());
                newShape = newCubic;
            }
            else if (originalShape instanceof Ellipse)
            {
                Ellipse originalEllipse = (Ellipse)(originalShape);
                Ellipse newEllipse = new Ellipse();
                newEllipse.setCenterX(originalEllipse.getCenterX());
                newEllipse.setCenterY(originalEllipse.getCenterY());
                newEllipse.setRadiusX(originalEllipse.getRadiusX());
                newEllipse.setRadiusY(originalEllipse.getRadiusY());
                newShape = newEllipse;
            }
            else if (originalShape instanceof Line)
            {
                Line originalLine = (Line)(originalShape);
                Line newLine = new Line();
                newLine.setStartX(originalLine.getStartX());
                newLine.setStartY(originalLine.getStartY());
                newLine.setEndX(originalLine.getEndX());
                newLine.setEndY(originalLine.getEndY());
                newShape = newLine;
            }
            else if (originalShape instanceof Polygon)
            {
                Polygon originalPolygon = (Polygon)(originalShape);
                List<Double> originalPoints = originalPolygon.getPoints();
                double[] newPoints = new double[originalPoints.size()];
                for (int i = 0; i < originalPoints.size(); ++i)
                    newPoints[i] = originalPoints.get(i);
                Polygon newPolygon = new Polygon(newPoints);
                newShape = newPolygon;
            }
            else if (originalShape instanceof Polyline)
            {
                Polyline originalPolyline = (Polyline)(originalShape);
                List<Double> originalPoints = originalPolyline.getPoints();
                double[] points = new double[originalPoints.size()];
                for (int i = 0; i < originalPoints.size(); ++i)
                    points[i] = originalPoints.get(i);
                Polyline newPolyline = new Polyline(points);
                newShape = newPolyline;
            }
            else if (originalShape instanceof QuadCurve)
            {
                QuadCurve originalQuad = (QuadCurve)(originalShape);
                QuadCurve newQuad = new QuadCurve();
                newQuad.setStartX(originalQuad.getStartX());
                newQuad.setStartY(originalQuad.getStartY());
                newQuad.setControlX(originalQuad.getControlX());
                newQuad.setControlY(originalQuad.getControlY());
                newQuad.setEndX(originalQuad.getEndX());
                newQuad.setEndY(originalQuad.getEndY());
                newShape = newQuad;
            }
            else if (originalShape instanceof Rectangle)
            {
                Rectangle originalRectangle = (Rectangle)(originalShape);
                Rectangle newRectangle = new Rectangle();
                newRectangle.setX(originalRectangle.getX());
                newRectangle.setY(originalRectangle.getY());
                newRectangle.setWidth(originalRectangle.getWidth());
                newRectangle.setHeight(originalRectangle.getHeight());
                newRectangle.setArcWidth(originalRectangle.getArcWidth());
                newRectangle.setArcHeight(originalRectangle.getArcHeight());
                newShape = newRectangle;
            }
            else if (originalShape instanceof SVGPath)
            {
                SVGPath originalPath = (SVGPath)(originalShape);
                SVGPath newPath = new SVGPath();
                newPath.setContent(originalPath.getContent());
                newPath.setFillRule(originalPath.getFillRule());
                newShape = newPath;
            }
            else if (originalShape instanceof Text)
            {
                Text originalText = (Text)(originalShape);
                Text newText = new Text();
                newText.setX(originalText.getX());
                newText.setY(originalText.getY());
                newText.setText(originalText.getText());
                newShape = newText;
            }
            for (Transform originalTransform : originalShape.getTransforms())
                newShape.getTransforms().add(originalTransform.clone());
            
            newShapes.add(newShape);
        }
        ShapeContainer copy = new ShapeContainer(getModelName(), newShapes);
        copy.shapeGroup.scaleXProperty().set(this.shapeGroup.scaleXProperty().get());
        copy.shapeGroup.scaleYProperty().set(this.shapeGroup.scaleYProperty().get());
        copy.shapeGroup.translateXProperty().set(this.shapeGroup.translateXProperty().get());
        copy.shapeGroup.translateYProperty().set(this.shapeGroup.translateYProperty().get());

        copy.setState(this.getState());
        copy.recalculateScreenExtents();
        return copy;
    }

    @Override
    public void clearElements()
    {
        shapes.clear();
        transformMoveToPreferred.setX(0.0);
        transformMoveToPreferred.setY(0.0);

        preferredXScale.set(1.0);
        transformScalePreferred.setX(1.0);
        preferredYScale.set(1.0);
        transformScalePreferred.setY(1.0);

        preferredRotationTurn.set(0.0);
        transformRotateTurnPreferred.setPivotX(0.0);
        transformRotateTurnPreferred.setPivotY(0.0);
        transformRotateTurnPreferred.setAngle(0.0);

        lastTransformedBoundsInParent = new RectangularBounds();
    }

    @Override
    public void translateBy(double xMove, double yMove)
    {
        transformMoveToPreferred.setX(transformMoveToPreferred.getX() + xMove);
        transformMoveToPreferred.setY(transformMoveToPreferred.getY() + yMove);

        notifyEnabled = false;
        updateLastTransformedBoundsInParentForTranslateByX(xMove);
        updateLastTransformedBoundsInParentForTranslateByY(yMove);
        notifyEnabled = true;
        notifyShapeHasChanged();
    }

    @Override
    public void translateTo(double xPosition, double yPosition)
    {
        notifyEnabled = false;
        translateXTo(xPosition);
        translateDepthPositionTo(yPosition);
        notifyEnabled = true;
        notifyShapeHasChanged();
    }

    @Override
    public void translateXTo(double xPosition)
    {
        double finalXPosition = xPosition;
/*
        RectangularBounds bounds = lastTransformedBoundsInParent;

        double newMaxX = xPosition + bounds.getWidth() / 2;
        double newMinX = xPosition - bounds.getWidth() / 2;

        if (newMinX < 0)
        {
            finalXPosition += -newMinX;
        } else if (newMaxX > printVolumeWidth)
        {
            finalXPosition -= (newMaxX - printVolumeWidth);
        }
*/
        RectangularBounds b = calculateBoundsInParentCoordinateSystem();
        double currentXPosition = lastTransformedBoundsInParent.getCentreX();
        double requiredTranslation = finalXPosition - currentXPosition;
        transformMoveToPreferred.setX(transformMoveToPreferred.getX() + requiredTranslation);

        updateLastTransformedBoundsInParentForTranslateByX(requiredTranslation);
    }

    @Override
    public void translateDepthPositionTo(double yPosition)
    {
        double finalYPosition = yPosition;
        double currentYPosition = lastTransformedBoundsInParent.getCentreY();
        double requiredTranslation = finalYPosition - currentYPosition;
        transformMoveToPreferred.setY(transformMoveToPreferred.getY() + requiredTranslation);

        updateLastTransformedBoundsInParentForTranslateByY(requiredTranslation);
    }

    @Override
    public void resizeHeight(double height)
    {
        Bounds bounds = getBoundsInLocal();

        double currentHeight = bounds.getHeight();

        double newScale = height / currentHeight;
        setYScale(newScale, false);
    }

    @Override
    public void resizeWidth(double width)
    {
        Bounds bounds = getBoundsInLocal();

        double originalWidth = bounds.getWidth();

        double newScale = width / originalWidth;
        setXScale(newScale, false);
    }

    @Override
    public void selectedAction()
    {
        setColourFromState();
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
            setShapeStrokeWidth(NOMINAL_STROKE_WIDTH);
        }

        return extentsChanged;
    }

    @Override
    public double getTransformedHeight()
    {
        return  getScaledHeight();
    }

    @Override
    public double getTransformedWidth()
    {
        return getScaledWidth();
    }
    

    @Override
    public double getScaledWidth()
    {
        if (originalModelBounds == null)
            updateOriginalModelBounds(); 
        return originalModelBounds.getWidth() * preferredXScale.doubleValue();
    }

    @Override
    public double getScaledHeight()
    {
        if (originalModelBounds == null)
            updateOriginalModelBounds(); 
        return originalModelBounds.getHeight() * preferredXScale.doubleValue();
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
        //setBedCentreOffsetTransform();
        //checkOffBed();
    }

    @Override
    public Point2D transformShapeToRealWorldCoordinates(float vertexX, float vertexY)
    {
        return bed.sceneToLocal(shapeGroup.localToScene(vertexX, vertexY));
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
        //steno.info("Relative sizes of model: X " + relativeXSize + " Y " + relativeYSize);

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
            preferredXScale.set(scaling);
            transformScalePreferred.setX(scaling);
            preferredYScale.set(scaling);
            transformScalePreferred.setY(scaling);
        }

        lastTransformedBoundsInParent = calculateBoundsInParentCoordinateSystem();
    }

    @Override
    public void setBedCentreOffsetTransform()
    {
    }
    
    protected void initialiseTransforms(boolean withMirror)
    {
        transformScalePreferred = new Scale(1, 1, 1);
        transformMoveToPreferred = new Translate(0, 0, 0);
        bedCentreOffsetX = 0.0;
        bedCentreOffsetY = 0.0;

        // Mirror about Y through the centre of the model to
        // mirror the model so the coordinates match the bed,
        // but leave the bounds unchanged.
        Bounds localBounds = getBoundsInLocal();
        transformRotateTurnPreferred = new Rotate(0, 0, 0, 0, Z_AXIS);
        rotationTransforms.add(transformRotateTurnPreferred);
        //setBedCentreOffsetTransform();

        /**
         * Rotations (which are all around the centre of the model) must be
         * applied before any translations.
         */        
        getTransforms().addAll(transformMoveToPreferred,
                transformRotateTurnPreferred,
                transformScalePreferred);
        
        if (withMirror)
        {
            transformMirror = new Scale(1, -1, 1);
            transformMirror.setPivotX(localBounds.getCenterX());
            transformMirror.setPivotY(localBounds.getCenterY());
            transformMirror.setPivotZ(0.0);
            getTransforms().add(transformMirror);
        }

        updateOriginalModelBounds();

        lastTransformedBoundsInParent = calculateBoundsInParentCoordinateSystem();
        notifyShapeHasChanged();
    }
    
    protected void setColourFromState()
    {
        Color fillColour = null;
        Color strokeColour = null;

        if (isSelected())
        {
            if (isOffBed.get())
                strokeColour = Color.CORAL;
            else
                strokeColour = StandardColours.HIGHLIGHT_ORANGE;
        }
        else if (isOffBed.get())
            strokeColour = Color.CRIMSON;
        else
            strokeColour = StandardColours.ROBOX_BLUE;

        if (isOffBed.get())
            fillColour = Color.CRIMSON;
        else
            fillColour = StandardColours.ROBOX_BLUE;

        fillColour = Color.color(fillColour.getRed(), fillColour.getGreen(), fillColour.getBlue(), 0.3);

        setShapeColours(fillColour, strokeColour);
    }

    protected void setShapeColours(Color fillColour, Color strokeColour)
    {
        for (Shape shape : shapes)
        {
            if (strokeColour != null)
            {
                shape.setStroke(strokeColour);
            }
            else
            {
                shape.setStroke(Color.TRANSPARENT);
            }
            if (fillColour != null)
                shape.setFill(fillColour);
            else
                shape.setFill(Color.TRANSPARENT);
        }
    }

    protected void setShapeStrokeWidth(double strokeWidth)
    {
        boolean adjustForScene = (bed != null && getScene() != null);
        
        for (Shape shape : shapes)
        {
            double w = strokeWidth;
            if (adjustForScene)
            {
                // Keep border width a constant size relative to the bed.
                Point2D sz = shape.sceneToLocal(bed.localToScene(0.0, 0.0));
                Point2D sw = shape.sceneToLocal(bed.localToScene(strokeWidth, 0.0));
                w = sw.subtract(sz).magnitude();
            }
            shape.setStrokeWidth(w);
        }
    }
    
    @Override
    public void setBedReference(Group bed)
    {
        super.setBedReference(bed);
        updateOriginalModelBounds();
        lastTransformedBoundsInParent = calculateBoundsInParentCoordinateSystem();
        notifyShapeHasChanged();
    }

    @Override
    protected RectangularBounds calculateBoundsInLocal()
    {
        
        Bounds groupBounds = getBoundsInLocal();
        
        return new RectangularBounds(groupBounds.getMinX(), groupBounds.getMaxX(),
                                     groupBounds.getMinY(), groupBounds.getMaxY(),
                                     groupBounds.getMinZ(), groupBounds.getMaxZ(),
                                     groupBounds.getWidth(), groupBounds.getHeight(), groupBounds.getDepth(),
                                     groupBounds.getCenterX(), groupBounds.getCenterY(), groupBounds.getCenterZ());
    }
    
    /**
     * Return the parent ShapeGroup else return null.
     */
    public ShapeGroup getParentShapeContainer()
    {
        if (getParent() instanceof ShapeGroup)
        {
            return (ShapeGroup) getParent();
        }
        else if (getParent() != null && getParent().getParent() instanceof ShapeGroup)
        {
            return (ShapeGroup) getParent().getParent();
        } 
        else
        {
            return null;
        }
    }

    @Override
    public RectangularBounds calculateBoundsInParentCoordinateSystem()
    {
        RectangularBounds rb = null;
        if (getParentShapeContainer() == null)
        {
            // If not in a group, the parent is "the bed". The calculation
            // currently only works if the scene has been instantiated.
            rb = calculateBoundsInBedCoordinateSystem();
        }
        else
        {
            Bounds groupBounds = getBoundsInParent();
            rb = new RectangularBounds(groupBounds.getMinX(), groupBounds.getMaxX(),
                                       groupBounds.getMinY(), groupBounds.getMaxY(),
                                       groupBounds.getMinZ(), groupBounds.getMaxZ(),
                                       groupBounds.getWidth(), groupBounds.getHeight(), groupBounds.getDepth(),
                                       groupBounds.getCenterX(), groupBounds.getCenterY(), groupBounds.getCenterZ());
        }
        //steno.info("ShapeContainer::calculateBoundsInParentCoordinateSystem() returns " + rb);
        return rb;
    }
    
    private Bounds getBoundsInBedCoordinateSystem()
    {
        // Get the bounds in the parent so that we get an axis-aligned bounding
        // box around the scaled/rotated/translated shape.
        Bounds b = getBoundsInParent();
        Node p = getParent();
        if (bed != null && p != null)
        {
            if (getScene() != null)
                b = bed.sceneToLocal(p.localToScene(b));
            else
                // Assume parent is bed reference
                b = p.localToParent(b); 
        }
        else
        {
            // Assume bed origin is at corner.
            b = new BoundingBox(b.getMinX() + 0.5 * printVolumeWidth, 
                                b.getMinY() + 0.5 * printVolumeDepth,
                                b.getWidth(),
                                b.getHeight()); // Confusingly, in 2D project, height direction in box is the printer depth direction.
        }
        return b;
    }    

    @Override
    public RectangularBounds calculateBoundsInBedCoordinateSystem()
    {
        Bounds bedBounds = getBoundsInBedCoordinateSystem();
   
        RectangularBounds rb = new RectangularBounds(bedBounds.getMinX(), bedBounds.getMaxX(),
                                        bedBounds.getMinY(), bedBounds.getMaxY(),
                                        bedBounds.getMinZ(), bedBounds.getMaxZ(),
                                        bedBounds.getWidth(), bedBounds.getHeight(), bedBounds.getDepth(),
                                        bedBounds.getCenterX(), bedBounds.getCenterY(), bedBounds.getCenterZ());

        //steno.info("ShapeContainer::calculateBoundsInParentCoordinateSystem() returns " + rb);
        
        return rb;
    }

    @Override
    protected void updateScaleTransform(boolean dropToBed)
    {
        lastTransformedBoundsInParent = calculateBoundsInParentCoordinateSystem();
        notifyShapeHasChanged();
    }
    
    @Override
    protected void setScalePivotToCentreOfModel()
    {
        transformScalePreferred.setPivotX(originalModelBounds.getCentreX());
        transformScalePreferred.setPivotY(originalModelBounds.getCentreY());
    }

    @Override
    protected void setRotationPivotsToCentreOfModel()
    {
        transformRotateTurnPreferred.setPivotX(originalModelBounds.getCentreX());
        transformRotateTurnPreferred.setPivotY(originalModelBounds.getCentreY());
    }
    
    protected void notifyShapeHasChanged()
    {
        if (notifyEnabled)
        {
            //steno.info("notifyShapeHasChanged: lastTransformedBoundsInParent = " + lastTransformedBoundsInParent);
            checkOffBed();
            setColourFromState();
            notifyShapeChange();
            notifyScreenExtentsChange();
        }
    }
    
    @Override
    public void checkOffBed()
    {
        // Only check if not in a ShapeGroup.
        if (getParent() != null && !(getParent().getParent() instanceof ShapeGroup))
        {
            Bounds bounds = getBoundsInBedCoordinateSystem();
            if (bounds != null)
            {
                double epsilon = 0.001;
                // For 2D shapes, X is width, Y is depth.

                if (MathUtils.compareDouble(bounds.getMinX(), 0, epsilon) == MathUtils.LESS_THAN
                        || MathUtils.compareDouble(bounds.getMaxX(), printVolumeWidth,
                                epsilon) == MathUtils.MORE_THAN
                        || MathUtils.compareDouble(bounds.getMinY(), 0, epsilon) == MathUtils.LESS_THAN
                        || MathUtils.compareDouble(bounds.getMaxY(), printVolumeDepth,
                                epsilon) == MathUtils.MORE_THAN)
                {
                    isOffBed.set(true);
                } else
                {
                    isOffBed.set(false);
                }
            }
        }
        // System.out.println("ShapeContainer.checkOffBed() set to " + Boolean.toString(isOffBed.get()));
    }

    private void updateLastTransformedBoundsInParentForTranslateByX(double deltaCentreX)
    {
        if (lastTransformedBoundsInParent == null)
            lastTransformedBoundsInParent = calculateBoundsInParentCoordinateSystem();
        else
            lastTransformedBoundsInParent.translateX(deltaCentreX);
        notifyShapeHasChanged();
    }

    private void updateLastTransformedBoundsInParentForTranslateByY(double deltaCentreY)
    {
        if (lastTransformedBoundsInParent == null)
            lastTransformedBoundsInParent = calculateBoundsInParentCoordinateSystem();
        else
            lastTransformedBoundsInParent.translateY(deltaCentreY);
        notifyShapeHasChanged();
    }
    
    public void updateLastTransformedBoundsInParent()
    {
        lastTransformedBoundsInParent = calculateBoundsInParentCoordinateSystem();
    }

    @Override
    public void moveToCentre()
    {
        lastTransformedBoundsInParent = calculateBoundsInParentCoordinateSystem();
        translateTo(0.5 * printVolumeWidth, 0.5 * printVolumeDepth);
    }
    
    public static void writeShape(final ObjectOutputStream out,
                                  final Shape shape)
        throws IOException
    {
        if (shape != null)
        {
            out.writeBoolean(false);
            Transform tl = shape.getLocalToParentTransform();
            out.writeDouble(tl.getMxx());
            out.writeDouble(tl.getMxy());
            out.writeDouble(tl.getMyx());
            out.writeDouble(tl.getMyy());
            out.writeDouble(tl.getTx());
            out.writeDouble(tl.getTy());
            if (shape instanceof Arc)
            {
                final Arc arc = (Arc) shape;
                out.writeObject(Arc.class);
                out.writeDouble(arc.getCenterX());
                out.writeDouble(arc.getCenterY());
                out.writeDouble(arc.getLength());
                out.writeDouble(arc.getRadiusX());
                out.writeDouble(arc.getRadiusY());
                out.writeDouble(arc.getStartAngle());
                int t = 0;
                switch (arc.getType()) 
                {
                    case CHORD:
                        t = 1;
                        break;

                    case ROUND:
                        t = 2;
                        break;
                        
                    case OPEN:
                    default:
                        t = 0;
                        break;
                }
                out.writeInt(t);
            }
            else if (shape instanceof Circle)
            {
                final Circle circle = (Circle) shape;
                out.writeObject(Circle.class);
                out.writeDouble(circle.getCenterX());
                out.writeDouble(circle.getCenterY());
                out.writeDouble(circle.getRadius());
            }
            else if (shape instanceof CubicCurve)
            {
                final CubicCurve cubic = (CubicCurve) shape;
                out.writeObject(CubicCurve.class);
                out.writeDouble(cubic.getStartX());
                out.writeDouble(cubic.getStartY());
                out.writeDouble(cubic.getControlX1());
                out.writeDouble(cubic.getControlY1());
                out.writeDouble(cubic.getControlX2());
                out.writeDouble(cubic.getControlY2());
                out.writeDouble(cubic.getEndX());
                out.writeDouble(cubic.getEndY());
            }
            else if (shape instanceof Ellipse)
            {
                final Ellipse ellipse = (Ellipse) shape;
                out.writeObject(Ellipse.class);
                out.writeDouble(ellipse.getCenterX());
                out.writeDouble(ellipse.getCenterY());
                out.writeDouble(ellipse.getRadiusX());
                out.writeDouble(ellipse.getRadiusY());
            }
            else if (shape instanceof Line)
            {
                final Line line = (Line) shape;
                out.writeObject(Line.class);
                out.writeDouble(line.getStartX());
                out.writeDouble(line.getStartY());
                out.writeDouble(line.getEndX());
                out.writeDouble(line.getEndY());
            }
            else if (shape instanceof Polygon)
            {
                final Polygon polygon = (Polygon) shape;
                out.writeObject(Polygon.class);
                List<Double> pointList = polygon.getPoints();
                double pointArray[] = new double[pointList.size()];
                for (int i = 0; i < pointList.size(); ++i)
                    pointArray[i] = pointList.get(i);
                out.writeObject(pointArray);
            }
            else if (shape instanceof Polyline)
            {
                final Polyline polyline = (Polyline) shape;
                out.writeObject(Polyline.class);
                List<Double> pointList = polyline.getPoints();
                double pointArray[] = new double[pointList.size()];
                for (int i = 0; i < pointList.size(); ++i)
                    pointArray[i] = pointList.get(i);
                out.writeObject(pointArray);
            }
            else if (shape instanceof QuadCurve)
            {
                final QuadCurve quad = (QuadCurve) shape;
                out.writeObject(QuadCurve.class);
                out.writeDouble(quad.getStartX());
                out.writeDouble(quad.getStartY());
                out.writeDouble(quad.getControlX());
                out.writeDouble(quad.getControlY());
                out.writeDouble(quad.getEndX());
                out.writeDouble(quad.getEndY());
            }
            else if (shape instanceof Rectangle)
            {
                final Rectangle rectangle = (Rectangle) shape;
                out.writeObject(Rectangle.class);
                out.writeDouble(rectangle.getX());
                out.writeDouble(rectangle.getY());
                out.writeDouble(rectangle.getWidth());
                out.writeDouble(rectangle.getHeight());
                out.writeDouble(rectangle.getArcWidth());
                out.writeDouble(rectangle.getArcHeight());
            }
            else if (shape instanceof SVGPath)
            {
                final SVGPath svgPath = (SVGPath) shape;
                out.writeObject(SVGPath.class);
                out.writeUTF(svgPath.getContent());
                out.writeObject(svgPath.getFillRule());
            }
            else if (shape instanceof Text)
            {
                final Text text = (Text) shape;
                out.writeObject(Text.class);
                out.writeDouble(text.getX());
                out.writeDouble(text.getY());
                out.writeUTF(text.getText());
            }
            else
            {
                throw new IOException();
            }
        }
        else
        {
            out.writeBoolean(true);
        }
    }

    private void writeObject(ObjectOutputStream out)
            throws IOException
    {
        out.writeUTF(getModelName());
        out.writeInt(modelId);
        out.writeInt(shapes.size());

        for (Shape shape : shapes)
        {
            writeShape(out, shape);
        }

        out.writeDouble(this.shapeGroup.scaleXProperty().get());
        out.writeDouble(this.shapeGroup.scaleYProperty().get());
        out.writeDouble(this.shapeGroup.translateXProperty().get());
        out.writeDouble(this.shapeGroup.translateYProperty().get());

        out.writeDouble(transformMoveToPreferred.getX());
        out.writeDouble(transformMoveToPreferred.getY());
        out.writeDouble(preferredXScale.get());
        out.writeDouble(preferredYScale.get());
        out.writeDouble(preferredRotationTurn.get());
    }

    private Shape readShape(final ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        Shape result = null;
        final boolean isNull = in.readBoolean();
        if (!isNull)
        {
            final double mxx = in.readDouble();
            final double mxy = in.readDouble();
            final double myx = in.readDouble();
            final double myy = in.readDouble();
            final double tx = in.readDouble();
            final double ty = in.readDouble();
            Affine a  = new Affine();
            a.append(mxx, mxy, tx, myx, myy, ty);
            final Class c = (Class) in.readObject();
            if (c.equals(Arc.class))
            {
                final double centreX = in.readDouble();
                final double centreY = in.readDouble();
                final double length = in.readDouble();
                final double radiusX = in.readDouble();
                final double radiusY = in.readDouble();
                final double startAngle = in.readDouble();
                final int t = in.readInt();
                ArcType at;
                switch (t) 
                {
                    case 1:
                        at = ArcType.CHORD;
                        break;

                    case 2:
                        at = ArcType.ROUND;
                        break;
                    case 0:
                    default:
                        at = ArcType.OPEN;
                        break;
                }
                
                Arc arc = new Arc();
                arc.setCenterX(centreX);
                arc.setCenterY(centreY);
                arc.setLength(length);
                arc.setRadiusX(radiusX);
                arc.setRadiusY(radiusY);
                arc.setStartAngle(startAngle);
                arc.setType(at);
                        
                result = arc;
            }
            else if (c.equals(Circle.class))
            {
                final double centerX = in.readDouble();
                final double centerY = in.readDouble();
                final double radius = in.readDouble();
                Circle circle = new Circle();
                circle.setCenterX(centerX);
                circle.setCenterY(centerY);
                circle.setRadius(radius);
                
                result = circle;
            }
            else if (c.equals(CubicCurve.class))
            {
                final double startX = in.readDouble();
                final double startY = in.readDouble();
                final double controlX1 = in.readDouble();
                final double controlY1 = in.readDouble();
                final double controlX2 = in.readDouble();
                final double controlY2 = in.readDouble();
                final double endX = in.readDouble();
                final double endY = in.readDouble();
                CubicCurve cubic = new CubicCurve();
                cubic.setStartX(startX);
                cubic.setStartY(startY);
                cubic.setControlX1(controlX1);
                cubic.setControlY1(controlY1);
                cubic.setControlX2(controlX2);
                cubic.setControlY2(controlY2);
                cubic.setEndX(endX);
                cubic.setEndY(endY);
                result = cubic;
            }
            else if (c.equals(Ellipse.class))
            {
                final double centerX = in.readDouble();
                final double centerY = in.readDouble();
                final double radiusX = in.readDouble();
                final double radiusY = in.readDouble();
                Ellipse ellipse = new Ellipse();
                ellipse.setCenterX(centerX);
                ellipse.setCenterY(centerY);
                ellipse.setRadiusX(radiusX);
                ellipse.setRadiusY(radiusY);
                result = ellipse;
            }
            else if (c.equals(Line.class))
            {
                final double startX = in.readDouble();
                final double startY = in.readDouble();
                final double endX = in.readDouble();
                final double endY = in.readDouble();
                Line line = new Line();
                line.setStartX(startX);
                line.setStartY(startY);
                line.setEndX(endX);
                line.setEndY(endY);
                result = line;
            }
            else if (c.equals(Polygon.class))
            {
                double[] points = (double[]) in.readObject();
                result = new Polygon(points);
            }
            else if (c.equals(Polyline.class))
            {
                double[] points = (double[]) in.readObject();
                result = new Polyline(points);
            }
            else if (c.equals(QuadCurve.class))
            {
                final double startX = in.readDouble();
                final double startY = in.readDouble();
                final double controlX = in.readDouble();
                final double controlY = in.readDouble();
                final double endX = in.readDouble();
                final double endY = in.readDouble();
                QuadCurve quad = new QuadCurve();
                quad.setStartX(startX);
                quad.setStartY(startY);
                quad.setControlX(controlX);
                quad.setControlY(controlY);
                quad.setEndX(endX);
                quad.setEndY(endY);
                result = quad;
            }
            else if (c.equals(Rectangle.class))
            {
                final double x = in.readDouble();
                final double y = in.readDouble();
                final double width = in.readDouble();
                final double height = in.readDouble();
                final double arcWidth = in.readDouble();
                final double arcHeight = in.readDouble();
                Rectangle rect = new Rectangle();
                rect.setX(x);
                rect.setY(y);
                rect.setWidth(width);
                rect.setHeight(height);
                rect.setArcWidth(arcWidth);
                rect.setArcHeight(arcHeight);
                result = rect;
            }
            else if (c.equals(SVGPath.class))
            {
                final String content = in.readUTF();
                final FillRule fillRule = (FillRule)in.readObject();
                SVGPath path = new SVGPath();
                path.setContent(content);
                path.setFillRule(fillRule);
                result = path;
            }
            else if (c.equals(Text.class))
            {
                final double x = in.readDouble();
                final double y = in.readDouble();
                final String content = in.readUTF();
                Text text = new Text();
                text.setX(x);
                text.setY(y);
                text.setText(content);
                result = text;
            }
            else
            {
              throw new ClassNotFoundException();
            }
            result.getTransforms().add(a);
        }
        return result;
    }

    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        try
        {
            String modelName = in.readUTF().trim();
            modelId = in.readInt();

            initialise();
            shapes = new ArrayList<>();
            setModelName(modelName);

            int nShapes = in.readInt();
            if (nShapes > 0)
            {
                for (int i = 0; i < nShapes; ++i)
                {
                    shapes.add(readShape(in));
                }
            }

            double groupScaleX = in.readDouble();
            double groupScaleY = in.readDouble();
            double groupTranslateX = in.readDouble();
            double groupTranslateY = in.readDouble();
            
            double storedMoveX = in.readDouble();
            double storedMoveY = in.readDouble();
            double storedXScale = in.readDouble();
            double storedYScale = in.readDouble();
            double storedRotationTurn = in.readDouble();

            shapeGroup = new Group();
            shapeGroup.getChildren().addAll(shapes);
            this.getChildren().add(shapeGroup);
            initialise();
            initialiseTransforms(true);

            shapeGroup.scaleXProperty().set(groupScaleX);
            shapeGroup.scaleYProperty().set(groupScaleY);
            shapeGroup.translateXProperty().set(groupTranslateX);
            shapeGroup.translateYProperty().set(groupTranslateY);

            transformMoveToPreferred.setX(storedMoveX);
            transformMoveToPreferred.setY(storedMoveY);
            preferredXScale.set(storedXScale);
            transformScalePreferred.setX(storedXScale);
            preferredYScale.set(storedYScale);
            transformScalePreferred.setY(storedYScale);
            preferredRotationTurn.set(storedRotationTurn);
            updateTransformsFromTurnAngle();
            notifyShapeHasChanged();
        }
        catch (Exception ex)
        {
            steno.exception("Oops", ex);
        }
    }

    @Override
    public void setRotationTurn(double value) {
        preferredRotationTurn.set(value);
        updateTransformsFromTurnAngle();
        notifyShapeHasChanged();
    }
    
    

    @Override
    public double getRotationTurn() {
        return preferredRotationTurn.get();
    }
    
    private void updateTransformsFromTurnAngle()
    {
        // Turn - around Z axis
        transformRotateTurnPreferred.setPivotX(originalModelBounds.getCentreX());
        transformRotateTurnPreferred.setPivotY(originalModelBounds.getCentreY());
        transformRotateTurnPreferred.setPivotZ(0);
        transformRotateTurnPreferred.setAngle(preferredRotationTurn.get());
        transformRotateTurnPreferred.setAxis(Z_AXIS);
    }
    
   /**
     * This method is used during an ungroup to blend the group's transform into
     * this one, thereby keeping this model in the same place.
     */
    public void applyGroupTransformToThis(ShapeGroup sGroup, double centreX, double centreY)
    {
        double xScaleFactor = getXScale() * sGroup.getXScale();
        double yScaleFactor = getYScale() * sGroup.getYScale();

        //Calculate the centre of the group in world co-ords
        //Point2D groupCentre = new Point2D(sGroup.getTransformedCentreX(),
        //                                  sGroup.getTransformedCentreDepth());

        //Point2D shapeCentre = new Point2D(getTransformedCentreX() + sGroup.transformMoveToPreferred.getX(),
        //        getTransformedCentreDepth() + sGroup.transformMoveToPreferred.getY());

        //Point2D groupCentreToModelCentre = shapeCentre.subtract(groupCentre);

        //Point2D scaledGroupCentreToModelCentre = new Point2D(groupCentreToModelCentre.getX() * xScaleFactor,
        //                                                     groupCentreToModelCentre.getY() * yScaleFactor);

        //Point2D turnedModelCentrePoint = sGroup.rotationTransforms.get(0).transform(scaledGroupCentreToModelCentre);

        //Point2D newShapeCentre = new Point2D(groupCentre.getX() + turnedModelCentrePoint.getX(),
        //                                     groupCentre.getY() + turnedModelCentrePoint.getY());

        translateTo(centreX, centreY);
        preferredRotationTurn.set(preferredRotationTurn.get() + sGroup.preferredRotationTurn.get());
        updateTransformsFromTurnAngle();
        setXScale(xScaleFactor, false);
        setYScale(yScaleFactor, false);
        lastTransformedBoundsInParent = calculateBoundsInParentCoordinateSystem();
    }
}
