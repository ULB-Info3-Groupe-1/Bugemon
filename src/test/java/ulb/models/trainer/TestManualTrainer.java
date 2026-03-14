package ulb.models.trainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.utils.test.TestUtilsBugemonTeam;
import ulb.utils.test.TestUtilsBugemons;

public class TestManualTrainer {
    @Test
    public void testRegisterSwitch() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team);
        Bugemon target = team.getBugemon("2").get();

        trainer.registerSwitch(target);

        TurnAction action = trainer.getAction();
        assertEquals(new TurnAction.SwitchAction(target), action);
    }

    @Test
    public void testRegisterSwitchDeadBugemon() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        TestUtilsBugemons.killBugemon(team, "2");
        ManualTrainer trainer = new ManualTrainer(team);

        assertThrows(IllegalArgumentException.class,
                     () -> trainer.registerSwitch(team.getBugemon("2").get()));
    }

    @Test
    public void testRegisterAttack() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team);
        var attack = trainer.getCurrentBugemonAttackList().get(0);

        trainer.registerAttack(attack);

        TurnAction action = trainer.getAction();
        assertEquals(new TurnAction.AttackAction(attack), action);
    }

    @Test
    public void testRegisterAttackNotInMoveSet() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team);

        // Build an attack with an ID that is guaranteed to not be in any Bugemon's attacks
        Attack foreignAttack = new Attack("UNKNOWN_ATTACK_ID", "Foreign", BugemonType.FLORA, "", 10,
                                          new java.util.ArrayList<>());

        assertThrows(IllegalArgumentException.class, () -> trainer.registerAttack(foreignAttack));
    }

    @Test
    public void testRegisterForfeit() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team);

        trainer.registerForfeit();

        TurnAction action = trainer.getAction();
        assertEquals(new TurnAction.ForfeitAction(), action);
    }

    @Test
    public void testSelectActionClearsRegistered() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team);

        trainer.registerForfeit();
        trainer.getAction(); // consume the pending action

        // no action registered -> should throw
        assertThrows(IllegalStateException.class, () -> trainer.getAction());
    }

    @Test
    public void testHasPendingAction() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team);

        assertThrows(IllegalStateException.class, () -> trainer.getAction());

        trainer.registerForfeit();
        assertEquals(true, trainer.hasPendingAction());

        trainer.getAction();
        assertEquals(false, trainer.hasPendingAction());
    }

    @Test
    public void testSwitchAfterKO() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team);
        Bugemon replacement = team.getBugemon("2").get();

        TestUtilsBugemons.killBugemon(team, "1"); // kill current bugemon
        trainer.switchAfterKO(replacement);

        assertEquals(replacement, trainer.getCurrentBugemon());
    }

    @Test
    public void testSwitchAfterKODeadTarget() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team);
        TestUtilsBugemons.killBugemon(team, "2");

        assertThrows(IllegalArgumentException.class,
                     () -> trainer.switchAfterKO(team.getBugemon("2").get()));
    }
}
