package ulb.services;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import ulb.factories.BugemonFactory;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon_team.exceptions.BugemonAlreadyExistsException;
import ulb.models.level_up.LevelUp;
import ulb.repositories.PlayerRepository;
import ulb.repositories.StaticDataRepository;
import ulb.repositories.dto.CreateBugemonDTO;
import ulb.repositories.dto.PlayerBugemonDTO;
import ulb.repositories.exceptions.BugemonNameIsEmptyException;
import ulb.repositories.exceptions.PlayernameIsEmptyException;

public class BugemonService {

    private final StaticDataRepository staticDataRepository;
    private final PlayerRepository playerRepository;
    private final PlayerService playerService;

    private Queue<LevelUp> levelUps = new ArrayDeque<>();

    // Cache for all default Bugemons to avoid multiple database calls
    private List<Bugemon> allDefaultBugemonsCache;

    public BugemonService(StaticDataRepository staticDataRepository, PlayerRepository playerRepository, PlayerService playerService) throws PlayernameIsEmptyException {
        this.staticDataRepository = staticDataRepository;
        this.playerRepository = playerRepository;
        this.playerService = playerService;
    }

    /**
     * Get all default Bugemons from the database. Cached after the first call.
     *
     * @return (List<Bugemon>) List of default Bugemons
     */
    public List<Bugemon> getAllDefaultBugemons() {
        if (this.allDefaultBugemonsCache == null) {
            this.allDefaultBugemonsCache = this.staticDataRepository.getAllDefaultBugemons();
        }
        return this.allDefaultBugemonsCache;
    }

    /**
     * Save a new bugemon in the database.
     *
     * @param bugemon
     *            (CreateBugemonDTO) the bugemon to be saved
     * @throws BugemonNameIsEmptyException
     *             if the name of the bugemon is empty
     */
    public void saveBugemon(CreateBugemonDTO bugemon)
            throws BugemonNameIsEmptyException, BugemonAlreadyExistsException {
        this.staticDataRepository.saveBugemon(bugemon);
        this.allDefaultBugemonsCache.add(BugemonFactory.createBugemon(bugemon));
    }

    public Bugemon getBugemonByName(String name) {
        return this.getAllDefaultBugemons().stream().filter(b -> b.getName().equals(name)).findFirst().orElse(null);
    }

    /**
     * Get all attacks matching a specific Bugemon type.
     *
     * @param type
     *            type used to filter attacks
     * @return attacks for the provided type
     */
    public List<Attack> getAttacksByType(BugemonType type) {
        return this.staticDataRepository.getAllAttacks().values().stream().filter(a -> a.type() == type).toList();
    }

    /**
     * Saves the state of a single bugemon to the database.
     *
     * @param bugemon
     *            the bugemon to save
     */
    public void saveBugemonState(Bugemon bugemon) {
        this.playerRepository.updatePlayerBugemon(
                new PlayerBugemonDTO(this.playerService.getPlayerId(), bugemon.getName(), bugemon.getDefense(), bugemon.getAttack(),
                        bugemon.getInitiative(), bugemon.getMaxHp(), bugemon.getXp(), bugemon.getLevel()));

        this.playerService.updateLocalTeams();
    }

    public int numPendingLevelUps() {
        return this.levelUps.size();
    }

    public boolean hasPendingLevelUps() {
        return this.numPendingLevelUps() > 0;
    }

    public LevelUp peekNextLevelUp() {
        return this.levelUps.peek();
    }

    public void applyNextLevelUp(int upgradeIdx) {
        LevelUp levelUp = this.levelUps.remove();
        levelUp.apply(upgradeIdx);

        this.saveBugemonState(levelUp.getBugemon());
    }

    public void distributeXp(Bugemon bugemon, int amount) {
        int numLevelUps = bugemon.gainXp(amount);

        if (numLevelUps > 0) {
            bugemon.restoreHp();
        }

        for (int i = 0; i < numLevelUps; i++) {
            this.levelUps.add(new LevelUp(bugemon));
        }

        this.saveBugemonState(bugemon);
    }
}
