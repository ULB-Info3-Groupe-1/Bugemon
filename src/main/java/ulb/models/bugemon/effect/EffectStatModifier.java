package ulb.models.bugemon.effect;

import java.util.Locale;

public record EffectStatModifier(EffectTarget target, EffectStat stat, int modifier,
                                 String duration) implements Effect {
    /**
     * Parses and returns the numeric part of the duration string.
     *
     * <p>
     * The duration is expected to be encoded as {@code "<n>_<unit>"}
     * (e.g., {@code "2_turns"}). This method splits the string on the first
     * {@code '_'} character and parses the leading segment as an integer.
     * </p>
     *
     * @return the number of turns (or other unit) this effect lasts.
     * @throws IllegalArgumentException if the duration string is {@code null}
     *         or does not contain an {@code '_'} separator.
     * @throws NumberFormatException if the part before {@code '_'} cannot be
     *         parsed as an integer.
     */
    public int extractDuration() {
        if (this.duration == null) {
            throw new IllegalArgumentException("Duration cannot be null");
        }

        String normalized = this.duration.trim().toLowerCase(Locale.ROOT);

        if ("permanent".equals(normalized)) {
            return -1; // infinite duration
        }

        if (!normalized.contains("_")) {
            throw new IllegalArgumentException("Invalid duration format: " + this.duration);
        }

        String numberPart = normalized.split("_", 2)[0];
        return Integer.parseInt(numberPart);
    }
}