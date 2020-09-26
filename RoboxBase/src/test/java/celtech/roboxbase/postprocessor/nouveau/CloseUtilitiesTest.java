package celtech.roboxbase.postprocessor.nouveau;

import celtech.roboxbase.postprocessor.nouveau.nodes.TravelNode;
import celtech.roboxbase.utils.BaseEnvironmentConfiguredTest;

/**
 *
 * @author Ian
 */
public class CloseUtilitiesTest extends BaseEnvironmentConfiguredTest
{

//    @Test
//    public void testFindClosestMovementNode()
//    {
//        ToolSelectNode tool1 = setupToolNodeWithInnerAndOuterSquare();
//
//        NozzleParameters nozzleParams = new NozzleParameters();
//        nozzleParams.setEjectionVolume(0.003f);
//
//        Project testProject = new Project();
//        testProject.getPrinterSettings().setSettingsName("BothNozzles");
//        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
//
//        CloseUtilities closeUtilities = new CloseUtilities(testProject,
//                testProject.getPrinterSettings().getSettings("RBX01-SM"),
//                "RBX01-SM");
//
//        List<SectionNode> sectionsToConsider = new ArrayList<>();
//        sectionsToConsider.add((SectionNode) tool1.getChildren().get(0));
//
//        List<GCodeEventNode> events = new ArrayList<>();
//        for (GCodeEventNode sectionNode : tool1.getChildren())
//        {
//            if (sectionNode instanceof SectionNode)
//            {
//                for (GCodeEventNode sectionChild : sectionNode.getChildren())
//                {
//                    //Make sure the list is reversed
//                    events.add(0, sectionChild);
//                }
//            }
//        }
//
//        Optional<IntersectionResult> result = Optional.empty();
//        
//        try
//        {
//            result = closeUtilities.findClosestMovementNode(events, true);
//        } catch (DidntFindEventException ex)
//        {
//            fail();
//        }
//        OutputUtilities output = new OutputUtilities();
//        output.outputNodes(tool1, 0);
//        assertTrue(result.isPresent());
//        assertSame(tool1.getChildren().get(0).getChildren().get(4), result.get().getClosestNode());
//        assertEquals(1, result.get().getIntersectionPoint().getX(), 0.01);
//        assertEquals(2.5, result.get().getIntersectionPoint().getY(), 0.01);
//    }
//
////    @Test
////    public void testFindClosestMovementNode_multiSection()
////    {
////        ToolSelectNode tool1 = setupToolNodeWithInnerAndOuterSquare();
////
////        NozzleParameters nozzleParams = new NozzleParameters();
////        nozzleParams.setEjectionVolume(0.003f);
////
////        Project testProject = new Project();
////        testProject.getPrinterSettings().setSettingsName("BothNozzles");
////        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
////
////        CloseUtilities closeUtilities = new CloseUtilities(testProject,
////                testProject.getPrinterSettings().getSettings("RBX01-SM"),
////                "RBX01-SM");
////
////        FillSectionNode fill1 = new FillSectionNode();
////        ExtrusionNode node1 = new ExtrusionNode();
////        node1.getMovement().setX(100);
////        node1.getMovement().setY(100);
////        node1.getExtrusion().setE(3);
////        tool1.addChildAtEnd(fill1);
////
////        List<SectionNode> sectionsToConsider = new ArrayList<>();
////        sectionsToConsider.add((SectionNode) tool1.getChildren().get(0));
////        sectionsToConsider.add(fill1);
////
////        Optional<IntersectionResult> result = closeUtilities.findClosestMovementNode(((ExtrusionNode) tool1.getChildren().get(1).getChildren().get(4)),
////                sectionsToConsider,
////                sectionsToConsider,
////                true);
////        OutputUtilities output = new OutputUtilities();
////        output.outputNodes(tool1, 0);
////        assertTrue(result.isPresent());
////        assertSame(tool1.getChildren().get(0).getChildren().get(4), result.get().getClosestNode());
////        assertEquals(1, result.get().getIntersectionPoint().getX(), 0.01);
////        assertEquals(2.5, result.get().getIntersectionPoint().getY(), 0.01);
////    }
//
////    @Test
////    public void testCloseScenario1()
////    {
//////        ;TYPE:WALL-INNER
////        InnerPerimeterSectionNode inner1 = new InnerPerimeterSectionNode();
//////G1 F1500 X116.514 Y69.371 E0.09407
////        inner1.addChildAtEnd(generateExtrusionNode(0.09407f, 116.514, 69.371));
//////G1 X119.125 Y69.371 E0.09145
////        inner1.addChildAtEnd(generateExtrusionNode(0.09145f, 119.125, 69.371));
//////G1 X119.125 Y72.057 E0.09407
////        inner1.addChildAtEnd(generateExtrusionNode(0.09407f, 119.125, 72.057));
//////G1 X116.514 Y72.057 E0.09145
////        inner1.addChildAtEnd(generateExtrusionNode(0.09145f, 116.514, 72.057));
//////G0 F12000 X116.164 Y72.407
////        inner1.addChildAtEnd(generateTravelNode(116.164, 72.407));
////
//////G1 F1500 X116.164 Y69.021 E0.11859
////        inner1.addChildAtEnd(generateExtrusionNode(0.11859f, 116.164, 69.021));
////
//////G1 X119.475 Y69.021 E0.11596
////        inner1.addChildAtEnd(generateExtrusionNode(0.11596f, 119.475, 69.021));
//////G1 X119.475 Y72.407 E0.11859
////        inner1.addChildAtEnd(generateExtrusionNode(0.11859f, 119.475, 72.407));
//////G1 X116.164 Y72.407 E0.11596
////        inner1.addChildAtEnd(generateExtrusionNode(0.11596f, 116.164, 72.407));
//////G0 F12000 X115.814 Y72.757
////        inner1.addChildAtEnd(generateTravelNode(115.814, 72.757));
////        inner1.recalculateExtrusion();
//////;TYPE:WALL-OUTER
////        OuterPerimeterSectionNode outer1 = new OuterPerimeterSectionNode();
//////G1 F1500 X115.814 Y68.671 E0.14311
////        outer1.addChildAtEnd(generateExtrusionNode(0.14311f, 115.814, 68.671));
//////G1 X119.825 Y68.671 E0.14048
////        outer1.addChildAtEnd(generateExtrusionNode(0.14048f, 119.825, 68.671));
//////G1 X119.825 Y72.757 E0.14311
////        outer1.addChildAtEnd(generateExtrusionNode(0.14311f, 119.825, 72.757));
//////G1 X115.814 Y72.757 E0.14048
////        outer1.addChildAtEnd(generateExtrusionNode(0.14048f, 115.814, 72.757));
//////G0 F12000 X115.999 Y72.482
////        outer1.addChildAtEnd(generateTravelNode(115.999, 72.482));
//////G0 X117.561 Y71.896
////        outer1.addChildAtEnd(generateTravelNode(117.561, 71.896));
////        outer1.recalculateExtrusion();
//////;TYPE:FILL
////        FillSectionNode fill1 = new FillSectionNode();
//////G1 F1500 X118.964 Y70.493 E0.07942
////        fill1.addChildAtEnd(generateExtrusionNode(0.07942f, 118.964, 70.493));
//////G0 F12000 X118.042 Y69.530
////        fill1.addChildAtEnd(generateTravelNode(118.042, 69.530));
//////G1 F1500 X116.673 Y70.899 E0.07749
////        ExtrusionNode extrusionToCloseFrom = generateExtrusionNode(0.07749f, 116.673, 70.899);
////        fill1.addChildAtEnd(extrusionToCloseFrom);
//////G1 F1800 E-0.50000
////        fill1.addChildAtEnd(new RetractNode());
//////G0 F12000 X116.514 Y63.485
////        fill1.addChildAtEnd(generateTravelNode(116.514, 63.485));
////        fill1.recalculateExtrusion();
////
////        NozzleParameters nozzleParams = new NozzleParameters();
////        nozzleParams.setEjectionVolume(0.003f);
////
////        Project testProject = new Project();
////        testProject.getPrinterSettings().setSettingsName("Fine");
////        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
////
////        SlicerParametersFile settings = SlicerParametersContainer.getSettings("Fine", "RBX01-DM");
////
////        CloseUtilities closeUtilities = new CloseUtilities(testProject,
////                settings,
////                "RBX01-DM");
////
////        List<SectionNode> sectionsToConsider = new ArrayList<>();
////        sectionsToConsider.add(inner1);
////        sectionsToConsider.add(outer1);
////        sectionsToConsider.add(fill1);
////
////        Optional<IntersectionResult> result = closeUtilities.findClosestMovementNode(extrusionToCloseFrom,
////                sectionsToConsider,
////                sectionsToConsider,
////                false);
////
////        OutputUtilities output = new OutputUtilities();
////        assertTrue(result.isPresent());
////        assertEquals(1, result.get().getIntersectionPoint().getX(), 0.01);
////        assertEquals(2.5, result.get().getIntersectionPoint().getY(), 0.01);
////    }
//
//    private ToolSelectNode setupToolNodeWithInnerAndOuterSquare()
//    {
//        ToolSelectNode tool1 = new ToolSelectNode();
//
//        OuterPerimeterSectionNode outer1 = new OuterPerimeterSectionNode();
//        InnerPerimeterSectionNode inner1 = new InnerPerimeterSectionNode();
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
//
//        ExtrusionNode extrusionNode2 = new ExtrusionNode();
//        extrusionNode2.getMovement().setX(10);
//        extrusionNode2.getMovement().setY(10);
//        extrusionNode2.getExtrusion().setE(0.1f);
//        extrusionNode2.getFeedrate().setFeedRate_mmPerMin(10);
//
//        ExtrusionNode extrusionNode3 = new ExtrusionNode();
//        extrusionNode3.getMovement().setX(0);
//        extrusionNode3.getMovement().setY(10);
//        extrusionNode3.getExtrusion().setE(0.1f);
//        extrusionNode3.getFeedrate().setFeedRate_mmPerMin(10);
//
//        ExtrusionNode extrusionNode4 = new ExtrusionNode();
//        extrusionNode4.getMovement().setX(0);
//        extrusionNode4.getMovement().setY(0);
//        extrusionNode4.getExtrusion().setE(0.1f);
//        extrusionNode4.getFeedrate().setFeedRate_mmPerMin(10);
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
//
//        ExtrusionNode extrusionNode6 = new ExtrusionNode();
//        extrusionNode6.getMovement().setX(9);
//        extrusionNode6.getMovement().setY(9);
//        extrusionNode6.getExtrusion().setE(0.1f);
//        extrusionNode6.getFeedrate().setFeedRate_mmPerMin(20);
//
//        ExtrusionNode extrusionNode7 = new ExtrusionNode();
//        extrusionNode7.getMovement().setX(1);
//        extrusionNode7.getMovement().setY(9);
//        extrusionNode7.getExtrusion().setE(0.1f);
//        extrusionNode7.getFeedrate().setFeedRate_mmPerMin(20);
//
//        ExtrusionNode extrusionNode8 = new ExtrusionNode();
//        extrusionNode8.getMovement().setX(1);
//        extrusionNode8.getMovement().setY(1);
//        extrusionNode8.getExtrusion().setE(0.1f);
//        extrusionNode8.getFeedrate().setFeedRate_mmPerMin(20);
//
//        inner1.addChildAtEnd(travel2);
//        inner1.addChildAtEnd(extrusionNode5);
//        inner1.addChildAtEnd(extrusionNode6);
//        inner1.addChildAtEnd(extrusionNode7);
//        inner1.addChildAtEnd(extrusionNode8);
//
//        tool1.addChildAtEnd(inner1);
//        tool1.addChildAtEnd(outer1);
//
//        return tool1;
//    }
//
//    private ExtrusionNode generateExtrusionNode(float e, double x, double y)
//    {
//        ExtrusionNode node = new ExtrusionNode();
//
//        node.getExtrusion().setE(e);
//        node.getMovement().setX(x);
//        node.getMovement().setY(y);
//
//        return node;
//    }

    private TravelNode generateTravelNode(double x, double y)
    {
        TravelNode node = new TravelNode();

        node.getMovement().setX(x);
        node.getMovement().setY(y);

        return node;
    }
}
