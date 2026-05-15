package ulb.models.combat;

import ulb.Configuration;
import ulb.models.bugemon.Attack;

public class DamageCalculator {
    AttackFactorFormula attackFactorFormula;
    ReductionFactorFormula reductionFactorFormula;

    public DamageCalculator() {
        this.attackFactorFormula = Configuration.Game.ATTACK_FACTOR_FORMULA;
        this.reductionFactorFormula = Configuration.Game.REDUCTION_FACTOR_FORMULA;
    }

    public int calculateDamage(CombatBugemon attacker, CombatBugemon defender, Attack attack) {

        double power = (double) attack.power();

        double attackFactor = this.attackFactorFormula.evaluate(attacker.getEffectiveAttack());
        double reductionFactor = this.reductionFactorFormula.evaluate(defender.getEffectiveDefense());

        double totalDamage = power * attackFactor * reductionFactor;

        return (int) Math.ceil(totalDamage);
    }

    @FunctionalInterface
    public interface AttackFactorFormula {
        double evaluate(int effectiveAttack);
    }

    @FunctionalInterface
    public interface ReductionFactorFormula {
        double evaluate(int effectiveDefense);
    }
}
