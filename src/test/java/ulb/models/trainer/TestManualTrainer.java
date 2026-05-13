package ulb.models.trainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.models.bugemon.effect.EffectHeal;
import ulb.models.bugemon.effect.EffectTarget;
import ulb.models.bugemon_team.Team;
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
        Team team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team, this.inventory);
        Bugemon target = team.get("2").get();

        trainer.registerSwitch(target);

        TurnAction action = trainer.getAction();
        assertEquals(new TurnAction.SwitchAction(target), action);
    }

    @Test
    public void testRegisterSwitchDeadBugemon() {
        Team team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        TestUtilsBugemons.killBugemon(team, "2");
        ManualTrainer trainer = new ManualTrainer(team, this.inventory);

        Bugemon target = team.get("2").get();
        assertThrows(IllegalArgumentException.class, () -> trainer.registerSwitch(target));
    }

    @Test
    public void testRegisterAttack() {
        Team team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team, this.inventory);
        var attack = trainer.getCurrentBugemonAttackList().get(0);

        trainer.registerAttack(attack);

        TurnAction action = trainer.getAction();
        assertEquals(new TurnAction.AttackAction(attack), action);
    }

    @Test
    public void testRegisterAttackNotInMoveSet() {
        Team team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team, this.inventory);

        // Build an attack with an ID that is guaranteed to not be in any Bugemon's
        // attacks
        Attack foreignAttack = new Attack("UNKNOWN_ATTACK_ID", "Foreign", BugemonType.FLORA, "", 10,
                new java.util.ArrayList<>());

        assertThrows(IllegalArgumentException.class, () -> trainer.registerAttack(foreignAttack));
    }

    @Test
    public void testRegisterUseItem() {
        Team team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team, this.inventory);

        trainer.registerUseItem(this.baieRevigorante);

        TurnAction action = trainer.getAction();
        assertEquals(new TurnAction.UseItemAction(this.baieRevigorante), action);
    }

    @Test
    public void testRegisterForfeit() {
        Team team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team, this.inventory);

        trainer.registerForfeit();

        TurnAction action = trainer.getAction();
        assertEquals(new TurnAction.ForfeitAction(), action);
    }

    @Test
    public void testSelectActionClearsRegistered() {
        Team team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team, this.inventory);

        trainer.registerForfeit();
        trainer.getAction(); // consume the pending action

        // no action registered -> should throw
        assertThrows(IllegalStateException.class, trainer::getAction);
    }

    @Test
    public void testHasPendingAction() {
        Team team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team, this.inventory);

        assertThrows(IllegalStateException.class, trainer::getAction);

        trainer.registerForfeit();
        assertEquals(true, trainer.hasPendingAction());

        trainer.getAction();
        assertEquals(false, trainer.hasPendingAction());
    }

    @Test
    public void testSwitchAfterKO() {
        Team team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team, this.inventory);
        Bugemon replacement = team.get("2").get();

        TestUtilsBugemons.killBugemon(team, "1"); // kill current bugemon
        trainer.switchAfterKO(replacement);

        assertEquals(replacement, trainer.getCurrentBugemon());
    }

    @Test
    public void testSwitchAfterKODeadTarget() {
        Team team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team, this.inventory);
        TestUtilsBugemons.killBugemon(team, "2");

        Bugemon replacement = team.get("2").get();
        assertThrows(IllegalArgumentException.class, () -> trainer.switchAfterKO(replacement));
    }
}
