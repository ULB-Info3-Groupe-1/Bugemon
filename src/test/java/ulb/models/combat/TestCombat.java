package ulb.models.combat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import ulb.common.EffectDuration;
import ulb.common.StatType;
import ulb.models.BugemonFixtures;
import ulb.models.ItemFixtures;
import ulb.models.bugemon.Attack;
import ulb.models.combat.effect.StatusEffect;
import ulb.models.combat.turn.TurnAction.AttackAction;
import ulb.models.combat.turn.TurnAction.ForfeitAction;
import ulb.models.combat.turn.TurnAction.ItemAction;
import ulb.models.combat.turn.TurnStep;
import ulb.models.combat.turn.TurnStep.AttackStep;
import ulb.models.combat.turn.TurnStep.HealBugemonStep;
import ulb.models.combat.turn.TurnStep.KoStep;
import ulb.models.combat.utils.DamageCalculator;
import ulb.models.combat.utils.EffectProcessor;
import ulb.models.item.Inventory;
import ulb.models.item.Item;

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

        this.combat = new Combat(this.playerTeam, this.opponentTeam, new Inventory(), new Inventory(),
                new AutoStrategy(this.seededRandom), new AutoStrategy(this.seededRandom), new DamageCalculator(),
                new EffectProcessor());
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
        // Opponent with high power and initiative attacks first and KOs the player
        Attack strongAtk = BugemonFixtures.attack(200);
        CombatTeam fastOppTeam = BugemonFixtures
                .teamOf(BugemonFixtures.bugemon(100, 100, 40, 90, List.of(strongAtk, strongAtk, strongAtk)));
        this.playerTeam.getActive().takeDamage(99);

        Combat c = new Combat(this.playerTeam, fastOppTeam, new Inventory(), new Inventory(),
                new AutoStrategy(this.seededRandom), new AutoStrategy(this.seededRandom), new DamageCalculator(),
                new EffectProcessor());

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

    @Test
    public void testAttackWithHealForThrower() {
        Attack healAtk = BugemonFixtures.attackWithThrowerHeal();
        CombatTeam playerTeam = BugemonFixtures
                .teamOf(BugemonFixtures.bugemon(500, 100, 40, 90, List.of(healAtk, healAtk, healAtk)));
        playerTeam.getActive().takeDamage(10);
        int hpBefore = playerTeam.getActive().getCurrentHp();

        Attack zeroPowerAttack = BugemonFixtures.zeroPowerAttack();
        CombatTeam opponentTeam = BugemonFixtures.teamOf(
                BugemonFixtures.bugemon(500, 50, 40, 30, List.of(zeroPowerAttack, zeroPowerAttack, zeroPowerAttack)));

        Combat c = new Combat(playerTeam, opponentTeam, new Inventory(), new Inventory(),
                new AutoStrategy(this.seededRandom), new AutoStrategy(this.seededRandom), new DamageCalculator(),
                new EffectProcessor());

        List<TurnStep> turnSteps = new ArrayList<>();
        c.resolveTurn(new AttackAction(healAtk), new AttackAction(zeroPowerAttack), turnSteps::addAll);

        assertTrue(playerTeam.getActive().getCurrentHp() > hpBefore);
    }

    @Test
    public void testAttackWithDefenseDebuffReducesOpponentDefense() {
        Attack debuffAtk = BugemonFixtures.attackWithDefenseDebuffOnOpponent();
        CombatTeam playerTeam = BugemonFixtures
                .teamOf(BugemonFixtures.bugemon(500, 50, 40, 90, List.of(debuffAtk, debuffAtk, debuffAtk)));

        Attack zeroPowerAttack = BugemonFixtures.zeroPowerAttack();
        CombatTeam opponentTeam = BugemonFixtures.teamOf(
                BugemonFixtures.bugemon(500, 50, 40, 30, List.of(zeroPowerAttack, zeroPowerAttack, zeroPowerAttack)));

        int defenseBefore = opponentTeam.getActive().getEffectiveDefense();

        Combat c = new Combat(playerTeam, opponentTeam, new Inventory(), new Inventory(),
                new AutoStrategy(this.seededRandom), new AutoStrategy(this.seededRandom), new DamageCalculator(),
                new EffectProcessor());
        c.resolveTurn(new AttackAction(debuffAtk), new AttackAction(zeroPowerAttack), steps -> {
        });

        assertTrue(opponentTeam.getActive().getEffectiveDefense() < defenseBefore);
    }

    @Test
    public void testAttackWithInitiativeBuffIncreasesThrowerInitiative() {
        Attack buffAtk = BugemonFixtures.attackWithInitiativeBuffOnThrower();
        CombatTeam playerTeam = BugemonFixtures
                .teamOf(BugemonFixtures.bugemon(500, 50, 40, 90, List.of(buffAtk, buffAtk, buffAtk)));

        Attack zeroPowerAttack = BugemonFixtures.zeroPowerAttack();
        CombatTeam opponentTeam = BugemonFixtures.teamOf(
                BugemonFixtures.bugemon(500, 50, 40, 30, List.of(zeroPowerAttack, zeroPowerAttack, zeroPowerAttack)));

        int initiativeBefore = playerTeam.getActive().getEffectiveInitiative();

        Combat c = new Combat(playerTeam, opponentTeam, new Inventory(), new Inventory(),
                new AutoStrategy(this.seededRandom), new AutoStrategy(this.seededRandom), new DamageCalculator(),
                new EffectProcessor());
        c.resolveTurn(new AttackAction(buffAtk), new AttackAction(zeroPowerAttack), steps -> {
        });

        assertTrue(playerTeam.getActive().getEffectiveInitiative() > initiativeBefore);
    }

    @Test
    public void testAttackWithResetMalusRemovesNegativeEffects() {
        Attack resetAtk = BugemonFixtures.attackWithResetMalus();
        CombatTeam playerTeam = BugemonFixtures
                .teamOf(BugemonFixtures.bugemon(500, 50, 40, 90, List.of(resetAtk, resetAtk, resetAtk)));

        // Pré-applique un malus de défense sur le joueur
        playerTeam.getActive().addEffect(new StatusEffect(StatType.DEFENSE, -20, EffectDuration.PERMANENT));
        int defenseWithMalus = playerTeam.getActive().getEffectiveDefense();

        Attack zeroPowerAttack = BugemonFixtures.zeroPowerAttack();
        CombatTeam opponentTeam = BugemonFixtures.teamOf(
                BugemonFixtures.bugemon(500, 50, 40, 30, List.of(zeroPowerAttack, zeroPowerAttack, zeroPowerAttack)));

        Combat c = new Combat(playerTeam, opponentTeam, new Inventory(), new Inventory(),
                new AutoStrategy(this.seededRandom), new AutoStrategy(this.seededRandom), new DamageCalculator(),
                new EffectProcessor());
        c.resolveTurn(new AttackAction(resetAtk), new AttackAction(zeroPowerAttack), steps -> {
        });

        assertTrue(playerTeam.getActive().getEffectiveDefense() > defenseWithMalus);
    }

    @Test
    public void testItemWithHealForThrower() {
        int healAmount = 10;

        Item item = ItemFixtures.healingItem(healAmount);

        Inventory playerInventory = new Inventory();
        playerInventory.addItem(item, 1);

        Attack zeroPowerAttack = BugemonFixtures.zeroPowerAttack();

        CombatTeam playerTeam = BugemonFixtures.teamOf(
                BugemonFixtures.bugemon(100, 50, 40, 90, List.of(zeroPowerAttack, zeroPowerAttack, zeroPowerAttack)));
        CombatTeam opponentTeam = BugemonFixtures.teamOf(
                BugemonFixtures.bugemon(100, 50, 40, 30, List.of(zeroPowerAttack, zeroPowerAttack, zeroPowerAttack)));

        playerTeam.getActive().takeDamage(20);
        int hpBefore = playerTeam.getActive().getCurrentHp();

        Combat c = new Combat(playerTeam, opponentTeam, playerInventory, new Inventory(),
                new AutoStrategy(this.seededRandom), new AutoStrategy(this.seededRandom), new DamageCalculator(),
                new EffectProcessor());

        List<TurnStep> turnSteps = new ArrayList<>();
        c.resolveTurn(new ItemAction(item), new AttackAction(zeroPowerAttack), turnSteps::addAll);

        assertEquals(hpBefore + healAmount, playerTeam.getActive().getCurrentHp());
        assertFalse(playerInventory.hasItem(item));
        assertTrue(turnSteps.stream().anyMatch(s -> s instanceof HealBugemonStep));
    }

    @Test
    public void testThrowIfBugemonDoesNotHaveAttack() {
        Attack availableAttack = BugemonFixtures.attack(1);
        Attack unavailableAttack = BugemonFixtures.attack(2);

        CombatTeam playerTeam = BugemonFixtures.teamOf(BugemonFixtures.bugemon(100, 50, 40, 90,
                List.of(availableAttack, availableAttack, availableAttack)));
        CombatTeam opponentTeam = BugemonFixtures.teamOf(BugemonFixtures.slowAqua());

        Combat c = new Combat(playerTeam, opponentTeam, new Inventory(), new Inventory(),
                new AutoStrategy(this.seededRandom), new AutoStrategy(this.seededRandom), new DamageCalculator(),
                new EffectProcessor());

        assertThrows(IllegalArgumentException.class, () -> {
            c.resolveTurn(new AttackAction(unavailableAttack), new AttackAction(this.aquaAttack), steps -> {
            });
        });

    }
}
