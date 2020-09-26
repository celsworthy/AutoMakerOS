package celtech.coreUI.visualisation.collision;

import javafx.concurrent.Task;
import javafx.scene.shape.MeshView;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class HullComputer extends Task<MeshView>
{

    private final Stenographer steno = StenographerFactory.getStenographer(HullComputer.class.getName());
    private final MeshView meshView;

    public HullComputer(MeshView meshView)
    {
        this.meshView = meshView;
    }

    @Override
    public MeshView call() throws Exception
    {
        steno.info("Starting hull computation ");
//        CSG modelAsCSG = MeshUtils.mesh2CSG(meshView);
//        CSG hull = modelAsCSG.hull();
//        MeshContainer javafxMesh = hull.toJavaFXMesh(null);
        
//        List<Polygon> hullPolys = hull.getPolygons();
//
//        ObjectArrayList<Vector3f> vectors = new ObjectArrayList<>();
//        for (Polygon poly : hullPolys)
//        {
//            for (Vertex vert : poly.vertices)
//            {
//                Vector3f newVector = new Vector3f((float) vert.pos.x, (float) vert.pos.y, (float) vert.pos.z);
//                steno.info("Adding vector " + newVector);
//                vectors.add(newVector);
//            }
//        }
//
//        CollisionShape hullShape= new ConvexHullShape(vectors);
        
        steno.info("Finished hull computation");
//        return javafxMesh.getAsMeshViews().get(0);
        return null;
    }
}
