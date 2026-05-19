package ulb.repositories.postgres;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.repositories.BugemonRepository;
import ulb.repositories.dto.PlayerBugemonDTO;

public class PostgresBugemonRepository extends AbstractRepository implements BugemonRepository {
    private static final Logger LOG = LoggerFactory.getLogger(PostgresBugemonRepository.class);

    public PostgresBugemonRepository(DatabaseConnection dbConnection, Map<String, String> queries) {
        super(dbConnection, queries);
    }

    @Override
    public List<PlayerBugemonDTO> findAll(String playername) {
        LOG.debug("Finding all bugemons for playername: {}", playername);
        return this.getPlayerBugemons(playername);
    }

    @Override
    public void removeAll(String playername) {
        LOG.debug("Removing all bugemons for playername: {}", playername);
        this.executeUpdate("RemoveAllPlayerBugemons", playername);
    }

    @Override
    public Optional<PlayerBugemonDTO> findByName(String playername, String bugemonName) {
        LOG.debug("Finding bugemon '{}' for playername: {}", bugemonName, playername);
        return this.getPlayerBugemons(playername).stream()
                .filter(b -> b.bugemonName().equals(bugemonName))
                .findFirst();
    }

    @Override
    public void save(String playername, PlayerBugemonDTO playerBugemon) {
        LOG.debug("Saving player bugemon '{}' for playername: {}", playerBugemon.bugemonName(), playername);
        executeUpdate("SavePlayerBugemon", playerBugemon.playername(), playerBugemon.bugemonName(),
                playerBugemon.currentDefense(), playerBugemon.currentAttackPower(),
                playerBugemon.currentInitiative(), playerBugemon.currentMaxHp(), playerBugemon.currentXp(),
                playerBugemon.currentLevel());
    }

    @Override
    public void delete(String playername, String bugemonName) {
        LOG.debug("Deleting player bugemon '{}' for playername: {}", bugemonName, playername);
        executeUpdate("DeletePlayerBugemon", playername, bugemonName);
    }

    public List<PlayerBugemonDTO> getPlayerBugemons(String playername) {
        LOG.debug("Getting bugemons for playername: {}", playername);
        return executeQuery("GetPlayerBugemons",
                rs -> new PlayerBugemonDTO(rs.getString(DatabaseColumns.COL_PLAYERNAME),
                        rs.getString(DatabaseColumns.COL_BUGEMON_NAME), rs.getInt(DatabaseColumns.COL_CURRENT_DEFENSE),
                        rs.getInt(DatabaseColumns.COL_CURRENT_ATTACK),
                        rs.getInt(DatabaseColumns.COL_CURRENT_INITIATIVE),
                        rs.getInt(DatabaseColumns.COL_CURRENT_MAX_HP), rs.getInt(DatabaseColumns.COL_CURRENT_XP),
                        rs.getInt(DatabaseColumns.COL_CURRENT_LEVEL)),
                playername);
    }

}
