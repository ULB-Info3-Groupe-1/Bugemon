package ulb.models.bugemon;

public enum ElementType {
    FLORA,
    AQUA,
    PYRO,
    LITHO,
    NORMAL;

    private static final double SUPER_EFFECTIVE = 1.5;
    private static final double NOT_VERY_EFFECTIVE = 0.75;
    private static final double NEUTRAL = 1.0;

    public double getMultiplierAgainst(ElementType defender) {
        if (this == defender) {
            return NEUTRAL;
        }

        return switch (this) {
            case FLORA -> switch (defender) {
                case AQUA -> SUPER_EFFECTIVE;
                case LITHO -> NOT_VERY_EFFECTIVE;
                default -> NEUTRAL;
            };
            case AQUA -> switch (defender) {
                case PYRO -> SUPER_EFFECTIVE;
                case FLORA -> NOT_VERY_EFFECTIVE;
                default -> NEUTRAL;
            };
            case PYRO -> switch (defender) {
                case LITHO -> SUPER_EFFECTIVE;
                case AQUA -> NOT_VERY_EFFECTIVE;
                default -> NEUTRAL;
            };
            case LITHO -> switch (defender) {
                case FLORA -> SUPER_EFFECTIVE;
                case PYRO -> NOT_VERY_EFFECTIVE;
                default -> NEUTRAL;
            };
            case NORMAL -> NEUTRAL;
        };
    }
}
