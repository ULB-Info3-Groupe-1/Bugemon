package ulb.models.skills;

import java.util.List;

import ulb.models.bugemon.ElementType;
import ulb.models.combat.effect.StatusEffect;
import ulb.models.item.ItemType;

public class SkillContext {

    public static final SkillContext NONE = new SkillContext(null, null);

    private final SkillTreeState state;
    private final SkillTree tree;

    public SkillContext(SkillTreeState state, SkillTree tree) {
        this.state = state;
        this.tree = tree;
    }

    public List<StatusEffect> getStatEffects() {
        if (this.state == null) {
            return List.of();
        }
        return this.state.getTotalStatBonus(this.tree);
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

    public int getStarterItemQuantity(ItemType type) {
        if (this.state == null) {
            return 0;
        }
        return this.state.getStarterItemQuantity(this.tree, type);
    }
}
