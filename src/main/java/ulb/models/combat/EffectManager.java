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
import ulb.models.trainer.Trainer;

/**
 * Manages the lifecycle of {@link ActiveEffect}s applied to
 * {@link Bugemon}s during combat.
 *
 * <p>
 * When an {@link Attack} is used, {@link #applyEffect(Trainer, Trainer, Attack)}
 * inspects the attack's {@link Effect} list, dispatches each effect to the
 * correct target(s) according to its {@link EffectTarget}, modifies the
 * targeted Bugemon's stats via {@link Bugemon#editStat(EffectStat, int)},
 * and stores the resulting {@link ActiveEffect} in an internal map keyed by
 * the affected {@link Bugemon}.
 * </p>
 *
 * <p>
 * At the end of each turn, {@link #update()} must be called to decrement
 * durations and automatically reverse expired effects, restoring the stat
 * to its pre-effect value.
 * </p>
 *
 * <p>
 * Only one {@link ActiveEffect} per {@link Bugemon} is tracked at a time;
 * if a new effect targets a Bugemon that already has an active one, the
 * previous entry is overwritten.
 * </p>
 *
 * @see ActiveEffect
 * @see Effect
 * @see EffectTarget
 * @see EffectStat
 * @see Bugemon#editStat(EffectStat, int)
 */
public class EffectManager {
    /**
     * Maps each {@link Bugemon} currently under the influence of an effect to
     * its corresponding {@link ActiveEffect}.
     *
     * <p>
     * The map is keyed by the {@link Bugemon} instance itself (not its ID) so
     * that the {@link #update()} pass can call {@link Bugemon#editStat} directly
     * on expiry without an additional lookup.
     * </p>
     */
    private Map<Bugemon, ActiveEffect> effects = new HashMap<>();

    // Methods

    /**
     * Advances the effect manager by one turn: decrements the duration of every
     * tracked {@link ActiveEffect} and, for any that have expired, reverses the
     * stat modification and removes the entry from the map.
     *
     * <p>
     * Reversal is performed by calling {@link Bugemon#editStat(EffectStat, int)}
     * with the negated modifier of the expired {@link Effect}, exactly undoing
     * the change that was applied when the effect was first activated.
     * </p>
     *
     * <p>
     * This method should be called once per turn, after all attacks and their
     * immediate effects have been resolved.
     * </p>
     */
    public void update() {
        for (Bugemon key : effects.keySet()) {
            ActiveEffect current = effects.get(key);
            if (current.isExpired()) {
                // restore effect and pop from map
                Effect currentEffect = current.getEffect();
                handleEffect(key, currentEffect.getStat(), -currentEffect.getModifier());
                effects.remove(key, current);
            } else {
                current.decrementDuration();
            }
        }
    }

    /**
     * Applies all {@link Effect}s of the given {@link Attack} to the appropriate
     * targets and records each as an {@link ActiveEffect} in the internal map.
     *
     * <p>
     * For each {@link Effect} in the attack, the target is resolved as follows:
     * <ul>
     *   <li>{@link EffectTarget#ADVERSARY} — the defender's current
     *       {@link Bugemon}.</li>
     *   <li>{@link EffectTarget#THROWER} — the attacker's current
     *       {@link Bugemon}.</li>
     *   <li>{@link EffectTarget#TEAM} — every {@link Bugemon} in the attacker's
     *       team.</li>
     * </ul>
     * </p>
     *
     * <p>
     * The initial stat modification is applied immediately via
     * {@link Bugemon#editStat(EffectStat, int)}, and the effect is stored with
     * its parsed duration so that {@link #update()} can reverse it once the
     * effect expires. If the duration string cannot be parsed, a duration of
     * {@code 0} is used (the effect expires at the start of the next
     * {@link #update()} call).
     * </p>
     *
     * @param attacker the {@link Trainer} whose Bugemon launched the attack;
     *                 must not be {@code null}.
     * @param defender the {@link Trainer} whose Bugemon receives the attack;
     *                 must not be {@code null}.
     * @param attack   the {@link Attack} whose effects are to be applied;
     *                 must not be {@code null}.
     * @throws KeyException if an {@link Effect} carries an unrecognised or
     *                      unhandled {@link EffectTarget} value.
     */
    public void applyEffect(Trainer attacker, Trainer defender, Attack attack) throws KeyException {
        List<Effect> effects = attack.getEffects();

        for (Effect e : effects) {
            EffectTarget target = e.getTarget();
            List<Bugemon> bugemons = new ArrayList<>();
            switch (target) {
                case EffectTarget.ADVERSARY:
                    bugemons.add(defender.getCurrentBugemon());
                    handleEffect(defender.getCurrentBugemon(), e.getStat(), e.getModifier());
                    break;
                case EffectTarget.THROWER:
                    bugemons.add(attacker.getCurrentBugemon());
                    handleEffect(attacker.getCurrentBugemon(), e.getStat(), e.getModifier());
                    break;
                case EffectTarget.TEAM:
                    for (Bugemon bugemon : attacker.getTeam()) {
                        bugemons.add(bugemon);
                        handleEffect(bugemon, e.getStat(), e.getModifier());
                    }
                    break;
                default:
                    throw new KeyException("Invalid or unhandled effect target");
            }

            // saving the effects
            for (Bugemon bugemon : bugemons) {
                int duration =
                        0; // default value; if duration couldn't be extracted, the effect expires
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
     * Applies a single stat modification to a {@link Bugemon} by delegating to
     * {@link Bugemon#editStat(EffectStat, int)}.
     *
     * <p>
     * Any exception thrown by {@link Bugemon#editStat} is caught and logged to
     * {@code System.err}; the method does not propagate it to the caller.
     * </p>
     *
     * @param bugemon the {@link Bugemon} whose stat is to be modified; must not
     *                be {@code null}.
     * @param stat    the {@link EffectStat} identifying which combat statistic
     *                to change.
     * @param value   the signed integer delta to add to the stat; positive values
     *                buff, negative values debuff.
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
