package bugemon.server.repositories.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bugemon.common.dto.persistence.FloorMapDTO;
import bugemon.common.dto.persistence.RunTeamDTO;
import bugemon.common.dto.persistence.TeamMemberDTO;
import bugemon.common.dto.persistence.TowerDTO;
import bugemon.common.models.utils.Position;
import bugemon.server.repositories.DatabaseConnection;
import bugemon.server.repositories.TowerRepository;

/**
 * PostgreSQL implementation of {@link TowerRepository}.
 *
 * <p>
 * Persists a tower-run snapshot as three related record sets: the run header (seed, team name, current floor/position),
 * visited room positions, and per-member HP values. Saving replaces all three sets atomically via
 * upsert/delete-then-insert operations.
 */
public class PostgresTowerRepository extends AbstractRepository implements TowerRepository {
    private static final Logger LOG = LoggerFactory.getLogger(PostgresTowerRepository.class);

    private record TowerRunRow(int seed, String teamName, int floor, int currentRow, int currentCol) {
    }

    private record MemberHpRow(String bugemonName, int slotPosition, int currentHp) {
    }

    public PostgresTowerRepository(DatabaseConnection dbConnection, Map<String, String> queries) {
        super(dbConnection, queries);
    }

    @Override
    public void save(String playerName, TowerDTO dto) {
        LOG.debug("Saving tower run for playerName: {}", playerName);

        Position cur = dto.floorMap().currentRoomPosition();
        executeUpdate("UpsertTowerRun", playerName, dto.seed(), dto.team().teamName(), dto.floorMap().floor(), cur.x(),
                cur.y());

        executeUpdate("DeleteTowerVisitedRooms", playerName);
        dto.floorMap().visitedRoomsPosition()
                .forEach(pos -> executeUpdate("InsertTowerVisitedRoom", playerName, pos.x(), pos.y()));

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

        List<Position> visitedRooms = executeQuery("GetTowerVisitedRooms",
                rs -> new Position(rs.getInt(DatabaseColumns.COL_ROW), rs.getInt(DatabaseColumns.COL_COL)), playerName);

        Map<TeamMemberDTO, Integer> hpPerMember = executeQuery("GetTowerTeamHp", this::mapMemberHpRow, playerName)
                .stream().collect(Collectors.toMap(r -> new TeamMemberDTO(r.bugemonName(), r.slotPosition()),
                        MemberHpRow::currentHp));

        FloorMapDTO floorMapDTO = new FloorMapDTO(run.floor(), visitedRooms,
                new Position(run.currentRow(), run.currentCol()));
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
                rs.getInt(DatabaseColumns.COL_FLOOR), rs.getInt("current_row"), rs.getInt("current_col"));
    }

    private MemberHpRow mapMemberHpRow(ResultSet rs) throws SQLException {
        return new MemberHpRow(rs.getString(DatabaseColumns.COL_BUGEMON_NAME),
                rs.getInt(DatabaseColumns.COL_SLOT_POSITION), rs.getInt(DatabaseColumns.COL_CURRENT_HP));
    }
}
