package ulb.common.dto;

import java.util.List;

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

    /**
     * Returns the list of stat-bonus {@link Upgrade}s offered to the player during this level-up event.
     *
     * <p>
     * Typically contains exactly three choices, each with randomly distributed bonuses across HP, attack, defense, and
     * initiative.
     * </p>
     *
     * @return an unmodifiable {@link List} of {@link Upgrade} instances; never {@code null}.
     */
    List<Upgrade> getChoices();
}
