package ulb.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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

import ulb.Configuration;
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
import ulb.models.skills.Skill;
import ulb.models.skills.SkillNode;
import ulb.models.skills.SkillTree;
import ulb.models.utils.Position;
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
    private static final String SKILL_ROOT_ID = "start";

    private static final Logger LOG = LoggerFactory.getLogger(Parser.class);

    // Constants for the paths to the JSON data files within the resources
    // directory
    private static final String JSON_ATTACK_PATH = Configuration.Json.ATTACK_PATH;
    private static final String JSON_BUGEMON_PATH = Configuration.Json.BUGEMON_PATH;
    private static final String JSON_ITEMS_PATH = Configuration.Json.ITEMS_PATH;

    // Static fields to hold the parsed data, accessible via getter methods
    private static Map<String, Attack> attacks;
    private static List<CreateBugemonDTO> bugemons;
    private static List<Item> items;
    private static Inventory inventory;
    private static SkillTree skillTree; // represent the tree data structure

    /**
     * Parses all JSON resource files and populates the static data fields. Must be called once before any
     * {@code get*()} accessor. Silently returns without populating any data if a resource file cannot be opened.
     */
    public void parse() {
        LOG.info("Parsing data");

        InputStream attacksStream;
        InputStream bugemonsStream;
        InputStream itemsStream;
        InputStream skillTreeStream;
        try {
            attacksStream = Parser.class.getResourceAsStream(JSON_ATTACK_PATH);
            bugemonsStream = Parser.class.getResourceAsStream(JSON_BUGEMON_PATH);
            itemsStream = Parser.class.getResourceAsStream(JSON_ITEMS_PATH);
            skillTreeStream = Parser.class.getResourceAsStream(Configuration.Json.SKILL_TREE_PATH);
            if (attacksStream == null || bugemonsStream == null || itemsStream == null || skillTreeStream == null) {
                throw new IOException("JSON files not found in resources: ");
            }
        } catch (IOException e) {
            LOG.error("Error loading JSON files: {}", e.getMessage());
            return;
        }

        Reader attacksReader = new InputStreamReader(attacksStream, StandardCharsets.UTF_8);
        Reader bugemonsReader = new InputStreamReader(bugemonsStream, StandardCharsets.UTF_8);
        Reader itemsReader = new InputStreamReader(itemsStream, StandardCharsets.UTF_8);
        Reader skillTreeReader = new InputStreamReader(skillTreeStream, StandardCharsets.UTF_8);
        parseAttacks(attacksReader);
        parseBugemons(bugemonsReader);
        parseItemsAndInventory(itemsReader);
        parseSkills(skillTreeReader);
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

    public final SkillTree getSkillTree() {
        return skillTree;
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

    private static void parseItemsAndInventory(Reader reader) {
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
                String itemId = entry.getKey();
                int quantity = entry.getValue();

                Item obj = items.stream().filter(o -> o.id().equals(itemId)).findFirst()
                        .orElseThrow(() -> new RuntimeException("Item with ID " + itemId + " not found"));
                inventory.addItem(obj, quantity);
            }
            reader.close();
        } catch (Exception e) {
            LOG.error("Error when parsing Items and inventory: {}", e.getMessage());
        }
    }

    private static void parseSkills(Reader reader) {
        Map<String, SkillNode> skillNodes = new HashMap<>();

        LOG.debug("Parsing Skill Tree");
        try {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray nodesArray = root.getAsJsonObject("skill_tree").getAsJsonArray("nodes");

            skillNodes = new HashMap<>();

            for (JsonElement nodeElement : nodesArray) {
                JsonObject nodeObj = nodeElement.getAsJsonObject();

                String id = nodeObj.get("id").getAsString();
                String name = nodeObj.get("nom").getAsString();
                String description = nodeObj.get("description").getAsString();
                int cost = nodeObj.get("cout").getAsInt();
                int maxLevel = nodeObj.get("max_niveau").getAsInt();

                // we don't use this as it is stupid to check that as we need just
                // to know the point given on the f*cking node... THAT WILL tell if the node
                // is unlocked or not and we'll handle the special case of the root node <3
                // thanks for you understanding...
                int initialLevel = id.equals(SKILL_ROOT_ID) ? 1 : 0;

                // SkillEffect effect = parseSkillEffect(nodeObj.get("effet"));

                Skill skill = new Skill(id, name, description, cost, maxLevel, initialLevel, null);

                JsonObject posObj = nodeObj.getAsJsonObject("position");
                Position position = new Position(posObj.get("x").getAsInt(), posObj.get("y").getAsInt());

                SkillNode skillNode = new SkillNode(skill, position);

                skillNodes.put(id, skillNode);

                // Assuming skillNodes are being built in the right order
                for (JsonElement req : nodeObj.getAsJsonArray("prerequis")) {
                    String reqId = req.getAsString();
                    SkillNode parent = skillNodes.get(reqId);
                    parent.addChild(skillNode);
                    skillNode.addParent(parent);
                }
            }

            skillTree = new SkillTree(skillNodes.get(SKILL_ROOT_ID));

            reader.close();

        } catch (Exception e) {
            LOG.error("Error when parsing skill tree: {}", e.getMessage());
        }
    }
}
