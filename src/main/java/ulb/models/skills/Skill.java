package ulb.models.skills;

import ulb.models.bugemon.effect.Effect;

/**
 * Represents a skill that can be unlocked in the skill tree.
 */
public record Skill(String id, String name, String description, int cost, int maxLevel, Effect effect,
        boolean isUnlocked) {
}
