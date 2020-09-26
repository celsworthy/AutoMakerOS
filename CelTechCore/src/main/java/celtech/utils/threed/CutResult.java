/*
 * Copyright 2015 CEL UK
 */
package celtech.utils.threed;

import celtech.utils.threed.MeshCutter2.TopBottom;
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
 * CutResult represents one of the two parts of the cut mesh. It is also responsible for identifying
 * the topology of the nested polygons forming the perimeters on the closing top face (i.e. which
 * perimeters/polygons are inside which other polygons).
 *
 * @author tony
 */
class CutResult {
    
    private final static Stenographer steno = StenographerFactory.getStenographer(
        CutResult.class.getName());

    /**
     * The child mesh that was created by the split.
     */
    final TriangleMesh mesh;
    /**
     * The indices of the vertices of the child mesh, in sequence, that form the perimeter of the
     * new open face that needs to be triangulated. Some loops (list of points) may be holes inside
     * other loops.
     */
    final Set<PolygonIndices> loopsOfVerticesOnOpenFace;

    final MeshCutter2.BedToLocalConverter bedToLocalConverter;

    TopBottom topBottom;

    public CutResult(TriangleMesh mesh, Set<PolygonIndices> loops,
            MeshCutter2.BedToLocalConverter bedToLocalConverter, TopBottom topBottom) {
        loopsOfVerticesOnOpenFace = loops;
        this.mesh = mesh;
        this.bedToLocalConverter = bedToLocalConverter;
        this.topBottom = topBottom;
    }

    /**
     * Identify which of the loops in loopsOfVerticesOnOpenFace are internal to other loops. There
     * should not be any overlapping loops in the incoming data. Each LoopSet has one outer loop and
     * zero or more inner loops.
     */
    public Set<LoopSet> identifyOuterLoopsAndInnerLoops() {
        Set<LoopSet> topLevelLoopSets = new HashSet<>();
        for (PolygonIndices polygonIndices : loopsOfVerticesOnOpenFace) {
            boolean added = false;
            for (LoopSet loopSet : topLevelLoopSets) {
                if (loopSet.contains(polygonIndices)) {
                    loopSet.addToContainingChild(polygonIndices);
                    added = true;
                    validateLoopSets(topLevelLoopSets);
                    break;
                }
            }
            if (!added) {
                Set<LoopSet> innerLoopSets = new HashSet<>();
                String ids = "";
                for (LoopSet loopSet : topLevelLoopSets) {
                    if (contains(polygonIndices, loopSet.outerLoop)) {
                        innerLoopSets.add(loopSet);
                        ids += loopSet.id;
                    }
                }
                if (!innerLoopSets.isEmpty()) {
                    LoopSet newLoopSet = new LoopSet(this, polygonIndices, innerLoopSets);
                    steno.debug("new loop set " + newLoopSet.id + " contains previous top level loopsets " + ids);
                    topLevelLoopSets.add(newLoopSet);
                    topLevelLoopSets.removeAll(innerLoopSets);
                    validateLoopSets(topLevelLoopSets);
                    added = true;
                }
            }
            if (!added) {
                steno.debug("new top level");
                // polygonIndices is neither in a topLevelLoopSet nor contains a topLevelLoopSet
                // so create a new toplevelLoopSet.
                LoopSet newLoopSet = new LoopSet(this, polygonIndices, new HashSet<>());
                topLevelLoopSets.add(newLoopSet);
                validateLoopSets(topLevelLoopSets);
            }
        }

        validateLoopSets(topLevelLoopSets);
        return topLevelLoopSets;
    }

    private Point getPointAt(PolygonIndices loop, int index) {
        Point3D point = makePoint3D(mesh, loop.get(index));
        Point3D pointInBed = bedToLocalConverter.localToBed(point);
        return new Point(pointInBed.getX(), pointInBed.getZ());
    }

    public boolean contains(PolygonIndices outerPolygon, PolygonIndices innerPolygon) {
        
        List<Integer> sharedIndices = new ArrayList(outerPolygon);
        sharedIndices.retainAll(innerPolygon);
        if (! sharedIndices.isEmpty()) {
            throw new RuntimeException("Inner and outer polygon share a vertex");
        }
        
        int numContained = 0;
        for (int i = 0; i < innerPolygon.size(); i++)
        {
            Point point = getPointAt(innerPolygon, i);
            if (contains(point, outerPolygon)) {
                numContained++;
            }
        }
        
        if (numContained > 0 && numContained != innerPolygon.size()) {
            throw new RuntimeException("Overlapping polygons detected");
        }
        
        return numContained > 0;
        
    }

    /**
     * Return if the given loop contains the given point. see
     * http://stackoverflow.com/questions/8721406/how-to-determine-if-a-point-is-inside-a-2d-convex-polygon
     */
    public boolean contains(Point test, PolygonIndices loop) {
        Point[] points = new Point[loop.size()];
        for (int k = 0; k < points.length; k++) {
            points[k] = getPointAt(loop, k);
        }
        return contains(test, points);
    }
    
