/*
 * Copyright 2015 CEL UK
 */
package celtech.utils.threed;

import celtech.coreUI.visualisation.metaparts.FloatArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.scene.shape.TriangleMesh;

/**
 * MeshSeparator takes an input {@link javafx.scene.shape.TriangleMesh} and returns multiple
 * TriangleMeshes according to the number of separate objects (non-joined) in the input mesh. The
 * concept of 'joining' is based on having a common vertex.
 *
 * @author tony
 */
public class MeshSeparator
{

    /**
     * Separate the given mesh into multiple meshes according to the number of separate (non-joined)
     * objects in the given mesh. For first vertex:.
     * <p>
     * 1) Create a new face group.</p><p>
     * 2) Find all faces using that vertex. Put into the group. Mark vertex and faces as done.
     * </p><p>
     * 3) For each face found, process vertices of the faces. Mark faces and vertices as done.
     * </p><p>
     * 4) Continue until all found connected faces / vertices are already marked. </p><p>
     * The face group now contains the indices of all faces in the first found object.</p><p>
     * Then </p><p>
     * 5) Find first unmarked vertex. Create a new face group. Continue as before. Repeat until
     * there are no unmarked vertices left. We have a number of groups. Each group is a separate
     * object.</p>
     */
    public static List<TriangleMesh> separate(TriangleMesh mesh)
    {
        List<TriangleMesh> meshes = new ArrayList<>();

        boolean[] vertexVisited = new boolean[mesh.getPoints().size() / 3];
        boolean[] faceVisited = new boolean[mesh.getFaces().size() / 6];
        
        Map<Integer, Set<Integer>> facesWithVertex = makeFacesWithVertex(mesh);

        while (true)
        {
            int startVertex = findFirstUnvisitedVertex(vertexVisited);
            if (startVertex == -1)
            {
                break;
            }
            Set<Integer> faceGroup = new HashSet<>();
            visitVertex(facesWithVertex, faceGroup, mesh, vertexVisited, faceVisited, startVertex);

            if (faceGroup.size() == mesh.getFaces().size() / 6) {
                // there is only one part, don't bother recreating it with makeSubMesh
                meshes.add(mesh);
                break;
            }
            TriangleMesh subMesh = makeSubMesh(mesh, faceGroup);
            meshes.add(subMesh);
        }

        return meshes;
    }

