/*
 * Copyright 2015 CEL UK
 */
package celtech.utils.threed;

import static celtech.utils.threed.MeshCutter2.makeSplitMesh;
import celtech.utils.threed.MeshUtils.MeshError;
import static celtech.utils.threed.TriangleCutterTest.makeNullConverter;
import celtech.utils.threed.importers.stl.STLFileParsingException;
import celtech.utils.threed.importers.stl.STLImporter;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.scene.shape.TriangleMesh;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author tony
 */
public class MeshCutter2Test
{

//    @Test
//    public void testCutSimpleCube() {
//        TriangleMesh mesh = TriangleCutterTest.createSimpleCube();
//        assertEquals(12, mesh.getFaces().size() / 6);
//
//        float cutHeight = 1f;
//        MeshCutter2.BedToLocalConverter nullBedToLocalConverter = makeNullConverter();
//
//        List<TriangleMesh> meshes = MeshCutter2.cut(mesh, cutHeight, nullBedToLocalConverter);
//        Assert.assertNotNull(meshes.get(0));
//        Assert.assertNotNull(meshes.get(1));
//
//    }
//    @Test
//    public void testMakeSplitMeshSimpleCubeBottom()
//    {
//        TriangleMesh mesh = TriangleCutterTest.createSimpleCube();
//        assertEquals(12, mesh.getFaces().size() / 6);
//
//        float cutHeight = 1f;
//        TriangleMesh childMesh = makeSplitMesh(mesh, cutHeight,
//                TriangleCutterTest.makeNullConverter(),
//                MeshCutter2.TopBottom.BOTTOM);
//
//        assertEquals(14, childMesh.getFaces().size() / 6);
//    }
//
//    @Test
//    public void testMakeSplitMeshSimpleCubeTop()
//    {
//        TriangleMesh mesh = TriangleCutterTest.createSimpleCube();
//        assertEquals(12, mesh.getFaces().size() / 6);
//
//        float cutHeight = 1f;
//        TriangleMesh childMesh = makeSplitMesh(mesh, cutHeight,
//                TriangleCutterTest.makeNullConverter(),
//                MeshCutter2.TopBottom.TOP);
//
//        assertEquals(14, childMesh.getFaces().size() / 6);
//    }
//
//    @Test
//    public void testCutCubeAlongMeshingLineReturnsTwoMeshes() throws STLFileParsingException
//    {
//
//        // this stl is meshed so that many vertices lie along Y=20
//        URL stlURL = this.getClass().getResource("/onecubeabovetheother_remeshed.stl");
//        File singleObjectSTLFile = new File(stlURL.getFile());
//        TriangleMesh mesh = new STLImporter().processBinarySTLData(singleObjectSTLFile);
//        MeshCutter2.BedToLocalConverter nullBedToLocalConverter = makeNullConverter();
//
//        List<TriangleMesh> meshes = MeshCutter2.cut(mesh, -20, nullBedToLocalConverter);
//        Assert.assertNotNull(meshes.get(0));
//        Assert.assertNotNull(meshes.get(1));
//    }
//
//    @Test
//    public void testMeshWithPointsOnCutPlane()
//    {
////        TriangleMesh mesh = createMeshWithPointsOnCutPlane();
////
////        Optional<MeshError> error = MeshUtils.validate(mesh);
////        assertTrue(!error.isPresent());
////
////        MeshCutter2.BedToLocalConverter nullBedToLocalConverter = makeNullConverter();
////
////        List<TriangleMesh> meshes = MeshCutter2.cut(mesh, 1, nullBedToLocalConverter);
////        Assert.assertNotNull(meshes.get(0));
////        Assert.assertNotNull(meshes.get(1));
//
//    }
//
//    private TriangleMesh createMeshWithPointsOnCutPlane()
//    {
//        TriangleMesh mesh = new TriangleMesh();
//        mesh.getPoints().addAll(0, 0, 0);
//        mesh.getPoints().addAll(0, 0, 1);
//        mesh.getPoints().addAll(1, 0, 1);
//        mesh.getPoints().addAll(1, 0, 0);
//        mesh.getPoints().addAll(0, 1, 0);
//        mesh.getPoints().addAll(0, 1, 1);
//        mesh.getPoints().addAll(1, 1, 1);
//        mesh.getPoints().addAll(1, 1, 0);
//        mesh.getPoints().addAll(0, 2, 0);
//        mesh.getPoints().addAll(0, 2, 1);
//        mesh.getPoints().addAll(1, 2, 1);
//        mesh.getPoints().addAll(1, 2, 0);
//        // one cube upon another
//        mesh.getFaces().addAll(0, 0, 2, 0, 1, 0);
//        mesh.getFaces().addAll(0, 0, 3, 0, 2, 0);
//        mesh.getFaces().addAll(0, 0, 1, 0, 5, 0);
//        mesh.getFaces().addAll(0, 0, 5, 0, 4, 0);
//        mesh.getFaces().addAll(1, 0, 6, 0, 5, 0);
//        mesh.getFaces().addAll(1, 0, 2, 0, 6, 0);
//        mesh.getFaces().addAll(2, 0, 7, 0, 6, 0);
//        mesh.getFaces().addAll(2, 0, 3, 0, 7, 0);
//        mesh.getFaces().addAll(3, 0, 4, 0, 7, 0);
//        mesh.getFaces().addAll(3, 0, 0, 0, 4, 0);
//        mesh.getFaces().addAll(4, 0, 5, 0, 9, 0);
//        mesh.getFaces().addAll(4, 0, 9, 0, 8, 0);
//        mesh.getFaces().addAll(5, 0, 10, 0, 9, 0);
//        mesh.getFaces().addAll(5, 0, 6, 0, 10, 0);
//        mesh.getFaces().addAll(6, 0, 11, 0, 10, 0);
//        mesh.getFaces().addAll(6, 0, 7, 0, 11, 0);
//        mesh.getFaces().addAll(7, 0, 8, 0, 11, 0);
//        mesh.getFaces().addAll(7, 0, 4, 0, 8, 0);
//        mesh.getFaces().addAll(11, 0, 8, 0, 10, 0);
//        mesh.getFaces().addAll(8, 0, 9, 0, 10, 0);
//        return mesh;
//    }
//
//    @Test
//    public void testEnricoSTLAt1() throws STLFileParsingException
//    {
//
//        URL stlURL = this.getClass().getResource("/enrico.stl");
//        File singleObjectSTLFile = new File(stlURL.getFile());
//        TriangleMesh mesh = new STLImporter().processBinarySTLData(singleObjectSTLFile);
//        Optional<MeshUtils.MeshError> error = MeshUtils.validate(mesh);
//        Assert.assertFalse(error.isPresent());
//
//        MeshCutter2.BedToLocalConverter nullBedToLocalConverter = makeNullConverter();
//
//        List<TriangleMesh> meshes = MeshCutter2.cut(mesh, -1f, nullBedToLocalConverter);
//        Assert.assertNotNull(meshes.get(0));
//        Assert.assertNotNull(meshes.get(1));
//    }
//
//    @Test
//    public void testEnricoSTLAt3p8() throws STLFileParsingException
//    {
//
//        URL stlURL = this.getClass().getResource("/enrico.stl");
//        File singleObjectSTLFile = new File(stlURL.getFile());
//        TriangleMesh mesh = new STLImporter().processBinarySTLData(singleObjectSTLFile);
//        Optional<MeshUtils.MeshError> error = MeshUtils.validate(mesh);
//        Assert.assertFalse(error.isPresent());
//
//        MeshCutter2.BedToLocalConverter nullBedToLocalConverter = makeNullConverter();
//
//        List<TriangleMesh> meshes = MeshCutter2.cut(mesh, -3.8f, nullBedToLocalConverter);
//        Assert.assertNotNull(meshes.get(0));
//        Assert.assertNotNull(meshes.get(1));
//    }
//
//    @Test
//    public void testEnricoSTLAt2() throws STLFileParsingException
//    {
//
//        URL stlURL = this.getClass().getResource("/enrico.stl");
//        File singleObjectSTLFile = new File(stlURL.getFile());
//        TriangleMesh mesh = new STLImporter().processBinarySTLData(singleObjectSTLFile);
//        Optional<MeshUtils.MeshError> error = MeshUtils.validate(mesh);
//        Assert.assertFalse(error.isPresent());
//
//        MeshCutter2.BedToLocalConverter nullBedToLocalConverter = makeNullConverter();
//
//        List<TriangleMesh> meshes = MeshCutter2.cut(mesh, -2f, nullBedToLocalConverter);
//        Assert.assertNotNull(meshes.get(0));
//        Assert.assertNotNull(meshes.get(1));
//    }
//
////    @Test
////    public void testEnricoSTLAt15() throws STLFileParsingException {
////
////        URL stlURL = this.getClass().getResource("/enrico.stl");
////        File singleObjectSTLFile = new File(stlURL.getFile());
////        TriangleMesh mesh = new STLImporter().processBinarySTLData(singleObjectSTLFile);
////        Optional<MeshUtils.MeshError> error = MeshUtils.validate(mesh);
////        Assert.assertFalse(error.isPresent());
////
////        MeshCutter2.BedToLocalConverter nullBedToLocalConverter = makeNullConverter();
////
////        List<TriangleMesh> meshes = MeshCutter2.cut(mesh, -1.5f, nullBedToLocalConverter);
////        Assert.assertNotNull(meshes.get(0));
////        Assert.assertNotNull(meshes.get(1));
////    }
//    @Test
//    public void testEnricoSTLAt3() throws STLFileParsingException
//    {
//
//        URL stlURL = this.getClass().getResource("/enrico.stl");
//        File singleObjectSTLFile = new File(stlURL.getFile());
//        TriangleMesh mesh = new STLImporter().processBinarySTLData(singleObjectSTLFile);
//        Optional<MeshUtils.MeshError> error = MeshUtils.validate(mesh);
//        Assert.assertFalse(error.isPresent());
//
//        MeshCutter2.BedToLocalConverter nullBedToLocalConverter = makeNullConverter();
//
//        List<TriangleMesh> meshes = MeshCutter2.cut(mesh, -3f, nullBedToLocalConverter);
//        Assert.assertNotNull(meshes.get(0));
//        Assert.assertNotNull(meshes.get(1));
//    }
//
//    @Test
//    public void testConvertEdgesToPolygonIndices()
//    {
//
//        ManifoldEdge edge0 = new ManifoldEdge(9, 8, null, null, 3);
//        ManifoldEdge edge1 = new ManifoldEdge(10, 9, null, null, 3);
//        ManifoldEdge edge2 = new ManifoldEdge(11, 10, null, null, 3);
//        ManifoldEdge edge3 = new ManifoldEdge(11, 12, null, null, 3);
//        ManifoldEdge edge4 = new ManifoldEdge(12, 13, null, null, 3);
//        ManifoldEdge edge5 = new ManifoldEdge(14, 13, null, null, 3);
//        ManifoldEdge edge6 = new ManifoldEdge(15, 14, null, null, 3);
//        ManifoldEdge edge7 = new ManifoldEdge(15, 16, null, null, 3);
//        List<ManifoldEdge> loop = new ArrayList<>();
//        loop.add(edge0);
//        loop.add(edge1);
//        loop.add(edge2);
//        loop.add(edge3);
//        loop.add(edge4);
//        loop.add(edge5);
//        loop.add(edge6);
//        loop.add(edge7);
//
//        PolygonIndices polygonIndices = MeshCutter2.convertEdgesToPolygonIndices(loop).getFirst();
//        System.out.println(polygonIndices);
//        PolygonIndices expectedPolygonIndices = new PolygonIndices();
//        expectedPolygonIndices.add(8);
//        expectedPolygonIndices.add(9);
//        expectedPolygonIndices.add(10);
//        expectedPolygonIndices.add(11);
//        expectedPolygonIndices.add(12);
//        expectedPolygonIndices.add(13);
//        expectedPolygonIndices.add(14);
//        expectedPolygonIndices.add(15);
//        expectedPolygonIndices.add(16);
//
//        assertEquals(expectedPolygonIndices, polygonIndices);
//
//    }

}
