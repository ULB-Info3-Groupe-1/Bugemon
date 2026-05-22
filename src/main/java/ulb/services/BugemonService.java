package ulb.services;

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

public class BugemonService {

    private final String playerName;
    private final StaticRepository staticDataRepository;
    private final BugemonRepository bugemonRepository;

    public BugemonService(StaticRepository staticDataRepository, BugemonRepository bugemonRepository,
            String playerName) {
        this.playerName = playerName;
        this.staticDataRepository = staticDataRepository;
        this.bugemonRepository = bugemonRepository;
    }

    public List<Bugemon> getDefaultBugemons() {
        return this.staticDataRepository.bugemons();
    }

    public List<Attack> getAttacks() {
        return this.staticDataRepository.attacks();
    }

    public List<Attack> getAttacks(ElementType bugemonType) {
        return this.staticDataRepository.attacks().stream().filter(attack -> attack.type() == bugemonType).toList();
    }

    public PlayerBugemon getPlayerBugemon(String bugemonName) {
        Bugemon base = this.staticDataRepository.bugemons().stream().filter(b -> b.name().equals(bugemonName))
                .findFirst().orElseThrow();
        return this.bugemonRepository.findByName(this.playerName, bugemonName).map(dto -> PlayerBugemon.from(base, dto))
                .orElse(new PlayerBugemon(base));
    }

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

    public void save(Team team) {
        team.getMembers().forEach(this::savePlayerBugemon);
    }

    public void savePlayerBugemon(PlayerBugemon bugemon) {
        this.bugemonRepository.save(bugemon.toDTO(this.playerName));
    }

    public CreateBugemonDTO createBugemon(String name, ElementType type, URL spriteUrl, int defense, int attack,
            int initiative, int maxHp, List<Attack> attacks) {
        return new CreateBugemonDTO(name, type, spriteUrl, defense, attack, initiative, maxHp, false, attacks);
    }

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
