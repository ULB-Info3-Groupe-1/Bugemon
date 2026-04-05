package ulb.common.dto;

import ulb.models.level_up.Upgrade;

/**
 * Read-only view of a level-up event, exposing the Bugemon that levelled up (via {@link BugemonDTO}) and the
 * stat-bonus {@link Upgrade}s offered to the player.
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
