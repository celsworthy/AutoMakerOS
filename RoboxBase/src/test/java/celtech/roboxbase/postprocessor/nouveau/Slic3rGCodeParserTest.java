package celtech.roboxbase.postprocessor.nouveau;

import celtech.roboxbase.postprocessor.nouveau.nodes.CommentNode;
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
public class Slic3rGCodeParserTest
{

    public Slic3rGCodeParserTest()
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
        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.FloatingPointNumber());

        String positiveNumber = "1.20\n";
        ParsingResult positiveNumberResult = runner.run(positiveNumber);
        assertFalse(positiveNumberResult.hasErrors());
        assertTrue(positiveNumberResult.matched);

        String negativeNumber = "-1.20\n";
        gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class);
        runner = new BasicParseRunner<>(gcodeParser.FloatingPointNumber());
        ParsingResult negativeNumberResult = runner.run(negativeNumber);
        assertFalse(negativeNumberResult.hasErrors());
        assertTrue(negativeNumberResult.matched);
    }

    @Test
    public void negativeFloatingPointNumberTest()
    {
        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class);
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
        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class);
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
    public void feedrateFloat()
    {
        String inputData = "F115.433";
        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class);

        Var<Integer> feedrateResult = new Var<>();
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.Feedrate(feedrateResult));
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(0, result.valueStack.size());
        assertEquals(115, feedrateResult.get().intValue());
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
        String commentPart = " move to first perimeter point ";
        String inputData = " ;" + commentPart + "\n";
        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class
        );
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.CommentDirective());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(1, result.valueStack.size());
        assertTrue(result.valueStack.peek(0) instanceof CommentNode);
        assertEquals(commentPart, ((CommentNode) result.valueStack.peek(0)).getRawCommentText());
    }

    @Test
    public void commentDirectiveNoMatch()
    {
        String inputData = InnerPerimeterSectionNode.designator + "\n";
        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class
        );
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.CommentDirective());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertFalse(result.matched);
    }

    @Test
    public void travelDirective()
    {
        String commentPart = " move to first perimeter point";
        String inputData = "G1 X112.395 Y82.193 F356.000 ;" + commentPart + "\n";
        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.TravelDirective());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(1, result.valueStack.size());
        assertTrue(result.valueStack.peek(0) instanceof TravelNode);
        assertEquals(112.395, ((TravelNode) result.valueStack.peek(0)).getMovement().getX(), 0.001);
        assertEquals(82.193, ((TravelNode) result.valueStack.peek(0)).getMovement().getY(), 0.001);
        assertEquals(356, ((TravelNode) result.valueStack.peek(0)).getFeedrate().getFeedRate_mmPerMin(), 0.001);
        assertEquals(commentPart, ((TravelNode) result.valueStack.peek(0)).getRawCommentText());
    }

    @Test
    public void retractDirective()
    {
        String commentPart = " move to first perimeter point";
        String inputData = "G1 E-0.05000 F3545.00000 ;" + commentPart + "\n";
        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.RetractDirective());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(1, result.valueStack.size());
        assertTrue(result.valueStack.peek(0) instanceof RetractNode);
        assertEquals(-0.05, ((RetractNode) result.valueStack.peek(0)).getExtrusion().getE(), 0.001);
        assertEquals(0, ((RetractNode) result.valueStack.peek(0)).getExtrusion().getD(), 0.001);
        assertEquals(3545, ((RetractNode) result.valueStack.peek(0)).getFeedrate().getFeedRate_mmPerMin(), 0.001);
        assertEquals(commentPart, ((RetractNode) result.valueStack.peek(0)).getRawCommentText());
    }

    @Test
    public void unretractDirective()
    {
        String commentPart = " unretract";
        String inputData = "G1 E0.30000 F1200.00000 ;" + commentPart + "\n";
        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.UnretractDirective());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(1, result.valueStack.size());
        assertTrue(result.valueStack.peek(0) instanceof UnretractNode);
        assertEquals(0.3, ((UnretractNode) result.valueStack.peek(0)).getExtrusion().getE(), 0.001);
        assertEquals(0, ((UnretractNode) result.valueStack.peek(0)).getExtrusion().getD(), 0.001);
        assertEquals(1200, ((UnretractNode) result.valueStack.peek(0)).getFeedrate().getFeedRate_mmPerMin(), 0.001);
        assertEquals(commentPart, ((UnretractNode) result.valueStack.peek(0)).getRawCommentText());
    }

    @Test
    public void gcodeDirective()
    {
        String commentPart = " use absolute coordinates";
        String inputData = "G90 ;" + commentPart + "\n";
        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class
        );
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.GCodeDirective());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(1, result.valueStack.size());
        assertTrue(result.valueStack.peek(0) instanceof GCodeDirectiveNode);
        assertEquals(90, ((GCodeDirectiveNode) result.valueStack.peek(0)).getGValue().intValue());
        assertEquals(commentPart, ((GCodeDirectiveNode) result.valueStack.peek(0)).getRawCommentText());
    }

    @Test
    public void mcodeDirective()
    {
        String commentPart = " use relative distances for extrusion";
        String inputData = "M83 ;" + commentPart + "\n";
        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.MCode());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(1, result.valueStack.size());
        assertTrue(result.valueStack.peek(0) instanceof MCodeNode);
        assertEquals(83, ((MCodeNode) result.valueStack.peek(0)).getMNumber());
        assertEquals(commentPart, ((MCodeNode) result.valueStack.peek(0)).getRawCommentText());
    }

    @Test
    public void extrusionDirective()
    {
        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.ExtrusionDirective());

        String commentPart = "perimeter";
        String eOnlyExtrude = "G1 X112.362 Y83.093 E0.04380 ;" + commentPart + "\n";
        ParsingResult inputData1Result = runner.run(eOnlyExtrude);

        assertFalse(inputData1Result.hasErrors());
        assertTrue(inputData1Result.matched);
        assertEquals(1, inputData1Result.valueStack.size());
        assertTrue(inputData1Result.valueStack.peek(0) instanceof ExtrusionNode);
        assertEquals(112.362, ((ExtrusionNode) inputData1Result.valueStack.peek(0)).getMovement().getX(), 0.001);
        assertEquals(83.093, ((ExtrusionNode) inputData1Result.valueStack.peek(0)).getMovement().getY(), 0.001);
        assertEquals(0, ((ExtrusionNode) inputData1Result.valueStack.peek(0)).getMovement().getZ(), 0.001);
        assertEquals(0.04380, ((ExtrusionNode) inputData1Result.valueStack.peek(0)).getExtrusion().getE(), 0.001);
        assertEquals(0, ((ExtrusionNode) inputData1Result.valueStack.peek(0)).getExtrusion().getD(), 0.0001);
        assertEquals(commentPart, ((ExtrusionNode) inputData1Result.valueStack.peek(0)).getRawCommentText());

        gcodeParser.resetLayer();

        String dOnlyExtrude = "G1 X112.395 Y82.822 D0.123 F480.000 ;" + commentPart + "\n";
        ParsingResult dOnlyExtrudeResult = runner.run(dOnlyExtrude);
        assertFalse(dOnlyExtrudeResult.hasErrors());
        assertTrue(dOnlyExtrudeResult.matched);
        assertEquals(1, dOnlyExtrudeResult.valueStack.size());
        assertTrue(dOnlyExtrudeResult.valueStack.peek(0) instanceof ExtrusionNode);
        assertEquals(112.395, ((ExtrusionNode) dOnlyExtrudeResult.valueStack.peek(0)).getMovement().getX(), 0.001);
        assertEquals(82.822, ((ExtrusionNode) dOnlyExtrudeResult.valueStack.peek(0)).getMovement().getY(), 0.001);
        assertEquals(0, ((ExtrusionNode) dOnlyExtrudeResult.valueStack.peek(0)).getMovement().getZ(), 0.001);
        assertEquals(0, ((ExtrusionNode) dOnlyExtrudeResult.valueStack.peek(0)).getExtrusion().getE(), 0.001);
        assertEquals(0.123, ((ExtrusionNode) dOnlyExtrudeResult.valueStack.peek(0)).getExtrusion().getD(), 0.0001);
        assertEquals(commentPart, ((ExtrusionNode) dOnlyExtrudeResult.valueStack.peek(0)).getRawCommentText());
    }

    @Test
    public void layerChangeDirective()
    {
        String commentPart = " move to next layer (66)";
        String inputData = "G1 Z0.300 F12000.000 ;" + commentPart + "\n";
        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class);
        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.LayerChangeDirective());
        ParsingResult result = runner.run(inputData);

        assertFalse(result.hasErrors());
        assertTrue(result.matched);
        assertEquals(1, result.valueStack.size());
        assertTrue(result.valueStack.peek(0) instanceof LayerChangeDirectiveNode);
        assertEquals(66, ((LayerChangeDirectiveNode) result.valueStack.peek(0)).getLayerNumber());
        assertEquals(0, ((LayerChangeDirectiveNode) result.valueStack.peek(0)).getMovement().getX(), 0.001);
        assertEquals(0, ((LayerChangeDirectiveNode) result.valueStack.peek(0)).getMovement().getY(), 0.001);
        assertEquals(0.3, ((LayerChangeDirectiveNode) result.valueStack.peek(0)).getMovement().getZ(), 0.001);
    }

    //@Test DISABLED23/09/15
    public void compoundTest()
    {
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

        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class);
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
        assertEquals(TravelNode.class, layerNode.getChildren().get(2).getClass());
        assertEquals(OrphanObjectDelineationNode.class, layerNode.getChildren().get(3).getClass());

        OrphanObjectDelineationNode objectNode = (OrphanObjectDelineationNode) layerNode.getChildren().get(3);

        assertEquals(
                3, objectNode.getChildren().size());
        assertEquals(InnerPerimeterSectionNode.class, objectNode.getChildren().get(0).getClass());
        assertEquals(OuterPerimeterSectionNode.class, objectNode.getChildren().get(1).getClass());
        assertEquals(FillSectionNode.class, objectNode.getChildren().get(2).getClass());
    }

    //@Test DISABLED23/09/15
    public void objectSectionTest()
    {
        String inputData = "T0 ; change extruder\n"
                + "G1 Z0.300 F12000.000 ; move to next layer (0)\n"
                + "G1 E-0.05000 F1200.00000 ; retract for toolchange\n"
                + "T2 ; change extruder\n"
                + "G1 E-0.05000 F1200.00000 ; retract\n"
                + "G1 X36.894 Y119.142 F12000.000 ; move to first perimeter point\n"
                + "G1 E0.30000 F1200.00000 ; unretract\n"
                + "G1 X40.334 Y118.098 E0.57770 F480.000 ; perimeter\n"
                + "G1 X43.703 Y117.765 E0.54395 ; perimeter\n"
                + "G1 X43.913 Y117.745 E0.03392 F480.000 ; perimeter\n"
                + "G1 X47.492 Y118.098 E0.57787 ; perimeter\n"
                + "G1 X50.932 Y119.142 E0.57770 ; perimeter\n"
                + "G1 X54.104 Y120.836 E0.57776 ; perimeter\n";

        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class
        );
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
        assertEquals(TravelNode.class, fillNode.getChildren().get(1).getClass());
        assertEquals(UnretractNode.class, fillNode.getChildren().get(2).getClass());
        assertEquals(ExtrusionNode.class, fillNode.getChildren().get(3).getClass());

        OuterPerimeterSectionNode outerNode = (OuterPerimeterSectionNode) node.getChildren().get(1);
        assertEquals(1, outerNode.getChildren().size());
        assertEquals(ExtrusionNode.class, outerNode.getChildren().get(0).getClass());
    }

