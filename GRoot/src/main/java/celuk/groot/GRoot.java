package celuk.groot;

import celuk.groot.controllers.RootStackController;
import celuk.groot.remote.RootServer;
import celuk.language.I18n;
import com.beust.jcommander.JCommander;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class GRoot extends Application {

    private static final GRootCommandLineArgs COMMAND_LINE_ARGS = new GRootCommandLineArgs();
    private Parent root = null;
    private RootStackController rootController = null;
    private RootServer server = null;
    
    @Override
    public void init() throws Exception {
        String configurationDirectory = COMMAND_LINE_ARGS.configurationDirectory;
        if (configurationDirectory.isBlank())
            COMMAND_LINE_ARGS.configurationDirectory = COMMAND_LINE_ARGS.installDirectory;
        server = new RootServer(COMMAND_LINE_ARGS.hostName, COMMAND_LINE_ARGS.portNumber, COMMAND_LINE_ARGS.configurationDirectory);
        server.initPIN(COMMAND_LINE_ARGS.pin);
        server.startUpdating(COMMAND_LINE_ARGS.updateInterval);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader rootLoader =  new FXMLLoader(getClass().getResource("/fxml/RootStack.fxml"), null);
        rootController = new RootStackController();
        rootController.setRootServer(server);
        rootLoader.setController(rootController);
        root = rootLoader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        
        stage.setTitle("GRoot");
        stage.setScene(scene);
        stage.getIcons().addAll(new Image(getClass().getResourceAsStream(
                "/icon/GRootIcon_256x256.png")),
                new Image(getClass().getResourceAsStream(
                                "/icon/GRootIcon_64x64.png")),
                new Image(getClass().getResourceAsStream(
                                "/icon/GRootIcon_32x32.png")));
        
        stage.show();
        
        //org.scenicview.ScenicView.show(scene);
    }

    @Override
    public void stop() {
        //System.out.println("stop");
        rootController.stop();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        new JCommander(COMMAND_LINE_ARGS).parse(args);
        I18n.loadMessages(COMMAND_LINE_ARGS.installDirectory,
                          I18n.getDefaultApplicationLocale(COMMAND_LINE_ARGS.languageTag));
        if (COMMAND_LINE_ARGS.showSplashScreen)
            System.setProperty("javafx.preloader", GRootPreloader.class.getName());
        launch(args);
    }
}
