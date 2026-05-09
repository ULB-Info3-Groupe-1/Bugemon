package ulb.services;

import java.util.List;

import ulb.models.skills.SkillNode;
import ulb.repositories.StaticDataRepository;

public class PlayerService {

    private List<SkillNode> skillTree;

    public PlayerService(StaticDataRepository staticDataRepository) {
        this.skillTree = staticDataRepository.getSkillTree();
    }

    public List<SkillNode> getSkillTree() {
        return this.skillTree;
    }

}
