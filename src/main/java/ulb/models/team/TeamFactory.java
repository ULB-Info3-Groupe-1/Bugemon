package ulb.models.team;

import java.util.List;
import java.util.Random;

import ulb.models.bugemon.Bugemon;
import ulb.models.player.PlayerBugemon;

/**
 * Abstract creator — declares the factory method {@link #create(int)} that each concrete subclass overrides to
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
     * Fills {@code team} with randomly-picked members from {@code available} until it reaches {@code size}.
     */
    protected void fillTeam(int size, Team team, List<Bugemon> available) {
        while (team.size() < size && !available.isEmpty()) {
            int idx = this.random.nextInt(available.size());
            team.add(new PlayerBugemon(available.remove(idx)));
        }
    }
}
