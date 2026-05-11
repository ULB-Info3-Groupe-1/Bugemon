package ulb.models.skills;

import java.util.ArrayList;
import java.util.List;

import ulb.models.utils.Position;

/**
 * Represents a node in the skill tree, containing a skill and its relationships to other skills.
 */
public class SkillNode {

    private Skill skill;
    private List<SkillNode> parents = new ArrayList<>();
    private List<SkillNode> children = new ArrayList<>();

    private Position position;

    public SkillNode(Skill skill, Position position) {
        this.skill = skill;
        this.position = position;
    }

    public Skill getSkill() {
        return this.skill;
    }

    public List<SkillNode> getParents() {
        return this.parents;
    }

    public List<SkillNode> getChildren() {
        return this.children;
    }

    public Position getPosition() {
        return this.position;
    }

    public String getDescription() {
        return this.skill.getDescription();
    }

    public boolean isUnlockable() {
        if (this.skill.isUnlocked()) {
            return false; // already unlocked
        }
        if (this.parents.isEmpty()) {
            return true; // no prerequisites
        }

        // FIX au moins un des parents doivent être unlock pas nécessairement tous...
        return this.parents.stream().anyMatch(parent -> parent.getSkill().isUnlocked());
    }

    public SkillNodeState getState() {
        if (this.skill.isUnlocked()) {
            return SkillNodeState.ACTIVE;
        }
        if (this.isUnlockable()) {
            return SkillNodeState.AVAILABLE;
        }
        return SkillNodeState.LOCKED;
    }

    public String getName() {
        return this.skill.getName();
    }

    public int getCost() {
        return this.skill.getCost();
    }

    public int getCurrentLevel() {
        return this.skill.getCurrentLevel();
    }

    public int getMaxLevel() {
        return this.skill.getMaxLevel();
    }

    // setters
    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public void addChild(SkillNode child) {
        this.children.add(child);
    }

    public void addParent(SkillNode parent) {
        this.parents.add(parent);
    }
}
