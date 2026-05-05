package ulb.common.dto;

import ulb.models.bugemon.BugemonType;

/**
 * Read-only view of a {@link ulb.models.bugemon.Bugemon} for use outside the model layer (e.g., in views and
 * controllers). Exposes only the subset of data required to render a Bugemon on screen.
 *
 * @see ulb.models.bugemon.Bugemon
 */
public interface BugemonDTO {
    /** Classpath-relative resource path to the sprite image (e.g., {@code "png/florachu.png"}). */
    String getSpriteURL();

    /** Stable, opaque identifier that uniquely distinguishes one Bugemon from another (e.g., {@code "001"}). */
    String getId();

    /** Human-readable label shown in the UI (e.g., {@code "Florachu"}). */
    String getName();

    /**
     * Elemental type of this Bugemon. Affects combat effectiveness — see
     * {@link ulb.services.CombatService#compareBugemonType} for matchup rules.
     *
     * @return the {@link BugemonType}; never {@code null}.
     */
    BugemonType getType();

    /**
     * Current HP. When it reaches zero the Bugemon is considered defeated and
     * {@link ulb.models.bugemon.Bugemon#isAlive()} returns {@code false}.
     */
    int getHp();

    int getMaxHp();

    /**
     * Current level. Starts at {@code 1} and increases when the Bugemon accumulates enough XP via
     * {@link ulb.models.bugemon.Bugemon#gainXp(int)}.
     */
    int getLevel();

    int getXp();

    /**
     * XP progress toward the next level as a fraction in {@code [0.0, 1.0]}.
     *
     * @return {@code 0.0} at the start of a level, {@code 1.0} at the threshold for the next level-up.
     */
    double getXpProgress();

    boolean isAlive();
}
