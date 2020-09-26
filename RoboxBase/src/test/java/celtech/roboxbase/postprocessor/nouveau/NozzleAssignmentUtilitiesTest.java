package celtech.roboxbase.postprocessor.nouveau;

import celtech.roboxbase.configuration.RoboxProfile;
import celtech.roboxbase.configuration.SlicerType;
import celtech.roboxbase.configuration.datafileaccessors.HeadContainer;
import celtech.roboxbase.configuration.datafileaccessors.RoboxProfileSettingsContainer;
import celtech.roboxbase.configuration.fileRepresentation.HeadFile;
import celtech.roboxbase.postprocessor.NozzleProxy;
import celtech.roboxbase.postprocessor.nouveau.nodes.ExtrusionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.FillSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.InnerPerimeterSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.LayerNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.ObjectDelineationNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.OuterPerimeterSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.SupportSectionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.ToolSelectNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.ExtrusionProvider;
import celtech.roboxbase.utils.BaseEnvironmentConfiguredTest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Ian
 */
public class NozzleAssignmentUtilitiesTest extends BaseEnvironmentConfiguredTest
{

    @Test
    public void testAssignExtrusionToCorrectExtruder()
    {
        LayerNode testLayer = new LayerNode();
        testLayer.setLayerNumber(1);

        ToolSelectNode tool1 = new ToolSelectNode();
        tool1.setToolNumber(0);
        ToolSelectNode tool2 = new ToolSelectNode();
        tool2.setToolNumber(1);
        ToolSelectNode tool3 = new ToolSelectNode();
        tool3.setToolNumber(0);

        InnerPerimeterSectionNode inner1 = new InnerPerimeterSectionNode();
        InnerPerimeterSectionNode inner2 = new InnerPerimeterSectionNode();
        OuterPerimeterSectionNode outer1 = new OuterPerimeterSectionNode();
        OuterPerimeterSectionNode outer2 = new OuterPerimeterSectionNode();
        FillSectionNode fill1 = new FillSectionNode();
        FillSectionNode fill2 = new FillSectionNode();

        ExtrusionNode extrusionNode1 = new ExtrusionNode();
        extrusionNode1.getExtrusion().setE(1f);
        ExtrusionNode extrusionNode2 = new ExtrusionNode();
        extrusionNode2.getExtrusion().setE(1f);
        ExtrusionNode extrusionNode3 = new ExtrusionNode();
        extrusionNode3.getExtrusion().setE(1f);
        ExtrusionNode extrusionNode4 = new ExtrusionNode();
        extrusionNode4.getExtrusion().setE(1f);
        ExtrusionNode extrusionNode5 = new ExtrusionNode();
        extrusionNode5.getExtrusion().setE(1f);
        ExtrusionNode extrusionNode6 = new ExtrusionNode();
        extrusionNode6.getExtrusion().setE(1f);
        ExtrusionNode extrusionNode7 = new ExtrusionNode();
        extrusionNode7.getExtrusion().setE(1f);
        ExtrusionNode extrusionNode8 = new ExtrusionNode();
        extrusionNode8.getExtrusion().setE(1f);
        ExtrusionNode extrusionNode9 = new ExtrusionNode();
        extrusionNode9.getExtrusion().setE(1f);
        ExtrusionNode extrusionNode10 = new ExtrusionNode();
        extrusionNode10.getExtrusion().setE(1f);
        ExtrusionNode extrusionNode11 = new ExtrusionNode();
        extrusionNode11.getExtrusion().setE(1f);
        ExtrusionNode extrusionNode12 = new ExtrusionNode();
        extrusionNode12.getExtrusion().setE(1f);
        ExtrusionNode extrusionNode13 = new ExtrusionNode();
        extrusionNode13.getExtrusion().setE(1f);
        ExtrusionNode extrusionNode14 = new ExtrusionNode();
        extrusionNode14.getExtrusion().setE(1f);
        ExtrusionNode extrusionNode15 = new ExtrusionNode();
        extrusionNode15.getExtrusion().setE(1f);
        ExtrusionNode extrusionNode16 = new ExtrusionNode();
        extrusionNode16.getExtrusion().setE(1f);
        ExtrusionNode extrusionNode17 = new ExtrusionNode();
        extrusionNode17.getExtrusion().setE(1f);
        ExtrusionNode extrusionNode18 = new ExtrusionNode();
        extrusionNode18.getExtrusion().setE(1f);

        tool1.addChildAtEnd(inner1);
        tool1.addChildAtEnd(outer1);

        tool2.addChildAtEnd(fill1);
        tool2.addChildAtEnd(fill2);

        tool3.addChildAtEnd(inner2);
        tool3.addChildAtEnd(outer2);

        inner1.addChildAtEnd(extrusionNode1);
        inner1.addChildAtEnd(extrusionNode2);
        inner1.addChildAtEnd(extrusionNode3);

        outer1.addChildAtEnd(extrusionNode4);
        outer1.addChildAtEnd(extrusionNode5);
        outer1.addChildAtEnd(extrusionNode6);

        fill1.addChildAtEnd(extrusionNode7);
        fill1.addChildAtEnd(extrusionNode8);
        fill1.addChildAtEnd(extrusionNode9);

        inner2.addChildAtEnd(extrusionNode10);
        inner2.addChildAtEnd(extrusionNode11);
        inner2.addChildAtEnd(extrusionNode12);

        outer2.addChildAtEnd(extrusionNode13);
        outer2.addChildAtEnd(extrusionNode14);
        outer2.addChildAtEnd(extrusionNode15);

        fill2.addChildAtEnd(extrusionNode16);
        fill2.addChildAtEnd(extrusionNode17);
        fill2.addChildAtEnd(extrusionNode18);

        testLayer.addChildAtEnd(tool1);
        testLayer.addChildAtEnd(tool2);
        testLayer.addChildAtEnd(tool3);

        HeadFile singleMaterialHead = HeadContainer.getHeadByID("RBX01-DM");

        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
        ppFeatures.enableFeature(PostProcessorFeature.OPEN_AND_CLOSE_NOZZLES);

        Optional<RoboxProfile> optionalRoboxProfile = RoboxProfileSettingsContainer.getInstance().getRoboxProfileWithName("Draft", SlicerType.Cura, "RBX01-SM");
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

        Map<Integer, Integer> objectToNozzleNumberMap = new HashMap();

        NozzleAssignmentUtilities assignmentUtilities = new NozzleAssignmentUtilities(
                nozzleProxies,
                roboxProfile,
                singleMaterialHead,
                ppFeatures,
                PostProcessingMode.SUPPORT_IN_FIRST_MATERIAL,
                objectToNozzleNumberMap);

        assertEquals(3, testLayer.getChildren().size());

        assignmentUtilities.assignExtrusionToCorrectExtruder(testLayer);

        assertEquals(3, testLayer.getChildren().size());

        assertFalse(((ExtrusionProvider) (outer2.getChildren().get(0))).getExtrusion().isEInUse());
        assertTrue(((ExtrusionProvider) (outer2.getChildren().get(0))).getExtrusion().isDInUse());

        assertTrue(((ExtrusionProvider) (fill1.getChildren().get(0))).getExtrusion().isEInUse());
        assertFalse(((ExtrusionProvider) (fill1.getChildren().get(0))).getExtrusion().isDInUse());

        assertFalse(((ExtrusionProvider) (outer1.getChildren().get(0))).getExtrusion().isEInUse());
        assertTrue(((ExtrusionProvider) (outer1.getChildren().get(0))).getExtrusion().isDInUse());
    }

