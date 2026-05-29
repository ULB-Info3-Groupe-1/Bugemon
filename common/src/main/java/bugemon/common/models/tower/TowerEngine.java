package bugemon.common.models.tower;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import bugemon.common.models.tower.room.Room;
import bugemon.common.models.tower.utils.FloorMapFactory;
import bugemon.common.models.utils.Position;

/**
 * Stateless tower-run logic shared by the client (which owns the live {@link TowerState} during play) and the server
 * (which persists it).
 *
 * <p>
 * All operations are pure functions of their arguments: creating a fresh run, generating a floor layout
 * deterministically from a seed, building the {@link FloorDisplayDTO} the view renders, restoring a run from its
 * persisted {@link TowerDTO}, and converting a run back to a {@link TowerDTO} for storage.
 */
public final class TowerEngine {

    private TowerEngine() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Creates a fresh tower run starting at {@link Configuration.Game#FLOOR_MIN} with a time-based seed.
     *
     * @param activeTeam
     *            the player's selected team for this run
     * @return the newly created {@link TowerState}
     */
    public static TowerState createTower(Team activeTeam) {
        RunTeam team = RunTeam.fromTeam(activeTeam);
        int seed = genTowerSeed();
        int floor = Configuration.Game.FLOOR_MIN;
        FloorMap floorMap = generateFloor(seed, floor);
        return new TowerState(seed, team, floor, floorMap);
    }

    /**
     * Generates the {@link FloorMap} for the given floor; the same seed and floor always produce the same layout.
     *
     * @param seed
     *            deterministic generation seed
     * @param floor
     *            the floor number to generate
     * @return the generated floor map
     */
    public static FloorMap generateFloor(int seed, int floor) {
        return new FloorMapFactory(seed).create(floor);
    }

    /**
     * Builds the {@link FloorDisplayDTO} for the run's current floor: room positions, states and connection segments.
     *
     * @param towerState
     *            the run whose current floor is rendered
     * @return a display DTO for the current floor
     */
    public static FloorDisplayDTO buildFloorDisplayDTO(TowerState towerState) {
        FloorMap map = towerState.getFloorMap();
        Room current = map.getCurrentRoom();
        Set<Room> clickable = new HashSet<>(map.getClickableRooms());

        List<RoomDisplayDTO> rooms = new ArrayList<>();
        for (Room room : map.getAllRooms()) {
            Position pos = map.getPosition(room);
            rooms.add(new RoomDisplayDTO(pos.x(), pos.y(), room.getType(), roomState(room, current, clickable)));
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

        return new FloorDisplayDTO(towerState.getCurrentFloor(), rooms, connections);
    }

    /**
     * Restores a {@link TowerState} from its persisted snapshot, regenerating the floor map from the saved seed and
     * floor, restoring visited/current rooms, and rebuilding each {@link RunBugemon} with its saved HP.
     *
     * @param dto
     *            the persisted run snapshot
     * @param team
     *            the team to rebuild the run roster from (should match the saved team name)
     * @return the restored {@link TowerState}
     */
    public static TowerState restore(TowerDTO dto, Team team) {
        FloorMap floorMap = generateFloor(dto.seed(), dto.floorMap().floor());

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

        return new TowerState(dto.seed(), runTeam, dto.floorMap().floor(), floorMap);
    }

    /**
     * Converts a {@link TowerState} to its persistable {@link TowerDTO}.
     *
     * @param playerName
     *            the owning player's name, stored on the run-team DTO
     * @param towerState
     *            the run to convert
     * @return the persistable snapshot
     */
    public static TowerDTO toDTO(String playerName, TowerState towerState) {
        Map<TeamMemberDTO, Integer> hpPerMember = new HashMap<>();
        RunTeam runTeam = towerState.getRunTeam();
        for (RunBugemon runBugemon : runTeam.getMembers()) {
            hpPerMember.put(new TeamMemberDTO(runBugemon.getName(), runTeam.getSlotOfMember(runBugemon)),
                    runBugemon.getCurrentHp());
        }
        FloorMap floorMap = towerState.getFloorMap();
        Position currentRoomPos = floorMap.getPosition(floorMap.getCurrentRoom());
        FloorMapDTO floorMapDTO = new FloorMapDTO(towerState.getCurrentFloor(), getVisitedRoomsPosition(floorMap),
                new Position(currentRoomPos.x(), currentRoomPos.y()));
        RunTeamDTO teamDTO = new RunTeamDTO(playerName, towerState.getTeamName(), hpPerMember);
        return new TowerDTO(towerState.getSeed(), floorMapDTO, teamDTO);
    }

    private static RoomState roomState(Room room, Room current, Set<Room> reachable) {
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

    private static List<Position> getVisitedRoomsPosition(FloorMap floorMap) {
        return floorMap.getVisitedRooms().stream().map(floorMap::getPosition).map(p -> new Position(p.x(), p.y()))
                .toList();
    }

    private static int genTowerSeed() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }
}
