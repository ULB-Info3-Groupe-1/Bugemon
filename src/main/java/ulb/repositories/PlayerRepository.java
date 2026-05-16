package ulb.repositories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.repositories.exceptions.PlayernameAlreadyExistsException;

public class PlayerRepository extends AbstractRepository {
    private static final Logger LOG = LoggerFactory.getLogger(PlayerRepository.class);
    private final InventoryRepository inventoryRepository;

    public PlayerRepository(DatabaseConnection dbConnection, InventoryRepository inventoryRepository,
            Map<String, String> queries) {
        super(dbConnection, queries);
        this.inventoryRepository = inventoryRepository;
    }

    // --- PLAYERS ---

    public void createPlayer(String playername) throws PlayernameAlreadyExistsException {
        LOG.debug("Creating player with name: {}", playername);
        try {
            this.executeUpdate("CreatePlayer", playername);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("duplicate key")
                    || (e.getCause() != null && e.getCause().getMessage().contains("duplicate key"))) {
                LOG.warn("Player creation failed due to duplicate playername: {}", playername);
                throw new PlayernameAlreadyExistsException("Player name already exists: " + playername);
            }
            throw e; // rethrow any other unexpected exceptions
        }
        this.inventoryRepository.addDefaultInventory(playername);
    }

    // --- TOWER FLOOR ---

    public void setPlayerCurrentFloor(String playername, int floorNumber) {
        this.executeUpdate("SetPlayerCurrentTowerFloor", floorNumber, playername);
    }

    public int getPlayerCurrentFloor(String playername) {
        return executeQuery("GetPlayerCurrentTowerFloor", rs -> rs.getInt(DatabaseColumns.COL_CURRENT_TOWER_FLOOR),
                playername).get(0);
    }

    public void resetPlayerCurrentFloor(String playername) {
        this.executeUpdate("ResetPlayerCurrentTowerFloor", playername);
    }

    // --- SKILL PLAYER ---

    public int getPlayerSkillPoints(String playername) {
        return this.executeQuery("GetPlayerSkillPoints", rs -> rs.getInt(DatabaseColumns.COL_SKILL_POINTS), playername)
                .get(0);
    }

    public void setPlayerPoints(int skillPoints, String playername) {
        this.executeUpdate("SetPlayerSkillPoints", skillPoints, playername);
    }

    public void savePlayerSkill(String playername, String skillId, int level) {
        this.executeUpdate("AddPlayerSkill", playername, skillId, level);
    }

    public void deletePlayerSkill(String playername, String skillId) {
        this.executeUpdate("DeletePlayerSkill", playername, skillId);
    }

    // return for a skill ID its currentLevel...
    public Map<String, Integer> getPlayerSkills(String playername) {
        List<Map.Entry<String, Integer>> rows = this.executeQuery("GetPlayerSkills", rs -> Map
                .entry(rs.getString(DatabaseColumns.COL_SKILL_ID), rs.getInt(DatabaseColumns.COL_CURRENT_LEVEL)),
                playername);

        Map<String, Integer> skills = new HashMap<>();

        for (Map.Entry<String, Integer> row : rows) {
            skills.put(row.getKey(), row.getValue());
        }

        return skills;
    }
}
