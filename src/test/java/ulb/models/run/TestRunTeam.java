package ulb.models.run;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ulb.models.BugemonFixtures;
import ulb.models.player.PlayerBugemon;

public class TestRunTeam {

    private RunBugemon aquaBugemon;
    private RunBugemon floraBugemon;
    private RunTeam team;

    @Before
    public void setUp() {
        this.aquaBugemon = new RunBugemon(new PlayerBugemon(BugemonFixtures.slowAqua()));
        this.floraBugemon = new RunBugemon(new PlayerBugemon(BugemonFixtures.fastFlora()));
        this.team = new RunTeam("team", List.of(this.aquaBugemon, this.floraBugemon));
    }

    @Test
    public void getEligibleForShouldExcludeBugemonWeakAgainstAttack() {
        // FLORA attack is super-effective against AQUA → only FLORA bugemon eligible
        List<RunBugemon> eligible = this.team.getEligibleFor(BugemonFixtures.floraAttack());
        assertEquals(1, eligible.size());
        assertTrue(eligible.contains(this.floraBugemon));
    }

    @Test
    public void getEligibleForShouldIncludeAllWhenNoneAreWeak() {
        // NORMAL attack is never super-effective → all members eligible
        List<RunBugemon> eligible = this.team.getEligibleFor(BugemonFixtures.attack("n", 10));
        assertEquals(2, eligible.size());
    }

    @Test
    public void getEligibleForShouldReturnEmptyWhenAllMembersAreWeak() {
        // AQUA attack is super-effective against PYRO; team is all AQUA → none weak against AQUA
        // Use a team where all members are weak: FLORA attack vs all-AQUA team
        RunTeam allAqua = new RunTeam("all-aqua",
                List.of(this.aquaBugemon, new RunBugemon(new PlayerBugemon(BugemonFixtures.slowAqua()))));
        List<RunBugemon> eligible = allAqua.getEligibleFor(BugemonFixtures.floraAttack());
        assertTrue(eligible.isEmpty());
    }
}
