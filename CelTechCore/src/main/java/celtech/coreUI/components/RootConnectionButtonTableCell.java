package celtech.coreUI.components;

import celtech.Lookup;
import celtech.coreUI.components.Notifications.GenericProgressBar;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.appManager.NotificationType;
import celtech.roboxbase.comms.DetectedServer;
import celtech.roboxbase.comms.DetectedServer.ServerStatus;
import celtech.roboxbase.comms.remote.Configuration;
import celtech.roboxbase.configuration.ApplicationVersion;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.utils.SystemUtils;
import celtech.utils.TaskWithProgessCallback;
import celtech.utils.WebUtil;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Ian
 */
public class RootConnectionButtonTableCell extends TableCell<DetectedServer, DetectedServer>
{
    private static final String ROOT_UPGRADE_FILE_PREFIX = "RootARM-32bit-";

    private static  FutureTask<Optional<File>> rootDownloadFuture = null;
    private static ExecutorService rootDownloadExecutor = Executors.newFixedThreadPool(1);
    private static GenericProgressBar rootSoftwareDownloadProgress;

    private GenericProgressBar rootSoftwareUploadProgress;

    private BooleanProperty inhibitUpdate = new SimpleBooleanProperty(false);
    
    private boolean userEnteredPin = false;
    
    @FXML
    private HBox connectedBox;

    @FXML
    private HBox disconnectedBox;

    @FXML
    private TextField pinEntryField;

    @FXML
    private Button updateButton;
    
    @FXML
    private Button downgradeButton;

    @FXML
    void connectToServer(ActionEvent event)
    {
        if (associatedServer != null)
        {
            userEnteredPin = true;
            associatedServer.setPin(pinEntryField.getText());
            associatedServer.connect();
        }
    }

    @FXML
    void disconnectFromServer(ActionEvent event)
    {
        if (associatedServer != null)
        {
            associatedServer.disconnect();
        }
    }

    @FXML
    void deleteServer(ActionEvent event)
    {
        if (associatedServer != null)
        {
            associatedServer.disconnect();
        }
    }
    
    @FXML
    void onPinKeyPressed(KeyEvent event)
    {
        if(event.getCode().equals(KeyCode.ENTER))
        {
           connectToServer(null);
        }
    }

    private static void tidyRootFiles(String path, String filename)
    {
        try
        {
            Files.newDirectoryStream(Paths.get(path), (p) ->
                                                      {
                                                          String f = p.getFileName().toString();
                                                          return Files.isRegularFile(p) && !f.equals(filename) && f.startsWith(ROOT_UPGRADE_FILE_PREFIX);
                                                      })
                 .forEach(rootARMPath -> 
                          { 
                              try
                              {
                                  Files.deleteIfExists(rootARMPath);
                              }
                              catch (IOException ex)
                              {
                              }
                          });
        }
        catch (IOException ex)
        {
        }
    }
    
    private void upgradeRootWithFile(String path, String filename)
    {
        if (associatedServer != null)
        {
            TaskWithProgessCallback<Boolean> rootUploader = new TaskWithProgessCallback<Boolean>()
            {
                @Override
                protected Boolean call() throws Exception
                {
                    inhibitUpdate.set(true);
                    Optional<File> rootFileOptional = getRootDownloadFuture(path, filename).get();
                    if (rootFileOptional.isPresent())
                    {
                        BaseLookup.getTaskExecutor().runOnGUIThread(() ->
                        {
                            rootSoftwareUploadProgress = Lookup.getProgressDisplay().addGenericProgressBarToDisplay(Lookup.i18n("rootScanner.rootUploadTitle"),
                                runningProperty(),
                                progressProperty());
                        });

                        return associatedServer.upgradeRootSoftware(path, filename, this);
                    }
                    else
                    {
                        inhibitUpdate.set(false);
                        return false;
                    }
                }

                @Override
                public void updateProgressPercent(double percentProgress)
                {
                    updateProgress(percentProgress, 100.0);
                }
            };

            rootUploader.setOnScheduled((event) ->
            {
                inhibitUpdate.set(true);
            });

            rootUploader.setOnFailed((event) ->
            {
                BaseLookup.getSystemNotificationHandler().showErrorNotification(Lookup.i18n("rootScanner.rootUploadTitle"), Lookup.i18n("rootScanner.failedUploadMessage"));
                Lookup.getProgressDisplay().removeGenericProgressBarFromDisplay(rootSoftwareUploadProgress);
                rootSoftwareUploadProgress = null;
                inhibitUpdate.set(false);
            });

            rootUploader.setOnSucceeded((event) ->
            {
                if ((boolean) event.getSource().getValue())
                {
                    BaseLookup.getSystemNotificationHandler().showDismissableNotification(Lookup.i18n("rootScanner.successfulUploadMessage"), Lookup.i18n("dialogs.OK"), NotificationType.NOTE);
                } else
                {
                    BaseLookup.getSystemNotificationHandler().showErrorNotification(Lookup.i18n("rootScanner.rootUploadTitle"), Lookup.i18n("rootScanner.failedUploadMessage"));
                }
                Lookup.getProgressDisplay().removeGenericProgressBarFromDisplay(rootSoftwareUploadProgress);
                rootSoftwareUploadProgress = null;
                inhibitUpdate.set(false);
            });

            if (rootSoftwareUploadProgress != null)
            {
                Lookup.getProgressDisplay().removeGenericProgressBarFromDisplay(rootSoftwareUploadProgress);
                rootSoftwareUploadProgress = null;
            }

            Thread rootUploaderThread = new Thread(rootUploader);
            rootUploaderThread.setName("Root uploader");
            rootUploaderThread.setDaemon(true);
            rootUploaderThread.start();
        }
    }