    @Test
    public void testInsertNozzleControlSectionsByObject()
    {
        LayerNode testLayer = new LayerNode();
        testLayer.setLayerNumber(1);

        ObjectDelineationNode object1 = new ObjectDelineationNode();
        object1.setObjectNumber(0);
        ObjectDelineationNode object2 = new ObjectDelineationNode();
        object2.setObjectNumber(1);

        InnerPerimeterSectionNode inner1 = new InnerPerimeterSectionNode();
        InnerPerimeterSectionNode inner2 = new InnerPerimeterSectionNode();
        OuterPerimeterSectionNode outer1 = new OuterPerimeterSectionNode();
        OuterPerimeterSectionNode outer2 = new OuterPerimeterSectionNode();
        FillSectionNode fill1 = new FillSectionNode();
        FillSectionNode fill2 = new FillSectionNode();

        ExtrusionNode extrusionNode1 = new ExtrusionNode();
        ExtrusionNode extrusionNode2 = new ExtrusionNode();
        ExtrusionNode extrusionNode3 = new ExtrusionNode();
        ExtrusionNode extrusionNode4 = new ExtrusionNode();
        ExtrusionNode extrusionNode5 = new ExtrusionNode();
        ExtrusionNode extrusionNode6 = new ExtrusionNode();
        ExtrusionNode extrusionNode7 = new ExtrusionNode();
        ExtrusionNode extrusionNode8 = new ExtrusionNode();
        ExtrusionNode extrusionNode9 = new ExtrusionNode();
        ExtrusionNode extrusionNode10 = new ExtrusionNode();
        ExtrusionNode extrusionNode11 = new ExtrusionNode();
        ExtrusionNode extrusionNode12 = new ExtrusionNode();
        ExtrusionNode extrusionNode13 = new ExtrusionNode();
        ExtrusionNode extrusionNode14 = new ExtrusionNode();
        ExtrusionNode extrusionNode15 = new ExtrusionNode();
        ExtrusionNode extrusionNode16 = new ExtrusionNode();
        ExtrusionNode extrusionNode17 = new ExtrusionNode();
        ExtrusionNode extrusionNode18 = new ExtrusionNode();

        object1.addChildAtEnd(inner1);
        object1.addChildAtEnd(outer1);
        object1.addChildAtEnd(fill1);

        object2.addChildAtEnd(fill2);
        object2.addChildAtEnd(inner2);
        object2.addChildAtEnd(outer2);

        inner1.addChildAtEnd(extrusionNode1);
        inner1.addChildAtEnd(extrusionNode2);
        inner1.addChildAtEnd(extrusionNode3);

        outer1.addChildAtEnd(extrusionNode4);
        outer1.addChildAtEnd(extrusionNode5);
        outer1.addChildAtEnd(extrusionNode6);

        fill1.addChildAtEnd(extrusionNode7);
        fill1.addChildAtEnd(extrusionNode8);
        fill1.addChildAtEnd(extrusionNode9);

        inner2.addChildAtEnd(extrusionNode10);
        inner2.addChildAtEnd(extrusionNode11);
        inner2.addChildAtEnd(extrusionNode12);

        outer2.addChildAtEnd(extrusionNode13);
        outer2.addChildAtEnd(extrusionNode14);
        outer2.addChildAtEnd(extrusionNode15);

        fill2.addChildAtEnd(extrusionNode16);
        fill2.addChildAtEnd(extrusionNode17);
        fill2.addChildAtEnd(extrusionNode18);

        testLayer.addChildAtEnd(object1);
        testLayer.addChildAtEnd(object2);

        // INPUT
        //                             layer
        //                               |
        //             -------------------------------------
        //             |                                   |
        //           object1                            object2
        //             |                                   |
        //     ---------------------         ----------------------------
        //     |         |         |         |             |            |
        //   inner1    outer1    fill1     fill2         inner2       outer2
        //     |         |         |         |             |            |
        //  -------   -------   -------   ---------    ---------    ---------
        //  |  |  |   |  |  |   |  |  |   |   |   |    |   |   |    |   |   |
        //  e1 e2 e3  e4 e5 e6  e7 e8 e9  e10 e11 e12  e13 e14 e15  e16 e17 e18
        // OUTPUT for object-based tool selection - support in object material
        //
        //                             layer
        //                               |
        //               ------------------------------------
        //               |                                  |
        //             tool(0)                            tool(1)
        //               |                                  |
        //     ---------------------          ----------------------------
        //     |         |         |          |             |            |
        //   inner     outer      fill      fill          inner        outer
        //     |         |         |          |             |            |
        //  -------   -------   -------   ---------    ---------    ---------
        //  |  |  |   |  |  |   |  |  |   |   |   |    |   |   |    |   |   |
        //  e1 e2 e3  e4 e5 e6  e7 e8 e9  e10 e11 e12  e13 e14 e15  e16 e17 e18
        HeadFile dualMaterialHead = HeadContainer.getHeadByID("RBX01-DM");

        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
        ppFeatures.enableFeature(PostProcessorFeature.OPEN_AND_CLOSE_NOZZLES);

        Optional<RoboxProfile> optionalRoboxProfile = RoboxProfileSettingsContainer.getInstance().getRoboxProfileWithName("Draft", SlicerType.Cura, "RBX01-SM");
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

        LayerPostProcessResult lastLayerParseResult = new LayerPostProcessResult(testLayer, 0, null, null, null, -1, 0);

        Map<Integer, Integer> objectToNozzleNumberMap = new HashMap();
        objectToNozzleNumberMap.put(0, 0);
        objectToNozzleNumberMap.put(1, 1);

        NozzleAssignmentUtilities assignmentUtilities = new NozzleAssignmentUtilities(
                nozzleProxies,
                roboxProfile,
                dualMaterialHead,
                ppFeatures,
                PostProcessingMode.SUPPORT_IN_FIRST_MATERIAL,
                objectToNozzleNumberMap);

        assertEquals(2, testLayer.getChildren().size());
        assertEquals(3, object1.getChildren().size());
        assertEquals(3, object2.getChildren().size());
        assertEquals(3, inner1.getChildren().size());
        assertEquals(3, outer1.getChildren().size());
        assertEquals(3, fill1.getChildren().size());
        assertEquals(3, inner2.getChildren().size());
        assertEquals(3, outer2.getChildren().size());
        assertEquals(3, fill2.getChildren().size());

        int lastObjectNumber = assignmentUtilities.insertNozzleControlSectionsByObject(testLayer, lastLayerParseResult);

        assertEquals(1, lastObjectNumber);
        assertEquals(2, testLayer.getChildren().size());
        assertTrue(testLayer.getChildren().get(0) instanceof ToolSelectNode);
        assertTrue(testLayer.getChildren().get(1) instanceof ToolSelectNode);

        ToolSelectNode tool1 = (ToolSelectNode) testLayer.getChildren().get(0);
        ToolSelectNode tool2 = (ToolSelectNode) testLayer.getChildren().get(1);

        assertEquals(3, tool1.getChildren().size());
        assertEquals(3, tool2.getChildren().size());

        assertSame(inner1, tool1.getChildren().get(0));
        assertSame(outer1, tool1.getChildren().get(1));
        assertSame(fill1, tool1.getChildren().get(2));

        assertSame(fill2, tool2.getChildren().get(0));
        assertSame(inner2, tool2.getChildren().get(1));
        assertSame(outer2, tool2.getChildren().get(2));
    }

