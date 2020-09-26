package celtech.coreUI.visualisation.modelDisplay;

import celtech.coreUI.visualisation.metaparts.FloatArrayList;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author Ian
 */
public class DimensionLines
{

    private final Group parentGroup;
    private final double offsetFromMaxMin;

    private final PhongMaterial materialToUse;

    // Dimension lines
    private final float arrowHeadWidth = 1;
    private final float arrowHeadLength = 5;
    private final float lineWidth = 0.25f;
    private Node heightUpArrowHead = null;
    private Node heightDownArrowHead = null;
    private Node heightLine = null;
    private Text heightLabel = new Text();
    private Node widthLeftArrowHead = null;
    private Node widthRightArrowHead = null;
    private Node widthLine = null;
    private Text widthLabel = new Text();
    private Node depthBackArrowHead = null;
    private Node depthForwardArrowHead = null;
    private Node depthLine = null;

    private enum ArrowHeadDirection
    {

        LEFT, RIGHT, UP, DOWN, FORWARD, BACK
    }

    private enum LineDirection
    {

        HORIZONTAL, VERTICAL, FORWARD_BACK
    }

    public DimensionLines(Group parentGroup, PhongMaterial materialToUse, double offsetFromMaxMin)
    {
        this.parentGroup = parentGroup;
        this.materialToUse = materialToUse;
        this.offsetFromMaxMin = offsetFromMaxMin;

        buildDimensionLines();
    }

    private void buildDimensionLines()
    {
        heightUpArrowHead = createArrowHead(ArrowHeadDirection.UP);
        heightDownArrowHead = createArrowHead(ArrowHeadDirection.DOWN);
        heightLine = createLine(LineDirection.VERTICAL);
        parentGroup.getChildren().add(heightLine);

        heightLabel.getStyleClass().add("dimension-label");
        heightLabel.setText("123.4");
        heightLabel.setScaleX(0.1);
        heightLabel.setScaleY(0.1);
        heightLabel.setScaleZ(0.1);
        heightLabel.setTextAlignment(TextAlignment.CENTER);
        heightLabel.setSmooth(false);
        parentGroup.getChildren().add(heightLabel);

        widthLeftArrowHead = createArrowHead(ArrowHeadDirection.LEFT);
        widthRightArrowHead = createArrowHead(ArrowHeadDirection.RIGHT);
        widthLine = createLine(LineDirection.HORIZONTAL);
        parentGroup.getChildren().add(widthLine);

        widthLabel.getStyleClass().add("dimension-label");
        widthLabel.setText("123.4");
        widthLabel.setScaleX(0.1);
        widthLabel.setScaleY(0.1);
        widthLabel.setScaleZ(0.1);
        widthLabel.setTextAlignment(TextAlignment.CENTER);
        widthLabel.setSmooth(false);
        parentGroup.getChildren().add(widthLabel);

//        depthBackArrowHead = createArrowHead(ArrowHeadDirection.BACK);
//        depthForwardArrowHead = createArrowHead(ArrowHeadDirection.FORWARD);
    }

    private MeshView createArrowHead(ArrowHeadDirection headDirection)
    {
        TriangleMesh triMesh = new TriangleMesh();

        switch (headDirection)
        {
            case UP:
                triMesh.getPoints().addAll(0, 0, 0);
                triMesh.getPoints().addAll(-arrowHeadWidth / 2, arrowHeadLength, 0);
                triMesh.getPoints().addAll(0, arrowHeadLength, 0);
                triMesh.getPoints().addAll(arrowHeadWidth / 2, arrowHeadLength, 0);
                triMesh.getFaces().addAll(0, 0, 1, 0, 2, 0);
                triMesh.getFaces().addAll(2, 0, 3, 0, 0, 0);
                break;
            case DOWN:
                triMesh.getPoints().addAll(0, 0, 0);
                triMesh.getPoints().addAll(-arrowHeadWidth / 2, -arrowHeadLength, 0);
                triMesh.getPoints().addAll(0, -arrowHeadLength, 0);
                triMesh.getPoints().addAll(arrowHeadWidth / 2, -arrowHeadLength, 0);
                triMesh.getFaces().addAll(0, 0, 2, 0, 1, 0);
                triMesh.getFaces().addAll(0, 0, 3, 0, 2, 0);
                break;
            case LEFT:
                triMesh.getPoints().addAll(0, 0, 0);
                triMesh.getPoints().addAll(arrowHeadLength, arrowHeadWidth / 2, 0);
                triMesh.getPoints().addAll(arrowHeadLength, 0, 0);
                triMesh.getPoints().addAll(arrowHeadLength, -arrowHeadWidth / 2, 0);
                triMesh.getFaces().addAll(0, 0, 1, 0, 2, 0);
                triMesh.getFaces().addAll(2, 0, 3, 0, 0, 0);
                break;
            case RIGHT:
                triMesh.getPoints().addAll(0, 0, 0);
                triMesh.getPoints().addAll(-arrowHeadLength, -arrowHeadWidth/2, 0);
                triMesh.getPoints().addAll(-arrowHeadLength, 0, 0);
                triMesh.getPoints().addAll(-arrowHeadLength, arrowHeadWidth/2, 0);
                triMesh.getFaces().addAll(0, 0, 2, 0, 1, 0);
                triMesh.getFaces().addAll(0, 0, 3, 0, 2, 0);
                break;
        }
        FloatArrayList texCoords = new FloatArrayList();
        texCoords.add(0f);
        texCoords.add(0f);
        triMesh.getTexCoords().addAll(texCoords.toFloatArray());

        int[] smoothingGroups = new int[triMesh.getFaces().size() / 6];
        for (int i = 0; i < smoothingGroups.length; i++)
        {
            smoothingGroups[i] = 0;
        }
        triMesh.getFaceSmoothingGroups().addAll(smoothingGroups);

        MeshView meshView = new MeshView();

        meshView.setMesh(triMesh);
        meshView.setMaterial(materialToUse);
        meshView.setCullFace(CullFace.BACK);
        meshView.setId("arrow_mesh");
        meshView.setMouseTransparent(true);

        return meshView;
    }

