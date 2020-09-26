package celuk.groot;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GRootPreloader extends Preloader {

    private Stage splashScreen = null;
    
    @Override
    public void start(Stage stage) throws Exception {
        splashScreen = stage;
        splashScreen.setScene(createScene());
        splashScreen.show();
    }

    public Scene createScene() {
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 480, 800);
        URL splashURL = getClass().getResource("/fxml/Splash.fxml");
        try {
            StackPane pane = FXMLLoader.load(splashURL);
            root.getChildren().setAll(pane);
        }
        catch (IOException ex) {
        }
        return scene;
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification scn)
    {
        if (scn.getType() == StateChangeNotification.Type.BEFORE_START) {
            hideSplashScreen();
        }
    }

    private void hideSplashScreen() {
        if (splashScreen != null) {
            splashScreen.hide();
            splashScreen = null;
        }
    }
}
