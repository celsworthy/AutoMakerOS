package celtech.roboxbase.configuration;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author ianhudson
 */
public class HeadFileFilter implements FileFilter
{

    /**
     *
     * @param pathname
     * @return
     */
    @Override
    public boolean accept(File pathname)
    {
        if (pathname.getName().endsWith(BaseConfiguration.headFileExtension))
        {
            return true;
        } else
        {
            return false;
        }
    }

}
