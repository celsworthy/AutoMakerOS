package celtech.roboxbase.printerControl.comms.commands;

import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.Macro;
import celtech.roboxbase.configuration.datafileaccessors.HeadContainer;
import celtech.roboxbase.configuration.hardwarevariants.PrinterType;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.utils.PrinterUtils;
import celtech.roboxbase.utils.SystemUtils;
import celtech.roboxbase.utils.tasks.Cancellable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author ianhudson
 */
public class GCodeMacros
{

    private static final Stenographer steno = StenographerFactory.getStenographer(GCodeMacros.class.
            getName());
    private static final String macroDefinitionString = "Macro:";
    private static final String macroSeparator = "#";

    public interface FilenameEncoder {

        public String getFilenameCode();
    }

    public enum SafetyIndicator implements FilenameEncoder
    {

        // Safeties off
        SAFETIES_OFF("U"),
        // Safeties on
        SAFETIES_ON("S"),
        DONT_CARE(null);

        private final String filenameCode;

        private SafetyIndicator(String filenameCode)
        {
            this.filenameCode = filenameCode;
        }

        @Override
        public String getFilenameCode()
        {
            return filenameCode;
        }

        public static SafetyIndicator getEnumForFilenameCode(String code)
        {
            SafetyIndicator foundValue = null;

            for (SafetyIndicator value : SafetyIndicator.values())
            {
                if (code.equals(value.getFilenameCode()))
                {
                    foundValue = value;
                    break;
                }
            }

            return foundValue;
        }
    }

    public enum NozzleUseIndicator implements FilenameEncoder
    {

        // Nozzle 0 only
        NOZZLE_0("N0"),
        // Nozzle 1 only
        NOZZLE_1("N1"),
        //Both nozzles
        BOTH("NB"),
        DONT_CARE(null);

        private final String filenameCode;

        private NozzleUseIndicator(String filenameCode)
        {
            this.filenameCode = filenameCode;
        }

        @Override
        public String getFilenameCode()
        {
            return filenameCode;
        }

        public static NozzleUseIndicator getEnumForFilenameCode(String code)
        {
            NozzleUseIndicator foundValue = null;

            for (NozzleUseIndicator value : NozzleUseIndicator.values())
            {
                if (code.equals(value.getFilenameCode()))
                {
                    foundValue = value;
                    break;
                }
            }

            return foundValue;
        }
    }

    /**
     *
     * @param macroFileName - this can include the macro execution directive at the     * start of the line
     * @param typeCode
     * @param headTypeCode
     * @param requireNozzle0
     * @param requireNozzle1
     * @param requireSafetyFeatures
     * @return
     * @throws java.io.IOException
     * @throws celtech.roboxbase.printerControl.comms.commands.MacroLoadException
     */
    public static ArrayList<String> getMacroContents(String macroFileName,
            Optional<PrinterType> typeCode,
            String headTypeCode,
            boolean requireNozzle0,
            boolean requireNozzle1,
            boolean requireSafetyFeatures) throws IOException, MacroLoadException
    {

        assert typeCode != null;

        ArrayList<String> contents = new ArrayList<>();
        ArrayList<String> parentMacros = new ArrayList<>();

        if (requireSafetyFeatures)
        {
            contents.add("; Printed with safety features ON");
        } else
        {
            contents.add("; Printed with safety features OFF");
        }

        NozzleUseIndicator nozzleUse;
        String specifiedHeadType = null;

        if (headTypeCode == null)
        {
            nozzleUse = NozzleUseIndicator.DONT_CARE;
            specifiedHeadType = HeadContainer.defaultHeadID;
        } else
        {
            specifiedHeadType = headTypeCode;

            if (!requireNozzle0 && !requireNozzle1)
            {
                nozzleUse = NozzleUseIndicator.DONT_CARE;
            } else if (requireNozzle0 && !requireNozzle1)
            {
                nozzleUse = NozzleUseIndicator.NOZZLE_0;
            } else if (!requireNozzle0 && requireNozzle1)
            {
                nozzleUse = NozzleUseIndicator.NOZZLE_1;
            } else
            {
                nozzleUse = NozzleUseIndicator.BOTH;
            }
        }

        appendMacroContents(contents, parentMacros, macroFileName,
                typeCode, specifiedHeadType, nozzleUse,
                (requireSafetyFeatures == false) ? GCodeMacros.SafetyIndicator.SAFETIES_OFF : GCodeMacros.SafetyIndicator.DONT_CARE);

        return contents;
    }

