package celuk.language;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public abstract class LanguagePropertiesResourceBundle extends ResourceBundle
{
    private static List<String> subDirectoryListToSearch;
    private static List<String> bundlePrefixList;
    static {
        subDirectoryListToSearch = new ArrayList<>();
        subDirectoryListToSearch.add(".");
        bundlePrefixList = new ArrayList<>();
        bundlePrefixList.add("");
    }
    
    /**
     * The base name for the ResourceBundles to load in.
     */
    private String baseName;

    /**
     * The package name where the properties files should be.
     */
    private Path baseDirectory;

    /**
     * The package name where the properties files should be.
     */
    private String languageFolderName = null;

    
    /**
     * A Map containing the combined resources of all parts building this
     * MultiplePropertiesResourceBundle.
     */
    private Map<String, Object> combined;
    
    private final Set<Locale> availableLocales = new HashSet<>();

    protected static void addBundlePrefix(String prefix)
    {
        if (!bundlePrefixList.contains(prefix))
            bundlePrefixList.add(prefix);
    }

    protected static void addSubDirectoryToSearch(String subDirectory)
    {
        if (!subDirectoryListToSearch.contains(subDirectory))
            subDirectoryListToSearch.add(subDirectory);
    }

    /**
     * Construct a <code>MultiplePropertiesResourceBundle</code> for the passed
     * in base-name.
     *
     * @param baseName the base-name that must be part of the properties file
     * names.
     */
    protected LanguagePropertiesResourceBundle(String baseName)
    {
        this(null, baseName);
    }

    /**
     * Construct a <code>MultiplePropertiesResourceBundle</code> for the passed
     * in base-name.
     *
     * @param packageName the package name where the properties files should be.
     * @param baseName the base-name that must be part of the properties file
     * names.
     */
    protected LanguagePropertiesResourceBundle(String packageName, String baseName)
    {
        this(packageName, "", baseName);
    }

    /**
     * Construct a <code>MultiplePropertiesResourceBundle</code> for the passed
     * in base-name.
     *
     * @param baseDirectory the package name where the properties files should
     * be.
     * @param languageFolderName
     * @param baseName the base-name that must be part of the properties file
     * names.
     */
    protected LanguagePropertiesResourceBundle(String baseDirectory,
            String languageFolderName,
            String baseName)
    {
        this.baseDirectory = Paths.get(baseDirectory).normalize();
        
        this.languageFolderName = languageFolderName;
        this.baseName = baseName;
        
        loadBundlesOnce();
    }
    
    @Override
    public Object handleGetObject(String key)
    {
        if (key == null)
        {
            throw new NullPointerException();
        }
        loadBundlesOnce();
        return combined.get(key);
    }
    
    @Override
    public Enumeration<String> getKeys()
    {
        loadBundlesOnce();
        ResourceBundle parent = this.parent;
        return new ResourceBundleEnumeration(combined.keySet(), (parent != null) ? parent.getKeys()
                : null);
    }
    
    private void addBundleData(Path resourcePath, String resourceName)
    {
        ResourceBundle bundle = null;
        try
        {
            File propFile = resourcePath.toFile();
            
            if (propFile.exists())
            {
                URL[] urlsToSearch =
                {
                    propFile.toURI().toURL()
                };
                URLClassLoader cl = new URLClassLoader(urlsToSearch);
                
                bundle = ResourceBundle.getBundle(resourceName, I18n.getApplicationLocale(), cl, new UTF8Control());
                Enumeration<String> keys = bundle.getKeys();
                String key = null;
                while (keys.hasMoreElements())
                {
                    key = keys.nextElement();
                    combined.put(key, bundle.getObject(key));
                }
            }
        } catch (MalformedURLException ex)
        {
            System.err.println("Failed to load multi-language data");
        }
    }

    /**
     * Load the resources once.
     */
    private void loadBundlesOnce()
    {
        if (combined == null)
        {
            combined = new HashMap<>(128);
            for (String subDirectory : subDirectoryListToSearch)
            {
                Path resourcePath = baseDirectory.resolve(subDirectory).resolve(languageFolderName);
                for (String prefix : bundlePrefixList)
                {
                    try {
                        addBundleData(resourcePath, prefix + baseName);
                    }
                    catch (Exception ex)
                    {
                        //System.out.println("Failed to load messages from \"" + resourcePath + "\" - \"" + prefix + baseName + "\"");
                    }
                }
                addAvailableLocales(resourcePath);
            }
        }
    }
    
    private void addAvailableLocales(Path resourcePath)
    {
        
        File commonDir =  resourcePath.toFile();
        
        availableLocales.add(Locale.ENGLISH);
        
        String[] filenamesToIngest = commonDir.list();
        
        if (filenamesToIngest != null)
        {
            for (String filename : filenamesToIngest)
            {
                filename = filename.toUpperCase().replaceFirst(".PROPERTIES", "");
                // Segment after LanguageData is the locale
                String[] nameParts = filename.split("LANGUAGEDATA_");
                if (nameParts.length > 1)
                {
                    Locale locale = null;
                    String[] languageStringParts = nameParts[1].split("_");
                
                    switch (languageStringParts.length)
                    {
                        case 1:
                            locale = new Locale(languageStringParts[0]);
                            availableLocales.add(locale);
                            break;
                        case 2:
                            locale = new Locale(languageStringParts[0], languageStringParts[1]);
                            availableLocales.add(locale);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
    
    public Set<Locale> getAvailableLocales()
    {
        return availableLocales;
    }
}
