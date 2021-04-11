/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celuk.gcodeviewer.comms;

import celuk.gcodeviewer.engine.RenderParameters;
import celuk.gcodeviewer.engine.RenderingEngine;
import celuk.gcodeviewer.entities.Entity;
import java.util.Scanner;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.joml.Vector3f;

/**
 *
 * @author Tony
 */
public class CommandHandler {
    private final static int MAX_COMMAND_COUNT = 10;
    private final static Stenographer STENO = StenographerFactory.getStenographer(CommandHandler.class.getName());
    private final CommandQueue commandQueue;
    private RenderParameters renderParameters;
    private RenderingEngine renderingEngine;

    public CommandHandler() {
        this.commandQueue = new CommandQueue();
        this.renderParameters = null;
        this.renderingEngine = null;
    }
    
    public void start() {
        // Command handler is not a thread because most of it's actions are fast and affect the rendering engine directly,
        // which can only be changed safely on the main thread. Any actions that do take a long time (e.g. loading a GCode file)
        // should be done on a separate thread
        STENO.debug("Starting command handler.");
        commandQueue.start(); 
    }
     
    public void stop() {
        commandQueue.stopRunning();
    }
    
    public void setRenderParameters(RenderParameters renderParameters) {
       this.renderParameters = renderParameters; 
    }

    public void setRenderingEngine(RenderingEngine renderingEngine) {
       this.renderingEngine = renderingEngine; 
    }
    
    public boolean processCommands() {
        for (int commandCount = 0; commandCount < MAX_COMMAND_COUNT && commandQueue.commandAvailable(); ++commandCount) {
            String command = commandQueue.getNextCommandFromQueue().trim();
            STENO.debug("Processing command " + command);
            if (command.equalsIgnoreCase("q") || command.equalsIgnoreCase("quit")) {
                return true;
            }
            else {
                try {
                    String commandParameter;
                    Scanner commandScanner = new Scanner(command);
                    if (commandScanner.hasNext()) {
                        String commandWord = commandScanner.next().toLowerCase();
                        switch (commandWord) {
                            case "bottom":
                            case "b":
                                if (commandScanner.hasNextInt())
                                    renderParameters.setBottomLayerToRender(commandScanner.nextInt());
                                else
                                    renderParameters.setBottomLayerToRender(renderParameters.getIndexOfBottomLayer());
                                break;

                            case "clear":
                            case "cl":
                                renderingEngine.clearGCode();
                                break;
                                
                            case "colour":
                            case "co":
                                processColourCommand(command, commandScanner);
                                break;

                            case "extruder-letter":
                            case "ex":
                                if (commandScanner.hasNext()) {
                                    String letter = commandScanner.next().toUpperCase();
                                    String newLetter = letter;
                                    if (commandScanner.hasNext())
                                        newLetter = commandScanner.next().trim().toUpperCase();
                                    if (newLetter.length() == 1 &&
                                        newLetter.matches("[CDEGHIJKLMNOPQRSTUVW]"))
                                    {
                                        if (letter.equalsIgnoreCase("D")) {
                                            renderingEngine.setExtruderLetterD(newLetter);
                                        }
                                        else if (letter.equalsIgnoreCase("E")) {
                                             renderingEngine.setExtruderLetterE(newLetter);   
                                        }
                                        else {
                                            System.out.println("Invalid extruder letter in command " + command);
                                        }
                                    }
                                }
                                else {
                                    System.out.println("No extruder letter in command " + command);
                                }
                                break;

                            case "first":
                            case "fi":
                                if (commandScanner.hasNextInt())
                                    renderParameters.setFirstSelectedLine(commandScanner.nextInt());
                                else
                                    renderParameters.setFirstSelectedLine(0);
                                break;

                            case "focus":
                            case "fo":
                                renderParameters.setWindowAction(RenderParameters.WindowAction.WINDOW_FOCUS);
                                break;

                            case "hide":
                            case "h":
                                processHideCommand(command, commandScanner);
                                break;

                            case "iconify":
                            case "i":
                                renderParameters.setWindowAction(RenderParameters.WindowAction.WINDOW_ICONIFY);
                                break;
                                    
                            case "last":
                            case "la":
                                if (commandScanner.hasNextInt())
                                    renderParameters.setLastSelectedLine(commandScanner.nextInt());
                                else
                                    renderParameters.setLastSelectedLine(renderParameters.getNumberOfLines());
                                break;

                            case "load":
                            case "lo":
                                if (commandScanner.hasNext())
                                {
                                    String filePath = commandScanner.nextLine().trim();
                                    if (filePath.length() > 2 && filePath.startsWith("\"") && filePath.endsWith("\""))
                                        filePath = filePath.substring(1, filePath.length() - 1);
                                    renderingEngine.startLoadingGCodeFile(filePath);
                                }
                                else
                                    renderingEngine.startLoadingGCodeFile(renderingEngine.getCurrentFilePath());
                                break;

                            case "nozzle-valves":
                            case "nv":
                                if (commandScanner.hasNext()) {
                                    String state = commandScanner.next().toUpperCase();
                                    if (state.equals("ON")) {
                                        renderingEngine.setHasNozzleValves(true);
                                    }
                                    else if (state.equals("OFF")) {
                                        renderingEngine.setHasNozzleValves(false);
                                    }
                                }                                
                                break;

                            case "printer":
                            case "p":
                                String printerType = commandScanner.next().toUpperCase();
                                renderingEngine.setPrinterType(printerType);
                                break;

                            case "restore":
                            case "r":
                                renderParameters.setWindowAction(RenderParameters.WindowAction.WINDOW_RESTORE);
                                break;

                            case "show":
                            case "s":
                                processShowCommand(command, commandScanner);
                                break;

                            case "tool":
                            case "to":
                                processToolCommand(command, commandScanner);
                                break;

                            case "top":
                            case "t":
                                if (commandScanner.hasNextInt())
                                    renderParameters.setTopLayerToRender(commandScanner.nextInt());
                                else
                                    renderParameters.setTopLayerToRender(renderParameters.getIndexOfTopLayer());
                                break;

                            default:
                                STENO.error("Ignoring command " + command);
                                break;
                        }
                    }
                }
                catch (RuntimeException ex) {
                    STENO.exception("Command parsing error", ex);
                }
            }
        }
        
        return false;
    }
    
