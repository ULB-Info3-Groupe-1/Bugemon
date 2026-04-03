/**
 * Level-up flow model. A {@link ulb.models.level_up.LevelUp} generates three random {@link ulb.models.level_up.Upgrade}
 * choices by distributing 10 points across four stats (HP and initiative are scaled ×2). A
 * {@link ulb.models.level_up.LevelUpSession} sequences multiple level-up events when several Bugemons level up after
 * the same combat.
 */
package ulb.models.level_up;
