package bugemon.common.models.combat.utils;

import java.util.ArrayList;
import java.util.List;

import bugemon.common.EffectTarget;
import bugemon.common.models.bugemon.Attack;
import bugemon.common.models.combat.CombatBugemon;
import bugemon.common.models.combat.CombatTeam;
import bugemon.common.models.combat.effect.StatusEffect;
import bugemon.common.models.combat.turn.TurnStep;
import bugemon.common.models.combat.turn.TurnStep.HealBugemonStep;
import bugemon.common.models.combat.turn.TurnStep.HealTeamStep;
import bugemon.common.models.effect.Effect;
import bugemon.common.models.effect.EffectVisitor;
import bugemon.common.models.effect.HealEffect;
import bugemon.common.models.effect.ResetMalusEffect;
import bugemon.common.models.effect.StatModifierEffect;

/**
 * Applies secondary {@link bugemon.common.models.effect.Effect} instances from an attack to the appropriate combat
 * targets and records each outcome as a {@link TurnStep}.
 *
 * <p>
 * The processor iterates over an attack's effect list and delegates each effect to an internal
 * {@link bugemon.common.models.effect.EffectVisitor}. Supported effect types are:
 * <ul>
 * <li>{@link bugemon.common.models.effect.HealEffect} — heals the thrower or the entire team.</li>
 * <li>{@link bugemon.common.models.effect.ResetMalusEffect} — clears negative status effects from the thrower.</li>
 * <li>{@link bugemon.common.models.effect.StatModifierEffect} — applies a
 * {@link bugemon.common.models.combat.effect.StatusEffect} to the thrower or the opponent.</li>
 * </ul>
 */
public class EffectProcessor {

    /**
     * Applies all secondary effects of an attack and returns the resulting steps.
     *
     * @param attack
     *            the attack whose effect list is processed
     * @param attacker
     *            the Bugemon that used the attack (effect thrower)
     * @param defender
     *            the Bugemon that was attacked (potential effect target)
     * @param attackerTeam
     *            the team of the attacker, used for team-wide heal effects
     * @return an ordered list of {@link TurnStep} events produced by the effects; never {@code null}, may be empty if
     *         no effect produced an observable step
     */
    public List<TurnStep> applyEffects(Attack attack, CombatBugemon attacker, CombatBugemon defender,
            CombatTeam attackerTeam) {
        List<TurnStep> steps = new ArrayList<>();
        for (Effect effect : attack.effects()) {
            steps.addAll(this.applySingleEffect(effect, attacker, defender, attackerTeam));
        }
        return steps;
    }

    /**
     * Applies a single effect to the correct target and returns any resulting steps.
     *
     * <p>
     * The target is determined by {@link bugemon.common.models.effect.Effect#getTarget()}. Unsupported target values
     * are silently ignored within each visitor branch.
     *
     * @param effect
     *            the effect to apply
     * @param thrower
     *            the Bugemon that triggered the effect (e.g. the attacker)
     * @param opponent
     *            the opposing Bugemon (may be the target depending on the effect)
     * @param throwerTeam
     *            the team of the thrower, used for team-scoped effects
     * @return the list of {@link TurnStep} events produced; never {@code null}, may be empty for effects with no
     *         observable outcome (e.g. stat modifiers)
     */
    public List<TurnStep> applySingleEffect(Effect effect, CombatBugemon thrower, CombatBugemon opponent,
            CombatTeam throwerTeam) {

        List<TurnStep> steps = new ArrayList<>();

        EffectTarget target = effect.getTarget();

        effect.accept(new EffectVisitor() {
            @Override
            public void visit(HealEffect healEffect) {
                switch (target) {
                    case TEAM :
                        throwerTeam.getAlive().forEach(b -> b.heal(healEffect.getAmount()));
                        steps.add(new HealTeamStep(throwerTeam, throwerTeam.getActive().getCurrentHp()));
                        break;
                    case THROWER :
                        thrower.heal(healEffect.getAmount());
                        steps.add(new HealBugemonStep(thrower, thrower.getCurrentHp()));
                        break;
                    default :
                        return;
                }
            }

            @Override
            public void visit(ResetMalusEffect resetMalusEffect) {
                if (target == EffectTarget.THROWER) {
                    thrower.clearMalusEffects();
                }
            }

            @Override
            public void visit(StatModifierEffect statModifierEffect) {
                StatusEffect effect = new StatusEffect(statModifierEffect.getStat(), statModifierEffect.getModifier(),
                        statModifierEffect.getDuration());
                switch (target) {
                    case THROWER :
                        thrower.addEffect(effect);
                        break;
                    case OPPONENT :
                        opponent.addEffect(effect);
                        break;
                    default :
                        return;
                }
            }
        });

        return steps;
    }
}
