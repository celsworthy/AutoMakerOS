package celuk.gcodeviewer.engine;

import celuk.gcodeviewer.entities.Entity;
import celuk.gcodeviewer.gcode.GCodeLine;
import celuk.gcodeviewer.gcode.GCodeLineParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.parboiled.Parboiled;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

/**
 * Process a GCode file line by line.
 * 
 * @author Tony Aldhous
 */
public class GCodeProcessor {
    
    private static final Stenographer STENO = StenographerFactory.getStenographer(GCodeProcessor.class.getName());
    
    int numberOfBottomLayer = Entity.NULL_LAYER;
    int numberOfTopLayer = Entity.NULL_LAYER;
    
    List<String> lines = new ArrayList<>();
    // Settings can be included in the GCode comments, and can be used to "tweak" the generated view.
    // For example, the infill width and thickness are used to correct the appearance of thick
    // infill layers.
    Map<String, Double> settingsMap = new HashMap<>(); // Settings read from the GCode comments.
        
    /**
     * Read G-Code file from the given file path passing each line to the consumer.
     * 
     * @param filePath path to the file to be processed
     * @return boolean indicating success or failure.
     */
    public boolean processFile(String filePath, GCodeConsumer consumer) {
        boolean success = false;
        try {
            FileReader fileReader = new FileReader(new File(filePath));
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            GCodeLineParser gCodeParser = Parboiled.createParser(GCodeLineParser.class);
            gCodeParser.setSettingsMap(settingsMap);
            
            ReportingParseRunner runner = new ReportingParseRunner<>(gCodeParser.Line());
            consumer.reset();
            int lineNumber = -1;
            for (String lineRead = bufferedReader.readLine(); lineRead != null; lineRead = bufferedReader.readLine()) {
                ++lineNumber;
                lineRead = lineRead.trim();
                lines.add(lineRead);
                if (!lineRead.isEmpty())
                {
                    gCodeParser.resetLine();
                    ParsingResult result = runner.run(lineRead);
                    if (result.hasErrors() || !result.matched) {
                        String errorReport = "Parsing failure on line " + lineNumber + ": ";
                        if (result.hasErrors())
                            errorReport += ErrorUtils.printParseErrors(result);
                        else
                        errorReport += "no match.";
                        STENO.error(errorReport);
                        throw new RuntimeException(errorReport);
                    } else {
                        GCodeLine line = gCodeParser.getLine();
                        line.lineNumber = lineNumber;
                        if (line.layerNumber > Entity.NULL_LAYER) {
                            if (numberOfTopLayer == Entity.NULL_LAYER || numberOfTopLayer < line.layerNumber)
                                numberOfTopLayer = line.layerNumber;
                            if (numberOfBottomLayer == Entity.NULL_LAYER || numberOfBottomLayer > line.layerNumber)
                                numberOfBottomLayer = line.layerNumber;
                        }
                        consumer.processLine(line);
                        //STENO.info("Read line" + Integer.toString(line.lineNumber));
                    }
                }
            }
            consumer.complete();
            STENO.info("Parsed " + Integer.toString(lineNumber) + " lines.");
            success = true;
        } catch (IOException ex) {
            STENO.error("IO exception when attempting to parse file: " + filePath);
            STENO.error(ex.toString());
        }
        
        return success;
    }

    /**
     * Read G-Code file from the given file path, returning it a list of GCodeLines.
     * 
     * @param filePath path to the file to be loaded
     * @return boolean indicating success or failure.
     */
    public List<GCodeLine> loadFile(String filePath) {
        boolean success = false;
        List<GCodeLine> lines = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(new File(filePath));
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            GCodeLineParser gCodeParser = Parboiled.createParser(GCodeLineParser.class);
            ReportingParseRunner runner = new ReportingParseRunner<>(gCodeParser.Line());
            int lineNumber = -1;
            for (String lineRead = bufferedReader.readLine(); lineRead != null; lineRead = bufferedReader.readLine()) {
                ++lineNumber;
                lineRead = lineRead.trim();
                if (!lineRead.isEmpty())
                {
                    gCodeParser.resetLine();
                    ParsingResult result = runner.run(lineRead);
                    if (result.hasErrors() || !result.matched) {
                        String errorReport = "Parsing failure on line " + lineNumber + ": ";
                        if (result.hasErrors())
                            errorReport += ErrorUtils.printParseErrors(result);
                        else
                        errorReport += "no match.";
                        STENO.error(errorReport);
                        throw new RuntimeException(errorReport);
                    } else {
                        GCodeLine line = gCodeParser.getLine();
                        line.lineNumber = lineNumber;
                        if (lines.isEmpty() ||numberOfTopLayer < line.layerNumber)
                            numberOfTopLayer = line.layerNumber;
                        if (lines.isEmpty() || numberOfBottomLayer > line.layerNumber)
                            numberOfBottomLayer = line.layerNumber;
                        lines.add(line);
                        //STENO.info("Read line" + Integer.toString(line.lineNumber));
                    }
                }
            }
            STENO.info("Parsed " + Integer.toString(lineNumber) + " lines.");
            success = true;
        } catch (IOException ex) {
            STENO.error("IO exception when attempting to parse file: " + filePath);
            STENO.error(ex.toString());
        }
        
        if (!success)
            lines.clear();
        
        return lines;
    }
    
    public int getNumberOfTopLayer()
    {
        return numberOfTopLayer;
    }
    
    public int getNumberOfBottomLayer()
    {
        return numberOfBottomLayer;
    }

    public List<String> getLines()
    {
        return lines;
    }

    public Map<String, Double> getSettings()
    {
        return settingsMap;
    }
}