    private void processColourCommand(String command, Scanner commandScanner) {
        String commandParameter = commandScanner.next().toLowerCase();
        switch (commandParameter) {
            case "type":
            case "ty":
                renderParameters.setColourMode(RenderParameters.ColourMode.COLOUR_AS_TYPE);
                break;

            case "tool":
            case "to":
                if (commandScanner.hasNextInt()) {
                    int toolIndex = commandScanner.nextInt();
                    processColourToolSubCommand(command, commandScanner, toolIndex);
                }
                else if (!commandScanner.hasNext()) {
                    renderParameters.setColourMode(RenderParameters.ColourMode.COLOUR_AS_TOOL);
                }
                else {
                    System.out.println("Unrecognised option in command " + command);
                }
                break;

            case "data":
            case "d":
                int dataIndex = -1;
                if (commandScanner.hasNextInt()) {
                    dataIndex = commandScanner.nextInt();
                    if (dataIndex < 0 || dataIndex >= Entity.N_DATA_VALUES)
                        dataIndex = -1;
                }
                else {
                    commandParameter = commandScanner.next().toLowerCase();
                    switch (commandParameter)
                    {
                        case "a":
                            dataIndex = 0;
                            break;
                        case "b":
                            dataIndex = 1;
                            break;
                        case "d":
                            dataIndex = 2;
                            break;
                        case "e":
                            dataIndex = 3;
                            break;
                        case "f":
                            dataIndex = 4;
                            break;
                        case "x":
                            dataIndex = 5;
                            break;
                        case "y":
                            dataIndex = 6;
                            break;
                        case "z":
                            dataIndex = 7;
                            break;
                        default:
                            break;
                    }
                }
                if (dataIndex >= 0)
                {
                    renderingEngine.colourSegmentsFromData(dataIndex);
                    renderingEngine.reloadSegmentColours();
                    renderParameters.setColourMode(RenderParameters.ColourMode.COLOUR_AS_DATA);
                }
                break;


            default:
                System.out.println("Unrecognised colour mode in command " + command);
        }
    }
    
