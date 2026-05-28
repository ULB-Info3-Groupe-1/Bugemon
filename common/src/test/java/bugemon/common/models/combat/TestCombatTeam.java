package bugemon.common.models.combat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import bugemon.common.models.BugemonFixtures;
import bugemon.common.models.bugemon.Bugemon;
import bugemon.common.models.player.PlayerBugemon;
import bugemon.common.models.run.RunBugemon;

public class TestCombatTeam {

    private CombatBugemon aliveBugemon;
    private CombatBugemon anotherAliveBugemon;

    private static CombatBugemon combatBugemon(Bugemon base) {
        return new CombatBugemon(new RunBugemon(new PlayerBugemon(base)));
    }

    @Before
    public void setUp() {
        this.aliveBugemon = combatBugemon(BugemonFixtures.fastFlora());
        this.anotherAliveBugemon = combatBugemon(BugemonFixtures.slowAqua());
    }

    @Test
    public void shouldThrowIllegalArgumentException_whenConstructingWithEmptyList() {
        assertThrows(IllegalArgumentException.class, () -> {
            new CombatTeam(List.of());
        });
    }

    @Test
    public void shouldThrowIllegalArgumentException_whenAllMembersAreKo() {
        this.aliveBugemon.takeDamage(this.aliveBugemon.getMaxHp());
        assertThrows(IllegalArgumentException.class, () -> {
            new CombatTeam(List.of(this.aliveBugemon));
        });
    }

    @Test
    public void shouldThrowIllegalArgumentException_whenSwitchingToKoTarget() {
        CombatTeam team = new CombatTeam(List.of(this.aliveBugemon, this.anotherAliveBugemon));

        this.anotherAliveBugemon.takeDamage(this.anotherAliveBugemon.getMaxHp());

        assertThrows(IllegalArgumentException.class, () -> {
            team.setActive(this.anotherAliveBugemon);
        });
    }

    @Test
    public void shouldThrowIllegalArgumentException_whenSwitchingToMemberNotInTeam() {
        CombatTeam team = new CombatTeam(List.of(this.aliveBugemon));

        assertThrows(IllegalArgumentException.class, () -> {
            team.setActive(this.anotherAliveBugemon);
        });
    }

    @Test
    public void shouldSwitchActive_whenTargetIsAliveAndInTeam() {
        CombatTeam team = new CombatTeam(List.of(this.aliveBugemon, this.anotherAliveBugemon));

        team.setActive(this.anotherAliveBugemon);

        assertEquals(this.anotherAliveBugemon, team.getActive());
    }

    @Test
    public void shouldSetFirstAliveAsActive_whenConstructed() {
        CombatTeam team = new CombatTeam(List.of(this.aliveBugemon, this.anotherAliveBugemon));
        assertEquals(this.aliveBugemon, team.getActive());
    }

    @Test
    public void shouldReportDefeated_whenAllMembersAreKoAfterDamage() {
        CombatTeam team = new CombatTeam(List.of(this.aliveBugemon));
        this.aliveBugemon.takeDamage(this.aliveBugemon.getMaxHp());
        assertEquals(true, team.isDefeated());
    }
}
