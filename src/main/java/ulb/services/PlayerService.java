package ulb.services;

import java.util.List;
import java.util.Map;

import ulb.models.skills.Skill;
import ulb.models.skills.SkillNode;
import ulb.models.skills.SkillTree;
import ulb.repositories.SkillRepository;

public class PlayerService {
    private final SkillRepository skillRepository;
    private final SkillTree skillTree;
    private final String playerName;

    public PlayerService(SkillRepository skillRepository, SkillTree skillTree, String playerName) {
        this.skillRepository = skillRepository;
        this.skillTree = skillTree;
        this.playerName = playerName;
        this.loadPlayerSkills();
    }

    private void loadPlayerSkills() {
        Map<String, Integer> saved = this.skillRepository.getPlayerSkills(this.playerName);
        for (SkillNode node : this.skillTree.getAllNodes()) {
            Integer level = saved.get(node.getSkill().getId());
            if (level != null) {
                node.getSkill().setCurrentLevel(level);
            }
        }
    }

    public List<Skill> getUnlockedSkills() {
        return this.skillTree.getAllNodes().stream().map(SkillNode::getSkill).filter(Skill::isUnlocked).toList();
    }

    public SkillTree getSkillTree() {
        return this.skillTree;
    }

    public SkillNode getSkillTreeRoot() {
        return this.skillTree.getRoot();
    }

    public int getAvailableSkillPoints() {
        return this.skillRepository.getPlayerSkillPoints(this.playerName);
    }

    public void addSkillPoints(int points) {
        this.skillRepository.setPlayerSkillPoints(this.playerName, this.getAvailableSkillPoints() + points);
    }

    public boolean unlockSkill(SkillNode node) {
        int skillPoints = this.getAvailableSkillPoints();
        if (!this.skillTree.canUnlock(node, skillPoints)) {
            return false;
        }
        skillPoints -= node.getSkill().getCost();
        node.getSkill().incrementLevel();
        this.skillRepository.setPlayerSkillPoints(this.playerName, skillPoints);
        this.skillRepository.savePlayerSkill(this.playerName, node.getSkill().getId(),
                node.getSkill().getCurrentLevel());
        return true;
    }

    public int refundSkillNode(SkillNode node) {
        if (!this.skillTree.canDowngrade(node)) {
            return 0;
        }
        int refund = this.skillTree.downgrade(node);
        this.skillRepository.setPlayerSkillPoints(this.playerName, this.getAvailableSkillPoints() + refund);
        this.updateSkillTree();
        return refund;
    }

    private void updateSkillTree() {
        for (SkillNode node : this.skillTree.getAllNodes()) {
            String id = node.getSkill().getId();
            int level = node.getSkill().getCurrentLevel();
            if (level > 0) {
                this.skillRepository.savePlayerSkill(this.playerName, id, level);
            } else {
                this.skillRepository.deletePlayerSkill(this.playerName, id);
            }
        }
    }

    public void save() {
        this.updateSkillTree();
    }
}
