/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.utils.threed;

import static celtech.utils.threed.MeshCutter2.makePoint3D;
import static celtech.utils.threed.MeshSeparator.makeFacesWithVertex;
import static celtech.utils.threed.MeshSeparator.setTextureAndSmoothing;
import static celtech.utils.threed.TriangleCutter.getVertex;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javafx.collections.ObservableFloatArray;
import javafx.scene.shape.TriangleMesh;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;


/**
 *
 * @author alynch
 */
public class MeshUtils
{

    private static final Stenographer steno = StenographerFactory.getStenographer(
        MeshUtils.class.getName());

    /**
     * Remove vertices that are not used by any faces.
     */
    public static void removeUnusedAndDuplicateVertices(TriangleMesh childMesh)
    {

        removeUnusedVertices(childMesh);

        removeDuplicateVertices(childMesh);
    }

    private static void removeUnusedVertices(TriangleMesh childMesh)
    {
        // array of new vertex index for previous index
        int[] newVertexIndices = new int[childMesh.getPoints().size()];
        for (int i = 0; i < newVertexIndices.length; i++)
        {
            newVertexIndices[i] = -1;
        }
        float[] newPoints = new float[childMesh.getPoints().size()];
        int nextNewPointIndex = 0;

        for (int i = 0; i < childMesh.getFaces().size(); i += 2)
        {
            int vertexIndex = childMesh.getFaces().get(i);
            if (newVertexIndices[vertexIndex] == -1)
            {
                newVertexIndices[vertexIndex] = nextNewPointIndex;
                newPoints[nextNewPointIndex * 3] = childMesh.getPoints().get(vertexIndex * 3);
                newPoints[nextNewPointIndex * 3 + 1] = childMesh.getPoints().get(vertexIndex * 3 + 1);
                newPoints[nextNewPointIndex * 3 + 2] = childMesh.getPoints().get(vertexIndex * 3 + 2);
                nextNewPointIndex++;
            }
            childMesh.getFaces().set(i, newVertexIndices[vertexIndex]);
        }

        childMesh.getPoints().clear();
        childMesh.getPoints().addAll(newPoints, 0, nextNewPointIndex * 3);
    }

    static void removeDuplicateVertices(TriangleMesh mesh)
    {
        Map<Integer, Integer> vertexReplacements = new HashMap<>();
        Map<Vertex, Integer> vertexToVertex = new HashMap<>();
        for (int vertexIndex = 0; vertexIndex < mesh.getPoints().size() / 3; vertexIndex++)
        {
            Vertex vertex = TriangleCutter.getVertex(mesh, vertexIndex);

            if (vertexToVertex.containsKey(vertex))
            {
                vertexReplacements.put(vertexIndex, vertexToVertex.get(vertex));
            } else
            {
                vertexToVertex.put(vertex, vertexIndex);
            }
        }
        replaceVertices(mesh, vertexReplacements);
    }

    static TriangleMesh copyMesh(TriangleMesh mesh)
    {
        TriangleMesh childMesh = new TriangleMesh();
        childMesh.getPoints().addAll(mesh.getPoints());
        childMesh.getFaces().addAll(mesh.getFaces());
        setTextureAndSmoothing(childMesh, childMesh.getFaces().size() / 6);
        return childMesh;
    }

    /**
     * Replace uses of vertex fromVertex (key) with toVertex (value).
     */
    private static void replaceVertices(TriangleMesh mesh, Map<Integer, Integer> vertexReplacements)
    {
        for (int faceIndex = 0; faceIndex < mesh.getFaces().size(); faceIndex += 2)
        {
            if (vertexReplacements.containsKey(mesh.getFaces().get(faceIndex)))
            {
                mesh.getFaces().set(faceIndex,
                                    vertexReplacements.get(mesh.getFaces().get(faceIndex)));
            }
        }
    }


    public enum MeshError
    {

        INVALID_VERTEX_ID, OPEN_MESH, MESH_NOT_ORIENTABLE;
    }

    public static Optional<MeshError> validate(TriangleMesh mesh)
    {
        return validate(mesh, null);
    }

    /**
     * Validate the mesh.
     */
    public static Optional<MeshError> validate(TriangleMesh mesh,
        MeshCutter2.BedToLocalConverter bedToLocalConverter)
    {
        if (testVerticesNotValid(mesh))
        {
            return Optional.of(MeshError.INVALID_VERTEX_ID);
        }

        if (testMeshIsOpen(mesh, bedToLocalConverter))
        {
            return Optional.of(MeshError.OPEN_MESH);
        }

        // quickly validate mesh is orientable (winding order correct for all faces)
        if (!testMeshIsOrientable(mesh))
        {
            return Optional.of(MeshError.MESH_NOT_ORIENTABLE);
        }

        steno.debug("check passed");

        return Optional.empty();
    }

