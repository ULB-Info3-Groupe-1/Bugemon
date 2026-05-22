package ulb;

import java.io.InputStream;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import org.slf4j.bridge.SLF4JBridgeHandler;

import ulb.bootstrap.GameBootstrapper;
import ulb.bootstrap.ServiceRegistry;
import ulb.controllers.MetaController;
import ulb.models.player.PlayerState;

/** JavaFX entry point, bootstraps the Bugemon game. */
public class Main extends Application {

    public static void main(String[] args) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        InputStream fontStream = Main.class.getResourceAsStream("/fonts/boldpixels.ttf");
        if (fontStream != null) {
            Font.loadFont(fontStream, Configuration.Ui.FONT_SIZE);
        }

        stage.setTitle(Configuration.Ui.STAGE_TITLE);
        stage.setMaximized(true);

        Scene scene = new Scene(new StackPane());
        scene.getStylesheets().add(Main.class.getResource("/css/tokens.css").toExternalForm());
        scene.getStylesheets().add(Main.class.getResource("/css/base.css").toExternalForm());
        scene.getStylesheets().add(Main.class.getResource("/css/buttons.css").toExternalForm());
        scene.getStylesheets().add(Main.class.getResource("/css/bugemon.css").toExternalForm());
        scene.getStylesheets().add(Main.class.getResource("/css/combat.css").toExternalForm());
        scene.getStylesheets().add(Main.class.getResource("/css/menus.css").toExternalForm());
        scene.getStylesheets().add(Main.class.getResource("/css/reward.css").toExternalForm());
        stage.setScene(scene);

        GameBootstrapper bootstrapper = new GameBootstrapper();
        bootstrapper.initializeDatabase();

        String playerName = "default_player";
        ServiceRegistry services = bootstrapper.createServices(playerName);
        PlayerState playerState = bootstrapper.createPlayerState(playerName, services.team, services.inventory,
                services.skill);
        MetaController metaController = new MetaController(stage, services, playerState);
        metaController.start();

    }
}
