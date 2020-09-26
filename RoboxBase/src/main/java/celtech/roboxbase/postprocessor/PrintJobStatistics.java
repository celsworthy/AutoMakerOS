/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.postprocessor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.List;
import java.util.Map;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.parboiled.common.FileUtils;

/**
 *
 * @author tony
 */
public class PrintJobStatistics
{

    @JsonIgnore
    private final static Stenographer steno = StenographerFactory.getStenographer(PrintJobStatistics.class.getName());

    private String printedWithHeadID;
    private String printedWithHeadType;
    private boolean requiresMaterial1;
    private boolean requiresMaterial2;
    private String printJobID;
    private String projectName;
    private String profileName;
    private float layerHeight;
    private int numberOfLines;
    private double eVolumeUsed;
    private double dVolumeUsed;
    private List<Integer> layerNumberToLineNumber;
    private Map<Integer, Double> layerNumberToPredictedDuration_E_FeedrateDependent;
    private Map<Integer, Double> layerNumberToPredictedDuration_D_FeedrateDependent;
    private Map<Integer, Double> layerNumberToPredictedDuration_FeedrateIndependent;
    private double predictedDuration;
    private int lineNumberOfFirstExtrusion;
    private String projectPath;
    
    @JsonIgnore
    private Date creationDate;

    @JsonIgnore
    public static final String DATA_PREFIX_IN_FILE = ";#Statistics:";
    @JsonIgnore
    public static final String DATA_SEPARATOR = "|->";

    public PrintJobStatistics()
    {
        printedWithHeadID = "";
        printedWithHeadType = "";
        requiresMaterial1 = false;
        requiresMaterial2 = false;
        printJobID = "";
        projectName = "";
        profileName = "";
        layerHeight = 0;
        numberOfLines = 0;
        eVolumeUsed = 0;
        dVolumeUsed = 0;
        lineNumberOfFirstExtrusion = 0;
        layerNumberToLineNumber = null;
        predictedDuration = 0;
    }

    public PrintJobStatistics(
            String printedWithHeadID,
            String printedWithHeadType,
            boolean requiresMaterial1,
            boolean requiresMaterial2,
            String printJobID,
            String projectName,
            String profileName,
            float layerHeight,
            int numberOfLines,
            double eVolumeUsed,
            double dVolumeUsed,
            int lineNumberOfFirstExtrusion,
            List<Integer> layerNumberToLineNumber,
            Map<Integer, Double> layerNumberToPredictedDuration_E_FeedrateDependent,
            Map<Integer, Double> layerNumberToPredictedDuration_D_FeedrateDependent,
            Map<Integer, Double> layerNumberToPredictedDuration_FeedrateIndependent,
            double predictedDuration
    )
    {
        this.printedWithHeadID = printedWithHeadID;
        this.printedWithHeadType = printedWithHeadType;
        this.requiresMaterial1 = requiresMaterial1;
        this.requiresMaterial2 = requiresMaterial2;
        this.printJobID = printJobID;
        this.projectName = projectName;
        this.profileName = profileName;
        this.layerHeight = layerHeight;
        this.numberOfLines = numberOfLines;
        this.eVolumeUsed = eVolumeUsed;
        this.dVolumeUsed = dVolumeUsed;
        this.lineNumberOfFirstExtrusion = lineNumberOfFirstExtrusion;
        this.layerNumberToLineNumber = layerNumberToLineNumber;
        this.layerNumberToPredictedDuration_E_FeedrateDependent = layerNumberToPredictedDuration_E_FeedrateDependent;
        this.layerNumberToPredictedDuration_D_FeedrateDependent = layerNumberToPredictedDuration_D_FeedrateDependent;
        this.layerNumberToPredictedDuration_FeedrateIndependent = layerNumberToPredictedDuration_FeedrateIndependent;
        this.predictedDuration = predictedDuration;
    }

    public String getPrintedWithHeadID()
    {
        return printedWithHeadID;
    }

    public void setPrintedWithHeadID(String printedWithHeadID)
    {
        this.printedWithHeadID = printedWithHeadID;
    }

