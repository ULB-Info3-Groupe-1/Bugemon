package ulb.models.team.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ulb.models.bugemon.Bugemon;
import ulb.models.team.Team;

/** Produces a {@link Team} filled with randomly-selected non-boss Bugémons. */
public class RandomTeamFactory extends TeamFactory {

    public RandomTeamFactory(List<Bugemon> bugemons, Random random) {
        super(bugemons, random);
    }

    @Override
    public Team create(int size) {
        Team team = new Team();
        List<Bugemon> available = new ArrayList<>(this.bugemons);
        available.removeIf(Bugemon::isBoss);
        this.fillTeam(size, team, available);
        return team;
    }
}
