package ulb.services;

import java.util.List;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonType;
import ulb.models.level_up.LevelUp;
import ulb.repositories.BugemonRepository;
import ulb.repositories.StaticDataRepository;
import ulb.repositories.dto.CreateBugemonDTO;
import ulb.repositories.dto.PlayerBugemonDTO;
import ulb.repositories.exceptions.BugemonNameIsEmptyException;
import ulb.services.exceptions.BugemonNameAlreadyExistsException;

public class BugemonService {

    private final String playername;
    private final StaticDataRepository staticDataRepository;
    private final BugemonRepository bugemonRepository;

    public BugemonService(StaticDataRepository staticDataRepository, BugemonRepository bugemonRepository,
            String playername) {
        this.playername = playername;
        this.staticDataRepository = staticDataRepository;
        this.bugemonRepository = bugemonRepository;
    }

    public List<Bugemon> getAllDefaultBugemons() {
        return this.staticDataRepository.getAllDefaultBugemons();
    }

    /**
     * Save a new bugemon in the database.
     *
     * @param bugemon
     *            (CreateBugemonDTO) the bugemon to be saved
     * @throws BugemonNameIsEmptyException
     *             if the name of the bugemon is empty
     */
    public void saveNewBugemon(CreateBugemonDTO bugemon)
            throws BugemonNameIsEmptyException, BugemonNameAlreadyExistsException {
        if (bugemon.name().isEmpty()) {
            throw new BugemonNameIsEmptyException("Bugemon name cannot be empty!");
        }

        if (this.getAllDefaultBugemons().stream().anyMatch(b -> b.getName().equals(bugemon.name()))) {
            throw new BugemonNameAlreadyExistsException("Bugemon name already exists!");
        }

        this.staticDataRepository.saveBugemon(bugemon);
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
        this.bugemonRepository.updatePlayerBugemon(
                new PlayerBugemonDTO(this.playername, bugemon.getName(), bugemon.getDefense(), bugemon.getAttack(),
                        bugemon.getInitiative(), bugemon.getMaxHp(), bugemon.getXp(), bugemon.getLevel()));
    }

    /**
     * Saves the level up of a bugemon to the database.
     *
     * @param levelUp
     *            the level up of the bugemon to save
     */
    public void saveLevelUp(LevelUp levelUp) {
        this.saveBugemonState(levelUp.getBugemon());
    }

    public void clearAllPlayerBugemons() {
        this.bugemonRepository.removeAllPlayerBugemon(this.playername);
    }

}
