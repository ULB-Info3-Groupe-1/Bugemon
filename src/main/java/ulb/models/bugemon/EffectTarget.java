package ulb.models.bugemon;

import com.google.gson.annotations.SerializedName;

/**
 * Enum representing the possible targets of an effect.
 */
public enum EffectTarget {
    @SerializedName("adversaire")
    ADVERSARY,

    @SerializedName("lanceur")
    THROWER,

    @SerializedName("equipe")
    TEAM,

    NONE,
}
