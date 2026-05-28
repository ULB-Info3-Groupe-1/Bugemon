package bugemon.common.dto.persistence;

/**
 * Serialisable snapshot of a single skill-tree node and its current investment level.
 *
 * @param skillId
 *            unique identifier of the skill node as defined in the skill-tree resource file
 * @param level
 *            the number of points the player has invested in this skill (0 = not unlocked)
 */
public record SkillDTO(String skillId, int level) {
}
