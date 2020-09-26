package celtech.coreUI.visualisation;

import celtech.Lookup;
import celtech.appManager.ApplicationMode;
import celtech.appManager.ApplicationStatus;
import celtech.appManager.Project;
import celtech.appManager.TimelapseSettingsData;
import celtech.appManager.undo.UndoableProject;
import celtech.coreUI.visualisation.svg.PrintableShape;
import celtech.coreUI.visualisation.svg.TextPath;
import celtech.modelcontrol.ProjectifiableThing;
import celtech.modelcontrol.TranslateableTwoD;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.fileRepresentation.PrinterSettingsOverrides;
import celtech.roboxbase.importers.twod.svg.DragKnifeCompensator;
import celtech.roboxbase.importers.twod.svg.SVGConverterConfiguration;
import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.StylusLiftNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.StylusPlungeNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.StylusScribeNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.StylusSwivelNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.TravelNode;
import celtech.roboxbase.postprocessor.stylus.PrintableShapesToGCode;
import celtech.roboxbase.utils.models.PrintableShapes;
import celtech.roboxbase.utils.models.ShapeForProcessing;
import celtech.utils.threed.importers.svg.ShapeContainer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
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
 */
public class SVGViewManager extends Pane implements Project.ProjectChangesListener
{

    private final Stenographer steno = StenographerFactory.getStenographer(SVGViewManager.class.getName());
    private final Project project;
    private final UndoableProject undoableProject;
    private final ApplicationStatus applicationStatus = ApplicationStatus.getInstance();
    private final ProjectSelection projectSelection;
    private ObservableList<ProjectifiableThing> loadedModels;

    private Shape shapeBeingDragged = null;
    private Point2D lastDragPosition = null;
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;

    private final double bedWidth = 210;
    private final double bedHeight = 150;
    private final double bedBorder = 10;

    private Group partsAndBed = new Group();
    private final Translate bedTranslate = new Translate();
    private final Scale bedScale = new Scale();
    private final Rectangle bed = new Rectangle(bedWidth, bedHeight);
    private final Group parts = new Group();
    private final Group gCodeOverlay = new Group();

    private Pane parentPane = null;

    private final ObjectProperty<DragMode> dragMode = new SimpleObjectProperty(DragMode.IDLE);
    private boolean justEnteredDragMode;

    private ContextMenu bedContextMenu = null;
    
