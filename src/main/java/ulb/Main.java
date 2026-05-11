package ulb;

import java.io.InputStream;
import java.util.List;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import org.slf4j.bridge.SLF4JBridgeHandler;

import ulb.controllers.MetaController;
import ulb.models.skills.SkillEffect.StatBonusEffect;
import ulb.repositories.BugemonRepository;
import ulb.repositories.DatabaseConnection;
import ulb.repositories.InventoryRepository;
import ulb.repositories.PlayerRepository;
import ulb.repositories.QueryLoader;
import ulb.repositories.StaticDataRepository;
import ulb.repositories.TeamRepository;
import ulb.services.BugemonService;
import ulb.services.InventoryService;
import ulb.services.PlayerService;
import ulb.services.SkillService;
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
        SkillService skillService = new SkillService(List.of()); // TODO: load skills from a data source
        BugemonService bugemonService = new BugemonService(staticDataRepository, bugemonRepository, playerName,
                skillService.getSkills(StatBonusEffect.class));
        PlayerService playerService = new PlayerService(staticDataRepository);
        TeamService teamService = new TeamService(playerRepository, teamRepository, bugemonRepository, playerName,
                skillService);
        InventoryService inventoryService = new InventoryService(playerName, inventoryRepository, skillService);
        TowerService towerService = new TowerService(playerRepository, playerName, bugemonService, teamService,
                inventoryService, skillService);
        MetaController controller = new MetaController(stage, bugemonService, playerService, teamService, towerService,
                inventoryService, skillService);
        controller.start();
    }
}
