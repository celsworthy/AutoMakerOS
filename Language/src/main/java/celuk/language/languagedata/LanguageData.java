package celuk.language.languagedata;

import celuk.language.I18n;
import celuk.language.LanguagePropertiesResourceBundle;

/**
 *
 * @author ianhudson
 */
public class LanguageData extends LanguagePropertiesResourceBundle
{
    
    public LanguageData()
    {
        super(I18n.getApplicationInstallDirectory(), "Language", "LanguageData");
    }
}
