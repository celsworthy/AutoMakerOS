package libertysystems.configuration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;

/**
 *
 * @author Ian Hudson
 * Liberty Systems Limited
 */
public class Configuration
{

    private final String configFilePropertyName = "libertySystems.configFile";
    private final String config_componentPrefix = "Components";
    private String configFileName = null;
    private boolean configLoaded = false;
    private static Configuration instance = null;
    private XMLConfiguration config = null;
    private final String userHomeReference = "\\$USER_HOME\\$";
    private final String appdataReference = "\\$APPDATA_HOME\\$";
    private final String configReferenceEnclosure = "#";
    private final String configReferenceSeparator = ":";
    private String userHomeDirectory = null;
    private String appdataDirectory = null;

    public static Configuration getInstance() throws ConfigNotLoadedException
    {
        if (instance == null)
        {
            instance = new Configuration();
        }

        return instance;
    }

    private Configuration() throws ConfigNotLoadedException
    {
        if (System.getProperty("os.name").startsWith("Windows"))
        {
            appdataDirectory = System.getenv("APPDATA");
            String registryValue = WindowsRegistry.currentUser("Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", "Personal");
            if (registryValue != null)
            {
                Path regPath = Paths.get(registryValue);
                if (Files.exists(regPath, LinkOption.NOFOLLOW_LINKS))
                {
                    userHomeDirectory = registryValue;
                }
            }
            if (userHomeDirectory == null)
                userHomeDirectory = System.getProperty("user.home");
        } else
        {
            appdataDirectory = System.getProperty("user.home");
            userHomeDirectory = appdataDirectory;
        }

        // The following two lines were removed as there was no comment explaining why
        // they were needed, and it stops network directories (which start with a double backslash)
        // from being used on Windows.
        //userHomeDirectory = userHomeDirectory.replaceAll("\\\\", "/");
        //appdataDirectory = appdataDirectory.replaceAll("\\\\", "/");

        configFileName = System.getProperty(configFilePropertyName);
        try
        {
            config = new XMLConfiguration(configFileName);
            config.setExpressionEngine(new XPathExpressionEngine());
            configLoaded = true;

        } catch (ConfigurationException ex)
        {
            // Can't use stenographer as this would give us a circular dependency
            System.err.println(">>>> ERROR loading system config (using -D" + configFilePropertyName + ") " + ex);
            throw new ConfigNotLoadedException(">>>> ERROR loading system config (using -D" + configFilePropertyName + ") " + ex.getMessage());
        }
    }

    public boolean isSystemConfigLoaded()
    {
        return configLoaded;
    }

    private String getComponentReference(String componentName, String configItem)
    {
        return componentName + "/" + configItem;
    }

    public String getString(String componentName, String configItem) throws ConfigNotLoadedException, ConfigItemIsAnArray
    {
        String returnVal = null;

        if (isSystemConfigLoaded())
        {
            List<HierarchicalConfiguration> nodes = config.configurationsAt(getComponentReference(componentName, configItem));
            if (nodes.size() > 1)
            {
                throw new ConfigItemIsAnArray(configItem + " in component " + componentName + " is an array, not a single item.");
            }

            Iterator<HierarchicalConfiguration> it = nodes.iterator();
            HierarchicalConfiguration node = it.next();

            String rawConfigValue = (String) node.getRootNode().getValue();

            returnVal = replaceReferences(componentName, rawConfigValue);

        }
        else
        {
            throw new ConfigNotLoadedException("System configuration unavailable");
        }

        return returnVal;
    }

    private String replaceReferences(String componentName, String configValue)
    {
        if (configValue != null)
        {
            int foundSeparatorAt = -1;

            while ((foundSeparatorAt = configValue.indexOf(configReferenceEnclosure, 0)) != -1)
            {
                //Find the matching separator
                int matchingSeparator = configValue.indexOf(configReferenceEnclosure, foundSeparatorAt + 1);
                if (matchingSeparator == -1)
                {
                    break;
                } else
                {
                    String reference = configValue.substring(foundSeparatorAt + 1, matchingSeparator);
                    String[] elements = reference.split(configReferenceSeparator);
                    String refComponentName = null;
                    String refConfigItem = null;

                    switch (elements.length)
                    {
                        case 1:
                            //Hmm - not sure we should allow non-fully-qualified references... probably won't work properly...
                            refComponentName = componentName;
                            refConfigItem = elements[0];
                            break;
                        case 2:
                            refComponentName = elements[0];
                            refConfigItem = elements[1];
                            break;
                        default:
                            break;
                    }

                    if (refComponentName != null && refConfigItem != null)
                    {
                        StringBuilder modifiedOutput = new StringBuilder();

                        if (foundSeparatorAt != 0)
                        {
                            modifiedOutput.append(configValue.substring(0, foundSeparatorAt));
                        }
                        modifiedOutput.append(config.getString(getComponentReference(refComponentName, refConfigItem)));
                        if (matchingSeparator != configValue.length() - 1)
                        {
                            modifiedOutput.append(configValue.substring(matchingSeparator + 1, configValue.length()));
                        }
                        configValue = modifiedOutput.toString();
                    }
                }
            }

            //Deal with the user's home directory...
            configValue = configValue.replaceAll(userHomeReference, Matcher.quoteReplacement(userHomeDirectory));
            configValue = configValue.replaceAll(appdataReference, Matcher.quoteReplacement(appdataDirectory));
        }

        return configValue;
    }

