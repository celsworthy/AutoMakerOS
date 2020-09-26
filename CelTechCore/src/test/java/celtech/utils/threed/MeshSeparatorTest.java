/*
 * Copyright 2015 CEL UK
 */
package celtech.utils.threed;

import celtech.utils.threed.importers.stl.STLFileParsingException;
import celtech.utils.threed.importers.stl.STLImporter;
import java.io.File;
import java.net.URL;
import java.util.List;
import javafx.scene.shape.TriangleMesh;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import org.junit.Test;

/**
 *
 * @author tony
 */
public class MeshSeparatorTest
{

    @Test
    public void testMeshOfOneObject() throws STLFileParsingException
    {
        URL stlURL = this.getClass().getResource("/pyramid1.stl");
        File singleObjectSTLFile = new File(stlURL.getFile());
        TriangleMesh mesh = new STLImporter().processBinarySTLData(singleObjectSTLFile);
        List<TriangleMesh> meshes = MeshSeparator.separate(mesh);    
        assertEquals(1, meshes.size());
        assertSame(mesh, meshes.get(0));
    }
    
    @Test
    public void testMeshOfTwoObjects() throws STLFileParsingException
    {
        URL stlURL = this.getClass().getResource("/twodiscs.stl");
        File singleObjectSTLFile = new File(stlURL.getFile());
        TriangleMesh mesh = new STLImporter().processBinarySTLData(singleObjectSTLFile);
        List<TriangleMesh> meshes = MeshSeparator.separate(mesh);    
        assertEquals(2, meshes.size());
        // each sub mesh should have 176 faces
        assertEquals(176, meshes.get(0).getFaces().size() / 6);
        assertEquals(176, meshes.get(1).getFaces().size() / 6);
    }    
    
}
