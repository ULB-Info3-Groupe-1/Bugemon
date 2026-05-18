package ulb.services;

import java.util.List;
import java.util.Map;

import ulb.models.player.PlayerState;
import ulb.models.skills.Skill;
import ulb.models.skills.SkillNode;
import ulb.models.skills.SkillTree;
import ulb.repositories.PlayerRepository;

public class PlayerService {
    private final PlayerRepository playerRepository;
    private final String playerName;

    public PlayerService(PlayerRepository playerRepository, String playerName) {
        this.playerName = playerName;
        this.playerRepository = playerRepository;
        this.loadPlayerSkills();
    }

    private void loadPlayerSkills() {
        SkillTree skillTree = this.playerRepository.getSkillTree();
        Map<String, Integer> saved = this.playerRepository.getPlayerSkills(this.playerName);
        for (SkillNode node : skillTree.getAllNodes()) {
            Integer level = saved.get(node.getSkill().getId());
            if (level != null) {
                node.getSkill().setCurrentLevel(level);
            }
        }
    }

    public List<Skill> getUnlockedSkills() {
        SkillTree skillTree = this.playerRepository.getSkillTree();
        return skillTree.getAllNodes().stream().map(SkillNode::getSkill).filter(Skill::isUnlocked).toList();
    }

    public SkillTree getSkillTree() {
        return this.playerRepository.getSkillTree();
    }

    public SkillNode getSkillTreeRoot() {
        SkillTree skillTree = this.playerRepository.getSkillTree();
        return skillTree.getRoot();
    }

    public int getAvailableSkillPoints() {
        return this.playerRepository.getPlayerSkillPoints(this.playerName);
    }

    public void addSkillPoints(int points) {
        this.playerRepository.setPlayerPoints(this.getAvailableSkillPoints() + points, this.playerName);
    }

    public boolean unlockSkill(SkillNode node) {
        int skillPoints = this.getAvailableSkillPoints();
        SkillTree skillTree = this.playerRepository.getSkillTree();
        if (!skillTree.canUnlock(node, skillPoints)) {
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
        SkillTree skillTree = this.playerRepository.getSkillTree();
        if (!skillTree.canDowngrade(node)) {
            return 0;
        }
        int refund = skillTree.downgrade(node);
        this.playerRepository.setPlayerPoints(this.getAvailableSkillPoints() + refund, this.playerName);
        this.updateSkillTree();
        return refund;
    }

    private void updateSkillTree() {
        SkillTree skillTree = this.playerRepository.getSkillTree();
        for (SkillNode node : skillTree.getAllNodes()) {
            String id = node.getSkill().getId();
            int level = node.getSkill().getCurrentLevel();
            if (level > 0) {
                this.playerRepository.savePlayerSkill(this.playerName, id, level);
            } else {
                this.playerRepository.deletePlayerSkill(this.playerName, id);
            }
        }
    }

    public void save() {
        this.updateSkillTree();
    }
}
