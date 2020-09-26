package celtech.roboxbase.postprocessor;

import celtech.roboxbase.postprocessor.GCodeValidator;
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
public class GCodeValidatorTest
{
    
    public GCodeValidatorTest()
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
     * Test of validate method, of class GCodeValidator.
     */
    @Test
    public void testValidateSuccess()
    {
        System.out.println("validate");
        GCodeValidator instance = new GCodeValidator(this.getClass().getResource("/postprocessor/validatorTest_1.gcode").getFile());
        boolean expResult = false;
        boolean result = instance.validate();
        assertEquals(expResult, result);
    }

    /**
     * Test of validate method, of class GCodeValidator.
     */
    @Test
    public void testValidateFail()
    {
        System.out.println("validate");
        GCodeValidator instance = new GCodeValidator(this.getClass().getResource("/postprocessor/validatorTest_2.gcode").getFile());
        boolean expResult = true;
        boolean result = instance.validate();
        assertEquals(expResult, result);
    }

    /**
     * Test of validate method, of class GCodeValidator.
     */
    @Test
    public void testValidateSuccessFromPartialOpen()
    {
        System.out.println("validate");
        GCodeValidator instance = new GCodeValidator(this.getClass().getResource("/postprocessor/validatorTest_3.gcode").getFile());
        boolean expResult = true;
        boolean result = instance.validate();
        assertEquals(expResult, result);
    }

    /**
     * Test of validate method, of class GCodeValidator.
     */
    @Test
    public void testValidateOpenWithoutClose()
    {
        System.out.println("validate");
        GCodeValidator instance = new GCodeValidator(this.getClass().getResource("/postprocessor/validatorTest_openWithoutClose.gcode").getFile());
        boolean expResult = false;
        boolean result = instance.validate();
        assertEquals(expResult, result);
    }
    
    
    
}
