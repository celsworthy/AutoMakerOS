package celtech.automaker;

import celtech.configuration.ApplicationConfiguration;
import celtech.roboxbase.ApplicationFeature;
import celtech.roboxbase.configuration.BaseConfiguration;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Preloader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class AutoMakerPreloader extends Preloader
{

    private static final Stenographer steno;
    private Stage preloaderStage;
    private Pane splashLayout;
    private double splashWidth;
    private double splashHeight;

    static
    {
        steno = StenographerFactory.getStenographer(AutoMaker.class.getName());
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        // Before splash initialise the BaseConfiguration application install directory and check the license
        // so it can get the version string. (Without this, BaseConfiguration.getApplicationVersion() returns null.)
        BaseConfiguration.getApplicationInstallDirectory(AutoMaker.class);
        
        // Enable all the pro version features.
        BaseConfiguration.enableApplicationFeature(ApplicationFeature.LATEST_CURA_VERSION);
        BaseConfiguration.enableApplicationFeature(ApplicationFeature.GCODE_VISUALISATION);
        BaseConfiguration.enableApplicationFeature(ApplicationFeature.OFFLINE_PRINTER);
        BaseConfiguration.enableApplicationFeature(ApplicationFeature.PRO_SPLASH_SCREEN);
        
        this.preloaderStage = stage;
        steno.debug("show splash - start");
        preloaderStage.toFront();
        preloaderStage.getIcons().addAll(new Image(getClass().getResourceAsStream(
                "/celtech/automaker/resources/images/AutoMakerIcon_256x256.png")),
                new Image(getClass().getResourceAsStream(
                                "/celtech/automaker/resources/images/AutoMakerIcon_64x64.png")),
                new Image(getClass().getResourceAsStream(
                                "/celtech/automaker/resources/images/AutoMakerIcon_32x32.png")));

        String splashImageName = BaseConfiguration.isApplicationFeatureEnabled(ApplicationFeature.PRO_SPLASH_SCREEN) 
                ? "Splash_AutoMakerPro.png" : "Splash_AutoMaker.png";
        Image splashImage = new Image(getClass().getResourceAsStream(
                ApplicationConfiguration.imageResourcePath + splashImageName));

        ImageView splash = new ImageView(splashImage);
        
        splashWidth = splashImage.getWidth();
        splashHeight = splashImage.getHeight();
        splashLayout = new AnchorPane();

        SimpleDateFormat yearFormatter = new SimpleDateFormat("YYYY");
        String yearString = yearFormatter.format(new Date());
        Text copyrightLabel = new Text("© " + yearString
                + " C Enterprise (UK) Ltd. All Rights Reserved.");
        copyrightLabel.getStyleClass().add("splashCopyright");
        AnchorPane.setBottomAnchor(copyrightLabel, 38.0);
        AnchorPane.setLeftAnchor(copyrightLabel, 21.0);

        String versionString;
        if(BaseConfiguration.isApplicationFeatureEnabled(ApplicationFeature.PRO_SPLASH_SCREEN)) {
            versionString = BaseConfiguration.getApplicationVersion() + "_P";
        } else {
            versionString = BaseConfiguration.getApplicationVersion();
        }
        Text versionLabel = new Text("Version " + versionString);
        versionLabel.getStyleClass().add("splashVersion");
        AnchorPane.setBottomAnchor(versionLabel, 20.0);
        AnchorPane.setLeftAnchor(versionLabel, 21.0);

        splashLayout.setStyle("-fx-background-color: rgba(255, 0, 0, 0);");
        splashLayout.getChildren().addAll(splash, copyrightLabel, versionLabel);

        Scene splashScene = new Scene(splashLayout, Color.TRANSPARENT);
        splashScene.getStylesheets().add(ApplicationConfiguration.getMainCSSFile());
        preloaderStage.initStyle(StageStyle.TRANSPARENT);

        final Rectangle2D bounds = Screen.getPrimary().getBounds();
        preloaderStage.setScene(splashScene);
        preloaderStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - splashWidth / 2);
        preloaderStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - splashHeight / 2);

        steno.debug("show splash");
        preloaderStage.show();
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification scn)
    {
        if (scn.getType() == StateChangeNotification.Type.BEFORE_START)
        {
            PauseTransition pauseForABit = new PauseTransition(Duration.millis(2000));
            FadeTransition fadeSplash = new FadeTransition(Duration.seconds(2), splashLayout);
            fadeSplash.setFromValue(1.0);
            fadeSplash.setToValue(0.0);
            fadeSplash.setOnFinished(actionEvent ->
            {
                preloaderStage.hide();
//                preloaderStage.setAlwaysOnTop(false);
            });

            SequentialTransition splashSequence = new SequentialTransition(pauseForABit, fadeSplash);
            splashSequence.play();
        }
    }
}
