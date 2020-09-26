package celuk.language;

import java.net.URL;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Tony Aldhous, derived from original test by Ian Hudson.
 */
public class LanguageDataResourceBundleTest
{
    private String getResourceDirectoryName(String resource)
    {
        URL resourceURL = LanguageDataResourceBundleTest.class.getResource(resource);
        String resourceDirectory = resourceURL.getPath();
        if (System.getProperty("os.name").startsWith("Windows") && resourceDirectory.matches("/[A-Za-z]:.*"))
        {
            // This seeems to be a bug. Windows paths from URLs come out with a leading /
            // e.g. /C:/Root/Leaf
            // Remove the leading slash
            resourceDirectory = resourceDirectory.substring(1);
        }
                
        return resourceDirectory;
    }

    @Before
    public void clearResources()
    {
        ResourceBundle.clearCache();
    }
    
    

    @Test
    public void testLocaleUK()
    {
        I18n.addBundlePrefix("UI_");
        I18n.addBundlePrefix("NoUI_");
        I18n.addSubDirectoryToSearch("Common");
        I18n.addSubDirectoryToSearch("AutoMaker");
        I18n.setApplicationLocale(Locale.UK);

        I18n.setApplicationInstallDirectory(getResourceDirectoryName("/InstallDir"));

        ResourceBundle bundle = ResourceBundle.getBundle("celuk.language.languagedata.LanguageData");
        assertEquals(
                "Nozzle firmware control", bundle.getString("error.ERROR_B_POSITION_LOST"));
        assertEquals(
                1045, bundle.keySet().size());
    }

    @Test
    public void testLocaleFrance_included()
    {
        I18n.setApplicationLocale(Locale.FRANCE);
        I18n.addBundlePrefix("UI_");
        I18n.addBundlePrefix("NoUI_");
        I18n.addSubDirectoryToSearch("Common");
        I18n.addSubDirectoryToSearch("AutoMaker");
        I18n.setApplicationInstallDirectory(getResourceDirectoryName("/InstallDir"));

        ResourceBundle bundle = ResourceBundle.getBundle("celuk.language.languagedata.LanguageData");
        assertEquals(
                "Contrôle firmware de la buse", bundle.getString("error.ERROR_B_POSITION_LOST"));
        assertEquals(
                1045, bundle.keySet().size());
    }

    @Test
    public void testLocaleNonExistent()
    {
        I18n.addBundlePrefix("UI_");
        I18n.addBundlePrefix("NoUI_");
        I18n.addSubDirectoryToSearch("Common");
        I18n.addSubDirectoryToSearch("AutoMaker");

        I18n.setApplicationLocale(Locale.ITALIAN);
        I18n.setApplicationInstallDirectory(getResourceDirectoryName("/InstallDir"));

        ResourceBundle bundle = ResourceBundle.getBundle("celuk.language.languagedata.LanguageData");
        assertEquals(
                "Nozzle firmware control", bundle.getString("error.ERROR_B_POSITION_LOST"));
        assertEquals(
                1045, bundle.keySet().size());
    }
}
