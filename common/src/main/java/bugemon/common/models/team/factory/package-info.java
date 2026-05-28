/**
 * Factory classes for building {@link bugemon.common.models.team.Team} instances used by NPC opponents.
 *
 * <p>
 * The abstract base class {@link bugemon.common.models.team.factory.TeamFactory} declares the {@code create(int, List)} factory
 * method. Two concrete implementations are provided:
 *
 * <ul>
 * <li>{@link bugemon.common.models.team.factory.BossTeamFactory} — always places the designated boss Bugémon first, then fills
 * remaining slots randomly.</li>
 * <li>{@link bugemon.common.models.team.factory.RandomTeamFactory} — fills all slots with randomly selected non-boss
 * Bugémons.</li>
 * </ul>
 *
 * <p>
 * A {@link java.util.Random} instance seeded from the run seed is injected at construction time to ensure reproducible
 * floor layouts.
 */
package bugemon.common.models.team.factory;
