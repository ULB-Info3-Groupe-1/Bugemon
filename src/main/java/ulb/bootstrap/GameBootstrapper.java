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
import ulb.repositories.TowerRepository;
import ulb.repositories.exceptions.PlayernameAlreadyExistsException;
import ulb.repositories.postgres.DatabaseInitializer;
import ulb.repositories.postgres.PostgresBugemonRepository;
import ulb.repositories.postgres.PostgresDatabaseConnection;
import ulb.repositories.postgres.PostgresInventoryRepository;
import ulb.repositories.postgres.PostgresPlayerRepository;
import ulb.repositories.postgres.PostgresSkillRepository;
import ulb.repositories.postgres.PostgresStaticRepository;
import ulb.repositories.postgres.PostgresTeamRepository;
import ulb.repositories.postgres.PostgresTowerRepository;
import ulb.repositories.resource.ResourceMusicRepository;
import ulb.services.BugemonService;
import ulb.services.CombatService;
import ulb.services.InventoryService;
import ulb.services.LevelUpService;
import ulb.services.MusicService;
import ulb.services.RewardService;
import ulb.services.SaveService;
import ulb.services.SkillService;
import ulb.services.TeamService;
import ulb.services.TowerService;
import ulb.utils.Parser;

/**
 * Wires together all infrastructure objects (database, repositories, services) and hands the fully-assembled
 * {@link ServiceRegistry} to the rest of the application.
 *
 * <p>
 * This class is the sole entry point for application startup: it initialises the database schema, parses static game
 * data, constructs every repository and service with their correct dependencies, and creates or resumes a player
 * session.
 */
public class GameBootstrapper {

    private final DatabaseConnection dbConnection;
    private final QueryLoader loader;
    private final Parser parser;
    private final Random random;

    private PlayerRepository playerRepository;

    /**
     * Creates a new bootstrapper, opens the database connection, loads SQL queries and parses all static game data
     * (Bugemons, attacks, items, skill tree) from bundled resources.
     */
    public GameBootstrapper() {
        this.dbConnection = new PostgresDatabaseConnection();
        this.loader = new QueryLoader();
        this.parser = new Parser();
        this.parser.parse();
        this.random = new Random();
    }

    /**
     * Runs the database initialiser, creating tables and seeding static data (Bugemons, attacks, items, skill-tree
     * nodes) if they do not already exist.
     */
    public void initializeDatabase() {
        DatabaseInitializer dbInitializer = new DatabaseInitializer(this.dbConnection, this.loader.getQueries(),
                this.parser.getBugemons(), this.parser.getAttacks(), this.parser.getItems(),
                this.parser.getSkillTree().getNodes());
        dbInitializer.initialize();
    }

    /**
     * Instantiates every repository and service for the given player and returns them as a {@link ServiceRegistry}.
     *
     * @param playerName
     *            the unique name identifying the current player session
     * @return a fully wired {@code ServiceRegistry} ready for use by controllers
     */
    public ServiceRegistry createServices(String playerName) {
        StaticRepository staticDataRepository = new PostgresStaticRepository(this.dbConnection,
                this.loader.getQueries(), this.parser.getInventory());
        InventoryRepository inventoryRepository = new PostgresInventoryRepository(this.dbConnection,
                this.loader.getQueries());
        this.playerRepository = new PostgresPlayerRepository(this.dbConnection, this.loader.getQueries(),
                inventoryRepository);
        SkillRepository skillRepository = new PostgresSkillRepository(this.dbConnection, this.loader.getQueries());
        BugemonRepository bugemonRepository = new PostgresBugemonRepository(this.dbConnection, this.loader.getQueries(),
                staticDataRepository);
        TeamRepository teamRepository = new PostgresTeamRepository(this.dbConnection, this.loader.getQueries(),
                bugemonRepository);
        TowerRepository towerRepository = new PostgresTowerRepository(this.dbConnection, this.loader.getQueries());
        MusicRepository musicRepository = new ResourceMusicRepository();

        BugemonService bugemonService = new BugemonService(staticDataRepository, bugemonRepository, playerName);
        TeamService teamService = new TeamService(teamRepository, bugemonRepository, playerName);
        InventoryService inventoryService = new InventoryService(inventoryRepository, staticDataRepository, this.random,
                playerName);
        SkillService skillService = new SkillService(skillRepository, staticDataRepository, playerName);
        TowerService towerService = new TowerService(towerRepository, playerName);
        SaveService saveService = new SaveService(skillService, bugemonService, inventoryService, teamService,
                towerService);
        CombatService combatService = new CombatService(this.random);
        LevelUpService levelUpService = new LevelUpService(bugemonRepository, this.random, playerName);
        MusicService musicService = new MusicService(musicRepository);
        RewardService rewardService = new RewardService(bugemonService, inventoryService, this.random);

        return new ServiceRegistry(bugemonService, teamService, inventoryService, skillService, towerService,
                combatService, saveService, levelUpService, musicService, rewardService);
    }

    /**
     * Loads (or creates) a player and returns the in-memory {@link PlayerState} that controllers share throughout the
     * session.
     *
     * @param playerName
     *            the unique name of the player
     * @param teamService
     *            used to load the player's active team
     * @param inventoryService
     *            used to load the player's inventory and create a default one on first login
     * @param skillService
     *            used to load the player's skill-tree progress
     * @return a {@code PlayerState} populated with the player's current team, inventory and skill-tree state
     */
    public PlayerState createPlayerState(String playerName, TeamService teamService, InventoryService inventoryService,
            SkillService skillService) {
        this.createUserIfNotExists(playerName, inventoryService);
        return new PlayerState(playerName, teamService.getActiveTeam().orElse(null), inventoryService.getInventory(),
                skillService.getSkillTreeState());
    }

    private void createUserIfNotExists(String playerName, InventoryService inventoryService) {
        try {
            this.playerRepository.createPlayer(playerName, inventoryService.getDefaultInventory());
        } catch (PlayernameAlreadyExistsException e) {
            // Without client/server architecture the database is local and the playername
            // is always 'default_player', so a duplicate on startup is expected and safe.
        }
    }
}
