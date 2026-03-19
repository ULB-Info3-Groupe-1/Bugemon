package ulb.models.bugemon;

import javafx.scene.effect.Effect;
import ulb.models.bugemon.Item.ItemType;

/*
public record Item(String id, @SerializedName("nom") String name, String description,
                         @SerializedName("categorie") ItemType type,
                         @SerializedName("effet") Effect effect, String sprite) {
    public enum ItemType { @SerializedName("soin") HEALING, @SerializedName("boost") BOOST }
}
*/

public final class ItemBuilder {
    private static final String DEFAULT_ID = "default_item_id";
    private static final String DEFAULT_NAME = "default item name";
    private static final String DEFAULT_DESCRIPTION = "default item description";
    private static final ItemType DEFAULT_TYPE = ItemType.BOOST;

    private String id = DEFAULT_ID;
    private String name = DEFAULT_NAME;
    private String description = DEFAULT_DESCRIPTION;
    private ItemType type = DEFAULT_TYPE;
}
