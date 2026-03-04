package ulb.models.combat;

import java.util.ArrayList;
import java.util.List;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Bugemon.BType;
import ulb.models.bugemon_team.BugemonTeam;

impoty

public class CombatHelper {

    public enum Efficiency {
        NEUTRAL,
        LOW,
        HIGH
    }

    public static Efficiency getEfficiencyFactor(Attack attack, Bugemon bugDefender) {
        BType attackType = attack.getType();
        BType defenderType = bugDefender.getType();

        return Efficiency.NEUTRAL;
    }

    public static double calculateDamage(Attack attack, Bugemon bugDefender) {
        return 0.0;
    }

    public static void applyEffect(Attack attack, Bugemon bugTarget) {
        return;
    }

    public static void applyEffect(Attack attack, BugemonTeam bugStrikerTeam) {
        return;
    }

    /**
    * Compare 2 types of a striker and a defender. Uses the logic of a cycle between the types.
    * @param strikerType The type of the striker
    * @param defenderType The type of the defender
    * @return (enum Efficiency) The Efficiency of the striker type on the defender type
 */
    public static Efficiency compareBType(BType strikerType, BType defenderType) {
        // It transforms first the enum BType in a list (already ordered) and get the index of both types.
        // Using modulo on the difference allows to return the correct efficiency.
        List<BType> typeCycle = new ArrayList<BType>(List.of(BType.values()));

        int strikerIdx = typeCycle.indexOf(strikerType);
        int defenderIdx = typeCycle.indexOf(strikerType);

        int difference = strikerIdx - defenderIdx % typeCycle.size();
        switch (difference) {
            case 0:
                return Efficiency.NEUTRAL;
            case 1:
                return Efficiency.HIGH;
            case 2:
                return Efficiency.NEUTRAL;
            default:
                return Efficiency.LOW; // All other possibilities are where we are at the end of typeCycle
        }
    }
}
