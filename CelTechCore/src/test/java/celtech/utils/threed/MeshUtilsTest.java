/*
 * Copyright 2015 CEL UK
 */
package celtech.utils.threed;

import javafx.scene.shape.TriangleMesh;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author tony
 */
public class MeshUtilsTest
{

    @Test
    public void testRemoveDuplicateVertices()
    {
        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(0, 1, 0);
        mesh.getPoints().addAll(0, 1, 3);
        mesh.getPoints().addAll(3, 1, 3);
        mesh.getPoints().addAll(3, 1, 0);

        mesh.getPoints().addAll(4, 1, 4);
        mesh.getPoints().addAll(4, 1, 7);
        mesh.getPoints().addAll(7, 1, 7);
        mesh.getPoints().addAll(7, 1, 4);

        mesh.getPoints().addAll(0, 1, 3);
        mesh.getPoints().addAll(3, 1, 3);

        mesh.getFaces().addAll(1, 0, 2, 0, 3, 0);
        mesh.getFaces().addAll(4, 0, 5, 0, 6, 0);
        mesh.getFaces().addAll(7, 0, 8, 0, 9, 0);
        
        assertEquals(8, mesh.getFaces().get(14));
        MeshUtils.removeDuplicateVertices(mesh);
        assertEquals(1, mesh.getFaces().get(14));

    }
    
    @Test
    public void testOrientableMesh()
    {
        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(0, 0, 0);
        mesh.getPoints().addAll(0, 1, 0);
        mesh.getPoints().addAll(1, 1, 0);
        mesh.getPoints().addAll(1, 0, 0);
        mesh.getPoints().addAll(0.5f, 0.5f, 0.5f);
        
        // square pyramid
        mesh.getFaces().addAll(0, 0, 4, 0, 1, 0);
        mesh.getFaces().addAll(1, 0, 4, 0, 2, 0);
        mesh.getFaces().addAll(3, 0, 2, 0, 4, 0);
        mesh.getFaces().addAll(0, 0, 3, 0, 4, 0);
        
        mesh.getFaces().addAll(0, 0, 1, 0, 2, 0);
        mesh.getFaces().addAll(0, 0, 2, 0, 3, 0);
        
        boolean orientable = MeshUtils.testMeshIsOrientable(mesh);
        assertTrue(orientable);

    }    
    
    @Test
    public void testNonOrientableMesh()
    {
        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(0, 0, 0);
        mesh.getPoints().addAll(0, 1, 0);
        mesh.getPoints().addAll(1, 1, 0);
        mesh.getPoints().addAll(1, 0, 0);
        mesh.getPoints().addAll(0.5f, 0.5f, 0.5f);
        
        // square pyramid
        mesh.getFaces().addAll(0, 0, 4, 0, 1, 0);
        mesh.getFaces().addAll(1, 0, 4, 0, 2, 0);
        mesh.getFaces().addAll(3, 0, 2, 0, 4, 0);
        mesh.getFaces().addAll(0, 0, 3, 0, 4, 0);
        
        mesh.getFaces().addAll(0, 0, 1, 0, 2, 0);
        // this face is the wrong way round
        mesh.getFaces().addAll(0, 0, 3, 0, 2, 0);
        
        boolean orientable = MeshUtils.testMeshIsOrientable(mesh);
        assertFalse(orientable);

    }    

}
