package celtech.roboxbase.printerControl;

import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.fileRepresentation.CameraSettings;
import celtech.roboxbase.postprocessor.PrintJobStatistics;
import java.io.File;
import java.io.IOException;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * A PrintJob represents a print run of a Project, and is associated with a print job directory in
 * the print spool directory.
 *
 * @author Ian
 */
public class PrintJob
{
    private static final Stenographer STENO = StenographerFactory.getStenographer(PrintJob.class.getName());
    
    
    private String jobUUID = null;
    private String printJobDirectory = null;
    private PrintJobStatistics statistics = null;
    private CameraSettings cameraData = null;
    private boolean cameraDataLoadAttempted = false;
    
    public PrintJob(String jobUUID)
    {
        this.jobUUID = jobUUID;
        this.printJobDirectory = BaseConfiguration.getPrintSpoolDirectory() + jobUUID
            + File.separator;
    }

    public PrintJob(String jobUUID, String printJobDirectory)
    {
        this.jobUUID = jobUUID;
        this.printJobDirectory = printJobDirectory;
    }

    /**
     * Get the location of the gcode file as produced by the slicer
     *
     * @return
     */
    public String getGCodeFileLocation()
    {
        String printjobFilename = printJobDirectory
            + jobUUID
            + BaseConfiguration.gcodeTempFileExtension;
        return printjobFilename;
    }

    /**
     * Return if the roboxised file is found in the print spool directory
     *
     * @return
     */
    public boolean roboxisedFileExists()
    {
        File printJobFile = new File(getRoboxisedFileLocation());
        return printJobFile.exists();
    }

    /**
     * @return the jobUUID
     */
    public String getJobUUID()
    {
        return jobUUID;
    }

    /**
     * @return the printJobDirectory
     */
    public String getJobDirectory()
    {
        return printJobDirectory;
    }

    /**
     * Get the location of the roboxised file
     *
     * @return
     */
    public String getRoboxisedFileLocation()
    {
        return printJobDirectory
            + jobUUID
            + BaseConfiguration.gcodePostProcessedFileHandle
            + BaseConfiguration.gcodeTempFileExtension;
    }

    /**
     * Get the location of the statistics file
     *
     * @return
     */
    public String getStatisticsFileLocation()
    {
        return printJobDirectory
            + jobUUID
            + BaseConfiguration.statisticsFileExtension;
    }

    public PrintJobStatistics getStatistics() throws IOException
    {
        if (statistics == null)
        {
            STENO.info("Looking for statistics file in location - " + getStatisticsFileLocation());
            statistics = PrintJobStatistics.importStatisticsFromGCodeFile(getStatisticsFileLocation());
        }
        return statistics;
    }

    /**
     * Get the location of the statistics file
     *
     * @return
     */
    public String getCameraDataFileLocation()
    {
        return printJobDirectory
            + jobUUID
            + BaseConfiguration.cameraDataFileExtension;
    }

    public CameraSettings getCameraData()
    {
        if (!cameraDataLoadAttempted && cameraData == null)
        {
            try {
                STENO.info("Looking for camera data file in location - " + getCameraDataFileLocation());
                cameraData = CameraSettings.readFromFile(getCameraDataFileLocation());
            }
            catch (IOException ex) {
                
            }
            finally {
                cameraDataLoadAttempted = true;
            }
        }
        return cameraData;
    }
}
