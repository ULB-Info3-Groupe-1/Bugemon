package ulb.models.bugemon;

/**
 * Elemental type of a {@link Bugemon} or {@link Attack}, used to compute type-matchup damage multipliers.
 *
 * <p>
 * The matchup matrix is:
 * <ul>
 * <li>FLORA is super-effective against AQUA, not very effective against LITHO</li>
 * <li>AQUA is super-effective against PYRO, not very effective against FLORA</li>
 * <li>PYRO is super-effective against LITHO, not very effective against AQUA</li>
 * <li>LITHO is super-effective against FLORA, not very effective against PYRO</li>
 * <li>NORMAL deals neutral damage to every type</li>
 * </ul>
 * Same-type matchups are always neutral.
 */
public enum ElementType {
    FLORA,
    AQUA,
    PYRO,
    LITHO,
    NORMAL;

    private static final double SUPER_EFFECTIVE = 1.5;
    private static final double NOT_VERY_EFFECTIVE = 0.75;
    private static final double NEUTRAL = 1.0;

    /**
     * Returns the damage multiplier when an attack of this type hits a {@code defender} of the given type.
     *
     * @param defender
     *            the elemental type of the defending Bugemon
     * @return {@code 1.5} for super-effective, {@code 0.75} for not very effective, or {@code 1.0} for neutral
     */
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
