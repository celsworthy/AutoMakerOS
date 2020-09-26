package celtech.coreUI.visualisation;

import celtech.CoreTest;
import celtech.Lookup;
import celtech.appManager.ApplicationMode;
import celtech.appManager.ApplicationStatus;
import celtech.appManager.ModelContainerProject;
import celtech.appManager.Project;
import celtech.appManager.TimelapseSettingsData;
import celtech.appManager.undo.UndoableProject;
import celtech.configuration.ApplicationConfiguration;
import celtech.coreUI.LayoutSubmode;
import celtech.coreUI.StandardColours;
import celtech.coreUI.visualisation.collision.CollisionManager;
import celtech.coreUI.visualisation.metaparts.ModelLoadResult;
import celtech.coreUI.visualisation.modelDisplay.SelectionHighlighter;
import celtech.modelcontrol.ModelContainer;
import celtech.modelcontrol.ModelGroup;
import celtech.modelcontrol.ProjectifiableThing;
import celtech.modelcontrol.TranslateableTwoD;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.datafileaccessors.FilamentContainer;
import celtech.roboxbase.configuration.datafileaccessors.PrinterContainer;
import celtech.roboxbase.configuration.fileRepresentation.PrinterDefinitionFile;
import celtech.roboxbase.configuration.fileRepresentation.PrinterSettingsOverrides;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterConnection;
import celtech.roboxbase.utils.TimeUtils;
import celtech.utils.threed.importers.obj.ObjImporter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.MeshView;
import javafx.util.Duration;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class ThreeDViewManager implements Project.ProjectChangesListener, ScreenCoordinateConverter
{

    private static final Stenographer steno = StenographerFactory.getStenographer(
            ThreeDViewManager.class.getName());

    private final ObservableList<ProjectifiableThing> loadedModels;
    private final ApplicationStatus applicationStatus = ApplicationStatus.getInstance();

    private final Group root3D = new Group();
    private SubScene subScene;
    private final SimpleObjectProperty<SubScene> subSceneProperty = new SimpleObjectProperty<>();

    final Group axisGroup = new Group();
    double DELTA_MULTIPLIER = 200.0;
    double CONTROL_MULTIPLIER = 0.1;
    double SHIFT_MULTIPLIER = 0.1;
    double ALT_MULTIPLIER = 0.5;

    /*
     * Model moving
     */
    private Point3D lastDragPosition;
    private final int dragPlaneHalfSize = 500;
    private final Box cameraTranslateDragPlane = new Box(dragPlaneHalfSize * 2, 0.1, dragPlaneHalfSize
            * 2);
    private final Box translationDragPlane = new Box(dragPlaneHalfSize * 2, 0.1, dragPlaneHalfSize
            * 2);
    private final Box verticalDragPlane = new Box(dragPlaneHalfSize * 2, dragPlaneHalfSize * 2, 0.1);
    private final Box zCutDisplayPlane = new Box(dragPlaneHalfSize * 2, 0.1, dragPlaneHalfSize * 2);

    private final Group models = new Group();
    /*
     * Selection stuff
     */
    private ObjectProperty<DragMode> dragMode = new SimpleObjectProperty(DragMode.IDLE);

    private final ReadOnlyDoubleProperty widthPropertyToFollow;
    private final ReadOnlyDoubleProperty heightPropertyToFollow;
    private final Set<ModelContainer> inSelectedGroupButNotSelected;

    private final Xform bedTranslateXform = new Xform(Xform.RotateOrder.YXZ, "BedXForm");
    private final Group bed = new Group();
    private Node printVolumeBoundingBox = null;
    private final PerspectiveCamera camera = new PerspectiveCamera(true);

    private final static double initialCameraDistance = 350;
    private double defaultXTranslate = 0;
    private double defaultYTranslate = 0;
    private double defaultDistance = initialCameraDistance;

    private final DoubleProperty cameraDistance = new SimpleDoubleProperty(initialCameraDistance);
    private final DoubleProperty demandedCameraRotationX = new SimpleDoubleProperty(0);
    private final DoubleProperty demandedCameraRotationY = new SimpleDoubleProperty(0);
    private final List<CameraViewChangeListener> cameraViewChangeListeners = new ArrayList<>();

    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;

    private final ProjectSelection projectSelection;

    private long lastAnimationTrigger = 0;

    private final AnimationTimer settingsScreenAnimationTimer = new AnimationTimer()
    {
        @Override
        public void handle(long now)
        {
            long difference = now - lastAnimationTrigger;
            if (difference > 50000000)
            {
                rotateCameraAroundAxes(0, 0.1);
                lastAnimationTrigger = now;
            }
        }
    };

    private final ModelContainerProject project;
    private final UndoableProject undoableProject;
    private final ObjectProperty<LayoutSubmode> layoutSubmode;
    private boolean justEnteredDragMode;

    private final PhongMaterial loaded1Material = new PhongMaterial(Color.BLUE);
    private final PhongMaterial loaded2Material = new PhongMaterial(Color.GREEN);
    private final PhongMaterial extruder1Material = new PhongMaterial(StandardColours.ROBOX_BLUE);
    private final PhongMaterial extruder1TransparentMaterial = new PhongMaterial(StandardColours.ROBOX_BLUE_TRANSPARENT);
    private final PhongMaterial extruder2Material = new PhongMaterial(StandardColours.HIGHLIGHT_ORANGE);
    private final PhongMaterial extruder2TransparentMaterial = new PhongMaterial(StandardColours.HIGHLIGHT_ORANGE_TRANSPARENT);
    private final PhongMaterial greyExcludedMaterial = new PhongMaterial(StandardColours.LIGHT_GREY_TRANSPARENT);
    private final PhongMaterial collidedMaterial = new PhongMaterial(Color.DARKORANGE);
    private final PhongMaterial outOfBoundsMaterial = new PhongMaterial(Color.RED);
    private final PhongMaterial zcutDisplayPlaneMaterial = new PhongMaterial(Color.web("#00005530"));

    private final CollisionManager collisionManager = new CollisionManager();

    private PrinterDefinitionFile currentPrinterConfiguration = null;

    public void transitionCameraTo(double milliseconds,
            double xTranslate,
            double yTranslate,
            double xAngle,
            double yAngle,
            double distance)
    {
        final Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(new KeyFrame[]
        {
            new KeyFrame(Duration.millis(milliseconds), new KeyValue[]
            {
                new KeyValue(bedTranslateXform.t.xProperty(), xTranslate, Interpolator.EASE_BOTH),
                new KeyValue(bedTranslateXform.t.yProperty(), yTranslate, Interpolator.EASE_BOTH),
                new KeyValue(demandedCameraRotationX, xAngle, Interpolator.EASE_BOTH),
                new KeyValue(demandedCameraRotationY, yAngle, Interpolator.EASE_BOTH),
                new KeyValue(cameraDistance, distance, Interpolator.EASE_BOTH)
            })
        });
        timeline.playFromStart();
    }

    public void transitionCameraTo(double milliseconds, double xAngle, double yAngle, double distance)
    {
        final Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(new KeyFrame[]
        {
            new KeyFrame(Duration.millis(milliseconds), new KeyValue[]
            {
                new KeyValue(demandedCameraRotationX, xAngle, Interpolator.EASE_BOTH),
                new KeyValue(demandedCameraRotationY, yAngle, Interpolator.EASE_BOTH),
                new KeyValue(cameraDistance, distance, Interpolator.EASE_BOTH)
            })
        });
        timeline.playFromStart();
    }

    private void rotateCameraAroundAxes(double xangle, double yangle)
    {
        double yAxisRotation = demandedCameraRotationY.get() - yangle;

        if (yAxisRotation >= 360)
        {
            yAxisRotation = yAxisRotation - 360;
        } else if (yAxisRotation < 0)
        {
            yAxisRotation = yAxisRotation + 360;
        }
        demandedCameraRotationY.set(yAxisRotation);

        double xAxisRotation = demandedCameraRotationX.get() - xangle;
        if (xAxisRotation > 89)
        {
            xAxisRotation = 89;
        } else if (xAxisRotation < 0)
        {
            xAxisRotation = 0;
        }
        demandedCameraRotationX.set(xAxisRotation);

        notifyModelsOfCameraViewChange();
    }

    private void notifyListenersOfCameraViewChange()
    {
        for (CameraViewChangeListener listener : cameraViewChangeListeners)
        {
            listener.cameraViewOfYouHasChanged(cameraDistance.get());
        }
    }

    private void notifyModelsOfCameraViewChange()
    {
        Set<ProjectifiableThing> selectedModels = projectSelection.getSelectedModelsSnapshot();
        Set<ModelContainer> modelContainers = (Set) selectedModels;
        for (ModelContainer modelContainer : modelContainers)
        {
            modelContainer.cameraViewOfYouHasChanged(cameraDistance.get());
        }

        notifyListenersOfCameraViewChange();
    }

    private void rotateCameraAroundAxesTo(double xangle, double yangle)
    {
        double yAxisRotation = yangle;

        if (yAxisRotation >= 360)
        {
            yAxisRotation = yAxisRotation - 360;
        } else if (yAxisRotation < 0)
        {
            yAxisRotation = yAxisRotation + 360;
        }
        demandedCameraRotationY.set(yAxisRotation);

        double xAxisRotation = xangle;
        if (xAxisRotation > 89)
        {
            xAxisRotation = 89;
        } else if (xAxisRotation < 0)
        {
            xAxisRotation = 0;
        }
        demandedCameraRotationX.set(xAxisRotation);

        notifyModelsOfCameraViewChange();

    }

    private void processModeChange()
    {
        switch (dragMode.get())
        {
            case IDLE:
                if (layoutSubmode.get() == LayoutSubmode.Z_CUT)
                {
                    models.setMouseTransparent(true);
                    translationDragPlane.setMouseTransparent(true);
                    verticalDragPlane.setMouseTransparent(true);
                    cameraTranslateDragPlane.setMouseTransparent(true);
                    zCutDisplayPlane.setMouseTransparent(false);
                    zCutDisplayPlane.setOpacity(1.0);
                } else
                {
                    models.setMouseTransparent(false);
                    translationDragPlane.setMouseTransparent(true);
                    verticalDragPlane.setMouseTransparent(true);
                    cameraTranslateDragPlane.setMouseTransparent(true);
                    zCutDisplayPlane.setMouseTransparent(true);
                    zCutDisplayPlane.setOpacity(0.0);
                }
                break;
            case ZCUT:
                models.setMouseTransparent(true);
                translationDragPlane.setMouseTransparent(true);
                verticalDragPlane.setMouseTransparent(false);
                cameraTranslateDragPlane.setMouseTransparent(true);
                zCutDisplayPlane.setMouseTransparent(true);
                zCutDisplayPlane.setOpacity(1.0);
                break;
            case TRANSLATING:
            case X_CONSTRAINED_TRANSLATE:
            case Z_CONSTRAINED_TRANSLATE:
            case ROTATE:
                translationDragPlane.setMouseTransparent(false);
                verticalDragPlane.setMouseTransparent(true);
                models.setMouseTransparent(true);
                cameraTranslateDragPlane.setMouseTransparent(true);
                zCutDisplayPlane.setMouseTransparent(true);
                zCutDisplayPlane.setOpacity(0.0);
                break;
            case SCALING:
                verticalDragPlane.setMouseTransparent(false);
                translationDragPlane.setMouseTransparent(true);
                models.setMouseTransparent(true);
                cameraTranslateDragPlane.setMouseTransparent(true);
                zCutDisplayPlane.setMouseTransparent(true);
                zCutDisplayPlane.setOpacity(0.0);
                break;
//            case CAMERA_TRANSLATE:
//                models.setMouseTransparent(true);
//                translationDragPlane.setMouseTransparent(true);
//                verticalDragPlane.setMouseTransparent(true);
//                cameraTranslateDragPlane.setMouseTransparent(false);
//                break;
        }

    }

    private final ChangeListener<LayoutSubmode> layoutSubmodeListener = new ChangeListener<LayoutSubmode>()
    {
        @Override
        public void changed(ObservableValue<? extends LayoutSubmode> observable, LayoutSubmode oldValue,
                LayoutSubmode newValue)
        {
            processModeChange();
        }
    };

    private final ChangeListener<DragMode> dragModeListener = new ChangeListener<DragMode>()
    {
        @Override
        public void changed(ObservableValue<? extends DragMode> observable, DragMode oldValue,
                DragMode newValue)
        {
            processModeChange();
        }
    };

    private void handleMouseDoubleClickedEvent(MouseEvent event)
    {
        Node intersectedNode = event.getPickResult().getIntersectedNode();
        if (intersectedNode instanceof MeshView)
        {
            if (inSelectedGroupButNotSelected.contains((ModelContainer) intersectedNode.getParent()))
            {
                return;
            }
            // if clicked mc is within a selected group then isolate the objects below the selected
            // group.e
            Set<ProjectifiableThing> selectedProjectifiableThings
                    = Lookup.getProjectGUIState(project).getProjectSelection().getSelectedModelsSnapshot();
            Set<ModelContainer> selectedModelContainers = (Set) selectedProjectifiableThings;
            Set<MeshView> selectedMeshViews
                    = selectedModelContainers.stream().
                            map(mc -> mc.descendentMeshViews()).
                            reduce(new HashSet<>(), (a, b) ->
                            {
                                a.addAll(b);
                                return a;
                            });
            if (selectedMeshViews.contains((MeshView) intersectedNode))
            {
                doSelectTranslateModel(intersectedNode, event.getPickResult().getIntersectedPoint(), event, false);
                updateGroupSelectionList();
                updateModelColours();
            }
        }
    }

    private void handleMouseSingleClickedEvent(MouseEvent event)
    {
        boolean handleThisEvent = true;
        PickResult pickResult = event.getPickResult();
        Point3D pickedPoint = pickResult.getIntersectedPoint();
        Node intersectedNode = pickResult.getIntersectedNode();

        boolean shortcut = event.isShortcutDown();

        ModelGroup ancestorGroup = null;

        //Drop out early if we shouldn't use this click
//        if (intersectedNode instanceof MeshView)
//        {
//            ModelContainer parentModel = (ModelContainer) intersectedNode.getParent();
//            ancestorGroup = getTopLevelAncestorGroup(parentModel);
//
//            if (layoutSubmode.get() == LayoutSubmode.SELECT
//                    && ancestorGroup != null
//                    && inSelectedGroupButNotSelected.isEmpty())
//            {
//                return;
//            }
//        }
        if (event.isPrimaryButtonDown())
        {
            if (intersectedNode.getParent() != null)
            {
                if (intersectedNode.getParent().getId() != null)
                {
                    if (intersectedNode.getParent().getId().equals(SelectionHighlighter.idString))
                    {
                        // Expect the selection highlighter to handle this one...
                        handleThisEvent = false;
                    }
                }
            }

            if (handleThisEvent)
            {
                if (intersectedNode instanceof MeshView)
                {
                    switch (layoutSubmode.get())
                    {
                        case SNAP_TO_GROUND:
                            ModelContainer rootModelContainer
                                    = ModelContainer.getRootModelContainer((MeshView) intersectedNode);
                            doSnapToGround(rootModelContainer, (MeshView) intersectedNode, pickResult);
                            break;
                        case Z_CUT:
                            break;
                        case SELECT:
//                            ModelContainer parentModel = (ModelContainer) intersectedNode.getParent();
//                            ancestorGroup = getTopLevelAncestorGroup(parentModel);
//                            if (ancestorGroup != null)
//                            {
//                                doSelectTranslateModel(ancestorGroup, pickedPoint, event);
//                            } else
//                            {
                            doSelectTranslateModel(intersectedNode, pickedPoint, event, true);
//                            }
                            break;
                    }
                } else if (intersectedNode == zCutDisplayPlane
                        && dragMode.get() != DragMode.ZCUT)
                {
                    orientVerticalDragPlane(pickResult);
                    setDragMode(DragMode.ZCUT);
                } else if (layoutSubmode.get() != LayoutSubmode.Z_CUT)
                {
                    projectSelection.deselectAllModels();
                }
                updateGroupSelectionList();
                updateModelColours();
            }
        }
    }

    private void doSelectTranslateModel(Node intersectedNode, Point3D pickedPoint, MouseEvent event, boolean findParentGroup)
    {
        Point3D pickedScenePoint = intersectedNode.localToScene(pickedPoint);
        Point3D pickedBedTranslateXformPoint = bedTranslateXform.sceneToLocal(
                pickedScenePoint);

        translationDragPlane.setTranslateY(pickedBedTranslateXformPoint.getY());

        Point3D pickedDragPlanePoint = translationDragPlane.sceneToLocal(
                pickedScenePoint);
        lastDragPosition = pickedDragPlanePoint;

        Point3D bedXToS = bedTranslateXform.localToParent(pickedPoint);
        verticalDragPlane.setTranslateX(bedXToS.getX());
        verticalDragPlane.setTranslateY(bedXToS.getY());
        verticalDragPlane.setTranslateZ(pickedPoint.getZ());

        setDragMode(DragMode.TRANSLATING);
        justEnteredDragMode = true;

        Parent parent = intersectedNode.getParent();
        if (!(parent instanceof ModelContainer))
        {
            parent = parent.getParent();
        }

        ModelContainer pickedModel = (ModelContainer) parent;
        // get top-level ModelContainer (could be grouped) that is not excluded from
        // selection
//        while (pickedModel.getParentModelContainer() instanceof ModelContainer
//                && !excludedFromSelection.contains(pickedModel.getParentModelContainer()))

        if (findParentGroup && !pickedModel.isSelected())
        {
            while (pickedModel.getParentModelContainer() instanceof ModelContainer)
            {
                pickedModel = (ModelContainer) pickedModel.getParentModelContainer();
            }
        }

//        List<Transform> modelRotationTransforms = pickedModel.getRotationTransforms();
//        translationDragPlane.getTransforms().clear();
//        translationDragPlane.getTransforms().addAll(modelRotationTransforms);
//        if (inSelectedGroupButNotSelected.isEmpty())
//        {
//            // Find the top-level group - only if we haven't selected a child in the group
//            // get top-level ModelContainer (could be grouped) that is not excluded from
//            // selection
//            while (pickedModel.getParentModelContainer() instanceof ModelContainer
//                    && !inSelectedGroupButNotSelected.contains(pickedModel.getParentModelContainer()))
//            {
//                pickedModel = (ModelContainer) pickedModel.getParentModelContainer();
//            }
//        }
        if (pickedModel.isSelected() == false)
        {
            boolean multiSelect = event.isShortcutDown();
            selectModel(pickedModel, multiSelect);
        } else
        {
            boolean multiSelect = event.isShortcutDown();
            if (multiSelect)
            {
                deselectModel(pickedModel);
            }
        }
    }

    private void updateGroupSelectionList()
    {
        inSelectedGroupButNotSelected.clear();

        Set<ProjectifiableThing> selectedProjectifiableThings = projectSelection.getSelectedModelsSnapshot();
        Set<ModelContainer> selectedModels = (Set) selectedProjectifiableThings;
        for (ModelContainer model : selectedModels)
        {
            ModelGroup parentGroup = getTopLevelAncestorGroup(model);
            if (parentGroup != null)
            {
                for (ModelContainer subContainer : parentGroup.getDescendentModelContainers())
                {
                    if (!projectSelection.getSelectedModelsSnapshot().contains(subContainer))
                    {
                        inSelectedGroupButNotSelected.add(subContainer);
                    }
                }
            }
        }
    }

    private void doSnapToGround(ModelContainer modelContainer, MeshView meshView,
            PickResult pickResult)
    {
        if (modelContainer != null)
        {
            int faceNumber = pickResult.getIntersectedFace();
            undoableProject.snapToGround(modelContainer, meshView, faceNumber);
            layoutSubmode.set(LayoutSubmode.SELECT);
        }
    }

    private void handleMouseDragEvent(MouseEvent event)
    {

        double mouseDeltaX = (mousePosX - mouseOldX);
        double mouseDeltaY = (mousePosY - mouseOldY);

        boolean shortcut = event.isShortcutDown();

        if (shortcut && event.isSecondaryButtonDown())
        {
            bedTranslateXform.setTx(bedTranslateXform.getTx() + mouseDeltaX * 0.3);  // -
            bedTranslateXform.setTy(bedTranslateXform.getTy() + mouseDeltaY * 0.3);  // -
            notifyModelsOfCameraViewChange();
        } else if (event.isAltDown())
        {
            double z = bedTranslateXform.getTz() + (mouseDeltaY * 0.2);
            cameraDistance.set(z);
            notifyModelsOfCameraViewChange();
        } else if (event.isSecondaryButtonDown())
        {
            rotateCameraAroundAxes(-mouseDeltaY * 2.0, mouseDeltaX * 2.0);
        } else if (dragMode.get() == DragMode.ZCUT && event.isPrimaryButtonDown())
        {
            Node intersectedNode = event.getPickResult().getIntersectedNode();
            //Move the model!
            if (intersectedNode == verticalDragPlane)
            {
                Point3D pickedPoint = event.getPickResult().getIntersectedPoint();
                Point3D pickedScenePoint = intersectedNode.localToScene(pickedPoint);
                Point3D pickedDragPlanePoint = verticalDragPlane.sceneToLocal(pickedScenePoint);
                if (lastDragPosition != null)
                {
                    Point3D resultant = pickedDragPlanePoint.subtract(lastDragPosition);

                    double newCutHeight = cutHeight.get() - resultant.getY();
                    if (newCutHeight > maxCutHeight)
                    {
                        newCutHeight = maxCutHeight;
                    } else if (newCutHeight < 0)
                    {
                        newCutHeight = 0;
                    }
//                    cutHeight.set(newCutHeight);
                    justEnteredDragMode = false;
                }
                lastDragPosition = pickedDragPlanePoint;
            }

        } else if (dragMode.get() == DragMode.TRANSLATING && event.isPrimaryButtonDown())
        {
            Node intersectedNode = event.getPickResult().getIntersectedNode();
            //Move the model!
            if (intersectedNode == translationDragPlane)
            {
                Point3D pickedPoint = event.getPickResult().getIntersectedPoint();
                Point3D pickedScenePoint = intersectedNode.localToScene(pickedPoint);
                Point3D pickedBedTranslateXformPoint = bedTranslateXform.sceneToLocal(
                        pickedScenePoint);

//                translationDragPlane.setTranslateY(pickedBedTranslateXformPoint.getY());
                Point3D pickedDragPlanePoint = translationDragPlane.sceneToLocal(pickedScenePoint);

                if (lastDragPosition != null)
                {
                    Point3D resultant = pickedDragPlanePoint.subtract(lastDragPosition);
                    translateSelection(resultant.getX(), resultant.getZ());
                    justEnteredDragMode = false;
                }
                lastDragPosition = pickedDragPlanePoint;
            }
//            else
//            {
//                steno.error(
//                    "In translation drag mode but intersected with something other than translation drag plane");
//            }
        } else if (dragMode.get() == DragMode.SCALING && event.isPrimaryButtonDown())
        {
            Node intersectedNode = event.getPickResult().getIntersectedNode();
            //Move the model!
            if (intersectedNode != verticalDragPlane)
            {
                steno.error(
                        "In scale drag mode but intersected with something other than scale drag plane");
            }
        }
    }

    private final EventHandler<MouseEvent> mouseEventHandler = event ->
    {

        mouseOldX = mousePosX;
        mouseOldY = mousePosY;
        mousePosX = event.getSceneX();
        mousePosY = event.getSceneY();

        if (event.getEventType() == MouseEvent.MOUSE_PRESSED)
        {
           if (applicationStatus.getMode() != ApplicationMode.SETTINGS)
            {
                if (event.getClickCount() == 2 && event.isPrimaryButtonDown())
                {
                    handleMouseDoubleClickedEvent(event);
                } else if (event.isPrimaryButtonDown()
                        || event.isSecondaryButtonDown())
                {
                    handleMouseSingleClickedEvent(event);
                }
            }

        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && dragMode.get()
                != DragMode.SCALING)
        {
            handleMouseDragEvent(event);

        } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED)
        {
            setDragMode(DragMode.IDLE);
            lastDragPosition = null;
        }
    };

    private final EventHandler<ScrollEvent> scrollEventHandler = event ->
    {
        if (event.getTouchCount() > 0)
        { // touch pad scroll
            bedTranslateXform.setTx(bedTranslateXform.getTx() - (0.01 * event.getDeltaX()));  // -
            bedTranslateXform.setTy(bedTranslateXform.getTy() + (0.01 * event.getDeltaY()));  // -
        } else
        {
            double z = bedTranslateXform.getTz() - event.getDeltaY();
//            double minimumZ = currentPrinterConfiguration.getPrinterType() == PrinterType.ROBOX_PRO ? -60.0 : 0.0;
            double minimumZ = -500;
            if (z >= minimumZ)
            {
                cameraDistance.set(z);
            }
        }
        notifyModelsOfCameraViewChange();
    };
    private final EventHandler<ZoomEvent> zoomEventHandler = event ->
    {
        if (!Double.isNaN(event.getZoomFactor()) && event.getZoomFactor() > 0.8
                && event.getZoomFactor() < 1.2)
        {
            double z = bedTranslateXform.getTz() / event.getZoomFactor();
            cameraDistance.set(z);
            notifyModelsOfCameraViewChange();
//            cameraDistance.set(cameraDistance.get() / event.getZoomFactor());
        }
    };

    private final ChangeListener<ApplicationMode> applicationModeListener
            = (ObservableValue<? extends ApplicationMode> ov, ApplicationMode oldMode, ApplicationMode newMode) ->
    {
        if (oldMode != newMode)
        {
            switch (newMode)
            {
                case SETTINGS:
                    deselectAllModels();
                    transitionCameraToDefaults();
//                            startSettingsAnimation();
                    break;
                default:
                    subScene.addEventHandler(MouseEvent.ANY, mouseEventHandler);
                    subScene.addEventHandler(ZoomEvent.ANY, zoomEventHandler);
                    subScene.addEventHandler(ScrollEvent.ANY, scrollEventHandler);
                    notifyModelsOfCameraViewChange();
//                            stopSettingsAnimation();
                    break;
            }
            updateModelColours();
        }
    };

    private MapChangeListener<Integer, Filament> effectiveFilamentListener = (MapChangeListener.Change<? extends Integer, ? extends Filament> change) ->
    {
        updateModelColours();
    };

    private void whenCurrentPrinterChanged(Printer printer)    {

        defaultDistance = initialCameraDistance;
        if (printer != null
                && printer.printerConfigurationProperty().get() != null)
        {
            currentPrinterConfiguration = printer.printerConfigurationProperty().get();

            defaultXTranslate = -currentPrinterConfiguration.getPrintVolumeWidth() / 2;
            defaultYTranslate = currentPrinterConfiguration.getPrintVolumeHeight() / 3;

            if (currentPrinterConfiguration.getTypeCode().equals("RBX10"))
            {
                defaultDistance = 550;
                defaultYTranslate = currentPrinterConfiguration.getPrintVolumeHeight() / 7;
                buildBed(true);
            } else
            {
                buildBed(false);
            }
            updateProjectifiableThings();

        } else
        {
            //Default view
            PrinterDefinitionFile defaultPrinterDefinition
                    = PrinterContainer.getPrinterByID(PrinterContainer.defaultPrinterID);
            defaultXTranslate = -defaultPrinterDefinition.getPrintVolumeWidth() / 2;
            defaultYTranslate = defaultPrinterDefinition.getPrintVolumeHeight() - 80;
        }
        addPrintVolumeBoundingBox(bed);
        deselectAllModels();
        transitionCameraToDefaults();

    }

    public ThreeDViewManager(ModelContainerProject project,
            ReadOnlyDoubleProperty widthProperty, ReadOnlyDoubleProperty heightProperty)
    {
        this.project = project;
        this.undoableProject = new UndoableProject(project);

        currentPrinterConfiguration = PrinterContainer.getPrinterByID(PrinterContainer.defaultPrinterID);

        loadedModels = project.getTopLevelThings();
        projectSelection = Lookup.getProjectGUIState(project).getProjectSelection();
        layoutSubmode = Lookup.getProjectGUIState(project).getLayoutSubmodeProperty();
        inSelectedGroupButNotSelected = Lookup.getProjectGUIState(project).getExcludedFromSelection();

        widthPropertyToFollow = widthProperty;
        heightPropertyToFollow = heightProperty;

        widthPropertyToFollow.addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1)
            {
                notifyListenersOfCameraViewChange();
            }
        });

        heightPropertyToFollow.addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1)
            {
                notifyListenersOfCameraViewChange();
            }
        });

        root3D.setId("Root");
        AnchorPane.setBottomAnchor(root3D, 0.0);
        AnchorPane.setTopAnchor(root3D, 0.0);
        AnchorPane.setLeftAnchor(root3D, 0.0);
        AnchorPane.setRightAnchor(root3D, 0.0);
        root3D.setPickOnBounds(false);

        cameraDistance.set(initialCameraDistance);

        root3D.getChildren().add(camera);
        camera.setNearClip(0.1);
        camera.setFarClip(5000.0);

        PointLight cameraLight = new PointLight();
        cameraLight.setTranslateX(camera.getTranslateX());
        cameraLight.setTranslateY(camera.getTranslateY());
        cameraLight.setTranslateZ(camera.getTranslateZ());
        root3D.getChildren().add(cameraLight);

        // Build SubScene
        subScene = new SubScene(root3D, widthProperty.getValue(), heightProperty.getValue(), true,
                SceneAntialiasing.BALANCED);
        this.subSceneProperty.set(subScene);
        subScene.setFill(Color.TRANSPARENT);
        subScene.setCamera(camera);

        buildBed(false);

        Lookup.getSelectedPrinterProperty().addListener(new ChangeListener<Printer>()
        {
            @Override
            public void changed(ObservableValue<? extends Printer> ov, Printer t, Printer t1)
            {
                whenCurrentPrinterChanged(t1);
            }
        });

        whenCurrentPrinterChanged(Lookup.getSelectedPrinterProperty().get());

        translationDragPlane.setId("DragPlane");
        translationDragPlane.setOpacity(0.0);
        translationDragPlane.setMaterial(greyExcludedMaterial);
        translationDragPlane.setMouseTransparent(true);
        translationDragPlane.setTranslateX(currentPrinterConfiguration.getPrintVolumeWidth());
        translationDragPlane.setTranslateZ(currentPrinterConfiguration.getPrintVolumeDepth());

        verticalDragPlane.setId("VerticalDragPlane");
        verticalDragPlane.setOpacity(0.0);
        verticalDragPlane.setMouseTransparent(true);
        verticalDragPlane.setRotationAxis(new Point3D(0, 1, 0));

        zCutDisplayPlane.setId("zCutDragPlane");
        zCutDisplayPlane.setOpacity(0.0);
        zCutDisplayPlane.setMouseTransparent(true);
        zCutDisplayPlane.setMaterial(zcutDisplayPlaneMaterial);

        PointLight overheadLight = new PointLight();

        overheadLight.setTranslateX(105);
        overheadLight.setTranslateY(-400);
        overheadLight.setTranslateZ(75);

        overheadLight.setColor(Color.WHITE.darker().darker().darker());

        bedTranslateXform.getChildren().clear();
        bedTranslateXform.getChildren().addAll(overheadLight, bed, models, translationDragPlane,
                verticalDragPlane, zCutDisplayPlane);
        root3D.getChildren().add(bedTranslateXform);

        double bedXOffsetFromCameraZero = -currentPrinterConfiguration.getPrintVolumeWidth() / 2;
        double bedZOffsetFromCameraZero = -currentPrinterConfiguration.getPrintVolumeDepth() / 2;

        bedTranslateXform.setTx(bedXOffsetFromCameraZero);
        bedTranslateXform.setTz(bedZOffsetFromCameraZero + cameraDistance.get());
        bedTranslateXform.setPivot(-bedXOffsetFromCameraZero, 0, -bedZOffsetFromCameraZero);

        bedTranslateXform.rx.angleProperty().bind(demandedCameraRotationX);
        bedTranslateXform.ry.angleProperty().bind(demandedCameraRotationY);
        bedTranslateXform.t.zProperty().bind(cameraDistance);

        transitionCameraToDefaults();

        subScene.widthProperty().bind(widthPropertyToFollow);
        subScene.heightProperty().bind(heightPropertyToFollow);

        applicationStatus.modeProperty().addListener(applicationModeListener);

        subScene.addEventHandler(MouseEvent.ANY, mouseEventHandler);
        subScene.addEventHandler(ZoomEvent.ANY, zoomEventHandler);
        subScene.addEventHandler(ScrollEvent.ANY, scrollEventHandler);

        layoutSubmode.addListener(
                (ObservableValue<? extends LayoutSubmode> ov, LayoutSubmode t, LayoutSubmode t1) ->
        {
            if (t1 == LayoutSubmode.SNAP_TO_GROUND)
            {
                subScene.setCursor(Cursor.HAND);
            } else
            {
                subScene.setCursor(Cursor.DEFAULT);
            }
        });

        dragMode.addListener(dragModeListener);
        layoutSubmode.addListener(layoutSubmodeListener);

        /**
         * Set up filament, application mode and printer listeners so that the
         * correct model colours are displayed.
         */
        setupFilamentListeners(project);
        updateModelColours();

        if (Lookup.getSelectedPrinterProperty().get() != null
                && Lookup.getSelectedPrinterProperty().get().effectiveFilamentsProperty() != null)
        {
            Lookup.getSelectedPrinterProperty().get().effectiveFilamentsProperty().addListener(effectiveFilamentListener);
        }

        Lookup.getSelectedPrinterProperty().addListener(
                (ObservableValue<? extends Printer> observable, Printer oldValue, Printer newValue) ->
        {
            if (oldValue != null)
            {
                oldValue.effectiveFilamentsProperty().removeListener(effectiveFilamentListener);
            }
            if (newValue != null)
            {
                newValue.effectiveFilamentsProperty().addListener(effectiveFilamentListener);
            }
            updateModelColours();
        });

        project.getPrinterSettings().getPrintSupportTypeOverrideProperty().addListener(
                (ObservableValue<? extends Object> observable, Object oldValue, Object newValue) ->
        {
            updateModelColours();
        });

        /**
         * Listen for adding and removing of models from the project
         */
        project.addProjectChangesListener(this);

        for (ProjectifiableThing model : loadedModels)
        {
            models.getChildren().add(model);

            addBedReferenceToModel((ModelContainer) model);
            ((ModelContainer) model).cameraViewOfYouHasChanged(cameraDistance.get());
            ((ModelContainer) model).heresYourCamera(camera);
        }
    }

    private void addBedReferenceToModel(ModelContainer model)
    {
        if (model instanceof ModelGroup)
        {
            model.getDescendentModelContainers().forEach(modelContainer ->
            {
                modelContainer.setBedReference(bed);
            });
        } else
        {
            model.setBedReference(bed);
        }
    }

    /**
     * Add / rebuild the correct assets to the bed according to the printer type
     *
     * @param brobox
     * @return
     */
    private void buildBed(boolean brobox) {
        URL bedOuterURL, peiSheetURL, bedClipsURL, bedGraphicURL;
        double bedZOffset, bedXOffset, bedYOffset;
        double peiDrop;
        bed.getChildren().clear();
        if (brobox)
        {
            bedOuterURL = CoreTest.class
                    .getResource(ApplicationConfiguration.modelResourcePath + "bed_frame_210x300.obj");
            peiSheetURL = CoreTest.class.getResource(ApplicationConfiguration.modelResourcePath
                    + "bed_glass_210x300.obj");
            bedClipsURL = null;
            bedGraphicURL = CoreTest.class.getResource(ApplicationConfiguration.imageResourcePath
                    + "Bed Graphic - RoboxPro.png");
            bedZOffset = 210;
            bedYOffset = 0;
            bedXOffset = 0;
            peiDrop = 0.5;
        } else
        {
            bedOuterURL = CoreTest.class
                    .getResource(ApplicationConfiguration.modelResourcePath + "bedBase.obj");
            peiSheetURL = CoreTest.class.getResource(ApplicationConfiguration.modelResourcePath
                    + "pei.obj");
            bedClipsURL = CoreTest.class.getResource(ApplicationConfiguration.modelResourcePath
                    + "clips.obj");
            bedGraphicURL = CoreTest.class.getResource(ApplicationConfiguration.imageResourcePath
                    + "Bed Graphic - Robox.png");
            bedZOffset = 150;
            bedYOffset = 0;
            bedXOffset = 0;
            peiDrop = 0.25;
        }

        PhongMaterial bedOuterMaterial = new PhongMaterial(Color.web("#0a0a0a"));

        PhongMaterial peiSheetMaterial = new PhongMaterial(Color.web("#a0a0a0"));
        peiSheetMaterial.setSpecularPower(1.2f);

        PhongMaterial bedClipsMaterial = new PhongMaterial(Color.web("#f0f0f0"));
        bedClipsMaterial.setSpecularPower(20f);

        ObjImporter bedOuterImporter = new ObjImporter();
        ModelLoadResult bedOuterLoadResult = bedOuterImporter.loadURL(null, bedOuterURL);
        MeshView outerMeshView = ((ModelContainer) bedOuterLoadResult.getProjectifiableThings().iterator().next()).getMeshView();
        outerMeshView.setMaterial(bedOuterMaterial);
        bed.getChildren().addAll(outerMeshView);

        ObjImporter peiSheetImporter = new ObjImporter();
        ModelLoadResult peiSheetLoadResult = peiSheetImporter.loadURL(null, peiSheetURL);
        MeshView peiMeshView = ((ModelContainer) peiSheetLoadResult.getProjectifiableThings().iterator().next()).getMeshView();
        peiMeshView.setMaterial(peiSheetMaterial);

        bed.getChildren().addAll(peiMeshView);

        if (bedClipsURL != null)
        {
        ObjImporter bedClipsImporter = new ObjImporter();
        ModelLoadResult bedClipsLoadResult = bedClipsImporter.loadURL(null, bedClipsURL);
        MeshView bedClipsMeshView = ((ModelContainer) bedClipsLoadResult.getProjectifiableThings().iterator().next()).getMeshView();
        bedClipsMeshView.setMaterial(bedClipsMaterial);
            bed.getChildren().addAll(bedClipsMeshView);
        }

        final Image roboxLogoImage = new Image(bedGraphicURL.toExternalForm());
        final ImageView bedGraphicView = new ImageView();
        Lighting lighting = new Lighting();
        lighting.setSurfaceScale(1.5);
        bedGraphicView.setEffect(lighting);

        bedGraphicView.setImage(roboxLogoImage);

        final Xform bedGraphicTransformNode = new Xform();

        bedGraphicTransformNode.setTz(bedZOffset);
        bedGraphicTransformNode.setTx(bedXOffset);
        bedGraphicTransformNode.setTy(bedYOffset);

        bedGraphicTransformNode.setRotateX(-90);
        bedGraphicTransformNode.setScale(0.1);

        peiMeshView.translateYProperty().set(peiDrop);

        bedGraphicTransformNode.getChildren().add(bedGraphicView);
        bedGraphicTransformNode.setId("LogoImage");

        bed.getChildren().add(bedGraphicTransformNode);
        bed.setMouseTransparent(true);

    }

    private Node createBoundingBox(double printVolumeWidth, double printVolumeDepth, double printVolumeHeight)
    {
        PhongMaterial boundsBoxMaterial = new PhongMaterial(Color.BLUE);
        Image illuminationMap = new Image(SelectionHighlighter.class.getResource(
                ApplicationConfiguration.imageResourcePath + "blueIlluminationMap.png").
                toExternalForm());
        boundsBoxMaterial.setSelfIlluminationMap(illuminationMap);

        Group boxGroup = new Group();

        double lineWidth = .1;

        Box lhf = new Box(lineWidth, printVolumeHeight, lineWidth);
        lhf.setMaterial(boundsBoxMaterial);
        lhf.setTranslateY(-printVolumeHeight / 2);

        Box rhf = new Box(lineWidth, printVolumeHeight, lineWidth);
        rhf.setMaterial(boundsBoxMaterial);
        rhf.setTranslateY(-printVolumeHeight / 2);
        rhf.setTranslateX(printVolumeWidth);

        Box lhb = new Box(lineWidth, printVolumeHeight, lineWidth);
        lhb.setMaterial(boundsBoxMaterial);
        lhb.setTranslateY(-printVolumeHeight / 2);
        lhb.setTranslateZ(printVolumeDepth);

        Box rhb = new Box(lineWidth, printVolumeHeight, lineWidth);
        rhb.setMaterial(boundsBoxMaterial);
        rhb.setTranslateY(-printVolumeHeight / 2);
        rhb.setTranslateX(printVolumeWidth);
        rhb.setTranslateZ(printVolumeDepth);

        Box lhftTOlhbt = new Box(lineWidth, lineWidth,
                printVolumeDepth);
        lhftTOlhbt.setMaterial(boundsBoxMaterial);
        lhftTOlhbt.setTranslateY(-printVolumeHeight);
        lhftTOlhbt.setTranslateZ(printVolumeDepth / 2);

        Box rhftTOrhbt = new Box(lineWidth, lineWidth,
                printVolumeDepth);
        rhftTOrhbt.setMaterial(boundsBoxMaterial);
        rhftTOrhbt.setTranslateX(printVolumeWidth);
        rhftTOrhbt.setTranslateY(-printVolumeHeight);
        rhftTOrhbt.setTranslateZ(printVolumeDepth / 2);

        Box lhftTOrhft = new Box(printVolumeWidth, lineWidth, lineWidth);
        lhftTOrhft.setMaterial(boundsBoxMaterial);
        lhftTOrhft.setTranslateX(printVolumeWidth / 2);
        lhftTOrhft.setTranslateY(-printVolumeHeight);

        Box lhbtTOrhbt = new Box(printVolumeWidth, lineWidth, lineWidth);
        lhbtTOrhbt.setMaterial(boundsBoxMaterial);
        lhbtTOrhbt.setTranslateX(printVolumeWidth / 2);
        lhbtTOrhbt.setTranslateY(-printVolumeHeight);
        lhbtTOrhbt.setTranslateZ(printVolumeDepth);

        boxGroup.getChildren().addAll(lhf, rhf, lhb, rhb,
                lhftTOlhbt, rhftTOrhbt,
                lhftTOrhft, lhbtTOrhbt);

        return boxGroup;
    }

    public void shutdown()
    {
        applicationStatus.modeProperty().removeListener(applicationModeListener);
        dragMode.removeListener(dragModeListener);
    }

    private void selectModel(ModelContainer selectedNode, boolean multiSelect)
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

        updateGroupSelectionList();
    }

    private void translateSelection(double x, double z)
    {
        undoableProject.translateModelsBy(projectSelection.getSelectedModelsSnapshot(TranslateableTwoD.class), x, z,
                !justEnteredDragMode);
    }

    public void deselectModel(ModelContainer pickedModel)
    {
        if (pickedModel.isSelected())
        {
            projectSelection.removeModelContainer(pickedModel);
        }

        updateGroupSelectionList();
    }

    public SubScene getSubScene()
    {
        return subScene;
    }

    private DoubleProperty demandedCameraRotationYProperty()
    {
        return demandedCameraRotationY;
    }

    private void setDragMode(DragMode value)
    {
        dragMode.set(value);
    }

    private double preAnimationCameraXAngle = 0;
    private double preAnimationCameraYAngle = 0;
    private boolean needToRevertCameraPosition = false;

    private void startSettingsAnimation()
    {
        preAnimationCameraXAngle = demandedCameraRotationX.get();
        preAnimationCameraYAngle = demandedCameraRotationY.get();
        rotateCameraAroundAxesTo(30, demandedCameraRotationYProperty().get());
        needToRevertCameraPosition = true;
        settingsScreenAnimationTimer.start();
    }

    private void stopSettingsAnimation()
    {
        settingsScreenAnimationTimer.stop();
        if (needToRevertCameraPosition == true)
        {
            rotateCameraAroundAxesTo(preAnimationCameraXAngle, preAnimationCameraYAngle);
            needToRevertCameraPosition = false;
        }
    }

    /**
     * Isolate the group contents for the selected group that contains the
     * selected MeshView.
     */
    private void isolateForSelectedMeshView(MeshView meshView)
    {
        ModelContainer parentModelContainer = (ModelContainer) meshView.getParent();
        ModelGroup ancestorSelectedGroup = getAncestorSelectedGroup(parentModelContainer);
        if (ancestorSelectedGroup != null)
        {
            projectSelection.deselectAllModels();

            //Just select the intersected node...
            projectSelection.addSelectedItem(parentModelContainer);

            updateGroupSelectionList();
        }
    }

    /**
     * Isolate the given ModelGroup/Container. De-isolate all other
     * modelgroups/containers.
     */
    private void isolateGroupChildren(ModelGroup modelGroup, ModelContainer selectedModel)
    {
        inSelectedGroupButNotSelected.clear();

        for (ModelContainer modelContainer : modelGroup.getDescendentModelContainers())
        {
            if (modelContainer != selectedModel)
            {
                inSelectedGroupButNotSelected.add(modelContainer);
            }
        }
    }

    /**
     * Get the first ancestor group that is selected. If no ancestor is selected
     * then return null.
     */
    private ModelGroup getTopLevelAncestorGroup(ModelContainer parentModelContainer)
    {
        ModelGroup topLevelGroup = null;

        if (parentModelContainer != null)
        {
            while (parentModelContainer.getParentModelContainer() != null)
            {
                parentModelContainer = parentModelContainer.getParentModelContainer();
                if (parentModelContainer instanceof ModelGroup)
                {
                    topLevelGroup = (ModelGroup) parentModelContainer;
                }
            }
        }
        return topLevelGroup;
    }

    /**
     * Get the first ancestor group that is selected. If no ancestor is selected
     * then return null.
     */
    private ModelGroup getAncestorSelectedGroup(ModelContainer parentModelContainer)
    {
        if (parentModelContainer != null)
        {
            while (parentModelContainer.getParentModelContainer() != null)
            {
                parentModelContainer = parentModelContainer.getParentModelContainer();
                if (projectSelection.isSelected(parentModelContainer))
                {
                    return (ModelGroup) parentModelContainer;
                }
            }
        }
        return null;
    }

    /**
     * If either the chosen filaments, x/y/z position , application mode or
     * printer changes then this must be called. In LAYOUT mode the filament
     * colours should reflect the project filament colours except if the
     * position is off the bed then that overrides the project colours. In
     * SETTINGS mode the filament colours should reflect the project print
     * settings filament colours, taking into account the support type.
     */
    private void updateModelColours(ProjectifiableThing projectifiableThing)
    {
        if (projectifiableThing instanceof ModelContainer)
        {
            ModelContainer modelContainer = (ModelContainer) projectifiableThing;
            if (modelContainer instanceof ModelGroup)
            {
                for (ModelContainer childModel : ((ModelGroup) modelContainer).getDescendentModelContainers())
                {
                    if (!(childModel instanceof ModelGroup))
                    {
                        updateModelColours(childModel);
                    }
                }
            } else
            {
                Printer selectedPrinter = Lookup.getSelectedPrinterProperty().get();
                Filament filament0 = null;
                Filament filament1 = null;

                if (selectedPrinter != null)
                {
                    filament0 = selectedPrinter.effectiveFilamentsProperty().get(0);
                    filament1 = selectedPrinter.effectiveFilamentsProperty().get(1);
                }
                PhongMaterial materialToUseForExtruder0 = null;
                PhongMaterial materialToUseForExtruder1 = null;

                if (applicationStatus.getMode() == ApplicationMode.SETTINGS)
                {
                    if (selectedPrinter != null
                            && selectedPrinter.headProperty().get() != null)
                    {
                        if (filament0 != FilamentContainer.UNKNOWN_FILAMENT)
                        {
                            materialToUseForExtruder0 = loaded1Material;
                            Color dispColour = filament0.getDisplayColour();
                            if (dispColour.getBlue() == 0
                                    && dispColour.getRed() == 0
                                    && dispColour.getGreen() == 0)
                            {
                                dispColour = dispColour.brighter().brighter();
                            }

                            loaded1Material.setDiffuseColor(dispColour);
                        } else
                        {
                            materialToUseForExtruder0 = greyExcludedMaterial;
                        }

                        //Single material heads can only use 1 material
                        if (selectedPrinter.headProperty().get().headTypeProperty().get() == Head.HeadType.SINGLE_MATERIAL_HEAD)
                        {
                            materialToUseForExtruder1 = materialToUseForExtruder0;
                        } else if (selectedPrinter.headProperty().get().headTypeProperty().get() == Head.HeadType.DUAL_MATERIAL_HEAD)
                        {
                            if (filament1 != FilamentContainer.UNKNOWN_FILAMENT)
                            {
                                materialToUseForExtruder1 = loaded2Material;
                                Color dispColour = filament1.getDisplayColour();
                                if (dispColour.getBlue() == 0
                                        && dispColour.getRed() == 0
                                        && dispColour.getGreen() == 0)
                                {
                                    dispColour = dispColour.brighter().brighter();
                                }
                                loaded2Material.setDiffuseColor(dispColour);
                            } else
                            {
                                materialToUseForExtruder1 = greyExcludedMaterial;
                            }

                            if (!selectedPrinter.extrudersProperty().get(0).isFittedProperty().get() && !selectedPrinter.extrudersProperty().get(1).isFittedProperty().get())
                            {
                                materialToUseForExtruder0 = greyExcludedMaterial;
                                materialToUseForExtruder1 = greyExcludedMaterial;
                            } else if (!selectedPrinter.extrudersProperty().get(0).isFittedProperty().get())
                            {
                                materialToUseForExtruder0 = materialToUseForExtruder1;
                            } else if (!selectedPrinter.extrudersProperty().get(1).isFittedProperty().get())
                            {
                                materialToUseForExtruder1 = materialToUseForExtruder0;
                            }
                        }
                    }
                } else
                {
                    materialToUseForExtruder0 = extruder1Material;
                    materialToUseForExtruder1 = extruder2Material;
                }
                
                if(selectedPrinter != null && 
                        selectedPrinter.printerConnectionProperty().isEqualTo(PrinterConnection.OFFLINE).get()) 
                {
                    materialToUseForExtruder0 = extruder1Material;
                    materialToUseForExtruder1 = extruder2Material;
                }

                updateModelColour(materialToUseForExtruder0, materialToUseForExtruder1, modelContainer);
            }
        }
    }

    /**
     * If either the chosen filaments, x/y/z position , application mode or
     * printer changes then this must be called. In LAYOUT mode the filament
     * colours should reflect the project filament colours except if the
     * position is off the bed then that overrides the project colours. In
     * SETTINGS mode the filament colours should reflect the project print
     * settings filament colours, taking into account the support type.
     */
    private void updateModelColours()
    {
        for (ProjectifiableThing model : loadedModels)
        {
            model.checkOffBed();
            updateModelColours(model);
        }
    }

    private void updateModelColour(PhongMaterial materialToUseForExtruder0, PhongMaterial materialToUseForExtruder1, ModelContainer model)
    {
        boolean showMisplacedColour = applicationStatus.getMode() == ApplicationMode.LAYOUT;

        if (inSelectedGroupButNotSelected.isEmpty())
        {
            model.updateColour(materialToUseForExtruder0, materialToUseForExtruder1, showMisplacedColour);
        } else
        {
            if (inSelectedGroupButNotSelected.contains(model))
            {
                model.updateColour(extruder1TransparentMaterial, extruder2TransparentMaterial, showMisplacedColour);
            } else if (model.isSelected())
            {
                model.updateColour(materialToUseForExtruder0, materialToUseForExtruder1, showMisplacedColour);
            } else
            {
                model.updateColour(greyExcludedMaterial, greyExcludedMaterial, showMisplacedColour);
            }
        }
    }

    private void deselectAllModels()
    {
        for (ProjectifiableThing modelContainer : loadedModels)
        {
            deselectModel((ModelContainer) modelContainer);
        }
    }

    /**
     * Models must reflect the project filament colours.
     */
    private void setupFilamentListeners(ModelContainerProject project)
    {
        project.getExtruder0FilamentProperty().addListener(
                (ObservableValue<? extends Filament> observable, Filament oldValue, Filament newValue) ->
        {
            updateModelColours();
        });

        project.getExtruder1FilamentProperty().addListener(
                (ObservableValue<? extends Filament> observable, Filament oldValue, Filament newValue) ->
        {
            updateModelColours();
        });
        updateModelColours();
    }

    @Override
    public void whenModelAdded(ProjectifiableThing projectifiableThing)
    {
        ModelContainer modelContainer = (ModelContainer) projectifiableThing;
        models.getChildren().add(modelContainer);
        modelContainer.setBedReference(bed);

        for (ModelContainer model : modelContainer.getModelsHoldingMeshViews())
        {
            model.setBedReference(bed);
            model.checkOffBed();
            model.heresYourCamera(camera);
        }
        updateModelColours();

        collisionManager.addModel(modelContainer);

        modelContainer.cameraViewOfYouHasChanged(cameraDistance.get());
        notifyListenersOfCameraViewChange();
    }

    @Override
    public void whenModelsRemoved(Set<ProjectifiableThing> modelContainers)
    {
        models.getChildren().removeAll(modelContainers);
        modelContainers.stream().forEach(model ->
        {
            collisionManager.removeModel((ModelContainer) model);
        });
    }

    @Override
    public void whenAutoLaidOut()
    {
        updateModelColours();
    }

    @Override
    public void whenModelsTransformed(Set<ProjectifiableThing> modelContainers)
    {
        updateModelColours();
        Set<ModelContainer> containers = (Set) modelContainers;
        collisionManager.modelsTransformed(containers);
    }

    @Override
    public void whenModelChanged(ProjectifiableThing modelContainer, String propertyName)
    {
        updateModelColours(modelContainer);
    }

    @Override
    public void whenPrinterSettingsChanged(PrinterSettingsOverrides printerSettings)
    {
    }

    @Override
    public void whenTimelapseSettingsChanged(TimelapseSettingsData timelapseSettings)
    {
    }

    private ReadOnlyDoubleProperty cutHeight = null;
    private double maxCutHeight = 0;

    private ChangeListener<Number> cutHeightChangeListener = new ChangeListener<Number>()
    {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
        {
            zCutDisplayPlane.setTranslateY(-newValue.doubleValue());
        }
    };

    public void showZCutPlane(ModelContainer modelContainer, ReadOnlyDoubleProperty cutHeight)
    {
        processModeChange();

        zCutDisplayPlane.setWidth(modelContainer.getTransformedWidth());
        zCutDisplayPlane.setDepth(modelContainer.getTransformedDepth());
        zCutDisplayPlane.setTranslateX(modelContainer.getTransformedCentreX());
        zCutDisplayPlane.setTranslateZ(modelContainer.getTransformedCentreDepth());
        zCutDisplayPlane.setTranslateY(-cutHeight.get());

        this.cutHeight = cutHeight;
        maxCutHeight = modelContainer.getTransformedHeight();
        cutHeight.addListener(cutHeightChangeListener);
    }

    public void clearZCutModelPlane()
    {
        models.setMouseTransparent(false);
        translationDragPlane.setMouseTransparent(true);
        verticalDragPlane.setMouseTransparent(true);
        cameraTranslateDragPlane.setMouseTransparent(true);
        zCutDisplayPlane.setMouseTransparent(true);
        zCutDisplayPlane.setOpacity(0.0);

        cutHeight.removeListener(cutHeightChangeListener);
        cutHeight = null;
    }

    public void changeZCutHeight(double height)
    {
        zCutDisplayPlane.setTranslateY(-height);
    }

    TimeUtils csgTimer = new TimeUtils();

    public List<ModelContainer> cutModelAt(ModelContainer modelContainer, double height)
    {
        List<ModelContainer> generatedMCs = new ArrayList<>();
//        try
//        {
//            csgTimer.timerStart(this, "toCSG");
//            CSG modelAsCSG = MeshUtils.mesh2CSG(modelContainer.getMeshView());
//
////            Bounds modelBounds = modelAsCSG.getBounds();
////            steno.info("Bounds of model " + modelBounds);
////            CSG copyOfmodelAsCSG = modelAsCSG.clone();
//            csgTimer.timerStop(this, "toCSG");
//            steno.info("Time to CSG " + csgTimer.timeTimeSoFar_ms(this, "toCSG"));
//
////            csgTimer.timerStart(this, "cut");
////            Vector3d boxWHD = new Vector3d(modelContainer.getTransformedWidth(), 5, modelContainer.getTransformedDepth());
////            Vector3d boxCentreTop = new Vector3d(0, -cutHeight.get(), 0);
//////            Vector3d boxCentreBottom = new Vector3d(modelContainer.getTransformedCentreX(), -modelContainer.getTransformedHeight() * 0.25, modelContainer.getTransformedCentreDepth());
////            csgTimer.timerStop(this, "cut");
////            steno.info("Time to cut " + csgTimer.timeTimeSoFar_ms(this, "cut"));
////
////            csgTimer.timerStart(this, "createCube");
////            CSG topCutbox = new Cube(boxCentreTop, boxWHD).toCSG();
//////            CSG topCutbox = new Cube(60).toCSG();
////            Bounds boxBounds = topCutbox.getBounds();
////            steno.info("Bounds of cutbox " + boxBounds);
//////            CSG bottomCutbox = new Cube(boxCentreBottom, boxWHD).toCSG();
////            csgTimer.timerStop(this, "createCube");
////            steno.info("Time to createCube " + csgTimer.timeTimeSoFar_ms(this, "createCube"));
//            csgTimer.timerStart(this, "split");
//            modelAsCSG.setOptType(CSG.OptType.NONE);
//            PlaneBisect bisector = new PlaneBisect();
//            PlaneBisect.TopBottomCutPair topAndBottom = bisector.clipAtHeight(modelAsCSG, height);
//            csgTimer.timerStop(this, "split");
//            steno.info("Time to split " + csgTimer.timeTimeSoFar_ms(this, "split"));
//
//            csgTimer.timerStart(this, "toMesh");
//            MeshContainer topPartContainer = topAndBottom.getTopPart().toJavaFXMeshSimple(null);
//            List<Mesh> topPartMeshes = topPartContainer.getMeshes();
//            MeshContainer bottomPartContainer = topAndBottom.getBottomPart().toJavaFXMeshSimple(null);
//            List<Mesh> bottomPartMeshes = bottomPartContainer.getMeshes();
//            csgTimer.timerStop(this, "toMesh");
//            steno.info("Time to extract mesh " + csgTimer.timeTimeSoFar_ms(this, "toMesh"));
//
//            steno.info("There are " + topPartMeshes.size() + " meshes");
//
////            csgTimer.timerStart(this, "Split");
////            List<TriangleMesh> subMeshes = MeshSeparator.separate((TriangleMesh) topPartMeshes.get(0));
////            csgTimer.timerStop(this, "Split");
////            System.out.println("Split " + csgTimer.timeTimeSoFar_ms(this, "Split"));
//            csgTimer.timerStart(this, "CreateModels");
//            int index = 1;
////            for (TriangleMesh mesh : (TriangleMesh)topPartMeshes.get(0))
////            {
//            TriangleMesh newMesh = (TriangleMesh) topPartMeshes.get(0);
//            celtech.utils.threed.MeshUtils.removeUnusedAndDuplicateVertices(newMesh);
//
//            MeshView newMeshView = new MeshView(newMesh);
//            ModelContainer mc = new ModelContainer(modelContainer.getModelFile(), newMeshView);
//            mc.setModelName(modelContainer.getModelName() + " " + index);
//            mc.setState(modelContainer.getState());
//            mc.getAssociateWithExtruderNumberProperty().set(
//                    modelContainer.getAssociateWithExtruderNumberProperty().get());
//            mc.dropToBed();
//            mc.checkOffBed();
//            generatedMCs.add(mc);
//            index++;
////            }
//            csgTimer.timerStop(this, "CreateModels");
//            System.out.println("CreateModels " + csgTimer.timeTimeSoFar_ms(this, "CreateModels"));
//        } catch (IOException ex)
//        {
//
//        }

        return generatedMCs;
    }

    private void orientVerticalDragPlane(PickResult pickResult)
    {
        Node pickedNode = pickResult.getIntersectedNode();
        Point3D pickedPoint = pickResult.getIntersectedPoint();
        Point3D scenePoint = pickedNode.localToScene(pickedPoint);
        Point3D bedPoint = bed.sceneToLocal(scenePoint);

        verticalDragPlane.setTranslateX(bedPoint.getX());
        verticalDragPlane.setTranslateZ(bedPoint.getZ());
        verticalDragPlane.setRotate(-demandedCameraRotationY.get());
    }

    public void addCameraViewChangeListener(CameraViewChangeListener listener)
    {
        if (!cameraViewChangeListeners.contains(listener))
        {
            cameraViewChangeListeners.add(listener);
        }
    }

    @Override
    public Point2D convertWorldCoordinatesToScreen(double worldX, double worldY, double worldZ)
    {
        return bed.localToScreen(worldX, worldY, worldZ);
    }

    public ReadOnlyObjectProperty<DragMode> getDragModeProperty()
    {
        return dragMode;
    }

    private void addPrintVolumeBoundingBox(Group bed) {
        if (bed != null)
        {
            Node newBoundingBox = createBoundingBox(currentPrinterConfiguration.getPrintVolumeWidth(),
                    currentPrinterConfiguration.getPrintVolumeDepth(),
                    currentPrinterConfiguration.getPrintVolumeHeight());
            bed.getChildren().remove(printVolumeBoundingBox);
            bed.getChildren().add(newBoundingBox);
            printVolumeBoundingBox = newBoundingBox;
        }
    }

    private void transitionCameraToDefaults()
    {
        transitionCameraTo(1000,
                defaultXTranslate,
                defaultYTranslate,
                30,
                0,
                defaultDistance);
    }

    private void updateProjectifiableThings() {
        for (ProjectifiableThing model : loadedModels)
        {
            model.setBedCentreOffsetTransform();
        }
    }
}
