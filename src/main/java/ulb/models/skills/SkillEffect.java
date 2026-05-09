package ulb.models.skills;

import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.effect.EffectStat;

public sealed interface SkillEffect {

    record StatBonusEffect(EffectStat stat, int bonus) implements SkillEffect {

    }

    record TypeMultiplierEffect(BugemonType type, double mult) implements SkillEffect {
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
