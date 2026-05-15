package ulb.common;

import com.google.gson.annotations.SerializedName;

public enum StatType {
    @SerializedName("pv")
    HP,
    @SerializedName("attaque")
    ATTACK,
    @SerializedName("defense")
    DEFENSE,
    @SerializedName("initiative")
    INITIATIVE,
}
