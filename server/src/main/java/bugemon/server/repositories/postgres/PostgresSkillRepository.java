package bugemon.server.repositories.postgres;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bugemon.common.dto.persistence.SkillDTO;
import bugemon.server.repositories.DatabaseConnection;
import bugemon.server.repositories.SkillRepository;

/**
 * PostgreSQL implementation of {@link SkillRepository}.
 *
 * <p>
 * Saving atomically replaces all skill rows (delete-then-insert) and updates the player's skill-point balance in a
 * single logical operation.
 */
public class PostgresSkillRepository extends AbstractRepository implements SkillRepository {
    private static final Logger LOG = LoggerFactory.getLogger(PostgresSkillRepository.class);

    public PostgresSkillRepository(DatabaseConnection dbConnection, Map<String, String> queries) {
        super(dbConnection, queries);
    }

    @Override
    public List<SkillDTO> findAll(String playerName) {
        LOG.debug("Finding all skills for playerName: {}", playerName);
        return this.executeQuery("GetPlayerSkills", rs -> new SkillDTO(rs.getString(DatabaseColumns.COL_SKILL_ID),
                rs.getInt(DatabaseColumns.COL_CURRENT_LEVEL)), playerName);
    }

    @Override
    public SkillDTO findById(String playerName, String skillId) {
        LOG.debug("Finding skill '{}' for playerName: {}", skillId, playerName);
        return this.findAll(playerName).stream().filter(s -> s.skillId().equals(skillId)).findFirst().orElse(null);
    }

    @Override
    public int findSkillLevel(String playerName, String skillId) {
        SkillDTO dto = this.findById(playerName, skillId);
        return dto != null ? dto.level() : 0;
    }

    @Override
    public int findSkillPoints(String playerName) {
        LOG.debug("Finding skill points for playerName: {}", playerName);
        return this.executeQuery("GetPlayerSkillPoints", rs -> rs.getInt(DatabaseColumns.COL_SKILL_POINTS), playerName)
                .get(0);
    }

    @Override
    public void save(String playerName, List<SkillDTO> skills, int skillPoints) {
        LOG.debug("Saving {} skills and {} skill points for playerName: {}", skills.size(), skillPoints, playerName);
        this.executeUpdate("SetPlayerSkillPoints", skillPoints, playerName);
        this.executeUpdate("DeleteAllPlayerSkills", playerName);
        skills.forEach(skill -> this.executeUpdate("AddPlayerSkill", playerName, skill.skillId(), skill.level()));
    }

    @Override
    public void delete(String playerName) {
        LOG.debug("Deleting all skills for playerName: {}", playerName);
        this.executeUpdate("DeleteAllPlayerSkills", playerName);
    }
}
