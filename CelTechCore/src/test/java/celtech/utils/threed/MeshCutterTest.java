///*
// * Copyright 2015 CEL UK
// */
//package celtech.utils.threed;
//
//import celtech.utils.threed.MeshCutter.BedToLocalConverter;
//import celtech.utils.threed.MeshCutter.Intersection;
//import celtech.utils.threed.MeshCutter.MeshPair;
//import static celtech.utils.threed.MeshCutter.getFaceIntersections;
//import static celtech.utils.threed.MeshCutter.getLoopsOfVertices;
//import static celtech.utils.threed.MeshCutter.getAdjacentIntersections;
//import static celtech.utils.threed.MeshSeparator.makeFacesWithVertex;
//import celtech.utils.threed.MeshUtils.MeshError;
//import celtech.utils.threed.importers.stl.STLFileParsingException;
//import celtech.utils.threed.importers.stl.STLImporter;
//import java.io.File;
//import java.net.URL;
//import java.util.Map;
//import java.util.Optional;
//import java.util.Set;
//import javafx.geometry.Point3D;
//import javafx.scene.shape.TriangleMesh;
//import org.junit.Assert;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//import org.junit.Test;
//
//
///**
// *
// * @author tony
// */
//public class MeshCutterTest
//{
//
//    public MeshCutterTest()
//    {
//    }
//
//    @Test
//    public void testCutCubeReturnsTwoMeshes() throws STLFileParsingException
//    {
//
//        URL stlURL = this.getClass().getResource("/simplecube.stl");
//        File singleObjectSTLFile = new File(stlURL.getFile());
//        TriangleMesh mesh = new STLImporter().processBinarySTLData(singleObjectSTLFile);
//        BedToLocalConverter nullBedToLocalConverter = makeNullConverter();
//
//        MeshPair meshes = MeshCutter.cut(mesh, -7, nullBedToLocalConverter);
//        Assert.assertNotNull(meshes.bottomMesh);
//        Assert.assertNotNull(meshes.topMesh);
//    }
//
//    @Test
//    public void testCutCubeWithHoleReturnsTwoMeshes() throws STLFileParsingException
//    {
//
//        URL stlURL = this.getClass().getResource("/cubewithhole.stl");
//        File singleObjectSTLFile = new File(stlURL.getFile());
//        TriangleMesh mesh = new STLImporter().processBinarySTLData(singleObjectSTLFile);
//        Optional<MeshError> error = MeshUtils.validate(mesh);
//        Assert.assertFalse(error.isPresent());
//
//        BedToLocalConverter nullBedToLocalConverter = makeNullConverter();
//
//        MeshPair meshes = MeshCutter.cut(mesh, -15, nullBedToLocalConverter);
//        Assert.assertNotNull(meshes.bottomMesh);
//        Assert.assertNotNull(meshes.topMesh);
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
//        BedToLocalConverter nullBedToLocalConverter = makeNullConverter();
//
//        MeshPair meshes = MeshCutter.cut(mesh, -20, nullBedToLocalConverter);
//        Assert.assertNotNull(meshes.bottomMesh);
//        Assert.assertNotNull(meshes.topMesh);
//    }
//    
//    @Test
//    public void testGetAdjacentIntersectionsForPointsOnCutPlane()
//    {
//        TriangleMesh mesh = createMeshWithPointsOnCutPlane();
//        
//        BedToLocalConverter nullBedToLocalConverter = makeNullConverter();
//        
//        boolean[] faceVisited = new boolean[mesh.getFaces().size() / 6];
//        Map<Integer, Set<Integer>> facesWithVertices = makeFacesWithVertex(mesh);
//    
//        Intersection intersection = new Intersection(3, Optional.empty(), 4);
//        
//        Set<Intersection> intersections = getAdjacentIntersections(intersection, mesh,
//                        1, nullBedToLocalConverter, facesWithVertices,  faceVisited);
//        assertEquals(3, intersections.size());
//    }
//    
//    @Test
//    public void testGetAdjacentIntersectionsForPointsOnCutPlane2()
//    {
//        TriangleMesh mesh = createMeshWithPointsOnCutPlane();
//        
//        BedToLocalConverter nullBedToLocalConverter = makeNullConverter();
//        
//        boolean[] faceVisited = new boolean[mesh.getFaces().size() / 6];
//        Map<Integer, Set<Integer>> facesWithVertices = makeFacesWithVertex(mesh);
//        
//        Intersection intersection = new Intersection(4, Optional.empty(), 6);
//        
//        Set<Intersection> intersections = getAdjacentIntersections(intersection, mesh,
//                        1, nullBedToLocalConverter, facesWithVertices,  faceVisited);
//        System.out.println("INTER " + intersections);
//    }
//
//    @Test
//    public void testMeshWithPointsOnCutPlane()
//    {
//        TriangleMesh mesh = createMeshWithPointsOnCutPlane();
//
//        Optional<MeshError> error = MeshUtils.validate(mesh);
//        assertTrue(!error.isPresent());
//
//        BedToLocalConverter nullBedToLocalConverter = makeNullConverter();
//
//        Set<MeshCutter.LoopOfVerticesAndCutFaces> cutFaces = getLoopsOfVertices(mesh, 1,
//                                                                                nullBedToLocalConverter);
//        System.out.println("loopsOfFaces" + cutFaces);
//
//        MeshPair meshes = MeshCutter.cut(mesh, 1, nullBedToLocalConverter);
//        Assert.assertNotNull(meshes.bottomMesh);
//        Assert.assertNotNull(meshes.topMesh);
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
//    public void testIntersectionEquality()
//    {
//        Edge edge = new Edge(100, 200);
//        Intersection intersection1 = new MeshCutter.Intersection(12, Optional.of(edge), 51);
//        Intersection intersection2 = new MeshCutter.Intersection(12, Optional.of(edge), 51);
//        assertEquals(intersection1, intersection2);
//
//        Intersection intersection3 = new MeshCutter.Intersection(12, Optional.of(edge), 52);
//        assertFalse(intersection1.equals(intersection3));
//
//        Edge edge2 = new Edge(100, 200);
//        Intersection intersection4 = new MeshCutter.Intersection(12, Optional.of(edge2), 51);
//        assertEquals(intersection1, intersection4);
//
//        Edge edge3 = new Edge(200, 200);
//        Intersection intersection5 = new MeshCutter.Intersection(12, Optional.of(edge3), 51);
//        assertFalse(intersection1.equals(intersection5));
//        
//        Intersection intersection6 = new MeshCutter.Intersection(12, Optional.empty(), 51);
//        Intersection intersection7 = new MeshCutter.Intersection(12, Optional.empty(), 51);
//        assertEquals(intersection6, intersection7);
//    }
//
//    @Test
//    public void testGetFaceIntersections()
//    {
//        TriangleMesh mesh = createSimpleCube();
//        BedToLocalConverter nullBedToLocalConverter = makeNullConverter();
//        Set<Intersection> intersections = MeshCutter.getFaceIntersections(5, mesh, 1f,
//                                                                          nullBedToLocalConverter);
//        assertEquals(2, intersections.size());
//        System.out.println(intersections);
//    }
//
//    @Test
//    public void testMeshWithNoPointsOnCutPlaneSimpleCube()
//    {
//        TriangleMesh mesh = createSimpleCube();
//
//        Optional<MeshError> error = MeshUtils.validate(mesh);
//        assertTrue(!error.isPresent());
//
//        BedToLocalConverter nullBedToLocalConverter = makeNullConverter();
//
//        float cutHeight = 1f;
//        Set<MeshCutter.LoopOfVerticesAndCutFaces> cutFaces = getLoopsOfVertices(mesh, cutHeight,
//                                                                                nullBedToLocalConverter);
//        assertEquals(1, cutFaces.size());
//
//        MeshPair meshes = MeshCutter.cut(mesh, cutHeight, nullBedToLocalConverter);
//        Assert.assertNotNull(meshes.bottomMesh);
//        Assert.assertNotNull(meshes.topMesh);
//
//    }
//
//    private BedToLocalConverter makeNullConverter()
//    {
//        BedToLocalConverter nullBedToLocalConverter = new BedToLocalConverter()
//        {
//
//            @Override
//            public Point3D localToBed(Point3D point)
//            {
//                return point;
//            }
//
//            @Override
//            public Point3D bedToLocal(Point3D point)
//            {
//                return point;
//            }
//        };
//        return nullBedToLocalConverter;
//    }
//
//    private TriangleMesh createSimpleCube()
//    {
//        TriangleMesh mesh = new TriangleMesh();
//        mesh.getPoints().addAll(0, 0, 0);
//        mesh.getPoints().addAll(0, 0, 2);
//        mesh.getPoints().addAll(2, 0, 2);
//        mesh.getPoints().addAll(2, 0, 0);
//        mesh.getPoints().addAll(0, 2, 0);
//        mesh.getPoints().addAll(0, 2, 2);
//        mesh.getPoints().addAll(2, 2, 2);
//        mesh.getPoints().addAll(2, 2, 0);
//        // one cube
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
//        mesh.getFaces().addAll(7, 0, 4, 0, 5, 0);
//        mesh.getFaces().addAll(7, 0, 5, 0, 6, 0);
//        return mesh;
//    }
//    
//    @Test
//    public void testgetFaceIntersectionsForTrianglesWithOneVertexOnPlane()
//    {
//        TriangleMesh mesh = createMeshWithOneVertexOnPlane();
//        BedToLocalConverter nullBedToLocalConverter = makeNullConverter();
//        
//        int faceIndex = 2;
//        Set<Intersection> intersections = getFaceIntersections(faceIndex, mesh,
//                                                         1,  nullBedToLocalConverter);
//        assertEquals(2, intersections.size());
//        for (Intersection intersection : intersections)
//        {
//            System.out.println(intersection);
//        }
//    }    
//    
//     @Test
//    public void testgetPossibleIntersectionsForTrianglesWithOneVertexOnPlane()
//    {
//        TriangleMesh mesh = createMeshWithOneVertexOnPlane();
//        BedToLocalConverter nullBedToLocalConverter = makeNullConverter();
//        
//        boolean[] faceVisited = new boolean[mesh.getFaces().size() / 6];
//        Map<Integer, Set<Integer>> facesWithVertices = makeFacesWithVertex(mesh);
//    
//        Intersection intersection = new Intersection(2, Optional.empty(), 7);
//        
//        Set<Intersection> intersections = getAdjacentIntersections(intersection, mesh,
//                        1, nullBedToLocalConverter, facesWithVertices,  faceVisited);
//        for (Intersection intersection2 : intersections)
//        {
//            System.out.println(intersection2);
//        }
//        }
//
//    @Test
//    public void testMeshWithTrianglesWithOneVertexOnPlane()
//    {
//        TriangleMesh mesh = createMeshWithOneVertexOnPlane();
//
//        Optional<MeshError> error = MeshUtils.validate(mesh);
//        assertTrue(!error.isPresent());
//
//        BedToLocalConverter nullBedToLocalConverter = makeNullConverter();
//
//        float cutHeight = 1f;
//        Set<MeshCutter.LoopOfVerticesAndCutFaces> cutFaces = getLoopsOfVertices(mesh, cutHeight,
//                                                                                nullBedToLocalConverter);
//        assertEquals(1, cutFaces.size());
//
//        MeshPair meshes = MeshCutter.cut(mesh, cutHeight, nullBedToLocalConverter);
//        Assert.assertNotNull(meshes.bottomMesh);
//        Assert.assertNotNull(meshes.topMesh);
//
//    }
//
//    private TriangleMesh createMeshWithOneVertexOnPlane()
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
//        // double height parallelepiped
//        mesh.getFaces().addAll(0, 0, 1, 0, 2, 0);
//        mesh.getFaces().addAll(0, 0, 2, 0, 3, 0);
//        mesh.getFaces().addAll(0, 0, 7, 0, 8, 0);
//        mesh.getFaces().addAll(0, 0, 3, 0, 7, 0);
//        mesh.getFaces().addAll(7, 0, 11, 0, 8, 0);
//        mesh.getFaces().addAll(2, 0, 10, 0, 7, 0);
//        mesh.getFaces().addAll(2, 0, 7, 0, 3, 0);
//        mesh.getFaces().addAll(7, 0, 10, 0, 11, 0);
//        mesh.getFaces().addAll(2, 0, 5, 0, 10, 0);
//        mesh.getFaces().addAll(2, 0, 1, 0, 5, 0);
//        mesh.getFaces().addAll(5, 0, 9, 0, 10, 0);
//        mesh.getFaces().addAll(0, 0, 8, 0, 5, 0);
//        mesh.getFaces().addAll(0, 0, 5, 0, 1, 0);
//        mesh.getFaces().addAll(8, 0, 9, 0, 5, 0);
//        mesh.getFaces().addAll(11, 0, 10, 0, 8, 0);
//        mesh.getFaces().addAll(8, 0, 10, 0, 9, 0);
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
//        Optional<MeshError> error = MeshUtils.validate(mesh);
//        Assert.assertFalse(error.isPresent());
//
//        BedToLocalConverter nullBedToLocalConverter = makeNullConverter();
//
//        MeshPair meshes = MeshCutter.cut(mesh, -1f, nullBedToLocalConverter);
//        Assert.assertNotNull(meshes.bottomMesh);
//        Assert.assertNotNull(meshes.topMesh);
//    }
//    
//    @Test
//    public void testEnricoSTLAt2() throws STLFileParsingException
//    {
//
//        URL stlURL = this.getClass().getResource("/enrico.stl");
//        File singleObjectSTLFile = new File(stlURL.getFile());
//        TriangleMesh mesh = new STLImporter().processBinarySTLData(singleObjectSTLFile);
//        Optional<MeshError> error = MeshUtils.validate(mesh);
//        Assert.assertFalse(error.isPresent());
//
//        BedToLocalConverter nullBedToLocalConverter = makeNullConverter();
//
//        MeshPair meshes = MeshCutter.cut(mesh, -2f, nullBedToLocalConverter);
//        Assert.assertNotNull(meshes.bottomMesh);
//        Assert.assertNotNull(meshes.topMesh);
//    }    
//
//    @Test
//    public void testEnricoSTLAt15() throws STLFileParsingException
//    {
//
//        URL stlURL = this.getClass().getResource("/enrico.stl");
//        File singleObjectSTLFile = new File(stlURL.getFile());
//        TriangleMesh mesh = new STLImporter().processBinarySTLData(singleObjectSTLFile);
//        Optional<MeshError> error = MeshUtils.validate(mesh);
//        Assert.assertFalse(error.isPresent());
//
//        BedToLocalConverter nullBedToLocalConverter = makeNullConverter();
//
//        MeshPair meshes = MeshCutter.cut(mesh, -1.5f, nullBedToLocalConverter);
//        Assert.assertNotNull(meshes.bottomMesh);
//        Assert.assertNotNull(meshes.topMesh);
//    }
//    
//    @Test
//    public void testEnricoSTLAt3() throws STLFileParsingException
//    {
//
//        URL stlURL = this.getClass().getResource("/enrico.stl");
//        File singleObjectSTLFile = new File(stlURL.getFile());
//        TriangleMesh mesh = new STLImporter().processBinarySTLData(singleObjectSTLFile);
//        Optional<MeshError> error = MeshUtils.validate(mesh);
//        Assert.assertFalse(error.isPresent());
//
//        BedToLocalConverter nullBedToLocalConverter = makeNullConverter();
//
//        MeshPair meshes = MeshCutter.cut(mesh, -3f, nullBedToLocalConverter);
//        Assert.assertNotNull(meshes.bottomMesh);
//        Assert.assertNotNull(meshes.topMesh);
//    }      
//    
//    @Test
//    public void testEnricoFaceIntersectionsFace1600at3() throws STLFileParsingException
//    {
//
//        URL stlURL = this.getClass().getResource("/enrico.stl");
//        File singleObjectSTLFile = new File(stlURL.getFile());
//        TriangleMesh mesh = new STLImporter().processBinarySTLData(singleObjectSTLFile);
//        Optional<MeshError> error = MeshUtils.validate(mesh);
//        Assert.assertFalse(error.isPresent());
//
//        BedToLocalConverter nullBedToLocalConverter = makeNullConverter();
//
//        MeshCutter.getFaceIntersections(1600, mesh, -3f, nullBedToLocalConverter);
//    }      
//
//}
