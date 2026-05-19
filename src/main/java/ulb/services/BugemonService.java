package ulb.services;

import java.util.ArrayList;
import java.util.List;

import ulb.factories.BugemonFactory;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.ElementType;
import ulb.models.level_up.LevelUp;
import ulb.models.player.PlayerBugemon;
import ulb.models.skills.Skill;
import ulb.models.team.Team;
import ulb.repositories.dto.CreateBugemonDTO;
import ulb.repositories.dto.PlayerBugemonDTO;
import ulb.repositories.exceptions.BugemonNameIsEmptyException;
import ulb.repositories.postgres.PlayerBugemonRepository;
import ulb.repositories.postgres.StaticDataRepository;
import ulb.services.exceptions.BugemonNameAlreadyExistsException;

public class BugemonService {

    private final String playername;
    private final StaticDataRepository staticDataRepository;
    private final PlayerBugemonRepository playerBugemonRepository;
    private final List<Skill> statBonusSkills;

    public BugemonService(StaticDataRepository staticDataRepository, PlayerBugemonRepository playerBugemonRepository,
            String playername, List<Skill> statBonusSkills) {
        this.playername = playername;
        this.staticDataRepository = staticDataRepository;
        this.playerBugemonRepository = playerBugemonRepository;
        this.statBonusSkills = statBonusSkills;
    }

    public List<Bugemon> getAllDefaultBugemons() {
        return this.staticDataRepository.getAllDefaultBugemons();
    }

    /**
     * Get all bugemons of the game and return the PlayerBugemons linked to the
     * bugemon.
     *
     * @return List of PlayerBugemons
     */
    public List<PlayerBugemon> getAllBugemons() {
        List<PlayerBugemonDTO> playerBugemons = this.playerBugemonRepository.getPlayerBugemons(this.playername);
        List<PlayerBugemon> listToReturn = new ArrayList<>();
        for (Bugemon bugemon : this.getAllDefaultBugemons()) {
            playerBugemons.stream().filter(pb -> pb.bugemonName().equals(bugemon.name())).findFirst()
                    .ifPresentOrElse((dto) -> {
                        listToReturn.add(BugemonFactory.createPlayerBugemon(bugemon, dto));
                    }, () -> listToReturn.add(BugemonFactory.createDefaultPlayerBugemon(bugemon)));
        }
        return listToReturn;
    }

    /**
     * Save a new bugemon in the database.
     *
     * @param bugemon
     *                (CreateBugemonDTO) the bugemon to be saved
     * @throws BugemonNameIsEmptyException
     *                                     if the name of the bugemon is empty
     */
    public void saveNewBugemon(CreateBugemonDTO bugemon)
            throws BugemonNameIsEmptyException, BugemonNameAlreadyExistsException {
        if (bugemon.name().isEmpty()) {
            throw new BugemonNameIsEmptyException("Bugemon name cannot be empty!");
        }

        if (this.getAllDefaultBugemons().stream().anyMatch(b -> b.name().equals(bugemon.name()))) {
            throw new BugemonNameAlreadyExistsException("Bugemon name already exists!");
        }

        this.staticDataRepository.saveBugemon(bugemon);
    }

    public Bugemon getBugemonByName(String name) {
        return this.getAllDefaultBugemons().stream().filter(b -> b.name().equals(name)).findFirst().orElse(null);
    }

    /**
     * Get all attacks matching a specific Bugemon type.
     *
     * @param type
     *             type used to filter attacks
     * @return attacks for the provided type
     */
    public List<Attack> getAttacksByType(ElementType type) {
        return this.staticDataRepository.getAllAttacks().values().stream().filter(a -> a.type() == type).toList();
    }

    /**
     * Saves the state of a single bugemon to the database.
     *
     * @param bugemon
     *                the bugemon to save
     */
    public void saveBugemonState(PlayerBugemon bugemon) {
        this.playerBugemonRepository.updatePlayerBugemon(
                new PlayerBugemonDTO(this.playername, bugemon.getName(), bugemon.getDefense(), bugemon.getAttack(),
                        bugemon.getInitiative(), bugemon.getMaxHp(), bugemon.getXp(), bugemon.getLevel()));
    }

    /**
     * Saves the level up of a bugemon to the database.
     *
     * @param levelUp
     *                the level up of the bugemon to save
     */
    public void saveLevelUp(LevelUp levelUp) {
        this.saveBugemonState(levelUp.getBugemon());
    }

    public void clearAllPlayerBugemons() {
        this.playerBugemonRepository.removeAllPlayerBugemon(this.playername);
    }

    public List<Skill> getStatBonusSkills() {
        return this.statBonusSkills;
    }

    public void save(Team activeTeam) {
        activeTeam.getMembers().forEach(this::saveBugemonState);
    }

}
