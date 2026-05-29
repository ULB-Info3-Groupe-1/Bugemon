package bugemon.common.models.skills;

import java.io.Serializable;

import bugemon.common.StatType;
import bugemon.common.models.bugemon.ElementType;
import bugemon.common.models.item.ItemType;

/**
 * Sealed interface representing the effect granted by unlocking a {@link SkillNode}.
 *
 * <p>
 * Each permitted record type corresponds to a distinct gameplay modifier:
 * <ul>
 * <li>{@link StatBonusEffect} — adds a flat bonus to a specific {@link bugemon.common.StatType}.</li>
 * <li>{@link TypeMultiplierEffect} — multiplies damage dealt or received for a given
 * {@link bugemon.common.models.bugemon.ElementType}.</li>
 * <li>{@link CritBonusEffect} — increases the probability of landing a critical hit.</li>
 * <li>{@link RegenPostCombatEffect} — restores a percentage of max HP after each combat.</li>
 * <li>{@link XpMultiplierEffect} — multiplies XP gained from battles.</li>
 * <li>{@link StarterItemsEffect} — grants a quantity of items of a given {@link bugemon.common.models.item.ItemType} at
 * the start of a run.</li>
 * <li>{@link RewardChoiceEffect} — increases the number of reward options offered after combat.</li>
 * </ul>
 */
public sealed interface SkillEffect extends Serializable {

    record StatBonusEffect(StatType stat, int bonus) implements SkillEffect {

    }

    record TypeMultiplierEffect(ElementType type, double mult) implements SkillEffect {
    }

    record CritBonusEffect(double extraChance) implements SkillEffect {
    }

    record RegenPostCombatEffect(double percent) implements SkillEffect {
    }

    record XpMultiplierEffect(double multiplier) implements SkillEffect {
    }

    record StarterItemsEffect(int quantity, ItemType type) implements SkillEffect {
    }

    record RewardChoiceEffect(int totalChoices) implements SkillEffect {
    }
}
