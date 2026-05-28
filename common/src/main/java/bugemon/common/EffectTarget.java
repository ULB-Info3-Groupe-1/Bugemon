package bugemon.common;

import com.google.gson.annotations.SerializedName;

/**
 * Recipient of an item or attack effect during combat.
 *
 * <p>
 * The JSON serialisation names ({@code "adversaire"}, {@code "lanceur"}, {@code "equipe"}) are defined in the static
 * resource files and mapped via Gson {@code @SerializedName}.
 */
public enum EffectTarget {
    /** The opposing Bugemon receives the effect. */
    @SerializedName("adversaire")
    OPPONENT,
    /** The Bugemon that used the attack or item receives the effect. */
    @SerializedName("lanceur")
    THROWER,
    /** Every member of the player's active team receives the effect. */
    @SerializedName("equipe")
    TEAM,
}
