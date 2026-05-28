package bugemon.common.models.skills;

import java.util.List;

import bugemon.common.models.bugemon.ElementType;
import bugemon.common.models.combat.effect.StatusEffect;
import bugemon.common.models.item.ItemType;

/**
 * Read-only facade that exposes the net effect of a player's unlocked skills during a run.
 *
 * <p>
 * {@link SkillContext} pairs a {@link SkillTreeState} (which nodes are unlocked and at what level) with the
 * corresponding {@link SkillTree} definition (node metadata and effects). All query methods aggregate the contributions
 * of every currently active node and return a ready-to-use value for the combat or reward system to consume.
 *
 * <p>
 * The sentinel {@link #NONE} instance is used when no player skill data is available (e.g. for NPC combatants). Every
 * method on {@code NONE} returns a neutral value — empty list, {@code 1.0} multiplier, or {@code 0} quantity — so
 * callers do not need null checks.
 *
 * <p>
 * Typical usage:
 * <ul>
 * <li>Combat initialisation reads {@link #getStatEffects()} to apply passive stat buffs to the player's Bugemons.</li>
 * <li>Damage calculation reads {@link #getTypeMultiplier(ElementType)} to scale elemental damage.</li>
 * <li>XP award reads {@link #getXpMultiplier()} to boost post-battle XP.</li>
 * <li>Run setup reads {@link #getStarterItemQuantity(ItemType)} to seed the player's inventory.</li>
 * </ul>
 */
public class SkillContext {

    /**
     * A no-op {@link SkillContext} used for NPC combatants or contexts where no skill tree is active. All query methods
     * return neutral values.
     */
    public static final SkillContext NONE = new SkillContext(null, null);

    private final SkillTreeState state;
    private final SkillTree tree;

    /**
     * Creates a {@link SkillContext} backed by the given state and tree definition.
     *
     * @param state
     *            the player's current unlock levels and skill-point balance; may be {@code null} for a no-op context
     * @param tree
     *            the immutable skill tree definition; may be {@code null} for a no-op context
     */
    public SkillContext(SkillTreeState state, SkillTree tree) {
        this.state = state;
        this.tree = tree;
    }

    /**
     * Returns the aggregated passive stat bonuses granted by all currently active skill nodes.
     *
     * @return an unmodifiable list of {@link bugemon.common.models.combat.effect.StatusEffect}s; empty when no skills are active
     */
    public List<StatusEffect> getStatEffects() {
        if (this.state == null) {
            return List.of();
        }
        return this.state.getTotalStatBonus(this.tree);
    }

    /**
     * Returns the cumulative damage multiplier for attacks of the given element type.
     *
     * <p>
     * A value of {@code 1.0} means no bonus. Values above {@code 1.0} increase damage dealt with that type.
     *
     * @param attackType
     *            the element type of the attack being evaluated
     * @return the combined type multiplier from all relevant unlocked nodes; {@code 1.0} if no skills apply
     */
    public double getTypeMultiplier(ElementType attackType) {
        if (this.state == null) {
            return 1.0;
        }
        return this.state.getTypeMultiplier(this.tree, attackType);
    }

    /**
     * Returns the cumulative XP gain multiplier applied after each battle.
     *
     * <p>
     * A value of {@code 1.0} means no bonus. Values above {@code 1.0} increase XP earned.
     *
     * @return the combined XP multiplier from all relevant unlocked nodes; {@code 1.0} if no skills apply
     */
    public double getXpMultiplier() {
        if (this.state == null) {
            return 1.0;
        }
        return this.state.getXpMultiplier(this.tree);
    }

    /**
     * Returns the number of items of the given type granted to the player's inventory at the start of a run.
     *
     * @param type
     *            the item type to query
     * @return the total starter-item quantity from all relevant unlocked nodes; {@code 0} if no skills apply
     */
    public int getStarterItemQuantity(ItemType type) {
        if (this.state == null) {
            return 0;
        }
        return this.state.getStarterItemQuantity(this.tree, type);
    }
}
