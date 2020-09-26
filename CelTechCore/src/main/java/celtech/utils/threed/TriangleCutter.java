/*
 * Copyright 2015 CEL UK
 */
package celtech.utils.threed;

import celtech.roboxbase.utils.Math.MathUtils;
import static celtech.roboxbase.utils.Math.MathUtils.EQUAL;
import static celtech.utils.threed.MeshCutter2.makePoint3D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.geometry.Point3D;
import javafx.scene.shape.TriangleMesh;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author tony
 */
public class TriangleCutter {
    
    private final static Stenographer steno = StenographerFactory.getStenographer(
        TriangleCutter.class.getName());

    static final float epsilon = 0.0001f;

    static Vertex getVertex(TriangleMesh mesh, int vertexIndex) {
        float x = mesh.getPoints().get(vertexIndex * 3);
        float y = mesh.getPoints().get(vertexIndex * 3 + 1);
        float z = mesh.getPoints().get(vertexIndex * 3 + 2);
        return new Vertex(x, y, z);
    }

    /**
     * Return the edges that the plane intersects (not touches).
     */
    static Set<Edge> getEdgesOfFaceThatPlaneIntersects(TriangleMesh mesh, int faceIndex,
            float cutHeight, MeshCutter2.BedToLocalConverter bedToLocalConverter) {
        Set<Edge> edges = new HashSet<>();
        int vertex0 = mesh.getFaces().get(faceIndex * 6);
        int vertex1 = mesh.getFaces().get(faceIndex * 6 + 2);
        int vertex2 = mesh.getFaces().get(faceIndex * 6 + 4);

        if (lineIntersectsPlane(mesh, vertex0, vertex1, cutHeight, bedToLocalConverter)) {
            edges.add(new Edge(vertex0, vertex1));
        }
        if (lineIntersectsPlane(mesh, vertex1, vertex2, cutHeight, bedToLocalConverter)) {
            edges.add(new Edge(vertex1, vertex2));
        }
        if (lineIntersectsPlane(mesh, vertex0, vertex2, cutHeight, bedToLocalConverter)) {
            edges.add(new Edge(vertex0, vertex2));
        }
        return edges;
    }

    /**
     * Add the vertex if it does not already exist in the mesh, and return its index. This is
     * inefficient and could easily be improved by caching vertices.
     */
    static int addNewOrGetVertex(TriangleMesh mesh, Vertex intersectingVertex) {
        for (int i = 0; i < mesh.getPoints().size() / 3; i++) {
            Vertex vertex = getVertex(mesh, i);
            if (vertex.equals(intersectingVertex)) {
//                steno.debug("vertex already exists at " + i);
                return i;
            }
        }

        mesh.getPoints().addAll((float) intersectingVertex.x, (float) intersectingVertex.y,
                (float) intersectingVertex.z);
//        steno.debug("add new vertex at index " + (mesh.getPoints().size() / 3 - 1) + " at "
//                + intersectingVertex);
        return mesh.getPoints().size() / 3 - 1;
    }

    /**
     * Calculate the coordinates of the intersection with the edge, add a new vertex at that point
     * and return the index of the new vertex.
     */
    static Integer makeIntersectingVertex(TriangleMesh mesh, Edge edge, float cutHeight,
            MeshCutter2.BedToLocalConverter bedToLocalConverter) {
        Vertex vertex = getIntersectingVertex(edge, mesh, cutHeight, bedToLocalConverter);
        int vertexIndex = addNewOrGetVertex(mesh, vertex);
        return vertexIndex;
    }

    static Point3D getFaceNormal(TriangleMesh mesh, int faceIndex) {
        int v0 = mesh.getFaces().get(faceIndex * 6);
        int v1 = mesh.getFaces().get(faceIndex * 6 + 2);
        int v2 = mesh.getFaces().get(faceIndex * 6 + 4);

        Point3D point0 = makePoint3D(mesh, v0);
        Point3D point1 = makePoint3D(mesh, v1);
        Point3D point2 = makePoint3D(mesh, v2);

        point0 = point0.normalize();
        point1 = point1.normalize();
        point2 = point2.normalize();

        return (point2.subtract(point0).crossProduct(point1.subtract(point0))).normalize();

    }

