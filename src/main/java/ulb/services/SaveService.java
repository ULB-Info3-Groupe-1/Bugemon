package ulb.services;

import ulb.models.player.PlayerState;
import ulb.models.tower.TowerState;

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

    public void clear(PlayerState playerState, TowerState towerState) {
        this.bugemonService.removePlayerBugemons();
        this.inventoryService.resetInventory();
        this.teamService.deleteTeams();
        playerState.clear();
        if (towerState != null) {
            towerState.clear();
        }
    }

    public void save(PlayerState playerState, TowerState towerState) {
        this.skillService.save(playerState.getSkillTreeState());
        playerState.getActiveTeam().ifPresent(this.bugemonService::save);
        this.inventoryService.save(playerState.getInventory());
        playerState.getActiveTeam().ifPresent(this.teamService::save);
        if (towerState != null) {
            this.towerService.save(towerState);
        }
    }
}
