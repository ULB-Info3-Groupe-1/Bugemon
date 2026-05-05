package ulb.models.bugemon.exceptions;

/** Thrown when a Bugemon is created with an invalid number of attacks. */
public class InvalidAttackCountException extends RuntimeException {
    private final int expected;
    private final int actual;

    public InvalidAttackCountException(int expected, int actual) {
        super("Bugemon must have exactly " + expected + " attacks (got " + actual + ")");
        this.expected = expected;
        this.actual = actual;
    }

    public int getExpected() {
        return expected;
    }

    public int getActual() {
        return actual;
    }
}
