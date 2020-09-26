/*
 * Copyright 2015 CEL UK
 */
package celtech.utils.threed;

import static celtech.utils.threed.MeshSeparator.makeFacesWithVertex;
import static celtech.utils.threed.MeshUtils.countFacesAdjacentToVertices;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.shape.TriangleMesh;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.apache.commons.math3.util.Pair;


/**
 * For algorithm see http://stackoverflow.com/questions/838076/small-cycle-finding-in-a-planar-graph
 *
 * @author tony
 */
public class NonManifoldLoopDetector
{
    private final static Stenographer steno = StenographerFactory.getStenographer(
        NonManifoldLoopDetector.class.getName());

    static enum Direction
    {

        FORWARDS, BACKWARDS;
    }

    /**
     * For the given mesh, identify the non-manifold edges and then determine the 
     * topological loops of edges based on shared vertices, returning to the start vertex.
     */
    public static Set<List<ManifoldEdge>> identifyNonManifoldLoops(TriangleMesh mesh,
        MeshCutter2.BedToLocalConverter bedToLocalConverter)
    {

        Set<ManifoldEdge> edges = getNonManifoldEdges(mesh, bedToLocalConverter);
//        steno.debug("non manifold edges " + edges.size() + " " + edges);
        Map<Integer, Set<ManifoldEdge>> edgesWithVertex = makeEdgesWithVertex(edges);

        Set<List<ManifoldEdge>> loops = new HashSet<>();
        if (edges.isEmpty())
        {
            return loops;
        }

        for (ManifoldEdge edge : edges)
        {
            Optional<List<ManifoldEdge>> loop = getLoopForEdgeInDirection(edge,
                                                                          edgesWithVertex,
                                                                          Direction.FORWARDS);
            if (loop.isPresent())
            {
                loops.add(loop.get());
            }

            loop = getLoopForEdgeInDirection(edge, edgesWithVertex, Direction.BACKWARDS);
            if (loop.isPresent())
            {
                loops.add(loop.get());
            }
        }

        loops = removeIdenticalLoops(loops);
        loops = removeLoopsWithChords(loops, edgesWithVertex);
        loops = removeLoopsWithZeroArea(loops);

        validateLoops(loops);

        Set<ManifoldEdge> usedEdges = new HashSet<>();
        for (List<ManifoldEdge> loop : loops)
        {
            for (ManifoldEdge edge : loop)
            {
                usedEdges.add(edge);
            }
        }
        edges.removeAll(usedEdges);
        /**
         * Debugging: unused edges indicate a problem in the topology of the non-manifold edges:
         * some of them do not form a loop. Continuing from this point with unused edges will cause
         * a child model to be an open mesh.
         */
        steno.debug("unused edges: " + edges.size());
        for (ManifoldEdge unusedEdge : edges)
        {
            steno.debug("" + unusedEdge);
        }

        return loops;
    }

    private static void validateLoops(Set<List<ManifoldEdge>> loops)
    {
        for (List<ManifoldEdge> loop : loops)
        {
            boolean valid = validateLoop(loop);
            if (!valid)
            {
                throw new RuntimeException("Invalid loop!");
            }
        }
    }

    static boolean validateLoop(List<ManifoldEdge> loop)
    {

        return true;
    }

