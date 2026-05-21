package ulb.repositories.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.common.dto.persistence.FloorMapDTO;
import ulb.common.dto.persistence.RunTeamDTO;
import ulb.common.dto.persistence.TeamMemberDTO;
import ulb.common.dto.persistence.TowerDTO;
import ulb.models.tower.FloorMap.RoomPosition;
import ulb.repositories.DatabaseConnection;
import ulb.repositories.TowerRepository;

public class PostgresTowerRepository extends AbstractRepository implements TowerRepository {
    private static final Logger LOG = LoggerFactory.getLogger(PostgresTowerRepository.class);

    private record TowerRunRow(int seed, String teamName, int floor) {
    }

    private record MemberHpRow(String bugemonName, int slotPosition, int currentHp) {
    }

    public PostgresTowerRepository(DatabaseConnection dbConnection, Map<String, String> queries) {
        super(dbConnection, queries);
    }

    @Override
    public void save(String playerName, TowerDTO dto) {
        LOG.debug("Saving tower run for playerName: {}", playerName);

        executeUpdate("UpsertTowerRun", playerName, dto.seed(), dto.team().teamName(), dto.floorMap().floor());

        executeUpdate("DeleteTowerVisitedRooms", playerName);
        dto.floorMap().visitedRoomsPosition()
                .forEach(pos -> executeUpdate("InsertTowerVisitedRoom", playerName, pos.row(), pos.col()));

        dto.team().hpPerMember().forEach((member, hp) -> executeUpdate("UpsertTowerMemberHp", playerName,
                member.bugemonName(), member.slotPosition(), hp));
    }

    @Override
    public Optional<TowerDTO> find(String playerName) {
        LOG.debug("Finding tower run for playerName: {}", playerName);

        List<TowerRunRow> runs = executeQuery("GetTowerRun", this::mapTowerRunRow, playerName);
        if (runs.isEmpty()) {
            return Optional.empty();
        }

        TowerRunRow run = runs.get(0);

        List<RoomPosition> visitedRooms = executeQuery("GetTowerVisitedRooms", this::mapRoomPosition, playerName);

        Map<TeamMemberDTO, Integer> hpPerMember = executeQuery("GetTowerTeamHp", this::mapMemberHpRow, playerName)
                .stream().collect(Collectors.toMap(r -> new TeamMemberDTO(r.bugemonName(), r.slotPosition()),
                        MemberHpRow::currentHp));

        FloorMapDTO floorMapDTO = new FloorMapDTO(run.floor(), visitedRooms);
        RunTeamDTO teamDTO = new RunTeamDTO(playerName, run.teamName(), hpPerMember);

        return Optional.of(new TowerDTO(run.seed(), floorMapDTO, teamDTO));
    }

    @Override
    public void delete(String playerName) {
        LOG.debug("Deleting tower run for playerName: {}", playerName);
        executeUpdate("DeleteTowerRun", playerName);
    }

    private TowerRunRow mapTowerRunRow(ResultSet rs) throws SQLException {
        return new TowerRunRow(rs.getInt(DatabaseColumns.COL_SEED), rs.getString(DatabaseColumns.COL_TEAM_NAME),
                rs.getInt(DatabaseColumns.COL_FLOOR));
    }

    private RoomPosition mapRoomPosition(ResultSet rs) throws SQLException {
        return new RoomPosition(rs.getInt(DatabaseColumns.COL_ROW), rs.getInt(DatabaseColumns.COL_COL));
    }

    private MemberHpRow mapMemberHpRow(ResultSet rs) throws SQLException {
        return new MemberHpRow(rs.getString(DatabaseColumns.COL_BUGEMON_NAME),
                rs.getInt(DatabaseColumns.COL_SLOT_POSITION), rs.getInt(DatabaseColumns.COL_CURRENT_HP));
    }
}
