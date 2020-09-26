package celtech.coreUI.controllers;

import celtech.Lookup;
import celtech.appManager.ApplicationMode;
import celtech.appManager.ApplicationStatus;
import celtech.appManager.ModelContainerProject;
import celtech.configuration.ApplicationConfiguration;
import celtech.coreUI.DisplayManager;
import celtech.coreUI.components.buttons.GraphicButtonWithLabel;
import celtech.coreUI.visualisation.ModelLoader;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.MachineType;
import celtech.utils.MyMiniFactoryLoadResult;
import celtech.utils.MyMiniFactoryLoader;
import celtech.web.AllCookiePolicy;
import celtech.web.PersistentCookieStore;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import netscape.javascript.JSObject;

/**
 *
 * @author Ian
 */
public class MyMiniFactoryLoaderController implements Initializable
{

    private static final Stenographer steno = StenographerFactory.getStenographer(
            MyMiniFactoryLoaderController.class.getName());

    private WebEngine webEngine = null;

    private final StringProperty fileDownloadLocation = new SimpleStringProperty("");

    private final String myMiniFactoryURLString = "https://cel-robox.myminifactory.com";
    private boolean forwardsPossible = false;
    private final ModelLoader modelLoader = new ModelLoader();

    @FXML
    private VBox webContentContainer;

    @FXML
    private GraphicButtonWithLabel addToProjectButton;

    @FXML
    private GraphicButtonWithLabel forwardButton;

    @FXML
    private GraphicButtonWithLabel backwardButton;

    @FXML
    void cancelPressed(ActionEvent event)
    {
        ApplicationStatus.getInstance().modeProperty().set(ApplicationMode.LAYOUT);
    }

    @FXML
    void backwardPressed(ActionEvent event)
    {
        JSObject history = (JSObject) webEngine.executeScript("history");
        history.call("back");
    }

    @FXML
    void forwardPressed(ActionEvent event)
    {
        JSObject history = (JSObject) webEngine.executeScript("history");
        history.call("forward");
    }

    @FXML
    void addToProjectPressed(ActionEvent event)
    {
        Platform.runLater(() ->
        {
            downloadFile(fileDownloadLocation.get());
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        addToProjectButton.disableProperty().bind(Bindings.equal("", fileDownloadLocation));

        Platform.runLater(() ->
        {
            boolean siteReachable = checkSiteIsReachable();

            if (siteReachable)
            {
                loadWebData();
            }
        });
    }

    public void loadWebData()
    {
        CookieStore persistentStore = new PersistentCookieStore();
        CookiePolicy policy = new AllCookiePolicy();
        CookieManager handler = new CookieManager(persistentStore, policy);
        CookieHandler.setDefault(handler);

        webContentContainer.getChildren().clear();

        WebView webView = new WebView();
        VBox.setVgrow(webView, Priority.ALWAYS);

        webEngine = webView.getEngine();
        webEngine.setUserDataDirectory(new File(BaseConfiguration.getUserTempDirectory()));

        webContentContainer.getChildren().addAll(webView);
        
        if (BaseConfiguration.getMachineType() == MachineType.MAC)
        {
            // Work around for WebEngine bug that causes text on page to be illegible on a Mac.
            String mmfOverrideCSS = getClass().getResource("/celtech/resources/css/mmf-override.css").toString();
            webEngine.setUserStyleSheetLocation(mmfOverrideCSS);
        }
        webEngine.load(myMiniFactoryURLString);
        webEngine.getDocument();

        webEngine.getLoadWorker().stateProperty().addListener(
                (ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) ->
        {
            switch (newState)
            {
                case RUNNING:
                    fileDownloadLocation.set("");
                    DisplayManager.getInstance().startSpinning(webContentContainer);
                    break;
                case SUCCEEDED:
                    fileDownloadLocation.set("");
                    DisplayManager.getInstance().stopSpinning();
                    Object fileLinkFunction = webEngine
                            .executeScript("window.autoMakerGetFileLink");
                    if (fileLinkFunction instanceof JSObject)
                    {
                        fileDownloadLocation.set((String) webEngine
                                .executeScript("window.autoMakerGetFileLink()"));
                    }
                    boolean okForBackwards = webEngine.getLocation().matches(".*\\/object\\/.*");
                    forwardsPossible |= okForBackwards;
                    boolean okForForwards = !okForBackwards && forwardsPossible;
                    backwardButton.disableProperty().set(!okForBackwards);
                    forwardButton.disableProperty().set(!okForForwards);
                    break;
                case CANCELLED:
                    fileDownloadLocation.set("");
                    DisplayManager.getInstance().stopSpinning();
                    break;
                case FAILED:
                    fileDownloadLocation.set("");
                    DisplayManager.getInstance().stopSpinning();
                    break;
            }
        });
    }

    private boolean alreadyDownloading = false;

    public void downloadFile(String fileURL)
    {
        if (!alreadyDownloading)
        {
            alreadyDownloading = true;
            DisplayManager.getInstance().startSpinning(webContentContainer);

            MyMiniFactoryLoader loader = new MyMiniFactoryLoader(fileURL);

            loader.setOnSucceeded((WorkerStateEvent event) ->
            {
                MyMiniFactoryLoadResult result = (MyMiniFactoryLoadResult) event.getSource().getValue();
                if (result.isSuccess())
                {
                    modelLoader.loadExternalModels(Lookup.getSelectedProjectProperty().get(), result.getFilesToLoad(), true, DisplayManager.getInstance(), false);
                }
                finishedWithEngines();
                ApplicationStatus.getInstance().setMode(ApplicationMode.LAYOUT);
            });

            loader.setOnFailed((WorkerStateEvent event) ->
            {
                finishedWithEngines();
            });

            loader.setOnCancelled((WorkerStateEvent event) ->
            {
                finishedWithEngines();
            });

            Thread loaderThread = new Thread(loader, "My minifactory downloader");
            loaderThread.start();
        }
    }

    private void finishedWithEngines()
    {
        alreadyDownloading = false;
        DisplayManager.getInstance().stopSpinning();
    }

    private boolean checkSiteIsReachable()
    {
        //This has been introduced since a lack of response from the far end seems to cause WebEngine to create lots and lots of threads
        // culminating in out of memory errors and preventing the printer from connecting.
        // Bit of a sticking plaster, but check to see if the site is reachable first...

        boolean available = false;

        try
        {
            URL obj = new URL(myMiniFactoryURLString);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            //con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.44 (KHTML, like Gecko) JavaFX/8.0 Safari/537.44");

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setConnectTimeout(500);
            int responseCode = con.getResponseCode();

            if (responseCode == 200)
            {
                available = true;
            } else
            {
                steno.warning("My Mini Factory site unavailable");
            }
        } catch (IOException ex)
        {
            steno.error("Exception whilst attempting to contact My Mini Factory site");
            steno.debug("Exception - " + ex.toString());
        }

        return available;
    }

}
