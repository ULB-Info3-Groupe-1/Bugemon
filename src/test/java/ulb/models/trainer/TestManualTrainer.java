package ulb.models.trainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.List;

import org.junit.Test;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonType;
import ulb.utils.test.TestUtilsBugemonTeam;
import ulb.utils.test.TestUtilsBugemons;

public class TestManualTrainer {
    private static Bugemon findById(List<Bugemon> team, String id) {
        return team.stream().filter(b -> b.getId().equals(id)).findFirst().get();
    }

    @Test
    public void testRegisterSwitch() {
        List<Bugemon> team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team);
        Bugemon target = findById(team, "2");

        trainer.registerSwitch(target);

        TurnAction action = trainer.getAction();
        assertEquals(new TurnAction.SwitchAction(target), action);
    }

    @Test
    public void testRegisterSwitchDeadBugemon() {
        List<Bugemon> team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        TestUtilsBugemons.killBugemon(team, "2");
        ManualTrainer trainer = new ManualTrainer(team);

        assertThrows(IllegalArgumentException.class,
                     () -> trainer.registerSwitch(findById(team, "2")));
    }

    @Test
    public void testRegisterAttack() {
        List<Bugemon> team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team);
        var attack = trainer.getCurrentBugemonAttackList().get(0);

        trainer.registerAttack(attack);

        TurnAction action = trainer.getAction();
        assertEquals(new TurnAction.AttackAction(attack), action);
    }

    @Test
    public void testRegisterAttackNotInMoveSet() {
        List<Bugemon> team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team);

        // Build an attack with an ID that is guaranteed to not be in any Bugemon's attacks
        Attack foreignAttack = new Attack("UNKNOWN_ATTACK_ID", "Foreign", BugemonType.FLORA, "", 10,
                                          new java.util.ArrayList<>());

        assertThrows(IllegalArgumentException.class, () -> trainer.registerAttack(foreignAttack));
    }

    @Test
    public void testRegisterForfeit() {
        List<Bugemon> team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team);

        trainer.registerForfeit();

        TurnAction action = trainer.getAction();
        assertEquals(new TurnAction.ForfeitAction(), action);
    }

    @Test
    public void testSelectActionClearsRegistered() {
        List<Bugemon> team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team);

        trainer.registerForfeit();
        trainer.getAction(); // consume the pending action

        // no action registered -> should throw
        assertThrows(IllegalStateException.class, () -> trainer.getAction());
    }

    @Test
    public void testHasPendingAction() {
        List<Bugemon> team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team);

        assertThrows(IllegalStateException.class, () -> trainer.getAction());

        trainer.registerForfeit();
        assertEquals(true, trainer.hasPendingAction());

        trainer.getAction();
        assertEquals(false, trainer.hasPendingAction());
    }

    @Test
    public void testSwitchAfterKO() {
        List<Bugemon> team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team);
        Bugemon replacement = findById(team, "2");

        TestUtilsBugemons.killBugemon(team, "1"); // kill current bugemon
        trainer.switchAfterKO(replacement);

        assertEquals(replacement, trainer.getCurrentBugemon());
    }

    @Test
    public void testSwitchAfterKODeadTarget() {
        List<Bugemon> team = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        ManualTrainer trainer = new ManualTrainer(team);
        TestUtilsBugemons.killBugemon(team, "2");

        assertThrows(IllegalArgumentException.class,
                     () -> trainer.switchAfterKO(findById(team, "2")));
    }
}
