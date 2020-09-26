/*
 * Copyright 2015 CEL UK
 */
package celtech.utils.threed;

import static celtech.utils.threed.TriangleCutterTest.makeNullConverter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javafx.geometry.Point3D;
import javafx.scene.shape.TriangleMesh;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;


/**
 *
 * @author tony
 */
public class NonManifoldLoopDetectorTest
{

    public static TriangleMesh createSimpleCubeWithMissingFace()
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
//        mesh.getFaces().addAll(0, 0, 2, 0, 1, 0);
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

    @Test
    public void testGetNonManifoldEdges()
    {
        MeshCutter2.BedToLocalConverter nullBedToLocalConverter = makeNullConverter();
        TriangleMesh mesh = createSimpleCubeWithMissingFace();
        Set<ManifoldEdge> edges = NonManifoldLoopDetector.getNonManifoldEdges(mesh,
                                                                              nullBedToLocalConverter);
        
        Set<ManifoldEdge> expectedEdges = new HashSet<>();
        Point3D point0InBed = MeshCutter2.makePoint3D(mesh, 0);
        Point3D point1InBed = MeshCutter2.makePoint3D(mesh, 1);
        Point3D point2InBed = MeshCutter2.makePoint3D(mesh, 2);

        expectedEdges.add(new ManifoldEdge(0, 1, point0InBed, point1InBed, 1));
        expectedEdges.add(new ManifoldEdge(1, 2, point1InBed, point2InBed, 4));
        expectedEdges.add(new ManifoldEdge(0, 2, point0InBed, point2InBed, 0));

        assertEquals(expectedEdges, edges);
    }

    @Test
    public void testLoopForEdgeInDirectionSimple()
    {
        Point3D vertex0 = new Point3D(1, 0, 0);
        Point3D vertex1 = new Point3D(2, 0, 0);
        Point3D vertex2 = new Point3D(1, 0, 1);
        Point3D vertex3 = new Point3D(2, 0, 1);
        Point3D vertex4 = new Point3D(3, 0, 1);
        Point3D vertex5 = new Point3D(4, 0, 1);
        Point3D vertex6 = new Point3D(1, 0, 3);
        Point3D vertex7 = new Point3D(2, 0, 3);
        Point3D vertex8 = new Point3D(0, 0, 2);
        Point3D vertex9 = new Point3D(0, 0, 4);

        Set<ManifoldEdge> manifoldEdges = new HashSet<>();
        ManifoldEdge edge0 = new ManifoldEdge(0, 1, vertex0, vertex1, 3);
        manifoldEdges.add(edge0);
        manifoldEdges.add(new ManifoldEdge(1, 2, vertex1, vertex2, 3));
        manifoldEdges.add(new ManifoldEdge(0, 2, vertex0, vertex2, 3));

        Map<Integer, Set<ManifoldEdge>> edgesWithPoint3D = NonManifoldLoopDetector.makeEdgesWithVertex(
            manifoldEdges);
        Optional<List<ManifoldEdge>> loop = NonManifoldLoopDetector.getLoopForEdgeInDirection(edge0,
                                                                                              edgesWithPoint3D,
                                                                                              NonManifoldLoopDetector.Direction.FORWARDS);

        List<ManifoldEdge> expectedLoop = new ArrayList<>();
        expectedLoop.add(new ManifoldEdge(0, 1, null, null, 3));
        expectedLoop.add(new ManifoldEdge(1, 2, null, null, 3));
        expectedLoop.add(new ManifoldEdge(2, 0, null, null, 3));

        assertEquals(expectedLoop, loop.get());

    }

