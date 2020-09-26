/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.postprocessor;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author tony
 */
public class PrintJobStatisticsTest
{

    private static final String[] standardStatistics
            =
            {
                ";#########################################################\n",
                ";                     Statistics\n",
                ";                     ==========\n",
                ";#Statistics:printedWithHeadID|->\"RBX01-SM\"\n",
                ";#Statistics:printedWithHeadType|->\"SINGLE_MATERIAL\"\n",
                ";#Statistics:requiresMaterial1|->true\n",
                ";#Statistics:requiresMaterial2|->false\n",
                ";#Statistics:printJobID|->\"abcde\"\n",
                ";#Statistics:projectName|->\"blah\"\n",
                ";#Statistics:profileName|->\"blah2\"\n",
                ";#Statistics:layerHeight|->1.0\n",
                ";#Statistics:numberOfLines|->5\n",
                ";#Statistics:eVolumeUsed|->100.0\n",
                ";#Statistics:dVolumeUsed|->0.0\n",
                ";#Statistics:layerNumberToLineNumber|->[6,7,8]\n",
                ";#Statistics:layerNumberToPredictedDuration_E_FeedrateDependent|->{\"0\":1.2,\"1\":2.3,\"2\":3.4}\n",
                ";#Statistics:layerNumberToPredictedDuration_D_FeedrateDependent|->{}\n",
                ";#Statistics:layerNumberToPredictedDuration_FeedrateIndependent|->{}\n",
                ";#Statistics:predictedDuration|->6.9\n",
                ";#Statistics:lineNumberOfFirstExtrusion|->5\n",
                ";#########################################################\n"
            };
    
    private static final String[] statisticsTestFileContents
            =
            {
                ";#########################################################\n",
                ";                     Statistics\n",
                ";                     ==========\n",
                ";#Statistics:printedWithHeadID|->\"RBX01-DM\"\n",
                ";#Statistics:printedWithHeadType|->\"DUAL_MATERIAL_HEAD\"\n",
                ";#Statistics:requiresMaterial1|->true\n",
                ";#Statistics:requiresMaterial2|->false\n",
                ";#Statistics:printJobID|->\"79cd9e28a03e4fcc\"\n",
                ";#Statistics:projectName|->\"Untitled-040336-250117\"\n",
                ";#Statistics:profileName|->\"Normal\"\n",
                ";#Statistics:layerHeight|->0.2\n",
                ";#Statistics:numberOfLines|->4713\n",
                ";#Statistics:eVolumeUsed|->641.7324906448484\n",
                ";#Statistics:dVolumeUsed|->0.0\n",
                ";#Statistics:layerNumberToLineNumber|->[164,384,557,837,1005,1183,1360,1538,1757,1982,2189,2478,2692,2951,3169,3279,3398,3517,3633,3792,3956,4065,4187,4302,4413,4506,4577,4642,4695,4713]\n",
                ";#Statistics:layerNumberToPredictedDuration_E_FeedrateDependent|->{\"0\":30.268274309326006,\"1\":20.833790393784863,\"2\":15.621462359774434,\"3\":16.891002088458706,\"4\":16.583233163792592,\"5\":15.903155249842973,\"6\":17.007330805514275,\"7\":16.088887683499692,\"8\":17.628296516348538,\"9\":16.23456545662815,\"10\":17.017811763848755,\"11\":16.74028760512741,\"12\":20.298751304231498,\"13\":19.938552503579626,\"14\":18.71395343755278,\"15\":15.488785707474419,\"16\":15.945605596450399,\"17\":16.12958636066601,\"18\":16.358095281770957,\"19\":16.283943704726713,\"20\":18.092318852116513,\"21\":16.02255083875586,\"22\":16.49662424157019,\"23\":17.222486490792274,\"24\":13.099579862257048,\"25\":11.593179643745255,\"26\":9.489163363866552,\"27\":5.752430903498861,\"28\":4.278684844780164,\"29\":0.0}\n",
                ";#Statistics:layerNumberToPredictedDuration_D_FeedrateDependent|->{\"0\":0.0,\"1\":0.0,\"2\":0.0,\"3\":0.0,\"4\":0.0,\"5\":0.0,\"6\":0.0,\"7\":0.0,\"8\":0.0,\"9\":0.0,\"10\":0.0,\"11\":0.0,\"12\":0.0,\"13\":0.0,\"14\":0.0,\"15\":0.0,\"16\":0.0,\"17\":0.0,\"18\":0.0,\"19\":0.0,\"20\":0.0,\"21\":0.0,\"22\":0.0,\"23\":0.0,\"24\":0.0,\"25\":0.0,\"26\":0.0,\"27\":0.0,\"28\":0.0,\"29\":0.0}\n",
                ";#Statistics:layerNumberToPredictedDuration_FeedrateIndependent|->{\"0\":63.21631677343754,\"1\":3.0006132446270173,\"2\":2.9570964926977195,\"3\":2.774247051584879,\"4\":2.811802200699282,\"5\":2.585250517096202,\"6\":2.7899694758055325,\"7\":2.76796242187439,\"8\":2.903618181358137,\"9\":3.0053157433707693,\"10\":2.8279598928877396,\"11\":3.9323749571158126,\"12\":3.237126781714111,\"13\":3.1851583629473774,\"14\":2.9328962376802785,\"15\":1.4466124195167835,\"16\":1.6983862572971702,\"17\":1.8360554494716435,\"18\":1.7977255309953635,\"19\":1.9966130721594535,\"20\":2.1900504531874514,\"21\":1.6311198930250286,\"22\":1.7985483738155073,\"23\":1.6305926032715663,\"24\":1.7991653833335255,\"25\":1.6914541297899925,\"26\":1.3903228328951536,\"27\":1.3805749115179093,\"28\":1.3927704698612784,\"29\":0.11063342198819834}\n",
                ";#Statistics:predictedDuration|->596.7407238708056\n",
                ";#Statistics:lineNumberOfFirstExtrusion|->0\n",
                ";#########################################################\n"
            };

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testDeJSONifier()
    {
        PrintJobStatistics actualResult = new PrintJobStatistics();

        for (String statsString : statisticsTestFileContents)
        {
            actualResult.updateValueFromStatsString(statsString);
        }

        assertEquals(0.2, actualResult.getLayerHeight(), 0.0001);
        assertEquals(63.21631677343754, actualResult.getLayerNumberToPredictedDuration_FeedrateIndependent().get(0), 0);
        assertEquals(0.11063342198819834, actualResult.getLayerNumberToPredictedDuration_FeedrateIndependent().get(29), 0);
    }

