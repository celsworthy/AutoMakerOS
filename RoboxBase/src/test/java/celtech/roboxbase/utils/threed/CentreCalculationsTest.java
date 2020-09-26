package celtech.roboxbase.utils.threed;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ianhudson
 */
public class CentreCalculationsTest
{
    
    public CentreCalculationsTest()
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
     * Test of basic centre method without input, of class CentreCalculations.
     */
    @Test
    public void testInit()
    {
        System.out.println("init");
        CentreCalculations instance = new CentreCalculations();
        Vector3D expectedResult = new Vector3D(0,0,0);
        Vector3D centre = instance.getResult();
        assertEquals(expectedResult, centre);
    }

    /**
     * Test of basic centre method with a single input, of class CentreCalculations.
     */
    @Test
    public void testSinglePointSeparate()
    {
        System.out.println("single point separate");
        CentreCalculations instance = new CentreCalculations();
        Vector3D expectedResult = new Vector3D(10,10,10);
        instance.processPoint(10, 10, 10);
        Vector3D centre = instance.getResult();
        assertEquals(expectedResult, centre);
    }

    /**
     * Test of basic centre method with a single input, of class CentreCalculations.
     */
    @Test
    public void testSinglePointCombined()
    {
        System.out.println("single point combined");
        CentreCalculations instance = new CentreCalculations();
        Vector3D expectedResult = new Vector3D(10,10,10);
        instance.processPoint(expectedResult);
        Vector3D centre = instance.getResult();
        assertEquals(expectedResult, centre);
    }

    /**
     * Test of reset method, of class CentreCalculations.
     */
    @Test
    public void testReset()
    {
        System.out.println("reset");
        CentreCalculations instance = new CentreCalculations();
        Vector3D expectedResult = new Vector3D(0,0,0);
        instance.processPoint(10, 10, 10);
        instance.reset();
        Vector3D centre = instance.getResult();
        assertEquals(expectedResult, centre);
    }

    /**
     * Test of centring algorithm, of class CentreCalculations.
     */
    @Test
    public void testCentreAlgo()
    {
        System.out.println("centre algorithm");
        CentreCalculations instance = new CentreCalculations();
        Vector3D expectedResult = new Vector3D(5,5,5);
        instance.processPoint(0, 0, 0);
        instance.processPoint(10, 10, 10);
        Vector3D centre = instance.getResult();
        assertEquals(expectedResult, centre);
    }
}
