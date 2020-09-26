package celtech.coreUI.visualisation.shapes;

/**
 * Polygon mesh where the points are symbolic. That is, the values of the 
 * points depend on other variables and they can be updated appropriately.
 */
public class SymbolicPolygonMesh {

    /**
     *
     */
    public SymbolicPointArray points;

    /**
     *
     */
    public float[] texCoords;

    /**
     *
     */
    public int[][] faces;

    /**
     *
     */
    public int[] faceSmoothingGroups;
    private int numEdgesInFaces = -1;

    /**
     *
     * @param points
     * @param texCoords
     * @param faces
     * @param faceSmoothingGroups
     */
    public SymbolicPolygonMesh(SymbolicPointArray points, float[] texCoords, int[][] faces, int[] faceSmoothingGroups) {
        this.points = points;
        this.texCoords = texCoords;
        this.faces = faces;
        this.faceSmoothingGroups = faceSmoothingGroups;
    }
    
    /**
     *
     * @param mesh
     */
    public SymbolicPolygonMesh(PolygonMesh mesh) {
        this.points = new OriginalPointArray(mesh);
        this.texCoords = mesh.getTexCoords().toArray(this.texCoords);
        this.faces = mesh.faces;
        this.faceSmoothingGroups = mesh.getFaceSmoothingGroups().toArray(null);
    }
    
    /**
     *
     * @return
     */
    public int getNumEdgesInFaces() {
        if (numEdgesInFaces == -1) {
            numEdgesInFaces = 0;
            for(int[] face : faces) {
                numEdgesInFaces += face.length;
            }
           numEdgesInFaces /= 2;
        }
        return numEdgesInFaces;
    }
}
