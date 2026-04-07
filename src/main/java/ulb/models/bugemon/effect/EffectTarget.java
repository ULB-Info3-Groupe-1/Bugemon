package ulb.models.bugemon.effect;

import com.google.gson.annotations.SerializedName;

public enum EffectTarget {
    @SerializedName("adversaire")
    ADVERSARY,
    @SerializedName("lanceur")
    THROWER,
    @SerializedName("equipe")
    TEAM,
}
