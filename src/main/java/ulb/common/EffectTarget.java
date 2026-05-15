package ulb.common;

import com.google.gson.annotations.SerializedName;

public enum EffectTarget {
    @SerializedName("adversaire")
    OPPONENT,
    @SerializedName("lanceur")
    THROWER,
    @SerializedName("equipe")
    TEAM,
}
