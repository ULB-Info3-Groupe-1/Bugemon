package ulb.controllers.combat;

import java.util.List;

import ulb.controllers.MetaController;
import ulb.controllers.MetaController.Window;
import ulb.models.combat.Combat;
import ulb.models.tower.Floor;
import ulb.models.tower.FloorNode;
import ulb.models.tower.Tower;
import ulb.models.tower.room.CombatRoom;
import ulb.models.tower.room.RewardRoom;
import ulb.models.tower.room.Room;
import ulb.services.BugemonService;
import ulb.services.PlayerService;

/**
 * Coordinates the NO Tower run. Has no view of its own — delegates combat display to
 * {@link MetaController#startTowerCombat} and receives results via {@link #onTowerCombatFinished}.
 *
 * <p>
 * Room navigation is automatic for now (first available child). When a TowerView is added, {@link #autoAdvance} is the
 * only method that needs to change: present choices instead of picking index 0.
 */
public class TowerController {
    private final MetaController metaController;
    private final PlayerService playerService;
    private final BugemonService bugemonService;

    private Tower tower;
    private boolean runEnded;

    public TowerController(MetaController metaController, PlayerService playerService, BugemonService bugemonService) {
        this.metaController = metaController;
        this.playerService = playerService;
        this.bugemonService = bugemonService;
    }

    /** Entry point called by MetaController on Window.NOTOWER. Creates a fresh Tower if none is active. */
    public void runTower() {
        if (this.tower == null) {
            this.tower = new Tower(this.playerService.getActiveTeam(), this.playerService, this.bugemonService);
            this.runEnded = false;
        }
        this.enterCurrentRoom();
    }

    /**
     * Called by MetaController when a tower combat ends.
     *
     * @param playerWon
     *            true if the player won the combat
     */
    public void onTowerCombatFinished(boolean playerWon) {
        if (!playerWon) {
            this.tower = null;
            this.runEnded = true;
            this.metaController.endTowerFlow();
            this.metaController.switchTo(Window.COMBAT_DEFEAT);
            return;
        }

        Floor floor = this.tower.getCurrentFloor();
        Room room = floor.getCurrentRoom();
        if (room instanceof CombatRoom combatRoom) {
            combatRoom.markCompleted();
        }
        this.autoAdvance(floor);
    }

    public boolean hasActiveRun() {
        return this.tower != null && !this.runEnded;
    }

    // ── Private ───────────────────────────────────────────────────────────────

    private void enterCurrentRoom() {
        Floor floor = this.tower.getCurrentFloor();
        Room room = floor.getCurrentRoom();

        if (room instanceof CombatRoom combatRoom) {
            Combat combat = combatRoom.getCombat(this.tower.getPlayerTrainer());
            this.metaController.startTowerCombat(combat);
            return;
        }

        if (room instanceof RewardRoom rewardRoom) {
            this.handleRewardRoom(rewardRoom);
        }

        // EmptyRoom or handled RewardRoom: move on immediately
        this.autoAdvance(floor);
    }

    /**
     * Moves to the first available child room and recurses, or advances to the next floor when the boss is reached.
     * TODO: when TowerView exists, replace index-0 pick with a player choice.
     */
    private void autoAdvance(Floor floor) {
        List<FloorNode> children = floor.getCurrentPosition().getChildren();
        if (!children.isEmpty()) {
            floor.moveTo(children.get(0));
            this.enterCurrentRoom();
            return;
        }

        if (floor.isComplete()) {
            this.advanceToNextFloor();
        }
        // else: stuck on a dead-end leaf that isn't the boss — generation issue, nothing to do
    }

    private void advanceToNextFloor() {
        if (this.tower.getCurrentFloorNumber() == 8) {
            this.tower = null;
            this.runEnded = true;
            this.metaController.endTowerFlow();
            this.metaController.switchTo(Window.COMBAT_VICTORY);
            return;
        }
        this.tower.goToNextFloor();
        this.enterCurrentRoom();
    }

    private void handleRewardRoom(RewardRoom rewardRoom) {
        // TODO: apply reward when RewardRoom exposes choices
    }
}
