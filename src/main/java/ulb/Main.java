package ulb;

import java.io.InputStream;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import org.slf4j.bridge.SLF4JBridgeHandler;

import ulb.controllers.MetaController;
import ulb.controllers.MetaController.Window;
import ulb.repository.DatabaseConnection;
import ulb.repository.PlayerRepository;
import ulb.repository.QueryLoader;
import ulb.repository.StaticDataRepository;
import ulb.services.BugemonService;
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

        stage.setTitle(STAGE_TITLE);
        stage.setResizable(false);
        stage.setFullScreenExitHint("");
        stage.setFullScreen(true);

        Scene scene = new Scene(new StackPane());
        scene.getStylesheets().add(Main.class.getResource("/css/tokens.css").toExternalForm());
        scene.getStylesheets().add(Main.class.getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);

        // TODO: remove hardcoded playername once a login screen exists
        QueryLoader loader = new QueryLoader();
        DatabaseConnection dbConnection = new DatabaseConnection();
        StaticDataRepository staticDataRepository = new StaticDataRepository(dbConnection, loader.getQueries());
        PlayerRepository playerRepository = new PlayerRepository(dbConnection, loader.getQueries());

        BugemonService bugemonService = new BugemonService(staticDataRepository);
        PlayerService playerService = new PlayerService(bugemonService, playerRepository, "default_player");
        MetaController controller = new MetaController(stage, bugemonService, playerService);
        controller.switchTo(Window.MAIN_MENU);
    }
}
