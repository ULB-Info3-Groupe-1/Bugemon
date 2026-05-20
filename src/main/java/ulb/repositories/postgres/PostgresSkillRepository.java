package ulb.repositories.postgres;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.repositories.DatabaseConnection;
import ulb.repositories.SkillRepository;
import ulb.repositories.dto.SkillDTO;

public class PostgresSkillRepository extends AbstractRepository implements SkillRepository {
    private static final Logger LOG = LoggerFactory.getLogger(PostgresSkillRepository.class);

    public PostgresSkillRepository(DatabaseConnection dbConnection, Map<String, String> queries) {
        super(dbConnection, queries);
    }

    @Override
    public List<SkillDTO> findAll(String playername) {
        LOG.debug("Finding all skills for playername: {}", playername);
        return this.executeQuery("GetPlayerSkills", rs -> new SkillDTO(rs.getString(DatabaseColumns.COL_SKILL_ID),
                rs.getInt(DatabaseColumns.COL_CURRENT_LEVEL)), playername);
    }

    @Override
    public SkillDTO findById(String playername, String skillId) {
        LOG.debug("Finding skill '{}' for playername: {}", skillId, playername);
        return this.findAll(playername).stream().filter(s -> s.skillId().equals(skillId)).findFirst().orElse(null);
    }

    @Override
    public int findSkillLevel(String playername, String skillId) {
        SkillDTO dto = this.findById(playername, skillId);
        return dto != null ? dto.level() : 0;
    }

    @Override
    public int findSkillPoints(String playername) {
        LOG.debug("Finding skill points for playername: {}", playername);
        return this.executeQuery("GetPlayerSkillPoints", rs -> rs.getInt(DatabaseColumns.COL_SKILL_POINTS), playername)
                .get(0);
    }

    @Override
    public void save(String playername, List<SkillDTO> skills, int skillPoints) {
        LOG.debug("Saving {} skills and {} skill points for playername: {}", skills.size(), skillPoints, playername);
        this.executeUpdate("SetPlayerSkillPoints", skillPoints, playername);
        this.executeUpdate("DeleteAllPlayerSkills", playername);
        skills.forEach(skill -> this.executeUpdate("AddPlayerSkill", playername, skill.skillId(), skill.level()));
    }

    @Override
    public void delete(String playername) {
        LOG.debug("Deleting all skills for playername: {}", playername);
        this.executeUpdate("DeleteAllPlayerSkills", playername);
    }
}
