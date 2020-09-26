package celtech.roboxbase.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian & George Salter
 */
public class ScriptUtils
{
    public static final Stenographer STENO = StenographerFactory.getStenographer(ScriptUtils.class.getName());
    
    public static String runScript(String pathToScript, int timeout, String... parameters)
    {
        List<String> command = new ArrayList<>();
        command.add(pathToScript);

        command.addAll(Arrays.asList(parameters));

        String c = new String();
        for(String s : command)
            c = c + " " + s;
        STENO.debug("Running script \"" + c + "\"");

        ProcessBuilder builder = new ProcessBuilder(command);

        String data = "";
            
        try
        {
            Process scriptProcess = builder.start();
            StringConsumer outputConsumer = new StringConsumer(scriptProcess.getInputStream());
            outputConsumer.start();
            if (timeout > 0) {
                if (scriptProcess.waitFor(timeout, TimeUnit.SECONDS)) {
                    if (scriptProcess.exitValue() == 0)
                        data = outputConsumer.getString();
                    else
                        STENO.error("Script error");
                }
                else {
                    STENO.error("Script timeout");
                    scriptProcess.destroyForcibly();
                }
            }
            else if (scriptProcess.waitFor() == 0)
                data = outputConsumer.getString();
            else
                STENO.error("Script error");
       } 
        catch (IOException | InterruptedException ex)
        {
            STENO.error("Error " + ex);
        }

        return data;
    }

    public static byte[] runByteScript(String pathToScript, int timeout, String... parameters)
    {
        List<String> command = new ArrayList<>();
        command.add(pathToScript);
        command.addAll(Arrays.asList(parameters));

        String c = new String();
        for(String s : command)
            c = c + " " + s;
        STENO.debug("Running script(B) \"" + c + "\"");
            
        ProcessBuilder builder = new ProcessBuilder(command);
        byte[] data = null;

        STENO.debug("Reading script output");
        try {
            Process scriptProcess = builder.start();
            ByteConsumer outputConsumer = new ByteConsumer(scriptProcess.getInputStream());
            outputConsumer.start();
            if (timeout > 0) {
                if (scriptProcess.waitFor(timeout, TimeUnit.SECONDS)) {
                    if (scriptProcess.exitValue() == 0)
                        data = outputConsumer.getBytes();
                    else
                        STENO.error("Byte script error");
                }
                else {
                    STENO.error("Byte script timeout");
                    scriptProcess.destroyForcibly();
                }
            }
            else if (scriptProcess.waitFor() == 0)
                data = outputConsumer.getBytes();
            else
                STENO.error("Byte script error");
        } 
        catch (IOException | InterruptedException ex)
        {
            STENO.error("Error " + ex);
        }

        return data;
    }
}