    @Test
    public void testGetLoopForEdgeInDirectionTwoAdjacentLoops()
    {

        Point3D vertex0 = new Point3D(1, 0, 0);
        Point3D vertex1 = new Point3D(2, 0, 0);
        Point3D vertex2 = new Point3D(1, 0, 1);
        Point3D vertex3 = new Point3D(2, 0, 1);
        Point3D vertex4 = new Point3D(3, 0, 1);
        Point3D vertex5 = new Point3D(4, 0, 1);
        Point3D vertex6 = new Point3D(1, 0, 3);
        Point3D vertex7 = new Point3D(2, 0, 3);
        Point3D vertex8 = new Point3D(0, 0, 2);
        Point3D vertex9 = new Point3D(0, 0, 4);

        Set<ManifoldEdge> manifoldEdges = new HashSet<>();
        ManifoldEdge edge0 = new ManifoldEdge(0, 1, vertex0, vertex1, 3);
        manifoldEdges.add(edge0);
        manifoldEdges.add(new ManifoldEdge(1, 3, vertex1, vertex3, 3));
        manifoldEdges.add(new ManifoldEdge(3, 2, vertex3, vertex2, 3));
        manifoldEdges.add(new ManifoldEdge(2, 0, vertex2, vertex0, 3));
        ManifoldEdge edge1 = new ManifoldEdge(3, 7, vertex3, vertex7, 3);
        manifoldEdges.add(edge1);
        manifoldEdges.add(new ManifoldEdge(7, 6, vertex7, vertex6, 3));
        manifoldEdges.add(new ManifoldEdge(6, 2, vertex6, vertex2, 3));

        Map<Integer, Set<ManifoldEdge>> edgesWithVertex = NonManifoldLoopDetector.makeEdgesWithVertex(
            manifoldEdges);

        Optional<List<ManifoldEdge>> loop1 = NonManifoldLoopDetector.getLoopForEdgeInDirection(edge0,
                                                                                               edgesWithVertex,
                                                                                               NonManifoldLoopDetector.Direction.BACKWARDS);
        for (ManifoldEdge manifoldEdge : loop1.get())
        {
            System.out.println(manifoldEdge);
        }
        assertEquals(4, loop1.get().size());

        Optional<List<ManifoldEdge>> loop2 = NonManifoldLoopDetector.getLoopForEdgeInDirection(edge1,
                                                                                               edgesWithVertex,
                                                                                               NonManifoldLoopDetector.Direction.FORWARDS);
        assertEquals(6, loop2.get().size());

        Set<List<ManifoldEdge>> loops = new HashSet<>();
        loops.add(loop1.get());
        loops.add(loop2.get());

//        MeshDebug.visualiseEdgeLoops(manifoldEdges, loops);
        assertFalse(NonManifoldLoopDetector.loopHasChord(loop1.get(), edgesWithVertex));
        assertTrue(NonManifoldLoopDetector.loopHasChord(loop2.get(), edgesWithVertex));

        loops = NonManifoldLoopDetector.removeLoopsWithChords(loops, edgesWithVertex);
        assertEquals(1, loops.size());

    }

    @Test
    public void testIdentifyNonManifoldLoops()
    {

        TriangleMesh mesh = createSimpleCubeWithMissingFace();

        MeshCutter2.BedToLocalConverter nullBedToLocalConverter = makeNullConverter();

        Set<List<ManifoldEdge>> loops = NonManifoldLoopDetector.identifyNonManifoldLoops(mesh,
                                                                                         nullBedToLocalConverter);
        for (List<ManifoldEdge> loop : loops)
        {
            System.out.println("XXXX");
            for (ManifoldEdge edge : loop)
            {
                System.out.println(edge);
            }
        }
        assertEquals(1, loops.size());

        List<ManifoldEdge> expectedLoop = new ArrayList<>();
        expectedLoop.add(new ManifoldEdge(0, 1, null, null, 1));
        expectedLoop.add(new ManifoldEdge(0, 2, null, null, 0));
        expectedLoop.add(new ManifoldEdge(2, 1, null, null, 4));

        assertEquals(expectedLoop, loops.iterator().next());
    }