    private static boolean testVerticesNotValid(TriangleMesh mesh)
    {
        // validate vertex indices
        int numVertices = mesh.getPoints().size() / 3;
        for (int i = 0; i < mesh.getFaces().size(); i += 2)
        {
            int vertexIndex = mesh.getFaces().get(i);
            if (vertexIndex < 0 || vertexIndex > numVertices)
            {
                return true;
            }
        }
        return false;
    }
    
        static Set<Edge> getFaceEdges(TriangleMesh mesh, int faceIndex)
    {
        int vertex0 = mesh.getFaces().get(faceIndex * 6);
        int vertex1 = mesh.getFaces().get(faceIndex * 6 + 2);
        int vertex2 = mesh.getFaces().get(faceIndex * 6 + 4);
        Edge edge1 = new Edge(vertex0, vertex1);
        Edge edge2 = new Edge(vertex1, vertex2);
        Edge edge3 = new Edge(vertex0, vertex2);
        Set<Edge> edges = new HashSet<>();
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        return edges;
    }
        
        
    /**
     * Test that the mesh is orientable. This means that the winding order for each face is
     * consistent with its neighbouring faces.
     */
    static boolean testMeshIsOrientable(TriangleMesh mesh) {
        Map<Integer, Set<Integer>> facesWithVertices = makeFacesWithVertex(mesh);
        return testMeshIsOrientable(mesh, facesWithVertices);
    }    

    /**
     * Test that the mesh is orientable. This means that the winding order for each face is
     * consistent with its neighbouring faces.
     */
    static boolean testMeshIsOrientable(TriangleMesh mesh, Map<Integer, Set<Integer>> facesWithVertices)
    {
        /**
         * Check that for every edge the opposing faces are oriented correctly.
         */
        
        Set<Edge> processedEdges = new HashSet<>();

        boolean warningEmitted = false;

        for (int faceIndex = 0; faceIndex < mesh.getFaces().size() / 6; faceIndex++)
        {
            Set<Edge> edges = getFaceEdges(mesh, faceIndex);
            for (Edge edge : edges)
            {
                if (processedEdges.contains(edge))
                {
                    continue;
                }
                processedEdges.add(edge);
                Set<Integer> facesForVertex0 = facesWithVertices.get(edge.v0);
                Set<Integer> facesWithv0 = new HashSet<>(facesForVertex0);
                Set<Integer> facesWithv1 = facesWithVertices.get(edge.v1);
                facesWithv0.retainAll(facesWithv1);
                facesWithv0.remove(faceIndex);
                if (facesWithv0.size() != 1)
                {
                    if (!warningEmitted)
                    {
                        steno.warning("Invalid topology while checking orientability");
                        warningEmitted = true;
                    }
                    continue;
//                    return false;
                }
                // we should now have the face on other side of the edge
                int opposingFaceIndex = facesWithv0.iterator().next();
                assert facesWithv0.size() == 1;
                if (!checkOrientationCompatible(mesh, faceIndex, opposingFaceIndex, edge))
                {
                    steno.debug("fails for faces " + faceIndex + " " + opposingFaceIndex
                        + " edge " + edge.v0 + " " + edge.v1);
                    return false;
                }
            }
        }
        return true;
    }


    enum Orientation
    {

        FORWARDS, BACKWARDS;
    }

    /**
     * Check that the winding order of the two faces is compatible with each other.
     */
    private static boolean checkOrientationCompatible(TriangleMesh mesh, int faceIndex1,
        int faceIndex2,
        Edge edge)
    {
        Orientation face1Orientation = getOrientation(mesh, faceIndex1, edge);
        Orientation face2Orientation = getOrientation(mesh, faceIndex2, edge);
        return face1Orientation != face2Orientation;
    }

