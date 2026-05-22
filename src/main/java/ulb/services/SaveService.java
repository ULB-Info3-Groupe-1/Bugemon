package ulb.services;

import ulb.models.player.PlayerState;

public class SaveService {
    private final SkillService skillService;
    private final BugemonService bugemonService;
    private final InventoryService inventoryService;
    private final TeamService teamService;
    private final TowerService towerService;

    public SaveService(SkillService skillService, BugemonService bugemonService, InventoryService inventoryService,
            TeamService teamService, TowerService towerService) {
        this.skillService = skillService;
        this.bugemonService = bugemonService;
        this.inventoryService = inventoryService;
        this.teamService = teamService;
        this.towerService = towerService;
    }

    public void clear(PlayerState playerState) {
        this.bugemonService.removePlayerBugemons();
        this.teamService.deleteTeams();
        this.teamService.unSetActiveTeam();
        playerState.clear();
        this.inventoryService.getDefaultInventory().items()
                .forEach((item, qty) -> playerState.getInventory().addItem(item, qty));
        this.inventoryService.save(playerState.getInventory());
        this.towerService.delete();
    }

    public void save(PlayerState playerState) {
        this.skillService.save(playerState.getSkillTreeState());
        playerState.getActiveTeam().ifPresent(this.bugemonService::save);
        this.inventoryService.save(playerState.getInventory());
        playerState.getActiveTeam().ifPresent(this.teamService::save);
        this.towerService.save();
    }
}