    @Test
    public void testGetRightmostEdge()
    {

        Point3D vertex0 = new Point3D(1, 0, 0);
        Point3D vertex1 = new Point3D(2, 0, 0);
        Point3D vertex2 = new Point3D(1, 0, 1);
        Point3D vertex3 = new Point3D(2, 0, 1);
        Point3D vertex4 = new Point3D(3, 0, 1);
        Point3D vertex5 = new Point3D(4, 0, 1);
        Point3D vertex6 = new Point3D(1, 0, 3);
        Point3D vertex7 = new Point3D(2, 0, 3);
        Point3D vertex8 = new Point3D(0, 0, 2);
        Point3D vertex9 = new Point3D(0, 0, 4);

        Set<ManifoldEdge> manifoldEdges = new HashSet<>();
        ManifoldEdge edge0 = new ManifoldEdge(0, 1, vertex0, vertex1, 3);
        ManifoldEdge edge1 = new ManifoldEdge(1, 3, vertex1, vertex3, 3);
        ManifoldEdge edge2 = new ManifoldEdge(3, 2, vertex3, vertex2, 3);
        ManifoldEdge edge3 = new ManifoldEdge(0, 2, vertex0, vertex2, 3);
        ManifoldEdge edge4 = new ManifoldEdge(3, 7, vertex3, vertex7, 3);
        ManifoldEdge edge5 = new ManifoldEdge(7, 6, vertex7, vertex6, 3);
        ManifoldEdge edge6 = new ManifoldEdge(6, 2, vertex6, vertex2, 3);
        manifoldEdges.add(edge0);
        manifoldEdges.add(edge1);
        manifoldEdges.add(edge2);
        manifoldEdges.add(edge3);
        manifoldEdges.add(edge4);
        manifoldEdges.add(edge5);
        manifoldEdges.add(edge6);

        Map<Integer, Set<ManifoldEdge>> edgesWithPoint3D = NonManifoldLoopDetector.makeEdgesWithVertex(
            manifoldEdges);

        int vertexId = 2;
        Set<ManifoldEdge> availableEdges = new HashSet<>(edgesWithPoint3D.get(vertexId));
        availableEdges.remove(edge3);

        ManifoldEdge rightmostEdge = NonManifoldLoopDetector.getRightmostEdge(vertexId, edge3,
                                                                              availableEdges);
        assertEquals(edge2, rightmostEdge);

        vertexId = 3;
        availableEdges = new HashSet<>(edgesWithPoint3D.get(vertexId));
        availableEdges.remove(edge1);

        rightmostEdge = NonManifoldLoopDetector.getRightmostEdge(vertexId, edge1,
                                                                 availableEdges);
        assertEquals(edge4, rightmostEdge);

        vertexId = 3;
        availableEdges = new HashSet<>(edgesWithPoint3D.get(vertexId));
        availableEdges.remove(edge4);

        rightmostEdge = NonManifoldLoopDetector.getRightmostEdge(vertexId, edge4,
                                                                 availableEdges);
        assertEquals(edge2, rightmostEdge);

    }

    @Test
    public void testGetLoop()
    {

        Point3D vertex9 = new Point3D(-6.999998f, -9.999f, -14.001001f);
        Point3D vertex8 = new Point3D(-16.999998f, -9.999f, -14.001001f);
        Point3D vertex10 = new Point3D(3.000002f, -9.999f, -14.001001f);
        Point3D vertex11 = new Point3D(3.000002f, -9.999f, -4.0005016f);
        Point3D vertex14 = new Point3D(-16.999998f, -9.999f, -4.0005016f);
        Point3D vertex13 = new Point3D(-16.999998f, -9.999f, 6.0f);
        Point3D vertex12 = new Point3D(3.000002f, -9.999f, 6.0f);
        Point3D vertex15 = new Point3D(-7.000002f, -9.999f, 6.0f);

        Set<ManifoldEdge> manifoldEdges = new HashSet<>();
        ManifoldEdge edge0 = new ManifoldEdge(9, 8, vertex9, vertex8, 3);
        ManifoldEdge edge1 = new ManifoldEdge(10, 9, vertex10, vertex9, 3);
        ManifoldEdge edge2 = new ManifoldEdge(11, 10, vertex11, vertex10, 3);
        ManifoldEdge edge3 = new ManifoldEdge(8, 14, vertex8, vertex14, 3);
        ManifoldEdge edge4 = new ManifoldEdge(12, 11, vertex12, vertex11, 3);
        ManifoldEdge edge5 = new ManifoldEdge(14, 13, vertex14, vertex13, 3);
        ManifoldEdge edge6 = new ManifoldEdge(15, 12, vertex15, vertex12, 3);
        ManifoldEdge edge7 = new ManifoldEdge(13, 15, vertex13, vertex15, 3);

        manifoldEdges.add(edge0);
        manifoldEdges.add(edge1);
        manifoldEdges.add(edge2);
        manifoldEdges.add(edge3);
        manifoldEdges.add(edge4);
        manifoldEdges.add(edge5);
        manifoldEdges.add(edge6);
        manifoldEdges.add(edge7);

        Map<Integer, Set<ManifoldEdge>> edgesWithPoint3D = NonManifoldLoopDetector.makeEdgesWithVertex(
            manifoldEdges);

        Optional<List<ManifoldEdge>> loop = NonManifoldLoopDetector.getLoopForEdgeInDirection(
            edge0, edgesWithPoint3D, NonManifoldLoopDetector.Direction.FORWARDS);
        for (ManifoldEdge manifoldEdge : loop.get())
        {
            System.out.println(manifoldEdge);
        }
        assertEquals(8, loop.get().size());

        Set<List<ManifoldEdge>> loops = new HashSet<>();
        loops.add(loop.get());

//        MeshDebug.visualiseEdgeLoops(manifoldEdges, loops);
    }

