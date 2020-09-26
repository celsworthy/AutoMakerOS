package celtech.coreUI.components;

import celtech.appManager.ApplicationMode;
import celtech.appManager.ApplicationStatus;
import celtech.coreUI.components.buttons.GraphicButton;
import java.io.IOException;
import java.net.URL;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

/**
 *
 * @author Ian
 */
public class TopMenuStrip extends HBox
{

    private ApplicationStatus applicationStatus = null;

    @FXML
    private GraphicButton aboutButton;

    @FXML
    private GraphicButton extrasMenuButton;

    @FXML
    private GraphicButton libraryButton;

    @FXML
    void extrasMenuPressed(ActionEvent event)
    {
        applicationStatus.setMode(ApplicationMode.EXTRAS_MENU);
    }

    @FXML
    void aboutPressed(ActionEvent event)
    {
        applicationStatus.setMode(ApplicationMode.ABOUT);
    }

    @FXML
    void libraryPressed(ActionEvent event)
    {
        applicationStatus.setMode(ApplicationMode.LIBRARY);
    }

    public TopMenuStrip()
    {
        super();
        URL fxml = getClass().getResource("/celtech/resources/fxml/components/TopMenuStrip.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxml);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.setClassLoader(getClass().getClassLoader());

        try
        {
            fxmlLoader.load();
        } catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    void initialize()
    {
        applicationStatus = ApplicationStatus.getInstance();

        BooleanBinding buttonDisabled
                = applicationStatus.modeProperty().isEqualTo(ApplicationMode.PURGE).
                or(applicationStatus.modeProperty().isEqualTo(ApplicationMode.CALIBRATION_CHOICE));

        aboutButton.disableProperty().bind(buttonDisabled);

        extrasMenuButton.disableProperty().bind(buttonDisabled);

        libraryButton.disableProperty().bind(buttonDisabled);
    }
}