    private static String cleanMacroName(String macroName)
    {
        return macroName.replaceFirst(macroDefinitionString, "").trim();
    }

    /**
     *
     * @param macroName
     * @return
     */
    private static ArrayList<String> appendMacroContents(ArrayList<String> contents,
            final ArrayList<String> parentMacros,
            final String macroName,
            Optional<PrinterType> typeCode,
            String headTypeCode,
            NozzleUseIndicator nozzleUse,
            SafetyIndicator safeties) throws IOException, MacroLoadException
    {
        String cleanedMacroName = cleanMacroName(macroName);

        if (!parentMacros.contains(cleanedMacroName))
        {
            steno.debug("Processing macro: " + cleanedMacroName);
            contents.add(";");
            contents.add("; Macro Start - " + cleanedMacroName);
            contents.add(";");

            parentMacros.add(cleanedMacroName);

            FileReader fileReader = null;

            try
            {
                fileReader = new FileReader(GCodeMacros.getFilename(cleanedMacroName,
                        typeCode,
                        headTypeCode,
                        nozzleUse,
                        safeties
                ));
                Scanner scanner = new Scanner(fileReader);

                while (scanner.hasNextLine())
                {
                    String line = scanner.nextLine();
                    line = line.trim();

                    if (isMacroExecutionDirective(line))
                    {
                        String subMacroName = line.replaceFirst(macroDefinitionString, "").trim();
                        if (subMacroName != null)
                        {
                            steno.debug("Sub-macro " + subMacroName + " detected");

                            appendMacroContents(contents, parentMacros, subMacroName,
                                    typeCode,
                                    headTypeCode,
                                    nozzleUse,
                                    safeties);
                        }
                    } else
                    {
                        contents.add(line);
                    }
                }
            } catch (FileNotFoundException ex)
            {
                throw new MacroLoadException("Failure to load contents of macro file " + macroName
                        + " : " + ex.getMessage());
            } finally
            {
                if (fileReader != null)
                {
                    fileReader.close();
                }
            }

            parentMacros.remove(macroName);
        } else
        {
            StringBuilder messageBuffer = new StringBuilder();
            messageBuffer.append("Macro circular dependency detected in chain: ");
            parentMacros.forEach(macro ->
            {
                messageBuffer.append(macro);
                messageBuffer.append("->");
            });
            messageBuffer.append(macroName);

            throw new MacroLoadException(messageBuffer.toString());
        }

        contents.add(";");
        contents.add("; Macro End - " + macroName);
        contents.add(";");

        return contents;
    }

    /**
     * For the given macroName check that there are no specialized macros present for different
     * printer types
     *
     * @param macroName
     */
    private static void checkNoSpecialisedMacrosFound(String macroName) {

    }

    private static boolean existsMacrosForPrinterType(File macroDirectory,
            String macroName, Optional<PrinterType> typeCode) {
        if (typeCode.isPresent())
        {
            File subDirectory = new File(macroDirectory.getAbsolutePath() + File.separator
                    + typeCode.get().getTypeCode());
            if (subDirectory.exists())
            {
                FilenameFilter filterForMacrosWithCorrectBase = new FilenameStartsWithFilter(macroName);
                String[] matchingMacroFilenames = subDirectory.list(filterForMacrosWithCorrectBase);
                return matchingMacroFilenames.length > 0;
            }
        }
        return false;
    }

