package celtech.roboxremote;

import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.postprocessor.PrintJobStatistics;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.collections.ListChangeListener;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author George Salter
 */
public class MountableMediaRegistry 
{
    
    private static final Stenographer STENO = StenographerFactory.getStenographer(MountableMediaRegistry.class.getName());
    
    private static final String ROBOX_GCODE_EXTENSION = "robox.gcode";
    private static final String STATISTICS_EXTENSION = "statistics";
    
    private static MountableMediaRegistry instance;
    
    private final List<File> mountedUSBDriectories = new ArrayList<>();
    
    private final ListChangeListener<? extends File> mountedUSBDirectoryListener 
            = (ListChangeListener.Change<? extends File> change) -> {
        while(change.next()) 
        {
            if(change.wasAdded()) 
            {
                for(File mountedDir : change.getAddedSubList()) 
                {
                    addMountedUSBDirectory(mountedDir);
                }
            } else if (change.wasRemoved()) 
            {
                for(File mountedDir : change.getRemoved()) 
                {
                    removeMountedUSBDirectory(mountedDir);
                }
            }
        }
    };
    
    private MountableMediaRegistry() {
        // First check if there are any directories already
        BaseLookup.MOUNTED_USB_DIRECTORIES.forEach((mountedDir) -> {
            addMountedUSBDirectory(mountedDir);
        });
        BaseLookup.MOUNTED_USB_DIRECTORIES.addListener((ListChangeListener<? super File>) mountedUSBDirectoryListener);
    }
    
    public static MountableMediaRegistry getInstance() 
    {
        if(instance == null) 
        {
            instance = new MountableMediaRegistry();
        }
        
        return instance;
    }
    
    public List<File> getMountedUSBDirectories() 
    {
        return mountedUSBDriectories;
    }
    
    private void addMountedUSBDirectory(File usbDir)
    {
        if(!mountedUSBDriectories.contains(usbDir)) 
        {
            STENO.debug("New mounted directory added at " + usbDir.getPath());
            mountedUSBDriectories.add(usbDir);
        }
    }
    
    private void removeMountedUSBDirectory(File usbDir) 
    {
        if(mountedUSBDriectories.contains(usbDir)) 
        {
            STENO.debug("Mounted directory was removed from " + usbDir.getPath());
            mountedUSBDriectories.remove(usbDir);
        }
    }
    
    public List<PrintJobStatistics> getPrintableProjectStats()
    {
        STENO.debug("Looking through mounted directories for statistics files.");
        
        List<PrintJobStatistics> stats = new ArrayList<>();
         
        mountedUSBDriectories.forEach((usbDir) -> {
            for(File projDir : usbDir.listFiles())
            {
                if(projDir.isDirectory()) 
                {
                    Optional<File> roboxGCodeFile = findRoboxGCodeFile(projDir.listFiles());
                    Optional<PrintJobStatistics> printJobStats = findStatistics(projDir.listFiles());
                    
                    if(roboxGCodeFile.isPresent() && printJobStats.isPresent()) 
                    {
                        STENO.debug("Both Robox GCode and Statistics present. Printable project found.");
                        printJobStats.get().setProjectPath(projDir.getPath() + File.separator);
                        stats.add(printJobStats.get());
                    }
                }
            }
        });
        
        return stats;
    }
    
    /**
     * Find the Robox Gcode file in a list of files
     * 
     * @param files
     * @return An Optional with the Robox GCode file if it exists
     */
    private Optional<File> findRoboxGCodeFile(File[] files) 
    {
        for(File file : files)
        {
            String fileName = file.getName();
            String[] nameParts = fileName.split("_");
            if(nameParts.length > 1 && nameParts[1].equals(ROBOX_GCODE_EXTENSION))
            {
                STENO.debug("Robox GCode found in mounted directory.");
                return Optional.of(file);
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Find the PrintJobStatistics in a list of Files
     * 
     * @param files
     * @return An Optional with the PrintJobStatistics if they exist
     */
    private Optional<PrintJobStatistics> findStatistics(File[] files) 
    {
        for(File file : files) 
        {
            String fileName = file.getName();
            String[] nameAndExtension = fileName.split("\\.");
            if(nameAndExtension.length > 1 
                    && nameAndExtension[1].equals(STATISTICS_EXTENSION)) 
            {
                try 
                {
                    STENO.debug("Statistics file found in mounted directory, attempting import.");
                    return Optional.of(PrintJobStatistics.importStatisticsFromGCodeFile(file.getPath()));
                } 
                catch (IOException ex) 
                {
                    STENO.error("Error discovering statistics file on USB media");
                    return Optional.empty();
                }
            }
        }
        
        return Optional.empty();
    }
}