    /**
     * Return the intersecting vertices of the face.
     */
    static Set<Vertex> getIntersectingEdgeVertices(TriangleMesh mesh, int faceIndex,
            float cutHeight, MeshCutter2.BedToLocalConverter bedToLocalConverter) {
        Set<Vertex> vertices = new HashSet<>();
        Set<Edge> edges = getEdgesOfFaceThatPlaneIntersects(mesh, faceIndex, cutHeight,
                bedToLocalConverter);
        for (Edge edge : edges) {
            int vertexIndex = makeIntersectingVertex(mesh, edge, cutHeight,
                    bedToLocalConverter);

            vertices.add(getVertex(mesh, vertexIndex));
        }

        Set<Integer> vertexIndices = getFaceVerticesIntersectingPlane(
                mesh, faceIndex, cutHeight, bedToLocalConverter);
        for (Integer vertexIndex : vertexIndices) {
            vertices.add(getVertex(mesh, vertexIndex));
        }

        return vertices;
    }

    /**
     * If the plane cuts the face then add the lower face(s) of the cut to the child mesh.
     */
    public static void splitFaceAndAddLowerFacesToMesh(TriangleMesh mesh, Set<Integer> facesToRemove,
            int faceIndex, float cutHeight,
            MeshCutter2.BedToLocalConverter bedToLocalConverter, MeshCutter2.TopBottom topBottom) {

        Point3D initialNormal = getFaceNormal(mesh, faceIndex);

//        steno.debug("face " + faceIndex + " TB " + topBottom);
        int v0 = mesh.getFaces().get(faceIndex * 6);
        int v1 = mesh.getFaces().get(faceIndex * 6 + 2);
        int v2 = mesh.getFaces().get(faceIndex * 6 + 4);

//        steno.debug("v0 " + getVertex(mesh, v0));
//        steno.debug("v1 " + getVertex(mesh, v1));
//        steno.debug("v2 " + getVertex(mesh, v2));
        float v0Height = (float) bedToLocalConverter.localToBed(makePoint3D(mesh, v0)).getY();
        float v1Height = (float) bedToLocalConverter.localToBed(makePoint3D(mesh, v1)).getY();
        float v2Height = (float) bedToLocalConverter.localToBed(makePoint3D(mesh, v2)).getY();

//        steno.debug("v0height " + v0Height);
//        steno.debug("v1height " + v1Height);
//        steno.debug("v2height " + v2Height);
        // are points below/above cut?
        boolean v0belowCut;
        boolean v1belowCut;
        boolean v2belowCut;
        boolean v0aboveCut;
        boolean v1aboveCut;
        boolean v2aboveCut;

        if (topBottom == MeshCutter2.TopBottom.BOTTOM) {
            v0belowCut = v0Height > cutHeight + epsilon;
            v1belowCut = v1Height > cutHeight + epsilon;
            v2belowCut = v2Height > cutHeight + epsilon;
            v0aboveCut = v0Height < cutHeight - epsilon;
            v1aboveCut = v1Height < cutHeight - epsilon;
            v2aboveCut = v2Height < cutHeight - epsilon;
        } else {
            v0belowCut = v0Height < cutHeight - epsilon;
            v1belowCut = v1Height < cutHeight - epsilon;
            v2belowCut = v2Height < cutHeight - epsilon;
            v0aboveCut = v0Height > cutHeight + epsilon;
            v1aboveCut = v1Height > cutHeight + epsilon;
            v2aboveCut = v2Height > cutHeight + epsilon;
        }

//        steno.debug(
//            v0belowCut + " " +
//            v1belowCut + " " +
//            v2belowCut + " " +
//            v0aboveCut+ " " +
//            v1aboveCut+ " " +
//            v2aboveCut
//            );
        if (v0aboveCut || v1aboveCut || v2aboveCut) {
            facesToRemove.add(faceIndex);
        }
        
        if (!v0aboveCut && !v1aboveCut && !v2aboveCut &&
            !v0belowCut && !v1belowCut && !v2belowCut) {
            // face lies on cutting plane
            facesToRemove.add(faceIndex);
        }

        boolean b01 = (v0belowCut && v1aboveCut) || (v1belowCut && v0aboveCut);
        boolean b12 = (v1belowCut && v2aboveCut) || (v2belowCut && v1aboveCut);
        boolean b02 = (v0belowCut && v2aboveCut) || (v2belowCut && v0aboveCut);

//       steno.debug("b01 b12 b02 " + b01 + " " + b12 + " " + b02);
        if (!b01 && !b12 && !b02) {
            //this face is not cut by the mesh
            return;
        }

        if (v0 == v1 || v1 == v2 || v0 == v2) {
            assert false;
        }

        // check for special case where one vertex of face is on cutting plane
        Set<Integer> vertexIndices = getFaceVerticesIntersectingPlane(
                mesh, faceIndex, cutHeight, bedToLocalConverter);

        if (vertexIndices.size() == 1) {
            steno.debug("one vertex on plane");
            cutWithOneVertexOnPlane(vertexIndices, v0, v1, v2,
                    v0belowCut, v1belowCut, v2belowCut,
                    mesh, faceIndex, cutHeight, bedToLocalConverter,
                    initialNormal);
            return;
        } else {
            assert vertexIndices.isEmpty();
        }

        // indices of intersecting vertices between v0->v1 etc
        int v01 = -1;
        int v12 = -1;
        int v02 = -1;

        // get vertex index for intersections v01, v12, v02
        if (b01) {
            Vertex vertex01 = getIntersectingVertex(new Edge(v0, v1), mesh, cutHeight,
                    bedToLocalConverter);
            v01 = addNewOrGetVertex(mesh, vertex01);
        }

        if (b12) {
            Vertex vertex12 = getIntersectingVertex(new Edge(v1, v2), mesh, cutHeight,
                    bedToLocalConverter);
            v12 = addNewOrGetVertex(mesh, vertex12);
        }

        if (b02) {
            Vertex vertex02 = getIntersectingVertex(new Edge(v0, v2), mesh, cutHeight,
                    bedToLocalConverter);
            v02 = addNewOrGetVertex(mesh, vertex02);
        }

        int numPointsBelowCut = 0;
        numPointsBelowCut += v0belowCut ? 1 : 0;
        numPointsBelowCut += v1belowCut ? 1 : 0;
        numPointsBelowCut += v2belowCut ? 1 : 0;

        // corner indices for new face A
        int c0 = -1;
        int c1 = -1;
        int c2 = -1;
        // corner indices for new face B
        int c3 = -1;
        int c4 = -1;
        int c5 = -1;
        if (numPointsBelowCut == 1) {
            // add face A
            if (v0belowCut) {
                c0 = v0;
                c1 = v01;
                c2 = v02;
            } else if (v1belowCut) {
                c0 = v1;
                c1 = v12;
                c2 = v01;
            } else if (v2belowCut) {
                c0 = v2;
                c1 = v02;
                c2 = v12;
            } else {
                throw new RuntimeException("Unexpected condition");
            }

            assert (c0 != -1 && c1 != -1 && c2 != -1);
            if (c0 != c1 && c1 != c2 && c2 != c0) {

                int[] vertices = new int[6];
                vertices[0] = c0;
                vertices[2] = c1;
                vertices[4] = c2;
                mesh.getFaces().addAll(vertices);
            }

        } else {
            // add faces A and B 
            if (v0belowCut && v1belowCut) {
                c0 = v0;
                c1 = v1;
                c2 = v12;
                c3 = v0;
                c4 = v12;
                c5 = v02;
            } else if (v1belowCut && v2belowCut) {
                c0 = v1;
                c1 = v2;
                c2 = v02;
                c3 = v1;
                c4 = v02;
                c5 = v01;
            } else if (v2belowCut && v0belowCut) {
                c0 = v2;
                c1 = v0;
                c2 = v01;
                c3 = v2;
                c4 = v01;
                c5 = v12;
            } else {
                throw new RuntimeException("Unexpected condition");
            }

            assert (c0 != -1 && c1 != -1 && c2 != -1 && c3 != -1 && c4 != -1 && c5 != -1);
            assert (c0 != c1 && c1 != c2 && c2 != c0) : faceIndex + " " + c0 + " " + c1 + " " + c2;

            int[] vertices = new int[6];
            vertices[0] = c0;
            vertices[2] = c1;
            vertices[4] = c2;
            mesh.getFaces().addAll(vertices);

            assert (c3 != c4 && c4 != c5 && c5 != c3) : faceIndex + " " + c3 + " " + c4 + " " + c5;
            vertices[0] = c3;
            vertices[2] = c4;
            vertices[4] = c5;
            mesh.getFaces().addAll(vertices);
        }

    }

