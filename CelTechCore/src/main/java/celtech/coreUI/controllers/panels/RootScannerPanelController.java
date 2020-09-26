package celtech.coreUI.controllers.panels;

import celtech.Lookup;
import celtech.WebEngineFix.AMURLStreamHandlerFactory;
import celtech.coreUI.components.RootCameraTableCell;
import celtech.coreUI.components.RootConnectionButtonTableCell;
import celtech.coreUI.components.RootTableCell;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.comms.DetectedServer;
import celtech.roboxbase.comms.DetectedServer.ServerStatus;
import celtech.roboxbase.comms.RemoteServerDetector;
import celtech.roboxbase.configuration.CoreMemory;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * FXML Controller class
 *
 * @author Ian
 */
public class RootScannerPanelController implements Initializable, MenuInnerPanel
{
    
    private static final Stenographer steno = StenographerFactory.getStenographer(RootScannerPanelController.class.getName());
    
    private final RemoteServerDetector remoteServerDetector = RemoteServerDetector.getInstance();
    
    public static String pinForCurrentServer = "";
    
    @FXML
    private TableView<DetectedServer> scannedRoots;
    
    private TableColumn colourColumn;
    private TableColumn nameColumn;
    private TableColumn ipAddressColumn;
    private TableColumn versionColumn;
    private TableColumn<DetectedServer, ServerStatus> statusColumn;
    private TableColumn<DetectedServer, DetectedServer> scannedRootButtonsColumn;
    private TableColumn cameraColumn;
    
    @FXML
    private TextField ipTextField;
    
    @FXML
    private Button addRootButton;
    
    @FXML
    private Button deleteRootButton;
    
    private static final String IPADDRESS_PATTERN
            = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    

    private DetectedServer findKnownServer(String ipAddress)
    {
        return currentServers.stream()
                             .filter(s -> s.getAddress().getHostAddress().equals(ipAddress))
                             .findAny()
                             .orElse(null);
    }
    
    @FXML
    private void manuallyAddRoot(ActionEvent event)
    {
        String enteredIP = ipTextField.getText();
        try
        {
            if (findKnownServer(enteredIP) == null)
            {
                InetAddress address = InetAddress.getByName(enteredIP);
                DetectedServer newServer = DetectedServer.createDetectedServer(address);
                newServer.setWasAutomaticallyAdded(false);
                checkAndAddServer(newServer);
            }
            ipTextField.setText("");
        } catch (UnknownHostException ex)
        {
            steno.error("Bad IP address for manually added Root: " + enteredIP);
        }
    }
    
    @FXML
    private void manuallyDeleteRoot(ActionEvent event)
    {
        String enteredIP = ipTextField.getText();
        DetectedServer matchingServer = findKnownServer(enteredIP);
        if (matchingServer != null)
        {
            matchingServer.disconnect();
            currentServers.remove(matchingServer);
        }
    }
    
    private final ObservableList<DetectedServer> currentServers = FXCollections.observableArrayList();
    
    private void checkAndAddServer(DetectedServer server)
    {
        if (!server.whoAreYou())
        {
            if (server.maxPollCountExceeded())
            {
                CoreMemory.getInstance().deactivateRoboxRoot(server);
                Platform.runLater(() ->
                {
                    currentServers.remove(server);
                });
            }
        } else
        {
            server.connect();
            Platform.runLater(() ->
            {
                currentServers.add(server);
            });
        }
    }

