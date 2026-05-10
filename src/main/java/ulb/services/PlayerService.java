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

    // temporary placeholder — replace once the player model tracks available skill points
    public int getAvailableSkillPoints() {
        return 0;
    }

}
