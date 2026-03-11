package ulb.models.combat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.List;
import org.junit.Test;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.effect.Effect;
import ulb.models.bugemon.effect.EffectStat;
import ulb.models.bugemon.effect.EffectTarget;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;
import ulb.utils.test.TestUtilsBugemons;

public class TestEffectManager {

    private Trainer createTrainer(String bugemonId) {
        BugemonTeam team = new BugemonTeam();
        team.addBugemon(TestUtilsBugemons.createDefaultBugemon(bugemonId));
        return new AutoTrainer(team);
    }

    @Test
    public void testApplyInvalidEffect() {
        EffectManager effectManager = new EffectManager();

        Trainer attacker = createTrainer("1");
        Trainer defender = createTrainer("2");

        Effect effect = new Effect(
            null,
            EffectTarget.NONE,
            EffectStat.DEFENSE,
            5,
            "1_tour"
        );
        Attack invalAttack = new Attack(
            "null",
            null,
            null,
            null,
            0,
            List.of(effect)
        );

        assertThrows(IllegalArgumentException.class, () ->
            effectManager.applyEffect(attacker, defender, invalAttack)
        );
    }

    @Test
    public void testApplyValidEffect() {
        EffectManager effectManager = new EffectManager();

        Trainer attacker = createTrainer("1");
        Trainer defender = createTrainer("2");

        Effect effect = new Effect(
            null,
            EffectTarget.ADVERSARY,
            EffectStat.ATTACK,
            5,
            "2_tour"
        );
        Attack attack = new Attack(
            "null",
            null,
            null,
            null,
            0,
            List.of(effect)
        );

        effectManager.applyEffect(attacker, defender, attack);
    }

    @Test
    public void testEffectDuration() {
        EffectManager effectManager = new EffectManager();

        Trainer attacker = createTrainer("1");
        Trainer defender = createTrainer("2");

        int defaultAttack = defender.getCurrentBugemon().getAttack();

        Effect effect = new Effect(
            null,
            EffectTarget.ADVERSARY,
            EffectStat.ATTACK,
            +5,
            "2_tour"
        );
        Attack attack = new Attack(
            "null",
            null,
            null,
            null,
            0,
            List.of(effect)
        );

        effectManager.applyEffect(attacker, defender, attack);

        assertEquals(
            defaultAttack + effect.getModifier(),
            defender.getCurrentBugemon().getAttack()
        );

        effectManager.update();

        assertEquals(
            defaultAttack + effect.getModifier(),
            defender.getCurrentBugemon().getAttack()
        );

        effectManager.update();

        assertEquals(defaultAttack, defender.getCurrentBugemon().getAttack());
    }
}
