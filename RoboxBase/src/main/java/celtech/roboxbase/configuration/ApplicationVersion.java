package celtech.roboxbase.configuration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author George Salter
 */
public class ApplicationVersion implements Comparable<ApplicationVersion>
{
    private String versionString;
    
    private int majorVersion;
    private int minorVersion;
    private int patchVersion;
    
    private String nonNumericVersion;
    
    //private boolean hasVersion = false;
    
    public ApplicationVersion(String versionString)
    {
        this.versionString = versionString;
        parseVersionString(versionString);
    }
    
    private void parseVersionString(String versionString)
    {
        Pattern versionNumberPattern = Pattern.compile("^[0-9]+\\.[0-9]+\\.[0-9]+");
        Matcher m = versionNumberPattern.matcher(versionString);
        
        if (m.find())
        {
            String versionNumber = m.group();
            String[] versions = versionNumber.split("\\.");
            majorVersion = Integer.parseInt(versions[0]);
            minorVersion = Integer.parseInt(versions[1]);
            patchVersion = Integer.parseInt(versions[2]);
            nonNumericVersion = versionString.replace(versionNumber, "");
            //hasVersion = true;
        } else
        {
            nonNumericVersion = versionString;
           // hasVersion = false;
        }
    }

    @Override
    public int compareTo(ApplicationVersion o) 
    {
        if (versionString.equals(o.getVersionString()))
        {
            return 0;
        }
        
        int majorComparison = Integer.compare(majorVersion, o.getMajorVersion());
        if (majorComparison == 0)
        {
            int minorComparison = Integer.compare(minorVersion, o.getMinorVersion());
            if (minorComparison == 0)
            {
                int patchComparison = Integer.compare(patchVersion, o.getPatchVersion());
                if (patchComparison == 0)
                {
                    // Make sure any true version takes precedence over a development version.
                    if (nonNumericVersion.equals(""))
                    {
                        return 1;
                    }
                    if (o.getNonNumericVersion().equals(""))
                    {
                        return -1;
                    }
                    else
                    {
                        return nonNumericVersion.compareTo(o.getNonNumericVersion());
                    }
                } else
                {
                    return patchComparison;
                }
            } else
            {
                return minorComparison;
            }
        } else
        {
            return majorComparison;
        }
    }

    public String getVersionString()
    {
        return versionString;
    }
    
    public int getMajorVersion() 
    {
        return majorVersion;
    }

    public int getMinorVersion() 
    {
        return minorVersion;
    }

    public int getPatchVersion() 
    {
        return patchVersion;
    }

    public String getNonNumericVersion() 
    {
        return nonNumericVersion;
    }    
    
    @Override
    public String toString()
    {
        return versionString;
    }
}
