package celuk.groot.controllers;

import celuk.groot.remote.PrintJobData;
import celuk.groot.remote.PrintJobListData;
import celuk.groot.remote.RootPrinter;
import celuk.language.I18n;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PrintController implements Initializable, Page {
    
    private class JobPanel {
        public GridPane jobGrid;
        public Label jobNameTitle;
        public Label jobCreatedTitle;
        public Label jobDurationTitle;
        public Label jobProfileTitle;
        public Label jobName;
        public Label jobCreated;
        public Label jobDuration;
        public Label jobProfile;
        public Pane jobProfileIcon;
        
        public JobPanel(GridPane jobGrid,
                        Label jobNameTitle,
                        Label jobCreatedTitle,
                        Label jobDurationTitle,
                        Label jobProfileTitle,
                        Label jobName,
                        Label jobCreated,
                        Label jobDuration,
                        Label jobProfile,
                        Pane jobProfileIcon) {
            this.jobGrid = jobGrid;
            this.jobNameTitle = jobNameTitle;
            this.jobCreatedTitle = jobCreatedTitle;
            this.jobDurationTitle = jobDurationTitle;
            this.jobProfileTitle = jobProfileTitle;
            this.jobName = jobName;
            this.jobCreated = jobCreated;
            this.jobDuration = jobDuration;
            this.jobProfile = jobProfile;
            this.jobProfileIcon = jobProfileIcon;
        }
    }
    
    @FXML
    private StackPane printPane;
    @FXML
    private Label printTitle;
    @FXML
    private VBox noPrintPanel;
    @FXML
    private Pane noPrintJobsIcon;
    @FXML
    private Label noPrintJobsTitle;
    @FXML
    private Label noPrintJobsDetails;
    @FXML
    private VBox jobsPanel;
    @FXML
    private GridPane jobGrid1;
    @FXML
    private Label job1NameTitle;
    @FXML
    private Label job1CreatedTitle;
    @FXML
    private Label job1DurationTitle;
    @FXML
    private Label job1ProfileTitle;
    @FXML
    private Label job1Name;
    @FXML
    private Label job1Created;
    @FXML
    private Label job1Duration;
    @FXML
    private Label job1Profile;
    @FXML
    private Pane job1ProfileIcon;

    @FXML
    private GridPane jobGrid2;
    @FXML
    private Label job2NameTitle;
    @FXML
    private Label job2CreatedTitle;
    @FXML
    private Label job2DurationTitle;
    @FXML
    private Label job2ProfileTitle;
    @FXML
    private Label job2Name;
    @FXML
    private Label job2Created;
    @FXML
    private Label job2Duration;
    @FXML
    private Label job2Profile;
    @FXML
    private Pane job2ProfileIcon;

    @FXML
    private GridPane jobGrid3;
    @FXML
    private Label job3NameTitle;
    @FXML
    private Label job3CreatedTitle;
    @FXML
    private Label job3DurationTitle;
    @FXML
    private Label job3ProfileTitle;
    @FXML
    private Label job3Name;
    @FXML
    private Label job3Created;
    @FXML
    private Label job3Duration;
    @FXML
    private Label job3Profile;
    @FXML
    private Pane job3ProfileIcon;

    @FXML
    private GridPane jobGrid4;
    @FXML
    private Label job4NameTitle;
    @FXML
    private Label job4CreatedTitle;
    @FXML
    private Label job4DurationTitle;
    @FXML
    private Label job4ProfileTitle;
    @FXML
    private Label job4Name;
    @FXML
    private Label job4Created;
    @FXML
    private Label job4Duration;
    @FXML
    private Label job4Profile;
    @FXML
    private Pane job4ProfileIcon;

    @FXML
    private GridPane navigatorBar;
    @FXML
    private Button nextPageButton;
    @FXML
    private Label pageLabel;
    @FXML
    private Button previousPageButton;

    @FXML
    private HBox bottomBarHBox;
    @FXML
    private Button leftButton;
    @FXML
    private Button middleButton;
    @FXML
    private Button rightButton;

    @FXML
    void nextPageAction(ActionEvent event) {
        ++currentPage;
        if (currentPage >= nPages)
            currentPage = nPages - 1;
        displayCurrentPage();
    }
    
    @FXML
    void previousPageAction(ActionEvent event) {
        --currentPage;
        if (currentPage < 0)
            currentPage = 0;
        displayCurrentPage();
    }
    
    @FXML
    void jobMouseAction(MouseEvent event) {
        if (event.getSource() instanceof GridPane) {
            GridPane jobPane = (GridPane)event.getSource();
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                jobPane.pseudoClassStateChanged(activePS, true);
            }
            else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                jobPane.pseudoClassStateChanged(activePS, false);
                if (jobPane.contains(jobPane.sceneToLocal(event.getSceneX(), event.getSceneY())))
                    printJob((PrintJobData)jobPane.getUserData());
            }
        }
    }

    @FXML
    void leftButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button)
            rootController.showPrintMenu(printer);
    }
    
    @FXML
    void middleButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button)
            rootController.showHomePage(printer);
    }

    @FXML
    void rightButtonAction(ActionEvent event) {
    }

    private final int JOBS_PER_PAGE = 4;
    private RootStackController rootController = null;
    private RootPrinter printer = null;
    private final JobPanel[] jobPanels = new JobPanel[JOBS_PER_PAGE];
    private Thread updateThread = null;
    private int currentPage = 0;
    private int nPages = 0;
    private String pageNumberFormat = "print.pageNumber";
    private String reprintTitleText= "reprint.title";
    private String usbPrintTitleText = "usbprint.title";
    private String noSuitableJobsTitleText = "print.noSuitableJobsTitle";
    private String noSuitableJobsDetailsText = "print.noSuitableJobsDetails";
    private String loadingDetailsText = "print.loadingDetails";
    private String loadingTitleText = "print.loadingTitle";
    private String noJobsTitleText = "print.noJobsTitle";
    private String noJobsDetailsText = "print.noJobsDetails";
    private String noMediaTitleText = "print.noMediaTitle";
    private String noMediaDetailsText = "print.noMediaDetails";
    private String noPrinterTitleText = "print.noPrinterTitle";
    private String noPrinterDetailsText = "print.noPrinterDetails";
    private String errorTitleText = "print.errorTitle";
    private String errorDetailsText = "print.errorDetails";
    private PrintJobListData currentJobList = null;
    private final PseudoClass activePS = PseudoClass.getPseudoClass("active");
    private boolean reprintMode = true;
    
    @Override
    public void setRootStackController(RootStackController rootController) {
        this.rootController = rootController;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pageNumberFormat = I18n.t(pageNumberFormat);
        translateLabels(job1NameTitle,
                        job1CreatedTitle,
                        job1DurationTitle,
                        job1ProfileTitle,
                        job2NameTitle,
                        job2CreatedTitle,
                        job2DurationTitle,
                        job2ProfileTitle,
                        job3NameTitle,
                        job3CreatedTitle,
                        job3DurationTitle,
                        job3ProfileTitle,
                        job4NameTitle,
                        job4CreatedTitle,
                        job4DurationTitle,
                        job4ProfileTitle);

        reprintTitleText = I18n.t(reprintTitleText);
        usbPrintTitleText = I18n.t(usbPrintTitleText);
        loadingDetailsText = I18n.t(loadingDetailsText);
        loadingTitleText = I18n.t(loadingTitleText);
        noSuitableJobsTitleText = I18n.t(noSuitableJobsTitleText);
        noSuitableJobsDetailsText = I18n.t(noSuitableJobsDetailsText);
        noJobsTitleText = I18n.t(noJobsTitleText);
        noJobsDetailsText = I18n.t(noJobsDetailsText);
        noMediaTitleText = I18n.t(noMediaTitleText);
        noMediaDetailsText = I18n.t(noMediaDetailsText);
        noPrinterTitleText = I18n.t(noPrinterTitleText);
        noPrinterDetailsText = I18n.t(noPrinterDetailsText);
        errorTitleText = I18n.t(errorTitleText);
        errorDetailsText = I18n.t(errorDetailsText);

        setReprintMode(true);
                
        jobPanels[0] = new JobPanel(jobGrid1,
                                    job1NameTitle,
                                    job1CreatedTitle,
                                    job1DurationTitle,
                                    job1ProfileTitle,
                                    job1Name,
                                    job1Created,
                                    job1Duration,
                                    job1Profile,
                                    job1ProfileIcon);
        jobPanels[1] = new JobPanel(jobGrid2,
                                    job2NameTitle,
                                    job2CreatedTitle,
                                    job2DurationTitle,
                                    job2ProfileTitle,
                                    job2Name,
                                    job2Created,
                                    job2Duration,
                                    job2Profile,
                                    job2ProfileIcon);
        jobPanels[2] = new JobPanel(jobGrid3,
                                    job3NameTitle,
                                    job3CreatedTitle,
                                    job3DurationTitle,
                                    job3ProfileTitle,
                                    job3Name,
                                    job3Created,
                                    job3Duration,
                                    job3Profile,
                                    job3ProfileIcon);
        jobPanels[3] = new JobPanel(jobGrid4,
                                    job4NameTitle,
                                    job4CreatedTitle,
                                    job4DurationTitle,
                                    job4ProfileTitle,
                                    job4Name,
                                    job4Created,
                                    job4Duration,
                                    job4Profile,
                                    job4ProfileIcon);
        for (JobPanel p : jobPanels) {
            p.jobGrid.setVisible(false);
        }
        jobsPanel.setVisible(false);
        jobsPanel.setManaged(false);
        noPrintPanel.setVisible(true);
        noPrintPanel.setManaged(true);
        rightButton.setVisible(false);
        printPane.setVisible(false);
    }
    
    @Override
    public void startUpdates() {
        Platform.runLater(() -> {
            clearPrintableJobs(null);
        });
        updateThread = new Thread(() -> {
            Thread t = updateThread;
            try {
                PrintJobListData jobList = printer.runListPrintableJobsTask(reprintMode).get();
                if (t == updateThread)
                    updatePrintableJobs(jobList);
            }
            catch (Exception ex) {
                System.err.println("Caught exception when attempting to update printable jobs.");
            }
        });
        updateThread.start();
    }
    
    @Override
    public void stopUpdates() {
        printer = null;
        updateThread = null;
    }

    @Override
    public void displayPage(RootPrinter printer) {
        this.printer = printer;
        if (!printPane.isVisible()) {
            startUpdates();
            printPane.setVisible(true);
        }
    }

    @Override
    public void hidePage() {
        stopUpdates();
        printPane.setVisible(false);
    }
    
    @Override
    public boolean isVisible() {
        return printPane.isVisible();
    }

    public void setReprintMode(boolean flag) {
        reprintMode = flag;
        // Set title and nothing available background
        if (reprintMode) {
            printTitle.setText(reprintTitleText);
        }
        else {
            printTitle.setText(usbPrintTitleText);
        }
    }
    
    private void displayNavigatorBar() {
        previousPageButton.setDisable(currentPage == 0);
        nextPageButton.setDisable(currentPage == (nPages - 1));
        pageLabel.setText(String.format(pageNumberFormat, currentPage + 1, nPages));
    }
    
    private void displayCurrentPage() {
        int startIndex = currentPage * JOBS_PER_PAGE;
        for (var jobIndex = 0; jobIndex < JOBS_PER_PAGE; jobIndex++) {
            
            JobPanel p = jobPanels[jobIndex];
            p.jobGrid.pseudoClassStateChanged(activePS, false);
            var pjIndex = startIndex + jobIndex;
            if (pjIndex < currentJobList.getJobs().size())
            {
                PrintJobData job = currentJobList.getJobs().get(pjIndex);

                p.jobGrid.setVisible(true);
                p.jobGrid.setUserData(job);
                p.jobName.setText(job.getPrintJobName());
                p.jobCreated.setText("");
                p.jobDuration.setText(secondsToHMS((int)job.getDurationInSeconds()));
                p.jobProfile.setText(job.getPrintProfileName());
            }
            else
            {
                p.jobGrid.setVisible(false);
                p.jobGrid.setUserData(null);
                p.jobName.setText("");
                p.jobCreated.setText("");
                p.jobDuration.setText("");
                p.jobProfile.setText("");
            }
        }

        displayNavigatorBar();
    }
                
    private void clearPrintableJobs(PrintJobListData.ListStatus status) {
        jobsPanel.setVisible(false);
        jobsPanel.setManaged(false);
        noPrintPanel.setVisible(true);
        noPrintPanel.setManaged(true);
        currentPage = 0;
        nPages = 0;
        for (JobPanel p : jobPanels) {
            p.jobGrid.setVisible(false);
            p.jobGrid.setUserData(null);
            p.jobGrid.pseudoClassStateChanged(activePS, false);
        }
        String titleText = loadingTitleText;
        String detailsText = loadingDetailsText;
        if (status != null) {
            switch (status) {
                case NO_SUITABLE_JOBS:
                    titleText = noSuitableJobsTitleText;
                    detailsText = noSuitableJobsDetailsText;
                    break;
                case NO_JOBS:
                    titleText = noJobsTitleText;
                    detailsText = noJobsDetailsText;
                    break;
                case NO_MEDIA:
                    titleText = noMediaTitleText;
                    detailsText = noMediaDetailsText;
                    break;
                case NO_PRINTER:
                    titleText = noPrinterTitleText;
                    detailsText = noPrinterDetailsText;
                    break;
                case ERROR:
                    titleText = errorTitleText;
                    detailsText = errorDetailsText;
                    break;
            }
        }
        noPrintJobsTitle.setText(titleText);
        noPrintJobsDetails.setText(detailsText);
    }

    private void updatePrintableJobs(PrintJobListData jobList) {
        Platform.runLater(() -> {
            currentJobList = jobList;
            if (jobList != null) {
                if (jobList.getStatus() == PrintJobListData.ListStatus.OK) {
                    jobsPanel.setVisible(true);
                    jobsPanel.setManaged(true);
                    noPrintPanel.setVisible(false);
                    noPrintPanel.setManaged(false);
                    currentPage = 0;
                    nPages = (int)Math.floor(currentJobList.getJobs().size() / (double)JOBS_PER_PAGE);
                    if ((currentJobList.getJobs().size() % JOBS_PER_PAGE) > 0 || nPages == 0)
                        nPages++;

                    displayCurrentPage();
                }
                else
                    clearPrintableJobs(jobList.getStatus());
            }
            else {
                clearPrintableJobs(PrintJobListData.ListStatus.ERROR);
            }
        });
    }

    private void printJob(PrintJobData job) {
        if (job != null) {
            try {
                if (job.getPrintJobPath() == null || job.getPrintJobPath().isEmpty())
                    printer.runReprintJobTask(job.getPrintJobID()).get();
                else
                    printer.runPrintUSBJobTask(job.getPrintJobID(), job.getPrintJobPath()).get();
                rootController.showHomePage(printer);
            }
            catch (Exception ex) {
                System.err.println("Caught exception when attempting to print job " + job.getPrintJobID());
            }
        }
    }
}
