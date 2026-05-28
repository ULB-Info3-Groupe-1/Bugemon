/**
 * Skill-tree model for the Bugemon game.
 *
 * <p>
 * A {@link bugemon.common.models.skills.SkillTree} is an immutable graph of
 * {@link bugemon.common.models.skills.SkillNode}s loaded from configuration. Each node carries a
 * {@link bugemon.common.models.skills.SkillEffect} that is applied when the node is unlocked. Run-time progress (unlock
 * levels, available skill points) is tracked in a separate {@link bugemon.common.models.skills.SkillTreeState} so the
 * tree definition can be shared across players.
 */
package bugemon.common.models.skills;
