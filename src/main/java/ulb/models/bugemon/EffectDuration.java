package ulb.models.bugemon;

public enum EffectDuration {
    PERMANENT,
    ONE_TURN;

    public boolean expiresAfterTurn() {
        return this == ONE_TURN;
    }
}
