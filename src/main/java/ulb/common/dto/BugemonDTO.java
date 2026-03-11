package ulb.common.dto;

import ulb.models.bugemon.BugemonType;

/**
 * Data Transfer Object (DTO) interface exposing a read-only view of a
 * {@link Bugemon} for use outside the model layer (e.g., in views and
 * controllers).
 *
 * <p>
 * {@code BugemonDTO} decouples the view and controller layers from the full
 * {@link Bugemon} implementation by providing only the subset of data required
 * to render a Bugemon on screen. Any class that implements this interface can
 * be safely passed to UI components without exposing mutable model internals.
 * </p>
 *
 * <p>
 * The {@link Bugemon} class itself implements this interface. Other lightweight
 * or decorating wrappers may also implement it.
 * </p>
 *
 * @see Bugemon
 */
public interface BugemonDTO {
    /**
     * Returns the path to the sprite image associated with this Bugemon.
     *
     * <p>
     * The returned string is a classpath-relative resource path (e.g.,
     * {@code "png/florachu.png"}) suitable for use with
     * {@link ClassLoader#getResource(String)}.
     * </p>
     *
     * @return the sprite resource path as a non-{@code null} {@code String}.
     */
    String getSpriteURL();

    /**
     * Returns the unique identifier of this Bugemon.
     *
     * <p>
     * The ID is a stable, opaque string that uniquely distinguishes one Bugemon
     * from another across the whole game (e.g., {@code "001"}).
     * </p>
     *
     * @return the unique identifier as a non-{@code null} {@code String}.
     */
    String getId();

    /**
     * Returns the display name of this Bugemon.
     *
     * <p>
     * The name is the human-readable label shown in the UI (e.g.,
     * {@code "Florachu"}).
     * </p>
     *
     * @return the display name as a non-{@code null} {@code String}.
     */
    String getName();

    /**
     * Returns the elemental type of this Bugemon.
     *
     * <p>
     * The type affects combat effectiveness calculations; see
     * {@link ulb.models.combat.CombatHelper#compareBType} for the type
     * matchup rules.
     * </p>
     *
     * <p>
     * <em>Note:</em> The {@link Bugemon.BType} enum is currently defined as a
     * nested type inside {@link Bugemon} and may be moved to a more neutral
     * location in a future refactor.
     * </p>
     *
     * @return the {@link Bugemon.BType} of this Bugemon; never {@code null}.
     */
    BugemonType getType();

    /**
     * Returns the current hit points (HP) of this Bugemon.
     *
     * <p>
     * HP represents the Bugemon's remaining health during a battle. When HP
     * reaches zero the Bugemon is considered defeated and
     * {@link Bugemon#isAlive()} returns {@code false}.
     * </p>
     *
     * @return the current HP as a non-negative {@code int}.
     */
    int getHp();

    /**
     * Returns the maximum health points (Max HP) of the Bugemon.
     * @return (int) the maximum health points of the Bugemon as an integer
     */
    int getMaxHp();

    int getLevel();
}
