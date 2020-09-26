/*
 * Copyright 2015 CEL UK
 */
package celtech.utils.threed;

import celtech.utils.threed.MeshCutter2.BedToLocalConverter;
import static celtech.utils.threed.MeshUtils.copyMesh;
import static celtech.utils.threed.TriangleCutter.getFaceNormal;
import static celtech.utils.threed.TriangleCutter.reverseLastFaceNormal;
import static celtech.utils.threed.TriangleCutter.splitFaceAndAddLowerFacesToMesh;
import celtech.utils.threed.importers.stl.STLFileParsingException;
import celtech.utils.threed.importers.stl.STLImporter;
import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javafx.geometry.Point3D;
import javafx.scene.shape.TriangleMesh;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author tony
 */
public class TriangleCutterTest
{

    public static TriangleMesh createSimpleCube()
    {
        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(0, 0, 0);
        mesh.getPoints().addAll(0, 0, 2);
        mesh.getPoints().addAll(2, 0, 2);
        mesh.getPoints().addAll(2, 0, 0);
        mesh.getPoints().addAll(0, 2, 0);
        mesh.getPoints().addAll(0, 2, 2);
        mesh.getPoints().addAll(2, 2, 2);
        mesh.getPoints().addAll(2, 2, 0);
        // one cube
        mesh.getFaces().addAll(0, 0, 2, 0, 1, 0);
        mesh.getFaces().addAll(0, 0, 3, 0, 2, 0);
        mesh.getFaces().addAll(0, 0, 1, 0, 5, 0);
        mesh.getFaces().addAll(0, 0, 5, 0, 4, 0);
        mesh.getFaces().addAll(1, 0, 6, 0, 5, 0);
        mesh.getFaces().addAll(1, 0, 2, 0, 6, 0);
        mesh.getFaces().addAll(2, 0, 7, 0, 6, 0);
        mesh.getFaces().addAll(2, 0, 3, 0, 7, 0);
        mesh.getFaces().addAll(3, 0, 4, 0, 7, 0);
        mesh.getFaces().addAll(3, 0, 0, 0, 4, 0);
        mesh.getFaces().addAll(7, 0, 4, 0, 5, 0);
        mesh.getFaces().addAll(7, 0, 5, 0, 6, 0);
        return mesh;
    }

    public static TriangleMesh createMeshWithPointsOnCutPlane()
    {
        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(0, 0, 0);
        mesh.getPoints().addAll(0, 0, 1);
        mesh.getPoints().addAll(1, 0, 1);
        mesh.getPoints().addAll(1, 0, 0);
        mesh.getPoints().addAll(0, 1, 0);
        mesh.getPoints().addAll(0, 1, 1);
        mesh.getPoints().addAll(1, 1, 1);
        mesh.getPoints().addAll(1, 1, 0);
        mesh.getPoints().addAll(0, 2, 0);
        mesh.getPoints().addAll(0, 2, 1);
        mesh.getPoints().addAll(1, 2, 1);
        mesh.getPoints().addAll(1, 2, 0);
        // one cube upon another
        mesh.getFaces().addAll(0, 0, 2, 0, 1, 0);
        mesh.getFaces().addAll(0, 0, 3, 0, 2, 0);
        mesh.getFaces().addAll(0, 0, 1, 0, 5, 0);
        mesh.getFaces().addAll(0, 0, 5, 0, 4, 0);
        mesh.getFaces().addAll(1, 0, 6, 0, 5, 0);
        mesh.getFaces().addAll(1, 0, 2, 0, 6, 0);
        mesh.getFaces().addAll(2, 0, 7, 0, 6, 0);
        mesh.getFaces().addAll(2, 0, 3, 0, 7, 0);
        mesh.getFaces().addAll(3, 0, 4, 0, 7, 0);
        mesh.getFaces().addAll(3, 0, 0, 0, 4, 0);
        mesh.getFaces().addAll(4, 0, 5, 0, 9, 0);
        mesh.getFaces().addAll(4, 0, 9, 0, 8, 0);
        mesh.getFaces().addAll(5, 0, 10, 0, 9, 0);
        mesh.getFaces().addAll(5, 0, 6, 0, 10, 0);
        mesh.getFaces().addAll(6, 0, 11, 0, 10, 0);
        mesh.getFaces().addAll(6, 0, 7, 0, 11, 0);
        mesh.getFaces().addAll(7, 0, 8, 0, 11, 0);
        mesh.getFaces().addAll(7, 0, 4, 0, 8, 0);
        mesh.getFaces().addAll(11, 0, 8, 0, 10, 0);
        mesh.getFaces().addAll(8, 0, 9, 0, 10, 0);
        return mesh;
    }

