package ulb.services;

import java.util.List;
import java.util.Map;

import ulb.models.skills.Skill;
import ulb.models.skills.SkillNode;
import ulb.models.skills.SkillTree;
import ulb.repositories.PlayerRepository;

public class PlayerService {
    private final PlayerRepository playerRepository;
    private final SkillTree skillTree;
    private final String playerName;

    public PlayerService(PlayerRepository playerRepository, String playerName) {
        this.skillTree = playerRepository.getSkillTree();
        this.playerName = playerName;
        this.playerRepository = playerRepository;
        this.loadPlayerSkills();
    }

    private void loadPlayerSkills() {
        Map<String, Integer> saved = this.playerRepository.getPlayerSkills(this.playerName);
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
        return this.playerRepository.getPlayerSkillPoints(this.playerName);
    }

    public void addSkillPoints(int points) {
        this.playerRepository.setPlayerPoints(this.getAvailableSkillPoints() + points, this.playerName);
    }

    public boolean unlockSkill(SkillNode node) {
        int skillPoints = this.getAvailableSkillPoints();
        if (!this.skillTree.canUnlock(node, skillPoints)) {
            return false;
        }
        skillPoints -= node.getSkill().getCost();
        node.getSkill().incrementLevel();
        this.playerRepository.setPlayerPoints(skillPoints, this.playerName);
        this.playerRepository.savePlayerSkill(this.playerName, node.getSkill().getId(),
                node.getSkill().getCurrentLevel());
        return true;
    }

    public int refundSkillNode(SkillNode node) {
        if (!this.skillTree.canDowngrade(node)) {
            return 0;
        }
        int refund = this.skillTree.downgrade(node);
        this.playerRepository.setPlayerPoints(this.getAvailableSkillPoints() + refund, this.playerName);
        this.updateSkillTree();
        return refund;
    }

    private void updateSkillTree() {
        for (SkillNode node : this.skillTree.getAllNodes()) {
            String id = node.getSkill().getId();
            int level = node.getSkill().getCurrentLevel();
            if (level > 0) {
                this.playerRepository.savePlayerSkill(this.playerName, id, level);
            } else {
                this.playerRepository.deletePlayerSkill(this.playerName, id);
            }
        }
    }
}
