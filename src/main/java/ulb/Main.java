package ulb;

import java.io.InputStream;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import org.slf4j.bridge.SLF4JBridgeHandler;

import ulb.controllers.MetaController;
import ulb.repositories.BugemonRepository;
import ulb.repositories.DatabaseConnection;
import ulb.repositories.InventoryRepository;
import ulb.repositories.PlayerRepository;
import ulb.repositories.QueryLoader;
import ulb.repositories.StaticDataRepository;
import ulb.repositories.TeamRepository;
import ulb.services.BugemonService;
import ulb.services.CombatService;
import ulb.services.InventoryService;
import ulb.services.PlayerService;
import ulb.services.TeamService;
import ulb.services.TowerService;

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
        BugemonRepository bugemonRepository = new BugemonRepository(dbConnection, loader.getQueries());
        InventoryRepository inventoryRepository = new InventoryRepository(dbConnection, staticDataRepository,
                loader.getQueries());
        PlayerRepository playerRepository = new PlayerRepository(dbConnection, inventoryRepository,
                loader.getQueries());
        TeamRepository teamRepository = new TeamRepository(dbConnection, staticDataRepository, bugemonRepository,
                loader.getQueries());

        String playerName = "default_player";
        BugemonService bugemonService = new BugemonService(staticDataRepository, bugemonRepository, playerName);
        PlayerService playerService = new PlayerService(staticDataRepository);
        TeamService teamService = new TeamService(playerRepository, teamRepository, bugemonRepository, playerName);
        InventoryService inventoryService = new InventoryService(playerRepository, inventoryRepository, playerName);
        TowerService towerService = new TowerService(playerRepository, playerName, bugemonService, teamService,
                inventoryService);
        CombatService combatService = new CombatService(bugemonService);
        MetaController controller = new MetaController(stage, bugemonService, playerService, teamService, towerService,
                inventoryService, combatService);
        controller.start();
    }
}
