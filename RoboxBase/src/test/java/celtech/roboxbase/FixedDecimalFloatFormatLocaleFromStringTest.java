/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase;

import celtech.roboxbase.comms.remote.FixedDecimalFloatFormat;
import celtech.roboxbase.comms.remote.FixedDecimalFloatFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author Ian
 */
@RunWith(Parameterized.class)
public class FixedDecimalFloatFormatLocaleFromStringTest
{

    private final double epsilon = 1e-10;
    private FixedDecimalFloatFormat formatter = null;
    private String inputString = null;
    private double expectedResult = 0;
    private Locale localeToUse = null;
    private static Locale originalLocale = Locale.getDefault();

    public FixedDecimalFloatFormatLocaleFromStringTest(String inputString, double expectedResult, Locale localeToUse)
    {
        this.inputString = inputString;
        this.expectedResult = expectedResult;
        this.localeToUse = localeToUse;
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
        Locale.setDefault(originalLocale);
    }

    @Before
    public void setUp()
    {
        Locale.setDefault(localeToUse);
        formatter = new FixedDecimalFloatFormat();
    }

    @After
    public void tearDown()
    {
    }

    @Parameterized.Parameters
    public static Collection testData()
    {
        return Arrays.asList(new Object[][]
        {
            {
                "-132.3", -132.3, Locale.UK
            },
            {
                "132.3", 132.3, Locale.UK
            },
            {
                "  131.3", 131.3, Locale.UK
            },
            {
                "123.1456", 123.1456, Locale.UK
            },
            {
                "132.3", 132.3, Locale.FRANCE
            },
            {
                "  131.3", 131.3, Locale.FRANCE
            },
            {
                "123.1456", 123.1456, Locale.FRANCE
            },
            {
                "132.3", 132.3, Locale.CHINA
            },
            {
                "  131.3", 131.3, Locale.CHINA
            },
            {
                "123.1456", 123.1456, Locale.CHINA
            },
            {
                "132.3", 132.3, Locale.KOREA
            },
            {
                "  131.3", 131.3, Locale.KOREA
            },
            {
                "123.1456", 123.1456, Locale.KOREA
            }
        }
        );
    }

    /**
     * Test of parsing, of class FixedDecimalFloatFormat.
     */
    @Test
    public void testParse()
    {
        double result = 0;

        System.out.println("Parsing " + inputString + " with Locale " + localeToUse.toString());
        try
        {
            result = formatter.parse(inputString).doubleValue();
        } catch (ParseException ex)
        {
            System.err.println("Error parsing input:" + inputString);
        }
        assertEquals(expectedResult, result, epsilon);
    }
}
