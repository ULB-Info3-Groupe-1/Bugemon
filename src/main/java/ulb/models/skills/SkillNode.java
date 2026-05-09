package ulb.models.skills;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ulb.models.utils.Position;

/**
 * Represents a node in the skill tree, containing a skill and its relationships to other skills.
 */
public class SkillNode {

    private Skill skill;
    private List<SkillNode> children = new ArrayList<>();
    private Optional<List<SkillNode>> parents = Optional.empty();

    private Position position;

    public SkillNode(Skill skill, Position position, List<SkillNode> parents) {
        this.skill = skill;
        this.position = position;
        this.parents = Optional.of(parents);
    }

    public Skill getSkill() {
        return this.skill;
    }

    public Position getPosition() {
        return this.position;
    }

    public boolean isUnlockable() {
        if (this.skill.isUnlocked()) {
            return false; // already unlocked
        }
        if (this.parents.isEmpty()) {
            return true; // no prerequisites
        }
        return this.parents.get().stream().allMatch(parent -> parent.getSkill().isUnlocked());
    }

    public List<SkillNode> getChildren() {
        return this.children;
    }

    // setters
    public void setSkill(Skill skill) {
        this.skill = skill;
    }
}
