package ulb.repositories;

import java.util.List;
import java.util.Map;

import ulb.repositories.dto.SkillDTO;

public interface SkillRepository {

    List<SkillDTO> findAll(String playername);

    SkillDTO findById(String playername, String skillId);

    int findSkillLevel(String playername, String skillId);

    int getPlayerSkillPoints(String playername);

    void setPlayerSkillPoints(String playername, int skillPoints);

    void savePlayerSkill(String playername, String skillId, int level);

    void deletePlayerSkill(String playername, String skillId);

    Map<String, Integer> getPlayerSkills(String playername);
}