    @Test
    public void testInsertNozzleControlSectionsByObject_SupportMaterial2()
    {
        LayerNode testLayer = new LayerNode();
        testLayer.setLayerNumber(1);

        ObjectDelineationNode object1 = new ObjectDelineationNode();
        object1.setObjectNumber(0);
        ObjectDelineationNode object2 = new ObjectDelineationNode();
        object2.setObjectNumber(1);
        ObjectDelineationNode object3 = new ObjectDelineationNode();
        object3.setObjectNumber(0);

        InnerPerimeterSectionNode inner1 = new InnerPerimeterSectionNode();
        InnerPerimeterSectionNode inner2 = new InnerPerimeterSectionNode();
        InnerPerimeterSectionNode inner3 = new InnerPerimeterSectionNode();
        OuterPerimeterSectionNode outer1 = new OuterPerimeterSectionNode();
        OuterPerimeterSectionNode outer2 = new OuterPerimeterSectionNode();
        OuterPerimeterSectionNode outer3 = new OuterPerimeterSectionNode();
        SupportSectionNode support1 = new SupportSectionNode();
        SupportSectionNode support2 = new SupportSectionNode();
        SupportSectionNode support3 = new SupportSectionNode();

        ExtrusionNode extrusionNode1 = new ExtrusionNode();
        ExtrusionNode extrusionNode2 = new ExtrusionNode();
        ExtrusionNode extrusionNode3 = new ExtrusionNode();
        ExtrusionNode extrusionNode4 = new ExtrusionNode();
        ExtrusionNode extrusionNode5 = new ExtrusionNode();
        ExtrusionNode extrusionNode6 = new ExtrusionNode();
        ExtrusionNode extrusionNode7 = new ExtrusionNode();
        ExtrusionNode extrusionNode8 = new ExtrusionNode();
        ExtrusionNode extrusionNode9 = new ExtrusionNode();
        ExtrusionNode extrusionNode10 = new ExtrusionNode();
        ExtrusionNode extrusionNode11 = new ExtrusionNode();
        ExtrusionNode extrusionNode12 = new ExtrusionNode();
        ExtrusionNode extrusionNode13 = new ExtrusionNode();
        ExtrusionNode extrusionNode14 = new ExtrusionNode();
        ExtrusionNode extrusionNode15 = new ExtrusionNode();
        ExtrusionNode extrusionNode16 = new ExtrusionNode();
        ExtrusionNode extrusionNode17 = new ExtrusionNode();
        ExtrusionNode extrusionNode18 = new ExtrusionNode();

        object1.addChildAtEnd(inner1);
        object1.addChildAtEnd(outer1);
        object1.addChildAtEnd(support1);

        object2.addChildAtEnd(inner2);
        object2.addChildAtEnd(outer2);
        object2.addChildAtEnd(support2);

        object3.addChildAtEnd(inner3);
        object3.addChildAtEnd(outer3);
        object3.addChildAtEnd(support3);

        inner1.addChildAtEnd(extrusionNode1);
        inner1.addChildAtEnd(extrusionNode2);
        inner1.addChildAtEnd(extrusionNode3);

        outer1.addChildAtEnd(extrusionNode4);
        outer1.addChildAtEnd(extrusionNode5);
        outer1.addChildAtEnd(extrusionNode6);

        support1.addChildAtEnd(extrusionNode7);
        support1.addChildAtEnd(extrusionNode8);
        support1.addChildAtEnd(extrusionNode9);

        inner2.addChildAtEnd(extrusionNode10);
        inner2.addChildAtEnd(extrusionNode11);
        inner2.addChildAtEnd(extrusionNode12);

        outer2.addChildAtEnd(extrusionNode13);
        outer2.addChildAtEnd(extrusionNode14);
        outer2.addChildAtEnd(extrusionNode15);

        support2.addChildAtEnd(extrusionNode16);
        support2.addChildAtEnd(extrusionNode17);
        support2.addChildAtEnd(extrusionNode18);

        testLayer.addChildAtEnd(object1);
        testLayer.addChildAtEnd(object2);
        testLayer.addChildAtEnd(object3);

        HeadFile dualMaterialHead = HeadContainer.getHeadByID("RBX01-DM");

        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
        ppFeatures.enableFeature(PostProcessorFeature.OPEN_AND_CLOSE_NOZZLES);

        Optional<RoboxProfile> optionalRoboxProfile = RoboxProfileSettingsContainer.getInstance().getRoboxProfileWithName("Draft", SlicerType.Cura, "RBX01-SM");
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

        LayerPostProcessResult lastLayerParseResult = new LayerPostProcessResult(testLayer, 0, null, null, null, -1, 0);

        Map<Integer, Integer> objectToNozzleNumberMap = new HashMap();
        objectToNozzleNumberMap.put(0, 0);
        objectToNozzleNumberMap.put(1, 1);
        objectToNozzleNumberMap.put(2, 0);

        NozzleAssignmentUtilities assignmentUtilities = new NozzleAssignmentUtilities(
                nozzleProxies,
                roboxProfile,
                dualMaterialHead,
                ppFeatures,
                PostProcessingMode.SUPPORT_IN_FIRST_MATERIAL,
                objectToNozzleNumberMap);

        assertEquals(3, testLayer.getChildren().size());
        assertEquals(3, object1.getChildren().size());
        assertEquals(3, object2.getChildren().size());
        assertEquals(3, inner1.getChildren().size());
        assertEquals(3, outer1.getChildren().size());
        assertEquals(3, support1.getChildren().size());
        assertEquals(3, inner2.getChildren().size());
        assertEquals(3, outer2.getChildren().size());
        assertEquals(3, support2.getChildren().size());

        int lastObjectNumber = assignmentUtilities.insertNozzleControlSectionsByObject(testLayer, lastLayerParseResult);

        assertEquals(0, lastObjectNumber);
        assertEquals(4, testLayer.getChildren().size());
        assertTrue(testLayer.getChildren().get(0) instanceof ToolSelectNode);
        assertTrue(testLayer.getChildren().get(1) instanceof ToolSelectNode);
        assertTrue(testLayer.getChildren().get(2) instanceof ToolSelectNode);
        assertTrue(testLayer.getChildren().get(3) instanceof ToolSelectNode);

        ToolSelectNode tool1 = (ToolSelectNode) testLayer.getChildren().get(0);
        ToolSelectNode tool2 = (ToolSelectNode) testLayer.getChildren().get(1);
        ToolSelectNode tool3 = (ToolSelectNode) testLayer.getChildren().get(2);
        ToolSelectNode tool4 = (ToolSelectNode) testLayer.getChildren().get(3);

        assertEquals(2, tool1.getChildren().size());
        assertEquals(4, tool2.getChildren().size());
        assertEquals(2, tool3.getChildren().size());
        assertEquals(1, tool4.getChildren().size());

        assertSame(inner1, tool1.getChildren().get(0));
        assertSame(outer1, tool1.getChildren().get(1));

        assertSame(support1, tool2.getChildren().get(0));
        assertSame(inner2, tool2.getChildren().get(1));
        assertSame(outer2, tool2.getChildren().get(2));
        assertSame(support2, tool2.getChildren().get(3));

        assertSame(inner3, tool3.getChildren().get(0));
        assertSame(outer3, tool3.getChildren().get(1));

        assertSame(support3, tool4.getChildren().get(0));
    }

