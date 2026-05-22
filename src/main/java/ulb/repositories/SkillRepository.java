package ulb.repositories;

import java.util.List;

import ulb.common.dto.persistence.SkillDTO;

public interface SkillRepository {

    List<SkillDTO> findAll(String playername);

    SkillDTO findById(String playername, String skillId);

    int findSkillLevel(String playername, String skillId);

    int findSkillPoints(String playername);

    void save(String playername, List<SkillDTO> skills, int skillPoints);

    void delete(String playername);
}
