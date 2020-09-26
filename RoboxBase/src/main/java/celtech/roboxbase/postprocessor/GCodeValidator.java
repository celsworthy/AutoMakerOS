package celtech.roboxbase.postprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class GCodeValidator
{

    private final Stenographer steno = StenographerFactory.getStenographer(GCodeValidator.class.getName());
    private final String gcodeFileToValidate;
    private final String nozzleControlPatternString = ".*B([\\.\\d]+).*";
    private final String extrusionPatternString = ".*G[01].* E[\\.\\d]+.*";
    private final Pattern nozzleControlPattern;
    private final Pattern extrusionPattern;

    public GCodeValidator(String gcodeFileToValidate)
    {
        this.gcodeFileToValidate = gcodeFileToValidate;

        nozzleControlPattern = Pattern.compile(nozzleControlPatternString);
        extrusionPattern = Pattern.compile(extrusionPatternString);
    }

    public boolean validate()
    {
        boolean fileIsValid = false;

        steno.info("Validating GCode " + gcodeFileToValidate);

        File inputFile = new File(gcodeFileToValidate);

        try
        {
            BufferedReader fileReader = new BufferedReader(new FileReader(inputFile));

            boolean nozzleOpen = false;

            String line;
            int lineNumber = 1;
            boolean safeToStartChecking = false;

            double lastNozzleControlValue = 0;

            while ((line = fileReader.readLine()) != null)
            {
                if (safeToStartChecking)
                {

                    Matcher nozzleControlMatcher = nozzleControlPattern.matcher(line);
                    Matcher extrusionMatcher = extrusionPattern.matcher(line);

                    double nozzleControlValue = 0;
                    boolean nozzleControlFound = false;
                    boolean extrusionFound = false;

                    if (nozzleControlMatcher.find())
                    {
                        nozzleControlFound = true;
                        nozzleControlValue = Float.valueOf(nozzleControlMatcher.group(1));
                    }

                    extrusionFound = extrusionMatcher.find();

                    if (nozzleControlFound)
                    {
                        if (nozzleOpen && nozzleControlValue > lastNozzleControlValue)
                        {
                            steno.error("Nozzle opened when it hadn't been closed on line " + lineNumber + " - " + line);
                            fileIsValid = false;
                        }

                        if (nozzleControlValue > 0)
                        {
                            steno.trace("Nozzle open on line " + lineNumber + " - " + line);
                            nozzleOpen = true;
                        } else
                        {
                            steno.trace("Nozzle closed on line " + lineNumber + " - " + line);
                            nozzleOpen = false;
                        }

                        lastNozzleControlValue = nozzleControlValue;
                    }

                    if (extrusionFound)
                    {
                        steno.trace("Extrusion on line " + lineNumber + " - " + line);
                    }

                    if (extrusionFound && !nozzleOpen)
                    {
                        steno.error("Extrusion with closed nozzle on line " + lineNumber + " - " + line);
                        fileIsValid = false;
                    }
                } else
                {
                    if (line.contains("; End of Pre print gcode"))
                    {
                        steno.info("Commencing validation from line " + lineNumber + " - " + line);
                        safeToStartChecking = true;
                        fileIsValid = true;
                    }
                }

                lineNumber++;
            }
        } catch (IOException ex)
        {
            steno.error("Failure to validate GCode file");
        }

        if (fileIsValid)
        {
            steno.info("GCode file " + gcodeFileToValidate + " is valid");
        } else
        {
            steno.warning("GCode file " + gcodeFileToValidate + " is invalid");
        }

        return fileIsValid;
    }
}
