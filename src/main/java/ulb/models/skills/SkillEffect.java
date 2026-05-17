package ulb.models.skills;

import ulb.common.StatType;
import ulb.models.bugemon.ElementType;

public sealed interface SkillEffect {

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

    record StarterItemsEffect(int quantity, String category) implements SkillEffect {
    }

    record RewardChoiceEffect(int totalChoices) implements SkillEffect {
    }
}
