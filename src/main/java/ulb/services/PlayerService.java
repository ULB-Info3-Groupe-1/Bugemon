package ulb.services;

import java.util.List;

import ulb.models.skills.Skill;
import ulb.models.skills.SkillNode;
import ulb.models.skills.SkillTree;
import ulb.repositories.SkillRepository;
import ulb.repositories.dto.SkillDTO;

public class PlayerService {
    private final SkillRepository skillRepository;
    private final SkillTree skillTree;
    private final String playerName;
    private int skillPoints;

    public PlayerService(SkillRepository skillRepository, SkillTree skillTree, String playerName) {
        this.skillRepository = skillRepository;
        this.skillTree = skillTree;
        this.playerName = playerName;
        this.skillPoints = skillRepository.findSkillPoints(playerName);
        this.loadPlayerSkills();
    }

    private void loadPlayerSkills() {
        List<SkillDTO> saved = this.skillRepository.findAll(this.playerName);
        for (SkillDTO dto : saved) {
            this.skillTree.getAllNodes().stream()
                    .filter(n -> n.getSkill().getId().equals(dto.skillId()))
                    .findFirst()
                    .ifPresent(n -> n.getSkill().setCurrentLevel(dto.level()));
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
        return this.skillPoints;
    }

    public void addSkillPoints(int points) {
        this.skillPoints += points;
        this.skillRepository.save(this.playerName, this.buildCurrentSkillDTOs(), this.skillPoints);
    }

    public boolean unlockSkill(SkillNode node) {
        if (!this.skillTree.canUnlock(node, this.skillPoints)) {
            return false;
        }
        this.skillPoints -= node.getSkill().getCost();
        node.getSkill().incrementLevel();
        this.skillRepository.save(this.playerName, this.buildCurrentSkillDTOs(), this.skillPoints);
        return true;
    }

    public int refundSkillNode(SkillNode node) {
        if (!this.skillTree.canDowngrade(node)) {
            return 0;
        }
        int refund = this.skillTree.downgrade(node);
        this.skillPoints += refund;
        this.skillRepository.save(this.playerName, this.buildCurrentSkillDTOs(), this.skillPoints);
        return refund;
    }

    public void save() {
        this.skillRepository.save(this.playerName, this.buildCurrentSkillDTOs(), this.skillPoints);
    }

    private List<SkillDTO> buildCurrentSkillDTOs() {
        return this.skillTree.getAllNodes().stream()
                .filter(n -> n.getSkill().getCurrentLevel() > 0)
                .map(n -> new SkillDTO(n.getSkill().getId(), n.getSkill().getCurrentLevel()))
                .toList();
    }
}
