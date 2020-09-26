/*
 * Copyright 2015 CEL UK
 */
package celtech.utils.threed;

import celtech.coreUI.visualisation.ApplicationMaterials;
import celtech.modelcontrol.ModelContainer;
import static celtech.utils.threed.MeshCutter2.makePoint3D;
import static celtech.utils.threed.MeshSeparator.addPointToMesh;
import static celtech.utils.threed.MeshSeparator.setTextureAndSmoothing;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.text.Text;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.triangulation.TriangulationPoint;


/**
 *
 * @author tony
 */
public class MeshDebug
{

    static ModelContainer node;

    static Set<Node> nodesToShow = new HashSet<>();

    static void clearNodesToShow()
    {
        nodesToShow.clear();
    }

    static void showFaceCentres(List<Integer> cutFaces, TriangleMesh mesh)
    {
        for (Integer faceIndex : cutFaces)
        {
            int v0 = mesh.getFaces().get(faceIndex * 6);
            int v1 = mesh.getFaces().get(faceIndex * 6 + 2);
            int v2 = mesh.getFaces().get(faceIndex * 6 + 4);
            double x0 = mesh.getPoints().get(v0 * 3);
            double y0 = mesh.getPoints().get(v0 * 3 + 1);
            double z0 = mesh.getPoints().get(v0 * 3 + 2);
            double x1 = mesh.getPoints().get(v1 * 3);
            double y1 = mesh.getPoints().get(v1 * 3 + 1);
            double z1 = mesh.getPoints().get(v1 * 3 + 2);
            double x2 = mesh.getPoints().get(v2 * 3);
            double y2 = mesh.getPoints().get(v2 * 3 + 1);
            double z2 = mesh.getPoints().get(v2 * 3 + 2);
            double xMin = Math.min(x0, Math.min(x1, x2));
            double xMax = Math.max(x0, Math.max(x1, x2));
            double x = (xMin + xMax) / 2;
            double yMin = Math.min(y0, Math.min(y1, y2));
            double yMax = Math.max(y0, Math.max(y1, y2));
            double y = (yMin + yMax) / 2;
            double zMin = Math.min(z0, Math.min(z1, z2));
            double zMax = Math.max(z0, Math.max(z1, z2));
            double z = (zMin + zMax) / 2;
            Sphere sphere = new Sphere(0.5);
            sphere.translateXProperty().set((x0 + x1 + x2) / 3.0);
            sphere.translateYProperty().set((y0 + y1 + y2) / 3.0);
            sphere.translateZProperty().set((z0 + z1 + z2) / 3.0);
            sphere.setMaterial(ApplicationMaterials.getDefaultModelMaterial());
            Text text = new Text(Integer.toString(faceIndex));
            text.translateXProperty().set((x0 + x1 + x2) / 3.0);
            text.translateYProperty().set((y0 + y1 + y2) / 3.0);
            text.translateZProperty().set((z0 + z1 + z2) / 3.0);

            if (node != null)
            {
                node.addChildNode(sphere);
                node.addChildNode(text);
            }
        }
    }

    static void showFace(TriangleMesh mesh, int faceIndex)
    {
        TriangleMesh triangle = new TriangleMesh();
        int[] vertices = new int[6];
        vertices[0] = mesh.getFaces().get(faceIndex * 6);
        vertices[2] = mesh.getFaces().get(faceIndex * 6 + 2);
        vertices[4] = mesh.getFaces().get(faceIndex * 6 + 4);
        triangle.getFaces().addAll(vertices);
        addPointToMesh(mesh, vertices[0], triangle);
        addPointToMesh(mesh, vertices[2], triangle);
        addPointToMesh(mesh, vertices[4], triangle);
        setTextureAndSmoothing(triangle, triangle.getFaces().size() / 6);
        MeshView meshView = new MeshView(triangle);
        meshView.setMaterial(ApplicationMaterials.pickedGCodeMaterial);
        if (node != null)
        {
            node.addChildNode(meshView);
        }
    }

