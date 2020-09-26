package celtech.roboxbase.printerControl.comms.commands;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author Ian
 */
public class FilenameStartsWithFilter implements FilenameFilter
{

    private final String baseFilename;
    private final int baseLength;

    public FilenameStartsWithFilter(String baseFilename)
    {
        this.baseFilename = baseFilename.toUpperCase();
        baseLength = this.baseFilename.length();
    }

    @Override
    public boolean accept(File dir, String name)
    {
        String n = name.toUpperCase();
        return (n.startsWith(baseFilename) &&
               (n.length() == baseLength ||
                n.charAt(baseLength) == '.' ||
                n.charAt(baseLength) == '#'));
    }
}