    public String getPrintedWithHeadType()
    {
        return printedWithHeadType;
    }

    public void setPrintedWithHeadType(String printedWithHeadType)
    {
        this.printedWithHeadType = printedWithHeadType;
    }

    public boolean getRequiresMaterial1()
    {
        return requiresMaterial1;
    }

    public void setRequiresMaterial1(boolean requiresMaterial1)
    {
        this.requiresMaterial1 = requiresMaterial1;
    }

    public boolean getRequiresMaterial2()
    {
        return requiresMaterial2;
    }

    public void setRequiresMaterial2(boolean requiresMaterial2)
    {
        this.requiresMaterial2 = requiresMaterial2;
    }

    public String getPrintJobID()
    {
        return printJobID;
    }

    public void setPrintJobID(String printJobID)
    {
        this.printJobID = printJobID;
    }

    /**
     * @return the numberOfLines
     */
    public int getNumberOfLines()
    {
        return numberOfLines;
    }

    /**
     * @return the volumeUsed for Extruder E
     */
    public double geteVolumeUsed()
    {
        return eVolumeUsed;
    }

    /**
     * @return the volumeUsed for Extruder D
     */
    public double getdVolumeUsed()
    {
        return dVolumeUsed;
    }

    /**
     * @return the layerNumberToLineNumber
     */
    public List<Integer> getLayerNumberToLineNumber()
    {
        return layerNumberToLineNumber;
    }

    public Map<Integer, Double> getLayerNumberToPredictedDuration_E_FeedrateDependent()
    {
        return layerNumberToPredictedDuration_E_FeedrateDependent;
    }

    public Map<Integer, Double> getLayerNumberToPredictedDuration_D_FeedrateDependent()
    {
        return layerNumberToPredictedDuration_D_FeedrateDependent;
    }

    public Map<Integer, Double> getLayerNumberToPredictedDuration_FeedrateIndependent()
    {
        return layerNumberToPredictedDuration_FeedrateIndependent;
    }

    /**
     * @return the lineNumberOfFirstExtrusion
     */
    public int getLineNumberOfFirstExtrusion()
    {
        return lineNumberOfFirstExtrusion;
    }

    /**
     * @return the predictedDuration
     */
    public double getPredictedDuration()
    {
        return predictedDuration;
    }

    public float getLayerHeight()
    {
        return layerHeight;
    }

    public void setLayerHeight(float layerHeight)
    {
        this.layerHeight = layerHeight;
    }

