package celtech.roboxbase.postprocessor.nouveau;

import celtech.roboxbase.postprocessor.nouveau.RoboxGCodeParser;
import celtech.roboxbase.postprocessor.nouveau.nodes.ExtrusionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.LayerChangeDirectiveNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.LayerNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.MCodeNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.NozzleValvePositionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.RetractNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.UnretractNode;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.parserunners.BasicParseRunner;
import org.parboiled.parserunners.TracingParseRunner;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.Var;

/**
 *
 * @author Ian
 */
public class RoboxGCodeParserTest
{

    public RoboxGCodeParserTest()
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
    public void testRetractDirective()
    {
        System.out.println("RetractDirective");
        RoboxGCodeParser gcodeParser = Parboiled.createParser(RoboxGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.RetractDirective());

        String testString = "G1 F1800 E-0.5\n";
        ParsingResult result = runner.run(testString);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(1, result.valueStack.size());
        assertTrue(result.valueStack.peek(0) instanceof RetractNode);
        assertEquals(-0.5, ((RetractNode) result.valueStack.peek(0)).getExtrusion().getE(), 0.001);
        assertEquals(0, ((RetractNode) result.valueStack.peek(0)).getExtrusion().getD(), 0.001);
        assertEquals(1800, ((RetractNode) result.valueStack.peek(0)).getFeedrate().getFeedRate_mmPerMin(), 0.001);
    }

    @Test
    public void testNozzleControlDirective_FloatingPoint()
    {
        System.out.println("NozzleControlDirective");
        RoboxGCodeParser gcodeParser = Parboiled.createParser(RoboxGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.NozzleControlDirective());

        String testString = "G1 B0.5\n";
        ParsingResult result = runner.run(testString);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(1, result.valueStack.size());
        assertTrue(result.valueStack.peek(0) instanceof NozzleValvePositionNode);
        assertEquals(0.5, ((NozzleValvePositionNode) result.valueStack.peek(0)).getNozzlePosition().getB(), 0.001);
    }

    @Test
    public void testNozzleControlDirective_Integer()
    {
        System.out.println("NozzleControlDirective");
        RoboxGCodeParser gcodeParser = Parboiled.createParser(RoboxGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.NozzleControlDirective());

        String testString = "G1 B1\n";
        ParsingResult result = runner.run(testString);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(1, result.valueStack.size());
        assertTrue(result.valueStack.peek(0) instanceof NozzleValvePositionNode);
        assertEquals(1, ((NozzleValvePositionNode) result.valueStack.peek(0)).getNozzlePosition().getB(), 0.001);
    }

    @Test
    public void testNozzleControlDirective_G0Integer()
    {
        System.out.println("NozzleControlDirective");
        RoboxGCodeParser gcodeParser = Parboiled.createParser(RoboxGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.NozzleControlDirective());

        String testString = "G0 B1\n";
        ParsingResult result = runner.run(testString);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(1, result.valueStack.size());
        assertTrue(result.valueStack.peek(0) instanceof NozzleValvePositionNode);
        assertEquals(1, ((NozzleValvePositionNode) result.valueStack.peek(0)).getNozzlePosition().getB(), 0.001);
    }

    @Test
    public void testMCode_SOnly()
    {
        System.out.println("MCode");
        RoboxGCodeParser gcodeParser = Parboiled.createParser(RoboxGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.MCode());

        String testString = "M104 S\n";
        ParsingResult result = runner.run(testString);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(1, result.valueStack.size());
        assertTrue(result.valueStack.peek(0) instanceof MCodeNode);
        assertEquals(104, ((MCodeNode) result.valueStack.peek(0)).getMNumber());
        assertTrue(((MCodeNode) result.valueStack.peek(0)).isSOnly());
        assertFalse(((MCodeNode) result.valueStack.peek(0)).isSAndNumber());
        assertFalse(((MCodeNode) result.valueStack.peek(0)).isTOnly());
        assertFalse(((MCodeNode) result.valueStack.peek(0)).isTAndNumber());
    }

    @Test
    public void testMCode_SValue()
    {
        System.out.println("MCode");
        RoboxGCodeParser gcodeParser = Parboiled.createParser(RoboxGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.MCode());

        String testString = "M104 S32\n";
        ParsingResult result = runner.run(testString);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(1, result.valueStack.size());
        assertTrue(result.valueStack.peek(0) instanceof MCodeNode);
        assertEquals(104, ((MCodeNode) result.valueStack.peek(0)).getMNumber());
        assertFalse(((MCodeNode) result.valueStack.peek(0)).isSOnly());
        assertTrue(((MCodeNode) result.valueStack.peek(0)).isSAndNumber());
        assertEquals(32, ((MCodeNode) result.valueStack.peek(0)).getSNumber());
        assertFalse(((MCodeNode) result.valueStack.peek(0)).isTOnly());
        assertFalse(((MCodeNode) result.valueStack.peek(0)).isTAndNumber());
    }

