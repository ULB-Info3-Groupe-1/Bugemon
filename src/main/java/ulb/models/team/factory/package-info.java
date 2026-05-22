/**
 * Factory classes for building {@link ulb.models.team.Team} instances used by NPC opponents.
 *
 * <p>
 * The abstract base class {@link ulb.models.team.factory.TeamFactory} declares the {@code create(int, List)} factory
 * method. Two concrete implementations are provided:
 *
 * <ul>
 * <li>{@link ulb.models.team.factory.BossTeamFactory} — always places the designated boss Bugémon first, then fills
 * remaining slots randomly.</li>
 * <li>{@link ulb.models.team.factory.RandomTeamFactory} — fills all slots with randomly selected non-boss
 * Bugémons.</li>
 * </ul>
 *
 * <p>
 * A {@link java.util.Random} instance seeded from the run seed is injected at construction time to ensure reproducible
 * floor layouts.
 */
package ulb.models.team.factory;