    /**
     * For the given direction along the edge(forwards or backwards) try walking the connected edges
     * and if it returns to the starting vertex then return the list of edges as a loop.
     */
    static Optional<List<ManifoldEdge>> getLoopForEdgeInDirection(
        ManifoldEdge edge, Map<Integer, Set<ManifoldEdge>> edgesWithVertex, Direction direction)
    {

        if (edge.isVisited(direction))
        {
            // already have explored this possible loop
            return Optional.empty();
        }
        edge.setVisited(direction);

        List<ManifoldEdge> loop = new ArrayList<>();

        ManifoldEdge previousEdge = edge;
        int previousVertexId;
        int firstVertexId;
        if (direction == Direction.FORWARDS)
        {
            previousVertexId = previousEdge.v1;
            firstVertexId = previousEdge.v0;
        } else
        {
            previousVertexId = previousEdge.v0;
            firstVertexId = previousEdge.v1;
        }

        Set<Integer> vertexIndices = new HashSet<>();
        vertexIndices.add(firstVertexId);

        while (true)
        {
            loop.add(previousEdge);
            vertexIndices.add(previousVertexId);

            Set<ManifoldEdge> availableEdges = new HashSet<>(edgesWithVertex.get(previousVertexId));
            if (availableEdges.isEmpty())
            {
                return Optional.empty();
            }

            availableEdges.remove(previousEdge);
            ManifoldEdge nextEdge;
            if (availableEdges.size() == 1)
            {
                nextEdge = availableEdges.iterator().next();
            } else if (availableEdges.isEmpty())
            {
                // this can only happen with an invalid model
                assert false;
                return Optional.empty();
            } else
            {
                nextEdge = getRightmostEdge(previousVertexId, previousEdge, availableEdges);
            }

            Direction nextDirection;
            int nextVertexId;
            if (nextEdge.v0 == previousVertexId)
            {
                nextDirection = Direction.FORWARDS;
                nextVertexId = nextEdge.v1;
            } else
            {
                nextDirection = Direction.BACKWARDS;
                nextVertexId = nextEdge.v0;
            }

            if (nextVertexId == firstVertexId)
            {
                loop.add(nextEdge);
                break;
            }
            if (loop.contains(nextEdge))
            {
                assert false;
                return Optional.empty();
            }
            if (vertexIndices.contains(nextVertexId))
            {
                return Optional.empty();
            }
            if (nextEdge.isVisited(nextDirection))
            {
                // already have explored this possible loop
                return Optional.empty();
            }
            nextEdge.setVisited(nextDirection);
            previousEdge = nextEdge;
            previousVertexId = nextVertexId;
        }

        return Optional.of(loop);
    }

    /**
     * For the edges that come into this vertex, other than the previous edge, return the edge that
     * is most clockwise to the previous edge.
     */
    static ManifoldEdge getRightmostEdge(int previousVertexId,
        ManifoldEdge previousEdge, Set<ManifoldEdge> availableEdges)
    {
        assert availableEdges.size() > 0;
        assert !availableEdges.contains(previousEdge);
        double largestAngle = -Double.MAX_VALUE;

        ManifoldEdge rightmostEdge = null;

        int vStart;
        Point3D vertexStart;
        int vMiddle = previousVertexId;
        Point3D vertexMiddle;
        if (previousEdge.v0 == previousVertexId)
        {
            vertexStart = previousEdge.point1;
            vertexMiddle = previousEdge.point0;
            vStart = previousEdge.v1;
        } else
        {
            vertexStart = previousEdge.point0;
            vertexMiddle = previousEdge.point1;
            vStart = previousEdge.v0;
        }
        int vEnd;
        Point3D vertexEnd;
        // make incoming vector middle -> start
//        System.out.println("vstart, vmiddle " + vStart + " " + vMiddle);
        Point2D incoming = new Point2D(vertexStart.getX() - vertexMiddle.getX(),
                                       vertexStart.getZ() - vertexMiddle.getZ());
//        System.out.println("incoming vector: " + (vertexMiddle.x - vertexStart.x)
//            + " " + (vertexMiddle.z - vertexStart.z));

        for (ManifoldEdge edge : availableEdges)
        {
            if (edge.v0 == vMiddle)
            {
                vEnd = edge.v1;
                vertexEnd = edge.point1;
            } else
            {
                vEnd = edge.v0;
                vertexEnd = edge.point0;
            }
//            System.out.println("vend " + vEnd);

            Point2D outgoing = new Point2D(vertexEnd.getX() - vertexMiddle.getX(),
                                           vertexEnd.getZ() - vertexMiddle.getZ());

            // get clockwise angle between the two vectors
            // http://stackoverflow.com/questions/14066933/direct-way-of-computing-clockwise-angle-between-2-vectors
            double dot = incoming.getX() * outgoing.getX() + incoming.getY() * outgoing.getY();      // dot product
            double det = incoming.getX() * outgoing.getY() - incoming.getY() * outgoing.getX();      // determinant
            double ccwAngle = Math.atan2(det, dot) % (2 * Math.PI);

            double cwAngle = (2 * Math.PI - ccwAngle) % (2 * Math.PI);
//            System.out.println("CW angle " + cwAngle + " " + edge);

            if (cwAngle > largestAngle)
            {
                largestAngle = cwAngle;
                rightmostEdge = edge;
            }
        }
        return rightmostEdge;
    }

