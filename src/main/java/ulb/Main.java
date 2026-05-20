package ulb;

import java.io.InputStream;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import org.slf4j.bridge.SLF4JBridgeHandler;

import ulb.controllers.MetaController;
import ulb.models.player.PlayerState;
import ulb.repositories.BugemonRepository;
import ulb.repositories.DatabaseConnection;
import ulb.repositories.InventoryRepository;
import ulb.repositories.PlayerRepository;
import ulb.repositories.QueryLoader;
import ulb.repositories.SkillRepository;
import ulb.repositories.StaticRepository;
import ulb.repositories.TeamRepository;
import ulb.repositories.exceptions.PlayernameAlreadyExistsException;
import ulb.repositories.postgres.DatabaseInitializer;
import ulb.repositories.postgres.PostgresBugemonRepository;
import ulb.repositories.postgres.PostgresDatabaseConnection;
import ulb.repositories.postgres.PostgresInventoryRepository;
import ulb.repositories.postgres.PostgresPlayerRepository;
import ulb.repositories.postgres.PostgresSkillRepository;
import ulb.repositories.postgres.PostgresStaticRepository;
import ulb.repositories.postgres.PostgresTeamRepository;
import ulb.services.BugemonService;
import ulb.services.InventoryService;
import ulb.services.SkillService;
import ulb.services.TeamService;
import ulb.services.TowerService;
import ulb.utils.Parser;

/** JavaFX entry point — bootstraps the Bugemon game. */
public class Main extends Application {

    public static void main(String[] args) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        launch(args);
    }

    private void createUserIfNotExists(String playerName, PlayerRepository playerRepository,
            StaticRepository staticRepository) {
        try {
            playerRepository.createPlayer(playerName, staticRepository.defaultInventory());
        } catch (PlayernameAlreadyExistsException e) {
            // Without client/server architecture the database is local and the playername
            // is always 'default_player', so a duplicate on startup is expected and safe.
        }
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
        DatabaseConnection dbConnection = new PostgresDatabaseConnection();
        Parser parser = new Parser();
        parser.parse();
        DatabaseInitializer dbInitializer = new DatabaseInitializer(dbConnection, loader.getQueries(),
                parser.getBugemons(), parser.getAttacks(), parser.getItems(), parser.getSkillTree().getNodes());
        dbInitializer.initialize();

        PostgresStaticRepository staticDataRepository = new PostgresStaticRepository(dbConnection, loader.getQueries(),
                parser.getInventory());
        InventoryRepository inventoryRepository = new PostgresInventoryRepository(dbConnection, loader.getQueries());
        PlayerRepository playerRepository = new PostgresPlayerRepository(dbConnection, loader.getQueries(),
                inventoryRepository);
        SkillRepository skillRepository = new PostgresSkillRepository(dbConnection, loader.getQueries());
        TeamRepository teamRepository = new PostgresTeamRepository(dbConnection, loader.getQueries());
        BugemonRepository bugemonRepository = new PostgresBugemonRepository(dbConnection, loader.getQueries(),
                staticDataRepository);

        String playerName = "default_player";
        this.createUserIfNotExists(playerName, playerRepository, staticDataRepository);

        SkillService skillService = new SkillService(skillRepository, staticDataRepository, playerName);
        BugemonService bugemonService = new BugemonService(staticDataRepository, bugemonRepository, playerName);
        TeamService teamService = new TeamService(teamRepository, bugemonRepository, playerName);
        InventoryService inventoryService = new InventoryService(playerName, inventoryRepository, staticDataRepository);
        TowerService towerService = new TowerService(playerRepository, playerName);

        PlayerState playerState = new PlayerState(playerName, null, inventoryService.getInventory(),
                skillService.getSkillTreeState());

        MetaController metaController = new MetaController(stage, bugemonService, teamService, towerService,
                inventoryService, skillService, playerState);
        metaController.start();

    }
}
