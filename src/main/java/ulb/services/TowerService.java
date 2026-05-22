package ulb.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import ulb.Configuration;
import ulb.common.RoomState;
import ulb.common.dto.display.ConnectionDisplayDTO;
import ulb.common.dto.display.FloorDisplayDTO;
import ulb.common.dto.display.RoomDisplayDTO;
import ulb.common.dto.persistence.FloorMapDTO;
import ulb.common.dto.persistence.RunTeamDTO;
import ulb.common.dto.persistence.TeamMemberDTO;
import ulb.common.dto.persistence.TowerDTO;
import ulb.models.run.RunBugemon;
import ulb.models.run.RunTeam;
import ulb.models.team.Team;
import ulb.models.tower.FloorMap;
import ulb.models.tower.TowerState;
import ulb.models.tower.room.Room;
import ulb.models.tower.utils.FloorMapFactory;
import ulb.models.utils.Position;
import ulb.repositories.TowerRepository;

public class TowerService {

    private final String playername;
    private final TowerRepository towerRepository;
    private TowerState activeTower = null;

    public TowerService(TowerRepository towerRepository, String playername) {
        this.playername = playername;
        this.towerRepository = towerRepository;
    }

    public TowerState createTower(Team activeTeam) {
        RunTeam team = RunTeam.fromTeam(activeTeam);
        int seed = this.genTowerSeed();
        int floor = Configuration.Game.FLOOR_MIN;
        FloorMap floorMap = this.generateFloor(seed, floor);
        this.activeTower = new TowerState(seed, team, floor, floorMap);
        return this.activeTower;
    }

    public void setActiveTower(TowerState towerState) {
        this.activeTower = towerState;
    }

    public Optional<TowerState> getActiveTower() {
        return Optional.ofNullable(this.activeTower);
    }

    public FloorMap generateFloor(int seed, int floor) {
        FloorMapFactory floorFactory = new FloorMapFactory(seed);
        return floorFactory.create(floor);
    }

    public void save() {
        if (this.activeTower != null) {
            this.towerRepository.save(this.playername, this.toDTO(this.activeTower));
        }
    }

    public void delete() {
        this.towerRepository.delete(this.playername);
        this.activeTower = null;
    }

    public FloorDisplayDTO buildFloorDisplayDTO() {
        if (this.activeTower == null) {
            throw new IllegalStateException("No active tower run");
        }
        FloorMap map = this.activeTower.getFloorMap();
        Room current = map.getCurrentRoom();
        Set<Room> clickable = new HashSet<>(map.getClickableRooms());

        List<RoomDisplayDTO> rooms = new ArrayList<>();
        for (Room room : map.getAllRooms()) {
            Position pos = map.getPosition(room);
            rooms.add(new RoomDisplayDTO(pos.x(), pos.y(), room.getType(), this.roomState(room, current, clickable)));
        }

        Set<Room> processed = new HashSet<>();
        List<ConnectionDisplayDTO> connections = new ArrayList<>();
        for (Room room : map.getAllRooms()) {
            Position posA = map.getPosition(room);
            for (Room neighbor : map.getNeighbors(room)) {
                if (!processed.contains(neighbor)) {
                    Position posB = map.getPosition(neighbor);
                    connections.add(new ConnectionDisplayDTO(posA.x(), posA.y(), posB.x(), posB.y()));
                }
            }
            processed.add(room);
        }

        return new FloorDisplayDTO(this.activeTower.getCurrentFloor(), rooms, connections);
    }

    private RoomState roomState(Room room, Room current, Set<Room> reachable) {
        if (room == current) {
            return RoomState.CURRENT;
        }
        if (reachable.contains(room)) {
            return RoomState.AVAILABLE;
        }
        if (room.isVisited()) {
            return RoomState.VISITED;
        }
        return RoomState.LOCKED;
    }

    private int genTowerSeed() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    public Optional<String> getSavedTeamName() {
        return this.towerRepository.find(this.playername).map(dto -> dto.team().teamName());
    }

    public Optional<TowerState> loadSaved(Team team) {
        return this.towerRepository.find(this.playername).map(dto -> {
            FloorMap floorMap = this.generateFloor(dto.seed(), dto.floorMap().floor());

            Set<Position> visitedPositions = new HashSet<>(dto.floorMap().visitedRoomsPosition());
            Position currentPos = dto.floorMap().currentRoomPosition();
            floorMap.getAllRooms().stream().filter(r -> visitedPositions.contains(floorMap.getPosition(r)))
                    .forEach(Room::markAsVisited);
            floorMap.getAllRooms().stream().filter(r -> floorMap.getPosition(r).equals(currentPos)).findFirst()
                    .ifPresent(floorMap::setCurrentRoom);

            Map<String, Integer> hpByName = new HashMap<>();
            dto.team().hpPerMember().forEach((member, hp) -> hpByName.put(member.bugemonName(), hp));
            List<RunBugemon> members = team.getMembers().stream()
                    .map(pb -> new RunBugemon(pb, hpByName.getOrDefault(pb.getName(), pb.getMaxHp()))).toList();
            RunTeam runTeam = new RunTeam(dto.team().teamName(), members);

            this.activeTower = new TowerState(dto.seed(), runTeam, dto.floorMap().floor(), floorMap);
            return this.activeTower;
        });
    }

    private TowerDTO toDTO(TowerState towerState) {
        Map<TeamMemberDTO, Integer> hpPerMember = new HashMap<>();
        RunTeam runTeam = towerState.getRunTeam();
        for (RunBugemon runBugemon : runTeam.getMembers()) {
            hpPerMember.put(new TeamMemberDTO(runBugemon.getName(), runTeam.getSlotOfMember(runBugemon)),
                    runBugemon.getCurrentHp());
        }
        FloorMap floorMap = towerState.getFloorMap();
        Position currentRoomPos = floorMap.getPosition(floorMap.getCurrentRoom());
        FloorMapDTO floorMapDTO = new FloorMapDTO(towerState.getCurrentFloor(), this.getVisitedRoomsPosition(floorMap),
                new Position(currentRoomPos.x(), currentRoomPos.y()));
        RunTeamDTO teamDTO = new RunTeamDTO(this.playername, towerState.getTeamName(), hpPerMember);

        return new TowerDTO(towerState.getSeed(), floorMapDTO, teamDTO);
    }

    private List<Position> getVisitedRoomsPosition(FloorMap floorMap) {
        return floorMap.getVisitedRooms().stream().map(r -> floorMap.getPosition(r))
                .map(p -> new Position(p.x(), p.y())).toList();
    }
}
