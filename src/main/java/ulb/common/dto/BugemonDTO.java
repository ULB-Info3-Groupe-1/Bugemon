package ulb.common.dto;

import ulb.models.bugemon.BugemonType;

/**
 * Read-only view of a {@link ulb.models.bugemon.Bugemon} for use outside the model layer.
 * Exposes only the subset of data required to render a Bugemon on screen.
 */
public interface BugemonDTO {
    /** Classpath-relative resource path to the sprite image (e.g., {@code "png/florachu.png"}). */
    String getSpriteURL();

    /** Stable, opaque identifier that uniquely distinguishes one Bugemon from another (e.g., {@code "001"}). */
    String getId();

    String getName();

    /**
     * Elemental type; never {@code null}.
     * Affects combat effectiveness — see {@link ulb.services.CombatService} for matchup rules.
     */
    BugemonType getType();

    /**
     * Current HP. When it reaches zero the Bugemon is considered defeated and
     * {@link ulb.models.bugemon.Bugemon#isAlive()} returns {@code false}.
     */
    int getHp();

    int getMaxHp();

    int getLevel();

    int getXp();

    /**
     * XP progress toward the next level as a fraction in {@code [0.0, 1.0]}.
     *
     * @return {@code 0.0} at the start of a level, {@code 1.0} at the threshold for the next level-up.
     */
    double getXpProgress();
}