    private void processColourToolSubCommand(String command, Scanner commandScanner, int toolIndex) {
            float r = -1.0f;
            float g = -1.0f;
            float b = -1.0f;
            if (commandScanner.hasNextFloat()) {
                r = commandScanner.nextFloat();
            }
            if (commandScanner.hasNextFloat()) {
                g = commandScanner.nextFloat();
            }
            if (commandScanner.hasNextFloat()) {
                b = commandScanner.nextFloat();
            }
            if (!commandScanner.hasNext() &&
                toolIndex >= 0 && toolIndex < renderParameters.getToolColours().size() &&
                r >= 0.0f && r <= 1.0f &&
                g >= 0.0f && g <= 1.0f &&
                b >= 0.0f && b <= 1.0f)
            {
                renderParameters.setColourForTool(toolIndex, new Vector3f(r, g, b));
            }
            else {
                System.out.println("Unrecognised option in colour command " + command);
            }
    }
    
    private void processHideCommand(String command, Scanner commandScanner) {
        String  commandParameter = commandScanner.next().toLowerCase();
        switch (commandParameter) {
            case "angles":
            case "a":
                renderParameters.setShowAngles(false);
                break;

            case "moves":
            case "m":
                renderParameters.setShowMoves(false);
                break;

            case "stylus":
            case "s":
                renderParameters.setShowStylus(false);
                break;

            case "tool":
            case "t":
                if (commandScanner.hasNextInt())
                    renderParameters.setShowFlagForTool(commandScanner.nextInt(), false);
                else
                   STENO.error("Unrecognised option in command " + command);
                break;

            case "window":
            case "w":
                renderParameters.setWindowAction(RenderParameters.WindowAction.WINDOW_HIDE);
                break;

            default:
                STENO.error("Unrecognised option in command " + command);
        }
    }

    private void processShowCommand(String command, Scanner commandScanner) {
        String  commandParameter = commandScanner.next().toLowerCase();
        switch (commandParameter) {
            case "angles":
            case "a":
                renderParameters.setShowAngles(true);
                break;
                
            case "moves":
            case "m":
                renderParameters.setShowMoves(true);
                break;

            case "stylus":
            case "s":
                renderParameters.setShowStylus(true);
                break;
                
            case "tool":
            case "t":
                if (commandScanner.hasNextInt())
                    renderParameters.setShowFlagForTool(commandScanner.nextInt(), true);
                else
                   STENO.error("Unrecognised option in command " + command);
                break;

            case "window":
            case "w":
                renderParameters.setWindowAction(RenderParameters.WindowAction.WINDOW_SHOW);
                break;

            default:
                STENO.error("Unrecognised option in command " + command);
        }
    }

    private void processToolCommand(String command, Scanner commandScanner) {
        int toolIndex = -1;
        String commandParameter = "";
        double value = -1.0;
        boolean processedOK = false;
        if (commandScanner.hasNextInt())
            toolIndex = commandScanner.nextInt();
        if (commandScanner.hasNext())
            commandParameter = commandScanner.next().toLowerCase();
        if (commandScanner.hasNextDouble())
            value = commandScanner.nextDouble();
        if (toolIndex >= 0) {
            switch (commandParameter) {
                case "colour":
                case "c":
                    processColourToolSubCommand(command, commandScanner, toolIndex);
                    processedOK = true;
                    break;

                case "hide":
                case "h":
                    renderParameters.setShowFlagForTool(toolIndex, false);
                    processedOK = true;
                    break;

                case "show":
                case "s":
                    renderParameters.setShowFlagForTool(toolIndex, true);
                    processedOK = true;
                    break;

                default:
                    break;
            }
        }
        
        if (!processedOK)
            System.out.println("Unrecognised option in command " + command);
    }
}
