package ulb.models.combat;

import java.util.ArrayList;
import java.util.List;

import ulb.common.EffectTarget;
import ulb.models.bugemon.Attack;
import ulb.models.combat.effect.StatusEffect;
import ulb.models.combat.turn.TurnStep;
import ulb.models.combat.turn.TurnStep.HealBugemonStep;
import ulb.models.combat.turn.TurnStep.HealTeamStep;
import ulb.models.effect.Effect;
import ulb.models.effect.EffectVisitor;
import ulb.models.effect.HealEffect;
import ulb.models.effect.ResetMalusEffect;
import ulb.models.effect.StatModifierEffect;

public class EffectProcessor {

    public List<TurnStep> applyEffects(Attack attack, CombatBugemon attacker, CombatBugemon defender,
            CombatTeam attackerTeam) {
        List<TurnStep> steps = new ArrayList<>();
        for (Effect effect : attack.effects()) {
            steps.addAll(this.applySingleEffect(effect, attacker, defender, attackerTeam));
        }
        return steps;
    }

    private List<TurnStep> applySingleEffect(Effect effect, CombatBugemon attacker, CombatBugemon defender,
            CombatTeam attackerTeam) {

        List<TurnStep> steps = new ArrayList<>();

        EffectTarget target = effect.getTarget();

        effect.accept(new EffectVisitor() {
            @Override
            public void visit(HealEffect healEffect) {
                switch (target) {
                    case TEAM :
                        steps.add(new HealTeamStep(attackerTeam));
                        attackerTeam.getAlive().forEach(b -> {
                            b.heal(healEffect.getAmount());
                        });
                        break;
                    case THROWER :
                        steps.add(new HealBugemonStep(attacker));
                        attacker.heal(healEffect.getAmount());
                        break;
                    default :
                        return;
                }
            }

            @Override
            public void visit(ResetMalusEffect resetMalusEffect) {
                switch (target) {
                    case THROWER :
                        attacker.clearMalusEffects();
                        break;
                    default :
                        return;
                }
            }

            @Override
            public void visit(StatModifierEffect statModifierEffect) {
                StatusEffect effect = new StatusEffect(statModifierEffect.getStat(), statModifierEffect.getModifier(),
                        statModifierEffect.getDuration());
                switch (target) {
                    case THROWER :
                        attacker.addEffect(effect);
                        break;
                    case OPPONENT :
                        defender.addEffect(effect);
                        break;
                    default :
                        return;
                }
            }
        });

        return steps;
    }
}
