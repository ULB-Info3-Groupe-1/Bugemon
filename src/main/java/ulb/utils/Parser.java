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
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.GameObject;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.ObjectWrapper;
import ulb.models.bugemon.effect.EffectType;

/**
 * Provides static utility methods for parsing the JSON data files that
 * describe the Bugemon game's content (attacks and Bugemons).
 *
 * <p>
 * The main entry point is {@link #parse()}, which reads the three bundled JSON
 * resource files (attacks, Bugemons, objects/inventory) in order, building an
 * ID-to-{@link Attack} map first so that Bugemon deserialization can resolve
 * attack references. Parsed data is stored in static fields and exposed via
 * {@link #getBugemons()}, {@link #getAttacks()}, {@link #getObjects()}, and
 * {@link #getInventory()}.
 * </p>
 *
 * <p>
 * Internally, parsing is delegated to three private static helpers —
 * {@link #parseAttacks(java.io.Reader)}, {@link #parseBugemons(java.io.Reader)},
 * and {@link #parseObjectsAndInventory(java.io.Reader)} — each of which uses a
 * customised {@link com.google.gson.Gson} instance with the appropriate type adapters.
 * </p>
 *
 * @see BugemonDeserializer
 * @see ulb.models.bugemon.Bugemon
 * @see ulb.models.bugemon.Attack
 */
public class Parser {
    // Constants for the paths to the JSON data files within the resources directory
    private static final String JSON_ATTACK_PATH = "/json/attaques.json";
    private static final String JSON_BUGEMON_PATH = "/json/bugemons.json";
    private static final String JSON_OBJECTS_PATH = "/json/objets.json";

    // Static fields to hold the parsed data, accessible via getter methods
    private static Map<String, Attack> attacks;
    private static List<Bugemon> bugemons;
    private static List<GameObject> objects;
    private static Inventory inventory;

    // Singleton instance of the Parser class
    private static Parser instance;

    /**
     * Private constructor to prevent instantiation of the Parser class, enforcing the singleton
     * pattern.
     */
    private Parser() {}

    /**
     * Returns the singleton instance of the Parser class, creating it if it does not already exist.
     * This method is thread-safe to ensure that only one instance of the Parser is created even in
     * a multi-threaded environment.
     * @return the singleton instance of the Parser class
     */
    public static synchronized Parser getInstance() {
        if (instance == null) {
            instance = new Parser();
        }
        return instance;
    }

    /**
     * Parses the three bundled JSON resource files (attacks, Bugemons,
     * objects/inventory) and populates the static data fields.
     *
     * <p>
     * Must be called once before any {@code get*()} accessor. Silently returns
     * without populating any data if a resource file cannot be opened.
     * </p>
     */
    public void parse() {
        InputStream attacksStream;
        InputStream bugemonsStream;
        InputStream objectsStream;
        try {
            attacksStream = getClass().getResourceAsStream(JSON_ATTACK_PATH);
            bugemonsStream = getClass().getResourceAsStream(JSON_BUGEMON_PATH);
            objectsStream = getClass().getResourceAsStream(JSON_OBJECTS_PATH);
            if (attacksStream == null || bugemonsStream == null || objectsStream == null) {
                throw new IOException("JSON files not found in resources: ");
            }
        } catch (IOException e) {
            // TODO: handle callback
            return;
        }

        Reader attacksReader = new InputStreamReader(attacksStream, StandardCharsets.UTF_8);
        Reader bugemonsReader = new InputStreamReader(bugemonsStream, StandardCharsets.UTF_8);
        Reader objectsReader = new InputStreamReader(objectsStream, StandardCharsets.UTF_8);
        parseAttacks(attacksReader);
        parseBugemons(bugemonsReader);
        parseObjectsAndInventory(objectsReader);
    }

    /**
     * Returns the list of Bugemon objects parsed from the JSON file, where each Bugemon is fully
     * constructed with its associated attacks resolved from the attacks map.
     * @return a list of Bugemon objects representing the parsed Bugemons from the JSON file
     */
    public final List<Bugemon> getBugemons() {
        return bugemons;
    }