    private MeshView createLine(LineDirection lineDirection)
    {
        TriangleMesh triMesh = new TriangleMesh();

        switch (lineDirection)
        {
            case VERTICAL:
                triMesh.getPoints().addAll(-lineWidth / 2, 0, 0);
                triMesh.getPoints().addAll(lineWidth / 2, 0, 0);
                triMesh.getPoints().addAll(lineWidth / 2, -1, 0);
                triMesh.getPoints().addAll(-lineWidth / 2, -1, 0);
                triMesh.getFaces().addAll(0, 0, 1, 0, 3, 0);
                triMesh.getFaces().addAll(1, 0, 2, 0, 3, 0);
                break;
            case HORIZONTAL:
                triMesh.getPoints().addAll(0, -lineWidth / 2, 0);
                triMesh.getPoints().addAll(0, lineWidth / 2, 0);
                triMesh.getPoints().addAll(-1, lineWidth / 2, 0);
                triMesh.getPoints().addAll(-1, -lineWidth / 2, 0);
                triMesh.getFaces().addAll(0, 0, 1, 0, 3, 0);
                triMesh.getFaces().addAll(1, 0, 2, 0, 3, 0);
                break;
        }
        FloatArrayList texCoords = new FloatArrayList();
        texCoords.add(0f);
        texCoords.add(0f);
        triMesh.getTexCoords().addAll(texCoords.toFloatArray());

        int[] smoothingGroups = new int[triMesh.getFaces().size() / 6];
        for (int i = 0; i < smoothingGroups.length; i++)
        {
            smoothingGroups[i] = 0;
        }
        triMesh.getFaceSmoothingGroups().addAll(smoothingGroups);

        MeshView meshView = new MeshView();

        meshView.setMesh(triMesh);
        meshView.setMaterial(materialToUse);
        meshView.setCullFace(CullFace.BACK);
        meshView.setId("arrow_mesh");
        meshView.setMouseTransparent(true);

        return meshView;
    }

    void place(double minX, double maxX, double minY, double maxY, double minZ, double maxZ)
    {
        double upArrowBottom = minY + offsetFromMaxMin;
        heightUpArrowHead.setTranslateY(upArrowBottom);
        heightUpArrowHead.setTranslateX(minX);
        heightUpArrowHead.setTranslateZ(minZ);

        double downArrowTop = maxY - offsetFromMaxMin;
        heightDownArrowHead.setTranslateY(downArrowTop);
        heightDownArrowHead.setTranslateX(minX);
        heightDownArrowHead.setTranslateZ(minZ);

        double heightLineScale = Math.abs(downArrowTop - upArrowBottom) - arrowHeadLength * 2;
//        heightLine.setTranslateY(-arrowHeadLength);
        heightLine.scaleYProperty().set(heightLineScale);
        heightLine.setTranslateX(minX);
        heightLine.setTranslateZ(minZ);

        heightLabel.setTranslateY(-20);
        heightLabel.setTranslateX(minX);
        heightLabel.setTranslateZ(minZ);

        double leftArrowLeft = minY + offsetFromMaxMin;
        widthLeftArrowHead.setTranslateY(leftArrowLeft);
        widthLeftArrowHead.setTranslateX(minX);
        widthLeftArrowHead.setTranslateZ(minZ);

        double rightArrowRight = maxY - offsetFromMaxMin;
        widthRightArrowHead.setTranslateY(rightArrowRight);
        widthRightArrowHead.setTranslateX(minX);
        widthRightArrowHead.setTranslateZ(minZ);

        double widthLineScale = Math.abs(leftArrowLeft - rightArrowRight) - arrowHeadLength * 2;
        widthLine.scaleYProperty().set(widthLineScale);
        widthLine.setTranslateX(minX);
        widthLine.setTranslateZ(minZ);

        widthLabel.setTranslateY(-20);
        widthLabel.setTranslateX(minX);
        widthLabel.setTranslateZ(minZ);
    }
}
