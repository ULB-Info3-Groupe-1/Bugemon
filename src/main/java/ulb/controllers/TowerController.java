package ulb.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.Configuration;
import ulb.common.RoomType;
import ulb.models.player.PlayerState;
import ulb.models.team.Team;
import ulb.models.tower.FloorMap;
import ulb.models.tower.TowerState;
import ulb.models.tower.exceptions.IllegalMoveException;
import ulb.models.tower.room.Room;
import ulb.models.utils.Position;
import ulb.services.TeamService;
import ulb.services.TowerService;
import ulb.views.FloorView;
import ulb.views.ViewLoader;

public class TowerController extends Controller<FloorView> implements FloorView.Listener {
    private static final Logger LOG = LoggerFactory.getLogger(TowerController.class);

    private final TowerService towerService;
    private final TeamService teamService;

    private PlayerState playerState;
    private TowerState towerState;

    public TowerController(MetaController metaController, PlayerState playerState, TowerService towerService,
            TeamService teamService) {
        super(metaController, ViewLoader.load(FloorView::new));
        this.view.setListener(this);
        this.playerState = playerState;
        this.towerService = towerService;
        this.teamService = teamService;
    }

    public void startRun() {
        Team activeTeam = this.playerState.getActiveTeam()
                .orElseThrow(() -> new IllegalStateException("Cannot start tower without an active team"));

        Optional<String> savedTeamName = this.towerService.getSavedTeamName();
        if (savedTeamName.isPresent()) {
            Optional<Team> savedTeam = this.teamService.getTeam(savedTeamName.get());
            if (savedTeam.isPresent()) {
                this.towerState = this.towerService.loadSaved(savedTeam.get())
                        .orElseGet(() -> this.towerService.createTower(activeTeam));
                return;
            }
            this.towerService.delete();
        }
        this.towerState = this.towerService.createTower(activeTeam);
    }

    @Override
    protected void show() {
        this.udpateDisplayedFloor();
    }

    @Override
    public void onRoomClicked(int x, int y) {
        FloorMap floorMap = this.towerState.getFloorMap();
        Room room = floorMap.getAllRooms().stream().filter(r -> {
            Position p = floorMap.getPosition(r);
            return p.x() == x && p.y() == y;
        }).findFirst().orElseThrow(() -> new IllegalStateException("No room at (" + x + "," + y + ")"));
        try {
            floorMap.movePlayerTo(room);
        } catch (IllegalMoveException e) {
            LOG.warn("Illegal move attempted to room at ({}, {}): {}", x, y, e.getMessage());
            return;
        }
        this.towerService.save();
        this.dispatchRoomAction(room);
    }

    @Override
    public void onReturnToMainMenu() {
        LOG.info("Player returned to main menu from tower");
        this.metaController.endTowerFlow();
        this.metaController.onMainMenu();
    }

    public void onTowerCombatFinished(boolean playerWon) {
        LOG.info("Tower combat finished, playerWon={}", playerWon);
        if (!playerWon) {
            this.towerService.delete();
            this.towerState = null;
            return;
        }
        this.towerService.save();
        Room currentRoom = this.towerState.getFloorMap().getCurrentRoom();
        if (currentRoom.getType() == RoomType.BOSS) {
            this.advanceFloorState();
        }
    }

    void onBonusRoomExited() {
        this.towerService.save();
        this.udpateDisplayedFloor();
    }

    private void dispatchRoomAction(Room room) {
        if (room.isVisited()) {
            this.udpateDisplayedFloor();
            return;
        }
        switch (room.getType()) {
            case COMBAT -> this.metaController.onStartTowerCombat(this.towerState.getRunTeam(),
                    this.towerState.getCurrentFloor(), false);
            case BOSS -> this.metaController.onStartTowerCombat(this.towerState.getRunTeam(),
                    this.towerState.getCurrentFloor(), true);
            case REWARD -> this.metaController.startRewardFlow(this.towerState.getRunTeam());
            default -> this.udpateDisplayedFloor();
        }
    }

    private void advanceFloorState() {
        int nextFloor = this.towerState.getCurrentFloor() + 1;
        if (nextFloor > Configuration.Game.FLOOR_MAX) {
            this.towerService.delete();
            this.towerState = null;
            return;
        }
        FloorMap nextFloorMap = this.towerService.generateFloor(this.towerState.getSeed(), nextFloor);
        this.towerState = new TowerState(this.towerState.getSeed(), this.towerState.getRunTeam(), nextFloor,
                nextFloorMap);
        this.towerService.setActiveTower(this.towerState);
        this.towerService.save();
    }

    private void udpateDisplayedFloor() {
        this.view.setFloorState(this.towerState.getCurrentFloor(), this.towerService.buildFloorDisplayDTO());
        super.show();
    }
}
