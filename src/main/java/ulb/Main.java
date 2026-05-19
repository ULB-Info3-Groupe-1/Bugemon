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
import ulb.models.skills.SkillEffect.StatBonusEffect;
import ulb.repositories.exceptions.PlayernameAlreadyExistsException;
import ulb.repositories.BugemonRepository;
import ulb.repositories.DatabaseConnection;
import ulb.repositories.InventoryRepository;
import ulb.repositories.PlayerRepository;
import ulb.repositories.QueryLoader;
import ulb.repositories.SkillRepository;
import ulb.repositories.StaticRepository;
import ulb.repositories.TeamRepository;
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
import ulb.services.PlayerService;
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
        Parser parser = new Parser();
        parser.parse();

        StaticDataRepository staticDataRepository = new StaticDataRepository(dbConnection, loader.getQueries(),
                        parser.getBugemons(), parser.getAttacks(), parser.getItems());
        PlayerBugemonRepository playerBugemonRepository = new PlayerBugemonRepository(dbConnection,
                        loader.getQueries());
        InventoryRepository inventoryRepository = new InventoryRepository(dbConnection, loader.getQueries(),
                        parser.getInventory());
        PlayerRepository playerRepository = new PlayerRepository(dbConnection, inventoryRepository,
                        loader.getQueries(),
                        parser.getSkillTree());
        TeamRepository teamRepository = new PostgresTeamRepository(dbConnection,
                        loader.getQueries());

        String playerName = "default_player";
        this.createUserIfNotExists(playerName, playerRepository);

        PlayerService playerService = new PlayerService(playerRepository, playerName);

        SkillService skillService = new SkillService(playerService.getUnlockedSkills());
        BugemonService bugemonService = new BugemonService(staticDataRepository, playerBugemonRepository,
                        playerName,
                        skillService.getSkills(StatBonusEffect.class));
        TeamService teamService = new TeamService(teamRepository, playerBugemonRepository, playerName);
        InventoryService inventoryService = new InventoryService(playerName, inventoryRepository, skillService);
        TowerService towerService = new TowerService(playerRepository, playerName);

        PlayerState playerState = new PlayerState(playerName, null, inventoryService.loadInventory(),
                playerService.getUnlockedSkills());

        MetaController metaController = new MetaController(stage, bugemonService, playerService, teamService,
                towerService, inventoryService, playerState);
        metaController.start();

    }
}