    @Test
    public void testMCode_TOnly()
    {
        System.out.println("MCode");
        RoboxGCodeParser gcodeParser = Parboiled.createParser(RoboxGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.MCode());

        String testString = "M104 T\n";
        ParsingResult result = runner.run(testString);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(1, result.valueStack.size());
        assertTrue(result.valueStack.peek(0) instanceof MCodeNode);
        assertEquals(104, ((MCodeNode) result.valueStack.peek(0)).getMNumber());
        assertFalse(((MCodeNode) result.valueStack.peek(0)).isSOnly());
        assertFalse(((MCodeNode) result.valueStack.peek(0)).isSAndNumber());
        assertTrue(((MCodeNode) result.valueStack.peek(0)).isTOnly());
        assertFalse(((MCodeNode) result.valueStack.peek(0)).isTAndNumber());
    }

    @Test
    public void testMCode_TValue()
    {
        System.out.println("MCode");
        RoboxGCodeParser gcodeParser = Parboiled.createParser(RoboxGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.MCode());

        String testString = "M104 T32\n";
        ParsingResult result = runner.run(testString);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(1, result.valueStack.size());
        assertTrue(result.valueStack.peek(0) instanceof MCodeNode);
        assertEquals(104, ((MCodeNode) result.valueStack.peek(0)).getMNumber());
        assertFalse(((MCodeNode) result.valueStack.peek(0)).isSOnly());
        assertFalse(((MCodeNode) result.valueStack.peek(0)).isSAndNumber());
        assertFalse(((MCodeNode) result.valueStack.peek(0)).isTOnly());
        assertTrue(((MCodeNode) result.valueStack.peek(0)).isTAndNumber());
        assertEquals(32, ((MCodeNode) result.valueStack.peek(0)).getTNumber());
    }

    @Test
    public void testMCode_STValue()
    {
        System.out.println("MCode");
        RoboxGCodeParser gcodeParser = Parboiled.createParser(RoboxGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.MCode());

        String testString = "M104 S13 T32\n";
        ParsingResult result = runner.run(testString);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(1, result.valueStack.size());
        assertTrue(result.valueStack.peek(0) instanceof MCodeNode);
        assertEquals(104, ((MCodeNode) result.valueStack.peek(0)).getMNumber());
        assertFalse(((MCodeNode) result.valueStack.peek(0)).isSOnly());
        assertTrue(((MCodeNode) result.valueStack.peek(0)).isSAndNumber());
        assertEquals(13, ((MCodeNode) result.valueStack.peek(0)).getSNumber());
        assertFalse(((MCodeNode) result.valueStack.peek(0)).isTOnly());
        assertTrue(((MCodeNode) result.valueStack.peek(0)).isTAndNumber());
        assertEquals(32, ((MCodeNode) result.valueStack.peek(0)).getTNumber());
    }

    @Test
    public void testMCode_STNoValue()
    {
        System.out.println("MCode");
        RoboxGCodeParser gcodeParser = Parboiled.createParser(RoboxGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.MCode());

        String testString = "M104 S T; Go to nozzle temperature from loaded reel - don't wait\n";
        ParsingResult result = runner.run(testString);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(1, result.valueStack.size());
        assertTrue(result.valueStack.peek(0) instanceof MCodeNode);
        assertEquals(104, ((MCodeNode) result.valueStack.peek(0)).getMNumber());
        assertTrue(((MCodeNode) result.valueStack.peek(0)).isSOnly());
        assertFalse(((MCodeNode) result.valueStack.peek(0)).isSAndNumber());
        assertTrue(((MCodeNode) result.valueStack.peek(0)).isTOnly());
        assertFalse(((MCodeNode) result.valueStack.peek(0)).isTAndNumber());
    }

