/**
 * Contains the classes representing the team of Bugemons owned by a trainer
 * in the Bugemon game.
 *
 * <h2>Package overview</h2>
 * <p>
 * A {@link ulb.models.bugemon_team.BugemonTeam} is an ordered, fixed-capacity
 * collection of up to six {@link ulb.models.bugemon.Bugemon}s. It is the
 * primary data structure passed to a {@link ulb.models.trainer.Trainer} at
 * construction time and referenced throughout the combat system.
 * </p>
 *
 * <h2>Key classes</h2>
 * <ul>
 *   <li>{@link ulb.models.bugemon_team.BugemonTeam} — the main collection
 *       class. It exposes add/remove/query operations, implements
 *       {@link java.lang.Iterable} for convenient iteration over live members,
 *       and provides a static factory
 *       {@link ulb.models.bugemon_team.BugemonTeam#createRandomTeam(java.util.List, int)}
 *       used by the combat system to build opponent teams on the fly.</li>
 *   <li>{@link ulb.models.bugemon_team.exceptions.BugemonAlreadyExistsException} —
 *       unchecked exception thrown by
 *       {@link ulb.models.bugemon_team.BugemonTeam#addBugemon(ulb.models.bugemon.Bugemon)}
 *       when an attempt is made to add a Bugemon whose ID is already present
 *       in the team.</li>
 * </ul>
 *
 * <h2>Constraints enforced by {@code BugemonTeam}</h2>
 * <ul>
 *   <li><strong>Capacity:</strong> a team holds at most 6 members; attempting
 *       to add a seventh throws {@link java.lang.IllegalStateException}.</li>
 *   <li><strong>Uniqueness:</strong> each Bugemon ID may appear at most once;
 *       duplicates are rejected with
 *       {@link ulb.models.bugemon_team.exceptions.BugemonAlreadyExistsException}.</li>
 *   <li><strong>Removal guards:</strong> removing from an empty team or
 *       requesting a member by an unknown ID throws
 *       {@link java.lang.IllegalArgumentException}.</li>
 * </ul>
 *
 * <h2>Design notes</h2>
 * <ul>
 *   <li>The backing store is a plain array of size 6; {@code null} slots
 *       represent empty positions. The {@link java.util.Iterator} returned by
 *       {@link ulb.models.bugemon_team.BugemonTeam#iterator()} skips
 *       {@code null} entries transparently.</li>
 *   <li>{@link ulb.models.bugemon_team.BugemonTeam#reset()} restores all
 *       members to their initial stat values by delegating to
 *       {@link ulb.models.bugemon.Bugemon#reset()}, making teams reusable
 *       across combat sessions.</li>
 *   <li>Random team creation via
 *       {@link ulb.models.bugemon_team.BugemonTeam#createRandomTeam(java.util.List, int)}
 *       deep-clones each selected Bugemon so the opponent's team holds
 *       independent instances that do not share state with the source pool.</li>
 * </ul>
 *
 * @see ulb.models.bugemon.Bugemon
 * @see ulb.models.trainer.Trainer
 * @see ulb.models.combat
 */
package ulb.models.bugemon_team;
