package celtech.coreUI.components;

import java.io.IOException;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 *
 * @author Ian
 */
public class ChoiceLinkButton extends Button
{

    @FXML
    VBox labelGroup;

    @FXML
    HyperlinkedLabel title;

    @FXML
    HyperlinkedLabel message;

    public ChoiceLinkButton()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
            "/celtech/resources/fxml/components/ChoiceLinkButton.fxml"));
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

        this.getStyleClass().add("error-dialog-choice-button");
    }

    public void setTitle(String i18nTitle)
    {
        title.replaceText(i18nTitle);
    }

    public String getTitle()
    {
        return title.getText();
    }

    public StringProperty titleProperty()
    {
        return title.textProperty();
    }

    public void setMessage(String i18nMessage)
    {
        message.replaceText(i18nMessage);
    }

    public String getMessage()
    {
        return message.getText();
    }

    public StringProperty messageProperty()
    {
        return message.textProperty();
    }
}
