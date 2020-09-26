package celuk.groot.controllers;

import celuk.groot.remote.PrinterStatusResponse;
import celuk.groot.remote.RootPrinter;
import celuk.groot.remote.RootServer;
import celuk.groot.remote.ServerStatusResponse;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.MapChangeListener;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class PrinterSelectController implements Initializable, Page {
    private static class PrinterPanel {
        public GridPane panelPane;
        public ImageView errorIV;
        public ImageView statusIV;
        public ProgressBar progress;
        public Label nameLabel;
        public RootPrinter printer;
        public String printerColour;
        public String armedColour;
        public String statusIcon;
        public boolean useDark;
        public boolean armed;
        
        public PrinterPanel(GridPane panelPane,
                            ImageView errorIV,
                            ImageView statusIV,
                            ProgressBar progress,
                            Label nameLabel) {
            this.panelPane = panelPane;
            this.errorIV = errorIV;
            this.statusIV = statusIV;
            this.progress = progress;
            this.nameLabel = nameLabel;
            clearPrinterData();
        }
        
        public final void clearPrinterData() {
            printer = null;
            printerColour = null;
            armedColour = null;
            statusIcon = null;
            useDark = false;
        }

        public final void reset() {
            clearPrinterData();
            panelPane.setVisible(false);
            panelPane.setStyle("");
            panelPane.pseudoClassStateChanged(DARK_PS, false);
            errorIV.pseudoClassStateChanged(DARK_PS, false);
            errorIV.setVisible(false);
            statusIV.pseudoClassStateChanged(DARK_PS, false);
            statusIV.pseudoClassStateChanged(PAUSED_PS, false);
            statusIV.pseudoClassStateChanged(PRINTING_PS, false);
            progress.setVisible(false);
            progress.setProgress(0.0);
            progress.pseudoClassStateChanged(DARK_PS, false);
            nameLabel.setText("-");
            armed = false;
        }
        
        public void setPanelColour() {
            panelPane.setStyle("-fx-background-color: "
                               + (armed ? armedColour : printerColour)
                               +";"
                               + "-fx-background-image: url(\""
                               + statusIcon
                               + "\");");
        }
    }
    
    @FXML
    private StackPane printerSelectPane;
    @FXML
    private VBox printerVBox;
    @FXML
    private GridPane printerGrid;
//    @FXML
//    private Button printer00Button;
    @FXML
    private GridPane printer00Pane;
    @FXML
    private ImageView printer00Error;
    @FXML
    private ImageView printer00Status;
    @FXML
    private ProgressBar printer00Progress;
    @FXML
    private Label printer00Name;
    @FXML
    private GridPane printer01Pane;
    @FXML
    private ImageView printer01Error;
    @FXML
    private ImageView printer01Status;
    @FXML
    private ProgressBar printer01Progress;
    @FXML
    private Label printer01Name;
    @FXML
    private GridPane printer10Pane;
    @FXML
    private ImageView printer10Error;
    @FXML
    private ImageView printer10Status;
    @FXML
    private ProgressBar printer10Progress;
    @FXML
    private Label printer10Name;
    @FXML
    private GridPane printer11Pane;
    @FXML
    private ImageView printer11Error;
    @FXML
    private ImageView printer11Status;
    @FXML
    private ProgressBar printer11Progress;
    @FXML
    private Label printer11Name;
    @FXML
    private GridPane printer20Pane;
    @FXML
    private ImageView printer20Error;
    @FXML
    private ImageView printer20Status;
    @FXML
    private ProgressBar printer20Progress;
    @FXML
    private Label printer20Name;
    @FXML
    private GridPane printer21Pane;
    @FXML
    private ImageView printer21Error;
    @FXML
    private ImageView printer21Status;
    @FXML
    private ProgressBar printer21Progress;
    @FXML
    private Label printer21Name;
    @FXML
    private VBox noPrintersVBox;
    @FXML
    private Label noPrintersLabel;
    @FXML
    private Label noPrintersDetailLabel;
    @FXML
    private HBox bottomBarHBox;
    @FXML
    private VBox rootIDVBox;
    @FXML
    private Label rootNameLabel;
    @FXML
    private Label rootAddressLabel;
    @FXML
    private Button serverSettingsButton;
        
    @FXML
    void selectPrinterAction(MouseEvent event)
    {
        if (rootController != null)
        {
            Node b = (Node)event.getSource();
            PrinterPanel pp = (PrinterPanel)b.getUserData();
            if (pp != null && pp.printer != null)
                rootController.showHomePage(pp.printer);
        }
    }
    
    @FXML
    void armAction(MouseEvent event)
    {
        if (rootController != null)
        {
            Node b = (Node)event.getSource();
            PrinterPanel pp = (PrinterPanel)b.getUserData();
            if (pp != null) {
                pp.armed = true;
                pp.setPanelColour();
            }
        }
    }
    
    @FXML
    void disarmAction(MouseEvent event)
    {
        if (rootController != null)
        {
            Node b = (Node)event.getSource();
            PrinterPanel pp = (PrinterPanel)b.getUserData();
            if (pp != null) {
                pp.armed = false;
                pp.setPanelColour();
            }
        }
    }
    
    @FXML
    void serverSettingsAction(ActionEvent event)
    {
        if (rootController != null && event.getSource() instanceof Button)
        {
            rootController.showServerSettingsMenu();
        }
    }
    
    private RootStackController rootController = null;
    private RootServer rootServer = null;
    private PrinterPanel[] panelArray = null;
    private final static PseudoClass DARK_PS = PseudoClass.getPseudoClass("dark");
    private final static PseudoClass PAUSED_PS = PseudoClass.getPseudoClass("paused");
    private final static PseudoClass PRINTING_PS = PseudoClass.getPseudoClass("printing");
            
    @Override
    public void setRootStackController(RootStackController rootController) {
        this.rootController = rootController;
        this.rootServer = rootController.getRootServer();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        translateLabels(noPrintersLabel, noPrintersDetailLabel);
        printerGrid.setManaged(false);
        printerGrid.setVisible(false);
        noPrintersVBox.setManaged(true);
        noPrintersVBox.setVisible(true);
        panelArray = new PrinterPanel[]
            {
                new PrinterPanel(printer00Pane, printer00Error, printer00Status, printer00Progress, printer00Name), 
                new PrinterPanel(printer01Pane, printer01Error, printer01Status, printer01Progress, printer01Name), 
                new PrinterPanel(printer10Pane, printer10Error, printer10Status, printer10Progress, printer10Name), 
                new PrinterPanel(printer11Pane, printer11Error, printer11Status, printer11Progress, printer11Name), 
                new PrinterPanel(printer20Pane, printer20Error, printer20Status, printer20Progress, printer20Name), 
                new PrinterPanel(printer21Pane, printer21Error, printer21Status, printer21Progress, printer21Name)
            };
        printer00Pane.setUserData(panelArray[0]);
        printer01Pane.setUserData(panelArray[1]);
        printer10Pane.setUserData(panelArray[2]);
        printer11Pane.setUserData(panelArray[3]);
        printer20Pane.setUserData(panelArray[4]);
        printer21Pane.setUserData(panelArray[5]);
        for (PrinterPanel pp : panelArray)
            pp.reset();

        printerSelectPane.setVisible(false);
    }
    
    private ChangeListener<ServerStatusResponse> serverStatusListener = (ob, ov, nv) -> {
        //System.out.println("PrinterSelectController::serverStatusListener");
        updateServerStatus(nv);
    };
    
    private ChangeListener<Boolean> printerMapHeartbeatListener = (pr, ov, nv) ->  {
        //System.out.println("PrinterSelectController::printerMapHeartbeatListener");
        updatePrinterGrid();
    };
    
    private ChangeListener<PrinterStatusResponse> printerStatusListener = (ob, ov, nv) -> {
        //System.out.println("PrinterSelectController::printerStatusListener");
        updatePrinterStatus(nv);
    };

    @Override
    public void startUpdates() {
        rootServer.getCurrentStatusProperty().addListener(serverStatusListener);
        rootServer.getCurrentPrinterMapHeartbeatProperty().addListener(printerMapHeartbeatListener);
        updateServerStatus(rootServer.getCurrentStatusProperty().get());
        updatePrinterGrid();
    }
    
    @Override
    public void stopUpdates() {
        rootServer.getCurrentStatusProperty().removeListener(serverStatusListener);
        rootServer.getCurrentPrinterMapHeartbeatProperty().removeListener(printerMapHeartbeatListener);
        clearPrinterGrid();
    }

    @Override
    public void displayPage(RootPrinter printer) {
        if (!printerSelectPane.isVisible()) {
            startUpdates();
            printerSelectPane.setVisible(true);
        }
    }
    
    @Override
    public void hidePage() {
        stopUpdates();
        printerSelectPane.setVisible(false);
    }
    
    @Override
    public boolean isVisible() {
        return printerSelectPane.isVisible();
    }
    
    private void updateServerStatus(ServerStatusResponse response) {
        Platform.runLater(() -> {
            if (response != null) {
                rootNameLabel.setText(response.getName());
                rootAddressLabel.setText(response.getServerIP());
            }
            else {
                rootNameLabel.setText("*");
                rootAddressLabel.setText("---.---.---.---");
            }
        });
    }

    private void clearPrinterGrid() {
        //System.out.println("RemoteServer::clearPrinterGrid");
        Platform.runLater(() -> {
            // Remove the lost printers.
            for (int index = 0; index < 6; ++index) {
                PrinterPanel pp = panelArray[index];
                RootPrinter printer = pp.printer;
                if (printer != null)
                {
                    printer.getCurrentStatusProperty().removeListener(printerStatusListener);
                    pp.reset();
                }
            }
        });
    }

    private void updatePrinterGrid() {
        //System.out.println("RemoteServer::updatePrinterGrid");
        Map<String, RootPrinter> currentPrinterMap = rootServer.getCurrentPrinterMap();
        Platform.runLater(() -> {
               
            // Remove the lost printers.
            for (int index = 0; index < 6; ++index) {
                PrinterPanel pp = panelArray[index];
                RootPrinter printer = pp.printer;
                if (printer != null && !currentPrinterMap.containsKey(printer.getPrinterId()))
                {
                    printer.getCurrentStatusProperty().removeListener(printerStatusListener);
                    pp.reset();
                }
            }
            
            if (currentPrinterMap.isEmpty()) {
                printerGrid.setManaged(false);
                printerGrid.setVisible(false);
                noPrintersVBox.setManaged(true);
                noPrintersVBox.setVisible(true);
            }
            else {
                printerGrid.setManaged(true);
                printerGrid.setVisible(true);
                noPrintersVBox.setManaged(false);
                noPrintersVBox.setVisible(false);

                List<RootPrinter> printerList = currentPrinterMap.entrySet()
                                                                 .stream()
                                                                 .map(e -> e.getValue())
                                                                 .collect(Collectors.toList());
                if (printerList.size() == 1) {
                    // Only one printer connected so go straight to the printer home page.
                    rootController.showHomePage(printerList.get(0));
                }
                else {
                    Collections.sort(printerList,
                                     (p1, p2) -> p1.getCurrentStatusProperty()
                                                   .get()
                                                   .getPrinterName()
                                                   .compareToIgnoreCase(p2.getCurrentStatusProperty()
                                                                          .get()
                                                                          .getPrinterName()));
                    int pIndex = 0;
                    for (int index = 0; index < 6; ++index) {
                        PrinterPanel pp = panelArray[index];
                        if (pIndex < printerList.size()) {
                            RootPrinter printer = printerList.get(pIndex);
                            pp.printer = printer;
                            printer.getCurrentStatusProperty().addListener(printerStatusListener);
                            updatePrinterStatus(printer.getCurrentStatusProperty().get());
                            pp.panelPane.setVisible(true);
                            ++pIndex;
                        }
                        else {
                            pp.reset();
                        }
                    }
                }
            }
        });
    }
    
    private void updatePrinterStatus(PrinterStatusResponse printerStatus) {
        for (int index = 0; index < 6; ++index) {
            PrinterPanel pp = panelArray[index];
            RootPrinter p = pp.printer;
            if (p != null && printerStatus != null && p.getPrinterId().equalsIgnoreCase(printerStatus.getPrinterID())) {
                pp.printerColour = printerStatus.getPrinterWebColourString();
                MachineDetails md = MachineDetails.getDetails(printerStatus.getPrinterTypeCode());
                pp.statusIcon = md.getStatusIcon(pp.printerColour, MachineDetails.OPACITY.PC20);
                pp.useDark = MachineDetails.getComplimentaryOption(pp.printerColour, true, false);
                Color pc = Color.web(pp.printerColour);
                // Derive a darker or lighter colour to show when the printer panel is armed.
                // It seems that sometimes darker()/brighter() may not do anything. If this
                // happens, then we try the alternative.
                Color ac;
                if (pp.useDark) {
                    ac = pc.darker().darker();
                    if (ac.equals(pc))
                        ac = pc.brighter().brighter();
                }
                else {
                    ac = pc.brighter().brighter();
                    if (ac.equals(pc))
                        ac = pc.darker().darker();
                }

                pp.armedColour = String.format("#%1$02X%2$02X%3$02X",
                                               Math.round(255.0 * ac.getRed()),
                                               Math.round(255.0 * ac.getGreen()),
                                               Math.round(255.0 * ac.getBlue()));

                boolean idleState = printerStatus.getPrinterStatusEnumValue().startsWith("IDLE");
                boolean pausedState = printerStatus.getPrinterStatusEnumValue().startsWith("PAUSE");
                boolean printingState = !idleState && !pausedState;
                boolean errorState = p.getHasActiveErrorProperty().get();
                // System.out.println("Printer \"" + printerName + "\" status = \"" + printerStatus.getPrinterStatusEnumValue() + "\".");
                
                double progress = -1.0;
                if (!idleState && printerStatus.getTotalDurationSeconds() > 0) {
                    double timeElapsed = printerStatus.getTotalDurationSeconds() - printerStatus.getEtcSeconds();
                    if (timeElapsed < 0.0)
                        timeElapsed = 0.0;
                    progress = ((double)timeElapsed / printerStatus.getTotalDurationSeconds());
                }
                
                pp.nameLabel.setText(printerStatus.getPrinterName());
                pp.setPanelColour();
                pp.panelPane.pseudoClassStateChanged(DARK_PS, pp.useDark);
                pp.progress.pseudoClassStateChanged(DARK_PS, pp.useDark);
                if (progress >= 0.0) {
                    pp.progress.setProgress(progress);
                    pp.progress.setVisible(true);
                }
                else {
                    pp.progress.setProgress(0.0);
                    pp.progress.setVisible(false);
                }

                pp.errorIV.pseudoClassStateChanged(DARK_PS, pp.useDark);
                pp.errorIV.setVisible(errorState);

                pp.statusIV.pseudoClassStateChanged(DARK_PS, pp.useDark);
                pp.statusIV.pseudoClassStateChanged(PAUSED_PS, pausedState);
                pp.statusIV.pseudoClassStateChanged(PRINTING_PS, printingState);
                break;
            }
        }
    }
}
