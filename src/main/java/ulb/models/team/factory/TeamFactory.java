package ulb.models.team.factory;

import java.util.List;
import java.util.Random;

import ulb.Configuration;
import ulb.models.bugemon.Bugemon;
import ulb.models.player.PlayerBugemon;
import ulb.models.team.Team;

/**
 * Abstract creator — declares the factory method {@link #create(int)} that each
 * concrete subclass overrides to
 * instantiate a specific {@link Team} variant.
 */
public abstract class TeamFactory {

    protected final List<Bugemon> bugemons;
    protected final Random random;

    protected TeamFactory(List<Bugemon> bugemons, Random random) {
        this.bugemons = bugemons;
        this.random = random;
    }

    /** Factory method: builds and returns a {@link Team} of the requested size. */
    public abstract Team create(int size);

    /**
     * Fills {@code team} with randomly-picked members from {@code available} until
     * it reaches {@code size}.
     */
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