    static void reverseLastFaceNormal(TriangleMesh mesh) {
        reverseFaceNormal(mesh, mesh.getFaces().size()/6 - 1);
    }

    static void reverseFaceNormal(TriangleMesh mesh, int faceIndex) {
        int v0 = mesh.getFaces().get(faceIndex * 6);
        int v1 = mesh.getFaces().get(faceIndex * 6 + 2);
        mesh.getFaces().set(faceIndex * 6, v1);
        mesh.getFaces().set(faceIndex * 6 + 2, v0);
    }

    private static void cutWithOneVertexOnPlane(Set<Integer> vertexIndices,
            int v0, int v1, int v2,
            boolean v0belowCut, boolean v1belowCut, boolean v2belowCut,
            TriangleMesh mesh, int faceIndex,
            float cutHeight, MeshCutter2.BedToLocalConverter bedToLocalConverter,
            Point3D initialNormal) {

        List<Integer> edgeVerticesIndices = new ArrayList<>();
        Set<Edge> edges = getEdgesOfFaceThatPlaneIntersects(mesh, faceIndex, cutHeight,
                bedToLocalConverter);

        for (Edge edge : edges) {
            int vertexIndex = makeIntersectingVertex(mesh, edge, cutHeight,
                    bedToLocalConverter);

            edgeVerticesIndices.add(vertexIndex);
        }
        if (edgeVerticesIndices.size() != 1) {
            steno.debug("vertices are ");
            v0 = mesh.getFaces().get(faceIndex * 6);
            v1 = mesh.getFaces().get(faceIndex * 6 + 2);
            v2 = mesh.getFaces().get(faceIndex * 6 + 4);
            steno.debug("v0A " + getVertex(mesh, v0));
            steno.debug("v1A " + getVertex(mesh, v1));
            steno.debug("v2A " + getVertex(mesh, v2));

        }
        assert edgeVerticesIndices.size() == 1 : "edges vertices size is "
                + edgeVerticesIndices.size();

        int vertexIndexOnPlane = vertexIndices.iterator().next();
        int vertexIndexCutting = edgeVerticesIndices.get(0);

        int c0 = -1;
        int c1 = -1;
        int c2 = -1;
        if (v0belowCut) {
            if (vertexIndexOnPlane == v1) {
                c0 = v0;
                c1 = vertexIndexOnPlane;
                c2 = vertexIndexCutting;
            } else {
                c0 = v0;
                c1 = vertexIndexCutting;
                c2 = vertexIndexOnPlane;
            }
        } else if (v1belowCut) {
            if (vertexIndexOnPlane == v0) {
                c0 = vertexIndexOnPlane;
                c1 = v1;
                c2 = vertexIndexCutting;
            } else {
                c0 = vertexIndexCutting;
                c1 = v1;
                c2 = vertexIndexOnPlane;
            }
        } else if (v2belowCut) {
            if (vertexIndexOnPlane == v0) {
                c0 = vertexIndexOnPlane;
                c1 = vertexIndexCutting;
                c2 = v2;
            } else {
                c0 = vertexIndexCutting;
                c1 = vertexIndexOnPlane;
                c2 = v2;
            }
        }
        int[] vertices = new int[6];
        vertices[0] = c0;
        vertices[2] = c1;
        vertices[4] = c2;
        mesh.getFaces().addAll(vertices);

        return;
    }