    public static TriangleMesh createMeshWithOneVertexOnPlane()
    {
        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(0, 0, 0);
        mesh.getPoints().addAll(0, 0, 1);
        mesh.getPoints().addAll(1, 0, 1);
        mesh.getPoints().addAll(1, 0, 0);
        mesh.getPoints().addAll(0, 1, 0);
        mesh.getPoints().addAll(0, 1, 1);
        mesh.getPoints().addAll(1, 1, 1);
        mesh.getPoints().addAll(1, 1, 0);
        mesh.getPoints().addAll(0, 2, 0);
        mesh.getPoints().addAll(0, 2, 1);
        mesh.getPoints().addAll(1, 2, 1);
        mesh.getPoints().addAll(1, 2, 0);
        // double height parallelepiped
        mesh.getFaces().addAll(0, 0, 1, 0, 2, 0);
        mesh.getFaces().addAll(0, 0, 2, 0, 3, 0);
        mesh.getFaces().addAll(0, 0, 7, 0, 8, 0);
        mesh.getFaces().addAll(0, 0, 3, 0, 7, 0);
        mesh.getFaces().addAll(7, 0, 11, 0, 8, 0);
        mesh.getFaces().addAll(2, 0, 10, 0, 7, 0);
        mesh.getFaces().addAll(2, 0, 7, 0, 3, 0);
        mesh.getFaces().addAll(7, 0, 10, 0, 11, 0);
        mesh.getFaces().addAll(2, 0, 5, 0, 10, 0);
        mesh.getFaces().addAll(2, 0, 1, 0, 5, 0);
        mesh.getFaces().addAll(5, 0, 9, 0, 10, 0);
        mesh.getFaces().addAll(0, 0, 8, 0, 5, 0);
        mesh.getFaces().addAll(0, 0, 5, 0, 1, 0);
        mesh.getFaces().addAll(8, 0, 9, 0, 5, 0);
        mesh.getFaces().addAll(11, 0, 10, 0, 8, 0);
        mesh.getFaces().addAll(8, 0, 10, 0, 9, 0);
        return mesh;
    }

    public static MeshCutter2.BedToLocalConverter makeNullConverter()
    {
        MeshCutter2.BedToLocalConverter nullBedToLocalConverter = new MeshCutter2.BedToLocalConverter()
        {

            @Override
            public Point3D localToBed(Point3D point)
            {
                return point;
            }

            @Override
            public Point3D bedToLocal(Point3D point)
            {
                return point;
            }
        };
        return nullBedToLocalConverter;
    }

    @Test
    public void testTriangulateForFaceWithVertexVeryCloseToCutPlane()
    {
        TriangleMesh mesh = createMeshWithPointsOnCutPlane();

        TriangleMesh childMesh = copyMesh(mesh);

        for (float cutHeight = 0.999f; cutHeight < 1f; cutHeight += 0.000001f)
        {
            int faceIndex = 2;
            Set<Integer> facesToRemove = new HashSet<>();
            splitFaceAndAddLowerFacesToMesh(childMesh, facesToRemove,
                    faceIndex, cutHeight, makeNullConverter(),
                    MeshCutter2.TopBottom.BOTTOM);

        }

    }

    @Test
    public void testTriangulateRegularTriangleForFaceNotCutByPlane()
    {
        TriangleMesh mesh = createSimpleCube();

        TriangleMesh childMesh = copyMesh(mesh);

        float cutHeight = 1f;
        int faceIndex = 0;
        Set<Integer> facesToRemove = new HashSet<>();
        splitFaceAndAddLowerFacesToMesh(childMesh, facesToRemove,
                faceIndex, cutHeight, makeNullConverter(),
                MeshCutter2.TopBottom.BOTTOM);

        assertEquals(12, childMesh.getFaces().size() / 6);

    }

    @Test
    public void testReverseLastFaceNormal()
    {
        TriangleMesh mesh = createSimpleCube();
        Point3D normal = getFaceNormal(mesh, mesh.getFaces().size() / 6 - 1);
        reverseLastFaceNormal(mesh);
        Point3D newNormal = getFaceNormal(mesh, mesh.getFaces().size() / 6 - 1);
        System.out.println(normal);
        System.out.println(newNormal);
    }

