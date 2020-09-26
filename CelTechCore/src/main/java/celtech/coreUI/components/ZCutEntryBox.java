package celtech.coreUI.components;

import celtech.Lookup;
import celtech.appManager.ModelContainerProject;
import celtech.appManager.undo.UndoableProject;
import celtech.coreUI.LayoutSubmode;
import celtech.coreUI.visualisation.ScreenExtents;
import celtech.coreUI.visualisation.ScreenExtentsProvider;
import celtech.coreUI.visualisation.ScreenExtentsProvider.ScreenExtentsListener;
import celtech.coreUI.visualisation.ScreenExtentsProviderTwoD;
import celtech.coreUI.visualisation.ThreeDViewManager;
import celtech.modelcontrol.ModelContainer;
import celtech.modelcontrol.ProjectifiableThing;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.utils.TimeUtils;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 *
 * @author Ian
 */
public class ZCutEntryBox extends HBox implements ScreenExtentsListener
{

    private final Pane paneInWhichControlResides;
    private final ObjectProperty<LayoutSubmode> layoutSubmodeProperty;
    private final ThreeDViewManager viewManager;
    private ModelContainer currentModel = null;
    private final ModelContainerProject project;
    private final UndoableProject undoableProject;
    private Thread cutThread = null;
    private TimeUtils timeUtils = new TimeUtils();

    @FXML
    private RestrictedNumberField cutHeight;

    @FXML
    private void accept(ActionEvent event)
    {
        layoutSubmodeProperty.set(LayoutSubmode.SELECT);
        ZCutEntryBox instance = this;

        if (cutThread == null)
        {
            Task cutTask = new Task<Void>()
            {
                @Override
                protected Void call() throws Exception
                {
                    Lookup.getSpinnerControl().startSpinning(paneInWhichControlResides);
                    timeUtils.timerStart(this, "Cut");
                    List<ModelContainer> resultingModels = viewManager.cutModelAt(currentModel, cutHeight.getAsDouble());
                    timeUtils.timerStop(this, "Cut");
                    System.out.println("Cut " + timeUtils.timeTimeSoFar_ms(this, "Cut"));
                    Set<ProjectifiableThing> modelToRemove = new HashSet<>();
                    modelToRemove.add(currentModel);
                    currentModel.removeScreenExtentsChangeListener(instance);
                    viewManager.clearZCutModelPlane();

                    Platform.runLater(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            project.removeModels(modelToRemove);
//                            project.addModel(currentModel);
//                            undoableProject.deleteModels(modelToRemove);
                            for (ModelContainer childModel : resultingModels)
                            {
                                project.addModel(childModel);
                            }
//                                undoableProject.addModel(model);

//                            Set<ModelContainer> modelContainers = Lookup.getProjectGUIState(project).getProjectSelection().getSelectedModelsSnapshot();
//                            undoableProject.cut(modelContainers, (float) -cutHeight.doubleValueProperty().get());
                            Lookup.getSpinnerControl().stopSpinning();

                        }
                    });

                    return null;
                }

            };

            cutTask.setOnSucceeded(new EventHandler()
            {
                @Override
                public void handle(Event event)
                {
                    cutThread = null;
                }
            });

            cutThread = new Thread(cutTask);
            cutThread.setName("CutThread");
            cutThread.start();
        } else
        {
            System.out.println("Cut threader is still working");
        }

//        List<ModelContainer> generatedModels = viewManager.cutModelAt(currentModel, cutHeight.doubleValueProperty().get());
//        for (ModelContainer mc : generatedModels)
//        {
//            project.addModel(mc);
//        }
    }

    @FXML
    private void cancel(ActionEvent event)
    {
        if (layoutSubmodeProperty != null)
        {
            layoutSubmodeProperty.set(LayoutSubmode.SELECT);
        }

        viewManager.clearZCutModelPlane();
        currentModel.removeScreenExtentsChangeListener(this);
    }

    public ZCutEntryBox()
    {
        paneInWhichControlResides = null;
        layoutSubmodeProperty = null;
        viewManager = null;
        project = null;
        undoableProject = null;
        loadContent();
    }

    public ZCutEntryBox(Pane paneInWhichControlResides,
            ObjectProperty<LayoutSubmode> layoutSubmodeProperty,
            ThreeDViewManager viewManager,
            ModelContainerProject project)
    {
        this.paneInWhichControlResides = paneInWhichControlResides;
        this.layoutSubmodeProperty = layoutSubmodeProperty;
        this.viewManager = viewManager;
        this.project = project;
        undoableProject = new UndoableProject(project);
        loadContent();
    }

    private void loadContent()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/celtech/resources/fxml/components/ZCutEntryBox.fxml"), BaseLookup.getLanguageBundle());
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        fxmlLoader.setClassLoader(this.getClass().getClassLoader());

        try
        {
            fxmlLoader.load();
        } catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    public void prime(ModelContainer modelContainer)
    {
        currentModel = modelContainer;
        cutHeight.setValue(modelContainer.getTransformedHeight() / 2);

        modelContainer.addScreenExtentsChangeListener(this);

//        viewManager.showZCutPlane(modelContainer, cutHeight.doubleValueProperty());

        positionCutBox(currentModel.getScreenExtents());
    }

    @Override
    public void screenExtentsChanged(ScreenExtentsProvider screenExtentsProvider)
    {
        System.out.println("New extents " + screenExtentsProvider.getScreenExtents());

        ScreenExtents extents = screenExtentsProvider.getScreenExtents();
        positionCutBox(extents);
    }

    private void positionCutBox(ScreenExtents extents)
    {
        //Half way up
        int yPosition = extents.maxY - ((extents.maxY - extents.minY) / 2);

        Bounds screenBoundsOfMe = localToScreen(getBoundsInParent());

        int xPosition = extents.minX;
        xPosition -= getMinWidth();

        if (xPosition < 0)
        {
            xPosition = 0;
        }

        //Always put this at the left hand edge
        Point2D position = paneInWhichControlResides.screenToLocal(xPosition, yPosition);
        System.out.println("Translating to " + position);
        setTranslateX(position.getX());
        setTranslateY(position.getY());
    }
}