    public SVGViewManager(Project project)
    {
        this.project = project;
        this.undoableProject = new UndoableProject(project);

        parentPane = this;

        this.setPickOnBounds(false);

        createBed();

        partsAndBed.getChildren().addAll(bed, parts);
        partsAndBed.getTransforms().addAll(bedTranslate, bedScale);

        Affine jfxToRealWorld = new Affine();
        jfxToRealWorld.appendScale(1, -1);
        jfxToRealWorld.appendTranslation(0, -bedHeight);

        gCodeOverlay.getTransforms().addAll(bedTranslate, bedScale, jfxToRealWorld);

        getChildren().add(partsAndBed);
        getChildren().add(gCodeOverlay);

        projectSelection = Lookup.getProjectGUIState(project).getProjectSelection();
        loadedModels = project.getTopLevelThings();

        applicationStatus.modeProperty().addListener(applicationModeListener);

        parentPane.addEventHandler(MouseEvent.ANY, mouseEventHandler);
        parentPane.addEventHandler(ZoomEvent.ANY, zoomEventHandler);
        parentPane.addEventHandler(ScrollEvent.ANY, scrollEventHandler);

//        setStyle("-fx-background-color: blue;");
        this.maxWidthProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
            {
                resizeBed();
            }
        });

        this.maxHeightProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
            {
                resizeBed();
            }
        });

        /**
         * Listen for adding and removing of models from the project
         */
        project.addProjectChangesListener(this);

        for (ProjectifiableThing projectifiableThing : project.getAllModels())
        {
            projectifiableThing.setBedReference(gCodeOverlay);
            parts.getChildren().add(projectifiableThing);
//            projectifiableThing.shrinkToFitBed();
        }
    }

    private void createBed()
    {
        bed.setFill(Color.ANTIQUEWHITE);
        // The bed is in mm units

        bedContextMenu = new ContextMenu();

        String cm1Text = "Add Text";
        String cm2Text = "Add Box";
        String cm3Text = "Add Circle";
        String cm4Text = "Generate GCode";

        MenuItem addTextMenuItem = new MenuItem(cm1Text);
        MenuItem addRectangleMenuItem = new MenuItem(cm2Text);
        MenuItem addCircleMenuItem = new MenuItem(cm3Text);
        MenuItem generateGCodeMenuItem = new MenuItem(cm4Text);

        addTextMenuItem.setOnAction((ActionEvent e) ->
        {
            TextPath newPath = new TextPath();

            ShapeContainer newShapeContainer = new ShapeContainer("Text", newPath);

            Point2D pointToPlaceAt = partsAndBed.screenToLocal(bedContextMenu.getAnchorX(), bedContextMenu.getAnchorY());

            newShapeContainer.translateTo(pointToPlaceAt.getX(), pointToPlaceAt.getY());

            Lookup.getSelectedProjectProperty().get().addModel(newShapeContainer);
        });

        addRectangleMenuItem.setOnAction((ActionEvent e) ->
        {
            Rectangle newShape = new Rectangle(10, 10);

            ShapeContainer newShapeContainer = new ShapeContainer("Rectangle", newShape);

            Point2D pointToPlaceAt = partsAndBed.screenToLocal(bedContextMenu.getAnchorX(), bedContextMenu.getAnchorY());

            newShapeContainer.translateTo(pointToPlaceAt.getX(), pointToPlaceAt.getY());

            Lookup.getSelectedProjectProperty().get().addModel(newShapeContainer);
        });

        addCircleMenuItem.setOnAction((ActionEvent e) ->
        {

            Circle newShape = new Circle(10);
            newShape.setSmooth(true);

            ShapeContainer newShapeContainer = new ShapeContainer("Circle", newShape);

            Point2D pointToPlaceAt = partsAndBed.screenToLocal(bedContextMenu.getAnchorX(), bedContextMenu.getAnchorY());
            newShapeContainer.translateTo(pointToPlaceAt.getX(), pointToPlaceAt.getY());

            Lookup.getSelectedProjectProperty().get().addModel(newShapeContainer);
        });

        generateGCodeMenuItem.setOnAction((ActionEvent e) ->
        {
            List<ShapeForProcessing> shapes = new ArrayList<>();
            parts.getChildren().forEach(child ->
            {
                if (child instanceof ShapeContainer)
                {
                    ShapeContainer shapeContainer = (ShapeContainer) child;
                    shapeContainer.getShapes().forEach((shape) ->
                    {
                        shapes.add(new ShapeForProcessing(shape, shapeContainer));
                    });
                }
            });

            PrintableShapes ps = new PrintableShapes(shapes, Lookup.getSelectedProjectProperty().get().getProjectName(), "test2D");
            List<GCodeEventNode> gcodeData = PrintableShapesToGCode.parsePrintableShapes(ps);
            DragKnifeCompensator dnc = new DragKnifeCompensator();
            List<GCodeEventNode> dragKnifeCompensatedGCodeNodes = dnc.doCompensation(gcodeData, 0.2);
            PrintableShapesToGCode.writeGCodeToFile(BaseConfiguration.getPrintSpoolDirectory() + "stylusTestRaw.gcode", gcodeData);
            PrintableShapesToGCode.writeGCodeToFile(BaseConfiguration.getPrintSpoolDirectory() + "stylusTestCompensated.gcode", dragKnifeCompensatedGCodeNodes);
            renderGCode(dragKnifeCompensatedGCodeNodes);
        });

        bedContextMenu.getItems().addAll(addTextMenuItem, addRectangleMenuItem, addCircleMenuItem, generateGCodeMenuItem);

        bed.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>()
        {
            @Override
            public void handle(ContextMenuEvent event)
            {
                Point3D parentPoint = partsAndBed.localToParent(event.getPickResult().getIntersectedPoint());
                bedContextMenu.show(parentPane, Side.TOP, parentPoint.getX(), parentPoint.getY());
            }
        });
    }

    private void resizeBed()
    {
        double viewAreaWidth = maxWidthProperty().get();
        double viewAreaHeight = maxHeightProperty().get();
        double displayAspect = viewAreaWidth / viewAreaHeight;
        double aspect = bedWidth / bedHeight;

        double newWidth = 0;
        double newHeight = 0;
        if (displayAspect >= aspect)
        {
            // Drive from height
            newWidth = (viewAreaHeight * aspect) - bedBorder;
            newHeight = viewAreaHeight - bedBorder;
        } else
        {
            //Drive from width
            newHeight = (viewAreaWidth / aspect) - bedBorder;
            newWidth = viewAreaWidth - bedBorder;
        }

        double scale = newWidth / bedWidth;

        bedScale.setX(scale);
        bedScale.setY(scale);

        double xOffset = ((viewAreaWidth - newWidth) / 2);
        double yOffset = ((viewAreaHeight - newHeight) / 2);

        bedTranslate.setX(xOffset);
        bedTranslate.setY(yOffset);
    }

    @Override
    public void whenModelAdded(ProjectifiableThing projectifiableThing)
    {
        steno.info("Bounds are " + projectifiableThing.getBoundsInLocal());
        projectifiableThing.setBedReference(gCodeOverlay);
        parts.getChildren().add(projectifiableThing);
//        projectifiableThing.shrinkToFitBed();
    }

    @Override
    public void whenModelsRemoved(Set<ProjectifiableThing> projectifiableThing)
    {
        parts.getChildren().removeAll(projectifiableThing);
    }

    @Override
    public void whenAutoLaidOut()
    {
    }

    @Override
    public void whenModelsTransformed(Set<ProjectifiableThing> projectifiableThing
    )
    {
    }

    @Override
    public void whenModelChanged(ProjectifiableThing modelContainer, String propertyName
    )
    {
    }

    @Override
    public void whenPrinterSettingsChanged(PrinterSettingsOverrides printerSettings
    )
    {
    }

    @Override
    public void whenTimelapseSettingsChanged(TimelapseSettingsData timelapseSettings)
    {
    }


    private final EventHandler<MouseEvent> mouseEventHandler = event ->
    {

        mouseOldX = mousePosX;
        mouseOldY = mousePosY;
        mousePosX = event.getSceneX();
        mousePosY = event.getSceneY();

        if (event.getEventType() == MouseEvent.MOUSE_PRESSED)
        {
            if (event.isPrimaryButtonDown()
                    || event.isSecondaryButtonDown())
            {
                handleMouseSingleClickedEvent(event);
            }

        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED)
        {
            dragShape(shapeBeingDragged, event);

        } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED)
        {
            shapeBeingDragged = null;
            dragMode.set(DragMode.IDLE);
        }
    };

    private final EventHandler<ZoomEvent> zoomEventHandler = event ->
    {
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
        double newScale = bedScale.getX() + (0.01 * event.getDeltaY());
        bedScale.setX(newScale);
        bedScale.setY(newScale);
    };

    private ShapeContainer findShapeContainerParent(Node shape)
    {
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
        boolean handleThisEvent = true;
        steno.info("source was " + event.getSource());
        steno.info("target was " + event.getTarget());
        PickResult pickResult = event.getPickResult();
        steno.info("picked was " + pickResult.getIntersectedNode());
        Point3D pickedPoint = pickResult.getIntersectedPoint();
        Node intersectedNode = pickResult.getIntersectedNode();

        boolean shortcut = event.isShortcutDown();

        if (intersectedNode != bed)
        {
            bedContextMenu.hide();
        }
        if (event.isPrimaryButtonDown())
        {
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
                steno.info("Picked: " + intersectedNode + " at " + pickedPoint);
                dragMode.set(DragMode.TRANSLATING);
                justEnteredDragMode = true;
                dragShape((Shape) intersectedNode, event);
            } else
            {
                projectSelection.deselectAllModels();
            }
        }