    private static synchronized Future<Optional<File>> getRootDownloadFuture(String rootFileDirectory,
                                                                                   String rootFileName)
    {
        Path rootFilePath = Paths.get(rootFileDirectory + rootFileName);
        try
        {
            if (!Files.exists(rootFilePath, LinkOption.NOFOLLOW_LINKS))
            {
                rootDownloadFuture = null;
            }
            else if (Files.size(rootFilePath) < 10000000)
            {
                // Sanity check - less than 10 Mb seems to be too small for a Root install file.
                Files.delete(rootFilePath);
                rootDownloadFuture = null;
            }
        }
        catch (IOException ex)
        {
            rootDownloadFuture = null;
        }

        // It is static synchronized, so all other instances are blocked.
        if (rootDownloadFuture == null)
        {
            TaskWithProgessCallback<Optional<File>> rootDownloader = new TaskWithProgessCallback<Optional<File>>()
            {
                @Override
                protected Optional<File> call() throws Exception
                {
                    BaseLookup.getTaskExecutor().runOnGUIThread(() ->
                    {
                        if (rootSoftwareDownloadProgress != null)
                        {
                            Lookup.getProgressDisplay().removeGenericProgressBarFromDisplay(rootSoftwareDownloadProgress);
                            rootSoftwareDownloadProgress = null;
                        }
                    });
            
                    if (Files.exists(rootFilePath, LinkOption.NOFOLLOW_LINKS))
                    {
                        return Optional.of(rootFilePath.toFile());
                    }
                    else
                    {
                        BaseLookup.getTaskExecutor().runOnGUIThread(() ->
                        {
                            rootSoftwareDownloadProgress = Lookup.getProgressDisplay().addGenericProgressBarToDisplay(Lookup.i18n("rootScanner.rootDownloadTitle"),
                                    runningProperty(),
                                    progressProperty());

                        });
                        
                        // Download the file from the web server.
                        URL rootDownloadURL = new URL("https://downloads.cel-uk.com/software/root/" + rootFileName);
                        if (SystemUtils.downloadFromUrl(rootDownloadURL, rootFilePath.toString(), this))
                            return Optional.of(rootFilePath.toFile());
                    }
                    return Optional.empty();
                }

                @Override
                public void updateProgressPercent(double percentProgress)
                {
                    updateProgress(percentProgress, 100.0);
                }
            };

            rootDownloader.setOnScheduled((result) ->
            {
            });

            rootDownloader.setOnSucceeded((result) ->
            {
                tidyRootFiles(rootFileDirectory, rootFileName);
                if (rootSoftwareDownloadProgress != null)
                {
                    BaseLookup.getSystemNotificationHandler().showInformationNotification(Lookup.i18n("rootScanner.rootDownloadTitle"), Lookup.i18n("rootScanner.successfulDownloadMessage"));
                    Lookup.getProgressDisplay().removeGenericProgressBarFromDisplay(rootSoftwareDownloadProgress);
                    rootSoftwareDownloadProgress = null;
                }
            });

            rootDownloader.setOnFailed((result) ->
            {
                BaseLookup.getSystemNotificationHandler().showErrorNotification(Lookup.i18n("rootScanner.rootDownloadTitle"), Lookup.i18n("rootScanner.failedDownloadMessage"));
                Lookup.getProgressDisplay().removeGenericProgressBarFromDisplay(rootSoftwareDownloadProgress);
                rootSoftwareDownloadProgress = null;
            });

            rootDownloadFuture = rootDownloader;
            rootDownloadExecutor.execute(rootDownloader);
        }
        return rootDownloadFuture;
    }

