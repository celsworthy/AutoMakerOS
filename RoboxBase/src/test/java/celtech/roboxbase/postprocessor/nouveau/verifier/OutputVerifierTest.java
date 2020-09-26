package celtech.roboxbase.postprocessor.nouveau.verifier;

import celtech.roboxbase.postprocessor.nouveau.LayerPostProcessResult;
import celtech.roboxbase.postprocessor.nouveau.PostProcessorFeature;
import celtech.roboxbase.postprocessor.nouveau.PostProcessorFeatureSet;
import celtech.roboxbase.postprocessor.nouveau.helpers.LayerDefinition;
import celtech.roboxbase.postprocessor.nouveau.helpers.TestDataGenerator;
import celtech.roboxbase.postprocessor.nouveau.helpers.ToolDefinition;
import celtech.roboxbase.postprocessor.nouveau.nodes.ExtrusionNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.MCodeNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.NozzleValvePositionNode;
import celtech.roboxbase.printerControl.model.Head.HeadType;
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
public class OutputVerifierTest
{

    public OutputVerifierTest()
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
     * Test of verifyAllLayers method, of class OutputVerifier.
     */
    @Test
    public void testVerifyAllLayers_noNozzleOpen()
    {
        System.out.println("verifyAllLayers");

        List<LayerDefinition> layers = new ArrayList<>();
        layers.add(new LayerDefinition(0, new ToolDefinition[]
        {
            new ToolDefinition(0, 5),
            new ToolDefinition(1, 500)
        }));

        List<LayerPostProcessResult> allLayerPostProcessResults = TestDataGenerator.generateLayerResults(layers);

        PostProcessorFeatureSet featureSet = new PostProcessorFeatureSet();
        featureSet.enableFeature(PostProcessorFeature.OPEN_AND_CLOSE_NOZZLES);

        OutputVerifier instance = new OutputVerifier(featureSet);
        List<VerifierResult> verifierResults = instance.verifyAllLayers(allLayerPostProcessResults, HeadType.DUAL_MATERIAL_HEAD);

        assertEquals(0, verifierResults.size());
    }

    /**
     * Test of verifyAllLayers method, of class OutputVerifier.
     */
    @Test
    public void testVerifyAllLayers_allGood()
    {
        System.out.println("verifyAllLayers");

        List<LayerDefinition> layers = new ArrayList<>();
        layers.add(new LayerDefinition(0, new ToolDefinition[]
        {
            new ToolDefinition(0, 5),
            new ToolDefinition(1, 500)
        }));

        List<LayerPostProcessResult> allLayerPostProcessResults = TestDataGenerator.generateLayerResults(layers);

        NozzleValvePositionNode openNozzle = new NozzleValvePositionNode();
        openNozzle.getNozzlePosition().setB(1.0);
        allLayerPostProcessResults.get(0).getLayerData().getChildren().get(0).addChildAtStart(openNozzle);

        PostProcessorFeatureSet featureSet = new PostProcessorFeatureSet();
        featureSet.enableFeature(PostProcessorFeature.OPEN_AND_CLOSE_NOZZLES);

        OutputVerifier instance = new OutputVerifier(featureSet);
        List<VerifierResult> verifierResults = instance.verifyAllLayers(allLayerPostProcessResults, HeadType.DUAL_MATERIAL_HEAD);

        assertEquals(0, verifierResults.size());
    }

    /**
     * Test of verifyAllLayers method, of class OutputVerifier.
     */
    @Test
    public void testVerifyAllLayers_heaterOnOff()
    {
        System.out.println("heaterOnOff");

        List<LayerDefinition> layers = new ArrayList<>();
        layers.add(new LayerDefinition(0, new ToolDefinition[]
        {
            new ToolDefinition(0, 5),
            new ToolDefinition(1, 500),
            new ToolDefinition(0, 500)
        }));

        List<LayerPostProcessResult> allLayerPostProcessResults = TestDataGenerator.generateLayerResults(layers);

        NozzleValvePositionNode openNozzle = new NozzleValvePositionNode();
        openNozzle.getNozzlePosition().setB(1.0);
        allLayerPostProcessResults.get(0).getLayerData().getChildren().get(0).addChildAtStart(openNozzle);

        MCodeNode switchOffNozzle0 = new MCodeNode();
        switchOffNozzle0.setMNumber(104);
        switchOffNozzle0.setSNumber(0);
        allLayerPostProcessResults.get(0).getLayerData().getChildren().get(0).addChildAtEnd(switchOffNozzle0);

        MCodeNode heatNozzle0 = new MCodeNode();
        heatNozzle0.setMNumber(104);
        heatNozzle0.setSOnly(true);
        allLayerPostProcessResults.get(0).getLayerData().getChildren().get(2).addChildAtStart(heatNozzle0);

        PostProcessorFeatureSet featureSet = new PostProcessorFeatureSet();
        featureSet.enableFeature(PostProcessorFeature.OPEN_AND_CLOSE_NOZZLES);

        OutputVerifier instance = new OutputVerifier(featureSet);
        List<VerifierResult> verifierResults = instance.verifyAllLayers(allLayerPostProcessResults, HeadType.DUAL_MATERIAL_HEAD);

        assertEquals(0, verifierResults.size());
    }

