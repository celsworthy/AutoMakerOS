package celtech.roboxbase.importers.twod.svg;

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
public class ShortestArcTest
{

    public ShortestArcTest()
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

    @Test
    public void testShortestArc1()
    {
        ShortestArc shortestArc = new ShortestArc(2.14, -2.14);

        assertEquals(2.00318, shortestArc.getAngularDifference(), 0.001);
        assertTrue(shortestArc.getStepValue() > 0);
    }

    @Test
    public void testShortestArc2()
    {
        ShortestArc shortestArc = new ShortestArc(2.14, 2.8);

        assertEquals(0.66, shortestArc.getAngularDifference(), 0.001);
        assertTrue(shortestArc.getStepValue() > 0);
    }

}
