/*
 * Copyright 2015 CEL UK
 */
package celtech.modelcontrol;

import celtech.coreUI.visualisation.ScreenExtentsProvider;
import celtech.roboxbase.utils.RectangularBounds;
import celtech.utils.threed.ThreeDUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * ShapeGroup is a ShapeContainer that is a group of child ShapeContainer or
 * other ShapeGroupa.
 *
 * @author tony
 */
public class ShapeGroup extends ShapeContainer implements ScreenExtentsProvider.ScreenExtentsListener
{
    private Stenographer steno = StenographerFactory.getStenographer(ShapeGroup.class.getName());
    private Set<ShapeContainer> childShapeContainers;

    private double turnRotationAngle = 0;
    private Rotate turnRotate = new Rotate(0, ThreeDUtils.Y_AXIS_JFX);

    public ShapeGroup(Set<ShapeContainer> shapeContainers)
    {
        super();
        setModelName("Group " + getModelId());
        initialise(shapeContainers);
    }

    public ShapeGroup(Set<ShapeContainer> shapeContainers, int groupModelId)
    {
        super("Group " + groupModelId, groupModelId);
        initialise(shapeContainers);
    }

    private void initialise(Set<ShapeContainer> shapeContainers)
    {
        setId(getModelName());
        childShapeContainers = new HashSet<>();
        childShapeContainers.addAll(shapeContainers);
        shapeGroup.getChildren().addAll(shapeContainers);
        initialiseTransforms(false);
        for (ShapeContainer sContainer : shapeContainers)
        {
            sContainer.addScreenExtentsChangeListener(this);
        }
        lastTransformedBoundsInParent = calculateBoundsInParentCoordinateSystem();
        updateOriginalModelBounds();
    }

    /**
     * Return the immediate children of this group.
     */
    public Set<ShapeContainer> getChildShapeContainers()
    {
        return Collections.unmodifiableSet(childShapeContainers);
    }

    /**
     * Return all descendent ShapeContainers of this ShapeGroup.
     * NOTE - this includes every node - e.g. if a group node is present then so are all of its children
     * @return 
     */
    public Set<ShapeContainer> getDescendentShapeContainers()
    {
        Set<ShapeContainer> sContainers = new HashSet<>(childShapeContainers);
        for (ShapeContainer sContainer : childShapeContainers)
        {
            if (sContainer instanceof ShapeGroup)
                sContainers.addAll(((ShapeGroup)sContainer).getDescendentShapeContainers());
        }

        return sContainers;
    }

    public void removeModel(ShapeContainer sContainer)
    {
        sContainer.removeScreenExtentsChangeListener(this);
        childShapeContainers.remove(sContainer);
    }

    @Override
    public ShapeContainer makeCopy()
    {
        Set<ShapeContainer> childShapes = new HashSet<>();
        for (ShapeContainer childShape : childShapeContainers)
        {
            ShapeContainer shapeContainerCopy = (ShapeContainer)childShape.makeCopy();
            shapeContainerCopy.setState(childShape.getState());
            childShapes.add(shapeContainerCopy);
        }
        ShapeGroup copy = new ShapeGroup(childShapes);
        copy.setXScale(this.getXScale(), true);
        copy.setYScale(this.getYScale(), true);
        copy.setRotationTurn(this.getRotationTurn());
        copy.recalculateScreenExtents();
        return copy;
    }

    @Override
    public void screenExtentsChanged(ScreenExtentsProvider screenExtentsProvider)
    {
        notifyScreenExtentsChange();
        notifyShapeChange();
    }
    
    @Override
    protected boolean recalculateScreenExtents()
    {
       if (childShapeContainers != null)
            childShapeContainers.forEach((s) -> s.recalculateScreenExtents()); 
       return super.recalculateScreenExtents();
    }
    
    /**
     * Update the given group structure with the details of this group.
     */
    public void addGroupStructure(Map<Integer, Set<Integer>> groupStructure)
    {
        for (ShapeContainer s : childShapeContainers)
        {
            if (groupStructure.get(modelId) == null)
            {
                groupStructure.put(modelId, new HashSet<>());
            }
            groupStructure.get(modelId).add(s.modelId);
        }
    }
    
    @Override
    public void setBedReference(Group bed)
    {
        super.setBedReference(bed);
        if (childShapeContainers != null)
            childShapeContainers.forEach((s) -> s.setBedReference(bed));
    }
    
    public void traverseAllChildren(Consumer<ShapeContainer> c)
    {
        // Non-recursive version of getDescendentShapeContainers. 
        if (childShapeContainers != null)
        {
            List<ShapeContainer> shapesToProcess = new ArrayList<>();
            int index = 0;
            shapesToProcess.addAll(childShapeContainers);
            while (index < shapesToProcess.size())
            {
                ShapeContainer sc = shapesToProcess.get(index);
                index++;
                c.accept(sc);
                if (sc instanceof ShapeGroup && ((ShapeGroup)sc).childShapeContainers != null)
                    shapesToProcess.addAll(((ShapeGroup)sc).childShapeContainers);
            }
        }
    }

    @Override
    public void checkOffBed()
    {
        super.checkOffBed();
        boolean offBed = isOffBed.get();
        traverseAllChildren((s) ->
        {
            s.isOffBedProperty().set(offBed);
            s.setColourFromState();
        });
    }
    
    public void setSelected(boolean selected)
    {
        super.setSelected(selected);
        traverseAllChildren((s) ->
        {
            s.setSelected(selected);
            s.setColourFromState();
        });
    }
}
