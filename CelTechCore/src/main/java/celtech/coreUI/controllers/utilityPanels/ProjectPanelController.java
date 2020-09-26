package celtech.coreUI.controllers.utilityPanels;

import celtech.Lookup;
import celtech.coreUI.controllers.StatusInsetController;
import celtech.roboxbase.postprocessor.PrintJobStatistics;
import celtech.roboxbase.printerControl.PrintJob;
import celtech.roboxbase.printerControl.PrinterStatus;
import celtech.roboxbase.printerControl.model.Printer;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * FXML Controller class
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class ProjectPanelController implements Initializable, StatusInsetController
{
    
    private final Stenographer steno = StenographerFactory.getStenographer(
            ProjectPanelController.class.getName());
    
    @FXML
    private VBox projectPanel;
    
    @FXML
    private Label projectName;
    
    @FXML
    private Label profileName;
    
    @FXML
    private Label layerHeight;
    
    private Printer currentPrinter = null;
    private ChangeListener<PrintJob> printJobChangeListener = (ObservableValue<? extends PrintJob> ov, PrintJob t, PrintJob printJob) ->
    {
        updateDisplay(printJob);
    };
    
    private void updateDisplay(PrintJob printJob)
    {
        if (printJob != null)
        {
            try
            {
                NumberFormat threeDPformatter = DecimalFormat.getNumberInstance(Locale.UK);
                threeDPformatter.setMaximumFractionDigits(3);
                threeDPformatter.setGroupingUsed(false);
                
                PrintJobStatistics stats = printJob.getStatistics();
                projectName.setText(stats.getProjectName());
                profileName.setText(stats.getProfileName());
                layerHeight.setText(threeDPformatter.format(stats.getLayerHeight()));
                projectPanel.setVisible(true);
            } catch (IOException ex)
            {
                projectPanel.setVisible(false);
                projectName.setText("");
                profileName.setText("");
                layerHeight.setText("");
                steno.debug("Unable to retrieve project name");
            }
        } else
        {
            projectPanel.setVisible(false);
            projectName.setText("");
            profileName.setText("");
            layerHeight.setText("");
        }
    }

    /**
     * Initialises the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        Lookup.getSelectedPrinterProperty().addListener((ObservableValue<? extends Printer> ov, Printer lastPrinter, Printer newPrinter) ->
        {
            if (currentPrinter != null)
            {
                currentPrinter.getPrintEngine().printJobProperty().removeListener(printJobChangeListener);
            }
            
            if (newPrinter == null)
            {
                projectPanel.setVisible(false);
                currentPrinter = null;
            } else
            {
                currentPrinter = newPrinter;
                newPrinter.getPrintEngine().printJobProperty().addListener(printJobChangeListener);
                updateDisplay(newPrinter.getPrintEngine().printJobProperty().get());
            }
        });
        
        projectPanel.setVisible(false);
    }
}
