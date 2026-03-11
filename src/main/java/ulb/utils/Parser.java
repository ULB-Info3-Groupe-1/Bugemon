/**
 * File name : Parser.java
 * Description : Class to parse the json files.
 *
 * @author Rocca Manuel
 * @date 28 feb. 2026
 * @version 1.0
 */

package ulb.utils;

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
import ulb.models.bugemon.effect.EffectType;

/**
 * Provides static utility methods for parsing the JSON data files that
 * describe the Bugemon game's content (attacks and Bugemons).
 *
 * <p>
 * The main entry point is {@link #parse(InputStream, InputStream)}, which
 * reads the attacks file first, builds an ID-to-{@link Attack} map, and then
 * parses the Bugemons file using that map to resolve attack references. Both
 * files are expected to be in the JSON format defined by the game's data
 * schema.
 * </p>
 *
 * <p>
 * Internally, parsing is delegated to two private static helpers —
 * {@link #parseAttacks(java.io.Reader)} and
 * {@link #parseBugemons(java.io.Reader, Map)} — each of which uses a
 * customised {@link com.google.gson.Gson} instance equipped with the
 * appropriate type adapters.
 * </p>
 *
 * @see BugemonDeserializer
 * @see ulb.models.bugemon.Bugemon
 * @see ulb.models.bugemon.Attack
 */
public class Parser {
    /**
     * Immutable value object returned by {@link #parse(InputStream, InputStream)}
     * containing the fully constructed game data loaded from the JSON files.
     *
     * <p>
     * A {@code ParseResult} bundles two collections:
     * <ul>
     *   <li>an {@link Attack} map keyed by attack ID, suitable for fast
     *       look-ups when resolving references; and</li>
     *   <li>a {@link List} of fully built {@link ulb.models.bugemon.Bugemon}
     *       objects, each with its attack list already resolved.</li>
     * </ul>
     * </p>
     *
     * @see Parser#parse(InputStream, InputStream)
     */
    public static class ParseResult {
        private final Map<String, Attack> attacksMap;
        private final List<Bugemon> bugemonList;

        /**
         * Constructs a {@code ParseResult} with the given attacks map and
         * Bugemon list.
         *
         * @param attacksMap  a map of attack IDs to their corresponding
         *                    {@link Attack} objects; must not be {@code null}.
         * @param bugemonList the list of fully constructed
         *                    {@link ulb.models.bugemon.Bugemon} objects; must
         *                    not be {@code null}.
         */
        public ParseResult(Map<String, Attack> attacksMap, List<Bugemon> bugemonList) {
            this.attacksMap = attacksMap;
            this.bugemonList = bugemonList;
        }

        /**
         * Returns the map of all available attacks, keyed by their unique
         * string identifier.
         *
         * @return an unmodifiable view (or the raw map) of attack ID to
         *         {@link Attack}; never {@code null}.
         */
        public Map<String, Attack> getAttacksMap() {
            return attacksMap;
        }

        /**
         * Returns the list of all available {@link ulb.models.bugemon.Bugemon}s
         * loaded from the game data files.
         *
         * @return the list of parsed {@link ulb.models.bugemon.Bugemon} objects;
         *         never {@code null}.
         */
        public List<Bugemon> getBugemonsList() {
            return bugemonList;
        }
    }

    /**
     * The main parsing method used to parse every file. Calls annex methods to
     * achieve its task.
     *
     * @param attacksStream  input stream for the attacks JSON file
     * @param bugemonsStream input stream for the bugemons JSON file
     * @return a ParseResult containing the attacks map and the list of bugemons
     */
    public static ParseResult parse(InputStream attacksStream, InputStream bugemonsStream) {
        Reader attacksReader = new InputStreamReader(attacksStream, StandardCharsets.UTF_8);
        Reader bugemonsReader = new InputStreamReader(bugemonsStream, StandardCharsets.UTF_8);

        // load attacks
        List<Attack> attackList = parseAttacks(attacksReader);

        Map<String, Attack> attacksMap =
                attackList.stream().collect(Collectors.toMap(Attack::getId, Function.identity()));

        // load bugemons
        List<Bugemon> bugemons = parseBugemons(bugemonsReader, attacksMap);

        return new ParseResult(attacksMap, bugemons);
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
    static class TypeDeserializer implements JsonDeserializer<BugemonType> {
        @Override
        public BugemonType deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
                                       JsonDeserializationContext context) {
            String value = json.getAsString();
            return BugemonType.valueOf(value.toUpperCase());
        }
    }

    /**
     * Custom Gson type adapter that deserialises a JSON string into an
     * {@link EffectType} enum constant.
     *
     * <p>
     * The adapter converts the raw JSON string to upper-case before calling
     * {@link EffectType#valueOf(String)}, making the
     * matching case-insensitive (e.g., {@code "stat_modifier"} resolves to
     * {@link EffectType#STAT_MODIFIER}).
     * </p>
     *
     * @see EffectType
     */
    static class EffectTypeDeserializer implements JsonDeserializer<EffectType> {
        @Override
        public EffectType deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
                                      JsonDeserializationContext context) {
            String value = json.getAsString();
            return EffectType.valueOf(value.toUpperCase());
        }
    }

    /**
     * Specific method for the parsing of the Attacks.
     *
     * @param reader reader providing the attacks JSON content
     * @return (AttackList) AttackList containing a List of every Attack in the json
     *         file.
     */
    static List<Attack> parseAttacks(Reader reader) {
        Gson gson = new GsonBuilder()
                            .registerTypeAdapter(BugemonType.class, new TypeDeserializer())
                            .registerTypeAdapter(EffectType.class, new EffectTypeDeserializer())
                            .create();

        try {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray attacksArray = root.getAsJsonArray("attaques");

            Type destType = new TypeToken<List<Attack>>() {}.getType();

            List<Attack> attacks = gson.fromJson(attacksArray, destType);

            reader.close();

            return attacks;
        } catch (Exception e) {
            // TODO: Use of a Logger or external error management ?
            System.out.println("Error when parsing attacks");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Specific method for the parsing of the Bugemons
     *
     * @param reader reader providing the bugemons JSON content
     * @param attackMap (Map<String, Attack>) A map containing every loaded attack
     *                  with their ids. Used to build the bugemons.
     * @return (List<Bugemon>) The list of the newly build Bugemon objects.
     */
    static List<Bugemon> parseBugemons(Reader reader, Map<String, Attack> attackMap) {
        Gson gson = new GsonBuilder()
                            .registerTypeAdapter(Bugemon.class, new BugemonDeserializer(attackMap))
                            .registerTypeAdapter(BugemonType.class, new TypeDeserializer())
                            .create();

        try {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray bugemonsArray = root.getAsJsonArray("bugemons");

            Type destType = new TypeToken<List<Bugemon>>() {}.getType();

            List<Bugemon> bugemons = gson.fromJson(bugemonsArray, destType);

            reader.close();
            return bugemons;
        } catch (Exception e) {
            // TODO: Use of a Logger or external error management ?
            System.out.println("Error when parsing bugemons");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Placeholder for future parsing of in-game objects (items, etc.).
     *
     * @param fileName path to the objects JSON file (not yet used).
     */
    static void parseObjects(Path fileName) {}

    /**
     * Placeholder for future parsing of the skill tree data.
     *
     * @param fileName path to the skill-tree JSON file (not yet used).
     */
    static void parseSkillTree(Path fileName) {}
}
