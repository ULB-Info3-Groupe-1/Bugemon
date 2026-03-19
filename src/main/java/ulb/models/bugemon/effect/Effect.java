package ulb.models.bugemon.effect;

import com.google.gson.annotations.SerializedName;

public record Effect(

        EffectType type,

        @SerializedName("cible") EffectTarget target,

        EffectStat stat,

        @SerializedName("modificateur") int modifier,

        EffectDuration duration

) {}
