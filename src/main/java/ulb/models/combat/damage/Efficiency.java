package ulb.models.combat.damage;

public enum Efficiency {
    SUPER_EFFICIENT(1.5),
    NORMAL(1.0),
    NOT_VERY_EFFICIENT(0.75);

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
}
