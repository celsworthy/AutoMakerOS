package celtech.coreUI.visualisation;

import celtech.Lookup;
import celtech.appManager.ApplicationMode;
import celtech.appManager.ApplicationStatus;
import celtech.appManager.Project;
import celtech.appManager.TimelapseSettingsData;
import celtech.appManager.undo.UndoableProject;
import celtech.configuration.ApplicationConfiguration;
import celtech.modelcontrol.ProjectifiableThing;
import celtech.modelcontrol.TranslateableTwoD;
import celtech.roboxbase.configuration.datafileaccessors.PrinterContainer;
import celtech.roboxbase.configuration.fileRepresentation.PrinterDefinitionFile;
import celtech.roboxbase.configuration.fileRepresentation.PrinterSettingsOverrides;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.modelcontrol.ShapeContainer;
import celtech.roboxbase.utils.Math.MathUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author ianhudson
 * 
 * The coordinate systems can be quite confusing.
 * In JFX, the origin is the top left corner of the screen.
 * X goes left to right, Y goes top to bottom and Z goes into the screen.
 * Thus, for a 3D shape X is width, Y is height and Z is depth.
 * 
 * For a 2D shape, X is width, Y is height. (There is no Z.)
 * This seems to be how it is drawn on the screen.
 * TODO - For the bed, the origin should be in the bottom left corner, and
 * Y should go from bottom to top.
 * 
 * When a 2D shape is placed on the 3D bed, the 2D Shapes X axis lies along the 3D beds X axis,
 * and the 2D shapes Y axis lies along the 3D beds Z axis, which is very confusing.
 * 
 * There is also scope for confusion because the TranslateableTwoD class translates x and z,
 * but the shape container subclass actually modifies x and y.
 * 
 * Curiously, the ScaleableTwoD scales x and y, not x and z.
 */
public class SVGViewManager extends Pane implements Project.ProjectChangesListener
{
    private final static Stenographer steno = StenographerFactory.getStenographer(SVGViewManager.class.getName());
    private final static double LENGTH_EPSILON = 0.0005;
    private static Image bedImage = null; // Shared across all SVG views.
    private static Image proBedImage = null; // Shared across all SVG views.
    private final Project project;
    private final UndoableProject undoableProject;
    private final ApplicationStatus applicationStatus = ApplicationStatus.getInstance();
    private final ProjectSelection projectSelection;
    private ObservableList<ProjectifiableThing> loadedModels;

    private Shape shapeBeingDragged = null;
    private Point2D lastDragPosition = null;
    private double mousePosX;
    private double mousePosY;
    private double mousePreviousX;
    private double mousePreviousY;
    
    private double fitScale = 1.0;
    private double minScale = 1.0;    
    private double maxScale = 1.0;    
    private double xOffsetAtFitScale = 0.0;
    private double yOffsetAtFitScale = 0.0;

    private double bedWidth = 210;
    private double bedHeight = 150;
    private final double bedBorder = 10;
    private final Affine bedMirror = new Affine();

    private Group partsAndBed = new Group();
    private final Translate bedTranslate = new Translate();
    private final Scale bedScale = new Scale();
    private final Rectangle bed = new Rectangle(bedWidth, bedHeight);
    private final Group parts = new Group();
    private ImageView bedImageView;
    private ImageView proBedImageView;
    
    private final ObjectProperty<DragMode> dragMode = new SimpleObjectProperty(DragMode.IDLE);
    private boolean justEnteredDragMode;