    /**
     * Macros are named as follows:
     * <baseMacroName>_<[S|U]>_<headType>_<[nozzle0Used|nozzle1Used]>
     * e.g. macroA_S_RBX01-SM - is a macro that should be used for safe mode
     * when using head
     * RBX01-SM
     *
     * Specialized macros can be provided for different PrinterTypes. This is done by placing the
     * macro for the given printer type in a subdirectory of the name of the type, e.g. RBX10. If,
     * for a given baseMacroName, there is one or more macros of that name in a given subdirectory,
     * then all macros of the same name in the base (default) directory will be ignored.
     *
     * @param macroName
     * @param typeCode
     * @param headTypeCode
     * @param nozzleUse
     * @param safeties
     * @return
     * @throws java.io.FileNotFoundException
     */
    public static String getFilename(String macroName,
            Optional<PrinterType> typeCode,
            String headTypeCode,
            NozzleUseIndicator nozzleUse,
            SafetyIndicator safeties) throws FileNotFoundException
    {

        assert typeCode != null;

        if (!typeCode.isPresent())
        {
            // not specifying a type code requires that there only be a default macro
            // with no specialisations
            checkNoSpecialisedMacrosFound(macroName);
        }

        //Try with all attributes first
        //
        FilenameFilter filterForMacrosWithCorrectBase = new FilenameStartsWithFilter(macroName);
        String baseMacroName = macroName.split(macroSeparator)[0];
            
        File macroDirectory = new File(BaseConfiguration.getCommonApplicationDirectory()
                + BaseConfiguration.macroFileSubpath);

        // if there is one or more macro for the given printer type than only check
        // for macros in that directory  -ignore all base directory macros
        if (existsMacrosForPrinterType(macroDirectory, baseMacroName, typeCode))
        {
            macroDirectory = new File(macroDirectory.getAbsolutePath() + File.separator
                    + typeCode.get().getTypeCode());
        }

        String[] matchingMacroFilenames = macroDirectory.list(filterForMacrosWithCorrectBase);

        int highestScore = -999;
        int indexOfHighestScoringFilename = -1;

        steno.debug("Assessing macro against head:" + headTypeCode + " nozzles:" + nozzleUse + " safeties:" + safeties);

        if (matchingMacroFilenames.length > 0)
        {
            for (int filenameCounter = 0; filenameCounter < matchingMacroFilenames.length; filenameCounter++)
            {
                int score = scoreMacroFilename(matchingMacroFilenames[filenameCounter], baseMacroName, headTypeCode, nozzleUse, safeties);
                steno.debug("Assessed macro file " + matchingMacroFilenames[filenameCounter] + " as score " + score);
                if (score > highestScore)
                {
                    indexOfHighestScoringFilename = filenameCounter;
                    highestScore = score;
                }
            }

            if (indexOfHighestScoringFilename < 0 || indexOfHighestScoringFilename >= matchingMacroFilenames.length)
            {
                steno.error("Couldn't find macro " + macroName + " with head " + headTypeCode + " nozzle " + nozzleUse.name() + " safety " + safeties.name());
                steno.error("indexOfHighestScoringFilename = " + Integer.toString(indexOfHighestScoringFilename));
                steno.error("matchingMacroFilenames.length = " + Integer.toString(matchingMacroFilenames.length));
                throw new FileNotFoundException("Couldn't find macro " + macroName + " with head " + headTypeCode + " nozzle " + nozzleUse.name() + " safety " + safeties.name());
            }
            String path = macroDirectory.getAbsolutePath() + File.separator
                    + matchingMacroFilenames[indexOfHighestScoringFilename];
            path = path.replace('\\', '/');
            steno.info("Found macro file " + path);
            return path;
        } else
        {
            steno.error("Couldn't find macro " + macroName + " with head " + headTypeCode + " nozzle " + nozzleUse.name() + " safety " + safeties.name());
            throw new FileNotFoundException("Couldn't find macro " + macroName + " with head " + headTypeCode + " nozzle " + nozzleUse.name() + " safety " + safeties.name());
        }
//        
//        if (macroFiles.length == 0 && safeties == SafetyIndicator.SAFETIES_OFF)
//        {
//            //There may not be a safeties off version of the file - look for it with a don't care
//            filterForMacrosWithCorrectBase = new MacroFilenameFilter(macroName, headTypeCode, nozzleUse, SafetyIndicator.DONT_CARE);
//            macroFiles = macroDirectory.listFiles(filterForMacrosWithCorrectBase);
//        }
//
//        if (macroFiles.length > 0)
//        {
//            if (macroFiles.length > 1)
//            {
//                steno.info("Found " + macroFiles.length + " macro files:");
//                for (int counter = 0; counter < macroFiles.length; counter++)
//                {
//                    steno.info(macroFiles[counter].getName());
//                }
//            }
//            return macroFiles[0].getAbsolutePath();
//        } else
//        {
//            steno.error("Couldn't find macro " + macroName + " with head " + headTypeCode + " nozzle " + nozzleUse.name() + " safety " + safeties.name());
//            throw new FileNotFoundException("Couldn't find macro " + macroName + " with head " + headTypeCode + " nozzle " + nozzleUse.name() + " safety " + safeties.name());
//        }
    }

