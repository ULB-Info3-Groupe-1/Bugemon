package bugemon.common.models.skills;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bugemon.common.Configuration;
import bugemon.common.models.bugemon.ElementType;
import bugemon.common.models.combat.effect.StatusEffect;
import bugemon.common.models.item.ItemType;
import bugemon.common.models.skills.SkillEffect.CritBonusEffect;
import bugemon.common.models.skills.SkillEffect.RegenPostCombatEffect;
import bugemon.common.models.skills.SkillEffect.RewardChoiceEffect;
import bugemon.common.models.skills.SkillEffect.StarterItemsEffect;
import bugemon.common.models.skills.SkillEffect.StatBonusEffect;
import bugemon.common.models.skills.SkillEffect.TypeMultiplierEffect;
import bugemon.common.models.skills.SkillEffect.XpMultiplierEffect;
import bugemon.common.models.skills.exceptions.IllegalNodeStateException;

/**
 * Mutable runtime state of the player's skill tree: which nodes are unlocked, at what level, and how many skill points
 * remain unspent.
 *
 * <p>
 * The state is intentionally decoupled from the immutable {@link SkillTree} definition so the same tree can be shared
 * across players. All mutating operations require the tree to be passed in for validation.
 *
 * <p>
 * <strong>Unlock rules:</strong>
 * <ul>
 * <li>A node is <em>available</em> when at least one of its prerequisites is active (level &gt; 0), or when the node
 * has no prerequisites.</li>
 * <li>Adding a point costs {@link SkillNode#cost()} skill points and increments the node's level up to
 * {@link SkillNode#maxLevel()}.</li>
 * <li>Removing a point refunds the cost. If the node drops to level 0 and any dependent node has no other active
 * prerequisite, that dependent is deactivated recursively — see {@link #cascadeDeactivate(String, SkillTree)}.</li>
 * </ul>
 *
 * <p>
 * Aggregate query methods ({@link #getTotalStatBonus}, {@link #getXpMultiplier}, etc.) iterate over all active nodes
 * and accumulate contributions from matching {@link SkillEffect} subtypes.
 *
 * <p>
 * Use {@link #restore(Map, int)} to reconstruct the state from persisted data, and {@link #clear()} to reset at the end
 * of a run.
 */
