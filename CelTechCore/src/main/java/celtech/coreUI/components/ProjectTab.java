package celtech.coreUI.components;

import celtech.Lookup;
import celtech.appManager.ApplicationMode;
import celtech.appManager.ApplicationStatus;
import celtech.appManager.ModelContainerProject;
import celtech.appManager.Project;
import celtech.appManager.ProjectCallback;
import celtech.appManager.ProjectManager;
import celtech.appManager.ProjectMode;
import celtech.appManager.ShapeContainerProject;
import celtech.configuration.ApplicationConfiguration;
import celtech.coreUI.LayoutSubmode;
import celtech.coreUI.controllers.ProjectAwareController;
import celtech.coreUI.visualisation.BedAxes;
import celtech.coreUI.visualisation.DimensionLineManager;
import celtech.coreUI.visualisation.DragMode;
import celtech.coreUI.visualisation.ModelLoader;
import celtech.coreUI.visualisation.SVGViewManager;
import celtech.coreUI.visualisation.ThreeDViewManager;
import celtech.modelcontrol.ModelContainer;
import celtech.modelcontrol.ProjectifiableThing;
import celtech.roboxbase.BaseLookup;
import static celtech.roboxbase.utils.DeDuplicator.suggestNonDuplicateName;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.effect.Glow;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class ProjectTab extends Tab implements ProjectCallback
{

    private final Stenographer steno = StenographerFactory.getStenographer(
            ProjectTab.class.getName());

    private final Label nonEditableProjectNameField = new Label();
    private final RestrictedTextField editableProjectNameField = new RestrictedTextField();
    private Project project = null;
    private AnchorPane basePane = null;
    private AnchorPane overlayPane = null;
    private ThreeDViewManager viewManager = null;
    private SVGViewManager svgViewManager = null;
    private final ProjectManager projectManager = ProjectManager.getInstance();
    private boolean titleBeingEdited = false;
    private final ModelLoader modelLoader = new ModelLoader();
    private DimensionLineManager dimensionLineManager = null;
    private BedAxes bedAxes = null;
    private ZCutEntryBox zCutEntryBox = null;
    private ObjectProperty<LayoutSubmode> layoutSubmode;
    private ProjectAwareController projectAwareController = null;

    private final ReadOnlyDoubleProperty tabDisplayWidthProperty;
    private final ReadOnlyDoubleProperty tabDisplayHeightProperty;

    private final BooleanProperty hideDimensions = new SimpleBooleanProperty(false);

    private final VBox nonSpecificModelIndicator = new VBox();

    private VBox rhInsetContainer = null;
    private LoadedPanelData settingsInsetPanelData = null;
    private LoadedPanelData timeCostInsetPanelData = null;
    private LoadedPanelData stylusSettingsInsetPanelData = null;
    private LoadedPanelData modelActionsInsetPanelData = null;
    private LoadedPanelData timelapseInsetPanelData = null;
    
    private class LoadedPanelData
    {

        private final Node node;
        private final ProjectAwareController controller;

        public LoadedPanelData(Node node, ProjectAwareController controller)
        {
            this.node = node;
            this.controller = controller;
        }

        public Node getNode()
        {
            return node;
        }

        public ProjectAwareController getController()
        {
            return controller;
        }
    }

    public ProjectTab(
            ReadOnlyDoubleProperty tabDisplayWidthProperty,
            ReadOnlyDoubleProperty tabDisplayHeightProperty)
    {
        this.tabDisplayWidthProperty = tabDisplayWidthProperty;
        this.tabDisplayHeightProperty = tabDisplayHeightProperty;
        coreInitialisation();
    }

    public ProjectTab(Project inboundProject,
            ReadOnlyDoubleProperty tabDisplayWidthProperty,
            ReadOnlyDoubleProperty tabDisplayHeightProperty,
            boolean loadingAtStartup)
    {
        project = inboundProject;
        this.tabDisplayWidthProperty = tabDisplayWidthProperty;
        this.tabDisplayHeightProperty = tabDisplayHeightProperty;
        coreInitialisation();
        initialiseWithProject(loadingAtStartup);
    }
    
    public Project getProject()
    {
        return project;
    }

    public ThreeDViewManager getThreeDViewManager()
    {
        return viewManager;
    }

    public SVGViewManager getSVGViewManager() 
    {
        return svgViewManager;
    }

    private void primeTabInsetPanels(boolean tabIsSelected)
    {
        if (rhInsetContainer != null)
        {
            if (tabIsSelected == false)
            {
                if (timeCostInsetPanelData != null)
                {
                    timeCostInsetPanelData.getController().shutdownController();
                    rhInsetContainer.getChildren().remove(timeCostInsetPanelData.getNode());
                    timeCostInsetPanelData = null;
                }
                if (settingsInsetPanelData != null)
                {
                    settingsInsetPanelData.getController().shutdownController();
                    rhInsetContainer.getChildren().remove(settingsInsetPanelData.getNode());
                    settingsInsetPanelData = null;
                }
                if (timelapseInsetPanelData != null)
                {
                    timelapseInsetPanelData.getController().shutdownController();
                    rhInsetContainer.getChildren().remove(timelapseInsetPanelData.getNode());
                    timelapseInsetPanelData = null;
                }
                if (stylusSettingsInsetPanelData != null)
                {
                    stylusSettingsInsetPanelData.getController().shutdownController();
                    rhInsetContainer.getChildren().remove(stylusSettingsInsetPanelData.getNode());
                    stylusSettingsInsetPanelData = null;
                }
            } else
            {
                switch (project.getMode())
                {
                    case SVG:
                        if (stylusSettingsInsetPanelData == null)
                        {
                            stylusSettingsInsetPanelData = loadInsetPanel("stylusSettingsInsetPanel.fxml", project);
                            stylusSettingsInsetPanelData.getNode().setVisible(false);
                            rhInsetContainer.getChildren().add(0, stylusSettingsInsetPanelData.getNode());
                        }
                        break;
                    
                    case MESH:
                        if (settingsInsetPanelData == null)
                        {
                            settingsInsetPanelData = loadInsetPanel("settingsInsetPanel.fxml", project);
                            settingsInsetPanelData.getNode().setVisible(false);
                            rhInsetContainer.getChildren().add(settingsInsetPanelData.getNode());
                        }
                        if (timeCostInsetPanelData == null)
                        {
                            timeCostInsetPanelData = loadInsetPanel("timeCostInsetPanel.fxml", project);
                            timeCostInsetPanelData.getNode().setVisible(false);
                            rhInsetContainer.getChildren().add(0, timeCostInsetPanelData.getNode());
                        }
                        break;
                    
                    default:
                        break;
                }
            }
        }
    }

    private void coreInitialisation()
    {
        setOnClosed((Event t) ->
        {
            steno.debug("Beginning project save");
            saveAndCloseProject();
            steno.debug("Completed project save");
            projectManager.saveState();
        });

        setOnSelectionChanged((Event t) ->
        {
            if (bedAxes != null)
            {
                bedAxes.updateArrowAndTextPosition();
            }
        });

        AnchorPane.setBottomAnchor(nonSpecificModelIndicator, 0.0);
        AnchorPane.setTopAnchor(nonSpecificModelIndicator, 0.0);
        AnchorPane.setLeftAnchor(nonSpecificModelIndicator, 0.0);
        AnchorPane.setRightAnchor(nonSpecificModelIndicator, 0.0);
        nonSpecificModelIndicator.setAlignment(Pos.CENTER);
        nonSpecificModelIndicator.setMouseTransparent(true);
        nonSpecificModelIndicator.setPickOnBounds(false);
        Label loadAModel = new Label(BaseLookup.i18n("projectTab.loadAModel"));
        loadAModel.getStyleClass().add("load-a-model-text");
        nonSpecificModelIndicator.getChildren().add(loadAModel);

        basePane = new AnchorPane();
        basePane.getStyleClass().add("project-view-background");

        overlayPane = new AnchorPane();
        overlayPane.setMouseTransparent(true);
        overlayPane.setPickOnBounds(false);

        basePane.getChildren().add(nonSpecificModelIndicator);

        setupDragHandlers();

        this.setContent(basePane);

        this.setGraphic(nonEditableProjectNameField);

        this.selectedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                primeTabInsetPanels(newValue);
            }
        });
    }

    private void initialiseWithProject(boolean loadingAtStartup)
    {
        rhInsetContainer = new VBox();
        rhInsetContainer.setSpacing(30);

        rhInsetContainer.mouseTransparentProperty().bind(ApplicationStatus.getInstance().modeProperty().isNotEqualTo(ApplicationMode.SETTINGS));
        basePane.getChildren().add(rhInsetContainer);

//        VBox dimensionContainer = new VBox();
//        dimensionContainer.setMouseTransparent(true);
//        AnchorPane.setBottomAnchor(dimensionContainer, 0.0);
//        AnchorPane.setTopAnchor(dimensionContainer, 0.0);
//        AnchorPane.setRightAnchor(dimensionContainer, 0.0);
//        AnchorPane.setLeftAnchor(dimensionContainer, 0.0);


        modelActionsInsetPanelData = loadInsetPanel("modelEditInsetPanel.fxml", project);
        AnchorPane.setTopAnchor(modelActionsInsetPanelData.getNode(), 30.0);
        AnchorPane.setLeftAnchor(modelActionsInsetPanelData.getNode(), 30.0);
        basePane.getChildren().add(modelActionsInsetPanelData.getNode());


        layoutSubmode = Lookup.getProjectGUIState(project).getLayoutSubmodeProperty();

        layoutSubmode.addListener(new ChangeListener<LayoutSubmode>()
        {
            @Override
            public void changed(ObservableValue<? extends LayoutSubmode> observable, LayoutSubmode oldValue, LayoutSubmode newValue)
            {
                if (newValue == LayoutSubmode.Z_CUT)
                {
                    Set<ProjectifiableThing> selectedModelContainers
                            = Lookup.getProjectGUIState(project).getProjectSelection().getSelectedModelsSnapshot();
                    if (project instanceof ModelContainerProject)
                    {
                        zCutEntryBox.prime((ModelContainer) selectedModelContainers.iterator().next());
                        overlayPane.getChildren().add(zCutEntryBox);
                    }
                } else
                {
                    if (overlayPane.getChildren().contains(zCutEntryBox))
                    {
                        overlayPane.getChildren().remove(zCutEntryBox);
                    }
                }
            }
        });

        AnchorPane.setTopAnchor(rhInsetContainer, 30.0);
        AnchorPane.setRightAnchor(rhInsetContainer, 30.0);

        setupNameFields();

        if (project instanceof ModelContainerProject)
        {
            setup3DView();
        } else if (project instanceof ShapeContainerProject)
        {
            setupSVGView();
        }

        fireProjectSelected();

        projectManager.projectOpened(project);
        projectManager.saveState();

        if (!loadingAtStartup)
        {
            primeTabInsetPanels(true);
        }
    }

    private void setup3DView()
    {
        dimensionLineManager = new DimensionLineManager(basePane, project, hideDimensions);

        nonSpecificModelIndicator.setVisible(false);
        viewManager = new ThreeDViewManager((ModelContainerProject) project,
                tabDisplayWidthProperty,
                tabDisplayHeightProperty);

        modelActionsInsetPanelData.getNode().mouseTransparentProperty().bind(viewManager.getDragModeProperty().isNotEqualTo(DragMode.IDLE));

        zCutEntryBox = new ZCutEntryBox(overlayPane, layoutSubmode, viewManager, (ModelContainerProject) project);
        bedAxes = new BedAxes(viewManager);
        viewManager.addCameraViewChangeListener(bedAxes);

        basePane.getChildren().add(0, viewManager.getSubScene());
        overlayPane.getChildren().add(bedAxes);

        hideDimensions.bind(viewManager.getDragModeProperty().isNotEqualTo(DragMode.IDLE));

    }

    private void setupSVGView()
    {
        nonSpecificModelIndicator.setVisible(false);
        svgViewManager = new SVGViewManager(project);
        //svgViewManager.setMaxWidth(basePane.getWidth());
        //svgViewManager.setMaxHeight(basePane.getHeight());

        AnchorPane.setBottomAnchor(svgViewManager, 0.0);
        AnchorPane.setTopAnchor(svgViewManager, 0.0);
        AnchorPane.setLeftAnchor(svgViewManager, 0.0);
        AnchorPane.setRightAnchor(svgViewManager, 0.0);

        basePane.getChildren().add(0, svgViewManager);
        
        // Not sure why, but if the base pane is passed to the dimension line manager as the
        // parent to which dimensions are added/removed, if a dimension is moved outside the base pane,
        // it resizes to fit. This triggers the svgViewManager to resize, which then causes the bed
        // to resize. This behaviour does not happen if the svgViewManager is passed as the parent
        // pane to which dimensions are added/removed.
        dimensionLineManager = new DimensionLineManager(svgViewManager, project, hideDimensions);
        hideDimensions.bind(svgViewManager.getDragModeProperty().isNotEqualTo(DragMode.IDLE));
    }

    private LoadedPanelData loadInsetPanel(String innerPanelFXMLName, Project project)
    {
        URL settingsInsetPanelURL = getClass().getResource(
                ApplicationConfiguration.fxmlPanelResourcePath + innerPanelFXMLName);
        FXMLLoader loader = new FXMLLoader(settingsInsetPanelURL, BaseLookup.getLanguageBundle());
        Node insetPanel = null;
        try
        {
            insetPanel = loader.load();
            projectAwareController = (ProjectAwareController) loader.getController();
            projectAwareController.setProject(project);
        } catch (IOException ex)
        {
            steno.exception("Unable to load inset panel: " + innerPanelFXMLName, ex);
        }
        return new LoadedPanelData(insetPanel, projectAwareController);
    }

    private void setupNameFields()
    {
        nonEditableProjectNameField.getStyleClass().add("nonEditableProjectTab");
        editableProjectNameField.getStyleClass().add("editableProjectTab");
        editableProjectNameField.setDirectorySafeName(true);
        editableProjectNameField.setRestrict(" -_0-9a-zA-Z\\p{L}\\p{M}*+");
        editableProjectNameField.setMaxLength(25);

        nonEditableProjectNameField.textProperty().bind(
                project.projectNameProperty());

        nonEditableProjectNameField.setOnMouseClicked((MouseEvent event) ->
        {
            if (event.getClickCount() == 2)
            {
                editableProjectNameField.setText(
                        nonEditableProjectNameField.getText());
                setGraphic(editableProjectNameField);
                editableProjectNameField.selectAll();
                editableProjectNameField.requestFocus();
                titleBeingEdited = true;
            }
        });

        editableProjectNameField.focusedProperty().addListener(
                new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov,
                    Boolean t, Boolean t1)
            {
                if (!t1)
                {
                    switchToNonEditableTitle();
                }
            }
        });

        editableProjectNameField.setOnAction((ActionEvent event) ->
        {
            switchToNonEditableTitle();
        });
    }

    private void setupDragHandlers()
    {
        basePane.setOnDragOver(new EventHandler<DragEvent>()
        {
            @Override
            public void handle(DragEvent event)
            {
                if (ApplicationStatus.getInstance().modeProperty().getValue()
                        == ApplicationMode.LAYOUT)
                {
                    if (event.getGestureSource() != basePane)
                    {
                        Dragboard dragboard = event.getDragboard();
                        if (dragboard.hasFiles())
                        {
                            List<File> fileList = dragboard.getFiles();
                            boolean accept = true;
                            for (File file : fileList)
                            {
                                boolean extensionFound = false;
                                ProjectMode projectMode = ProjectMode.NONE;
                                if (project != null)
                                {
                                    projectMode = project.getMode();
                                }
                                List<String> extensions = ApplicationConfiguration.
                                        getSupportedFileExtensions(projectMode);

                                for (String extension : extensions)
                                {
                                    if (file.getName().toUpperCase().endsWith(
                                            extension.toUpperCase()))
                                    {
                                        extensionFound = true;
                                        break;
                                    }
                                }

                                if (!extensionFound)
                                {
                                    accept = false;
                                    break;
                                }
                            }

                            if (accept)
                            {
                                event.acceptTransferModes(TransferMode.COPY);
                                event.consume();
                            }
                        }
                    }
                }
            }
        });

        basePane.setOnDragEntered(new EventHandler<DragEvent>()
        {
            public void handle(DragEvent event)
            {
                /* the drag-and-drop gesture entered the target */
 /* show to the user that it is an actual gesture target */
                if (ApplicationStatus.getInstance().modeProperty().getValue()
                        == ApplicationMode.LAYOUT)
                {
                    if (event.getGestureSource() != basePane)
                    {
                        Dragboard dragboard = event.getDragboard();
                        if (dragboard.hasFiles())
                        {
                            List<File> fileList = dragboard.getFiles();
                            boolean accept = true;
                            for (File file : fileList)
                            {
                                boolean extensionFound = false;
                                ProjectMode projectMode = ProjectMode.NONE;
                                if (project != null)
                                {
                                    projectMode = project.getMode();
                                }
                                List<String> extensions = ApplicationConfiguration.
                                        getSupportedFileExtensions(projectMode);
                                for (String extension : extensions)
                                {
                                    if (file.getName().endsWith(extension))
                                    {
                                        extensionFound = true;
                                        break;
                                    }
                                }

                                if (!extensionFound)
                                {
                                    accept = false;
                                    break;
                                }
                            }

                            if (accept)
                            {
                                basePane.setEffect(new Glow());
                                event.consume();
                            }
                        }
                    }
                }
            }
        });

        basePane.setOnDragExited(new EventHandler<DragEvent>()
        {
            public void handle(DragEvent event)
            {
                /* mouse moved away, remove the graphical cues */
                basePane.setEffect(null);

                event.consume();
            }
        });

        basePane.setOnDragDropped((DragEvent event) ->
        {
            boolean success = false;
            if (event.getGestureTarget() == basePane)
            {
                /* data dropped */
                steno.debug("onDragDropped");
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                if (db.hasFiles())
                {
                    modelLoader.loadExternalModels(project, db.getFiles(), true, this, false);
                } else
                {
                    steno.error("No files in dragboard");
                }
                /* let the source know whether the string was successfully
                 * transferred and used */
                event.setDropCompleted(success);

                event.consume();
            }
            /* let the source know whether the string was successfully
             * transferred and used */
            event.setDropCompleted(success);

            event.consume();
        });
    }

    private void switchToNonEditableTitle()
    {
        if (titleBeingEdited == true)
        {
            String newProjectName = editableProjectNameField.getText();
            Set<String> currentProjectNames = projectManager.getOpenAndAvailableProjectNames();
            newProjectName = suggestNonDuplicateName(newProjectName, currentProjectNames);
            project.setProjectName(newProjectName);
            project.setProjectNameModified(true);
            setGraphic(nonEditableProjectNameField);
            titleBeingEdited = false;
        }
    }

    public void saveAndCloseProject()
    {
        if (viewManager != null)
            viewManager.shutdown();
        if (project != null)
        {
            Project.saveProject(project);
            if (projectAwareController != null)
                projectAwareController.setProject(null);
            if (projectManager != null)
                projectManager.projectClosed(project);
            project = null;
        }
    }

    public void fireProjectSelected()
    {
        Lookup.setSelectedProject(project);
    }
    
    public void fireProjectDeselected()
    {
        if (project != null && !project.isProjectSaved())
        {
            Project.saveProject(project);
        }
    }

    @Override
    public void modelAddedToProject(Project project)
    {
        if (this.project == null)
        {
            this.project = project;
            initialiseWithProject(false);
        }
    }

    public void initialiseBlank3DProject()
    {
        if (this.project == null)
        {
            ModelContainerProject newProject = new ModelContainerProject();
            this.project = newProject;
            initialiseWithProject(false);
        }
    }

    public void initialiseBlank2DProject()
    {
        if (this.project == null)
        {
            ShapeContainerProject newProject = new ShapeContainerProject();
            this.project = newProject;
            initialiseWithProject(false);
        }
    }
    
    public Rectangle2D getPreviewRectangle()
    {
        Rectangle2D nRectangle = null;
        Node ss = null;
        if (viewManager != null)
            ss = viewManager.getSubScene();
        else if (svgViewManager != null)
            ss = svgViewManager;
        Bounds ssBounds = ss.localToScreen(ss.getBoundsInLocal());
        Bounds rhBounds = rhInsetContainer.localToScreen(rhInsetContainer.getBoundsInLocal());

        double w = rhBounds.getWidth(); // Width of the rhs is a measure of the size of the app.
        nRectangle = new Rectangle2D(ssBounds.getMinX() + 0.05 * w,
                                     ssBounds.getMinY() + 0.05 * w,
                                     ssBounds.getWidth() - 1.2 * w,
                                     ssBounds.getHeight() - 0.35 * w);
        return nRectangle;
    }

}