    protected static int scoreMacroFilename(String filename,
            String baseMacroName,
            String headTypeCode,
            NozzleUseIndicator nozzleUse,
            SafetyIndicator safeties)
    {
        int score = 0;

        String[] filenameSplit = filename.split("\\.");

        String specifiedHeadFile = headTypeCode;
        String fileHeadFile = null;

        NozzleUseIndicator specifiedNozzleUseIndicator = nozzleUse;
        SafetyIndicator specifiedSafetyIndicator = safeties;

        NozzleUseIndicator fileNozzleUseIndicator = null;
        SafetyIndicator fileSafetyIndicator = null;

        if (filenameSplit.length == 2
                && ("." + filenameSplit[1]).equalsIgnoreCase(BaseConfiguration.macroFileExtension))
        {
            String[] nameParts = filenameSplit[0].split(macroSeparator);
            
            int namePartCounter = 0;

            for (String namePart : nameParts)
            {
                if (namePartCounter == 0)
                {
                    if (!namePart.equalsIgnoreCase(baseMacroName))
                    {
                        // Reject on the basis that the base part of the file name does not match the base macro name.
                        return -9999;
                    }
                }
                else // (namePartCounter > 0)
                {
                    if (NozzleUseIndicator.getEnumForFilenameCode(namePart) != null)
                    {
                        fileNozzleUseIndicator = NozzleUseIndicator.getEnumForFilenameCode(namePart);
                    } else if (SafetyIndicator.getEnumForFilenameCode(namePart) != null)
                    {
                        fileSafetyIndicator = SafetyIndicator.getEnumForFilenameCode(namePart);
                    } else
                    {
                        //It wasn't a nozzle spec or a safety spec, so it must be a head...
                        fileHeadFile = namePart;
                    }
                }
                namePartCounter++;
            }

            // Not specified and not present -- 2 points
            // Specified and equal -- 2 points
            // Specified as SM head and file is DC -- 2 
            // Specified, not equal but file is DC -- 1 points
            // Otherwise -2 points
            if ((specifiedHeadFile == null
                    && fileHeadFile == null)
                    || (specifiedHeadFile != null
                    && specifiedHeadFile.equals(fileHeadFile))
                    || (specifiedHeadFile != null && specifiedHeadFile.equals(HeadContainer.defaultHeadID)
                    && fileHeadFile == null))
            {
                score += 2;
            } else if (specifiedHeadFile != null
                    && fileHeadFile == null)
                {
                score += 1;
                } else
                {
                score -= 2;
                    }

            if ((specifiedNozzleUseIndicator == NozzleUseIndicator.DONT_CARE
                    && fileNozzleUseIndicator == null)
                    || (specifiedNozzleUseIndicator != NozzleUseIndicator.DONT_CARE
                    && specifiedNozzleUseIndicator == fileNozzleUseIndicator))
            {
                score += 2;
            } else if (specifiedNozzleUseIndicator != NozzleUseIndicator.DONT_CARE
                    && fileNozzleUseIndicator == null)
                {
                score += 1;
                } else
                {
                score -= 2;
                    }

            if ((specifiedSafetyIndicator == SafetyIndicator.DONT_CARE
                    && fileSafetyIndicator == null)
                    || (specifiedSafetyIndicator != SafetyIndicator.DONT_CARE
                    && specifiedSafetyIndicator == fileSafetyIndicator))
            {
                score += 2;
            } else if (specifiedSafetyIndicator != SafetyIndicator.DONT_CARE
                    && fileSafetyIndicator == null)
                {
                score += 1;
                } else
                {
                score -= 2;
                    }
        } else
        {
            steno.warning("Couldn't score macro file: " + filename);
        }

        return score;
    }

