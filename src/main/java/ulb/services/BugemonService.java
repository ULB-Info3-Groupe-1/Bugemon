package ulb.services;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ulb.common.dto.PlayerBugemonDTO;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.ElementType;
import ulb.models.player.PlayerBugemon;
import ulb.models.team.Team;
import ulb.repositories.BugemonRepository;
import ulb.repositories.StaticRepository;
import ulb.repositories.dto.CreateBugemonDTO;
import ulb.repositories.exceptions.BugemonNameIsEmptyException;
import ulb.services.exceptions.BugemonNameAlreadyExistsException;

public class BugemonService {

    private final String playername;
    private final StaticRepository staticDataRepository;
    private final BugemonRepository bugemonRepository;

    public BugemonService(StaticRepository staticDataRepository, BugemonRepository bugemonRepository,
            String playername) {
        this.playername = playername;
        this.staticDataRepository = staticDataRepository;
        this.bugemonRepository = bugemonRepository;
    }

    public List<Bugemon> getDefaultBugemons() {
        return this.staticDataRepository.bugemons();
    }

    public List<Attack> getAttacks(ElementType bugemonType) {
        return this.staticDataRepository.attacks().stream().filter(attack -> attack.type() == bugemonType).toList();
    }

    public PlayerBugemon getPlayerBugemon(String bugemonName) {
        return this.bugemonRepository.findByName(this.playername, bugemonName)
                .map(dto -> PlayerBugemon.from(this.staticDataRepository.bugemons().stream()
                        .filter(b -> b.name().equals(bugemonName)).findFirst().orElseThrow(), dto))
                .orElse(new PlayerBugemon(this.staticDataRepository.bugemons().stream()
                        .filter(b -> b.name().equals(bugemonName)).findFirst().orElseThrow()));
    }

    public List<PlayerBugemon> getPlayerBugemons() {
        List<PlayerBugemonDTO> playerBugemons = this.bugemonRepository.findAll(this.playername);
        List<PlayerBugemon> listToReturn = new ArrayList<>();
        for (Bugemon bugemon : this.staticDataRepository.bugemons()) {
            playerBugemons.stream().filter(pb -> pb.getName().equals(bugemon.name())).findFirst()
                    .ifPresentOrElse(dto -> listToReturn.add(PlayerBugemon.from(bugemon, dto)), () -> listToReturn.add(new PlayerBugemon(bugemon)));
        }
        return listToReturn;
    }

    public void save(Team team) {
        team.getMembers().forEach(this::savePlayerBugemon);
    }

    public void savePlayerBugemon(PlayerBugemon bugemon) {
        this.bugemonRepository.save(this.playername, bugemon.toDTO(this.playername));
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
        this.bugemonRepository.removeAll(this.playername);
    }
}
