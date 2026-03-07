package ulb.models.bugemon;

import com.google.gson.annotations.SerializedName;

public enum EffectTarget {
    @SerializedName("adversaire")
    ADVERSARY,

    @SerializedName("lanceur")
    THROWER,

    @SerializedName("equipe")
    TEAM,

    NONE,
}
