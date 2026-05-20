package ulb.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ulb.models.skills.SkillTree;
import ulb.models.skills.SkillTreeState;
import ulb.repositories.SkillRepository;
import ulb.repositories.StaticRepository;
import ulb.repositories.dto.SkillDTO;

public class SkillService {

    private final String playername;

    private final SkillRepository skillRepository;
    private final StaticRepository staticRepository;

    public SkillService(SkillRepository skillRepository, StaticRepository staticRepository, String playername) {
        this.skillRepository = skillRepository;
        this.staticRepository = staticRepository;

        this.playername = playername;
    }

    public void save(SkillTreeState skillTreeState) {
        this.skillRepository.save(this.playername, this.toDTO(skillTreeState), skillTreeState.getSkillPoints());
    }

    public SkillTreeState getSkillTreeState() {
        List<SkillDTO> dtos = this.skillRepository.findAll(this.playername);
        int skillPoints = this.skillRepository.findSkillPoints(this.playername);
        Map<String, Integer> levels = dtos.stream().collect(Collectors.toMap(SkillDTO::skillId, SkillDTO::level));
        return SkillTreeState.restore(levels, skillPoints);
    }

    public SkillTree getSkillTree() {
        return this.staticRepository.skillTree();
    }

    public void addPoint(SkillTreeState skillTreeState, SkillTree skillTree, String nodeId) {
        skillTreeState.addPoint(nodeId, skillTree);
    }

    public void removePoint(SkillTreeState skillTreeState, SkillTree skillTree, String nodeId) {
        skillTreeState.removePoint(nodeId, skillTree);
    }

    private List<SkillDTO> toDTO(SkillTreeState skillTreeState) {
        return skillTreeState.getSkillLevels().entrySet().stream()
                .map(entry -> new SkillDTO(entry.getKey(), entry.getValue())).toList();
    }

}