//        } else if (event.isSecondaryButtonDown())
//        {
////            intersectedNode.fireEvent(new ContextMenuEvent(ContextMenuEvent.CONTEXT_MENU_REQUESTED,
////                    event.getX(), event.getY(),
////                    event.getScreenX(), event.getScreenY(),
////                    false,
////                    pickResult));
//        }
    }

    private void dragShape(Shape shapeToDrag, MouseEvent event)
    {
        Point2D newPosition = new Point2D(event.getSceneX(), event.getSceneY());
        if (shapeBeingDragged != null)
        {
            steno.info("New position = " + newPosition);
            Point2D resultantPosition = partsAndBed.sceneToLocal(newPosition).subtract(partsAndBed.sceneToLocal(lastDragPosition));
            steno.info("Resultant " + resultantPosition);
//            if (shapeBeingDragged == bed)
//            {
//                bedTranslate.setX(bedTranslate.getX() + resultantPosition.getX());
//                bedTranslate.setY(bedTranslate.getY() + resultantPosition.getY());
//            } else
            {

                undoableProject.translateModelsBy(projectSelection.getSelectedModelsSnapshot(TranslateableTwoD.class), resultantPosition.getX(), resultantPosition.getY(),
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
                if (oldMode != newMode)
                {
                    switch (newMode)
                    {
                        case SETTINGS:
                            parentPane.removeEventHandler(MouseEvent.ANY, mouseEventHandler);
                            deselectAllModels();
                            break;
                        default:
                            parentPane.addEventHandler(MouseEvent.ANY, mouseEventHandler);
                            parentPane.addEventHandler(ZoomEvent.ANY, zoomEventHandler);
                            parentPane.addEventHandler(ScrollEvent.ANY, scrollEventHandler);
                            break;
                    }
//                    updateModelColours();
                }
            };

    private void selectModel(ShapeContainer selectedNode, boolean multiSelect)
    {
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
        for (ProjectifiableThing modelContainer : loadedModels)
        {
            deselectModel((ShapeContainer) modelContainer);
        }
    }

    public void deselectModel(ShapeContainer pickedModel)
    {
        if (pickedModel.isSelected())
        {
            projectSelection.removeModelContainer(pickedModel);
        }
    }

    private void renderGCode(List<GCodeEventNode> gcodeNodes)
    {
        gCodeOverlay.getChildren().clear();

        double currentX = 0;
        double currentY = 0;
        boolean isInContact = false;

        for (GCodeEventNode node : gcodeNodes)
        {
            if (node instanceof StylusLiftNode)
            {
                //Not in contact
                isInContact = false;
//                Circle contactCircle = new Circle(0.5);
//                contactCircle.setFill(Color.RED);
//                contactCircle.setCenterX(currentX);
//                contactCircle.setCenterY(currentY);
//                gCodeOverlay.getChildren().add(contactCircle);
            } else if (node instanceof StylusPlungeNode)
            {
                //Now in contact
                isInContact = true;
//                Circle contactCircle = new Circle(0.5);
//                contactCircle.setFill(Color.GREEN);
//                contactCircle.setCenterX(currentX);
//                contactCircle.setCenterY(currentY);
//                gCodeOverlay.getChildren().add(contactCircle);
            } else if (node instanceof StylusScribeNode)
            {
                StylusScribeNode scribeNode = (StylusScribeNode) node;
                Line newLine = new Line();
                newLine.setStartX(currentX);
                newLine.setStartY(currentY);
                currentX = scribeNode.getMovement().getX();
                currentY = scribeNode.getMovement().getY();
                newLine.setEndX(currentX);
                newLine.setEndY(currentY);
                newLine.setStrokeWidth(0.1);

                if (isInContact)
                {
                    newLine.setStroke(Color.GREEN);
                } else
                {
                    newLine.setStroke(Color.RED);
                }
                gCodeOverlay.getChildren().add(newLine);
            } else if (node instanceof StylusSwivelNode)
            {
                StylusSwivelNode scribeNode = (StylusSwivelNode) node;
                Line newLine = new Line();
                newLine.setStartX(currentX);
                newLine.setStartY(currentY);
                currentX = scribeNode.getMovement().getX();
                currentY = scribeNode.getMovement().getY();
                newLine.setEndX(currentX);
                newLine.setEndY(currentY);
                newLine.setStrokeWidth(0.1);

                if (isInContact)
                {
                    newLine.setStroke(Color.PURPLE);
                } else
                {
                    newLine.setStroke(Color.BLUE);
                }
                gCodeOverlay.getChildren().add(newLine);
            } else if (node instanceof TravelNode)
            {
                TravelNode travelNode = (TravelNode) node;
                Line newLine = new Line();
                newLine.setStartX(currentX);
                newLine.setStartY(currentY);
                currentX = travelNode.getMovement().getX();
                currentY = travelNode.getMovement().getY();
                newLine.setEndX(currentX);
                newLine.setEndY(currentY);
                newLine.setStrokeWidth(0.5);

                if (isInContact)
                {
                    newLine.setStroke(Color.GREEN);
                } else
                {
                    newLine.setStroke(Color.RED);
                }
                gCodeOverlay.getChildren().add(newLine);
            }
        }
    }
}