    public String getFilenameString(String componentName, String configItem, String defaultValue) throws ConfigNotLoadedException
    {
        String retVal = getString(componentName, configItem, defaultValue);

        return retVal;
    }

    public String getString(String componentName, String configItem, String defaultValue) throws ConfigNotLoadedException
    {
        String returnval = null;

        if (isSystemConfigLoaded())
        {
            String rawConfigValue = config.getString(getComponentReference(componentName, configItem));

            returnval = replaceReferences(componentName, rawConfigValue);

            if (returnval == null)
            {
                returnval = defaultValue;
            }
        } else if (configFileName != null && configFileName.equalsIgnoreCase("$test$"))
        {
            // Test mode
            returnval = defaultValue;
        }
        else
        {
            throw new ConfigNotLoadedException("System configuration unavailable");
        }

        return returnval;
    }

    public boolean setString(String componentName, String configItem, String newValue) throws ConfigNotLoadedException
    {
        boolean returnval = false;

        if (isSystemConfigLoaded())
        {
            config.setProperty(getComponentReference(componentName, configItem), newValue);
            returnval = true;
        } else
        {
            throw new ConfigNotLoadedException("System configuration unavailable");
        }

        return returnval;
    }

    public String[] getStringArray(String componentName, String configItem) throws ConfigNotLoadedException
    {
        String[] returnVal = null;

        if (isSystemConfigLoaded())
        {
            returnVal = config.getStringArray(getComponentReference(componentName, configItem));
        } else
        {
            throw new ConfigNotLoadedException("System configuration unavailable");
        }

        return returnVal;
    }

    public boolean getBoolean(String componentName, String configItem) throws ConfigNotLoadedException
    {
        boolean returnval = false;

        if (isSystemConfigLoaded())
        {
            returnval = config.getBoolean(getComponentReference(componentName, configItem));
        } else
        {
            throw new ConfigNotLoadedException("System configuration unavailable");
        }

        return returnval;
    }

    public int getInt(String componentName, String configItem) throws ConfigNotLoadedException
    {
        int returnval = -1;

        if (isSystemConfigLoaded())
        {
            returnval = config.getInt(getComponentReference(componentName, configItem));
        } else
        {
            throw new ConfigNotLoadedException("System configuration unavailable");
        }

        return returnval;
    }

    public String[] getComponents() throws ConfigNotLoadedException
    {
        String[] returnVal = null;

        if (isSystemConfigLoaded())
        {
            List<HierarchicalConfiguration> nodes = config.configurationsAt("/*");
            Iterator<HierarchicalConfiguration> it = nodes.iterator();
            returnVal = new String[nodes.size()];

            int i = 0;

            while (it.hasNext())
            {
                HierarchicalConfiguration node = it.next();
                returnVal[i] = node.getRootNode().getName();
                i++;
            }

        } else
        {
            throw new ConfigNotLoadedException("System configuration unavailable");
        }

        return returnVal;
    }

    public String[] getConfigItems(String componentName) throws ConfigNotLoadedException
    {
        HashSet tempStore = new HashSet();
        String[] returnVal = null;

        if (isSystemConfigLoaded())
        {
            List<HierarchicalConfiguration> nodes = config.configurationsAt(componentName + "/*");
            Iterator<HierarchicalConfiguration> it = nodes.iterator();

            while (it.hasNext())
            {
                HierarchicalConfiguration node = it.next();
                tempStore.add(node.getRootNode().getName());
            }

            returnVal = (String[]) tempStore.toArray(new String[0]);

        } else
        {
            throw new ConfigNotLoadedException("System configuration unavailable");
        }

        return returnVal;
    }

    public void addConfigurationListener(String componentName, ConfigurationListener listener)
    {

        config.addConfigurationListener(listener);
    }

    public static void main(String[] args)
    {
        try
        {
            Configuration configuration = Configuration.getInstance();
            System.out.println("Appdata dir is " + configuration.appdataDirectory + " user dir is " + configuration.userHomeDirectory);
        } catch (ConfigNotLoadedException ex)
        {
            System.err.println("Exception: " + ex);
        }

    }
}
