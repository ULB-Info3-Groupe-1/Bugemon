package ulb.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ulb.common.dto.persistence.SkillDTO;
import ulb.models.skills.SkillContext;
import ulb.models.skills.SkillTree;
import ulb.models.skills.SkillTreeState;
import ulb.models.skills.exceptions.IllegalNodeStateException;
import ulb.repositories.SkillRepository;
import ulb.repositories.StaticRepository;

public class SkillService {

    private final String playerName;

    private final SkillRepository skillRepository;
    private final StaticRepository staticRepository;

    public SkillService(SkillRepository skillRepository, StaticRepository staticRepository, String playerName) {
        this.skillRepository = skillRepository;
        this.staticRepository = staticRepository;

        this.playerName = playerName;
    }

    public void save(SkillTreeState skillTreeState) {
        this.skillRepository.save(this.playerName, this.toDTO(skillTreeState), skillTreeState.getSkillPoints());
    }

    public SkillTreeState getSkillTreeState() {
        List<SkillDTO> dtos = this.skillRepository.findAll(this.playerName);
        int skillPoints = this.skillRepository.findSkillPoints(this.playerName);
        Map<String, Integer> levels = dtos.stream().collect(Collectors.toMap(SkillDTO::skillId, SkillDTO::level));
        return SkillTreeState.restore(levels, skillPoints);
    }

    public SkillTree getSkillTree() {
        return this.staticRepository.skillTree();
    }

    public SkillContext buildSkillContext(SkillTreeState state) {
        return new SkillContext(state, this.getSkillTree());
    }

    public void addPoint(SkillTreeState skillTreeState, SkillTree skillTree, String nodeId)
            throws IllegalNodeStateException {
        skillTreeState.addPoint(nodeId, skillTree);
    }

    public void removePoint(SkillTreeState skillTreeState, SkillTree skillTree, String nodeId)
            throws IllegalNodeStateException {
        skillTreeState.removePoint(nodeId, skillTree);
    }

    private List<SkillDTO> toDTO(SkillTreeState skillTreeState) {
        return skillTreeState.getSkillLevels().entrySet().stream()
                .map(entry -> new SkillDTO(entry.getKey(), entry.getValue())).toList();
    }

}
