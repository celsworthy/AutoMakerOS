package celtech;

import celtech.modelcontrol.ModelContainer;
import java.io.File;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 *
 * @author Ian
 */
public class TestUtils
{

    public ModelContainer makeModelContainer(boolean useExtruder0)
    {
        MeshView meshView = new MeshView(new Shape3DRectangle(2, 3));
        ModelContainer modelContainer = new ModelContainer(new File("testModel"), meshView);
        modelContainer.setUseExtruder0(useExtruder0);
        return modelContainer;
    }
    
    public ModelContainer makeModelContainer(boolean useExtruder0, int x, int y)
    {
        MeshView meshView = new MeshView(new Shape3DRectangle(x, y));
        ModelContainer modelContainer = new ModelContainer(new File("testModel"), meshView);
        modelContainer.setUseExtruder0(useExtruder0);
        return modelContainer;
    }
    
    public static class Shape3DRectangle extends TriangleMesh
    {

        public Shape3DRectangle(float Width, float Height)
        {
            float[] points =
            {
                -Width / 2, Height / 2, 0, // idx p0
                -Width / 2, -Height / 2, 0, // idx p1
                Width / 2, Height / 2, 0, // idx p2
                Width / 2, -Height / 2, 0  // idx p3
            };
            float[] texCoords =
            {
                1, 1, // idx t0
                1, 0, // idx t1
                0, 1, // idx t2
                0, 0  // idx t3
            };

            int[] faces =
            {
                2, 3, 0, 2, 1, 0,
                2, 3, 1, 0, 3, 1
            };

            this.getPoints().setAll(points);
            this.getTexCoords().setAll(texCoords);
            this.getFaces().setAll(faces);
        }
    }
}
