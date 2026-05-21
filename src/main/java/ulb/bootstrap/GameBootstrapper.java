package ulb.bootstrap;

import java.util.Random;

import ulb.models.player.PlayerState;
import ulb.repositories.BugemonRepository;
import ulb.repositories.DatabaseConnection;
import ulb.repositories.InventoryRepository;
import ulb.repositories.MusicRepository;
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
import ulb.repositories.resource.ResourceMusicRepository;
import ulb.services.BugemonService;
import ulb.services.CombatService;
import ulb.services.InventoryService;
import ulb.services.MusicService;
import ulb.services.SaveService;
import ulb.services.SkillService;
import ulb.services.TeamService;
import ulb.services.TowerService;
import ulb.utils.Parser;

public class GameBootstrapper {

    private final DatabaseConnection dbConnection;
    private final QueryLoader loader;
    private final Parser parser;
    private final Random random;

    private PlayerRepository playerRepository;
    private StaticRepository staticDataRepository;

    public GameBootstrapper() {
        this.dbConnection = new PostgresDatabaseConnection();
        this.loader = new QueryLoader();
        this.parser = new Parser();
        this.parser.parse();
        this.random = new Random();
    }

    public void initializeDatabase() {
        DatabaseInitializer dbInitializer = new DatabaseInitializer(this.dbConnection, this.loader.getQueries(),
                this.parser.getBugemons(), this.parser.getAttacks(), this.parser.getItems(),
                this.parser.getSkillTree().getNodes());
        dbInitializer.initialize();
    }

    public ServiceRegistry createServices(String playerName) {
        this.staticDataRepository = new PostgresStaticRepository(this.dbConnection, this.loader.getQueries(),
                this.parser.getInventory());
        InventoryRepository inventoryRepository = new PostgresInventoryRepository(this.dbConnection,
                this.loader.getQueries());
        this.playerRepository = new PostgresPlayerRepository(this.dbConnection, this.loader.getQueries(),
                inventoryRepository);
        SkillRepository skillRepository = new PostgresSkillRepository(this.dbConnection, this.loader.getQueries());
        BugemonRepository bugemonRepository = new PostgresBugemonRepository(this.dbConnection, this.loader.getQueries(),
                this.staticDataRepository);
        TeamRepository teamRepository = new PostgresTeamRepository(this.dbConnection, this.loader.getQueries(),
                bugemonRepository);
        MusicRepository musicRepository = new ResourceMusicRepository();

        BugemonService bugemonService = new BugemonService(this.staticDataRepository, bugemonRepository, playerName);
        TeamService teamService = new TeamService(teamRepository, bugemonRepository, playerName);
        InventoryService inventoryService = new InventoryService(playerName, inventoryRepository,
                this.staticDataRepository);
        SkillService skillService = new SkillService(skillRepository, this.staticDataRepository, playerName);
        TowerService towerService = new TowerService(this.playerRepository, playerName);
        SaveService saveService = new SaveService(skillService, bugemonService, inventoryService, teamService);
        CombatService combatService = new CombatService(this.random, bugemonService);
        MusicService musicService = new MusicService(musicRepository);

        return new ServiceRegistry(bugemonService, teamService, inventoryService, skillService, towerService,
                combatService, saveService, musicService);
    }

    public PlayerState createPlayerState(String playerName, InventoryService inventoryService,
            SkillService skillService) {
        this.createUserIfNotExists(playerName);
        return new PlayerState(playerName, null, inventoryService.getInventory(), skillService.getSkillTreeState());
    }

    private void createUserIfNotExists(String playerName) {
        try {
            this.playerRepository.createPlayer(playerName, this.staticDataRepository.defaultInventory());
        } catch (PlayernameAlreadyExistsException e) {
            // Without client/server architecture the database is local and the playername
            // is always 'default_player', so a duplicate on startup is expected and safe.
        }
    }
}
