package celtech.roboxbase.postprocessor.nouveau;

import celtech.roboxbase.utils.BaseEnvironmentConfiguredTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 *
 * @author Ian
 */
public class PostProcessorTest extends BaseEnvironmentConfiguredTest
{

    private double movementEpsilon = 0.001;
    private double nozzleEpsilon = 0.01;

    public PostProcessorTest()
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

    @After
    public void tearDown()
    {
    }

    /**
     * Test of processInput method, of class PostProcessor.
     */
//    @Test
//    public void testProcessInput()
//    {
//        System.out.println("processInput");
//        URL inputURL = this.getClass().getResource("/postprocessor/curaTwoObjects.gcode");
//        String inputFilename = inputURL.getFile();
//        String outputFilename = inputFilename + ".out";
//        HeadFile singleMaterialHead = HeadContainer.getHeadByID("RBX01-SM");
//
//        setPostProcessorOutputWriterFactory(LiveGCodeOutputWriter::new);
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
//        PostProcessor postProcessor = new PostProcessor(inputFilename,
//                outputFilename,
//                singleMaterialHead,
//                testProject,
//                testProject.getPrinterSettings().getSettings("RBX01-SM"),
//                ppFeatures,
//                "RBX01-SM",
//                null);
//
//        RoboxiserResult result = postProcessor.processInput();
//        assertTrue(result.isSuccess());
//    }
    /**
     * Test of processInput method, of class PostProcessor.
     */
//    @Test
//    public void testComplexInput()
//    {
//        System.out.println("complexInput");
//        URL inputURL = this.getClass().getResource("/postprocessor/complexTest.gcode");
//        String inputFilename = inputURL.getFile();
//        String outputFilename = inputFilename + ".out";
//        HeadFile singleMaterialHead = HeadContainer.getHeadByID("RBX01-DM");
//
//        setPostProcessorOutputWriterFactory(LiveGCodeOutputWriter::new);
//
//        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
//        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
//        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
//        ppFeatures.enableFeature(PostProcessorFeature.OPEN_AND_CLOSE_NOZZLES);
//
//        Project testProject = new Project();
//        testProject.getPrinterSettings().setSettingsName("Draft");
//        testProject.setPrintQuality(PrintQualityEnumeration.DRAFT);
//
//        TestUtils utils = new TestUtils();
//        ModelContainer modelContainer1 = utils.makeModelContainer(true);
//        testProject.addModel(modelContainer1);
//
//        ModelContainer modelContainer2 = utils.makeModelContainer(false);
//        testProject.addModel(modelContainer2);
//
//        TestPrinter testPrinter = new TestPrinter();
//
//        PostProcessor postProcessor = new PostProcessor(testPrinter,
//                inputFilename,
//                outputFilename,
//                singleMaterialHead,
//                testProject,
//                testProject.getPrinterSettings().getSettings("RBX01-DM"),
//                ppFeatures,
//                "RBX01-DM",
//                null);
//
//        RoboxiserResult result = postProcessor.processInput();
//        assertTrue(result.isSuccess());
//    }
//
//    /**
//     * Test of processInput method, of class PostProcessor.
//     */
//    @Test
//    public void testCura_2_colour_dice()
//    {
//        System.out.println("Cura 2 colour dice");
//        URL inputURL = this.getClass().getResource("/postprocessor/cura_2_colour_dice.gcode");
//        String inputFilename = inputURL.getFile();
//        String outputFilename = inputFilename + ".out";
//        HeadFile singleMaterialHead = HeadContainer.getHeadByID("RBX01-DM");
//
//        setPostProcessorOutputWriterFactory(LiveGCodeOutputWriter::new);
//
//        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();
//        ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
//        ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
//        ppFeatures.enableFeature(PostProcessorFeature.OPEN_AND_CLOSE_NOZZLES);
//
//        Project testProject = new Project();
//        testProject.getPrinterSettings().setSettingsName("Draft");
//        testProject.setPrintQuality(PrintQualityEnumeration.DRAFT);
//
//        TestUtils utils = new TestUtils();
//        ModelContainer modelContainer1 = utils.makeModelContainer(true);
//        testProject.addModel(modelContainer1);
//
//        ModelContainer modelContainer2 = utils.makeModelContainer(false);
//        testProject.addModel(modelContainer2);
//
//        TestPrinter testPrinter = new TestPrinter();
//
//        PostProcessor postProcessor = new PostProcessor(testPrinter,
//                inputFilename,
//                outputFilename,
//                singleMaterialHead,
//                testProject,
//                testProject.getPrinterSettings().getSettings("RBX01-DM"),
//                ppFeatures,
//                "RBX01-SM",
//                null);
//
//        RoboxiserResult result = postProcessor.processInput();
//        assertTrue(result.isSuccess());
//    }
}
