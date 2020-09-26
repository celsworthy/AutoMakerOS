package celtech.modelcontrol;

import celtech.coreUI.visualisation.ApplicationMaterials;
import celtech.coreUI.visualisation.CameraViewChangeListener;
import celtech.coreUI.visualisation.Edge;
import celtech.coreUI.visualisation.ScreenExtents;
import celtech.coreUI.visualisation.ScreenExtentsProviderThreeD;
import celtech.coreUI.visualisation.ShapeProviderThreeD;
import celtech.coreUI.visualisation.collision.CollisionShapeListener;
import celtech.coreUI.visualisation.metaparts.FloatArrayList;
import celtech.coreUI.visualisation.metaparts.IntegerArrayList;
import celtech.coreUI.visualisation.modelDisplay.SelectionHighlighter;
import celtech.roboxbase.utils.Math.MathUtils;
import celtech.roboxbase.utils.RectangularBounds;
import celtech.roboxbase.utils.threed.MeshToWorldTransformer;
import celtech.utils.threed.MeshCutter2;
import celtech.utils.threed.MeshSeparator;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableFloatArray;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.ObservableFaceArray;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction;
import org.apache.commons.math3.optim.univariate.UnivariatePointValuePair;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class ModelContainer extends ProjectifiableThing implements Serializable,
        Comparable,
        CameraViewChangeListener,
        MeshToWorldTransformer,
        ScaleableThreeD,
        TranslateableThreeD,
        ResizeableThreeD,
        RotatableThreeD,
        ScreenExtentsProviderThreeD,
        ShapeProviderThreeD
{
    private static final Stenographer steno = StenographerFactory.getStenographer(ShapeContainer.class.getName());
    private static final long serialVersionUID = 1L;
    protected static int nextModelId = 1;
    private boolean isInvalidMesh = false;

    protected Translate transformDropToBedYAdjust;
    private Rotate transformRotateTwistPreferred;
    private Rotate transformRotateLeanPreferred;

    private MeshView meshView;

    /**
     * Property wrapper around the scale.
     */
    private DoubleProperty preferredZScale;
    /**
     * Property wrappers around the rotations.
     */
    private DoubleProperty preferredRotationTwist;
    private DoubleProperty preferredRotationLean;

    /**
     * The bounds of the object in its parent. For top level objects this is
     * also the bounds in the bed coordinates. They are kept valid even after
     * translates etc.
     */
    private SelectionHighlighter selectionHighlighter;

    /**
     * Print the part using the extruder of the given number.
     */
    protected IntegerProperty associateWithExtruderNumber = new SimpleIntegerProperty(0);

    private double cameraDistance = 1;

    private Camera cameraViewingMe = null;

    private MeshView collisionShape = null;
    private List<CollisionShapeListener> collisionShapeListeners = new ArrayList<>();

    public ModelContainer()
    {
        super();
    }

    public ModelContainer(File modelFile, MeshView meshView)
    {
        super(modelFile);
        this.meshView = meshView;
        getChildren().add(meshView);

        initialise(modelFile);
        initialiseTransforms();
    }

    public ModelContainer(File modelFile, MeshView meshView, int extruderAssociation)
    {
        this(modelFile, meshView);
        associateWithExtruderNumber.set(extruderAssociation);
    }

    /**
     * Return the parent ModelGroup else return null.
     */
    public ModelContainer getParentModelContainer()
    {
        if (getParent() instanceof ModelContainer)
        {
            return (ModelContainer) getParent();
        } else if (getParent() != null && getParent().getParent() instanceof ModelContainer)
        {
            return (ModelContainer) getParent().getParent();
        } else
        {
            return null;
        }
    }

    /**
     * Clear the meshes so as to free memory.
     */
    @Override
    public void clearElements()
    {
        getChildren().clear();
    }

    public void setUseExtruder0(boolean useExtruder0)
    {
        associateWithExtruderNumber.set(useExtruder0 ? 0 : 1);
    }

    public void printTransforms()
    {
        System.out.println("Transforms for: " + getId());
        System.out.println("==============================================");
        System.out.println("transformscalepreferred is " + transformScalePreferred);
        System.out.println("transformMovetopreferred is " + transformMoveToPreferred);
        System.out.println("transformSnapToGroundYAdjust is " + transformDropToBedYAdjust);
        System.out.println("transformRotateLeanPreferred is " + transformRotateLeanPreferred);
        System.out.println("transformRotateTwistPreferred " + transformRotateTwistPreferred);
        System.out.println("transformRotateTurnPreferred " + transformRotateTurnPreferred);
        System.out.println("transformBedCentre " + transformBedCentre);
        System.out.println("==============================================");
    }

    public List<Transform> getRotationTransforms()
    {
        return rotationTransforms;
    }

    protected void initialiseTransforms()
    {
        transformScalePreferred = new Scale(1, 1, 1);
        transformDropToBedYAdjust = new Translate(0, 0, 0);
        transformMoveToPreferred = new Translate(0, 0, 0);
        transformBedCentre = new Translate(0, 0, 0);

        transformRotateLeanPreferred = new Rotate(0, 0, 0, 0, X_AXIS);
        transformRotateTwistPreferred = new Rotate(0, 0, 0, 0, Y_AXIS);
        transformRotateTurnPreferred = new Rotate(0, 0, 0, 0, Z_AXIS);
        rotationTransforms.add(transformRotateTurnPreferred);
        rotationTransforms.add(transformRotateLeanPreferred);
        rotationTransforms.add(transformRotateTwistPreferred);

        setBedCentreOffsetTransform();

        /**
         * Rotations (which are all around the centre of the model) must be
         * applied before any translations.
         */
        getTransforms().addAll(transformDropToBedYAdjust,
                transformMoveToPreferred,
                transformBedCentre,
                transformRotateTurnPreferred,
                transformRotateLeanPreferred,
                transformRotateTwistPreferred
        );

        if (meshView != null)
        {
            meshView.getTransforms().addAll(transformScalePreferred);
//            HullComputer hullComputer = new HullComputer(meshView);
//
//            hullComputer.setOnSucceeded(workerStateEvent ->
//            {
//                collisionShape = hullComputer.getValue();
//                for (CollisionShapeListener listener : collisionShapeListeners)
//                {
//                    listener.collisionShapeAvailable(this);
//                }
//            });

//            Lookup.getTaskExecutor().runTaskAsDaemon(hullComputer);
        }

        updateOriginalModelBounds();

        lastTransformedBoundsInParent = calculateBoundsInParentCoordinateSystem();

        notifyShapeChange();
        notifyScreenExtentsChange();
    }

    @Override
    protected void setRotationPivotsToCentreOfModel()
    {

        transformRotateTurnPreferred.setPivotX(originalModelBounds.getCentreX());
        transformRotateTurnPreferred.setPivotY(originalModelBounds.getCentreY());
        transformRotateTurnPreferred.setPivotZ(originalModelBounds.getCentreZ());
    }

    @Override
    public void moveToCentre()
    {
        translateTo(bedCentreOffsetX, bedCentreOffsetZ);

        lastTransformedBoundsInParent = calculateBoundsInParentCoordinateSystem();
    }

    protected void initialise(File modelFile)
    {
        setModelFile(modelFile);
        modelId = nextModelId;
        nextModelId += 1;

        if (modelFile != null)
        {
            setModelName(modelFile.getName());
            this.setId(modelFile.getName() + Integer.toString(modelId));
        } else
        {
            setModelName("group " + modelId);
            this.setId("group " + modelId);
        }

        preferredXScale = new SimpleDoubleProperty(1);
        preferredYScale = new SimpleDoubleProperty(1);
        preferredZScale = new SimpleDoubleProperty(1);
        preferredRotationLean = new SimpleDoubleProperty(0);
        preferredRotationTwist = new SimpleDoubleProperty(0);
        preferredRotationTurn = new SimpleDoubleProperty(0);
        rotationTransforms = new ArrayList<>();
        collisionShapeListeners = new ArrayList<>();
    }

    void clearBedTransform()
    {
        updateLastTransformedBoundsInParentForTranslateByX(-bedCentreOffsetX);
        updateLastTransformedBoundsInParentForTranslateByZ(-bedCentreOffsetZ);
        transformBedCentre.setX(0);
        transformBedCentre.setY(0);
        transformBedCentre.setZ(0);
    }

    private Translate currenttransformBedCentre;

    double currentDropY;

    public void saveAndClearDropToBedYTransform()
    {
        currentDropY = transformDropToBedYAdjust.getY();
        transformDropToBedYAdjust.setY(0);
    }

    public void restoreDropToBedYTransform()
    {
        transformDropToBedYAdjust.setY(currentDropY);
    }

    public void saveAndClearBedTransform()
    {
        currenttransformBedCentre = transformBedCentre.clone();
        transformBedCentre.setX(0);
        transformBedCentre.setY(0);
        transformBedCentre.setZ(0);
    }

    public void restoreBedTransform()
    {
        transformBedCentre.setX(currenttransformBedCentre.getX());
        transformBedCentre.setY(currenttransformBedCentre.getY());
        transformBedCentre.setZ(currenttransformBedCentre.getZ());
    }

    /**
     * Set transformBedCentre according to the position of the centre of the
     * bed.
     */
    @Override
    public void setBedCentreOffsetTransform()
    {
        double xDelta = printVolumeWidth / 2 - bedCentreOffsetX;
        double zDelta = printVolumeDepth / 2 - bedCentreOffsetZ;
        bedCentreOffsetX = printVolumeWidth / 2;
        bedCentreOffsetY = 0;
        bedCentreOffsetZ = printVolumeDepth / 2;
        transformBedCentre.setX(bedCentreOffsetX);
        transformBedCentre.setY(bedCentreOffsetY);
        transformBedCentre.setZ(bedCentreOffsetZ);
        updateLastTransformedBoundsInParentForTranslateByX(xDelta);
        updateLastTransformedBoundsInParentForTranslateByZ(zDelta);
    }

    /**
     * Make a copy of this ModelContainer and return it.
     *
     * @return
     */
    @Override
    public ProjectifiableThing makeCopy()
    {
        MeshView newMeshView = new MeshView();
        newMeshView.setMesh(meshView.getMesh());
        newMeshView.setMaterial(ApplicationMaterials.getDefaultModelMaterial());
        newMeshView.setCullFace(CullFace.BACK);
        newMeshView.setId(meshView.getId());

        ModelContainer copy = new ModelContainer(getModelFile(), newMeshView);
        copy.setUseExtruder0(associateWithExtruderNumber.get() == 0);
        copy.setState(this.getState());
        copy.recalculateScreenExtents();
        return copy;
    }

    @Override
    public void translateBy(double xMove, double zMove)
    {
        translateBy(xMove, 0, zMove);
    }

    @Override
    public void translateBy(double xMove, double yMove, double zMove)
    {
        Point3D localPoint = new Point3D(xMove, yMove, zMove);

        List<ModelContainer> useMyTransforms = new ArrayList<>();

        Node currentNode = this;
        while (currentNode != bed
                && currentNode.getParent() != null)
        {
            Parent parent = currentNode.getParent();
            if (parent instanceof ModelContainer)
            {
                useMyTransforms.add((ModelContainer) parent);
            } else if (parent instanceof ModelGroup)
            {
                useMyTransforms.add((ModelGroup) parent);
            }

            currentNode = parent;
        }

        for (int containerIndex = useMyTransforms.size() - 1; containerIndex >= 0; containerIndex--)
        {
            List<Transform> parentTransforms = useMyTransforms.get(containerIndex).getRotationTransforms();
            try
            {
                Transform concatenatedTransforms = parentTransforms.get(2).createInverse()
                        .createConcatenation(parentTransforms.get(1).createInverse()
                                .createConcatenation(parentTransforms.get(0).createInverse()));
                localPoint = concatenatedTransforms.deltaTransform(localPoint);
            } catch (NonInvertibleTransformException ex)
            {
                steno.error("Couldn't invert rotation transform");
            }
        }

        transformMoveToPreferred.setX(transformMoveToPreferred.getX() + localPoint.getX());
        transformMoveToPreferred.setY(transformMoveToPreferred.getY() + localPoint.getY());
        transformMoveToPreferred.setZ(transformMoveToPreferred.getZ() + localPoint.getZ());

        updateLastTransformedBoundsInParentForTranslateByX(localPoint.getX());
        updateLastTransformedBoundsInParentForTranslateByY(localPoint.getY());
        updateLastTransformedBoundsInParentForTranslateByZ(localPoint.getZ());

        checkOffBed();

//        keepOnBedXZ();
    }

    public double getMoveToPreferredX()
    {
        return transformMoveToPreferred.getX();
    }

    public double getMoveToPreferredZ()
    {
        return transformMoveToPreferred.getZ();
    }

    public RectangularBounds getLocalBounds()
    {
        return originalModelBounds;
    }

    /**
     * N.BÃƒÂ¯Ã‚Â¼Ã…Â½It only works for top level objects ieÃƒÂ¯Ã‚Â¼Ã…Â½top
     * level groups or ungrouped models.
     */
    public void translateFrontLeftTo(double xPosition, double zPosition)
    {
        double newXPosition = xPosition - bedCentreOffsetX
                + lastTransformedBoundsInParent.getWidth() / 2.0;
        double newZPosition = zPosition - bedCentreOffsetZ
                + lastTransformedBoundsInParent.getHeight() / 2.0;
        double deltaXPosition = newXPosition - transformMoveToPreferred.getX();
        double deltaZPosition = newZPosition - transformMoveToPreferred.getZ();
        translateBy(deltaXPosition, deltaZPosition);
    }

    /**
     * Move the CENTRE of the object to the desired x,z position.
     *
     * @param xPosition
     * @param zPosition
     */
    @Override
    public void translateTo(double xPosition, double zPosition)
    {
        translateXTo(xPosition);
        translateDepthPositionTo(zPosition);
    }

    public void centreObjectOnBed()
    {
        transformMoveToPreferred.setX(0);
        transformMoveToPreferred.setZ(0);
    }

    @Override
    public void shrinkToFitBed()
    {
        BoundingBox printableBoundingBox = (BoundingBox) getBoundsInLocal();

        double scaling = 1.0;

        double relativeXSize = printableBoundingBox.getWidth() / printVolumeWidth;
        double relativeYSize = printableBoundingBox.getHeight() / printVolumeHeight;
        double relativeZSize = printableBoundingBox.getDepth() / printVolumeDepth;
        steno.info("Relative sizes of model: X " + relativeXSize + " Y " + relativeYSize + " Z "
                + relativeZSize);

        if (relativeXSize > relativeYSize && relativeXSize > relativeZSize)
        {
            if (relativeXSize > 1)
            {
                scaling = 1 / relativeXSize;
            }
        } else if (relativeYSize > relativeXSize && relativeYSize > relativeZSize)
        {
            if (relativeYSize > 1)
            {
                scaling = 1 / relativeYSize;
            }

        } else
        {
            //Z size must be the largest
            if (relativeZSize > 1)
            {
                scaling = 1 / relativeZSize;
            }
        }

        if (scaling != 1.0f)
        {
            setXScale(scaling, true);
            setYScale(scaling, true);
            setZScale(scaling, true);
        }

        lastTransformedBoundsInParent = calculateBoundsInParentCoordinateSystem();
    }

    /**
     * Return a set of all descendent ModelContainers (and include this one)
     * that have MeshView children.
     *
     * @return
     */
    public Set<ModelContainer> getModelsHoldingMeshViews()
    {
        Set<ModelContainer> modelsHoldingMeshViews = new HashSet<>();
        modelsHoldingMeshViews.add(this);
        return modelsHoldingMeshViews;
    }

    /**
     * Return a set of all descendent ModelContainers (and include this one)
     * that have ModelContainer children.
     * @return
     */
    public Collection<? extends ModelContainer> getModelsHoldingModels()
    {
        Set<ModelContainer> modelsHoldingModels = new HashSet<>();
        return modelsHoldingModels;
    }

    public void addGroupStructure(Map<Integer, Set<Integer>> groupStructure)
    {
    }

    /**
     * Rotate the model in Lean and Twist so that the chosen face is pointing
     * down (ie aligned with the Y axis). Lean is easy to get, and we then use
     * an optimiser to establish Twist.
     *
     * @param snapFaceIndex
     */
    public void snapToGround(MeshView meshView, int snapFaceIndex)
    {
        Vector3D faceNormal = getFaceNormal(meshView, snapFaceIndex);
        Vector3D downVector = new Vector3D(0, 1, 0);

        Rotation requiredRotation = new Rotation(faceNormal, downVector);

        /**
         * get angle that Y is moved through, to give RL (lean rotation).
         */
        Vector3D yPrime = requiredRotation.applyTo(new Vector3D(0, -1, 0));
        Vector3D Y = new Vector3D(0, -1, 0);
        double leanAngle = Vector3D.angle(yPrime, Y);
        setRotationLean(Math.toDegrees(leanAngle));

        if (Math.abs(leanAngle - 180) < 0.02)
        {
            // no twist required, we can stop here
            return;
        }

        // Calculate twist using an optimizer (typically needs less than 30 iterations in
        // this example)
        long start = System.nanoTime();
        BrentOptimizer optimizer = new BrentOptimizer(1e-3, 1e-4);
        UnivariatePointValuePair pair = optimizer.optimize(new MaxEval(70),
                new UnivariateObjectiveFunction(
                        new ApplyTwist(meshView, snapFaceIndex)),
                GoalType.MINIMIZE,
                new SearchInterval(0, 360));
        steno.debug("optimiser took " + (int) ((System.nanoTime() - start) * 10e-6) + " ms"
                + " and "
                + optimizer.getEvaluations() + " evaluations");
        setRotationTwist(pair.getPoint());

        dropToBed();
    }

    private static Point3D toPoint3D(Vector3D vector)
    {
        return new Point3D(vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     * Return the maximum and minimum y values of the coordinates of this model
     * in bed coords.
     */
    public List<Float> getMaxAndMinYInBedCoords()
    {
        List<Float> maxMin = new ArrayList<>();
        RectangularBounds modelBounds = calculateBoundsInBedCoordinateSystem();
        maxMin.add((float) modelBounds.getMaxY());
        maxMin.add((float) modelBounds.getMinY());
        return maxMin;
    }

    private class ApplyTwist implements UnivariateFunction
    {

        final Vector3D faceNormal;
        final Vector3D faceCentre;
        Transform localToBedTransform;
        Node bed;

        public ApplyTwist(MeshView meshView, int faceIndex)
        {
            faceNormal = getFaceNormal(meshView, faceIndex);
            faceCentre = getFaceCentre(meshView, faceIndex);
            bed = getRootModelContainer(meshView).getParent();
        }

        Point3D getRotatedFaceNormal()
        {

            Point3D rotatedFaceCentre = bed.sceneToLocal(localToScene(toPoint3D(faceCentre)));

            Point3D rotatedFaceCentrePlusNormal = bed.sceneToLocal(
                    localToScene(toPoint3D(faceCentre.add(faceNormal))));

            Point3D rotatedFaceNormal = rotatedFaceCentrePlusNormal.subtract(rotatedFaceCentre);
            return rotatedFaceNormal;
        }

        @Override
        public double value(double twistDegrees)
        {
            // This value function returns how far off the resultant rotated face normal is
            // from the Y axis. The optimiser tries to minimise this function (i.e. align
            // rotated face normal with Y).
            setRotationTwist(twistDegrees);
            Point3D rotatedFaceNormal = getRotatedFaceNormal();
            double deviation = rotatedFaceNormal.angle(Y_AXIS);
            return deviation;
        }
    }

    @Override
    protected void updateScaleTransform(boolean dropToBed)
    {
        if (dropToBed)
        {
            dropToBed();
        }
        checkOffBed();
        notifyShapeChange();
        notifyScreenExtentsChange();
    }

    @Override
    public void setZScale(double scaleFactor, boolean dropToBed)
    {
        preferredZScale.set(scaleFactor);
        transformScalePreferred.setZ(scaleFactor);
        updateScaleTransform(dropToBed);
    }

    /**
     * We present the rotations to the user as Lean - Twist - Turn.
     */
    private void updateTransformsFromLeanTwistTurnAngles()
    {
        // Twist - around object Y axis
        transformRotateTwistPreferred.setPivotX(originalModelBounds.getCentreX());
        transformRotateTwistPreferred.setPivotY(originalModelBounds.getCentreY());
        transformRotateTwistPreferred.setPivotZ(originalModelBounds.getCentreZ());
        transformRotateTwistPreferred.setAngle(preferredRotationTwist.get());
        transformRotateTwistPreferred.setAxis(Y_AXIS);

        // Lean - around Z axis
        transformRotateLeanPreferred.setPivotX(originalModelBounds.getCentreX());
        transformRotateLeanPreferred.setPivotY(originalModelBounds.getCentreY());
        transformRotateLeanPreferred.setPivotZ(originalModelBounds.getCentreZ());
        transformRotateLeanPreferred.setAngle(preferredRotationLean.get());
        transformRotateLeanPreferred.setAxis(Z_AXIS);

        // Turn - around bed Y axis
        transformRotateTurnPreferred.setPivotX(originalModelBounds.getCentreX());
        transformRotateTurnPreferred.setPivotY(originalModelBounds.getCentreY());
        transformRotateTurnPreferred.setPivotZ(originalModelBounds.getCentreZ());
        transformRotateTurnPreferred.setAngle(preferredRotationTurn.get());
        transformRotateTurnPreferred.setAxis(Y_AXIS);
    }

    @Override
    public double getZScale()
    {
        return preferredZScale.get();
    }

    @Override
    public void setRotationTwist(double value)
    {
        setRotationTwist(value, true);
    }

    public void setRotationTwist(double value, boolean dropToBed)
    {
        preferredRotationTwist.set(value);
        updateTransformsFromLeanTwistTurnAngles();

        if (dropToBed)
        {
            dropToBed();
        }
        checkOffBed();
        notifyShapeChange();
        notifyScreenExtentsChange();
    }

    @Override
    public double getRotationTwist()
    {
        return preferredRotationTwist.get();
    }

    @Override
    public void setRotationTurn(double value)
    {
        setRotationTurn(value, true);
    }

    public void setRotationTurn(double value, boolean dropToBed)
    {
        preferredRotationTurn.set(value);
        updateTransformsFromLeanTwistTurnAngles();

        if (dropToBed)
        {
            dropToBed();
        }
        checkOffBed();
        notifyShapeChange();
        notifyScreenExtentsChange();
    }

    @Override
    public double getRotationTurn()
    {
        return preferredRotationTurn.get();
    }

    @Override
    public void setRotationLean(double value)
    {
        setRotationLean(value, true);
    }

    public void setRotationLean(double value, boolean dropToBed)
    {
        preferredRotationLean.set(value);
        updateTransformsFromLeanTwistTurnAngles();

        if (dropToBed)
        {
            dropToBed();
        }
        checkOffBed();
        notifyShapeChange();
        notifyScreenExtentsChange();
    }

    @Override
    public double getRotationLean()
    {
        return preferredRotationLean.get();
    }

    @Override
    public void selectedAction()
    {
        if (isSelected())
        {
            if (selectionHighlighter == null)
            {
                addSelectionHighlighter();
            }
            showSelectionHighlighter();
        } else
        {
            hideSelectionHighlighter();
        }
    }

    public BooleanProperty isSelectedProperty()
    {
        return isSelected;
    }

    private void writeObject(ObjectOutputStream out)
            throws IOException
    {
        out.writeUTF(getModelName());

        // unused
        out.writeInt(0);

        TriangleMesh triMesh = (TriangleMesh) meshView.getMesh();

        int[] smoothingGroups = triMesh.getFaceSmoothingGroups().toArray(null);
        out.writeObject(smoothingGroups);

        int[] faces = triMesh.getFaces().toArray(null);
        out.writeObject(faces);

        float[] points = triMesh.getPoints().toArray(null);
        out.writeObject(points);

        out.writeDouble(transformMoveToPreferred.getX());
        out.writeDouble(transformMoveToPreferred.getZ());
        out.writeDouble(getXScale());
        out.writeDouble(getRotationTwist());
        // was not used (was snapFaceIndex) - now modelId
        out.writeInt(modelId);
        out.writeInt(associateWithExtruderNumber.get());
        out.writeDouble(getYScale());
        out.writeDouble(getZScale());
        out.writeDouble(getRotationLean());
        out.writeDouble(getRotationTurn());

        out.writeDouble(transformDropToBedYAdjust.getY());
    }

    private MeshView readContainer_1_03_00_Contents(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        ModelContentsEnumeration modelContentsType = (ModelContentsEnumeration) in.readObject();

        int numberOfMeshes = in.readInt();

        int[] smoothingGroups = (int[]) in.readObject();
        int[] faces = (int[]) in.readObject();
        float[] points = (float[]) in.readObject();

        TriangleMesh triMesh = new TriangleMesh();

        FloatArrayList texCoords = new FloatArrayList();
        texCoords.add(0f);
        texCoords.add(0f);

        triMesh.getPoints().addAll(points);
        triMesh.getTexCoords().addAll(texCoords.toFloatArray());
        triMesh.getFaces().addAll(faces);
        triMesh.getFaceSmoothingGroups().addAll(smoothingGroups);

        MeshView newMesh = new MeshView(triMesh);
        newMesh.setMaterial(ApplicationMaterials.getDefaultModelMaterial());
        newMesh.setCullFace(CullFace.BACK);
        newMesh.setId(getModelName() + "_mesh");

        return newMesh;
    }

    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException
    {
        boolean was_legacy_model = false;
        associateWithExtruderNumber = new SimpleIntegerProperty(0);

        String modelName = in.readUTF();

        try
        {
            int numberOfMeshesNowUnused = in.readInt();

            int[] smoothingGroups = (int[]) in.readObject();
            int[] faces = (int[]) in.readObject();
            float[] points = (float[]) in.readObject();

            TriangleMesh triMesh = new TriangleMesh();

            FloatArrayList texCoords = new FloatArrayList();
            texCoords.add(0f);
            texCoords.add(0f);

            triMesh.getPoints().addAll(points);
            triMesh.getTexCoords().addAll(texCoords.toFloatArray());
            triMesh.getFaces().addAll(faces);
            triMesh.getFaceSmoothingGroups().addAll(smoothingGroups);

            meshView = new MeshView(triMesh);
            meshView.setMaterial(ApplicationMaterials.getDefaultModelMaterial());
            meshView.setCullFace(CullFace.BACK);
            meshView.setId(modelName + "_mesh");
        } catch (IOException ex)
        {
            meshView = readContainer_1_03_00_Contents(in);
            was_legacy_model = true;
        }

        getChildren().add(meshView);

        initialise(new File(modelName));

        double storedX = in.readDouble();
        double storedY = 0;
        double storedZ = in.readDouble();
        double storedScaleX = in.readDouble();
        double storedRotationTwist = in.readDouble();
        int storedSnapFaceIndexLegacy = in.readInt();
        int storedModelId = storedSnapFaceIndexLegacy;

        double storedScaleY = storedScaleX;
        double storedScaleZ = storedScaleX;
        double storedRotationLean = 0d;
        double storedRotationTurn = 0d;
        boolean convertSnapFace = false;
        if (in.available() > 0)
        {
            // Introduced in version 1.??
            associateWithExtruderNumber.set(in.readInt());
            storedScaleY = in.readDouble();
            storedScaleZ = in.readDouble();
            storedRotationLean = in.readDouble();
            storedRotationTurn = in.readDouble();
            if (storedModelId > 0)
            {
                modelId = storedModelId;
            }
        } else
        {
            convertSnapFace = true;
        }

        if (in.available() > 0)
        {
            storedY = in.readDouble();
        }

        initialiseTransforms();

        transformMoveToPreferred.setX(storedX);
        transformMoveToPreferred.setZ(storedZ);
        transformDropToBedYAdjust.setY(storedY);

        preferredXScale.set(storedScaleX);
        preferredYScale.set(storedScaleY);
        preferredZScale.set(storedScaleZ);
        preferredRotationLean.set(storedRotationLean);
        preferredRotationTwist.set(storedRotationTwist);
        preferredRotationTurn.set(storedRotationTurn);

        transformScalePreferred.setX(storedScaleX);
        transformScalePreferred.setY(storedScaleY);
        transformScalePreferred.setZ(storedScaleZ);

        updateTransformsFromLeanTwistTurnAngles();

        if (convertSnapFace)
        {
            snapToGround(meshView, storedSnapFaceIndexLegacy);
        }

        if (was_legacy_model)
        {
            dropToBed();
        }

        lastTransformedBoundsInParent = calculateBoundsInParentCoordinateSystem();

        notifyShapeChange();
        notifyScreenExtentsChange();
    }

    private void readObjectNoData()
            throws ObjectStreamException
    {

    }

    public MeshView getMeshView()
    {
        return meshView;
    }

    public IntegerProperty getAssociateWithExtruderNumberProperty()
    {
        return associateWithExtruderNumber;
    }

    @Override
    public void resizeWidth(double width)
    {
        RectangularBounds bounds = getLocalBounds();

        double originalWidth = bounds.getWidth();

        double newScale = width / originalWidth;
        setXScale(newScale, true);
        notifyShapeChange();
        notifyScreenExtentsChange();
    }

    @Override
    public void resizeHeight(double height)
    {
        RectangularBounds bounds = getLocalBounds();

        double currentHeight = bounds.getHeight();

        double newScale = height / currentHeight;

        setYScale(newScale, true);
        notifyShapeChange();
        notifyScreenExtentsChange();
    }

    @Override
    public void resizeDepth(double depth)
    {

        RectangularBounds bounds = getLocalBounds();

        double currentDepth = bounds.getDepth();

        double newScale = depth / currentDepth;

        setZScale(newScale, true);
        notifyShapeChange();
        notifyScreenExtentsChange();
    }

    /**
     * N.B. It only works for top level objects i.e. top level groups or
     * ungrouped models.
     *
     * @param xPosition
     */
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

        double currentXPosition = getTransformedCentreX();
        double requiredTranslation = finalXPosition - currentXPosition;
        transformMoveToPreferred.setX(transformMoveToPreferred.getX() + requiredTranslation);

        updateLastTransformedBoundsInParentForTranslateByX(requiredTranslation);
        checkOffBed();
        notifyShapeChange();
        notifyScreenExtentsChange();
    }

    /**
     * N.B. It only works for top level objects i.e. top level groups or
     * ungrouped models.
     *
     * @param zPosition
     */
    @Override
    public void translateDepthPositionTo(double zPosition)
    {
        RectangularBounds bounds = lastTransformedBoundsInParent;

        double newMaxZ = zPosition + bounds.getDepth() / 2;
        double newMinZ = zPosition - bounds.getDepth() / 2;

        double finalZPosition = zPosition;

        if (newMinZ < 0)
        {
            finalZPosition += -newMinZ;
        } else if (newMaxZ > printVolumeDepth)
        {
            finalZPosition -= (newMaxZ - printVolumeDepth);
        }

        double currentZPosition = getTransformedCentreDepth();
        double requiredTranslation = finalZPosition - currentZPosition;
        transformMoveToPreferred.setZ(transformMoveToPreferred.getZ() + requiredTranslation);

        updateLastTransformedBoundsInParentForTranslateByZ(requiredTranslation);
        checkOffBed();
        notifyShapeChange();
        notifyScreenExtentsChange();
    }

    void translateYPositionTo(double yPosition)
    {
        double currentYPosition = getTransformedCentreY();
        double requiredTranslation = yPosition - currentYPosition;
        transformDropToBedYAdjust.setY(yPosition);
        updateLastTransformedBoundsInParentForTranslateByY(yPosition);
        checkOffBed();
        notifyShapeChange();
        notifyScreenExtentsChange();
    }

    public double getYAdjust()
    {
        return transformDropToBedYAdjust.getY();
    }

    /**
     * This method is used during an ungroup to blend the group's transform into
     * this one, thereby keeping this model in the same place.
     */
    public void applyGroupTransformToThis(ModelGroup modelGroup)
    {
        double xScaleFactor = getXScale() * modelGroup.getXScale();
        double yScaleFactor = getYScale() * modelGroup.getYScale();
        double zScaleFactor = getZScale() * modelGroup.getZScale();

        //Calculate the centre of the group in world co-ords
        Point3D groupCentre = new Point3D(modelGroup.getTransformedCentreX(),
                modelGroup.getTransformedCentreY(),
                modelGroup.getTransformedCentreDepth());

        Point3D modelCentre = new Point3D(getTransformedCentreX() + modelGroup.transformMoveToPreferred.getX(),
                getTransformedCentreY() + modelGroup.transformDropToBedYAdjust.getY(),
                getTransformedCentreDepth() + modelGroup.transformMoveToPreferred.getZ());

        Point3D groupCentreToModelCentre = modelCentre.subtract(groupCentre);

        Point3D scaledGroupCentreToModelCentre = new Point3D(groupCentreToModelCentre.getX() * xScaleFactor,
                groupCentreToModelCentre.getY() * yScaleFactor,
                groupCentreToModelCentre.getZ() * zScaleFactor);

        Point3D twistedModelCentrePoint = modelGroup.getRotationTransforms().get(2).transform(scaledGroupCentreToModelCentre);
        Point3D leanedModelCentrePoint = modelGroup.getRotationTransforms().get(1).transform(twistedModelCentrePoint);
        Point3D turnedModelCentrePoint = modelGroup.getRotationTransforms().get(0).transform(leanedModelCentrePoint);

        Point3D newModelCentre = new Point3D(
                groupCentre.getX() + turnedModelCentrePoint.getX(),
                groupCentre.getY() + turnedModelCentrePoint.getY(),
                groupCentre.getZ() + turnedModelCentrePoint.getZ());

        translateTo(newModelCentre.getX(), newModelCentre.getZ());
        setRotationTurn(getRotationTurn() + modelGroup.getRotationTurn(), false);
        setRotationLean(getRotationLean() + modelGroup.getRotationLean(), false);
        setRotationTwist(getRotationTwist() + modelGroup.getRotationTwist(), false);
        setXScale(xScaleFactor, false);
        setYScale(yScaleFactor, false);
        setZScale(zScaleFactor, false);
        transformDropToBedYAdjust.setY(0);
        RectangularBounds modelBoundsParent = calculateBoundsInBedCoordinateSystem();
        transformDropToBedYAdjust.setY(-modelBoundsParent.getMaxY() + (lastTransformedBoundsInParent.getMaxY() * yScaleFactor));
        lastTransformedBoundsInParent = calculateBoundsInParentCoordinateSystem();
    }

    /**
     * Check if this object is off the bed. N.B. It only works for top level
     * objects i.e. top level groups or ungrouped models.
     */
    @Override
    public void checkOffBed()
    {
        RectangularBounds bounds = lastTransformedBoundsInParent;

        if (bounds != null)
        {
            double epsilon = 0.001;

            // For 3D models. X is width, Z is depth, Y is -Height.
            // We have to negate the print volume height for this test as up = -ve Y in jfx!
            if (MathUtils.compareDouble(bounds.getMinX(), 0, epsilon) == MathUtils.LESS_THAN
                    || MathUtils.compareDouble(bounds.getMaxX(), printVolumeWidth,
                            epsilon) == MathUtils.MORE_THAN
                    || MathUtils.compareDouble(bounds.getMinZ(), 0, epsilon) == MathUtils.LESS_THAN
                    || MathUtils.compareDouble(bounds.getMaxZ(), printVolumeDepth,
                            epsilon) == MathUtils.MORE_THAN
                    || MathUtils.compareDouble(bounds.getMaxY(), 0, epsilon) == MathUtils.MORE_THAN
                    || MathUtils.compareDouble(bounds.getMinY(), -printVolumeHeight,
                            epsilon) == MathUtils.LESS_THAN)
            {
                isOffBed.set(true);
            } else
            {
                isOffBed.set(false);
            }
        }
    }

    /**
     * Provides a way of checking to see if a model is in the print volume when
     * considering additional offsets for rafts etc
     *
     * Offsets are in bed co-ordinates and affect the max height of the object
     *
     * @param heightOffset
     * @return
     */
    public boolean isModelTooHighWithOffset(double heightOffset)
    {
        RectangularBounds bounds = lastTransformedBoundsInParent;

        boolean isOutOfPrintVolume = false;

        double epsilon = 0.001;

        if (MathUtils.compareDouble(-bounds.getMinY(),
                printVolumeHeight - heightOffset,
                epsilon) == MathUtils.MORE_THAN)
        {
            isOutOfPrintVolume = true;
        }

        return isOutOfPrintVolume;
    }

    /**
     * Calculate max/min X,Y,Z before the transforms have been applied (ie the
     * original model dimensions before any transforms).
     */
    @Override
    protected RectangularBounds calculateBoundsInLocal()
    {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;

        TriangleMesh mesh = (TriangleMesh) meshView.getMesh();
        ObservableFloatArray originalPoints = mesh.getPoints();

        for (int pointOffset = 0; pointOffset < originalPoints.size(); pointOffset += 3)
        {
            float xPos = originalPoints.get(pointOffset);
            float yPos = originalPoints.get(pointOffset + 1);
            float zPos = originalPoints.get(pointOffset + 2);

            minX = Math.min(xPos, minX);
            minY = Math.min(yPos, minY);
            minZ = Math.min(zPos, minZ);

            maxX = Math.max(xPos, maxX);
            maxY = Math.max(yPos, maxY);
            maxZ = Math.max(zPos, maxZ);
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

    public Set<MeshView> descendentMeshViews()
    {
        Set<MeshView> descendentMeshViews = new HashSet<>();
        descendentMeshViews.add(meshView);

        return descendentMeshViews;
    }

    public static ModelContainer getRootModelContainer(MeshView meshView)
    {
        return (ModelContainer) ((ModelContainer) meshView.getParent()).getRootModelContainer();
    }

    public ModelContainer getRootModelContainer()
    {
        ModelContainer parentModelContainer = this;

        while (parentModelContainer.getParentModelContainer() instanceof ModelContainer)
        {
            parentModelContainer = parentModelContainer.getParentModelContainer();
        }
        return (ModelContainer) parentModelContainer;
    }

    /**
     * Return a BedToLocal converter for this ModelContainer. N.B. Before using
     * this the bed centre transform and dropToBed transform must be cleared,
     * otherwise it does not work for the purposes intended.
     */
    public MeshCutter2.BedToLocalConverter getBedToLocalConverter()
    {
        return new MeshCutter2.BedToLocalConverter()
        {

            @Override
            public Point3D localToBed(Point3D point)
            {
                Point3D pointScene = localToScene(point);
                Point3D pointBed = bed.sceneToLocal(pointScene);
                return pointBed;
            }

            @Override
            public Point3D bedToLocal(Point3D point)
            {
                Point3D pointScene = bed.localToScene(point);
                return sceneToLocal(pointScene);
            }
        };

    }

    /**
     * Calculate max/min X,Y,Z after all the transforms have been applied all
     * the way to the bed coordinate system.
     */
    @Override
    public RectangularBounds calculateBoundsInBedCoordinateSystem()
    {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;

        for (Node meshViewNode : descendentMeshViews())
        {
            MeshView meshView = (MeshView) meshViewNode;

            ModelContainer rootModelContainer = getRootModelContainer(meshView);

            TriangleMesh mesh = (TriangleMesh) meshView.getMesh();
            ObservableFloatArray originalPoints = mesh.getPoints();

            for (int pointOffset = 0; pointOffset < originalPoints.size(); pointOffset += 3)
            {
                float xPos = originalPoints.get(pointOffset);
                float yPos = originalPoints.get(pointOffset + 1);
                float zPos = originalPoints.get(pointOffset + 2);

                Point3D pointInScene = meshView.localToScene(xPos, yPos, zPos);
                
                Point3D pointInBed = rootModelContainer.localToParent(
                        rootModelContainer.sceneToLocal(pointInScene));
//                System.out.println("point is " + xPos + " " + yPos + " " + zPos + " in bed is "
//                    + pointInBed.toString());

                minX = Math.min(pointInBed.getX(), minX);
                minY = Math.min(pointInBed.getY(), minY);
                minZ = Math.min(pointInBed.getZ(), minZ);

                maxX = Math.max(pointInBed.getX(), maxX);
                maxY = Math.max(pointInBed.getY(), maxY);
                maxZ = Math.max(pointInBed.getZ(), maxZ);
            }
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

    /**
     * Calculate max/min X,Y,Z after the transforms have been applied (ie in the
     * parent node).
     */
    @Override
    public RectangularBounds calculateBoundsInParentCoordinateSystem()
    {

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;

        ModelContainer parentModelContainer = getParentModelContainer();
        if (parentModelContainer == null)
        {
            // If not in a group, the parent is "the bed".
            return calculateBoundsInBedCoordinateSystem();
        }

        for (Node meshViewNode : descendentMeshViews())
        {
            MeshView meshView = (MeshView) meshViewNode;

            TriangleMesh mesh = (TriangleMesh) meshView.getMesh();
            ObservableFloatArray originalPoints = mesh.getPoints();

            for (int pointOffset = 0; pointOffset < originalPoints.size(); pointOffset += 3)
            {
                float xPos = originalPoints.get(pointOffset);
                float yPos = originalPoints.get(pointOffset + 1);
                float zPos = originalPoints.get(pointOffset + 2);

                Point3D pointInScene = meshView.localToScene(xPos, yPos, zPos);
                Point3D pointInParent = parentModelContainer.sceneToLocal(pointInScene);

                minX = Math.min(pointInParent.getX(), minX);
                minY = Math.min(pointInParent.getY(), minY);
                minZ = Math.min(pointInParent.getZ(), minZ);

                maxX = Math.max(pointInParent.getX(), maxX);
                maxY = Math.max(pointInParent.getY(), maxY);
                maxZ = Math.max(pointInParent.getZ(), maxZ);
            }
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

    /**
     * Try to split into parts. If the ModelContainer is composed of more than
     * one part then make a group of them.
     */
    public ModelContainer splitIntoParts()
    {
        Set<ModelContainer> parts = new HashSet<>();
        ItemState state = getState();
        String modelName = getModelName();

        ModelContainer modelContainer = this;

        List<TriangleMesh> subMeshes = MeshSeparator.separate((TriangleMesh) getMeshView().getMesh());
        if (subMeshes.size() > 1)
        {
            int ix = 1;
            for (TriangleMesh subMesh : subMeshes)
            {
                MeshView meshView = new MeshView(subMesh);
                ModelContainer newModelContainer = new ModelContainer(
                        getModelFile(), meshView);
                newModelContainer.setState(state);
                newModelContainer.associateWithExtruderNumber.set(associateWithExtruderNumber.get());
                parts.add(newModelContainer);

                newModelContainer.setModelName(modelName + " " + ix);

                ix++;
            }
            modelContainer = new ModelGroup(parts);
        }
        return modelContainer;
    }

    /**
     * THIS METHOD IS NOT CURRENTLY IN USE PROBABLY SHOULD BE BINNED IN FAVOUR
     * OF AN APPROACH SIMILAR TO THE SPLIT FUNCTION
     *
     * @return
     */
    public ArrayList<ModelContainer> cutToSize()
    {
        TriangleMesh mesh = (TriangleMesh) meshView.getMesh();
        ObservableFaceArray originalFaces = mesh.getFaces();
        ObservableFloatArray originalPoints = mesh.getPoints();

        double minPrintableY = printVolumeHeight;
        int numberOfBins = (int) Math.
                ceil(Math.abs(originalModelBounds.getHeight() / minPrintableY));

        ArrayList<ModelContainer> outputMeshes = new ArrayList<>();

        ArrayList<IntegerArrayList> newFaces = new ArrayList();
        ArrayList<FloatArrayList> newPoints = new ArrayList();

        for (int i = 0; i < numberOfBins; i++)
        {
            newFaces.add(new IntegerArrayList());
            newPoints.add(new FloatArrayList());
        }

        for (int triOffset = 0; triOffset < originalFaces.size(); triOffset += 6)
        {
            int vertex1Ref = originalFaces.get(triOffset) * 3;
            float x1Pos = originalPoints.get(vertex1Ref);
            float y1Pos = originalPoints.get(vertex1Ref + 1);
            float z1Pos = originalPoints.get(vertex1Ref + 2);
            int vertex1Bin = (int) Math.floor((Math.abs(y1Pos) + originalModelBounds.getMaxY())
                    / -minPrintableY);

            int vertex2Ref = originalFaces.get(triOffset + 2) * 3;
            float x2Pos = originalPoints.get(vertex2Ref);
            float y2Pos = originalPoints.get(vertex2Ref + 1);
            float z2Pos = originalPoints.get(vertex2Ref + 2);
            int vertex2Bin = (int) Math.floor((Math.abs(y2Pos) + originalModelBounds.getMaxY())
                    / -minPrintableY);

            int vertex3Ref = originalFaces.get(triOffset + 4) * 3;
            float x3Pos = originalPoints.get(vertex3Ref);
            float y3Pos = originalPoints.get(vertex3Ref + 1);
            float z3Pos = originalPoints.get(vertex3Ref + 2);
            int vertex3Bin = (int) Math.floor((Math.abs(y3Pos) + originalModelBounds.getMaxY())
                    / -minPrintableY);

//            steno.info("Considering " + y1Pos + ":" + y2Pos + ":" + y3Pos);
            if (vertex1Bin == vertex2Bin && vertex1Bin == vertex3Bin)
            {
                newFaces.get(vertex1Bin).add(newPoints.size() / 3);
                newFaces.get(vertex1Bin).add(0);
                newPoints.get(vertex1Bin).add(x1Pos);
                newPoints.get(vertex1Bin).add(y1Pos);
                newPoints.get(vertex1Bin).add(z1Pos);

                newFaces.get(vertex1Bin).add(newPoints.size() / 3);
                newFaces.get(vertex1Bin).add(0);
                newPoints.get(vertex1Bin).add(x2Pos);
                newPoints.get(vertex1Bin).add(y2Pos);
                newPoints.get(vertex1Bin).add(z2Pos);

                newFaces.get(vertex1Bin).add(newPoints.size() / 3);
                newFaces.get(vertex1Bin).add(0);
                newPoints.get(vertex1Bin).add(x3Pos);
                newPoints.get(vertex1Bin).add(y3Pos);
                newPoints.get(vertex1Bin).add(z3Pos);
            }
        }

        FloatArrayList texCoords = new FloatArrayList();
        texCoords.add(0f);
        texCoords.add(0f);

        for (int binCounter = 0; binCounter < numberOfBins; binCounter++)
        {
            TriangleMesh output = new TriangleMesh();

            output.getPoints().addAll(newPoints.get(binCounter).toFloatArray());
            output.getTexCoords().addAll(texCoords.toFloatArray());
            output.getFaces().addAll(newFaces.get(binCounter).toIntArray());
            int[] smoothingGroups = new int[newFaces.get(binCounter).size() / 6];
            for (int i = 0; i < smoothingGroups.length; i++)
            {
                smoothingGroups[i] = 0;
            }
            output.getFaceSmoothingGroups().addAll(smoothingGroups);

            MeshView meshView = new MeshView();

            meshView.setMesh(output);
            meshView.setMaterial(ApplicationMaterials.getDefaultModelMaterial());
            meshView.setCullFace(CullFace.BACK);
            meshView.setId(getId() + "-" + binCounter);

            ModelContainer modelContainer = new ModelContainer(getModelFile(), meshView);

            outputMeshes.add(modelContainer);
        }

        return outputMeshes;
    }

    /**
     * This compareTo implementation compares based on the overall size of the
     * model.
     */
    @Override
    public int compareTo(Object o) throws ClassCastException
    {
        int returnVal = 0;

        ModelContainer compareToThis = (ModelContainer) o;
        if (getTotalSize() > compareToThis.getTotalSize())
        {
            returnVal = 1;
        } else if (getTotalSize() < compareToThis.getTotalSize())
        {
            returnVal = -1;
        }

        return returnVal;
    }

    /**
     * Return the face normal for the face of the given index.
     *
     */
    Vector3D getFaceNormal(MeshView meshView, int faceNumber) throws MathArithmeticException
    {
        TriangleMesh triMesh = (TriangleMesh) meshView.getMesh();
        int baseFaceIndex = faceNumber * 6;
        int v1PointIndex = triMesh.getFaces().get(baseFaceIndex);
        int v2PointIndex = triMesh.getFaces().get(baseFaceIndex + 2);
        int v3PointIndex = triMesh.getFaces().get(baseFaceIndex + 4);
        ObservableFloatArray points = triMesh.getPoints();
        Vector3D v1 = convertToVector3D(points, v1PointIndex);
        Vector3D v2 = convertToVector3D(points, v2PointIndex);
        Vector3D v3 = convertToVector3D(points, v3PointIndex);
        Vector3D result1 = v2.subtract(v1);
        Vector3D result2 = v3.subtract(v1);
        Vector3D faceNormal = result1.crossProduct(result2);
        Vector3D currentVectorNormalised = faceNormal.normalize();
        return currentVectorNormalised;
    }

    Vector3D getFaceCentre(MeshView meshView, int faceNumber)
    {
        TriangleMesh triMesh = (TriangleMesh) meshView.getMesh();
        int baseFaceIndex = faceNumber * 6;
        int v1PointIndex = triMesh.getFaces().get(baseFaceIndex);
        int v2PointIndex = triMesh.getFaces().get(baseFaceIndex + 2);
        int v3PointIndex = triMesh.getFaces().get(baseFaceIndex + 4);
        ObservableFloatArray points = triMesh.getPoints();
        Vector3D v1 = convertToVector3D(points, v1PointIndex);
        Vector3D v2 = convertToVector3D(points, v2PointIndex);
        Vector3D v3 = convertToVector3D(points, v3PointIndex);

        return new Vector3D((v1.getX() + v2.getX() + v3.getX()) / 3.0d,
                (v1.getY() + v2.getY() + v3.getY()) / 3.0d,
                (v1.getZ() + v2.getZ() + v3.getZ()) / 3.0d);
    }

    private Vector3D convertToVector3D(ObservableFloatArray points, int v1PointIndex)
    {
        Vector3D v1 = new Vector3D(points.get(v1PointIndex * 3), points.get((v1PointIndex * 3)
                + 1), points.get((v1PointIndex * 3) + 2));
        return v1;
    }

    public void dropToBed()
    {
        // Correct transformPostRotationYAdjust for change in height (Y)
        transformDropToBedYAdjust.setY(0);
        RectangularBounds modelBoundsParent = calculateBoundsInBedCoordinateSystem();
        transformDropToBedYAdjust.setY(-modelBoundsParent.getMaxY());
        lastTransformedBoundsInParent = calculateBoundsInParentCoordinateSystem();
    }

    @Override
    public double getCentreZ()
    {
        return getLocalBounds().getCentreZ();
    }

    @Override
    public double getCentreY()
    {
        return getLocalBounds().getCentreY();
    }

    @Override
    public double getCentreX()
    {
        return getLocalBounds().getCentreX();
    }

    public double getTransformedCentreY()
    {
        return lastTransformedBoundsInParent.getCentreY();
    }

    @Override
    public double getScaledHeight()
    {
        return getLocalBounds().getHeight() * preferredYScale.doubleValue();
    }

    @Override
    public double getScaledDepth()
    {
        return getLocalBounds().getDepth() * preferredZScale.doubleValue();
    }

    @Override
    public double getScaledWidth()
    {
        return getLocalBounds().getWidth() * preferredXScale.doubleValue();
    }

    /**
     * Get the width on the bed but ONLY for top-level models.
     */
    public double getTotalWidth()
    {
        double totalwidth = lastTransformedBoundsInParent.getWidth();
        return totalwidth;
    }

    /**
     * Get the depth on the bed but ONLY for top-level models.
     */
    public double getTotalDepth()
    {
        double totaldepth = lastTransformedBoundsInParent.getDepth();
        return totaldepth;
    }

    /**
     * Get a relative measure of the total size on the bed but ONLY for
     * top-level models.
     */
    public double getTotalSize()
    {
        return getTotalWidth() + getTotalDepth();
    }

    public void addSelectionHighlighter()
    {
        selectionHighlighter = new SelectionHighlighter(this, cameraDistance);
        getChildren().add(selectionHighlighter);
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

    private void updateLastTransformedBoundsInParentForTranslateByZ(double deltaCentreZ)
    {
        if (lastTransformedBoundsInParent != null)
        {
            lastTransformedBoundsInParent.translateZ(deltaCentreZ);
        }
        notifyShapeChange();
        notifyScreenExtentsChange();
    }

    private void showSelectionHighlighter()
    {

        selectionHighlighter.setVisible(true);
    }

    private void hideSelectionHighlighter()
    {
        selectionHighlighter.setVisible(false);
    }

    @Override
    public Point3D transformMeshToRealWorldCoordinates(float vertexX, float vertexY, float vertexZ)
    {
        return bed.sceneToLocal(meshView.localToScene(vertexX, vertexY, vertexZ));
    }

    /**
     * If this model is associated with the given extruder number then recolour
     * it to the given colour, also taking into account if it is misplaced (off
     * the bed). Also call the same method on any child ModelContainers.
     *
     * @param extruder0Material
     * @param extruder1Material
     * @param showMisplacedColour
     */
    public void updateColour(final PhongMaterial extruder0Material, final PhongMaterial extruder1Material,
            boolean showMisplacedColour)
    {
        boolean offBed = getRootModelContainer().isOffBed.get();
        if (showMisplacedColour)
        {
            if (offBed)
            {
                meshView.setMaterial(ApplicationMaterials.getOffBedModelMaterial());
            } else if (isCollided)
            {
                meshView.setMaterial(ApplicationMaterials.getCollidedModelMaterial());
            }
        }
        if (!showMisplacedColour || (!offBed && !isCollided))
        {
            switch (associateWithExtruderNumber.get())
            {
                case 0:
                    if (extruder0Material == null)
                    {
                        meshView.setMaterial(ApplicationMaterials.getDefaultModelMaterial());
                    } else
                    {
                        meshView.setMaterial(extruder0Material);
                    }
                    break;
                case 1:
                    if (extruder1Material == null)
                    {
                        meshView.setMaterial(ApplicationMaterials.getDefaultModelMaterial());
                    } else
                    {
                        meshView.setMaterial(extruder1Material);
                    }
                    break;
                default:
                    meshView.setMaterial(ApplicationMaterials.getDefaultModelMaterial());
                    break;
            }
        }
    }

    @Override
    protected boolean recalculateScreenExtents()
    {
        boolean extentsChanged = false;

        if (getLocalBounds() != null)
        {
            double halfWidth = getScaledWidth() / 2;
            double halfDepth = getScaledDepth() / 2;
            double halfHeight = getScaledHeight() / 2;
            double minX = getCentreX() - halfWidth;
            double maxX = getCentreX() + halfWidth;
            double minZ = getCentreZ() - halfDepth;
            double maxZ = getCentreZ() + halfDepth;
            double minY = getCentreY() - halfHeight;
            double maxY = getCentreY() + halfHeight;

            Point2D frontLeftBottom = localToScreen(minX, maxY, minZ);
            Point2D frontRightBottom = localToScreen(maxX, maxY, minZ);
            Point2D backLeftBottom = localToScreen(minX, maxY, maxZ);
            Point2D backRightBottom = localToScreen(maxX, maxY, maxZ);
            Point2D frontLeftTop = localToScreen(minX, minY, minZ);
            Point2D frontRightTop = localToScreen(maxX, minY, minZ);
            Point2D backLeftTop = localToScreen(minX, minY, maxZ);
            Point2D backRightTop = localToScreen(maxX, minY, maxZ);

//            Point3D frontLeftBottomScene = localToScene(minX, maxY, minZ);
//            Point3D frontRightBottomScene = localToScene(maxX, maxY, minZ);
//            Point3D backLeftBottomScene = localToScene(minX, maxY, maxZ);
//            Point3D backRightBottomScene = localToScene(maxX, maxY, maxZ);
//            Point3D frontLeftTopScene = localToScene(minX, minY, minZ);
//            Point3D frontRightTopScene = localToScene(maxX, minY, minZ);
//            Point3D backLeftTopScene = localToScene(minX, minY, maxZ);
//            Point3D backRightTopScene = localToScene(maxX, minY, maxZ);
//            Point2D frontLeftBottom = CameraHelper.project(cameraViewingMe, localToScene(minX, maxY, minZ));
//            Point2D frontRightBottom = CameraHelper.project(cameraViewingMe, localToScene(maxX, maxY, minZ));
//            Point2D backLeftBottom = CameraHelper.project(cameraViewingMe, localToScene(minX, maxY, maxZ));
//            Point2D backRightBottom = CameraHelper.project(cameraViewingMe, localToScene(maxX, maxY, maxZ));
//            Point2D frontLeftTop = CameraHelper.project(cameraViewingMe, localToScene(minX, minY, minZ));
//            Point2D frontRightTop = CameraHelper.project(cameraViewingMe, localToScene(maxX, minY, minZ));
//            Point2D backLeftTop = CameraHelper.project(cameraViewingMe, localToScene(minX, minY, maxZ));
//            Point2D backRightTop = CameraHelper.project(cameraViewingMe, localToScene(maxX, minY, maxZ));
////        Point3D frontLeftBottomCamera = cameraTransform.sceneToLocal(frontLeftBottomScene);
//        Point3D frontRightBottomCamera = cameraTransform.sceneToLocal(frontRightBottomScene);
//        Point3D backLeftBottomCamera = cameraTransform.sceneToLocal(backLeftBottomScene);
//        Point3D backRightBottomCamera = cameraTransform.sceneToLocal(backRightBottomScene);
//        Point3D frontLeftTopCamera = cameraTransform.sceneToLocal(frontLeftTopScene);
//        Point3D frontRightTopCamera = cameraTransform.sceneToLocal(frontRightTopScene);
//        Point3D backLeftTopCamera = cameraTransform.sceneToLocal(backLeftTopScene);
//        Point3D backRightTopCamera = cameraTransform.sceneToLocal(backRightTopScene);
//
//        Point2D frontLeftBottom = cameraTransform.localToScreen(frontLeftBottomCamera);
//        Point2D frontRightBottom = cameraTransform.localToScreen(frontRightBottomCamera);
//        Point2D backLeftBottom = cameraTransform.localToScreen(backLeftBottomCamera);
//        Point2D backRightBottom = cameraTransform.localToScreen(backRightBottomCamera);
//        Point2D frontLeftTop = cameraTransform.localToScreen(frontLeftTopCamera);
//        Point2D frontRightTop = cameraTransform.localToScreen(frontRightTopCamera);
//        Point2D backLeftTop = cameraTransform.localToScreen(backLeftTopCamera);
//        Point2D backRightTop = cameraTransform.localToScreen(backRightTopCamera);
            ScreenExtents lastExtents = extents;
            if (extents == null && frontLeftBottom != null)
            {
                extents = new ScreenExtents();
            }

            if (extents != null && frontLeftBottom != null)
            {
                extents.heightEdges.clear();
                extents.heightEdges.add(0, new Edge(frontLeftBottom, frontLeftTop));
                extents.heightEdges.add(1, new Edge(frontRightBottom, frontRightTop));
                extents.heightEdges.add(2, new Edge(backLeftBottom, backLeftTop));
                extents.heightEdges.add(3, new Edge(backRightBottom, backRightTop));

                extents.widthEdges.clear();
                extents.widthEdges.add(0, new Edge(frontLeftBottom, frontRightBottom));
                extents.widthEdges.add(1, new Edge(backLeftBottom, backRightBottom));
                extents.widthEdges.add(2, new Edge(frontLeftTop, frontRightTop));
                extents.widthEdges.add(3, new Edge(backLeftTop, backRightTop));

                extents.depthEdges.clear();
                extents.depthEdges.add(0, new Edge(frontLeftBottom, backLeftBottom));
                extents.depthEdges.add(1, new Edge(frontRightBottom, backRightBottom));
                extents.depthEdges.add(2, new Edge(frontLeftTop, backLeftTop));
                extents.depthEdges.add(3, new Edge(frontRightTop, backRightTop));

                extents.recalculateMaxMin();
            }

            if (extents != null
                    && !extents.equals(lastExtents))
            {
                extentsChanged = true;
            }
        }

        return extentsChanged;
    }

    @Override
    public double getTransformedHeight()
    {
        return getScaledHeight();
    }

    @Override
    public double getTransformedWidth()
    {
        return getScaledWidth();
    }

    @Override
    public double getTransformedDepth()
    {
        return getScaledDepth();
    }

    @Override
    public void heresYourCamera(Camera camera)
    {
        this.cameraViewingMe = camera;
    }

    @Override
    public void cameraViewOfYouHasChanged(double cameraDistance)
    {
        this.cameraDistance = cameraDistance;
        if (selectionHighlighter != null)
        {
            selectionHighlighter.cameraDistanceChange(cameraDistance);
        }
        notifyScreenExtentsChange();
    }

    public Set<ModelContainer> getChildModelContainers()
    {
        return Collections.EMPTY_SET;
    }

    public Set<ModelContainer> getDescendentModelContainers()
    {
        return Collections.EMPTY_SET;

    }

    @Override
    public ThreeDItemState getState()
    {
        return new ThreeDItemState(modelId,
                transformMoveToPreferred.getX(),
                transformDropToBedYAdjust.getY(),
                transformMoveToPreferred.getZ(),
                preferredXScale.get(), preferredYScale.get(), preferredZScale.get(),
                preferredRotationTwist.get(), preferredRotationTurn.get(),
                preferredRotationLean.get());
    }

    @Override
    public void setState(ItemState state)
    {
        if (state instanceof ThreeDItemState)
        {
            ThreeDItemState convertedState = (ThreeDItemState) state;
            transformMoveToPreferred.setX(convertedState.x);
            transformMoveToPreferred.setZ(convertedState.z);
            transformDropToBedYAdjust.setY(convertedState.y);

            preferredXScale.set(convertedState.preferredXScale);
            transformScalePreferred.setX(convertedState.preferredXScale);
            preferredYScale.set(convertedState.preferredYScale);
            transformScalePreferred.setY(convertedState.preferredYScale);
            preferredZScale.set(convertedState.preferredZScale);
            transformScalePreferred.setZ(convertedState.preferredZScale);

            preferredRotationLean.set(convertedState.preferredRotationLean);
            preferredRotationTwist.set(convertedState.preferredRotationTwist);
            preferredRotationTurn.set(convertedState.preferredRotationTurn);

            updateTransformsFromLeanTwistTurnAngles();

            lastTransformedBoundsInParent = calculateBoundsInParentCoordinateSystem();
            notifyScreenExtentsChange();
            notifyShapeChange();
        }
    }

    public boolean isInvalidMesh()
    {
        return isInvalidMesh;
    }

    public void setIsInvalidMesh(boolean isInvalidMesh)
    {
        this.isInvalidMesh = isInvalidMesh;
    }

    public MeshView getCollisionShape()
    {
        return collisionShape;
    }

    public MeshView addCollisionShapeListener(CollisionShapeListener collisionShapeListener)
    {
        collisionShapeListeners.add(collisionShapeListener);
        return collisionShape;
    }

    public void removeCollisionShapeListener(CollisionShapeListener collisionShapeListener)
    {
        collisionShapeListeners.remove(collisionShapeListener);
    }

    @Override
    protected void printVolumeBoundsUpdated()
    {
        checkOffBed();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Id:");
        sb.append(modelId);
        sb.append("\n");
        sb.append("X:");
        sb.append(getState().x);
        sb.append("\t");
        sb.append("Y:");
        sb.append(getState().y);
        sb.append("\t");
        sb.append("Z:");
        sb.append(getState().z);
        sb.append("\n");
        sb.append("Scale X:");
        sb.append(preferredXScale);
        sb.append("\t");
        sb.append("Scale Y:");
        sb.append(preferredYScale);
        sb.append("\t");
        sb.append("Scale Z:");
        sb.append(preferredZScale);
        sb.append("\n");
        sb.append("Lean:");
        sb.append(preferredRotationLean);
        sb.append("\t");
        sb.append("Twist:");
        sb.append(preferredRotationTwist);
        sb.append("\t");
        sb.append("Turn:");
        sb.append(preferredRotationTurn);
        sb.append("\n");

        return sb.toString();
    }

    public void updateLastTransformedBoundsInParent()
    {
        lastTransformedBoundsInParent = calculateBoundsInParentCoordinateSystem();
    }
}
