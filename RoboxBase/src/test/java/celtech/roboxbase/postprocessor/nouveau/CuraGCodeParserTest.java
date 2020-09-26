package celtech.roboxbase.postprocessor.nouveau;

import celtech.roboxbase.postprocessor.nouveau.Slic3rGCodeParser;
import celtech.roboxbase.postprocessor.nouveau.CuraGCodeParser;
import celtech.roboxbase.postprocessor.nouveau.nodes.ExtrusionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.FillSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeDirectiveNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.InnerPerimeterSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.LayerChangeDirectiveNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.LayerNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.MCodeNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.OuterPerimeterSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.RetractNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.ObjectDelineationNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.OrphanObjectDelineationNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.OrphanSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.SupportSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.TravelNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.UnretractNode;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.BasicParseRunner;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.Var;

/**
 *
 * @author Ian
 */
public class CuraGCodeParserTest
{

    public CuraGCodeParserTest()
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
    public void floatingPointNumberTest()
    {
        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.FloatingPointNumber());

        String positiveNumber = "1.20\n";
        ParsingResult positiveNumberResult = runner.run(positiveNumber);
        assertFalse(positiveNumberResult.hasErrors());
        assertTrue(positiveNumberResult.matched);

        String negativeNumber = "-1.20\n";
        gcodeParser = Parboiled.createParser(CuraGCodeParser.class);
        runner = new BasicParseRunner<>(gcodeParser.FloatingPointNumber());
        ParsingResult negativeNumberResult = runner.run(negativeNumber);
        assertFalse(negativeNumberResult.hasErrors());
        assertTrue(negativeNumberResult.matched);
    }

    @Test
    public void negativeFloatingPointNumberTest()
    {
        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.NegativeFloatingPointNumber());

        String positiveNumber = "1.20\n";
        ParsingResult positiveNumberResult = runner.run(positiveNumber);
        assertFalse(positiveNumberResult.hasErrors());
        assertFalse(positiveNumberResult.matched);

        String negativeNumber = "-1.20\n";
        ParsingResult negativeNumberResult = runner.run(negativeNumber);
        assertFalse(negativeNumberResult.hasErrors());
        assertTrue(negativeNumberResult.matched);
    }

    @Test
    public void positiveFloatingPointNumberTest()
    {
        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.PositiveFloatingPointNumber());

        String positiveNumber = "1.20\n";
        ParsingResult positiveNumberResult = runner.run(positiveNumber);
        assertFalse(positiveNumberResult.hasErrors());
        assertTrue(positiveNumberResult.matched);

        String negativeNumber = "-1.20\n";
        ParsingResult negativeNumberResult = runner.run(negativeNumber);
        assertFalse(negativeNumberResult.hasErrors());
        assertFalse(negativeNumberResult.matched);
    }

    @Test
    public void feedrateInteger()
    {
        String inputData = "F12000";
        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class);

        Var<Integer> feedrateResult = new Var<>();
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.Feedrate(feedrateResult));
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(0, result.valueStack.size());
        assertEquals(12000, feedrateResult.get().intValue());
    }

    @Test
    public void comment()
    {
        String commentPart = " move to first perimeter point ";
        String inputData = " ;" + commentPart;
        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class);

        Var<String> commentResult = new Var<>();
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.Comment(commentResult));
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(0, result.valueStack.size());
        assertEquals(commentPart, commentResult.get());
    }

    @Test
    public void commentDirective()
    {
        String inputData = ";Hello this is a comment \n";
        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class
        );
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.CommentDirective());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);

    }

    @Test
    public void commentDirectiveNoMatch()
    {
        String inputData = InnerPerimeterSectionNode.designator + "\n";
        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class
        );
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.CommentDirective());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertFalse(result.matched);
    }

    @Test
    public void travelDirective()
    {
        String inputData = "G0 F12000 X88.302 Y42.421\n";
        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class);
        gcodeParser.setPrintVolumeBounds(210, 150, 100);

        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.TravelDirective());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(1, result.valueStack.size());
        assertTrue(result.valueStack.peek(0) instanceof TravelNode);
        assertEquals(88.302, ((TravelNode) result.valueStack.peek(0)).getMovement().getX(), 0.001);
        assertEquals(42.421, ((TravelNode) result.valueStack.peek(0)).getMovement().getY(), 0.001);
        assertEquals(12000, ((TravelNode) result.valueStack.peek(0)).getFeedrate().getFeedRate_mmPerMin(), 0.001);

        String inputData2 = "G1 F1800 X116.392 Y99.457 E1.96742\n";
        ParsingResult result2 = runner.run(inputData2);
        assertFalse(result2.hasErrors());
        assertFalse(result2.matched);

        String inputData3 = "G0 F12000 X146.378 Y92.092\n";
        ParsingResult result3 = runner.run(inputData3);

        assertFalse(result3.hasErrors());
        assertTrue(result3.matched);
        assertEquals(1, result3.valueStack.size());
        assertTrue(result3.valueStack.peek(0) instanceof TravelNode);
        assertEquals(146.378, ((TravelNode) result2.valueStack.peek(0)).getMovement().getX(), 0.001);
        assertEquals(92.092, ((TravelNode) result2.valueStack.peek(0)).getMovement().getY(), 0.001);
        assertEquals(12000, ((TravelNode) result2.valueStack.peek(0)).getFeedrate().getFeedRate_mmPerMin(), 0.001);

        //Don't match layer change directives
        String inputData4 = "G0 F12000 X75.479 Y64.037 Z1.900\n";
        ParsingResult result4 = runner.run(inputData4);
        assertFalse(result4.hasErrors());
        assertFalse(result4.matched);

        String inputData5 = "G0 F12000 X146.378 Y92.092\n";
        ParsingResult result5 = runner.run(inputData5);

        assertFalse(result5.hasErrors());
        assertTrue(result5.matched);
        assertEquals(1, result5.valueStack.size());
        assertTrue(result5.valueStack.peek(0) instanceof TravelNode);
        assertEquals(146.378, ((TravelNode) result5.valueStack.peek(0)).getMovement().getX(), 0.001);
        assertEquals(92.092, ((TravelNode) result5.valueStack.peek(0)).getMovement().getY(), 0.001);
        assertEquals(12000, ((TravelNode) result5.valueStack.peek(0)).getFeedrate().getFeedRate_mmPerMin(), 0.001);
    }

    @Test
    public void retractDirective()
    {
        String inputData = "G1 F1800 E-0.50000\n";
        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.RetractDirective());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(1, result.valueStack.size());
        assertTrue(result.valueStack.peek(0) instanceof RetractNode);
        assertEquals(-0.5, ((RetractNode) result.valueStack.peek(0)).getExtrusion().getE(), 0.001);
        assertEquals(0, ((RetractNode) result.valueStack.peek(0)).getExtrusion().getD(), 0.001);
        assertEquals(1800, ((RetractNode) result.valueStack.peek(0)).getFeedrate().getFeedRate_mmPerMin(), 0.001);
    }

    @Test
    public void unretractDirective()
    {
        String inputData = "G1 F1800 E0.70000\n";
        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.UnretractDirective());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(1, result.valueStack.size());
        assertTrue(result.valueStack.peek(0) instanceof UnretractNode);
        assertEquals(0.7, ((UnretractNode) result.valueStack.peek(0)).getExtrusion().getE(), 0.001);
        assertEquals(0, ((UnretractNode) result.valueStack.peek(0)).getExtrusion().getD(), 0.001);
        assertEquals(1800, ((UnretractNode) result.valueStack.peek(0)).getFeedrate().getFeedRate_mmPerMin(), 0.001);
    }

    @Test
    public void gcodeDirective()
    {
        String inputData = "G92\n";
        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class
        );
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.GCodeDirective());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(1, result.valueStack.size());
        assertTrue(result.valueStack.peek(0) instanceof GCodeDirectiveNode);
        assertEquals(92, ((GCodeDirectiveNode) result.valueStack.peek(0)).getGValue().intValue());
    }

    @Test
    public void mcodeDirective()
    {
        String inputData = "M107\n";
        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.MCode());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(1, result.valueStack.size());
        assertTrue(result.valueStack.peek(0) instanceof MCodeNode);
        assertEquals(107, ((MCodeNode) result.valueStack.peek(0)).getMNumber());
    }

    @Test
    public void extrusionDirective()
    {
        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class);
        gcodeParser.setPrintVolumeBounds(210, 150, 100);

        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.ExtrusionDirective());

        String eOnlyExtrude = "G1 X1.4 Y12.3 E1.3\n";
        ParsingResult inputData1Result = runner.run(eOnlyExtrude);

        assertFalse(inputData1Result.hasErrors());
        assertTrue(inputData1Result.matched);
        assertEquals(1.4, ((ExtrusionNode) inputData1Result.valueStack.peek(0)).getMovement().getX(), 0.001);
        assertEquals(12.3, ((ExtrusionNode) inputData1Result.valueStack.peek(0)).getMovement().getY(), 0.001);
        assertEquals(0, ((ExtrusionNode) inputData1Result.valueStack.peek(0)).getMovement().getZ(), 0.001);
        assertEquals(1.3, ((ExtrusionNode) inputData1Result.valueStack.peek(0)).getExtrusion().getE(), 0.001);
        assertEquals(0, ((ExtrusionNode) inputData1Result.valueStack.peek(0)).getExtrusion().getD(), 0.0001);

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

        gcodeParser.resetLayer();

        String spiralExtrude = "G1 F100 X1.4 Y12.3 Z0.3 D1.3\n";
        ParsingResult spiralExtrudeResult = runner.run(spiralExtrude);
        assertFalse(spiralExtrudeResult.hasErrors());
        assertTrue(spiralExtrudeResult.matched);
        assertEquals(1.4, ((ExtrusionNode) spiralExtrudeResult.valueStack.peek(0)).getMovement().getX(), 0.001);
        assertEquals(12.3, ((ExtrusionNode) spiralExtrudeResult.valueStack.peek(0)).getMovement().getY(), 0.001);
        assertEquals(0.3, ((ExtrusionNode) spiralExtrudeResult.valueStack.peek(0)).getMovement().getZ(), 0.001);
        assertEquals(0, ((ExtrusionNode) spiralExtrudeResult.valueStack.peek(0)).getExtrusion().getE(), 0.001);
        assertEquals(1.3, ((ExtrusionNode) spiralExtrudeResult.valueStack.peek(0)).getExtrusion().getD(), 0.0001);
    }

    @Test
    public void layerChangeDirective()
    {
        String inputData = "G0 Z1.3\n";
        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class
        );
        gcodeParser.setPrintVolumeBounds(210, 150, 100);

        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.LayerChangeDirective());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(1, result.valueStack.size());
        assertTrue(result.valueStack.peek(0) instanceof LayerChangeDirectiveNode);
        assertEquals(0, ((LayerChangeDirectiveNode) result.valueStack.peek(0)).getMovement().getX(), 0.001);
        assertEquals(0, ((LayerChangeDirectiveNode) result.valueStack.peek(0)).getMovement().getY(), 0.001);
        assertEquals(1.3, ((LayerChangeDirectiveNode) result.valueStack.peek(0)).getMovement().getZ(), 0.001);

        String inputData2 = "G0 F12000 X146.378 Y92.092\n";
        ParsingResult result2 = runner.run(inputData2);

        assertFalse(result2.hasErrors());
        assertFalse(result2.matched);

        String inputData3 = "G0 F12000 X75.479 Y64.037 Z1.900\n";
        ParsingResult result3 = runner.run(inputData3);

        assertFalse(result3.hasErrors());
        assertTrue(result3.matched);
        assertEquals(1, result3.valueStack.size());
        assertTrue(result3.valueStack.peek(0) instanceof LayerChangeDirectiveNode);
        assertEquals(75.479, ((LayerChangeDirectiveNode) result3.valueStack.peek(0)).getMovement().getX(), 0.001);
        assertEquals(64.037, ((LayerChangeDirectiveNode) result3.valueStack.peek(0)).getMovement().getY(), 0.001);
        assertEquals(1.9, ((LayerChangeDirectiveNode) result3.valueStack.peek(0)).getMovement().getZ(), 0.001);
        assertEquals(12000, ((LayerChangeDirectiveNode) result3.valueStack.peek(0)).getFeedrate().getFeedRate_mmPerMin(), 0.001);
    }

    @Test
    public void compoundTest()
    {
        String alternateInput = ";LAYER:0\n"
                + "M107\n"
                + "G1 F1800 E-0.50000\n"
                + "G0 F12000 X63.440 Y93.714 Z0.480\n"
                + ";TYPE:WALL-INNER\n"
                + "G1 F1800 E0.00000\n"
                + "G1 F1200 X63.569 Y94.143 E0.05379\n"
                + "G1 X63.398 Y94.154 E0.02058\n"
                + "G1 X63.353 Y93.880 E0.03334\n"
                + "G1 X63.309 Y93.825 E0.00846\n"
                + "G1 X63.440 Y93.714 E0.02062\n"
                + "G0 F12000 X63.120 Y93.197\n"
                + ";TYPE:WALL-OUTER\n"
                + "G1 F1200 X62.564 Y91.751 E0.18603\n"
                + "G1 X61.057 Y88.531 E0.42691\n"
                + "G1 X60.380 Y86.590 E0.24685\n"
                + "G1 X60.346 Y86.419 E0.02094\n"
                + "G1 X61.247 Y86.240 E0.11031\n";

        String inputData = ";LAYER:0\n"
                + "M107\n"
                + "G1 F1800 E-0.50000\n"
                + "G0 F12000 X88.302 Y42.421 Z1.020\n"
                + ";TYPE:WALL-INNER\n"
                + "G1 F1800 E0.00000\n"
                + "G1 F840 X115.304 Y42.421 E5.40403\n"
                + "G1 X115.304 Y114.420 E14.40948\n"
                + "G1 X88.302 Y114.420 E5.40403\n"
                + "G1 X88.302 Y42.421 E14.40948\n"
                + "G0 F12000 X87.302 Y41.421\n"
                + ";TYPE:WALL-OUTER\n"
                + "G1 F840 X116.304 Y41.421 E5.80430\n"
                + "G1 X116.304 Y115.420 E14.80975\n"
                + "G1 X87.302 Y115.420 E5.80430\n"
                + "G1 X87.302 Y41.421 E14.80975\n"
                + "G0 F12000 X87.902 Y41.931\n"
                + "G0 X88.782 Y42.820\n"
                + ";TYPE:FILL\n"
                + "G1 F840 X114.903 Y68.941 E5.91448\n"
                + "G0 F12000 X114.903 Y70.355\n"
                + "G1 F840 X88.700 Y44.153 E5.93294\n"
                + "G0 F12000 X88.700 Y45.567\n"
                + "G1 F840 X114.903 Y71.769 E5.93294\n"
                + "G0 F12000 X114.903 Y73.184\n";

        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class);
        gcodeParser.setPrintVolumeBounds(210, 150, 100);

        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.Layer());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        LayerNode layerNode = gcodeParser.getLayerNode();

        assertNotNull(layerNode);

        assertEquals(
                4, layerNode.getChildren().size());

        assertEquals(MCodeNode.class, layerNode.getChildren().get(0).getClass());
        assertEquals(RetractNode.class, layerNode.getChildren().get(1).getClass());
        assertEquals(LayerChangeDirectiveNode.class, layerNode.getChildren().get(2).getClass());
        assertEquals(OrphanObjectDelineationNode.class, layerNode.getChildren().get(3).getClass());

        OrphanObjectDelineationNode objectNode = (OrphanObjectDelineationNode) layerNode.getChildren().get(3);

        assertEquals(
                3, objectNode.getChildren().size());
        assertEquals(InnerPerimeterSectionNode.class, objectNode.getChildren().get(0).getClass());
        assertEquals(OuterPerimeterSectionNode.class, objectNode.getChildren().get(1).getClass());
        assertEquals(FillSectionNode.class, objectNode.getChildren().get(2).getClass());
    }

    @Test
    public void objectSectionTest()
    {
        String inputData = "T1\n"
                + ";TYPE:FILL\n"
                + "G1 F1800 E-0.50000\n"
                + "G0 F12000 X88.302 Y42.421 Z1.020\n"
                + "G1 F1800 E0.00000\n"
                + "G1 X12.3 Y14.5 E1.00000\n"
                + ";TYPE:WALL-OUTER\n"
                + "G1 X125.3 Y14.5 E1.00000\n";

        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class
        );
        gcodeParser.setPrintVolumeBounds(210, 150, 100);

        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.ObjectSection());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);

        assertFalse(result.valueStack.isEmpty());
        assertTrue(result.valueStack.peek() instanceof ObjectDelineationNode);

        ObjectDelineationNode node = (ObjectDelineationNode) result.valueStack.pop();
        assertEquals(2, node.getChildren().size());
        assertEquals(FillSectionNode.class, node.getChildren().get(0).getClass());
        assertEquals(OuterPerimeterSectionNode.class, node.getChildren().get(1).getClass());

        FillSectionNode fillNode = (FillSectionNode) node.getChildren().get(0);
        assertEquals(4, fillNode.getChildren().size());
        assertEquals(RetractNode.class, fillNode.getChildren().get(0).getClass());
        assertEquals(LayerChangeDirectiveNode.class, fillNode.getChildren().get(1).getClass());
        assertEquals(UnretractNode.class, fillNode.getChildren().get(2).getClass());
        assertEquals(ExtrusionNode.class, fillNode.getChildren().get(3).getClass());

        OuterPerimeterSectionNode outerNode = (OuterPerimeterSectionNode) node.getChildren().get(1);
        assertEquals(1, outerNode.getChildren().size());
        assertEquals(ExtrusionNode.class, outerNode.getChildren().get(0).getClass());
    }

    @Test
    public void objectSectionNoTriggerTest()
    {
        String inputData = ";TYPE:FILL\n"
                + "G1 F1800 E-0.50000\n"
                + "G0 F12000 X88.302 Y42.421 Z1.020\n"
                + "G1 F1800 E0.00000\n"
                + "G1 X12.3 Y14.5 E1.00000\n"
                + ";TYPE:WALL-OUTER\n"
                + "G1 X125.3 Y314.5 E1.00000\n";

        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class
        );
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.ObjectSection());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertFalse(result.matched);
    }

    @Test
    public void orphanObjectSectionTriggerTest()
    {
        String inputData = ";TYPE:FILL\n"
                + "G1 F1800 E-0.50000\n"
                + "G0 F12000 X88.302 Y42.421 Z1.020\n"
                + "G1 F1800 E0.00000\n"
                + "G1 X12.3 Y14.5 E1.00000\n"
                + ";TYPE:WALL-OUTER\n"
                + "G1 X125.3 Y34.5 E1.00000\n";

        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class
        );
        gcodeParser.setPrintVolumeBounds(210, 150, 100);

        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.OrphanObjectSection());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);

        assertFalse(result.valueStack.isEmpty());
        assertTrue(result.valueStack.peek() instanceof OrphanObjectDelineationNode);

        OrphanObjectDelineationNode node = (OrphanObjectDelineationNode) result.valueStack.pop();
        assertEquals(2, node.getChildren().size());
        assertEquals(FillSectionNode.class, node.getChildren().get(0).getClass());
        assertEquals(OuterPerimeterSectionNode.class, node.getChildren().get(1).getClass());

        FillSectionNode fillNode = (FillSectionNode) node.getChildren().get(0);
        assertEquals(4, fillNode.getChildren().size());
        assertEquals(RetractNode.class, fillNode.getChildren().get(0).getClass());
        assertEquals(LayerChangeDirectiveNode.class, fillNode.getChildren().get(1).getClass());
        assertEquals(UnretractNode.class, fillNode.getChildren().get(2).getClass());
        assertEquals(ExtrusionNode.class, fillNode.getChildren().get(3).getClass());

        OuterPerimeterSectionNode outerNode = (OuterPerimeterSectionNode) node.getChildren().get(1);
        assertEquals(1, outerNode.getChildren().size());
        assertEquals(ExtrusionNode.class, outerNode.getChildren().get(0).getClass());

    }

    @Test
    public void layerNode_multipleObjects()
    {
        String inputData = ";LAYER:0\n"
                + "M107\n"
                + "G1 F1800 E-0.50000\n"
                + "G0 F12000 X75.672 Y67.458 Z0.480\n"
                + ";TYPE:WALL-OUTER\n"
                + "G1 F1800 E0.00000\n"
                + "G1 F1200 X75.710 Y67.425 E0.00604\n"
                + "G1 X90.334 Y67.415 E1.75606\n"
                + "G1 X90.391 Y67.423 E0.00691\n"
                + "G1 X90.421 Y67.442 E0.00426\n"
                + "G1 X90.446 Y67.479 E0.00536\n"
                + "G1 X90.454 Y74.696 E0.86662\n"
                + "G1 X90.445 Y74.753 E0.00693\n"
                + "G1 X90.428 Y74.781 E0.00393\n"
                + "G1 X90.392 Y74.807 E0.00533\n"
                + "G1 X75.774 Y74.815 E1.75534\n"
                + "G1 X75.707 Y74.803 E0.00817\n"
                + "G1 X75.680 Y74.780 E0.00426\n"
                + "G1 X75.662 Y74.737 E0.00560\n"
                + "G1 X75.654 Y67.532 E0.86518\n"
                + "G1 X75.672 Y67.458 E0.00915\n"
                + "G0 F12000 X76.354 Y67.815\n"
                + "G1 F1200 X76.354 Y74.415 E0.79253\n"
                + "G1 X82.772 Y74.415 E0.77068\n"
                + "G1 X82.699 Y74.237 E0.02310\n"
                + "G1 X82.774 Y74.237 E0.00901\n"
                + "G1 X83.040 Y74.697 E0.06381\n"
                + "G1 X83.315 Y74.256 E0.06241\n"
                + "G1 X83.244 Y74.415 E0.02091\n"
                + "G1 X89.754 Y74.415 E0.78173\n"
                + "G1 X89.754 Y67.815 E0.79253\n"
                + "G1 X83.246 Y67.815 E0.78149\n"
                + "G1 X83.319 Y67.979 E0.02156\n"
                + "G1 X83.038 Y67.532 E0.06340\n"
                + "G1 X82.776 Y67.990 E0.06336\n"
                + "G1 X82.698 Y67.990 E0.00937\n"
                + "G1 X82.770 Y67.815 E0.02272\n"
                + "G1 X76.354 Y67.815 E0.77044\n"
                + "G1 F1800 E-0.50000\n"
                + "G0 F12000 X75.894 Y69.297\n"
                + ";TYPE:FILL\n"
                + "G1 F1800 E10.35819\n"
                + "G1 F1200 X76.113 Y69.516 E0.02479\n"
                + "G1 F1800 E-0.50000\n"
                + "G0 F12000 X75.897 Y71.185\n"
                + "G1 F1800 E10.38298\n"
                + "G1 F1200 X76.113 Y71.401 E0.02445\n"
                + "G1 F1800 E-0.50000\n"
                + "G0 F12000 X75.899 Y73.072\n"
                + "G1 F1800 E10.40744\n"
                + "G1 F1200 X76.113 Y73.286 E0.02423\n"
                + "G1 F1800 E-0.50000\n"
                + "G0 F12000 X89.993 Y73.970\n"
                + "G1 F1800 E10.43166\n"
                + "G1 F1200 X90.213 Y74.190 E0.02491\n"
                + "G0 F12000 X90.053 Y74.190\n"
                + "G0 X90.051 Y72.303\n"
                + "G0 X90.211 Y72.303\n"
                + "G1 F1200 X89.993 Y72.085 E0.02468\n"
                + "G1 F1800 E-0.50000\n"
                + "G0 F12000 X90.208 Y70.415\n"
                + "G1 F1800 E10.48125\n"
                + "G1 F1200 X89.993 Y70.200 E0.02434\n"
                + "G1 F1800 E-0.50000\n"
                + "G0 F12000 X90.206 Y68.527\n"
                + "G1 F1800 E10.50559\n"
                + "G1 F1200 X89.993 Y68.315 E0.02406\n"
                + "G92 E0\n"
                + "G1 F1800 E-14.50000\n"
                + "T1\n"
                + "G0 F12000 X108.579 Y67.566\n"
                + ";TYPE:WALL-OUTER\n"
                + "G1 F1800 E0.00000\n"
                + "G1 F1200 X108.582 Y67.535 E0.00374\n"
                + "G1 X108.599 Y67.497 E0.00500\n"
                + "G1 X108.608 Y67.485 E0.00180\n"
                + "G1 X108.643 Y67.461 E0.00510\n"
                + "G1 X123.265 Y67.452 E1.75582\n"
                + "G1 X123.316 Y67.461 E0.00622\n"
                + "G1 X123.346 Y67.480 E0.00426\n"
                + "G1 X123.371 Y67.517 E0.00536\n"
                + "G1 X123.379 Y74.728 E0.86590\n"
                + "G1 X123.370 Y74.790 E0.00752\n"
                + "G1 X123.353 Y74.818 E0.00393\n"
                + "G1 X123.317 Y74.844 E0.00533\n"
                + "G1 X108.699 Y74.852 E1.75534\n"
                + "G1 X108.642 Y74.843 E0.00693\n"
                + "G1 X108.627 Y74.835 E0.00204\n"
                + "G1 X108.606 Y74.818 E0.00324\n"
                + "G1 X108.588 Y74.790 E0.00400\n"
                + "G1 X108.579 Y67.566 E0.86746\n";

        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class
        );
        gcodeParser.setPrintVolumeBounds(210, 150, 100);

        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.Layer());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);

        LayerNode layerNode = gcodeParser.getLayerNode();

        assertEquals(5, layerNode.getChildren().size());
    }

    @Test
    public void objectNode_orphanSection()
    {
        String layer0 = ";LAYER:0\n"
                + "M107\n"
                + "G1 F1800 E-0.50000\n"
                + "G0 F12000 X75.672 Y67.458 Z0.480\n"
                + ";TYPE:WALL-OUTER\n"
                + "G1 F1800 E0.00000\n"
                + "G1 F1200 X75.710 Y67.425 E0.00604\n"
                + "G1 X90.334 Y67.415 E1.75606\n"
                + "G1 X90.391 Y67.423 E0.00691\n"
                + "G1 X90.421 Y67.442 E0.00426\n"
                + "G1 X90.446 Y67.479 E0.00536\n"
                + "G1 X90.454 Y74.696 E0.86662\n"
                + "G1 X90.445 Y74.753 E0.00693\n"
                + "G1 X90.428 Y74.781 E0.00393\n"
                + "G1 X83.038 Y67.532 E0.06340\n"
                + "G1 X82.776 Y67.990 E0.06336\n"
                + "G1 X82.698 Y67.990 E0.00937\n"
                + "G1 X82.770 Y67.815 E0.02272\n"
                + "G1 X76.354 Y67.815 E0.77044\n"
                + "G1 F1800 E-0.50000\n"
                + "G0 F12000 X75.894 Y69.297\n"
                + ";TYPE:FILL\n"
                + "G1 F1800 E10.35819\n"
                + "G1 F1200 X76.113 Y69.516 E0.02479\n"
                + "G1 F1800 E-0.50000\n"
                + "G0 F12000 X75.897 Y71.185\n"
                + "G1 F1800 E10.38298\n"
                + "G1 F1200 X76.113 Y71.401 E0.02445\n"
                + "G1 F1800 E-0.50000\n"
                + "G1 F1800 E10.50559\n"
                + "G1 F1200 X89.993 Y68.315 E0.02406\n"
                + "G92 E0\n"
                + "G1 F1800 E-14.50000\n"
                + "T1\n"
                + "G0 F12000 X108.579 Y67.566\n"
                + "G1 F1800 E0.00000\n"
                + "G1 F1200 X108.582 Y67.535 E0.00374\n"
                + "G1 X108.599 Y67.497 E0.00500\n"
                + "G1 X108.608 Y67.485 E0.00180\n"
                + "G1 X108.643 Y67.461 E0.00510\n"
                + "G1 X123.265 Y67.452 E1.75582\n"
                + "G1 X123.316 Y67.461 E0.00622\n"
                + "G1 X123.346 Y67.480 E0.00426\n"
                + "G1 X123.371 Y67.517 E0.00536\n"
                + "G1 X123.379 Y74.728 E0.86590\n"
                + "G1 X123.370 Y74.790 E0.00752\n"
                + "G1 X123.353 Y74.818 E0.00393\n"
                + "G1 X123.317 Y74.844 E0.00533\n"
                + "G1 X108.699 Y74.852 E1.75534\n"
                + "G1 X108.642 Y74.843 E0.00693\n"
                + "G1 X108.627 Y74.835 E0.00204\n"
                + "G1 X108.606 Y74.818 E0.00324\n"
                + "G1 X108.588 Y74.790 E0.00400\n"
                + "G1 X108.579 Y67.566 E0.86746\n"
                + ";TYPE:WALL-OUTER\n"
                + "G1 F1800 E10.35819\n"
                + "G1 F1200 X76.113 Y69.516 E0.02479\n";

        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class
        );
        gcodeParser.setPrintVolumeBounds(210, 150, 100);

        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.Layer());
        ParsingResult result = runner.run(layer0);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);

        LayerNode layerNode = gcodeParser.getLayerNode();

        assertEquals(5, layerNode.getChildren().size());
        assertTrue(layerNode.getChildren().get(3) instanceof OrphanObjectDelineationNode);
        assertTrue(layerNode.getChildren().get(4) instanceof ObjectDelineationNode);

        ObjectDelineationNode objectNode = (ObjectDelineationNode) layerNode.getChildren().get(4);
        // Used to return an OrphanSectionNode. Now it knows what the current section is, so
        // returns the appropriate section type.
        // assertTrue(objectNode.getChildren().get(1) instanceof OrphanSectionNode);
        assertTrue(objectNode.getChildren().get(1) instanceof FillSectionNode);
        assertTrue(objectNode.getChildren().get(2) instanceof OuterPerimeterSectionNode);
    }

    @Test
    public void fillSectionTest()
    {
        String inputData = ";TYPE:FILL\n"
                + "G1 F1800 E-0.50000\n"
                + "G0 F12000 X88.302 Y42.421 Z1.020\n"
                + "G1 F1800 E0.00000\n"
                + "G1 X12.3 Y14.5 E1.00000\n";
        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class
        );
        gcodeParser.setPrintVolumeBounds(210, 150, 100);

        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.FillSection());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);

        assertFalse(result.valueStack.isEmpty());
        assertTrue(result.valueStack.peek() instanceof FillSectionNode);

        FillSectionNode node = (FillSectionNode) result.valueStack.pop();
        assertEquals(4, node.getChildren().size());
        assertEquals(RetractNode.class, node.getChildren().get(0).getClass());
        assertEquals(LayerChangeDirectiveNode.class, node.getChildren().get(1).getClass());
        assertEquals(UnretractNode.class, node.getChildren().get(2).getClass());
        assertEquals(ExtrusionNode.class, node.getChildren().get(3).getClass());
    }

    @Test
    public void innerPerimeterTest()
    {
        String inputData = ";TYPE:WALL-INNER\n"
                + "G1 F1800 E-0.50000\n"
                + "G0 F12000 X88.302 Y42.421 Z1.020\n"
                + "G1 F1800 E0.00000\n"
                + "G1 X12.3 Y14.5 E1.00000\n";
        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class);
        gcodeParser.setPrintVolumeBounds(210, 150, 100);

        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.InnerPerimeterSection());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);

        assertFalse(result.valueStack.isEmpty());
        assertTrue(result.valueStack.peek() instanceof InnerPerimeterSectionNode);

        InnerPerimeterSectionNode sectionNode = (InnerPerimeterSectionNode) result.valueStack.pop();
        assertEquals(4, sectionNode.getChildren().size());
        assertEquals(RetractNode.class, sectionNode.getChildren().get(0).getClass());
        assertEquals(LayerChangeDirectiveNode.class, sectionNode.getChildren().get(1).getClass());
        assertEquals(UnretractNode.class, sectionNode.getChildren().get(2).getClass());
        assertEquals(ExtrusionNode.class, sectionNode.getChildren().get(3).getClass());
    }

    @Test
    public void outerPerimeterTest()
    {
        String inputData = ";TYPE:WALL-OUTER\n"
                + "G1 F1800 E-0.50000\n"
                + "G0 F12000 X88.302 Y42.421 Z1.020\n"
                + "G1 F1800 E0.00000\n"
                + "G1 X12.3 Y14.5 E1.00000\n";
        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class);
        gcodeParser.setPrintVolumeBounds(210, 150, 100);

        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.OuterPerimeterSection());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);

        assertFalse(result.valueStack.isEmpty());
        assertTrue(result.valueStack.peek() instanceof OuterPerimeterSectionNode);

        OuterPerimeterSectionNode sectionNode = (OuterPerimeterSectionNode) result.valueStack.pop();
        assertEquals(4, sectionNode.getChildren().size());
        assertEquals(RetractNode.class, sectionNode.getChildren().get(0).getClass());
        assertEquals(LayerChangeDirectiveNode.class, sectionNode.getChildren().get(1).getClass());
        assertEquals(UnretractNode.class, sectionNode.getChildren().get(2).getClass());
        assertEquals(ExtrusionNode.class, sectionNode.getChildren().get(3).getClass());
    }

    @Test
    public void supportSectionTest()
    {
        String inputData = ";TYPE:SUPPORT\n"
                + "G1 F1800 E-0.50000\n"
                + "G0 F12000 X88.302 Y42.421 Z1.020\n"
                + "G1 F1800 E0.00000\n"
                + "G1 X12.3 Y14.5 E1.00000\n";
        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class
        );
        gcodeParser.setPrintVolumeBounds(210, 150, 100);

        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.SupportSection());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);

        assertFalse(result.valueStack.isEmpty());
        assertTrue(result.valueStack.peek() instanceof SupportSectionNode);

        SupportSectionNode sectionNode = (SupportSectionNode) result.valueStack.pop();
        assertEquals(4, sectionNode.getChildren().size());
        assertEquals(RetractNode.class, sectionNode.getChildren().get(0).getClass());
        assertEquals(LayerChangeDirectiveNode.class, sectionNode.getChildren().get(1).getClass());
        assertEquals(UnretractNode.class, sectionNode.getChildren().get(2).getClass());
        assertEquals(ExtrusionNode.class, sectionNode.getChildren().get(3).getClass());
    }

    @Test
    public void isASectionTest()
    {
        String section1 = ";TYPE:WALL-OUTER\n";
        String section2 = ";TYPE:WALL-INNER\n";
        String section3 = ";TYPE:FILL\n";
        String section4 = ";TYPE:SUPPORT\n";
        String section5 = ";TYPE:SKIN\n";

        String notASection = ";ddTYPE:WALL-OUTER\n";

        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class
        );
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.IsASection());

        ParsingResult result = runner.run(section1);
        assertFalse(result.hasErrors());
        assertTrue(result.matched);

        result = runner.run(section2);
        assertFalse(result.hasErrors());
        assertTrue(result.matched);

        result = runner.run(section3);
        assertFalse(result.hasErrors());
        assertTrue(result.matched);

        result = runner.run(section4);
        assertFalse(result.hasErrors());
        assertTrue(result.matched);

        result = runner.run(section5);
        assertFalse(result.hasErrors());
        assertTrue(result.matched);

        result = runner.run(notASection);
        assertFalse(result.hasErrors());
        assertFalse(result.matched);
    }

    @Test
    public void anySectionTest()
    {
        String section1 = ";TYPE:WALL-OUTER\n"
                + "G1 F1800 E-0.50000\n"
                + "G0 F12000 X88.302 Y42.421 Z1.020\n"
                + "G1 F1800 E0.00000\n"
                + "G1 X12.3 Y14.5 E1.00000\n";
        String section2 = ";TYPE:WALL-INNER\n"
                + "G1 F1800 E-0.50000\n"
                + "G0 F12000 X88.302 Y42.421 Z1.020\n"
                + "G1 F1800 E0.00000\n"
                + "G1 X12.3 Y14.5 E1.00000\n";
        String section3 = ";TYPE:FILL\n"
                + "G1 F1800 E-0.50000\n"
                + "G0 F12000 X88.302 Y42.421 Z1.020\n"
                + "G1 F1800 E0.00000\n"
                + "G1 X12.3 Y14.5 E1.00000\n";
        String section4 = ";TYPE:SUPPORT\n"
                + "G1 F1800 E-0.50000\n"
                + "G0 F12000 X88.302 Y42.421 Z1.020\n"
                + "G1 F1800 E0.00000\n"
                + "G1 X12.3 Y14.5 E1.00000\n";
        String section5 = ";TYPE:SKIN\n"
                + "G1 F1800 E-0.50000\n"
                + "G0 F12000 X88.302 Y42.421 Z1.020\n"
                + "G1 F1800 E0.00000\n"
                + "G1 X12.3 Y14.5 E1.00000\n";

        String notASection = "T1\n";

        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class
        );
        gcodeParser.setPrintVolumeBounds(210, 150, 100);

        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.AnySection());

        ParsingResult result = runner.run(section1);
        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertFalse(result.valueStack.isEmpty());
        assertTrue(result.valueStack.peek() instanceof OuterPerimeterSectionNode);

        result = runner.run(section2);
        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertFalse(result.valueStack.isEmpty());
        assertTrue(result.valueStack.peek() instanceof InnerPerimeterSectionNode);

        result = runner.run(section3);
        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertFalse(result.valueStack.isEmpty());
        assertTrue(result.valueStack.peek() instanceof FillSectionNode);

