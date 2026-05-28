package bugemon.server.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import bugemon.common.dto.persistence.DefaultInventoryDTO;
import bugemon.common.models.bugemon.Attack;
import bugemon.common.models.bugemon.Bugemon;
import bugemon.common.models.bugemon.ElementType;
import bugemon.common.models.effect.Effect;
import bugemon.common.models.effect.StatModifierEffect;
import bugemon.common.models.item.Item;
import bugemon.common.models.item.ItemType;
import bugemon.common.models.skills.SkillNode;
import bugemon.common.models.skills.SkillTree;

public class TestParser {

    @Test
    public void testAttackParsing() {
        Parser parser = new Parser();
        parser.parse();

        // check if a map has been returned
        assertNotNull(parser.getAttacks());

        // check if the attacks were parsed correctly
        Attack fouetLiane = parser.getAttacks().values().stream().filter(a -> "fouet_liane".equals(a.id())).findFirst()
                .orElseThrow();
        assertEquals("fouet_liane", fouetLiane.id());

        // check effect type
        Attack racinesVives = parser.getAttacks().values().stream().filter(a -> "racines_vives".equals(a.id()))
                .findFirst().orElseThrow();
        List<Effect> effects = racinesVives.effects();
        assertEquals(StatModifierEffect.class, effects.get(0).getClass());
    }

    @Test
    public void testBugemonParsing() {
        Parser parser = new Parser();
        parser.parse();

        // check if a list has been returned
        assertNotNull(parser.getBugemons());

        // check attributes
        Bugemon florachu = parser.getBugemons().stream().filter(b -> "Florachu".equals(b.name())).findFirst()
                .orElseThrow();
        assertEquals("Florachu", florachu.name());

        Bugemon moussil = parser.getBugemons().stream().filter(b -> "Moussil".equals(b.name())).findFirst()
                .orElseThrow();
        assertEquals(ElementType.FLORA, moussil.type());

        // check attacks resolve to the same instances held in the attack map
        Bugemon verdurion = parser.getBugemons().stream().filter(b -> "Verdurion".equals(b.name())).findFirst()
                .orElseThrow();
        for (Attack a : verdurion.attacks()) {
            assertEquals(a, parser.getAttacks().get(a.id()));
        }

        // check stats
        Bugemon loopine = parser.getBugemons().stream().filter(b -> "Loopine".equals(b.name())).findFirst()
                .orElseThrow();
        assertEquals(50, loopine.attack());
        assertEquals(85, loopine.hp());
        assertEquals(50, loopine.defense());
        assertEquals(60, loopine.initiative());
    }

    @Test
    public void testParseWithInputStreams() {
        InputStream attacksStream = TestParser.class.getResourceAsStream("/json/attaques.json");
        InputStream bugemonsStream = TestParser.class.getResourceAsStream("/json/bugemons.json");
        InputStream itemsStream = TestParser.class.getResourceAsStream("/json/objets.json");

        assertNotNull(attacksStream);
        assertNotNull(bugemonsStream);
        assertNotNull(itemsStream);

        Parser parser = new Parser();
        parser.parse();

        // check attacks
        Map<String, Attack> attacksMap = parser.getAttacks();
        Attack fouetLiane = attacksMap.get("fouet_liane");
        assertNotNull(fouetLiane);
        assertEquals("fouet_liane", fouetLiane.id());

        // check bugemons
        List<Bugemon> bugemons = parser.getBugemons();
        Bugemon florachu = bugemons.stream().filter(b -> "Florachu".equals(b.name())).findFirst().orElseThrow();
        assertEquals("Florachu", florachu.name());

        Bugemon moussil = bugemons.stream().filter(b -> "Moussil".equals(b.name())).findFirst().orElseThrow();
        assertEquals(ElementType.FLORA, moussil.type());
    }

    @Test
    public void testParseItems() {
        InputStream itemsStream = TestParser.class.getResourceAsStream("/json/objets.json");
        assertNotNull(itemsStream);

        Parser parser = new Parser();
        parser.parse();

        List<Item> itemsList = parser.getItems();
        DefaultInventoryDTO inventory = parser.getInventory();

        assertNotNull(itemsList);
        assertNotNull(inventory);

        Item testItem = itemsList.stream().filter(o -> "baie_revigorante".equals(o.id())).findFirst().orElseThrow();

        assertEquals("baie_revigorante", testItem.id());
        assertEquals("Baie Revigorante", testItem.name());
        assertEquals("Restaure 20 PV au Bugémon actif.", testItem.description());
        assertEquals(ItemType.HEALING, testItem.type());

        Map<Item, Integer> items = inventory.items();
        assertEquals(7, items.values().stream().mapToInt(i -> i).sum());

        long revigoranteCount = items.entrySet().stream().filter(e -> "baie_revigorante".equals(e.getKey().id()))
                .mapToLong(Map.Entry::getValue).sum();
        long toniqueCount = items.entrySet().stream().filter(e -> "baie_tonique".equals(e.getKey().id()))
                .mapToLong(Map.Entry::getValue).sum();
        long gelCount = items.entrySet().stream().filter(e -> "gel_defensif".equals(e.getKey().id()))
                .mapToLong(Map.Entry::getValue).sum();
        long serumCount = items.entrySet().stream().filter(e -> "serum_offensif".equals(e.getKey().id()))
                .mapToLong(Map.Entry::getValue).sum();

        assertEquals(3, revigoranteCount);
        assertEquals(2, toniqueCount);
        assertEquals(1, gelCount);
        assertEquals(1, serumCount);
    }

    @Test
    public void testParseSkills() {
        Parser parser = new Parser();
        parser.parse();

        SkillTree skillTree = parser.getSkillTree();
        assertNotNull(skillTree);

        SkillNode root = skillTree.getById("start");

        assertEquals("Départ", root.name());
        assertEquals(0, root.cost());
        assertEquals(1, root.maxLevel());
        assertEquals(0, root.x());
        assertEquals(0, root.y());
    }
}
