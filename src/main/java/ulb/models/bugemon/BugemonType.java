package ulb.models.bugemon;

import java.util.Map;

/** Elemental type of a Bugemon; used for type-matchup calculations during combat. */
public enum BugemonType {
    FLORA,
    AQUA,
    PYRO,
    LITHO;

    private static Map<BugemonType, Map<BugemonType, Efficiency>> strongAgainst;

    public Efficiency getEfficiencyAgainst(BugemonType opponentType) {
        return strongAgainst.get(this).get(opponentType);
    };

    static {
        strongAgainst = Map.of(
            BugemonType.FLORA, Map.of(
                BugemonType.FLORA, Efficiency.NEUTRAL,
                BugemonType.AQUA, Efficiency.HIGH,
                BugemonType.PYRO, Efficiency.NEUTRAL,
                BugemonType.LITHO, Efficiency.LOW
            ),

            BugemonType.AQUA, Map.of(
                BugemonType.FLORA, Efficiency.LOW,
                BugemonType.AQUA, Efficiency.NEUTRAL,
                BugemonType.PYRO, Efficiency.HIGH,
                BugemonType.LITHO, Efficiency.NEUTRAL
            ),

            BugemonType.PYRO, Map.of(
                BugemonType.FLORA, Efficiency.NEUTRAL,
                BugemonType.AQUA, Efficiency.LOW,
                BugemonType.PYRO, Efficiency.NEUTRAL,
                BugemonType.LITHO, Efficiency.HIGH
            ),

            BugemonType.LITHO, Map.of(
                BugemonType.FLORA, Efficiency.HIGH,
                BugemonType.AQUA, Efficiency.NEUTRAL,
                BugemonType.PYRO, Efficiency.LOW,
                BugemonType.LITHO, Efficiency.NEUTRAL
            )
        );
    }
}
