package celtech.coreUI.components.buttons;

import java.io.IOException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ToggleButton;

/**
 *
 * @author Ian
 */
public class GraphicToggleButton extends ToggleButton
{

    private final StringProperty fxmlFileName = new SimpleStringProperty("");

    public GraphicToggleButton()
    {
        loadFXML();

        this.getStyleClass().add("graphic-button");
    }

    public String getFxmlFileName()
    {
        return fxmlFileName.get();
    }

    public void setFxmlFileName(String fxmlFileName)
    {
        this.fxmlFileName.set(fxmlFileName);

        loadFXML();
    }

    public StringProperty getFxmlFileNameProperty()
    {
        return fxmlFileName;
    }

    private void loadFXML() throws RuntimeException
    {
        if (fxmlFileName.get().equalsIgnoreCase("") == false)
        {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/celtech/resources/fxml/buttons/" + fxmlFileName.get() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);

            fxmlLoader.setClassLoader(this.getClass().getClassLoader());

            try
            {
                fxmlLoader.load();
            } catch (IOException exception)
            {
                throw new RuntimeException(exception);
            }
        }
    }
}
