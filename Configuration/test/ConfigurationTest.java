/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import libertysystems.configuration.ConfigNotLoadedException;
import libertysystems.configuration.Configuration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ianhudson
 */
public class ConfigurationTest
{
    
    public ConfigurationTest()
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    @Test
    public void obtainConfiguration()
    {
        try
        {
        Configuration config = Configuration.getInstance();
        }
        catch (ConfigNotLoadedException ex)
        {
            fail("Exception raised:" + ex);
        }
        
        
    }
}
