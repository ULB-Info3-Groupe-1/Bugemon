package ulb.models.combat.damage;

import ulb.Configuration;
import ulb.common.DamageResult;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.ElementType;
import ulb.models.combat.CombatBugemon;
import ulb.models.skills.SkillContext;

public class DamageCalculator {
    AttackFactorFormula attackFactorFormula;
    ReductionFactorFormula reductionFactorFormula;

    public DamageCalculator() {
        this.attackFactorFormula = Configuration.Game.ATTACK_FACTOR_FORMULA;
        this.reductionFactorFormula = Configuration.Game.REDUCTION_FACTOR_FORMULA;
    }

    public DamageResult calculateDamage(CombatBugemon attacker, CombatBugemon defender, Attack attack,
            SkillContext attackerSkillContext) {
        double power = attack.power();

        int attackStat = attacker.getEffectiveAttack();
        double attackFactor = this.attackFactorFormula.evaluate(attackStat);
        double reductionFactor = this.reductionFactorFormula.evaluate(defender.getEffectiveDefense());

        double typeMultiplier = attack.type().getMultiplierAgainst(defender.getType())
                * attackerSkillContext.getTypeMultiplier(attack.type());
        Efficiency efficiency = this.previewEfficiency(attack.type(), defender.getType());

        int totalDamage = (int) Math.ceil(power * attackFactor * reductionFactor * typeMultiplier);

        return new DamageResult(totalDamage, efficiency);
    }

    public int calculateDamage(Attack attack, int attackerEffectiveAttack, int defenderEffectiveDefense) {
        double power = attack.power();
        double attackFactor = this.attackFactorFormula.evaluate(attackerEffectiveAttack);
        double reductionFactor = this.reductionFactorFormula.evaluate(defenderEffectiveDefense);
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

    public Efficiency previewEfficiency(ElementType attackType, ElementType defenderType) {
        return Efficiency.fromMultiplier(attackType.getMultiplierAgainst(defenderType));
    }
}