    /**
     * This test was taken from cutting the repaired Eiffel tower (at 45 degree lean) at 51. It shows
     * some strangely ordered vertices, which prevent triangulation.
     */
    @Test
    public void testEiffelTower1()
    {
        Point3D p56832 = new Point3D(-38.88096904592214, -49.385915225137296, -46.14887970685962);
        Point3D p56833 = new Point3D(-38.88097090038349, -49.38591707959864, -44.36129450798035);
        Point3D p56834 = new Point3D(-38.880969130215846, -49.385915309431, -43.69737410545349);
        Point3D p56963 = new Point3D(-39.01334209755754, -49.38591615236797, -39.44788074493408);
        Point3D p56887 = new Point3D(-39.387256799253805, -49.38591750106711, -39.379934310913086);
        Point3D p56888 = new Point3D(-39.38725637778532, -49.38591707959863, -39.58413887023926);
        Point3D p56889 = new Point3D(-39.38725418614919, -49.385914887962514, -40.03571271896362);
        Point3D p56890 = new Point3D(-39.38725469191138, -49.38591539372469, -43.88319635391235);
        Point3D p56891 = new Point3D(-39.38725469191138, -49.38591539372469, -43.93436002731323);
        Point3D p56892 = new Point3D(-39.387255956316835, -49.38591665813014, -43.84689521789551);
        Point3D p56893 = new Point3D(-39.38725604061054, -49.38591674242383, -43.614954710006714);
        Point3D p56894 = new Point3D(-39.387256546372726, -49.38591724818602, -42.97742938995361);
        Point3D p56895 = new Point3D(-39.38725502908616, -49.385915730899484, -42.74850130081177);
        Point3D p56896 = new Point3D(-39.387256209197915, -49.385916911011236, -41.96891736984253);
        Point3D p56904 = new Point3D(-39.387256546372726, -49.38591724818602, -53.95808219909668);
        Point3D p56905 = new Point3D(-39.387255703435756, -49.38591640524906, -53.90200328826907);
        Point3D p56906 = new Point3D(-39.38725561914205, -49.38591632095536, -53.778010845184326);
        Point3D p56907 = new Point3D(-39.38725469191138, -49.38591539372469, -53.81824779510501);
        Point3D p56908 = new Point3D(-39.38725899088993, -49.38591969270324, -53.59621810913089);
        Point3D p56909 = new Point3D(-39.38725545055465, -49.38591615236797, -53.47531032562259);
        Point3D p56910 = new Point3D(-39.38725688354751, -49.385917585360815, -53.386425018310575);
        Point3D p56911 = new Point3D(-39.387255366260945, -49.385916068074266, -53.08371448516846);
        Point3D p56912 = new Point3D(-39.387256209197915, -49.38591691101125, -52.78511714935303);
        Point3D p56913 = new Point3D(-39.38725494479246, -49.38591564660578, -53.04733610153198);
        Point3D p56914 = new Point3D(-39.38725460761768, -49.385915309430985, -53.13784217834473);
        Point3D p56915 = new Point3D(-39.387255703435756, -49.38591640524906, -52.9297060966492);
        Point3D p56916 = new Point3D(-39.387254270442895, -49.3859149722562, -51.84006643295288);
        Point3D p56917 = new Point3D(-39.387255956316835, -49.38591665813016, -51.584752559661865);
        Point3D p56918 = new Point3D(-39.387255113379865, -49.38591581519317, -50.146244764328);
        Point3D p56919 = new Point3D(-39.387254523323975, -49.385915225137296, -50.92670965194702);
        Point3D p56920 = new Point3D(-39.38725637778532, -49.38591707959864, -49.85773754119873);
        Point3D p56921 = new Point3D(-39.38725494479246, -49.38591564660578, -50.71536731719971);
        Point3D p56922 = new Point3D(-39.387254270442895, -49.3859149722562, -48.63220167160034);
        Point3D p56923 = new Point3D(-39.387256546372726, -49.38591724818603, -48.27738118171692);
        Point3D p56924 = new Point3D(-39.38725494479246, -49.38591564660578, -46.71616040915251);
        Point3D p56925 = new Point3D(-39.38725612490424, -49.38591682671753, -46.76641961187124);
        Point3D p56926 = new Point3D(-39.38725696784121, -49.38591766965452, -45.94303339719772);
        Point3D p56927 = new Point3D(-39.38725604061054, -49.385916742423845, -43.75698447227478);
        Point3D p56928 = new Point3D(-39.38725587202313, -49.385916573836454, -44.52596426010135);
        Point3D p56813 = new Point3D(-38.88098573607414, -49.38593191528932, -53.86614465713501);
        Point3D p56814 = new Point3D(-38.880968287278876, -49.38591446649403, -53.67492628097534);
        Point3D p56942 = new Point3D(-39.26786657854001, -49.38591412931925, -53.93638610839844);
        Point3D p56815 = new Point3D(-38.88096972027171, -49.385915899486875, -53.12182378768921);
        Point3D p56816 = new Point3D(-38.88097056320868, -49.385916742423845, -39.47194385528567);
        Point3D p56817 = new Point3D(-38.8809703103276, -49.38591648954275, -39.54799509048465);
        Point3D p56818 = new Point3D(-38.880970226033895, -49.38591640524905, -39.71592617034912);
        Point3D p56819 = new Point3D(-38.880970226033895, -49.38591640524905, -40.252967834472656);
        Point3D p56820 = new Point3D(-38.88096955168433, -49.385915730899484, -50.97497892379761);
        Point3D p56821 = new Point3D(-38.8809703103276, -49.38591648954275, -49.80960440635684);
        Point3D p56822 = new Point3D(-38.88097064750238, -49.38591682671753, -49.64062309265137);
        Point3D p56823 = new Point3D(-38.880968287278876, -49.38591446649403, -48.97670602798462);
        Point3D p56824 = new Point3D(-38.88097385066288, -49.38592002987802, -48.61001980304718);
        Point3D p56825 = new Point3D(-38.88096862445366, -49.38591480366881, -47.234533190727234);
        Point3D p56826 = new Point3D(-38.88096811869147, -49.38591429790665, -47.62708133459094);
        Point3D p56827 = new Point3D(-38.88096786581039, -49.385914045025544, -53.085031032562284);
        Point3D p56828 = new Point3D(-38.88096963597803, -49.38591581519319, -48.169516921043396);
        Point3D p56829 = new Point3D(-38.88096584276167, -49.38591202197681, -42.363021850585966);
        Point3D p56830 = new Point3D(-38.88097005744652, -49.38591623666166, -42.53199481964111);
        Point3D p56831 = new Point3D(-38.88096972027171, -49.385915899486875, -46.845308899879456);
        ManifoldEdge edge0 = new ManifoldEdge(56813, 56814, p56813, p56814, 51318);
        ManifoldEdge edge1 = new ManifoldEdge(56814, 56815, p56814, p56815, 51320);
        ManifoldEdge edge2 = new ManifoldEdge(56815, 56827, p56815, p56827, 51332);
        ManifoldEdge edge3 = new ManifoldEdge(56827, 56820, p56827, p56820, 51334);
        ManifoldEdge edge4 = new ManifoldEdge(56820, 56821, p56820, p56821, 51327);
        ManifoldEdge edge5 = new ManifoldEdge(56821, 56822, p56821, p56822, 51328);
        ManifoldEdge edge6 = new ManifoldEdge(56822, 56823, p56822, p56823, 51329);
        ManifoldEdge edge7 = new ManifoldEdge(56823, 56824, p56823, p56824, 51330);
        ManifoldEdge edge8 = new ManifoldEdge(56824, 56828, p56824, p56828, 51335);
        ManifoldEdge edge9 = new ManifoldEdge(56828, 56826, p56828, p56826, 51337);
        ManifoldEdge edge10 = new ManifoldEdge(56826, 56825, p56826, p56825, 51331);
        ManifoldEdge edge11 = new ManifoldEdge(56825, 56831, p56825, p56831, 51341);
        ManifoldEdge edge12 = new ManifoldEdge(56831, 56832, p56831, p56832, 51342);
        ManifoldEdge edge13 = new ManifoldEdge(56832, 56833, p56832, p56833, 51344);
        ManifoldEdge edge14 = new ManifoldEdge(56833, 56834, p56833, p56834, 51347);
        ManifoldEdge edge15 = new ManifoldEdge(56834, 56830, p56834, p56830, 51346);
        ManifoldEdge edge16 = new ManifoldEdge(56830, 56829, p56830, p56829, 51340);
        ManifoldEdge edge17 = new ManifoldEdge(56829, 56819, p56829, p56819, 51339);
        ManifoldEdge edge18 = new ManifoldEdge(56819, 56818, p56819, p56818, 51325);
        ManifoldEdge edge19 = new ManifoldEdge(56818, 56817, p56818, p56817, 51323);
        ManifoldEdge edge20 = new ManifoldEdge(56817, 56816, p56817, p56816, 51321);
        ManifoldEdge edge21 = new ManifoldEdge(56816, 56963, p56816, p56963, 51529);
        ManifoldEdge edge22 = new ManifoldEdge(56963, 56887, p56963, p56887, 52285);
        ManifoldEdge edge23 = new ManifoldEdge(56887, 56888, p56887, p56888, 51424);
        ManifoldEdge edge24 = new ManifoldEdge(56888, 56889, p56888, p56889, 51426);
        ManifoldEdge edge25 = new ManifoldEdge(56889, 56896, p56889, p56896, 51437);
        ManifoldEdge edge26 = new ManifoldEdge(56896, 56895, p56896, p56895, 51439);
        ManifoldEdge edge27 = new ManifoldEdge(56895, 56894, p56895, p56894, 51436);
        ManifoldEdge edge28 = new ManifoldEdge(56894, 56893, p56894, p56893, 51434);
        ManifoldEdge edge29 = new ManifoldEdge(56893, 56890, p56893, p56890, 51432);
        ManifoldEdge edge30 = new ManifoldEdge(56890, 56891, p56890, p56891, 51428);
        ManifoldEdge edge31 = new ManifoldEdge(56891, 56892, p56891, p56892, 51430);
        ManifoldEdge edge32 = new ManifoldEdge(56892, 56927, p56892, p56927, 51473);
        ManifoldEdge edge33 = new ManifoldEdge(56927, 56928, p56927, p56928, 51475);
        ManifoldEdge edge34 = new ManifoldEdge(56928, 56926, p56928, p56926, 51477);
        ManifoldEdge edge35 = new ManifoldEdge(56926, 56925, p56926, p56925, 51471);
        ManifoldEdge edge36 = new ManifoldEdge(56925, 56924, p56925, p56924, 51470);
        ManifoldEdge edge37 = new ManifoldEdge(56924, 56923, p56924, p56923, 51469);
        ManifoldEdge edge38 = new ManifoldEdge(56923, 56922, p56923, p56922, 51467);
        ManifoldEdge edge39 = new ManifoldEdge(56922, 56919, p56922, p56919, 51466);
        ManifoldEdge edge40 = new ManifoldEdge(56919, 56918, p56919, p56918, 51462);
        ManifoldEdge edge41 = new ManifoldEdge(56918, 56920, p56918, p56920, 51463);
        ManifoldEdge edge42 = new ManifoldEdge(56920, 56921, p56920, p56921, 51464);
        ManifoldEdge edge43 = new ManifoldEdge(56921, 56917, p56921, p56917, 51465);
        ManifoldEdge edge44 = new ManifoldEdge(56917, 56916, p56917, p56916, 51461);
        ManifoldEdge edge45 = new ManifoldEdge(56916, 56915, p56916, p56915, 51460);
        ManifoldEdge edge46 = new ManifoldEdge(56915, 56914, p56915, p56914, 51458);
        ManifoldEdge edge47 = new ManifoldEdge(56914, 56913, p56914, p56913, 51457);
        ManifoldEdge edge48 = new ManifoldEdge(56913, 56912, p56913, p56912, 51456);
        ManifoldEdge edge49 = new ManifoldEdge(56912, 56911, p56912, p56911, 51455);
        ManifoldEdge edge50 = new ManifoldEdge(56911, 56910, p56911, p56910, 51454);
        ManifoldEdge edge51 = new ManifoldEdge(56910, 56909, p56910, p56909, 51453);
        ManifoldEdge edge52 = new ManifoldEdge(56909, 56908, p56909, p56908, 51452);
        ManifoldEdge edge53 = new ManifoldEdge(56908, 56907, p56908, p56907, 51451);
        ManifoldEdge edge54 = new ManifoldEdge(56907, 56906, p56907, p56906, 51450);
        ManifoldEdge edge55 = new ManifoldEdge(56906, 56905, p56906, p56905, 51449);
        ManifoldEdge edge56 = new ManifoldEdge(56905, 56904, p56905, p56904, 51448);
        ManifoldEdge edge57 = new ManifoldEdge(56904, 56942, p56904, p56942, 51503);
        ManifoldEdge edge58 = new ManifoldEdge(56942, 56813, p56942, p56813, 52344);
        
        Set<ManifoldEdge> manifoldEdges = new HashSet<>();
        manifoldEdges.add(edge0);
        manifoldEdges.add(edge1);
        manifoldEdges.add(edge2);
        manifoldEdges.add(edge3);
        manifoldEdges.add(edge4);
        manifoldEdges.add(edge5);
        manifoldEdges.add(edge6);
        manifoldEdges.add(edge7);
        manifoldEdges.add(edge8);
        manifoldEdges.add(edge9);
        manifoldEdges.add(edge10);
        manifoldEdges.add(edge11);
        manifoldEdges.add(edge12);
        manifoldEdges.add(edge13);
        manifoldEdges.add(edge14);
        manifoldEdges.add(edge15);
        manifoldEdges.add(edge16);
        manifoldEdges.add(edge17);
        manifoldEdges.add(edge18);
        manifoldEdges.add(edge19);
        manifoldEdges.add(edge20);
        manifoldEdges.add(edge21);
        manifoldEdges.add(edge22);
        manifoldEdges.add(edge23);
        manifoldEdges.add(edge24);
        manifoldEdges.add(edge25);
        manifoldEdges.add(edge26);
        manifoldEdges.add(edge27);
        manifoldEdges.add(edge28);
        manifoldEdges.add(edge29);
        manifoldEdges.add(edge30);
        manifoldEdges.add(edge31);
        manifoldEdges.add(edge32);
        manifoldEdges.add(edge33);
        manifoldEdges.add(edge34);
        manifoldEdges.add(edge35);
        manifoldEdges.add(edge36);
        manifoldEdges.add(edge37);
        manifoldEdges.add(edge38);
        manifoldEdges.add(edge39);
        manifoldEdges.add(edge40);
        manifoldEdges.add(edge41);
        manifoldEdges.add(edge42);
        manifoldEdges.add(edge43);
        manifoldEdges.add(edge44);
        manifoldEdges.add(edge45);
        manifoldEdges.add(edge46);
        manifoldEdges.add(edge47);
        manifoldEdges.add(edge48);
        manifoldEdges.add(edge49);
        manifoldEdges.add(edge50);
        manifoldEdges.add(edge51);
        manifoldEdges.add(edge52);
        manifoldEdges.add(edge53);
        manifoldEdges.add(edge54);
        manifoldEdges.add(edge55);
        manifoldEdges.add(edge56);
        manifoldEdges.add(edge57);
        manifoldEdges.add(edge58);
        
        Map<Integer, Set<ManifoldEdge>> edgesWithPoint3D = NonManifoldLoopDetector.makeEdgesWithVertex(
            manifoldEdges);

        Optional<List<ManifoldEdge>> loop = NonManifoldLoopDetector.getLoopForEdgeInDirection(
            edge0, edgesWithPoint3D, NonManifoldLoopDetector.Direction.FORWARDS);
        for (ManifoldEdge manifoldEdge : loop.get())
        {
            System.out.println(manifoldEdge);
        }
        assertEquals(59, loop.get().size());

        for (ManifoldEdge edge : loop.get())
        {
            System.out.println(edge.v0 + " " + edge.v1);
        }
        
        
        Set<List<ManifoldEdge>> loops = new HashSet<>();
        loops.add(loop.get());
//        MeshDebug.visualiseEdgeLoops(manifoldEdges, loops);        

    }

}
