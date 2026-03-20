/**
 * File name : Parser.java
 * Description : Class to parse the json files.
 *
 * @author Rocca Manuel
 * @date 28 feb. 2026
 * @version 1.0
 */

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
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
import ulb.models.bugemon.ItemWrapper;
import ulb.models.bugemon.effect.Effect;
import ulb.models.bugemon.effect.EffectDuration;
import ulb.models.bugemon.effect.EffectHeal;
import ulb.models.bugemon.effect.EffectResetMalus;
import ulb.models.bugemon.effect.EffectStat;
import ulb.models.bugemon.effect.EffectStatModifier;
import ulb.models.bugemon.effect.EffectTarget;

/**
 * Provides static utility methods for parsing the JSON data files that
 * describe the Bugemon game's content (attacks and Bugemons).
 *
 * <p>
 * The main entry point is {@link #parse()}, which reads the three bundled JSON
 * resource files (attacks, Bugemons, Items/inventory) in order, building an
 * ID-to-{@link Attack} map first so that Bugemon deserialization can resolve
 * attack references. Parsed data is stored in static fields and exposed via
 * {@link #getBugemons()}, {@link #getAttacks()}, {@link #getItems()}, and
 * {@link #getInventory()}.
 * </p>
 *
 * <p>
 * Internally, parsing is delegated to three private static helpers —
 * {@link #parseAttacks(java.io.Reader)}, {@link #parseBugemons(java.io.Reader)},
 * and {@link #parseItemsAndInventory(java.io.Reader)} — each of which uses a
 * customised {@link com.google.gson.Gson} instance with the appropriate type adapters.
 * </p>
 *
 * @see BugemonDeserializer
 * @see ulb.models.bugemon.Bugemon
 * @see ulb.models.bugemon.Attack
 */
public class Parser {
    private static final Logger LOGGER = Logger.getLogger(Parser.class.getName());

    // Constants for the paths to the JSON data files within the resources directory
    private static final String JSON_ATTACK_PATH = "/json/attaques.json";
    private static final String JSON_BUGEMON_PATH = "/json/bugemons.json";
    private static final String JSON_ITEMS_PATH = "/json/objets.json";

    // Static fields to hold the parsed data, accessible via getter methods
    private static Map<String, Attack> attacks;
    private static List<Bugemon> bugemons;
    private static List<Item> items;
    private static Inventory inventory;

    /**
     * Parses the three bundled JSON resource files (attacks, Bugemons,
     * Items/inventory) and populates the static data fields.
     *
     * <p>
     * Must be called once before any {@code get*()} accessor. Silently returns
     * without populating any data if a resource file cannot be opened.
     * </p>
     */
    public void parse() {
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
            LOGGER.severe("Error loading JSON files: " + e.getMessage());
            return;
        }

