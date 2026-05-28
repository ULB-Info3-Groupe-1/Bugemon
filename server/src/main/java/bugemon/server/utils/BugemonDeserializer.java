package bugemon.server.utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import bugemon.common.models.bugemon.Attack;
import bugemon.common.models.bugemon.Bugemon;
import bugemon.common.models.bugemon.ElementType;

/**
 * Custom Gson deserializer for {@link Bugemon}; resolves attack IDs via a pre-loaded map. Validates that the sprite
 * resource exists at parse time.
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
        String name = obj.get("nom").getAsString();
        ElementType type = context.deserialize(obj.get("type"), ElementType.class);
        String sprite = obj.get("sprite").getAsString();
        boolean starter = obj.get("starter").getAsBoolean();

        Map<String, Integer> statsMap = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : statsObj.entrySet()) {
            statsMap.put(entry.getKey(), entry.getValue().getAsInt());
        }

        List<Attack> attackList = new ArrayList<>();
        for (JsonElement e : obj.getAsJsonArray("attaques")) {
            String attackId = e.getAsString();
            Attack attack = this.attacksMap.get(attackId);
            if (attack == null) {
                throw new JsonParseException("Attack not found: " + attackId);
            }
            attackList.add(attack);
        }

        return new Bugemon(name, statsMap.get("pv"), statsMap.get("attaque"), statsMap.get("defense"),
                statsMap.get("initiative"), type, attackList, sprite, starter,
                name.equals(bugemon.common.Configuration.Game.BOSS_NAME));
    }
}
