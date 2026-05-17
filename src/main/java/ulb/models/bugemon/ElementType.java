package ulb.models.bugemon;

import java.util.Map;

import ulb.models.type.Efficiency;

public enum ElementType {
    FLORA,
    AQUA,
    PYRO,
    LITHO,
    NORMAL;

    private static Map<ElementType, Map<ElementType, Efficiency>> strongAgainst;

    public Efficiency getEfficiencyAgainst(ElementType opponentType) {
        return strongAgainst.get(this).get(opponentType);
    }

    static {
        strongAgainst = Map.of(ElementType.FLORA,
                Map.of(ElementType.FLORA, Efficiency.NEUTRAL, ElementType.AQUA, Efficiency.HIGH, ElementType.PYRO,
                        Efficiency.NEUTRAL, ElementType.LITHO, Efficiency.LOW, ElementType.NORMAL, Efficiency.NEUTRAL),

                ElementType.AQUA,
                Map.of(ElementType.FLORA, Efficiency.LOW, ElementType.AQUA, Efficiency.NEUTRAL, ElementType.PYRO,
                        Efficiency.HIGH, ElementType.LITHO, Efficiency.NEUTRAL, ElementType.NORMAL, Efficiency.NEUTRAL),

                ElementType.PYRO,
                Map.of(ElementType.FLORA, Efficiency.NEUTRAL, ElementType.AQUA, Efficiency.LOW, ElementType.PYRO,
                        Efficiency.NEUTRAL, ElementType.LITHO, Efficiency.HIGH, ElementType.NORMAL, Efficiency.NEUTRAL),

                ElementType.LITHO,
                Map.of(ElementType.FLORA, Efficiency.HIGH, ElementType.AQUA, Efficiency.NEUTRAL, ElementType.PYRO,
                        Efficiency.LOW, ElementType.LITHO, Efficiency.NEUTRAL, ElementType.NORMAL, Efficiency.NEUTRAL),

                ElementType.NORMAL,
                Map.of(ElementType.FLORA, Efficiency.NEUTRAL, ElementType.AQUA, Efficiency.NEUTRAL, ElementType.PYRO,
                        Efficiency.NEUTRAL, ElementType.LITHO, Efficiency.NEUTRAL, ElementType.NORMAL,
                        Efficiency.NEUTRAL));
    }
}