    /**
     * If the vertices of the face are eg v0,v1,v2 then the edge v0,v1 is FORWARDS whereas the edge
     * v1,v0 is BACKWARDS. Note that v2,v0 is also FORWARDS and v0,v2 is BACKWARDS.
     */
    private static Orientation getOrientation(TriangleMesh mesh, int faceIndex, Edge edge)
    {
        int v0 = mesh.getFaces().get(faceIndex * 6);
        int v1 = mesh.getFaces().get(faceIndex * 6 + 2);
        int v2 = mesh.getFaces().get(faceIndex * 6 + 4);

        if (edge.v0 == v0 && edge.v1 == v1)
        {
            return Orientation.FORWARDS;
        }
        if (edge.v0 == v1 && edge.v1 == v2)
        {
            return Orientation.FORWARDS;
        }
        if (edge.v0 == v2 && edge.v1 == v0)
        {
            return Orientation.FORWARDS;
        }
        return Orientation.BACKWARDS;
    }

    /**
     * Check if mesh is open (not all edges are incident to two faces).
     */
    private static boolean testMeshIsOpen(TriangleMesh mesh,
        MeshCutter2.BedToLocalConverter bedToLocalConverter)
    {
      
        Map<Integer, Set<Integer>> facesWithVertices = makeFacesWithVertex(mesh);

        for (int faceIndex = 0; faceIndex < mesh.getFaces().size() / 6; faceIndex++)
        {
            if (countFacesAdjacentToVertices(mesh, facesWithVertices, faceIndex, 0, 1) != 1)
            {
                steno.debug("problem 01 for face " + faceIndex);
                printFace(mesh, faceIndex, bedToLocalConverter);
                int v0 = mesh.getFaces().get(faceIndex * 6);
                int v1 = mesh.getFaces().get(faceIndex * 6 + 2);
                Vertex vertex0 = getVertex(mesh, v0);
                Vertex vertex1 = getVertex(mesh, v1);
                MeshDebug.clearNodesToShow();
                MeshDebug.showSphere(vertex0.x, vertex0.y, vertex0.z);
                MeshDebug.showSphere(vertex1.x, vertex1.y, vertex1.z);
                
                return true;
            }
            if (countFacesAdjacentToVertices(mesh, facesWithVertices, faceIndex, 1, 2) != 1)
            {
                steno.debug("problem 12 for face " + faceIndex);
                printFace(mesh, faceIndex, bedToLocalConverter);
                int v1 = mesh.getFaces().get(faceIndex * 6 + 2);
                int v2 = mesh.getFaces().get(faceIndex * 6 + 4);
                Vertex vertex1 = getVertex(mesh, v1);
                Vertex vertex2 = getVertex(mesh, v2);
                MeshDebug.clearNodesToShow();
                MeshDebug.showSphere(vertex2.x, vertex2.y, vertex2.z);
                MeshDebug.showSphere(vertex1.x, vertex1.y, vertex1.z);
                return true;
            }
            if (countFacesAdjacentToVertices(mesh, facesWithVertices, faceIndex, 0, 2) != 1)
            {
                steno.debug("problem 02 for face " + faceIndex);
                printFace(mesh, faceIndex, bedToLocalConverter);
                return true;
            }
        }
        return false;
    }
    
    public static void printFace(TriangleMesh mesh, int faceIndex,
        MeshCutter2.BedToLocalConverter bedToLocalConverter)
    {
        int v0 = mesh.getFaces().get(faceIndex * 6);
        int v1 = mesh.getFaces().get(faceIndex * 6 + 2);
        int v2 = mesh.getFaces().get(faceIndex * 6 + 4);
        steno.debug("v0Local " + getVertex(mesh, v0));
        steno.debug("v1Local " + getVertex(mesh, v1));
        steno.debug("v2Local " + getVertex(mesh, v2));
        
        if (bedToLocalConverter != null)
        {
            steno.debug("v0 " + bedToLocalConverter.localToBed(makePoint3D(mesh, v0)));
            steno.debug("v1 " + bedToLocalConverter.localToBed(makePoint3D(mesh, v1)));
            steno.debug("v2 " + bedToLocalConverter.localToBed(makePoint3D(mesh, v2)));
        }
    }

    public static int countFacesAdjacentToVertices(TriangleMesh mesh,
        Map<Integer, Set<Integer>> facesWithVertices,
        int faceIndex, int vertexIndexOffset0, int vertexIndexOffset1)
    {
        Set<Integer> facesWithVertex0 = new HashSet(facesWithVertices.get(
            mesh.getFaces().get(faceIndex * 6 + vertexIndexOffset0 * 2)));

        Set<Integer> facesWithVertex1 = facesWithVertices.get(
            mesh.getFaces().get(faceIndex * 6 + vertexIndexOffset1 * 2));
        facesWithVertex0.remove(faceIndex);
        facesWithVertex0.retainAll(facesWithVertex1);
        return facesWithVertex0.size();
    }

}
