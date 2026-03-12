package ulb.models.bugemon;

import com.google.gson.annotations.SerializedName;

public record GameObject(String id, @SerializedName("nom") String name, String description,
                         @SerializedName("categorie") OType type,
                         @SerializedName("effet") Effect effect, String sprite) {
    public enum OType { @SerializedName("soin") HEALING, @SerializedName("boost") BOOST }
}