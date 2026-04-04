package ulb;

import java.io.InputStream;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import org.slf4j.bridge.SLF4JBridgeHandler;

import ulb.controllers.MetaController;
import ulb.controllers.MetaController.Window;
import ulb.services.PlayerService;

/** JavaFX entry point — bootstraps the Bugemon game. */
public class Main extends Application {
    private static final String STAGE_TITLE = "Bugemon";

    public static void main(String[] args) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        InputStream fontStream = Main.class.getResourceAsStream("/fonts/boldpixels.ttf");
        if (fontStream != null) {
            Font.loadFont(fontStream, 16);
        }

        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        stage.setTitle(STAGE_TITLE);
        stage.setMaximized(true);
        stage.setMinWidth(screen.getWidth() * 0.6);
        stage.setMinHeight(screen.getHeight() * 0.5);

        Scene scene = new Scene(new StackPane());
        scene.getStylesheets().add(Main.class.getResource("/css/tokens.css").toExternalForm());
        scene.getStylesheets().add(Main.class.getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);

        // TODO: remove hardcoded username once a login screen exists
        PlayerService playerService = new PlayerService("default_user");
        MetaController controller = new MetaController(stage, playerService);
        controller.switchTo(Window.MAIN_MENU);
    }
}
