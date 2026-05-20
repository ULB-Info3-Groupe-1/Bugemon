package ulb.repositories.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.models.bugemon.Bugemon;
import ulb.repositories.BugemonRepository;
import ulb.repositories.DatabaseConnection;
import ulb.repositories.dto.PlayerBugemonDTO;

public class PostgresBugemonRepository extends AbstractRepository implements BugemonRepository {
    private static final Logger LOG = LoggerFactory.getLogger(PostgresBugemonRepository.class);
    private final PostgresStaticRepository staticDataRepository;

    public PostgresBugemonRepository(DatabaseConnection dbConnection, Map<String, String> queries,
            PostgresStaticRepository staticDataRepository) {
        super(dbConnection, queries);
        this.staticDataRepository = staticDataRepository;
    }

    @Override
    public List<PlayerBugemonDTO> findAll(String playername) {
        LOG.debug("Finding all bugemons for playername: {}", playername);
        return executeQuery("GetPlayerBugemons", this::mapPlayerBugemon, playername);
    }

    @Override
    public Optional<PlayerBugemonDTO> findByName(String playername, String bugemonName) {
        LOG.debug("Finding bugemon '{}' for playername: {}", bugemonName, playername);
        return executeQuery("GetPlayerBugemonByName", this::mapPlayerBugemon, playername, bugemonName).stream()
                .findFirst();
    }

    @Override
    public Optional<Bugemon> findBase(String name) {
        LOG.debug("Finding base bugemon '{}'", name);
        return this.staticDataRepository.findBugemonByName(name);
    }

    @Override
    public void save(String playername, PlayerBugemonDTO dto) {
        LOG.debug("Saving player bugemon '{}' for playername: {}", dto.bugemonName(), playername);
        executeUpdate("SavePlayerBugemon", dto.playername(), dto.bugemonName(), dto.bonusDefense(),
                dto.bonusAttackPower(), dto.bonusInitiative(), dto.bonusMaxHp(), dto.xp(), dto.level());
    }

    @Override
    public void delete(String playername, String bugemonName) {
        LOG.debug("Deleting player bugemon '{}' for playername: {}", bugemonName, playername);
        executeUpdate("DeletePlayerBugemon", playername, bugemonName);
    }

    @Override
    public void removeAll(String playername) {
        LOG.debug("Removing all bugemons for playername: {}", playername);
        executeUpdate("RemoveAllPlayerBugemons", playername);
    }

    private PlayerBugemonDTO mapPlayerBugemon(ResultSet rs) throws SQLException {
        return new PlayerBugemonDTO(rs.getString(DatabaseColumns.COL_PLAYERNAME),
                rs.getString(DatabaseColumns.COL_BUGEMON_NAME), rs.getInt(DatabaseColumns.COL_CURRENT_DEFENSE),
                rs.getInt(DatabaseColumns.COL_CURRENT_ATTACK), rs.getInt(DatabaseColumns.COL_CURRENT_INITIATIVE),
                rs.getInt(DatabaseColumns.COL_CURRENT_MAX_HP), rs.getInt(DatabaseColumns.COL_CURRENT_XP),
                rs.getInt(DatabaseColumns.COL_CURRENT_LEVEL));
    }
}
