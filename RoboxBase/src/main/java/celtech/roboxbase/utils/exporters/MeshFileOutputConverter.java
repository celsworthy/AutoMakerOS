package celtech.roboxbase.utils.exporters;

import celtech.roboxbase.utils.models.MeshForProcessing;
import java.util.List;

/**
 *
 * @author Ian
 */
public interface MeshFileOutputConverter
{

    /**
     * Output the stl or amf file for the given project to the file indicated by the project job
     * UUID.
     * @param meshesForProcessing
     * @param printJobUUID
     * @param outputAsSingleFile
     * @return List of filenames that have been created
     */
    MeshExportResult outputFile(List<MeshForProcessing> meshesForProcessing, String printJobUUID, boolean outputAsSingleFile);

    /**
     * Output the stl or amf file for the given project to the file indicated by the project job
     * UUID.
     * @param meshesForProcessing
     * @param printJobUUID
     * @param printJobDirectory
     * @param outputAsSingleFile
     * @return List of filenames that have been created
     */
    MeshExportResult outputFile(List<MeshForProcessing> meshesForProcessing, String printJobUUID, String printJobDirectory, boolean outputAsSingleFile);
}
