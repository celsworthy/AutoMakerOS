/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.coreUI.components;

import celtech.configuration.ApplicationConfiguration;
import celtech.coreUI.DisplayManager;
import celtech.coreUI.controllers.ModalDialogController;
import java.io.IOException;
import java.net.URL;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author ianhudson
 */
public class ModalDialog
{

    private Stenographer steno = StenographerFactory.getStenographer(ModalDialog.class.getName());
    private Stage dialogStage = null;
    private ModalDialogController dialogController = null;

    /**
     *
     * @param windowTitle
     */
    public ModalDialog(String windowTitle)
    {
        initialise(windowTitle);
    }

    /**
     *
     */
    public ModalDialog()
    {
        initialise(null);
    }

    private void initialise(String windowTitle)
    {
        if (windowTitle != null)
        {
            dialogStage = new Stage(StageStyle.UTILITY);
            dialogStage.setTitle(windowTitle);
        } else
        {
            dialogStage = new Stage(StageStyle.TRANSPARENT);
        }

        dialogStage.setResizable(false);

        URL dialogFXMLURL = ModalDialog.class.getResource(ApplicationConfiguration.fxmlResourcePath + "ModalDialog.fxml");
        FXMLLoader dialogLoader = new FXMLLoader(dialogFXMLURL);
        try
        {
            Parent dialogBoxScreen = (Parent) dialogLoader.load();
            dialogController = (ModalDialogController) dialogLoader.getController();

            Scene dialogScene = new Scene(dialogBoxScreen, Color.TRANSPARENT);
            dialogScene.getStylesheets().add(ApplicationConfiguration.getMainCSSFile());
            dialogStage.setScene(dialogScene);
            dialogStage.initOwner(DisplayManager.getMainStage());
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogController.configure(dialogStage);
        } catch (IOException ex)
        {
            steno.error("Couldn't load dialog box FXML");
        }
    }

    /**
     *
     * @param title
     */
    public void setTitle(String title)
    {
        dialogController.setDialogTitle(title);
    }

    /**
     *
     * @param message
     */
    public void setMessage(String message)
    {
        dialogController.setDialogMessage(message);
    }

    /**
     *
     * @param text
     * @return
     */
    public int addButton(String text)
    {
        return dialogController.addButton(text);
    }

    /**
     *
     * @param text
     * @param disabler
     * @return
     */
    public int addButton(String text, ReadOnlyBooleanProperty disabler)
    {
        return dialogController.addButton(text, disabler);
    }

    /**
     *
     * @return
     */
    public int show()
    {
        dialogStage.showAndWait();

        return dialogController.getButtonValue();
    }

    /**
     *
     */
    public void close()
    {
        dialogStage.hide();
    }

    /**
     *
     * @return
     */
    public boolean isShowing()
    {
        return dialogStage.isShowing();
    }

    /**
     *
     * @param content
     */
    public void setContent(Node content)
    {
        dialogController.setContent(content);
    }
}
