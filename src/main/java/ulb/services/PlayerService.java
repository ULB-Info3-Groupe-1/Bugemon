package ulb.services;

import java.util.List;

import ulb.models.skills.SkillNode;
import ulb.models.skills.SkillTree;
import ulb.repositories.StaticDataRepository;

public class PlayerService {

    private int availablePoints = 0; // TODO: persistence into db
    private SkillTree skillTree;

    public PlayerService(StaticDataRepository staticDataRepository) {
        this.skillTree = staticDataRepository.getSkillTree();
    }

    public SkillTree getSkillTree() {
        return this.skillTree;
    }

    public SkillNode getSkillTreeRoot() {
        return this.skillTree.getRoot();
    }

    // temporary placeholder, replace once the player model tracks available skill points
    public int getAvailableSkillPoints() {
        return availablePoints;
    }

    public void buySkillNode(SkillNode node) {
        if (this.skillTree.canUnlock(node, this.availablePoints)) {
            node.getSkill().incrementLevel();
            this.availablePoints -= node.getSkill().getCost();
        }
    }

    public void refundSkillNode(SkillNode node) {
        if (this.skillTree.canDowngrade(node)) {
            int refund = this.skillTree.downgrade(node);
            this.availablePoints += refund;
        }
    }
}
