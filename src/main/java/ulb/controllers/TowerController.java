package ulb.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.models.player.PlayerState;
import ulb.models.tower.TowerState;
import ulb.models.tower.room.Room;
import ulb.services.SaveService;
import ulb.services.TowerService;
import ulb.views.FloorView;
import ulb.views.ViewLoader;

public class TowerController extends Controller<FloorView> implements FloorView.Listener {
    private static final Logger LOG = LoggerFactory.getLogger(TowerController.class);

    private final TowerService towerService;
    private final SaveService saveService;

    private PlayerState playerState;
    private TowerState towerState;

    public TowerController(MetaController metaController, PlayerState playerState, SaveService saveService,
            TowerService towerService) {
        super(metaController, ViewLoader.load(FloorView::new));
        this.view.setListener(this);
        this.playerState = playerState;
        this.saveService = saveService;
        this.towerService = towerService;
        this.towerState = towerService.createTower();
    }

    @Override
    protected void show() {

        this.udpateDisplayedFloor();
    }

    @Override
    public void onRoomClicked(Room room) {
    }

    @Override
    public void onReturnToMainMenu() {
        LOG.info("Player returned to main menu from tower");
        this.metaController.endTowerFlow();
        this.metaController.onMainMenu();
    }

    public void onTowerCombatFinished(boolean playerWon) {
        LOG.info("Tower combat finished, playerWon={}", playerWon);
    }

    private void endTowerFlow(boolean playerWon) {
        this.towerState = null;
        this.metaController.endTowerFlow();
        this.metaController.onCombatFinished(playerWon);
    }

    /**
     * Shows the floor map before continuing the run.
     */
    private void udpateDisplayedFloor() {

        super.show();
    }

    private void updateDisplayedFloorStructure() {

    }
}
