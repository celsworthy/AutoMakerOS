package celtech.roboxbase.postprocessor.nouveau;

import celtech.roboxbase.postprocessor.nouveau.nodes.ExtrusionNode;
import celtech.roboxbase.utils.BaseEnvironmentConfiguredTest;

/**
 *
 * @author Ian
 */
public class CloseLogicTest extends BaseEnvironmentConfiguredTest
{

    private double movementEpsilon = 0.001;
    private double nozzleEpsilon = 0.01;

//    @Test
//    public void testInsertNozzleCloseFullyAfterEvent()
//    {
//        LayerNode testLayer = new LayerNode();
//        InnerPerimeterSectionNode inner = new InnerPerimeterSectionNode();
//        OuterPerimeterSectionNode outer = new OuterPerimeterSectionNode();
//        FillSectionNode fill = new FillSectionNode();
//
//        ExtrusionNode extrusionNode1 = new ExtrusionNode();
//        ExtrusionNode extrusionNode2 = new ExtrusionNode();
//        ExtrusionNode extrusionNode3 = new ExtrusionNode();
//
//        outer.addChildAtEnd(extrusionNode1);
//        outer.addChildAtEnd(extrusionNode2);
//        outer.addChildAtEnd(extrusionNode3);
//
//        testLayer.addChildAtEnd(inner);
//        testLayer.addChildAtEnd(outer);
//        testLayer.addChildAtEnd(fill);
//
//        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
//        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
//        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
//        ppFeatures.enableFeature(PostProcessorFeature.CLOSE_ON_TASK_CHANGE);
//
//        Project testProject = new Project();
//        testProject.getPrinterSettings().setSettingsName("BothNozzles");
//        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
//
//        CloseLogic closeLogic = new CloseLogic(testProject,
//                testProject.getPrinterSettings().getSettings("RBX01-SM"),
//                ppFeatures, "RBX01-SM");
//
//        assertEquals(3, testLayer.getChildren().size());
//        assertEquals(3, outer.getChildren().size());
//
//        NozzleParameters testNozzleParameters = new NozzleParameters();
//        testNozzleParameters.setOpenPosition(1.0f);
//        testNozzleParameters.setClosedPosition(0);
//        NozzleProxy testNozzle = new NozzleProxy(testNozzleParameters);
//
//        closeLogic.insertNozzleCloseFullyAfterEvent(extrusionNode1, testNozzle);
//
//        assertEquals(3, testLayer.getChildren().size());
//        assertEquals(4, outer.getChildren().size());
//        assertTrue(outer.getChildren().get(1) instanceof NozzleValvePositionNode);
//        assertEquals(0.0, ((NozzleValvePositionNode) outer.getChildren().get(1)).getNozzlePosition().getB(), 0.0001);
//    }
//
//    @Test
//    public void testCloseToEndOfFill_noSplitsRequired()
//    {
//        FillSectionNode fill1 = new FillSectionNode();
//
//        TravelNode travel1 = new TravelNode();
//        travel1.getMovement().setX(0);
//        travel1.getMovement().setY(0);
//
//        ExtrusionNode extrusionNode1 = new ExtrusionNode();
//        extrusionNode1.getExtrusion().setE(1f);
//        extrusionNode1.getMovement().setX(1);
//        extrusionNode1.getMovement().setY(1);
//
//        ExtrusionNode extrusionNode2 = new ExtrusionNode();
//        extrusionNode2.getExtrusion().setE(1f);
//        extrusionNode2.getMovement().setX(2);
//        extrusionNode2.getMovement().setY(2);
//
//        ExtrusionNode extrusionNode3 = new ExtrusionNode();
//        extrusionNode3.getExtrusion().setE(1f);
//        extrusionNode3.getMovement().setX(3);
//        extrusionNode3.getMovement().setY(3);
//
//        ExtrusionNode extrusionNode4 = new ExtrusionNode();
//        extrusionNode4.getExtrusion().setE(1f);
//        extrusionNode4.getMovement().setX(4);
//        extrusionNode4.getMovement().setY(4);
//
//        ExtrusionNode extrusionNode5 = new ExtrusionNode();
//        extrusionNode5.getExtrusion().setE(1f);
//        extrusionNode5.getMovement().setX(5);
//        extrusionNode5.getMovement().setY(5);
//
//        ExtrusionNode extrusionNode6 = new ExtrusionNode();
//        extrusionNode6.getExtrusion().setE(1f);
//        extrusionNode6.getMovement().setX(6);
//        extrusionNode6.getMovement().setY(6);
//
//        ExtrusionNode extrusionNode7 = new ExtrusionNode();
//        extrusionNode7.getExtrusion().setE(1f);
//        extrusionNode7.getMovement().setX(7);
//        extrusionNode7.getMovement().setY(7);
//
//        ExtrusionNode extrusionNode8 = new ExtrusionNode();
//        extrusionNode8.getExtrusion().setE(1f);
//        extrusionNode8.getMovement().setX(8);
//        extrusionNode8.getMovement().setY(8);
//
//        ExtrusionNode extrusionNode9 = new ExtrusionNode();
//        extrusionNode9.getExtrusion().setE(1f);
//        extrusionNode9.getMovement().setX(9);
//        extrusionNode9.getMovement().setY(9);
//
//        RetractNode retractNode = new RetractNode();
//        retractNode.setPriorExtrusionNode(extrusionNode9);
//        List<SectionNode> sectionsToConsider = new ArrayList<>();
//        sectionsToConsider.add(fill1);
//        retractNode.setSectionsToConsider(sectionsToConsider);
//
//        fill1.addChildAtEnd(travel1);
//        fill1.addChildAtEnd(extrusionNode1);
//        fill1.addChildAtEnd(extrusionNode2);
//        fill1.addChildAtEnd(extrusionNode3);
//        fill1.addChildAtEnd(extrusionNode4);
//        fill1.addChildAtEnd(extrusionNode5);
//        fill1.addChildAtEnd(extrusionNode6);
//        fill1.addChildAtEnd(extrusionNode7);
//        fill1.addChildAtEnd(extrusionNode8);
//        fill1.addChildAtEnd(extrusionNode9);
//        fill1.addChildAtEnd(retractNode);
//        fill1.recalculateExtrusion();
//
//        NozzleParameters nozzleParams = new NozzleParameters();
//
//        nozzleParams.setEjectionVolume(2);
//
//        NozzleProxy testProxy = new NozzleProxy(nozzleParams);
//
//        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
//        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
//        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
//        ppFeatures.enableFeature(PostProcessorFeature.CLOSE_ON_TASK_CHANGE);
//        ppFeatures.enableFeature(PostProcessorFeature.GRADUAL_CLOSE);
//
//        Project testProject = new Project();
//        testProject.getPrinterSettings().setSettingsName("BothNozzles");
//        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
//
//        CloseLogic closeLogic = new CloseLogic(testProject,
//                testProject.getPrinterSettings().getSettings("RBX01-SM"),
//                ppFeatures, "RBX01-SM");
//
//        Optional<CloseResult> closeResult = Optional.empty();
//
//        try
//        {
//            closeResult = closeLogic.insertProgressiveNozzleClose(retractNode, retractNode.getSectionsToConsider(), testProxy);
//        } catch (NotEnoughAvailableExtrusionException | NodeProcessingException | CannotCloseFromPerimeterException | NoPerimeterToCloseOverException | PostProcessingError ex)
//        {
//            fail();
//        }
//        assertTrue(closeResult.isPresent());
//
//        //Should have elided the same volume as the ejection volume
//        assertEquals(2, closeResult.get().getNozzleCloseOverVolume(), 0.01);
//
//        assertEquals(11, fill1.getChildren().size());
//
//        assertTrue(fill1.getChildren().get(7) instanceof ExtrusionNode);
//        ExtrusionNode node0 = (ExtrusionNode) fill1.getChildren().get(7);
//        assertSame(extrusionNode7, node0);
//        assertFalse(node0.getNozzlePosition().isBSet());
//        assertEquals(1, node0.getExtrusion().getE(), movementEpsilon);
//
//        assertTrue(fill1.getChildren().get(8) instanceof ExtrusionNode);
//        ExtrusionNode node1 = (ExtrusionNode) fill1.getChildren().get(8);
//        assertSame(extrusionNode8, node1);
//        assertTrue(node1.getNozzlePosition().isBSet());
//        assertEquals(0.5, node1.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node1.getExtrusion().isEInUse());
//
//        assertTrue(fill1.getChildren().get(9) instanceof ExtrusionNode);
//        ExtrusionNode node2 = (ExtrusionNode) fill1.getChildren().get(9);
//        assertSame(extrusionNode9, node2);
//        assertTrue(node2.getNozzlePosition().isBSet());
//        assertEquals(0, node2.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node2.getExtrusion().isEInUse());
//    }
//
//    @Test
//    public void testCloseToEndOfFill_splitsRequired()
//    {
//        FillSectionNode fill1 = new FillSectionNode();
//
//        TravelNode travel1 = new TravelNode();
//        travel1.getMovement().setX(0);
//        travel1.getMovement().setY(0);
//
//        ExtrusionNode extrusionNode1 = new ExtrusionNode();
//        extrusionNode1.getExtrusion().setE(1f);
//        extrusionNode1.getMovement().setX(1);
//        extrusionNode1.getMovement().setY(1);
//
//        ExtrusionNode extrusionNode2 = new ExtrusionNode();
//        extrusionNode2.getExtrusion().setE(1f);
//        extrusionNode2.getMovement().setX(2);
//        extrusionNode2.getMovement().setY(2);
//
//        ExtrusionNode extrusionNode3 = new ExtrusionNode();
//        extrusionNode3.getExtrusion().setE(1f);
//        extrusionNode3.getMovement().setX(3);
//        extrusionNode3.getMovement().setY(3);
//
//        ExtrusionNode extrusionNode4 = new ExtrusionNode();
//        extrusionNode4.getExtrusion().setE(1f);
//        extrusionNode4.getMovement().setX(4);
//        extrusionNode4.getMovement().setY(4);
//
//        ExtrusionNode extrusionNode5 = new ExtrusionNode();
//        extrusionNode5.getExtrusion().setE(1f);
//        extrusionNode5.getMovement().setX(5);
//        extrusionNode5.getMovement().setY(5);
//
//        ExtrusionNode extrusionNode6 = new ExtrusionNode();
//        extrusionNode6.getExtrusion().setE(1f);
//        extrusionNode6.getMovement().setX(6);
//        extrusionNode6.getMovement().setY(6);
//
//        ExtrusionNode extrusionNode7 = new ExtrusionNode();
//        extrusionNode7.getExtrusion().setE(1f);
//        extrusionNode7.getMovement().setX(7);
//        extrusionNode7.getMovement().setY(7);
//
//        ExtrusionNode extrusionNode8 = new ExtrusionNode();
//        extrusionNode8.getExtrusion().setE(1f);
//        extrusionNode8.getMovement().setX(8);
//        extrusionNode8.getMovement().setY(8);
//
//        ExtrusionNode extrusionNode9 = new ExtrusionNode();
//        extrusionNode9.getExtrusion().setE(3f);
//        extrusionNode9.getMovement().setX(9);
//        extrusionNode9.getMovement().setY(9);
//
//        RetractNode retractNode = new RetractNode();
//        retractNode.setPriorExtrusionNode(extrusionNode9);
//        List<SectionNode> sectionsToConsider = new ArrayList<>();
//        sectionsToConsider.add(fill1);
//        retractNode.setSectionsToConsider(sectionsToConsider);
//
//        fill1.addChildAtEnd(travel1);
//        fill1.addChildAtEnd(extrusionNode1);
//        fill1.addChildAtEnd(extrusionNode2);
//        fill1.addChildAtEnd(extrusionNode3);
//        fill1.addChildAtEnd(extrusionNode4);
//        fill1.addChildAtEnd(extrusionNode5);
//        fill1.addChildAtEnd(extrusionNode6);
//        fill1.addChildAtEnd(extrusionNode7);
//        fill1.addChildAtEnd(extrusionNode8);
//        fill1.addChildAtEnd(extrusionNode9);
//        fill1.addChildAtEnd(retractNode);
//        fill1.recalculateExtrusion();
//
//        NozzleParameters nozzleParams = new NozzleParameters();
//
//        nozzleParams.setEjectionVolume(2);
//
//        NozzleProxy testProxy = new NozzleProxy(nozzleParams);
//
//        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
//        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
//        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
//        ppFeatures.enableFeature(PostProcessorFeature.CLOSE_ON_TASK_CHANGE);
//        ppFeatures.enableFeature(PostProcessorFeature.GRADUAL_CLOSE);
//
//        Project testProject = new Project();
//        testProject.getPrinterSettings().setSettingsName("BothNozzles");
//        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
//
//        CloseLogic closeLogic = new CloseLogic(testProject,
//                testProject.getPrinterSettings().getSettings("RBX01-SM"),
//                ppFeatures, "RBX01-SM");
//
//        Optional<CloseResult> closeResult = Optional.empty();
//
//        try
//        {
//            closeResult = closeLogic.insertProgressiveNozzleClose(retractNode, retractNode.getSectionsToConsider(), testProxy);
//        } catch (NotEnoughAvailableExtrusionException | NodeProcessingException | CannotCloseFromPerimeterException | NoPerimeterToCloseOverException | PostProcessingError ex)
//        {
//            fail();
//        }
//
//        assertTrue(closeResult.isPresent());
//
//        //Should have elided the same volume as the ejection volume
//        assertEquals(2, closeResult.get().getNozzleCloseOverVolume(), 0.01);
//
//        assertEquals(12, fill1.getChildren().size());
//
//        assertTrue(fill1.getChildren().get(7) instanceof ExtrusionNode);
//        ExtrusionNode node0 = (ExtrusionNode) fill1.getChildren().get(7);
//        assertSame(extrusionNode7, node0);
//        assertFalse(node0.getNozzlePosition().isBSet());
//        assertEquals(1, node0.getExtrusion().getE(), movementEpsilon);
//
//        assertTrue(fill1.getChildren().get(9) instanceof ExtrusionNode);
//        ExtrusionNode node1 = (ExtrusionNode) fill1.getChildren().get(9);
//        assertFalse(node1.getNozzlePosition().isBSet());
//        assertEquals(8.333, node1.getMovement().getX(), movementEpsilon);
//        assertEquals(8.333, node1.getMovement().getY(), movementEpsilon);
//        assertTrue(node1.getExtrusion().isEInUse());
//
//        assertTrue(fill1.getChildren().get(10) instanceof ExtrusionNode);
//        ExtrusionNode node2 = (ExtrusionNode) fill1.getChildren().get(10);
//        assertSame(extrusionNode9, node2);
//        assertTrue(node2.getNozzlePosition().isBSet());
//        assertEquals(0, node2.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node2.getExtrusion().isEInUse());
//    }
//
//    @Test
//    public void testCloseToEndOfFill_checkUsesAllSections()
//    {
//        InnerPerimeterSectionNode inner1 = new InnerPerimeterSectionNode();
//        OuterPerimeterSectionNode outer1 = new OuterPerimeterSectionNode();
//        FillSectionNode fill1 = new FillSectionNode();
//        SkinSectionNode skin1 = new SkinSectionNode();
//
//        TravelNode travel1 = new TravelNode();
//        travel1.getMovement().setX(0);
//        travel1.getMovement().setY(0);
//
//        ExtrusionNode extrusionNode1 = new ExtrusionNode();
//        extrusionNode1.getExtrusion().setE(1f);
//        extrusionNode1.getMovement().setX(1);
//        extrusionNode1.getMovement().setY(1);
//
//        ExtrusionNode extrusionNode2 = new ExtrusionNode();
//        extrusionNode2.getExtrusion().setE(1f);
//        extrusionNode2.getMovement().setX(2);
//        extrusionNode2.getMovement().setY(2);
//
//        ExtrusionNode extrusionNode3 = new ExtrusionNode();
//        extrusionNode3.getExtrusion().setE(1f);
//        extrusionNode3.getMovement().setX(3);
//        extrusionNode3.getMovement().setY(3);
//
//        ExtrusionNode extrusionNode4 = new ExtrusionNode();
//        extrusionNode4.getExtrusion().setE(1f);
//        extrusionNode4.getMovement().setX(4);
//        extrusionNode4.getMovement().setY(4);
//
//        ExtrusionNode extrusionNode5 = new ExtrusionNode();
//        extrusionNode5.getExtrusion().setE(1f);
//        extrusionNode5.getMovement().setX(5);
//        extrusionNode5.getMovement().setY(5);
//
//        ExtrusionNode extrusionNode6 = new ExtrusionNode();
//        extrusionNode6.getExtrusion().setE(1f);
//        extrusionNode6.getMovement().setX(6);
//        extrusionNode6.getMovement().setY(6);
//
//        ExtrusionNode extrusionNode7 = new ExtrusionNode();
//        extrusionNode7.getExtrusion().setE(1f);
//        extrusionNode7.getMovement().setX(7);
//        extrusionNode7.getMovement().setY(7);
//
//        ExtrusionNode extrusionNode8 = new ExtrusionNode();
//        extrusionNode8.getExtrusion().setE(1f);
//        extrusionNode8.getMovement().setX(8);
//        extrusionNode8.getMovement().setY(8);
//
//        ExtrusionNode extrusionNode9 = new ExtrusionNode();
//        extrusionNode9.getExtrusion().setE(1f);
//        extrusionNode9.getMovement().setX(9);
//        extrusionNode9.getMovement().setY(9);
//
//        inner1.addChildAtEnd(travel1);
//        inner1.addChildAtEnd(extrusionNode1);
//        inner1.addChildAtEnd(extrusionNode2);
//        inner1.addChildAtEnd(extrusionNode3);
//        outer1.addChildAtEnd(extrusionNode4);
//        outer1.addChildAtEnd(extrusionNode5);
//        outer1.addChildAtEnd(extrusionNode6);
//        outer1.addChildAtEnd(extrusionNode7);
//        outer1.addChildAtEnd(extrusionNode8);
//        fill1.addChildAtEnd(extrusionNode9);
//
//        RetractNode retractNode = new RetractNode();
//        fill1.addChildAtEnd(retractNode);
//
//        ToolSelectNode tool1 = new ToolSelectNode();
//        tool1.addChildAtEnd(inner1);
//        tool1.addChildAtEnd(outer1);
//        tool1.addChildAtEnd(fill1);
//
//        NozzleParameters nozzleParams = new NozzleParameters();
//
//        nozzleParams.setEjectionVolume(2.5f);
//
//        NozzleProxy testProxy = new NozzleProxy(nozzleParams);
//
//        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
//        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
//        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
//        ppFeatures.enableFeature(PostProcessorFeature.CLOSE_ON_TASK_CHANGE);
//        ppFeatures.enableFeature(PostProcessorFeature.GRADUAL_CLOSE);
//
//        Project testProject = new Project();
//        testProject.getPrinterSettings().setSettingsName("BothNozzles");
//        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
//
//        CloseLogic closeLogic = new CloseLogic(testProject,
//                testProject.getPrinterSettings().getSettings("RBX01-SM"),
//                ppFeatures, "RBX01-SM");
//
//        List<SectionNode> sectionsToConsider = new ArrayList<>();
//        inner1.recalculateExtrusion();
//        sectionsToConsider.add(inner1);
//        outer1.recalculateExtrusion();
//        sectionsToConsider.add(outer1);
//        fill1.recalculateExtrusion();
//        sectionsToConsider.add(fill1);
//
//        retractNode.setPriorExtrusionNode(extrusionNode9);
//        retractNode.setSectionsToConsider(sectionsToConsider);
//
//        Optional<CloseResult> closeResult = Optional.empty();
//        OutputUtilities output = new OutputUtilities();
//        output.outputNodes(tool1, 0);
//
//        try
//        {
//            closeResult = closeLogic.insertProgressiveNozzleClose(retractNode, retractNode.getSectionsToConsider(), testProxy);
//        } catch (NotEnoughAvailableExtrusionException | NodeProcessingException | CannotCloseFromPerimeterException | NoPerimeterToCloseOverException | PostProcessingError ex)
//        {
//            fail();
//        }
//
//        assertTrue(closeResult.isPresent());
//
//        output.outputNodes(tool1, 0);
//
//        //Should have elided the same volume as the ejection volume
//        assertEquals(2.5, closeResult.get().getNozzleCloseOverVolume(), 0.01);
//
//        assertEquals(3, inner1.getChildren().size());
//        assertEquals(2, outer1.getChildren().size());
//        assertEquals(3, fill1.getChildren().size());
//
//        assertTrue(inner1.getChildren().get(0) instanceof ExtrusionNode);
//        ExtrusionNode node0 = (ExtrusionNode) inner1.getChildren().get(0);
//        assertSame(extrusionNode5, node0);
//        assertFalse(node0.getNozzlePosition().isBSet());
//        assertEquals(1, node0.getExtrusion().getE(), movementEpsilon);
//
//        assertTrue(inner1.getChildren().get(1) instanceof ExtrusionNode);
//        ExtrusionNode node1 = (ExtrusionNode) inner1.getChildren().get(1);
//        assertSame(extrusionNode6, node1);
//        assertFalse(node1.getNozzlePosition().isBSet());
//        assertEquals(1, node1.getExtrusion().getE(), movementEpsilon);
//
//        assertTrue(inner1.getChildren().get(2) instanceof ExtrusionNode);
//        ExtrusionNode node2 = (ExtrusionNode) inner1.getChildren().get(2);
//        assertSame(extrusionNode7, node2);
//        assertTrue(node2.getNozzlePosition().isBSet());
//        assertEquals(0.66, node2.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node2.getExtrusion().isEInUse());
//
//        assertTrue(fill1.getChildren().get(0) instanceof ExtrusionNode);
//        ExtrusionNode node3 = (ExtrusionNode) fill1.getChildren().get(0);
//        assertSame(extrusionNode8, node3);
//        assertTrue(node3.getNozzlePosition().isBSet());
//        assertEquals(0.33, node3.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node3.getExtrusion().isEInUse());
//
//        assertTrue(fill1.getChildren().get(1) instanceof ExtrusionNode);
//        ExtrusionNode node4 = (ExtrusionNode) fill1.getChildren().get(1);
//        assertSame(extrusionNode9, node4);
//        assertTrue(node4.getNozzlePosition().isBSet());
//        assertEquals(0.0, node4.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node4.getExtrusion().isEInUse());
//    }
//
//    @Test
//    public void testOverwriteClose_SingleSection()
//    {
//        FillSectionNode fill1 = new FillSectionNode();
//
//        TravelNode travel1 = new TravelNode();
//        travel1.getMovement().setX(0);
//        travel1.getMovement().setY(0);
//
//        ExtrusionNode extrusionNode1 = new ExtrusionNode();
//        extrusionNode1.getExtrusion().setE(1f);
//        extrusionNode1.getMovement().setX(1);
//        extrusionNode1.getMovement().setY(1);
//
//        ExtrusionNode extrusionNode2 = new ExtrusionNode();
//        extrusionNode2.getExtrusion().setE(1f);
//        extrusionNode2.getMovement().setX(2);
//        extrusionNode2.getMovement().setY(2);
//
//        ExtrusionNode extrusionNode3 = new ExtrusionNode();
//        extrusionNode3.getExtrusion().setE(1f);
//        extrusionNode3.getMovement().setX(3);
//        extrusionNode3.getMovement().setY(3);
//
//        ExtrusionNode extrusionNode4 = new ExtrusionNode();
//        extrusionNode4.getExtrusion().setE(1f);
//        extrusionNode4.getMovement().setX(4);
//        extrusionNode4.getMovement().setY(4);
//
//        ExtrusionNode extrusionNode5 = new ExtrusionNode();
//        extrusionNode5.getExtrusion().setE(1f);
//        extrusionNode5.getMovement().setX(5);
//        extrusionNode5.getMovement().setY(5);
//
//        ExtrusionNode extrusionNode6 = new ExtrusionNode();
//        extrusionNode6.getExtrusion().setE(1f);
//        extrusionNode6.getMovement().setX(6);
//        extrusionNode6.getMovement().setY(6);
//
//        ExtrusionNode extrusionNode7 = new ExtrusionNode();
//        extrusionNode7.getExtrusion().setE(1f);
//        extrusionNode7.getMovement().setX(7);
//        extrusionNode7.getMovement().setY(7);
//
//        ExtrusionNode extrusionNode8 = new ExtrusionNode();
//        extrusionNode8.getExtrusion().setE(1f);
//        extrusionNode8.getMovement().setX(8);
//        extrusionNode8.getMovement().setY(8);
//
//        ExtrusionNode extrusionNode9 = new ExtrusionNode();
//        extrusionNode9.getExtrusion().setE(1f);
//        extrusionNode9.getMovement().setX(9);
//        extrusionNode9.getMovement().setY(9);
//
//        fill1.addChildAtEnd(travel1);
//        fill1.addChildAtEnd(extrusionNode1);
//        fill1.addChildAtEnd(extrusionNode2);
//        fill1.addChildAtEnd(extrusionNode3);
//        fill1.addChildAtEnd(extrusionNode4);
//        fill1.addChildAtEnd(extrusionNode5);
//        fill1.addChildAtEnd(extrusionNode6);
//        fill1.addChildAtEnd(extrusionNode7);
//
//        ToolSelectNode tool1 = new ToolSelectNode();
//        tool1.addChildAtEnd(fill1);
//
//        NozzleParameters nozzleParams = new NozzleParameters();
//
//        nozzleParams.setEjectionVolume(4);
//
//        NozzleProxy testProxy = new NozzleProxy(nozzleParams);
//
//        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
//        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
//        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
//        ppFeatures.enableFeature(PostProcessorFeature.CLOSE_ON_TASK_CHANGE);
//
//        Project testProject = new Project();
//        testProject.getPrinterSettings().setSettingsName("BothNozzles");
//        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
//
//        CloseLogic closeLogic = new CloseLogic(testProject,
//                testProject.getPrinterSettings().getSettings("Draft"),
//                ppFeatures, "RBX01-SM");
//
//        List<SectionNode> sectionsToConsider = new ArrayList<>();
//        fill1.recalculateExtrusion();
//        sectionsToConsider.add(fill1);
//        
//        InScopeEvents inScopeEvents = closeLogic.extractAvailableMovements(extrusionNode7, sectionsToConsider, true, false);
//
//        Optional<CloseResult> closeResult = Optional.empty();
//        try
//        {
//            closeResult = closeLogic.overwriteClose(inScopeEvents, testProxy, false);
//        } catch (NotEnoughAvailableExtrusionException ex)
//        {
//            fail();
//        }
//        assertTrue(closeResult.isPresent());
//
////        assertEquals(8, outer1.getChildren().size());
////        assertEquals(1, support1.getChildren().size());
////        assertEquals(1, fill1.getChildren().size());
////
////        assertTrue(outer1.getChildren().get(5) instanceof ExtrusionNode);
////        ExtrusionNode node0 = (ExtrusionNode) outer1.getChildren().get(5);
////        assertSame(extrusionNode5, node0);
////        assertFalse(node0.getNozzlePosition().isBSet());
////        assertEquals(1, node0.getExtrusion().getE(), movementEpsilon);
////
////        assertTrue(outer1.getChildren().get(6) instanceof ExtrusionNode);
////        ExtrusionNode node1 = (ExtrusionNode) outer1.getChildren().get(6);
////        assertSame(extrusionNode6, node1);
////        assertFalse(node1.getNozzlePosition().isBSet());
////        assertEquals(1, node1.getExtrusion().getE(), movementEpsilon);
////
////        assertTrue(outer1.getChildren().get(7) instanceof ExtrusionNode);
////        ExtrusionNode node2 = (ExtrusionNode) outer1.getChildren().get(7);
////        assertSame(extrusionNode7, node2);
////        assertFalse(node2.getNozzlePosition().isBSet());
////        assertEquals(1, node2.getExtrusion().getE(), movementEpsilon);
////
////        assertTrue(support1.getChildren().get(0) instanceof ExtrusionNode);
////        ExtrusionNode node3 = (ExtrusionNode) support1.getChildren().get(0);
////        assertSame(extrusionNode8, node3);
////        assertFalse(node3.getNozzlePosition().isBSet());
////        assertEquals(1, node3.getExtrusion().getE(), movementEpsilon);
////
////        assertTrue(fill1.getChildren().get(0) instanceof ExtrusionNode);
////        ExtrusionNode node4 = (ExtrusionNode) fill1.getChildren().get(0);
////        assertSame(extrusionNode9, node4);
////        assertFalse(node4.getNozzlePosition().isBSet());
////        assertEquals(1, node4.getExtrusion().getE(), movementEpsilon);
//    }
//
//    @Test
//    public void testCloseToEndOfFill_checkUsesPeriIfRequired()
//    {
//        OuterPerimeterSectionNode outer1 = new OuterPerimeterSectionNode();
//        FillSectionNode fill1 = new FillSectionNode();
//        SupportSectionNode support1 = new SupportSectionNode();
//
//        TravelNode travel1 = new TravelNode();
//        travel1.getMovement().setX(0);
//        travel1.getMovement().setY(0);
//
//        ExtrusionNode extrusionNode1 = new ExtrusionNode();
//        extrusionNode1.getExtrusion().setE(1f);
//        extrusionNode1.getMovement().setX(1);
//        extrusionNode1.getMovement().setY(1);
//
//        ExtrusionNode extrusionNode2 = new ExtrusionNode();
//        extrusionNode2.getExtrusion().setE(1f);
//        extrusionNode2.getMovement().setX(2);
//        extrusionNode2.getMovement().setY(2);
//
//        ExtrusionNode extrusionNode3 = new ExtrusionNode();
//        extrusionNode3.getExtrusion().setE(1f);
//        extrusionNode3.getMovement().setX(3);
//        extrusionNode3.getMovement().setY(3);
//
//        ExtrusionNode extrusionNode4 = new ExtrusionNode();
//        extrusionNode4.getExtrusion().setE(1f);
//        extrusionNode4.getMovement().setX(4);
//        extrusionNode4.getMovement().setY(4);
//
//        ExtrusionNode extrusionNode5 = new ExtrusionNode();
//        extrusionNode5.getExtrusion().setE(1f);
//        extrusionNode5.getMovement().setX(5);
//        extrusionNode5.getMovement().setY(5);
//
//        ExtrusionNode extrusionNode6 = new ExtrusionNode();
//        extrusionNode6.getExtrusion().setE(1f);
//        extrusionNode6.getMovement().setX(6);
//        extrusionNode6.getMovement().setY(6);
//
//        ExtrusionNode extrusionNode7 = new ExtrusionNode();
//        extrusionNode7.getExtrusion().setE(1f);
//        extrusionNode7.getMovement().setX(7);
//        extrusionNode7.getMovement().setY(7);
//
//        ExtrusionNode extrusionNode8 = new ExtrusionNode();
//        extrusionNode8.getExtrusion().setE(1f);
//        extrusionNode8.getMovement().setX(8);
//        extrusionNode8.getMovement().setY(8);
//
//        ExtrusionNode extrusionNode9 = new ExtrusionNode();
//        extrusionNode9.getExtrusion().setE(1f);
//        extrusionNode9.getMovement().setX(9);
//        extrusionNode9.getMovement().setY(9);
//
//        outer1.addChildAtEnd(travel1);
//        outer1.addChildAtEnd(extrusionNode1);
//        outer1.addChildAtEnd(extrusionNode2);
//        outer1.addChildAtEnd(extrusionNode3);
//        outer1.addChildAtEnd(extrusionNode4);
//        outer1.addChildAtEnd(extrusionNode5);
//        outer1.addChildAtEnd(extrusionNode6);
//        outer1.addChildAtEnd(extrusionNode7);
//        support1.addChildAtEnd(extrusionNode8);
//        fill1.addChildAtEnd(extrusionNode9);
//
//        ToolSelectNode tool1 = new ToolSelectNode();
//        tool1.addChildAtEnd(outer1);
//        tool1.addChildAtEnd(support1);
//        tool1.addChildAtEnd(fill1);
//
//        NozzleParameters nozzleParams = new NozzleParameters();
//
//        nozzleParams.setEjectionVolume(3);
//
//        NozzleProxy testProxy = new NozzleProxy(nozzleParams);
//
//        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
//        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
//        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
//        ppFeatures.enableFeature(PostProcessorFeature.CLOSE_ON_TASK_CHANGE);
//
//        Project testProject = new Project();
//        testProject.getPrinterSettings().setSettingsName("BothNozzles");
//        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
//
//        CloseLogic closeLogic = new CloseLogic(testProject,
//                testProject.getPrinterSettings().getSettings("RBX01-SM"),
//                ppFeatures, "RBX01-SM");
//
//        List<SectionNode> sectionsToConsider = new ArrayList<>();
//        outer1.recalculateExtrusion();
//        sectionsToConsider.add(outer1);
//        support1.recalculateExtrusion();
//        sectionsToConsider.add(support1);
//        fill1.recalculateExtrusion();
//        sectionsToConsider.add(fill1);
//
//        Optional<CloseResult> closeResult = Optional.empty();
//        InScopeEvents inScopeEvents = closeLogic.extractAvailableMovements(extrusionNode9, sectionsToConsider, true, false);
//
//        try
//        {
//            closeResult = closeLogic.overwriteClose(inScopeEvents, testProxy, false);
//        } catch (NotEnoughAvailableExtrusionException ex)
//        {
//            fail();
//        }
//        OutputUtilities output = new OutputUtilities();
//        output.outputNodes(tool1, 0);
//
//        assertTrue(closeResult.isPresent());
//        //Should have elided the same volume as the ejection volume
//        assertEquals(3, closeResult.get().getNozzleCloseOverVolume(), 0.01);
//
//        assertEquals(8, outer1.getChildren().size());
//        assertEquals(1, support1.getChildren().size());
//        assertEquals(1, fill1.getChildren().size());
//
//        assertTrue(outer1.getChildren().get(5) instanceof ExtrusionNode);
//        ExtrusionNode node0 = (ExtrusionNode) outer1.getChildren().get(5);
//        assertSame(extrusionNode5, node0);
//        assertFalse(node0.getNozzlePosition().isBSet());
//        assertEquals(1, node0.getExtrusion().getE(), movementEpsilon);
//
//        assertTrue(outer1.getChildren().get(6) instanceof ExtrusionNode);
//        ExtrusionNode node1 = (ExtrusionNode) outer1.getChildren().get(6);
//        assertSame(extrusionNode6, node1);
//        assertFalse(node1.getNozzlePosition().isBSet());
//        assertEquals(1, node1.getExtrusion().getE(), movementEpsilon);
//
//        assertTrue(outer1.getChildren().get(7) instanceof ExtrusionNode);
//        ExtrusionNode node2 = (ExtrusionNode) outer1.getChildren().get(7);
//        assertSame(extrusionNode7, node2);
//        assertTrue(node2.getNozzlePosition().isBSet());
//        assertEquals(0.66, node2.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node2.getExtrusion().isEInUse());
//
//        assertTrue(support1.getChildren().get(0) instanceof ExtrusionNode);
//        ExtrusionNode node3 = (ExtrusionNode) support1.getChildren().get(0);
//        assertSame(extrusionNode8, node3);
//        assertTrue(node3.getNozzlePosition().isBSet());
//        assertEquals(0.33, node3.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node3.getExtrusion().isEInUse());
//
//        assertTrue(fill1.getChildren().get(0) instanceof ExtrusionNode);
//        ExtrusionNode node4 = (ExtrusionNode) fill1.getChildren().get(0);
//        assertSame(extrusionNode9, node4);
//        assertTrue(node4.getNozzlePosition().isBSet());
//        assertEquals(0.0, node4.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node4.getExtrusion().isEInUse());
//    }
//
//    @Test
//    public void testCloseToEndOfSection_splitAtEndRequired()
//    {
//        FillSectionNode fill1 = new FillSectionNode();
//
//        TravelNode travel1 = new TravelNode();
//        travel1.getMovement().setX(0);
//        travel1.getMovement().setY(0);
//
//        ExtrusionNode extrusionNode1 = new ExtrusionNode();
//        extrusionNode1.getExtrusion().setE(1f);
//        extrusionNode1.getMovement().setX(1);
//        extrusionNode1.getMovement().setY(1);
//        extrusionNode1.getFeedrate().setFeedRate_mmPerMin(10);
//
//        ExtrusionNode extrusionNode2 = new ExtrusionNode();
//        extrusionNode2.getExtrusion().setE(1f);
//        extrusionNode2.getMovement().setX(2);
//        extrusionNode2.getMovement().setY(2);
//        extrusionNode2.getFeedrate().setFeedRate_mmPerMin(10);
//
//        ExtrusionNode extrusionNode3 = new ExtrusionNode();
//        extrusionNode3.getExtrusion().setE(1f);
//        extrusionNode3.getMovement().setX(3);
//        extrusionNode3.getMovement().setY(3);
//        extrusionNode3.getFeedrate().setFeedRate_mmPerMin(10);
//
//        ExtrusionNode extrusionNode4 = new ExtrusionNode();
//        extrusionNode4.getExtrusion().setE(1f);
//        extrusionNode4.getMovement().setX(4);
//        extrusionNode4.getMovement().setY(4);
//        extrusionNode4.getFeedrate().setFeedRate_mmPerMin(10);
//
//        ExtrusionNode extrusionNode5 = new ExtrusionNode();
//        extrusionNode5.getExtrusion().setE(1f);
//        extrusionNode5.getMovement().setX(5);
//        extrusionNode5.getMovement().setY(5);
//        extrusionNode5.getFeedrate().setFeedRate_mmPerMin(10);
//
//        ExtrusionNode extrusionNode6 = new ExtrusionNode();
//        extrusionNode6.getExtrusion().setE(1f);
//        extrusionNode6.getMovement().setX(6);
//        extrusionNode6.getMovement().setY(6);
//        extrusionNode6.getFeedrate().setFeedRate_mmPerMin(10);
//
//        ExtrusionNode extrusionNode7 = new ExtrusionNode();
//        extrusionNode7.getExtrusion().setE(1f);
//        extrusionNode7.getMovement().setX(7);
//        extrusionNode7.getMovement().setY(7);
//        extrusionNode7.getFeedrate().setFeedRate_mmPerMin(10);
//
//        ExtrusionNode extrusionNode8 = new ExtrusionNode();
//        extrusionNode8.getExtrusion().setE(1f);
//        extrusionNode8.getMovement().setX(8);
//        extrusionNode8.getMovement().setY(8);
//        extrusionNode8.getFeedrate().setFeedRate_mmPerMin(10);
//
//        ExtrusionNode extrusionNode9 = new ExtrusionNode();
//        extrusionNode9.getExtrusion().setE(1f);
//        extrusionNode9.getMovement().setX(9);
//        extrusionNode9.getMovement().setY(9);
//        extrusionNode9.getFeedrate().setFeedRate_mmPerMin(10);
//
//        fill1.addChildAtEnd(travel1);
//        fill1.addChildAtEnd(extrusionNode1);
//        fill1.addChildAtEnd(extrusionNode2);
//        fill1.addChildAtEnd(extrusionNode3);
//        fill1.addChildAtEnd(extrusionNode4);
//        fill1.addChildAtEnd(extrusionNode5);
//        fill1.addChildAtEnd(extrusionNode6);
//        fill1.addChildAtEnd(extrusionNode7);
//        fill1.addChildAtEnd(extrusionNode8);
//        fill1.addChildAtEnd(extrusionNode9);
//
//        ToolSelectNode tool1 = new ToolSelectNode();
//        tool1.addChildAtEnd(fill1);
//
//        NozzleParameters nozzleParams = new NozzleParameters();
//        nozzleParams.setEjectionVolume(8.75f);
//
//        NozzleProxy testProxy = new NozzleProxy(nozzleParams);
//
//        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
//        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
//        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
//        ppFeatures.enableFeature(PostProcessorFeature.CLOSE_ON_TASK_CHANGE);
//
//        Project testProject = new Project();
//        testProject.getPrinterSettings().setSettingsName("BothNozzles");
//        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
//
//        OutputUtilities output = new OutputUtilities();
//        output.outputNodes(tool1, 0);
//
//        CloseLogic closeLogic = new CloseLogic(testProject,
//                testProject.getPrinterSettings().getSettings("RBX01-SM"),
//                ppFeatures, "RBX01-SM");
//
//        List<SectionNode> sectionsToConsider = new ArrayList<>();
//        fill1.recalculateExtrusion();
//        sectionsToConsider.add(fill1);
//
//        Optional<CloseResult> closeResult = Optional.empty();
//        InScopeEvents inScopeEvents = closeLogic.extractAvailableMovements(extrusionNode9, sectionsToConsider, true, false);
//
//        try
//        {
//            closeResult = closeLogic.overwriteClose(inScopeEvents, testProxy, false);
//        } catch (NotEnoughAvailableExtrusionException ex)
//        {
//            fail();
//        }
//        output.outputNodes(tool1, 0);
//
//        assertTrue(closeResult.isPresent());
//
//        //Should have elided the same volume as the ejection volume
//        assertEquals(8.75, closeResult.get().getNozzleCloseOverVolume(), 0.01);
//
//        assertEquals(11, fill1.getChildren().size());
//
//        assertTrue(fill1.getChildren().get(0) instanceof TravelNode);
//        TravelNode tnode0 = (TravelNode) fill1.getChildren().get(0);
//        assertEquals(0, tnode0.getMovement().getX(), movementEpsilon);
//        assertEquals(0, tnode0.getMovement().getY(), movementEpsilon);
//
//        assertTrue(fill1.getChildren().get(1) instanceof ExtrusionNode);
//        ExtrusionNode node0 = (ExtrusionNode) fill1.getChildren().get(1);
//        assertFalse(node0.getNozzlePosition().isBSet());
//        assertEquals(0.25, node0.getExtrusion().getE(), movementEpsilon);
//        assertEquals(0.25, node0.getMovement().getX(), movementEpsilon);
//        assertEquals(0.25, node0.getMovement().getY(), movementEpsilon);
//        //The new node needs to have the same feedrate as the other extrusions
//        assertEquals(extrusionNode9.getFeedrate().getFeedRate_mmPerMin(), node0.getFeedrate().getFeedRate_mmPerMin(), 0.01);
//
//        assertTrue(fill1.getChildren().get(2) instanceof ExtrusionNode);
//        ExtrusionNode node1 = (ExtrusionNode) fill1.getChildren().get(2);
//        assertTrue(node1.getNozzlePosition().isBSet());
//        assertEquals(0.91, node1.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node1.getExtrusion().isEInUse());
//
//        assertTrue(fill1.getChildren().get(3) instanceof ExtrusionNode);
//        ExtrusionNode node2 = (ExtrusionNode) fill1.getChildren().get(3);
//        assertTrue(node2.getNozzlePosition().isBSet());
//        assertEquals(0.8, node2.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node2.getExtrusion().isEInUse());
//
//        assertTrue(fill1.getChildren().get(4) instanceof ExtrusionNode);
//        ExtrusionNode node3 = (ExtrusionNode) fill1.getChildren().get(4);
//        assertTrue(node3.getNozzlePosition().isBSet());
//        assertEquals(0.69, node3.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node3.getExtrusion().isEInUse());
//
//        assertTrue(fill1.getChildren().get(5) instanceof ExtrusionNode);
//        ExtrusionNode node4 = (ExtrusionNode) fill1.getChildren().get(5);
//        assertTrue(node4.getNozzlePosition().isBSet());
//        assertEquals(0.57, node4.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node4.getExtrusion().isEInUse());
//
//        assertTrue(fill1.getChildren().get(6) instanceof ExtrusionNode);
//        ExtrusionNode node5 = (ExtrusionNode) fill1.getChildren().get(6);
//        assertTrue(node5.getNozzlePosition().isBSet());
//        assertEquals(0.46, node5.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node5.getExtrusion().isEInUse());
//
//        assertTrue(fill1.getChildren().get(7) instanceof ExtrusionNode);
//        ExtrusionNode node6 = (ExtrusionNode) fill1.getChildren().get(7);
//        assertTrue(node6.getNozzlePosition().isBSet());
//        assertEquals(0.34, node6.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node6.getExtrusion().isEInUse());
//
//        assertTrue(fill1.getChildren().get(8) instanceof ExtrusionNode);
//        ExtrusionNode node7 = (ExtrusionNode) fill1.getChildren().get(8);
//        assertTrue(node7.getNozzlePosition().isBSet());
//        assertEquals(0.23, node7.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node7.getExtrusion().isEInUse());
//
//        assertTrue(fill1.getChildren().get(9) instanceof ExtrusionNode);
//        ExtrusionNode node8 = (ExtrusionNode) fill1.getChildren().get(9);
//        assertTrue(node8.getNozzlePosition().isBSet());
//        assertEquals(0.11, node8.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node8.getExtrusion().isEInUse());
//
//        assertTrue(fill1.getChildren().get(10) instanceof ExtrusionNode);
//        ExtrusionNode node9 = (ExtrusionNode) fill1.getChildren().get(10);
//        assertTrue(node9.getNozzlePosition().isBSet());
//        assertEquals(0, node9.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node9.getExtrusion().isEInUse());
//    }
//
//    @Test
//    public void testCloseToEndOfFill_notEnoughVolumeInSection()
//    {
//        LayerNode testLayer = new LayerNode();
//
//        FillSectionNode fill1 = new FillSectionNode();
//
//        TravelNode travel1 = new TravelNode();
//        travel1.getMovement().setX(0);
//        travel1.getMovement().setY(0);
//
//        ExtrusionNode extrusionNode1 = new ExtrusionNode();
//        extrusionNode1.getExtrusion().setE(1f);
//        extrusionNode1.getMovement().setX(1);
//        extrusionNode1.getMovement().setY(1);
//
//        ExtrusionNode extrusionNode2 = new ExtrusionNode();
//        extrusionNode2.getExtrusion().setE(1f);
//        extrusionNode2.getMovement().setX(2);
//        extrusionNode2.getMovement().setY(2);
//
//        ExtrusionNode extrusionNode3 = new ExtrusionNode();
//        extrusionNode3.getExtrusion().setE(1f);
//        extrusionNode3.getMovement().setX(3);
//        extrusionNode3.getMovement().setY(3);
//
//        ExtrusionNode extrusionNode4 = new ExtrusionNode();
//        extrusionNode4.getExtrusion().setE(1f);
//        extrusionNode4.getMovement().setX(4);
//        extrusionNode4.getMovement().setY(4);
//
//        ExtrusionNode extrusionNode5 = new ExtrusionNode();
//        extrusionNode5.getExtrusion().setE(1f);
//        extrusionNode5.getMovement().setX(5);
//        extrusionNode5.getMovement().setY(5);
//
//        ExtrusionNode extrusionNode6 = new ExtrusionNode();
//        extrusionNode6.getExtrusion().setE(1f);
//        extrusionNode6.getMovement().setX(6);
//        extrusionNode6.getMovement().setY(6);
//
//        ExtrusionNode extrusionNode7 = new ExtrusionNode();
//        extrusionNode7.getExtrusion().setE(1f);
//        extrusionNode7.getMovement().setX(7);
//        extrusionNode7.getMovement().setY(7);
//
//        ExtrusionNode extrusionNode8 = new ExtrusionNode();
//        extrusionNode8.getExtrusion().setE(1f);
//        extrusionNode8.getMovement().setX(8);
//        extrusionNode8.getMovement().setY(8);
//
//        ExtrusionNode extrusionNode9 = new ExtrusionNode();
//        extrusionNode9.getExtrusion().setE(1f);
//        extrusionNode9.getMovement().setX(9);
//        extrusionNode9.getMovement().setY(9);
//
//        fill1.addChildAtEnd(travel1);
//        fill1.addChildAtEnd(extrusionNode1);
//        fill1.addChildAtEnd(extrusionNode2);
//        fill1.addChildAtEnd(extrusionNode3);
//        fill1.addChildAtEnd(extrusionNode4);
//        fill1.addChildAtEnd(extrusionNode5);
//        fill1.addChildAtEnd(extrusionNode6);
//        fill1.addChildAtEnd(extrusionNode7);
//        fill1.addChildAtEnd(extrusionNode8);
//        fill1.addChildAtEnd(extrusionNode9);
//
//        OuterPerimeterSectionNode outer1 = new OuterPerimeterSectionNode();
//        ExtrusionNode extrusionNode10 = new ExtrusionNode();
//        outer1.addChildAtEnd(extrusionNode10);
//
//        testLayer.addChildAtEnd(fill1);
//        testLayer.addChildAtEnd(outer1);
//
//        NozzleParameters nozzleParams = new NozzleParameters();
//        nozzleParams.setEjectionVolume(10f);
//
//        NozzleProxy testProxy = new NozzleProxy(nozzleParams);
//
//        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
//        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
//        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
//        ppFeatures.enableFeature(PostProcessorFeature.CLOSE_ON_TASK_CHANGE);
//
//        Project testProject = new Project();
//        testProject.getPrinterSettings().setSettingsName("BothNozzles");
//        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
//
//        CloseLogic closeLogic = new CloseLogic(testProject,
//                testProject.getPrinterSettings().getSettings("RBX01-SM"),
//                ppFeatures, "RBX01-SM");
//
//        List<SectionNode> sectionsToConsider = new ArrayList<>();
//        fill1.recalculateExtrusion();
//        sectionsToConsider.add(fill1);
//        outer1.recalculateExtrusion();
//        sectionsToConsider.add(outer1);
//
//        Optional<CloseResult> closeResult = Optional.empty();
//        InScopeEvents inScopeEvents = closeLogic.extractAvailableMovements(extrusionNode9, sectionsToConsider, true, false);
//
//        try
//        {
//            closeResult = closeLogic.overwriteClose(inScopeEvents, testProxy, false);
//        } catch (NotEnoughAvailableExtrusionException ex)
//        {
//            fail();
//        }
//        assertTrue(closeResult.isPresent());
////        //Should have elided the volume in the fill section
////        //We didn't have enough volume for the entire eject volume
////        assertEquals(9, closeResult.get().getNozzleCloseOverVolume(), 0.01);
////
////        assertEquals(10, fill1.getChildren().size());
////
////        assertTrue(fill1.getChildren().get(0) instanceof TravelNode);
////        TravelNode tnode0 = (TravelNode) fill1.getChildren().get(0);
////        assertEquals(0, tnode0.getMovement().getX(), movementEpsilon);
////        assertEquals(0, tnode0.getMovement().getY(), movementEpsilon);
////
////        assertTrue(fill1.getChildren().get(1) instanceof ExtrusionNode);
////        ExtrusionNode node0 = (ExtrusionNode) fill1.getChildren().get(1);
////        assertTrue(node0.getNozzlePosition().isBSet());
////        assertEquals(0.89, node0.getNozzlePosition().getB(), nozzleEpsilon);
////        assertEquals(1, node0.getMovement().getX(), movementEpsilon);
////        assertEquals(1, node0.getMovement().getY(), movementEpsilon);
////        assertFalse(node0.getExtrusion().isEInUse());
////
////        assertTrue(fill1.getChildren().get(2) instanceof ExtrusionNode);
////        ExtrusionNode node1 = (ExtrusionNode) fill1.getChildren().get(2);
////        assertTrue(node1.getNozzlePosition().isBSet());
////        assertEquals(0.77, node1.getNozzlePosition().getB(), nozzleEpsilon);
////        assertFalse(node1.getExtrusion().isEInUse());
////
////        assertTrue(fill1.getChildren().get(3) instanceof ExtrusionNode);
////        ExtrusionNode node2 = (ExtrusionNode) fill1.getChildren().get(3);
////        assertTrue(node2.getNozzlePosition().isBSet());
////        assertEquals(0.66, node2.getNozzlePosition().getB(), nozzleEpsilon);
////        assertFalse(node2.getExtrusion().isEInUse());
////
////        assertTrue(fill1.getChildren().get(4) instanceof ExtrusionNode);
////        ExtrusionNode node3 = (ExtrusionNode) fill1.getChildren().get(4);
////        assertTrue(node3.getNozzlePosition().isBSet());
////        assertEquals(0.56, node3.getNozzlePosition().getB(), nozzleEpsilon);
////        assertFalse(node3.getExtrusion().isEInUse());
////
////        assertTrue(fill1.getChildren().get(5) instanceof ExtrusionNode);
////        ExtrusionNode node4 = (ExtrusionNode) fill1.getChildren().get(5);
////        assertTrue(node4.getNozzlePosition().isBSet());
////        assertEquals(0.44, node4.getNozzlePosition().getB(), nozzleEpsilon);
////        assertFalse(node4.getExtrusion().isEInUse());
////
////        assertTrue(fill1.getChildren().get(6) instanceof ExtrusionNode);
////        ExtrusionNode node5 = (ExtrusionNode) fill1.getChildren().get(6);
////        assertTrue(node5.getNozzlePosition().isBSet());
////        assertEquals(0.33, node5.getNozzlePosition().getB(), nozzleEpsilon);
////        assertFalse(node5.getExtrusion().isEInUse());
////
////        assertTrue(fill1.getChildren().get(7) instanceof ExtrusionNode);
////        ExtrusionNode node6 = (ExtrusionNode) fill1.getChildren().get(7);
////        assertTrue(node6.getNozzlePosition().isBSet());
////        assertEquals(0.22, node6.getNozzlePosition().getB(), nozzleEpsilon);
////        assertFalse(node6.getExtrusion().isEInUse());
////
////        assertTrue(fill1.getChildren().get(8) instanceof ExtrusionNode);
////        ExtrusionNode node7 = (ExtrusionNode) fill1.getChildren().get(8);
////        assertTrue(node7.getNozzlePosition().isBSet());
////        assertEquals(0.11, node7.getNozzlePosition().getB(), nozzleEpsilon);
////        assertFalse(node7.getExtrusion().isEInUse());
////
////        assertTrue(fill1.getChildren().get(9) instanceof ExtrusionNode);
////        ExtrusionNode node8 = (ExtrusionNode) fill1.getChildren().get(9);
////        assertTrue(node8.getNozzlePosition().isBSet());
////        assertEquals(0, node8.getNozzlePosition().getB(), nozzleEpsilon);
////        assertFalse(node8.getExtrusion().isEInUse());
//    }
//
//    @Test
//    public void testCloseToEndOfFill_splitAtStartPriorIsInSection()
//    {
//        FillSectionNode fill1 = new FillSectionNode();
//
//        ExtrusionNode extrusionNode1 = new ExtrusionNode();
//        extrusionNode1.getExtrusion().setE(1f);
//        extrusionNode1.getMovement().setX(1);
//        extrusionNode1.getMovement().setY(1);
//
//        ExtrusionNode extrusionNode2 = new ExtrusionNode();
//        extrusionNode2.getExtrusion().setE(1f);
//        extrusionNode2.getMovement().setX(2);
//        extrusionNode2.getMovement().setY(2);
//
//        ExtrusionNode extrusionNode3 = new ExtrusionNode();
//        extrusionNode3.getExtrusion().setE(1f);
//        extrusionNode3.getMovement().setX(3);
//        extrusionNode3.getMovement().setY(3);
//
//        ExtrusionNode extrusionNode4 = new ExtrusionNode();
//        extrusionNode4.getExtrusion().setE(1f);
//        extrusionNode4.getMovement().setX(4);
//        extrusionNode4.getMovement().setY(4);
//
//        ExtrusionNode extrusionNode5 = new ExtrusionNode();
//        extrusionNode5.getExtrusion().setE(1f);
//        extrusionNode5.getMovement().setX(5);
//        extrusionNode5.getMovement().setY(5);
//
//        ExtrusionNode extrusionNode6 = new ExtrusionNode();
//        extrusionNode6.getExtrusion().setE(1f);
//        extrusionNode6.getMovement().setX(6);
//        extrusionNode6.getMovement().setY(6);
//
//        ExtrusionNode extrusionNode7 = new ExtrusionNode();
//        extrusionNode7.getExtrusion().setE(1f);
//        extrusionNode7.getMovement().setX(7);
//        extrusionNode7.getMovement().setY(7);
//
//        ExtrusionNode extrusionNode8 = new ExtrusionNode();
//        extrusionNode8.getExtrusion().setE(1f);
//        extrusionNode8.getMovement().setX(8);
//        extrusionNode8.getMovement().setY(8);
//
//        ExtrusionNode extrusionNode9 = new ExtrusionNode();
//        extrusionNode9.getExtrusion().setE(1f);
//        extrusionNode9.getMovement().setX(9);
//        extrusionNode9.getMovement().setY(9);
//
//        fill1.addChildAtEnd(extrusionNode1);
//        fill1.addChildAtEnd(extrusionNode2);
//        fill1.addChildAtEnd(extrusionNode3);
//        fill1.addChildAtEnd(extrusionNode4);
//        fill1.addChildAtEnd(extrusionNode5);
//        fill1.addChildAtEnd(extrusionNode6);
//        fill1.addChildAtEnd(extrusionNode7);
//        fill1.addChildAtEnd(extrusionNode8);
//        fill1.addChildAtEnd(extrusionNode9);
//
//        OuterPerimeterSectionNode outer1 = new OuterPerimeterSectionNode();
//
//        TravelNode travel1 = new TravelNode();
//        travel1.getMovement().setX(0);
//        travel1.getMovement().setY(0);
//
//        outer1.addChildAtEnd(travel1);
//
//        ToolSelectNode tool1 = new ToolSelectNode();
//        tool1.addChildAtEnd(outer1);
//        tool1.addChildAtEnd(fill1);
//
//        NozzleParameters nozzleParams = new NozzleParameters();
//        nozzleParams.setEjectionVolume(8.75f);
//
//        NozzleProxy testProxy = new NozzleProxy(nozzleParams);
//
//        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
//        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
//        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
//        ppFeatures.enableFeature(PostProcessorFeature.CLOSE_ON_TASK_CHANGE);
//
//        Project testProject = new Project();
//        testProject.getPrinterSettings().setSettingsName("BothNozzles");
//        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
//
//        OutputUtilities output = new OutputUtilities();
//        output.outputNodes(tool1, 0);
//
//        CloseLogic closeLogic = new CloseLogic(testProject,
//                testProject.getPrinterSettings().getSettings("RBX01-SM"),
//                ppFeatures, "RBX01-SM");
//
//        List<SectionNode> sectionsToConsider = new ArrayList<>();
//        outer1.recalculateExtrusion();
//        sectionsToConsider.add(outer1);
//        fill1.recalculateExtrusion();
//        sectionsToConsider.add(fill1);
//
//        Optional<CloseResult> closeResult = Optional.empty();
//        InScopeEvents inScopeEvents = closeLogic.extractAvailableMovements(extrusionNode9, sectionsToConsider, true, false);
//
//        try
//        {
//            closeResult = closeLogic.overwriteClose(inScopeEvents, testProxy, false);
//        } catch (NotEnoughAvailableExtrusionException ex)
//        {
//            fail();
//        }
//        assertTrue(closeResult.isPresent());
//
//        output.outputNodes(tool1, 0);
//        assertEquals(10, fill1.getChildren().size());
//
//        assertTrue(fill1.getChildren().get(0) instanceof ExtrusionNode);
//        ExtrusionNode node0 = (ExtrusionNode) fill1.getChildren().get(0);
//        assertFalse(node0.getNozzlePosition().isBSet());
//        assertEquals(0.25, node0.getExtrusion().getE(), movementEpsilon);
//        assertEquals(0.25, node0.getMovement().getX(), movementEpsilon);
//        assertEquals(0.25, node0.getMovement().getY(), movementEpsilon);
//
//        assertTrue(fill1.getChildren().get(1) instanceof ExtrusionNode);
//        ExtrusionNode node1 = (ExtrusionNode) fill1.getChildren().get(1);
//        assertTrue(node1.getNozzlePosition().isBSet());
//        assertEquals(0.91, node1.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node1.getExtrusion().isEInUse());
//
//        assertTrue(fill1.getChildren().get(2) instanceof ExtrusionNode);
//        ExtrusionNode node2 = (ExtrusionNode) fill1.getChildren().get(2);
//        assertTrue(node2.getNozzlePosition().isBSet());
//        assertEquals(0.8, node2.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node2.getExtrusion().isEInUse());
//
//        assertTrue(fill1.getChildren().get(3) instanceof ExtrusionNode);
//        ExtrusionNode node3 = (ExtrusionNode) fill1.getChildren().get(3);
//        assertTrue(node3.getNozzlePosition().isBSet());
//        assertEquals(0.69, node3.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node3.getExtrusion().isEInUse());
//
//        assertTrue(fill1.getChildren().get(4) instanceof ExtrusionNode);
//        ExtrusionNode node4 = (ExtrusionNode) fill1.getChildren().get(4);
//        assertTrue(node4.getNozzlePosition().isBSet());
//        assertEquals(0.57, node4.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node4.getExtrusion().isEInUse());
//
//        assertTrue(fill1.getChildren().get(5) instanceof ExtrusionNode);
//        ExtrusionNode node5 = (ExtrusionNode) fill1.getChildren().get(5);
//        assertTrue(node5.getNozzlePosition().isBSet());
//        assertEquals(0.46, node5.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node5.getExtrusion().isEInUse());
//
//        assertTrue(fill1.getChildren().get(6) instanceof ExtrusionNode);
//        ExtrusionNode node6 = (ExtrusionNode) fill1.getChildren().get(6);
//        assertTrue(node6.getNozzlePosition().isBSet());
//        assertEquals(0.34, node6.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node6.getExtrusion().isEInUse());
//
//        assertTrue(fill1.getChildren().get(7) instanceof ExtrusionNode);
//        ExtrusionNode node7 = (ExtrusionNode) fill1.getChildren().get(7);
//        assertTrue(node7.getNozzlePosition().isBSet());
//        assertEquals(0.23, node7.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node7.getExtrusion().isEInUse());
//
//        assertTrue(fill1.getChildren().get(8) instanceof ExtrusionNode);
//        ExtrusionNode node8 = (ExtrusionNode) fill1.getChildren().get(8);
//        assertTrue(node8.getNozzlePosition().isBSet());
//        assertEquals(0.11, node8.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node8.getExtrusion().isEInUse());
//
//        assertTrue(fill1.getChildren().get(9) instanceof ExtrusionNode);
//        ExtrusionNode node9 = (ExtrusionNode) fill1.getChildren().get(9);
//        assertTrue(node9.getNozzlePosition().isBSet());
//        assertEquals(0, node9.getNozzlePosition().getB(), nozzleEpsilon);
//        assertFalse(node9.getExtrusion().isEInUse());
//    }
//
////    @Test
////    public void testCloseOverSections()
////    {
////        ToolSelectNode tool1 = setupToolNodeWithInnerAndOuterSquare(true, false);
////
////        NozzleParameters nozzleParams = new NozzleParameters();
////        nozzleParams.setEjectionVolume(0.15f);
////
////        NozzleProxy testProxy = new NozzleProxy(nozzleParams);
////        testProxy.setCurrentPosition(1.0); // The nozzle starts fully open
////
////        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
////        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
////        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
////        ppFeatures.enableFeature(PostProcessorFeature.CLOSE_ON_TASK_CHANGE);
////
////        Project testProject = new Project();
////        testProject.getPrinterSettings().setSettingsName("BothNozzles");
////        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
////
////        CloseLogic closeLogic = new CloseLogic(testProject,
////                testProject.getPrinterSettings().getSettings("RBX01-SM"),
////                ppFeatures, "RBX01-SM");
////
////        List<SectionNode> sectionsToCloseOver = new ArrayList<>();
////        //Add fill, then inner, then external
////        sectionsToCloseOver.add((SectionNode) tool1.getChildren().get(0));
////        sectionsToCloseOver.add((SectionNode) tool1.getChildren().get(1));
////        sectionsToCloseOver.add((SectionNode) tool1.getChildren().get(2));
//////        InScopeEvents inScopeEvents = closeLogic.extractAvailableMovements(sectionsToCloseOver, sectionsToConsider, true, false);
//////
//////        Optional<CloseResult> closeResult = closeLogic.copyClose(tool1.getChildren().get(1).getChildren().get(4),
//////                sectionsToCloseOver,
//////                testProxy);
////
////        assertTrue(closeResult.isPresent());
////
////        //Should have elided the ejection volume
////        assertEquals(0.15, closeResult.get().getNozzleCloseOverVolume(), 0.01);
////
////        OuterPerimeterSectionNode outerResult = (OuterPerimeterSectionNode) tool1.getChildren().get(1);
////        assertEquals(7, outerResult.getChildren().size());
////
////        assertTrue(outerResult.getChildren().get(5) instanceof ExtrusionNode);
////        ExtrusionNode extrusionResult1 = (ExtrusionNode) outerResult.getChildren().get(5);
////        assertEquals(1, extrusionResult1.getMovement().getX(), movementEpsilon);
////        assertEquals(9, extrusionResult1.getMovement().getY(), movementEpsilon);
////        assertFalse(extrusionResult1.getExtrusion().isEInUse());
////        assertEquals(0.333, extrusionResult1.getNozzlePosition().getB(), movementEpsilon);
////        assertEquals(20, extrusionResult1.getFeedrate().getFeedRate_mmPerMin(), 0.01);
////
////        assertTrue(outerResult.getChildren().get(6) instanceof ExtrusionNode);
////        ExtrusionNode extrusionResult2 = (ExtrusionNode) outerResult.getChildren().get(6);
////        assertEquals(5, extrusionResult2.getMovement().getX(), movementEpsilon);
////        assertEquals(9, extrusionResult2.getMovement().getY(), movementEpsilon);
////        assertFalse(extrusionResult2.getExtrusion().isEInUse());
////        assertEquals(0, extrusionResult2.getNozzlePosition().getB(), movementEpsilon);
////        assertEquals(20, extrusionResult2.getFeedrate().getFeedRate_mmPerMin(), 0.01);
////    }
//
////    @Test
////    public void testAddClosesUsingSpecifiedNode_backwardInFirstSegment()
////    {
////        ToolSelectNode tool1 = setupToolNodeWithInnerAndOuterSquare(true, false);
////
////        NozzleParameters nozzleParams = new NozzleParameters();
////        nozzleParams.setEjectionVolume(0.05f);
////
////        NozzleProxy testProxy = new NozzleProxy(nozzleParams);
////        testProxy.setCurrentPosition(1.0); // The nozzle starts fully open
////
////        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
////        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
////        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
////        ppFeatures.enableFeature(PostProcessorFeature.CLOSE_ON_TASK_CHANGE);
////
////        Project testProject = new Project();
////        testProject.getPrinterSettings().setSettingsName("BothNozzles");
////        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
////
////        OutputUtilities output = new OutputUtilities();
////        output.outputNodes(tool1, 0);
////
////        CloseLogic closeLogic = new CloseLogic(testProject,
////                testProject.getPrinterSettings().getSettings("RBX01-SM"),
////                ppFeatures, "RBX01-SM");
////
////        List<SectionNode> sectionsToCloseOver = new ArrayList<>();
////        //Add fill, then inner, then external
////        sectionsToCloseOver.add((SectionNode)tool1.getChildren().get(2));
////        sectionsToCloseOver.add((SectionNode)tool1.getChildren().get(0));
////        sectionsToCloseOver.add((SectionNode)tool1.getChildren().get(1));
////
////        Optional<CloseResult> closeResult = closeLogic.copyClose(tool1.getChildren().get(1).getChildren().get(4),
////                sectionsToCloseOver,
////                testProxy);
////        
////        Optional<CloseResult> closeResult = closeLogic.copyClose((ExtrusionNode) tool1.getChildren().get(1).getChildren().get(4),
////                tool1.getChildren().get(0).getChildren().get(4),
////                testProxy, false,
////                0, false);
////
////        output.outputNodes(tool1, 0);
////
////        assertTrue(closeResult.isPresent());
////
////        //Should have elided the ejection volume
////        assertEquals(0.05, closeResult.get().getNozzleCloseOverVolume(), 0.01);
////
////        OuterPerimeterSectionNode outerResult = (OuterPerimeterSectionNode) tool1.getChildren().get(1);
////        assertEquals(6, outerResult.getChildren().size());
////
////        assertTrue(outerResult.getChildren().get(5) instanceof ExtrusionNode);
////        ExtrusionNode extrusionResult1 = (ExtrusionNode) outerResult.getChildren().get(5);
////        assertEquals(1, extrusionResult1.getMovement().getX(), movementEpsilon);
////        assertEquals(5, extrusionResult1.getMovement().getY(), movementEpsilon);
////        assertFalse(extrusionResult1.getExtrusion().isEInUse());
////        assertEquals(0, extrusionResult1.getNozzlePosition().getB(), nozzleEpsilon);
////        assertEquals(20, extrusionResult1.getFeedrate().getFeedRate_mmPerMin(), 0.01);
////    }
////
////    @Test
////    public void testAddClosesUsingSpecifiedNode_overAvailableVolume()
////    {
////        ToolSelectNode tool1 = setupToolNodeWithInnerAndOuterSquare(true, false);
////
////        NozzleParameters nozzleParams = new NozzleParameters();
////        nozzleParams.setEjectionVolume(4f);
////
////        NozzleProxy testProxy = new NozzleProxy(nozzleParams);
////        testProxy.setCurrentPosition(1.0); // The nozzle starts fully open
////
////        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
////        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
////        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
////        ppFeatures.enableFeature(PostProcessorFeature.CLOSE_ON_TASK_CHANGE);
////
////        Project testProject = new Project();
////        testProject.getPrinterSettings().setSettingsName("BothNozzles");
////        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
////
////        CloseLogic closeLogic = new CloseLogic(testProject,
////                testProject.getPrinterSettings().getSettings("RBX01-SM"),
////                ppFeatures, "RBX01-SM");
////
////        Optional<CloseResult> closeResult = closeLogic.copyClose((ExtrusionNode) tool1.getChildren().get(1).getChildren().get(4),
////                tool1.getChildren().get(0).getChildren().get(4),
////                testProxy, false,
////                0.3, true);
////
////        assertTrue(closeResult.isPresent());
////
////        //The elided volume should be equivalent to that of the nodes we copied (4 in this instance)
////        assertEquals(4, closeResult.get().getNozzleCloseOverVolume(), 0.01);
////
////        OuterPerimeterSectionNode outerResult = (OuterPerimeterSectionNode) tool1.getChildren().get(1);
////        assertEquals(8, outerResult.getChildren().size());
////
////        assertTrue(outerResult.getChildren().get(5) instanceof ExtrusionNode);
////        ExtrusionNode extrusionResult1 = (ExtrusionNode) outerResult.getChildren().get(5);
////        assertEquals(1, extrusionResult1.getMovement().getX(), movementEpsilon);
////        assertEquals(9, extrusionResult1.getMovement().getY(), movementEpsilon);
////        assertFalse(extrusionResult1.getExtrusion().isEInUse());
////        assertEquals(0.67, extrusionResult1.getNozzlePosition().getB(), nozzleEpsilon);
////
////        assertTrue(outerResult.getChildren().get(6) instanceof ExtrusionNode);
////        ExtrusionNode extrusionResult2 = (ExtrusionNode) outerResult.getChildren().get(6);
////        assertEquals(9, extrusionResult2.getMovement().getX(), movementEpsilon);
////        assertEquals(9, extrusionResult2.getMovement().getY(), movementEpsilon);
////        assertFalse(extrusionResult2.getExtrusion().isEInUse());
////        assertEquals(0.33, extrusionResult2.getNozzlePosition().getB(), nozzleEpsilon);
////
////        assertTrue(outerResult.getChildren().get(7) instanceof ExtrusionNode);
////        ExtrusionNode extrusionResult3 = (ExtrusionNode) outerResult.getChildren().get(7);
////        assertEquals(9, extrusionResult3.getMovement().getX(), movementEpsilon);
////        assertEquals(1, extrusionResult3.getMovement().getY(), movementEpsilon);
////        assertFalse(extrusionResult3.getExtrusion().isEInUse());
////        assertEquals(0, extrusionResult3.getNozzlePosition().getB(), nozzleEpsilon);
////    }
//    //TODO reinstate
////    @Test
////    public void testCloseUsingSectionTemplate_ejectVolumeTooSmall_dontClose()
////    {
////        ToolSelectNode tool1 = setupToolNodeWithInnerAndOuterSquare(true, true);
////
////        NozzleParameters nozzleParams = new NozzleParameters();
////        nozzleParams.setEjectionVolume(100f);
////
////        NozzleProxy testProxy = new NozzleProxy(nozzleParams);
////        testProxy.setCurrentPosition(1.0);
////
////        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
////        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
////        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
////        ppFeatures.enableFeature(PostProcessorFeature.CLOSES_ON_RETRACT);
////        ppFeatures.enableFeature(PostProcessorFeature.CLOSE_ON_TASK_CHANGE);
////
////        Project testProject = new Project();
////        testProject.getPrinterSettings().setSettingsName("BothNozzles");
////        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
////
////        List<SectionNode> sectionsToConsider = new ArrayList<>();
////        sectionsToConsider.add((SectionNode) tool1.getChildren().get(0));
////        sectionsToConsider.add((SectionNode) tool1.getChildren().get(1));
////        sectionsToConsider.add((SectionNode) tool1.getChildren().get(2));
////
////        sectionsToConsider.forEach(section ->
////        {
////            section.recalculateExtrusion();
////        });
////
////        CloseLogic closeLogic = new CloseLogic(testProject,
////                testProject.getPrinterSettings().getSettings("RBX01-SM"),
////                ppFeatures, "RBX01-SM");
////
////        Optional<CloseResult> closeResult = Optional.empty();
////
////        try
////        {
////            closeResult = closeLogic.insertProgressiveNozzleClose(sectionsToConsider,
////                    (ExtrusionNode) (tool1.getChildren().get(1).getChildren().get(4)),
////                    testProxy);
////            fail("Correct exception was not raised");
////        } catch (CannotCloseFromPerimeterException | NoPerimeterToCloseOverException | NodeProcessingException | NotEnoughAvailableExtrusionException | PostProcessingError ex)
////        {
////            assertTrue(ex instanceof NotEnoughAvailableExtrusionException);
////        }
////
////        assertFalse(closeResult.isPresent());
////        
////        assertEquals(5, tool1.getChildren().get(0).getChildren().size());
////        assertEquals(5, tool1.getChildren().get(1).getChildren().size());
////        assertEquals(6, tool1.getChildren().get(2).getChildren().size());
////
////    }
////    @Test
////    public void testCloseUsingSectionTemplate_closeOnNextFill()
////    {
////        ToolSelectNode tool1 = setupToolNodeWithInnerAndOuterSquare(true, true);
////
////        NozzleParameters nozzleParams = new NozzleParameters();
////        nozzleParams.setEjectionVolume(0.15f);
////
////        NozzleProxy testProxy = new NozzleProxy(nozzleParams);
////        testProxy.setCurrentPosition(1.0);
////
////        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
////        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
////        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
////        ppFeatures.enableFeature(PostProcessorFeature.CLOSES_ON_RETRACT);
////        ppFeatures.enableFeature(PostProcessorFeature.CLOSE_ON_TASK_CHANGE);
////
////        Project testProject = new Project();
////        testProject.getPrinterSettings().setSettingsName("CloseTest");
////        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
////
////        CloseLogic closeLogic = new CloseLogic(testProject,
////                testProject.getPrinterSettings().getSettings("RBX01-SM"),
////                ppFeatures, "RBX01-SM");
////
////        Optional<CloseResult> closeResult = Optional.empty();
////
//////                closeLogic.closeInwardsOntoPerimeter((SectionNode) tool1.getChildren().get(2),
//////                ((ExtrusionNode) tool1.getChildren().get(1).getChildren().get(4)),
//////                testProxy);
////        assertTrue(closeResult.isPresent());
//////        assertTrue(closeResult.get().getClosestNode().isPresent());
//////        assertTrue(closeResult.get().getClosestNode().get() instanceof MovementProvider);
//////        Movement closestMovement = ((MovementProvider) closeResult.get().getClosestNode().get()).getMovement();
//////        assertEquals(2, closestMovement.getX(), movementEpsilon);
//////        assertEquals(2, closestMovement.getY(), movementEpsilon);
////
////        OuterPerimeterSectionNode outerResult = (OuterPerimeterSectionNode) tool1.getChildren().get(1);
////        assertEquals(7, outerResult.getChildren().size());
////
////        assertTrue(outerResult.getChildren().get(5) instanceof ExtrusionNode);
////        ExtrusionNode extrusionResult1 = (ExtrusionNode) outerResult.getChildren().get(5);
////        assertEquals(2, extrusionResult1.getMovement().getX(), movementEpsilon);
////        assertEquals(8, extrusionResult1.getMovement().getY(), movementEpsilon);
////        assertFalse(extrusionResult1.getExtrusion().isEInUse());
////        assertEquals(0.333, extrusionResult1.getNozzlePosition().getB(), nozzleEpsilon);
////
////        assertTrue(outerResult.getChildren().get(6) instanceof ExtrusionNode);
////        ExtrusionNode extrusionResult2 = (ExtrusionNode) outerResult.getChildren().get(6);
////        assertEquals(3.5, extrusionResult2.getMovement().getX(), movementEpsilon);
////        assertEquals(8, extrusionResult2.getMovement().getY(), movementEpsilon);
////        assertFalse(extrusionResult2.getExtrusion().isEInUse());
////        assertEquals(0, extrusionResult2.getNozzlePosition().getB(), nozzleEpsilon);
////    }
////    @Test
////    public void testCloseUsingSectionTemplate_closeOnPreviousInner()
////    {
////        ToolSelectNode tool1 = setupToolNodeWithInnerAndOuterSquare(true, false);
////
////        NozzleParameters nozzleParams = new NozzleParameters();
////        nozzleParams.setEjectionVolume(0.15f);
////
////        NozzleProxy testProxy = new NozzleProxy(nozzleParams);
////        testProxy.setCurrentPosition(1.0);
////
////        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
////        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
////        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
////        ppFeatures.enableFeature(PostProcessorFeature.CLOSES_ON_RETRACT);
////        ppFeatures.enableFeature(PostProcessorFeature.CLOSE_ON_TASK_CHANGE);
////
////        Project testProject = new Project();
////        testProject.getPrinterSettings().setSettingsName("CloseTest");
////        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
////
////        CloseLogic closeLogic = new CloseLogic(testProject,
////                testProject.getPrinterSettings().getSettings("RBX01-SM"),
////                ppFeatures, "RBX01-SM");
////
////        Optional<CloseResult> closeResult = Optional.empty();
////
//////        closeLogic.closeInwardsOntoPerimeter((SectionNode) tool1.getChildren().get(0),
//////                ((ExtrusionNode) tool1.getChildren().get(1).getChildren().get(4)),
//////                testProxy);
////        assertTrue(closeResult.isPresent());
//////        assertTrue(closeResult.get().getClosestNode().isPresent());
//////        assertTrue(closeResult.get().getClosestNode().get() instanceof MovementProvider);
//////        Movement closestMovement = ((MovementProvider) closeResult.get().getClosestNode().get()).getMovement();
////
//////        assertEquals(1, closestMovement.getX(), movementEpsilon);
//////        assertEquals(1, closestMovement.getY(), movementEpsilon);
////        OuterPerimeterSectionNode outerResult = (OuterPerimeterSectionNode) tool1.getChildren().get(1);
////        assertEquals(7, outerResult.getChildren().size());
////
////        assertTrue(outerResult.getChildren().get(5) instanceof ExtrusionNode);
////        ExtrusionNode extrusionResult1 = (ExtrusionNode) outerResult.getChildren().get(5);
////        assertEquals(1, extrusionResult1.getMovement().getX(), movementEpsilon);
////        assertEquals(9, extrusionResult1.getMovement().getY(), movementEpsilon);
////        assertFalse(extrusionResult1.getExtrusion().isEInUse());
////        assertEquals(0.33, extrusionResult1.getNozzlePosition().getB(), nozzleEpsilon);
////
////        assertTrue(outerResult.getChildren().get(6) instanceof ExtrusionNode);
////        ExtrusionNode extrusionResult2 = (ExtrusionNode) outerResult.getChildren().get(6);
////        assertEquals(5, extrusionResult2.getMovement().getX(), movementEpsilon);
////        assertEquals(9, extrusionResult2.getMovement().getY(), movementEpsilon);
////        assertFalse(extrusionResult2.getExtrusion().isEInUse());
////        assertEquals(0, extrusionResult2.getNozzlePosition().getB(), nozzleEpsilon);
////    }
////    @Test
////    public void testCloseUsingSectionTemplate_multipleRetractsInSection()
////    {
////        ToolSelectNode tool1 = setupToolNodeWithInnerAndOuterSquare(true, false);
////
////        //Now add a nozzle open denoting the start of a new subsection
////        NozzlePositionProvider closingNode = (NozzlePositionProvider) tool1.getChildren().get(1).getChildren().get(1);
////        closingNode.getNozzlePosition().setB(0);
////
////        NozzleParameters nozzleParams = new NozzleParameters();
////        nozzleParams.setEjectionVolume(0.15f);
////
////        NozzleProxy testProxy = new NozzleProxy(nozzleParams);
////        testProxy.setCurrentPosition(1.0);
////
////        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
////        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
////        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
////        ppFeatures.enableFeature(PostProcessorFeature.CLOSES_ON_RETRACT);
////        ppFeatures.enableFeature(PostProcessorFeature.CLOSE_ON_TASK_CHANGE);
////
////        Project testProject = new Project();
////        testProject.getPrinterSettings().setSettingsName("BothNozzles");
////        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
////
////        CloseLogic closeLogic = new CloseLogic(testProject, ppFeatures);
////
////        closeLogic.closeInwardsOntoPerimeter((SectionNode) tool1.getChildren().get(0),
////                ((ExtrusionNode) tool1.getChildren().get(1).getChildren().get(4)), testProxy);
////
////        OuterPerimeterSectionNode outerResult = (OuterPerimeterSectionNode) tool1.getChildren().get(1);
////        assertEquals(8, outerResult.getChildren().size());
////
////        assertTrue(outerResult.getChildren().get(5) instanceof TravelNode);
////        TravelNode travelResult1 = (TravelNode) outerResult.getChildren().get(5);
////        assertEquals(1, travelResult1.getMovement().getX(), movementEpsilon);
////        assertEquals(1, travelResult1.getMovement().getY(), movementEpsilon);
////
////        assertTrue(outerResult.getChildren().get(6) instanceof ExtrusionNode);
////        ExtrusionNode extrusionResult1 = (ExtrusionNode) outerResult.getChildren().get(6);
////        assertEquals(1, extrusionResult1.getMovement().getX(), movementEpsilon);
////        assertEquals(9, extrusionResult1.getMovement().getY(), movementEpsilon);
////        assertFalse(extrusionResult1.getExtrusion().isEInUse());
////        assertEquals(0.333, extrusionResult1.getNozzlePosition().getB(), movementEpsilon);
////
////        assertTrue(outerResult.getChildren().get(7) instanceof ExtrusionNode);
////        ExtrusionNode extrusionResult2 = (ExtrusionNode) outerResult.getChildren().get(7);
////        assertEquals(5, extrusionResult2.getMovement().getX(), movementEpsilon);
////        assertEquals(9, extrusionResult2.getMovement().getY(), movementEpsilon);
////        assertFalse(extrusionResult2.getExtrusion().isEInUse());
////        assertEquals(0, extrusionResult2.getNozzlePosition().getB(), movementEpsilon);
////    }
//    /**
//     * Creates a test setup
//     *
//     * @param addInner
//     * @param addFill
//     * @return
//     */
//    private ToolSelectNode setupToolNodeWithInnerAndOuterSquare(boolean addInner,
//            boolean addFill)
//    {
//        ToolSelectNode tool1 = new ToolSelectNode();
//
//        InnerPerimeterSectionNode inner1 = new InnerPerimeterSectionNode();
//        OuterPerimeterSectionNode outer1 = new OuterPerimeterSectionNode();
//        FillSectionNode fill1 = new FillSectionNode();
//
//        TravelNode travel1 = new TravelNode();
//        travel1.getMovement().setX(0);
//        travel1.getMovement().setY(0);
//
//        ExtrusionNode extrusionNode1 = new ExtrusionNode();
//        extrusionNode1.getMovement().setX(10);
//        extrusionNode1.getMovement().setY(0);
//        extrusionNode1.getExtrusion().setE(0.1f);
//        extrusionNode1.getFeedrate().setFeedRate_mmPerMin(10);
//        extrusionNode1.setCommentText("ex1");
//
//        ExtrusionNode extrusionNode2 = new ExtrusionNode();
//        extrusionNode2.getMovement().setX(10);
//        extrusionNode2.getMovement().setY(10);
//        extrusionNode2.getExtrusion().setE(0.1f);
//        extrusionNode2.getFeedrate().setFeedRate_mmPerMin(10);
//        extrusionNode2.setCommentText("ex2");
//
//        ExtrusionNode extrusionNode3 = new ExtrusionNode();
//        extrusionNode3.getMovement().setX(0);
//        extrusionNode3.getMovement().setY(10);
//        extrusionNode3.getExtrusion().setE(0.1f);
//        extrusionNode3.getFeedrate().setFeedRate_mmPerMin(10);
//        extrusionNode3.setCommentText("ex3");
//
//        ExtrusionNode extrusionNode4 = new ExtrusionNode();
//        extrusionNode4.getMovement().setX(0);
//        extrusionNode4.getMovement().setY(0);
//        extrusionNode4.getExtrusion().setE(0.1f);
//        extrusionNode4.getFeedrate().setFeedRate_mmPerMin(10);
//        extrusionNode4.setCommentText("ex4");
//
//        outer1.addChildAtEnd(travel1);
//        outer1.addChildAtEnd(extrusionNode1);
//        outer1.addChildAtEnd(extrusionNode2);
//        outer1.addChildAtEnd(extrusionNode3);
//        outer1.addChildAtEnd(extrusionNode4);
//
//        TravelNode travel2 = new TravelNode();
//        travel2.getMovement().setX(1);
//        travel2.getMovement().setY(1);
//
//        ExtrusionNode extrusionNode5 = new ExtrusionNode();
//        extrusionNode5.getMovement().setX(9);
//        extrusionNode5.getMovement().setY(1);
//        extrusionNode5.getExtrusion().setE(0.1f);
//        extrusionNode5.getFeedrate().setFeedRate_mmPerMin(20);
//        extrusionNode5.setCommentText("ex5");
//
//        ExtrusionNode extrusionNode6 = new ExtrusionNode();
//        extrusionNode6.getMovement().setX(9);
//        extrusionNode6.getMovement().setY(9);
//        extrusionNode6.getExtrusion().setE(0.1f);
//        extrusionNode6.getFeedrate().setFeedRate_mmPerMin(20);
//        extrusionNode6.setCommentText("ex6");
//
//        ExtrusionNode extrusionNode7 = new ExtrusionNode();
//        extrusionNode7.getMovement().setX(1);
//        extrusionNode7.getMovement().setY(9);
//        extrusionNode7.getExtrusion().setE(0.1f);
//        extrusionNode7.getFeedrate().setFeedRate_mmPerMin(20);
//        extrusionNode7.setCommentText("ex7");
//
//        ExtrusionNode extrusionNode8 = new ExtrusionNode();
//        extrusionNode8.getMovement().setX(1);
//        extrusionNode8.getMovement().setY(1);
//        extrusionNode8.getExtrusion().setE(0.1f);
//        extrusionNode8.getFeedrate().setFeedRate_mmPerMin(20);
//        extrusionNode8.setCommentText("ex8");
//
//        inner1.addChildAtEnd(travel2);
//        inner1.addChildAtEnd(extrusionNode5);
//        inner1.addChildAtEnd(extrusionNode6);
//        inner1.addChildAtEnd(extrusionNode7);
//        inner1.addChildAtEnd(extrusionNode8);
//
//        TravelNode travel3 = new TravelNode();
//        travel3.getMovement().setX(2);
//        travel3.getMovement().setY(2);
//
//        ExtrusionNode extrusionNode9 = new ExtrusionNode();
//        extrusionNode9.getMovement().setX(2);
//        extrusionNode9.getMovement().setY(8);
//        extrusionNode9.getExtrusion().setE(0.1f);
//        extrusionNode9.getFeedrate().setFeedRate_mmPerMin(10);
//        extrusionNode9.setCommentText("ex9");
//
//        ExtrusionNode extrusionNode10 = new ExtrusionNode();
//        extrusionNode10.getMovement().setX(5);
//        extrusionNode10.getMovement().setY(8);
//        extrusionNode10.getExtrusion().setE(0.1f);
//        extrusionNode10.getFeedrate().setFeedRate_mmPerMin(10);
//        extrusionNode10.setCommentText("ex10");
//
//        ExtrusionNode extrusionNode11 = new ExtrusionNode();
//        extrusionNode11.getMovement().setX(5);
//        extrusionNode11.getMovement().setY(2);
//        extrusionNode11.getExtrusion().setE(0.1f);
//        extrusionNode11.getFeedrate().setFeedRate_mmPerMin(10);
//        extrusionNode11.setCommentText("ex11");
//
//        ExtrusionNode extrusionNode12 = new ExtrusionNode();
//        extrusionNode12.getMovement().setX(8);
//        extrusionNode12.getMovement().setY(2);
//        extrusionNode12.getExtrusion().setE(0.1f);
//        extrusionNode12.getFeedrate().setFeedRate_mmPerMin(10);
//        extrusionNode12.setCommentText("ex12");
//
//        ExtrusionNode extrusionNode13 = new ExtrusionNode();
//        extrusionNode13.getMovement().setX(8);
//        extrusionNode13.getMovement().setY(8);
//        extrusionNode13.getExtrusion().setE(0.1f);
//        extrusionNode13.getFeedrate().setFeedRate_mmPerMin(10);
//        extrusionNode13.setCommentText("ex13");
//
//        fill1.addChildAtEnd(travel3);
//        fill1.addChildAtEnd(extrusionNode9);
//        fill1.addChildAtEnd(extrusionNode10);
//        fill1.addChildAtEnd(extrusionNode11);
//        fill1.addChildAtEnd(extrusionNode12);
//        fill1.addChildAtEnd(extrusionNode13);
//
//        if (addInner)
//        {
//            tool1.addChildAtEnd(inner1);
//        }
//
//        tool1.addChildAtEnd(outer1);
//
//        if (addFill)
//        {
//            tool1.addChildAtEnd(fill1);
//        }
//
//        return tool1;
//    }
//
//    @Test
//    public void testInsertCloseNodes_withRetracts()
//    {
//        LayerNode testLayer = new LayerNode();
//        testLayer.setLayerNumber(1);
//
//        ToolSelectNode tool1 = new ToolSelectNode();
//        tool1.setToolNumber(0);
//        ToolSelectNode tool2 = new ToolSelectNode();
//        tool2.setToolNumber(1);
//        ToolSelectNode tool3 = new ToolSelectNode();
//        tool3.setToolNumber(0);
//
//        InnerPerimeterSectionNode inner1 = new InnerPerimeterSectionNode();
//        InnerPerimeterSectionNode inner2 = new InnerPerimeterSectionNode();
//        OuterPerimeterSectionNode outer1 = new OuterPerimeterSectionNode();
//        OuterPerimeterSectionNode outer2 = new OuterPerimeterSectionNode();
//        FillSectionNode fill1 = new FillSectionNode();
//        FillSectionNode fill2 = new FillSectionNode();
//
//        tool1.addChildAtEnd(inner1);
//        tool1.addChildAtEnd(outer1);
//
//        tool2.addChildAtEnd(fill1);
//        tool2.addChildAtEnd(fill2);
//
//        tool3.addChildAtEnd(inner2);
//        tool3.addChildAtEnd(outer2);
//
//        ExtrusionNode extrusionNode1 = generateExtrusionNode(1, 1, 1);
//        ExtrusionNode extrusionNode2 = generateExtrusionNode(1, 9, 1);
//        ExtrusionNode extrusionNode3 = generateExtrusionNode(1, 9, 9);
//
//        inner1.addChildAtEnd(extrusionNode1);
//        inner1.addChildAtEnd(extrusionNode2);
//        inner1.addChildAtEnd(extrusionNode3);
//
//        ExtrusionNode extrusionNode4 = generateExtrusionNode(1, 0, 0);
//        ExtrusionNode extrusionNode5 = generateExtrusionNode(1, 10, 0);
//        ExtrusionNode extrusionNode6 = generateExtrusionNode(1, 10, 10);
//
//        outer1.addChildAtEnd(extrusionNode4);
//        outer1.addChildAtEnd(extrusionNode5);
//
//        RetractNode retract1 = new RetractNode();
//        retract1.setPriorExtrusionNode(extrusionNode5);
//        outer1.addChildAtEnd(retract1);
//        outer1.addChildAtEnd(extrusionNode6);
//        ExtrusionNode extrusionNode7 = generateExtrusionNode(1, 0, 0);
//        ExtrusionNode extrusionNode8 = generateExtrusionNode(1, 1, 1);
//        ExtrusionNode extrusionNode9 = generateExtrusionNode(1, 2, 2);
//
//        fill1.addChildAtEnd(extrusionNode7);
//        fill1.addChildAtEnd(extrusionNode8);
//        fill1.addChildAtEnd(extrusionNode9);
//
//        ExtrusionNode extrusionNode10 = generateExtrusionNode(1, 1, 1);
//        ExtrusionNode extrusionNode11 = generateExtrusionNode(1, 9, 1);
//        ExtrusionNode extrusionNode12 = generateExtrusionNode(1, 9, 9);
//
//        inner2.addChildAtEnd(extrusionNode10);
//        inner2.addChildAtEnd(extrusionNode11);
//        inner2.addChildAtEnd(extrusionNode12);
//        RetractNode retract2 = new RetractNode();
//        retract2.setPriorExtrusionNode(extrusionNode12);
//        inner2.addChildAtEnd(retract2);
//
//        ExtrusionNode extrusionNode13 = generateExtrusionNode(1, 0, 0);
//        ExtrusionNode extrusionNode14 = generateExtrusionNode(1, 10, 0);
//        ExtrusionNode extrusionNode15 = generateExtrusionNode(1, 10, 10);
//
//        outer2.addChildAtEnd(extrusionNode13);
//        outer2.addChildAtEnd(extrusionNode14);
//        outer2.addChildAtEnd(extrusionNode15);
//
//        ExtrusionNode extrusionNode16 = generateExtrusionNode(1, 0, 0);
//        ExtrusionNode extrusionNode17 = generateExtrusionNode(1, 1, 1);
//        ExtrusionNode extrusionNode18 = generateExtrusionNode(1, 2, 2);
//
//        fill2.addChildAtEnd(extrusionNode16);
//        RetractNode retract3 = new RetractNode();
//        retract3.setPriorExtrusionNode(extrusionNode16);
//        fill2.addChildAtEnd(retract3);
//        fill2.addChildAtEnd(extrusionNode17);
//        fill2.addChildAtEnd(extrusionNode18);
//        RetractNode retract4 = new RetractNode();
//        retract4.setPriorExtrusionNode(extrusionNode18);
//        fill2.addChildAtEnd(retract4);
//
//        testLayer.addChildAtEnd(tool1);
//        testLayer.addChildAtEnd(tool2);
//        testLayer.addChildAtEnd(tool3);
//
//        // INPUT
//        //
//        //                                layer
//        //                                  |
//        //          ----------------------------------------------------------    
//        //          |                        |                               |
//        //        tool(0)                  tool(1)                         tool(0)
//        //          |                        |                               |
//        //     -----------            ----------------                ---------------
//        //     |         |            |              |                |             |
//        //   inner1    outer1       fill1          fill2            inner2        outer2
//        //     |         |            |              |                |             |
//        //  -------   ----------   -------   ----------------   -------------   ---------
//        //  |  |  |   |  |  |  |   |  |  |   |   |  |   |   |   |   |   |   |   |   |   |
//        //  e1 e2 e3  e4 e5 r1 e6  e7 e8 e9  e10 r1 e11 e12 r3  e13 e14 e15 r4  e16 e17 e18
//        // OUTPUT
//        //
//        //                                                           layer
//        //                                                             |
//        //                    ---------------------------------------------------------------------------------------    
//        //                    |                                        |                                            |
//        //                  tool(0)                                 tool(1)                                      tool(0)
//        //                    |                                        |                                            |
//        //         --------------------                      ----------------------                        ------------------
//        //         |                  |                      |                    |                        |                |
//        //       inner1             outer1                 fill1                fill2                    inner2           outer2
//        //         |                  |                      |                    |                        |                |
//        //    ----------   -----------------------      ----------   --------------------------      ------------------     -------------------
//        //    |  |  |  |   |  |    |    |   |    |      |  |  |  |   |     |     |  |   |     |      |  |   |   |     |     |   |   |   |     |
//        //  open e1 e2 e3  e4 e5 close open e6 close  open e7 e8 e9  e10 close open e11 e12 close  open e13 e14 e15 close  open e16 e17 e18 close
//        HeadFile singleMaterialHead = HeadContainer.getHeadByID("RBX01-SM");
//
//        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
//        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
//        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
//        ppFeatures.enableFeature(PostProcessorFeature.CLOSE_ON_TASK_CHANGE);
//
//        assertEquals(3, testLayer.getChildren().size());
//        assertTrue(testLayer.getChildren().get(0) instanceof ToolSelectNode);
//        assertTrue(testLayer.getChildren().get(1) instanceof ToolSelectNode);
//        assertTrue(testLayer.getChildren().get(2) instanceof ToolSelectNode);
//        assertEquals(2, tool1.getChildren().size());
//        assertEquals(2, tool2.getChildren().size());
//        assertEquals(2, tool2.getChildren().size());
//        assertEquals(3, inner1.getChildren().size());
//        assertEquals(4, outer1.getChildren().size());
//        assertEquals(3, fill1.getChildren().size());
//        assertEquals(4, inner2.getChildren().size());
//        assertEquals(3, outer2.getChildren().size());
//        assertEquals(5, fill2.getChildren().size());
//
//        Project testProject = new Project();
//        testProject.getPrinterSettings().setSettingsName("BothNozzles");
//        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
//
//        List<NozzleProxy> nozzleProxies = new ArrayList<>();
//        for (int nozzleIndex = 0;
//                nozzleIndex < testProject.getPrinterSettings().getSettings("RBX01-SM").getNozzleParameters()
//                .size(); nozzleIndex++)
//        {
//            NozzleProxy proxy = new NozzleProxy(testProject.getPrinterSettings().getSettings("RBX01-SM").getNozzleParameters().get(nozzleIndex));
//            proxy.setNozzleReferenceNumber(nozzleIndex);
//            nozzleProxies.add(proxy);
//        }
//
//        LayerNode emptyLayer = new LayerNode(0);
//
//        LayerPostProcessResult lastLayerParseResult = new LayerPostProcessResult(Optional.empty(), emptyLayer, 0, 0, 0, 0, null, null, -1);
//
//        CloseLogic closeLogic = new CloseLogic(testProject,
//                testProject.getPrinterSettings().getSettings("RBX01-SM"),
//                ppFeatures, "RBX01-SM");
//
//        closeLogic.insertCloseNodes(testLayer, lastLayerParseResult, nozzleProxies);
//
//        OutputUtilities output = new OutputUtilities();
//        output.outputNodes(testLayer, 0);
//
//        assertEquals(3, testLayer.getChildren().size());
//        assertTrue(testLayer.getChildren().get(0) instanceof ToolSelectNode);
//        assertTrue(testLayer.getChildren().get(1) instanceof ToolSelectNode);
//        assertTrue(testLayer.getChildren().get(2) instanceof ToolSelectNode);
//
//        assertEquals(3, inner1.getChildren().size());
//        assertEquals(4, outer1.getChildren().size());
//        assertEquals(3, fill1.getChildren().size());
//        assertEquals(5, fill2.getChildren().size());
//        assertEquals(4, inner2.getChildren().size());
//        assertEquals(3, outer2.getChildren().size());
//
//        assertTrue(outer1.getChildren().get(2) instanceof NozzleValvePositionNode);
//        assertEquals(0.0, ((NozzleValvePositionNode) outer1.getChildren().get(2)).getNozzlePosition().getB(), 0.0001);
//
//        assertTrue(outer1.getChildren().get(2) instanceof NozzleValvePositionNode);
//        assertEquals(0.0, ((NozzleValvePositionNode) outer1.getChildren().get(2)).getNozzlePosition().getB(), 0.0001);
//
//        assertTrue(fill2.getChildren().get(1) instanceof NozzleValvePositionNode);
//        assertEquals(0.0, ((NozzleValvePositionNode) fill2.getChildren().get(1)).getNozzlePosition().getB(), 0.0001);
//
//        assertTrue(fill2.getChildren().get(4) instanceof NozzleValvePositionNode);
//        assertEquals(0.0, ((NozzleValvePositionNode) fill2.getChildren().get(4)).getNozzlePosition().getB(), 0.0001);
//
//        assertTrue(inner2.getChildren().get(3) instanceof NozzleValvePositionNode);
//        assertEquals(0.0, ((NozzleValvePositionNode) inner2.getChildren().get(3)).getNozzlePosition().getB(), 0.0001);
//    }
//
//    //TODO reinstate
////    @Test
////    public void testInsertCloseNodes_shortExtrusionFromOuter()
////    {
////        LayerNode testLayer = new LayerNode();
////        testLayer.setLayerNumber(1);
////
////        ToolSelectNode tool1 = new ToolSelectNode();
////        tool1.setToolNumber(0);
////
////        OuterPerimeterSectionNode outer1 = new OuterPerimeterSectionNode();
////
////        ExtrusionNode extrusionNode1 = new ExtrusionNode();
////        extrusionNode1.getExtrusion().setE(0.01f);
////        ExtrusionNode extrusionNode2 = new ExtrusionNode();
////        extrusionNode2.getExtrusion().setE(0.01f);
////        ExtrusionNode extrusionNode3 = new ExtrusionNode();
////        extrusionNode3.getExtrusion().setE(0.01f);
////        ExtrusionNode extrusionNode4 = new ExtrusionNode();
////        extrusionNode4.getExtrusion().setE(0.01f);
////        ExtrusionNode extrusionNode5 = new ExtrusionNode();
////        extrusionNode5.getExtrusion().setE(0.01f);
////        ExtrusionNode extrusionNode6 = new ExtrusionNode();
////        extrusionNode6.getExtrusion().setE(0.01f);
////        ExtrusionNode extrusionNode7 = new ExtrusionNode();
////        extrusionNode7.getExtrusion().setE(0.01f);
////        ExtrusionNode extrusionNode8 = new ExtrusionNode();
////        extrusionNode8.getExtrusion().setE(0.01f);
////
////        tool1.addChildAtEnd(outer1);
////
////        outer1.addChildAtEnd(extrusionNode1);
////        outer1.addChildAtEnd(extrusionNode2);
////        outer1.addChildAtEnd(extrusionNode3);
////        outer1.addChildAtEnd(extrusionNode4);
////        outer1.addChildAtEnd(extrusionNode5);
////        outer1.addChildAtEnd(extrusionNode6);
////        outer1.addChildAtEnd(extrusionNode7);
////        outer1.addChildAtEnd(extrusionNode8);
////        outer1.recalculateExtrusion();
////        
////        RetractNode retract1 = new RetractNode();
////        retract1.getExtrusion().setE(-5);
////        retract1.setPriorExtrusionNode(extrusionNode8);
////        outer1.addChildAtEnd(retract1);
////        outer1.recalculateExtrusion();
////
////        testLayer.addChildAtEnd(tool1);
////
////        HeadFile singleMaterialHead = HeadContainer.getHeadByID("RBX01-SM");
////
////        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
////        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
////        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
////        ppFeatures.enableFeature(PostProcessorFeature.CLOSES_ON_RETRACT);
////        ppFeatures.enableFeature(PostProcessorFeature.CLOSE_ON_TASK_CHANGE);
////        ppFeatures.enableFeature(PostProcessorFeature.GRADUAL_CLOSE);
////
////        assertEquals(1, testLayer.getChildren().size());
////        assertTrue(testLayer.getChildren().get(0) instanceof ToolSelectNode);
////        assertEquals(1, tool1.getChildren().size());
////        assertEquals(9, outer1.getChildren().size());
////
////        Project testProject = new Project();
////        testProject.getPrinterSettings().setSettingsName("BothNozzles");
////        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
////
////        List<NozzleProxy> nozzleProxies = new ArrayList<>();
////        for (int nozzleIndex = 0;
////                nozzleIndex < testProject.getPrinterSettings().getSettings("RBX01-SM").getNozzleParameters()
////                .size(); nozzleIndex++)
////        {
////            NozzleProxy proxy = new NozzleProxy(testProject.getPrinterSettings().getSettings("RBX01-SM").getNozzleParameters().get(nozzleIndex));
////            proxy.setNozzleReferenceNumber(nozzleIndex);
////            nozzleProxies.add(proxy);
////        }
////
////        LayerNode emptyLayer = new LayerNode(0);
////        LayerPostProcessResult lastLayerParseResult = new LayerPostProcessResult(Optional.empty(), emptyLayer, 0, 0, 0, 0, null, null, -1);
////
////        CloseLogic closeLogic = new CloseLogic(testProject,
////                testProject.getPrinterSettings().getSettings("RBX01-SM"),
////                ppFeatures, "RBX01-SM");
////        
////        NodeManagementUtilities nodeUtils = new NodeManagementUtilities(ppFeatures);
////        nodeUtils.calculatePerRetractExtrusionAndNode(testLayer);
////
////        OutputUtilities output = new OutputUtilities();
////        output.outputNodes(testLayer, 0);
////
////        closeLogic.insertCloseNodes(testLayer, lastLayerParseResult, nozzleProxies);
////
////        output.outputNodes(testLayer, 0);
////
////        assertEquals(1, testLayer.getChildren().size());
////        assertTrue(testLayer.getChildren().get(0) instanceof ToolSelectNode);
////
////        assertEquals(8, outer1.getChildren().size());
////
////        assertTrue(outer1.getChildren().get(1) instanceof ExtrusionNode);
////        assertEquals(0.86, ((ExtrusionNode) outer1.getChildren().get(1)).getNozzlePosition().getB(), nozzleEpsilon);
////
////        assertTrue(outer1.getChildren().get(7) instanceof ExtrusionNode);
////        assertEquals(0.0, ((ExtrusionNode) outer1.getChildren().get(7)).getNozzlePosition().getB(), nozzleEpsilon);
////    }
////    @Test
////    public void testCloseFromEreToEre()
////    {
////        LayerNode testLayer = new LayerNode();
////        testLayer.setLayerNumber(1);
////
////        ToolSelectNode tool1 = new ToolSelectNode();
////        tool1.setToolNumber(0);
////
////        OuterPerimeterSectionNode outer1 = new OuterPerimeterSectionNode();
////
////        ExtrusionNode extrusionNode1 = new ExtrusionNode();
////        extrusionNode1.getExtrusion().setE(0.01f);
////        ExtrusionNode extrusionNode2 = new ExtrusionNode();
////        extrusionNode2.getExtrusion().setE(0.01f);
////        ExtrusionNode extrusionNode3 = new ExtrusionNode();
////        extrusionNode3.getExtrusion().setE(0.01f);
////        ExtrusionNode extrusionNode4 = new ExtrusionNode();
////        extrusionNode4.getExtrusion().setE(0.01f);
////        ExtrusionNode extrusionNode5 = new ExtrusionNode();
////        extrusionNode5.getExtrusion().setE(0.01f);
////        ExtrusionNode extrusionNode6 = new ExtrusionNode();
////        extrusionNode6.getExtrusion().setE(0.01f);
////        ExtrusionNode extrusionNode7 = new ExtrusionNode();
////        extrusionNode7.getExtrusion().setE(0.01f);
////        ExtrusionNode extrusionNode8 = new ExtrusionNode();
////        extrusionNode8.getExtrusion().setE(0.01f);
////
////        RetractNode retract1 = new RetractNode();
////
////        tool1.addChildAtEnd(outer1);
////
////        outer1.addChildAtEnd(extrusionNode1);
////        outer1.addChildAtEnd(extrusionNode2);
////        outer1.addChildAtEnd(extrusionNode3);
////        outer1.addChildAtEnd(extrusionNode4);
////        outer1.addChildAtEnd(extrusionNode5);
////        outer1.addChildAtEnd(extrusionNode6);
////        outer1.addChildAtEnd(extrusionNode7);
////        outer1.addChildAtEnd(extrusionNode8);
////
////        testLayer.addChildAtEnd(tool1);
////
////        HeadFile singleMaterialHead = HeadContainer.getHeadByID("RBX01-SM");
////
////        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
////        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
////        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
////        ppFeatures.enableFeature(PostProcessorFeature.CLOSES_ON_RETRACT);
////        ppFeatures.enableFeature(PostProcessorFeature.CLOSE_ON_TASK_CHANGE);
////        ppFeatures.enableFeature(PostProcessorFeature.GRADUAL_CLOSE);
////
////        assertEquals(1, testLayer.getChildren().size());
////        assertTrue(testLayer.getChildren().get(0) instanceof ToolSelectNode);
////        assertEquals(1, tool1.getChildren().size());
////        assertEquals(8, outer1.getChildren().size());
////
////        Project testProject = new Project();
////        testProject.getPrinterSettings().setSettingsName("BothNozzles");
////        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
////
////        List<NozzleProxy> nozzleProxies = new ArrayList<>();
////        for (int nozzleIndex = 0;
////                nozzleIndex < testProject.getPrinterSettings().getSettings("RBX01-SM").getNozzleParameters()
////                .size(); nozzleIndex++)
////        {
////            NozzleProxy proxy = new NozzleProxy(testProject.getPrinterSettings().getSettings("RBX01-SM").getNozzleParameters().get(nozzleIndex));
////            proxy.setNozzleReferenceNumber(nozzleIndex);
////            nozzleProxies.add(proxy);
////        }
////
////        LayerPostProcessResult lastLayerParseResult = new LayerPostProcessResult(Optional.empty(), testLayer, 0, 0, 0, 0, null, null, -1);
////
////        CloseLogic closeLogic = new CloseLogic(testProject,
////                testProject.getPrinterSettings().getSettings("RBX01-SM"),
////                ppFeatures, "RBX01-SM");
////
////        closeLogic.closeFromEreToEre(extrusionNode4, extrusionNode8, nozzleProxies.get(0), true);
////
////        OutputUtilities output = new OutputUtilities();
////        output.outputNodes(testLayer, 0);
////
////        assertEquals(1, testLayer.getChildren().size());
////        assertTrue(testLayer.getChildren().get(0) instanceof ToolSelectNode);
////
////        assertEquals(8, outer1.getChildren().size());
////
////        assertTrue(outer1.getChildren().get(4) instanceof ExtrusionNode);
////        assertEquals(0.75, ((ExtrusionNode) outer1.getChildren().get(4)).getNozzlePosition().getB(), nozzleEpsilon);
////
////        assertTrue(outer1.getChildren().get(5) instanceof ExtrusionNode);
////        assertEquals(0.5, ((ExtrusionNode) outer1.getChildren().get(5)).getNozzlePosition().getB(), nozzleEpsilon);
////
////        assertTrue(outer1.getChildren().get(6) instanceof ExtrusionNode);
////        assertEquals(0.25, ((ExtrusionNode) outer1.getChildren().get(6)).getNozzlePosition().getB(), nozzleEpsilon);
////
////        assertTrue(outer1.getChildren().get(7) instanceof ExtrusionNode);
////        assertEquals(0.0, ((ExtrusionNode) outer1.getChildren().get(7)).getNozzlePosition().getB(), nozzleEpsilon);
////    }
////    @Test
////    public void testCopyExtrusionEventsForwards()
////    {
////        ToolSelectNode tool1 = setupToolNodeWithInnerAndOuterSquare(true, true);
////
////        HeadFile singleMaterialHead = HeadContainer.getHeadByID("RBX01-SM");
////
////        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
////        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
////        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
////        ppFeatures.enableFeature(PostProcessorFeature.CLOSE_ON_TASK_CHANGE);
////        ppFeatures.enableFeature(PostProcessorFeature.GRADUAL_CLOSE);
////
////        Project testProject = new Project();
////        testProject.getPrinterSettings().setSettingsName("BothNozzles");
////        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
////
////        List<NozzleProxy> nozzleProxies = new ArrayList<>();
////        for (int nozzleIndex = 0;
////                nozzleIndex < testProject.getPrinterSettings().getSettings("RBX01-SM").getNozzleParameters()
////                .size(); nozzleIndex++)
////        {
////            NozzleProxy proxy = new NozzleProxy(testProject.getPrinterSettings().getSettings("RBX01-SM").getNozzleParameters().get(nozzleIndex));
////            proxy.setNozzleReferenceNumber(nozzleIndex);
////            nozzleProxies.add(proxy);
////        }
////
////        CloseLogic closeLogic = new CloseLogic(testProject,
////                testProject.getPrinterSettings().getSettings("RBX01-SM"),
////                ppFeatures, "RBX01-SM");
////
////        List<SectionNode> sections = new ArrayList<>();
////        sections.add((SectionNode) tool1.getChildren().get(0));
////        sections.add((SectionNode) tool1.getChildren().get(1));
////        sections.add((SectionNode) tool1.getChildren().get(2));
////
////        Optional<CloseResult> closeResult = Optional.empty();
////
////        try
////        {
////            closeResult = closeLogic.copyClose(sections, (ExtrusionNode) tool1.getChildren().get(2).getChildren().get(5), nozzleProxies.get(0), false);
////        } catch (NotEnoughAvailableExtrusionException | PostProcessingError ex)
////        {
////            fail();
////        }
////
////        assertTrue(closeResult.isPresent());
////
////        OutputUtilities output = new OutputUtilities();
////        output.outputNodes(tool1, 0);
////
//////        assertEquals(1, testLayer.getChildren().size());
//////        assertTrue(testLayer.getChildren().get(0) instanceof ToolSelectNode);
//////
//////        assertEquals(8, outer1.getChildren().size());
//////        assertTrue(outer1.getChildren().get(4) instanceof ExtrusionNode);
//////        assertEquals(0.75, ((ExtrusionNode) outer1.getChildren().get(4)).getNozzlePosition().getB(), nozzleEpsilon);
//////
//////        assertTrue(outer1.getChildren().get(5) instanceof ExtrusionNode);
//////        assertEquals(0.5, ((ExtrusionNode) outer1.getChildren().get(5)).getNozzlePosition().getB(), nozzleEpsilon);
//////
//////        assertTrue(outer1.getChildren().get(6) instanceof ExtrusionNode);
//////        assertEquals(0.25, ((ExtrusionNode) outer1.getChildren().get(6)).getNozzlePosition().getB(), nozzleEpsilon);
//////
//////        assertTrue(outer1.getChildren().get(7) instanceof ExtrusionNode);
//////        assertEquals(0.0, ((ExtrusionNode) outer1.getChildren().get(7)).getNozzlePosition().getB(), nozzleEpsilon);
////    }

    private ExtrusionNode generateExtrusionNode(float e, double x, double y)
    {
        ExtrusionNode node = new ExtrusionNode();

        node.getExtrusion().setE(e);
        node.getMovement().setX(x);
        node.getMovement().setY(y);

        return node;
    }
}