    @Test
    public void testTriangulateRegularTriangleForFaceWithTwoEdgesCutByPlaneBottom()
    {
        TriangleMesh mesh = createSimpleCube();

        TriangleMesh childMesh = copyMesh(mesh);

        float cutHeight = 1f;
        int faceIndex = 2;
        Set<Integer> facesToRemove = new HashSet<>();
        splitFaceAndAddLowerFacesToMesh(childMesh, facesToRemove,
                faceIndex, cutHeight, makeNullConverter(),
                MeshCutter2.TopBottom.BOTTOM);

        assertEquals(13, childMesh.getFaces().size() / 6);

        int newFaceIndex = 12;
        int v0 = childMesh.getFaces().get(newFaceIndex * 6);
        int v1 = childMesh.getFaces().get(newFaceIndex * 6 + 2);
        int v2 = childMesh.getFaces().get(newFaceIndex * 6 + 4);

        assertEquals(5, v0);
        assertEquals(9, v1);
        assertEquals(8, v2);

    }

    @Test
    public void testTriangulateRegularTriangleForFaceWithTwoEdgesCutByPlaneTop()
    {
        TriangleMesh mesh = createSimpleCube();

        TriangleMesh childMesh = copyMesh(mesh);

        float cutHeight = 1f;
        int faceIndex = 2;
        Set<Integer> facesToRemove = new HashSet<>();
        splitFaceAndAddLowerFacesToMesh(childMesh, facesToRemove,
                faceIndex, cutHeight, makeNullConverter(),
                MeshCutter2.TopBottom.TOP);

        assertEquals(14, childMesh.getFaces().size() / 6);

        int newFaceIndex1 = 12;
        int v0 = childMesh.getFaces().get(newFaceIndex1 * 6);
        int v1 = childMesh.getFaces().get(newFaceIndex1 * 6 + 2);
        int v2 = childMesh.getFaces().get(newFaceIndex1 * 6 + 4);

        assertEquals(0, v0);
        assertEquals(1, v1);
        assertEquals(8, v2);

        int newFaceIndex2 = 13;
        v0 = childMesh.getFaces().get(newFaceIndex2 * 6);
        v1 = childMesh.getFaces().get(newFaceIndex2 * 6 + 2);
        v2 = childMesh.getFaces().get(newFaceIndex2 * 6 + 4);

        assertEquals(0, v0);
        assertEquals(8, v1);
        assertEquals(9, v2);

    }

    @Test
    public void testTriangulateForFaceWithOneVertexOnPlaneBottom()
    {
        TriangleMesh mesh = createMeshWithOneVertexOnPlane();

        TriangleMesh childMesh = copyMesh(mesh);
        assertEquals(16, childMesh.getFaces().size() / 6);

        float cutHeight = 1f;
        int faceIndex = 2;
        Set<Integer> facesToRemove = new HashSet<>();
        splitFaceAndAddLowerFacesToMesh(childMesh, facesToRemove,
                faceIndex, cutHeight, makeNullConverter(),
                MeshCutter2.TopBottom.BOTTOM);

        assertEquals(17, childMesh.getFaces().size() / 6);

        int newFaceIndex = 16;
        int v0 = childMesh.getFaces().get(newFaceIndex * 6);
        int v1 = childMesh.getFaces().get(newFaceIndex * 6 + 2);
        int v2 = childMesh.getFaces().get(newFaceIndex * 6 + 4);

        assertEquals(4, v0);
        assertEquals(7, v1);
        assertEquals(8, v2);

    }

    @Test
    public void testTriangulateForFaceWithOneVertexOnPlaneTop()
    {
        TriangleMesh mesh = createMeshWithOneVertexOnPlane();

        TriangleMesh childMesh = copyMesh(mesh);
        assertEquals(16, childMesh.getFaces().size() / 6);

        float cutHeight = 1f;
        int faceIndex = 2;
        Set<Integer> facesToRemove = new HashSet<>();
        splitFaceAndAddLowerFacesToMesh(childMesh, facesToRemove,
                faceIndex, cutHeight, makeNullConverter(),
                MeshCutter2.TopBottom.TOP);

        assertEquals(17, childMesh.getFaces().size() / 6);

        int newFaceIndex = 16;
        int v0 = childMesh.getFaces().get(newFaceIndex * 6);
        int v1 = childMesh.getFaces().get(newFaceIndex * 6 + 2);
        int v2 = childMesh.getFaces().get(newFaceIndex * 6 + 4);

        assertEquals(0, v0);
        assertEquals(7, v1);
        assertEquals(4, v2);

    }

