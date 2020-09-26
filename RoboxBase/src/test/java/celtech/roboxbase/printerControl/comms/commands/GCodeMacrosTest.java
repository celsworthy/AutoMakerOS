package celtech.roboxbase.printerControl.comms.commands;

import celtech.roboxbase.configuration.hardwarevariants.PrinterType;
import celtech.roboxbase.utils.BaseEnvironmentConfiguredTest;
import java.util.Optional;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Ian
 */
public class GCodeMacrosTest extends BaseEnvironmentConfiguredTest
{

    @Test
    public void testGetFilenameNoSpecialisation() throws Exception {
        PrinterType typeCode = PrinterType.ROBOX;
        String headTypeCode = "RBX01-SM";
        GCodeMacros.NozzleUseIndicator nozzleUse = GCodeMacros.NozzleUseIndicator.NOZZLE_0;
        String fileName = GCodeMacros.getFilename("Purge_T1", Optional.of(typeCode),
                headTypeCode, nozzleUse,
                GCodeMacros.SafetyIndicator.SAFETIES_ON);
        assertTrue(fileName.endsWith("Common/Macros/Purge_T1.gcode"));
    }

    @Test
    public void testGetFilenameCheckBase() throws Exception {
        PrinterType typeCode = PrinterType.ROBOX;
        String headTypeCode = "RBX01-SM";
        GCodeMacros.NozzleUseIndicator nozzleUse = GCodeMacros.NozzleUseIndicator.NOZZLE_0;
        String fileName = GCodeMacros.getFilename("Home_all", Optional.of(typeCode),
                headTypeCode, nozzleUse,
                GCodeMacros.SafetyIndicator.SAFETIES_ON);
        assertTrue(fileName.endsWith("Common/Macros/Home_all.gcode"));
    }

    @Test
    public void testGetFilenameForHeadSpecialisation() throws Exception {
        PrinterType typeCode = PrinterType.ROBOX;
        String headTypeCode = "RBX01-DM";
        GCodeMacros.NozzleUseIndicator nozzleUse = GCodeMacros.NozzleUseIndicator.NOZZLE_0;
        String fileName = GCodeMacros.getFilename("PurgeMaterial", Optional.of(typeCode),
                headTypeCode, nozzleUse,
                GCodeMacros.SafetyIndicator.SAFETIES_ON);
        assertTrue(fileName.endsWith("/Common/Macros/PurgeMaterial#RBX01-DM#N0.gcode"));
    }

    @Test
    public void testGetFilenameForNozzleSpecialisation() throws Exception {
        PrinterType typeCode = PrinterType.ROBOX;
        String headTypeCode = "RBX01-DM";
        GCodeMacros.NozzleUseIndicator nozzleUse = GCodeMacros.NozzleUseIndicator.NOZZLE_1;
        String fileName = GCodeMacros.getFilename("Short_Purge", Optional.of(typeCode),
                headTypeCode, nozzleUse,
                GCodeMacros.SafetyIndicator.SAFETIES_ON);
        assertTrue(fileName.endsWith("/Common/Macros/Short_Purge#RBX01-DM#N1.gcode"));
    }

    @Test
    public void testGetFilenameForPrinterTypeSpecialisation() throws Exception {
        PrinterType typeCode = PrinterType.ROBOX_PRO;
        String headTypeCode = "RBX01-SM";
        GCodeMacros.NozzleUseIndicator nozzleUse = GCodeMacros.NozzleUseIndicator.NOZZLE_1;
        String fileName = GCodeMacros.getFilename("Remove_Head", Optional.of(typeCode),
                headTypeCode, nozzleUse,
                GCodeMacros.SafetyIndicator.SAFETIES_ON);
        assertTrue(fileName.endsWith("/Common/Macros/RBX10/Remove_Head.gcode"));
    }

    @Test
    public void testGetFilenameForPrinterTypeSpecialisation2() throws Exception {
        PrinterType typeCode = PrinterType.ROBOX_PRO;
        String headTypeCode = "RBX01-SM";
        GCodeMacros.NozzleUseIndicator nozzleUse = GCodeMacros.NozzleUseIndicator.NOZZLE_1;
        String fileName = GCodeMacros.getFilename("after_print", Optional.of(typeCode),
                headTypeCode, nozzleUse,
                GCodeMacros.SafetyIndicator.SAFETIES_ON);
        assertTrue(fileName.endsWith("/Common/Macros/after_print.gcode"));
    }

    @Test
    public void testGetFilenameForPrinterTypeNozzleSpecialisation() throws Exception {
        PrinterType typeCode = PrinterType.ROBOX_PRO;
        String headTypeCode = "RBX01-SM";
        GCodeMacros.NozzleUseIndicator nozzleUse = GCodeMacros.NozzleUseIndicator.NOZZLE_1;
        String fileName = GCodeMacros.getFilename("Short_Purge", Optional.of(typeCode),
                headTypeCode, nozzleUse,
                GCodeMacros.SafetyIndicator.SAFETIES_ON);
        assertTrue(fileName.endsWith("/Common/Macros/RBX10/Short_Purge#N1.gcode"));
    }

