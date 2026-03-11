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
    public void testQueueSwitch() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team);
        Bugemon target = team.getBugemon("2").get();

        trainer.queueSwitch(target);

        TurnAction action = trainer.selectAction();
        assertEquals(new TurnAction.SwitchAction(target), action);
    }

    @Test
    public void testQueueSwitchDeadBugemon() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        TestUtilsBugemons.killBugemon(team, "2");
        ManualTrainer trainer = new ManualTrainer(team);

        assertThrows(IllegalArgumentException.class,
                     () -> trainer.queueSwitch(team.getBugemon("2").get()));
    }

    @Test
    public void testQueueAttack() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team);
        var attack = trainer.getCurrentBugemonAttackList().get(0);

        trainer.queueAttack(attack);

        TurnAction action = trainer.selectAction();
        assertEquals(new TurnAction.AttackAction(attack), action);
    }

    @Test
    public void testQueueAttackNotInMoveSet() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team);

        // Build an attack with an ID that is guaranteed to not be in any Bugemon's move-set
        Attack foreignAttack = new Attack("UNKNOWN_ATTACK_ID", "Foreign", BugemonType.FLORA, "", 10,
                                          new java.util.ArrayList<>());

        assertThrows(IllegalArgumentException.class, () -> trainer.queueAttack(foreignAttack));
    }

    @Test
    public void testQueueForfeit() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team);

        trainer.queueForfeit();

        TurnAction action = trainer.selectAction();
        assertEquals(new TurnAction.ForfeitAction(), action);
    }

    @Test
    public void testSelectActionClearsQueue() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team);

        trainer.queueForfeit();
        trainer.selectAction(); // consume the pending action

        // queue is now empty — should throw
        assertThrows(IllegalStateException.class, () -> trainer.selectAction());
    }

    @Test
    public void testHasPendingAction() {
        BugemonTeam team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team);

        assertThrows(IllegalStateException.class, () -> trainer.selectAction());

        trainer.queueForfeit();
        assertEquals(true, trainer.hasPendingAction());

        trainer.selectAction();
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
