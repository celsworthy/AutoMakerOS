package celtech.coreUI.visualisation.shapes;

import javafx.scene.shape.TriangleMesh;

/**
 * A 3D geometric point array that has the x, y, z coordinates of every point
 * as a function of other variables.
 */
public abstract class SymbolicPointArray {
    final TriangleMesh tmesh = new TriangleMesh();

    /**
     *
     */
    final public float[] data;

    /**
     *
     */
    final public int numPoints;
    
    /**
     *
     * @param data
     */
    protected SymbolicPointArray(float[] data) {
        this.data = data;
        this.numPoints = data.length/tmesh.getPointElementSize();
    }
    
    /**
     * Updates the variables x, y, z based on the state of the other variables
     * that this symbolic point depends on.
     */
    public abstract void update();
}
