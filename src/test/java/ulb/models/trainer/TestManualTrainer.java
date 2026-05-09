package ulb.models.trainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.models.bugemon.effect.EffectHeal;
import ulb.models.bugemon.effect.EffectTarget;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.utils.test.TestUtilsBugemonTeam;
import ulb.utils.test.TestUtilsBugemons;

public class TestManualTrainer {
    private Inventory inventory;
    private Item baieRevigorante;

    @Before
    public void setUp() {
        this.inventory = mock(Inventory.class);

        this.baieRevigorante = new Item("baie_revigorante", "Baie Revigorante", "Restaure 20 PV au Bugémon actif.",
                Item.ItemType.HEALING, new EffectHeal(EffectTarget.THROWER, 20));

        when(this.inventory.hasItem(this.baieRevigorante)).thenReturn(true);
    }

    @Test
    public void testRegisterSwitch() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team, this.inventory, List.of());
        Bugemon target = team.get("2").get();

        trainer.registerSwitch(target);

        TurnAction action = trainer.getAction();
        assertEquals(new TurnAction.SwitchAction(target), action);
    }

    @Test
    public void testRegisterSwitchDeadBugemon() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        TestUtilsBugemons.killBugemon(team, "2");
        ManualTrainer trainer = new ManualTrainer(team, this.inventory, List.of());

        Bugemon target = team.get("2").get();
        assertThrows(IllegalArgumentException.class, () -> trainer.registerSwitch(target));
    }

    @Test
    public void testRegisterAttack() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team, this.inventory, List.of());
        var attack = trainer.getCurrentBugemonAttackList().get(0);

        trainer.registerAttack(attack);

        TurnAction action = trainer.getAction();
        assertEquals(new TurnAction.AttackAction(attack), action);
    }

    @Test
    public void testRegisterAttackNotInMoveSet() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team, this.inventory, List.of());

        // Build an attack with an ID that is guaranteed to not be in any Bugemon's
        // attacks
        Attack foreignAttack = new Attack("UNKNOWN_ATTACK_ID", "Foreign", BugemonType.FLORA, "", 10,
                new java.util.ArrayList<>());

        assertThrows(IllegalArgumentException.class, () -> trainer.registerAttack(foreignAttack));
    }

    @Test
    public void testRegisterUseItem() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team, this.inventory, List.of());

        trainer.registerUseItem(this.baieRevigorante);

        TurnAction action = trainer.getAction();
        assertEquals(new TurnAction.UseItemAction(this.baieRevigorante), action);
    }

    @Test
    public void testRegisterForfeit() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team, this.inventory, List.of());

        trainer.registerForfeit();

        TurnAction action = trainer.getAction();
        assertEquals(new TurnAction.ForfeitAction(), action);
    }

    @Test
    public void testSelectActionClearsRegistered() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team, this.inventory, List.of());

        trainer.registerForfeit();
        trainer.getAction(); // consume the pending action

        // no action registered -> should throw
        assertThrows(IllegalStateException.class, trainer::getAction);
    }

    @Test
    public void testHasPendingAction() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team, this.inventory, List.of());

        assertThrows(IllegalStateException.class, trainer::getAction);

        trainer.registerForfeit();
        assertEquals(true, trainer.hasPendingAction());

        trainer.getAction();
        assertEquals(false, trainer.hasPendingAction());
    }

    @Test
    public void testSwitchAfterKO() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team, this.inventory, List.of());
        Bugemon replacement = team.get("2").get();

        TestUtilsBugemons.killBugemon(team, "1"); // kill current bugemon
        trainer.switchAfterKO(replacement);

        assertEquals(replacement, trainer.getCurrentBugemon());
    }

    @Test
    public void testSwitchAfterKODeadTarget() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team, this.inventory, List.of());
        TestUtilsBugemons.killBugemon(team, "2");

        Bugemon replacement = team.get("2").get();
        assertThrows(IllegalArgumentException.class, () -> trainer.switchAfterKO(replacement));
    }
}
