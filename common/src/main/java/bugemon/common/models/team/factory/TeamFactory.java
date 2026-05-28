package bugemon.common.models.team.factory;

import java.util.List;
import java.util.Random;

import bugemon.common.Configuration;
import bugemon.common.models.bugemon.Bugemon;
import bugemon.common.models.player.PlayerBugemon;
import bugemon.common.models.team.Team;

/**
 * Abstract base class for team factories using the Factory Method pattern.
 *
 * <p>
 * Declares the factory method {@link #create(int, List)} that each concrete subclass overrides to assemble a specific
 * {@link Team} variant. The pool of available Bugémons is passed at call time so that the factory always operates on
 * the current, floor-specific pool.
 *
 * <p>
 * A {@link Random} instance seeded from the run seed is injected at construction time to guarantee reproducible team
 * composition across identical seeds.
 */
public abstract class TeamFactory {

    protected final Random random;

    /**
     * @param random
     *            the seeded random number generator used for member selection
     */
    protected TeamFactory(Random random) {
        this.random = random;
    }

    /**
     * Factory method: builds and returns a {@link Team} of the requested {@code size} drawn from the given
     * {@code bugemons} pool.
     *
     * @param size
     *            the desired number of members; must be within configured min/max bounds
     * @param bugemons
     *            the pool of candidate Bugémons to draw from
     * @return a new {@link Team} populated according to the concrete strategy
     * @throws IllegalArgumentException
     *             if {@code size} is outside the configured bounds
     */
    public abstract Team create(int size, List<Bugemon> bugemons);

    /**
     * Fills {@code team} with randomly selected members from {@code available} until {@code team} reaches {@code size}
     * members or {@code available} is exhausted.
     *
     * @param size
     *            the target team size
     * @param team
     *            the team to fill (may already contain some members)
     * @param available
     *            the mutable list of candidates; chosen entries are removed in-place
     */
    protected void fillTeam(int size, Team team, List<Bugemon> available) {
        while (team.size() < size && !available.isEmpty()) {
            int idx = this.random.nextInt(available.size());
            team.add(new PlayerBugemon(available.remove(idx)));
        }
    }

    /**
     * Validates that {@code size} falls within the configured team size bounds.
     *
     * @param size
     *            the size to validate
     * @throws IllegalArgumentException
     *             if {@code size} is below the minimum or above the maximum
     */
    protected void checkSize(int size) {
        if (size < Configuration.Game.MIN_TEAM_SIZE) {
            throw new IllegalArgumentException(
                    String.format("Team size must be at least %d.", Configuration.Game.MIN_TEAM_SIZE));
        }
        if (size > Configuration.Game.MAX_TEAM_SIZE) {
            throw new IllegalArgumentException(
                    String.format("Team size cannot exceed %d.", Configuration.Game.MAX_TEAM_SIZE));
        }
    }
}
