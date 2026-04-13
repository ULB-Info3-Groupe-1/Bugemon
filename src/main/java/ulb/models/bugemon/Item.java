package ulb.models.bugemon;

import com.google.gson.annotations.SerializedName;

import ulb.models.bugemon.effect.Effect;

public record Item(String id, @SerializedName("nom") String name, String description,
        @SerializedName("categorie") ItemType type, @SerializedName("effet") Effect effect) {
    public enum ItemType {
        @SerializedName("soin")
        HEALING,
        @SerializedName("boost")
        BOOST
    }
}
