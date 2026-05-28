package bugemon.server.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import bugemon.common.Configuration;
import bugemon.common.RoomState;
import bugemon.common.dto.display.ConnectionDisplayDTO;
import bugemon.common.dto.display.FloorDisplayDTO;
import bugemon.common.dto.display.RoomDisplayDTO;
import bugemon.common.dto.persistence.FloorMapDTO;
import bugemon.common.dto.persistence.RunTeamDTO;
import bugemon.common.dto.persistence.TeamMemberDTO;
import bugemon.common.dto.persistence.TowerDTO;
import bugemon.common.models.run.RunBugemon;
import bugemon.common.models.run.RunTeam;
import bugemon.common.models.team.Team;
import bugemon.common.models.tower.FloorMap;
import bugemon.common.models.tower.TowerState;
import bugemon.common.models.tower.room.Room;
import bugemon.common.models.tower.utils.FloorMapFactory;
import bugemon.common.models.utils.Position;
import bugemon.server.repositories.TowerRepository;

/**
 * Service that manages the lifecycle of a tower run.
 *
 * <p>
 * Responsibilities include creating and loading {@link bugemon.common.models.tower.TowerState} instances, generating floor maps
 * via {@link bugemon.common.models.tower.utils.FloorMapFactory}, persisting and deleting run state, and building the
 * {@link bugemon.common.dto.display.FloorDisplayDTO} consumed by the floor view.
 */
public class TowerService {

    private final String playerName;
    private final TowerRepository towerRepository;
    private TowerState activeTower = null;

    /**
     * Constructs a {@code TowerService} bound to the given player.
     *
     * @param towerRepository
     *            repository for persisting tower run state
     * @param playerName
     *            the name of the player whose tower data this service manages
     */
    public TowerService(TowerRepository towerRepository, String playerName) {
        this.playerName = playerName;
        this.towerRepository = towerRepository;
    }

    /**
     * Creates a fresh tower run starting at floor {@link bugemon.common.Configuration.Game#FLOOR_MIN} using a time-based seed,
     * wraps {@code activeTeam} in a {@link bugemon.common.models.run.RunTeam}, and stores the result as the active tower.
     *
     * @param activeTeam
     *            the player's selected team for this run
     * @return the newly created {@link bugemon.common.models.tower.TowerState}
     */
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

    /**
     * Generates a {@link bugemon.common.models.tower.FloorMap} for the given floor using the provided seed. The same seed and
     * floor always produce the same map layout.
     *
     * @param seed
     *            the deterministic seed for map generation
     * @param floor
     *            the floor number to generate
     * @return the generated {@code FloorMap}
     */
    public FloorMap generateFloor(int seed, int floor) {
        FloorMapFactory floorFactory = new FloorMapFactory(seed);
        return floorFactory.create(floor);
    }

    public void save() {
        if (this.activeTower != null) {
            this.towerRepository.save(this.playerName, this.toDTO(this.activeTower));
        }
    }

    public void delete() {
        this.towerRepository.delete(this.playerName);
        this.activeTower = null;
    }

    /**
     * Builds a {@link bugemon.common.dto.display.FloorDisplayDTO} from the active tower's current floor map. The DTO
     * carries room positions, states, and connection segments needed by the floor view.
     *
     * @return a display DTO for the current floor
     * @throws IllegalStateException
     *             if no tower run is active
     */
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
            return room.isVisited() ? RoomState.VISITED_AVAILABLE : RoomState.AVAILABLE;
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
        return this.towerRepository.find(this.playerName).map(dto -> dto.team().teamName());
    }

    /**
     * Loads a previously saved tower run for the player and reconstructs it using {@code team}.
     *
     * <p>
     * Regenerates the floor map from the saved seed and floor number, restores visited-room state and the current room,
     * and rebuilds each {@link bugemon.common.models.run.RunBugemon} with its saved HP.
     *
     * @param team
     *            the team to use for the restored run (should match the saved team name)
     * @return the restored {@link bugemon.common.models.tower.TowerState}, or an empty optional if no save exists
     */
    public Optional<TowerState> loadSaved(Team team) {
        return this.towerRepository.find(this.playerName).map(dto -> {
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
        RunTeamDTO teamDTO = new RunTeamDTO(this.playerName, towerState.getTeamName(), hpPerMember);

        return new TowerDTO(towerState.getSeed(), floorMapDTO, teamDTO);
    }

    private List<Position> getVisitedRoomsPosition(FloorMap floorMap) {
        return floorMap.getVisitedRooms().stream().map(floorMap::getPosition).map(p -> new Position(p.x(), p.y()))
                .toList();
    }
}
