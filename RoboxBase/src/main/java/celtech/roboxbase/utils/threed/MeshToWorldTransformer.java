package celtech.roboxbase.utils.threed;

import javafx.geometry.Point3D;

/**
 *
 * @author ianhudson
 */
public interface MeshToWorldTransformer
{
    public Point3D transformMeshToRealWorldCoordinates(float vertexX, float vertexY, float vertexZ);
}
