package ulb.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.models.bugemon.effect.Effect;
import ulb.models.bugemon.effect.EffectDuration;
import ulb.models.bugemon.effect.EffectHeal;
import ulb.models.bugemon.effect.EffectResetMalus;
import ulb.models.bugemon.effect.EffectStat;
import ulb.models.bugemon.effect.EffectStatModifier;
import ulb.models.bugemon.effect.EffectTarget;
import ulb.repositories.dto.CreateBugemonDTO;

/**
 * Parses the three bundled JSON resource files (attacks, Bugemons, items/inventory). The main entry point is
 * {@link #parse()}, which builds an ID-to-{@link Attack} map first so that Bugemon deserialisation can resolve attack
 * references. Results are exposed via {@link #getBugemons()}, {@link #getAttacks()}, {@link #getItems()}, and
 * {@link #getInventory()}.
 *
 * @see BugemonDeserializer
 */
public class Parser {
    private static final Logger LOG = LoggerFactory.getLogger(Parser.class);

    // Constants for the paths to the JSON data files within the resources directory
    private static final String JSON_ATTACK_PATH = "/json/attaques.json";
    private static final String JSON_BUGEMON_PATH = "/json/bugemons.json";
    private static final String JSON_ITEMS_PATH = "/json/objets.json";

    // Static fields to hold the parsed data, accessible via getter methods
    private static Map<String, Attack> attacks;
    private static List<CreateBugemonDTO> bugemons;
    private static List<Item> items;
    private static Inventory inventory;

    /**
     * Parses all JSON resource files and populates the static data fields. Must be called once before any
     * {@code get*()} accessor. Silently returns without populating any data if a resource file cannot be opened.
     */
    public void parse() {
        LOG.info("Parsing data");

        InputStream attacksStream;
        InputStream bugemonsStream;
        InputStream itemsStream;
        try {
            attacksStream = getClass().getResourceAsStream(JSON_ATTACK_PATH);
            bugemonsStream = getClass().getResourceAsStream(JSON_BUGEMON_PATH);
            itemsStream = getClass().getResourceAsStream(JSON_ITEMS_PATH);
            if (attacksStream == null || bugemonsStream == null || itemsStream == null) {
                throw new IOException("JSON files not found in resources: ");
            }
        } catch (IOException e) {
            LOG.error("Error loading JSON files: {}", e.getMessage());
            return;
        }

        Reader attacksReader = new InputStreamReader(attacksStream, StandardCharsets.UTF_8);
        Reader bugemonsReader = new InputStreamReader(bugemonsStream, StandardCharsets.UTF_8);
        Reader itemsReader = new InputStreamReader(itemsStream, StandardCharsets.UTF_8);
        parseAttacks(attacksReader);
        parseBugemons(bugemonsReader);
        parseItemsAndInventory(itemsReader);

        LOG.info("Finished parsing data");
    }

    public final List<CreateBugemonDTO> getBugemons() {
        return bugemons;
    }

    public final List<Item> getItems() {
        return items;
    }

    public final Inventory getInventory() {
        return inventory;
    }

    public final Map<String, Attack> getAttacks() {
        return attacks;
    }

