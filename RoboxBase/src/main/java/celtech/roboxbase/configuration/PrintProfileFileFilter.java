package celtech.roboxbase.configuration;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author ianhudson
 */
public class PrintProfileFileFilter implements FileFilter
{

    /**
     *
     * @param pathname
     * @return
     */
    @Override
    public boolean accept(File pathname)
    {
        if (pathname.getName().endsWith(BaseConfiguration.printProfileFileExtension))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
}
