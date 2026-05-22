package ulb.models.item;

import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import ulb.models.effect.Effect;

/**
 * Immutable value object representing an item the player can carry and use.
 *
 * <p>
 * Equality and hashing are based solely on {@code id}, so two {@code Item} instances with the same identifier are
 * considered the same item regardless of other fields.
 *
 * <p>
 * Gson field aliases map JSON keys {@code "nom"}, {@code "categorie"}, and {@code "effet"} to the corresponding Java
 * fields.
 */
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
