package celtech.coreUI.controllers.panels;

import celtech.Lookup;
import celtech.appManager.ApplicationMode;
import celtech.appManager.ApplicationStatus;
import celtech.appManager.ProjectMode;
import celtech.configuration.ApplicationConfiguration;
import celtech.configuration.DirectoryMemoryProperty;
import celtech.coreUI.DisplayManager;
import celtech.coreUI.components.InsetPanelMenu;
import celtech.coreUI.components.InsetPanelMenuItem;
import celtech.coreUI.visualisation.ModelLoader;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.utils.SystemUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import netscape.javascript.JSObject;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Ian
 */
public class LoadModelInsetPanelController implements Initializable
{

    private final FileChooser modelFileChooser = new FileChooser();
    private DisplayManager displayManager = null;
    private static final Stenographer steno = StenographerFactory.getStenographer(
        LoadModelInsetPanelController.class.getName());
    private ModelLoader modelLoader = new ModelLoader();

    @FXML
    private VBox container;

    @FXML
    private VBox webContentContainer;

    @FXML
    private InsetPanelMenu menu;

    @FXML
    void cancelPressed(ActionEvent event)
    {
        ApplicationStatus.getInstance().returnToLastMode();
    }

    @FXML
    void addToProjectPressed(ActionEvent event)
    {
        Platform.runLater(() ->
        {
            ListIterator iterator = modelFileChooser.getExtensionFilters().listIterator();

            while (iterator.hasNext())
            {
                iterator.next();
                iterator.remove();
            }

            String descriptionOfFile = Lookup.i18n("dialogs.meshFileChooserDescription");

            modelFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(descriptionOfFile,
                                                ApplicationConfiguration.getSupportedFileExtensionWildcards(
                                                    ProjectMode.MESH)),
                new FileChooser.ExtensionFilter(descriptionOfFile,
                                                ApplicationConfiguration.getSupportedFileExtensionWildcards(
                                                    ProjectMode.SVG)));

            modelFileChooser.setInitialDirectory(ApplicationConfiguration.getLastDirectoryFile(DirectoryMemoryProperty.LAST_MODEL_DIRECTORY));

            List<File> files;

            files = modelFileChooser.showOpenMultipleDialog(displayManager.getMainStage());

            if (files != null && !files.isEmpty())
            {
                ApplicationConfiguration.setLastDirectory(
                    DirectoryMemoryProperty.LAST_MODEL_DIRECTORY,
                    files.get(0).getParentFile().getAbsolutePath());
                modelLoader.loadExternalModels(Lookup.getSelectedProjectProperty().get(), files,
                                               true, null, false);
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        displayManager = DisplayManager.getInstance();

        menu.setTitle(Lookup.i18n("loadModel.menuTitle"));

        InsetPanelMenuItem myComputerItem = new InsetPanelMenuItem();
        myComputerItem.setTitle(Lookup.i18n("loadModel.myComputer"));

        InsetPanelMenuItem myMiniFactoryItem = new InsetPanelMenuItem();
        myMiniFactoryItem.setTitle(Lookup.i18n("loadModel.myMiniFactory"));

        menu.addMenuItem(myComputerItem);
        menu.addMenuItem(myMiniFactoryItem);

        modelFileChooser.setTitle(Lookup.i18n("dialogs.modelFileChooser"));
        modelFileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter(Lookup.i18n("dialogs.modelFileChooserDescription"),
                                            ApplicationConfiguration.getSupportedFileExtensionWildcards(
                                                ProjectMode.NONE)));

        ApplicationStatus.getInstance().modeProperty().addListener(
            (ObservableValue<? extends ApplicationMode> observable, ApplicationMode oldValue, ApplicationMode newValue) ->
            {
                if (newValue == ApplicationMode.ADD_MODEL && oldValue != newValue)
                {
                    webContentContainer.getChildren().clear();

                    WebView webView = new WebView();
                    VBox.setVgrow(webView, Priority.ALWAYS);

                    final WebEngine webEngine = webView.getEngine();

                    webEngine.getLoadWorker().stateProperty().addListener(
                        new ChangeListener<State>()
                        {
                            @Override
                            public void changed(ObservableValue<? extends State> ov,
                                State oldState, State newState)
                            {
                                switch (newState)
                                {
                                    case RUNNING:
                                        break;
                                    case SUCCEEDED:
                                        JSObject win = (JSObject) webEngine.executeScript("window");
                                        win.setMember("automaker", new WebCallback());
                                        break;
                                }
                            }
                        }
                    );
                    webContentContainer.getChildren().addAll(webView);
                    webEngine.load("http://cel-robox.myminifactory.com");
                }
            });
    }

    public class WebCallback
    {

        public void downloadFile(String fileURL)
        {
            steno.debug("Got download URL of " + fileURL);

            String tempID = SystemUtils.generate16DigitID();
            try
            {
                URL downloadURL = new URL(fileURL);

                String extension = FilenameUtils.getExtension(fileURL);
                final String tempFilename = BaseConfiguration.getApplicationStorageDirectory()
                    + File.separator + tempID + "." + extension;

                URLConnection urlConn = downloadURL.openConnection();

                InputStream webInputStream = urlConn.getInputStream();

                if (extension.equalsIgnoreCase("stl"))
                {
                    steno.debug("Got stl file from My Mini Factory");
                    final String targetname = BaseConfiguration.getUserStorageDirectory()
                        + File.separator + FilenameUtils.getBaseName(fileURL);
                    writeStreamToFile(webInputStream, targetname);
                } else if (extension.equalsIgnoreCase("zip"))
                {
                    steno.debug("Got zip file from My Mini Factory");
                    writeStreamToFile(webInputStream, tempFilename);
                    ZipFile zipFile = new ZipFile(tempFilename);
                    try
                    {
                        final Enumeration<? extends ZipEntry> entries = zipFile.entries();
                        final List<File> filesToLoad = new ArrayList<>();
                        while (entries.hasMoreElements())
                        {
                            final ZipEntry entry = entries.nextElement();
                            final String tempTargetname = BaseConfiguration.getUserStorageDirectory()
                                + File.separator + entry.getName();
                            writeStreamToFile(zipFile.getInputStream(entry), tempTargetname);
                            filesToLoad.add(new File(tempTargetname));
                        }
                        modelLoader.loadExternalModels(Lookup.getSelectedProjectProperty().get(),
                                                       filesToLoad, null);
                    } finally
                    {
                        zipFile.close();
                    }
                } else if (extension.equalsIgnoreCase("rar"))
                {
                    steno.debug("Got rar file from My Mini Factory");
                }

                webInputStream.close();

            } catch (IOException ex)
            {
                steno.error("Failed to download My Mini Factory file :" + fileURL);
            }
        }
    }

    private void writeStreamToFile(InputStream is, String localFilename) throws IOException
    {
        FileOutputStream fos = null;

        try
        {
            fos = new FileOutputStream(localFilename);   //open outputstream to local file

            byte[] buffer = new byte[4096];              //declare 4KB buffer
            int len;

            //while we have availble data, continue downloading and storing to local file
            while ((len = is.read(buffer)) > 0)
            {
                fos.write(buffer, 0, len);
            }
        } finally
        {
            try
            {
                if (is != null)
                {
                    is.close();
                }
            } finally
            {
                if (fos != null)
                {
                    fos.close();
                }
            }
        }
    }

}
