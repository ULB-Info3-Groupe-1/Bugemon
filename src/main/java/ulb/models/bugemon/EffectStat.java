package ulb.models.bugemon;

import com.google.gson.annotations.SerializedName;

/**
 * Enum representing the stats that can be affected by an effect.
 */
public enum EffectStat {
    @SerializedName("pv")
    HP,
    
    @SerializedName("attaque")
    ATTACK,

    @SerializedName("defense")
    DEFENSE,

    @SerializedName("initiative")
    INITIATIVE,
};