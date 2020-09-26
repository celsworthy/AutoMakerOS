/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.services.gcodepreview;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.MachineType;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Tony
 */
public class GCodePreviewTask extends Task<Boolean> {

    private static final Stenographer steno = StenographerFactory.getStenographer(GCodePreviewTask.class.getName());
    private OutputStream stdInStream;
    private final IntegerProperty layerCountProperty = new SimpleIntegerProperty(0);
    private List<String> pendingCommands = null;
    private final String projectDirectory;
    private String printerType;
    private Rectangle2D normalisedScreenBounds;
    
    public GCodePreviewTask(String projectDirectory, String printerType, Rectangle2D normalisedScreenBounds)
    {
        this.projectDirectory = projectDirectory;
        this.printerType = printerTypeOrDefault(printerType);
        this.normalisedScreenBounds = normalisedScreenBounds;
        this.stdInStream = null;
    }

    private String printerTypeOrDefault(String printerType)
    {
        String pt = (printerType != null ? printerType.trim() : "");
        if (pt.isEmpty())
            pt = "DEFAULT";
        
        return pt;
    }

    public IntegerProperty getLayerCountProperty()
    {
        return layerCountProperty;
    }
    
    private void writeToInStream(String command) throws IOException
    {
        steno.debug("Writing command \"" + command + "\"");
        stdInStream.write(command.getBytes());
        stdInStream.write('\n');
    }
    
    public synchronized void writeCommand(String command)
    {
        if (this.stdInStream == null) {
            if (pendingCommands == null)
                pendingCommands = new ArrayList<>();
            pendingCommands.add(command);
        }
        else
        {
            try {
                flushPendingCommands();
                writeToInStream(command);
                stdInStream.flush();
            }
            catch (IOException ex) {
                steno.warning("Failed to write command \"" + command + "\": " + ex.getMessage());
            }
        }
    }

    public synchronized void flushPendingCommands() throws IOException
    {
        if (pendingCommands != null)
        {
            for (int i = 0; i < pendingCommands.size(); ++i)
                writeToInStream(pendingCommands.get(i));
            pendingCommands = null;
        }
    }

    public void loadGCodeFile(String filePath)
    {
        StringBuilder command = new StringBuilder();
        command.append("load ");
        command.append(filePath);
        command.trimToSize();

        writeCommand(command.toString());
    }

    public void setPrinterType(String printerType)
    {
        this.printerType = printerTypeOrDefault(printerType);
        StringBuilder command = new StringBuilder();
        command.append("printer ");
        command.append(this.printerType);
        command.trimToSize();

        writeCommand(command.toString());
    }

    public void setToolColour(int toolIndex, Color colour)
    {
        StringBuilder command = new StringBuilder();
        command.append("colour tool ");
        command.append(Integer.toString(toolIndex));
        command.append(" ");
        command.append(Double.toString(colour.getRed()));
        command.append(" ");
        command.append(Double.toString(colour.getGreen()));
        command.append(" ");
        command.append(Double.toString(colour.getBlue()));
        command.trimToSize();

        writeCommand(command.toString());
    }

    public void setTopLayer(int topLayer)
    {
        StringBuilder command = new StringBuilder();
        command.append("top ");
        command.append(topLayer);
        command.trimToSize();

        writeCommand(command.toString());
    }

    public void setMovesVisible(boolean flag)
    {
        StringBuilder command = new StringBuilder();
        if (flag)
            command.append("show moves");
        else
            command.append("hide moves");
        command.trimToSize();

        writeCommand(command.toString());
    }
    
    public void setStylusMovesVisible(boolean flag)
    {
        StringBuilder command = new StringBuilder();
        if (flag)
            command.append("show stylus");
        else
            command.append("hide stylus");
        command.trimToSize();

        writeCommand(command.toString());
    }
    
    public void clearGCode()
    {
        writeCommand("clear");
    }

    public void giveFocus()
    {
        writeCommand("focus");
    }

    public void terminatePreview()
    {
        if (this.stdInStream != null)
        {
            String command = "q";
            writeCommand(command.toString());
        }
    }

    @Override
    protected Boolean call() throws Exception {
        Boolean succeeded = false;
        ArrayList<String> commands = new ArrayList<>();
        String jvmLocation = System.getProperties().getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        commands.add(jvmLocation);
        if (BaseConfiguration.getMachineType() == MachineType.MAC)
            commands.add("-XstartOnFirstThread");
        commands.add("-DlibertySystems.configFile=" + BaseConfiguration.getGCodeViewerDirectory() + "GCodeViewer.configFile.xml");
        commands.add("-jar");
        commands.add(BaseConfiguration.getGCodeViewerDirectory() + "GCodeViewer.jar");
        //commands.add("-wt");

        String languageTag = BaseConfiguration.getApplicationLocale();
        if (languageTag != null) {
            commands.add("-l");
            commands.add(languageTag);
        }
        
        if (printerType != null) {
            commands.add("-p");
            commands.add(printerType);
        }
 
        if (projectDirectory != null) {
            commands.add("-pd");
            commands.add(projectDirectory);
        }
        
        if (normalisedScreenBounds != null) {
            commands.add("-wn");
            commands.add("-wx");
            commands.add(Double.toString(normalisedScreenBounds.getMinX()));
            commands.add("-wy");
            commands.add(Double.toString(normalisedScreenBounds.getMinY()));
            commands.add("-ww");
            commands.add(Double.toString(normalisedScreenBounds.getWidth()));
            commands.add("-wh");
            commands.add(Double.toString(normalisedScreenBounds.getHeight()));
        }
        
        if (commands.size() > 0)
        {
            steno.debug("GCodePreviewTask command is \"" + String.join(" ", commands) + "\"");
            ProcessBuilder previewProcessBuilder = new ProcessBuilder(commands);
            previewProcessBuilder.redirectErrorStream(true);

            Process previewProcess = null;
            try {
                previewProcess = previewProcessBuilder.start();

                GCodePreviewConsumer outputConsumer = new GCodePreviewConsumer(previewProcess.getInputStream());
                outputConsumer.setLayerCountProperty(layerCountProperty);
                synchronized(this){
                    this.stdInStream =  previewProcess.getOutputStream();
                    try {
                        flushPendingCommands();
                        stdInStream.flush();
                    }
                    catch (IOException ex) {
                        steno.warning("Failed to flush pending commands: " + ex.getMessage());
                    }
                }

                // Start output consumer.
                outputConsumer.start();
                
                int exitStatus = previewProcess.waitFor();
                switch (exitStatus)
                {
                    case 0:
                        steno.debug("GCode previewer terminated successfully ");
                        succeeded = true;
                        break;
                    default:
                        steno.error("Failure when invoking gcode previewer with command line: \"" + String.join(
                                " ", commands) + "\"");
                        steno.error("GCode Previewer terminated with exit code " + exitStatus);
                        break;
                }
            }
            catch (IOException ex) {
                steno.error("Exception whilst running gcode previewer: " + ex);
            } 
            catch (InterruptedException ex) {
                steno.warning("Interrupted whilst waiting for GCode Previewer to complete");
                if (previewProcess != null)
                {
                    previewProcess.destroyForcibly();
                }
            }
        } else
        {
            steno.error("Couldn't run GCode Previewer - no commands for OS ");
        }

        return succeeded;
    }
}
