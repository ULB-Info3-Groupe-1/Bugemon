/**
 * File name : EffectManager.java
 * Description : A helper class for the Combat
 * instances for the management of the Effects of an Attack.
 *
 * @author Rocca Manuel
 * @date 3 mar. 2026
 * @version 1.0
 */

package ulb.models.combat;

import java.security.KeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ulb.models.bugemon.ActiveEffect;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Effect;
import ulb.models.bugemon.EffectStat;
import ulb.models.bugemon.EffectTarget;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.trainer.Trainer;

/**
 *
 */
public class EffectManager {

    // String: Bugemon id
    // ActiveEffect: the applied effect and its duration
    private Map<Bugemon, ActiveEffect> effects = new HashMap<>();

    // Methods

    /**
     * Updates the duration of the effects and restores their values once they have
     * expired.
     */
    public void update() {
        for (Bugemon key : effects.keySet()) {
            ActiveEffect current = effects.get(key);
            if (current.isExpired()) {
                // restore effect and pop from map
                Effect currentEffect = current.getEffect();
                handleEffect(
                        key,
                        currentEffect.getStat(),
                        -currentEffect.getModifier());
                effects.remove(key, current);
            } else {
                current.decrementDuration();
            }
        }
    }

    /**
     * Applies the effect of the attack based on its target.
     *
     * @param attacker (Trainer) The trainer that launched the attack.
     * @param defender (Trainer) The trainer that takes the attack.
     * @param attack   (Attack) The thrown attack by the attacker.
     */
    public void applyEffect(Trainer attacker, Trainer defender, Attack attack)
            throws KeyException {
        List<Effect> effects = attack.getEffects();

        for (Effect e : effects) {
            EffectTarget target = e.getTarget();
            List<Bugemon> bugemons = new ArrayList<>();
            switch (target) {
                case EffectTarget.ADVERSARY:
                    bugemons.add(defender.getCurrentBugemon());
                    handleEffect(
                            defender.getCurrentBugemon(),
                            e.getStat(),
                            e.getModifier());
                    break;
                case EffectTarget.THROWER:
                    bugemons.add(attacker.getCurrentBugemon());
                    handleEffect(
                            attacker.getCurrentBugemon(),
                            e.getStat(),
                            e.getModifier());
                    break;
                case EffectTarget.TEAM:
                    BugemonTeam team = attacker.getTeam();
                    for (Bugemon bugemon : team.getTeam()) {
                        bugemons.add(bugemon);
                        handleEffect(bugemon, e.getStat(), e.getModifier());
                    }
                    break;
                default:
                    throw new KeyException("Invalid or unhandled effect target");
            }

            // saving the effects
            for (Bugemon bugemon : bugemons) {
                int duration = 0; // default value; if duration couldn't be extracted, the effect expires
                // immediately
                try {
                    duration = e.extractDuration() - 1;
                } catch (Exception exception) {
                    // TODO: handle exception
                    System.err.println(exception.getStackTrace());
                }
                ActiveEffect activeEffect = new ActiveEffect(e, duration);
                this.effects.put(bugemon, activeEffect);
            }
        }
    }

    /**
     * Handles the application of an effect on a Bugemon by editing the stat of the Bugemon based on the stat 
     * and the modifier of the effect.
     * @param bugemon the Bugemon on which the effect is applied
     * @param stat the stat that is affected by the effect
     * @param value the value of the modifier of the effect to be applied to the stat of the Bugemon
     */
    private void handleEffect(Bugemon bugemon, EffectStat stat, int value) {
        try {
            bugemon.editStat(stat, value);
        } catch (Exception e) {
            // TODO: handle exception -> Logger ?
            System.err.println(e.getStackTrace());
        }
    }
}
