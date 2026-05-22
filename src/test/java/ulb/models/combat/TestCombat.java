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
import ulb.models.combat.damage.DamageCalculator;
import ulb.models.combat.effect.StatusEffect;
import ulb.models.combat.strategy.AutoStrategy;
import ulb.models.combat.turn.TurnAction.AttackAction;
import ulb.models.combat.turn.TurnAction.ForfeitAction;
import ulb.models.combat.turn.TurnAction.ItemAction;
import ulb.models.combat.turn.TurnStep;
import ulb.models.combat.turn.TurnStep.AttackStep;
import ulb.models.combat.turn.TurnStep.HealBugemonStep;
import ulb.models.combat.turn.TurnStep.KoStep;
import ulb.models.combat.utils.EffectProcessor;
import ulb.models.item.Inventory;
import ulb.models.item.Item;
import ulb.models.skills.SkillContext;
import ulb.models.skills.SkillEffect;
import ulb.models.skills.SkillNode;
import ulb.models.skills.SkillTree;
import ulb.models.skills.SkillTreeState;

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
        SkillContext skillContext = this.skillContextCreationHelper();
        this.combat = new CombatBuilder().playerTeam(this.playerTeam).opponentTeam(this.opponentTeam).floor(2)
                .bossMode(false).playerInventory(new Inventory()).opponentInventory(new Inventory())
                .playerStrategy(new AutoStrategy(this.seededRandom))
                .opponentStrategy(new AutoStrategy(this.seededRandom)).damageCalculator(new DamageCalculator())
                .effectProcessor(new EffectProcessor()).playerSkillContext(skillContext).build();
    }

    public SkillContext skillContextCreationHelper() {
        // Create 3 hardcoded skills from skill_tree.json
        SkillNode hp1 = new SkillNode("hp_1", "+10 HP", "Tous vos Bugémons commencent avec +10 HP maximum", -1, 1, 3, 1,
                new SkillEffect.StatBonusEffect(StatType.HP, 10), List.of());

        SkillNode attaque1 = new SkillNode("attaque_1", "+3 Attaque", "Tous vos Bugémons commencent avec +3 Attaque", 1,
                1, 3, 1, new SkillEffect.StatBonusEffect(StatType.ATTACK, 3), List.of("hp_1"));

        SkillNode critiqueChance = new SkillNode("critique_chance", "Œil critique", "+5% de chance de coup critique", 2,
                2, 1, 3, new SkillEffect.CritBonusEffect(5), List.of("attaque_1"));

        // Build skill tree
        SkillTree skillTree = new SkillTree(List.of(hp1, attaque1, critiqueChance));
        SkillTreeState skillTreeState = new SkillTreeState();

        // Earn skill points and add skills to the tree state
        skillTreeState.addPoint();
        skillTreeState.addPoint();
        skillTreeState.addPoint();
        skillTreeState.addPoint();
        skillTreeState.addPoint();
        skillTreeState.addPoint();

        try {
            skillTreeState.addPoint("hp_1", skillTree);
            skillTreeState.addPoint("attaque_1", skillTree);
            skillTreeState.addPoint("critique_chance", skillTree);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create SkillContext with the state and tree
        return new SkillContext(skillTreeState, skillTree);
    }

    @Test
    public void testResolveTurnProducesActions() {
        AttackAction playerAttack = new AttackAction(this.floraAttack);
        AttackAction opponentAttack = new AttackAction(this.aquaAttack);

        List<TurnStep> turnSteps = new ArrayList<>();

        this.combat.resolveTurn(playerAttack, opponentAttack, turnSteps::addAll);

        assertFalse(turnSteps.isEmpty());
        // Should contain at least 2 AttackSteps (player and opponent)
        long attackCount = turnSteps.stream().filter(AttackStep.class::isInstance).count();
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
        AttackStep first = steps.stream().filter(AttackStep.class::isInstance).map(s -> (AttackStep) s).findFirst()
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

        boolean hasKo = steps.stream().anyMatch(KoStep.class::isInstance);
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
        Attack strongAtk = BugemonFixtures.attack("atk-strong", 200);
        CombatTeam fastOppTeam = BugemonFixtures
                .teamOf(BugemonFixtures.bugemon(100, 100, 40, 90, List.of(strongAtk, strongAtk, strongAtk)));
        this.playerTeam.getActive().takeDamage(99);

        Combat c = new CombatBuilder().playerTeam(this.playerTeam).opponentTeam(fastOppTeam).floor(2).bossMode(false)
                .playerInventory(new Inventory()).opponentInventory(new Inventory())
                .playerStrategy(new AutoStrategy(this.seededRandom))
                .opponentStrategy(new AutoStrategy(this.seededRandom)).damageCalculator(new DamageCalculator())
                .effectProcessor(new EffectProcessor()).playerSkillContext(SkillContext.NONE).build();

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
        long attackCount = steps.stream().filter(AttackStep.class::isInstance).count();
        assertEquals("Only the first attacker should be able to attack", 1, attackCount);
    }

    @Test
    public void testAttackWithHealForThrower() {
        Attack healAtk = BugemonFixtures.attackWithThrowerHeal();
        CombatTeam playerCombatTeam = BugemonFixtures
                .teamOf(BugemonFixtures.bugemon(500, 100, 40, 90, List.of(healAtk, healAtk, healAtk)));
        playerCombatTeam.getActive().takeDamage(10);
        int hpBefore = playerCombatTeam.getActive().getCurrentHp();

        Attack zeroPowerAttack = BugemonFixtures.zeroPowerAttack();
        CombatTeam healTestOpponentTeam = BugemonFixtures.teamOf(
                BugemonFixtures.bugemon(500, 50, 40, 30, List.of(zeroPowerAttack, zeroPowerAttack, zeroPowerAttack)));

        Combat c = new CombatBuilder().playerTeam(playerCombatTeam).opponentTeam(healTestOpponentTeam).floor(2)
                .bossMode(false).playerInventory(new Inventory()).opponentInventory(new Inventory())
                .playerStrategy(new AutoStrategy(this.seededRandom))
                .opponentStrategy(new AutoStrategy(this.seededRandom)).damageCalculator(new DamageCalculator())
                .effectProcessor(new EffectProcessor()).playerSkillContext(SkillContext.NONE).build();

        List<TurnStep> turnSteps = new ArrayList<>();
        c.resolveTurn(new AttackAction(healAtk), new AttackAction(zeroPowerAttack), turnSteps::addAll);

        assertTrue(playerCombatTeam.getActive().getCurrentHp() > hpBefore);
    }

    @Test
    public void testAttackWithDefenseDebuffReducesOpponentDefense() {
        Attack debuffAtk = BugemonFixtures.attackWithDefenseDebuffOnOpponent();
        CombatTeam debuffPlayerTeam = BugemonFixtures
                .teamOf(BugemonFixtures.bugemon(500, 50, 40, 90, List.of(debuffAtk, debuffAtk, debuffAtk)));

        Attack zeroPowerAttack = BugemonFixtures.zeroPowerAttack();
        CombatTeam debuffOpponentTeam = BugemonFixtures.teamOf(
                BugemonFixtures.bugemon(500, 50, 40, 30, List.of(zeroPowerAttack, zeroPowerAttack, zeroPowerAttack)));

        int defenseBefore = debuffOpponentTeam.getActive().getEffectiveDefense();

        Combat c = new CombatBuilder().playerTeam(debuffPlayerTeam).opponentTeam(debuffOpponentTeam).floor(2)
                .bossMode(false).playerInventory(new Inventory()).opponentInventory(new Inventory())
                .playerStrategy(new AutoStrategy(this.seededRandom))
                .opponentStrategy(new AutoStrategy(this.seededRandom)).damageCalculator(new DamageCalculator())
                .effectProcessor(new EffectProcessor()).playerSkillContext(SkillContext.NONE).build();

        c.resolveTurn(new AttackAction(debuffAtk), new AttackAction(zeroPowerAttack), steps -> {
        });

        assertTrue(debuffOpponentTeam.getActive().getEffectiveDefense() < defenseBefore);
    }

    @Test
    public void testAttackWithInitiativeBuffIncreasesThrowerInitiative() {
        Attack buffAtk = BugemonFixtures.attackWithInitiativeBuffOnThrower();
        CombatTeam buffPlayerTeam = BugemonFixtures
                .teamOf(BugemonFixtures.bugemon(500, 50, 40, 90, List.of(buffAtk, buffAtk, buffAtk)));

        Attack zeroPowerAttack = BugemonFixtures.zeroPowerAttack();
        CombatTeam buffOpponentTeam = BugemonFixtures.teamOf(
                BugemonFixtures.bugemon(500, 50, 40, 30, List.of(zeroPowerAttack, zeroPowerAttack, zeroPowerAttack)));

        int initiativeBefore = buffPlayerTeam.getActive().getEffectiveInitiative();

        Combat c = new CombatBuilder().playerTeam(buffPlayerTeam).opponentTeam(buffOpponentTeam).floor(2)
                .bossMode(false).playerInventory(new Inventory()).opponentInventory(new Inventory())
                .playerStrategy(new AutoStrategy(this.seededRandom))
                .opponentStrategy(new AutoStrategy(this.seededRandom)).damageCalculator(new DamageCalculator())
                .effectProcessor(new EffectProcessor()).playerSkillContext(SkillContext.NONE).build();

        c.resolveTurn(new AttackAction(buffAtk), new AttackAction(zeroPowerAttack), steps -> {
        });

        assertTrue(buffPlayerTeam.getActive().getEffectiveInitiative() > initiativeBefore);
    }

    @Test
    public void testAttackWithResetMalusRemovesNegativeEffects() {
        Attack resetAtk = BugemonFixtures.attackWithResetMalus();
        CombatTeam resetPlayerTeam = BugemonFixtures
                .teamOf(BugemonFixtures.bugemon(500, 50, 40, 90, List.of(resetAtk, resetAtk, resetAtk)));

        // Pre apply a negative effect to the player's active Bugemon defense
        resetPlayerTeam.getActive().addEffect(new StatusEffect(StatType.DEFENSE, -20, EffectDuration.PERMANENT));
        int defenseWithMalus = resetPlayerTeam.getActive().getEffectiveDefense();

        Attack zeroPowerAttack = BugemonFixtures.zeroPowerAttack();
        CombatTeam resetOpponentTeam = BugemonFixtures.teamOf(
                BugemonFixtures.bugemon(500, 50, 40, 30, List.of(zeroPowerAttack, zeroPowerAttack, zeroPowerAttack)));

        Combat c = new CombatBuilder().playerTeam(resetPlayerTeam).opponentTeam(resetOpponentTeam).floor(2)
                .bossMode(false).playerInventory(new Inventory()).opponentInventory(new Inventory())
                .playerStrategy(new AutoStrategy(this.seededRandom))
                .opponentStrategy(new AutoStrategy(this.seededRandom)).damageCalculator(new DamageCalculator())
                .effectProcessor(new EffectProcessor()).playerSkillContext(SkillContext.NONE).build();

        c.resolveTurn(new AttackAction(resetAtk), new AttackAction(zeroPowerAttack), steps -> {
        });

        assertTrue(resetPlayerTeam.getActive().getEffectiveDefense() > defenseWithMalus);
    }

    @Test
    public void testItemWithHealForThrower() {
        int healAmount = 10;

        Item item = ItemFixtures.healingItem(healAmount);

        Inventory playerInventory = new Inventory();
        playerInventory.addItem(item, 1);

        Attack zeroPowerAttack = BugemonFixtures.zeroPowerAttack();

        CombatTeam itemPlayerTeam = BugemonFixtures.teamOf(
                BugemonFixtures.bugemon(100, 50, 40, 90, List.of(zeroPowerAttack, zeroPowerAttack, zeroPowerAttack)));
        CombatTeam itemOpponentTeam = BugemonFixtures.teamOf(
                BugemonFixtures.bugemon(100, 50, 40, 30, List.of(zeroPowerAttack, zeroPowerAttack, zeroPowerAttack)));

        itemPlayerTeam.getActive().takeDamage(20);
        int hpBefore = itemPlayerTeam.getActive().getCurrentHp();

        Combat c = new CombatBuilder().playerTeam(itemPlayerTeam).opponentTeam(itemOpponentTeam).floor(2)
                .bossMode(false).playerInventory(playerInventory).opponentInventory(new Inventory())
                .playerStrategy(new AutoStrategy(this.seededRandom))
                .opponentStrategy(new AutoStrategy(this.seededRandom)).damageCalculator(new DamageCalculator())
                .effectProcessor(new EffectProcessor()).playerSkillContext(SkillContext.NONE).build();

        List<TurnStep> turnSteps = new ArrayList<>();
        c.resolveTurn(new ItemAction(item), new AttackAction(zeroPowerAttack), turnSteps::addAll);

        assertEquals(hpBefore + healAmount, itemPlayerTeam.getActive().getCurrentHp());
        assertFalse(playerInventory.hasItem(item));
        assertTrue(turnSteps.stream().anyMatch(HealBugemonStep.class::isInstance));
    }

    @Test
    public void testThrowIfBugemonDoesNotHaveAttack() {
        Attack availableAttack = BugemonFixtures.attack("atk-available", 1);
        Attack unavailableAttack = BugemonFixtures.attack("atk-unavailable", 2);

        CombatTeam throwPlayerTeam = BugemonFixtures.teamOf(
                BugemonFixtures.bugemon(100, 50, 40, 90, List.of(availableAttack, availableAttack, availableAttack)));
        CombatTeam throwOpponentTeam = BugemonFixtures.teamOf(BugemonFixtures.slowAqua());

        Combat c = new CombatBuilder().playerTeam(throwPlayerTeam).opponentTeam(throwOpponentTeam).floor(2)
                .bossMode(false).playerInventory(new Inventory()).opponentInventory(new Inventory())
                .playerStrategy(new AutoStrategy(this.seededRandom))
                .opponentStrategy(new AutoStrategy(this.seededRandom)).damageCalculator(new DamageCalculator())
                .effectProcessor(new EffectProcessor()).playerSkillContext(SkillContext.NONE).build();

        AttackAction action1 = new AttackAction(unavailableAttack);
        AttackAction action2 = new AttackAction(this.aquaAttack);

        assertThrows(IllegalArgumentException.class, () -> {
            c.resolveTurn(action1, action2, steps -> {
            });
        });
    }

    @Test
    public void testSkillsStatBonusInCombat() {
        assertEquals(110, this.combat.getPlayerTeam().getActive().getCurrentHp()); // Base 100 + 10 from skill
        assertEquals(53, this.combat.getPlayerTeam().getActive().getEffectiveAttack()); // Base 50 + 3 from skill
    }
}
