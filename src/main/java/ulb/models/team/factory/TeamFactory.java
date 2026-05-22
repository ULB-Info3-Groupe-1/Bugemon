package ulb.models.team.factory;

import java.util.List;
import java.util.Random;

import ulb.Configuration;
import ulb.models.bugemon.Bugemon;
import ulb.models.player.PlayerBugemon;
import ulb.models.team.Team;

/**
 * Abstract creator. Declares the factory method {@link #create(int, List)} that each concrete subclass overrides to
 * instantiate a specific {@link Team} variant.
 *
 * The list of available bugemons is passed at call time so the factory always uses the current pool.
 */
public abstract class TeamFactory {

    protected final Random random;

    protected TeamFactory(Random random) {
        this.random = random;
    }

    /** Factory method: builds and returns a {@link Team} of the requested size from the given bugemon pool. */
    public abstract Team create(int size, List<Bugemon> bugemons);

    protected void fillTeam(int size, Team team, List<Bugemon> available) {
        while (team.size() < size && !available.isEmpty()) {
            int idx = this.random.nextInt(available.size());
            team.add(new PlayerBugemon(available.remove(idx)));
        }
    }

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