    /**
     * Make the map of vertexIndex to the edges that connect to it.
     */
    static Map<Integer, Set<ManifoldEdge>> makeEdgesWithVertex(Set<ManifoldEdge> edges)
    {
        Map<Integer, Set<ManifoldEdge>> edgesWithVertex = new HashMap<>();
        for (ManifoldEdge edge : edges)
        {
            if (!edgesWithVertex.containsKey(edge.v0))
            {
                edgesWithVertex.put(edge.v0, new HashSet<>());
            }
            if (!edgesWithVertex.containsKey(edge.v1))
            {
                edgesWithVertex.put(edge.v1, new HashSet<>());
            }
            edgesWithVertex.get(edge.v0).add(edge);
            edgesWithVertex.get(edge.v1).add(edge);
        }
        return edgesWithVertex;
    }

    static Set<List<ManifoldEdge>> removeIdenticalLoops(Set<List<ManifoldEdge>> loops)
    {
        Set<Set<Integer>> seenVertexSets = new HashSet<>();
        Set<List<ManifoldEdge>> uniqueLoops = new HashSet<>();
        for (List<ManifoldEdge> loop : loops)
        {
            PolygonIndices vertices = MeshCutter2.convertEdgesToPolygonIndices(loop).getFirst();
            Set<Integer> vertexSet = new HashSet<>(vertices);
            if (seenVertexSets.contains(vertexSet))
            {
                continue;
            } else
            {
                seenVertexSets.add(vertexSet);
                uniqueLoops.add(loop);
            }
        }
        return uniqueLoops;
    }

    /**
     * Remove loops that have a chord (another edge) cutting across them. These occur as a
     * consequence of the maze-walk algorithm going around the outside perimeter of a chorded loop.
     */
    static Set<List<ManifoldEdge>> removeLoopsWithChords(Set<List<ManifoldEdge>> loops,
        Map<Integer, Set<ManifoldEdge>> edgesWithVertex)
    {
        Set<List<ManifoldEdge>> loopsWithChords = new HashSet<>();
        for (List<ManifoldEdge> loop : loops)
        {
            if (loopHasChord(loop, edgesWithVertex))
            {
                continue;
            }
            loopsWithChords.add(loop);
        }
        return loopsWithChords;
    }

    static Set<List<ManifoldEdge>> removeLoopsWithZeroArea(Set<List<ManifoldEdge>> loops)
    {
        Set<List<ManifoldEdge>> loopsWithZeroArea = new HashSet<>();
        for (List<ManifoldEdge> loop : loops)
        {
            if (loopHasZeroArea(loop))
            {
                continue;
            }
            loopsWithZeroArea.add(loop);
        }
        return loopsWithZeroArea;
    }

    private static boolean loopHasZeroArea(List<ManifoldEdge> loop)
    {
        Pair<PolygonIndices, List<Point3D>> pair = MeshCutter2.convertEdgesToPolygonIndices(loop);
        List<Point3D> points3D = pair.getSecond();
        Point[] points = new Point[points3D.size()];
        for (int k = 0; k < points.length; k++)
        {
            points[k] = new Point(points3D.get(k).getX(), points3D.get(k).getZ());
        }
        return (getPolygonArea(points) == 0);
    }

