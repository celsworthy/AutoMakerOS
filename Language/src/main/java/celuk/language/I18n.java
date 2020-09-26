/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celuk.language;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Tony
 */
public class I18n {
    private static LanguagePropertiesResourceBundle i18nbundle = null;
    private static Locale applicationLocale = null;
    private static String applicationInstallDirectory = null;

    public static void addBundlePrefix(String prefix)
    {
        LanguagePropertiesResourceBundle.addBundlePrefix(prefix);
    }

    public static void addSubDirectoryToSearch(String subDirectory)
    {
        LanguagePropertiesResourceBundle.addSubDirectoryToSearch(subDirectory);
    }

    public static ResourceBundle getLanguageBundle()
    {
        return i18nbundle;
    }

    public static String t(String stringId)
    {
        String langString = null;
        try
        {
           langString = i18nbundle.getString(stringId);
           langString = substituteTemplates(langString);
        }
        catch (MissingResourceException ex)
        {
            langString = stringId;
        }
        return langString;
    }

    /**
     * Strings containing templates (eg *T14) should be substituted with the
     * correct text.
     *
     * @param langString
     * @return
     */
    public static String substituteTemplates(String langString)
    {
        String patternString = ".*\\*T(\\d\\d).*";
        Pattern pattern = Pattern.compile(patternString);
        while (true)
        {
            Matcher matcher = pattern.matcher(langString);
            if (matcher.find())
            {
                String template = "*T" + matcher.group(1);
                String templatePattern = "\\*T" + matcher.group(1);
                langString = langString.replaceAll(templatePattern, t(template));
            } else
            {
                break;
            }
        }
        return langString;
    }

    public static Locale getDefaultApplicationLocale(String languageTag)
    {
        Locale appLocale;
        if (languageTag == null || languageTag.length() == 0)
        {
            appLocale = Locale.getDefault();
        } else
        {
            String[] languageElements = languageTag.split("-");
            switch (languageElements.length)
            {
                case 1:
                    appLocale = new Locale(languageElements[0]);
                    break;
                case 2:
                    appLocale = new Locale(languageElements[0], languageElements[1]);
                    break;
                case 3:
                    appLocale = new Locale(languageElements[0], languageElements[1],
                            languageElements[2]);
                    break;
                default:
                    appLocale = Locale.getDefault();
                    break;
            }
        }
        
        return appLocale;
    }

    public static Locale getApplicationLocale()
    {
        return applicationLocale;
    }

    public static void setApplicationLocale(Locale locale)
    {
        applicationLocale = locale;
    }

    public static String getApplicationInstallDirectory()
    {
        return applicationInstallDirectory;
    }

    public static void setApplicationInstallDirectory(String directory)
    {
        applicationInstallDirectory = directory;
    }

    public static void loadMessages(String installDirectory, Locale appLocale)
    {
        applicationInstallDirectory = installDirectory;
        applicationLocale = appLocale;
        if (applicationLocale == null)
            applicationLocale = Locale.getDefault();

        i18nbundle = null;
        try
        {
            i18nbundle = (LanguagePropertiesResourceBundle)ResourceBundle.getBundle("celuk.language.languagedata.LanguageData", applicationLocale);
        }
        catch (Exception ex)
        {
            i18nbundle = null;
        }

        if (i18nbundle == null)
        {
            applicationLocale = Locale.ENGLISH;
            i18nbundle = (LanguagePropertiesResourceBundle)ResourceBundle.getBundle("celuk.language.languagedata.LanguageData", applicationLocale);
        }
    }
    
    public static Set<Locale> getAvailableLocales()
    {
        if (i18nbundle != null)
            return i18nbundle.getAvailableLocales();
        else
            return null;
    }
}
