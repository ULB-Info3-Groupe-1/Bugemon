package ulb.models.trainer;

/**
 * Alpha-beta MiniMax used by {@link AutoTrainer}. The search explores
 * attack/switch/item actions and simulates turns
 * deterministically using existing combat formulas.
 */
public class MiniMax {
    private static final int WIN_SCORE = 1_000_000; // Arbitrary large score to represent a guaranteed win
    private final int maxDepth;

    public MiniMax(int maxDepth) {
        if (maxDepth <= 0) {
            throw new IllegalArgumentException("maxDepth must be > 0");
        }
        this.maxDepth = maxDepth;
    }
}
