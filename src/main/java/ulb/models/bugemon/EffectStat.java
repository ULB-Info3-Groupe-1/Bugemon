package ulb.models.bugemon;

import com.google.gson.annotations.SerializedName;

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