    @FXML
    void updateRoot(ActionEvent event)
    {
        String pathToRootFile = BaseConfiguration.getUserTempDirectory();
        String rootFile = ROOT_UPGRADE_FILE_PREFIX + BaseConfiguration.getApplicationVersion() + ".zip";
        upgradeRootWithFile(pathToRootFile, rootFile);
    }
    
    @FXML
    void downgradeRoot(ActionEvent event)
    {
        boolean downgradeConfirmed = BaseLookup.getSystemNotificationHandler().showAreYouSureYouWantToDowngradeDialog();
        if (downgradeConfirmed)
        {
            updateRoot(event);
        }
    }

    @FXML
    private void launchRootManager(ActionEvent event)
    {
        String url = "http://" + associatedServer.getServerIP() + ":" + Configuration.remotePort + "/index.html";
        WebUtil.launchURL(url);
    }

    private StackPane buttonHolder;
    private DetectedServer associatedServer = null;
    private ChangeListener<ServerStatus> serverStatusListener = new ChangeListener<ServerStatus>()
    {
        @Override
        public void changed(ObservableValue<? extends ServerStatus> observable, ServerStatus oldValue, ServerStatus newValue)
        {
            processServerStatus(newValue);
        }
    };

    public RootConnectionButtonTableCell()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/celtech/resources/fxml/components/RootConnectionButtonTableCell.fxml"),
                BaseLookup.getLanguageBundle());
        fxmlLoader.setController(this);

        fxmlLoader.setClassLoader(this.getClass().getClassLoader());

        try
        {
            buttonHolder = fxmlLoader.load();
        } catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }

        updateButton.disableProperty().bind(inhibitUpdate);
        downgradeButton.disableProperty().bind(inhibitUpdate);
    }

    @Override
    protected void updateItem(DetectedServer item, boolean empty)
    {
        super.updateItem(item, empty);

        if (item != associatedServer)
        {
            if (associatedServer != null)
            {
                associatedServer.serverStatusProperty().removeListener(serverStatusListener);
            }
            if (item != null)
            {
                item.serverStatusProperty().addListener(serverStatusListener);
            }
            associatedServer = item;
        }

        if (item != null && !empty)
        {
            setGraphic(buttonHolder);
            processServerStatus(item.getServerStatus());
        } else
        {
            setGraphic(null);
        }
    }

    private void processServerStatus(ServerStatus status)
    {
        switch (status)
        {
            case CONNECTED:
                connectedBox.setVisible(true);
                disconnectedBox.setVisible(false);
                updateButton.setVisible(false);
                downgradeButton.setVisible(false);
                userEnteredPin = false;
                break;
            case NOT_CONNECTED:
                disconnectedBox.setVisible(true);
                pinEntryField.clear();
                connectedBox.setVisible(false);
                updateButton.setVisible(false);
                downgradeButton.setVisible(false);
                userEnteredPin = false;
                break;
            case WRONG_VERSION:
                handleWrongVersionCase();
                break;
            case WRONG_PIN:
                if (userEnteredPin)
                {
                    BaseLookup.getSystemNotificationHandler().showErrorNotification(Lookup.i18n("rootScanner.PIN"), Lookup.i18n("rootScanner.incorrectPIN"));
                    userEnteredPin = false;
                }
                associatedServer.setServerStatus(ServerStatus.NOT_CONNECTED);
                break;
            case UPGRADING:
            default:
                disconnectedBox.setVisible(false);
                connectedBox.setVisible(false);
                updateButton.setVisible(false);
                downgradeButton.setVisible(false);
                userEnteredPin = false;
                break;
        }
    }
    
    private void handleWrongVersionCase()
    {
        ApplicationVersion localVersion = new ApplicationVersion(BaseConfiguration.getApplicationVersion());
        ApplicationVersion serverVersion = associatedServer.getVersion();
        
        int comparison = localVersion.compareTo(serverVersion);
        
        if (comparison < 0)
        {
            // Local version is lower than server
            downgradeButton.setVisible(true);
            updateButton.setVisible(false);
        } else if (comparison > 0)
        {
            // Local version is higher than server
            updateButton.setVisible(true);
            downgradeButton.setVisible(false);
        }
        
        disconnectedBox.setVisible(false);
        connectedBox.setVisible(false);
        userEnteredPin = false;
    }
}
