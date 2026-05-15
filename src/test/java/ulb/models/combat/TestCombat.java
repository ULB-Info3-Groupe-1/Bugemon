package ulb.models.combat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import ulb.models.BugemonFixtures;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.combat.turn.TurnAction.AttackAction;
import ulb.models.combat.turn.TurnAction.ForfeitAction;
import ulb.models.combat.turn.TurnStep;
import ulb.models.combat.turn.TurnStep.AttackStep;
import ulb.models.combat.turn.TurnStep.KoStep;
import ulb.models.combat.utils.CombatResult;
import ulb.models.combat.utils.DamageCalculator;
import ulb.models.bugemon.ElementType;

public class TestCombat {

    private Attack floraAttack;
    private Attack aquaAttack;
    private CombatTeam playerTeam;
    private CombatTeam opponentTeam;
    private Combat combat;
    private Random seededRandom;

    @Before
    public void setUp() {
        this.seededRandom = new Random(42);
        this.floraAttack = BugemonFixtures.floraAttack();
        this.aquaAttack = BugemonFixtures.aquaAttack();

        this.playerTeam = BugemonFixtures.teamOf(BugemonFixtures.fastFlora());
        this.opponentTeam = BugemonFixtures.teamOf(BugemonFixtures.slowAqua());

        this.combat = new Combat(this.playerTeam, this.opponentTeam, new AutoStrategy(this.seededRandom),
                new AutoStrategy(this.seededRandom), new DamageCalculator());
    }

    @Test
    public void testResolveTurnProducesActions() {
        AttackAction playerAttack = new AttackAction(this.floraAttack);
        AttackAction opponentAttack = new AttackAction(this.floraAttack);

        List<TurnStep> turnSteps = new ArrayList<>();

        this.combat.resolveTurn(playerAttack, opponentAttack, turnSteps::addAll);

        assertFalse(turnSteps.isEmpty());
        // Should contain at least 2 AttackSteps (player and opponent)
        long attackCount = turnSteps.stream().filter(a -> a instanceof AttackStep).count();
        assertTrue("At least one attack occured", attackCount >= 1);
    }

    @Test
    public void testPlayerAttacksFirstWithHigherInitiative() {
        // Player init=70, Opponent init=30 → player first
        AttackAction playerAttack = new AttackAction(this.floraAttack);
        AttackAction opponentAttack = new AttackAction(this.aquaAttack);

        List<TurnStep> steps = new ArrayList<>();
        this.combat.resolveTurn(playerAttack, opponentAttack, steps::addAll);

        // First AttackStep should be from player (higher initiative)
        AttackStep first = steps.stream().filter(s -> s instanceof AttackStep).map(s -> (AttackStep) s).findFirst()
                .orElseThrow();

        assertEquals("Player with higher initiative is first to attack", playerAttack.attack(), first.attack());
    }

    @Test
    public void testForfeitEndsCombat() {
        List<TurnStep> steps = new ArrayList<>();
        this.combat.resolveTurn(new ForfeitAction(), new AttackAction(this.floraAttack), steps::addAll);

        assertTrue(this.combat.isFinished());
        assertEquals(CombatResult.DEFEAT, this.combat.getResult());
        assertTrue(steps.isEmpty());
    }

    @Test
    public void testKoProducesKoAction() {
        // Give opponent very low HP so it gets KO
        this.opponentTeam.getActive().takeDamage(95);

        AttackAction playerAtk = new AttackAction(this.floraAttack);
        AttackAction opponentAtk = new AttackAction(this.floraAttack);

        List<TurnStep> steps = new ArrayList<>();
        this.combat.resolveTurn(playerAtk, opponentAtk, steps::addAll);

        boolean hasKo = steps.stream().anyMatch(a -> a instanceof KoStep);
        assertTrue("A Ko should occured", hasKo);
    }

    @Test
    public void testVictoryWhenOpponentDefeated() {
        // Give opponent 1 HP so it will be KO by any attack
        this.opponentTeam.getActive().takeDamage(99);

        AttackAction playerAtk = new AttackAction(this.floraAttack);
        AttackAction opponentAtk = new AttackAction(this.floraAttack);

        List<TurnStep> steps = new ArrayList<>();
        this.combat.resolveTurn(playerAtk, opponentAtk, steps::addAll);

        assertTrue(this.combat.isFinished());
        assertEquals(CombatResult.VICTORY, this.combat.getResult());
    }

    @Test
    public void testDefeatWhenPlayerDefeated() {
        // Give player 1 HP so it will be KO by any attack
        // Create new combat to give more initiative to opponent
        Attack strongAtk = new Attack("strong", "Strong", "", 200, ElementType.AQUA, List.of());
        Bugemon fastOpp = new Bugemon("o2", "FastOpp", 100, 100, 40, 90, ElementType.AQUA,
                List.of(strongAtk, strongAtk, strongAtk), "", false);

        CombatTeam fastOppTeam = BugemonFixtures.teamOf(fastOpp);
        this.playerTeam.getActive().takeDamage(99);

        Combat c = new Combat(this.playerTeam, fastOppTeam, new AutoStrategy(this.seededRandom),
                new AutoStrategy(this.seededRandom), new DamageCalculator());

        c.resolveTurn(new AttackAction(this.floraAttack), new AttackAction(strongAtk), steps -> {
        });

        assertTrue(c.isFinished());
        assertEquals(CombatResult.DEFEAT, c.getResult());
    }

    @Test
    public void testKoSecondDoesNotAttack() {
        // If defender is KO by first attacker, second should not attack
        this.opponentTeam.getActive().takeDamage(99);

        AttackAction playerAtk = new AttackAction(this.floraAttack);
        AttackAction opponentAtk = new AttackAction(this.floraAttack);

        List<TurnStep> steps = new ArrayList<>();
        this.combat.resolveTurn(playerAtk, opponentAtk, steps::addAll);

        // Player attacks first (init 70 > 30), KO opponent
        // Opponent should NOT attack
        long attackCount = steps.stream().filter(a -> a instanceof AttackStep).count();
        assertEquals("Only the first attacker should be able to attack", 1, attackCount);
    }
}
