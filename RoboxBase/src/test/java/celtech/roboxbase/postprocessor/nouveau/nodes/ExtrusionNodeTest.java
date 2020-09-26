package celtech.roboxbase.postprocessor.nouveau.nodes;

import celtech.roboxbase.postprocessor.nouveau.nodes.ExtrusionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.nodeFunctions.DurationCalculationException;
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
public class ExtrusionNodeTest
{

    public ExtrusionNodeTest()
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
     * Test of clone method, of class ExtrusionNode.
     */
    @Test
    public void testClone() throws Exception
    {
        System.out.println("clone");
        ExtrusionNode instance = new ExtrusionNode();
        instance.getNozzlePosition().setB(4);
        instance.getMovement().setX(1);
        instance.getMovement().setY(2);
        instance.getMovement().setZ(3);
        instance.getFeedrate().setFeedRate_mmPerMin(14);
        instance.getExtrusion().setD(5);
        instance.getExtrusion().setE(6);

        ExtrusionNode result = instance.clone();
        double epsilon = 0.001;
        assertEquals(4, result.getNozzlePosition().getB(), epsilon);
        assertEquals(1, result.getMovement().getX(), epsilon);
        assertEquals(2, result.getMovement().getY(), epsilon);
        assertEquals(3, result.getMovement().getZ(), epsilon);
        assertEquals(14, result.getFeedrate().getFeedRate_mmPerMin(), epsilon);
        assertEquals(5, result.getExtrusion().getD(), epsilon);
        assertEquals(6, result.getExtrusion().getE(), epsilon);
    }

    /**
     * Test of timeToReach method, of class ExtrusionNode.
     */
    @Test
    public void testTimeToReach()
    {
        System.out.println("timeToReach");

        ExtrusionNode sourceNode = new ExtrusionNode();
        sourceNode.getMovement().setX(0);
        sourceNode.getMovement().setY(0);
        sourceNode.getFeedrate().setFeedRate_mmPerMin(600);

        ExtrusionNode destinationNode = new ExtrusionNode();
        destinationNode.getMovement().setX(10);
        destinationNode.getMovement().setY(0);
        destinationNode.getFeedrate().setFeedRate_mmPerMin(0);

        double result = -1;

        try
        {
            result = sourceNode.timeToReach(destinationNode);
        } catch (DurationCalculationException ex)
        {
            fail("Exception during test");
        }

        assertEquals(1, result, 0.0);

        //Should be half the time
        sourceNode.getMovement().setX(0);
        sourceNode.getMovement().setY(0);
        sourceNode.getFeedrate().setFeedRate_mmPerMin(6000);
        destinationNode.getMovement().setX(10);
        destinationNode.getMovement().setY(0);
        destinationNode.getFeedrate().setFeedRate_mmPerMin(0);

        double result2 = -1;

        try
        {
            result2 = sourceNode.timeToReach(destinationNode);
        } catch (DurationCalculationException ex)
        {
            fail("Exception during test");
        }

        assertEquals(0.1, result2, 0.0);

        sourceNode.getMovement().setX(0);
        sourceNode.getMovement().setY(0);
        sourceNode.getFeedrate().setFeedRate_mmPerMin(600);
        destinationNode.getMovement().setX(3);
        destinationNode.getMovement().setY(4);
        destinationNode.getFeedrate().setFeedRate_mmPerMin(0);

        double result3 = -1;

        try
        {
            result3 = sourceNode.timeToReach(destinationNode);
        } catch (DurationCalculationException ex)
        {
            fail("Exception during test");
        }

        assertEquals(0.5, result3, 0.0);
    }
}
