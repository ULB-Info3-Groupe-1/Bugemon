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

/** Service for Bugemon data access and XP/level-up lifecycle. Caches all default Bugemons after the first DB load. */
public class BugemonService {

    private final StaticDataRepository staticDataRepository;
    private final PlayerRepository playerRepository;
    private final PlayerService playerService;

    private Queue<LevelUp> levelUps = new ArrayDeque<>();

    // Cache for all default Bugemons to avoid multiple database calls
    private List<Bugemon> allDefaultBugemonsCache;

    public BugemonService(StaticDataRepository staticDataRepository, PlayerRepository playerRepository,
            PlayerService playerService) {
        this.staticDataRepository = staticDataRepository;
        this.playerRepository = playerRepository;
        this.playerService = playerService;
    }

    /** Returns all game-defined Bugemons; result is cached after the first database call. */
    public List<Bugemon> getAllDefaultBugemons() {
        if (this.allDefaultBugemonsCache == null) {
            this.allDefaultBugemonsCache = this.staticDataRepository.getAllDefaultBugemons();
        }
        return this.allDefaultBugemonsCache;
    }

    /**
     * Persists a new Bugemon and adds it to the in-memory cache.
     *
     * @throws BugemonNameIsEmptyException if the name is blank
     */
    public void saveBugemon(CreateBugemonDTO bugemon)
            throws BugemonNameIsEmptyException, BugemonAlreadyExistsException {
        this.staticDataRepository.saveBugemon(bugemon);
        this.allDefaultBugemonsCache.add(BugemonFactory.createBugemon(bugemon));
    }

    public Bugemon getBugemonByName(String name) {
        return this.getAllDefaultBugemons().stream().filter(b -> b.getName().equals(name)).findFirst().orElse(null);
    }

    /** Returns all attacks whose type matches {@code type}. */
    public List<Attack> getAttacksByType(BugemonType type) {
        return this.staticDataRepository.getAllAttacks().values().stream().filter(a -> a.type() == type).toList();
    }

    /** Persists the current stats (HP, XP, level) of {@code bugemon} and refreshes the local team cache. */
    public void saveBugemonState(Bugemon bugemon) {
        this.playerRepository.updatePlayerBugemon(new PlayerBugemonDTO(this.playerService.getPlayerId(),
                bugemon.getName(), bugemon.getDefense(), bugemon.getAttack(), bugemon.getInitiative(),
                bugemon.getMaxHp(), bugemon.getXp(), bugemon.getLevel()));

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