    static void showSphere(double x, double y, double z)
    {
        Sphere sphere = new Sphere(0.5);
        sphere.translateXProperty().set(x);
        sphere.translateYProperty().set(y);
        sphere.translateZProperty().set(z);
        sphere.setMaterial(ApplicationMaterials.getDefaultModelMaterial());
        if (node != null)
        {
            node.addChildNode(sphere);
        } else
        {
            nodesToShow.add(sphere);
        }
    }

    public static void setDebuggingNode(ModelContainer node)
    {
        MeshDebug.node = node;
        for (Node nodeToShow : nodesToShow)
        {
            node.addChildNode(nodeToShow);
        }
        nodesToShow.clear();
    }

    static void showNewVertices(List<Integer> newVertices, TriangleMesh mesh)
    {
        if (node != null)
        {
            for (Integer newVertex : newVertices)
            {
                Sphere sphere = new Sphere(0.5);
                sphere.translateXProperty().set(mesh.getPoints().get(newVertex * 3));
                sphere.translateYProperty().set(mesh.getPoints().get(newVertex * 3 + 1));
                sphere.translateZProperty().set(mesh.getPoints().get(newVertex * 3 + 2));
                sphere.setMaterial(ApplicationMaterials.getOffBedModelMaterial());
                node.addChildNode(sphere);
            }
        }
    }

    static void showIncomingMesh(TriangleMesh mesh)
    {
        System.out.println(mesh.getVertexFormat());
        System.out.println(mesh.getVertexFormat().getVertexIndexSize());
        System.out.println(mesh.getVertexFormat().getPointIndexOffset());
        for (int i = 0; i < mesh.getPoints().size() / 3; i++)
        {
            System.out.println("point " + i + " is " + mesh.getPoints().get(i * 3) + " "
                + mesh.getPoints().get(i * 3 + 1) + " " + mesh.getPoints().get(i * 3 + 2));
            showSphere(mesh.getPoints().get(i * 3), mesh.getPoints().get(i * 3 + 1),
                       mesh.getPoints().get(i * 3 + 2));
        }
        for (int i = 0; i < mesh.getFaces().size() / 6; i++)
        {
            System.out.println("face " + i + " is " + mesh.getFaces().get(i * 6) + " "
                + mesh.getFaces().get(i * 6 + 2) + " " + mesh.getFaces().get(i * 6 + 4));
        }
    }

    static boolean close = false;

    static void close()
    {
        close = true;
    }

