package ulb.utils;

import ulb.models.bugemon.*;
import java.lang.reflect.Type;
import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class BugemonDeserializer implements JsonDeserializer<Bugemon> {

    private final Map<String, Attack> attacksMap;

    public BugemonDeserializer(Map<String, Attack> attacksMap) {
        this.attacksMap = attacksMap;
    }

    @Override
    public Bugemon deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject obj = json.getAsJsonObject();

        String id = obj.get("id").getAsString();
        String name = obj.get("nom").getAsString();
        ulb.models.bugemon.Type type = context.deserialize(obj.get("type"), ulb.models.bugemon.Type.class);
        String sprite = obj.get("sprite").getAsString();
        boolean starter = obj.get("starter").getAsBoolean();

        Stats stats = context.deserialize(obj.get("stats"), Stats.class);

        List<Attack> attacks = new ArrayList<>();
        JsonArray ids = obj.getAsJsonArray("attaques");

        for (JsonElement e : ids) {
            String attackId = e.getAsString();
            Attack attack = this.attacksMap.get(attackId);

            if (attack != null) {
                attacks.add(attack);
            }
        }

        AttackList attackList = new AttackList(attacks);
        return new Bugemon(id, name, type, sprite, stats, attackList, starter);
    }
}
