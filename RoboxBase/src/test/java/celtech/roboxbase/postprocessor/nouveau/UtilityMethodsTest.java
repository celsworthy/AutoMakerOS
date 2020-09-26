package celtech.roboxbase.postprocessor.nouveau;

import celtech.roboxbase.configuration.RoboxProfile;
import celtech.roboxbase.configuration.SlicerType;
import celtech.roboxbase.configuration.datafileaccessors.RoboxProfileSettingsContainer;
import celtech.roboxbase.configuration.slicer.NozzleParameters;
import celtech.roboxbase.postprocessor.NozzleProxy;
import celtech.roboxbase.postprocessor.nouveau.nodes.ExtrusionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.FillSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.InnerPerimeterSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.LayerNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.OuterPerimeterSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.ToolSelectNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.TravelNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.NozzlePositionProvider;
import celtech.roboxbase.utils.BaseEnvironmentConfiguredTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mock;

/**
 *
 * @author Ian
 */
public class UtilityMethodsTest extends BaseEnvironmentConfiguredTest
{
    
//    @Test
//    public void testInsertOpenNodes()
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
//        ExtrusionNode extrusionNode1 = new ExtrusionNode();
//        extrusionNode1.getExtrusion().setE(1f);
//        extrusionNode1.setCommentText("Ex1");
//
//        ExtrusionNode extrusionNode2 = new ExtrusionNode();
//        extrusionNode2.getExtrusion().setE(1f);
//        extrusionNode2.setCommentText("Ex2");
//
//        ExtrusionNode extrusionNode3 = new ExtrusionNode();
//        extrusionNode3.getExtrusion().setE(1f);
//        extrusionNode3.setCommentText("Ex3");
//
//        ExtrusionNode extrusionNode4 = new ExtrusionNode();
//        extrusionNode4.getExtrusion().setE(1f);
//
//        ExtrusionNode extrusionNode5 = new ExtrusionNode();
//        extrusionNode5.getExtrusion().setE(1f);
//
//        ExtrusionNode extrusionNode6 = new ExtrusionNode();
//        extrusionNode6.getExtrusion().setE(1f);
//
//        ExtrusionNode extrusionNode7 = new ExtrusionNode();
//        extrusionNode7.getExtrusion().setE(1f);
//
//        ExtrusionNode extrusionNode8 = new ExtrusionNode();
//        extrusionNode8.getExtrusion().setE(1f);
//
//        ExtrusionNode extrusionNode9 = new ExtrusionNode();
//        extrusionNode9.getExtrusion().setE(1f);
//
//        ExtrusionNode extrusionNode10 = new ExtrusionNode();
//        extrusionNode10.getExtrusion().setE(1f);
//
//        ExtrusionNode extrusionNode11 = new ExtrusionNode();
//        extrusionNode11.getExtrusion().setE(1f);
//
//        ExtrusionNode extrusionNode12 = new ExtrusionNode();
//        extrusionNode12.getExtrusion().setE(1f);
//
//        ExtrusionNode extrusionNode13 = new ExtrusionNode();
//        extrusionNode13.getExtrusion().setE(1f);
//
//        ExtrusionNode extrusionNode14 = new ExtrusionNode();
//        extrusionNode14.getExtrusion().setE(1f);
//
//        ExtrusionNode extrusionNode15 = new ExtrusionNode();
//        extrusionNode15.getExtrusion().setE(1f);
//
//        ExtrusionNode extrusionNode16 = new ExtrusionNode();
//        extrusionNode16.getExtrusion().setE(1f);
//
//        ExtrusionNode extrusionNode17 = new ExtrusionNode();
//        extrusionNode17.getExtrusion().setE(1f);
//
//        ExtrusionNode extrusionNode18 = new ExtrusionNode();
//        extrusionNode18.getExtrusion().setE(1f);
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
//        inner1.addChildAtEnd(extrusionNode1);
//        inner1.addChildAtEnd(extrusionNode2);
//        inner1.addChildAtEnd(extrusionNode3);
//
//        outer1.addChildAtEnd(extrusionNode4);
//        outer1.addChildAtEnd(extrusionNode5);
//        outer1.addChildAtEnd(extrusionNode6);
//
//        fill1.addChildAtEnd(extrusionNode7);
//        fill1.addChildAtEnd(extrusionNode8);
//        fill1.addChildAtEnd(extrusionNode9);
//
//        inner2.addChildAtEnd(extrusionNode10);
//        inner2.addChildAtEnd(extrusionNode11);
//        inner2.addChildAtEnd(extrusionNode12);
//
//        outer2.addChildAtEnd(extrusionNode13);
//        outer2.addChildAtEnd(extrusionNode14);
//        outer2.addChildAtEnd(extrusionNode15);
//
//        fill2.addChildAtEnd(extrusionNode16);
//        fill2.addChildAtEnd(extrusionNode17);
//        fill2.addChildAtEnd(extrusionNode18);
//
//        testLayer.addChildAtEnd(tool1);
//        testLayer.addChildAtEnd(tool2);
//        testLayer.addChildAtEnd(tool3);
//
//        // INPUT
//        //
//        //                             layer
//        //                               |
//        //          -----------------------------------------------    
//        //          |                    |                        |
//        //        tool(0)              tool(1)                 tool(0)
//        //          |                    |                        |
//        //     -----------         ------------            -------------
//        //     |         |         |          |            |           |
//        //   inner1    outer1    fill1      fill2        inner2       outer2
//        //     |         |         |          |            |            |
//        //  -------   -------   -------   ---------    ---------    ---------
//        //  |  |  |   |  |  |   |  |  |   |   |   |    |   |   |    |   |   |
//        //  e1 e2 e3  e4 e5 e6  e7 e8 e9  e10 e11 e12  e13 e14 e15  e16 e17 e18
//        // OUTPUT
//        //
//        //                                            layer
//        //                                              |
//        //              ------------------------------------------------------    
//        //              |                       |                            |
//        //           tool(0)                  tool(1)                      tool(0)
//        //              |                       |                            |
//        //     ----------------            --------------             -----------------
//        //     |              |            |            |             |               |
//        //   inner1         outer1       fill1        fill2         inner2          outer2
//        //     |              |            |            |             |               |
//        //  ------------   -------   ------------   ---------    -----------    -------------
//        //  |    |  |  |   |  |  |   |    |  |  |   |   |   |    |    |    |    |   |   |   |
//        //  open e1 e2 e3  e4 e5 e6  open e7 e8 e9  e10 e11 e12  open e13 e14  e15  e16 e17 e18
//        //
//        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
//        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
//        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
//        ppFeatures.enableFeature(PostProcessorFeature.CLOSE_ON_TASK_CHANGE);
//        ppFeatures.enableFeature(PostProcessorFeature.GRADUAL_CLOSE);
//
//        assertEquals(3, testLayer.getChildren().size());
//        assertTrue(testLayer.getChildren().get(0) instanceof ToolSelectNode);
//        assertTrue(testLayer.getChildren().get(1) instanceof ToolSelectNode);
//        assertTrue(testLayer.getChildren().get(2) instanceof ToolSelectNode);
//        assertEquals(2, tool1.getChildren().size());
//        assertEquals(2, tool2.getChildren().size());
//        assertEquals(2, tool2.getChildren().size());
//        assertEquals(3, inner1.getChildren().size());
//        assertEquals(3, outer1.getChildren().size());
//        assertEquals(3, fill1.getChildren().size());
//        assertEquals(3, inner2.getChildren().size());
//        assertEquals(3, outer2.getChildren().size());
//        assertEquals(3, fill2.getChildren().size());
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
//        UtilityMethods utilityMethods = new UtilityMethods(ppFeatures, testProject,
//            testProject.getPrinterSettings().getSettings("RBX01-SM"),
//            "RBX01-SM");
//        LayerPostProcessResult lastLayerParseResult = new LayerPostProcessResult(Optional.empty(), testLayer, 0, 0, 0, 10, null, null, -1);
//
//        utilityMethods.insertOpenNodes(testLayer, lastLayerParseResult);
//
//        OutputUtilities output = new OutputUtilities();
//        output.outputNodes(testLayer, 0);
//
//        assertEquals(3, testLayer.getChildren().size());
//        assertTrue(testLayer.getChildren().get(0) instanceof ToolSelectNode);
//        assertTrue(testLayer.getChildren().get(1) instanceof ToolSelectNode);
//        assertTrue(testLayer.getChildren().get(2) instanceof ToolSelectNode);
//
//        assertEquals(4, inner1.getChildren().size());
//        assertEquals(3, outer1.getChildren().size());
//        assertEquals(4, fill1.getChildren().size());
//        assertEquals(3, fill2.getChildren().size());
//        assertEquals(4, inner2.getChildren().size());
//        assertEquals(3, outer2.getChildren().size());
//
//        assertTrue(inner1.getChildren().get(0) instanceof NozzleValvePositionNode);
//        assertEquals(1.0, ((NozzleValvePositionNode) inner1.getChildren().get(0)).getNozzlePosition().getB(), 0.0001);
//
//        assertTrue(fill1.getChildren().get(0) instanceof NozzleValvePositionNode);
//        assertEquals(1.0, ((NozzleValvePositionNode) fill1.getChildren().get(0)).getNozzlePosition().getB(), 0.0001);
//
//        assertTrue(inner2.getChildren().get(0) instanceof NozzleValvePositionNode);
//        assertEquals(1.0, ((NozzleValvePositionNode) inner2.getChildren().get(0)).getNozzlePosition().getB(), 0.0001);
//    }
//    //@Test DISABLED23/09/15
//    public void testInsertCloseNodes()
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
//        ExtrusionNode extrusionNode1 = new ExtrusionNode();
//        extrusionNode1.getExtrusion().setE(1f);
//        extrusionNode1.getMovement().setX(1);
//        extrusionNode1.getMovement().setY(1);
//        extrusionNode1.setCommentText("Ex1");
//
//        ExtrusionNode extrusionNode2 = new ExtrusionNode();
//        extrusionNode2.getExtrusion().setE(1f);
//        extrusionNode2.getMovement().setX(9);
//        extrusionNode2.getMovement().setY(1);
//        extrusionNode2.setCommentText("Ex2");
//
//        ExtrusionNode extrusionNode3 = new ExtrusionNode();
//        extrusionNode3.getExtrusion().setE(1f);
//        extrusionNode3.getMovement().setX(9);
//        extrusionNode3.getMovement().setY(9);
//        extrusionNode3.setCommentText("Ex3");
//
//        ExtrusionNode extrusionNode4 = new ExtrusionNode();
//        extrusionNode4.getExtrusion().setE(1f);
//        extrusionNode4.getMovement().setX(1);
//        extrusionNode4.getMovement().setY(9);
//
//        ExtrusionNode extrusionNode5 = new ExtrusionNode();
//        extrusionNode5.getExtrusion().setE(1f);
//        extrusionNode5.getMovement().setX(0);
//        extrusionNode5.getMovement().setY(0);
//
//        ExtrusionNode extrusionNode6 = new ExtrusionNode();
//        extrusionNode6.getExtrusion().setE(1f);
//        extrusionNode6.getMovement().setX(10);
//        extrusionNode6.getMovement().setY(0);
//
//        ExtrusionNode extrusionNode7 = new ExtrusionNode();
//        extrusionNode7.getExtrusion().setE(1f);
//        extrusionNode7.getMovement().setX(10);
//        extrusionNode7.getMovement().setY(10);
//
//        ExtrusionNode extrusionNode8 = new ExtrusionNode();
//        extrusionNode8.getExtrusion().setE(1f);
//        extrusionNode8.getMovement().setX(0);
//        extrusionNode8.getMovement().setY(10);
//
//        inner1.addChildAtEnd(extrusionNode1);
//        inner1.addChildAtEnd(extrusionNode2);
//        inner1.addChildAtEnd(extrusionNode3);
//        inner1.addChildAtEnd(extrusionNode4);
//
//        outer1.addChildAtEnd(extrusionNode5);
//        outer1.addChildAtEnd(extrusionNode6);
//        outer1.addChildAtEnd(extrusionNode7);
//        outer1.addChildAtEnd(extrusionNode8);
//
//        tool2.addChildAtEnd(fill1);
//        tool2.addChildAtEnd(fill2);
//
//        ExtrusionNode extrusionNode9 = new ExtrusionNode();
//        extrusionNode9.getExtrusion().setE(1f);
//        extrusionNode9.getMovement().setX(0);
//        extrusionNode9.getMovement().setY(0);
//
//        ExtrusionNode extrusionNode10 = new ExtrusionNode();
//        extrusionNode10.getExtrusion().setE(1f);
//        extrusionNode10.getMovement().setX(1);
//        extrusionNode10.getMovement().setY(1);
//
//        ExtrusionNode extrusionNode11 = new ExtrusionNode();
//        extrusionNode11.getExtrusion().setE(1f);
//        extrusionNode11.getMovement().setX(2);
//        extrusionNode11.getMovement().setY(2);
//
//        ExtrusionNode extrusionNode12 = new ExtrusionNode();
//        extrusionNode12.getExtrusion().setE(1f);
//        extrusionNode12.getMovement().setX(1);
//        extrusionNode12.getMovement().setY(1);
//
//        ExtrusionNode extrusionNode13 = new ExtrusionNode();
//        extrusionNode13.getExtrusion().setE(1f);
//        extrusionNode13.getMovement().setX(2);
//        extrusionNode13.getMovement().setY(2);
//
//        ExtrusionNode extrusionNode14 = new ExtrusionNode();
//        extrusionNode14.getExtrusion().setE(1f);
//        extrusionNode14.getMovement().setX(3);
//        extrusionNode14.getMovement().setY(3);
//
//        fill1.addChildAtEnd(extrusionNode9);
//        fill1.addChildAtEnd(extrusionNode10);
//        fill1.addChildAtEnd(extrusionNode11);
//
//        fill2.addChildAtEnd(extrusionNode12);
//        fill2.addChildAtEnd(extrusionNode13);
//        fill2.addChildAtEnd(extrusionNode14);
//
//        tool3.addChildAtEnd(inner2);
//        tool3.addChildAtEnd(outer2);
//
//        ExtrusionNode extrusionNode15 = new ExtrusionNode();
//        extrusionNode15.getExtrusion().setE(1f);
//        extrusionNode15.getMovement().setX(1);
//        extrusionNode15.getMovement().setY(1);
//
//        ExtrusionNode extrusionNode16 = new ExtrusionNode();
//        extrusionNode16.getExtrusion().setE(1f);
//        extrusionNode16.getMovement().setX(9);
//        extrusionNode16.getMovement().setY(1);
//
//        ExtrusionNode extrusionNode17 = new ExtrusionNode();
//        extrusionNode17.getExtrusion().setE(1f);
//        extrusionNode17.getMovement().setX(9);
//        extrusionNode17.getMovement().setY(9);
//
//        ExtrusionNode extrusionNode18 = new ExtrusionNode();
//        extrusionNode18.getExtrusion().setE(1f);
//        extrusionNode18.getMovement().setX(1);
//        extrusionNode18.getMovement().setY(9);
//
//        ExtrusionNode extrusionNode19 = new ExtrusionNode();
//        extrusionNode19.getExtrusion().setE(1f);
//        extrusionNode19.getMovement().setX(0);
//        extrusionNode19.getMovement().setY(0);
//
//        ExtrusionNode extrusionNode20 = new ExtrusionNode();
//        extrusionNode20.getExtrusion().setE(1f);
//        extrusionNode20.getMovement().setX(10);
//        extrusionNode20.getMovement().setY(1);
//
//        ExtrusionNode extrusionNode21 = new ExtrusionNode();
//        extrusionNode21.getExtrusion().setE(1f);
//        extrusionNode21.getMovement().setX(10);
//        extrusionNode21.getMovement().setY(10);
//
//        ExtrusionNode extrusionNode22 = new ExtrusionNode();
//        extrusionNode22.getExtrusion().setE(1f);
//        extrusionNode22.getMovement().setX(1);
//        extrusionNode22.getMovement().setY(10);
//
//        inner2.addChildAtEnd(extrusionNode15);
//        inner2.addChildAtEnd(extrusionNode16);
//        inner2.addChildAtEnd(extrusionNode17);
//        inner2.addChildAtEnd(extrusionNode18);
//
//        outer2.addChildAtEnd(extrusionNode19);
//        outer2.addChildAtEnd(extrusionNode20);
//        outer2.addChildAtEnd(extrusionNode21);
//        outer2.addChildAtEnd(extrusionNode22);
//
//        testLayer.addChildAtEnd(tool1);
//        testLayer.addChildAtEnd(tool2);
//        testLayer.addChildAtEnd(tool3);
//
//        HeadFile singleMaterialHead = HeadContainer.getHeadByID("RBX01-SM");
//
//        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
//        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
//        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
//        ppFeatures.enableFeature(PostProcessorFeature.CLOSE_ON_TASK_CHANGE);
//        ppFeatures.enableFeature(PostProcessorFeature.GRADUAL_CLOSE);
//
//        assertEquals(3, testLayer.getChildren().size());
//        assertTrue(testLayer.getChildren().get(0) instanceof ToolSelectNode);
//        assertTrue(testLayer.getChildren().get(1) instanceof ToolSelectNode);
//        assertTrue(testLayer.getChildren().get(2) instanceof ToolSelectNode);
//        assertEquals(2, tool1.getChildren().size());
//        assertEquals(2, tool2.getChildren().size());
//        assertEquals(2, tool2.getChildren().size());
//        assertEquals(4, inner1.getChildren().size());
//        assertEquals(4, outer1.getChildren().size());
//        assertEquals(3, fill1.getChildren().size());
//        assertEquals(4, inner2.getChildren().size());
//        assertEquals(4, outer2.getChildren().size());
//        assertEquals(3, fill2.getChildren().size());
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
//        UtilityMethods utilityMethods = new UtilityMethods(ppFeatures, testProject,
//            testProject.getPrinterSettings().getSettings("RBX01-SM"),
//            "RBX01-SM");
//        LayerPostProcessResult lastLayerParseResult = new LayerPostProcessResult(Optional.empty(), testLayer, 0, 0, 0, 10, null, null, -1);
//
//        utilityMethods.suppressUnnecessaryToolChangesAndInsertToolchangeOpensAndCloses(testLayer, lastLayerParseResult, nozzleProxies);
//
//        assertEquals(3, testLayer.getChildren().size());
//        assertTrue(testLayer.getChildren().get(0) instanceof ToolSelectNode);
//        assertTrue(testLayer.getChildren().get(1) instanceof ToolSelectNode);
//        assertTrue(testLayer.getChildren().get(2) instanceof ToolSelectNode);
//
//        assertEquals(4, inner1.getChildren().size());
//        assertEquals(6, outer1.getChildren().size());
//        assertEquals(3, fill1.getChildren().size());
//        assertEquals(3, fill2.getChildren().size());
//        assertEquals(4, inner2.getChildren().size());
//        assertEquals(6, outer2.getChildren().size());
//
//        assertTrue(outer1.getChildren().get(5) instanceof NozzlePositionProvider);
//        assertEquals(0.0, ((NozzlePositionProvider) outer1.getChildren().get(5)).getNozzlePosition().getB(), 0.0001);
//
//        assertTrue(fill2.getChildren().get(2) instanceof NozzlePositionProvider);
//        assertEquals(0.0, ((NozzlePositionProvider) fill2.getChildren().get(2)).getNozzlePosition().getB(), 0.0001);
//
//        assertTrue(outer2.getChildren().get(5) instanceof NozzlePositionProvider);
//        assertEquals(0.0, ((NozzlePositionProvider) outer2.getChildren().get(5)).getNozzlePosition().getB(), 0.0001);
//    }
    @Test
    public void testSuppressUnnecessaryToolChangesAndInsertToolchangeCloses()
    {
        LayerNode testLayer = new LayerNode();
        testLayer.setLayerNumber(1);

        ToolSelectNode tool1 = new ToolSelectNode();
        tool1.setToolNumber(0);
        ToolSelectNode tool2 = new ToolSelectNode();
        tool2.setToolNumber(0);
        ToolSelectNode tool3 = new ToolSelectNode();
        tool3.setToolNumber(1);

        InnerPerimeterSectionNode inner1 = new InnerPerimeterSectionNode();
        InnerPerimeterSectionNode inner2 = new InnerPerimeterSectionNode();
        OuterPerimeterSectionNode outer1 = new OuterPerimeterSectionNode();
        OuterPerimeterSectionNode outer2 = new OuterPerimeterSectionNode();
        FillSectionNode fill1 = new FillSectionNode();
        FillSectionNode fill2 = new FillSectionNode();

        tool1.addChildAtEnd(inner1);
        tool1.addChildAtEnd(outer1);

        ExtrusionNode extrusionNode1 = new ExtrusionNode();
        extrusionNode1.getExtrusion().setE(1f);
        extrusionNode1.getMovement().setX(1);
        extrusionNode1.getMovement().setY(1);
        extrusionNode1.setCommentText("Ex1");

        ExtrusionNode extrusionNode2 = new ExtrusionNode();
        extrusionNode2.getExtrusion().setE(1f);
        extrusionNode2.getMovement().setX(9);
        extrusionNode2.getMovement().setY(1);
        extrusionNode2.setCommentText("Ex2");

        ExtrusionNode extrusionNode3 = new ExtrusionNode();
        extrusionNode3.getExtrusion().setE(1f);
        extrusionNode3.getMovement().setX(9);
        extrusionNode3.getMovement().setY(9);
        extrusionNode3.setCommentText("Ex3");

        ExtrusionNode extrusionNode4 = new ExtrusionNode();
        extrusionNode4.getExtrusion().setE(1f);
        extrusionNode4.getMovement().setX(1);
        extrusionNode4.getMovement().setY(9);

        ExtrusionNode extrusionNode5 = new ExtrusionNode();
        extrusionNode5.getExtrusion().setE(1f);
        extrusionNode5.getMovement().setX(0);
        extrusionNode5.getMovement().setY(0);

        ExtrusionNode extrusionNode6 = new ExtrusionNode();
        extrusionNode6.getExtrusion().setE(1f);
        extrusionNode6.getMovement().setX(10);
        extrusionNode6.getMovement().setY(0);

        ExtrusionNode extrusionNode7 = new ExtrusionNode();
        extrusionNode7.getExtrusion().setE(1f);
        extrusionNode7.getMovement().setX(10);
        extrusionNode7.getMovement().setY(10);

        ExtrusionNode extrusionNode8 = new ExtrusionNode();
        extrusionNode8.getExtrusion().setE(1f);
        extrusionNode8.getMovement().setX(0);
        extrusionNode8.getMovement().setY(10);

        inner1.addChildAtEnd(extrusionNode1);
        inner1.addChildAtEnd(extrusionNode2);
        inner1.addChildAtEnd(extrusionNode3);
        inner1.addChildAtEnd(extrusionNode4);

        outer1.addChildAtEnd(extrusionNode5);
        outer1.addChildAtEnd(extrusionNode6);
        outer1.addChildAtEnd(extrusionNode7);
        outer1.addChildAtEnd(extrusionNode8);

        tool2.addChildAtEnd(fill1);
        tool2.addChildAtEnd(fill2);

        ExtrusionNode extrusionNode9 = new ExtrusionNode();
        extrusionNode9.getExtrusion().setE(1f);
        extrusionNode9.getMovement().setX(0);
        extrusionNode9.getMovement().setY(0);

        ExtrusionNode extrusionNode10 = new ExtrusionNode();
        extrusionNode10.getExtrusion().setE(1f);
        extrusionNode10.getMovement().setX(1);
        extrusionNode10.getMovement().setY(1);

        ExtrusionNode extrusionNode11 = new ExtrusionNode();
        extrusionNode11.getExtrusion().setE(1f);
        extrusionNode11.getMovement().setX(2);
        extrusionNode11.getMovement().setY(2);

        ExtrusionNode extrusionNode12 = new ExtrusionNode();
        extrusionNode12.getExtrusion().setE(1f);
        extrusionNode12.getMovement().setX(1);
        extrusionNode12.getMovement().setY(1);

        ExtrusionNode extrusionNode13 = new ExtrusionNode();
        extrusionNode13.getExtrusion().setE(1f);
        extrusionNode13.getMovement().setX(2);
        extrusionNode13.getMovement().setY(2);

        ExtrusionNode extrusionNode14 = new ExtrusionNode();
        extrusionNode14.getExtrusion().setE(1f);
        extrusionNode14.getMovement().setX(3);
        extrusionNode14.getMovement().setY(3);

        fill1.addChildAtEnd(extrusionNode9);
        fill1.addChildAtEnd(extrusionNode10);
        fill1.addChildAtEnd(extrusionNode11);

        fill2.addChildAtEnd(extrusionNode12);
        fill2.addChildAtEnd(extrusionNode13);
        fill2.addChildAtEnd(extrusionNode14);

        tool3.addChildAtEnd(inner2);
        tool3.addChildAtEnd(outer2);

        ExtrusionNode extrusionNode15 = new ExtrusionNode();
        extrusionNode15.getExtrusion().setE(1f);
        extrusionNode15.getMovement().setX(1);
        extrusionNode15.getMovement().setY(1);

        ExtrusionNode extrusionNode16 = new ExtrusionNode();
        extrusionNode16.getExtrusion().setE(1f);
        extrusionNode16.getMovement().setX(9);
        extrusionNode16.getMovement().setY(1);

        ExtrusionNode extrusionNode17 = new ExtrusionNode();
        extrusionNode17.getExtrusion().setE(1f);
        extrusionNode17.getMovement().setX(9);
        extrusionNode17.getMovement().setY(9);

        ExtrusionNode extrusionNode18 = new ExtrusionNode();
        extrusionNode18.getExtrusion().setE(1f);
        extrusionNode18.getMovement().setX(1);
        extrusionNode18.getMovement().setY(9);

        ExtrusionNode extrusionNode19 = new ExtrusionNode();
        extrusionNode19.getExtrusion().setE(1f);
        extrusionNode19.getMovement().setX(0);
        extrusionNode19.getMovement().setY(0);

        ExtrusionNode extrusionNode20 = new ExtrusionNode();
        extrusionNode20.getExtrusion().setE(1f);
        extrusionNode20.getMovement().setX(10);
        extrusionNode20.getMovement().setY(1);

        ExtrusionNode extrusionNode21 = new ExtrusionNode();
        extrusionNode21.getExtrusion().setE(1f);
        extrusionNode21.getMovement().setX(10);
        extrusionNode21.getMovement().setY(10);

        ExtrusionNode extrusionNode22 = new ExtrusionNode();
        extrusionNode22.getExtrusion().setE(1f);
        extrusionNode22.getMovement().setX(1);
        extrusionNode22.getMovement().setY(10);

        inner2.addChildAtEnd(extrusionNode15);
        inner2.addChildAtEnd(extrusionNode16);
        inner2.addChildAtEnd(extrusionNode17);
        inner2.addChildAtEnd(extrusionNode18);

        outer2.addChildAtEnd(extrusionNode19);
        outer2.addChildAtEnd(extrusionNode20);
        outer2.addChildAtEnd(extrusionNode21);
        outer2.addChildAtEnd(extrusionNode22);

        testLayer.addChildAtEnd(tool1);
        testLayer.addChildAtEnd(tool2);
        testLayer.addChildAtEnd(tool3);

        NozzleParameters nozzleParams = new NozzleParameters();
        nozzleParams.setEjectionVolume(1f);

        Optional<NozzleProxy> testProxy = Optional.of(new NozzleProxy(nozzleParams));
        testProxy.get().setCurrentPosition(1.0);

        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
        ppFeatures.enableFeature(PostProcessorFeature.OPEN_AND_CLOSE_NOZZLES);

        Optional<RoboxProfile> optionalRoboxProfile = RoboxProfileSettingsContainer.getInstance().getRoboxProfileWithName("Draft", SlicerType.Cura, "RBX01-DM");
        RoboxProfile roboxProfile = optionalRoboxProfile.get();
        
        List<NozzleProxy> nozzleProxies = new ArrayList<>();
        for (int nozzleIndex = 0;
                nozzleIndex < roboxProfile.getNozzleParameters()
                .size(); nozzleIndex++)
        {
            NozzleProxy proxy = new NozzleProxy(roboxProfile.getNozzleParameters().get(nozzleIndex));
            proxy.setNozzleReferenceNumber(nozzleIndex);
            nozzleProxies.add(proxy);
        }

        NodeManagementUtilities nmu = new NodeManagementUtilities(ppFeatures, nozzleProxies);

        UtilityMethods utilityMethods = new UtilityMethods(ppFeatures,
                roboxProfile,
                "RBX01-DM",
                nmu,
                null
        );
        LayerPostProcessResult lastLayerParseResult = new LayerPostProcessResult(testLayer, 0, null, null, null, -1, 0);

        OutputUtilities opUtils = new OutputUtilities();

        opUtils.outputNodes(testLayer, 0);
        utilityMethods.suppressUnnecessaryToolChangesAndInsertToolchangeCloses(testLayer, lastLayerParseResult, nozzleProxies);

        opUtils.outputNodes(testLayer, 0);

        assertEquals(3, testLayer.getChildren().size());
        assertFalse(tool1.isNodeOutputSuppressed());
        assertTrue(tool2.isNodeOutputSuppressed());
        assertFalse(tool3.isNodeOutputSuppressed());

        assertTrue(tool2.getAbsolutelyTheLastEvent() instanceof NozzlePositionProvider);
        assertTrue(((NozzlePositionProvider) tool2.getAbsolutelyTheLastEvent()).getNozzlePosition().isBSet());
    }

//    @Test
//    public void testInsertNozzleOpenFullyBeforeEvent_noReplenish()
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
//        assertEquals(3, testLayer.getChildren().size());
//        assertEquals(3, outer.getChildren().size());
//
//        Project testProject = new Project();
//        testProject.getPrinterSettings().setSettingsName("BothNozzles");
//        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
//
//        UtilityMethods utilityMethods = new UtilityMethods(ppFeatures, testProject, 
//            testProject.getPrinterSettings().getSettings("RBX01-SM"), 
//            "RBX01-SM");
//        utilityMethods.insertNozzleOpenFullyBeforeEvent(extrusionNode1);
//
//        assertEquals(3, testLayer.getChildren().size());
//        assertEquals(4, outer.getChildren().size());
//        assertTrue(outer.getChildren().get(0) instanceof NozzleValvePositionNode);
//        assertEquals(1.0, ((NozzleValvePositionNode) outer.getChildren().get(0)).getNozzlePosition().getB(), 0.0001);
//    }
//    @Test
//    public void testInsertNozzleOpenFullyBeforeEvent_noReplenishRequired()
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
//        ppFeatures.enableFeature(PostProcessorFeature.REPLENISH_BEFORE_OPEN);
//
//        assertEquals(3, testLayer.getChildren().size());
//        assertEquals(3, outer.getChildren().size());
//
//        Project testProject = new Project();
//        testProject.getPrinterSettings().setSettingsName("BothNozzles");
//        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
//
//        UtilityMethods utilityMethods = new UtilityMethods(ppFeatures, testProject,
//            testProject.getPrinterSettings().getSettings("RBX01-SM"),
//            "RBX01-SM");
//        utilityMethods.insertNozzleOpenFullyBeforeEvent(extrusionNode1);
//
//        assertEquals(3, testLayer.getChildren().size());
//        assertEquals(4, outer.getChildren().size());
//        assertTrue(outer.getChildren().get(0) instanceof NozzleValvePositionNode);
//        assertEquals(1.0, ((NozzleValvePositionNode) outer.getChildren().get(0)).getNozzlePosition().getB(), 0.0001);
//    }
//    @Test
//    public void testInsertNozzleOpenFullyBeforeEvent_withReplenish()
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
//        ppFeatures.enableFeature(PostProcessorFeature.REPLENISH_BEFORE_OPEN);
//
//        assertEquals(3, testLayer.getChildren().size());
//        assertEquals(3, outer.getChildren().size());
//
//        Project testProject = new Project();
//        testProject.getPrinterSettings().setSettingsName("BothNozzles");
//        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
//
//        UtilityMethods utilityMethods = new UtilityMethods(ppFeatures, testProject,
//            testProject.getPrinterSettings().getSettings("RBX01-SM"),
//            "RBX01-SM");
//        extrusionNode1.setElidedExtrusion(0.4);
//        utilityMethods.insertNozzleOpenFullyBeforeEvent(extrusionNode1);
//        OutputUtilities output = new OutputUtilities();
//        output.outputNodes(testLayer, 0);
//
//        assertEquals(3, testLayer.getChildren().size());
//        assertEquals(5, outer.getChildren().size());
////        assertTrue(outer.getChildren().get(0) instanceof ReplenishNode);
////        assertTrue(outer.getChildren().get(1) instanceof NozzleValvePositionNode);
////        assertEquals(0.4, ((ReplenishNode) outer.getChildren().get(0)).getExtrusion().getE(), 0.0001);
//        assertEquals(1.0, ((NozzleValvePositionNode) outer.getChildren().get(1)).getNozzlePosition().getB(), 0.0001);
//    }
    private ToolSelectNode setupToolNodeWithInnerAndOuterSquare(boolean addInner,
            boolean addFill)
    {
        ToolSelectNode tool1 = new ToolSelectNode();

        InnerPerimeterSectionNode inner1 = new InnerPerimeterSectionNode();
        OuterPerimeterSectionNode outer1 = new OuterPerimeterSectionNode();
        FillSectionNode fill1 = new FillSectionNode();

        TravelNode travel1 = new TravelNode();
        travel1.getMovement().setX(0);
        travel1.getMovement().setY(0);

        ExtrusionNode extrusionNode1 = new ExtrusionNode();
        extrusionNode1.getMovement().setX(10);
        extrusionNode1.getMovement().setY(0);
        extrusionNode1.getExtrusion().setE(0.1f);
        extrusionNode1.getFeedrate().setFeedRate_mmPerMin(10);
        extrusionNode1.setCommentText("ex1");

        ExtrusionNode extrusionNode2 = new ExtrusionNode();
        extrusionNode2.getMovement().setX(10);
        extrusionNode2.getMovement().setY(10);
        extrusionNode2.getExtrusion().setE(0.1f);
        extrusionNode2.getFeedrate().setFeedRate_mmPerMin(10);
        extrusionNode2.setCommentText("ex2");

        ExtrusionNode extrusionNode3 = new ExtrusionNode();
        extrusionNode3.getMovement().setX(0);
        extrusionNode3.getMovement().setY(10);
        extrusionNode3.getExtrusion().setE(0.1f);
        extrusionNode3.getFeedrate().setFeedRate_mmPerMin(10);
        extrusionNode3.setCommentText("ex3");

        ExtrusionNode extrusionNode4 = new ExtrusionNode();
        extrusionNode4.getMovement().setX(0);
        extrusionNode4.getMovement().setY(0);
        extrusionNode4.getExtrusion().setE(0.1f);
        extrusionNode4.getFeedrate().setFeedRate_mmPerMin(10);
        extrusionNode4.setCommentText("ex4");

        outer1.addChildAtEnd(travel1);
        outer1.addChildAtEnd(extrusionNode1);
        outer1.addChildAtEnd(extrusionNode2);
        outer1.addChildAtEnd(extrusionNode3);
        outer1.addChildAtEnd(extrusionNode4);

        TravelNode travel2 = new TravelNode();
        travel2.getMovement().setX(1);
        travel2.getMovement().setY(1);

        ExtrusionNode extrusionNode5 = new ExtrusionNode();
        extrusionNode5.getMovement().setX(9);
        extrusionNode5.getMovement().setY(1);
        extrusionNode5.getExtrusion().setE(0.1f);
        extrusionNode5.getFeedrate().setFeedRate_mmPerMin(20);
        extrusionNode5.setCommentText("ex5");

        ExtrusionNode extrusionNode6 = new ExtrusionNode();
        extrusionNode6.getMovement().setX(9);
        extrusionNode6.getMovement().setY(9);
        extrusionNode6.getExtrusion().setE(0.1f);
        extrusionNode6.getFeedrate().setFeedRate_mmPerMin(20);
        extrusionNode6.setCommentText("ex6");

        ExtrusionNode extrusionNode7 = new ExtrusionNode();
        extrusionNode7.getMovement().setX(1);
        extrusionNode7.getMovement().setY(9);
        extrusionNode7.getExtrusion().setE(0.1f);
        extrusionNode7.getFeedrate().setFeedRate_mmPerMin(20);
        extrusionNode7.setCommentText("ex7");

        ExtrusionNode extrusionNode8 = new ExtrusionNode();
        extrusionNode8.getMovement().setX(1);
        extrusionNode8.getMovement().setY(1);
        extrusionNode8.getExtrusion().setE(0.1f);
        extrusionNode8.getFeedrate().setFeedRate_mmPerMin(20);
        extrusionNode8.setCommentText("ex8");

        inner1.addChildAtEnd(travel2);
        inner1.addChildAtEnd(extrusionNode5);
        inner1.addChildAtEnd(extrusionNode6);
        inner1.addChildAtEnd(extrusionNode7);
        inner1.addChildAtEnd(extrusionNode8);

        TravelNode travel3 = new TravelNode();
        travel3.getMovement().setX(2);
        travel3.getMovement().setY(2);

        ExtrusionNode extrusionNode9 = new ExtrusionNode();
        extrusionNode9.getMovement().setX(2);
        extrusionNode9.getMovement().setY(8);
        extrusionNode9.getExtrusion().setE(0.1f);
        extrusionNode9.getFeedrate().setFeedRate_mmPerMin(10);
        extrusionNode9.setCommentText("ex9");

        ExtrusionNode extrusionNode10 = new ExtrusionNode();
        extrusionNode10.getMovement().setX(5);
        extrusionNode10.getMovement().setY(8);
        extrusionNode10.getExtrusion().setE(0.1f);
        extrusionNode10.getFeedrate().setFeedRate_mmPerMin(10);
        extrusionNode10.setCommentText("ex10");

        ExtrusionNode extrusionNode11 = new ExtrusionNode();
        extrusionNode11.getMovement().setX(5);
        extrusionNode11.getMovement().setY(2);
        extrusionNode11.getExtrusion().setE(0.1f);
        extrusionNode11.getFeedrate().setFeedRate_mmPerMin(10);
        extrusionNode11.setCommentText("ex11");

        ExtrusionNode extrusionNode12 = new ExtrusionNode();
        extrusionNode12.getMovement().setX(8);
        extrusionNode12.getMovement().setY(2);
        extrusionNode12.getExtrusion().setE(0.1f);
        extrusionNode12.getFeedrate().setFeedRate_mmPerMin(10);
        extrusionNode12.setCommentText("ex12");

        ExtrusionNode extrusionNode13 = new ExtrusionNode();
        extrusionNode13.getMovement().setX(8);
        extrusionNode13.getMovement().setY(8);
        extrusionNode13.getExtrusion().setE(0.1f);
        extrusionNode13.getFeedrate().setFeedRate_mmPerMin(10);
        extrusionNode13.setCommentText("ex13");

        fill1.addChildAtEnd(travel3);
        fill1.addChildAtEnd(extrusionNode9);
        fill1.addChildAtEnd(extrusionNode10);
        fill1.addChildAtEnd(extrusionNode11);
        fill1.addChildAtEnd(extrusionNode12);
        fill1.addChildAtEnd(extrusionNode13);

        if (addInner)
        {
            tool1.addChildAtEnd(inner1);
        }

        tool1.addChildAtEnd(outer1);

        if (addFill)
        {
            tool1.addChildAtEnd(fill1);
        }

        return tool1;
    }
}