    static Vertex getIntersectingVertex(Edge edge, TriangleMesh mesh, float cutHeight,
            MeshCutter2.BedToLocalConverter bedToLocalConverter) {
        int v0 = edge.v0;
        int v1 = edge.v1;

        Point3D p0Bed = bedToLocalConverter.localToBed(makePoint3D(mesh, v0));
        Point3D p1Bed = bedToLocalConverter.localToBed(makePoint3D(mesh, v1));

        double v0X = p0Bed.getX();
        double v1X = p1Bed.getX();
        double v0Y = p0Bed.getY();
        double v1Y = p1Bed.getY();
        double v0Z = p0Bed.getZ();
        double v1Z = p1Bed.getZ();
        double proportionAlongEdge;
        if (Math.abs(v1Y - v0Y) < 1e-7) {
            assert false;
            proportionAlongEdge = 0;
        } else {
            proportionAlongEdge = (cutHeight - v0Y) / (v1Y - v0Y);
        }
        float interX = (float) (v0X + (v1X - v0X) * proportionAlongEdge);
        float interZ = (float) (v0Z + (v1Z - v0Z) * proportionAlongEdge);

        Point3D intersectingPointInBed = new Point3D(interX, (float) cutHeight, interZ);
        Point3D intersectingPoint = bedToLocalConverter.bedToLocal(intersectingPointInBed);

        Vertex vertex = new Vertex((float) intersectingPoint.getX(),
                (float) intersectingPoint.getY(),
                (float) intersectingPoint.getZ());
        return vertex;
    }

