package bugemon.server.bootstrap;

import java.util.Random;

import bugemon.common.dto.persistence.DefaultInventoryDTO;
import bugemon.server.repositories.BugemonRepository;
import bugemon.server.repositories.DatabaseConnection;
import bugemon.server.repositories.InventoryRepository;
import bugemon.server.repositories.PlayerRepository;
import bugemon.server.repositories.QueryLoader;
import bugemon.server.repositories.SkillRepository;
import bugemon.server.repositories.StaticRepository;
import bugemon.server.repositories.TeamRepository;
import bugemon.server.repositories.TowerRepository;
import bugemon.server.repositories.postgres.DatabaseInitializer;
import bugemon.server.repositories.postgres.PostgresBugemonRepository;
import bugemon.server.repositories.postgres.PostgresDatabaseConnection;
import bugemon.server.repositories.postgres.PostgresInventoryRepository;
import bugemon.server.repositories.postgres.PostgresPlayerRepository;
import bugemon.server.repositories.postgres.PostgresSkillRepository;
import bugemon.server.repositories.postgres.PostgresStaticRepository;
import bugemon.server.repositories.postgres.PostgresTeamRepository;
import bugemon.server.repositories.postgres.PostgresTowerRepository;
import bugemon.server.services.BugemonService;
import bugemon.server.services.InventoryService;
import bugemon.server.services.LevelUpService;
import bugemon.server.services.RewardService;
import bugemon.server.services.SaveService;
import bugemon.server.services.SkillService;
import bugemon.server.services.TeamService;
import bugemon.server.services.TowerService;
import bugemon.server.utils.Parser;

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
