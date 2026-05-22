package ulb.repositories.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.common.dto.persistence.PlayerBugemonDTO;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.repositories.BugemonRepository;
import ulb.repositories.DatabaseConnection;
import ulb.repositories.StaticRepository;

/**
 * PostgreSQL implementation of {@link BugemonRepository}.
 *
 * <p>
 * Player-owned Bugemon are stored with their per-player stat bonuses and current XP/level. Base archetype lookups are
 * delegated to the injected {@link StaticRepository}.
 */
public class PostgresBugemonRepository extends AbstractRepository implements BugemonRepository {
    private static final Logger LOG = LoggerFactory.getLogger(PostgresBugemonRepository.class);
    private final StaticRepository staticDataRepository;

    public PostgresBugemonRepository(DatabaseConnection dbConnection, Map<String, String> queries,
            StaticRepository staticDataRepository) {
        super(dbConnection, queries);
        this.staticDataRepository = staticDataRepository;
    }

    @Override
    public List<PlayerBugemonDTO> findAll(String playerName) {
        LOG.debug("Finding all bugemons for playerName: {}", playerName);
        return executeQuery("GetPlayerBugemons", this::mapPlayerBugemon, playerName);
    }

    @Override
    public Optional<PlayerBugemonDTO> findByName(String playerName, String bugemonName) {
        LOG.debug("Finding bugemon '{}' for playerName: {}", bugemonName, playerName);
        return executeQuery("GetPlayerBugemonByName", this::mapPlayerBugemon, playerName, bugemonName).stream()
                .findFirst();
    }

    @Override
    public Optional<Bugemon> findBase(String name) {
        LOG.debug("Finding base bugemon '{}'", name);
        return this.staticDataRepository.findBugemonByName(name);
    }

    @Override
    public void save(PlayerBugemonDTO dto) {
        LOG.debug("Saving player bugemon '{}' for playerName: {}", dto.bugemonName(), dto.playerName());
        executeUpdate("SavePlayerBugemon", dto.playerName(), dto.bugemonName(), dto.bonusDefense(),
                dto.bonusAttackPower(), dto.bonusInitiative(), dto.bonusMaxHp(), dto.xp(), dto.level(),
                dto.attacks().get(0).id(), dto.attacks().get(1).id(), dto.attacks().get(2).id());
        executeUpdate("UpdatePlayerBugemon", dto.bonusDefense(), dto.bonusAttackPower(), dto.bonusInitiative(),
                dto.bonusMaxHp(), dto.xp(), dto.level(), dto.attacks().get(0).id(), dto.attacks().get(1).id(),
                dto.attacks().get(2).id(), dto.playerName(), dto.bugemonName());
    }

    @Override
    public void delete(String playerName, String bugemonName) {
        LOG.debug("Deleting player bugemon '{}' for playerName: {}", bugemonName, playerName);
        executeUpdate("DeletePlayerBugemon", playerName, bugemonName);
    }

    @Override
    public void removeAll(String playerName) {
        LOG.debug("Removing all bugemons for playerName: {}", playerName);
        executeUpdate("RemoveAllPlayerBugemons", playerName);
    }

    private PlayerBugemonDTO mapPlayerBugemon(ResultSet rs) throws SQLException {
        List<Attack> allAttacks = this.staticDataRepository.attacks();
        List<Attack> attacks = List
                .of(rs.getString(DatabaseColumns.COL_ATTACK_ID_1), rs.getString(DatabaseColumns.COL_ATTACK_ID_2),
                        rs.getString(DatabaseColumns.COL_ATTACK_ID_3))
                .stream().map(id -> allAttacks.stream().filter(a -> a.id().equals(id)).findFirst().orElseThrow())
                .toList();
        return new PlayerBugemonDTO(rs.getString(DatabaseColumns.COL_PLAYERNAME),
                rs.getString(DatabaseColumns.COL_BUGEMON_NAME), rs.getInt(DatabaseColumns.COL_CURRENT_DEFENSE),
                rs.getInt(DatabaseColumns.COL_CURRENT_ATTACK), rs.getInt(DatabaseColumns.COL_CURRENT_INITIATIVE),
                rs.getInt(DatabaseColumns.COL_CURRENT_MAX_HP), rs.getInt(DatabaseColumns.COL_CURRENT_XP),
                rs.getInt(DatabaseColumns.COL_CURRENT_LEVEL), attacks);
    }
}
