package ulb.models.item;

import com.google.gson.annotations.SerializedName;

import ulb.models.effect.Effect;

public record Item(String id, @SerializedName("nom") String name, String description,
        @SerializedName("categorie") ItemType type, @SerializedName("effet") Effect effect) {
}