    public static boolean isMacroExecutionDirective(String input)
    {
        return input.startsWith(macroDefinitionString);
    }

//    private String getMacroNameFromDirective(String macroDirective)
//    {
//        String macroName = null;
//        String[] parts = macroDirective.split(":");
//        if (parts.length == 2)
//        {
//            macroName = parts[1].trim();
//        } else
//        {
//            steno.error("Saw macro directive but couldn't understand it: " + macroDirective);
//        }
//        return macroName;
//    }

    public static int getNumberOfOperativeLinesInMacro(String macroDirective,
            Optional<PrinterType> typeCode,
            String headType,
            boolean useNozzle0,
            boolean useNozzle1,
            boolean requireSafetyFeatures)
    {
        int linesInMacro = 0;
        String macro = cleanMacroName(macroDirective);
        if (macro != null)
        {
            try
            {
                List<String> contents = getMacroContents(macro, typeCode,
                        headType, useNozzle0, useNozzle1, requireSafetyFeatures);
                for (String line : contents)
                {
                    if (line.trim().startsWith(";") == false && line.equals("") == false)
                    {
                        linesInMacro++;
                    }
                }
            } catch (IOException | MacroLoadException ex)
            {
                steno.error("Error trying to get number of lines in macro " + macro);
            }
        }

        return linesInMacro;
    }

    public static void sendMacroLineByLine(Printer printer, Macro macro, Cancellable cancellable) throws IOException, MacroLoadException
    {
        PrinterType typeCode = printer.findPrinterType();

        ArrayList<String> macroLines = GCodeMacros.getMacroContents(macro.getMacroFileName(),
                Optional.of(typeCode), printer.headProperty().get().typeCodeProperty().get(),
                false, false, false);

        for (String macroLine : macroLines)
        {
            String lineToTransmit = SystemUtils.cleanGCodeForTransmission(macroLine);
            if (lineToTransmit.length() > 0)
            {
                printer.sendRawGCode(lineToTransmit, false);
                if (PrinterUtils.waitOnBusy(printer, cancellable))
                {
                    return;
                }
            }
        }
    }

    /**
     *
     * @param aFile
     * @param commentCharacter
     * @return
     */
    public static int countLinesInMacroFile(File aFile, String commentCharacter, Optional<PrinterType> typeCode)
    {
        return countLinesInMacroFile(aFile, commentCharacter, typeCode, null, false, false, false);
    }

    /**
     *
     * @param aFile
     * @param commentCharacter
     * @param typeCode
     * @param headType
     * @param useNozzle0
     * @param useNozzle1
     * @param requireSafetyFeatures
     * @return
     */
    public static int countLinesInMacroFile(File aFile,
            String commentCharacter,
            Optional<PrinterType> typeCode,
            String headType,
            boolean useNozzle0,
            boolean useNozzle1,
            boolean requireSafetyFeatures)
    {

        // if typeCode is null then ensure that no alternative macros exist in subdirectory
        LineNumberReader reader = null;
        int numberOfLines = 0;
        try
        {
            String lineRead;
            reader = new LineNumberReader(new FileReader(aFile));

            while ((lineRead = reader.readLine()) != null)
            {
                lineRead = lineRead.trim();
                if (GCodeMacros.isMacroExecutionDirective(lineRead))
                {
                    numberOfLines += GCodeMacros.getNumberOfOperativeLinesInMacro(lineRead,
                            typeCode, headType, useNozzle0, useNozzle1, requireSafetyFeatures);
                } else if (lineRead.startsWith(commentCharacter) == false && lineRead.equals("")
                        == false)
                {
                    numberOfLines++;
                }
            };
            return numberOfLines;
        } catch (Exception ex)
        {
            return -1;
        } finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                } catch (IOException ex)
                {
                    steno.error("Failed to close file during line number read: " + ex);
                }
            }
        }
    }
}
