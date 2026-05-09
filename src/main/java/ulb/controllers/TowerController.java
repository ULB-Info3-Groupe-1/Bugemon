package ulb.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.models.combat.Combat;
import ulb.models.tower.Floor;
import ulb.models.tower.FloorNode;
import ulb.models.tower.Tower;
import ulb.models.tower.room.CombatRoom;
import ulb.models.tower.room.EmptyRoom;
import ulb.models.tower.room.RewardRoom;
import ulb.models.tower.room.RoomVisitor;
import ulb.services.TowerService;
import ulb.views.FloorView;
import ulb.views.ViewLoader;

public class TowerController extends Controller<FloorView> implements FloorView.Listener, RoomVisitor {
    private static final Logger LOG = LoggerFactory.getLogger(TowerController.class);

    private final TowerService towerService;

    private Tower tower;

    public TowerController(MetaController metaController, TowerService towerService) {
        super(metaController, ViewLoader.load(FloorView::new));
        this.towerService = towerService;
        this.view.setListener(this);
    }

    @Override
    public void onRoomClicked(FloorNode node) {
        this.towerService.movePlayer(this.tower, node, this);
        this.view.animatePlayerTo(node);
        this.view.refreshRoomStates();
    }

    public void visitCombatRoom(CombatRoom combatRoom) {
        LOG.info("Entering combat room (boss={})", combatRoom.isBoss());
        Combat combat = combatRoom.getCombat();
        this.metaController.startTowerCombat(combat);
    }

    public void visitRewardRoom(RewardRoom rewardRoom) {
        LOG.info("Entering reward room");
        // NOT IMPLEMENTED
    }

    public void visitEmptyRoom(EmptyRoom emptyRoom) {
        LOG.debug("Entering empty room");
        // No action needed for empty rooms, but method is here for clarity and future
        // extensibility.
    }

    @Override
    public void onReturnToMainMenu() {
        LOG.info("Player returned to main menu from tower");
        this.metaController.endTowerFlow();
        this.metaController.onMainMenu();
    }

    public void onTowerCombatFinished(boolean playerWon) {
        LOG.info("Tower combat finished, playerWon={}", playerWon);
        this.towerService.handleCombatEnd(this.tower, playerWon);
        if (this.tower.isFinished() || !playerWon) {
            this.endTowerFlow(playerWon);
        } else {
            this.showFloor();
        }
    }

    private void endTowerFlow(boolean playerWon) {
        // reset tower progress because the player has finished the tower or lost
        this.towerService.clearTowerProgress();
        this.tower = null;
        this.metaController.endTowerFlow();
        this.metaController.onCombatFinished(playerWon);
    }

    /**
     * Runs the Tower flow until a combat starts, the run ends, or the tower is completed. Reward rooms are resolved
     * immediately; combat rooms continue via callback.
     */
    public void runTower() {
        this.tower = this.towerService.createTower();
        this.showFloor();
    }

    /**
     * Shows the floor map before continuing the run.
     */
    private void showFloor() {
        this.view.setFloorNumber(this.tower.getCurrentFloorNumber());
        this.view.setInstruction();
        this.setupFloorStructure(this.tower.getCurrentFloor());
        this.view.setPlayerPosition(this.tower.getPlayerPosition());
        this.view.refreshRoomStates();
        this.show();
    }

    private void setupFloorStructure(Floor currentFloor) {
        List<FloorNode> floorNodes = currentFloor.getFloorNodes();

        this.view.setFloorNodes(floorNodes);
    }
}
