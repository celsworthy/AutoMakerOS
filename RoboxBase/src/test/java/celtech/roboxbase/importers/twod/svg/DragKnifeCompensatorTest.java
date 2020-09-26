package celtech.roboxbase.importers.twod.svg;

import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.StylusScribeNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.TravelNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Movement;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.MovementProvider;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Renderable;
import java.util.ArrayList;
import java.util.List;
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
public class DragKnifeCompensatorTest
{

    public DragKnifeCompensatorTest()
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
     * Test of doCompensation method, of class DragKnifeCompensator.
     */
    @Test
    public void testDoCompensation()
    {
        System.out.println("doCompensation");
        List<GCodeEventNode> uncompensatedParts = new ArrayList<>();

        TravelNode travelToStart = new TravelNode();
        travelToStart.getMovement().setX(100);
        travelToStart.getMovement().setY(100);
        uncompensatedParts.add(travelToStart);

        StylusScribeNode firstCut = new StylusScribeNode();
        firstCut.getMovement().setX(100);
        firstCut.getMovement().setY(110);
        uncompensatedParts.add(firstCut);

        StylusScribeNode secondCut = new StylusScribeNode();
        secondCut.getMovement().setX(110);
        secondCut.getMovement().setY(110);
        uncompensatedParts.add(secondCut);

        StylusScribeNode thirdCut = new StylusScribeNode();
        thirdCut.getMovement().setX(110);
        thirdCut.getMovement().setY(100);
        uncompensatedParts.add(thirdCut);

        StylusScribeNode fourthCut = new StylusScribeNode();
        fourthCut.getMovement().setX(100);
        fourthCut.getMovement().setY(100);
        uncompensatedParts.add(fourthCut);

        double forwards_value = 2.0;
        List<GCodeEventNode> expResult = null;
        DragKnifeCompensator dnc = new DragKnifeCompensator();
        List<GCodeEventNode> result = dnc.doCompensation(uncompensatedParts, forwards_value);

        result.forEach(node ->
        {
            if (node instanceof Renderable)
            {
                System.out.println(((Renderable) node).renderForOutput());
            }

            if (node instanceof TravelNode)
            {
                TravelNode travelNode = (TravelNode) node;

                if (travelNode.getMovement().isZSet())
                {
                    System.out.println(" To X" + travelNode.getMovement().getX() + " Y" + travelNode.getMovement().getY() + " Z" + travelNode.getMovement().getZ());
                } else
                {
                    System.out.println(" To X" + travelNode.getMovement().getX() + " Y" + travelNode.getMovement().getY());
                }
            }
        });
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

}
