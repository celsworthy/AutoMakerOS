package celtech.modelcontrol;

import celtech.Lookup;
import celtech.coreUI.visualisation.ScreenExtents;
import celtech.coreUI.visualisation.ScreenExtentsProvider;
import celtech.coreUI.visualisation.ScreenExtentsProviderTwoD;
import celtech.coreUI.visualisation.ShapeProviderTwoD;
import celtech.roboxbase.configuration.datafileaccessors.PrinterContainer;
import celtech.roboxbase.configuration.fileRepresentation.PrinterDefinitionFile;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.utils.RectangularBounds;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

/**
 *
 * @author ianhudson
 */
public abstract class ProjectifiableThing extends Group implements ScreenExtentsProviderTwoD, ShapeProviderTwoD
{

    private File modelFile;
    protected boolean isCollided = false;
    protected BooleanProperty isSelected;
    protected BooleanProperty isOffBed;
    protected ScreenExtents extents = null;
    private List<ShapeProviderTwoD.ShapeChangeListener> shapeChangeListeners;
    private List<ScreenExtentsProviderTwoD.ScreenExtentsListener> screenExtentsChangeListeners;
    protected double printVolumeWidth = 0;
    protected double printVolumeDepth = 0;
    protected double printVolumeHeight = 0;
    protected Group bed;

    /**
     * The modelId is only guaranteed unique at the project level because it
     * could be reloaded with duplicate values from saved models into other
     * projects.
     */
    protected int modelId = -1;
    private SimpleStringProperty modelName;
    protected Translate transformBedCentre;
    protected Scale transformScalePreferred;
    protected Rotate transformRotateTurnPreferred;
    protected Translate transformMoveToPreferred;
    protected List<Transform> rotationTransforms;
    protected RectangularBounds lastTransformedBoundsInParent;
    protected RectangularBounds originalModelBounds = null;
    protected static final Point3D Y_AXIS = new Point3D(0, 1, 0);
    protected static final Point3D Z_AXIS = new Point3D(0, 0, 1);
    protected static final Point3D X_AXIS = new Point3D(1, 0, 0);
    protected DoubleProperty preferredXScale;
    protected DoubleProperty preferredYScale;
    protected DoubleProperty preferredRotationTurn;

    protected double bedCentreOffsetX = 0.0;
    protected double bedCentreOffsetY = 0.0;
    protected double bedCentreOffsetZ = 0.0;

    public ProjectifiableThing()
    {
        initialise();
    }

    public ProjectifiableThing(File modelFile)
    {
        this.modelFile = modelFile;
        initialise();
    }

    private void initialise()
    {
        isSelected = new SimpleBooleanProperty(false);
        isOffBed = new SimpleBooleanProperty(false);
        shapeChangeListeners = new ArrayList<>();
        screenExtentsChangeListeners = new ArrayList<>();

        Lookup.getSelectedPrinterProperty().addListener((ObservableValue<? extends Printer> ov, Printer t, Printer t1) ->
        {
            updatePrintVolumeBounds(t1);
        });

        updatePrintVolumeBounds(Lookup.getSelectedPrinterProperty().get());
    }

    public int getModelId()
    {
        return modelId;
    }

    public abstract ItemState getState();

    public abstract void setState(ItemState state);

    /**
     * Make a copy of this ModelContainer and return it.
     *
     * @return
     */
    public abstract ProjectifiableThing makeCopy();

    public abstract void clearElements();

    public void setModelFile(File modelFile)
    {
        this.modelFile = modelFile;
    }

    public File getModelFile()
    {
        return modelFile;
    }

    public final void addChildNodes(ObservableList<Node> nodes)
    {
        getChildren().addAll(nodes);
    }

    public final void addChildNode(Node node)
    {
        getChildren().add(node);
    }

    public final ObservableList<Node> getChildNodes()
    {
        return getChildren();
    }

    public void setSelected(boolean selected)
    {
        isSelected.set(selected);
        selectedAction();
    }

    public final boolean isSelected()
    {
        return isSelected.get();
    }

    public abstract void selectedAction();

    public final void setModelName(String modelName)
    {
        if (this.modelName == null)
        {
            this.modelName = new SimpleStringProperty();
        }
        this.modelName.set(modelName);
    }

    public final String getModelName()
    {
        return modelName.get();
    }

    public final void setCollided(boolean collided)
    {
        this.isCollided = collided;
    }

    public final boolean isCollided()
    {
        return isCollided;
    }

    protected abstract boolean recalculateScreenExtents();

    @Override
    public final ScreenExtents getScreenExtents()
    {
        if (extents == null)
        {
            recalculateScreenExtents();
        }
        return extents;
    }

    @Override
    public final void addScreenExtentsChangeListener(ScreenExtentsProvider.ScreenExtentsListener listener)
    {
        recalculateScreenExtents();
        screenExtentsChangeListeners.add(listener);
    }

    @Override
    public final void removeScreenExtentsChangeListener(
            ScreenExtentsProvider.ScreenExtentsListener listener)
    {
        screenExtentsChangeListeners.remove(listener);
    }

