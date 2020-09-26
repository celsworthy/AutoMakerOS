package celtech.coreUI.controllers.panels;

import celtech.coreUI.controllers.SlidablePanel;
import celtech.coreUI.controllers.SlideOutHandleController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class PrinterStatusSlideOutPanelController implements Initializable, SlidablePanel
{

    private final Stenographer steno = StenographerFactory.getStenographer(
        PrinterStatusSlideOutPanelController.class.getName());

    @FXML
    private SlideOutHandleController SlideOutHandleController;

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
    }

    @Override
    public void slideIn()
    {
        SlideOutHandleController.slideIn();
    }

}
