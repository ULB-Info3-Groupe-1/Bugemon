package ulb.controllers.combat;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.controllers.Controller;
import ulb.controllers.MetaController;
import ulb.controllers.MetaController.Window;
import ulb.models.combat.Combat;
import ulb.models.tower.Floor;
import ulb.models.tower.FloorNode;
import ulb.models.tower.Tower;
import ulb.models.tower.room.CombatRoom;
import ulb.models.tower.room.EmptyRoom;
import ulb.models.tower.room.RewardRoom;
import ulb.models.tower.room.Room;
import ulb.models.tower.room.RoomVisitor;
import ulb.models.trainer.ManualTrainer;
import ulb.services.BugemonService;
import ulb.services.PlayerService;
import ulb.services.exceptions.NoActiveTeamException;
import ulb.views.FloorView;
import ulb.views.ViewLoader;

public class TowerController extends Controller<FloorView> implements FloorView.Listener, RoomVisitor {
    private static final Logger LOG = LoggerFactory.getLogger(TowerController.class);

    private Optional<Tower> tower;
    private final PlayerService playerService;
    private final BugemonService bugemonService;

    public TowerController(MetaController metaController, PlayerService playerService, BugemonService bugemonService) {
        super(metaController, ViewLoader.load(FloorView::new));
        this.playerService = playerService;
        this.bugemonService = bugemonService;
        this.tower = Optional.empty();

        this.view.setListener(this);
    }

    @Override
    public void onRoomClicked(FloorNode node) {
        Floor currentFloor = this.tower.get().getCurrentFloor();

        currentFloor.moveTo(node);

        FloorNode nextNode = currentFloor.getCurrentPosition();
        Room selectedRoom = nextNode.getRoom();
        LOG.debug("Player moved to node at position {}", nextNode.getPosition());
        this.view.animatePlayerTo(nextNode);

        if (!selectedRoom.isVisited()) {
            LOG.debug("Visiting unvisited room of type {}", selectedRoom.getType());
            selectedRoom.visit(this);
            selectedRoom.setVisited();
        }
        this.refreshFloorViewState();
    }

    public void visitCombatRoom(CombatRoom combatRoom) {
        LOG.info("Entering combat room (boss={})", combatRoom.isBoss());
        Combat combat = combatRoom.getCombat(new ManualTrainer(
                this.playerService.getActiveTeam().orElseThrow(() -> new IllegalStateException("No active team")),
                this.playerService.getInventory()));
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
        this.tower = Optional.empty();
        this.metaController.endTowerFlow();
        this.metaController.onReturnToMainMenu();
    }

    public void onTowerCombatFinished(boolean playerWon) {
        LOG.info("Tower combat finished, playerWon={}", playerWon);
        if (!playerWon) {
            this.tower = Optional.empty();
            try {
                this.playerService.restoreHpActiveTeam();
            } catch (NoActiveTeamException e) {
                throw new IllegalStateException("No active team when combat ended is not possible", e);
            }
            this.metaController.endTowerFlow();
            this.metaController.switchTo(Window.COMBAT_DEFEAT);
            return;
        }

        if (this.tower.get().isCompleted()) {
            LOG.info("Tower completed, switching to victory screen");
            this.tower = Optional.empty();
            this.metaController.endTowerFlow();
            this.metaController.switchTo(Window.COMBAT_VICTORY);
            return;
        }

        if (this.bugemonService.hasPendingLevelUps()) {
            this.metaController.switchTo(Window.LEVEL_UP);
        } else {
            this.metaController.switchTo(Window.NOTOWER);
        }
    }

    /**
     * Runs the Tower flow until a combat starts, the run ends, or the tower is completed. Reward rooms are resolved
     * immediately; combat rooms continue via callback.
     */
    public void runTower() {
        if (this.playerService.getActiveTeam().isEmpty()) {
            LOG.warn("runTower called with no active team, aborting");
            return;
        }

        if (this.tower.isEmpty()) {
            LOG.info("Starting new tower run");
            this.tower = Optional
                    .of(new Tower(this.playerService.getActiveTeam().get(), this.playerService, this.bugemonService));

        }

        this.showFloor();
    }

    /**
     * Shows the floor map before continuing the run.
     */
    private void showFloor() {
        Floor currentFloor = this.tower.get().getCurrentFloor();

        this.view.setFloorNumber(this.tower.get().getCurrentFloorNumber() + 1);
        this.view.setInstruction();
        this.setupFloorStructure(currentFloor);
        this.view.setPlayerPosition(currentFloor.getCurrentPosition());

        this.refreshFloorViewState();
        this.show();
    }

    private void setupFloorStructure(Floor currentFloor) {
        List<FloorNode> floorNodes = currentFloor.getFloorNodes();

        this.view.setFloorNodes(floorNodes);
    }

    private void refreshFloorViewState() {
        this.tower.get().updateRoomsState();
        this.view.refreshRoomStates();
    }

    public boolean hasActiveRun() {
        return this.tower.isPresent();
    }
}
