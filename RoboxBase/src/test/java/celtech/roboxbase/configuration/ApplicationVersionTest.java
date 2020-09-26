package celtech.roboxbase.configuration;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author George Salter
 */
public class ApplicationVersionTest 
{
    @Test
    public void testSameVersionComparison()
    {
        ApplicationVersion version1 = new ApplicationVersion("4.00.00");
        ApplicationVersion version2 = new ApplicationVersion("4.00.00");
        
        int comparison1 = version1.compareTo(version2);
        
        assertTrue(comparison1 == 0);
    }
    
    @Test
    public void testSameNonNumericVersionComparison()
    {
        ApplicationVersion version1 = new ApplicationVersion("gsdev-20190505");
        ApplicationVersion version2 = new ApplicationVersion("gsdev-20190505");
        
        int comparison1 = version1.compareTo(version2);
        
        assertTrue(comparison1 == 0);
    }
    
    @Test
    public void testPatchVersionComparison()
    {
        ApplicationVersion version1 = new ApplicationVersion("4.00.00");
        ApplicationVersion version2 = new ApplicationVersion("4.00.02");
        
        int comparison1 = version1.compareTo(version2);
        int comparison2 = version2.compareTo(version1);
        
        assertTrue(comparison1 < 0);
        assertTrue(comparison2 > 0);
    }
    
    @Test
    public void testMinorVersionComparison()
    {
        ApplicationVersion version1 = new ApplicationVersion("4.00.00");
        ApplicationVersion version2 = new ApplicationVersion("4.02.00");
        
        int comparison1 = version1.compareTo(version2);
        int comparison2 = version2.compareTo(version1);
        
        assertTrue(comparison1 < 0);
        assertTrue(comparison2 > 0);
    }
    
    @Test
    public void testMajorVersionComparison()
    {
        ApplicationVersion version1 = new ApplicationVersion("4.00.00");
        ApplicationVersion version2 = new ApplicationVersion("5.00.00");
        
        int comparison1 = version1.compareTo(version2);
        int comparison2 = version2.compareTo(version1);
        
        assertTrue(comparison1 < 0);
        assertTrue(comparison2 > 0);
    }
    
    @Test
    public void testTrueVersionVsDevelopmentVersionComparison()
    {
        ApplicationVersion version1 = new ApplicationVersion("4.02.02");
        ApplicationVersion version2 = new ApplicationVersion("4.02.02_RC4");
        
        int comparison1 = version1.compareTo(version2);
        int comparison2 = version2.compareTo(version1);
        
        // comparison 1 should be more more than as version 1 is a true version, so higher
        // than a development/release candidate version
        assertTrue(comparison1 > 0);
        assertTrue(comparison2 < 0);
    }
    
    @Test
    public void testDevelopmentVersionCompariston()
    {
        ApplicationVersion version1 = new ApplicationVersion("gsdev-20190505");
        ApplicationVersion version2 = new ApplicationVersion("tadev-20190807");
        
        int comparison1 = version1.compareTo(version2);
        int comparison2 = version2.compareTo(version1);
        
        assertTrue(comparison1 < 0);
        assertTrue(comparison2 > 0);
    }
    
    @Test
    public void testTrueVersionVsNonNumericStringCompariston()
    {
        ApplicationVersion version1 = new ApplicationVersion("4.00.00");
        ApplicationVersion version2 = new ApplicationVersion("tadev-20190807");
        
        int comparison1 = version1.compareTo(version2);
        int comparison2 = version2.compareTo(version1);
        
        assertTrue(comparison1 > 0);
        assertTrue(comparison2 < 0);
    }
}
