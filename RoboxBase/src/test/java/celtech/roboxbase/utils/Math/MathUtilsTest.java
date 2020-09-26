package celtech.roboxbase.utils.Math;

import celtech.roboxbase.utils.Math.PolarCoordinate;
import celtech.roboxbase.utils.Math.MathUtils;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Segment;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Ian
 */
public class MathUtilsTest
{

    public MathUtilsTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    /**
     * Test of angleDegreesToCartesianCWFromTop method, of class MathUtils.
     */
    @Test
    public void testAngleDegreesToCartesianCWFromTop()
    {
        System.out.println("angleDegreesToCartesianCWFromTop");
        double angle = 0.0;
        double radius = 10.0;
        Point2D expResult = new Point2D(0, -10);
        Point2D result = MathUtils.angleDegreesToCartesianCWFromTop(angle, radius, true);
        assertEquals(expResult, result);
    }

    /**
     * Test of cartesianToAngleDegreesCCWFromRight method, of class MathUtils.
     */
    @Test
    public void testCartesianToAngleDegreesCWFromTop()
    {
        System.out.println("cartesianToAngleDegreesCWFromTop");
        double xPos = 0.0;
        double yPos = 0.0;
        double expResult = 0.0;
        double result = MathUtils.cartesianToAngleDegreesCWFromTop(xPos, yPos);
        assertEquals(expResult, result, 0.0);

        xPos = 5.0;
        yPos = 5.0;
        expResult = 45.0;
        result = MathUtils.cartesianToAngleDegreesCWFromTop(xPos, yPos);
        assertEquals(expResult, result, 0.0);

        xPos = 5.0;
        yPos = 0.0;
        expResult = 90.0;
        result = MathUtils.cartesianToAngleDegreesCWFromTop(xPos, yPos);
        assertEquals(expResult, result, 0.0);

        xPos = 5.0;
        yPos = -5.0;
        expResult = 135.0;
        result = MathUtils.cartesianToAngleDegreesCWFromTop(xPos, yPos);
        assertEquals(expResult, result, 0.0);

        xPos = 0.0;
        yPos = -5.0;
        expResult = 180.0;
        result = MathUtils.cartesianToAngleDegreesCWFromTop(xPos, yPos);
        assertEquals(expResult, result, 0.0);

        xPos = -5.0;
        yPos = -5.0;
        expResult = 225.0;
        result = MathUtils.cartesianToAngleDegreesCWFromTop(xPos, yPos);
        assertEquals(expResult, result, 0.0);

        xPos = -5.0;
        yPos = 0.0;
        expResult = 270.0;
        result = MathUtils.cartesianToAngleDegreesCWFromTop(xPos, yPos);
        assertEquals(expResult, result, 0.0);

        xPos = -5.0;
        yPos = 5.0;
        expResult = 315.0;
        result = MathUtils.cartesianToAngleDegreesCWFromTop(xPos, yPos);
        assertEquals(expResult, result, 0.0);
    }
//
//    /**
//     * Test of boundAzimuthRadians method, of class MathUtils.
//     */
//    @Test
//    public void testBoundAzimuthRadians()
//    {
//        System.out.println("boundAzimuthRadians");
//        double azimuth = 0.0;
//        double expResult = 0.0;
//        double result = MathUtils.boundAzimuthRadians(azimuth);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of boundAzimuthDegrees method, of class MathUtils.
//     */
//    @Test
//    public void testBoundAzimuthDegrees()
//    {
//        System.out.println("boundAzimuthDegrees");
//        double azimuth = 0.0;
//        double expResult = 0.0;
//        double result = MathUtils.boundAzimuthDegrees(azimuth);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    @Test
    public void testSphericalToCartesianLocalSpaceAdjusted()
    {
        System.out.println("getsphericalToCartesianLocalSpaceAdjusted");

        double radius = 100;
        double epsilon = 0.001;

        PolarCoordinate input1 = new PolarCoordinate(0, 0, radius);
        Point3D result1 = MathUtils.sphericalToCartesianLocalSpaceAdjusted(input1);
        assertNotNull(result1);
        assertEquals(0, result1.getX(), epsilon);
        assertEquals(0, result1.getY(), epsilon);
        assertEquals(-100, result1.getZ(), epsilon);

        PolarCoordinate input2 = new PolarCoordinate(Math.PI / 4, 0, radius);
        Point3D result2 = MathUtils.sphericalToCartesianLocalSpaceAdjusted(input2);
        assertNotNull(result2);
        assertEquals(0, result2.getX(), epsilon);
        assertEquals(-70.7106, result2.getY(), epsilon);
        assertEquals(-70.7106, result2.getZ(), epsilon);

        PolarCoordinate input3 = new PolarCoordinate(0, Math.PI / 2, radius);
        Point3D result3 = MathUtils.sphericalToCartesianLocalSpaceAdjusted(input3);
        assertNotNull(result3);
        assertEquals(100, result3.getX(), epsilon);
        assertEquals(0, result3.getY(), epsilon);
        assertEquals(0, result3.getZ(), epsilon);
    }

//    @Test
//    public void testSphericalToCartesianLocalSpaceUnadjusted()
//    {
//        System.out.println("getsphericalToCartesianLocalSpaceUnadjusted");
//        
//        double radius = 100;
//        double epsilon = 0.001;
//        
//        PolarCoordinate input1 = new PolarCoordinate(0, 0, radius);
//        Point3D result1 = MathUtils.sphericalToCartesianLocalSpaceUnadjusted(input1);
//        assertNotNull(result1);
//        assertEquals(0, result1.getX(), epsilon);
//        assertEquals(0, result1.getY(), epsilon);
//        assertEquals(-100, result1.getZ(), epsilon);
//
//        PolarCoordinate input2 = new PolarCoordinate(Math.PI / 4, 0, radius);
//        Point3D result2 = MathUtils.sphericalToCartesianLocalSpaceAdjusted(input2);
//        assertNotNull(result2);
//        assertEquals(0, result2.getX(), epsilon);
//        assertEquals(-70.7106, result2.getY(), epsilon);
//        assertEquals(-70.7106, result2.getZ(), epsilon);
//    }
    /**
     * Test of getOrthogonalLineToLinePoints method, of class MathUtils.
     */
    @Test
    public void testGetOrthogonalLineToLinePoints()
    {
        System.out.println("getOrthogonalLineToLinePoints");
        double orthogonalLength = 3;
        Vector2D startPoint = new Vector2D(0, 5);
        Vector2D endPoint = new Vector2D(10, 5);
        Vector2D expectedStartPoint = new Vector2D(5, 8);
        Vector2D expectedEndPoint = new Vector2D(5, 2);
        Segment result = MathUtils.getOrthogonalLineToLinePoints(orthogonalLength, startPoint, endPoint);
        assertEquals(expectedStartPoint, result.getStart());
        assertEquals(expectedEndPoint, result.getEnd());
    }

