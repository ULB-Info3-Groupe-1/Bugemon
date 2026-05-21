package ulb.models.skills;

import ulb.models.bugemon.ElementType;

public class SkillContext {

    public static final SkillContext NONE = new SkillContext(null, null);

    private final SkillTreeState state;
    private final SkillTree tree;

    public SkillContext(SkillTreeState state, SkillTree tree) {
        this.state = state;
        this.tree = tree;
    }

    public int getAttackBonus() {
        if (this.state == null) {
            return 0;
        }
        return this.state.getTotalStatBonus(this.tree).getBonusAttack();
    }

    public int getDefenseBonus() {
        if (this.state == null) {
            return 0;
        }
        return this.state.getTotalStatBonus(this.tree).getBonusDefense();
    }

    public double getTypeMultiplier(ElementType attackType) {
        if (this.state == null) {
            return 1.0;
        }
        return this.state.getTypeMultiplier(this.tree, attackType);
    }

    public double getXpMultiplier() {
        if (this.state == null) {
            return 1.0;
        }
        return this.state.getXpMultiplier(this.tree);
    }
}