    public final void notifyScreenExtentsChange()
    {
        if (recalculateScreenExtents())
        {
            for (ScreenExtentsProvider.ScreenExtentsListener screenExtentsListener : screenExtentsChangeListeners)
            {
                screenExtentsListener.screenExtentsChanged(this);
            }
        }
    }

    @Override
    public final void addShapeChangeListener(ShapeProviderTwoD.ShapeChangeListener listener)
    {
        shapeChangeListeners.add(listener);
    }

    @Override
    public final void removeShapeChangeListener(ShapeProviderTwoD.ShapeChangeListener listener)
    {
        shapeChangeListeners.remove(listener);
    }

    /**
     * This method must be called at the end of any operation that changes one
     * or more of the transforms.
     */
    public final void notifyShapeChange()
    {
        for (ShapeProviderTwoD.ShapeChangeListener shapeChangeListener : shapeChangeListeners)
        {
            shapeChangeListener.shapeChanged(this);
        }
    }

    private void updatePrintVolumeBounds(Printer printer)
    {
        if (printer != null
                && printer.printerConfigurationProperty().get() != null)
        {
            printVolumeWidth = printer.printerConfigurationProperty().get().getPrintVolumeWidth();
            printVolumeDepth = printer.printerConfigurationProperty().get().getPrintVolumeDepth();
            printVolumeHeight = printer.printerConfigurationProperty().get().getPrintVolumeHeight();
        } else
        {
            PrinterDefinitionFile defaultPrinterConfiguration = PrinterContainer.getPrinterByID(PrinterContainer.defaultPrinterID);
            printVolumeWidth = defaultPrinterConfiguration.getPrintVolumeWidth();
            printVolumeDepth = defaultPrinterConfiguration.getPrintVolumeDepth();
            printVolumeHeight = defaultPrinterConfiguration.getPrintVolumeHeight();
        }
        printVolumeBoundsUpdated();
    }

    protected abstract void printVolumeBoundsUpdated();

    public abstract void checkOffBed();

    public BooleanProperty isOffBedProperty()
    {
        return isOffBed;
    }

    public abstract void moveToCentre();

    public void setBedReference(Group bed)
    {
        this.bed = bed;
    }

    public abstract void setBedCentreOffsetTransform();

    public RectangularBounds getOriginalModelBounds()
    {
        return originalModelBounds;
    }

    public abstract void shrinkToFitBed();

    protected abstract RectangularBounds calculateBoundsInLocal();

    public abstract RectangularBounds calculateBoundsInBedCoordinateSystem();

    protected void setScalePivotToCentreOfModel()
    {
        transformScalePreferred.setPivotX(getBoundsInLocal().getMinX()
                + getBoundsInLocal().getWidth() / 2.0);
        transformScalePreferred.setPivotY(getBoundsInLocal().getMinY()
                + getBoundsInLocal().getHeight() / 2.0);

        if (this instanceof ScaleableThreeD)
        {
            transformScalePreferred.setPivotZ(getBoundsInLocal().getMinZ()
                    + getBoundsInLocal().getDepth() / 2.0);
        }
    }

    protected void setRotationPivotsToCentreOfModel()
    {
        transformRotateTurnPreferred.setPivotX(originalModelBounds.getCentreX());
        transformRotateTurnPreferred.setPivotY(originalModelBounds.getCentreY());

        if (this instanceof RotatableThreeD)
        {
            transformRotateTurnPreferred.setPivotZ(originalModelBounds.getCentreZ());
        }
    }

    public void updateOriginalModelBounds()
    {
        originalModelBounds = calculateBoundsInLocal();
        setScalePivotToCentreOfModel();
        setRotationPivotsToCentreOfModel();
    }

    public abstract RectangularBounds calculateBoundsInParentCoordinateSystem();

    protected abstract void updateScaleTransform(boolean dropToBed);

    public Scale getTransformScale()
    {
        return transformScalePreferred;
    }

    public double getXScale()
    {
        return preferredXScale.get();
    }

    public double getYScale()
    {
        return preferredYScale.get();
    }

    public void setXScale(double scaleFactor, boolean dropToBed)
    {
        preferredXScale.set(scaleFactor);
        transformScalePreferred.setX(scaleFactor);
        updateScaleTransform(dropToBed);
    }

    public void setYScale(double scaleFactor, boolean dropToBed)
    {
        preferredYScale.set(scaleFactor);
        transformScalePreferred.setY(scaleFactor);
        updateScaleTransform(dropToBed);
    }

    public double getTransformedCentreDepth()
    {
        if (this instanceof TranslateableThreeD)
        {
            return lastTransformedBoundsInParent.getCentreZ();
        } else
        {
            return lastTransformedBoundsInParent.getCentreY();
        }
    }

    public double getTransformedCentreX()
    {
        return lastTransformedBoundsInParent.getCentreX();
    }
    
    public RectangularBounds getLastTransformedBoundsInParent()
    {
        return lastTransformedBoundsInParent;
    }
}