public class SkillTreeState implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<String, Integer> skillLevels;
    private int skillPoints;

    /** Creates a new, empty state with zero skill points and no unlocked nodes. */
    public SkillTreeState() {
        this.skillLevels = new HashMap<>();
        this.skillPoints = 0;
    }

    /**
     * Reconstructs a {@link SkillTreeState} from persisted data.
     *
     * @param skillLevels
     *            a map of node ID to unlock level; entries with level 0 are ignored
     * @param skillPoints
     *            the number of unspent skill points to restore
     * @return a new {@link SkillTreeState} with the given data applied
     */
    public static SkillTreeState restore(Map<String, Integer> skillLevels, int skillPoints) {
        SkillTreeState state = new SkillTreeState();
        state.skillLevels.putAll(skillLevels);
        state.skillPoints = skillPoints;
        return state;
    }

    /**
     * Returns an unmodifiable snapshot of all node IDs that have been unlocked at least once, mapped to their current
     * level.
     *
     * @return an unmodifiable view of the node-level map
     */
    public Map<String, Integer> getSkillLevels() {
        return Collections.unmodifiableMap(this.skillLevels);
    }

    public int getNodeLevel(String skillId) {
        return this.skillLevels.getOrDefault(skillId, 0);
    }

    public int getSkillPoints() {
        return this.skillPoints;
    }

    public SkillStatus getStatus(String nodeId, SkillTree tree) {
        int level = this.getNodeLevel(nodeId);
        if (level > 0) {
            return SkillStatus.ACTIVE;
        }
        if (this.isAvailable(tree.getById(nodeId))) {
            return SkillStatus.AVAILABLE;
        }
        return SkillStatus.LOCKED;
    }

    /**
     * Returns {@code true} if at least one of {@code node}'s prerequisites is currently active, or if the node has no
     * prerequisites.
     */
    private boolean isAvailable(SkillNode node) {
        if (node.prerequisites().isEmpty()) {
            return true;
        }
        return node.prerequisites().stream().anyMatch(prereqId -> this.getNodeLevel(prereqId) > 0);
    }

    /**
     * Returns {@code true} if the player may spend a point on the given node: the node is available, has not reached
     * its maximum level, and there are enough skill points to cover the cost.
     *
     * @param nodeId
     *            the node identifier to check
     * @param tree
     *            the skill tree definition
     * @return {@code true} if a point can be added
     */
    public boolean canAddPoint(String nodeId, SkillTree tree) {
        SkillNode node = tree.getById(nodeId);
        return this.skillPoints >= node.cost() && this.getNodeLevel(nodeId) < node.maxLevel() && this.isAvailable(node);
    }

    /**
     * Returns {@code true} if a point can be removed from the given node without leaving any active dependent node
     * without an active prerequisite.
     *
     * @param nodeId
     *            the node identifier to check
     * @param tree
     *            the skill tree definition
     * @return {@code true} if a point can be removed safely
     */
    public boolean canRemovePoint(String nodeId, SkillTree tree) {
        return this.getNodeLevel(nodeId) > 0 && !this.wouldBreakDependents(nodeId, tree);
    }

    /**
     * Awards one unspent skill point to the player's balance (e.g. at the end of a floor).
     */
    public void addPoint() {
        this.skillPoints++;
    }

    /**
     * Spends one skill point to increment the unlock level of the specified node.
     *
     * @param nodeId
     *            the node to unlock or advance
     * @param tree
     *            the skill tree definition used for validation
     * @throws IllegalNodeStateException
     *             if {@link #canAddPoint(String, SkillTree)} returns {@code false}
     */
    public void addPoint(String nodeId, SkillTree tree) throws IllegalNodeStateException {
        if (!this.canAddPoint(nodeId, tree)) {
            throw new IllegalNodeStateException("Unable to add a point to skill : " + nodeId);
        }
        this.skillLevels.merge(nodeId, 1, Integer::sum);
        this.skillPoints -= tree.getById(nodeId).cost();
    }

    /**
     * Removes one unlock level from the specified node and refunds the cost.
     *
     * <p>
     * If the node drops to level 0, {@link #cascadeDeactivate(String, SkillTree)} is triggered to deactivate any
     * dependents that no longer have an active prerequisite.
     *
     * @param nodeId
     *            the node to downgrade
     * @param tree
     *            the skill tree definition used for cascade logic
     * @throws IllegalNodeStateException
     *             if the node is already at level 0
     */
    public void removePoint(String nodeId, SkillTree tree) throws IllegalNodeStateException {
        int level = this.getNodeLevel(nodeId);
        if (level <= 0) {
            throw new IllegalNodeStateException("No Skill point to remove at : " + nodeId);
        }
        int cost = tree.getById(nodeId).cost();
        int newLevel = level - 1;
        if (newLevel == 0) {
            this.skillLevels.remove(nodeId);
            this.skillPoints += cost;
            this.cascadeDeactivate(nodeId, tree);
        } else {
            this.skillLevels.put(nodeId, newLevel);
            this.skillPoints += cost;
        }
    }

    public void clear() {
        this.skillLevels.clear();
        this.skillPoints = 0;
    }

    /**
     * Recursively deactivates dependents of {@code removedNodeId} that have no remaining active prerequisite. Refunded
     * points are added back to the balance for each deactivated node.
     *
     * @param removedNodeId
     *            the node that was just deactivated (level dropped to 0)
     * @param tree
     *            the skill tree definition for traversing dependents
     */
    private void cascadeDeactivate(String removedNodeId, SkillTree tree) {
        for (SkillNode dependent : tree.getDependents(removedNodeId)) {
            int dependentLevel = this.getNodeLevel(dependent.id());
            if (dependentLevel > 0 && !this.isAvailable(dependent)) {
                this.skillPoints += dependent.cost() * dependentLevel;
                this.skillLevels.remove(dependent.id());
                this.cascadeDeactivate(dependent.id(), tree);
            }
        }
    }

    /**
     * Returns {@code true} if removing one level from {@code nodeId} would leave an active dependent with no other
     * active prerequisite, making the removal unsafe without a cascade.
     *
     * <p>
     * Called by {@link #canRemovePoint} to give the UI a chance to warn the user before triggering a cascade.
     *
     * @param nodeId
     *            the candidate node to remove a point from
     * @param tree
     *            the skill tree definition
     * @return {@code true} if at least one active dependent would lose its last active prerequisite
     */
    private boolean wouldBreakDependents(String nodeId, SkillTree tree) {
        if (this.getNodeLevel(nodeId) > 1) {
            return false;
        }
        for (SkillNode dependent : tree.getDependents(nodeId)) {
            if (this.getNodeLevel(dependent.id()) <= 0) {
                continue;
            }
            boolean hasOtherActivePrereq = dependent.prerequisites().stream().filter(pId -> !pId.equals(nodeId))
                    .anyMatch(pId -> this.getNodeLevel(pId) > 0);
            if (!hasOtherActivePrereq) {
                return true;
            }
        }
        return false;
    }

    /**
     * Aggregates passive stat bonuses from all active {@link SkillEffect.StatBonusEffect} nodes, scaled by their
     * respective unlock levels.
     *
     * @param tree
     *            the skill tree definition
     * @return a list of {@link bugemon.common.models.combat.effect.StatusEffect}s representing the combined stat
     *         bonuses
     */
    public List<StatusEffect> getTotalStatBonus(SkillTree tree) {

        List<StatusEffect> effects = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : this.skillLevels.entrySet()) {
            SkillNode node = tree.getById(entry.getKey());
            int level = entry.getValue();
            // visitor pattern would be cleaner
            if (node.effect() instanceof StatBonusEffect eff) {
                effects.add(new StatusEffect(eff.stat(), eff.bonus() * level, null));
            }
        }
        return effects;
    }

    /**
     * Computes the cumulative XP gain multiplier from all active {@link SkillEffect.XpMultiplierEffect} nodes.
     *
     * <p>
     * Each level of a node contributes {@code (multiplier - 1.0)} to the bonus, which is then added to a base of
     * {@code 1.0}.
     *
     * @param tree
     *            the skill tree definition
     * @return the total XP multiplier; {@code 1.0} if no relevant nodes are active
     */
    public double getXpMultiplier(SkillTree tree) {
        double bonus = 0.0;
        for (Map.Entry<String, Integer> entry : this.skillLevels.entrySet()) {
            SkillNode node = tree.getById(entry.getKey());
            if (node.effect() instanceof XpMultiplierEffect eff) {
                bonus += (eff.multiplier() - 1.0) * entry.getValue();
            }
        }
        return 1.0 + bonus;
    }

    /**
     * Computes the total critical-hit bonus from all active {@link SkillEffect.CritBonusEffect} nodes, scaled by their
     * unlock levels.
     *
     * @param tree
     *            the skill tree definition
     * @return the summed crit bonus; {@code 0} if no relevant nodes are active
     */
    public int getCritBonus(SkillTree tree) {
        int total = 0;
        for (Map.Entry<String, Integer> entry : this.skillLevels.entrySet()) {
            SkillNode node = tree.getById(entry.getKey());
            if (node.effect() instanceof CritBonusEffect eff) {
                total += eff.extraChance() * entry.getValue();
            }
        }
        return total;
    }

    /**
     * Computes the cumulative damage multiplier for the given element type from all active
     * {@link SkillEffect.TypeMultiplierEffect} nodes that match {@code elementType}.
     *
     * <p>
     * Each matching level contributes {@code (mult - 1.0)} to the bonus, added to a base of {@code 1.0}.
     *
     * @param tree
     *            the skill tree definition
     * @param elementType
     *            the element type to look up
     * @return the combined type multiplier; {@code 1.0} if no relevant nodes are active
     */
    public double getTypeMultiplier(SkillTree tree, ElementType elementType) {
        double bonus = 0.0;
        for (Map.Entry<String, Integer> entry : this.skillLevels.entrySet()) {
            SkillNode node = tree.getById(entry.getKey());
            if (node.effect() instanceof TypeMultiplierEffect eff && elementType == eff.type()) {
                bonus += (eff.mult() - 1.0) * entry.getValue();
            }
        }
        return 1.0 + bonus;
    }

    public int getRegenPercent(SkillTree tree) {
        int total = 0;
        for (Map.Entry<String, Integer> entry : this.skillLevels.entrySet()) {
            SkillNode node = tree.getById(entry.getKey());
            if (node.effect() instanceof RegenPostCombatEffect eff) {
                total += eff.percent() * entry.getValue();
            }
        }
        return total;
    }

    public int getStarterItemQuantity(SkillTree tree, ItemType type) {
        int total = 0;
        for (Map.Entry<String, Integer> entry : this.skillLevels.entrySet()) {
            SkillNode node = tree.getById(entry.getKey());
            if (node.effect() instanceof StarterItemsEffect eff && eff.type() == type) {
                total += eff.quantity() * entry.getValue();
            }
        }
        return total;
    }

    public int getLevelUpChoiceCount(SkillTree tree) {
        int count = Configuration.Skill.DEFAULT_LEVEL_UP_CHOICE_COUNT;
        for (Map.Entry<String, Integer> entry : this.skillLevels.entrySet()) {
            SkillNode node = tree.getById(entry.getKey());
            if (node.effect() instanceof RewardChoiceEffect eff) {
                count = Math.max(count, eff.totalChoices());
            }
        }
        return count;
    }
}
