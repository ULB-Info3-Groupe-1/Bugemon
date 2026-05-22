package ulb.models.item;

import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import ulb.models.effect.Effect;

public record Item(String id, @SerializedName("nom") String name, String description,
        @SerializedName("categorie") ItemType type, @SerializedName("effet") Effect effect) {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Item other)) {
            return false;
        }
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }
}
