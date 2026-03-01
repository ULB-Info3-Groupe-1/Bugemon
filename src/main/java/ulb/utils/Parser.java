/**
 * File name : Parser.java
 * Description : Class to parse the json files.
 * 
 * @author Rocca Manuel
 * @date 28 feb. 2026
 * @version 1.0
 */

package ulb.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.AttackList;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.EffectType;

/**
 * Class with static methods to parse the json files containing the data about
 * the Bugemon game.
 */
public class Parser {

     /**
     * Class representing the result of the parsing to get the list of the available bugemons and the list of the attacks
     */
    public static class ParseResult {

        private final Map<String, Attack> attacksMap;
        private final List<Bugemon> bugemonList;

        public ParseResult(Map<String, Attack> attacksMap, List<Bugemon> bugemonList) {
            this.attacksMap = attacksMap;
            this.bugemonList = bugemonList;
        }

        /**
         * Return the list of the available attacks
         * @return Map<String, Attack> Map String and Attack class for
         */
        public Map<String, Attack> getAttacksMap() {
            return attacksMap;
        }

        /**
         * Return the list of the available bugemons
         * @return List<Bugemon> The list of the available bugemons
         */
        public List<Bugemon> getBugemonsList() {
            return bugemonList;
        }
    }

    /**
     * The main parsing method used to parse every file. Calls annex methods to
     * achieve its task.
     *
     * @param directory The directory containing the json files.
     *                  The path must be relative to the place of executions.
     */
    public static ParseResult parse(String directory) {
        Path dirPath = Paths.get(directory);

        // load attacks
        Path attacksPath = dirPath.resolve("attaques.json");
        AttackList attackList = parseAttacks(attacksPath);

        List<Attack> attacks = attackList.getAttacks();
        Map<String, Attack> attacksMap = new HashMap<>();

        for (Attack a : attacks) {
            attacksMap.put(a.getId(), a);
        }

        // load bugemons
        Path bugemonPath = dirPath.resolve("bugemons.json");
        List<Bugemon> bugemons = parseBugemons(bugemonPath, attacksMap);

        return new ParseResult(attacksMap, bugemons);
    }

    /**
     * Implements a custom deserializer for the Bugemon class. Specific to the Gson
     * library.
     */
    static class TypeDeserializer implements JsonDeserializer<Bugemon.BType> {

        @Override
        public Bugemon.BType deserialize(
                JsonElement json,
                java.lang.reflect.Type typeOfT,
                JsonDeserializationContext context) {
            String value = json.getAsString();
            return Bugemon.BType.valueOf(value.toUpperCase());
        }
    }

    /**
     * Implements a custom deserializer for the Effect class. Specific to the Gson
     * library.
     */
    static class EffectTypeDeserializer
            implements JsonDeserializer<ulb.models.bugemon.EffectType> {

        @Override
        public ulb.models.bugemon.EffectType deserialize(
                JsonElement json,
                java.lang.reflect.Type typeOfT,
                JsonDeserializationContext context) {
            String value = json.getAsString();
            return ulb.models.bugemon.EffectType.valueOf(value.toUpperCase());
        }
    }

    /**
     * Specific method for the parsing of the Attacks.
     * 
     * @param fileName The path to the json file.
     * @return (AttackList) AttackList containing a List of every Attack in the json
     *         file.
     */
    static AttackList parseAttacks(Path fileName) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Bugemon.BType.class, new TypeDeserializer())
                .registerTypeAdapter(EffectType.class, new EffectTypeDeserializer())
                .create();

        try (FileReader reader = new FileReader(fileName.toFile())) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray attacksArray = root.getAsJsonArray("attaques");

            Type destType = new TypeToken<List<Attack>>() {}.getType();

            List<Attack> attacks = gson.fromJson(attacksArray, destType);

            reader.close();

            return new AttackList(attacks);
        } catch (Exception e) {
            // TODO: Use of a Logger or external error management ?
            String errorMessage = "Error when trying to open %s";
            String errorOutput = String.format(errorMessage, fileName);
            System.out.println(errorOutput);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Specific method for the parsing of the Bugemons
     * 
     * @param fileName  The path to the json file.
     * @param attackMap (Map<String, Attack>) A map containing every loaded attack
     *                  with their ids. Used to build the bugemons.
     * @return (List<Bugemon>) The list of the newly build Bugemon objects.
     */
    static List<Bugemon> parseBugemons(
            Path fileName,
            Map<String, Attack> attackMap) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        Bugemon.class,
                        new BugemonDeserializer(attackMap))
                .registerTypeAdapter(Bugemon.BType.class, new TypeDeserializer())
                .create();

        try (FileReader reader = new FileReader(fileName.toFile())) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray bugemonsArray = root.getAsJsonArray("bugemons");

            Type destType = new TypeToken<List<Bugemon>>() {}.getType();

            List<Bugemon> bugemons = gson.fromJson(bugemonsArray, destType);

            reader.close();
            return bugemons;
        } catch (Exception e) {
            // TODO: Use of a Logger or external error management ?

            e.printStackTrace();
        }
        return null;
    }

    static void parseObjects(Path fileName) {
    }

    static void parseSkillTree(Path fileName) {
    }
}
