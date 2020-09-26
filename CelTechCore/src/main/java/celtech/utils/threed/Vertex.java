/*
 * Copyright 2015 CEL UK
 */
package celtech.utils.threed;

import javafx.scene.shape.TriangleMesh;


/**
 * @author tony
 */
final class Vertex
{

    int meshVertexIndex;
    final float x;
    final float y;
    final float z;

    public Vertex(int meshVertexIndex, float x, float y, float z)
    {
        this.meshVertexIndex = meshVertexIndex;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vertex(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        meshVertexIndex = -1;
    }

    @Override
    public String toString()
    {
        return "Vertex{" + "meshVertexIndex=" + meshVertexIndex + ", x=" + x + ", y=" + y + ", z="
            + z + '}';
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Vertex))
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }

        Vertex other = (Vertex) obj;
        if (other.x != x)
        {
            return false;
        }
        if (other.y != y)
        {
            return false;
        }
        if (other.z != z)
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        return (int) (x * 1237f + y * 107f + 23 * z);
    }

//    boolean almostEquals(TriangleMesh mesh, Vertex otherVertex)
//    {
//
//        int rawBitsX = Float.floatToIntBits(x);
//        int rawBitsY = Float.floatToIntBits(y);
//        int rawBitsZ = Float.floatToIntBits(z);
//        
//        int rawBitsXOther = Float.floatToIntBits(otherVertex.x);
//        int rawBitsYOther = Float.floatToIntBits(otherVertex.y);
//        int rawBitsZOther = Float.floatToIntBits(otherVertex.z);
//        
//        if (Math.abs(rawBitsX - rawBitsXOther) < 3
//            && Math.abs(rawBitsY - rawBitsYOther) < 3
//            && Math.abs(rawBitsZ - rawBitsZOther) < 3) {
//            return true;
//        }
//        return false;
//        
//    }
}
