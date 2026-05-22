package ulb.models.bugemon.exceptions;

/**
 * Thrown when a {@link ulb.models.bugemon.Bugemon} is constructed with an attack list whose size does not equal the
 * required count ({@link ulb.models.bugemon.Bugemon#ATTACKS_COUNT}).
 */
public class InvalidAttackCountException extends RuntimeException {
    private final int expected;
    private final int actual;

    /**
     * @param expected
     *            the required number of attacks
     * @param actual
     *            the number of attacks supplied by the caller
     */
    public InvalidAttackCountException(int expected, int actual) {
        super("Bugemon must have exactly " + expected + " attacks (got " + actual + ")");
        this.expected = expected;
        this.actual = actual;
    }

    /** Returns the required attack count. */
    public int getExpected() {
        return this.expected;
    }

    /** Returns the attack count that was actually supplied. */
    public int getActual() {
        return this.actual;
    }
}
