package celtech.coreUI;

import celtech.Lookup;
import celtech.appManager.ApplicationMode;
import celtech.appManager.ApplicationStatus;
import celtech.appManager.ModelContainerProject;
import celtech.appManager.Project;
import celtech.appManager.ProjectCallback;
import celtech.appManager.ProjectManager;
import celtech.appManager.undo.CommandStack;
import celtech.appManager.undo.UndoableProject;
import celtech.configuration.ApplicationConfiguration;
import celtech.coreUI.components.Notifications.NotificationArea;
import celtech.coreUI.components.ProgressDialog;
import celtech.coreUI.components.ProjectTab;
import celtech.coreUI.components.Spinner;
import celtech.coreUI.components.TopMenuStrip;
import celtech.coreUI.controllers.InfoScreenIndicatorController;
import celtech.coreUI.controllers.PrinterStatusPageController;
import celtech.coreUI.controllers.panels.LibraryMenuPanelController;
import celtech.coreUI.controllers.panels.PreviewManager;
import celtech.coreUI.controllers.panels.PurgeInsetPanelController;
import celtech.coreUI.keycommands.HiddenKey;
import celtech.coreUI.keycommands.KeyCommandListener;
import celtech.coreUI.keycommands.UnhandledKeyListener;
import celtech.coreUI.visualisation.ModelLoader;
import celtech.coreUI.visualisation.ProjectSelection;
import celtech.modelcontrol.ProjectifiableThing;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.comms.DummyPrinterCommandInterface;
import celtech.roboxbase.comms.RoboxCommsManager;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.RoboxProfile;
import celtech.roboxbase.configuration.fileRepresentation.CameraProfile;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterIdentity;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.Glow;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class DisplayManager implements EventHandler<KeyEvent>, KeyCommandListener, UnhandledKeyListener, SpinnerControl,
        ProjectCallback
{

    private static final Stenographer steno = StenographerFactory.getStenographer(
            DisplayManager.class.getName());

    private static final int START_SCALING_WINDOW_HEIGHT = 700;
    private static final double MINIMUM_SCALE_FACTOR = 0.7;

    private static ApplicationStatus applicationStatus;
    private static ProjectManager projectManager;

    private static DisplayManager instance;
    private static Stage mainStage;
    private static Scene scene;

    private HBox mainHolder;
    private StackPane sidePanelContainer;
    private final HashMap<ApplicationMode, Pane> insetPanels;
    private final AnchorPane rhPanel;
    private final VBox projectTabPaneHolder;
    private final HashMap<ApplicationMode, Initializable> insetPanelControllers;
    private VBox sidePanel;

    private static AnchorPane interchangeablePanelAreaWithNotificationArea;
    private static TabPane tabDisplay;
    private static SingleSelectionModel<Tab> tabDisplaySelectionModel;
    private static Tab printerStatusTab;
    private static Tab addPageTab;
    private Tab lastLayoutTab;

    /*
     * Project loading
     */
    private ProgressDialog modelLoadDialog = null;

    private InfoScreenIndicatorController infoScreenIndicatorController = null;

    private static final String addDummyPrinterCommand = "AddDummy";
    private static final String dummyCommandPrefix = "dummy:";

    private StackPane rootStackPane;
    private AnchorPane rootAnchorPane;
    private Pane spinnerContainer;
    private Spinner spinner;

    private final NotificationArea notificationArea = new NotificationArea();

    //Display scaling
    private BooleanProperty nodesMayHaveMoved;
    
    private final BooleanProperty libraryModeEntered = new SimpleBooleanProperty(false);

    public enum DisplayScalingMode
    {

        NORMAL,
        SHORT,
        VERY_SHORT
    }

    private ObjectProperty<DisplayScalingMode> displayScalingModeProperty = new SimpleObjectProperty<>(DisplayScalingMode.NORMAL);
    private final int SHORT_SCALE_BELOW_HEIGHT = 890;
    private final int VERY_SHORT_SCALE_BELOW_HEIGHT = 700;

    // This is here solely so it shutdown can be called on it when the application closes.
    // If other things need to be added, it should be changed to a more generic callback mechanism.
    private PreviewManager previewManager = null;

    private DisplayManager()
    {
        this.rootStackPane = new StackPane();
        this.nodesMayHaveMoved = new SimpleBooleanProperty(false);
        this.insetPanelControllers = new HashMap<>();
        this.insetPanels = new HashMap<>();
        applicationStatus = ApplicationStatus.getInstance();
        projectManager = ProjectManager.getInstance();
        this.projectTabPaneHolder = new VBox();
        AnchorPane.setBottomAnchor(projectTabPaneHolder, 0.0);
        AnchorPane.setTopAnchor(projectTabPaneHolder, 0.0);
        AnchorPane.setLeftAnchor(projectTabPaneHolder, 0.0);
        AnchorPane.setRightAnchor(projectTabPaneHolder, 0.0);
        this.rhPanel = new AnchorPane();
        steno.debug("Starting AutoMaker - initialising display manager...");
        steno.debug("Starting AutoMaker - machine type is " + BaseConfiguration.getMachineType());
    }
    
    // This is here solely so shutdown can be called on it when the application closes.
    public void setPreviewManager(PreviewManager previewManager) {
        this.previewManager = previewManager;
    }

    private void loadProjectsAtStartup()
    {
        steno.debug("start load projects");
        // Load up any projects that were open last time we shut down....
        ProjectManager pm = ProjectManager.getInstance();
        List<Project> preloadedProjects = pm.getOpenProjects();

        for (int projectNumber = preloadedProjects.size() - 1; projectNumber >= 0; projectNumber--)
        {
            Project project = preloadedProjects.get(projectNumber);
            ProjectTab newProjectTab = new ProjectTab(project, tabDisplay.widthProperty(),
                    tabDisplay.heightProperty(), true);
            tabDisplay.getTabs().add(1, newProjectTab);
        }
        steno.debug("end load projects");
    }

    public void showAndSelectPrintProfile(RoboxProfile roboxProfile)
    {
        ApplicationStatus.getInstance().setMode(ApplicationMode.LIBRARY);
        Initializable initializable = insetPanelControllers.get(ApplicationMode.LIBRARY);
        LibraryMenuPanelController controller = (LibraryMenuPanelController) initializable;
        controller.showAndSelectPrintProfile(roboxProfile);
    }

    public void showAndSelectCameraProfile(CameraProfile profile)
    {
        ApplicationStatus.getInstance().setMode(ApplicationMode.LIBRARY);
        Initializable initializable = insetPanelControllers.get(ApplicationMode.LIBRARY);
        LibraryMenuPanelController controller = (LibraryMenuPanelController) initializable;
        controller.showAndSelectCameraProfile(profile);
    }

    private void switchPagesForMode(ApplicationMode oldMode, ApplicationMode newMode)
    {
        libraryModeEntered.set(false);
        infoScreenIndicatorController.setSelected(newMode == ApplicationMode.STATUS);

        // Remove the existing side panel
        if (oldMode != null)
        {
            Pane lastInsetPanel = insetPanels.get(oldMode);
            if (lastInsetPanel != null)
            {
                interchangeablePanelAreaWithNotificationArea.getChildren().remove(lastInsetPanel);
            } else
            {
                if (interchangeablePanelAreaWithNotificationArea.getChildren().contains(projectTabPaneHolder))
                {
                    interchangeablePanelAreaWithNotificationArea.getChildren().remove(projectTabPaneHolder);
                }
            }
        }

        // Now add the relevant new one...
        Pane newInsetPanel = insetPanels.get(newMode);
        if (newInsetPanel != null)
        {
            AnchorPane.setBottomAnchor(newInsetPanel, 0.0);
            AnchorPane.setTopAnchor(newInsetPanel, 0.0);
            AnchorPane.setLeftAnchor(newInsetPanel, 0.0);
            AnchorPane.setRightAnchor(newInsetPanel, 0.0);
            interchangeablePanelAreaWithNotificationArea.getChildren().add(0, newInsetPanel);
        }

        if (null != newMode)
        switch (newMode) {
            case LAYOUT:
                interchangeablePanelAreaWithNotificationArea.getChildren().add(0, projectTabPaneHolder);
                //Switch tabs if necessary
                if (tabDisplaySelectionModel.getSelectedItem() instanceof ProjectTab
                        == false)
                {
                    if (lastLayoutTab != null
                            && tabDisplay.getTabs().contains(lastLayoutTab))
                    {
                        //Select the last project tab
                        tabDisplaySelectionModel.select(lastLayoutTab);
                    } else
                    {
                        //Select either the first tab or the the + tab (so that a new project is added)
                        tabDisplaySelectionModel.select(1);
                    }
                }   break;
            case SETTINGS:
                interchangeablePanelAreaWithNotificationArea.getChildren().add(0, projectTabPaneHolder);
                break;
            case STATUS:
                interchangeablePanelAreaWithNotificationArea.getChildren().add(0, projectTabPaneHolder);
                tabDisplaySelectionModel.select(0);
                break;
            case LIBRARY:
                libraryModeEntered.set(true);
                break;
            default:
                break;
        }
    }

    public static DisplayManager getInstance()
    {
        if (instance == null)
        {
            try
            {
                instance = new DisplayManager();
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        return instance;
    }

    /**
     * Show the spinner, and keep it centred on the given region.
     */
    @Override
    public void startSpinning(Region centreRegion)
    {
        spinner.setVisible(true);
        spinner.startSpinning();
        spinner.setCentreNode(centreRegion);
    }

    /**
     * Stop and hide the spinner.
     */
    @Override
    public void stopSpinning()
    {
        spinner.setVisible(false);
        spinner.stopSpinning();
    }

    public void configureDisplayManager(Stage mainStage, String applicationName,
            String modelsToLoadAtStartup_projectName,
            List<String> modelsToLoadAtStartup,
            boolean dontGroupStartupModels)
    {
        steno.debug("start configure display manager");
        this.mainStage = mainStage;
        mainStage.setTitle(applicationName + " - "
                + BaseConfiguration.getApplicationVersion());
        BaseConfiguration.setTitleAndVersion(Lookup.i18n(
                "application.title")
                + " - " + BaseConfiguration.getApplicationVersion());

        rootAnchorPane = new AnchorPane();

        rootStackPane.getChildren().add(rootAnchorPane);

        spinnerContainer = new Pane();
        spinnerContainer.setMouseTransparent(true);
        spinnerContainer.setPickOnBounds(false);
        spinner = new Spinner();
        spinner.setVisible(false);
        spinnerContainer.getChildren().add(spinner);
        Lookup.setSpinnerControl(this);

        AnchorPane.setBottomAnchor(rootAnchorPane, 0.0);
        AnchorPane.setLeftAnchor(rootAnchorPane, 0.0);
        AnchorPane.setRightAnchor(rootAnchorPane, 0.0);
        AnchorPane.setTopAnchor(rootAnchorPane, 0.0);

        mainHolder = new HBox();
        mainHolder.setPrefSize(-1, -1);

        AnchorPane.setBottomAnchor(mainHolder, 0.0);
        AnchorPane.setLeftAnchor(mainHolder, 0.0);
        AnchorPane.setRightAnchor(mainHolder, 0.0);
        AnchorPane.setTopAnchor(mainHolder, 0.0);

        rootAnchorPane.getChildren().add(mainHolder);
        rootAnchorPane.getChildren().add(spinnerContainer);

        // Load in all of the side panels
        steno.debug("setup panels for mode");
        for (ApplicationMode mode : ApplicationMode.values())
        {
            setupPanelsForMode(mode);
        }

        // Create a place to hang the side panels from
        sidePanelContainer = new StackPane();
        HBox.setHgrow(sidePanelContainer, Priority.NEVER);

        try
        {
            URL fxmlFileName = getClass().getResource(ApplicationConfiguration.fxmlPanelResourcePath + "printerStatusSidePanel.fxml");
            steno.debug("About to load side panel fxml: " + fxmlFileName);
            FXMLLoader sidePanelLoader = new FXMLLoader(fxmlFileName, BaseLookup.getLanguageBundle());
            sidePanel = (VBox) sidePanelLoader.load();
        } catch (Exception ex)
        {
            steno.exception("Couldn't load side panel", ex);
        }
        sidePanelContainer.getChildren().add(sidePanel);

        mainHolder.getChildren().add(sidePanelContainer);

        projectTabPaneHolder.getStyleClass().add("master-details-pane");
        HBox.setHgrow(projectTabPaneHolder, Priority.ALWAYS);

        HBox.setHgrow(rhPanel, Priority.ALWAYS);

        interchangeablePanelAreaWithNotificationArea = new AnchorPane();
        AnchorPane.setBottomAnchor(interchangeablePanelAreaWithNotificationArea, 0.0);
        AnchorPane.setTopAnchor(interchangeablePanelAreaWithNotificationArea, 0.0);
        AnchorPane.setLeftAnchor(interchangeablePanelAreaWithNotificationArea, 0.0);
        AnchorPane.setRightAnchor(interchangeablePanelAreaWithNotificationArea, 0.0);
        rhPanel.getChildren().add(interchangeablePanelAreaWithNotificationArea);

        HBox topMenuStrip = new TopMenuStrip();
        AnchorPane.setTopAnchor(topMenuStrip, 0.0);
        AnchorPane.setLeftAnchor(topMenuStrip, 0.0);
        AnchorPane.setRightAnchor(topMenuStrip, 0.0);
        rhPanel.getChildren().add(topMenuStrip);

        mainHolder.getChildren().add(rhPanel);

        // Configure the main display tab pane - just the printer status page to start with
        tabDisplay = new TabPane();
        tabDisplay.setPickOnBounds(false);
        tabDisplay.setOnKeyPressed(this);
        tabDisplay.setTabMinHeight(56);
        tabDisplay.setTabMaxHeight(56);
        tabDisplaySelectionModel = tabDisplay.getSelectionModel();
        tabDisplay.getStyleClass().add("main-project-tabPane");
        configureProjectDragNDrop(tabDisplay);

        VBox.setVgrow(tabDisplay, Priority.ALWAYS);
        AnchorPane.setBottomAnchor(tabDisplay, 0.0);
        AnchorPane.setTopAnchor(tabDisplay, 0.0);
        AnchorPane.setLeftAnchor(tabDisplay, 0.0);
        AnchorPane.setRightAnchor(tabDisplay, 0.0);

        // The printer status tab will always be visible - the page is static
        try
        {
            FXMLLoader printerStatusPageLoader = new FXMLLoader(getClass().getResource(
                    ApplicationConfiguration.fxmlResourcePath
                    + "PrinterStatusPage.fxml"), BaseLookup.getLanguageBundle());
            AnchorPane printerStatusPage = printerStatusPageLoader.load();
            PrinterStatusPageController printerStatusPageController = printerStatusPageLoader.
                    getController();
            printerStatusPageController.
                    configure(projectTabPaneHolder);

            printerStatusTab = new Tab();
            FXMLLoader printerStatusPageLabelLoader = new FXMLLoader(getClass().getResource(
                    ApplicationConfiguration.fxmlResourcePath
                    + "infoScreenIndicator.fxml"), BaseLookup.getLanguageBundle());
            VBox printerStatusLabelGroup = printerStatusPageLabelLoader.load();
            infoScreenIndicatorController = printerStatusPageLabelLoader.getController();
            printerStatusTab.setGraphic(printerStatusLabelGroup);
            printerStatusTab.setClosable(false);
            printerStatusTab.setContent(printerStatusPage);
            tabDisplay.getTabs().add(printerStatusTab);

            tabDisplaySelectionModel.selectedItemProperty().addListener(
                    (ObservableValue<? extends Tab> ov, Tab lastTab, Tab newTab) ->
                    {
                        if (newTab == addPageTab)
                        {
                            createAndAddNewProjectTab();

                            if (applicationStatus.getMode() != ApplicationMode.LAYOUT)
                            {
                                applicationStatus.setMode(ApplicationMode.LAYOUT);
                            }
                        } else if (newTab instanceof ProjectTab)
                        {
                            if (applicationStatus.getMode() != ApplicationMode.LAYOUT)
                            {
                                applicationStatus.setMode(ApplicationMode.LAYOUT);
                            }

                            if (lastTab != newTab)
                            {
                                ProjectTab projectTab = (ProjectTab) tabDisplaySelectionModel.getSelectedItem();
                                projectTab.fireProjectSelected();
                            }
                        } else
                        {
                            //Going to status
                            if (lastTab instanceof ProjectTab)
                            {
                                lastLayoutTab = lastTab;
                            }

                            if (applicationStatus.getMode() != ApplicationMode.STATUS)
                            {
                                applicationStatus.setMode(ApplicationMode.STATUS);
                            }
                        }
                        
                        if (lastTab instanceof ProjectTab)
                        {
                            ProjectTab lastProjectTab = (ProjectTab) lastTab;
                            lastProjectTab.fireProjectDeselected();
                        }
                    });

            AnchorPane.setBottomAnchor(notificationArea, 90.0);
            AnchorPane.setLeftAnchor(notificationArea, 0.0);
            AnchorPane.setRightAnchor(notificationArea, 0.0);
            interchangeablePanelAreaWithNotificationArea.getChildren().add(notificationArea);
            projectTabPaneHolder.getChildren().add(tabDisplay);
        } catch (IOException ex)
        {
            steno.exception("Failed to load printer status page", ex);
        }

        applicationStatus.modeProperty().addListener(
                (ObservableValue<? extends ApplicationMode> ov, ApplicationMode oldMode, ApplicationMode newMode) ->
                {
                    switchPagesForMode(oldMode, newMode);
                });

        applicationStatus.setMode(ApplicationMode.STATUS);

        try
        {
            URL menuStripURL = getClass().getResource(ApplicationConfiguration.fxmlPanelResourcePath
                    + "LayoutStatusMenuStrip.fxml");
            FXMLLoader menuStripLoader = new FXMLLoader(menuStripURL, BaseLookup.getLanguageBundle());
            VBox menuStripControls = (VBox) menuStripLoader.load();
            menuStripControls.prefWidthProperty().bind(projectTabPaneHolder.widthProperty());
            projectTabPaneHolder.getChildren().add(menuStripControls);
        } catch (IOException ex)
        {
            steno.exception("Failed to load menu strip controls", ex);
        }

        modelLoadDialog = new ProgressDialog(ModelLoader.modelLoaderService);

        scene = new Scene(rootStackPane, ApplicationConfiguration.DEFAULT_WIDTH,
                ApplicationConfiguration.DEFAULT_HEIGHT);

        scene.getStylesheets().add(ApplicationConfiguration.getMainCSSFile());

        scene.widthProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(
                    ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
            {
                whenWindowChangesSize();
            }
        });

        scene.heightProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(
                    ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
            {
                whenWindowChangesSize();
            }
        });

        projectTabPaneHolder.widthProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(
                    ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
            {
                whenWindowChangesSize();
            }
        });

        mainStage.maximizedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(
                    ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                whenWindowChangesSize();
            }
        });

        mainStage.fullScreenProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(
                    ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                steno.debug("Stage fullscreen = " + newValue.booleanValue());
                whenWindowChangesSize();
            }
        });

        HiddenKey hiddenKeyThing = new HiddenKey();
        hiddenKeyThing.addCommandSequence(addDummyPrinterCommand);
        hiddenKeyThing.addCommandWithParameterSequence(dummyCommandPrefix);
        hiddenKeyThing.addKeyCommandListener(this);
        hiddenKeyThing.addUnhandledKeyListener(this);
        hiddenKeyThing.captureHiddenKeys(scene);

        // Camera required to allow 2D shapes to be rotated in 3D in the '2D' UI
        PerspectiveCamera controlOverlaycamera = new PerspectiveCamera(false);

        scene.setCamera(controlOverlaycamera);

        mainStage.setScene(scene);

        addPageTab = new Tab();
        addPageTab.setText("+");
        addPageTab.setClosable(false);
        tabDisplay.getTabs().add(addPageTab);
        
        // Create ContextMenu for addPageTab.
        ContextMenu contextMenu = new ContextMenu();
        MenuItem item1 = new MenuItem("Open Project ...");
        item1.setOnAction((ActionEvent event) -> {
            openProject();
        });
        // Add MenuItem to ContextMenu
        contextMenu.getItems().add(item1);
        addPageTab.contextMenuProperty().set(contextMenu);

        steno.debug("load projects");
        loadProjectsAtStartup();
        loadModelsIntoNewProject(modelsToLoadAtStartup_projectName,
                modelsToLoadAtStartup,
                dontGroupStartupModels);

        rootAnchorPane.layout();

        steno.debug("end configure display manager");
    }
    
    private void openProject()
    {
        FileChooser projectChooser = new FileChooser();
        projectChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Robox Project", "*.robox"));
        projectChooser.setInitialDirectory(new File(ApplicationConfiguration.getProjectDirectory()));
        List<File> files =  projectChooser.showOpenMultipleDialog(getMainStage());
        if (files != null && !files.isEmpty()) {
            files.forEach(projectFile ->
                {
                    loadProject(projectFile);
                });
        }
    }

    private void setupPanelsForMode(ApplicationMode mode)
    {
        try
        {
            URL fxmlFileName = getClass().getResource(mode.getInsetPanelFXMLName());
            if (fxmlFileName != null)
            {
                steno.debug("About to load inset panel fxml: " + fxmlFileName);
                FXMLLoader insetPanelLoader = new FXMLLoader(fxmlFileName,
                        BaseLookup.getLanguageBundle());
                insetPanelLoader.setController(mode.getControllerClass().newInstance());
                Pane insetPanel = (Pane) insetPanelLoader.load();
                Initializable insetPanelController = insetPanelLoader.getController();
                insetPanel.setId(mode.name());
                insetPanels.put(mode, insetPanel);
                insetPanelControllers.put(mode, insetPanelController);
            }
        } catch (Exception ex)
        {
            insetPanels.put(mode, null);
            insetPanelControllers.put(mode, null);
            steno.exception("Couldn't load inset panel for mode:" + mode, ex);
        }
    }

    private ProjectTab createAndAddNewProjectTab()
    {
        ProjectTab projectTab = new ProjectTab(tabDisplay.widthProperty(),
                tabDisplay.heightProperty());
        tabDisplay.getTabs().add(tabDisplay.getTabs().size() - 1, projectTab);
        tabDisplaySelectionModel.select(projectTab);
        Lookup.setSelectedProject(null);
        return projectTab;
    }

    public Rectangle2D getNormalisedPreviewRectangle()
    {
        Rectangle2D nRectangle = null;
        Tab currentTab = tabDisplaySelectionModel.getSelectedItem();

        if (currentTab instanceof ProjectTab)
        {
            ProjectTab currentProjectTab = (ProjectTab)currentTab;
            Rectangle2D previewBounds =  currentProjectTab.getPreviewRectangle();
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            nRectangle = new Rectangle2D(previewBounds.getMinX() / primaryScreenBounds.getWidth(), 
                                         previewBounds.getMinY() / primaryScreenBounds.getHeight(),
                                         previewBounds.getWidth() / primaryScreenBounds.getWidth(),
                                         previewBounds.getHeight() / primaryScreenBounds.getHeight());
        }
        return nRectangle;
    }
        
    public static Stage getMainStage()
    {
        return mainStage;
    }

    public ProjectTab getTabForProject(Project project)
    {
        ProjectTab pTab = null;
        if (tabDisplay != null) {
            pTab = tabDisplay.getTabs()
                             .filtered((t) -> ((t instanceof ProjectTab) && ((ProjectTab)t).getProject() == project))
                             .stream().map((t) -> ((ProjectTab)t)).findAny().orElse(null);
        }
        return pTab;
    }
    
    public void shutdown()
    {
        // This is here solely so it shutdown can be called on it when the application closes.
        if (previewManager != null)
        {
            previewManager.shutdown();
        }
        
        if (projectManager != null)
        {
            projectManager.saveState();
        }

        if (tabDisplay != null)
        {
            tabDisplay.getTabs().stream().filter((tab) -> (tab instanceof ProjectTab)).forEach(
                    (tab) ->
                    {
                        ((ProjectTab) tab).saveAndCloseProject();
                    });
        }
    }

    /**
     * Key handler for whole application Delete - deletes selected model
     *
     * @param event
     */
    @Override
    public void handle(KeyEvent event)
    {
        if (applicationStatus.getMode() == ApplicationMode.LAYOUT)
        {
            Tab currentTab = tabDisplaySelectionModel.getSelectedItem();
            if (currentTab instanceof ProjectTab)
            {
                Project project = Lookup.getSelectedProjectProperty().get();
                UndoableProject undoableProject = new UndoableProject(project);
                switch (event.getCode())
                {
                    case DELETE:
                    case BACK_SPACE:
                        if (Lookup.getProjectGUIState(project) != null)
                        {
                            if (Lookup.getProjectGUIState(project).getProjectGUIRules().canRemoveOrDuplicateSelection().get())
                            {
                                deleteSelectedModels(project, undoableProject);
                            }
                        }
                        event.consume();
                        break;
                    case A:
                        if (event.isShortcutDown())
                        {
                            selectAllModels(project);
                            event.consume();
                        }
                        break;
                    case Z:
                        if (event.isShortcutDown() && (!event.isShiftDown()))
                        {
                            undoCommand(project);
                            event.consume();
                        } else if (event.isShortcutDown() && event.isShiftDown())
                        {
                            redoCommand(project);
                            event.consume();
                        }
                        break;
                    case Y:
                        if (event.isShortcutDown())
                        {
                            redoCommand(project);
                            event.consume();
                        }
                        break;
                    default:
                        break;
                }
            }
        } else if (applicationStatus.getMode() == ApplicationMode.STATUS
                && Lookup.getUserPreferences().isAdvancedMode())
        {
            switch (event.getCode())
            {
                case G:
                    Lookup.getUserPreferences().setShowGCode(true);
                    break;
            }
        }
    }

    private void selectAllModels(Project project)
    {
        ProjectSelection projectSelection
                = Lookup.getProjectGUIState(project).getProjectSelection();
        for (ProjectifiableThing modelContainer : project.getTopLevelThings())
        {
            projectSelection.addSelectedItem(modelContainer);
        }
    }

    private void deleteSelectedModels(Project project, UndoableProject undoableProject)
    {
        Set<ProjectifiableThing> selectedModels
                = Lookup.getProjectGUIState(project).getProjectSelection().
                getSelectedModelsSnapshot();
        undoableProject.deleteModels(selectedModels);
    }

    private void undoCommand(Project project)
    {
        CommandStack commandStack = Lookup.getProjectGUIState(project).getCommandStack();
        if (commandStack.getCanUndo().get())
        {
            try
            {
                commandStack.undo();
            } catch (CommandStack.UndoException ex)
            {
                steno.debug("cannot undo " + ex);
            }
        }
    }

    private void redoCommand(Project project)
    {
        CommandStack commandStack = Lookup.getProjectGUIState(project).getCommandStack();
        if (commandStack.getCanRedo().get())
        {
            try
            {
                commandStack.redo();
            } catch (CommandStack.UndoException ex)
            {
                steno.debug("cannot undo " + ex);
            }
        }
    }

    public PurgeInsetPanelController getPurgeInsetPanelController()
    {
        return (PurgeInsetPanelController) insetPanelControllers.get(ApplicationMode.PURGE);
    }

    /**
     * This is fired when the main window or one of the internal windows may
     * have changed size.
     */
    private void whenWindowChangesSize()
    {
        nodesMayHaveMoved.set(!nodesMayHaveMoved.get());

//        steno.info("Window size change: " + scene.getWidth() + " : " + scene.getHeight());
        if (scene.getHeight() < VERY_SHORT_SCALE_BELOW_HEIGHT)
        {
            if (displayScalingModeProperty.get() != DisplayScalingMode.VERY_SHORT)
            {
                displayScalingModeProperty.set(DisplayScalingMode.VERY_SHORT);
            }
        } else if (scene.getHeight() < SHORT_SCALE_BELOW_HEIGHT)
        {
            if (displayScalingModeProperty.get() != DisplayScalingMode.SHORT)
            {
                displayScalingModeProperty.set(DisplayScalingMode.SHORT);
            }
        } else
        {
            if (displayScalingModeProperty.get() != DisplayScalingMode.NORMAL)
            {
                displayScalingModeProperty.set(DisplayScalingMode.NORMAL);
            }
        }

        double scaleFactor = 1.0;
        if (scene.getHeight() < START_SCALING_WINDOW_HEIGHT)
        {
            scaleFactor = scene.getHeight() / START_SCALING_WINDOW_HEIGHT;
            if (scaleFactor < MINIMUM_SCALE_FACTOR)
            {
                scaleFactor = MINIMUM_SCALE_FACTOR;
            }
        }

        rootAnchorPane.setScaleX(scaleFactor);
        rootAnchorPane.setScaleY(scaleFactor);
        rootAnchorPane.setScaleZ(scaleFactor);

        rootAnchorPane.setPrefWidth(scene.getWidth() / scaleFactor);
        rootAnchorPane.setMinWidth(scene.getWidth() / scaleFactor);
        rootAnchorPane.setPrefHeight(scene.getHeight() / scaleFactor);
        rootAnchorPane.setMinHeight(scene.getHeight() / scaleFactor);
    }

    public ReadOnlyBooleanProperty nodesMayHaveMovedProperty()
    {
        return nodesMayHaveMoved;
    }

    @Override
    public boolean trigger(String commandSequence, String capturedParameter)
    {
        boolean handled = false;

        switch (commandSequence)        {
            case addDummyPrinterCommand:
                RoboxCommsManager.getInstance().addDummyPrinter(false);
                handled = true;
                break;
            case dummyCommandPrefix:
                Printer currentPrinter = Lookup.getSelectedPrinterProperty().get();
                PrinterIdentity pid = currentPrinter.getPrinterIdentity();
                if (pid.printeryearOfManufactureProperty().get().equals(
                        DummyPrinterCommandInterface.dummyYear)) {
                    currentPrinter.sendRawGCode(
                            capturedParameter.replaceAll("/", " ").trim().toUpperCase(), true);
                    handled = true;
                }
                break;
        }

        return handled;
    }

    @Override
    public void unhandledKeyEvent(KeyEvent keyEvent)
    {
        //Try sending the keyEvent to the in-focus project
        handle(keyEvent);
    }

    private void loadProject(File projectFile) {
        try {
            Project p = projectManager.getProjectIfOpen(FilenameUtils.getBaseName(projectFile.getName()))
                .orElseGet(() ->
                {
                    Project newProject = ProjectManager.loadProject(projectFile.getAbsolutePath());
                    if (newProject != null)
                    {
                        ProjectTab newProjectTab = new ProjectTab(newProject,
                                                                  tabDisplay.widthProperty(),
                                                                  tabDisplay.heightProperty(),
                                                                  false
                        );

                        tabDisplay.getTabs().add(tabDisplay.getTabs().size() - 1, newProjectTab);
                    }
                    return newProject;
                });
            if (p != null)
            {
                ProjectTab pt = getTabForProject(p);
                tabDisplaySelectionModel.select(pt);
                if (applicationStatus.getMode() != ApplicationMode.LAYOUT)
                {
                    applicationStatus.setMode(ApplicationMode.LAYOUT);
                }
            }
        }
        catch (Exception ex) {
            steno.exception("Failed to open project", ex);
        }
    }
    
    private void configureProjectDragNDrop(Node basePane)
    {
        basePane.setOnDragOver((DragEvent event) ->
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

                        if (file.getName().toUpperCase().endsWith(
                                ApplicationConfiguration.projectFileExtension
                                .toUpperCase()))
                        {
                            extensionFound = true;
                            break;
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
                    }
                }
            }

            event.consume();
        });

        basePane.setOnDragEntered((DragEvent event) ->
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
                            if (file.getName().toUpperCase().endsWith(
                                    ApplicationConfiguration.projectFileExtension
                                    .toUpperCase()))
                            {
                                extensionFound = true;
                                break;
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
                        }
                    }
                }
            }
            event.consume();
        });

        basePane.setOnDragExited((DragEvent event) ->
        {
            /* mouse moved away, remove the graphical cues */
            basePane.setEffect(null);

            event.consume();
        });

        basePane.setOnDragDropped((DragEvent event) ->
        {
            /* data dropped */
            steno.debug("onDragDropped");
            /* if there is a string data on dragboard, read it and use it */
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles())
            {
                db.getFiles().forEach(file ->
                {
                    loadProject(file);
                });

            } else
            {
                steno.error("No files in dragboard");
            }
            /* let the source know whether the string was successfully
             * transferred and used */
            event.setDropCompleted(success);

            event.consume();
        });
    }

    public ReadOnlyObjectProperty<DisplayScalingMode> getDisplayScalingModeProperty()
    {
        return displayScalingModeProperty;
    }

    public void loadModelsIntoNewProject(String projectName, List<String> modelsWithPaths, boolean dontGroupModels)
    {
        List<File> listOfFiles = new ArrayList<>();

        if (modelsWithPaths != null
                && modelsWithPaths.size() > 0)
        {
            modelsWithPaths.forEach(modelRef ->
            {
                File fileRef = new File(modelRef);

                if (fileRef.exists())
                {
                    listOfFiles.add(fileRef);
                }
            });
        }

        Runnable loaderRunnable = () ->
        {
            Project newProject = new ModelContainerProject();
            newProject.setProjectName(projectName);

            ModelLoader loader = new ModelLoader();
            loader.loadExternalModels(newProject, listOfFiles, false, null, false);
            ProjectTab projectTab = new ProjectTab(newProject, tabDisplay.widthProperty(),
                    tabDisplay.heightProperty(), false);
            tabDisplay.getTabs().add(tabDisplay.getTabs().size() - 1, projectTab);
            tabDisplay.getSelectionModel().select(projectTab);
        };

        if (Lookup.getUserPreferences().isFirstUse())
        {
            File firstUsePrintFile = new File(BaseConfiguration.
                    getApplicationModelDirectory().concat("RBX_ROBOT_MM.stl"));

            Project newProject = new ModelContainerProject();
            newProject.setProjectName(Lookup.i18n("myFirstPrintTitle"));

            List<File> fileToLoad = new ArrayList<>();
            fileToLoad.add(firstUsePrintFile);
            ModelLoader loader = new ModelLoader();

            if (listOfFiles.size() > 0)
            {
                ChangeListener<Boolean> firstUseModelLoadListener = new ChangeListener<Boolean>()
                {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> ov, Boolean wasRunning, Boolean isRunning)
                    {
                        if (wasRunning && !isRunning)
                        {
                            BaseLookup.getTaskExecutor().runOnGUIThread(loaderRunnable);
                            loader.modelLoadingProperty().removeListener(this);
                        }
                    }
                };

                loader.modelLoadingProperty().addListener(firstUseModelLoadListener);
            }
            loader.loadExternalModels(newProject, fileToLoad, false, null, false);

            ProjectTab projectTab = new ProjectTab(newProject, tabDisplay.widthProperty(),
                    tabDisplay.heightProperty(), false);
            tabDisplay.getTabs().add(1, projectTab);

            Lookup.getUserPreferences().setFirstUse(false);
        } else if (listOfFiles.size() > 0)
        {
            BaseLookup.getTaskExecutor().runOnGUIThread(loaderRunnable);
        }
    }

    @Override
    public void modelAddedToProject(Project project)
    {
        if (tabDisplay.getSelectionModel().getSelectedItem() instanceof ProjectTab)
        {
            ((ProjectTab) tabDisplay.getSelectionModel().getSelectedItem()).modelAddedToProject(project);
        }

        if (Lookup.getSelectedProjectProperty().get() == null
                || Lookup.getSelectedProjectProperty().get() != project)
        {
            Lookup.setSelectedProject(project);
        }
    }

    public void initialiseBlank3DProject()
    {
        ((ProjectTab) tabDisplay.getSelectionModel().getSelectedItem()).initialiseBlank3DProject();
    }

    public void initialiseBlank2DProject()
    {
        ((ProjectTab) tabDisplay.getSelectionModel().getSelectedItem()).initialiseBlank2DProject();
    }
    
    public BooleanProperty libraryModeEnteredProperty()
    {
        return libraryModeEntered;
    }
}