    public String getProjectName()
    {
        return projectName;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    public String getProfileName()
    {
        return profileName;
    }

    public void setProfileName(String profileName)
    {
        this.profileName = profileName;
    }

    public void setNumberOfLines(int numberOfLines)
    {
        this.numberOfLines = numberOfLines;
    }

    public void seteVolumeUsed(double eVolumeUsed)
    {
        this.eVolumeUsed = eVolumeUsed;
    }

    public void setdVolumeUsed(double dVolumeUsed)
    {
        this.dVolumeUsed = dVolumeUsed;
    }

    public void setLayerNumberToLineNumber(List<Integer> layerNumberToLineNumber)
    {
        this.layerNumberToLineNumber = layerNumberToLineNumber;
    }

    public void setLayerNumberToPredictedDuration_E_FeedrateDependent(Map<Integer, Double> layerNumberToPredictedDuration_E_FeedrateDependent)
    {
        this.layerNumberToPredictedDuration_E_FeedrateDependent = layerNumberToPredictedDuration_E_FeedrateDependent;
    }

    public void setLayerNumberToPredictedDuration_D_FeedrateDependent(Map<Integer, Double> layerNumberToPredictedDuration_D_FeedrateDependent)
    {
        this.layerNumberToPredictedDuration_D_FeedrateDependent = layerNumberToPredictedDuration_D_FeedrateDependent;
    }

    public void setLayerNumberToPredictedDuration_FeedrateIndependent(Map<Integer, Double> layerNumberToPredictedDuration_FeedrateIndependent)
    {
        this.layerNumberToPredictedDuration_FeedrateIndependent = layerNumberToPredictedDuration_FeedrateIndependent;
    }

    public void setPredictedDuration(double predictedDuration)
    {
        this.predictedDuration = predictedDuration;
    }

    public void setLineNumberOfFirstExtrusion(int lineNumberOfFirstExtrusion)
    {
        this.lineNumberOfFirstExtrusion = lineNumberOfFirstExtrusion;
    }

    public String getProjectPath() 
    {
        return projectPath;
    }

    public void setProjectPath(String projectPath) 
    {
        this.projectPath = projectPath;
    }

    @JsonIgnore
    public Date getCreationDate()
    {
        return creationDate;
    }

    @JsonIgnore
    public void setCreationDate(Date date)
    {
        this.creationDate = date;
    }

    public void updateValueFromStatsString(String statsString)
    {
        ObjectMapper mapper = new ObjectMapper();

        if (statsString.startsWith(DATA_PREFIX_IN_FILE)
                && statsString.contains(DATA_SEPARATOR))
        {
            String fieldName = statsString.substring(DATA_PREFIX_IN_FILE.length(), statsString.indexOf(DATA_SEPARATOR));
            String jsonData = statsString.substring(statsString.indexOf(DATA_SEPARATOR) + DATA_SEPARATOR.length());
            try
            {
                Field fieldToUpdate = PrintJobStatistics.class.getDeclaredField(fieldName);
                //TODO THIS IS SO HORRIBLE IT IS BEYOND BELIEF!!
                //Jackson doesn't know what type the key is so we have to force the type
                if (fieldName.startsWith("layerNumberToPredictedDuration"))
                {
                    fieldToUpdate.set(this, mapper.readValue(jsonData, new TypeReference<Map<Integer, Double>>()
                    {
                    }));
                } else
                {
                    fieldToUpdate.set(this, mapper.readValue(jsonData, fieldToUpdate.getType()));
                }
            } catch (NoSuchFieldException | IllegalAccessException | IOException ex)
            {
                steno.error("Couldn't update field " + fieldName);
            }
        }
    }

    public void writeStatisticsToFile(String statisticsFileLocation) throws IOException
    {
        FileUtils.ensureParentDir(new File(statisticsFileLocation));

        setCreationDate(new Date());

        BufferedWriter writer = new BufferedWriter(new FileWriter(statisticsFileLocation));
        try
        {
            writer.write(";#########################################################\n");
            writer.write(";                     Statistics\n");
            writer.write(";                     ==========\n");

            ObjectMapper mapper = new ObjectMapper();

            for (Field field : this.getClass().getDeclaredFields())
            {
                if (!(field.getDeclaredAnnotations().length == 1
                        && field.getDeclaredAnnotations()[0] instanceof JsonIgnore))
                {
                    try
                    {
                        String jsonifiedData = mapper.writeValueAsString(field.get(this));
                        String outputString = DATA_PREFIX_IN_FILE + field.getName() + DATA_SEPARATOR + jsonifiedData;
                        writer.write(outputString + "\n");
                    } catch (JsonProcessingException | IllegalAccessException ex)
                    {
                        steno.exception("Exception processing " + field.getName(), ex);
                    }
                }
            }
            writer.write(";#########################################################\n");
            writer.flush();
            writer.close();
        } catch (IOException ex)
        {
            steno.exception("Unable to write statistics", ex);
        }
    }

    public static PrintJobStatistics importStatisticsFromGCodeFile(String roboxisedFileLocation) throws FileNotFoundException, IOException
    {
        PrintJobStatistics result = new PrintJobStatistics();

        File roboxisedFile = new File(roboxisedFileLocation);
        BufferedReader fileReader = new BufferedReader(new FileReader(roboxisedFile));

        String line;

        while ((line = fileReader.readLine()) != null)
        {
            result.updateValueFromStatsString(line);
        }

        Path roboxisedFilePath = Paths.get(roboxisedFile.toURI());
        BasicFileAttributes attr = Files.readAttributes(roboxisedFilePath, BasicFileAttributes.class);
        result.setCreationDate(new Date(attr.creationTime().toMillis()));
        return result;
    }
}
