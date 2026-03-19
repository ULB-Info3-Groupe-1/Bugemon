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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ulb.models.bugemon.ActiveEffect;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.effect.Effect;
import ulb.models.bugemon.effect.EffectStat;
import ulb.models.bugemon.effect.EffectTarget;
import ulb.models.bugemon.effect.EffectType;
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
     * This method is called once per turn, <em>before</em> the trainers select
     * their actions and attacks are resolved. Effects applied during the
     * previous turn are therefore ticked at the start of the following turn.
     * </p>
     */
    public void update() {
        List<Bugemon> toRemove = new ArrayList<>();
        for (Bugemon key : effects.keySet()) {
            ActiveEffect current = effects.get(key);
            if (current.isExpired()) {
                // restore effect and pop from map
                Effect effect = current.getEffect();
                handleEffect(key, effect.getStat(), -effect.getModifier());
                toRemove.add(key);
            } else {
                current.decrementDuration();
            }
        }
        toRemove.forEach(effects::remove);
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
     *
     * <p>
     * The initial stat modification is applied immediately via
     * {@link Bugemon#editStat(EffectStat, int)}, and the effect is stored with
     * its parsed duration (decremented by one to account for the current turn)
     * so that {@link #update()} can reverse it once the effect expires.
     * </p>
     *
     * <p>
     * <strong>Duration fallback:</strong> if {@link Effect#extractDuration()}
     * throws (malformed or {@code null} duration string), a duration of
     * {@code 0} is used instead, meaning the effect will be reversed at the
     * very start of the next {@link #update()} call.
     * </p>
     *
     * <p>
     * <strong>Overwrite behaviour:</strong> if the targeted {@link Bugemon}
     * already has an active effect tracked in the internal map, the previous
     * entry is silently overwritten by the new one. The old effect's stat
     * modification is <em>not</em> reversed before the overwrite; callers
     * should be aware that stacking effects is not supported.
     * </p>
     *
     * @param attacker the {@link Trainer} whose Bugemon launched the attack;
     *                 must not be {@code null}.
     * @param defender the {@link Trainer} whose Bugemon receives the attack;
     *                 must not be {@code null}.
     * @param attack   the {@link Attack} whose effects are to be applied;
     *                 must not be {@code null}.
     */
    public void applyEffect(Trainer attacker, Trainer defender, Attack attack) {
        List<Effect> effects = attack.effects();

        for (Effect e : effects) {
            EffectTarget target = e.getTarget();
            List<Bugemon> bugemons = new ArrayList<>();
            if (e.getTypeEffect() == EffectType.SOIN) {
                e.setStat(EffectStat.HP);
            }
            switch (target) {
                case ADVERSARY:
                    bugemons.add(defender.getCurrentBugemon());
                    break;
                case THROWER:
                    bugemons.add(attacker.getCurrentBugemon());
                    break;
                case EffectTarget.TEAM:
                    bugemons.addAll(attacker.getTeam().getAll());
                    break;
                default:
                    throw new IllegalArgumentException("Illegal effect target: " + target);
            }

            switch (e.getTypeEffect()) {
                case STAT_MODIFIER:
                    for (Bugemon bugemon : bugemons) {
                        handleEffect(bugemon, e.getStat(), e.getModifier());

                        int duration = 0;
                        try {
                            int extracted = e.extractDuration();
                            duration = (extracted == -1) ? -1 : extracted - 1;
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }

                        ActiveEffect activeEffect = new ActiveEffect(e, duration);
                        this.effects.put(bugemon, activeEffect);
                    }
                    break;

                case SOIN:
                    for (Bugemon bugemon : bugemons) {
                        handleHeal(bugemon, e.getValue());
                    }
                    break;

                case RESET_MALUS:
                    // TODO: implement reset malus logic
                    break;

                default:
                    throw new IllegalArgumentException("Illegal effect type: " + e.getTypeEffect());
            }
        }
    }

    /**
     * Applies a healing effect to the specified {@link Bugemon} by calling
     * {@link Bugemon#editStat(EffectStat, int)} with the provided value
     * @param bugemon the {@link Bugemon} to heal; must not be {@code null}.
     * @param value the amount of HP to restore; must be a positive integer. If {@code null} or
     *         non-positive, the method does nothing. (Negative or zero values are not treated as
     *         damage or debuffs; they are simply ignored for healing purposes.)
     */
    private void handleHeal(Bugemon bugemon, Integer value) {
        if (value == null || value <= 0) {
            return;
        }
        try {
            bugemon.editStat(EffectStat.HP, value);
        } catch (Exception e) {
            e.printStackTrace();
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
        if (stat == null) {
            System.err.println("Effect stat is null, effect ignored.");
            return;
        }

        try {
            bugemon.editStat(stat, value);
        } catch (Exception e) {
            // TODO: handle exception -> Logger ?
            e.printStackTrace();
        }
    }
}