        Reader attacksReader = new InputStreamReader(attacksStream, StandardCharsets.UTF_8);
        Reader bugemonsReader = new InputStreamReader(bugemonsStream, StandardCharsets.UTF_8);
        Reader itemsReader = new InputStreamReader(itemsStream, StandardCharsets.UTF_8);
        parseAttacks(attacksReader);
        parseBugemons(bugemonsReader);
        parseItemsAndInventory(itemsReader);
    }

    /**
     * Returns the list of Bugemon Items parsed from the JSON file, where each Bugemon is fully
     * constructed with its associated attacks resolved from the attacks map.
     * @return a list of Bugemon Items representing the parsed Bugemons from the JSON file
     */
    public final List<Bugemon> getBugemons() {
        return bugemons;
    }

    /**
     * Returns the list of {@link Item} instances parsed from the Items JSON file.
     * @return the parsed game Items, or {@code null} if {@link #parse()} has not been called.
     */
    public final List<Item> getItems() {
        return items;
    }

    /**
     * Returns the Inventory object parsed from the JSON file, which contains the initial inventory
     * of the player at the start of the game, with each Item and its corresponding quantity.
     * @return an Inventory object representing the parsed inventory from the JSON file
     */
    public final Inventory getInventory() {
        return inventory;
    }

    /**
     * Returns the map of attacks parsed from the JSON file, where each key is
     * an attack ID and each value is the corresponding {@link Attack} object.
     *
     * @return a map of attack IDs to Attack Items
     */
    public final Map<String, Attack> getAttacks() {
        return attacks;
    }

    /**
     * Custom Gson type adapter that deserialises a JSON string into a
     * {@link BugemonType} enum constant.
     *
     * <p>
     * The adapter converts the raw JSON string to upper-case before calling
     * {@link BugemonType#valueOf(String)}, making the matching
     * case-insensitive with respect to the data file (e.g., {@code "flora"}
     * and {@code "FLORA"} both resolve to {@link BugemonType#FLORA}).
     * </p>
     *
     * @see BugemonType
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
        public Effect deserialize(JsonElement json, Type typeOfT,
                                  JsonDeserializationContext context) {
            JsonObject effectObject = json.getAsJsonObject();
            String effectType = effectObject.get("type").getAsString().toLowerCase();

            EffectTarget target =
                    context.deserialize(effectObject.get("cible"), EffectTarget.class);

            return switch (effectType) {
                case "stat_modifier" ->
                    new EffectStatModifier(
                            target, context.deserialize(effectObject.get("stat"), EffectStat.class),
                            effectObject.get("modificateur").getAsInt(),
                            context.deserialize(effectObject.get("duree"), EffectDuration.class));
                case "soin" -> new EffectHeal(target, effectObject.get("valeur").getAsInt());
                case "reset_malus" -> new EffectResetMalus(target);
                default -> throw new JsonParseException("Unknown effect type: " + effectType);
            };
        }
    }

    /**
     * Parses the attacks JSON file and returns a list of {@link Attack} Items.
     *
     * @param reader reader providing the attacks JSON content
     */
    private static void parseAttacks(Reader reader) {
        Gson gson = new GsonBuilder()
                            .registerTypeAdapter(BugemonType.class, new TypeDeserializer())
                            .registerTypeAdapter(EffectDuration.class, new DurationDeserializer())
                            .registerTypeAdapter(Effect.class, new EffectDeserializer())
                            .create();

        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        try {
            reader.close();
        } catch (IOException e) {
            LOGGER.severe("Error when parsing attacks: " + e.getMessage());
        }

        JsonArray attacksArray = root.getAsJsonArray("attaques");

        Type destType = new TypeToken<List<Attack>>() {}.getType();

        List<Attack> attacksList = gson.fromJson(attacksArray, destType);

        attacks = attacksList.stream().collect(Collectors.toMap(Attack::id, Function.identity()));
    }

    /**
     * Parses the bugemons JSON file and returns a list of fully constructed
     * {@link ulb.models.bugemon.Bugemon} Items.
     *
     * @param reader reader providing the bugemons JSON content
     */
    private static void parseBugemons(Reader reader) {
        Gson gson = new GsonBuilder()
                            .registerTypeAdapter(Bugemon.class, new BugemonDeserializer(attacks))
                            .registerTypeAdapter(BugemonType.class, new TypeDeserializer())
                            .create();

        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        try {
            reader.close();
        } catch (IOException e) {
            LOGGER.severe("Error when parsing bugemons: " + e.getMessage());
        }

        JsonArray bugemonsArray = root.getAsJsonArray("bugemons");

        Type destType = new TypeToken<List<Bugemon>>() {}.getType();

        bugemons = gson.fromJson(bugemonsArray, destType);
    }

    /**
     * Parses the Items JSON file and builds both the list of {@link Item}s
     * and the starting {@link Inventory}.
     *
     * @param reader reader providing the Items JSON content.
     * @return an {@link ItemWrapper} containing the parsed Items and inventory,
     *         or {@code null} if parsing fails.
     */
    static ItemWrapper parseItemsAndInventory(Reader reader) {
        Gson gson = new GsonBuilder()
                            .registerTypeAdapter(EffectDuration.class, new DurationDeserializer())
                            .registerTypeAdapter(Effect.class, new EffectDeserializer())
                            .create();

        try {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            // Extract and parse Items
            JsonArray itemsArray = root.getAsJsonArray("objets");
            Type desType = new TypeToken<List<Item>>() {}.getType();
            List<Item> items = gson.fromJson(itemsArray, desType);

            // Extract and parse inventory
            JsonObject startInventory = root.getAsJsonObject("inventaire_depart");
            Type invType = new TypeToken<Map<String, Integer>>() {}.getType();
            Map<String, Integer> inventoryMap = gson.fromJson(startInventory, invType);

            Inventory inventory = new Inventory();
            for (Map.Entry<String, Integer> entry : inventoryMap.entrySet()) {
                String objectId = entry.getKey();
                int quantity = entry.getValue();

                Item obj = items.stream()
                                   .filter(o -> o.id().equals(objectId))
                                   .findFirst()
                                   .orElseThrow(()
                                                        -> new RuntimeException("Object with ID "
                                                                                + objectId
                                                                                + " not found"));
                inventory.addItem(obj, quantity);
            }

            reader.close();
            return new ItemWrapper(items, inventory);
        } catch (Exception e) {
            LOGGER.severe("Error when parsing Items and inventory: " + e.getMessage());
        }
        return null;
    }
}
