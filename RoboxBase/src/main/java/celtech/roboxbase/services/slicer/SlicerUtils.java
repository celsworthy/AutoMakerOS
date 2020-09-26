package celtech.roboxbase.services.slicer;

import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.MachineType;
import celtech.roboxbase.configuration.SlicerType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author George Salter
 */
public class SlicerUtils 
{
    public static final Stenographer STENO = StenographerFactory.getStenographer(SlicerUtils.class.getName());
    
    public static void killSlicing(SlicerType slicerType)
    {
        String windowsKillCommand = "";
        String macKillCommand = "";
        String linuxKillCommand = "";

        switch (slicerType)
        {
            case Slic3r:
                break;
            case Cura:
            case Cura4:
                windowsKillCommand = "taskkill /IM \"CuraEngine.exe\" /F";
                macKillCommand = "./KillCuraEngine.mac.sh";
                linuxKillCommand = "./KillCuraEngine.linux.sh";
                break;
        }
        
        MachineType machineType = BaseConfiguration.getMachineType();
        List<String> commands = new ArrayList<>();
        
        switch (machineType)
        {
            case WINDOWS_95:
                commands.add("command.com");
                commands.add("/S");
                commands.add("/C");
                commands.add(windowsKillCommand);
                break;
            case WINDOWS:
                commands.add("cmd.exe");
                commands.add("/S");
                commands.add("/C");
                commands.add(windowsKillCommand);
                break;
            case MAC:
                commands.add(macKillCommand);
                break;
            case LINUX_X64:
            case LINUX_X86:
                commands.add(linuxKillCommand);
                break;
        }
        
        if (!commands.isEmpty())
        {
            ProcessBuilder killSlicerProcessBuilder = new ProcessBuilder(commands);
            if (machineType != MachineType.WINDOWS && machineType != MachineType.WINDOWS_95)
            {
                String binDir = BaseConfiguration.getBinariesDirectory();
                STENO.debug("Set working directory (Non-Windows) to " + binDir);
                killSlicerProcessBuilder.directory(new File(binDir));
            }
            try 
            {       
                Process slicerKillProcess = killSlicerProcessBuilder.start();
                slicerKillProcess.waitFor();
            } catch (IOException | InterruptedException ex) 
            {
                STENO.exception("Exception whilst killing slicer", ex);
            }
        }
    }
}
