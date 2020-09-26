package celtech.roboxbase.utils.exporters;

import java.util.List;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author ianhudson
 */
public class MeshExportResult
{

    private final List<String> createdFiles;
    private final Vector3D centre;

    public MeshExportResult(List<String> createdFiles, Vector3D centre)
    {
        this.createdFiles = createdFiles;
        this.centre = centre;
    }

    public List<String> getCreatedFiles()
    {
        return createdFiles;
    }

    public Vector3D getCentre()
    {
        return centre;
    }
}
