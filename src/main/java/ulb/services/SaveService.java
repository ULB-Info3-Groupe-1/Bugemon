package ulb.services;

import ulb.models.player.PlayerState;

public class SaveService {
    private final PlayerService playerService;
    private final BugemonService bugemonService;
    private final InventoryService inventoryService;
    private final TeamService teamService;

    public SaveService(PlayerService playerService, BugemonService bugemonService, InventoryService inventoryService,
            TeamService teamService, PlayerState playerState) {
        this.playerService = playerService;
        this.bugemonService = bugemonService;
        this.inventoryService = inventoryService;
        this.teamService = teamService;
    }

    public void save(PlayerState playerState) {
        this.playerService.save();
        playerState.getActiveTeam().ifPresent(this.bugemonService::save);
        this.inventoryService.save(playerState.getInventory());
        playerState.getActiveTeam().ifPresent(this.teamService::save);
    }
}