    public static boolean contains(Point test, Point[] points) {
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = points.length - 1; i < points.length; j = i++) {
            if ((points[i].y > test.y) != (points[j].y > test.y) && (test.x < (points[j].x
                    - points[i].x) * (test.y - points[i].y) / (points[j].y - points[i].y) + points[i].x)) {
                result = !result;
            }
        }
        return result;
    }

    private void validateLoopSets(Set<LoopSet> loopSets) {
        validateLoopSetsAreExclusive(loopSets);
        for (LoopSet loopSet : loopSets) {
            validateLoopSet(loopSet);
        }
    }

    private void validateLoopSetsAreExclusive(Set<LoopSet> loopSets) {
        for (LoopSet loopSet : loopSets) {
            Set<LoopSet> otherLoopSets = new HashSet<>(loopSets);
            otherLoopSets.remove(loopSet);
            for (LoopSet otherLoopSet : otherLoopSets) {
                assert !otherLoopSet.contains(loopSet.outerLoop) : otherLoopSet.id + " contains " + loopSet.id;
                assert !loopSet.contains(otherLoopSet.outerLoop) : loopSet.id + " contains " + otherLoopSet.id;
            }
        }
    }

    private void validateLoopSet(LoopSet loopSet) {
        validateLoopSetsAreExclusive(loopSet.innerLoopSets);
        for (LoopSet innerLoopSet : loopSet.innerLoopSets) {
            assert loopSet.contains(innerLoopSet.outerLoop) :
                    loopSet.outerLoop.name + " does not enclose " + innerLoopSet.outerLoop.name;
        }
        for (LoopSet innerLoopSet : loopSet.innerLoopSets) {
            validateLoopSet(innerLoopSet);

        }
    }

}

/**
 * PolygonIndices is a list of Integers each of which is a vertex (or face) id in the mesh. It is
 * therefore effectively a (usually closed) loop of vertices.
 *
 * @author tony
 */
class PolygonIndices extends ArrayList<Integer> {

    /**
     * The name is used only in testing.
     */
    String name;

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "PolygonIndices{" + "name=" + name + super.toString() + '}';
    }

}

/**
 * A Region is an outer polygon and zero or more inner polygons (holes), forming a region that can
 * be triangulated.
 */
class Region {

    final PolygonIndices outerLoop;
    final Set<PolygonIndices> innerLoops;

    public Region(PolygonIndices outerLoop, Set<PolygonIndices> innerLoops) {
        this.outerLoop = outerLoop;
        this.innerLoops = innerLoops;
    }
}

/**
 * A LoopSet is an outer polygon and a set of contained inner LoopSets.
 */
class LoopSet {
    
    private final static Stenographer steno = StenographerFactory.getStenographer(
        LoopSet.class.getName());

    static int nextId = 0;
    int id;

    final PolygonIndices outerLoop;
    final Set<LoopSet> innerLoopSets;
    final CutResult cutResult;

    public LoopSet(CutResult cutResult, PolygonIndices outerLoop, Set<LoopSet> innerLoopSets) {
        this.cutResult = cutResult;
        this.outerLoop = outerLoop;
        this.innerLoopSets = innerLoopSets;
        id = nextId;
        steno.debug("created loopset " + id);
        nextId++;
    }

    public boolean contains(PolygonIndices polygonIndices) {
        return cutResult.contains(outerLoop, polygonIndices);
    }

    /**
     * Return all the Regions ({@link Region}) described by this LoopSet. The outerLoop of this
     * LoopSet must be the outer perimeter of a Region (i.e. do not call this method on LoopSets
     * whose outerLoop is an inner perimeter / hole).
     */
    public Set<Region> getRegions() {
        Set<Region> regions = new HashSet<>();
        Set<PolygonIndices> innerLoops = new HashSet<>();

        for (LoopSet innerLoopSet : innerLoopSets) {
            innerLoops.add(innerLoopSet.outerLoop);
            for (LoopSet innerInnerLoopSet : innerLoopSet.innerLoopSets) {
                regions.addAll(innerInnerLoopSet.getRegions());
            }
        }
        Region region = new Region(outerLoop, innerLoops);
        regions.add(region);
        return regions;
    }

    /**
     * If the given polygonIndices is contained by one of the inner LoopSets then ask that inner
     * LoopSet to add it to one of its inner (containing) children, otherwise if no inner LoopSet
     * contains the given polygonIndices then add it as another inner LoopSet of this LoopSet.
     */
    public void addToContainingChild(PolygonIndices polygonIndices) {
        if (!contains(polygonIndices)) {
            throw new RuntimeException("given polygonIndices must be contained by outer loop");
        }
        boolean added = false;
        for (LoopSet innerLoopSet : innerLoopSets) {
            if (innerLoopSet.contains(polygonIndices)) {
                innerLoopSet.addToContainingChild(polygonIndices);
                added = true;
                break;
            }
        }
        if (!added) {
            Set<LoopSet> innerInnerLoopSets = new HashSet<>();
            String ids = "";
            for (LoopSet innerLoopSet : innerLoopSets) {
                if (cutResult.contains(polygonIndices, innerLoopSet.outerLoop)) {
                    innerInnerLoopSets.add(innerLoopSet);
                    ids += innerLoopSet.id;
                }
            }
            if (!innerInnerLoopSets.isEmpty()) {
                LoopSet newLoopSet = new LoopSet(cutResult, polygonIndices, innerInnerLoopSets);
                steno.debug("new loop set " + newLoopSet.id + " contains previous inner loopsets " + ids + " of loopsets " + id);
                innerLoopSets.add(newLoopSet);
                innerLoopSets.removeAll(innerInnerLoopSets);
                added = true;
            }
        }
        if (!added) {
            // add given polygonIndices as a new inner LoopSet.
            LoopSet newLoopSet = new LoopSet(cutResult, polygonIndices, new HashSet<>());
            innerLoopSets.add(newLoopSet);
            steno.debug("loopset " + id + " has new inner loop set " + newLoopSet.id);
        }
    }
}

/**
 * The X and Z coordinate of the point in the bed space maps to X and Y for polygon analysis. The
 * cut height (Y) is fixed in the bed coordinate system so we ignore that dimension for polygon
 * analysis.
 */
class Point {

    final double x;
    final double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