    /**
     * Does the line intersect the plane (not just touch).
     */
    static boolean lineIntersectsPlane(TriangleMesh mesh, int vertex0, int vertex1,
            float cutHeight, MeshCutter2.BedToLocalConverter bedToLocalConverter) {

        float y0 = (float) bedToLocalConverter.localToBed(makePoint3D(mesh, vertex0)).getY();
        float y1 = (float) bedToLocalConverter.localToBed(makePoint3D(mesh, vertex1)).getY();

        if (((y0 < cutHeight - epsilon) && (cutHeight + epsilon < y1))
                || ((y1 < cutHeight - epsilon) && (cutHeight + epsilon < y0))) {
            return true;
        }
        return false;
    }

    static Set<Integer> getFaceVerticesIntersectingPlane(TriangleMesh mesh, int faceIndex,
            float cutHeight, MeshCutter2.BedToLocalConverter bedToLocalConverter) {
        Set<Integer> vertexIndices = new HashSet<>();

        int vertex0 = mesh.getFaces().get(faceIndex * 6);
        int vertex1 = mesh.getFaces().get(faceIndex * 6 + 2);
        int vertex2 = mesh.getFaces().get(faceIndex * 6 + 4);
        Point3D point0InBed = bedToLocalConverter.localToBed(makePoint3D(mesh, vertex0));
        Point3D point1InBed = bedToLocalConverter.localToBed(makePoint3D(mesh, vertex1));
        Point3D point2InBed = bedToLocalConverter.localToBed(makePoint3D(mesh, vertex2));
        if (MathUtils.compareFloat((float) point0InBed.getY(), cutHeight, epsilon) == EQUAL) {
            vertexIndices.add(vertex0);
        }
        if (MathUtils.compareFloat((float) point1InBed.getY(), cutHeight, epsilon) == EQUAL) {
            vertexIndices.add(vertex1);
        }
        if (MathUtils.compareFloat((float) point2InBed.getY(), cutHeight, epsilon) == EQUAL) {
            vertexIndices.add(vertex2);
        }
        return vertexIndices;
    }

}
