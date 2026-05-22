package ulb.common.dto.display;

import ulb.models.bugemon.ElementType;

/**
 * Lightweight display snapshot of a Bugemon during an active combat run, carrying only the fields needed to render the
 * combat HUD.
 *
 * @param name
 *            display name of the Bugemon
 * @param level
 *            current level
 * @param currentHp
 *            remaining hit points
 * @param maxHp
 *            maximum hit points (base + bonuses)
 * @param type
 *            elemental type, used to tint the combat UI
 */
public record RunBugemonDisplayDTO(String name, int level, int currentHp, int maxHp, ElementType type) {
}
