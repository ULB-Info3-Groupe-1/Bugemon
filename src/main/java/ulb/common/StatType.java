package ulb.common;

import com.google.gson.annotations.SerializedName;

/**
 * Combat statistic that an item effect or skill bonus can modify on a Bugemon.
 *
 * <p>
 * The JSON serialisation names ({@code "pv"}, {@code "attaque"}, {@code "defense"}, {@code "initiative"}) come from the
 * static resource files and are mapped via Gson {@code @SerializedName}.
 */
public enum StatType {
    /** Hit points — the Bugemon's total health pool. */
    @SerializedName("pv")
    HP,
    /** Attack power — determines damage dealt to the opponent. */
    @SerializedName("attaque")
    ATTACK,
    /** Defense — reduces incoming damage. */
    @SerializedName("defense")
    DEFENSE,
    /** Initiative — determines turn order in combat. */
    @SerializedName("initiative")
    INITIATIVE,
}
