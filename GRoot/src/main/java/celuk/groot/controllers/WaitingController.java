package celuk.groot.controllers;

import celuk.groot.remote.RootPrinter;
import celuk.groot.remote.ServerStatusResponse;
import celuk.language.I18n;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class WaitingController implements Initializable, Page {

    @FXML
    private StackPane waitingPane;
    
    @FXML
    private Label statusLabel;
    
    private final String connectingMessage = I18n.t("waiting.connecting");
    private final String upgradingMessage = I18n.t("waiting.upgrading");

    private ChangeListener<ServerStatusResponse> serverStatusListener = (ob, ov, nv) -> {
        //System.out.println("WaitingController::serverStatusListener()");
        if (nv != null &&
            (nv.getUpgradeStatus() == null ||
            !nv.getUpgradeStatus().equalsIgnoreCase("upgrading")))
            acknowledgeConnected();
    };

    private RootStackController rootController = null;
    
    @Override
    public void setRootStackController(RootStackController rootController) {
        this.rootController = rootController;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        waitingPane.setVisible(false);
        statusLabel.setText(I18n.t(connectingMessage));
    }
    
    @Override
    public void startUpdates() {
        //System.out.println("WaitingController::startUpdates()");
        rootController.getRootServer().getCurrentStatusProperty().addListener(serverStatusListener);
    }
    
    @Override
    public void stopUpdates() {
        //System.out.println("WaitingController::stopUpdates()");
        rootController.getRootServer().getCurrentStatusProperty().removeListener(serverStatusListener);
    }

    @Override
    public void displayPage(RootPrinter printer) {
        //System.out.println("WaitingController::displayPage()");
        if (!waitingPane.isVisible()) {
            startUpdates();
            waitingPane.setVisible(true);
        }
    }

    @Override
    public void hidePage() {
        //System.out.println("WaitingController::hidePage()");
        stopUpdates();
        waitingPane.setVisible(false);
    }

    @Override
    public boolean isVisible() {
        return waitingPane.isVisible();
    }

    public void acknowledgeConnected() {
        rootController.showPrinterSelectPage();
    }

    public void setAsConnecting() {
        statusLabel.setText(connectingMessage);
    }

    public void setAsUpgrading() {
        statusLabel.setText(upgradingMessage);
    }
}
