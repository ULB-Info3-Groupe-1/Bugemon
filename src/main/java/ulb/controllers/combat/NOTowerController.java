package ulb.controllers.combat;

import java.io.IOException;
import javafx.stage.Stage;

import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.controllers.MetaController.Window;
import ulb.models.no_tower.Floor;
import ulb.models.no_tower.NOTower;
import ulb.models.no_tower.room.CombatRoom;
import ulb.models.no_tower.room.RewardRoom;
import ulb.models.no_tower.room.Room;
import ulb.services.BugemonService;
import ulb.services.PlayerService;
import ulb.views.combat.ManualCombatView;

public class NOTowerController extends Controller<ManualCombatView> {
    private NOTower noTower;
    private final PlayerService playerService;
    private final BugemonService bugemonService;
    private boolean runEnded;
    private Stage stage;

    public NOTowerController(MetaController metaController, PlayerService playerService,
            BugemonService bugemonService) {
        super(metaController, new ManualCombatView());
        this.noTower = null;
        this.playerService = playerService;
        this.bugemonService = bugemonService;
    }

    /**
     * Runs the NO Tower flow until a combat starts, the run ends, or the tower is completed. Reward rooms are resolved
     * immediately; combat rooms continue via callback.
     */
    public void runNOTower(Stage newStage) {
        this.stage = newStage;

        if (!this.ensureRunIsReady()) {
            return;
        }

        this.continueRun();
    }

    /**
     * Ensures that a NO Tower run can be started or continued. If the player has no active team, or if the current run
     * has ended, a new run is initialised. If a new run cannot be started.
     *
     * @return
     */
    private boolean ensureRunIsReady() {
        if (this.playerService.getActiveTeam().isEmpty()) {
            this.runEnded = true;
            this.metaController.switchTo(Window.CREATE_TEAM);
            return false;
        }

        if (this.noTower == null || this.runEnded) {
            this.noTower = new NOTower(this.playerService.getActiveTeam(), this.playerService, this.bugemonService);
            this.runEnded = false;
        }

        return true;
    }

    /**
     * Continues the current NO Tower run until a combat room is reached, the run ends, or the tower is completed.
     */
    private void continueRun() {
        while (!this.runEnded) {
            Floor currentFloor = this.noTower.getCurrentFloor();

            if (currentFloor.isComplete()) {
                if (!this.advanceToNextFloorIfPossible()) {
                    return;
                }
                continue;
            }

            Room currentRoom = currentFloor.getCurrentRoom();
            this.handleRoom(currentFloor, currentRoom);

            if (currentRoom instanceof CombatRoom) {
                return;
            }
        }
    }

    private void handleRoom(Floor floor, Room room) {
        if (room instanceof CombatRoom combatRoom) {
            try {
                ManualCombatController manualCombatController = new ManualCombatController(this.metaController,
                        this.playerService, this.bugemonService);
                // FIXME: manualCombatController.setOnCombatFinished(playerWon -> this.handleCombatResult(playerWon,
                // floor));
                manualCombatController.startCombat(combatRoom.combat());
                manualCombatController.display(this.stage);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to initialize manual combat", e);
            }
            return;
        }

        if (room instanceof RewardRoom rewardRoom) {
            this.handleRewardRoom(rewardRoom);
            floor.getNextRooms();
            return;
        }

        throw new IllegalStateException("Unknown room type: " + room.getClass().getSimpleName());
    }

    private void handleCombatResult(boolean playerWon, Floor floor) {
        if (!playerWon) {
            this.runEnded = true;
            this.metaController.endNOTowerFlow();
            this.metaController.switchTo(Window.COMBAT_DEFEAT);
            this.playerService.restoreHpActiveTeam();
            return;
        }

        floor.getNextRooms();
        this.continueRun();
    }

    private void handleRewardRoom(RewardRoom rewardRoom) {
        // TODO: apply player reward once RewardRoom exposes concrete reward choices.
    }

    private boolean advanceToNextFloorIfPossible() {
        if (!this.noTower.isFloorComplete()) {
            throw new IllegalStateException("Current floor is not complete");
        }

        // Reaching the end of floor 9 means the NO Tower run is complete.
        if (this.noTower.getCurrentFloorNumber() == 8) {
            this.runEnded = true;
            this.metaController.endNOTowerFlow();
            this.metaController.switchTo(Window.COMBAT_VICTORY);
            return false;
        }

        this.noTower.goToNextFloor();
        return true;
    }

    public boolean hasActiveRun() {
        return this.noTower != null && !this.runEnded;
    }
}
