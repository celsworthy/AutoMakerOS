package celtech.coreUI.components;

import celtech.Lookup;
import celtech.configuration.ApplicationConfiguration;
import celtech.coreUI.DisplayManager;
import celtech.coreUI.controllers.PrinterIDDialogController;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.printerControl.model.Printer;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
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
public class PrinterIDDialog
{

    private Stenographer steno = StenographerFactory.getStenographer(PrinterIDDialog.class.getName());
    private Stage dialogStage = null;
    private PrinterIDDialogController dialogController = null;

    /**
     *
     */
    public PrinterIDDialog()
    {
        dialogStage = new Stage(StageStyle.TRANSPARENT);
        URL dialogFXMLURL = PrinterIDDialog.class.getResource(ApplicationConfiguration.fxmlResourcePath + "PrinterIDDialog.fxml");
        FXMLLoader dialogLoader = new FXMLLoader(dialogFXMLURL, BaseLookup.getLanguageBundle());
        try
        {
            Parent dialogBoxScreen = (Parent) dialogLoader.load();
            dialogController = (PrinterIDDialogController) dialogLoader.getController();

            Scene dialogScene = new Scene(dialogBoxScreen, Color.TRANSPARENT);
            dialogScene.getStylesheets().add(ApplicationConfiguration.getMainCSSFile());
            dialogStage.setScene(dialogScene);
            dialogStage.initOwner(DisplayManager.getMainStage());
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogController.configure(dialogStage);
        } catch (IOException ex)
        {
            steno.error("Couldn't load printer ID dialog box FXML");
        }
    }

    /**
     *
     * @return
     */
    public boolean show()
    {
        dialogStage.showAndWait();
        return dialogController.okPressed();
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
     * @return
     */
    public String getChosenPrinterName()
    {
        return dialogController.getChosenPrinterName();
    }

    /**
     *
     * @param printerToUse
     */
    public void setPrinterToUse(Printer printerToUse)
    {
        dialogController.setPrinterToUse(printerToUse);
    }

    /**
     *
     * @param colour
     */
    public void setChosenDisplayColour(Color colour)
    {
        dialogController.setChosenColour(colour);
    }

    /**
     *
     * @return
     */
    public Color getChosenDisplayColour()
    {
        return dialogController.getChosenDisplayColour();
    }

    /**
     *
     * @param printerName
     */
    public void setChosenPrinterName(String printerName)
    {
        dialogController.setChosenPrinterName(printerName);
    }
}
