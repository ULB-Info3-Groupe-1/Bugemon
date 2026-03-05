package ulb.models.combat;

import java.util.ArrayList;
import java.util.List;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon.BType;
import ulb.models.bugemon.Stats;
import ulb.models.trainer.Trainer;

public class CombatHelper {

    public enum Efficiency {
        NEUTRAL,
        LOW,
        HIGH,
    }

    public static Trainer attackPriority(Trainer trainer1, Trainer trainer2) {
        int initiativeTrainer1 = trainer1.getCurrentBugemonInitiative();
        int initiativeTrainer2 = trainer2.getCurrentBugemonInitiative();

        if (initiativeTrainer1 < initiativeTrainer2) {
            return trainer2;
        } else if (initiativeTrainer1 > initiativeTrainer2) {
            return trainer1;
        } else {
            return Math.random() <= 0.5 ? trainer1 : trainer2;
        }
    }

    public static double calculateDamage(
        Attack attack,
        Stats strikerStats,
        Stats defenderStats,
        BType defenderType
    ) {
        int power = attack.getPower();
        double attackFactor = (100.0 + strikerStats.getAttack()) / 100.0;
        double reductionFactor = 100.0 / (defenderStats.getDefense() + 100.0);
        double typeFactor = getEfficiencyFactor(attack, defenderType);
        double criticFactor = Math.random() <= 0.1 ? 1.5 : 1.0;

        double result =
            power * attackFactor * reductionFactor * typeFactor * criticFactor;

        return result;
    }

    public static double getEfficiencyFactor(
        Attack attack,
        BType defenderType
    ) {
        Efficiency efficiency = compareBType(attack.getType(), defenderType);

        if (efficiency.equals(Efficiency.LOW)) {
            return 0.75;
        } else if (efficiency.equals(Efficiency.HIGH)) {
            return 1.50;
        } else {
            return 1.00;
        }
    }

    /**
     * Compare 2 types of a striker and a defender. Uses the logic of a cycle between the types.
     * @param strikerType The type of the striker
     * @param defenderType The type of the defender
     * @return (enum Efficiency) The Efficiency of the striker type on the defender type
     */
    public static Efficiency compareBType(
        BType strikerType,
        BType defenderType
    ) {
        // It transforms first the enum BType in a list (already ordered) and get the index of both types.
        // Using modulo on the difference allows to return the correct efficiency.
        List<BType> typeCycle = new ArrayList<BType>(List.of(BType.values()));

        int strikerIdx = typeCycle.indexOf(strikerType);
        int defenderIdx = typeCycle.indexOf(defenderType);

        int difference = Math.floorMod(
            strikerIdx - defenderIdx,
            typeCycle.size()
        );

        if (difference == 1) {
            return Efficiency.LOW; //
        } else if (difference == typeCycle.size() - 1) {
            return Efficiency.HIGH;
        } else {
            return Efficiency.NEUTRAL; // All other possibilities are neutral
        }
    }
}
