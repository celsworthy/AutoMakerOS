package celuk.groot.controllers;

import celuk.groot.remote.RootPrinter;
import celuk.groot.remote.RootServer;
import celuk.groot.remote.ServerStatusResponse;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class RootStackController implements Initializable {

    private static final int MAX_POLL_COUNT = 3;
    private static final String FXML_RESOURCE_PATH = "/fxml/";

    @FXML
    private AnchorPane rootAnchorPane;
    
    private final URL aboutPageURL = getClass().getResource(FXML_RESOURCE_PATH + "About.fxml");
    private final URL accessPINPageURL = getClass().getResource(FXML_RESOURCE_PATH + "AccessPIN.fxml");
    private final URL waitingPageURL = getClass().getResource(FXML_RESOURCE_PATH + "Waiting.fxml");
    private final URL consolePageURL = getClass().getResource(FXML_RESOURCE_PATH + "Console.fxml");
    private final URL controlPageURL = getClass().getResource(FXML_RESOURCE_PATH + "Control.fxml");
    private final URL headParametersPageURL = getClass().getResource(FXML_RESOURCE_PATH + "HeadParameters.fxml");
    private final URL homePageURL = getClass().getResource(FXML_RESOURCE_PATH + "Home.fxml");
    private final URL loginPageURL = getClass().getResource(FXML_RESOURCE_PATH + "Login.fxml");
    private final URL printerColourPageURL = getClass().getResource(FXML_RESOURCE_PATH + "PrinterColour.fxml");
    private final URL namePageURL = getClass().getResource(FXML_RESOURCE_PATH + "Name.fxml");
    private final URL printerSelectPageURL = getClass().getResource(FXML_RESOURCE_PATH + "PrinterSelect.fxml");
    private final URL printPageURL = getClass().getResource(FXML_RESOURCE_PATH + "Print.fxml");
    private final URL purgePageURL = getClass().getResource(FXML_RESOURCE_PATH + "Purge.fxml");
    private final URL purgeIntroPageURL = getClass().getResource(FXML_RESOURCE_PATH + "PurgeIntro.fxml");
    private final URL resetPINPageURL = getClass().getResource(FXML_RESOURCE_PATH + "ResetPIN.fxml");
    private final URL tweakPageURL = getClass().getResource(FXML_RESOURCE_PATH + "Tweak.fxml");
    private final URL wirelessPageURL = getClass().getResource(FXML_RESOURCE_PATH + "Wireless.fxml");
    private final URL mainMenuURL = getClass().getResource(FXML_RESOURCE_PATH + "MainMenu.fxml");
    private final URL menuURL = getClass().getResource(FXML_RESOURCE_PATH + "Menu.fxml");

    private AboutController aboutPage = null;
    private AccessPINController accessPINPage = null;
    private WaitingController waitingPage = null;
    private ConsoleController consolePage = null;
    private ControlController controlPage = null;
    private HeadParametersController headParametersPage = null;
    private HomeController homePage = null;
    private LoginController loginPage = null;
    private PrinterColourController printerColourPage = null;
    private PrinterNameController printerNamePage = null;
    private PrinterSelectController printerSelectPage = null;
    private PrintController printPage = null;
    private PurgeController purgePage = null;
    private PurgeIntroController purgeIntroPage = null;
    private ResetPINController resetPINPage = null;
    private ServerNameController serverNamePage = null;
    private TweakController tweakPage = null;
    private WirelessController wirelessPage = null;
    private SecurityMenuController securityMenu = null;
    private IdentityMenuController identityMenu = null;
    private MainMenuController mainMenu = null;
    private MaintenanceMenuController maintenanceMenu = null;
    private PrintMenuController printMenu = null;
    private SettingsMenuController settingsMenu = null;
    private ServerSettingsMenuController serverSettingsMenu = null;
    private EjectStuckMenuController ejectStuckMenu = null;
    private CleanNozzlesMenuController cleanNozzlesMenu = null;
    private TestAxisSpeedMenuController testAxisSpeedMenu = null;
    private final List<Page> pages = new ArrayList<>();

    private RootServer server = null;
    private Insets offsets = new Insets(0.0, 0.0, 0.0, 0.0);
    private ErrorAlertController errorManager = null;
    private RootPrinter currentPrinter = null;
    private boolean isUpgrading = false;
    private int pollCount = 0;
    
    private ChangeListener<Boolean> serverStatusHeartbeatListener = (ob, ov, nv) -> {
        //System.out.println("RootStackController::ServeStatusListener()");

        // isUpgrading remains true when contact is lost (i.e. status is null)
        // so the page keeps showing "Upgrading" when contact is lost.
        ServerStatusResponse status = server.getCurrentStatusProperty().get();
        if (status != null) {
            pollCount = 0;
            isUpgrading = (status.getUpgradeStatus() != null &&
                           status.getUpgradeStatus().equalsIgnoreCase("upgrading"));
        }
        else if (pollCount <= MAX_POLL_COUNT)
            ++pollCount;
        if (pollCount > MAX_POLL_COUNT || isUpgrading) {
            Platform.runLater(() -> {
                currentPrinter = null;
                hidePages(waitingPage);
                showWaitingPage(null, isUpgrading);
            });
        }
    };

    private ChangeListener<Boolean> printerMapHeartbeatListener = (pr, ov, nv) ->  {
        RootPrinter p = currentPrinter;
        if (p != null && !server.getCurrentPrinterMap().containsKey(p.getPrinterId())) {
            Platform.runLater(() -> {
                currentPrinter = null;
                hidePages(printerSelectPage);
                printerSelectPage.displayPage(null);
            });
        }
    };
    
    private final ChangeListener<Boolean> authorisedListener = (op, ov, nv) ->  {
        // This should be invoked if an HTTP request fails because of lack of authorisation.
        if (nv == false) {
            Platform.runLater(() -> {
                hidePages(loginPage);
                loginPage.displayPage(currentPrinter);
            });            
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Most pages are loaded when first shown. The following commonly used
        // pages are loaded immediately.
        consolePage = (ConsoleController)(loadPage(consolePageURL, null));
        controlPage = (ControlController)(loadPage(controlPageURL, null));
        homePage = (HomeController)(loadPage(homePageURL, null));
        loginPage = (LoginController)(loadPage(loginPageURL, null));
        printerSelectPage = (PrinterSelectController)(loadPage(printerSelectPageURL, null));
        waitingPage = (WaitingController)(loadPage(waitingPageURL, null));
        
        // Menus - all but the main menu use the same FXML page.
        mainMenu = (MainMenuController)(loadPage(mainMenuURL, null));

        server.getCurrentPrinterMapHeartbeatProperty().addListener(printerMapHeartbeatListener);
        server.getAuthorisedProperty().addListener(authorisedListener);
        server.getCurrentStatusHeartbeatProperty().addListener(serverStatusHeartbeatListener);
        errorManager = new ErrorAlertController(server);
        errorManager.prepareDialog();

        hidePages(waitingPage);
        waitingPage.setAsConnecting();
        waitingPage.displayPage(null);
    }
    
    public void showAboutPage(RootPrinter printer) {
        Platform.runLater(() -> {
            if (aboutPage == null) {
                aboutPage = (AboutController)(loadPage(aboutPageURL, null));
            }
            hidePages(aboutPage);
            aboutPage.displayPage(printer);
        });
    }

    public void showAccessPINPage(RootPrinter printer) {
        Platform.runLater(() -> {
            if (accessPINPage == null) {
                accessPINPage = (AccessPINController)(loadPage(accessPINPageURL, null));
            }
            hidePages(accessPINPage);
            accessPINPage.displayPage(printer);
        });
    }

    public void showWaitingPage(RootPrinter printer, boolean isUpgrading) {
        Platform.runLater(() -> {
            if (waitingPage == null) {
                waitingPage = (WaitingController)(loadPage(waitingPageURL, null));
            }
            hidePages(waitingPage);
            if (isUpgrading)
                waitingPage.setAsUpgrading();
            else
                waitingPage.setAsConnecting();
            waitingPage.displayPage(printer);
        });
    }

    public void showConsolePage(RootPrinter printer, boolean returnToControl) {
        Platform.runLater(() -> {
            if (consolePage == null) {
                consolePage = (ConsoleController)(loadPage(consolePageURL, null));
            }
            hidePages(consolePage);
            consolePage.setReturnToControl(returnToControl);
            consolePage.displayPage(printer);
        });
    }

    public void showControlPage(RootPrinter printer) {
        Platform.runLater(() -> {
            if (controlPage == null) {
                controlPage = (ControlController)(loadPage(controlPageURL, null));
            }
            hidePages(controlPage);
            controlPage.displayPage(printer);
        });
    }

    public void showHeadParametersPage(RootPrinter printer) {
        Platform.runLater(() -> {
            if (headParametersPage == null) {
                headParametersPage = (HeadParametersController)(loadPage(headParametersPageURL, null));
            }
            hidePages(headParametersPage);
            headParametersPage.displayPage(printer);
        });
    }

    public void showHomePage(RootPrinter printer) {
        Platform.runLater(() -> {
            if (homePage == null) {
                homePage = (HomeController)(loadPage(homePageURL, null));
            }
            hidePages(homePage);
            currentPrinter = printer;
            homePage.displayPage(printer);
        });
    }
    
    public void showLoginPage(RootPrinter printer) {
        Platform.runLater(() -> {
            if (loginPage == null) {
                loginPage = (LoginController)(loadPage(loginPageURL, null));
            }
            hidePages(loginPage);
            loginPage.displayPage(printer);
        });
    }

    public void showPrinterColourPage(RootPrinter printer) {
        Platform.runLater(() -> {
            if (printerColourPage == null) {
                printerColourPage = (PrinterColourController)(loadPage(printerColourPageURL, null));
            }
            hidePages(printerColourPage);
            printerColourPage.displayPage(printer);
        });
    }
    
    public void showPrinterNamePage(RootPrinter printer) {
        Platform.runLater(() -> {
            if (printerNamePage == null) {
                printerNamePage = (PrinterNameController)(loadPage(namePageURL, PrinterNameController.class));
            }
            hidePages(printerNamePage);
            printerNamePage.displayPage(printer);
        });
    }
    
    public void showPrinterSelectPage() {
        Platform.runLater(() -> {
            if (printerSelectPage == null) {
                printerSelectPage = (PrinterSelectController)(loadPage(printerSelectPageURL, null));
            }
            hidePages(printerSelectPage);
            currentPrinter = null;
            printerSelectPage.displayPage(null);
        });
    }
    
    public void showReprintPage(RootPrinter printer) {
        Platform.runLater(() -> {
            if (printPage == null) {
                printPage = (PrintController)(loadPage(printPageURL, null));
            }
            hidePages(printPage);
            printPage.setReprintMode(true);
            printPage.displayPage(printer);
        });
    }

    public void showResetPINPage(RootPrinter printer) {
        Platform.runLater(() -> {
            if (resetPINPage == null) {
                resetPINPage = (ResetPINController)(loadPage(resetPINPageURL, null));
            }
            hidePages(resetPINPage);
            resetPINPage.displayPage(printer);
        });
    }
    
    public void showPurgePage(RootPrinter printer) {
        Platform.runLater(() -> {
            if (purgePage == null) {
                purgePage = (PurgeController)(loadPage(purgePageURL, null));
            }
            hidePages(purgePage);
            purgePage.displayPage(printer);
        });
    }

    public void showPurgeIntroPage(RootPrinter printer) {
        Platform.runLater(() -> {
            if (purgeIntroPage == null) {
                purgeIntroPage = (PurgeIntroController)(loadPage(purgeIntroPageURL, null));
            }
            hidePages(purgePage);
            purgeIntroPage.displayPage(printer);
        });
    }

    public void showRemoveHeadPage(RootPrinter printer) {
        Platform.runLater(() -> {
            //if (removeHeadPage == null) {
            //    removeHeadPage = (RemoveHeadController)(loadPage(removeHeadPageURL, null));
            //}
            // hidePages(removeHeadPage);
            //removeHeadPage.displayPage(printer);
        });
    }

    public void showServerNamePage() {
        Platform.runLater(() -> {
            if (serverNamePage == null) {
                serverNamePage = (ServerNameController)(loadPage(namePageURL, ServerNameController.class));
            }
            hidePages(serverNamePage);
            serverNamePage.displayPage(null);
        });
    }
    
    public void showTweakPage(RootPrinter printer) {
        Platform.runLater(() -> {
            if (tweakPage == null) {
                tweakPage = (TweakController)(loadPage(tweakPageURL, null));
            }
            hidePages(tweakPage);
            tweakPage.displayPage(printer);
        });
    }

    public void showWirelessPage(RootPrinter printer) {
        Platform.runLater(() -> {
            if (wirelessPage == null) {
                wirelessPage = (WirelessController)(loadPage(wirelessPageURL, null));
            }
            hidePages(wirelessPage);
            wirelessPage.displayPage(printer);
        });
    }

    public void showUSBPrintPage(RootPrinter printer) {
        Platform.runLater(() -> {
            if (printPage == null) {
                printPage = (PrintController)(loadPage(printPageURL, null));
            }
            hidePages(printPage);
            printPage.setReprintMode(false);
            printPage.displayPage(printer);
        });
    }

    public void showCleanNozzlesMenu(RootPrinter printer) {
        Platform.runLater(() -> {
            if (cleanNozzlesMenu == null) {
                cleanNozzlesMenu = (CleanNozzlesMenuController)(loadPage(menuURL, CleanNozzlesMenuController.class));
            }
            hidePages(cleanNozzlesMenu);
            cleanNozzlesMenu.displayPage(printer);
        });
    }

    public void showEjectStuckMenu(RootPrinter printer) {
        Platform.runLater(() -> {
            if (ejectStuckMenu == null) {
                ejectStuckMenu = (EjectStuckMenuController)(loadPage(menuURL, EjectStuckMenuController.class));
            }
            hidePages(ejectStuckMenu);
            ejectStuckMenu.displayPage(printer);
        });
    }

    public void showIdentityMenu(RootPrinter printer) {
        Platform.runLater(() -> {
            if (identityMenu == null) {
                identityMenu = (IdentityMenuController)(loadPage(menuURL, IdentityMenuController.class));
            }
            hidePages(identityMenu);
            identityMenu.displayPage(printer);
        });
    }

    public void showMainMenu(RootPrinter printer) {
        Platform.runLater(() -> {
            if (mainMenu == null) {
                mainMenu = (MainMenuController)(loadPage(mainMenuURL, null));
            }
            hidePages(mainMenu);
            mainMenu.displayPage(printer);
        });
    }

    public void showMaintenanceMenu(RootPrinter printer) {
        Platform.runLater(() -> {
            if (maintenanceMenu == null) {
                maintenanceMenu = (MaintenanceMenuController)(loadPage(menuURL, MaintenanceMenuController.class));
            }
            hidePages(maintenanceMenu);
            maintenanceMenu.displayPage(printer);
        });
    }

    public void showPrintMenu(RootPrinter printer) {
        Platform.runLater(() -> {
            if (printMenu == null) {
                printMenu = (PrintMenuController)(loadPage(menuURL, PrintMenuController.class));
            }
            hidePages(printMenu);
            printMenu.displayPage(printer);
        });
    }

    public void showSecurityMenu(RootPrinter printer) {
        Platform.runLater(() -> {
            if (securityMenu == null) {
                securityMenu = (SecurityMenuController)(loadPage(menuURL, SecurityMenuController.class));
            }
            hidePages(securityMenu);
            securityMenu.displayPage(printer);
        });
    }

    public void showSettingsMenu(RootPrinter printer) {
        Platform.runLater(() -> {
            if (settingsMenu == null) {
                settingsMenu = (SettingsMenuController)(loadPage(menuURL, SettingsMenuController.class));
            }
            hidePages(settingsMenu);
            settingsMenu.displayPage(printer);
        });
    }
    
    public void showServerSettingsMenu() {
        Platform.runLater(() -> {
            if (serverSettingsMenu == null) {
                serverSettingsMenu = (ServerSettingsMenuController)(loadPage(menuURL, ServerSettingsMenuController.class));
            }
            hidePages(serverSettingsMenu);
            serverSettingsMenu.displayPage(null);
        });
    }

    public void showTestAxisSpeedMenu(RootPrinter printer) {
        Platform.runLater(() -> {
            if (testAxisSpeedMenu == null) {
                testAxisSpeedMenu = (TestAxisSpeedMenuController)(loadPage(menuURL, TestAxisSpeedMenuController.class));
            }
            hidePages(testAxisSpeedMenu);
            testAxisSpeedMenu.displayPage(printer);
        });
    }

    public void setOffsets(double top, double right, double bottom, double left) {
        offsets = new Insets(top, right, bottom, left);
    }
    
    public RootServer getRootServer() {
        return server;
    }

    public void setRootServer(RootServer server) {
        this.server = server;
    }

    public void stop() {
        pages.forEach(Page::stopUpdates);
    }
        
    private void hidePages(Page pageToShow) {
        pages.stream()
             .filter(p -> p != pageToShow && p.isVisible())
             .forEach(p -> p.hidePage());
    }
    
    private <T extends Page> T loadPage(URL pageURL, Class<T> controller) {
        T page = null;
        try {
            FXMLLoader pageLoader =  new FXMLLoader(pageURL, null);
            if (controller != null) {
                
                try {
                    page = controller.getDeclaredConstructor().newInstance();
                }
                catch (Exception ex) {
                    System.err.println("Exception thown when instantiating page controller");
                }
                pageLoader.setController(page);
            }
            StackPane pagePane = pageLoader.load();
            setAnchors(pagePane);
            if (page == null)
                page = pageLoader.getController();

            page.setRootStackController(this);
            pages.add(page);
            rootAnchorPane.getChildren().add(pagePane);
        }
        catch (IOException ex) {
            System.err.println(ex);
            ex.printStackTrace(System.err);
            System.exit(1);
        }
        return page;
    }
    
    private void setAnchors(Node n) {
        AnchorPane.setBottomAnchor(n, offsets.getBottom());
        AnchorPane.setTopAnchor(n, offsets.getTop());
        AnchorPane.setLeftAnchor(n, offsets.getLeft());
        AnchorPane.setRightAnchor(n, offsets.getRight());
    }
}