    /**
     * Custom Gson type adapter that deserialises a JSON string into a {@link BugemonType} enum constant. Converts the
     * raw value to upper-case before calling {@link BugemonType#valueOf(String)}, so {@code "flora"} and
     * {@code "FLORA"} both resolve to {@link BugemonType#FLORA}.
     */
    private static class TypeDeserializer implements JsonDeserializer<BugemonType> {
        @Override
        public BugemonType deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
                JsonDeserializationContext context) {
            String value = json.getAsString();
            return BugemonType.valueOf(value.toUpperCase());
        }
    }

    private static class DurationDeserializer implements JsonDeserializer<EffectDuration> {
        @Override
        public EffectDuration deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
                JsonDeserializationContext context) {
            String value = json.getAsString().toLowerCase().trim();
            if ("permanent".equals(value)) {
                return EffectDuration.PERMANENT;
            }
            return EffectDuration.ONE_TURN;
        }
    }

    public static class EffectDeserializer implements JsonDeserializer<Effect> {
        @Override
        public Effect deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            JsonObject effectObject = json.getAsJsonObject();
            String effectType = effectObject.get("type").getAsString().toLowerCase();

            EffectTarget target = context.deserialize(effectObject.get("cible"), EffectTarget.class);

            return switch (effectType) {
                case "stat_modifier" ->
                    new EffectStatModifier(target, context.deserialize(effectObject.get("stat"), EffectStat.class),
                            effectObject.get("modificateur").getAsInt(),
                            context.deserialize(effectObject.get("duree"), EffectDuration.class));
                case "soin" -> new EffectHeal(target, effectObject.get("valeur").getAsInt());
                case "reset_malus" -> new EffectResetMalus(target);
                default -> throw new JsonParseException("Unknown effect type: " + effectType);
            };
        }
    }

    private static void parseAttacks(Reader reader) {
        LOG.debug("Parsing Attacks");
        Gson gson = new GsonBuilder().registerTypeAdapter(BugemonType.class, new TypeDeserializer())
                .registerTypeAdapter(EffectDuration.class, new DurationDeserializer())
                .registerTypeAdapter(Effect.class, new EffectDeserializer()).create();

        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        try {
            reader.close();
        } catch (IOException e) {
            LOG.error("Error when parsing attacks: {}", e.getMessage());
        }

        JsonArray attacksArray = root.getAsJsonArray("attaques");

        Type destType = new TypeToken<List<Attack>>() {
        }.getType();

        List<Attack> attacksList = gson.fromJson(attacksArray, destType);

        attacks = attacksList.stream().collect(Collectors.toMap(Attack::id, Function.identity()));
    }

    private static void parseBugemons(Reader reader) {
        LOG.debug("Parsing Bugemon");
        Gson gson = new GsonBuilder().registerTypeAdapter(Bugemon.class, new BugemonDeserializer(attacks))
                .registerTypeAdapter(BugemonType.class, new TypeDeserializer()).create();

        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        try {
            reader.close();
        } catch (IOException e) {
            LOG.error("Error when parsing bugemons:  {}", e.getMessage());
        }

        JsonArray bugemonsArray = root.getAsJsonArray("bugemons");

        Type destType = new TypeToken<List<Bugemon>>() {
        }.getType();

        bugemons = gson.fromJson(bugemonsArray, destType);
    }

    static void parseItemsAndInventory(Reader reader) {
        LOG.debug("Parsing Items and inventory");
        Gson gson = new GsonBuilder().registerTypeAdapter(EffectDuration.class, new DurationDeserializer())
                .registerTypeAdapter(Effect.class, new EffectDeserializer()).create();

        try {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            // Extract and parse Items
            JsonArray itemsArray = root.getAsJsonArray("objets");
            Type desType = new TypeToken<List<Item>>() {
            }.getType();
            items = gson.fromJson(itemsArray, desType);

            // Extract and parse inventory
            JsonObject startInventory = root.getAsJsonObject("inventaire_depart");
            Type invType = new TypeToken<Map<String, Integer>>() {
            }.getType();
            Map<String, Integer> inventoryMap = gson.fromJson(startInventory, invType);

            inventory = new Inventory();
            for (Map.Entry<String, Integer> entry : inventoryMap.entrySet()) {
                String objectId = entry.getKey();
                int quantity = entry.getValue();

                Item obj = items.stream().filter(o -> o.id().equals(objectId)).findFirst()
                        .orElseThrow(() -> new RuntimeException("Object with ID " + objectId + " not found"));
                inventory.addItem(obj, quantity);
            }
            reader.close();
        } catch (Exception e) {
            LOG.error("Error when parsing Items and inventory: {}", e.getMessage());
        }
    }
}
