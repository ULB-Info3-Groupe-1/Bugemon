package bugemon.common.models.team.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import bugemon.common.models.bugemon.Bugemon;
import bugemon.common.models.player.PlayerBugemon;
import bugemon.common.models.team.Team;

/**
 * Produces a {@link Team} led by the designated boss Bugémon, with remaining slots filled by randomly selected non-boss
 * members.
 *
 * <p>
 * Exactly one Bugémon in the pool must be marked with {@link bugemon.common.models.bugemon.Bugemon#isBoss()}; if none is found,
 * {@link #create(int, List)} throws {@link IllegalStateException}.
 */
public class BossTeamFactory extends TeamFactory {

    /**
     * @param random
     *            the seeded random number generator used for non-boss member selection
     */
    public BossTeamFactory(Random random) {
        super(random);
    }

    @Override
    public Team create(int size, List<Bugemon> bugemons) {
        this.checkSize(size);
        Bugemon boss = bugemons.stream().filter(Bugemon::isBoss).findAny()
                .orElseThrow(() -> new IllegalStateException("No boss Bugemon found"));
        Team team = new Team();
        team.add(new PlayerBugemon(boss));
        List<Bugemon> available = new ArrayList<>(bugemons);
        available.removeIf(Bugemon::isBoss);
        this.fillTeam(size, team, available);
        return team;
    }
}