    @Test
    public void testInsertNozzleControlSectionsByObject_SupportMaterial1()
    {
        LayerNode testLayer = new LayerNode();
        testLayer.setLayerNumber(1);

        ObjectDelineationNode object1 = new ObjectDelineationNode();
        object1.setObjectNumber(0);
        ObjectDelineationNode object2 = new ObjectDelineationNode();
        object2.setObjectNumber(1);
        ObjectDelineationNode object3 = new ObjectDelineationNode();
        object3.setObjectNumber(0);

        InnerPerimeterSectionNode inner1 = new InnerPerimeterSectionNode();
        InnerPerimeterSectionNode inner2 = new InnerPerimeterSectionNode();
        InnerPerimeterSectionNode inner3 = new InnerPerimeterSectionNode();
        OuterPerimeterSectionNode outer1 = new OuterPerimeterSectionNode();
        OuterPerimeterSectionNode outer2 = new OuterPerimeterSectionNode();
        OuterPerimeterSectionNode outer3 = new OuterPerimeterSectionNode();
        SupportSectionNode support1 = new SupportSectionNode();
        SupportSectionNode support2 = new SupportSectionNode();
        SupportSectionNode support3 = new SupportSectionNode();

        ExtrusionNode extrusionNode1 = new ExtrusionNode();
        ExtrusionNode extrusionNode2 = new ExtrusionNode();
        ExtrusionNode extrusionNode3 = new ExtrusionNode();
        ExtrusionNode extrusionNode4 = new ExtrusionNode();
        ExtrusionNode extrusionNode5 = new ExtrusionNode();
        ExtrusionNode extrusionNode6 = new ExtrusionNode();
        ExtrusionNode extrusionNode7 = new ExtrusionNode();
        ExtrusionNode extrusionNode8 = new ExtrusionNode();
        ExtrusionNode extrusionNode9 = new ExtrusionNode();
        ExtrusionNode extrusionNode10 = new ExtrusionNode();
        ExtrusionNode extrusionNode11 = new ExtrusionNode();
        ExtrusionNode extrusionNode12 = new ExtrusionNode();
        ExtrusionNode extrusionNode13 = new ExtrusionNode();
        ExtrusionNode extrusionNode14 = new ExtrusionNode();
        ExtrusionNode extrusionNode15 = new ExtrusionNode();
        ExtrusionNode extrusionNode16 = new ExtrusionNode();
        ExtrusionNode extrusionNode17 = new ExtrusionNode();
        ExtrusionNode extrusionNode18 = new ExtrusionNode();

        object1.addChildAtEnd(inner1);
        object1.addChildAtEnd(outer1);
        object1.addChildAtEnd(support1);

        object2.addChildAtEnd(inner2);
        object2.addChildAtEnd(outer2);
        object2.addChildAtEnd(support2);

        object3.addChildAtEnd(inner3);
        object3.addChildAtEnd(outer3);
        object3.addChildAtEnd(support3);

        inner1.addChildAtEnd(extrusionNode1);
        inner1.addChildAtEnd(extrusionNode2);
        inner1.addChildAtEnd(extrusionNode3);

        outer1.addChildAtEnd(extrusionNode4);
        outer1.addChildAtEnd(extrusionNode5);
        outer1.addChildAtEnd(extrusionNode6);

        support1.addChildAtEnd(extrusionNode7);
        support1.addChildAtEnd(extrusionNode8);
        support1.addChildAtEnd(extrusionNode9);

        inner2.addChildAtEnd(extrusionNode10);
        inner2.addChildAtEnd(extrusionNode11);
        inner2.addChildAtEnd(extrusionNode12);

        outer2.addChildAtEnd(extrusionNode13);
        outer2.addChildAtEnd(extrusionNode14);
        outer2.addChildAtEnd(extrusionNode15);

        support2.addChildAtEnd(extrusionNode16);
        support2.addChildAtEnd(extrusionNode17);
        support2.addChildAtEnd(extrusionNode18);

        testLayer.addChildAtEnd(object1);
        testLayer.addChildAtEnd(object2);
        testLayer.addChildAtEnd(object3);

        HeadFile dualMaterialHead = HeadContainer.getHeadByID("RBX01-DM");

        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
        ppFeatures.enableFeature(PostProcessorFeature.OPEN_AND_CLOSE_NOZZLES);

        Optional<RoboxProfile> optionalRoboxProfile = RoboxProfileSettingsContainer.getInstance().getRoboxProfileWithName("Draft", SlicerType.Cura, "RBX01-SM");
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

        LayerPostProcessResult lastLayerParseResult = new LayerPostProcessResult(testLayer, 0, null, null, null, -1, 0);

        Map<Integer, Integer> objectToNozzleNumberMap = new HashMap();
        objectToNozzleNumberMap.put(0, 1);
        objectToNozzleNumberMap.put(1, 0);
        objectToNozzleNumberMap.put(2, 1);

        NozzleAssignmentUtilities assignmentUtilities = new NozzleAssignmentUtilities(
                nozzleProxies,
                roboxProfile,
                dualMaterialHead,
                ppFeatures,
                PostProcessingMode.SUPPORT_IN_FIRST_MATERIAL,
                objectToNozzleNumberMap);

        assertEquals(3, testLayer.getChildren().size());
        assertEquals(3, object1.getChildren().size());
        assertEquals(3, object2.getChildren().size());
        assertEquals(3, inner1.getChildren().size());
        assertEquals(3, outer1.getChildren().size());
        assertEquals(3, support1.getChildren().size());
        assertEquals(3, inner2.getChildren().size());
        assertEquals(3, outer2.getChildren().size());
        assertEquals(3, support2.getChildren().size());

        int lastObjectNumber = assignmentUtilities.insertNozzleControlSectionsByObject(testLayer, lastLayerParseResult);

        assertEquals(0, lastObjectNumber);
        assertEquals(3, testLayer.getChildren().size());
        assertTrue(testLayer.getChildren().get(0) instanceof ToolSelectNode);
        assertTrue(testLayer.getChildren().get(1) instanceof ToolSelectNode);
        assertTrue(testLayer.getChildren().get(2) instanceof ToolSelectNode);

        ToolSelectNode tool1 = (ToolSelectNode) testLayer.getChildren().get(0);
        ToolSelectNode tool2 = (ToolSelectNode) testLayer.getChildren().get(1);
        ToolSelectNode tool3 = (ToolSelectNode) testLayer.getChildren().get(2);

        assertEquals(3, tool1.getChildren().size());
        assertEquals(2, tool2.getChildren().size());
        assertEquals(4, tool3.getChildren().size());

        assertSame(inner1, tool1.getChildren().get(0));
        assertSame(outer1, tool1.getChildren().get(1));
        assertSame(support1, tool1.getChildren().get(2));

        assertSame(inner2, tool2.getChildren().get(0));
        assertSame(outer2, tool2.getChildren().get(1));

        assertSame(support2, tool3.getChildren().get(0));
        assertSame(inner3, tool3.getChildren().get(1));
        assertSame(outer3, tool3.getChildren().get(2));
        assertSame(support3, tool3.getChildren().get(3));
    }

//    @Test
//    public void testInsertNozzleControlSectionsByTask()
//    {
//        LayerNode testLayer = new LayerNode();
//        testLayer.setLayerNumber(1);
//
//        ObjectDelineationNode object1 = new ObjectDelineationNode();
//        object1.setObjectNumber(11);
//        ObjectDelineationNode object2 = new ObjectDelineationNode();
//        object2.setObjectNumber(22);
//
//        InnerPerimeterSectionNode inner1 = new InnerPerimeterSectionNode();
//        InnerPerimeterSectionNode inner2 = new InnerPerimeterSectionNode();
//        OuterPerimeterSectionNode outer1 = new OuterPerimeterSectionNode();
//        OuterPerimeterSectionNode outer2 = new OuterPerimeterSectionNode();
//        FillSectionNode fill1 = new FillSectionNode();
//        FillSectionNode fill2 = new FillSectionNode();
//
//        ExtrusionNode extrusionNode1 = new ExtrusionNode();
//        ExtrusionNode extrusionNode2 = new ExtrusionNode();
//        ExtrusionNode extrusionNode3 = new ExtrusionNode();
//        ExtrusionNode extrusionNode4 = new ExtrusionNode();
//        ExtrusionNode extrusionNode5 = new ExtrusionNode();
//        ExtrusionNode extrusionNode6 = new ExtrusionNode();
//        ExtrusionNode extrusionNode7 = new ExtrusionNode();
//        ExtrusionNode extrusionNode8 = new ExtrusionNode();
//        ExtrusionNode extrusionNode9 = new ExtrusionNode();
//        ExtrusionNode extrusionNode10 = new ExtrusionNode();
//        ExtrusionNode extrusionNode11 = new ExtrusionNode();
//        ExtrusionNode extrusionNode12 = new ExtrusionNode();
//        ExtrusionNode extrusionNode13 = new ExtrusionNode();
//        ExtrusionNode extrusionNode14 = new ExtrusionNode();
//        ExtrusionNode extrusionNode15 = new ExtrusionNode();
//        ExtrusionNode extrusionNode16 = new ExtrusionNode();
//        ExtrusionNode extrusionNode17 = new ExtrusionNode();
//        ExtrusionNode extrusionNode18 = new ExtrusionNode();
//
//        object1.addChildAtEnd(inner1);
//        object1.addChildAtEnd(outer1);
//        object1.addChildAtEnd(fill1);
//
//        object2.addChildAtEnd(fill2);
//        object2.addChildAtEnd(inner2);
//        object2.addChildAtEnd(outer2);
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
//        testLayer.addChildAtEnd(object1);
//        testLayer.addChildAtEnd(object2);
//
//        // INPUT
//        //                             layer
//        //                               |
//        //             -------------------------------------
//        //             |                                   |
//        //           object1                            object2
//        //             |                                   |
//        //     ---------------------         ----------------------------
//        //     |         |         |         |             |            |
//        //   inner1    outer1    fill1     fill2         inner2       outer2
//        //     |         |         |         |             |            |
//        //  -------   -------   -------   ---------    ---------    ---------
//        //  |  |  |   |  |  |   |  |  |   |   |   |    |   |   |    |   |   |
//        //  e1 e2 e3  e4 e5 e6  e7 e8 e9  e10 e11 e12  e13 e14 e15  e16 e17 e18
//        // OUTPUT for task-based tool selection
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
//        // OUTPUT for object-based tool selection - support in object material
//        //
//        //                             layer
//        //                               |
//        //               ------------------------------------
//        //               |                                  |
//        //            object1                             object2
//        //               |                                  |
//        //             tool(0)                            tool(1)
//        //               |                                  |
//        //     ---------------------          ----------------------------
//        //     |         |         |          |             |            |
//        //   inner     outer      fill      fill          inner        outer
//        //     |         |         |          |             |            |
//        //  -------   -------   -------   ---------    ---------    ---------
//        //  |  |  |   |  |  |   |  |  |   |   |   |    |   |   |    |   |   |
//        //  e1 e2 e3  e4 e5 e6  e7 e8 e9  e10 e11 e12  e13 e14 e15  e16 e17 e18
//        // OUTPUT for support in material 2  TBD
//        //
//        //                             layer
//        //                               |
//        //               ------------------------------------
//        //               |                                  |
//        //            object1                             object2
//        //               |                                  |
//        //             tool(0)                            tool(1)
//        //               |                                  |
//        //     ---------------------          ----------------------------
//        //     |         |         |          |             |            |
//        //   inner     outer      fill      fill          inner        outer
//        //     |         |         |          |             |            |
//        //  -------   -------   -------   ---------    ---------    ---------
//        //  |  |  |   |  |  |   |  |  |   |   |   |    |   |   |    |   |   |
//        //  e1 e2 e3  e4 e5 e6  e7 e8 e9  e10 e11 e12  e13 e14 e15  e16 e17 e18
//        HeadFile singleMaterialHead = HeadContainer.getHeadByID("RBX01-SM");
//
//        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
//        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
//        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
//        ppFeatures.enableFeature(PostProcessorFeature.CLOSE_ON_TASK_CHANGE);
//
//        Project testProject = new Project();
//        testProject.getPrinterSettings().setSettingsName("Draft");
//        testProject.setPrintQuality(PrintQualityEnumeration.CUSTOM);
//
//        TestUtils utils = new TestUtils();
//        ModelContainer modelContainer1 = utils.makeModelContainer(true);
//        testProject.addModel(modelContainer1);
//
//        LayerPostProcessResult lastLayerParseResult = new LayerPostProcessResult(testLayer, 0, 0, 0, 0, null, null, -1);
//
//        List<NozzleProxy> nozzleProxies = new ArrayList<>();
//
//        for (int nozzleIndex = 0;
//                nozzleIndex < testProject.getPrinterSettings().getSettings("RBX01-SM").getNozzleParameters()
//                .size(); nozzleIndex++)
//        {
//            NozzleProxy proxy = new NozzleProxy(testProject.getPrinterSettings().getSettings("RBX01-SM").getNozzleParameters().get(nozzleIndex));
//            proxy.setNozzleReferenceNumber(nozzleIndex);
//            nozzleProxies.add(proxy);
//        }
//
//        NozzleAssignmentUtilities assignmentUtilities = new NozzleAssignmentUtilities(
//                nozzleProxies,
//                testProject.getPrinterSettings().getSettings("RBX01-SM"),
//                singleMaterialHead,
//                ppFeatures,
//                testProject,
//                PostProcessingMode.TASK_BASED_NOZZLE_SELECTION);
//
//        assertEquals(2, testLayer.getChildren().size());
//        assertEquals(3, object1.getChildren().size());
//        assertEquals(3, object2.getChildren().size());
//        assertEquals(3, inner1.getChildren().size());
//        assertEquals(3, outer1.getChildren().size());
//        assertEquals(3, fill1.getChildren().size());
//        assertEquals(3, inner2.getChildren().size());
//        assertEquals(3, outer2.getChildren().size());
//        assertEquals(3, fill2.getChildren().size());
//
//        int lastObjectNumber = assignmentUtilities.insertNozzleControlSectionsByTask(testLayer, lastLayerParseResult, PostProcessingMode.TASK_BASED_NOZZLE_SELECTION);
//
//        assertEquals(22, lastObjectNumber);
//        assertEquals(3, testLayer.getChildren().size());
//        assertTrue(testLayer.getChildren().get(0) instanceof ToolSelectNode);
//        assertTrue(testLayer.getChildren().get(1) instanceof ToolSelectNode);
//        assertTrue(testLayer.getChildren().get(2) instanceof ToolSelectNode);
//
//        ToolSelectNode tool1 = (ToolSelectNode) testLayer.getChildren().get(0);
//        ToolSelectNode tool2 = (ToolSelectNode) testLayer.getChildren().get(1);
//        ToolSelectNode tool3 = (ToolSelectNode) testLayer.getChildren().get(2);
//
//        assertEquals(2, tool1.getChildren().size());
//        assertEquals(2, tool2.getChildren().size());
//        assertEquals(2, tool3.getChildren().size());
//
//        assertSame(inner1, tool1.getChildren().get(0));
//        assertSame(outer1, tool1.getChildren().get(1));
//
//        assertSame(fill1, tool2.getChildren().get(0));
//        assertSame(fill2, tool2.getChildren().get(1));
//
//        assertSame(inner2, tool3.getChildren().get(0));
//        assertSame(outer2, tool3.getChildren().get(1));
//    }
}