    /**
     * Returns the list of {@link GameObject} instances parsed from the objects JSON file.
     * @return the parsed game objects, or {@code null} if {@link #parse()} has not been called.
     */
    public final List<GameObject> getObjects() {
        return objects;
    }

    /**
     * Returns the Inventory object parsed from the JSON file, which contains the initial inventory
     * of the player at the start of the game, with each GameObject and its corresponding quantity.
     * @return an Inventory object representing the parsed inventory from the JSON file
     */
    public final Inventory getInventory() {
        return inventory;
    }

    /**
     * Returns the map of attacks parsed from the JSON file, where each key is
     * an attack ID and each value is the corresponding {@link Attack} object.
     *
     * @return a map of attack IDs to Attack objects
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

    /**
     * Parses the attacks JSON file and returns a list of {@link Attack} objects.
     *
     * @param reader reader providing the attacks JSON content
     */
    private static void parseAttacks(Reader reader) {
        Gson gson = new GsonBuilder()
                            .registerTypeAdapter(BugemonType.class, new TypeDeserializer())
                            .create();

        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        try {
            reader.close();
        } catch (IOException e) {
            System.err.println("Error when parsing attacks");
        }

        JsonArray attacksArray = root.getAsJsonArray("attaques");

        Type destType = new TypeToken<List<Attack>>() {}.getType();

        List<Attack> attacksList = gson.fromJson(attacksArray, destType);

        attacks = attacksList.stream().collect(Collectors.toMap(Attack::id, Function.identity()));
    }

    /**
     * Parses the bugemons JSON file and returns a list of fully constructed
     * {@link ulb.models.bugemon.Bugemon} objects.
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
            System.err.println("Error when parsing bugemons");
        }

        JsonArray bugemonsArray = root.getAsJsonArray("bugemons");

        Type destType = new TypeToken<List<Bugemon>>() {}.getType();

        bugemons = gson.fromJson(bugemonsArray, destType);
    }

    /**
     * Parses the objects JSON file and builds both the list of {@link GameObject}s
     * and the starting {@link Inventory}.
     *
     * @param reader reader providing the objects JSON content.
     * @return an {@link ObjectWrapper} containing the parsed objects and inventory,
     *         or {@code null} if parsing fails.
     */
    static ObjectWrapper parseObjectsAndInventory(Reader reader) {
        Gson gson = new GsonBuilder().create();

        try {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            // Extract and parse objects
            JsonArray objectsArray = root.getAsJsonArray("objets");
            Type desType = new TypeToken<List<GameObject>>() {}.getType();
            List<GameObject> objects = gson.fromJson(objectsArray, desType);

            // Extract and parse inventory
            JsonObject startInventory = root.getAsJsonObject("inventaire_depart");
            Type invType = new TypeToken<Map<String, Integer>>() {}.getType();
            Map<String, Integer> inventoryMap = gson.fromJson(startInventory, invType);

            Inventory inventory = new Inventory();
            for (Map.Entry<String, Integer> entry : inventoryMap.entrySet()) {
                String objectId = entry.getKey();
                int quantity = entry.getValue();

                GameObject obj = objects.stream()
                                         .filter(o -> o.id().equals(objectId))
                                         .findFirst()
                                         .orElseThrow(()
                                                              -> new RuntimeException(
                                                                      "Object with ID " + objectId
                                                                      + " not found"));
                inventory.addObject(obj, quantity);
            }

            reader.close();
            return new ObjectWrapper(objects, inventory);

        } catch (Exception e) {
            // TODO: Use of a Logger or external error management ?
            System.out.println("Error when parsing objects and inventory");
            e.printStackTrace();
        }
        return null;
    }

    /** Placeholder for future skill-tree parsing. Currently a no-op. */
    static void parseSkillTree(Path fileName) {}
}
