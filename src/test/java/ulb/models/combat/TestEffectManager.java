package ulb.models.combat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.List;

import org.junit.Test;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.effect.Effect;
import ulb.models.bugemon.effect.EffectStat;
import ulb.models.bugemon.effect.EffectTarget;
import ulb.models.bugemon.effect.EffectType;
import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;
import ulb.utils.test.TestUtilsBugemons;

public class TestEffectManager {
    private Trainer createTrainer(String bugemonId) {
        return new AutoTrainer(List.of(TestUtilsBugemons.createDefaultBugemon(bugemonId)));
    }

    @Test
    public void testApplyInvalidEffectTarget() {
        EffectManager effectManager = new EffectManager();

        Trainer attacker = createTrainer("1");
        Trainer defender = createTrainer("2");

        Effect effect = new Effect(EffectType.STAT_MODIFIER, EffectTarget.NONE, EffectStat.DEFENSE,
                                   5, "1_tour");
        Attack invalAttack = new Attack("null", null, null, null, 0, List.of(effect));

        assertThrows(IllegalArgumentException.class,
                     () -> effectManager.applyEffect(attacker, defender, invalAttack));
    }

    @Test
    public void testApplyValidStatModifierEffect() {
        EffectManager effectManager = new EffectManager();

        Trainer attacker = createTrainer("1");
        Trainer defender = createTrainer("2");

        Effect effect = new Effect(EffectType.STAT_MODIFIER, EffectTarget.ADVERSARY,
                                   EffectStat.ATTACK, 5, "2_tour");
        Attack attack = new Attack("null", null, null, null, 0, List.of(effect));

        effectManager.applyEffect(attacker, defender, attack);
    }

    @Test
    public void testEffectDuration() {
        EffectManager effectManager = new EffectManager();

        Trainer attacker = createTrainer("1");
        Trainer defender = createTrainer("2");

        int defaultAttack = defender.getCurrentBugemon().getAttack();

        Effect effect = new Effect(EffectType.STAT_MODIFIER, EffectTarget.ADVERSARY,
                                   EffectStat.ATTACK, +5, "2_tour");
        Attack attack = new Attack("null", null, null, null, 0, List.of(effect));

        effectManager.applyEffect(attacker, defender, attack);

        assertEquals(defaultAttack + effect.getModifier(),
                     defender.getCurrentBugemon().getAttack());

        effectManager.update();
        assertEquals(defaultAttack + effect.getModifier(),
                     defender.getCurrentBugemon().getAttack());

        effectManager.update();
        assertEquals(defaultAttack, defender.getCurrentBugemon().getAttack());
    }

    @Test
    public void testApplyHealingEffect() {
        EffectManager effectManager = new EffectManager();

        Trainer attacker = createTrainer("1");
        Trainer defender = createTrainer("2");

        int beforeHp = attacker.getCurrentBugemon().getHp();
        attacker.getCurrentBugemon().takeDamage(20);
        int damagedHp = attacker.getCurrentBugemon().getHp();

        Effect healEffect = new Effect(EffectType.SOIN, EffectTarget.THROWER, null, 0, null);
        healEffect.setValue(15);

        Attack healAttack = new Attack("heal", null, null, null, 0, List.of(healEffect));

        effectManager.applyEffect(attacker, defender, healAttack);

        assertEquals(damagedHp + 15, attacker.getCurrentBugemon().getHp());

        effectManager.update();
        assertEquals(damagedHp + 15, attacker.getCurrentBugemon().getHp());

        assertEquals(beforeHp - 5, attacker.getCurrentBugemon().getHp());
    }
}