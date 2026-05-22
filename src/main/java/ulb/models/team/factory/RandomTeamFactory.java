package ulb.models.team.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ulb.models.bugemon.Bugemon;
import ulb.models.team.Team;

/**
 * Produces a {@link Team} filled entirely with randomly selected non-boss Bugémons.
 *
 * <p>
 * Boss Bugémons (as identified by {@link ulb.models.bugemon.Bugemon#isBoss()}) are excluded from the candidate pool
 * before selection.
 */
public class RandomTeamFactory extends TeamFactory {

    /**
     * @param random
     *            the seeded random number generator used for member selection
     */
    public RandomTeamFactory(Random random) {
        super(random);
    }

    @Override
    public Team create(int size, List<Bugemon> bugemons) {
        this.checkSize(size);
        Team team = new Team();
        List<Bugemon> available = new ArrayList<>(bugemons);
        available.removeIf(Bugemon::isBoss);
        this.fillTeam(size, team, available);
        return team;
    }
}
