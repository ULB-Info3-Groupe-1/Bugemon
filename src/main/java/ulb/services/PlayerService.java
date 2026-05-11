package ulb.services;

import ulb.models.skills.SkillNode;
import ulb.models.skills.SkillTree;
import ulb.repositories.StaticDataRepository;

public class PlayerService {

    private int availablePoints = 100; // TODO: persistence into db
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

    public int getAvailableSkillPoints() {
        return this.availablePoints;
    }

    public void addSkillPoints(int points) {
        this.availablePoints += points;
    }

    public boolean unlockSkill(SkillNode node) {
        if (!this.skillTree.canUnlock(node, this.availablePoints)) {
            return false;
        }
        this.availablePoints -= node.getSkill().getCost();
        node.getSkill().incrementLevel();
        return true;
    }

    public int refundSkillNode(SkillNode node) {
        if (!this.skillTree.canDowngrade(node)) {
            return 0;
        }
        int refund = this.skillTree.downgrade(node);
        this.availablePoints += refund;
        return refund;
    }
}