    @Test
    public void testTriangulateRegularTriangleForFaceWithTwoEdgesCutByPlaneBottomBedConverterLateral()
    {
        BedToLocalConverter bedToLocalConverter = makeLateralConverter();

        TriangleMesh mesh = createSimpleCube();

        TriangleMesh childMesh = copyMesh(mesh);

        float cutHeight = 1f;
        int faceIndex = 2;
        Set<Integer> facesToRemove = new HashSet<>();
        splitFaceAndAddLowerFacesToMesh(childMesh, facesToRemove,
                faceIndex, cutHeight, bedToLocalConverter,
                MeshCutter2.TopBottom.BOTTOM);

        assertEquals(13, childMesh.getFaces().size() / 6);

        int newFaceIndex = 12;
        int v0 = childMesh.getFaces().get(newFaceIndex * 6);
        int v1 = childMesh.getFaces().get(newFaceIndex * 6 + 2);
        int v2 = childMesh.getFaces().get(newFaceIndex * 6 + 4);

        assertEquals(5, v0);
        assertEquals(9, v1);
        assertEquals(8, v2);

        // test new points
        Vertex v8 = TriangleCutter.getVertex(childMesh, 8);
        assertEquals(0, v8.x, 0.0001);
        assertEquals(1, v8.y, 0.0001);
        assertEquals(2, v8.z, 0.0001);

        Vertex v9 = TriangleCutter.getVertex(childMesh, 9);
        assertEquals(0, v9.x, 0.0001);
        assertEquals(1, v9.y, 0.0001);
        assertEquals(1, v9.z, 0.0001);

    }

    @Test
    public void testTriangulateRegularTriangleForFaceWithTwoEdgesCutByPlaneTopBedConverterLateral()
    {

        BedToLocalConverter bedToLocalConverter = makeLateralConverter();

        TriangleMesh mesh = createSimpleCube();

        TriangleMesh childMesh = copyMesh(mesh);

        float cutHeight = 1f;
        int faceIndex = 2;
        Set<Integer> facesToRemove = new HashSet<>();
        splitFaceAndAddLowerFacesToMesh(childMesh, facesToRemove,
                faceIndex, cutHeight, bedToLocalConverter,
                MeshCutter2.TopBottom.TOP);

        assertEquals(14, childMesh.getFaces().size() / 6);

        int newFaceIndex1 = 12;
        int v0 = childMesh.getFaces().get(newFaceIndex1 * 6);
        int v1 = childMesh.getFaces().get(newFaceIndex1 * 6 + 2);
        int v2 = childMesh.getFaces().get(newFaceIndex1 * 6 + 4);

        assertEquals(0, v0);
        assertEquals(1, v1);
        assertEquals(8, v2);

        int newFaceIndex2 = 13;
        v0 = childMesh.getFaces().get(newFaceIndex2 * 6);
        v1 = childMesh.getFaces().get(newFaceIndex2 * 6 + 2);
        v2 = childMesh.getFaces().get(newFaceIndex2 * 6 + 4);

        assertEquals(0, v0);
        assertEquals(8, v1);
        assertEquals(9, v2);

    }

    @Test
    public void testEnricoSTLAt1Face1612DuplicateVerticesCreated() throws STLFileParsingException
    {

        URL stlURL = this.getClass().getResource("/enrico.stl");
        File singleObjectSTLFile = new File(stlURL.getFile());
        TriangleMesh mesh = new STLImporter().processBinarySTLData(singleObjectSTLFile);
        Optional<MeshUtils.MeshError> error = MeshUtils.validate(mesh);
        Assert.assertFalse(error.isPresent());

        MeshCutter2.BedToLocalConverter nullBedToLocalConverter = makeNullConverter();

        TriangleMesh childMesh = copyMesh(mesh);

        float cutHeight = 1f;
        int faceIndex = 1612;
        Set<Integer> facesToRemove = new HashSet<>();
        splitFaceAndAddLowerFacesToMesh(childMesh, facesToRemove,
                faceIndex, cutHeight, nullBedToLocalConverter,
                MeshCutter2.TopBottom.TOP);
    }

    private BedToLocalConverter makeLateralConverter()
    {
        MeshCutter2.BedToLocalConverter bedToLocalConverter = new MeshCutter2.BedToLocalConverter()
        {

            @Override
            public Point3D localToBed(Point3D point)
            {
                return new Point3D(point.getX() + 1, point.getY(), point.getZ() + 2);
            }

            @Override
            public Point3D bedToLocal(Point3D point)
            {
                return new Point3D(point.getX() - 1, point.getY(), point.getZ() - 2);
            }
        };
        return bedToLocalConverter;
    }

}
