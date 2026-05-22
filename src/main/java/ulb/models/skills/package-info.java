/**
 * Skill-tree model for the Bugemon game.
 *
 * <p>
 * A {@link ulb.models.skills.SkillTree} is an immutable graph of {@link ulb.models.skills.SkillNode}s loaded from
 * configuration. Each node carries a {@link ulb.models.skills.SkillEffect} that is applied when the node is unlocked.
 * Run-time progress (unlock levels, available skill points) is tracked in a separate
 * {@link ulb.models.skills.SkillTreeState} so the tree definition can be shared across players.
 */
package ulb.models.skills;