//    @Test
//    public void objectSectionNoTriggerTest()
//    {
//        String inputData = ";TYPE:FILL\n"
//                + "G1 F1800 E-0.50000\n"
//                + "G0 F12000 X88.302 Y42.421 Z1.020\n"
//                + "G1 F1800 E0.00000\n"
//                + "G1 X12.3 Y14.5 E1.00000\n"
//                + ";TYPE:WALL-OUTER\n"
//                + "G1 X125.3 Y314.5 E1.00000\n";
//
//        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class
//        );
//        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.ObjectSection());
//        ParsingResult result = runner.run(inputData);
//
//        assertFalse(result.hasErrors());
//        assertFalse(result.matched);
//    }
//
//    @Test
//    public void orphanObjectSectionTriggerTest()
//    {
//        String inputData = ";TYPE:FILL\n"
//                + "G1 F1800 E-0.50000\n"
//                + "G0 F12000 X88.302 Y42.421 Z1.020\n"
//                + "G1 F1800 E0.00000\n"
//                + "G1 X12.3 Y14.5 E1.00000\n"
//                + ";TYPE:WALL-OUTER\n"
//                + "G1 X125.3 Y314.5 E1.00000\n";
//
//        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class
//        );
//        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.OrphanObjectSection());
//        ParsingResult result = runner.run(inputData);
//
//        assertFalse(result.hasErrors());
//        assertTrue(result.matched);
//
//        assertFalse(result.valueStack.isEmpty());
//        assertTrue(result.valueStack.peek() instanceof OrphanObjectDelineationNode);
//
//        OrphanObjectDelineationNode node = (OrphanObjectDelineationNode) result.valueStack.pop();
//        assertEquals(2, node.getChildren().size());
//        assertEquals(FillSectionNode.class, node.getChildren().get(0).getClass());
//        assertEquals(OuterPerimeterSectionNode.class, node.getChildren().get(1).getClass());
//
//        FillSectionNode fillNode = (FillSectionNode) node.getChildren().get(0);
//        assertEquals(4, fillNode.getChildren().size());
//        assertEquals(RetractNode.class, fillNode.getChildren().get(0).getClass());
//        assertEquals(TravelNode.class, fillNode.getChildren().get(1).getClass());
//        assertEquals(UnretractNode.class, fillNode.getChildren().get(2).getClass());
//        assertEquals(ExtrusionNode.class, fillNode.getChildren().get(3).getClass());
//
//        OuterPerimeterSectionNode outerNode = (OuterPerimeterSectionNode) node.getChildren().get(1);
//        assertEquals(1, outerNode.getChildren().size());
//        assertEquals(ExtrusionNode.class, outerNode.getChildren().get(0).getClass());
//
//    }
//
//    @Test
//    public void layerNode_multipleObjects()
//    {
//        String inputData = ";LAYER:0\n"
//                + "M107\n"
//                + "G1 F1800 E-0.50000\n"
//                + "G0 F12000 X75.672 Y67.458 Z0.480\n"
//                + ";TYPE:WALL-OUTER\n"
//                + "G1 F1800 E0.00000\n"
//                + "G1 F1200 X75.710 Y67.425 E0.00604\n"
//                + "G1 X90.334 Y67.415 E1.75606\n"
//                + "G1 X90.391 Y67.423 E0.00691\n"
//                + "G1 X90.421 Y67.442 E0.00426\n"
//                + "G1 X90.446 Y67.479 E0.00536\n"
//                + "G1 X90.454 Y74.696 E0.86662\n"
//                + "G1 X90.445 Y74.753 E0.00693\n"
//                + "G1 X90.428 Y74.781 E0.00393\n"
//                + "G1 X90.392 Y74.807 E0.00533\n"
//                + "G1 X75.774 Y74.815 E1.75534\n"
//                + "G1 X75.707 Y74.803 E0.00817\n"
//                + "G1 X75.680 Y74.780 E0.00426\n"
//                + "G1 X75.662 Y74.737 E0.00560\n"
//                + "G1 X75.654 Y67.532 E0.86518\n"
//                + "G1 X75.672 Y67.458 E0.00915\n"
//                + "G0 F12000 X76.354 Y67.815\n"
//                + "G1 F1200 X76.354 Y74.415 E0.79253\n"
//                + "G1 X82.772 Y74.415 E0.77068\n"
//                + "G1 X82.699 Y74.237 E0.02310\n"
//                + "G1 X82.774 Y74.237 E0.00901\n"
//                + "G1 X83.040 Y74.697 E0.06381\n"
//                + "G1 X83.315 Y74.256 E0.06241\n"
//                + "G1 X83.244 Y74.415 E0.02091\n"
//                + "G1 X89.754 Y74.415 E0.78173\n"
//                + "G1 X89.754 Y67.815 E0.79253\n"
//                + "G1 X83.246 Y67.815 E0.78149\n"
//                + "G1 X83.319 Y67.979 E0.02156\n"
//                + "G1 X83.038 Y67.532 E0.06340\n"
//                + "G1 X82.776 Y67.990 E0.06336\n"
//                + "G1 X82.698 Y67.990 E0.00937\n"
//                + "G1 X82.770 Y67.815 E0.02272\n"
//                + "G1 X76.354 Y67.815 E0.77044\n"
//                + "G1 F1800 E-0.50000\n"
//                + "G0 F12000 X75.894 Y69.297\n"
//                + ";TYPE:FILL\n"
//                + "G1 F1800 E10.35819\n"
//                + "G1 F1200 X76.113 Y69.516 E0.02479\n"
//                + "G1 F1800 E-0.50000\n"
//                + "G0 F12000 X75.897 Y71.185\n"
//                + "G1 F1800 E10.38298\n"
//                + "G1 F1200 X76.113 Y71.401 E0.02445\n"
//                + "G1 F1800 E-0.50000\n"
//                + "G0 F12000 X75.899 Y73.072\n"
//                + "G1 F1800 E10.40744\n"
//                + "G1 F1200 X76.113 Y73.286 E0.02423\n"
//                + "G1 F1800 E-0.50000\n"
//                + "G0 F12000 X89.993 Y73.970\n"
//                + "G1 F1800 E10.43166\n"
//                + "G1 F1200 X90.213 Y74.190 E0.02491\n"
//                + "G0 F12000 X90.053 Y74.190\n"
//                + "G0 X90.051 Y72.303\n"
//                + "G0 X90.211 Y72.303\n"
//                + "G1 F1200 X89.993 Y72.085 E0.02468\n"
//                + "G1 F1800 E-0.50000\n"
//                + "G0 F12000 X90.208 Y70.415\n"
//                + "G1 F1800 E10.48125\n"
//                + "G1 F1200 X89.993 Y70.200 E0.02434\n"
//                + "G1 F1800 E-0.50000\n"
//                + "G0 F12000 X90.206 Y68.527\n"
//                + "G1 F1800 E10.50559\n"
//                + "G1 F1200 X89.993 Y68.315 E0.02406\n"
//                + "G92 E0\n"
//                + "G1 F1800 E-14.50000\n"
//                + "T1\n"
//                + "G0 F12000 X108.579 Y67.566\n"
//                + ";TYPE:WALL-OUTER\n"
//                + "G1 F1800 E0.00000\n"
//                + "G1 F1200 X108.582 Y67.535 E0.00374\n"
//                + "G1 X108.599 Y67.497 E0.00500\n"
//                + "G1 X108.608 Y67.485 E0.00180\n"
//                + "G1 X108.643 Y67.461 E0.00510\n"
//                + "G1 X123.265 Y67.452 E1.75582\n"
//                + "G1 X123.316 Y67.461 E0.00622\n"
//                + "G1 X123.346 Y67.480 E0.00426\n"
//                + "G1 X123.371 Y67.517 E0.00536\n"
//                + "G1 X123.379 Y74.728 E0.86590\n"
//                + "G1 X123.370 Y74.790 E0.00752\n"
//                + "G1 X123.353 Y74.818 E0.00393\n"
//                + "G1 X123.317 Y74.844 E0.00533\n"
//                + "G1 X108.699 Y74.852 E1.75534\n"
//                + "G1 X108.642 Y74.843 E0.00693\n"
//                + "G1 X108.627 Y74.835 E0.00204\n"
//                + "G1 X108.606 Y74.818 E0.00324\n"
//                + "G1 X108.588 Y74.790 E0.00400\n"
//                + "G1 X108.579 Y67.566 E0.86746\n";
//
//        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class
//        );
//        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.Layer());
//        ParsingResult result = runner.run(inputData);
//
//        assertFalse(result.hasErrors());
//        assertTrue(result.matched);
//
//        LayerNode layerNode = gcodeParser.getLayerNode();
//
//        assertEquals(5, layerNode.getChildren().size());
//    }
//
//    @Test
//    public void objectNode_orphanSection()
//    {
//        String layer0 = ";LAYER:0\n"
//                + "M107\n"
//                + "G1 F1800 E-0.50000\n"
//                + "G0 F12000 X75.672 Y67.458 Z0.480\n"
//                + ";TYPE:WALL-OUTER\n"
//                + "G1 F1800 E0.00000\n"
//                + "G1 F1200 X75.710 Y67.425 E0.00604\n"
//                + "G1 X90.334 Y67.415 E1.75606\n"
//                + "G1 X90.391 Y67.423 E0.00691\n"
//                + "G1 X90.421 Y67.442 E0.00426\n"
//                + "G1 X90.446 Y67.479 E0.00536\n"
//                + "G1 X90.454 Y74.696 E0.86662\n"
//                + "G1 X90.445 Y74.753 E0.00693\n"
//                + "G1 X90.428 Y74.781 E0.00393\n"
//                + "G1 X83.038 Y67.532 E0.06340\n"
//                + "G1 X82.776 Y67.990 E0.06336\n"
//                + "G1 X82.698 Y67.990 E0.00937\n"
//                + "G1 X82.770 Y67.815 E0.02272\n"
//                + "G1 X76.354 Y67.815 E0.77044\n"
//                + "G1 F1800 E-0.50000\n"
//                + "G0 F12000 X75.894 Y69.297\n"
//                + ";TYPE:FILL\n"
//                + "G1 F1800 E10.35819\n"
//                + "G1 F1200 X76.113 Y69.516 E0.02479\n"
//                + "G1 F1800 E-0.50000\n"
//                + "G0 F12000 X75.897 Y71.185\n"
//                + "G1 F1800 E10.38298\n"
//                + "G1 F1200 X76.113 Y71.401 E0.02445\n"
//                + "G1 F1800 E-0.50000\n"
//                + "G1 F1800 E10.50559\n"
//                + "G1 F1200 X89.993 Y68.315 E0.02406\n"
//                + "G92 E0\n"
//                + "G1 F1800 E-14.50000\n"
//                + "T1\n"
//                + "G0 F12000 X108.579 Y67.566\n"
//                + "G1 F1800 E0.00000\n"
//                + "G1 F1200 X108.582 Y67.535 E0.00374\n"
//                + "G1 X108.599 Y67.497 E0.00500\n"
//                + "G1 X108.608 Y67.485 E0.00180\n"
//                + "G1 X108.643 Y67.461 E0.00510\n"
//                + "G1 X123.265 Y67.452 E1.75582\n"
//                + "G1 X123.316 Y67.461 E0.00622\n"
//                + "G1 X123.346 Y67.480 E0.00426\n"
//                + "G1 X123.371 Y67.517 E0.00536\n"
//                + "G1 X123.379 Y74.728 E0.86590\n"
//                + "G1 X123.370 Y74.790 E0.00752\n"
//                + "G1 X123.353 Y74.818 E0.00393\n"
//                + "G1 X123.317 Y74.844 E0.00533\n"
//                + "G1 X108.699 Y74.852 E1.75534\n"
//                + "G1 X108.642 Y74.843 E0.00693\n"
//                + "G1 X108.627 Y74.835 E0.00204\n"
//                + "G1 X108.606 Y74.818 E0.00324\n"
//                + "G1 X108.588 Y74.790 E0.00400\n"
//                + "G1 X108.579 Y67.566 E0.86746\n"
//                + ";TYPE:WALL-OUTER\n"
//                + "G1 F1800 E10.35819\n"
//                + "G1 F1200 X76.113 Y69.516 E0.02479\n";
//
//        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class
//        );
//        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.Layer());
//        ParsingResult result = runner.run(layer0);
//
//        assertFalse(result.hasErrors());
//        assertTrue(result.matched);
//
//        LayerNode layerNode = gcodeParser.getLayerNode();
//
//        assertEquals(5, layerNode.getChildren().size());
//        assertTrue(layerNode.getChildren().get(3) instanceof OrphanObjectDelineationNode);
//        assertTrue(layerNode.getChildren().get(4) instanceof ObjectDelineationNode);
//
//        ObjectDelineationNode objectNode = (ObjectDelineationNode) layerNode.getChildren().get(4);
//        assertTrue(objectNode.getChildren().get(1) instanceof OrphanSectionNode);
//        assertTrue(objectNode.getChildren().get(2) instanceof OuterPerimeterSectionNode);
//    }
//
//    @Test
//    public void fillSectionTest()
//    {
//        String inputData = ";TYPE:FILL\n"
//                + "G1 F1800 E-0.50000\n"
//                + "G0 F12000 X88.302 Y42.421 Z1.020\n"
//                + "G1 F1800 E0.00000\n"
//                + "G1 X12.3 Y14.5 E1.00000\n";
//        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class
//        );
//        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.FillSection());
//        ParsingResult result = runner.run(inputData);
//
//        assertFalse(result.hasErrors());
//        assertTrue(result.matched);
//
//        assertFalse(result.valueStack.isEmpty());
//        assertTrue(result.valueStack.peek() instanceof FillSectionNode);
//
//        FillSectionNode node = (FillSectionNode) result.valueStack.pop();
//        assertEquals(4, node.getChildren().size());
//        assertEquals(RetractNode.class, node.getChildren().get(0).getClass());
//        assertEquals(TravelNode.class, node.getChildren().get(1).getClass());
//        assertEquals(UnretractNode.class, node.getChildren().get(2).getClass());
//        assertEquals(ExtrusionNode.class, node.getChildren().get(3).getClass());
//    }
//
//    @Test
//    public void innerPerimeterTest()
//    {
//        String inputData = ";TYPE:WALL-INNER\n"
//                + "G1 F1800 E-0.50000\n"
//                + "G0 F12000 X88.302 Y42.421 Z1.020\n"
//                + "G1 F1800 E0.00000\n"
//                + "G1 X12.3 Y14.5 E1.00000\n";
//        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class);
//        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.InnerPerimeterSection());
//        ParsingResult result = runner.run(inputData);
//
//        assertFalse(result.hasErrors());
//        assertTrue(result.matched);
//
//        assertFalse(result.valueStack.isEmpty());
//        assertTrue(result.valueStack.peek() instanceof InnerPerimeterSectionNode);
//
//        InnerPerimeterSectionNode sectionNode = (InnerPerimeterSectionNode) result.valueStack.pop();
//        assertEquals(4, sectionNode.getChildren().size());
//        assertEquals(RetractNode.class, sectionNode.getChildren().get(0).getClass());
//        assertEquals(TravelNode.class, sectionNode.getChildren().get(1).getClass());
//        assertEquals(UnretractNode.class, sectionNode.getChildren().get(2).getClass());
//        assertEquals(ExtrusionNode.class, sectionNode.getChildren().get(3).getClass());
//    }
//
//    @Test
//    public void outerPerimeterTest()
//    {
//        String inputData = ";TYPE:WALL-OUTER\n"
//                + "G1 F1800 E-0.50000\n"
//                + "G0 F12000 X88.302 Y42.421 Z1.020\n"
//                + "G1 F1800 E0.00000\n"
//                + "G1 X12.3 Y14.5 E1.00000\n";
//        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class
//        );
//        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.OuterPerimeterSection());
//        ParsingResult result = runner.run(inputData);
//
//        assertFalse(result.hasErrors());
//        assertTrue(result.matched);
//
//        assertFalse(result.valueStack.isEmpty());
//        assertTrue(result.valueStack.peek() instanceof OuterPerimeterSectionNode);
//
//        OuterPerimeterSectionNode sectionNode = (OuterPerimeterSectionNode) result.valueStack.pop();
//        assertEquals(4, sectionNode.getChildren().size());
//        assertEquals(RetractNode.class, sectionNode.getChildren().get(0).getClass());
//        assertEquals(TravelNode.class, sectionNode.getChildren().get(1).getClass());
//        assertEquals(UnretractNode.class, sectionNode.getChildren().get(2).getClass());
//        assertEquals(ExtrusionNode.class, sectionNode.getChildren().get(3).getClass());
//    }
//
//    @Test
//    public void supportSectionTest()
//    {
//        String inputData = ";TYPE:SUPPORT\n"
//                + "G1 F1800 E-0.50000\n"
//                + "G0 F12000 X88.302 Y42.421 Z1.020\n"
//                + "G1 F1800 E0.00000\n"
//                + "G1 X12.3 Y14.5 E1.00000\n";
//        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class
//        );
//        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.SupportSection());
//        ParsingResult result = runner.run(inputData);
//
//        assertFalse(result.hasErrors());
//        assertTrue(result.matched);
//
//        assertFalse(result.valueStack.isEmpty());
//        assertTrue(result.valueStack.peek() instanceof SupportSectionNode);
//
//        SupportSectionNode sectionNode = (SupportSectionNode) result.valueStack.pop();
//        assertEquals(4, sectionNode.getChildren().size());
//        assertEquals(RetractNode.class, sectionNode.getChildren().get(0).getClass());
//        assertEquals(TravelNode.class, sectionNode.getChildren().get(1).getClass());
//        assertEquals(UnretractNode.class, sectionNode.getChildren().get(2).getClass());
//        assertEquals(ExtrusionNode.class, sectionNode.getChildren().get(3).getClass());
//    }
//
//    @Test
//    public void isASectionTest()
//    {
//        String section1 = ";TYPE:WALL-OUTER\n";
//        String section2 = ";TYPE:WALL-INNER\n";
//        String section3 = ";TYPE:FILL\n";
//        String section4 = ";TYPE:SUPPORT\n";
//        String section5 = ";TYPE:SKIN\n";
//
//        String notASection = ";ddTYPE:WALL-OUTER\n";
//
//        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class
//        );
//        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.IsASection());
//
//        ParsingResult result = runner.run(section1);
//        assertFalse(result.hasErrors());
//        assertTrue(result.matched);
//
//        result = runner.run(section2);
//        assertFalse(result.hasErrors());
//        assertTrue(result.matched);
//
//        result = runner.run(section3);
//        assertFalse(result.hasErrors());
//        assertTrue(result.matched);
//
//        result = runner.run(section4);
//        assertFalse(result.hasErrors());
//        assertTrue(result.matched);
//        
//        result = runner.run(section5);
//        assertFalse(result.hasErrors());
//        assertTrue(result.matched);
//
//        result = runner.run(notASection);
//        assertFalse(result.hasErrors());
//        assertFalse(result.matched);
//    }
//
//    @Test
//    public void anySectionTest()
//    {
//        String section1 = ";TYPE:WALL-OUTER\n"
//                + "G1 F1800 E-0.50000\n"
//                + "G0 F12000 X88.302 Y42.421 Z1.020\n"
//                + "G1 F1800 E0.00000\n"
//                + "G1 X12.3 Y14.5 E1.00000\n";
//        String section2 = ";TYPE:WALL-INNER\n"
//                + "G1 F1800 E-0.50000\n"
//                + "G0 F12000 X88.302 Y42.421 Z1.020\n"
//                + "G1 F1800 E0.00000\n"
//                + "G1 X12.3 Y14.5 E1.00000\n";
//        String section3 = ";TYPE:FILL\n"
//                + "G1 F1800 E-0.50000\n"
//                + "G0 F12000 X88.302 Y42.421 Z1.020\n"
//                + "G1 F1800 E0.00000\n"
//                + "G1 X12.3 Y14.5 E1.00000\n";
//        String section4 = ";TYPE:SUPPORT\n"
//                + "G1 F1800 E-0.50000\n"
//                + "G0 F12000 X88.302 Y42.421 Z1.020\n"
//                + "G1 F1800 E0.00000\n"
//                + "G1 X12.3 Y14.5 E1.00000\n";
//        String section5 = ";TYPE:SKIN\n"
//                + "G1 F1800 E-0.50000\n"
//                + "G0 F12000 X88.302 Y42.421 Z1.020\n"
//                + "G1 F1800 E0.00000\n"
//                + "G1 X12.3 Y14.5 E1.00000\n";
//
//        String notASection = "T1\n";
//
//        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class
//        );
//        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.AnySection());
//
//        ParsingResult result = runner.run(section1);
//        assertFalse(result.hasErrors());
//        assertTrue(result.matched);
//        assertFalse(result.valueStack.isEmpty());
//        assertTrue(result.valueStack.peek() instanceof OuterPerimeterSectionNode);
//
//        result = runner.run(section2);
//        assertFalse(result.hasErrors());
//        assertTrue(result.matched);
//        assertFalse(result.valueStack.isEmpty());
//        assertTrue(result.valueStack.peek() instanceof InnerPerimeterSectionNode);
//
//        result = runner.run(section3);
//        assertFalse(result.hasErrors());
//        assertTrue(result.matched);
//        assertFalse(result.valueStack.isEmpty());
//        assertTrue(result.valueStack.peek() instanceof FillSectionNode);
//
////        result = runner.run(section4);
////        assertFalse(result.hasErrors());
////        assertTrue(result.matched);
////        
////        result = runner.run(section5);
////        assertFalse(result.hasErrors());
////        assertTrue(result.matched);
//        result = runner.run(notASection);
//        assertFalse(result.matched);
//    }
//
//    @Test
//    public void notASectionTest()
//    {
//        String section = ";TYPE:WALL-OUTER\n";
//        String notASection = ";ddTYPE:WALL-OUTER\n";
//
//        Slic3rGCodeParser gcodeParser = Parboiled.createParser(Slic3rGCodeParser.class
//        );
//        BasicParseRunner runner = new BasicParseRunner<>(gcodeParser.NotASection());
//
//        ParsingResult result = runner.run(section);
//        assertFalse(result.hasErrors());
//        assertFalse(result.matched);
//
//        result = runner.run(notASection);
//        assertFalse(result.hasErrors());
//        assertTrue(result.matched);
//    }
//
}
