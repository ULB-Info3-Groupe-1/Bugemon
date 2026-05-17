package ulb.models.combat.damage;

import ulb.Configuration;
import ulb.common.DamageResult;
import ulb.models.bugemon.Attack;
import ulb.models.combat.CombatBugemon;

public class DamageCalculator {
    AttackFactorFormula attackFactorFormula;
    ReductionFactorFormula reductionFactorFormula;

    public DamageCalculator() {
        this.attackFactorFormula = Configuration.Game.ATTACK_FACTOR_FORMULA;
        this.reductionFactorFormula = Configuration.Game.REDUCTION_FACTOR_FORMULA;
    }

    public DamageResult calculateDamage(CombatBugemon attacker, CombatBugemon defender, Attack attack) {
        double power = attack.power();

        double attackFactor = this.attackFactorFormula.evaluate(attacker.getEffectiveAttack());
        double reductionFactor = this.reductionFactorFormula.evaluate(defender.getEffectiveDefense());

        double typeMultiplier = attack.type().getMultiplierAgainst(defender.getType());
        Efficiency efficiency = Efficiency.fromMultiplier(typeMultiplier);

        int totalDamage = (int) Math.ceil(power * attackFactor * reductionFactor * typeMultiplier);

        return new DamageResult(totalDamage, efficiency);
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