//        result = runner.run(section4);
//        assertFalse(result.hasErrors());
//        assertTrue(result.matched);
//        
//        result = runner.run(section5);
//        assertFalse(result.hasErrors());
//        assertTrue(result.matched);
        result = runner.run(notASection);
        assertFalse(result.matched);
    }

    @Test
    public void notASectionTest()
    {
        String section = ";TYPE:WALL-OUTER\n";
        String notASection = ";ddTYPE:WALL-OUTER\n";

        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class
        );
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.NotASection());

        ParsingResult result = runner.run(section);
        assertFalse(result.hasErrors());
        assertFalse(result.matched);

        result = runner.run(notASection);
        assertFalse(result.hasErrors());
        assertTrue(result.matched);
    }

    @Test
    public void curaMixedLayerStartTest()
    {
        String inputData = ";LAYER:1\n"
                + "M106 S94\n"
                + "G0 F12000 X103.562 Y79.849 Z0.600\n"
                + ";TYPE:WALL-INNER\n"
                + "G1 F900 X103.749 Y79.311 E0.13679\n"
                + "G1 X103.810 Y78.751 E0.13529\n"
                + "G1 X103.749 Y78.186 E0.13648\n"
                + "G1 X103.561 Y77.649 E0.13664\n"
                + "G1 X103.258 Y77.165 E0.13714\n"
                + "G1 X102.852 Y76.752 E0.13909\n"
                + "G1 X102.302 Y76.409 E0.15567\n"
                + "G1 X101.638 Y76.222 E0.16567\n"
                + "G1 X101.219 Y76.188 E0.10096\n";

        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class);
        gcodeParser.setPrintVolumeBounds(210, 150, 100);

        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.Layer());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        LayerNode layerNode = gcodeParser.getLayerNode();

        assertNotNull(layerNode);

        assertEquals(
                3, layerNode.getChildren().size());

        assertEquals(MCodeNode.class, layerNode.getChildren().get(0).getClass());
        assertEquals(LayerChangeDirectiveNode.class, layerNode.getChildren().get(1).getClass());
        assertEquals(OrphanObjectDelineationNode.class, layerNode.getChildren().get(2).getClass());

        OrphanObjectDelineationNode objectNode = (OrphanObjectDelineationNode) layerNode.getChildren().get(2);

        assertEquals(
                1, objectNode.getChildren().size());
        assertEquals(InnerPerimeterSectionNode.class, objectNode.getChildren().get(0).getClass());

        InnerPerimeterSectionNode inner = (InnerPerimeterSectionNode) objectNode.getChildren().get(0);
        assertEquals(ExtrusionNode.class, inner.getChildren().get(0).getClass());

    }

    @Test
    public void curaTravelFirstLayerStartTest()
    {
        String inputData = ";LAYER:7\n"
                + "G0 F12000 X109.157 Y81.288 Z2.400\n"
                + ";TYPE:WALL-INNER\n"
                + "G1 F600 X109.157 Y81.299 E0.00264\n"
                + "G1 X99.549 Y81.288 E2.30747\n"
                + "G1 X99.351 Y81.246 E0.04861\n"
                + "G1 X99.173 Y81.166 E0.04687\n"
                + "G1 X99.015 Y81.051 E0.04693\n"
                + "G1 X98.885 Y80.905 E0.04695\n"
                + "G1 X98.787 Y80.736 E0.04692\n"
                + "G1 X98.727 Y80.552 E0.04648\n"
                + "G1 X98.700 Y80.293 E0.06254\n"
                + "G1 X98.700 Y69.712 E2.54115\n"
                + "G1 X98.727 Y69.446 E0.06421\n"
                + "G1 X98.788 Y69.258 E0.04747\n"
                + "G1 X98.882 Y69.095 E0.04519\n"
                + "G1 X99.014 Y68.949 E0.04727\n"
                + "G1 X99.174 Y68.832 E0.04760\n"
                + "G1 X99.353 Y68.752 E0.04709\n"
                + "G1 X99.548 Y68.711 E0.04786\n"
                + "G1 X110.451 Y68.711 E2.61848\n"
                + "G1 X110.646 Y68.752 E0.04786\n"
                + "G1 X110.824 Y68.832 E0.04687\n"
                + "G1 X110.984 Y68.948 E0.04746\n"
                + "G1 X111.115 Y69.094 E0.04711\n"
                + "G1 X111.208 Y69.255 E0.04465\n"
                + "G1 X111.274 Y69.454 E0.05035\n"
                + "G1 X111.300 Y69.701 E0.05965\n"
                + "G1 X111.300 Y80.297 E2.54475\n"
                + "G1 X111.272 Y80.553 E0.06185\n"
                + "G1 X111.212 Y80.735 E0.04602\n"
                + "G1 X111.113 Y80.907 E0.04766\n"
                + "G1 X110.982 Y81.052 E0.04693\n"
                + "G1 X110.823 Y81.168 E0.04727\n"
                + "G1 X110.648 Y81.246 E0.04601\n"
                + "G1 X110.451 Y81.288 E0.04838\n"
                + "G1 X109.157 Y81.288 E0.31077\n"
                + "G0 F12000 X109.957 Y82.088\n"
                + ";TYPE:WALL-OUTER\n"
                + "G1 F600 X109.957 Y82.100 E0.00288\n"
                + "G1 X99.461 Y82.088 E2.52074\n"
                + "G1 X99.103 Y82.012 E0.08789\n"
                + "G1 X98.766 Y81.860 E0.08879\n"
                + "G1 X98.477 Y81.649 E0.08594\n"
                + "G1 X98.232 Y81.376 E0.08810\n"
                + "G1 X98.053 Y81.065 E0.08618\n"
                + "G1 X97.940 Y80.719 E0.08742\n"
                + "G1 X97.900 Y80.336 E0.09248\n"
                + "G1 X97.900 Y69.670 E2.56156\n"
                + "G1 X97.940 Y69.279 E0.09439\n"
                + "G1 X98.053 Y68.933 E0.08742\n"
                + "G1 X98.232 Y68.621 E0.08639\n"
                + "G1 X98.474 Y68.353 E0.08672\n"
                + "G1 X98.776 Y68.133 E0.08973\n"
                + "G1 X99.103 Y67.987 E0.08600\n"
                + "G1 X99.461 Y67.911 E0.08789\n"
                + "G1 X110.538 Y67.911 E2.66027\n"
                + "G1 X110.896 Y67.987 E0.08789\n"
                + "G1 X111.223 Y68.133 E0.08600\n"
                + "G1 X111.523 Y68.351 E0.08906\n"
                + "G1 X111.765 Y68.620 E0.08690\n"
                + "G1 X111.942 Y68.928 E0.08531\n"
                + "G1 X112.060 Y69.281 E0.08939\n"
                + "G1 X112.100 Y69.664 E0.09248\n"
                + "G1 X112.100 Y80.336 E2.56300\n"
                + "G1 X112.058 Y80.722 E0.09325\n"
                + "G1 X111.944 Y81.067 E0.08726\n"
                + "G1 X111.764 Y81.380 E0.08671\n"
                + "G1 X111.521 Y81.650 E0.08724\n"
                + "G1 X111.225 Y81.864 E0.08772\n"
                + "G1 X110.894 Y82.012 E0.08708\n"
                + "G1 X110.538 Y82.088 E0.08742\n"
                + "G1 X109.957 Y82.088 E0.13953\n"
                + "G0 F12000 X109.668 Y80.967\n"
                + ";TYPE:FILL\n"
                + "G1 F600 X110.313 Y80.322 E0.19141\n"
                + "G1 X110.504 Y79.564 E0.19853\n"
                + "G1 X110.463 Y78.475 E0.32192\n"
                + "G1 X110.978 Y77.394 E0.28757\n"
                + "G0 F12000 X110.979 Y75.696\n"
                + "G1 F600 X105.700 Y80.975 E1.79296\n"
                + "G0 F12000 X105.135 Y80.975\n"
                + "G1 F600 X106.336 Y80.339 E0.28844\n"
                + "G1 X106.830 Y80.976 E0.14302\n"
                + "G1 X108.422 Y79.950 E0.45486\n"
                + "G0 F12000 X105.057 Y79.921\n"
                + "G1 F600 X103.558 Y80.854 E0.35672\n"
                + "G1 X102.677 Y80.604 E0.08000\n"
                + "G1 X101.686 Y80.463 E0.24941\n"
                + "G1 X100.491 Y80.527 E0.34381\n"
                + "G1 X99.261 Y80.625 E0.29411\n"
                + "G1 X99.522 Y79.234 E0.34074\n"
                + "G1 X100.016 Y78.174 E0.28086\n"
                + "G0 F12000 X99.018 Y76.343\n"
                + "G1 F600 X106.331 Y69.031 E2.48361\n"
                + "G0 F12000 X108.629 Y69.031\n"
                + "G1 F600 X110.979 Y71.381 E0.79815\n"
                + "G0 F12000 X100.075 Y71.327\n"
                + "G1 F600 X99.018 Y71.817 E0.35849\n"
                + "G1 X99.707 Y69.997 E0.30262\n"
                + "G1 X99.018 Y70.120 E0.16809\n"
                + "G0 F12000 X99.018 Y70.734\n"
                + "G1 F600 X109.252 Y80.967 E3.47570\n"
                + "G0 F12000 X108.525 Y80.979\n"
                + "G1 F600 X109.475 Y80.028 E0.32283\n"
                + "G0 F12000 X99.675 Y77.383\n"
                + "G1 F600 X99.019 Y77.473 E0.22263\n"
                + "G1 X99.154 Y76.773 E0.17121\n";

        CuraGCodeParser gcodeParser = Parboiled.createParser(CuraGCodeParser.class);
        gcodeParser.setPrintVolumeBounds(210, 150, 100);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.Layer());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        LayerNode layerNode = gcodeParser.getLayerNode();

        assertNotNull(layerNode);

        assertEquals(2, layerNode.getChildren().size());

        assertEquals(LayerChangeDirectiveNode.class, layerNode.getChildren().get(0).getClass());
        assertEquals(OrphanObjectDelineationNode.class, layerNode.getChildren().get(1).getClass());

        OrphanObjectDelineationNode objectNode = (OrphanObjectDelineationNode) layerNode.getChildren().get(1);

        assertEquals(
                3, objectNode.getChildren().size());
        assertEquals(InnerPerimeterSectionNode.class, objectNode.getChildren().get(0).getClass());
        assertEquals(OuterPerimeterSectionNode.class, objectNode.getChildren().get(1).getClass());
        assertEquals(FillSectionNode.class, objectNode.getChildren().get(2).getClass());
    }
}