    @Test
    public void testLoadFromFile()
    {
        URL inputURL = this.getClass().getResource("/postprocessor/statisticsTestInput.gcode");
        PrintJobStatistics expectedResult = createTestPrintJobStatistics();
        try
        {
            PrintJobStatistics actualResult = PrintJobStatistics.importStatisticsFromGCodeFile(inputURL.getPath());

            assertEquals("RBX01-DM", actualResult.getPrintedWithHeadID());
            assertEquals("DUAL_MATERIAL_HEAD", actualResult.getPrintedWithHeadType());
            assertEquals("79cd9e28a03e4fcc", actualResult.getPrintJobID());
            assertEquals("Untitled-040336-250117", actualResult.getProjectName());
            assertEquals("Normal", actualResult.getProfileName());
            assertEquals(0.2, actualResult.getLayerHeight(), 0.0001);
            assertEquals(596.7407238708056, actualResult.getPredictedDuration(), 0.0001);
            assertEquals(63.21631677343754, actualResult.getLayerNumberToPredictedDuration_FeedrateIndependent().get(0), 0);
        } catch (IOException ex)
        {
            fail(ex.getMessage());
        }
    }
//
//    @Test
//    public void testWriteStatistics()
//    {
//        try
//        {
//            TestGCodeOutputWriter testGCWriter = new TestGCodeOutputWriter(null);
//            PrintJobStatistics printJobStatistics = createTestPrintJobStatistics();
//            printJobStatistics.writeStatisticsToFile(testGCWriter);
//
//            assertThat(testGCWriter.writtenLines, IsIterableContainingInOrder.contains(standardStatistics));
//        } catch (IOException ex)
//        {
//            fail(ex.getMessage());
//        }
//    }

    private PrintJobStatistics createTestPrintJobStatistics()
    {
        String printedWithHeadID = "RBX01-SM";
        String printedWithHeadType = "SINGLE_MATERIAL";
        boolean requiresMaterial1 = true;
        boolean requiresMaterial2 = false;
        String printJobID = "abcde";
        String projectName = "blah";
        String profileName = "blah2";
        double volumeUsed = 100;
        int lineNumberOfFirstExtrusion = 5;
        List<Integer> layerNumberToLineNumber = new ArrayList<>();
        Map<Integer, Double> layerNumberToPredictedDuration_E = new HashMap<>();
        Map<Integer, Double> layerNumberToPredictedDuration_D = new HashMap<>();
        Map<Integer, Double> layerNumberToPredictedDuration_feedrateIndependent = new HashMap<>();

        layerNumberToLineNumber.add(6);
        layerNumberToLineNumber.add(7);
        layerNumberToLineNumber.add(8);

        layerNumberToPredictedDuration_E.put(0, 1.2);
        layerNumberToPredictedDuration_E.put(1, 2.3);
        layerNumberToPredictedDuration_E.put(2, 3.4);

        PrintJobStatistics printJobStatistics = new PrintJobStatistics(
                printedWithHeadID,
                printedWithHeadType,
                requiresMaterial1,
                requiresMaterial2,
                printJobID,
                projectName,
                profileName,
                1,
                lineNumberOfFirstExtrusion,
                volumeUsed,
                0,
                lineNumberOfFirstExtrusion,
                layerNumberToLineNumber,
                layerNumberToPredictedDuration_E,
                layerNumberToPredictedDuration_D,
                layerNumberToPredictedDuration_feedrateIndependent,
                6.9);

        return printJobStatistics;
    }

}
