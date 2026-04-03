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
 * Custom Gson deserializer for {@link Bugemon}; resolves attack IDs via a pre-loaded map. Sprite paths are normalised
 * to {@code "png/<name>"} if the prefix is absent.
 */
public class BugemonDeserializer implements JsonDeserializer<Bugemon> {
    private final Map<String, Attack> attacksMap;

    public BugemonDeserializer(Map<String, Attack> attacksMap) {
        this.attacksMap = attacksMap;
    }

    @Override
    public Bugemon deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        JsonObject statsObj = obj.getAsJsonObject("stats");
        String id = obj.get("id").getAsString();
        String name = obj.get("nom").getAsString();
        BugemonType type = context.deserialize(obj.get("type"), BugemonType.class);
        String sprite = obj.get("sprite").getAsString();
        boolean starter = obj.get("starter").getAsBoolean();

        if (sprite != null && !sprite.startsWith("png/")) {
            sprite = "png/" + sprite;
        }
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

        return new BugemonBuilder().id(id).name(name).type(type).sprite(sprite).hp(statsMap.get("pv"))
                .attack(statsMap.get("attaque")).defense(statsMap.get("defense")).initiative(statsMap.get("initiative"))
                .attackList(attackList).isStarter(starter).build();
    }
}