    public SVGViewManager(Project project)
    {
        //steno.info("SVGViewManager");
        this.project = project;
        this.undoableProject = new UndoableProject(project);

        this.setPickOnBounds(false);
        
        getChildren().add(partsAndBed);

        createBed();
        
        // In JFX, the origin is at the top left, with the X axis
        // pointing left to right and the Y axis pointing top to bottom:
        //
        // O--X-->--------------------------------------
        // Y                                           |
        // V                                           |
        // |                                           |
        // |                                           |
        // |                                           |
        // |                                           |
        // |                                           |
        // ---------------------------------------------
        
        // The following transform mirrors in Y, and moves the origin
        // to the bottom left.
        bedMirror.setMyy(-1.0);
        bedMirror.setTy(bedHeight);

        // The bed rectangle represents the printer bed.
        // The parts group holds all the ShapeContainers. It has the origin
        // at the centre of the workspace and is translated by half the
        // bed width and height to bring it into printer space, with 
        // the origin at the front left of the bed (looking from the front
        // of the printer to the back).
        
        partsAndBed.getChildren().addAll(bed, bedImageView, proBedImageView, parts);
        
        partsAndBed.getTransforms().addAll(bedTranslate, bedScale, bedMirror);
        // getTransforms().addAll(t, s, a) are applied in the order (x') = t s a (x)
        //                                                          (y')         (y)
        //partsAndBed.getTransforms().addAll(bedTranslate, bedScale);

        projectSelection = Lookup.getProjectGUIState(project).getProjectSelection();
        loadedModels = project.getTopLevelThings();
        applicationStatus.modeProperty().addListener(applicationModeListener);

        addEventHandler(MouseEvent.ANY, mouseEventHandler);
        addEventHandler(ZoomEvent.ANY, zoomEventHandler);
        addEventHandler(ScrollEvent.ANY, scrollEventHandler);
        setStyle("-fx-background-color: blue;");

        Lookup.getSelectedPrinterProperty().addListener(new ChangeListener<Printer>()
        {
            @Override
            public void changed(ObservableValue<? extends Printer> ov, Printer t, Printer t1)
            {
                whenCurrentPrinterChanged(t1);
            }
        });
        whenCurrentPrinterChanged(Lookup.getSelectedPrinterProperty().get());

        this.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (newValue.doubleValue() > 0.0)
                resizeBed();
        });

        this.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (newValue.doubleValue() > 0.0)
                resizeBed();
        });

        /**
         * Listen for adding and removing of models from the project
         */
        project.addProjectChangesListener(this);

        for (ProjectifiableThing projectifiableThing : loadedModels)
        {
            if (projectifiableThing instanceof ShapeContainer)
            {
                ShapeContainer shape = (ShapeContainer)projectifiableThing;
                //steno.info("SVGViewManager::SVGViewManager - Bounds are " + shape.getBoundsInLocal());
                parts.getChildren().add(shape);
                shape.setBedReference(partsAndBed);
            }
        }
    }

    private void createBed()
    {
        //steno.info("createBed");
        bed.setFill(Color.LIGHTGREY);
        // The bed is in mm units
        
        URL proBedGraphicURL = getClass().getResource(ApplicationConfiguration.imageResourcePath
                + "Bed Graphic - RoboxPro.png");
        URL bedGraphicURL = getClass().getResource(ApplicationConfiguration.imageResourcePath
                + "Bed Graphic - Robox.png");
        if (bedImage == null)
            bedImage = new Image(bedGraphicURL.toExternalForm());
        bedImageView = new ImageView(bedImage);
        bedImageView.setVisible(false);
        bedImageView.setScaleY(-1);
        
        if (proBedImage == null)
            proBedImage = new Image(proBedGraphicURL.toExternalForm());
        proBedImageView = new ImageView(proBedImage);
        proBedImageView.setVisible(false);
        proBedImageView.setScaleY(-1);
    }

    private void calculateBedFitScale()
    {
        //steno.info("calculateBedFitScale");
        
        double viewAreaWidth = widthProperty().get();
        if (viewAreaWidth < 70)
            viewAreaWidth = 70;
        viewAreaWidth -= 2 * bedBorder;
        double viewAreaHeight = heightProperty().get();
        if (viewAreaHeight < 70)
            viewAreaHeight = 70;
        viewAreaHeight -= 2 * bedBorder;
        double displayAspect = viewAreaWidth / viewAreaHeight;
        double aspect = bedWidth / bedHeight;

        double newWidth = 0;
        double newHeight = 0;
        if (displayAspect >= aspect)
        {
            // Drive from height
            newWidth = viewAreaHeight * aspect;
            newHeight = viewAreaHeight;
        } else
        {
            //Drive from width
            newHeight = viewAreaWidth / aspect;
            newWidth = viewAreaWidth;
        }

        fitScale = newWidth / bedWidth;
        minScale = 0.1 * fitScale;
        maxScale = 10.0 * fitScale;

        xOffsetAtFitScale = ((viewAreaWidth - newWidth) / 2) + bedBorder;
        yOffsetAtFitScale = ((viewAreaHeight - newHeight) / 2) + bedBorder;

        //System.out.println("calculateBedFitScale");
        //System.out.println("  widthProperty = " +
        //                   Double.toString(widthProperty().get()) + 
        //                   ", heightProperty = " + 
        //                   Double.toString(heightProperty().get()));
        //System.out.println("  bed = (" +
        //                   Double.toString(bedWidth) + 
        //                   ", " + 
        //                   Double.toString(bedHeight) +
        //                   ")");
        //System.out.println("  viewArea = (" +
        //                   Double.toString(viewAreaWidth) + 
        //                   ", " + 
        //                   Double.toString(viewAreaHeight) + 
        //                   ")");
        //System.out.println("  fitScale = " +
        //                   Double.toString(fitScale));
        //System.out.println("  offsetAtFitScale = (" +
        //                   Double.toString(xOffsetAtFitScale) + 
        //                   ", " + 
        //                   Double.toString(yOffsetAtFitScale) + 
        //                   ")");
    }

    private void resizeBed()
    {
        // steno.info("resizeBed");
        calculateBedFitScale();
        
        bedScale.setX(fitScale);
        bedScale.setY(fitScale);

        bedTranslate.setX(xOffsetAtFitScale);
        bedTranslate.setY(yOffsetAtFitScale);

        //System.out.println("  originOnScreen = " + localToScreen(0,0));
        //System.out.println("  cornerOnScreen = " + localToScreen(widthProperty().get(),heightProperty().get()));
        //System.out.println("  bedOriginOnScreen = " + bed.localToScreen(0,0));
        //System.out.println("  bedCornerOnScreen = " + bed.localToScreen(bedWidth, bedHeight));

        notifyScreenExtentsChange();
    }

    @Override
    public void whenModelAdded(ProjectifiableThing projectifiableThing)
    {
        if (projectifiableThing instanceof ShapeContainer)
        {
            ShapeContainer shape = (ShapeContainer)projectifiableThing;
            parts.getChildren().add(shape);
            shape.setBedReference(partsAndBed);
            //shape.setBedCentreOffsetTransform();
    //        projectifiableThing.shrinkToFitBed();
        }
    }

    @Override
    public void whenModelsRemoved(Set<ProjectifiableThing> projectifiableThing)
    {
        //steno.info("whenModelsRemoved");
        parts.getChildren().removeAll(projectifiableThing);
    }

    @Override
    public void whenAutoLaidOut()
    {
        //steno.info("whenAutoLaidOut");
    }

    @Override
    public void whenModelsTransformed(Set<ProjectifiableThing> projectifiableThing
    )
    {
        //steno.info("whenModelsTransformed");
    }

    @Override
    public void whenModelChanged(ProjectifiableThing modelContainer, String propertyName
    )
    {
        //steno.info("whenModelChanged");
    }

    @Override
    public void whenPrinterSettingsChanged(PrinterSettingsOverrides printerSettings
    )
    {
        //steno.info("whenPrinterSettingsChanged");
    }

    private void debugPrintBounds(String message, Bounds b)
    {
        System.out.println(message +
                           "(" + 
                           Double.toString(b.getMinX()) + 
                           ", " + 
                           Double.toString(b.getMinY()) + 
                           ", " + 
                           Double.toString(b.getMaxX()) + 
                           ", " + 
                           Double.toString(b.getMaxY()) + 
                           ") [ " + 
                           Double.toString(b.getCenterX()) + 
                           ", " + 
                           Double.toString(b.getCenterY()) + 
                           "]");
    }
    
    private void debugPrintNode(String padding, Node n)
    {
        ShapeContainer sc = null;
        if (n instanceof ShapeContainer)
        {
            sc = (ShapeContainer)n;
            System.out.println(padding + sc.getClass().getSimpleName() +
                               "[" + Integer.toString(sc.getModelId()) + "]");
        }
        else
            System.out.println(padding + n.getClass().getSimpleName() +
                    "[" + Integer.toString(n.hashCode()) + "]");
        Node p = n.getParent();
        if (p instanceof ProjectifiableThing)
        {
            System.out.println(padding + "  P = " + p.getClass().getSimpleName() +
                               "[" + Integer.toString(((ProjectifiableThing)p).getModelId()) + "]");
        }
        else
            System.out.println(padding + "  P = " + p.getClass().getSimpleName() +
                    "[" + Integer.toString(p.hashCode()) + "]");
        debugPrintBounds(padding + "  S L: ", n.getBoundsInLocal());
        debugPrintBounds(padding + "  S P: ", n.getBoundsInParent());
        if (sc != null)
            sc.debugPrintTransforms(padding + "  S: ");
    }
    
    private void debugPrintAllBounds(String message)
    {
        System.out.println(message);
        debugPrintBounds("  this L: ", getBoundsInLocal());
        debugPrintBounds("  this P: ", getBoundsInParent());
        debugPrintBounds("  partsAndBed L: ", partsAndBed.getBoundsInLocal());
        debugPrintBounds("  partsAndBed P: ", partsAndBed.getBoundsInParent());
        debugPrintBounds("  bed L: ", bed.getBoundsInLocal());
        debugPrintBounds("  bed P: ", bed.getBoundsInParent());
        System.out.println("  bed O: " + sceneToLocal(bed.localToScene(0.0, 0.0)));
        List<Node> nodesToPrint = new ArrayList<>();
        int index = 0;
        nodesToPrint.addAll(parts.getChildren());
        while (index < nodesToPrint.size())
        {
            Node n = nodesToPrint.get(index);
            index++;
            debugPrintNode("  ", n);
            if (n instanceof Group)
                nodesToPrint.addAll(((Group)n).getChildren());
        }
    }
    
    @Override
    public void whenTimelapseSettingsChanged(TimelapseSettingsData timelapseSettings)
    {
    }

    private final EventHandler<MouseEvent> mouseEventHandler = event ->
    {
        mousePreviousX = mousePosX;
        mousePreviousY = mousePosY;
        mousePosX = event.getSceneX();
        mousePosY = event.getSceneY();

        //debugPrintAllBounds("mouseEvent");
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED)
        {
            //steno.info("MouseEvent.MOUSE_PRESSED");
            if (event.isPrimaryButtonDown()
                    || event.isSecondaryButtonDown())
            {
                handleMouseSingleClickedEvent(event);
            }

        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED)
        {
            //steno.info("MouseEvent.MOUSE_DRAGGED");
            dragShape(shapeBeingDragged, event);

        } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED)
        {
            //steno.info("MouseEvent.MOUSE_RELEASED");
            shapeBeingDragged = null;
            lastDragPosition = null;
            //steno.info("Setting DragMode to IDLE");
            dragMode.set(DragMode.IDLE);
        }
    };

    private final EventHandler<ZoomEvent> zoomEventHandler = event ->
    {
        //steno.info("Zoom event handler");
        if (!Double.isNaN(event.getZoomFactor()) && event.getZoomFactor() > 0.8
                && event.getZoomFactor() < 1.2)
        {
            double newScale = bedScale.getX() * event.getZoomFactor();

            bedScale.setX(newScale);
            bedScale.setY(newScale);
        }
    };

    private final EventHandler<ScrollEvent> scrollEventHandler = event ->
    {
        //steno.info("Scroll event handler");
        // What is the current position of the mouse in bed coordinates.
        mousePreviousX = mousePosX;
        mousePreviousY = mousePosY;
        mousePosX = event.getSceneX();
        mousePosY = event.getSceneY();

        setAndConstrainBedScale(bedScale.getX() + 0.01 * event.getDeltaY(), true);
    };
    
    private void setAndConstrainBedScale(double newScale, boolean scaleAboutMousePosition)
    {
        if (newScale < minScale)
            newScale = minScale;
        if (newScale > maxScale)
            newScale = maxScale;
        if (newScale != bedScale.getX())
        {
            // Change the bed offset so that the point at the centre of the view
            // (or the position under the mouse if scaleAboutMousePosition is
            // true) remains in the same place after the scale change.
            double newOffsetX = bedTranslate.getX();
            double newOffsetY = bedTranslate.getY();
            double viewPointX = mousePosX;
            double viewPointY = mousePosY;
            if (!scaleAboutMousePosition)
            {
                // use centre of view instead of mouse position.
                Point2D viewCentre = localToScene(0.5 * widthProperty().get(),
                                                  0.5 * heightProperty().get());
                viewPointX = viewCentre.getX();
                viewPointY = viewCentre.getY();
            }
            Point2D mousePaneLocal = sceneToLocal(viewPointX, viewPointY);
            Point2D mouseLocal = partsAndBed.sceneToLocal(viewPointX, viewPointY);
            newOffsetX = mousePaneLocal.getX() - newScale * mouseLocal.getX();
            newOffsetY = mousePaneLocal.getY() + newScale * (mouseLocal.getY() - bedHeight);

            bedScale.setX(newScale);
            bedScale.setY(newScale);
            setAndConstrainBedOffset(newOffsetX, newOffsetY);
            notifyScreenExtentsChange();
        }
    }
    
    private void setAndConstrainBedOffset(double proposedXOffset, double proposedYOffset)
    {
        double newOffsetX = proposedXOffset;
        double newOffsetY = proposedYOffset;
                
        bedTranslate.setX(newOffsetX);
        bedTranslate.setY(newOffsetY);
        // Adjust to ensure edges of bed do not creep around screen.
        //Bounds parentBounds = getBoundsInLocal();
        Bounds pbBounds = partsAndBed.getBoundsInParent();
        double viewAreaWidth = widthProperty().get();
        double viewAreaHeight = heightProperty().get();
        double bedAreaWidth = bedScale.getX() * bedWidth;
        double bedAreaHeight = bedScale.getY() * bedHeight;

        if (bedAreaWidth <= viewAreaWidth)
        {
            // Bed width is smaller than parent.
            // Centre in parent.
            newOffsetX = 0.5 * (viewAreaWidth - bedAreaWidth);
        }
        else if (newOffsetX < viewAreaWidth - bedAreaWidth)
        {
            // Bed width is larger than parent, but right edge is inside  parent.
            // Keep right edge at right of parent.
            newOffsetX = viewAreaWidth - bedAreaWidth;
        }
        else if (pbBounds.getMinX() > 0.0)
        {
            // Bed width is larger than parent, but left edge is inside  parent.
            // Keep left edge at left of parent.
            newOffsetX = 0.0;
        }

        if (bedAreaHeight <= viewAreaHeight)
        {
            // Bed height is smaller than parent.
            // Centre in parent.
            newOffsetY = 0.5 * (viewAreaHeight - bedAreaHeight);
        }
        else if (newOffsetY < viewAreaHeight - bedAreaHeight)
        {
            // Bed width is larger than parent, but bottom edge is inside  parent.
            // Keep bottom edge at bottom of parent.
            newOffsetY = viewAreaHeight - bedAreaHeight;
        }
        else if (newOffsetY > 0.0)
        {
            // Bed width is larger than parent, but top edge is inside  parent.
            // Keep top edge at top of parent.
            newOffsetY = 0.0;
        }

        bedTranslate.setX(newOffsetX);
        bedTranslate.setY(newOffsetY);
    }

    private void notifyScreenExtentsChange()
    {
        if (loadedModels != null)
            loadedModels.forEach(m -> m.notifyScreenExtentsChange());
    }
    
    private ShapeContainer findShapeContainerParent(Node shape)
    {
        //steno.info("findShapeContainerParent");
        ShapeContainer sc = null;
        Node currentNode = shape;

        while (currentNode != null)
        {
            if (currentNode instanceof ShapeContainer)
            {
                sc = (ShapeContainer) currentNode;
            }
            currentNode = currentNode.getParent();
        }

        return sc;
    }

    private void handleMouseSingleClickedEvent(MouseEvent event)
    {
        //steno.info("handleMouseSingleClickedEvent");
        boolean handleThisEvent = true;
        //steno.info("    source: " + event.getSource());
        //steno.info("    target: " + event.getTarget());
        PickResult pickResult = event.getPickResult();
        Point3D pickedPoint = pickResult.getIntersectedPoint();
        Node intersectedNode = pickResult.getIntersectedNode();
        //steno.info("   picked: " + intersectedNode + " @ " + pickedPoint);
                
        boolean shortcut = event.isShortcutDown();

        if (event.isPrimaryButtonDown())
        {
            //steno.info("    event.isPrimaryButtonDown");
            if (intersectedNode != bed
                    && intersectedNode instanceof Shape)
            {
                ShapeContainer sc = findShapeContainerParent(intersectedNode);
                if (sc != null)
                {
                    if (event.isShortcutDown())
                    {
                        projectSelection.addSelectedItem(sc);
                    } else
                    {
                        projectSelection.deselectAllModels();
                        projectSelection.addSelectedItem(sc);
                    }
                }
                //steno.info("    Setting drag mode to TRANSLATING");
                dragMode.set(DragMode.TRANSLATING);
                justEnteredDragMode = true;
                dragShape((Shape) intersectedNode, event);
            } else
            {
                projectSelection.deselectAllModels();
            }
        }
        else if (event.isSecondaryButtonDown())
        {
            //steno.info("    event.isSecondaryButtonDown");
            shapeBeingDragged = bed;
            dragMode.set(DragMode.TRANSLATING);
            justEnteredDragMode = true;
            lastDragPosition = null;
            dragShape(bed, event);
            
////            intersectedNode.fireEvent(new ContextMenuEvent(ContextMenuEvent.CONTEXT_MENU_REQUESTED,
////                    event.getX(), event.getY(),
////                    event.getScreenX(), event.getScreenY(),
////                    false,
////                    pickResult));
        }
    }

    private void dragShape(Shape shapeToDrag, MouseEvent event)
    {
        //steno.info("dragShape");
        Point2D newPosition = new Point2D(event.getSceneX(), event.getSceneY());
        if (shapeBeingDragged != null && lastDragPosition != null)
        {
            //steno.info("New position = " + newPosition);
            Point2D positionDelta = partsAndBed.sceneToLocal(newPosition).subtract(partsAndBed.sceneToLocal(lastDragPosition));
            //steno.info("Position delta = " + positionDelta);
            if (shapeBeingDragged == bed)
            {
                if (!event.isShiftDown() && !event.isAltDown() && positionDelta.magnitude() > LENGTH_EPSILON)
                {
                    // bedTranslate is applied after bedScale and bedMirror, so these transforms
                    // must be applied to the positionDelta.
                    setAndConstrainBedOffset(bedTranslate.getX() + bedScale.getX() * positionDelta.getX(),
                                             bedTranslate.getY() - bedScale.getY() * positionDelta.getY());
                    notifyScreenExtentsChange();
                }
                else if (event.isAltDown() && !event.isShiftDown() && !event.isControlDown())
                {
                    double mouseDeltaX = mousePosX - mousePreviousX;
                    double mouseDeltaY = mousePosY - mousePreviousY;
                    double scaleDelta = 0.01 * Math.sqrt(mouseDeltaX * mouseDeltaX + mouseDeltaY * mouseDeltaY);
                    if (mouseDeltaY > 0.0)
                        scaleDelta = -scaleDelta;
                    setAndConstrainBedScale(bedScale.getX() + scaleDelta, false);
                }
            } 
            else if (shapeBeingDragged != bed) 
            {
                undoableProject.translateModelsBy(projectSelection.getSelectedModelsSnapshot(TranslateableTwoD.class), positionDelta.getX(), positionDelta.getY(),
                        !justEnteredDragMode);
            }

            justEnteredDragMode = false;
        }
        shapeBeingDragged = shapeToDrag;
        lastDragPosition = newPosition;
    }

    private final ChangeListener<ApplicationMode> applicationModeListener
            = (ObservableValue<? extends ApplicationMode> ov, ApplicationMode oldMode, ApplicationMode newMode) ->
            {
                //steno.info("applicationModeListener");
                if (oldMode != newMode)
                {
                    switch (newMode)
                    {
                        case SETTINGS:
                            removeEventHandler(MouseEvent.ANY, mouseEventHandler);
                            removeEventHandler(ZoomEvent.ANY, zoomEventHandler);
                            removeEventHandler(ScrollEvent.ANY, scrollEventHandler);
                            deselectAllModels();
                            break;
                        default:
                            addEventHandler(MouseEvent.ANY, mouseEventHandler);
                            addEventHandler(ZoomEvent.ANY, zoomEventHandler);
                            addEventHandler(ScrollEvent.ANY, scrollEventHandler);
                            break;
                    }
//                    updateModelColours();
                }
            };

    private void selectModel(ShapeContainer selectedNode, boolean multiSelect)
    {
        //steno.info("selectModel");
        if (selectedNode == null)
        {
            projectSelection.deselectAllModels();
        } else if (selectedNode.isSelected() == false)
        {
            if (multiSelect == false)
            {
                projectSelection.deselectAllModels();
            }
            projectSelection.addSelectedItem(selectedNode);
        }
    }

    private void deselectAllModels()
    {
        //steno.info("deselectAllModels");
        for (ProjectifiableThing modelContainer : loadedModels)
        {
            deselectModel((ShapeContainer) modelContainer);
        }
    }

    public void deselectModel(ShapeContainer pickedModel)
    {
        //steno.info("deselectModel");
        if (pickedModel.isSelected())
        {
            projectSelection.removeModelContainer(pickedModel);
        }
    }
    
    public ReadOnlyObjectProperty<DragMode> getDragModeProperty()
    {
        return dragMode;
    }
     
    private void whenCurrentPrinterChanged(Printer printer)
    {
        PrinterDefinitionFile printerConfiguration = null;
        if (printer != null &&
            printer.printerConfigurationProperty().get() != null)
        {
            printerConfiguration = printer.printerConfigurationProperty().get();
        }
        else
        {
            printerConfiguration = PrinterContainer.getPrinterByID(PrinterContainer.defaultPrinterID);
        }
        deselectAllModels();

        bedWidth = printerConfiguration.getPrintVolumeWidth();
        bedHeight = printerConfiguration.getPrintVolumeDepth();
        ImageView bedView = null;
        if (printerConfiguration.getTypeCode().equals("RBX10"))
        {
            bedImageView.setVisible(false);
            bedView = proBedImageView;
        }
        else
        {
            proBedImageView.setVisible(false);
            bedView = bedImageView;
        }   
        bed.setWidth(bedWidth);
        bed.setHeight(bedHeight);

        bedView.setVisible(true);
        bedView.setFitWidth(bedWidth);
        bedView.setFitHeight(bedHeight);

        // Bed mirror transform also translates by the bed height so the origin is at
        // the bottom left after the mirror.
        bedMirror.setTy(bedHeight);
        resizeBed();
        
        // The parts group holds all the ShapeContainers in the project and has the origin
        // at the centre of the workspace.
        //
        // The bed has the origin at front left corner (looking from the front of the printer to the back).
        // The parts group is translated by half the bed width and height to place
        // the origin of the group at the centre of the bed.
        //
        if (parts != null)
        {
            parts.setTranslateX(0.5 * bedWidth);
            parts.setTranslateY(0.5 * bedHeight);
        }
    }

}
