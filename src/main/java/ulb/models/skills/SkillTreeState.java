package ulb.models.skills;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ulb.Configuration;
import ulb.common.StatType;
import ulb.models.bugemon.ElementType;
import ulb.models.player.BonusStats;
import ulb.models.skills.SkillEffect.CritBonusEffect;
import ulb.models.skills.SkillEffect.RegenPostCombatEffect;
import ulb.models.skills.SkillEffect.RewardChoiceEffect;
import ulb.models.item.ItemType;
import ulb.models.skills.SkillEffect.StarterItemsEffect;
import ulb.models.skills.SkillEffect.StatBonusEffect;
import ulb.models.skills.SkillEffect.TypeMultiplierEffect;
import ulb.models.skills.SkillEffect.XpMultiplierEffect;
import ulb.models.skills.exceptions.IllegalNodeStateException;

public class SkillTreeState {

    private final Map<String, Integer> skillLevels;
    private int skillPoints;

    public SkillTreeState() {
        this.skillLevels = new HashMap<>();
        this.skillPoints = 0;
    }

    public static SkillTreeState restore(Map<String, Integer> skillLevels, int skillPoints) {
        SkillTreeState state = new SkillTreeState();
        state.skillLevels.putAll(skillLevels);
        state.skillPoints = skillPoints;
        return state;
    }

    public Map<String, Integer> getSkillLevels() {
        return Collections.unmodifiableMap(this.skillLevels);
    }

    public int getNodeLevel(String skillId) {
        return this.skillLevels.getOrDefault(skillId, 0);
    }

    public void earnSkillPoint() {
        this.skillPoints++;
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

    private boolean isAvailable(SkillNode node) {
        if (node.prerequisites().isEmpty()) {
            return true;
        }
        return node.prerequisites().stream().anyMatch(prereqId -> this.getNodeLevel(prereqId) > 0);
    }

    public boolean canAddPoint(String nodeId, SkillTree tree) {
        SkillNode node = tree.getById(nodeId);
        return this.skillPoints >= node.cost() && this.getNodeLevel(nodeId) < node.maxLevel() && this.isAvailable(node);
    }

    public boolean canRemovePoint(String nodeId, SkillTree tree) {
        return this.getNodeLevel(nodeId) > 0 && !this.wouldBreakDependents(nodeId, tree);
    }

    public void addPoint(String nodeId, SkillTree tree) throws IllegalNodeStateException {
        if (!this.canAddPoint(nodeId, tree)) {
            throw new IllegalNodeStateException("Unable to add a point to skill : " + nodeId);
        }
        this.skillLevels.merge(nodeId, 1, Integer::sum);
        this.skillPoints -= tree.getById(nodeId).cost();
    }

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

    public BonusStats getTotalStatBonus(SkillTree tree) {
        int hp = 0;
        int atk = 0;
        int def = 0;
        int init = 0;
        for (Map.Entry<String, Integer> entry : this.skillLevels.entrySet()) {
            SkillNode node = tree.getById(entry.getKey());
            int level = entry.getValue();
            // visitor pattern would be cleaner
            if (node.effect() instanceof StatBonusEffect(StatType stat, int bonus)) {
                int b = bonus;
                switch (stat) {
                case HP -> hp += b * level;
                case ATTACK -> atk += b * level;
                case DEFENSE -> def += b * level;
                case INITIATIVE -> init += b * level;
                default -> throw new IllegalStateException("Unexpected stat type: " + stat);
                }
            }
        }
        return new BonusStats(hp, atk, def, init);
    }

    public double getXpMultiplier(SkillTree tree) {
        double bonus = 0.0;
        for (Map.Entry<String, Integer> entry : this.skillLevels.entrySet()) {
            SkillNode node = tree.getById(entry.getKey());
            if (node.effect() instanceof XpMultiplierEffect(double multiplier)) {
                bonus += (multiplier - 1.0) * entry.getValue();
            }
        }
        return 1.0 + bonus;
    }

    public int getCritBonus(SkillTree tree) {
        int total = 0;
        for (Map.Entry<String, Integer> entry : this.skillLevels.entrySet()) {
            SkillNode node = tree.getById(entry.getKey());
            if (node.effect() instanceof CritBonusEffect(double extraChance)) {
                total += extraChance * entry.getValue();
            }
        }
        return total;
    }

    public double getTypeMultiplier(SkillTree tree, ElementType elementType) {
        double bonus = 0.0;
        for (Map.Entry<String, Integer> entry : this.skillLevels.entrySet()) {
            SkillNode node = tree.getById(entry.getKey());
            if (node.effect() instanceof TypeMultiplierEffect(ElementType type, double mult) && elementType == type) {
                bonus += (mult - 1.0) * entry.getValue();
            }
        }
        return 1.0 + bonus;
    }

    public int getRegenPercent(SkillTree tree) {
        int total = 0;
        for (Map.Entry<String, Integer> entry : this.skillLevels.entrySet()) {
            SkillNode node = tree.getById(entry.getKey());
            if (node.effect() instanceof RegenPostCombatEffect(double percent)) {
                total += percent * entry.getValue();
            }
        }
        return total;
    }

    public int getStarterItemQuantity(SkillTree tree, ItemType type) {
        int total = 0;
        for (Map.Entry<String, Integer> entry : this.skillLevels.entrySet()) {
            SkillNode node = tree.getById(entry.getKey());
            if (node.effect() instanceof StarterItemsEffect(int qty, ItemType t) && t == type) {
                total += qty * entry.getValue();
            }
        }
        return total;
    }

    public int getLevelUpChoiceCount(SkillTree tree) {
        int count = Configuration.Skill.DEFAULT_LEVEL_UP_CHOICE_COUNT;
        for (Map.Entry<String, Integer> entry : this.skillLevels.entrySet()) {
            SkillNode node = tree.getById(entry.getKey());
            if (node.effect() instanceof RewardChoiceEffect(int totalChoices)) {
                count = Math.max(count, totalChoices);
            }
        }
        return count;
    }

}
