/*
 * Copyright 2014 CEL UK
 */
package celtech.coreUI.visualisation;

/**
 *
 * @author tony
 */
public interface ShapeProvider
{

    public void addShapeChangeListener(ShapeChangeListener listener);

    public void removeShapeChangeListener(ShapeChangeListener listener);

    public interface ShapeChangeListener
    {
        public void shapeChanged(ShapeProvider shapeProvider);
    }
}
