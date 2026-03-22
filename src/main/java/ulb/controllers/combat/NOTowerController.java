package ulb.controllers.combat;

import java.io.IOException;

import ulb.models.combat.Combat;
import ulb.models.no_tower.Floor;
import ulb.models.no_tower.NOTower;
import ulb.models.no_tower.room.Room;
import ulb.models.no_tower.room.CombatRoom;
import ulb.models.no_tower.room.RewardRoom;
import ulb.models.player.Player;
import ulb.models.trainer.ManualTrainer;
import ulb.views.combat.ManualCombatView;
import ulb.controllers.Controller;
import ulb.controllers.MetaController;

public class NOTowerController extends Controller<NOTowerView> {

    // Attributes

    private NOTower noTower;
    private Player player;
    private ManualCombatController manualCombatController;

    // Constructor

    public NOTowerController(MetaController metaController, Player player) throws IOException {
        super(metaController, new NOTowerView(), player);
        this.noTower = new NOTower(player.getActiveTeam());
        this.player = player;
    }

    // Methods

    public void runNOTower() {
        // TODO: implement the NOTower loop, using the existing Combat system for each
        // floor's battle
        Floor currentFloor = noTower.getCurrentFloor();
        while (!currentFloor.isComplete()) {
            Room currentRoom = currentFloor.getCurrentRoom();
            this.handleRoom(currentRoom);
        }
        this.noTower.goToNextFloor();
    }

    private void handleRoom(Room room) throws IllegalStateException {
        if (room instanceof CombatRoom) {
            CombatRoom combatRoom = (CombatRoom) room;
            this.manualCombatController = new ManualCombatController(this.metaController, this.player);
            this.manualCombatController.startCombat();
        } else if (room instanceof RewardRoom) {
            // TODO: implement reward room handling
        } else {
            throw new IllegalStateException("Unknown room type");
        }
    }

    private void handleCombatResult() {
        // TODO: implement combat result handling and kick the player out of the NOTower
        // if they lose.
    }
}
