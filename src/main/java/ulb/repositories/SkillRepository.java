package ulb.repositories;

import java.util.List;

import ulb.common.dto.persistence.SkillDTO;

public interface SkillRepository {

    List<SkillDTO> findAll(String playerName);

    SkillDTO findById(String playerName, String skillId);

    int findSkillLevel(String playerName, String skillId);

    int findSkillPoints(String playerName);

    void save(String playerName, List<SkillDTO> skills, int skillPoints);

    void delete(String playerName);
}
