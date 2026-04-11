package ulb.models.combat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonBuilder;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.models.bugemon.effect.EffectHeal;
import ulb.models.bugemon.effect.EffectTarget;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.ManualTrainer;
import ulb.utils.test.TestUtilsBugemons;
import ulb.utils.test.TestUtilsTrainer;

public class TestManualCombat {

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

    private Bugemon bugemonWithHp(String name, int hp, int initiative) {
        return new BugemonBuilder().name(name).hp(hp).attack(0).defense(0).initiative(initiative)
                .addAttack(this.defaultAttack).build();
    }

    private AutoTrainer autoOf(Bugemon... bugemons) {
        BugemonTeam team = new BugemonTeam();
        for (Bugemon b : bugemons) {
            team.add(b);
        }
        return new AutoTrainer(team);
    }

    private ManualTrainer manualOf(Bugemon... bugemons) {
        BugemonTeam team = new BugemonTeam();
        for (Bugemon b : bugemons) {
            team.add(b);
        }
        return new ManualTrainer(team, new Inventory());
    }

    // ── Initiative ordering ───────────────────────────────────────────────────

    @Test
    public void turn_firstAttackStep_shouldBelongToHigherInitiativeTrainer() {
        // Initiative ordering only applies via resolveDualAttack, triggered when playerTrainer is ManualTrainer
        Bugemon slowBugemon = this.bugemonWithHp("slow", 100, 1);
        Bugemon fastBugemon = this.bugemonWithHp("fast", 100, 1000);
        ManualTrainer slowPlayer = this.manualOf(slowBugemon);
        AutoTrainer fastOpponent = this.autoOf(fastBugemon);
        Combat combat = new Combat(slowPlayer, fastOpponent);

        slowPlayer.registerAttack(this.defaultAttack);
        List<TurnStep> steps = stepsAsList(combat.turn());

        TurnStep.AttackStep first = (TurnStep.AttackStep) steps.stream().filter(s -> s instanceof TurnStep.AttackStep)
                .findFirst().orElseThrow();
        assertEquals(fastOpponent, first.attacker());
    }

    // ── Switch ────────────────────────────────────────────────────────────────

    @Test
    public void turn_shouldContainSwitchStep_whenManualTrainerSwitches() {
        Bugemon b1 = TestUtilsBugemons.createDefaultBugemon("1");
        Bugemon b2 = TestUtilsBugemons.createDefaultBugemon("2");
        ManualTrainer player = this.manualOf(b1, b2);
        AutoTrainer opponent = TestUtilsTrainer.createDefaultAutoTrainer();
        Combat combat = new Combat(player, opponent);

        player.registerSwitch(b2);
        List<TurnStep> steps = stepsAsList(combat.turn());

        assertTrue(steps.stream().anyMatch(s -> s instanceof TurnStep.SwitchStep));
        TurnStep.SwitchStep switchStep = (TurnStep.SwitchStep) steps.stream()
                .filter(s -> s instanceof TurnStep.SwitchStep).findFirst().orElseThrow();
        assertEquals(player, switchStep.trainer());
        assertEquals(b2, switchStep.getBugemon());
    }

    @Test
    public void turn_shouldContainOnlyOneAttackStep_whenManualTrainerSwitches() {
        Bugemon b1 = TestUtilsBugemons.createDefaultBugemon("1");
        Bugemon b2 = TestUtilsBugemons.createDefaultBugemon("2");
        ManualTrainer player = this.manualOf(b1, b2);
        AutoTrainer opponent = TestUtilsTrainer.createDefaultAutoTrainer();
        Combat combat = new Combat(player, opponent);

        player.registerSwitch(b2);
        List<TurnStep> steps = stepsAsList(combat.turn());

        long attackCount = steps.stream().filter(s -> s instanceof TurnStep.AttackStep).count();
        assertEquals(1, attackCount);
    }

    // ── Item ──────────────────────────────────────────────────────────────────

    @Test
    public void turn_shouldContainItemStep_whenManualTrainerUsesItem() {
        Item baie = new Item("baie", "Baie", "Restaure 20 PV.", Item.ItemType.HEALING,
                new EffectHeal(EffectTarget.THROWER, 20));
        Inventory inventory = new Inventory();
        inventory.addItem(baie, 1);
        Bugemon b1 = TestUtilsBugemons.createDefaultBugemon("1");
        BugemonTeam team = new BugemonTeam();
        team.add(b1);
        ManualTrainer player = new ManualTrainer(team, inventory);
        AutoTrainer opponent = TestUtilsTrainer.createDefaultAutoTrainer();
        Combat combat = new Combat(player, opponent);

        player.registerUseItem(baie);
        List<TurnStep> steps = stepsAsList(combat.turn());

        assertTrue(steps.stream().anyMatch(s -> s instanceof TurnStep.ItemStep));
        TurnStep.ItemStep itemStep = (TurnStep.ItemStep) steps.stream().filter(s -> s instanceof TurnStep.ItemStep)
                .findFirst().orElseThrow();
        assertEquals(player, itemStep.trainer());
        assertEquals("Baie", itemStep.getItemName());
    }

    // ── Forfeit ───────────────────────────────────────────────────────────────

    @Test
    public void turn_shouldContainForfeitStep_whenManualTrainerForfeits() {
        ManualTrainer player = this.manualOf(TestUtilsBugemons.createDefaultBugemon("1"));
        AutoTrainer opponent = TestUtilsTrainer.createDefaultAutoTrainer();
        Combat combat = new Combat(player, opponent);

        player.registerForfeit();
        List<TurnStep> steps = stepsAsList(combat.turn());

        assertTrue(steps.stream().anyMatch(s -> s instanceof TurnStep.ForfeitStep));
        TurnStep.ForfeitStep forfeit = (TurnStep.ForfeitStep) steps.stream()
                .filter(s -> s instanceof TurnStep.ForfeitStep).findFirst().orElseThrow();
        assertEquals(player, forfeit.trainer());
    }

    @Test
    public void turn_forfeit_shouldAlsoProduceTrainerKoStep() {
        ManualTrainer player = this.manualOf(TestUtilsBugemons.createDefaultBugemon("1"));
        AutoTrainer opponent = TestUtilsTrainer.createDefaultAutoTrainer();
        Combat combat = new Combat(player, opponent);

        player.registerForfeit();
        List<TurnStep> steps = stepsAsList(combat.turn());

        assertTrue(steps.stream().anyMatch(s -> s instanceof TurnStep.TrainerKoStep));
        TurnStep.TrainerKoStep koStep = (TurnStep.TrainerKoStep) steps.stream()
                .filter(s -> s instanceof TurnStep.TrainerKoStep).findFirst().orElseThrow();
        assertEquals(player, koStep.trainerKo());
    }

    @Test
    public void turn_forfeit_shouldNotRunOtherActions() {
        ManualTrainer player = this.manualOf(TestUtilsBugemons.createDefaultBugemon("1"));
        AutoTrainer opponent = TestUtilsTrainer.createDefaultAutoTrainer();
        Combat combat = new Combat(player, opponent);

        player.registerForfeit();
        List<TurnStep> steps = stepsAsList(combat.turn());

        assertFalse(steps.stream().anyMatch(s -> s instanceof TurnStep.AttackStep));
    }
}
