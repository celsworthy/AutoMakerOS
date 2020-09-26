package celtech.roboxbase.printerControl.comms.commands;

import celtech.roboxbase.printerControl.comms.commands.MacroFilenameFilter;
import celtech.roboxbase.printerControl.comms.commands.GCodeMacros;
import celtech.roboxbase.configuration.datafileaccessors.HeadContainer;
import celtech.roboxbase.printerControl.model.Head;
import java.io.File;
import java.io.FilenameFilter;
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
public class MacroFilenameFilterTest
{

    public MacroFilenameFilterTest()
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
     * Test of accept method, of class MacroFilenameFilter.
     */
    @Test
    public void testDontAcceptWithWrongMacroName()
    {
        FilenameFilter testFilter = new MacroFilenameFilter("testFile",
                null,
                GCodeMacros.NozzleUseIndicator.DONT_CARE,
                GCodeMacros.SafetyIndicator.DONT_CARE);

        assertFalse(testFilter.accept(null, "fred.gcode"));
    }

    @Test
    public void testAcceptWithCorrectMacroName()
    {
        FilenameFilter testFilter = new MacroFilenameFilter("testFile",
                null,
                GCodeMacros.NozzleUseIndicator.DONT_CARE,
                GCodeMacros.SafetyIndicator.DONT_CARE);

        assertTrue(testFilter.accept(null, "testFile.gcode"));
    }

    @Test
    public void testDontAcceptWithCorrectMacroNameUnrequestedModifier()
    {
        FilenameFilter testFilter = new MacroFilenameFilter("testFile",
                null,
                GCodeMacros.NozzleUseIndicator.DONT_CARE,
                GCodeMacros.SafetyIndicator.DONT_CARE);

        assertFalse(testFilter.accept(null, "testFile#U.gcode"));
    }

    @Test
    public void testDontAcceptWithCorrectMacroNameRequestedModifierNotPresent()
    {
        FilenameFilter testFilter = new MacroFilenameFilter("testFile",
                HeadContainer.defaultHeadID,
                GCodeMacros.NozzleUseIndicator.DONT_CARE,
                GCodeMacros.SafetyIndicator.DONT_CARE);

        assertFalse(testFilter.accept(null, "testFile#U.gcode"));
    }

    @Test
    public void testAcceptWithCorrectMacroNameRequestedModifierPresent()
    {
        FilenameFilter testFilter = new MacroFilenameFilter("testFile",
                "RBX01-DM",
                GCodeMacros.NozzleUseIndicator.DONT_CARE,
                GCodeMacros.SafetyIndicator.DONT_CARE);

        assertTrue(testFilter.accept(null, "testFile#RBX01-DM.gcode"));
    }

    @Test
    public void testAcceptWithCorrectMacroNameMatchingModifierAndExtraModifier()
    {
        FilenameFilter testFilter = new MacroFilenameFilter("testFile",
                "RBX01-DM",
                GCodeMacros.NozzleUseIndicator.DONT_CARE,
                GCodeMacros.SafetyIndicator.DONT_CARE);

        assertFalse(testFilter.accept(null, "testFile#U#RBX01-DM.gcode"));
    }

    @Test
    public void testSafetyOff()
    {
        FilenameFilter testFilter = new MacroFilenameFilter("testFile",
                "RBX01-DM",
                GCodeMacros.NozzleUseIndicator.DONT_CARE,
                GCodeMacros.SafetyIndicator.SAFETIES_OFF);

        assertTrue(testFilter.accept(null, "testFile#U#RBX01-DM.gcode"));
    }
    
    @Test
    public void testSimulateSafetyFallback()
    {
        FilenameFilter testFilter = new MacroFilenameFilter("testFile",
                "RBX01-DM",
                GCodeMacros.NozzleUseIndicator.DONT_CARE,
                GCodeMacros.SafetyIndicator.SAFETIES_OFF);

        assertFalse(testFilter.accept(null, "testFile#RBX01-DM.gcode"));

        FilenameFilter testFilter2 = new MacroFilenameFilter("testFile",
                "RBX01-DM",
                GCodeMacros.NozzleUseIndicator.DONT_CARE,
                GCodeMacros.SafetyIndicator.DONT_CARE);

        assertTrue(testFilter2.accept(null, "testFile#RBX01-DM.gcode"));
    }
}
