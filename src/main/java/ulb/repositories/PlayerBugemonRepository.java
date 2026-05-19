package ulb.repositories;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.repositories.dto.PlayerBugemonDTO;

public class PlayerBugemonRepository extends AbstractRepository {
    private static final Logger LOG = LoggerFactory.getLogger(PlayerBugemonRepository.class);

    public PlayerBugemonRepository(DatabaseConnection dbConnection, Map<String, String> queries) {
        super(dbConnection, queries);
    }

    public void removeAllPlayerBugemon(String playername) {
        LOG.debug("Removing all bugemons for playername: {}", playername);
        this.executeUpdate("RemoveAllPlayerBugemons", playername);
    }

    public void savePlayerBugemon(PlayerBugemonDTO d) {
        LOG.debug("Saving player bugemon: {}", d);
        executeUpdate("SavePlayerBugemon", d.playername(), d.bugemonName(), d.currentDefense(), d.currentAttackPower(),
                d.currentInitiative(), d.currentMaxHp(), d.currentXp(), d.currentLevel());
    }

    public void updatePlayerBugemon(PlayerBugemonDTO d) {
        LOG.debug("Updating player bugemon: {}", d);
        executeUpdate("UpdatePlayerBugemon", d.currentDefense(), d.currentAttackPower(), d.currentInitiative(),
                d.currentMaxHp(), d.currentXp(), d.currentLevel(), d.playername(), d.bugemonName());
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
