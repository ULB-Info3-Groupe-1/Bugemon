package ulb.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.models.combat.Combat;
import ulb.models.tower.Floor;
import ulb.models.tower.FloorNode;
import ulb.models.tower.Tower;
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
    protected void show() {
        this.tower = this.towerService.createTower();
        this.udpateDisplayedFloor();
    }

    @Override
    public void onRoomClicked(FloorNode node) {
        FloorNode before = this.tower.getPlayerPosition();
        this.towerService.movePlayer(this.tower, node, this);
        FloorNode after = this.tower.getPlayerPosition();
        if (!after.equals(before)) {
            this.view.animatePlayerTo(after);
        }
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
            this.udpateDisplayedFloor();
        }
    }

    private void endTowerFlow(boolean playerWon) {
        // reset tower progress because the player has finished the tower or lost
        this.tower = null;
        this.metaController.endTowerFlow();
        this.metaController.onCombatFinished(playerWon);
    }

    /**
     * Shows the floor map before continuing the run.
     */
    private void udpateDisplayedFloor() {
        this.view.setFloorNumber(this.tower.getCurrentFloorNumber());
        this.view.setInstruction();
        this.updateDisplayedFloorStructure(this.tower.getCurrentFloor());
        this.view.setPlayerPosition(this.tower.getPlayerPosition());
        this.view.refreshRoomStates();
        super.show();
    }

    private void updateDisplayedFloorStructure(Floor currentFloor) {
        List<FloorNode> floorNodes = currentFloor.getFloorNodes();
        this.view.setFloorNodes(floorNodes);
    }
}
