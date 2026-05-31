package ulb.bootstrap;

import java.util.Random;

import ulb.common.dto.persistence.DefaultInventoryDTO;
import ulb.repositories.BugemonRepository;
import ulb.repositories.DatabaseConnection;
import ulb.repositories.InventoryRepository;
import ulb.repositories.PlayerRepository;
import ulb.repositories.QueryLoader;
import ulb.repositories.SkillRepository;
import ulb.repositories.StaticRepository;
import ulb.repositories.TeamRepository;
import ulb.repositories.TowerRepository;
import ulb.repositories.postgres.DatabaseInitializer;
import ulb.repositories.postgres.PostgresBugemonRepository;
import ulb.repositories.postgres.PostgresDatabaseConnection;
import ulb.repositories.postgres.PostgresInventoryRepository;
import ulb.repositories.postgres.PostgresPlayerRepository;
import ulb.repositories.postgres.PostgresSkillRepository;
import ulb.repositories.postgres.PostgresStaticRepository;
import ulb.repositories.postgres.PostgresTeamRepository;
import ulb.repositories.postgres.PostgresTowerRepository;
import ulb.services.game.BugemonService;
import ulb.services.game.InventoryService;
import ulb.services.game.LevelUpService;
import ulb.services.game.RewardService;
import ulb.services.game.SaveService;
import ulb.services.game.SkillService;
import ulb.services.game.TeamService;
import ulb.services.game.TowerService;
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
    private final RepositoryRegistry repositoryRegistry;

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

        this.parser.parse();
        this.initializeDatabase();
        this.repositoryRegistry = this.createRepositories();
    }

    /**
     * Runs the database initialiser, creating tables and seeding static data (Bugemons, attacks, items, skill-tree
     * nodes) if they do not already exist.
     */
    private void initializeDatabase() {
        DatabaseInitializer dbInitializer = new DatabaseInitializer(this.dbConnection, this.loader.getQueries(),
                this.parser.getBugemons(), this.parser.getAttacks(), this.parser.getItems(),
                this.parser.getSkillTree().getNodes());
        dbInitializer.initialize();
    }

    public RepositoryRegistry getRepositories() {
        return this.repositoryRegistry;
    }

    public DefaultInventoryDTO getDefaultInventory() {
        return this.parser.getInventory();
    }

    public Random getRandom() {
        return this.random;
    }

    /**
     * Instantiates every repository and service for the given player and returns them as a {@link ServiceRegistry}.
     *
     * @param playerName
     *            the unique name identifying the current player session
     * @return a fully wired {@code ServiceRegistry} ready for use by controllers
     */
    public ServiceRegistry createServices(String playerName) {
        BugemonService bugemonService = new BugemonService(this.repositoryRegistry.staticDataRepository,
                this.repositoryRegistry.bugemonRepository, playerName);
        TeamService teamService = new TeamService(this.repositoryRegistry.teamRepository,
                this.repositoryRegistry.bugemonRepository, playerName);
        InventoryService inventoryService = new InventoryService(this.repositoryRegistry.inventoryRepository,
                this.repositoryRegistry.staticDataRepository, this.random, playerName);
        SkillService skillService = new SkillService(this.repositoryRegistry.skillRepository,
                this.repositoryRegistry.staticDataRepository, playerName);
        TowerService towerService = new TowerService(this.repositoryRegistry.towerRepository, playerName);
        SaveService saveService = new SaveService(skillService, bugemonService, inventoryService, teamService,
                towerService);
        LevelUpService levelUpService = new LevelUpService(this.repositoryRegistry.bugemonRepository, this.random,
                playerName);
        RewardService rewardService = new RewardService(bugemonService, inventoryService, this.random);

        return new ServiceRegistry(bugemonService, teamService, inventoryService, skillService, towerService,
                saveService, levelUpService, rewardService);
    }

    private RepositoryRegistry createRepositories() {
        StaticRepository staticDataRepository = new PostgresStaticRepository(this.dbConnection,
                this.loader.getQueries(), this.parser.getInventory());
        InventoryRepository inventoryRepository = new PostgresInventoryRepository(this.dbConnection,
                this.loader.getQueries());
        PlayerRepository playerRepository = new PostgresPlayerRepository(this.dbConnection, this.loader.getQueries(),
                inventoryRepository);
        SkillRepository skillRepository = new PostgresSkillRepository(this.dbConnection, this.loader.getQueries());
        BugemonRepository bugemonRepository = new PostgresBugemonRepository(this.dbConnection, this.loader.getQueries(),
                staticDataRepository);
        TeamRepository teamRepository = new PostgresTeamRepository(this.dbConnection, this.loader.getQueries(),
                bugemonRepository);
        TowerRepository towerRepository = new PostgresTowerRepository(this.dbConnection, this.loader.getQueries());

        return new RepositoryRegistry(staticDataRepository, inventoryRepository, playerRepository, skillRepository,
                bugemonRepository, teamRepository, towerRepository);
    }
}