    /**
     * Test of verifyAllLayers method, of class OutputVerifier.
     */
    @Test
    public void testVerifyAllLayers_nozzleCloseInExtrusion()
    {
        System.out.println("nozzleCloseInExtrusion");

        List<LayerDefinition> layers = new ArrayList<>();
        layers.add(new LayerDefinition(0, new ToolDefinition[]
        {
            new ToolDefinition(0, 5),
            new ToolDefinition(1, 500)
        }));

        List<LayerPostProcessResult> allLayerPostProcessResults = TestDataGenerator.generateLayerResults(layers);

        NozzleValvePositionNode openNozzle = new NozzleValvePositionNode();
        openNozzle.getNozzlePosition().setB(1.0);
        allLayerPostProcessResults.get(0).getLayerData().getChildren().get(0).addChildAtStart(openNozzle);

        ExtrusionNode extrusionToOperateOn = ((ExtrusionNode) allLayerPostProcessResults.get(0).getLayerData().getChildren().get(0).getAbsolutelyTheLastEvent());
        extrusionToOperateOn.getExtrusion().dNotInUse();
        extrusionToOperateOn.getExtrusion().eNotInUse();
        extrusionToOperateOn.getNozzlePosition().setB(0);

        NozzleValvePositionNode openNozzle2 = new NozzleValvePositionNode();
        openNozzle2.getNozzlePosition().setB(1.0);
        allLayerPostProcessResults.get(0).getLayerData().getChildren().get(1).addChildAtStart(openNozzle2);

        PostProcessorFeatureSet featureSet = new PostProcessorFeatureSet();
        featureSet.enableFeature(PostProcessorFeature.OPEN_AND_CLOSE_NOZZLES);

        OutputVerifier instance = new OutputVerifier(featureSet);
        List<VerifierResult> verifierResults = instance.verifyAllLayers(allLayerPostProcessResults, HeadType.DUAL_MATERIAL_HEAD);

        assertEquals(0, verifierResults.size());
    }

    /**
     * Test of verifyAllLayers method, of class OutputVerifier.
     */
    @Test
    public void testVerifyAllLayers_nozzleCloseInExtrusionWithRetract()
    {
        System.out.println("nozzleCloseInExtrusionWithRetract");

        List<LayerDefinition> layers = new ArrayList<>();
        layers.add(new LayerDefinition(0, new ToolDefinition[]
        {
            new ToolDefinition(0, 5),
            new ToolDefinition(1, 500)
        }));

        List<LayerPostProcessResult> allLayerPostProcessResults = TestDataGenerator.generateLayerResults(layers);

        NozzleValvePositionNode openNozzle = new NozzleValvePositionNode();
        openNozzle.getNozzlePosition().setB(1.0);
        allLayerPostProcessResults.get(0).getLayerData().getChildren().get(0).addChildAtStart(openNozzle);

        ExtrusionNode extrusionToOperateOn = ((ExtrusionNode) allLayerPostProcessResults.get(0).getLayerData().getChildren().get(0).getAbsolutelyTheLastEvent());
        extrusionToOperateOn.getExtrusion().dNotInUse();
        extrusionToOperateOn.getExtrusion().eNotInUse();
        extrusionToOperateOn.getNozzlePosition().setB(0);
        extrusionToOperateOn.getExtrusion().setE(-10);

        NozzleValvePositionNode openNozzle2 = new NozzleValvePositionNode();
        openNozzle2.getNozzlePosition().setB(1.0);
        allLayerPostProcessResults.get(0).getLayerData().getChildren().get(1).addChildAtStart(openNozzle2);

        PostProcessorFeatureSet featureSet = new PostProcessorFeatureSet();
        featureSet.enableFeature(PostProcessorFeature.OPEN_AND_CLOSE_NOZZLES);

        OutputVerifier instance = new OutputVerifier(featureSet);
        List<VerifierResult> verifierResults = instance.verifyAllLayers(allLayerPostProcessResults, HeadType.DUAL_MATERIAL_HEAD);

        assertEquals(0, verifierResults.size());
    }
}
