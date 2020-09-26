package celuk.groot.controllers;

import celuk.groot.remote.FilamentDetails;
import celuk.groot.remote.PrinterStatusResponse;
import celuk.groot.remote.RootPrinter;
import celuk.groot.remote.ServerStatusResponse;
import celuk.language.I18n;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class HomeController implements Initializable, Page {
    
    @FXML
    private StackPane homePane;
    @FXML
    private Button printerButton;
    @FXML
    private Label modelLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label filament1DescriptionLabel;
    @FXML
    private Label filament1TypeLabel;
    @FXML
    private Label filament1RemainingLabel;
    @FXML
    private Pane filament1Pane;
    @FXML
    private Button filament1EjectButton;
    @FXML
    private Label filament2TypeLabel;
    @FXML
    private Label filament2DescriptionLabel;
    @FXML
    private Label filament2RemainingLabel;
    @FXML
    private Pane filament2Pane;
    @FXML
    private Button filament2EjectButton;
    @FXML
    private Label leftNozzleTempLabel;
    @FXML
    private Label leftNozzleTitleLabel;
    @FXML
    private Label rightNozzleTempLabel;
    @FXML
    private Label rightNozzleTitleLabel;
    @FXML
    private Label bedTempLabel;
    @FXML
    private Label bedTitleLabel;
    @FXML
    private Label ambientTempLabel;
    @FXML
    private Label ambientTitleLabel;
    @FXML
    private VBox idleVBox;
    @FXML
    private VBox jobVBox;
    @FXML
    private Pane jobStatusIcon;
    @FXML
    private Label jobStatusLabel;
    @FXML
    private Pane jobRemainingIcon;
    @FXML
    private Label jobEtcLabel;
    @FXML
    private ProgressBar jobProgressBar;
    @FXML
    private HBox jobControlHBox;
    @FXML
    private Button tweakButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button cancelButton;
    @FXML
    private GridPane jobStatusGrid;
    @FXML
    private Label jobNameTitleLabel;
    @FXML
    private Label jobNameLabel;
    @FXML
    private Label jobCreatedTitleLabel;
    @FXML
    private Label jobCreatedLabel;
    @FXML
    private Label jobDurationTitleLabel;
    @FXML
    private Label jobDurationLabel;
    @FXML
    private Label jobProfileTitleLabel;
    @FXML
    private Pane jobProfileIcon;
    @FXML
    private Label jobProfileLabel;
    @FXML
    private Button leftButton;
    @FXML
    private Button middleButton;
    @FXML
    private Button rightButton;

    private RootStackController rootController = null;
    private RootPrinter printer = null;
    private final PseudoClass pausedPS = PseudoClass.getPseudoClass("paused");
    private final PseudoClass playingPS = PseudoClass.getPseudoClass("playing");
    private final PseudoClass idlePS = PseudoClass.getPseudoClass("idle");
    private final String rightNozzleTitle = I18n.t("common.rightNozzle");
    private final String leftNozzleTitle = I18n.t("common.leftNozzle");
    private final String nozzlesTitle = I18n.t("common.nozzles");
    private final String nozzleTitle = I18n.t("common.nozzle");

    @FXML
    void printerButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
//            rootController.showPrinterMenu(this, printer);
        }
    }
    
    @FXML
    void ejectButtonAction(ActionEvent event) {
        int filamentNumber = (event.getSource() == filament1EjectButton ? 1 : 2);
        printer.runEjectFilamentTask(filamentNumber);
    }
    
    @FXML
    void tweakButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            rootController.showTweakPage(printer);
        }
    }

    @FXML
    void pauseButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            PrinterStatusResponse status = printer.getCurrentStatusProperty().get();
            if (status.isCanPause() && !status.isCanResume())
                printer.runPauseTask();
            else if (!status.isCanPause() && status.isCanResume())
                printer.runResumeTask();
        }
    }

    @FXML
    void cancelButtonAction(ActionEvent event) {
        if (rootController != null &&
            event.getSource() instanceof Button &&
            (printer.getCurrentStatusProperty().get().getPrinterStatusEnumValue().startsWith("HEATING") ||
             printer.getCurrentStatusProperty().get().isCanCancel())) {
            printer.runCancelTask();
        }
    }

    @FXML
    void leftButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button)
            rootController.showPrinterSelectPage();
    }
    
    @FXML
    void middleButtonAction(ActionEvent event)
    {
        if (rootController != null && event.getSource() instanceof Button)
            rootController.showMainMenu(printer);
    }

    @FXML
    void rightButtonAction(ActionEvent event)
    {
        if (rootController != null && event.getSource() instanceof Button)
           printer.runUnlockDoorTask();
    }

    private ChangeListener<ServerStatusResponse> serverStatusListener = (ob, ov, nv) -> {
        //System.out.println("HomeController \"" + printer.getPrinterId() + "\" serverStatusListener");
        updateServerStatus(nv);
    };

    private ChangeListener<Boolean> printerMapHeartbeatListener = (pr, ov, nv) ->  {
        //System.out.println("HomeController::printerMapHeartbeatListener");
        configureBackButton();
    };

    private ChangeListener<PrinterStatusResponse> printerStatusListener = (ob, ov, nv) -> {
        //System.out.println("HomeController \"" + printer.getPrinterId() + "\"printerStatusListener");
        updatePrinterStatus(nv);
    };
    
    @Override
    public void setRootStackController(RootStackController rootController) {
        this.rootController = rootController;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Translate text on page.
        translateLabels(leftNozzleTitleLabel,
                        rightNozzleTitleLabel,
                        bedTitleLabel,
                        ambientTitleLabel,
                        jobNameTitleLabel,
                        jobCreatedTitleLabel,
                        jobDurationTitleLabel,
                        jobProfileTitleLabel);

        homePane.setVisible(false);
        jobVBox.setVisible(false);
        jobVBox.setManaged(false);
        jobProgressBar.setVisible(false);
        jobProgressBar.setManaged(false);
        jobStatusGrid.setVisible(false);
        jobStatusGrid.setManaged(false);
        jobControlHBox.setVisible(false);
        jobControlHBox.setManaged(false);
    }
    
    @Override
    public void startUpdates() {
        printer.getRootServer().getCurrentStatusProperty().addListener(serverStatusListener);
        printer.getRootServer().getCurrentPrinterMapHeartbeatProperty().addListener(printerMapHeartbeatListener);
        printer.getCurrentStatusProperty().addListener(printerStatusListener);
        updateServerStatus(printer.getRootServer().getCurrentStatusProperty().get());
        updatePrinterStatus(printer.getCurrentStatusProperty().get());
        configureBackButton();
    }
    
    @Override
    public void stopUpdates() {
        // Printer can be null if
        // the home page has never been shown.
        if (printer != null) {
            printer.getRootServer().getCurrentStatusProperty().removeListener(serverStatusListener);
            printer.getRootServer().getCurrentPrinterMapHeartbeatProperty().removeListener(printerMapHeartbeatListener);
            printer.getCurrentStatusProperty().removeListener(printerStatusListener);
            printer = null;
        }
    }

        @Override
    public void displayPage(RootPrinter printer) {
        this.printer = printer;
        if (!homePane.isVisible()) {
            startUpdates();
            homePane.setVisible(true);
        }
    }
    
    @Override
    public void hidePage() {
        stopUpdates();
        homePane.setVisible(false);
    }

    @Override
    public boolean isVisible() {
        return homePane.isVisible();
    }
    
    private void updateServerStatus(ServerStatusResponse serverStatus) {
        Platform.runLater(() -> {
            if (serverStatus != null) {
                addressLabel.setText(serverStatus.getServerIP());
            }
            else {
                addressLabel.setText("---.---.---.---");
            }
        });
    }
    
    private void configureBackButton() {
        boolean showBackButton = (rootController.getRootServer().getCurrentPrinterMap().size() > 1);
        Platform.runLater(() -> {
            leftButton.setDisable(!showBackButton);
            leftButton.setVisible(showBackButton);
        });
    }
    
    private void updatePrinterStatus(PrinterStatusResponse printerStatus) {
        if (printerStatus != null) {
            MachineDetails md = MachineDetails.getDetails(printerStatus.getPrinterTypeCode());

            Platform.runLater(() -> {
                printerButton.setStyle("-fx-background-color: "
                                       + printerStatus.getPrinterWebColourString()
                                       + ";"
                                       + "-fx-background-image: url(\""
                                       + md.getMachineIcon(printerStatus.getPrinterWebColourString())
                                       + "\");");
                nameLabel.setText(printerStatus.getPrinterName());
                modelLabel.setText(I18n.t(md.model));
                idleVBox.setStyle("-fx-background-image: url(\""
                                 + md.getStatusIconDark20()
                                 + "\");");
                updateFilamentStatus(printerStatus, 0);
                updateFilamentStatus(printerStatus, 1);
                updateFilamentEjectStatus(printerStatus);
                updateHeadStatus(printerStatus);
                updatePrintStatus(printerStatus);
                updateJobStatus(printerStatus);
                updateControlStatus(printerStatus);
            });
        }
    }
    
    private void updateFilamentStatus(PrinterStatusResponse printerStatus, int filamentIndex) {
        String typeValue = null;
        String descriptionValue = null;
        boolean showLoaded = false;
        String halfReelIconResource = "url(\"/image/home-no-half-reel.png\")";
        boolean reelKnown = false; 
        double remaining = -1.0;
    
        if (printerStatus.getAttachedFilaments() != null &&
            printerStatus.getAttachedFilaments().length > filamentIndex) {
            FilamentDetails filament = printerStatus.getAttachedFilaments()[filamentIndex];
            showLoaded = filament.getMaterialLoaded();
            if (filament.getFilamentName() != null && !filament.getFilamentName().isEmpty()) {
                typeValue = filament.getMaterialName();
                descriptionValue = filament.getFilamentName();
                if (filament.getCustomFlag()) {
                    halfReelIconResource = "url(\"/image/home-custom-half-reel.png\")";
                }
                else {
                    halfReelIconResource = "url(\"/image/home-smart-half-reel.png\")";
                }
                remaining = filament.getRemainingFilament();
                reelKnown = true;
            }
        }
        else
        {
            if (showLoaded)
            {
                descriptionValue = "Unknown Filament";
                halfReelIconResource = "url(\"/image/home-unknown-half-reel.png\")";
            }
            else
            {
                descriptionValue = "No Filament";
                halfReelIconResource = "url(\"/image/home-no-half-reel.png\")";
            }
        }
    
        String typePrefix;
        Label descriptionLabel;
        Label typeLabel;
        Label remainingLabel;
        Pane reelPane;
        Button ejectButton;

        if (filamentIndex == 0) {
            typePrefix = "1:";
            descriptionLabel = filament1DescriptionLabel;
            typeLabel = filament1TypeLabel;
            remainingLabel = filament1RemainingLabel;
            reelPane = filament1Pane;
            ejectButton = filament1EjectButton;
        }
        else {
            typePrefix = "2:";
            descriptionLabel = filament2DescriptionLabel;
            typeLabel = filament2TypeLabel;
            remainingLabel = filament2RemainingLabel;
            reelPane = filament2Pane;
            ejectButton = filament2EjectButton;
        }

        if (typeValue != null)
        {
            typeLabel.setText(typePrefix + typeValue);
            if (descriptionValue != null)
            {
                descriptionLabel.setText(descriptionValue);
            }
            else
            {
                descriptionLabel.setText("");
            }
            if (remaining > -1.0)
                remainingLabel.setText(Long.toString(Math.round(remaining)) + "m");
            else
                remainingLabel.setText("");
        }
        else
        {
            typeLabel.setText(typePrefix);
            descriptionLabel.setText("");
            remainingLabel.setText("");
        }
        
        if (reelKnown)
            descriptionLabel.setStyle("-fx-background-color: white;");
        else
            descriptionLabel.setStyle("-fx-background-color: #b2b2b2;");

        reelPane.setStyle("-fx-background-image: " + halfReelIconResource + ";");
        if (showLoaded)
            ejectButton.setVisible(true);
        else
            ejectButton.setVisible(false);
    }

    private void updateFilamentEjectStatus(PrinterStatusResponse printerStatus) {
        boolean disableEject1Button = false;
        boolean disableEject2Button = false;
    
        if (printerStatus.getAttachedFilaments() != null) {
            disableEject1Button = (printerStatus.getAttachedFilaments().length < 1 || !printerStatus.getAttachedFilaments()[0].getCanEject());
            disableEject2Button = (printerStatus.getAttachedFilaments().length < 2 || !printerStatus.getAttachedFilaments()[1].getCanEject());
        }

        filament1EjectButton.setDisable(disableEject1Button);
        filament2EjectButton.setDisable(disableEject2Button);
    }
    
    private void updateHeadStatus(PrinterStatusResponse printerStatus) {
        bedTempLabel.setText(Integer.toString(printerStatus.getBedTemperature()) + "°C");
        ambientTempLabel.setText(Integer.toString(printerStatus.getAmbientTemperature()) + "°C");
        String leftNozzleTemperature = "";
        String rightNozzleTemperature = "";
        int numberOfNozzleHeaters = 0;
        if (printerStatus.getNozzleTemperature() != null)
            numberOfNozzleHeaters = printerStatus.getNozzleTemperature().length;
        switch (numberOfNozzleHeaters)
        {
            case 0:
                leftNozzleTempLabel.setVisible(false);
                leftNozzleTempLabel.setManaged(false);
                leftNozzleTitleLabel.setVisible(false);
                leftNozzleTitleLabel.setManaged(false);
                leftNozzleTitleLabel.setText("");
                rightNozzleTempLabel.setVisible(false);
                rightNozzleTempLabel.setManaged(false);
                rightNozzleTitleLabel.setVisible(false);
                rightNozzleTitleLabel.setManaged(false);
                leftNozzleTitleLabel.setText("");
                break;
            case 1:
                leftNozzleTempLabel.setVisible(false);
                leftNozzleTempLabel.setManaged(false);
                leftNozzleTitleLabel.setVisible(false);
                leftNozzleTitleLabel.setManaged(false);
                leftNozzleTitleLabel.setText("");
                
                rightNozzleTempLabel.setVisible(true);
                rightNozzleTempLabel.setManaged(true);
                rightNozzleTitleLabel.setVisible(true);
                rightNozzleTitleLabel.setManaged(true);

                if (printerStatus.getHeadTypeCode().equalsIgnoreCase("RBX01-SM") || 
                    printerStatus.getHeadTypeCode().equalsIgnoreCase("RBX01-S2"))
                    rightNozzleTitleLabel.setText(nozzlesTitle);
                else
                    rightNozzleTitleLabel.setText(nozzleTitle);
                if (printerStatus.getNozzleTemperature()[0] > 0.0)
                    rightNozzleTemperature = Integer.toString(printerStatus.getNozzleTemperature()[0]) + "°C";
                break;
            case 2:
                leftNozzleTempLabel.setVisible(true);
                leftNozzleTempLabel.setManaged(true);
                leftNozzleTitleLabel.setVisible(true);
                leftNozzleTitleLabel.setManaged(true);
                leftNozzleTitleLabel.setText(leftNozzleTitle);
                rightNozzleTempLabel.setVisible(true);
                rightNozzleTempLabel.setManaged(true);
                rightNozzleTitleLabel.setVisible(true);
                rightNozzleTitleLabel.setManaged(true);
                rightNozzleTitleLabel.setText(rightNozzleTitle);
                if (printerStatus.getNozzleTemperature()[0] > 0.0)
                    leftNozzleTemperature = Integer.toString(printerStatus.getNozzleTemperature()[0]) + "°C";
                if (printerStatus.getNozzleTemperature()[1] > 0.0)
                    rightNozzleTemperature = Integer.toString(printerStatus.getNozzleTemperature()[1]) + "°C";
                break;
        }
        leftNozzleTempLabel.setText(leftNozzleTemperature);
        rightNozzleTempLabel.setText(rightNozzleTemperature);
    }
    
    private void updateJobStatus(PrinterStatusResponse printerStatus) {

        if (printerStatus.getPrinterStatusEnumValue().startsWith("IDLE"))
        {
            // Hide all the job nodes.
            jobVBox.setVisible(false);
            jobVBox.setManaged(false);
            jobProgressBar.setVisible(false);
            jobProgressBar.setManaged(false);
            jobStatusGrid.setVisible(false);
            jobStatusGrid.setManaged(false);
            jobControlHBox.setVisible(false);
            jobControlHBox.setManaged(false);
        }
        else
        {
            jobVBox.setVisible(true);
            jobVBox.setManaged(true);
            boolean showStatusGrid = false;
            if (printerStatus.getPrintJobName() == null || printerStatus.getPrintJobName().isBlank()) {
                jobNameLabel.setText("");
            }
            else {
                jobNameLabel.setText(printerStatus.getPrintJobName());
                showStatusGrid = true;
            }
            jobCreatedLabel.setText("");
            if (printerStatus.getTotalDurationSeconds() <= 0)
                jobDurationLabel.setText("");
            else
            {
                jobDurationLabel.setText(secondsToHMS(printerStatus.getTotalDurationSeconds()));
                // Only show panel if there is a name.
                // showStatusGrid = true; 
            }
            if (printerStatus.getPrintJobSettings() == null || printerStatus.getPrintJobSettings().isBlank())
                jobProfileLabel.setText("");
            else
            {
                jobProfileLabel.setText(printerStatus.getPrintJobSettings());
                // Only show panel if there is a name.
                // showStatusGrid = true; 
            }

            jobStatusGrid.setVisible(showStatusGrid);
            jobStatusGrid.setManaged(showStatusGrid);
        }
    }

    private void updatePrintStatus(PrinterStatusResponse printerStatus)
    {
        boolean paused = false;
        boolean playing = false;
        boolean idle = false;
        switch(printerStatus.getPrinterStatusEnumValue())
        {
            case "PRINTING_PROJECT":
            case "RESUME_PENDING":
            case "RUNNING_MACRO_FILE":
                playing = true;
                break;
            case "PAUSED":
			case "SELFIE_PAUSE":
            case "PAUSE_PENDING":
                paused = true;
                break;
            case "IDLE":
                idle = true;
                break;

            // These are all the states of which I am aware.
            case "LOADING_FILAMENT_D":
            case "LOADING_FILAMENT_E":
            case "UNLOADING_FILAMENT_D":
            case "UNLOADING_FILAMENT_E":
            case "CALIBRATING_NOZZLE_ALIGNMENT":
            case "CALIBRATING_NOZZLE_HEIGHT":
            case "CALIBRATING_NOZZLE_OPENING":
            case "OPENING_DOOR":
            case "PURGING_HEAD":
            case "REMOVING_HEAD":
            case "HEATING":
            default:
                break;
        }
        
        jobStatusIcon.pseudoClassStateChanged(pausedPS, paused);
        jobStatusIcon.pseudoClassStateChanged(playingPS, playing);
        jobStatusIcon.pseudoClassStateChanged(idlePS, idle);
        jobStatusIcon.setVisible(paused || playing || idle);
        jobStatusIcon.setManaged(paused || playing || idle);
            
        String jobStatusText = printerStatus.getPrinterStatusString();
        // For some reason all but the heating statuses are translated.
        if (jobStatusText.startsWith("heating-"))
            jobStatusText = I18n.t("common." + jobStatusText.replace('-', '.'));
        jobStatusLabel.setText(jobStatusText);

        if ((printerStatus.getPrinterStatusEnumValue().startsWith("PRINTING_PROJECT")
                || printerStatus.getPrinterStatusEnumValue().startsWith("RUNNING_MACRO")
                || printerStatus.getPrinterStatusEnumValue().startsWith("PAUSED")
                || printerStatus.getPrinterStatusEnumValue().startsWith("SELFIE_PAUSE")
                || printerStatus.getPrinterStatusEnumValue().startsWith("PAUSE_PENDING")
                || printerStatus.getPrinterStatusEnumValue().startsWith("RESUME_PENDING"))
                && printerStatus.getTotalDurationSeconds() >= 0
                && printerStatus.getEtcSeconds() > 0)
        {
            jobRemainingIcon.setVisible(true);
            jobRemainingIcon.setManaged(true);
            jobEtcLabel.setText(secondsToHMS(printerStatus.getEtcSeconds()));
            jobEtcLabel.setVisible(true);
            jobEtcLabel.setManaged(true);
            jobProgressBar.setVisible(true);
            jobProgressBar.setManaged(true);

            int timeElapsed = printerStatus.getTotalDurationSeconds() -  printerStatus.getEtcSeconds();
            if (timeElapsed < 0)
                timeElapsed = 0;
            if (timeElapsed <= 0 || printerStatus.getTotalDurationSeconds() <= 0)
            {
                jobProgressBar.setProgress(0.0);
            }
            else
            {
                double progressFraction = (double)timeElapsed / (double)printerStatus.getTotalDurationSeconds();
                jobProgressBar.setProgress(progressFraction);
            }
            jobEtcLabel.setText(secondsToHMS(printerStatus.getEtcSeconds()));
        }
        else
        {
            jobRemainingIcon.setVisible(false);
            jobRemainingIcon.setManaged(false);
            jobEtcLabel.setText("");
            jobEtcLabel.setVisible(false);
            jobEtcLabel.setManaged(false);
            if (printerStatus.getPrinterStatusEnumValue().startsWith("HEATING") &&
               printerStatus.getHeatingProgress() >= 0 &&
               printerStatus.getHeatingProgress() <= 100)
            {
                jobProgressBar.setProgress(0.01F * printerStatus.getHeatingProgress());
                jobProgressBar.setVisible(true);
                jobProgressBar.setManaged(true);
            }
            else
            {
                jobProgressBar.setProgress(0.0);
                jobProgressBar.setVisible(false);
                jobProgressBar.setManaged(false);
            }
        }
    }

    private void updateControlStatus(PrinterStatusResponse printerStatus) {
        if (printerStatus.getPrinterStatusEnumValue().startsWith("IDLE")) {
            jobControlHBox.setVisible(false);
            jobControlHBox.setManaged(false);
        }
        else {
            jobControlHBox.setVisible(true);
            jobControlHBox.setManaged(true);

            if (printerStatus.isCanPause()) {
                pauseButton.setDisable(false);
                pauseButton.pseudoClassStateChanged(pausedPS, false);
            }
            else if (printerStatus.isCanResume()) {
                pauseButton.setDisable(false);
                pauseButton.pseudoClassStateChanged(pausedPS, true);
            }
            else {
                pauseButton.setDisable(true);
                pauseButton.pseudoClassStateChanged(pausedPS, true);
            }

            cancelButton.setDisable(!(printerStatus.getPrinterStatusEnumValue().startsWith("HEATING") ||
                                      printerStatus.isCanCancel()));

            if (printerStatus.getPrinterStatusEnumValue().startsWith("PRINTING_PROJECT"))
            {
                tweakButton.setDisable(false);
                tweakButton.setVisible(true);        
            }
            else {
                tweakButton.setDisable(true);
                tweakButton.setVisible(false);        
            }
        }
        
        rightButton.setDisable(!printerStatus.isCanOpenDoor());
    }
}
