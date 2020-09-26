/*
 * Copyright 2014 CEL UK
 */
package celtech.coreUI.components.Notifications;

import celtech.Lookup;
import celtech.appManager.GCodeGeneratorManager;
import celtech.appManager.ModelContainerProject;
import celtech.appManager.Project;
import celtech.appManager.ProjectMode;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;

/**
 *
 * @author tony
 */
public class PrintPreparationStatusBar extends AppearingProgressBar implements Initializable
{
    private Printer printer = null;
    private Project project;
    
    private GCodeGeneratorManager gCodeGenManager = null;

    private final ChangeListener<Boolean> serviceStatusListener = (ObservableValue<? extends Boolean> ov, Boolean lastState, Boolean newState) ->
    {
        reassessStatus();
    };

    private final ChangeListener<Number> serviceProgressListener = (ObservableValue<? extends Number> ov, Number lastState, Number newState) ->
    {
        reassessStatus();
    };

    private final EventHandler<ActionEvent> cancelEventHandler = new EventHandler<ActionEvent>()
    {
        @Override
        public void handle(ActionEvent t)
        {
            try
            {
                if (gCodeGenManager != null)
                    gCodeGenManager.cancelPrintOrSaveTask();
                if (printer.canCancelProperty().get())
                    printer.cancel(null, Lookup.getUserPreferences().isSafetyFeaturesOn());
            } catch (PrinterException ex)
            {
                System.out.println("Couldn't resume print");
            }
        }
    };

    public PrintPreparationStatusBar()
    {
        super();

        getStyleClass().add("secondaryStatusBar");
    }
    
    public void bindToPrinter(Printer printer) 
    {
        this.printer = printer;
        printer.getPrintEngine().transferGCodeToPrinterService.runningProperty().addListener(serviceStatusListener);
        printer.getPrintEngine().transferGCodeToPrinterService.progressProperty().addListener(serviceProgressListener);
        
        cancelButton.setOnAction(cancelEventHandler);
        
        if (project != null) 
        {
            reassessStatus();
        }
    }
    
    public void bindToProject(Project project) 
    {
        this.project = project;
        
        if (project instanceof ModelContainerProject) 
        {
            gCodeGenManager = ((ModelContainerProject) project).getGCodeGenManager();
            if (gCodeGenManager != null)
            {
                gCodeGenManager.selectedTaskRunningProperty().addListener(serviceStatusListener);
                gCodeGenManager.selectedTaskProgressProperty().addListener(serviceProgressListener);
            }
        }
        
        if (printer != null) 
        {
            reassessStatus();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        super.initialize(location, resources);
        targetLegendRequired(false);
        targetValueRequired(false);
        currentValueRequired(false);
        progressRequired(true);
        layerDataRequired(false);
    }

    private void reassessStatus()
    {
        boolean showBar = false;
        
        if (gCodeGenManager!= null && gCodeGenManager.printOrSaveTaskRunningProperty().get() && gCodeGenManager.selectedTaskRunningProperty().get())
        {
            largeProgressDescription.setText(gCodeGenManager.getSelectedTaskMessage());
            progressBar.setProgress(gCodeGenManager.selectedTaskProgressProperty().get());
            cancelButton.visibleProperty().set(true);
            showBar = true;
        } else
        {
            cancelButton.visibleProperty().set(true);
        }
        
        if (printer != null && printer.getPrintEngine().transferGCodeToPrinterService.runningProperty().get())
        {
            largeProgressDescription.setText(Lookup.i18n("printerStatus.sendingToPrinter"));
            progressBar.setProgress(printer.getPrintEngine().transferGCodeToPrinterService.getProgress());
            //Cancel is provided from the print bar in this mode
            cancelButton.visibleProperty().set(false);
            showBar = true;
        }
        
        if (showBar)
        {
            startSlidingInToView();
        } else
        {
            startSlidingOutOfView();
        }
    }

    public void unbindAll()
    {
        unbindFromProject();
        unbindFromPrinter();
        // Hide the bar if it is currently shown.
        startSlidingOutOfView();
    }
    
    public void unbindFromPrinter() {
        if (printer != null) {
            printer.getPrintEngine().transferGCodeToPrinterService.runningProperty().removeListener(serviceStatusListener);
            printer.getPrintEngine().transferGCodeToPrinterService.progressProperty().removeListener(serviceProgressListener);
            printer = null;
        }
    }
    
    public void unbindFromProject() {
        if(project instanceof ModelContainerProject) 
        
        {
            gCodeGenManager = ((ModelContainerProject) project).getGCodeGenManager();
            if (gCodeGenManager != null) {
                gCodeGenManager.selectedTaskRunningProperty().removeListener(serviceStatusListener);
                gCodeGenManager.selectedTaskProgressProperty().removeListener(serviceProgressListener);
            }
            project = null;
        }
    }
}
