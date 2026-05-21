package ulb.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ulb.Configuration;
import ulb.models.run.RunBugemon;
import ulb.models.run.RunTeam;
import ulb.models.team.Team;
import ulb.models.tower.FloorMap;
import ulb.models.tower.FloorMap.RoomPosition;
import ulb.models.tower.TowerState;
import ulb.models.tower.utils.FloorMapFactory;
import ulb.repositories.TowerRepository;
import ulb.repositories.dto.FloorMapDTO;
import ulb.repositories.dto.RunTeamDTO;
import ulb.repositories.dto.TeamMemberDTO;
import ulb.repositories.dto.TowerDTO;

public class TowerService {

    private final String playername;
    private final TowerRepository towerRepository;

    public TowerService(TowerRepository towerRepository, String playername) {
        this.playername = playername;
        this.towerRepository = towerRepository;
    }

    public TowerState createTower(Team activeTeam) {
        RunTeam team = RunTeam.fromTeam(activeTeam);

        int seed = this.genTowerSeed();

        int floor = Configuration.Game.FLOOR_MIN;

        FloorMap floorMap = this.generateFloor(seed, floor);

        return new TowerState(seed, team, floor, floorMap);
    }

    public FloorMap generateFloor(int seed, int floor) {
        FloorMapFactory floorFactory = new FloorMapFactory(seed);
        return floorFactory.create(floor);
    }

    public void save(TowerState towerState) {
        this.towerRepository.save(this.playername, this.toDTO(towerState));
    }

    private int genTowerSeed() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
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
