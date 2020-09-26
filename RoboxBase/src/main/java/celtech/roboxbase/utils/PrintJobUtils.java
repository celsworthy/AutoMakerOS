package celtech.roboxbase.utils;

import celtech.roboxbase.camera.CameraInfo;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.fileRepresentation.CameraSettings;
import celtech.roboxbase.postprocessor.PrintJobStatistics;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author George Salter
 */
public class PrintJobUtils {
    
    private static final Stenographer STENO = StenographerFactory.getStenographer(PrintJobUtils.class.getName());
    
    public static void assignPrintJobIdToProject(String jobUUID, String printJobDirectoryName, String printQuality, Optional<CameraSettings> cameraData) {
        try {
            renameFilesInPrintJob(jobUUID, printJobDirectoryName, printQuality);
            String statisticsFileLocation = printJobDirectoryName
                    + File.separator 
                    + jobUUID
                    + BaseConfiguration.statisticsFileExtension;
            PrintJobStatistics statistics = PrintJobStatistics.importStatisticsFromGCodeFile(statisticsFileLocation);
            statistics.setPrintJobID(jobUUID);
            statistics.writeStatisticsToFile(statisticsFileLocation);
        } catch (IOException ex)
        {
            STENO.exception("Exception when reading or writing statistics file", ex);
        }
        cameraData.ifPresent((cd) -> {
            try {
                String cameraFileLocation = printJobDirectoryName
                        + File.separator 
                        + jobUUID
                        + BaseConfiguration.cameraDataFileExtension;
                cd.writeToFile(cameraFileLocation);
            }
            catch (IOException ex) {
                STENO.exception("Exception when writing camera data", ex);
            }
        });
    }
    
    private static void renameFilesInPrintJob(String jobUUID, String printJobDirectory, String printQuality) {
        File printJobDir = new File(printJobDirectory);
        Stream.of(printJobDir.listFiles()).forEach(file -> {
            try {
                String originalFile = file.getPath();
                if(originalFile.contains(printQuality)) {
                    String newFile = originalFile.replace(printQuality, jobUUID);
                    Files.move(new File(originalFile), new File(newFile));
                }
            } catch (IOException ex) {
                STENO.exception("Error when renaiming files for print job: " + jobUUID, ex);
            }
        });
    }
}