    static double getPolygonArea(Point[] polygon)
    {
        int N = polygon.length;

        int i;
        int j;
        double area = 0;
        for (i = 0; i < N; i++)
        {
            j = (i + 1) % N;
            area += polygon[i].x * polygon[j].y;
            area -= polygon[i].y * polygon[j].x;
        }
        area /= 2;
        return area < 0 ? -area : area;
    }

    static boolean loopHasChord(List<ManifoldEdge> loop,
        Map<Integer, Set<ManifoldEdge>> edgesWithVertex)
    {
        /**
         * Algorithm: go round each vertex. Find edges into the vertex other than the two in the
         * loop. If the centre-point of the edge is contained by the loop then this edge is on a
         * chord.
         */
        Pair<PolygonIndices, List<Point3D>> pair = MeshCutter2.convertEdgesToPolygonIndices(loop);
        PolygonIndices vertexIndices = pair.getFirst();
        List<Point3D> points3D = pair.getSecond();
        Point[] points = new Point[points3D.size()];
        for (int k = 0; k < points.length; k++)
        {
            points[k] = new Point(points3D.get(k).getX(), points3D.get(k).getZ());
        }
        for (Integer vertexIndex : vertexIndices)
        {
            Set<ManifoldEdge> edgesIntoVertex = new HashSet(edgesWithVertex.get(vertexIndex));
            edgesIntoVertex.removeAll(loop);
            if (!edgesIntoVertex.isEmpty())
            {

                for (ManifoldEdge edge : edgesIntoVertex)
                {
                    Point3D point0 = edge.point0;
                    Point3D point1 = edge.point1;
                    Point edgeCentrePoint = new Point(
                        (point0.getX() + point1.getX()) / 2d,
                        (point0.getZ() + point1.getZ()) / 2d);
                    if (CutResult.contains(edgeCentrePoint, points))
                    {
                        return true;
                    }
                }
            }
        }

        return false;

    }

    static Set<ManifoldEdge> getNonManifoldEdges(TriangleMesh mesh,
        MeshCutter2.BedToLocalConverter bedToLocalConverter)
    {
        Map<Integer, Set<Integer>> facesWithVertices = makeFacesWithVertex(mesh);
        Set<ManifoldEdge> nonManifoldEdges = new HashSet<>();
        for (int faceIndex = 0; faceIndex < mesh.getFaces().size() / 6; faceIndex++)
        {
            int v0 = mesh.getFaces().get(faceIndex * 6);
            int v1 = mesh.getFaces().get(faceIndex * 6 + 2);
            int v2 = mesh.getFaces().get(faceIndex * 6 + 4);

            Point3D point0InBed = bedToLocalConverter.localToBed(MeshCutter2.makePoint3D(mesh, v0));
            Point3D point1InBed = bedToLocalConverter.localToBed(MeshCutter2.makePoint3D(mesh, v1));
            Point3D point2InBed = bedToLocalConverter.localToBed(MeshCutter2.makePoint3D(mesh, v2));

            if (countFacesAdjacentToVertices(mesh, facesWithVertices, faceIndex, 0, 1) != 1)
            {
                nonManifoldEdges.add(new ManifoldEdge(v0, v1, point0InBed, point1InBed, faceIndex));
            }
            if (countFacesAdjacentToVertices(mesh, facesWithVertices, faceIndex, 1, 2) != 1)
            {
                nonManifoldEdges.add(new ManifoldEdge(v1, v2, point1InBed, point2InBed, faceIndex));
            }
            if (countFacesAdjacentToVertices(mesh, facesWithVertices, faceIndex, 0, 2) != 1)
            {
                nonManifoldEdges.add(new ManifoldEdge(v0, v2, point0InBed, point2InBed, faceIndex));
            }
        }

        return nonManifoldEdges;
    }

}
