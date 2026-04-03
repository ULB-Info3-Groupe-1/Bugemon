package ulb.common.dto;

import ulb.models.level_up.Upgrade;

/**
 * Data Transfer Object (DTO) interface exposing the data needed to render a level-up screen for a
 * {@link ulb.models.bugemon.Bugemon} that has accumulated enough XP to advance to the next level.
 *
 * <p>
 * {@code LevelUpDTO} decouples the view and controller layers from the full {@link ulb.models.level_up.LevelUp}
 * implementation by exposing only the subset of information required to display the level-up prompt: the Bugemon that
 * levelled up (via {@link BugemonDTO}) and the list of stat-bonus {@link Upgrade}s offered to the player.
 * </p>
 *
 * <p>
 * The {@link ulb.models.level_up.LevelUp} class implements this interface.
 * </p>
 *
 * @see ulb.models.level_up.LevelUp
 * @see BugemonDTO
 * @see Upgrade
 */
public interface LevelUpDTO {
    /**
     * Returns a {@link BugemonDTO} view of the {@link ulb.models.bugemon.Bugemon} that triggered this level-up event.
     *
     * @return the levelling-up Bugemon as a {@link BugemonDTO}; never {@code null}.
     */
    BugemonDTO getBugemon();

    Upgrade get(int idx);
}
