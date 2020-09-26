
package celtech.utils.threed;

import java.util.List;
import java.util.concurrent.Callable;
import javafx.scene.shape.TriangleMesh;

/**
 *
 * @author Ian
 */
public class MeshSeparatorRunner implements Callable
{
private final TriangleMesh meshToSeparate;

    public MeshSeparatorRunner(TriangleMesh mesh)
    {
        this.meshToSeparate = mesh;
    }
    
    @Override
    public List<TriangleMesh> call() throws Exception
    {
        List<TriangleMesh> result = MeshSeparator.separate(meshToSeparate);
        
        return result;
    }    
}
