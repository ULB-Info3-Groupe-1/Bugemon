package ulb.models.item;

import com.google.gson.annotations.SerializedName;

public enum ItemType {
    @SerializedName("soin")
    HEALING,
    @SerializedName("boost")
    BOOST
}