    static void visualiseEdgeLoops(Set<ManifoldEdge> nonManifoldEdges, Set<List<ManifoldEdge>> loops)
    {

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                JFrame f = new JFrame("Edge Loops");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                MyPanel panel = new MyPanel();
                f.add(panel);
                JButton button = new JButton("Quit");
                panel.add(button);
                panel.showLoops(loops);
                panel.showNonManifoldEdges(nonManifoldEdges);

                button.addActionListener(new ActionListener()
                {

                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e)
                    {
                        close();
                    }

                });
                panel.addMouseListener(new MouseAdapter()
                {
                    @Override
                    public void mousePressed(java.awt.event.MouseEvent e)
                    {
                        panel.startMouse(e.getX(), e.getY());
                        panel.requestFocusInWindow();
                    }

                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent e)
                    {
                        panel.endMouse(e.getX(), e.getY());
                    }
                });
                panel.addKeyListener(new KeyAdapter()
                {
                    @Override
                    public void keyTyped(KeyEvent e)
                    {
                        System.out.println("key typed");
                        if (e.getKeyChar() == 'z')
                        {
                            panel.zoomIn();
                        } else if (e.getKeyChar() == 'Z')
                        {
                            panel.zoomOut();
                        }
                    }
                });
                f.pack();
                f.setSize(1200, 1200);
                f.setVisible(true);
            }

        });
        while (true)
        {
            if (close)
            {
                break;
            }
            try
            {
                Thread.sleep(200);
            } catch (InterruptedException ex)
            {
                Logger.getLogger(MeshDebug.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    static void visualiseDLPolygon(Polygon outerPolygon)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                JFrame f = new JFrame("Edge Loops");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                MyPanel panel = new MyPanel();
                f.add(panel);
                JButton button = new JButton("Quit");
                panel.add(button);
                panel.showPolygon(outerPolygon);
                button.addActionListener(new ActionListener()
                {

                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e)
                    {
                        close();
                        f.setVisible(false);
                    }

                });
                panel.addMouseListener(new MouseAdapter()
                {
                    @Override
                    public void mousePressed(java.awt.event.MouseEvent e)
                    {
                        panel.startMouse(e.getX(), e.getY());
                        panel.requestFocusInWindow();
                    }

                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent e)
                    {
                        panel.endMouse(e.getX(), e.getY());
                    }
                });
                panel.addKeyListener(new KeyAdapter()
                {
                    @Override
                    public void keyTyped(KeyEvent e)
                    {
                        System.out.println("key typed");
                        if (e.getKeyChar() == 'z')
                        {
                            panel.zoomIn();
                        } else if (e.getKeyChar() == 'Z')
                        {
                            panel.zoomOut();
                        }
                    }
                });

                f.pack();
                f.setSize(1200, 1200);
                f.setVisible(true);
            }

        });
        while (true)
        {
            if (close)
            {
                break;
            }
            try
            {
                Thread.sleep(200);
            } catch (InterruptedException ex)
            {
                Logger.getLogger(MeshDebug.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    static void visualisePolygonIndices(TriangleMesh mesh,
        Set<PolygonIndices> loopsOfVertices1, Set<PolygonIndices> loopsOfVertices2,
        MeshCutter2.BedToLocalConverter bedToLocalConverter,
        java.awt.Color color1, java.awt.Color color2)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                JFrame f = new JFrame("Edge Loops");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                MyPanel panel = new MyPanel();
                f.add(panel);
                JButton button = new JButton("Quit");
                panel.add(button);
                panel.showPolygonIndices(mesh, loopsOfVertices1, loopsOfVertices2,
                                         bedToLocalConverter, color1, color2);
                button.addActionListener(new ActionListener()
                {

                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e)
                    {
                        close();
                        f.setVisible(false);
                    }

                });
                panel.addMouseListener(new MouseAdapter()
                {
                    @Override
                    public void mousePressed(java.awt.event.MouseEvent e)
                    {
                        panel.startMouse(e.getX(), e.getY());
                        panel.requestFocusInWindow();
                    }

                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent e)
                    {
                        panel.endMouse(e.getX(), e.getY());
                    }
                });
                panel.addKeyListener(new KeyAdapter()
                {
                    @Override
                    public void keyTyped(KeyEvent e)
                    {
                        System.out.println("key typed");
                        if (e.getKeyChar() == 'z')
                        {
                            panel.zoomIn();
                        } else if (e.getKeyChar() == 'Z')
                        {
                            panel.zoomOut();
                        }
                    }
                });

                f.pack();
                f.setSize(1200, 1200);
                f.setVisible(true);
            }

        });
        while (true)
        {
            if (close)
            {
                break;
            }
            try
            {
                Thread.sleep(200);
            } catch (InterruptedException ex)
            {
                Logger.getLogger(MeshDebug.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}


class MyPanel extends JPanel
{

    Set<List<ManifoldEdge>> loops;
    private Set<ManifoldEdge> nonManifoldEdges;
    private Polygon outerPolygon;

    double zoomCentreX, zoomCentreY;
    double zoomFactor = 1.0d;
    private TriangleMesh mesh;
    private Set<PolygonIndices> polygonIndices1;
    private MeshCutter2.BedToLocalConverter bedToLocalConverter;
    private Color polygonIndicesColor1;
    private Set<PolygonIndices> polygonIndices2;
    private Color polygonIndicesColor2;

    public MyPanel()
    {
        setBorder(BorderFactory.createLineBorder(Color.black));
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(250, 200);
    }

    public void paintComponent(Graphics g1)
    {
        final Graphics2D g = (Graphics2D) g1.create();
        super.paintComponent(g);

        double scale = 1d;
        int xOffset = 0;
        int yOffset = 0;

        g.drawOval(xOffset - 15, yOffset - 15, 30, 30);

        if (nonManifoldEdges != null)
        {

            double minX = Double.MAX_VALUE;
            double maxX = -Double.MAX_VALUE;
            double minZ = Double.MAX_VALUE;
            double maxZ = -Double.MAX_VALUE;
            for (ManifoldEdge edge : nonManifoldEdges)
            {
                if (edge.point0.getX() < minX)
                {
                    minX = edge.point0.getX();
                }
                if (edge.point0.getX() > maxX)
                {
                    maxX = edge.point0.getX();
                }
                if (edge.point1.getX() < minX)
                {
                    minX = edge.point1.getX();
                }
                if (edge.point1.getX() > maxX)
                {
                    maxX = edge.point1.getX();
                }
                if (edge.point0.getZ() < minZ)
                {
                    minZ = edge.point0.getZ();
                }
                if (edge.point0.getZ() > maxZ)
                {
                    maxZ = edge.point0.getZ();
                }
                if (edge.point1.getZ() < minZ)
                {
                    minZ = edge.point1.getZ();
                }
                if (edge.point1.getZ() > maxZ)
                {
                    maxZ = edge.point1.getZ();
                }
            }

            double scaleX = getWidth() / (maxX - minX);
            double scaleZ = getHeight() / (maxZ - minZ);
            scale = zoomFactor * (Math.min(scaleX, scaleZ) / 1.5d);
            System.out.println("zoom is " + zoomFactor + " scale is " + scale);

            xOffset = -(int) ((minX) * scale) + (int) zoomCentreX;
            yOffset = -(int) ((minZ) * scale) + (int) zoomCentreY;
            System.out.println("offsets: " + xOffset + " " + yOffset);

            g.setColor(Color.green);
            int i = 0;
            for (ManifoldEdge edge : nonManifoldEdges)
            {
//                System.out.println(
//                    "draw " + 
//                        (xOffset + (int) edge.point0.getX() * scale) + " "  +
//                        (yOffset + (int) edge.point0.getZ() * scale) + " " + 
//                        (xOffset + (int) edge.point1.getX() * scale) + " " + 
//                        (yOffset + (int) edge.point1.getZ() * scale));
                g.drawLine(xOffset + (int) (edge.point0.getX() * scale),
                           yOffset + (int) (edge.point0.getZ() * scale),
                           xOffset + (int) (edge.point1.getX() * scale),
                           yOffset + (int) (edge.point1.getZ() * scale));
                g.drawOval(xOffset - 5 + (int) ((edge.point0.getX() + edge.point1.getX()) / 2d
                    * scale),
                           yOffset - 5 + (int) ((edge.point0.getZ() + edge.point1.getZ()) / 2d
                           * scale),
                           10, 10);
//                g.drawString("" + i,
//                             xOffset - 5 + (int) ((edge.point0.getX() + edge.point1.getX()) / 2d
//                             * scale) + 10,
//                             yOffset - 5 + (int) ((edge.point0.getZ() + edge.point1.getZ()) / 2d
//                             * scale) + 10);
                i++;
            }
        }

        if (loops != null)
        {
            g.setColor(Color.red);
            for (List<ManifoldEdge> loop : loops)
            {
//                System.out.println("draw loop");
                int i = 0;
                for (ManifoldEdge edge : loop)
                {
//                    System.out.println("draw edge " + edge);
//                    System.out.println(edge.vertex0.x + "," + edge.vertex0.z);
                    g.drawLine(xOffset + (int) (edge.point0.getX() * scale),
                               yOffset + (int) (edge.point0.getZ() * scale),
                               xOffset + (int) (edge.point1.getX() * scale),
                               yOffset + (int) (edge.point1.getZ() * scale));
                    g.setFont(new Font("Verdana", Font.BOLD, 7));
                    g.drawString("" + i,
                                 xOffset + (int) (edge.point0.getX() * scale) + 10,
                                 yOffset + (int) (edge.point0.getZ() * scale) + 10);
                    i++;
                }
            }
        }

        if (outerPolygon != null)
        {
            double minX = Double.MAX_VALUE;
            double maxX = -Double.MAX_VALUE;
            double minZ = Double.MAX_VALUE;
            double maxZ = -Double.MAX_VALUE;
            for (TriangulationPoint point : outerPolygon.getPoints())
            {
                double x = point.getX();
                double z = point.getY();
                if (x < minX)
                {
                    minX = x;
                }
                if (x > maxX)
                {
                    maxX = x;
                }
                if (z < minZ)
                {
                    minZ = z;
                }
                if (z > maxZ)
                {
                    maxZ = z;
                }
            }
            double width = getWidth();
            double scaleX = width / (maxX - minX);
            double scaleZ = getHeight() / (maxZ - minZ);
            scale = zoomFactor * Math.min(scaleX, scaleZ) / 1.5d;
//            System.out.println("scale is " + scale);

            xOffset -= minX * scale;
            yOffset -= minZ * scale;
//            System.out.println("offsets " + xOffset + "," + yOffset);
            drawPolygon(outerPolygon, g, xOffset, yOffset, scale, Color.BLUE);
        }

        if (polygonIndices1 != null)
        {
            drawPolygonIndices(g, polygonIndices1, polygonIndicesColor1);
        }
        if (polygonIndices2 != null)
        {
            drawPolygonIndices(g, polygonIndices2, polygonIndicesColor2);
        }
    }

    private void drawPolygonIndices(final Graphics2D g, Set<PolygonIndices> polygonIndices,
        Color color)
    {
        double scale;
        int xOffset;
        int yOffset;
        Set<Integer> allVertexIndices = new HashSet<>();
        for (List<Integer> loop : polygonIndices)
        {
            allVertexIndices.addAll(loop);
        }
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;
        for (Integer vertexIndex : allVertexIndices)
        {
            Point3D point = bedToLocalConverter.localToBed(makePoint3D(mesh, vertexIndex));
            double x = point.getX();
            double z = point.getZ();
            if (x < minX)
            {
                minX = x;
            }
            if (x > maxX)
            {
                maxX = x;
            }
            if (z < minZ)
            {
                minZ = z;
            }
            if (z > maxZ)
            {
                maxZ = z;
            }
        }
        double width = getWidth();
        double scaleX = width / (maxX - minX);
        double scaleZ = getHeight() / (maxZ - minZ);
        scale = zoomFactor * (Math.min(scaleX, scaleZ) / 1.5d);
        System.out.println("zoom is " + zoomFactor + " scale is " + scale);
        xOffset = -(int) ((minX) * scale) + (int) zoomCentreX;
        yOffset = -(int) ((minZ) * scale) + (int) zoomCentreY;
        System.out.println("offsets: " + xOffset + " " + yOffset);
        g.setColor(color);
        g.setFont(new Font("Verdana", Font.BOLD, 7));

        for (List<Integer> loop : polygonIndices)
        {
            Point3D startPoint = bedToLocalConverter.localToBed(makePoint3D(mesh, loop.get(0)));
            double startX = startPoint.getX() * scale;
            double startY = startPoint.getZ() * scale;
            double beginX = startX;
            double beginY = startY;
            double endX = 0, endY = 0;
            for (int i = 1; i < loop.size(); i++)
            {
                Point3D point = bedToLocalConverter.localToBed(makePoint3D(mesh, loop.get(i)));
                endX = point.getX() * scale;
                endY = point.getZ() * scale;
                g.drawLine(xOffset + (int) (startX),
                           yOffset + (int) (startY),
                           xOffset + (int) (endX),
                           yOffset + (int) (endY));
//                    System.out.println("draw " +
//                        (xOffset + (int) (startX)) + "," + 
//                        (yOffset + (int) (startY)) + " " +
//                        (xOffset + (int) (endX)) + "," +
//                        (yOffset + (int) (endY)));
                startX = endX;
                startY = endY;

                
                g.drawString("" + i,
                             (int) (xOffset + (int) (startX)) + 10,
                             (int) (yOffset + (int) (startY)) + 10);
            }
            g.drawLine(xOffset + (int) (endX),
                       yOffset + (int) (endY),
                       xOffset + (int) (beginX),
                       yOffset + (int) (beginY));
        }
    }

    private void drawPolygon(Polygon polygon, final Graphics2D g,
        int xOffset, int yOffset, double scale, Color color)
    {
        g.setColor(color);
        TriangulationPoint startPoint = polygon.getPoints().get(0);
        double startX = startPoint.getX() * scale;
        double startY = startPoint.getY() * scale;
        double beginX = startX;
        double beginY = startY;
        double endX = 0, endY = 0;
        for (TriangulationPoint point : polygon.getPoints())
        {
            endX = point.getX() * scale;
            endY = point.getY() * scale;
            g.drawLine(xOffset + (int) (startX),
                       yOffset + (int) (startY),
                       xOffset + (int) (endX),
                       yOffset + (int) (endY));
//            System.out.println("draw " + startX + "," + startY + " " + endX + "," + endY);
            startX = endX;
            startY = endY;
        }
        g.drawLine(xOffset + (int) (endX),
                   yOffset + (int) (endY),
                   xOffset + (int) (beginX),
                   yOffset + (int) (beginY));
//        if (polygon.getHoles() != null)
//        {
//            for (Polygon hole : polygon.getHoles())
//            {
//                drawPolygon(hole, g, xOffset, yOffset, scale, Color.ORANGE);
//            }
//        }
    }

    void showLoops(Set<List<ManifoldEdge>> loops)
    {
        this.loops = loops;
    }

    void showNonManifoldEdges(Set<ManifoldEdge> nonManifoldEdges)
    {
        this.nonManifoldEdges = nonManifoldEdges;
    }

    void showPolygon(Polygon outerPolygon)
    {
        this.outerPolygon = outerPolygon;
    }

    void selectZoomCentre(int x, int z)
    {
        zoomCentreX = x;
        zoomCentreY = z;
        repaint();
    }

    void zoomIn()
    {
        zoomFactor *= 2;
        repaint();
    }

    void zoomOut()
    {
        zoomFactor /= 2;
        zoomCentreX = 0;
        zoomCentreY = 0;
        repaint();
    }

    int startMouseX, startMouseY;

    void startMouse(int x, int y)
    {
        startMouseX = x;
        startMouseY = y;
    }

    void endMouse(int x, int y)
    {
        zoomCentreX += x - startMouseX;
        zoomCentreY += y - startMouseY;
        System.out.println("zoom centre set to " + zoomCentreX + " " + zoomCentreY);
        repaint();
    }

    void showPolygonIndices(TriangleMesh mesh, Set<PolygonIndices> loopsOfVertices1,
        Set<PolygonIndices> loopsOfVertices2,
        MeshCutter2.BedToLocalConverter bedToLocalConverter, Color color1, Color color2)
    {
        this.mesh = mesh;
        this.polygonIndices1 = loopsOfVertices1;
        this.polygonIndices2 = loopsOfVertices2;
        this.bedToLocalConverter = bedToLocalConverter;
        this.polygonIndicesColor1 = color1;
        this.polygonIndicesColor2 = color2;
    }
}
