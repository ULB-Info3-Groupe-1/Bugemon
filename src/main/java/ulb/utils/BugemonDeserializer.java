package ulb.utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonBuilder;
import ulb.models.bugemon.BugemonType;

/**
 * Custom JSON deserializer for {@link Bugemon} objects.
 * <p>
 * This deserializer reads a JSON representation of a Bugemon and constructs a fully populated
 * {@link Bugemon} instance, resolving attack references from a pre-loaded map of {@link Attack}
 * objects.
 * </p>
 */
public class BugemonDeserializer implements JsonDeserializer<Bugemon> {
    /**
     * A map of attack IDs to their corresponding {@link Attack} objects, used to resolve attack
     * references during deserialization.
     */
    private final Map<String, Attack> attacksMap;

    /**
     * Constructs a new {@code BugemonDeserializer} with the given map of attacks.
     *
     * @param attacksMap
     *            a map of attack IDs to {@link Attack} objects used to resolve attack references in
     *            the Bugemon JSON data
     */
    public BugemonDeserializer(Map<String, Attack> attacksMap) {
        this.attacksMap = attacksMap;
    }

    /**
     * Deserializes a JSON element into a {@link Bugemon} object.
     * <p>
     * The JSON object is expected to contain the following fields:
     * <ul>
     * <li>{@code id} - the unique identifier of the Bugemon</li>
     * <li>{@code nom} - the name of the Bugemon</li>
     * <li>{@code type} - the type of the Bugemon, deserialized as {@link BugemonType}</li>
     * <li>{@code sprite} - the path to the sprite image; if not prefixed with {@code "png/"}, the
     * prefix is added automatically</li>
     * <li>{@code starter} - whether the Bugemon is a starter Bugemon</li>
     * <li>{@code stats} - a JSON object containing stat key-value pairs (e.g., hp, attack, defense,
     * initiative)</li>
     * <li>{@code attaques} - a JSON array of attack IDs referencing entries in the attacks map</li>
     * </ul>
     *
     * @param json
     *            the JSON element to deserialize
     * @param typeOfT
     *            the type of the object to deserialize into
     * @param context
     *            the deserialization context
     * @return a fully constructed {@link Bugemon} instance
     * @throws JsonParseException
     *             if the JSON is not in the expected format
     */
    @Override
    public Bugemon deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        String id = obj.get("id").getAsString();
        String name = obj.get("nom").getAsString();
        BugemonType type = context.deserialize(obj.get("type"), BugemonType.class);
        String sprite = obj.get("sprite").getAsString();
        boolean starter = obj.get("starter").getAsBoolean();

        if (sprite != null && !sprite.startsWith("png/")) {
            sprite = "png/" + sprite;
        }

        JsonObject statsObj = obj.getAsJsonObject("stats");
        Map<String, Integer> statsMap = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : statsObj.entrySet()) {
            statsMap.put(entry.getKey(), entry.getValue().getAsInt());
        }

        List<Attack> attackList = new ArrayList<>();
        JsonArray idsAttacks = obj.getAsJsonArray("attaques");

        for (JsonElement e : idsAttacks) {
            String attackId = e.getAsString();
            Attack attack = this.attacksMap.get(attackId);

            if (attack != null) {
                attackList.add(attack);
            }
        }

        return new BugemonBuilder().id(id).name(name).type(type).sprite(sprite)
                .hp(statsMap.get("pv")).attack(statsMap.get("attaque"))
                .defense(statsMap.get("defense")).initiative(statsMap.get("initiative"))
                .attackList(attackList).isStarter(starter).build();
    }
}
