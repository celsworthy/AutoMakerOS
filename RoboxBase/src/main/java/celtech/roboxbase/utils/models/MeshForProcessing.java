package celtech.roboxbase.utils.models;

import celtech.roboxbase.utils.threed.MeshToWorldTransformer;
import javafx.scene.shape.MeshView;

/**
 * This class is used to hold models and associated data that are used for slicing or postprocessing activities
 * @author ianhudson
 */
public class MeshForProcessing
{
    private final MeshView meshView;
    private final MeshToWorldTransformer meshToWorldTransformer;

    public MeshForProcessing(MeshView meshView, MeshToWorldTransformer meshToWorldTransformer)
    {
        this.meshView = meshView;
        this.meshToWorldTransformer = meshToWorldTransformer;
    }

    public MeshToWorldTransformer getMeshToWorldTransformer()
    {
        return meshToWorldTransformer;
    }

    public MeshView getMeshView()
    {
        return meshView;
    }
}
