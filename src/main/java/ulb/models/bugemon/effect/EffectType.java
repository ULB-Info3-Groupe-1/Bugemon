package ulb.models.bugemon.effect;

import com.google.gson.annotations.SerializedName;

public enum EffectType {
    @SerializedName("stat_modifier") STAT_MODIFIER,
    @SerializedName("reset_malus") RESET_PENALTY,
    @SerializedName("soin") HEAL,
}
