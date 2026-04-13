package ulb.models.bugemon.effect;

import com.google.gson.annotations.SerializedName;

/** The stat targeted by an {@link EffectStatModifier}. */
public enum EffectStat {
    @SerializedName("pv")
    HP,
    @SerializedName("attaque")
    ATTACK,
    @SerializedName("defense")
    DEFENSE,
    @SerializedName("initiative")
    INITIATIVE,
}
