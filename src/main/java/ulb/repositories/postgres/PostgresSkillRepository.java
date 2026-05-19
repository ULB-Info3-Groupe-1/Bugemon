package ulb.repositories.postgres;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.repositories.DatabaseConnection;
import ulb.repositories.SkillRepository;

public class PostgresSkillRepository extends AbstractRepository implements SkillRepository {
    private static final Logger LOG = LoggerFactory.getLogger(PostgresSkillRepository.class);

    public PostgresSkillRepository(DatabaseConnection dbConnection, Map<String, String> queries) {
        super(dbConnection, queries);
    }

    @Override
    public int getPlayerSkillPoints(String playername) {
        LOG.debug("Getting skill points for playername: {}", playername);
        return this.executeQuery("GetPlayerSkillPoints", rs -> rs.getInt(DatabaseColumns.COL_SKILL_POINTS), playername)
                .get(0);
    }

    @Override
    public void setPlayerSkillPoints(String playername, int skillPoints) {
        LOG.debug("Setting skill points {} for playername: {}", skillPoints, playername);
        this.executeUpdate("SetPlayerSkillPoints", skillPoints, playername);
    }

    @Override
    public void savePlayerSkill(String playername, String skillId, int level) {
        LOG.debug("Saving skill '{}' at level {} for playername: {}", skillId, level, playername);
        this.executeUpdate("AddPlayerSkill", playername, skillId, level);
    }

    @Override
    public void deletePlayerSkill(String playername, String skillId) {
        LOG.debug("Deleting skill '{}' for playername: {}", skillId, playername);
        this.executeUpdate("DeletePlayerSkill", playername, skillId);
    }

    @Override
    public Map<String, Integer> getPlayerSkills(String playername) {
        LOG.debug("Getting all skills for playername: {}", playername);
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