    /**
     * Test of getMacroContents method, of class GCodeMacros.
     */
    @Test
    public void testScoreMacroFilename() throws Exception
    {
        // File: DC, DC, DC
        // Specification: DC, DC, DC
        String filename1 = "before_print.gcode";
        int score1 = GCodeMacros.scoreMacroFilename(filename1,
                "before_print",
                null,
                GCodeMacros.NozzleUseIndicator.DONT_CARE,
                GCodeMacros.SafetyIndicator.DONT_CARE);
        assertEquals(6, score1);

        // File: DM, DC, DC
        // Specification: DC, DC, DC
        String filename2 = "before_print#RBX01-DM.gcode";
        int score2 = GCodeMacros.scoreMacroFilename(filename2,
                "before_print",
                null,
                GCodeMacros.NozzleUseIndicator.DONT_CARE,
                GCodeMacros.SafetyIndicator.DONT_CARE);
        assertEquals(2, score2);

        // File: DM, 0 DC
        // Specification: DC, DC, DC
        String filename3 = "before_print#RBX01-DM#N0.gcode";
        int score3 = GCodeMacros.scoreMacroFilename(filename3,
                "before_print",
                null,
                GCodeMacros.NozzleUseIndicator.DONT_CARE,
                GCodeMacros.SafetyIndicator.DONT_CARE);
        assertEquals(-2, score3);

        // File: DM, 0 DC
        // Specification: DM, DC, DC
        String filename4 = "before_print#RBX01-DM#N0.gcode";
        int score4 = GCodeMacros.scoreMacroFilename(filename4,
                "before_print",
                "RBX01-DM",
                GCodeMacros.NozzleUseIndicator.DONT_CARE,
                GCodeMacros.SafetyIndicator.DONT_CARE);
        assertEquals(2, score4);

        String filename5 = "before_print#RBX01-DM#N0.gcode";
        int score5 = GCodeMacros.scoreMacroFilename(filename5,
                "before_print",
                "RBX01-DM",
                GCodeMacros.NozzleUseIndicator.NOZZLE_0,
                GCodeMacros.SafetyIndicator.DONT_CARE);
        assertEquals(6, score5);

        String filename6 = "before_print#RBX01-DM#N0.gcode";
        int score6 = GCodeMacros.scoreMacroFilename(filename6,
                "before_print",
                "RBX01-SM",
                GCodeMacros.NozzleUseIndicator.DONT_CARE,
                GCodeMacros.SafetyIndicator.DONT_CARE);
        assertEquals(-2, score6);

        String filename7 = "before_print.gcode";
        int score7 = GCodeMacros.scoreMacroFilename(filename7,
                "before_print",
                "RBX01-SM",
                GCodeMacros.NozzleUseIndicator.DONT_CARE,
                GCodeMacros.SafetyIndicator.DONT_CARE);
        assertEquals(6, score7);

        String filename8 = "before_print#RBX01-SM.gcode";
        int score8 = GCodeMacros.scoreMacroFilename(filename8,
                "before_print",
                "RBX01-SM",
                GCodeMacros.NozzleUseIndicator.DONT_CARE,
                GCodeMacros.SafetyIndicator.DONT_CARE);
        assertEquals(6, score8);
    }

    @Test
    public void testScoreMacroFilenameRank() throws Exception
    {
        String headTypeToScoreAgainst = "RBX01-DM";
        GCodeMacros.NozzleUseIndicator nozzleToScoreAgainst = GCodeMacros.NozzleUseIndicator.NOZZLE_1;
        GCodeMacros.SafetyIndicator safetyToScoreAgainst = GCodeMacros.SafetyIndicator.SAFETIES_OFF;

        String filename1 = "eject_stuck_material#N0.gcode";
        int score1 = GCodeMacros.scoreMacroFilename(filename1,
                "eject_stuck_material",
                headTypeToScoreAgainst,
                nozzleToScoreAgainst,
                safetyToScoreAgainst);
        assertEquals(0, score1);

        String filename2 = "eject_stuck_material#N1.gcode";
        int score2 = GCodeMacros.scoreMacroFilename(filename2,
                "eject_stuck_material",
                headTypeToScoreAgainst,
                nozzleToScoreAgainst,
                safetyToScoreAgainst);
        assertEquals(4, score2);

        String filename3 = "eject_stuck_material#RBX01-DM#N0.gcode";
        int score3 = GCodeMacros.scoreMacroFilename(filename3,
                "eject_stuck_material",
                headTypeToScoreAgainst,
                nozzleToScoreAgainst,
                safetyToScoreAgainst);
        assertEquals(1, score3);

        String filename4 = "eject_stuck_material#RBX01-DM#N1.gcode";
        int score4 = GCodeMacros.scoreMacroFilename(filename4,
                "eject_stuck_material",
                headTypeToScoreAgainst,
                nozzleToScoreAgainst,
                safetyToScoreAgainst);
        assertEquals(5, score4);
    }

