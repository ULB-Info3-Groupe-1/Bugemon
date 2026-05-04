package ulb;

import java.io.InputStream;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import org.slf4j.bridge.SLF4JBridgeHandler;

import ulb.controllers.MetaController;
import ulb.repositories.DatabaseConnection;
import ulb.repositories.PlayerRepository;
import ulb.repositories.QueryLoader;
import ulb.repositories.StaticDataRepository;
import ulb.services.BugemonService;
import ulb.services.CombatService;
import ulb.services.InventoryService;
import ulb.services.TeamService;

/** JavaFX entry point — bootstraps the Bugemon game. */
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
            Font.loadFont(fontStream, 16);
        }

        stage.setTitle(Configuration.Ui.STAGE_TITLE);
        stage.setMaximized(true);

        Scene scene = new Scene(new StackPane());
        scene.getStylesheets().add(Main.class.getResource("/css/tokens.css").toExternalForm());
        scene.getStylesheets().add(Main.class.getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);

        QueryLoader loader = new QueryLoader();
        DatabaseConnection dbConnection = new DatabaseConnection();
        StaticDataRepository staticDataRepository = new StaticDataRepository(dbConnection, loader.getQueries());
        PlayerRepository playerRepository = new PlayerRepository(dbConnection, staticDataRepository,
                loader.getQueries());

        String playerName = "default_player";
        int playerId = playerRepository.getPlayerIdOrCreatePlayer(playerName);

        BugemonService bugemonService = new BugemonService(staticDataRepository, playerRepository, playerId);
        TeamService teamService = new TeamService(playerRepository, playerId);
        InventoryService inventoryService = new InventoryService(playerRepository, playerId);
        CombatService combatService = new CombatService(bugemonService);
        MetaController controller = new MetaController(stage, bugemonService, teamService, inventoryService,
                combatService);
    }
}