    @Test
    public void extrusionDirective()
    {
        RoboxGCodeParser gcodeParser = Parboiled.createParser(RoboxGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.ExtrusionDirective());

        String commentText = "; Remainder pre close towards end";
        String eOnlyExtrude = "G1 F1140 X86.369 Y66.648 E0.09495 " + commentText + "\n";
        ParsingResult inputData1Result = runner.run(eOnlyExtrude);

        assertFalse(inputData1Result.hasErrors());
        assertTrue(inputData1Result.matched);
        assertTrue(inputData1Result.valueStack.peek(0) instanceof ExtrusionNode);
        assertEquals(86.369, ((ExtrusionNode) inputData1Result.valueStack.peek(0)).getMovement().getX(), 0.001);
        assertEquals(66.648, ((ExtrusionNode) inputData1Result.valueStack.peek(0)).getMovement().getY(), 0.001);
        assertEquals(0, ((ExtrusionNode) inputData1Result.valueStack.peek(0)).getMovement().getZ(), 0.001);
        assertEquals(0.09495, ((ExtrusionNode) inputData1Result.valueStack.peek(0)).getExtrusion().getE(), 0.001);
        assertEquals(0, ((ExtrusionNode) inputData1Result.valueStack.peek(0)).getExtrusion().getD(), 0.0001);
        assertEquals(1140, ((ExtrusionNode) inputData1Result.valueStack.peek(0)).getFeedrate().getFeedRate_mmPerMin(), 0.001);
        assertEquals(commentText, ((ExtrusionNode) inputData1Result.valueStack.peek(0)).getCommentText());

        gcodeParser.resetLayer();

        String dOnlyExtrude = "G1 F100 X1.4 Y12.3 D1.3\n";
        ParsingResult dOnlyExtrudeResult = runner.run(dOnlyExtrude);
        assertFalse(dOnlyExtrudeResult.hasErrors());
        assertTrue(dOnlyExtrudeResult.matched);
        assertEquals(1.4, ((ExtrusionNode) dOnlyExtrudeResult.valueStack.peek(0)).getMovement().getX(), 0.001);
        assertEquals(12.3, ((ExtrusionNode) dOnlyExtrudeResult.valueStack.peek(0)).getMovement().getY(), 0.001);
        assertEquals(0, ((ExtrusionNode) dOnlyExtrudeResult.valueStack.peek(0)).getMovement().getZ(), 0.001);
        assertEquals(0, ((ExtrusionNode) dOnlyExtrudeResult.valueStack.peek(0)).getExtrusion().getE(), 0.001);
        assertEquals(1.3, ((ExtrusionNode) dOnlyExtrudeResult.valueStack.peek(0)).getExtrusion().getD(), 0.0001);
    }

    @Test
    public void testLayerChangeDirective()
    {
        System.out.println("LayerChangeDirective");
        RoboxGCodeParser gcodeParser = Parboiled.createParser(RoboxGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.LayerChangeDirective());

        String commentText = "; Blah";
        String testString = "G1 F12000 X85.372 Y65.599 Z0.300" + commentText + "\n";
        ParsingResult result = runner.run(testString);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(1, result.valueStack.size());
        assertTrue(result.valueStack.peek(0) instanceof LayerChangeDirectiveNode);
        assertEquals(85.372, ((LayerChangeDirectiveNode) result.valueStack.peek(0)).getMovement().getX(), 0.001);
        assertEquals(65.599, ((LayerChangeDirectiveNode) result.valueStack.peek(0)).getMovement().getY(), 0.001);
        assertEquals(0.3, ((LayerChangeDirectiveNode) result.valueStack.peek(0)).getMovement().getZ(), 0.001);
        assertEquals(12000, ((LayerChangeDirectiveNode) result.valueStack.peek(0)).getFeedrate().getFeedRate_mmPerMin(), 0.001);
        assertEquals(commentText, ((LayerChangeDirectiveNode) result.valueStack.peek(0)).getCommentText());
    }
    
    @Test
    public void testUnretractDirective()
    {
        System.out.println("UnretractDirective");
        RoboxGCodeParser gcodeParser = Parboiled.createParser(RoboxGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.UnretractDirective());

        String commentText = "; Replenishing elided extrusion";
        String testString = "G1 F150 D0.30000 " + commentText + "\n";
        ParsingResult result = runner.run(testString);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(1, result.valueStack.size());
        assertTrue(result.valueStack.peek(0) instanceof UnretractNode);
        assertFalse(((UnretractNode) result.valueStack.peek(0)).getExtrusion().isEInUse());
        assertTrue(((UnretractNode) result.valueStack.peek(0)).getExtrusion().isDInUse());
        assertEquals(0.3, ((UnretractNode) result.valueStack.peek(0)).getExtrusion().getD(), 0.001);
        assertEquals(150, ((UnretractNode) result.valueStack.peek(0)).getFeedrate().getFeedRate_mmPerMin(), 0.001);
        assertEquals(commentText, ((UnretractNode) result.valueStack.peek(0)).getCommentText());
    }
}
