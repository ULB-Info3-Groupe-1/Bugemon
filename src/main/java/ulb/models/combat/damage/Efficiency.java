package ulb.models.combat.damage;

import ulb.models.bugemon.ElementType;

public enum Efficiency {
    NOT_VERY_EFFICIENT(0.75),
    NORMAL(1.0),
    SUPER_EFFICIENT(1.5);

    private final double multiplier;

    Efficiency(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return this.multiplier;
    }

    public static Efficiency fromMultiplier(double mult) {
        if (mult > 1.0) {
            return SUPER_EFFICIENT;
        }
        if (mult < 1.0) {
            return NOT_VERY_EFFICIENT;
        }
        return NORMAL;
    }

    // TODO: this likely introduces code dup with other methods (notably in CombatService)
    public static Efficiency preview(ElementType type1, ElementType type2) {
        return fromMultiplier(type1.getMultiplierAgainst(type2));
    }

    public boolean isAtLeastNormal() {
        return this.compareTo(NORMAL) >= 0;
    }
}
