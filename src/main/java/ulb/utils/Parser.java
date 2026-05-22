package ulb.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
import ulb.common.EffectDuration;
import ulb.common.EffectTarget;
import ulb.common.StatType;
import ulb.common.dto.persistence.DefaultInventoryDTO;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.ElementType;
import ulb.models.effect.Effect;
import ulb.models.effect.HealEffect;
import ulb.models.effect.ResetMalusEffect;
import ulb.models.effect.StatModifierEffect;
import ulb.models.item.Item;
import ulb.models.item.ItemType;
import ulb.models.skills.SkillEffect;
import ulb.models.skills.SkillNode;
import ulb.models.skills.SkillTree;

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

    private static final String STR_VALEUR = "valeur";

    // Constants for the paths to the JSON data files within the resources
    // directory
    private static final String JSON_ATTACK_PATH = Configuration.Json.ATTACK_PATH;
    private static final String JSON_BUGEMON_PATH = Configuration.Json.BUGEMON_PATH;
    private static final String JSON_ITEMS_PATH = Configuration.Json.ITEMS_PATH;

    // Static fields to hold the parsed data, accessible via getter methods
    private static Map<String, Attack> attacks;
    private static List<Bugemon> bugemons;
    private static List<Item> items;
    private static DefaultInventoryDTO inventory;
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

    public final List<Bugemon> getBugemons() {
        return bugemons;
    }

    public final List<Item> getItems() {
        return items;
    }

    public final DefaultInventoryDTO getInventory() {
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
    private static class TypeDeserializer implements JsonDeserializer<ElementType> {
        @Override
        public ElementType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            String value = json.getAsString();
            return ElementType.valueOf(value.toUpperCase());
        }
    }

    private static class ItemTypeDeserializer implements JsonDeserializer<ItemType> {
        @Override
        public ItemType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return parseItemType(json.getAsString());
        }
    }

    private static class DurationDeserializer implements JsonDeserializer<EffectDuration> {
        @Override
        public EffectDuration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
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
                    new StatModifierEffect(target, context.deserialize(effectObject.get("stat"), StatType.class),
                            effectObject.get("modificateur").getAsInt(),
                            context.deserialize(effectObject.get("duree"), EffectDuration.class));
                case "soin" -> new HealEffect(target, effectObject.get(STR_VALEUR).getAsInt());
                case "reset_malus" -> new ResetMalusEffect(target);
                default -> throw new JsonParseException("Unknown effect type: " + effectType);
            };
        }
    }

    private static void parseAttacks(Reader reader) {
        LOG.debug("Parsing Attacks");
        Gson gson = new GsonBuilder().registerTypeAdapter(ElementType.class, new TypeDeserializer())
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
                .registerTypeAdapter(ElementType.class, new TypeDeserializer()).create();

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
                .registerTypeAdapter(Effect.class, new EffectDeserializer())
                .registerTypeAdapter(ItemType.class, new ItemTypeDeserializer()).create();

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
            Map<String, Integer> inventoryRaw = gson.fromJson(startInventory, invType);
            mapInventory(inventoryRaw, items);
            reader.close();
        } catch (Exception e) {
            LOG.error("Error when parsing Items and inventory: {}", e.getMessage());
        }
    }

    private static void mapInventory(Map<String, Integer> inventoryRaw, List<Item> itemsParsed) {
        Map<Item, Integer> inventoryMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : inventoryRaw.entrySet()) {
            String itemId = entry.getKey();
            int quantity = entry.getValue();

            Item obj = itemsParsed.stream().filter(o -> o.id().equals(itemId)).findFirst()
                    .orElseThrow(() -> new RuntimeException("Item with ID " + itemId + " not found"));
            inventoryMap.put(obj, quantity);
        }
        inventory = new DefaultInventoryDTO(inventoryMap);
    }

    private static void parseSkills(Reader reader) {
        LOG.debug("Parsing Skill Tree");
        List<SkillNode> nodes = new ArrayList<>();
        try {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray nodesArray = root.getAsJsonObject("skill_tree").getAsJsonArray("nodes");

            for (JsonElement nodeElement : nodesArray) {
                JsonObject obj = nodeElement.getAsJsonObject();

                String id = obj.get("id").getAsString();
                String name = obj.get("nom").getAsString();
                String description = obj.get("description").getAsString();
                int cost = obj.get("cout").getAsInt();
                int maxLevel = obj.get("max_niveau").getAsInt();

                JsonObject posObj = obj.getAsJsonObject("position");
                int x = posObj.get("x").getAsInt();
                int y = posObj.get("y").getAsInt();

                List<String> prerequisites = new ArrayList<>();
                for (JsonElement prereq : obj.getAsJsonArray("prerequis")) {
                    prerequisites.add(prereq.getAsString());
                }

                SkillEffect effect = null;
                JsonElement effElem = obj.get("effet");
                if (effElem != null && !effElem.isJsonNull()) {
                    effect = parseSkillEffect(effElem.getAsJsonObject());
                }

                nodes.add(new SkillNode(id, name, description, x, y, maxLevel, cost, effect, prerequisites));
            }

            skillTree = new SkillTree(nodes);
            reader.close();
        } catch (Exception e) {
            LOG.error("Error when parsing skill tree: {}", e.getMessage());
        }
    }

    private static SkillEffect parseSkillEffect(JsonObject obj) {
        String type = obj.get("type").getAsString();
        return switch (type) {
            case "stat_bonus" -> new SkillEffect.StatBonusEffect(parseStatType(obj.get("stat").getAsString()),
                    obj.get(STR_VALEUR).getAsInt());
            case "type_multiplicateur" -> new SkillEffect.TypeMultiplierEffect(
                    ElementType.valueOf(obj.get("type_cible").getAsString().toUpperCase()),
                    obj.get(STR_VALEUR).getAsDouble());
            case "critique_bonus" -> new SkillEffect.CritBonusEffect(obj.get(STR_VALEUR).getAsDouble());
            case "regen_post_combat" -> new SkillEffect.RegenPostCombatEffect(obj.get("valeur_pourcent").getAsDouble());
            case "xp_multiplicateur" -> new SkillEffect.XpMultiplierEffect(obj.get(STR_VALEUR).getAsDouble());
            case "objets_bonus" -> new SkillEffect.StarterItemsEffect(obj.get("quantite").getAsInt(),
                    parseItemType(obj.get("categorie").getAsString()));
            case "recompense_choix" -> new SkillEffect.RewardChoiceEffect(obj.get(STR_VALEUR).getAsInt());
            default -> throw new IllegalArgumentException("Unknown skill effect type: " + type);
        };
    }

    private static StatType parseStatType(String statStr) {
        return switch (statStr) {
            case "hp" -> StatType.HP;
            case "attaque" -> StatType.ATTACK;
            case "defense" -> StatType.DEFENSE;
            case "initiative" -> StatType.INITIATIVE;
            default -> throw new IllegalArgumentException("Unknown stat type: " + statStr);
        };
    }

    private static ItemType parseItemType(String category) {
        return switch (category) {
            case "soin" -> ItemType.HEALING;
            case "boost" -> ItemType.BOOST;
            default -> throw new IllegalArgumentException("Unknown item category: " + category);
        };
    }
}
