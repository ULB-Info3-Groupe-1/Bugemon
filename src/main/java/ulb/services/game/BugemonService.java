package ulb.services.game;

import java.net.URL;
import java.util.List;

import ulb.common.dto.persistence.CreateBugemonDTO;
import ulb.common.dto.persistence.PlayerBugemonDTO;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.ElementType;
import ulb.models.player.PlayerBugemon;
import ulb.models.team.Team;
import ulb.repositories.BugemonRepository;
import ulb.repositories.StaticRepository;
import ulb.repositories.exceptions.BugemonNameIsEmptyException;
import ulb.services.exceptions.BugemonNameAlreadyExistsException;

/**
 * Service that manages Bugemon data for a specific player.
 *
 * <p>
 * Combines static species data (from {@link ulb.repositories.StaticRepository}) with per-player progression data (from
 * {@link ulb.repositories.BugemonRepository}) to produce {@link ulb.models.player.PlayerBugemon} instances. Also
 * handles the creation of new custom Bugemons and persisting team state.
 */
public class BugemonService {

    private final String playerName;
    private final StaticRepository staticDataRepository;
    private final BugemonRepository bugemonRepository;

    /**
     * Constructs a {@code BugemonService} bound to the given player.
     *
     * @param staticDataRepository
     *            repository providing static species and attack definitions
     * @param bugemonRepository
     *            repository for per-player Bugemon progression
     * @param playerName
     *            the name of the player whose data this service manages
     */
    public BugemonService(StaticRepository staticDataRepository, BugemonRepository bugemonRepository,
            String playerName) {
        this.playerName = playerName;
        this.staticDataRepository = staticDataRepository;
        this.bugemonRepository = bugemonRepository;
    }

    /** Returns all static Bugemon species definitions, including boss species. */
    public List<Bugemon> getDefaultBugemons() {
        return this.staticDataRepository.bugemons();
    }

    /** Returns all available attacks across all element types. */
    public List<Attack> getAttacks() {
        return this.staticDataRepository.attacks();
    }

    /**
     * Returns all attacks whose element type matches {@code bugemonType}.
     *
     * @param bugemonType
     *            the element type to filter by
     */
    public List<Attack> getAttacks(ElementType bugemonType) {
        return this.staticDataRepository.attacks().stream().filter(attack -> attack.type() == bugemonType).toList();
    }

    /**
     * Returns the player's {@link ulb.models.player.PlayerBugemon} for the given species name. If the player has no
     * saved progression for that species, returns a fresh instance at base stats.
     *
     * @param bugemonName
     *            the species name to look up
     * @return the player's {@code PlayerBugemon}, never {@code null}
     * @throws java.util.NoSuchElementException
     *             if the species name does not exist in the static data
     */
    public PlayerBugemon getPlayerBugemon(String bugemonName) {
        Bugemon base = this.staticDataRepository.bugemons().stream().filter(b -> b.name().equals(bugemonName))
                .findFirst().orElseThrow();
        return this.bugemonRepository.findByName(this.playerName, bugemonName).map(dto -> PlayerBugemon.from(base, dto))
                .orElse(new PlayerBugemon(base));
    }

    /**
     * Returns all non-boss species as {@link ulb.models.player.PlayerBugemon} instances for this player. Species
     * without saved progression are returned at base stats.
     */
    public List<PlayerBugemon> getPlayerBugemons() {
        List<PlayerBugemonDTO> playerBugemons = this.bugemonRepository.findAll(this.playerName);

        // players cannot own boss bugemons
        return this.staticDataRepository.bugemons().stream().filter(bugemon -> !bugemon.isBoss())
                // search for the PlayerBugemon corresponding to this Bugemon
                .map(bugemon -> playerBugemons.stream().filter(pb -> pb.bugemonName().equals(bugemon.name()))
                        // if one was found then take that PlayerBugemon
                        .findFirst().map(dto -> PlayerBugemon.from(bugemon, dto))
                        // otherwise create a new PlayerBugemon based on the bugemon
                        .orElseGet(() -> new PlayerBugemon(bugemon)))
                .toList();
    }

    /**
     * Persists the progression of every member of {@code team}.
     *
     * @param team
     *            the team whose members are saved
     */
    public void save(Team team) {
        team.getMembers().forEach(this::savePlayerBugemon);
    }

    public void savePlayerBugemon(PlayerBugemon bugemon) {
        this.bugemonRepository.save(bugemon.toDTO(this.playerName));
    }

    /**
     * Assembles a {@link ulb.common.dto.persistence.CreateBugemonDTO} from the provided parameters without persisting
     * it. Callers should pass the result to {@link #saveNewBugemon(CreateBugemonDTO)}.
     *
     * @param name
     *            the species name
     * @param type
     *            the element type
     * @param spriteUrl
     *            URL pointing to the sprite image file
     * @param defense
     *            base defense stat
     * @param attack
     *            base attack stat
     * @param initiative
     *            base initiative stat
     * @param maxHp
     *            base maximum HP
     * @param attacks
     *            list of attacks to assign to this species
     * @return the assembled DTO, not yet persisted
     */
    public CreateBugemonDTO createBugemon(String name, ElementType type, URL spriteUrl, int defense, int attack,
            int initiative, int maxHp, List<Attack> attacks) {
        return new CreateBugemonDTO(name, type, spriteUrl, defense, attack, initiative, maxHp, false, attacks);
    }

    /**
     * Validates and persists a new custom Bugemon species.
     *
     * @param bugemon
     *            the DTO describing the new species
     * @throws ulb.repositories.exceptions.BugemonNameIsEmptyException
     *             if the species name is blank
     * @throws BugemonNameAlreadyExistsException
     *             if a species with the same name already exists
     */
    public void saveNewBugemon(CreateBugemonDTO bugemon)
            throws BugemonNameIsEmptyException, BugemonNameAlreadyExistsException {
        if (bugemon.name().isEmpty()) {
            throw new BugemonNameIsEmptyException("Bugemon name cannot be empty!");
        }

        if (this.staticDataRepository.bugemons().stream().anyMatch(b -> b.name().equals(bugemon.name()))) {
            throw new BugemonNameAlreadyExistsException("Bugemon name already exists!");
        }

        this.staticDataRepository.saveBugemon(bugemon);
    }

    public void removePlayerBugemons() {
        this.bugemonRepository.removeAll(this.playerName);
    }
}