    @Test
    public void testScoreMacroFilename_DefaultSM() throws Exception
    {
        String headTypeToScoreAgainst = "RBX01-SM";
        GCodeMacros.NozzleUseIndicator nozzleToScoreAgainst = GCodeMacros.NozzleUseIndicator.DONT_CARE;
        GCodeMacros.SafetyIndicator safetyToScoreAgainst = GCodeMacros.SafetyIndicator.DONT_CARE;

        String filename1 = "before_print.gcode";
        int score1 = GCodeMacros.scoreMacroFilename(filename1,
                "before_print",
                headTypeToScoreAgainst,
                nozzleToScoreAgainst,
                safetyToScoreAgainst);
        assertEquals(6, score1);

        String filename2 = "before_print#RBX01-DL.gcode";
        int score2 = GCodeMacros.scoreMacroFilename(filename2,
                "before_print",
                headTypeToScoreAgainst,
                nozzleToScoreAgainst,
                safetyToScoreAgainst);
        assertEquals(2, score2);

        String filename3 = "before_print#RBX01-DM#N0.gcode";
        int score3 = GCodeMacros.scoreMacroFilename(filename3,
                "before_print",
                headTypeToScoreAgainst,
                nozzleToScoreAgainst,
                safetyToScoreAgainst);
        assertEquals(-2, score3);

        String filename4 = "before_print#RBX01-DM#NB.gcode";
        int score4 = GCodeMacros.scoreMacroFilename(filename4,
                "before_print",
                headTypeToScoreAgainst,
                nozzleToScoreAgainst,
                safetyToScoreAgainst);
        assertEquals(-2, score4);
}

    @Test
    public void testScoreMacroFilename_safeties() throws Exception
    {
        String headTypeToScoreAgainst = "RBX01-SM";
        GCodeMacros.NozzleUseIndicator nozzleToScoreAgainst = GCodeMacros.NozzleUseIndicator.DONT_CARE;
        GCodeMacros.SafetyIndicator safetyToScoreAgainst = GCodeMacros.SafetyIndicator.DONT_CARE;

        String filename1 = "before_print.gcode";
        int score1 = GCodeMacros.scoreMacroFilename(filename1,
                "before_print",
                headTypeToScoreAgainst,
                nozzleToScoreAgainst,
                safetyToScoreAgainst);
        assertEquals(6, score1);

        String filename2 = "before_print#U.gcode";
        int score2 = GCodeMacros.scoreMacroFilename(filename2,
                "before_print",
                headTypeToScoreAgainst,
                nozzleToScoreAgainst,
                safetyToScoreAgainst);
        assertEquals(2, score2);

        String filename3 = "before_print#S.gcode";
        int score3 = GCodeMacros.scoreMacroFilename(filename3,
                "before_print",
                headTypeToScoreAgainst,
                nozzleToScoreAgainst,
                safetyToScoreAgainst);
        assertEquals(2, score3);

        safetyToScoreAgainst = GCodeMacros.SafetyIndicator.SAFETIES_OFF;
        String filename4 = "before_print#U.gcode";
        int score4 = GCodeMacros.scoreMacroFilename(filename4,
                "before_print",
                headTypeToScoreAgainst,
                nozzleToScoreAgainst,
                safetyToScoreAgainst);
        assertEquals(6, score4);

        String filename5 = "before_print#RBX01-SL.gcode";
        int score5 = GCodeMacros.scoreMacroFilename(filename5,
                "before_print",
                headTypeToScoreAgainst,
                nozzleToScoreAgainst,
                safetyToScoreAgainst);
        assertEquals(1, score5);
    }

    @Test
    public void testScoreMacroFilename_unrecognisedHead() throws Exception
    {

        String headTypeToScoreAgainst = "RBX01-SM";
        GCodeMacros.NozzleUseIndicator nozzleToScoreAgainst = GCodeMacros.NozzleUseIndicator.DONT_CARE;
        GCodeMacros.SafetyIndicator safetyToScoreAgainst = GCodeMacros.SafetyIndicator.DONT_CARE;

        String filename1 = "before_print.gcode";
        int score1 = GCodeMacros.scoreMacroFilename(filename1,
                "before_print",
                headTypeToScoreAgainst,
                nozzleToScoreAgainst,
                safetyToScoreAgainst);
        assertEquals(6, score1);

        String filename2 = "before_print#XXXXX-XX.gcode";
        int score2 = GCodeMacros.scoreMacroFilename(filename2,
                "before_print",
                headTypeToScoreAgainst,
                nozzleToScoreAgainst,
                safetyToScoreAgainst);
        assertEquals(2, score2);
    }
}