    /**
     * Initialises the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        URL.setURLStreamHandlerFactory(new AMURLStreamHandlerFactory());
        
        colourColumn = new TableColumn<>();
        colourColumn.setPrefWidth(20);
        colourColumn.setResizable(false);
        colourColumn.setCellValueFactory(new PropertyValueFactory<>("colours"));
        colourColumn.setCellFactory(column -> {
            return new TableCell<DetectedServer, List<String>>() {
                
                @Override
                protected void updateItem(List<String> colours, boolean empty) {
                    super.updateItem(colours, empty); //This is mandatory

                    setText(null);
                    
                    if (colours == null || empty || colours.isEmpty()) { //If the cell is empty
                        setStyle("");
                    } else { //If the cell is not empty

                        // For now let's get the first colour
                        Color printerColour = Color.valueOf(colours.get(0));
                        String printerColourFormatted = formatColor(printerColour);
                        setStyle("-fx-background-color: " + printerColourFormatted);
                    }
                }
            };
        });
        
        nameColumn = new TableColumn<>();
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setText(Lookup.i18n("rootScanner.name"));
        nameColumn.setPrefWidth(160);
        nameColumn.setResizable(false);
        nameColumn.setStyle("-fx-alignment: CENTER_LEFT;");
        
        ipAddressColumn = new TableColumn<>();
        ipAddressColumn.setCellValueFactory(new PropertyValueFactory<>("serverIP"));
        ipAddressColumn.setText(Lookup.i18n("rootScanner.ipAddress"));
        ipAddressColumn.setPrefWidth(100);
        ipAddressColumn.setResizable(false);
        ipAddressColumn.setStyle("-fx-alignment: CENTER;");
        
        versionColumn = new TableColumn<>();
        versionColumn.setCellValueFactory(new PropertyValueFactory<>("version"));
        versionColumn.setText(Lookup.i18n("rootScanner.version"));
        versionColumn.setPrefWidth(100);
        versionColumn.setResizable(false);
        versionColumn.setStyle("-fx-alignment: CENTER;");
        
        statusColumn = new TableColumn<>();
        statusColumn.setCellFactory(statusCell -> new RootTableCell());
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("serverStatus"));
        statusColumn.setPrefWidth(40);
        statusColumn.setResizable(false);
        
        scannedRootButtonsColumn = new TableColumn<>();
        scannedRootButtonsColumn.setCellFactory(buttonCell -> new RootConnectionButtonTableCell());
        scannedRootButtonsColumn.setCellValueFactory((CellDataFeatures<DetectedServer, DetectedServer> p) -> new SimpleObjectProperty<>(p.getValue()));
        scannedRootButtonsColumn.setMinWidth(350);
        scannedRootButtonsColumn.setMaxWidth(Integer.MAX_VALUE);
        scannedRootButtonsColumn.setResizable(false);
        
        cameraColumn = new TableColumn<>();
        cameraColumn.setCellFactory(cameraCell -> new RootCameraTableCell());
        cameraColumn.setCellValueFactory(new PropertyValueFactory<>("cameraDetected"));
        cameraColumn.setText(Lookup.i18n("rootScanner.camera"));
        cameraColumn.setPrefWidth(60);
        cameraColumn.setResizable(false);
        
        scannedRoots.getColumns().add(colourColumn);
        scannedRoots.getColumns().add(nameColumn);
        scannedRoots.getColumns().add(ipAddressColumn);
        scannedRoots.getColumns().add(versionColumn);
        scannedRoots.getColumns().add(statusColumn);
        scannedRoots.getColumns().add(scannedRootButtonsColumn);
        scannedRoots.getColumns().add(cameraColumn);
        scannedRoots.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        HBox.setHgrow(scannedRoots, Priority.ALWAYS);
        
        scannedRoots.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        scannedRoots.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DetectedServer>()
        {
            @Override
            public void changed(ObservableValue<? extends DetectedServer> observable, DetectedServer oldValue, DetectedServer newValue)
            {
                if (newValue == null
                        || newValue.getWasAutomaticallyAdded())
                {
                    ipTextField.setText("");
                } else
                {
                    ipTextField.setText(newValue.getServerIP());
                }
            }
        });
        
        scannedRoots.setItems(currentServers);
        
        scannedRoots.setPlaceholder(new Text(BaseLookup.i18n("rootScanner.noRemoteServersFound")));
        
        ipTextField.textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
            {
                String enteredIP = ipTextField.getText();
                if (enteredIP.matches(IPADDRESS_PATTERN))
                {
                    DetectedServer matchingServer = findKnownServer(enteredIP);
                    
                    addRootButton.setDisable(matchingServer != null); // Can't add existing server.
                    deleteRootButton.setDisable(matchingServer == null || matchingServer.getWasAutomaticallyAdded());
                } else
                {
                    addRootButton.setDisable(true);
                    deleteRootButton.setDisable(true);
                }
            }
        });
        
        addRootButton.setDisable(true);
        deleteRootButton.setDisable(true);
        
        currentServers.addListener(new ListChangeListener<DetectedServer>()
        {
            @Override
            public void onChanged(ListChangeListener.Change<? extends DetectedServer> change)
            {
                if (currentServers.size() > 0)
                {
                    scannedRoots.getSelectionModel().selectFirst();
                } else
                {
                    scannedRoots.getSelectionModel().clearSelection();
                }
            }
        });
        

                
        Task<Void> scannerTask = new Task<Void>()
        {
           
            @Override
            protected Void call() throws Exception
            {
                List<DetectedServer> serversToCheck = new ArrayList<>(CoreMemory.getInstance().getActiveRoboxRoots());
                serversToCheck.forEach((server) ->
                {
                    checkAndAddServer(server);
                });
                        
                while (!isCancelled())
                {
                    List<DetectedServer> foundServers = remoteServerDetector.searchForServers();
                    
                    Platform.runLater(() ->
                    {
                        List<DetectedServer> serversToAdd = new ArrayList<>();
                        List<DetectedServer> serversToRemove = new ArrayList<>();
                        
                        for (DetectedServer server : foundServers)
                        {
                            if (!currentServers.contains(server))
                            {
                                serversToAdd.add(server);
                            }
                        }
                        
                        for (DetectedServer server : currentServers)
                        {
                            if (!foundServers.contains(server)
                                    && server.getWasAutomaticallyAdded())
                            {
                                serversToRemove.add(server);
                            }
                        }
                        
                        for (DetectedServer server : serversToAdd)
                        {
                            steno.info("RootScanner adding server \"" + server.getDisplayName() + "\"");
                            currentServers.add(server);
                        }
                        
                        for (DetectedServer server : serversToRemove)
                        {
                            if (server.incrementPollCount())
                            {
                                steno.info("RootScanner removing server \"" + server.getDisplayName() + "\"");
                                currentServers.remove(server);
                                server.disconnect();
                            }
                            else
                            {
                                steno.debug("RootScannerPanelController not removing server \"" + server.getDisplayName() + "\" as it has not exceeded it's maximum allowed poll count." );
                            }
                       }
                    });
                    try
                    {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex)
                    {
                    }
                }
                
                return null;
            }
        };
        
        Thread scannerThread = new Thread(scannerTask);
        scannerThread.setDaemon(true);
        scannerThread.setName("RootScanner");
        scannerThread.start();
    }
    
    @Override
    public String getMenuTitle()
    {
        return "preferences.root";
    }
    
    @Override
    public List<OperationButton> getOperationButtons()
    {
        return null;
    }
    
    // Format color as string for CSS (#rrggbb format, values in hex).
    private String formatColor(Color c) {
        int r = (int) (255 * c.getRed());
        int g = (int) (255 * c.getGreen());
        int b = (int) (255 * c.getBlue());
        return String.format("#%02x%02x%02x", r, g, b);
    }
    
    @Override
    public void panelSelected() {}
}
