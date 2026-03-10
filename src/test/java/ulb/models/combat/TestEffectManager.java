/**
 * File name : TestActiveEffect.java
 * Description : Test class for the ActiveEffect class.
 *
 * @author Rocca Manuel
 * @date 3 mar. 2026
 * @version 1.0
 */

package ulb.models.combat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.security.KeyException;
import java.util.List;

import org.junit.Test;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Effect;
import ulb.models.bugemon.EffectStat;
import ulb.models.bugemon.EffectTarget;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.trainer.Trainer;
import ulb.utils.test.TestUtilsBugemons;

public class TestEffectManager {
    @Test
    public void testApplyInvalidEffect() {
        // init objects to test
        EffectManager effectManager = new EffectManager();

        BugemonTeam team1 = new BugemonTeam();
        BugemonTeam team2 = new BugemonTeam();

        Bugemon bugemon1 = TestUtilsBugemons.createDefaultBugemon("1");
        Bugemon bugemon2 = TestUtilsBugemons.createDefaultBugemon("2");

        team1.addBugemon(bugemon1);
        team2.addBugemon(bugemon2);

        Trainer attacker = new Trainer(team1);
        Trainer defender = new Trainer(team2);

        // @Test invalid target
        // applying an effect with an invalid target; should throw a KeyException
        Effect effect = new Effect(null, EffectTarget.NONE, EffectStat.DEFENSE, 5, "1_tour");

        Attack invalAttack = new Attack("null", null, null, null, 0, List.of(effect));

        assertThrows(KeyException.class,
                     () -> effectManager.applyEffect(attacker, defender, invalAttack));
    }

    @Test
    public void testApplyValidEffect() {
        // init objects to test
        EffectManager effectManager = new EffectManager();

        BugemonTeam team1 = new BugemonTeam();
        BugemonTeam team2 = new BugemonTeam();

        Bugemon bugemon1 = TestUtilsBugemons.createDefaultBugemon("1");
        Bugemon bugemon2 = TestUtilsBugemons.createDefaultBugemon("2");

        team1.addBugemon(bugemon1);
        team2.addBugemon(bugemon2);

        Trainer attacker = new Trainer(team1);
        Trainer defender = new Trainer(team2);

        // @Test valid target
        // applying a valid effect; shouldn't throw any exception
        Effect effect = new Effect(null, EffectTarget.ADVERSARY, EffectStat.ATTACK, 5, "2_tour");

        Attack attack = new Attack("null", null, null, null, 0, List.of(effect));
        try {
            effectManager.applyEffect(attacker, defender, attack);
        } catch (KeyException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEffectDuration() {
        // test the expiration of the effect after its duration is over
        EffectManager effectManager = new EffectManager();

        BugemonTeam team1 = new BugemonTeam();
        BugemonTeam team2 = new BugemonTeam();

        Bugemon bugemon1 = TestUtilsBugemons.createDefaultBugemon("1");
        Bugemon bugemon2 = TestUtilsBugemons.createDefaultBugemon("2");

        int defaultAttack = bugemon2.getAttack();

        team1.addBugemon(bugemon1);
        team2.addBugemon(bugemon2);

        Trainer attacker = new Trainer(team1);
        Trainer defender = new Trainer(team2);

        Effect effect = new Effect(null, EffectTarget.ADVERSARY, EffectStat.ATTACK, +5, "2_tour");

        Attack attack = new Attack("null", null, null, null, 0, List.of(effect));
        try {
            effectManager.applyEffect(attacker, defender, attack);
        } catch (KeyException e) {
            e.printStackTrace();
        }

        // @Test effect application and expiration
        // testing the expiration of the effect and the stat modification
        assertEquals(defender.getCurrentBugemon().getAttack(),
                     defaultAttack + effect.getModifier());

        effectManager.update();

        assertEquals(defender.getCurrentBugemon().getAttack(),
                     defaultAttack + effect.getModifier());

        effectManager.update();

        assertEquals(defender.getCurrentBugemon().getAttack(), defaultAttack);
    }
}
