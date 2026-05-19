package ulb.services;

import ulb.models.player.PlayerState;
import ulb.repositories.exceptions.TeamEmptyException;
import ulb.repositories.exceptions.TeamNameAlreadyExistsException;
import ulb.repositories.exceptions.TeamNameEmptyException;

public class SaveService {
    private final PlayerService playerService;
    private final BugemonService bugemonService;
    private final InventoryService inventoryService;
    private final TeamService teamService;

    private PlayerState playerState;

    public SaveService(PlayerService playerService, BugemonService bugemonService, InventoryService inventoryService,
            TeamService teamService, PlayerState playerState) {
        this.playerService = playerService;
        this.bugemonService = bugemonService;
        this.inventoryService = inventoryService;
        this.teamService = teamService;
        this.playerState = playerState;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public void save() {
        this.playerService.save();
        this.playerState.getActiveTeam().ifPresent(this.bugemonService::save);
        this.inventoryService.save(this.playerState.getInventory());
        this.playerState.getActiveTeam().ifPresent(team -> {
            try {
                this.teamService.saveTeam(team);
            } catch (TeamNameAlreadyExistsException | TeamEmptyException | TeamNameEmptyException e) {
                // TODO: check normal to catch error here ?
                throw new IllegalStateException();
            }
        });
    }
}
