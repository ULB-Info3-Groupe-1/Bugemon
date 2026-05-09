package ulb.models.skills;

import ulb.models.utils.Position;

public class SkillNode {

    private Skill skill;

    private Position position;

    public SkillNode(Skill skill, Position position) {
        this.skill = skill;
        this.position = position;
    }

    public Skill getSkill() {
        return this.skill;
    }

    public Position getPosition() {
        return this.position;
    }

    // setters

    public void setSkill(Skill skill) {
        this.skill = skill;
    }
}
