package ulb.services;

import java.util.ArrayList;
import java.util.List;

import ulb.models.bugemon.Bugemon;
import ulb.models.player.PlayerBugemon;
import ulb.models.team.Team;
import ulb.repositories.BugemonRepository;
import ulb.repositories.StaticRepository;
import ulb.repositories.dto.CreateBugemonDTO;
import ulb.repositories.dto.PlayerBugemonDTO;
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

    public List<PlayerBugemon> getPlayerBugemons() {
        List<PlayerBugemonDTO> playerBugemons = this.bugemonRepository.findAll(this.playername);
        List<PlayerBugemon> listToReturn = new ArrayList<>();
        for (Bugemon bugemon : this.staticDataRepository.bugemons()) {
            playerBugemons.stream().filter(pb -> pb.bugemonName().equals(bugemon.name())).findFirst()
                    .ifPresentOrElse((dto) -> {
                        listToReturn.add(PlayerBugemon.from(bugemon, dto));
                    }, () -> listToReturn.add(new PlayerBugemon(bugemon)));
        }
        return listToReturn;
    }

    public void save(Team team) {
        team.getMembers().forEach(this::savePlayerBugemon);
    }

    public void savePlayerBugemon(PlayerBugemon bugemon) {
        this.bugemonRepository.save(this.playername, bugemon.toDTO(this.playername));
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
