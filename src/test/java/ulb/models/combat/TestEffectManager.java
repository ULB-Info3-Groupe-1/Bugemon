/**
 * File name : TestActiveEffect.java
 * Description : Test class for the ActiveEffect class.
 *
 * @author Rocca Manuel
 * @date 3 mar. 2026
 * @version 1.0
 */

package ulb.models.test_combat;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.KeyException;
import java.util.List;
import org.junit.Test;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Effect;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.combat.EffectManager;
import ulb.models.trainer.Trainer;
import ulb.utils.TestUtilsBugemons;

public class TestEffectManager {

    @Test
    public void testApplyEffect() {
        // init objects to test
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

        // @Test invalid target
        // applying an effect with an invalid target; should throw a KeyException
        Effect effect1 = new Effect(null, "invalid", "defense", 5, "1_tour");

        Attack invalAttack = new Attack(
            "null",
            null,
            null,
            null,
            0,
            List.of(effect1)
        );

        assertThrows(KeyException.class, () ->
            effectManager.applyEffect(attacker, defender, invalAttack)
        );

        // @Test valid target
        // applying a valid effect; shouldn't throw any exception
        Effect effect2 = new Effect(null, "adversaire", "attaque", 5, "2_tour");
        Attack attack = new Attack(
            "null",
            null,
            null,
            null,
            0,
            List.of(effect2)
        );
        try {
            effectManager.applyEffect(attacker, defender, attack);
        } catch (KeyException e) {
            e.printStackTrace();
        }

        // @Test effect application and expiration
        // testing the expiration of the effect and the stat modification
        assertEquals(
            defender.getCurrentBugemon().getAttack(),
            defaultAttack + effect2.getModifier()
        );

        effectManager.update();

        assertEquals(
            defender.getCurrentBugemon().getAttack(),
            defaultAttack + effect2.getModifier()
        );

        effectManager.update();

        assertEquals(defender.getCurrentBugemon().getAttack(), defaultAttack);
    }
}
