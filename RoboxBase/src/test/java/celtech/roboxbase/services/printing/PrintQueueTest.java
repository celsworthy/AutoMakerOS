/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.services.printing;

import celtech.roboxbase.printerControl.model.PrintEngine;
import celtech.roboxbase.utils.BaseEnvironmentConfiguredTest;

/**
 *
 * @author tony
 */
public class PrintQueueTest extends BaseEnvironmentConfiguredTest
{

    static final int WAIT_INTERVAL = 500;
    static final int MAX_WAIT_INTERVAL = 3000;
    static final String DRAFT_SETTINGS = "DraftSettings";

    PrintEngine printQueue;

//    /**
//     * Test that progressProperty is 0 at start of print
//     */
//    @Test
//    public void testProgressPropertyIsZeroAtStartOfPrint()
//    {
//        TestCommandInterface testCommandInterface = new TestCommandInterface(null, "Test HardwarePrinter", false, 500);
//        TestSlicerService slicerService = new TestSlicerService();
//        HardwarePrinter printer = new HardwarePrinter(null, testCommandInterface);
//
//        printQueue = new PrintEngine(printer);
//        ReadOnlyDoubleProperty result = printQueue.progressProperty();
//        assertEquals(0d, result.get(), 0.001);
//    }
//
//    @Before
//    public void setupPrintQueue()
//    {
//        TestCommandInterface testCommandInterface = new TestCommandInterface(null, "Test HardwarePrinter", false, 500);
//        TestSlicerService slicerService = new TestSlicerService();
//        HardwarePrinter testPrinter = new HardwarePrinter(null, testCommandInterface);
//
//        testPrinter.setBedTargetTemperature(120);
//        testPrinter.setBedTargetTemperature(120);
//
//        TestListFilesResponse listFilesResponse = new TestListFilesResponse();
////        testPrinter.setListFilesResonse(listFilesResponse);
//
//        TestSlicerService testSlicerService = new TestSlicerService();
//        printQueue = new PrintEngine(testPrinter, slicerService);
//
//        STLImporter stlImporter = new STLImporter();
//        DoubleProperty progressProperty = new SimpleDoubleProperty(0);
//        URL pyramidSTLURL = this.getClass().getResource("/pyramid1.stl");
//        ModelLoadResult modelLoadResult = stlImporter.loadFile(null,
//                                                               pyramidSTLURL.getFile(),
//                                                               null,
//                                                               progressProperty);
//        ObservableList<ModelContainer> modelList = FXCollections.observableArrayList(
//            modelLoadResult.getModelContainer());
//        project = new Project("abcdef", "Pyramid", modelList);
//        project.setProjectMode(ProjectMode.MESH);
//    }
//
//    @Test
//    public void testETCCalculatorCreatedForReprint() throws
//        InterruptedException, IOException, URISyntaxException
//    {
//        SlicerParameters SlicerParameters = SlicerParametersContainer.getSettingsByProfileName(
//            DRAFT_SETTINGS);
//        String TEST_JOB_ID = "asdxyz";
//        project.addPrintJobID(TEST_JOB_ID);
//        File printJobDirectory = new File(
//            ApplicationConfiguration.getPrintSpoolDirectory() + TEST_JOB_ID);
//        printJobDirectory.mkdir();
//        System.out.println("PJD " + printJobDirectory);
//        URL pyramidURL = this.getClass().getResource("/pyramid.statistics");
//        Path statisticsFile = Paths.get(pyramidURL.toURI());
//        Path destinationStatisticsFile = Paths.get(printJobDirectory
//            + File.separator + TEST_JOB_ID
//            + ApplicationConfiguration.statisticsFileExtension);
//        Files.copy(statisticsFile, destinationStatisticsFile);
//
//        TestListFilesResponse listFilesResponse = new TestListFilesResponse(
//            TEST_JOB_ID);
//        testPrinter.setListFilesResonse(listFilesResponse);
//
//        printQueue.printProject(project, PrintQualityEnumeration.DRAFT,
//                                SlicerParameters);
//
//        int totalWaitTime = 0;
//        while (true)
//        {
//            System.out.println("STATUS " + printQueue.getPrintStatus());
//            if (PrinterStatus.PRINTING.equals(
//                printQueue.getPrintStatus()))
//            {
//                break;
//            }
//            Thread.sleep(WAIT_INTERVAL);
//            totalWaitTime += WAIT_INTERVAL;
//            if (totalWaitTime > MAX_WAIT_INTERVAL)
//            {
//                fail("Test print took too long");
//            }
//        }
//
//        int linesInFile = printQueue.linesInPrintingFileProperty().get();
//        testPrinter.setPrintJobLineNumber(linesInFile);
//
//        ReadOnlyDoubleProperty progress = printQueue.progressProperty();
//        assertEquals(1.0d, progress.get(), 0.001);
//    }
//
//    /**
//     * Test that progressProperty is 1 at end of print
//     *
//     * @throws java.io.IOException
//     * @throws java.lang.InterruptedException
//     */
//    @Test
//    public void testProgressPropertyIsOneAtEndOfPrint() throws IOException, InterruptedException
//    {
//        SlicerParameters SlicerParameters = SlicerParametersContainer.getSettingsByProfileName(
//            DRAFT_SETTINGS);
//        printQueue.printProject(project, PrintQualityEnumeration.DRAFT,
//                                SlicerParameters);
//
//        int totalWaitTime = 0;
//        while (true)
//        {
//            System.out.println("STATUS " + printQueue.getPrintStatus());
//            if (PrinterStatus.PRINTING.equals(
//                printQueue.getPrintStatus()))
//            {
//                break;
//            }
//            Thread.sleep(WAIT_INTERVAL);
//            totalWaitTime += WAIT_INTERVAL;
//            if (totalWaitTime > MAX_WAIT_INTERVAL)
//            {
//                fail("Test print took too long");
//            }
//        }
//
//        testPrinter.setPrintJobLineNumber(0);
//        Thread.sleep(2000);
//        testPrinter.setPrintJobLineNumber(1);
//
//        int linesInFile = printQueue.linesInPrintingFileProperty().get();
//        testPrinter.setPrintJobLineNumber(linesInFile);
//
//        int ETC = printQueue.progressETCProperty().get();
//        assertEquals(0, ETC);
//        ReadOnlyDoubleProperty progress = printQueue.progressProperty();
//        assertEquals(1.0d, progress.get(), 0.001);
//    }
//
//    @Test
//    public void testCurrentLayerAtEndOfPrint() throws IOException, InterruptedException
//    {
//        SlicerParameters SlicerParameters = SlicerParametersContainer.getSettingsByProfileName(
//            DRAFT_SETTINGS);
//        printQueue.printProject(project, PrintQualityEnumeration.DRAFT,
//                                SlicerParameters);
//
//        int totalWaitTime = 0;
//        while (true)
//        {
//            System.out.println("STATUS " + printQueue.getPrintStatus());
//            if (PrinterStatus.PRINTING.equals(
//                printQueue.getPrintStatus()))
//            {
//                break;
//            }
//            Thread.sleep(WAIT_INTERVAL);
//            totalWaitTime += WAIT_INTERVAL;
//            if (totalWaitTime > MAX_WAIT_INTERVAL)
//            {
//                fail("Test print took too long");
//            }
//        }
//
//        testPrinter.setPrintJobLineNumber(0);
//        Thread.sleep(1000);
//        testPrinter.setPrintJobLineNumber(1);
//
//        int linesInFile = printQueue.linesInPrintingFileProperty().get();
//        testPrinter.setPrintJobLineNumber(linesInFile);
//
//        int currentLayer = printQueue.progressCurrentLayerProperty().get();
//        assertEquals(66, currentLayer);
//    }
//
//    @Test
//    public void testCurrentLayerAtStartOfPrint() throws IOException, InterruptedException
//    {
//        SlicerParameters SlicerParameters = SlicerParametersContainer.getSettingsByProfileName(
//            DRAFT_SETTINGS);
//        printQueue.printProject(project, PrintQualityEnumeration.DRAFT,
//                                SlicerParameters);
//
//        int totalWaitTime = 0;
//        while (true)
//        {
//            System.out.println("STATUS " + printQueue.getPrintStatus());
//            if (PrinterStatus.PRINTING.equals(
//                printQueue.getPrintStatus()))
//            {
//                break;
//            }
//            Thread.sleep(WAIT_INTERVAL);
//            totalWaitTime += WAIT_INTERVAL;
//            if (totalWaitTime > MAX_WAIT_INTERVAL)
//            {
//                fail("Test print took too long");
//            }
//        }
//
//        testPrinter.setPrintJobLineNumber(0);
//        Thread.sleep(1000);
//        testPrinter.setPrintJobLineNumber(1);
//        int currentLayer = printQueue.progressCurrentLayerProperty().get();
//        assertEquals(0, currentLayer);
//    }

}