    /**
     * Test of getOrthogonalLineToLinePoints method, of class MathUtils.
     */
    @Test
    public void testGetOrthogonalLineToLinePointsReverseDirection()
    {
        System.out.println("getOrthogonalLineToLinePoints");
        double orthogonalLength = 3;
        Vector2D startPoint = new Vector2D(0, -3);
        Vector2D endPoint = new Vector2D(10, -3);
        Vector2D expectedStartPoint = new Vector2D(5, -6);
        Vector2D expectedEndPoint = new Vector2D(5, 0);
        Segment result = MathUtils.getOrthogonalLineToLinePoints(orthogonalLength, endPoint, startPoint);
        assertEquals(expectedStartPoint, result.getStart());
        assertEquals(expectedEndPoint, result.getEnd());
    }

    /**
     * Test of getSegmentIntersection method, of class MathUtils.
     */
    @Test
    public void testGetSegmentIntersection()
    {
        System.out.println("getSegmentIntersection");

        Vector2D firstLineStart = new Vector2D(0, 0);
        Vector2D firstLineEnd = new Vector2D(10, 0);
        Line firstLine = new Line(firstLineStart, firstLineEnd, 1e-12);
        Segment firstSegment = new Segment(firstLineStart, firstLineEnd, firstLine);

        Vector2D secondLineStart = new Vector2D(5, 5);
        Vector2D secondLineEnd = new Vector2D(5, -5);
        Line secondLine = new Line(secondLineStart, secondLineEnd, 1e-12);
        Segment secondSegment = new Segment(secondLineStart, secondLineEnd, secondLine);

        Vector2D thirdLineStart = new Vector2D(15, 5);
        Vector2D thirdLineEnd = new Vector2D(15, -5);
        Line thirdLine = new Line(thirdLineStart, thirdLineEnd, 1e-12);
        Segment thirdSegment = new Segment(thirdLineStart, thirdLineEnd, thirdLine);

        Vector2D expResult = new Vector2D(5, 0);
        Vector2D result = MathUtils.getSegmentIntersection(firstSegment, secondSegment);

        assertEquals(expResult, result);

        Vector2D nullResult = MathUtils.getSegmentIntersection(firstSegment, thirdSegment);
        assertNull(nullResult);
    }

    /**
     * Test of doesPointLieWithinSegment method, of class MathUtils.
     */
    @Test
    public void testDoesPointLieWithinSegment()
    {
        System.out.println("doesPointLieWithinSegment");
        Vector2D passPoint = new Vector2D(107.47999999999999, 64.53900000000002);
        Vector2D failPoint = new Vector2D(107.47999999999999, 61.53900000000002);

        Vector2D firstLineStart = new Vector2D(107.48, 65.55500005626678);
        Vector2D firstLineEnd = new Vector2D(107.48, 62.322999943733215);

        Line firstLine = new Line(firstLineStart, firstLineEnd, 1e-12);
        Segment firstSegment = new Segment(firstLineStart, firstLineEnd, firstLine);

        boolean passResult = MathUtils.doesPointLieWithinSegment(passPoint, firstSegment);

        assertTrue(passResult);

        boolean failResult = MathUtils.doesPointLieWithinSegment(failPoint, firstSegment);
        assertFalse(failResult);
    }

}