    /**
     * Find the index of the first vertex where visited is false. If all vertices have been visited
     * then return -1.
     */
    private static int findFirstUnvisitedVertex(boolean[] vertexVisited)
    {
        for (int i = 0; i < vertexVisited.length; i++)
        {
            if (!vertexVisited[i])
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * Make a sub mesh from the given mesh, composed of all the faces found in the given faceGroup.
     * The faceGroup is the set of the indices of the faces.
     */
    private static TriangleMesh makeSubMesh(TriangleMesh mesh, Set<Integer> faceGroup)
    {
        TriangleMesh subMesh = new TriangleMesh();

        int numFaces = faceGroup.size();
        Set<Integer> vertices = getUsedVertices(mesh, faceGroup);
        // The nth element of this list contains the vertex index of the same point in the submesh
        int[] newVertexIndices = new int[mesh.getPoints().size()];

        int ix = 0;
        for (Integer vertex : vertices)
        {
            // vertex is the index of the vertex in mesh and ix is the index of the vertex in the submesh
            addPointToMesh(mesh, vertex, subMesh);
            newVertexIndices[vertex] = ix;
            ix++;
        }
        
        for (Integer face : faceGroup) {
            addFaceToMesh(mesh, face, subMesh, newVertexIndices);
        }

        setTextureAndSmoothing(subMesh, numFaces);
        return subMesh;
    }
    
    /**
     * Add the face of the given index in mesh to the sub mesh, using the vertex mapping given in
     * newVertexIndices.
     */
    private static void addFaceToMesh(TriangleMesh mesh, int faceIndex, TriangleMesh subMesh,
        int[] newVertexIndices)
    {
        int[] vertices = new int[6];
        int originalVertex0 = mesh.getFaces().get(faceIndex * 6);
        vertices[0] = newVertexIndices[originalVertex0];
        int originalVertex1 = mesh.getFaces().get(faceIndex * 6 + 2);
        vertices[2] = newVertexIndices[originalVertex1];
        int originalVertex2 = mesh.getFaces().get(faceIndex * 6 + 4);
        vertices[4] = newVertexIndices[originalVertex2];  
        subMesh.getFaces().addAll(vertices);
    }
    

    /**
     * Add the vertex details of the given vertex from the parent mesh to the sub mesh.
     */
    public static void addPointToMesh(TriangleMesh parentMesh, Integer vertex, TriangleMesh subMesh)
    {
        subMesh.getPoints().addAll(parentMesh.getPoints(), vertex * 3, 3);
    }

    /**
     * Get the vertices being used by the given faces.
     */
    private static Set<Integer> getUsedVertices(TriangleMesh mesh, Set<Integer> faceGroup)
    {
        Set<Integer> vertices = new HashSet<>();
        for (Integer faceIndex : faceGroup)
        {
            vertices.add(mesh.getFaces().get(faceIndex * 6));
            vertices.add(mesh.getFaces().get(faceIndex * 6 + 2));
            vertices.add(mesh.getFaces().get(faceIndex * 6 + 4));
        }
        return vertices;
    }

    /**
     * Add the boilerplate texture and smoothing data to the given mesh.
     */
    public static void setTextureAndSmoothing(TriangleMesh mesh, int numFaces)
    {
        FloatArrayList texCoords = new FloatArrayList();
        texCoords.add(0f);
        texCoords.add(0f);
        mesh.getTexCoords().setAll(texCoords.toFloatArray());

        int[] smoothingGroups = new int[numFaces];
        for (int i = 0; i < smoothingGroups.length; i++)
        {
            smoothingGroups[i] = 0;
        }
        mesh.getFaceSmoothingGroups().setAll(smoothingGroups);
    }

    /**
     * Get the unmarked vertices of this face and visit them, marking it and its connected faces.
     */
    private static void visitFace(Map<Integer, Set<Integer>> facesWithVertex, Set<Integer> faceGroup, TriangleMesh mesh, boolean[] vertexVisited,
        boolean[] faceVisited,
        Integer faceIndex)
    {
        faceVisited[faceIndex] = true;
        int vertex0 = mesh.getFaces().get(faceIndex * 6);
        if (!vertexVisited[vertex0])
        {
            visitVertex(facesWithVertex, faceGroup, mesh, vertexVisited, faceVisited, vertex0);
        }
        int vertex1 = mesh.getFaces().get(faceIndex * 6 + 2);
        if (!vertexVisited[vertex1])
        {
            visitVertex(facesWithVertex, faceGroup, mesh, vertexVisited, faceVisited, vertex1);
        }
        int vertex2 = mesh.getFaces().get(faceIndex * 6 + 4);
        if (!vertexVisited[vertex2])
        {
            visitVertex(facesWithVertex, faceGroup, mesh, vertexVisited, faceVisited, vertex2);
        }
    }

    /**
     * Find unmarked faces that use this vertex and add them to the group. Mark all the faces and
     * visit each of them.
     */
    private static void visitVertex(Map<Integer, Set<Integer>> facesWithVertex, Set<Integer> faceGroup, TriangleMesh mesh,
        boolean[] vertexVisited,
        boolean[] faceVisited, int vertexIndex)
    {
        vertexVisited[vertexIndex] = true;
        Set<Integer> faceIndices = facesWithVertex.get(vertexIndex);
        faceGroup.addAll(faceIndices);
        for (Integer faceIndex : faceIndices)
        {
            if (!faceVisited[faceIndex])
            {
                visitFace(facesWithVertex, faceGroup, mesh, vertexVisited, faceVisited, faceIndex);
            }
        }
    }

    
    /**
     * Create a map of vertex to faces using the vertex.
     */
    public static Map<Integer, Set<Integer>> makeFacesWithVertex(TriangleMesh mesh)
    {
        
        Map<Integer, Set<Integer>> facesWithVertex = new HashMap<>();
        
        for (int faceIndex = 0; faceIndex < mesh.getFaces().size() / 6; faceIndex++) {

            updateFacesWithVertices(mesh, faceIndex, facesWithVertex);            
        }
        return facesWithVertex;
    }

    public static void updateFacesWithVertices(TriangleMesh mesh, int faceIndex,
        Map<Integer, Set<Integer>> facesWithVertex)
    {
        int vertex0 = mesh.getFaces().get(faceIndex * 6);
        if (! facesWithVertex.containsKey(vertex0)) {
            facesWithVertex.put(vertex0, new HashSet<>());
        }
        facesWithVertex.get(vertex0).add(faceIndex);
        
        int vertex1 = mesh.getFaces().get(faceIndex * 6 + 2);
        if (! facesWithVertex.containsKey(vertex1)) {
            facesWithVertex.put(vertex1, new HashSet<>());
        }
        facesWithVertex.get(vertex1).add(faceIndex);
        
        int vertex2 = mesh.getFaces().get(faceIndex * 6 + 4);
        if (! facesWithVertex.containsKey(vertex2)) {
            facesWithVertex.put(vertex2, new HashSet<>());
        }
        facesWithVertex.get(vertex2).add(faceIndex);
    }

}
