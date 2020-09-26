package celtech.coreUI.controllers.panels;

import celtech.Lookup;
import celtech.appManager.ApplicationStatus;
import celtech.coreUI.components.VerticalMenu;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

public abstract class MenuPanelController implements Initializable
{

    protected class InnerPanelDetails
    {

        Node node;
        MenuInnerPanel innerPanel;

        InnerPanelDetails(Node node, MenuInnerPanel innerPanel)
        {
            this.node = node;
            this.innerPanel = innerPanel;
        }
    }

    List<InnerPanelDetails> innerPanelDetails = new ArrayList<>();

    private final Stenographer steno = StenographerFactory.getStenographer(MenuPanelController.class.getName());

    private ResourceBundle resources;

    @FXML
    protected VerticalMenu panelMenu;

    @FXML
    private VBox insetNodeContainer;

    @FXML
    private HBox buttonBoxContainer;

    InnerPanelDetails profileDetails;
    ProfileLibraryPanelController profileDetailsController;

    protected String paneli18Name = "";

    private final ObjectProperty<MenuInnerPanel> innerPanelProperty = new SimpleObjectProperty<>(null);

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

        this.resources = resources;

        ButtonBox buttonBox = new ButtonBox(innerPanelProperty);
        buttonBoxContainer.getChildren().add(buttonBox);

        setupInnerPanels();

        buildExtras();

//        DisplayManager.getInstance().getDisplayScalingModeProperty().addListener(new ChangeListener<DisplayManager.DisplayScalingMode>()
//        {
//
//            @Override
//            public void changed(ObservableValue<? extends DisplayManager.DisplayScalingMode> ov, DisplayManager.DisplayScalingMode t, DisplayManager.DisplayScalingMode t1)
//            {
//                switch (t1)
//                {
//                    case SHORT:
//                    case VERY_SHORT:
//                        Insets shortInsets = new Insets(20, 0, 0, 0);
//                        insetNodeContainer.setPadding(shortInsets);
//                        break;
//                    default:
//                        Insets normalInsets = new Insets(95, 0, 0, 0);
//                        insetNodeContainer.setPadding(normalInsets);
//                        break;
//                }
//            }
//        });
    }

    /**
     * Define the inner panels to be offered in the main menu. For the future
     * this is configuration information that could be e.g. stored in XML or in
     * a plugin.
     */
    protected abstract void setupInnerPanels();

    /**
     * Load the given inner panel.
     *
     * @param fxmlLocation
     * @param extrasMenuInnerPanel
     * @return
     */
    protected InnerPanelDetails loadInnerPanel(String fxmlLocation, MenuInnerPanel extrasMenuInnerPanel)
    {
        URL fxmlURL = getClass().getResource(fxmlLocation);
        FXMLLoader loader = new FXMLLoader(fxmlURL, resources);
        loader.setController(extrasMenuInnerPanel);
        try
        {
            Node node = loader.load();
            InnerPanelDetails innerPanelDetails = new InnerPanelDetails(node, extrasMenuInnerPanel);
            this.innerPanelDetails.add(innerPanelDetails);
            return innerPanelDetails;
        } catch (IOException ex)
        {
            steno.exception("Unable to load panel: " + fxmlLocation, ex);
        }
        return null;
    }

    /**
     * For each InnerPanel, create a menu item that will open it.
     */
    private void buildExtras()
    {
        panelMenu.setTitle(Lookup.i18n(paneli18Name));

        for (InnerPanelDetails innerPanelDetails : innerPanelDetails)
        {
            panelMenu.addItem(Lookup.i18n(innerPanelDetails.innerPanel.getMenuTitle()), () ->
            {
                openInnerPanel(innerPanelDetails);
            }, null);
        }
    }

    /**
     * Open the given inner panel.
     */
    private void openInnerPanel(InnerPanelDetails innerPanelDetails)
    {
        insetNodeContainer.getChildren().clear();
        insetNodeContainer.getChildren().add(innerPanelDetails.node);
        innerPanelProperty.set(innerPanelDetails.innerPanel);
        innerPanelDetails.innerPanel.panelSelected();
    }

    @FXML
    private void okPressed(ActionEvent event)
    {
        ApplicationStatus.getInstance().returnToLastMode();
    }
}