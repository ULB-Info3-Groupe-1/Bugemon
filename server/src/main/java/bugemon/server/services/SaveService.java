package bugemon.server.services;

import bugemon.common.models.player.PlayerState;

/**
 * Facade service that orchestrates full-state save and reset operations across all other services.
 *
 * <p>
 * Delegates to {@link BugemonService}, {@link TeamService}, {@link InventoryService}, {@link SkillService}, and
 * {@link TowerService} so that controllers only need a single dependency for persistence operations.
 */
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

    /**
     * Resets all persistent player data to a new-game state.
     *
     * <p>
     * Removes all Bugemon progression, deletes all teams, clears the tower run, resets the player state, seeds the
     * inventory with the default starting items, and persists the fresh inventory.
     *
     * @param playerState
     *            the in-memory player state to clear and reinitialise
     */
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

    /**
     * Persists the current in-progress game state: skill tree, active team Bugemon progression, inventory, and the
     * tower run.
     *
     * @param playerState
     *            the in-memory player state to persist
     */
    public void save(PlayerState playerState) {
        this.skillService.save(playerState.getSkillTreeState());
        playerState.getActiveTeam().ifPresent(this.bugemonService::save);
        this.inventoryService.save(playerState.getInventory());
        playerState.getActiveTeam().ifPresent(this.teamService::save);
        this.towerService.save();
    }
}
