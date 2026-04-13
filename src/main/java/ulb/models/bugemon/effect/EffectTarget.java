package ulb.models.bugemon.effect;

import com.google.gson.annotations.SerializedName;

/** Which Bugemon(s) an attack effect is applied to. */
public enum EffectTarget {
    @SerializedName("adversaire")
    OPPONENT,
    @SerializedName("lanceur")
    THROWER,
    @SerializedName("equipe")
    TEAM,
}
