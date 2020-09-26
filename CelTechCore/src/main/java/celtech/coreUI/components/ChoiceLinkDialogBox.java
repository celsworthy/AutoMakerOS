package celtech.coreUI.components;

import celtech.configuration.ApplicationConfiguration;
import celtech.coreUI.DisplayManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Ian
 */
public class ChoiceLinkDialogBox extends StackPane
{
    
    public class PrinterDisconnectedException extends Exception {

        public PrinterDisconnectedException(String message)
        {
            super(message);
        }
        
    }

    @FXML
    private VBox rootVBox;
    
    @FXML
    private HyperlinkedLabel title;

    @FXML
    private HyperlinkedLabel message;

    @FXML
    private VBox buttonContainer;

    private Stage dialogStage = null;

    private Optional<ChoiceLinkButton> chosenButton = Optional.empty();
    
    private final boolean closeOnPrinterDisconnect;
    
    private boolean closeOnPrinterConnect = false;
    
    private boolean closedDueToPrinterDisconnect = false;
    
    private static List<ChoiceLinkDialogBox> openDialogs = new ArrayList<>();
    
    /**
     * When the printer is disconnected close all related dialog boxes.
     */
    public static void whenPrinterDisconnected() {
        for (ChoiceLinkDialogBox openDialog : new ArrayList<>(openDialogs))
        {
            if (openDialog.closeOnPrinterDisconnect) {
                openDialog.closeDueToPrinterDisconnect();
            }
        }
    } 
    
    public static void whenPrinterConnected() {
        openDialogs.stream()
                .filter(openDialog -> openDialog.closeOnPrinterConnect)
                .forEach(openDialog -> openDialog.close());
    }
    
    public void closeDueToPrinterDisconnect() {
        closedDueToPrinterDisconnect = true;
        close();
    }
    
    public boolean closedDueToPrinterDisconnect() {
        return closedDueToPrinterDisconnect;
    }

    public ChoiceLinkDialogBox()
    {
        this.closeOnPrinterDisconnect = true;
    }
    
    public ChoiceLinkDialogBox(boolean closeOnPrinterDisconnect)
    {
        
        this.closeOnPrinterDisconnect = closeOnPrinterDisconnect;
        
        openDialogs.add(this);
        
        dialogStage = new Stage(StageStyle.TRANSPARENT);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
            "/celtech/resources/fxml/components/ChoiceLinkDialogBox.fxml"));
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

        Scene dialogScene = new Scene(this, Color.TRANSPARENT);
        dialogScene.getStylesheets().add(ApplicationConfiguration.getMainCSSFile());
        dialogStage.setScene(dialogScene);
        dialogStage.initOwner(DisplayManager.getMainStage());
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        //getStyleClass().add("error-dialog");
    }
    
    public ChoiceLinkDialogBox(boolean closeOnPrinterDisconnect, boolean closeOnPrinterConnect)
    {
        this(closeOnPrinterDisconnect);
        this.closeOnPrinterConnect = closeOnPrinterConnect;
    }

    public void setTitle(final String i18nTitle)
    {
        title.replaceText(i18nTitle);
    }

    public void setMessage(String i18nMessage)
    {
        message.replaceText(i18nMessage);
    }

    public ChoiceLinkButton addChoiceLink(String i18Title, String i18nMessage)
    {
        ChoiceLinkButton button = new ChoiceLinkButton();
        button.setTitle(i18Title);
        button.setMessage(i18nMessage);
        configureButtonListener(button);

        return button;
    }

    public ChoiceLinkButton addChoiceLink(String i18Title)
    {
        ChoiceLinkButton button = new ChoiceLinkButton();
        button.setTitle(i18Title);
        configureButtonListener(button);

        return button;
    }
    
    public void addControl(Control control) {
        buttonContainer.getChildren().add(control);
    }

    private void configureButtonListener(ChoiceLinkButton button)
    {
        button.pressedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(
                ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                chosenButton = Optional.of(button);
                dialogStage.close();
            }
        });
        buttonContainer.getChildren().add(button);
    }

    public ChoiceLinkButton addChoiceLink(ChoiceLinkButton preconfiguredButton)
    {
        configureButtonListener(preconfiguredButton);

        return preconfiguredButton;
    }

    /**
     *
     * @return
     */
    public Optional<ChoiceLinkButton> getUserInput() throws PrinterDisconnectedException
    {
//        dialogStage.setWidth(DisplayManager.getMainStage().getWidth());
        dialogStage.showAndWait();
        openDialogs.remove(this);
        
        if (closedDueToPrinterDisconnect) {
            
            throw new PrinterDisconnectedException("Printer disconnected");
        }

        return chosenButton;
    }

    /**
     *
     * @return
     */
    public boolean isShowing()
    {
        return dialogStage.isShowing();
    }

    public void close()
    {
        openDialogs.remove(this);
        dialogStage.close();
    }
}
