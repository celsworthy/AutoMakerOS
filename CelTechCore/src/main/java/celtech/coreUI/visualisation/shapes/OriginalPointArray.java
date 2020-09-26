package celtech.coreUI.visualisation.shapes;

/**
 *
 * @author Ian
 */
public class OriginalPointArray extends SymbolicPointArray {
    PolygonMesh mesh;

    /**
     *
     * @param mesh
     */
    public OriginalPointArray(PolygonMesh mesh) {
        super(new float[mesh.getPoints().size()]);
        this.mesh = mesh;
    }

    @Override
    public void update() {
        mesh.getPoints().copyTo(0, data, 0, data.length);
    }
}
