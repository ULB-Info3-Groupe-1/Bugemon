package ulb.models.combat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonBuilder;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.trainer.AutoTrainer;
import ulb.utils.test.TestUtilsBugemons;
import ulb.utils.test.TestUtilsTrainer;

public class TestCombat {
    private Attack defaultAttack;

    @Before
    public void setUp() {
        this.defaultAttack = new Attack("atk", "TestAttack", BugemonType.FLORA, "", 30, new ArrayList<>());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static List<TurnStep> stepsAsList(TurnResult result) {
        List<TurnStep> list = new ArrayList<>();
        result.steps().forEachRemaining(list::add);
        return list;
    }

    /** Simulates controller KO reactions between turns so combat can continue. */
    private static void processKoReactions(TurnResult result) {
        stepsAsList(result).forEach(step -> {
            if (step instanceof TurnStep.BugemonKoStep koStep && !koStep.trainer().isDefeated()) {
                koStep.trainer().reactToKo();
            }
        });
    }

    private static boolean hasTrainerKoStep(TurnResult result) {
        return stepsAsList(result).stream().anyMatch(s -> s instanceof TurnStep.TrainerKoStep);
    }

    private Bugemon bugemonWithHp(String name, int hp, int initiative) {
        List<Attack> attacks = List.of(this.defaultAttack, TestUtilsBugemons.createAttack("atk2", BugemonType.FLORA, 0),
                TestUtilsBugemons.createAttack("atk3", BugemonType.FLORA, 0));
        return new BugemonBuilder().name(name).hp(hp).attack(0).defense(0).initiative(initiative).attackList(attacks)
                .build();
    }

    private Bugemon strongAttacker(String name) {
        // AutoTrainer picks randomly among attacks; keep all three lethal so tests stay deterministic.
        Attack powerAttack1 = new Attack("power1", "PowerAttack1", BugemonType.FLORA, "", 9999, new ArrayList<>());
        Attack powerAttack2 = new Attack("power2", "PowerAttack2", BugemonType.FLORA, "", 9999, new ArrayList<>());
        Attack powerAttack3 = new Attack("power3", "PowerAttack3", BugemonType.FLORA, "", 9999, new ArrayList<>());
        List<Attack> attacks = List.of(powerAttack1, powerAttack2, powerAttack3);
        return new BugemonBuilder().name(name).hp(100).attack(9999).defense(0).initiative(9999).attackList(attacks)
                .build();
    }

    private AutoTrainer autoOf(Bugemon... bugemons) {
        BugemonTeam team = new BugemonTeam();
        for (Bugemon b : bugemons) {
            team.add(b);
        }
        return new AutoTrainer(team);
    }

    // ── Attack steps ──────────────────────────────────────────────────────────

    @Test
    public void turn_shouldContainTwoAttackSteps_whenBothTrainersAttack() {
        AutoTrainer t1 = TestUtilsTrainer.createDefaultAutoTrainer();
        AutoTrainer t2 = TestUtilsTrainer.createDefaultAutoTrainer();
        Combat combat = new Combat(t1, t2);

        List<TurnStep> steps = stepsAsList(combat.turn());

        long attackCount = steps.stream().filter(s -> s instanceof TurnStep.AttackStep).count();
        assertEquals(2, attackCount);
    }

    @Test
    public void turn_attackStep_shouldReduceDefenderHp() {
        AutoTrainer attacker = TestUtilsTrainer.createDefaultAutoTrainer();
        AutoTrainer defender = TestUtilsTrainer.createDefaultAutoTrainer();
        Combat combat = new Combat(attacker, defender);
        int initialHp = defender.getCurrentBugemonHp();

        combat.turn();

        assertTrue(defender.getCurrentBugemonHp() < initialHp);
    }

    @Test
    public void turn_attackStep_shouldContainCorrectAttacker() {
        AutoTrainer t1 = TestUtilsTrainer.createDefaultAutoTrainer();
        AutoTrainer t2 = TestUtilsTrainer.createDefaultAutoTrainer();
        Combat combat = new Combat(t1, t2);

        List<TurnStep> steps = stepsAsList(combat.turn());

        boolean t1Attacks = steps.stream().anyMatch(s -> s instanceof TurnStep.AttackStep a && a.attacker() == t1);
        boolean t2Attacks = steps.stream().anyMatch(s -> s instanceof TurnStep.AttackStep a && a.attacker() == t2);
        assertTrue(t1Attacks);
        assertTrue(t2Attacks);
    }

    // ── KO steps ──────────────────────────────────────────────────────────────

    @Test
    public void turn_shouldContainBugemonKoStep_whenBugemonFaintsButTrainerHasOthers() {
        Bugemon weak = this.bugemonWithHp("weak", 1, 0);
        Bugemon survivor = this.bugemonWithHp("survivor", 100, 0);
        AutoTrainer victim = this.autoOf(weak, survivor);
        AutoTrainer attacker = this.autoOf(this.strongAttacker("str"));
        Combat combat = new Combat(attacker, victim);

        List<TurnStep> steps = stepsAsList(combat.turn());

        assertTrue(steps.stream().anyMatch(s -> s instanceof TurnStep.BugemonKoStep));
        assertFalse(steps.stream().anyMatch(s -> s instanceof TurnStep.TrainerKoStep));
    }

    @Test
    public void turn_bugemonKoStep_shouldReferenceVictimTrainer() {
        Bugemon weak = this.bugemonWithHp("weak", 1, 0);
        Bugemon survivor = this.bugemonWithHp("survivor", 100, 0);
        AutoTrainer victim = this.autoOf(weak, survivor);
        AutoTrainer attacker = this.autoOf(this.strongAttacker("str"));
        Combat combat = new Combat(attacker, victim);

        List<TurnStep> steps = stepsAsList(combat.turn());

        TurnStep.BugemonKoStep koStep = (TurnStep.BugemonKoStep) steps.stream()
                .filter(s -> s instanceof TurnStep.BugemonKoStep).findFirst().orElseThrow();
        assertEquals(victim, koStep.trainer());
    }

    @Test
    public void turn_shouldContainTrainerKoStep_whenLastBugemonFaints() {
        Bugemon weak = this.bugemonWithHp("weak", 1, 0);
        AutoTrainer victim = this.autoOf(weak);
        AutoTrainer attacker = this.autoOf(this.strongAttacker("str"));
        Combat combat = new Combat(attacker, victim);

        List<TurnStep> steps = stepsAsList(combat.turn());

        assertTrue(steps.stream().anyMatch(s -> s instanceof TurnStep.TrainerKoStep));
        TurnStep.TrainerKoStep koStep = (TurnStep.TrainerKoStep) steps.stream()
                .filter(s -> s instanceof TurnStep.TrainerKoStep).findFirst().orElseThrow();
        assertEquals(victim, koStep.trainerKo());
    }

    @Test
    public void turn_shouldNotContainSecondAttackStep_whenFirstAttackKillsOpponent() {
        Bugemon weak = this.bugemonWithHp("weak", 1, 0);
        Bugemon survivor = this.bugemonWithHp("survivor", 100, 0);
        // strong attacker has initiative=9999 so goes first, kills weak before it
        // attacks
        AutoTrainer victim = this.autoOf(weak, survivor);
        AutoTrainer attacker = this.autoOf(this.strongAttacker("str"));
        Combat combat = new Combat(attacker, victim);

        List<TurnStep> steps = stepsAsList(combat.turn());

        long attackCount = steps.stream().filter(s -> s instanceof TurnStep.AttackStep).count();
        assertEquals(1, attackCount);
    }

    // ── Full combat lifecycle ─────────────────────────────────────────────────

    @Test
    public void combat_shouldEventuallyEndWithTrainerKoStep() {
        AutoTrainer t1 = TestUtilsTrainer.createDefaultAutoTrainer();
        AutoTrainer t2 = TestUtilsTrainer.createDefaultAutoTrainer();
        Combat combat = new Combat(t1, t2);

        TurnResult lastResult = null;
        boolean ended = false;
        int maxTurns = 1000;

        while (!ended && maxTurns-- > 0) {
            lastResult = combat.turn();
            processKoReactions(lastResult);
            ended = hasTrainerKoStep(lastResult);
        }

        assertTrue("Combat should end within 1000 turns", ended);
        assertTrue(t1.isDefeated() || t2.isDefeated());
    }

    @Test
    public void combat_winner_shouldBeNonDefeatedTrainer_afterTrainerKoStep() {
        AutoTrainer t1 = TestUtilsTrainer.createDefaultAutoTrainer();
        AutoTrainer t2 = TestUtilsTrainer.createDefaultAutoTrainer();
        Combat combat = new Combat(t1, t2);

        TurnResult lastResult = null;
        boolean ended = false;
        int maxTurns = 1000;

        while (!ended && maxTurns-- > 0) {
            lastResult = combat.turn();
            processKoReactions(lastResult);
            ended = hasTrainerKoStep(lastResult);
        }

        assertTrue("Combat should end within 1000 turns", ended);

        // The TrainerKoStep tells us who lost; the other is the winner
        final TurnResult finalResult = lastResult;
        TurnStep.TrainerKoStep koStep = (TurnStep.TrainerKoStep) stepsAsList(finalResult).stream()
                .filter(s -> s instanceof TurnStep.TrainerKoStep).findFirst().orElseThrow();

        assertTrue(koStep.trainerKo().isDefeated());
        assertFalse(koStep.trainerKo() == t1 ? t2.isDefeated() : t1.isDefeated());
    }

    @Test
    public void combat_withOneHitKiller_shouldEndInOneTurn() {
        Bugemon weak = this.bugemonWithHp("weak", 1, 0);
        AutoTrainer victim = this.autoOf(weak);
        AutoTrainer killer = this.autoOf(this.strongAttacker("str"));
        Combat combat = new Combat(killer, victim);

        TurnResult result = combat.turn();

        assertTrue(hasTrainerKoStep(result));
    }

    @Test
    public void combat_participationTracking_shouldMarkCurrentBugemon() {
        AutoTrainer t1 = TestUtilsTrainer.createDefaultAutoTrainer();
        AutoTrainer t2 = TestUtilsTrainer.createDefaultAutoTrainer();
        Combat combat = new Combat(t1, t2);

        combat.turn();

        assertFalse(t1.getParticipatingBugemons().isEmpty());
        assertFalse(t2.getParticipatingBugemons().isEmpty());
    }

    @Test
    public void turn_shouldReturnDifferentResultEachCall() {
        AutoTrainer t1 = TestUtilsTrainer.createDefaultAutoTrainer();
        AutoTrainer t2 = TestUtilsTrainer.createDefaultAutoTrainer();
        Combat combat = new Combat(t1, t2);

        TurnResult r1 = combat.turn();
        TurnResult r2 = combat.turn();

        assertNotSame(r1, r2);
    }
}
