package ulb.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import ulb.Configuration;
import ulb.models.player.PlayerState;
import ulb.models.run.RunBugemon;
import ulb.models.run.RunTeam;
import ulb.models.tower.Floor;
import ulb.models.tower.FloorMap;
import ulb.models.tower.FloorMap.RoomPosition;
import ulb.models.tower.TowerState;
import ulb.models.tower.utils.FloorFactory;
import ulb.repositories.TowerRepository;
import ulb.repositories.dto.FloorMapDTO;
import ulb.repositories.dto.RunTeamDTO;
import ulb.repositories.dto.TeamMemberDTO;
import ulb.repositories.dto.TowerDTO;

public class TowerService {

    private final String playername;
    private final PlayerState playerState;
    private final TowerRepository towerRepository;

    public TowerService(PlayerState playerState, TowerRepository towerRepository, String playername) {
        this.playername = playername;
        this.playerState = playerState;
        this.towerRepository = towerRepository;
    }

    public TowerState createTower() {
        RunTeam activeTeam = RunTeam.fromTeam(playerState.getActiveTeam()
                .orElseThrow(() -> new IllegalStateException("Cannot start a run without an active team")));
        int seed = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        Random random = new Random(seed);
        FloorFactory floorFactory = new FloorFactory(random);
        Floor floor = floorFactory.create();

        return new TowerState(seed, activeTeam, Configuration.Game.FLOOR_MIN, floor);

    }

    public Floor generateFloor(TowerState towerState, int targetFloorNumber) {
        int seed = Objects.hash(towerState.getSeed(), targetFloorNumber);
        Random random = new Random(seed);
        FloorFactory floorFactory = new FloorFactory(random);
        return floorFactory.create();
    }

    public void save(TowerState towerState) {
        towerRepository.save(playername, toDTO(towerState));
    }

    private TowerDTO toDTO(TowerState towerState) {
        Map<TeamMemberDTO, Integer> hpPerMember = new HashMap<>();
        RunTeam runTeam = towerState.getRunTeam();
        for (RunBugemon runBugemon : runTeam.getMembers()) {
            hpPerMember.put(new TeamMemberDTO(runBugemon.getName(), runTeam.getSlotOfMember(runBugemon)),
                    runBugemon.getCurrentHp());
        }
        FloorMapDTO floorMapDTO = new FloorMapDTO(towerState.getCurrentFloor(),
                this.getVisitedRoomsPosition(towerState.getFloorMap()));
        RunTeamDTO teamDTO = new RunTeamDTO(this.playername, towerState.getTeamName(), hpPerMember);

        return new TowerDTO(towerState.getSeed(), floorMapDTO, teamDTO);
    }

    private List<RoomPosition> getVisitedRoomsPosition(FloorMap floorMap) {
        return floorMap.getVisitedRooms().stream().map(floorMap::getPosition).toList();
    }
}
